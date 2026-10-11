"""Register a new creature: python3 tools/addentity.py id Class w h 0xEGG1 0xEGG2 "English" "日本語"
Package = id without underscores (entity.<pkg>.<Class>Entity, client.<pkg>.<Class>Model/Renderer). Idempotent."""
import sys, json, re, os
i, C, w, h, e1, e2, en, ja = sys.argv[1:9]
pk = i.replace('_', ''); U = i.upper()
base = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main')
J = base + '/java/com/reevil/'
me = open(J + 'registry/ModEntities.java').read()
if f'RegistryObject<EntityType<{C}Entity>>' not in me:
    me = me.replace('import net.minecraft.world.entity.EntityType;', f'import com.reevil.entity.{pk}.{C}Entity;\nimport net.minecraft.world.entity.EntityType;', 1)
    decl = (f'    public static final RegistryObject<EntityType<{C}Entity>> {U} = ENTITIES.register("{i}",\n'
            f'        () -> EntityType.Builder.of({C}Entity::new, MobCategory.MONSTER).sized({w}f, {h}f).clientTrackingRange(10).build("{i}"));\n'
            f'    public static final RegistryObject<Item> {U}_EGG = egg("{i}", {U}, {e1}, {e2});\n')
    me = me.replace('    public static void onAttributes', decl + '\n    public static void onAttributes', 1)
    me = me.replace('    }\n\n    private ModEntities()', f'        e.put({U}.get(), {C}Entity.createAttributes().build());\n    }}\n\n    private ModEntities()', 1)
    open(J + 'registry/ModEntities.java', 'w').write(me)
cs = open(J + 'client/ClientSetup.java').read()
if f'{C}Renderer' not in cs:
    cs = cs.replace('import com.reevil.registry.ModEntities;', f'import com.reevil.client.{pk}.{C}Model;\nimport com.reevil.client.{pk}.{C}Renderer;\nimport com.reevil.registry.ModEntities;', 1)
    cs = cs.replace('    }\n\n    @SubscribeEvent\n    public static void renderers', f'        e.registerLayerDefinition({C}Model.LAYER, {C}Model::createBodyLayer);\n    }}\n\n    @SubscribeEvent\n    public static void renderers', 1)
    cs = cs.replace('    }\n\n    private ClientSetup()', f'        e.registerEntityRenderer(ModEntities.{U}.get(), {C}Renderer::new);\n    }}\n\n    private ClientSetup()', 1)
    open(J + 'client/ClientSetup.java', 'w').write(cs)
R = base + '/resources/assets/reevil/'
for f, name, egg in (('en_us', en, ' Spawn Egg'), ('ja_jp', ja, 'のスポーンエッグ')):
    p = R + f'lang/{f}.json'; d = json.load(open(p))
    d[f'entity.reevil.{i}'] = name; d[f'item.reevil.{i}_spawn_egg'] = name + egg
    json.dump(d, open(p, 'w'), indent=1, ensure_ascii=False)
json.dump({"parent": "minecraft:item/template_spawn_egg"}, open(R + f'models/item/{i}_spawn_egg.json', 'w'))
print('registered', i)
