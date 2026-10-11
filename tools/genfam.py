"""Generate thin 'variant' creatures that subclass an existing one (entity + shared-rig model + renderer). Usage: edit FAMS and run; then tools/addentity.py for each."""
import os
J = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/java/com/reevil/')
def gen(f):
    pk, C = f['pk'], f['C']
    os.makedirs(J + f'entity/{pk}', exist_ok=True); os.makedirs(J + f'client/{pk}', exist_ok=True)
    open(J + f'entity/{pk}/{C}Entity.java', 'w').write(f'''package com.reevil.entity.{pk};

import com.reevil.entity.{f['pent']};
{f.get('imports', '')}import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** {f['doc']} */
public class {C}Entity extends {f['pe']} {{
    public {C}Entity(EntityType<{C}Entity> type, Level level) {{ super(unchecked(type), level); xpReward = {f.get('xp', 20)}; }}

    @SuppressWarnings({{"unchecked", "rawtypes"}})
    private static EntityType<{f['pe']}> unchecked(EntityType<{C}Entity> t) {{ return (EntityType) t; }}

    public static AttributeSupplier.Builder createAttributes() {{
        return Monster.createMonsterAttributes()
            {f['attrs']};
    }}
{f.get('extra', '')}}}
''')
    open(J + f'client/{pk}/{C}Model.java', 'w').write(f'''package com.reevil.client.{pk};

import com.reevil.ReEvilMod;
import com.reevil.client.{f['pmod']};
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the {f['pm']} rig and clips. */
public class {C}Model extends {f['pm']} {{
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "{f['id']}"), "main");
    public {C}Model(ModelPart root) {{ super(root); }}
    public static LayerDefinition createBodyLayer() {{ return {f['pm']}.createBodyLayer(); }}
}}
''')
    open(J + f'client/{pk}/{C}Renderer.java', 'w').write(f'''package com.reevil.client.{pk};

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.{f.get('retimp', f['pent'])};
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class {C}Renderer extends BioRenderer<{f.get('ret', f['pe'])}, {C}Model> {{
    public {C}Renderer(EntityRendererProvider.Context ctx) {{
        super(ctx, new {C}Model(ctx.bakeLayer({C}Model.LAYER)), {f.get('shadow', '0.6f')}, "{f['id']}");
    }}

    @Override protected void scale({f.get('ret', f['pe'])} e, PoseStack ps, float partial) {{ ps.scale({f.get('scale', '1.0f')}, {f.get('scale', '1.0f')}, {f.get('scale', '1.0f')}); }}
    @Override protected float getFlipDegrees({f.get('ret', f['pe'])} e) {{ return 0f; }}
}}
''')
