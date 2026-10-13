import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.io.File;
import java.util.*;

/** rest-pose bounds of every creature model: prints height/width/depth in blocks (1 block = 16 px). */
public class ModelBounds {
    public static void main(String[] a) throws Exception {
        File root = new File("src/main/java/com/reevil/client");
        for (File d : root.listFiles()) {
            if (!d.isDirectory()) continue;
            for (File f : d.listFiles()) {
                if (!f.getName().endsWith("Model.java")) continue;
                String cn = "com.reevil.client." + d.getName() + "." + f.getName().replace(".java", "");
                try {
                    Class<?> c = Class.forName(cn);
                    LayerDefinition ld = (LayerDefinition) c.getMethod("createBodyLayer").invoke(null);
                    ModelPart rt = ld.bakeRoot();
                    float[] b = {1e9f, 1e9f, 1e9f, -1e9f, -1e9f, -1e9f};
                    rt.visit(new PoseStack(), (pose, path, idx, cube) -> {
                        for (int i = 0; i < 8; i++) {
                            float x = (i & 1) == 0 ? cube.minX : cube.maxX, y = (i & 2) == 0 ? cube.minY : cube.maxY, z = (i & 4) == 0 ? cube.minZ : cube.maxZ;
                            Vector4f v = new Vector4f(x / 16f, y / 16f, z / 16f, 1f); v.mul(pose.pose());
                            b[0] = Math.min(b[0], v.x); b[1] = Math.min(b[1], v.y); b[2] = Math.min(b[2], v.z); b[3] = Math.max(b[3], v.x); b[4] = Math.max(b[4], v.y); b[5] = Math.max(b[5], v.z);
                        }
                    });
                    System.out.printf("%s H=%.2f W=%.2f D=%.2f%n", f.getName().replace("Model.java", ""), b[4] - b[1], b[3] - b[0], b[5] - b[2]);
                } catch (Throwable t) { System.out.println(f.getName() + " ERR " + t); }
            }
        }
    }
}
