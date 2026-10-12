"""Redesign reskinned creatures: replaces/adds cubes on the PARENT rig (same bone names -> same clips) and paints the new cubes into an extended texture canvas.
Spec -> client/<id>/<C>Model.java + textures (all <id>*.png get extended and painted).  Run: python3 tools/variantgen.py [id ...]"""
import os, re, sys, glob, random, math
from PIL import Image
from texlib import Tex, shade, OUT

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/java/com/reevil/client')

def F(*a): return ', '.join(('%sf' % x if isinstance(x, float) or not float(x).is_integer() else '%d' % x) if not isinstance(x, str) else x for x in a)

# ---------------------------------------------------------------- painting
def paint_cube(t, u, v, w, h, d, sty, tint, rng):
    base = tuple(int(c * tint) for c in sty['c']); noise = sty.get('n', 0.08); det = sty.get('d')
    t.box(u, v, w, h, d, base, noise, grad=sty.get('g', 0.12))
    fc = t.faces(u, v, w, h, d)
    fx, fy, fw, fh = fc['front']
    if det == 'fur':
        for x, y, ww, hh in fc.values():
            for i in range(ww):
                k = rng.uniform(0.75, 1.2)
                for j in range(hh): t.px_set(x + i, y + j, shade(base, k * rng.uniform(0.95, 1.05)))
    elif det in ('eye', 'redeye'):
        sc, pu = ((230, 226, 170), (20, 10, 10)) if det == 'eye' else ((230, 40, 28), (255, 200, 120))
        t.rect(fx, fy, fw, fh, sc, 0.04)
        t.rect(fx + fw // 2, fy + fh // 2, max(1, fw // 3), max(1, fh // 3), pu, 0)
    elif det == 'mouth':
        t.rect(fx, fy, fw, fh, (52, 12, 20), 0.1)
        for i in range(0, fw, 2):
            t.px_set(fx + i, fy, (230, 220, 180)); t.px_set(fx + i + 1, fy + fh - 1, (230, 220, 180))
            if fh > 4: t.px_set(fx + i, fy + 1, (210, 198, 160))
    elif det == 'veins':
        for x, y, ww, hh in fc.values():
            for _ in range(max(1, ww * hh // 18)):
                px, py = x + rng.randrange(ww), y + rng.randrange(hh)
                for _ in range(rng.randint(2, 5)):
                    t.px_set(px, py, (200, 36, 30)); px = min(max(px + rng.choice((-1, 0, 1)), x), x + ww - 1); py = min(max(py + 1, y), y + hh - 1)
    elif det == 'bruise':
        t.blots(fx, fy, fw, fh, shade(base, 0.6), max(2, fw * fh // 10), 3)
    elif det == 'spike':
        for x, y, ww, hh in fc.values():
            for j in range(hh):
                for i in range(ww): t.px_set(x + i, y + j, shade(base, 1.25 - 0.9 * j / max(1, hh - 1)))

def pack(cubes, W, v0):
    x = y = 0; rowh = 0; out = []; 
    for c in cubes:
        w, h, d = c[3], c[4], c[5]; fw, fh = 2 * d + 2 * w, d + h
        if x + fw > W: x = 0; y += rowh; rowh = 0
        out.append((x, v0 + y)); x += fw; rowh = max(rowh, fh)
    return v0 + y + rowh

# ---------------------------------------------------------------- generator
def gen(s):
    import copy; s = copy.deepcopy(s)
    pkg, C = s['pkg'], s['C']; sty = s['sty']
    cubes_all = [(pi, c) for pi, p in enumerate(s['parts']) for c in p['cubes']]
    flat = [c for _, c in cubes_all]
    dims = [tuple(math.ceil(v) for v in c[3:6]) for c in flat]
    H = pack([c[:3] + d + c[6:] for c, d in zip(flat, dims)], s['W'], s['H0']); H = (H + 7) // 8 * 8
    pos = pack(flat, s['W'], s['H0']) and None
    # re-pack to get positions
    x = y = 0; rowh = 0; uv = []
    for dm in dims:
        w, h, d = dm; fw, fh = 2 * d + 2 * w, d + h
        if x + fw > s['W']: x = 0; y += rowh; rowh = 0
        uv.append((x, s['H0'] + y)); x += fw; rowh = max(rowh, fh)
    lines = []
    lines.append('        MeshDefinition mesh = %s.mesh();' % s['pm'])
    lines.append('        PartDefinition root = mesh.getRoot();')
    n = 0; k = 0
    for cl in s.get('clear', []):
        path = cl.split('/'); par = 'root' + ''.join('.getChild("%s")' % q for q in path[:-1])
        lines.append('        %s.addOrReplaceChild("%s", CubeListBuilder.create(), PartPose.ZERO);' % (par, path[-1]))
    for p in s['parts']:
        path = p['path'].split('/'); par = 'root' + ''.join('.getChild("%s")' % q for q in path[:-1])
        cb = 'CubeListBuilder.create()'
        for c in p['cubes']:
            cb += '\n            .texOffs(%d, %d).addBox(%s)' % (uv[k][0], uv[k][1], F(*c[:6])); k += 1
        lines.append('        %s.addOrReplaceChild("%s", %s,\n            %s);' % (par, path[-1], cb, p['pose']))
    lines.append('        return LayerDefinition.create(mesh, %d, %d);' % (s['W'], H))
    cf = s.get('clipfn'); clipsrc = ('    @Override\n    protected com.reevil.anim.AnimClip clip(com.reevil.entity.mrx.MrXEntity e, int id) {\n        com.reevil.anim.AnimClip c = com.reevil.client.mrx.SignatureClips.%s(id);\n        return c != null ? c : super.clip(e, id);\n    }\n' % cf) if cf else ''
    src = '''package com.reevil.client.%(pkg)s;

import com.reevil.ReEvilMod;
import com.reevil.client.%(ppkg)s.%(pm)s;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/** Own silhouette (generated by tools/variantgen.py): the %(pm)s rig, so all clips apply, with replaced/added parts. */
public class %(C)sModel extends %(pm)s {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "%(id)s"), "main");
    public %(C)sModel(ModelPart root) { super(root); }
%(clip)s    public static LayerDefinition createBodyLayer() {
%(body)s
    }
}
''' % dict(clip=clipsrc, pkg=pkg, ppkg=s['ppkg'], pm=s['pm'], C=C, id=s['id'], body='\n'.join(lines))
    open(os.path.join(ROOT, pkg, C + 'Model.java'), 'w').write(src)
    # textures
    files = sorted(glob.glob(os.path.join(OUT, s['id'] + '.png')) + glob.glob(os.path.join(OUT, s['id'] + '_*.png')))
    for fi, f in enumerate(files):
        old = Image.open(f).convert('RGBA')
        if old.size[0] != s['W']: print('WIDTH MISMATCH', f, old.size); continue
        old = old.crop((0, 0, s['W'], s['H0']))
        t = Tex(s['W'], H, 7 + fi); t.img.paste(old, (0, 0)); t.px = t.img.load()
        tint = 1.0 if fi == 0 else max(0.6, 0.9 - 0.1 * fi)
        rng = random.Random(fi + 3)
        for c, dm, (u, v) in zip(flat, dims, uv): paint_cube(t, u, v, dm[0], dm[1], dm[2], sty[c[6]], tint, rng)
        t.img.save(f)
    print('generated', s['id'], 'H=', H, 'cubes', len(flat), 'textures', len(files))

SPECS = {}
def spec(**kw): SPECS[kw['id']] = kw

# palettes
FLESH = dict(c=(196, 150, 128), n=0.1); BRUISE = dict(c=(122, 72, 86), n=0.12, d='bruise'); MOUTH = dict(c=(60, 20, 26), d='mouth'); EYE = dict(c=(200, 170, 140), d='eye')
FLESHV = dict(c=(190, 140, 120), n=0.1, d='veins')
BLK = dict(c=(24, 16, 24), n=0.1); BLK2 = dict(c=(52, 28, 46), n=0.1, d='veins'); RED = dict(c=(200, 36, 30), d='redeye'); SPK = dict(c=(70, 40, 60), d='spike')
FUR = dict(c=(92, 80, 68), n=0.1, d='fur'); FUR2 = dict(c=(60, 52, 46), n=0.1, d='fur'); NOSE = dict(c=(24, 18, 18), n=0.05)

spec(id='haos', pkg='haos', C='Haos', ppkg='plant42', pm='Plant42Model', W=256, H0=256,
  clear=['base/stem1/leaf0', 'base/stem1/leaf1', 'base/stem1/leaf2', 'base/stem1/leaf3', 'base/stem1/stem2/head/petalT', 'base/stem1/stem2/head/petalB', 'base/stem1/stem2/head/petalL', 'base/stem1/stem2/head/petalR'],
  sty=dict(f=FLESH, b=BRUISE, m=MOUTH, e=EYE, v=FLESHV),
  parts=[
    dict(path='base', pose='PartPose.offset(0, 24, 0)', cubes=[(-14, -8, -14, 28, 8, 28, 'b'), (-9, -12, -9, 18, 6, 18, 'f'), (-16, -5, 2, 8, 5, 10, 'v')]),
    dict(path='base/stem1', pose='PartPose.offset(0, -5, 0)', cubes=[(-9, -28, -8, 18, 28, 16, 'f'), (-13, -24, -5, 5, 13, 9, 'b'), (8, -18, -6, 6, 14, 10, 'v'), (-6, -14, -10, 12, 5, 3, 'm'),
        (-3, -22, -10, 3, 3, 2, 'e'), (3, -19, -10, 3, 3, 2, 'e'), (9, -27, -2, 3, 15, 3, 'f'), (9, -13, -3, 4, 4, 5, 'v'), (-12, -25, 1, 3, 14, 3, 'f'), (-13, -12, 0, 4, 4, 5, 'v'), (-4, -6, -9, 3, 3, 2, 'e')]),
    dict(path='base/stem1/stem2', pose='PartPose.offset(0, -28, 0)', cubes=[(-8, -24, -8, 16, 24, 16, 'v'), (-10, -14, -4, 4, 8, 8, 'b')]),
    dict(path='base/stem1/stem2/head', pose='PartPose.offset(0, -22, 0)', cubes=[(-11, -20, -11, 22, 20, 22, 'f'), (7, -25, -6, 10, 13, 10, 'b'), (-17, -18, -4, 9, 12, 9, 'v'), (-5, -27, -8, 10, 8, 12, 'f'),
        (-9, -14, -14, 18, 6, 3, 'm'), (-6, -8, -13, 12, 5, 4, 'f'), (-9, -19, -13, 3, 3, 2, 'e'), (5, -17, -13, 3, 3, 2, 'e'), (-1, -23, -13, 3, 3, 2, 'e'), (11, -10, -4, 3, 10, 3, 'f'), (-14, -13, 3, 3, 9, 3, 'v')]),
  ])

spec(id='uroboros', pkg='uroboros', C='Uroboros', ppkg='plant42', pm='Plant42Model', W=256, H0=256,
  clear=['base/stem1/leaf0', 'base/stem1/leaf1', 'base/stem1/leaf2', 'base/stem1/leaf3', 'base/stem1/stem2/head/petalT', 'base/stem1/stem2/head/petalB', 'base/stem1/stem2/head/petalL', 'base/stem1/stem2/head/petalR'],
  sty=dict(k=BLK, k2=BLK2, r=RED, s=SPK),
  parts=[
    dict(path='base', pose='PartPose.offset(0, 24, 0)', cubes=[(-14, -5, -14, 28, 5, 28, 'k'), (-10, -9, -10, 20, 4, 20, 'k2'), (-17, -4, -2, 8, 3, 3, 's'), (9, -4, -2, 8, 3, 3, 's')]),
    dict(path='base/stem1', pose='PartPose.offset(0, -5, 0)', cubes=[(-6, -28, -6, 12, 28, 12, 'k'), (-8, -25, -8, 16, 3, 16, 'k2'), (-8, -15, -8, 16, 3, 16, 'k2'), (-8, -6, -8, 16, 3, 16, 'k2'),
        (6, -21, -1, 6, 2, 2, 's'), (-12, -11, -1, 6, 2, 2, 's'), (-1, -18, 6, 2, 2, 6, 's'), (-1, -8, -12, 2, 2, 6, 's'), (6, -9, 3, 5, 2, 2, 's'), (-11, -23, -3, 5, 2, 2, 's')]),
    dict(path='base/stem1/stem2', pose='PartPose.offset(0, -28, 0)', cubes=[(-5, -24, -5, 10, 24, 10, 'k'), (-7, -16, -7, 14, 3, 14, 'k2'), (-7, -6, -7, 14, 3, 14, 'k2')]),
    dict(path='base/stem1/stem2/head', pose='PartPose.offset(0, -22, 0)', cubes=[(-9, -18, -9, 18, 18, 18, 'k'), (-4, -13, -11, 8, 6, 2, 'r'), (-1, -28, -1, 2, 10, 2, 's'), (8, -14, -1, 8, 2, 2, 's'), (-16, -14, -1, 8, 2, 2, 's'),
        (-1, -14, 8, 2, 2, 8, 's'), (-1, -6, -15, 2, 2, 6, 's'), (-6, -22, -6, 3, 6, 3, 's'), (3, -22, 3, 3, 6, 3, 's'), (-6, -22, 3, 3, 6, 3, 's'), (3, -22, -6, 3, 6, 3, 's'), (-8, -9, -10, 16, 2, 2, 'k2')]),
  ])

spec(id='lycan', pkg='lycan', C='Lycan', ppkg='hunter', pm='HunterModel', W=128, H0=128,
  sty=dict(f=FUR, g=FUR2, n=NOSE, m=MOUTH, e=EYE),
  parts=[
    dict(path='base/hips/chest/neck/head', pose='PartPose.offsetAndRotation(0f, -3.5f, -0.5f, -0.3f, 0f, 0f)', cubes=[(-3.5, -4, -5, 7, 6, 7, 'f'), (-2.5, -2, -12, 5, 3, 7, 'f'), (-1.5, -2.5, -13, 3, 2, 1, 'n'),
        (-3.5, -7.5, -2, 2, 4, 2, 'g'), (1.5, -7.5, -2, 2, 4, 2, 'g'), (-3, -1, -6, 2, 2, 1, 'e'), (1, -1, -6, 2, 2, 1, 'e'), (-4, -2, -2, 8, 4, 5, 'g')]),
    dict(path='base/hips/chest', pose='PartPose.offsetAndRotation(0f, -1f, 0f, 0.55f, 0f, 0f)', cubes=[(-5.5, -13, -4, 11, 13, 8, 'f'), (-7, -15, -5.5, 14, 6, 11, 'g'), (-1, -12, 4, 2, 11, 2, 'g'), (-6.5, -9, -4.5, 13, 6, 9, 'f')]),
    dict(path='base/hips/tail', pose='PartPose.offsetAndRotation(0f, 0f, 2.5f, -0.6f, 0f, 0f)', cubes=[(-2.5, -2.5, 0, 5, 5, 8, 'f')]),
    dict(path='base/hips/tail/tail2', pose='PartPose.offsetAndRotation(0f, 0f, 7.5f, -0.3f, 0f, 0f)', cubes=[(-2, -2, 0, 4, 4, 7, 'g')]),
  ])


# ---- palettes (batch 2)
HAIRW = dict(c=(225, 225, 232), n=0.06, d='fur'); HAIRB = dict(c=(18, 14, 18), n=0.08, d='fur'); ROBE = dict(c=(26, 20, 30), n=0.1); ROBER = dict(c=(118, 28, 34), n=0.1); GOLD = dict(c=(196, 156, 62), n=0.08)
PLATE = dict(c=(122, 114, 104), n=0.1); STEEL = dict(c=(172, 178, 186), n=0.08); BONE = dict(c=(214, 204, 172), n=0.08); FLESH2 = dict(c=(150, 82, 84), n=0.1, d='veins')
MOLD = dict(c=(18, 24, 20), n=0.1, d='bruise'); SACK = dict(c=(168, 138, 98), n=0.12); DARK = dict(c=(8, 6, 8), n=0.02); APRON = dict(c=(142, 124, 110), n=0.1, d='bruise')
WDRESS = dict(c=(234, 230, 228), n=0.05); CLOTH = dict(c=(176, 42, 38), n=0.1); PALE = dict(c=(206, 198, 190), n=0.06); DAUG = dict(c=(150, 150, 164), n=0.06)

def A(path, pose, *cubes): return dict(path=path, pose=pose, cubes=list(cubes))
Z = 'PartPose.ZERO'

spec(id='daughter', pkg='daughter', C='Daughter', ppkg='licker', pm='LickerModel', W=128, H0=64, clear=['base/torso/neck1/neck2/head/jaw/t1', 'base/torso/neck1/neck2/head/jaw/t1/t2', 'base/torso/neck1/neck2/head/jaw/t1/t2/t3', 'base/torso/neck1/neck2/head/jaw/t1/t2/t3/t4', 'base/torso/neck1/neck2/head/jaw/t1/t2/t3/t4/t5', 'base/torso/neck1/neck2/head/jaw/t1/t2/t3/t4/t5/t6'],
  sty=dict(h=HAIRB, d=ROBE, g=GOLD, p=DAUG),
  parts=[A('base/torso/neck1/neck2/head/hair', Z, (-4.5, -5, -0.5, 9, 8, 6, 'h'), (-4, -3, 5, 8, 2, 9, 'h'), (-3, -8, -4, 6, 3, 8, 'h')),
         A('base/skirt', 'PartPose.offsetAndRotation(0, -1, -1, 0.1f, 0, 0)', (-6, -3, -4, 12, 9, 13, 'd'), (-7, 2, -5, 14, 3, 15, 'd')),
         A('base/torso/dress', Z, (-4.5, -4.5, -10.5, 9, 8, 11, 'd'), (-5, -5, -10.5, 10, 1, 4, 'g')),
         A('base/torso/neck1/neck2/head/fangL', Z, (-2.5, 1, -8.4, 1, 3, 1, 'p'), (1.5, 1, -8.4, 1, 3, 1, 'p'))])

spec(id='napad', pkg='napad', C='Napad', ppkg='licker', pm='LickerModel', W=128, H0=64,
  sty=dict(s=STEEL, b=BONE, p=PLATE),
  parts=[A('base/torso/armL/foreL/handL/scythe', 'PartPose.offsetAndRotation(0, 2, -2, 0.2f, 0, 0)', (-0.5, 0, -12, 1, 3, 14, 's'), (-0.5, 0, -16, 1, 2, 5, 's')),
         A('base/torso/armR/foreR/handR/scythe', 'PartPose.offsetAndRotation(0, 2, -2, 0.2f, 0, 0)', (-0.5, 0, -12, 1, 3, 14, 's'), (-0.5, 0, -16, 1, 2, 5, 's')),
         A('base/torso/spines', Z, (-1, -7, -9, 2, 4, 2, 'b'), (-1, -7, -6, 2, 5, 2, 'b'), (-1, -7, -3, 2, 6, 2, 'b'), (-1, -6, 0, 2, 4, 2, 'b')),
         A('base/torso/neck1/neck2/head/snout', Z, (-2.5, -3, -13, 5, 4, 6, 'p'), (-3.5, -7, -2, 2, 4, 2, 'b'), (1.5, -7, -2, 2, 4, 2, 'b'))])

spec(id='reaper', pkg='reaper', C='Reaper', ppkg='licker', pm='LickerModel', W=128, H0=64,
  sty=dict(p=PLATE, b=BONE, d=DARK),
  parts=[A('base/torso/shell', Z, (-5, -6, -10.5, 10, 3, 12, 'p'), (-4, -8, -9, 8, 3, 9, 'p'), (-6, -4, -9, 2, 5, 8, 'p'), (4, -4, -9, 2, 5, 8, 'p')),
         A('base/torso/neck1/neck2/head/mand', Z, (-5, -2, -9, 2, 2, 7, 'b'), (3, -2, -9, 2, 2, 7, 'b'), (-1, -8, -7, 2, 3, 4, 'p')),
         A('base/shellb', Z, (-4, -5, -2, 8, 3, 8, 'p'), (-1, -8, 0, 2, 4, 5, 'b'))])

spec(id='miranda', pkg='miranda', C='Miranda', ppkg='nemesis', pm='NemesisModel', W=128, H0=64,
  sty=dict(h=HAIRW, r=ROBE, g=GOLD, w=WDRESS),
  parts=[A('base/robe', Z, (-6.5, -1, -4, 13, 7, 8, 'r'), (-7.5, 6, -5, 15, 10, 10, 'r'), (-5, -1, -4.5, 10, 2, 1, 'g')),
         A('base/torso/neck/head/hair', Z, (-4, -8, -1, 8, 12, 5, 'h'), (-4, -8, -4, 8, 2, 4, 'h'), (-3, -3, 4, 6, 12, 2, 'h')),
         A('base/torso/mantle', Z, (-6.5, -12, -3.5, 13, 4, 7, 'g'), (-5.5, -9, -3, 11, 3, 6, 'r'))])

spec(id='saddler', pkg='saddler', C='Saddler', ppkg='nemesis', pm='NemesisModel', W=128, H0=64,
  sty=dict(r=ROBER, g=GOLD, d=DARK, k=ROBE),
  parts=[A('base/robe', Z, (-5.5, -1, -3.5, 11, 7, 7, 'r'), (-6.5, 6, -4.5, 13, 10, 9, 'r'), (-1, -1, -4.6, 2, 15, 1, 'g')),
         A('base/torso/neck/head/hood', Z, (-4.5, -8.5, -1, 9, 9, 5.5, 'r'), (-4.5, -8.5, -4.5, 1.5, 7, 4, 'r'), (3, -8.5, -4.5, 1.5, 7, 4, 'r'), (-4, -9.5, -4.5, 8, 1.5, 6, 'r'), (-0.5, -11.5, -1, 1, 3, 1, 'g')),
         A('base/torso/collar', Z, (-6, -12.5, -3, 12, 3, 6, 'g'), (-4.5, -9, -2.6, 9, 5, 1, 'k'))])

spec(id='simmons', pkg='simmons', C='Simmons', ppkg='nemesis', pm='NemesisModel', W=128, H0=64,
  sty=dict(f=FLESH2, b=BONE, k=ROBE, p=PALE),
  parts=[A('base/torso/mass', Z, (-8, -13, -4, 16, 7, 8, 'f'), (-9, -10, 0, 5, 7, 6, 'f'), (4, -9, -4.5, 6, 6, 4, 'f')),
         A('base/torso/armR/lump', Z, (-3.5, -1, -3.5, 7, 7, 7, 'f'), (-4, 4, -1, 2, 6, 2, 'b')),
         A('base/torso/spines', Z, (-3, -13.5, 2.5, 2, 5, 2, 'b'), (1, -12, 2.5, 2, 4, 2, 'b'), (-1, -15, 3, 2, 6, 2, 'b')),
         A('base/torso/neck/head/jawmass', Z, (-3.5, -3, -4.2, 7, 4, 2, 'f'))])

spec(id='eveline', pkg='eveline', C='Eveline', ppkg='nemesis', pm='NemesisModel', W=128, H0=64,
  sty=dict(h=HAIRB, w=WDRESS, b=BONE),
  parts=[A('base/dress', Z, (-6, -1, -3.5, 12, 4, 7, 'w'), (-7.5, 3, -4.5, 15, 9, 9, 'w')),
         A('base/torso/neck/head/hair', Z, (-4, -8, -4, 8, 2, 8, 'h'), (-4.5, -7, -1, 9, 18, 5, 'h'), (-4.5, -7, -4, 1.5, 12, 4, 'h'), (3, -7, -4, 1.5, 12, 4, 'h')),
         A('base/torso/neck/head/bow', Z, (-1, -10, -4, 2, 2, 2, 'w'))])

spec(id='zealot', pkg='zealot', C='Zealot', ppkg='ganado', pm='GanadoModel', W=128, H0=64,
  sty=dict(r=ROBE, g=GOLD, c=CLOTH),
  parts=[A('base/robe', Z, (-5, -1.5, -2.5, 10, 8, 5, 'r'), (-6, 5, -3, 12, 7, 6, 'r')), A('base/torso/neck/head/hood', Z, (-4.5, -8, -1, 9, 9, 5, 'r'), (-4.5, -8, -4.5, 1.5, 7, 4, 'r'), (3, -8, -4.5, 1.5, 7, 4, 'r'), (-4, -9, -4.5, 8, 1.5, 6, 'r')),
         A('base/torso/sash', Z, (-4.5, -10.5, -2.5, 9, 3, 5, 'g'), (-4.4, -7, -2.5, 1, 7, 5, 'c'))])
spec(id='salvador', pkg='salvador', C='Salvador', ppkg='ganado', pm='GanadoModel', W=128, H0=64,
  sty=dict(s=SACK, d=DARK, a=APRON),
  parts=[A('base/torso/neck/head/sack', Z, (-4, -8, -4, 8, 8, 8, 's'), (-3.5, -9, -3.5, 7, 1, 7, 's'), (-2.5, -5, -4.2, 2, 2, 1, 'd'), (0.5, -5, -4.2, 2, 2, 1, 'd')),
         A('base/torso/apron', Z, (-4.5, -9, -2.6, 9, 11, 1, 'a'))])
spec(id='majini', pkg='majini', C='Majini', ppkg='ganado', pm='GanadoModel', W=128, H0=64,
  sty=dict(c=CLOTH, b=BONE, k=ROBE),
  parts=[A('base/torso/neck/head/band', Z, (-4, -5, -4.2, 8, 1.5, 8.4, 'c')), A('base/torso/strap', Z, (-4.3, -10, -2.3, 2, 11, 4.6, 'k'), (2.3, -10, -2.3, 2, 11, 4.6, 'k'))])
spec(id='javo', pkg='javo', C='Javo', ppkg='ganado', pm='GanadoModel', W=128, H0=64,
  sty=dict(f=FLESH2, b=BONE, k=MOLD),
  parts=[A('base/torso/armR/lump', Z, (-3, 0, -3, 6, 7, 6, 'f'), (-1, 6, -4, 2, 5, 2, 'b')), A('base/torso/neck/head/horns', Z, (-3, -9, -1, 1.5, 4, 1.5, 'b'), (1.5, -9, -1, 1.5, 4, 1.5, 'b'), (-3.5, -4, 3, 7, 3, 2, 'f')),
         A('base/torso/spines', Z, (-1, -10, 2, 2, 5, 2, 'b'), (-3.5, -8, 2, 2, 4, 2, 'b'), (1.5, -8, 2, 2, 4, 2, 'b'), (-5.5, -7, -2, 3, 4, 4, 'f'))])
spec(id='molded', pkg='molded', C='Molded', ppkg='ganado', pm='GanadoModel', W=128, H0=64,
  sty=dict(m=MOLD, k=dict(c=(30, 38, 32), n=0.1, d='spike')),
  parts=[A('base/torso/neck/head/mass', Z, (-4, -8, -4, 8, 2, 8, 'm'), (-4.5, -6, -4.5, 3, 6, 3, 'm'), (-1.5, -9, -2, 3, 4, 3, 'k')),
         A('base/torso/mold', Z, (-6.5, -11, -2.5, 3, 4, 5, 'm'), (3.5, -10, -2.5, 3, 3, 5, 'm'), (-1, -11, 2, 2, 6, 3, 'k'), (-4, -9, 2, 2, 5, 3, 'k'), (2, -9, 2, 2, 5, 3, 'k'))])

spec(id='armadura', pkg='armadura', C='Armadura', ppkg='zombie', pm='ZombieModel', W=128, H0=64,
  sty=dict(p=PLATE, s=STEEL, d=DARK),
  parts=[A('body/waist/chest/neck/head/helm', Z, (-4.5, -7, -4.5, 9, 7, 9, 'p'), (-1, -9, -4, 2, 3, 8, 's'), (-3, -4, -4.6, 6, 1, 1, 'd')),
         A('body/waist/chest/plate', Z, (-5, -6, -3, 10, 6, 1, 's'), (-5, -6, 2, 10, 6, 1, 's'), (-7.5, -7, -3, 3, 3, 6, 'p'), (4.5, -7, -3, 3, 3, 6, 'p')),
         A('body/waist/skirt', Z, (-4.5, 0, -2.5, 9, 3, 5, 'p'))])
spec(id='iron_maiden', pkg='ironmaiden', C='IronMaiden', ppkg='zombie', pm='ZombieModel', W=128, H0=64,
  sty=dict(s=STEEL, d=DARK, r=dict(c=(110, 30, 26), n=0.1)),
  parts=[A('body/waist/chest/cage', Z, (-6, -8, -4, 12, 10, 8, 's'), (-5, -7, -6.5, 1, 1, 3, 'r'), (-1, -7, -6.5, 1, 1, 3, 'r'), (3, -7, -6.5, 1, 1, 3, 'r'), (-3, -3, -6.5, 1, 1, 3, 'r'), (1, -3, -6.5, 1, 1, 3, 'r'), (-1, 0, -6.5, 1, 1, 3, 'r')),
         A('body/waist/chest/neck/head/helm', Z, (-4.5, -7, -4.5, 9, 7, 9, 's'), (-3, -4, -4.6, 6, 1, 1, 'd'))])
spec(id='regenerador', pkg='regenerador', C='Regenerador', ppkg='zombie', pm='ZombieModel', W=128, H0=64,
  sty=dict(b=BONE, m=MOUTH, f=FLESH2),
  parts=[A('body/waist/chest/ribs', Z, (-4, -5, -3, 8, 1, 1, 'b'), (-4, -3, -3, 8, 1, 1, 'b'), (-4, -1, -3, 8, 1, 1, 'b'), (-2, -4, -3.2, 4, 3, 1, 'm')),
         A('body/waist/chest/neck/head/crown', Z, (-3, -8, -3, 6, 2, 6, 'f'), (-1, -10, -1, 2, 3, 2, 'b'))])
spec(id='pale_head', pkg='palehead', C='PaleHead', ppkg='zombie', pm='ZombieModel', W=128, H0=64,
  sty=dict(p=PALE, b=dict(c=(150, 60, 66), n=0.1, d='veins')),
  parts=[A('body/waist/chest/neck/head/dome', Z, (-3.5, -12, -3.5, 7, 7, 7, 'p'), (-4, -9, -4, 8, 2, 8, 'b')), A('body/waist/chest/neck/head/fangs', Z, (-2.5, 0, -5.8, 1, 2, 1, 'p'), (1.5, 0, -5.8, 1, 2, 1, 'p'))])
spec(id='fat_molded', pkg='fatmolded', C='FatMolded', ppkg='zombie', pm='ZombieModel', W=128, H0=64,
  sty=dict(m=MOLD, k=dict(c=(30, 38, 32), n=0.1, d='spike')),
  parts=[A('body/waist/chest/belly', Z, (-6.5, -4, -4.5, 13, 10, 9, 'm'), (-3, -6, -5, 6, 3, 3, 'k')), A('body/waist/chest/neck/head/lump', Z, (-3, -8, -3, 6, 3, 6, 'm'), (2, -6, -4.5, 3, 4, 3, 'k')),
         A('body/waist/chest/spikes', Z, (-5, -7, 2.5, 2, 4, 3, 'k'), (-1, -8, 2.5, 2, 5, 3, 'k'), (3, -7, 2.5, 2, 4, 3, 'k'))])


# ---- batch 3: Mr.X rig family, El Gigante family, Krauser family
HAIRBR = dict(c=(92, 58, 34), n=0.08, d='fur'); WOOD = dict(c=(120, 84, 52), n=0.1); WHT = dict(c=(238, 234, 230), n=0.04); PINK = dict(c=(190, 80, 90), n=0.08)
TAN = dict(c=(196, 176, 130), n=0.08); OLIVE = dict(c=(70, 82, 56), n=0.1); BLOOD = dict(c=(120, 20, 22), n=0.12, d='bruise')
SPKB = dict(c=(200, 190, 160), n=0.06, d='spike')

spec(clipfn='wesker', id='jill', pkg='jill', C='Jill', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(h=HAIRBR, r=RED, k=ROBE),
  parts=[A('base/torso/neck/head/bob', Z, (-4.5, -8, -4.5, 9, 3, 9, 'h'), (-4.5, -6, 0.5, 9, 7, 4, 'h'), (-4.5, -6, -4.5, 1.5, 4, 4, 'h'), (3, -6, -4.5, 1.5, 4, 4, 'h')),
         A('base/torso/device', Z, (-2, -8, -2.8, 4, 4, 1, 'r'), (-4.6, -10, -2.6, 9.2, 1, 5.2, 'k'))])
spec(clipfn='heisenberg', id='heisenberg', pkg='heisenberg', C='Heisenberg', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(s=STEEL, d=DARK, w=WOOD, k=ROBE),
  parts=[A('base/torso/armL/gauntlet', Z, (-2.8, -0.5, -2.8, 5.6, 9, 5.6, 's')), A('base/torso/armL/foreL/gauntlet2', Z, (-2.8, 0, -2.8, 5.6, 9, 5.6, 's'), (-3.2, 8, -1, 1, 4, 2, 's')),
         A('base/torso/neck/head/cap', Z, (-4.5, -8.5, -4.5, 9, 2, 9, 'd'), (-3.5, -7, -6.5, 7, 1, 3, 'd')),
         A('base/torso/armR/foreR/handR/hammer', 'PartPose.offsetAndRotation(0, 2, -1, -0.5f, 0, 0)', (-0.6, -2, -0.6, 1.2, 22, 1.2, 'w'), (-3, 19, -3, 6, 7, 6, 's'), (-3.5, 20.5, -2, 7, 4, 4, 's')),
         A('base/torso/coat2', Z, (-5, -3, -3, 10, 7, 6, 'k'))])
spec(clipfn='jack', id='jack_baker', pkg='jackbaker', C='JackBaker', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(s=STEEL, w=WOOD, b=BLOOD, a=APRON),
  parts=[A('base/torso/armR/foreR/handR/shovel', 'PartPose.offsetAndRotation(0, 2, -1, -0.5f, 0, 0)', (-0.6, -4, -0.6, 1.2, 24, 1.2, 'w'), (-3, 19, -1, 6, 8, 1.5, 's'), (-0.5, 18, -1.2, 1, 6, 2, 'b')),
         A('base/torso/stains', Z, (-4.6, -10, -2.6, 9.2, 5, 5.2, 'b'))])
spec(clipfn='claws', id='verdugo', pkg='verdugo', C='Verdugo', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(s=STEEL, b=BONE, f=FLESH2, d=DARK),
  parts=[A('base/torso/armR/foreR/blade', Z, (-0.5, 1, -3, 1, 14, 3, 's'), (-0.5, 12, -5, 1, 4, 2, 's')), A('base/torso/armL/foreL/blade', Z, (-0.5, 1, -3, 1, 14, 3, 's'), (-0.5, 12, -5, 1, 4, 2, 's')),
         A('base/torso/neck/head/mask', Z, (-3.8, -6.5, -4.4, 7.6, 5, 1, 'b'), (-3.5, -5, -4.8, 7, 1, 1, 'd')),
         A('base/torso/spine', Z, (-1, -10, 2.5, 2, 4, 2, 'b'), (-1, -7, 2.5, 2, 4, 2, 'b'), (-1, -4, 2.5, 2, 4, 2, 'b'), (-6.5, -11, -2, 3, 3, 4, 'f'), (3.5, -11, -2, 3, 3, 4, 'f'))])
spec(id='ustanak', pkg='ustanak', C='Ustanak', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(p=PLATE, s=STEEL, d=DARK),
  parts=[A('base/torso/plate', Z, (-5, -10.5, -3, 10, 6, 1, 'p'), (-8, -12, -3, 4, 4, 6, 'p'), (4, -12, -3, 4, 4, 6, 'p'), (-4.5, -4, -3, 9, 3, 1, 's')),
         A('base/torso/neck/head/mask', Z, (-4, -7.5, -4.4, 8, 5, 1.2, 'p'), (-3, -4.5, -4.8, 6, 1, 1, 'd'), (-4.5, -8, -4.5, 9, 2, 9, 'p'))])
spec(clipfn='claws', id='dimitrescu', pkg='dimitrescu', C='Dimitrescu', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(w=WHT, d=DARK, h=HAIRB, p=PINK),
  parts=[A('base/torso/neck/head/hat', Z, (-8, -8.5, -8, 16, 1, 16, 'w'), (-4.5, -12, -4.5, 9, 4, 9, 'w'), (-4.6, -9.5, -4.6, 9.2, 1, 9.2, 'p')),
         A('base/torso/neck/head/hair', Z, (-4.5, -7, 1, 9, 11, 3, 'h')),
         A('base/skirt', Z, (-5.5, -2, -3.5, 11, 8, 7, 'w'), (-6.5, 6, -4.5, 13, 10, 9, 'w'), (-6.6, 14, -4.6, 13.2, 1, 9.2, 'p'))])
spec(id='sturm', pkg='sturm', C='Sturm', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(p=PLATE, s=STEEL, d=DARK, o=dict(c=(176, 92, 30), n=0.08)),
  parts=[A('base/torso/neck/head/helm', Z, (-4.5, -8, -4.5, 9, 9, 9, 'p'), (-3, -5, -4.7, 6, 1.5, 1, 'd')), A('base/torso/tanks', Z, (-4, -10, 2.5, 3, 11, 3, 's'), (1, -10, 2.5, 3, 11, 3, 's'), (-3.5, -11, 2.8, 7, 1, 1, 'o')),
         A('base/torso/armor', Z, (-5, -10.5, -3, 10, 7, 1, 'p'), (-8, -12, -3, 4, 5, 6, 'p'), (4, -12, -3, 4, 5, 6, 'p')),
         A('base/torso/armR/foreR/handR/nozzle', 'PartPose.offsetAndRotation(0, 2, 0, -1.4f, 0, 0)', (-1, -1, -9, 2, 2, 10, 'd'), (-1.5, -1.5, -2, 3, 3, 3, 's'))])
spec(id='urias', pkg='urias', C='Urias', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(f=FLESH2, b=SPKB, p=PALE),
  parts=[A('base/torso/muscle', Z, (-5.5, -10.5, -3, 11, 7, 6, 'f'), (-8, -12, -3.5, 4, 4, 7, 'f'), (4, -12, -3.5, 4, 4, 7, 'f')), A('base/torso/ridge', Z, (-1, -11, 2.5, 2, 6, 3, 'b'), (-4, -9, 2.5, 2, 5, 3, 'b'), (2, -9, 2.5, 2, 5, 3, 'b'), (-1, -3, 2.5, 2, 4, 3, 'b')),
         A('base/torso/neck/head/crest', Z, (-1, -10, -3, 2, 4, 6, 'b'), (-4, -4, -4.4, 8, 2, 1, 'f'))])
spec(id='adjule', pkg='adjule', C='Adjule', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(c=CLOTH, t=TAN, d=DARK),
  parts=[A('base/torso/neck/head/turban', Z, (-4.5, -9, -4.5, 9, 5, 9, 'c'), (-4.5, -5, -4.5, 9, 2, 9, 't'), (-4, -4.5, -4.6, 8, 2.5, 1, 'd')), A('base/torso/cape', 'PartPose.offsetAndRotation(0, -10, 2.6f, 0.12f, 0, 0)', (-5, 0, 0, 10, 16, 1, 'c'), (-5, 0, 0.1, 10, 2, 1, 't'))])
spec(id='ndesu', pkg='ndesu', C='Ndesu', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(b=SPKB, f=FLESH2, w=WOOD, s=STEEL, t=TAN),
  parts=[A('base/torso/spikes', Z, (-7.5, -12, -1, 3, 3, 3, 'b'), (4.5, -12, -1, 3, 3, 3, 'b'), (-1, -12, 2.5, 2, 6, 2, 'b'), (-4, -9, 2.5, 2, 5, 2, 'b'), (2, -9, 2.5, 2, 5, 2, 'b')),
         A('base/loin', Z, (-5, 0, -3, 10, 7, 1, 't'), (-5, 0, 2, 10, 7, 1, 't')),
         A('base/torso/armR/foreR/handR/mace', 'PartPose.offsetAndRotation(0, 2, -1, -0.5f, 0, 0)', (-0.6, -2, -0.6, 1.2, 18, 1.2, 'w'), (-3, 14, -3, 6, 7, 6, 's'), (-0.5, 12, -4.5, 1, 1, 9, 'b'))])

spec(id='ogroman', pkg='ogroman', C='Ogroman', ppkg='elgigante', pm='ElGiganteModel', W=128, H0=64, sty=dict(f=FLESH2, b=SPKB),
  parts=[A('base/torso/tumors', Z, (-7, -11, 1, 5, 5, 5, 'f'), (3, -9, 2, 4, 4, 4, 'f'), (-3, -6, 2.5, 5, 4, 4, 'f')), A('base/torso/horns', Z, (-8.5, -12, -1, 3, 4, 3, 'b'), (5.5, -12, -1, 3, 4, 3, 'b'), (-1, -12, 2.5, 2, 5, 2, 'b')),
         A('base/torso/armR/lump', Z, (-3.5, 0, -3.5, 7, 6, 7, 'f'))])
spec(id='executioner_majini', pkg='executionermajini', C='ExecutionerMajini', ppkg='elgigante', pm='ElGiganteModel', W=128, H0=64, sty=dict(s=SACK, d=DARK, w=WOOD, m=STEEL, a=APRON),
  parts=[A('base/torso/neck/head/hood', Z, (-4, -8.5, -4, 8, 9, 8, 's'), (-3, -5, -4.2, 2, 2, 1, 'd'), (1, -5, -4.2, 2, 2, 1, 'd')), A('base/torso/apron', Z, (-4.6, -10, -2.6, 9.2, 12, 1, 'a')),
         A('base/torso/armR/foreR/handR/axe', 'PartPose.offsetAndRotation(0, 2, -1, -0.5f, 0, 0)', (-0.7, -4, -0.7, 1.4, 26, 1.4, 'w'), (-0.6, 16, -9, 1.2, 10, 10, 'm'), (-1, 16, -10, 2, 10, 2, 'm'))])
spec(id='mendez', pkg='mendez', C='Mendez', ppkg='elgigante', pm='ElGiganteModel', W=128, H0=64, sty=dict(f=FLESH2, p=PALE, b=SPKB, k=ROBE),
  parts=[A('base/torso/neck/head/jawmass', Z, (-3.5, -3, -4.3, 7, 5, 2, 'f'), (-1.5, -2, -5.5, 3, 3, 1, 'b')), A('base/torso/worm', Z, (-3, -11, 2.5, 6, 8, 4, 'f'), (-2, -14, 3.5, 4, 4, 3, 'f'), (-1, -16, 4, 2, 3, 2, 'b')),
         A('base/torso/coat2', Z, (-5, -2, -3, 10, 8, 6, 'k'))])
spec(id='soldat', pkg='soldat', C='Soldat', ppkg='krauser', pm='KrauserModel', W=128, H0=64, sty=dict(o=OLIVE, d=DARK, s=STEEL),
  parts=[A('base/torso/neck/head/helm', Z, (-4, -8, -4, 8, 4, 8, 'o'), (-3, -5, -4.4, 6, 3, 1, 'd'), (-2, -3, -5, 4, 3, 2, 's')), A('base/torso/vest', Z, (-4.6, -10, -2.6, 9.2, 8, 5.2, 'o'), (-3, -9, -2.9, 2, 2, 1, 's'), (1, -9, -2.9, 2, 2, 1, 's'))])
spec(id='gatling_majini', pkg='gatlingmajini', C='GatlingMajini', ppkg='krauser', pm='KrauserModel', W=128, H0=64, sty=dict(s=STEEL, d=DARK, t=TAN),
  parts=[A('base/torso/armR/foreR/handR/gatling', 'PartPose.offsetAndRotation(0, 2, 0, -1.4f, 0, 0)', (-2, -2, -4, 4, 4, 6, 'd'), (-1.5, -1.5, -14, 1, 1, 10, 's'), (0.5, -1.5, -14, 1, 1, 10, 's'), (-0.5, 0.5, -14, 1, 1, 10, 's'), (-0.5, -2.5, -14, 1, 1, 10, 's')),
         A('base/torso/ammo', Z, (-4.6, -9, -2.6, 9.2, 2, 5.2, 't'), (-3, -7, 2.5, 6, 6, 3, 'd'))])

if __name__ == '__main__':
    for k in (sys.argv[1:] or SPECS): gen(SPECS[k])
