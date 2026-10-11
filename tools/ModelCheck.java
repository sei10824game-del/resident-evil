import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.lang.reflect.*;
import java.util.*;

public class ModelCheck {
    static Object get(Field f, Object o) throws Exception { f.setAccessible(true); return f.get(o); }

    public static void main(String[] a) throws Exception {
        String[] names = {"zombie.ZombieModel", "crimsonhead.CrimsonHeadModel", "cerberus.CerberusModel", "crow.CrowModel", "hunter.HunterModel", "chimera.ChimeraModel",
            "giantspider.GiantSpiderModel", "webspinner.WebSpinnerModel", "neptune.NeptuneModel", "yawn.YawnModel", "plant42.Plant42Model", "tyrant.TyrantModel", "lisatrevor.LisaTrevorModel", "licker.LickerModel", "mrx.MrXModel", "birkin.BirkinModel", "alligator.AlligatorModel", "ivy.IvyModel", "giantmoth.GiantMothModel", "larva.LarvaModel", "nemesis.NemesisModel", "gravedigger.GraveDiggerModel", "draindeimos.DrainDeimosModel"};
        int bad = 0;
        for (String n : names) {
            try {
                Class<?> c = Class.forName("com.reevil.client." + n);
                LayerDefinition ld = (LayerDefinition) c.getMethod("createBodyLayer").invoke(null);
                ModelPart root = ld.bakeRoot();
                BioModel<?> m = (BioModel<?>) c.getConstructor(ModelPart.class).newInstance(root);
                Field bf = BioModel.class.getDeclaredField("bones"); bf.setAccessible(true);
                @SuppressWarnings("unchecked") Map<String, ModelPart> bones = (Map<String, ModelPart>) bf.get(m);
                List<AnimClip> clips = new ArrayList<>();
                for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass())
                    for (Field f : k.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue;
                        f.setAccessible(true);
                        Object v = f.get(null);
                        if (v instanceof AnimClip) clips.add((AnimClip) v);
                        else if (v instanceof AnimClip[]) { for (AnimClip z : (AnimClip[]) v) if (z != null) clips.add(z); }
                        else if (v instanceof Map) for (Object y : ((Map<?, ?>) v).values()) if (y instanceof AnimClip) clips.add((AnimClip) y);
                    }
                Set<String> missing = new TreeSet<>();
                for (AnimClip cl : clips) for (String b : cl.bones()) if (!bones.containsKey(b)) missing.add(b);
                // sample all clips for NaN
                int nan = 0;
                for (AnimClip cl : clips) for (String b : cl.bones()) for (float t = 0; t <= 1.0001f; t += 0.05f) for (float v : cl.sample(b, t)) if (Float.isNaN(v) || Float.isInfinite(v)) nan++;
                System.out.println((missing.isEmpty() && nan == 0 ? "OK   " : "BAD  ") + n + " bones=" + bones.size() + " clips=" + clips.size() + (missing.isEmpty() ? "" : " MISSING-BONES=" + missing) + (nan > 0 ? " NaN=" + nan : ""));
                if (!missing.isEmpty() || nan > 0) bad++;
            } catch (Throwable t) {
                Throwable r = t instanceof InvocationTargetException ? t.getCause() : t;
                System.out.println("FAIL " + n + " -> " + r);
                r.printStackTrace(System.out);
                bad++;
            }
        }
        System.out.println("bad=" + bad);
    }
}
