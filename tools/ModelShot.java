import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Offline look check: software-renders every creature model (rest pose, back 3/4 + front 3/4) with its texture to PNG, no GL / no game needed.
 * The entity is constructor-less but carries its real type, so CanonVisuals / CanonClips (mutation parts, swung tails, 40+ move clips) apply as in game.
 * usage (repo root, after ./gradlew classes): tools/shots.sh <outDir> [nameFilter] [mut] [form] [moveId] [clip%]
 */
public class ModelShot {
    static final int S = 320;
    static class Q implements VertexConsumer {
        List<float[]> v = new ArrayList<>();
        public void vertex(float x, float y, float z, float r, float g, float b, float a, float u, float w, int o, int l, float nx, float ny, float nz) { v.add(new float[]{x, y, z, u, w, nx, ny, nz}); }
        public VertexConsumer vertex(double x, double y, double z) { return this; }
        public VertexConsumer color(int r, int g, int b, int a) { return this; }
        public VertexConsumer uv(float u, float w) { return this; }
        public VertexConsumer overlayCoords(int u, int w) { return this; }
        public VertexConsumer uv2(int u, int w) { return this; }
        public VertexConsumer normal(float x, float y, float z) { return this; }
        public void endVertex() { }
        public void defaultColor(int r, int g, int b, int a) { }
        public void unsetDefaultColor() { }
    }

    static BufferedImage shot(List<float[]> vs, BufferedImage tex, double yawDeg) {
        double yaw = Math.toRadians(yawDeg), pit = Math.toRadians(12), cy = Math.cos(yaw), sy = Math.sin(yaw), cp = Math.cos(pit), sp = Math.sin(pit);
        float[][] p = new float[vs.size()][];
        float[] lo = {1e9f, 1e9f}, hi = {-1e9f, -1e9f};
        for (int i = 0; i < p.length; i++) {
            float[] a = vs.get(i);
            double x = a[0] * cy + a[2] * sy, z = -a[0] * sy + a[2] * cy, y = a[1] * cp - z * sp, z2 = a[1] * sp + z * cp;
            double nx = a[5] * cy + a[7] * sy, nz = -a[5] * sy + a[7] * cy, ny = a[6] * cp - nz * sp, nz2 = a[6] * sp + nz * cp;
            p[i] = new float[]{(float) x, (float) y, (float) z2, a[3], a[4], (float) Math.max(0, -nz2 * 0.8 - ny * 0.45 + nx * 0.3)};
            for (int k = 0; k < 2; k++) { lo[k] = Math.min(lo[k], p[i][k]); hi[k] = Math.max(hi[k], p[i][k]); }
        }
        float sc = (S - 16) / Math.max(hi[0] - lo[0], hi[1] - lo[1]), ox = (S - (hi[0] + lo[0]) * sc) / 2, oy = (S - (hi[1] + lo[1]) * sc) / 2;
        BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < S * S; i++) img.setRGB(i % S, i / S, 0x8090a0);
        float[] zb = new float[S * S]; Arrays.fill(zb, 1e9f);
        for (int q = 0; q + 3 < p.length; q += 4) for (int[] t : new int[][]{{0, 1, 2}, {0, 2, 3}}) {
            float[] a = p[q + t[0]], b = p[q + t[1]], c = p[q + t[2]];
            float ax = a[0] * sc + ox, ay = a[1] * sc + oy, bx = b[0] * sc + ox, by = b[1] * sc + oy, cx = c[0] * sc + ox, cyy = c[1] * sc + oy;
            float den = (by - cyy) * (ax - cx) + (cx - bx) * (ay - cyy);
            if (Math.abs(den) < 1e-6) continue;
            int x0 = (int) Math.max(0, Math.min(ax, Math.min(bx, cx))), x1 = (int) Math.min(S - 1, Math.max(ax, Math.max(bx, cx)) + 1);
            int y0 = (int) Math.max(0, Math.min(ay, Math.min(by, cyy))), y1 = (int) Math.min(S - 1, Math.max(ay, Math.max(by, cyy)) + 1);
            for (int y = y0; y <= y1; y++) for (int x = x0; x <= x1; x++) {
                float w0 = ((by - cyy) * (x + .5f - cx) + (cx - bx) * (y + .5f - cyy)) / den, w1 = ((cyy - ay) * (x + .5f - cx) + (ax - cx) * (y + .5f - cyy)) / den, w2 = 1 - w0 - w1;
                if (w0 < 0 || w1 < 0 || w2 < 0) continue;
                float z = w0 * a[2] + w1 * b[2] + w2 * c[2];
                if (z >= zb[y * S + x]) continue;
                int rgb = 0xffc08040;
                if (tex != null) {
                    int tx = Math.floorMod((int) ((w0 * a[3] + w1 * b[3] + w2 * c[3]) * tex.getWidth()), tex.getWidth()), ty = Math.floorMod((int) ((w0 * a[4] + w1 * b[4] + w2 * c[4]) * tex.getHeight()), tex.getHeight());
                    rgb = tex.getRGB(tx, ty);
                    if ((rgb >>> 24) < 128) continue;
                }
                float sh = 0.55f + 0.45f * Math.min(1, a[5]);
                zb[y * S + x] = z;
                img.setRGB(x, y, ((int) ((rgb >> 16 & 255) * sh) << 16) | ((int) ((rgb >> 8 & 255) * sh) << 8) | (int) ((rgb & 255) * sh));
            }
        }
        return img;
    }



    static ModelPart part(ModelPart p, String path) { for (String s : path.split("/")) { if (!p.hasChild(s)) return null; p = p.getChild(s); } return p; }

    /** a real registered EntityType for the creature, so id-keyed client code (CanonVisuals, CanonClips) sees the world-less entity as itself */
    static net.minecraft.world.entity.EntityType<?> typeFor(String id) throws Exception {
        var reg = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE; var rl = new net.minecraft.resources.ResourceLocation("reevil", id);
        if (reg.containsKey(rl)) return reg.get(rl);
        for (Class<?> k = reg.getClass(); k != null; k = k.getSuperclass()) for (Field f : k.getDeclaredFields()) {   // the registry is frozen after bootstrap: reopen it
            if (f.getName().equals("frozen") || f.getName().equals("locked")) { f.setAccessible(true); f.setBoolean(reg, false); }
            if (f.getName().equals("unregisteredIntrusiveHolders")) { f.setAccessible(true); if (f.get(reg) == null) f.set(reg, new IdentityHashMap<>()); }
        }
        return net.minecraft.core.Registry.register(reg, rl, net.minecraft.world.entity.EntityType.Builder.createNothing(net.minecraft.world.entity.MobCategory.MONSTER).build(id));
    }

    static Map<String, Integer> vars;
    static sun.misc.Unsafe unsafe;
    static int bad, pct;

    public static void main(String[] a) throws Exception {
        Field uf = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); uf.setAccessible(true); unsafe = (sun.misc.Unsafe) uf.get(null);
        net.minecraft.SharedConstants.tryDetectVersion(); net.minecraft.server.Bootstrap.bootStrap();
        Path src = Paths.get("src/main/java/com/reevil/client"), texDir = Paths.get("src/main/resources/assets/reevil/textures/entity");
        vars = Map.of("mut", a.length > 2 ? Integer.parseInt(a[2]) : 0, "form", a.length > 3 ? Integer.parseInt(a[3]) : 0, "move", a.length > 4 ? Integer.parseInt(a[4]) : 0);
        pct = a.length > 5 ? Integer.parseInt(a[5]) : 50;
        String setup = Files.readString(src.resolve("ClientSetup.java"));
        Map<String, Path> renderers = new HashMap<>();
        try (var st = Files.walk(src)) { st.filter(f -> f.toString().endsWith("Renderer.java")).forEach(f -> renderers.put(f.getFileName().toString().replace(".java", ""), f)); }
        Pattern rp = Pattern.compile("new (\\w+Model)\\(ctx\\.bakeLayer[^;]*?\"(\\w+)\"\\s*\\)");
        Matcher reg = Pattern.compile("registerEntityRenderer\\(ModEntities\\.(\\w+)\\.get\\(\\), (\\w+)::new").matcher(setup);
        Set<String> renderTex = new HashSet<>(); // texture bases owned by some renderer, so "zombie_x" is not mistaken for a suffix of "zombie" when "zombie_x" is its own creature
        for (Path r : renderers.values()) { Matcher m = rp.matcher(Files.readString(r)); if (m.find()) renderTex.add(m.group(2)); }
        new File(a[0]).mkdirs();
        while (reg.find()) {
            String id = reg.group(1).toLowerCase(Locale.ROOT);
            if (a.length > 1 && !id.contains(a[1])) continue;
            try {
                Path r = renderers.get(reg.group(2));
                String txt = Files.readString(r);
                Matcher m = rp.matcher(txt);
                if (!m.find()) throw new IllegalStateException("no model in " + r);
                Matcher im = Pattern.compile("import (com\\.reevil\\.client\\.[\\w.]*\\." + m.group(1) + ");").matcher(txt);
                String cls = im.find() ? im.group(1) : "com.reevil.client." + src.relativize(r.getParent()).toString().replace('/', '.') + "." + m.group(1);
                LayerDefinition ld = (LayerDefinition) Class.forName(cls).getMethod("createBodyLayer").invoke(null);
                ModelPart root = ld.bakeRoot();
                // every texture this renderer can pick (base + suffixed forms) must have the layer's aspect ratio, else the UVs land on the wrong pixels
                Field mf = LayerDefinition.class.getDeclaredField("material"); mf.setAccessible(true); Object mat = mf.get(ld);
                Field wf = mat.getClass().getDeclaredField("xTexSize"), hf = mat.getClass().getDeclaredField("yTexSize"); wf.setAccessible(true); hf.setAccessible(true);
                int lw = wf.getInt(mat), lh = hf.getInt(mat);
                try (var ds = Files.newDirectoryStream(texDir, m.group(2) + "*.png")) { for (Path x : ds) {
                    String fn = x.getFileName().toString().replace(".png", "");
                    String own = ""; for (String rb : renderTex) if ((fn.equals(rb) || fn.startsWith(rb + "_")) && rb.length() > own.length()) own = rb;
                    if (!own.equals(m.group(2))) continue;
                    BufferedImage im2 = ImageIO.read(x.toFile());
                    if (im2.getWidth() * lh != im2.getHeight() * lw) { System.out.println("TEXSIZE " + id + ": " + fn + ".png is " + im2.getWidth() + "x" + im2.getHeight() + " but the model layer is " + lw + "x" + lh); bad++; }
                } }
                // real setupAnim on a constructor-less entity (no world needed): idle pose + the model's own hide/scale logic
                Matcher em = Pattern.compile("import (com\\.reevil\\.entity\\.[\\w.]+Entity);").matcher(txt);
                if (em.find()) try {
                    Class<?> ec;   // the creature's own entity class (it may override what its renderer's generic parent shows, e.g. hasHat)
                    try { ec = Class.forName("com.reevil.entity." + id.replace("_", "") + "." + reg.group(2).replace("Renderer", "Entity")); } catch (ClassNotFoundException x) { ec = Class.forName(em.group(1)); }
                    Object e = unsafe.allocateInstance(ec);
                    Field tf = net.minecraft.world.entity.Entity.class.getDeclaredField("type"); tf.setAccessible(true); tf.set(e, typeFor(id));
                    Field df = net.minecraft.world.entity.Entity.class.getDeclaredField("entityData"); df.setAccessible(true);
                    df.set(e, new net.minecraft.network.syncher.SynchedEntityData((net.minecraft.world.entity.Entity) e));
                    Method dm = null;
                    for (Class<?> k = ec; dm == null; k = k.getSuperclass()) try { dm = k.getDeclaredMethod("defineSynchedData"); } catch (NoSuchMethodException x) { }
                    dm.setAccessible(true); dm.invoke(e);
                    ((com.reevil.entity.BioMonster) e).setMut(vars.get("mut")); ((com.reevil.entity.BioMonster) e).setForm(vars.get("form"));
                    if (vars.get("move") > 0) {   // attack pose: move id at pct% of its clip
                        var ed = ((net.minecraft.world.entity.Entity) e).getEntityData(); int[] val = {vars.get("move"), pct, 100}; String[] fn = {"DATA_MOVE", "DATA_MOVE_TICK", "DATA_MOVE_DUR"};
                        for (int i = 0; i < 3; i++) { Field f = com.reevil.entity.BioMonster.class.getDeclaredField(fn[i]); f.setAccessible(true); ed.set((net.minecraft.network.syncher.EntityDataAccessor<Integer>) f.get(null), val[i]); }
                    }
                    Object model = Class.forName(cls).getConstructor(ModelPart.class).newInstance(root);
                    for (Method sm : model.getClass().getMethods()) if (sm.getName().equals("setupAnim") && sm.getParameterCount() == 6 && sm.getParameterTypes()[0] == com.reevil.entity.BioMonster.class) { sm.invoke(model, e, 0f, 0f, 0f, 0f, 0f); break; }
                } catch (Throwable x) { System.out.println("bind pose only: " + id + " " + (x.getCause() != null ? x.getCause() : x)); }
                Q q = new Q();
                root.render(new PoseStack(), q, 0, 0);
                Path t = texDir.resolve(m.group(2) + ".png");
                if (!Files.exists(t)) try (var ds = Files.newDirectoryStream(texDir, m.group(2) + "_*.png")) { for (Path x : ds) if (!Files.exists(t) || x.compareTo(t) < 0) t = x; }
                BufferedImage tex = Files.exists(t) ? ImageIO.read(t.toFile()) : null;
                BufferedImage out = new BufferedImage(S * 2, S, BufferedImage.TYPE_INT_RGB);
                out.getGraphics().drawImage(shot(q.v, tex, 150), 0, 0, null);
                out.getGraphics().drawImage(shot(q.v, tex, -35), S, 0, null);
                out.getGraphics().drawString(id + (tex == null ? " NO TEXTURE" : ""), 4, 12);
                ImageIO.write(out, "png", new File(a[0], id + ".png"));
            } catch (Throwable e) { bad++; System.out.println("FAIL " + id + " " + (e.getCause() != null ? e.getCause() : e)); }
        }
        System.out.println("bad=" + bad);
        if (bad > 0) System.exit(1);
    }
}
