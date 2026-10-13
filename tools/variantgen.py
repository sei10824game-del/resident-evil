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
    elif det == 'face':
        hc = sty.get('hc', (40, 36, 34)); ec = sty.get('ec', (16, 10, 10)); mc = sty.get('mc', (70, 20, 24))
        for nm in ('top', 'back'): x, y, ww, hh = fc[nm]; t.rect(x, y, ww, hh, hc, 0.08)
        for nm in ('left', 'right'):
            x, y, ww, hh = fc[nm]; t.rect(x, y, ww, max(1, hh // 2), hc, 0.08)
        t.rect(fx, fy, fw, max(1, int(fh * 0.22)), hc, 0.08)
        ey = fy + int(fh * 0.45)
        if sty.get('shades'): t.rect(fx, ey, fw, 2, (8, 8, 10), 0.02)
        else:
            t.rect(fx + max(1, fw // 5), ey, max(1, fw // 5), 1, ec, 0); t.rect(fx + fw - 2 * max(1, fw // 5), ey, max(1, fw // 5), 1, ec, 0)
        if sty.get('beard'):
            t.rect(fx, fy + int(fh * 0.62), fw, fh - int(fh * 0.62), hc, 0.08)
            for nm in ('left', 'right'): x, y, ww, hh = fc[nm]; t.rect(x, y + hh // 2, ww, hh - hh // 2, hc, 0.08)
        t.rect(fx + fw // 4, fy + int(fh * 0.8), max(2, fw // 2), 1, mc, 0.05)
        if sty.get('fringe'): t.rect(fx, fy, fw, max(2, int(fh * 0.5)), hc, 0.08)
    elif det == 'bruise':
        t.blots(fx, fy, fw, fh, shade(base, 0.72), max(1, fw * fh // 30), 2)
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
        lines.append('        %s.addOrReplaceChild("%s", %s,\n            %s);' % (par, path[-1], cb, re.sub(r'(?<![\w.])(-?\d+\.\d+)(?![\df])', r'\1f', p['pose'])))
    lines.append('        return LayerDefinition.create(mesh, %d, %d);' % (s['W'], H))
    cf = s.get('clipfn'); cc = s.get('clipcls', 'com.reevil.client.mrx.SignatureClips'); ce = s.get('clipent', 'com.reevil.entity.mrx.MrXEntity')
    clipsrc = ('    @Override\n    protected com.reevil.anim.AnimClip clip(%s e, int id) {\n        com.reevil.anim.AnimClip c = %s.%s(id);\n        return c != null ? c : super.clip(e, id);\n    }\n' % (ce, cc, cf)) if cf else ''
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
    for suf in s.get('svar', {}):
        pth = os.path.join(OUT, s['id'] + suf + '.png')
        if not os.path.exists(pth): Image.new('RGBA', (s['W'], s['H0']), (0, 0, 0, 0)).save(pth)
    if s.get('texfrom'):   # seed the base texture from another PNG (recoloured)
        src = Image.open(os.path.join(OUT, s['texfrom'][0] + '.png')).convert('RGBA'); s['texfrom'][1](src).save(os.path.join(OUT, s['id'] + '.png'))
    files = sorted(glob.glob(os.path.join(OUT, s['id'] + '.png')) + glob.glob(os.path.join(OUT, s['id'] + '_*.png')))
    for fi, f in enumerate(files):
        old = Image.open(f).convert('RGBA')
        if old.size[0] != s['W']: print('width mismatch -> blank base', f, old.size); old = Image.new('RGBA', (s['W'], s['H0']), (0, 0, 0, 0))
        old = old.crop((0, 0, s['W'], s['H0']))
        t = Tex(s['W'], H, 7 + fi); t.img.paste(old, (0, 0)); t.px = t.img.load()
        tint = 1.0 if fi == 0 else max(0.6, 0.9 - 0.1 * fi)
        rng = random.Random(fi + 3)
        suf = os.path.basename(f)[len(s['id']):-4]
        sty2 = dict(sty); sty2.update(s.get('svar', {}).get(suf, {}))
        if suf in s.get('svar', {}): tint = 1.0
        for c, dm, (u, v) in zip(flat, dims, uv): paint_cube(t, u, v, dm[0], dm[1], dm[2], sty2[c[6]], tint, rng)
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


# ---- batch 4: full-body rebuilds
GSKIN = dict(c=(150, 146, 140), n=0.1, d='bruise'); GSHIRT = dict(c=(176, 176, 170), n=0.12, d='bruise'); GPANTS = dict(c=(58, 60, 58), n=0.1, d='bruise')
GHAIR = dict(c=(52, 52, 56), n=0.1, d='fur'); GFACE = dict(c=(150, 146, 140), n=0.08, d='face', hc=(48, 48, 52), mc=(90, 24, 28), fringe=True)

def limb2(path_pairs): pass

def lycan_parts():
    P = [A('base/hips', 'PartPose.offset(0, -4.5, 0)', (-5, -2, -3.5, 10, 6, 7, 'pa')),
         A('base/hips/chest', 'PartPose.offsetAndRotation(0, -1, 0, 0.55f, 0, 0)', (-7, -13, -4.5, 14, 13, 9, 'sh'), (-10, -14, -3.5, 4, 6, 7, 'sk'), (6, -14, -3.5, 4, 6, 7, 'sk'), (-1, -12, 4.5, 2, 6, 2, 'sk')),
         A('base/hips/chest/neck', 'PartPose.offsetAndRotation(0, -12.5, -1, -0.25f, 0, 0)', (-3, -4, -3, 6, 5, 6, 'sk')),
         A('base/hips/chest/neck/head', 'PartPose.offsetAndRotation(0, -3.5, -0.5, -0.3f, 0, 0)', (-4, -7, -4, 8, 8, 8, 'fc'), (-4.5, -8, -4.5, 9, 3, 9, 'hr'), (-4.5, -6, 0, 9, 12, 4, 'hr'), (-4.8, -6, -4.8, 9.6, 4, 3, 'hr'), (-4.8, -6, -3, 1.5, 12, 4, 'hr'), (3.3, -6, -3, 1.5, 12, 4, 'hr')),
         A('base/hips/chest/neck/head/jaw', 'PartPose.offset(0, 0.5, -4.5)', (-3, 0, 0, 6, 2, 4.5, 'sk')),
         A('base/hips/tail', 'PartPose.offsetAndRotation(0, 0, 2.5, -0.6f, 0, 0)', ), A('base/hips/tail/tail2', 'PartPose.offsetAndRotation(0, 0, 7.5, -0.3f, 0, 0)', )]
    P = [p for p in P if p['cubes']] + []
    for n, sx in (('L', 1), ('R', -1)):
        P += [A('base/hips/chest/arm' + n, 'PartPose.offsetAndRotation(%d, -11, 0, -0.72f, 0, %sf)' % (int(7.5 * sx) if False else (7 if sx > 0 else -7), -0.1 * sx), (-3, -1.5, -3, 6, 9, 6, 'sh')),
              A('base/hips/chest/arm%s/fore%s' % (n, n), 'PartPose.offsetAndRotation(%sf, 6.5f, 0, -0.35f, 0, 0)' % (0.5 * sx), (-2.8, 0, -2.8, 5.6, 9, 5.6, 'sk')),
              A('base/hips/chest/arm%s/fore%s/hand%s' % (n, n, n), 'PartPose.offset(0, 7.5f, 0)', (-3.2, 0, -3.2, 6.4, 4, 6.4, 'sk'), (-3.2, 3.5, -3.2, 1.5, 2.5, 1.5, 'sk'), (-0.7, 3.5, -3.4, 1.5, 3, 1.5, 'sk'), (1.7, 3.5, -3.2, 1.5, 2.5, 1.5, 'sk')),
              A('base/thigh' + n, 'PartPose.offsetAndRotation(%sf, -3.5f, 0, -0.55f, 0, 0)' % (3 * sx), (-3.2, -1, -3.5, 6.4, 9, 7, 'pa')),
              A('base/thigh%s/shin%s' % (n, n), 'PartPose.offsetAndRotation(0, 6, 0, 1.1f, 0, 0)', (-2.8, 0, -2.8, 5.6, 8, 5.6, 'pa')),
              A('base/thigh%s/shin%s/foot%s' % (n, n, n), 'PartPose.offsetAndRotation(0, 6, 0, -0.7f, 0, 0)', (-2.8, 0, -2.8, 5.6, 6, 5.6, 'sk')),
              A('base/thigh%s/shin%s/foot%s/toes%s' % (n, n, n, n), 'PartPose.offsetAndRotation(0, 4.5f, 0, 0.15f, 0, 0)', (-3.2, 0, -6, 6.4, 2, 8, 'sk'))]
    P.append(A('base/hips/chest/armR/foreR/handR/axe', 'PartPose.offsetAndRotation(0, 3, -1, -1.2f, 0, 0)', (-0.7, -3, -0.7, 1.4, 20, 1.4, 'wd'), (-0.6, 14, -7, 1.2, 8, 8, 'st')))
    return P
LY_CLEAR = ['base/hips/tail', 'base/hips/tail/tail2', 'base/hips/chest/armL/foreL/handL/clawL1', 'base/hips/chest/armL/foreL/handL/clawL2', 'base/hips/chest/armL/foreL/handL/clawL3', 'base/hips/chest/armR/foreR/handR/clawR1', 'base/hips/chest/armR/foreR/handR/clawR2', 'base/hips/chest/armR/foreR/handR/clawR3']
spec(id='lycan', pkg='lycan', C='Lycan', ppkg='hunter', pm='HunterModel', W=128, H0=128, clear=LY_CLEAR,
  sty=dict(sk=GSKIN, sh=GSHIRT, pa=GPANTS, hr=GHAIR, fc=GFACE, wd=WOOD, st=STEEL), parts=lycan_parts())

# ---- batch 5: Mr.X-rig humans rebuilt with own proportions
def humanoid(torso, head, arm, fore, hand, thigh, shin, foot, coat=None, skirt=None, neck=None, hips=None):
    """MrX rig poses. Each arg = list of cube tuples (x,y,z,w,h,d,style)."""
    P = []
    if hips: P.append(A('base', 'PartPose.offset(0, 6.5, 0)', *hips))
    P.append(A('base/torso', 'PartPose.offset(0, -1.5, 0)', *torso))
    if neck: P.append(A('base/torso/neck', 'PartPose.offset(0, -10, 0)', *neck))
    P.append(A('base/torso/neck/head', 'PartPose.offset(0, -1, 0)', *head))
    for n, sg in (('L', 1), ('R', -1)):
        P += [A('base/torso/arm' + n, 'PartPose.offsetAndRotation(%sf, -9, 0, 0, 0, %sf)' % (6.5 * sg, 0.04 * sg), *arm),
              A('base/torso/arm%s/fore%s' % (n, n), 'PartPose.offsetAndRotation(0, 9, 0, -0.1f, 0, 0)', *fore),
              A('base/torso/arm%s/fore%s/hand%s' % (n, n, n), 'PartPose.offset(0, 9, 0)', *hand),
              A('base/thigh' + n, 'PartPose.offset(%sf, 1.5f, 0)' % (2 * sg), *thigh),
              A('base/thigh%s/shin%s' % (n, n), 'PartPose.offset(0, 7, 0)', *shin),
              A('base/thigh%s/shin%s/foot%s' % (n, n, n), 'PartPose.offset(0, 7, 0)', *foot)]
        P.append(A('base/thigh%s/coat%s' % (n, n), 'PartPose.ZERO', *(coat or [(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'sk')])))
    return P

# --- Jack Baker: big, bulky southern man: torn dirty shirt, overalls/jeans, beard
JSKIN = dict(c=(190, 150, 124), n=0.1, d='bruise'); JSHIRT = dict(c=(150, 150, 138), n=0.14, d='bruise'); JJEANS = dict(c=(60, 76, 108), n=0.12, d='bruise'); JBOOT = dict(c=(52, 38, 28), n=0.1)
JFACE = dict(c=(190, 150, 124), n=0.08, d='face', hc=(70, 52, 38), beard=True, mc=(110, 40, 40))
jk = humanoid(torso=[(-6, -10, -3.5, 12, 10, 7, 'sh'), (-6.2, -3, -3.7, 12.4, 3, 7.4, 'jn')], neck=[(-2.5, -1, -2.5, 5, 2, 5, 'sk')],
  head=[(-4, -8, -4, 8, 8, 8, 'fc'), (-4.3, -9, -4.3, 8.6, 2.5, 8.6, 'hr')],
  arm=[(-3.5, 0, -3, 7, 9, 6, 'sk'), (-3.7, -0.5, -3.2, 7.4, 4, 6.4, 'sh')], fore=[(-3, 0, -3, 6, 9, 6, 'sk')], hand=[(-2.8, 0, -2.8, 5.6, 4, 5.6, 'sk')],
  thigh=[(-3.2, 0, -3.2, 6.4, 7, 6.4, 'jn')], shin=[(-3, 0, -3, 6, 7, 6, 'jn')], foot=[(-3, 0, -6.5, 6, 3, 9, 'bt')])
spec(id='jack_baker', pkg='jackbaker', C='JackBaker', ppkg='mrx', pm='MrXModel', W=128, H0=64, clipfn='jack', clear=['base/torso/neck/head/hatBrim/hatCrown', 'base/torso/neck/head/hatBrim'],
  sty=dict(sk=JSKIN, sh=JSHIRT, jn=JJEANS, bt=JBOOT, fc=JFACE, hr=dict(c=(70, 52, 38), n=0.1, d='fur'), s=STEEL, w=WOOD, b=BLOOD, a=APRON),
  parts=jk + [A('base/torso/armR/foreR/handR/shovel', 'PartPose.offsetAndRotation(0, 2, -1, -0.5f, 0, 0)', (-0.6, -4, -0.6, 1.2, 24, 1.2, 'w'), (-3, 19, -1, 6, 8, 1.5, 's'), (-0.5, 18, -1.2, 1, 6, 2, 'b')),
              A('base/torso/stains', Z, (-6.3, -10, -3.8, 12.6, 5, 7.6, 'b'))])

# --- Heisenberg: stocky, long black leather coat, shades, flat cap, steel left arm
HCOAT = dict(c=(26, 24, 28), n=0.1); HSHIRT = dict(c=(150, 60, 60), n=0.08); HSKIN = dict(c=(196, 160, 134), n=0.08); HPANTS = dict(c=(40, 40, 46), n=0.08)
HFACE = dict(c=(196, 160, 134), n=0.08, d='face', hc=(30, 26, 26), shades=True, beard=True)
hb = humanoid(torso=[(-5.5, -10, -3, 11, 10, 6, 'cw'), (-2, -10, -3.1, 4, 8, 1, 'sr')], neck=[(-2, -1, -2, 4, 2, 4, 'sk')],
  head=[(-3.5, -7, -3.5, 7, 7, 7, 'fc'), (-4, -8, -4, 8, 2, 8, 'ca')], arm=[(-2.5, 0, -2.5, 5, 9, 5, 'cw')], fore=[(-2.4, 0, -2.4, 4.8, 9, 4.8, 'cw')], hand=[(-2, 0, -2, 4, 3, 4, 'sk')],
  thigh=[(-2.3, 0, -2.3, 4.6, 7, 4.6, 'pn')], shin=[(-2.3, 0, -2.3, 4.6, 7, 4.6, 'pn')], foot=[(-2.3, 0, -5, 4.6, 2, 7, 'ca')],
  coat=[(-3.2, -1, -3.8, 6.4, 14, 7.6, 'cw')])
spec(id='heisenberg', pkg='heisenberg', C='Heisenberg', ppkg='mrx', pm='MrXModel', W=128, H0=64, clipfn='heisenberg', clear=['base/torso/neck/head/hatBrim/hatCrown', 'base/torso/neck/head/hatBrim'],
  sty=dict(cw=HCOAT, sr=HSHIRT, sk=HSKIN, pn=HPANTS, fc=HFACE, ca=dict(c=(30, 28, 30), n=0.06), s=STEEL, w=WOOD, d=DARK, k=ROBE),
  parts=hb + [A('base/torso/armL/gauntlet', Z, (-3, -0.5, -3, 6, 9, 6, 's')), A('base/torso/armL/foreL/gauntlet2', Z, (-3, 0, -3, 6, 9, 6, 's'), (-3.4, 8, -1, 1, 4, 2, 's')),
              A('base/torso/armR/foreR/handR/hammer', 'PartPose.offsetAndRotation(0, 2, -1, -0.5f, 0, 0)', (-0.6, -2, -0.6, 1.2, 22, 1.2, 'w'), (-3, 19, -3, 6, 7, 6, 's'), (-3.5, 20.5, -2, 7, 4, 4, 's'))])

# --- Verdugo: tall emaciated skinless-looking bio-weapon, long thin limbs, exposed ribs/muscle, bare jaw
VSKIN = dict(c=(122, 130, 112), n=0.12, d='veins'); VMUS = dict(c=(150, 52, 54), n=0.12, d='veins'); VBONE = dict(c=(214, 204, 172), n=0.08)
VFACE = dict(c=(122, 130, 112), n=0.08, d='face', hc=(122, 130, 112), ec=(220, 40, 30), mc=(60, 14, 18))
vd = humanoid(torso=[(-3.5, -10, -2, 7, 10, 4, 'mu'), (-3.7, -9, -2.2, 7.4, 1, 4.4, 'bn'), (-3.7, -7, -2.2, 7.4, 1, 4.4, 'bn'), (-3.7, -5, -2.2, 7.4, 1, 4.4, 'bn'), (-1, -10, 2, 2, 8, 2, 'bn')], neck=[(-1.5, -2, -1.5, 3, 4, 3, 'sk')],
  head=[(-3, -8, -3, 6, 8, 6, 'fc'), (-1, -10, -2, 2, 3, 4, 'bn')], arm=[(-1.5, 0, -1.5, 3, 11, 3, 'sk')], fore=[(-1.5, 0, -1.5, 3, 11, 3, 'sk')], hand=[(-2, 0, -2, 4, 4, 4, 'sk'), (-2, 3.5, -2, 1, 5, 1, 'bn'), (-0.5, 3.5, -2, 1, 6, 1, 'bn'), (1, 3.5, -2, 1, 5, 1, 'bn')],
  thigh=[(-1.7, 0, -1.7, 3.4, 8, 3.4, 'mu')], shin=[(-1.5, 0, -1.5, 3, 8, 3, 'sk')], foot=[(-2, 0, -5, 4, 2, 6, 'sk')], coat=[(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'sk')])
spec(id='verdugo', pkg='verdugo', C='Verdugo', ppkg='mrx', pm='MrXModel', W=128, H0=64, clipfn='claws', clear=['base/torso/neck/head/hatBrim/hatCrown', 'base/torso/neck/head/hatBrim'],
  sty=dict(sk=VSKIN, mu=VMUS, bn=VBONE, fc=VFACE, s=STEEL),
  parts=vd + [A('base/torso/armR/foreR/blade', Z, (-0.5, 1, -3, 1, 15, 3, 's'), (-0.5, 13, -5, 1, 4, 2, 's')), A('base/torso/armL/foreL/blade', Z, (-0.5, 1, -3, 1, 15, 3, 's'), (-0.5, 13, -5, 1, 4, 2, 's'))])


# ---- batch 6: moths, Neptune family, Colmillos, Gnezdo, Grave Digger
DK = dict(c=(30, 28, 20), n=0.08); YB = dict(c=(214, 172, 40), n=0.08); FZ = dict(c=(220, 200, 150), n=0.12, d='fur'); WG = dict(c=(200, 90, 40), n=0.1); SP = dict(c=(40, 20, 60), n=0.06)
SC = dict(c=(190, 170, 140), n=0.1, d='veins'); EYC = dict(c=(230, 220, 120), n=0.05); HR = dict(c=(60, 40, 30), n=0.1, d='fur')
spec(id='drain_deimos', pkg='draindeimos', C='DrainDeimos', ppkg='giantmoth', pm='GiantMothModel', W=128, H0=64, sty=dict(d=DK, b=YB),
  parts=[A('body/abdomen/stinger', 'PartPose.offset(0, 0, 12)', (-1, -1, 0, 2, 2, 9, 'd'), (-0.5, -0.5, 8, 1, 1, 4, 'd')), A('body/abdomen/bands', Z, (-3.3, -3.3, 2, 6.6, 6.6, 1.5, 'b'), (-3.3, -3.3, 5, 6.6, 6.6, 1.5, 'b'), (-3.3, -3.3, 8, 6.6, 6.6, 1.5, 'b')),
         A('body/head/pinc', Z, (-3, -1, -5.5, 1, 1, 3, 'd'), (2, -1, -5.5, 1, 1, 3, 'd')), A('body/spk', Z, (-1, -4.5, -2, 2, 2, 2, 'd'), (-1, -4.5, 1, 2, 2, 2, 'd'))])
leg = lambda sg, i, side: A('body/xleg%s%d' % (side, i), 'PartPose.offsetAndRotation(%s, 1, %s, 0.2f, 0, %sf)' % (2.5 * sg, -2 + i * 2, 0.9 * sg), (-0.5, 0, -0.5, 1, 9, 1, 'lg'))
spec(id='marguerite', pkg='marguerite', C='Marguerite', ppkg='giantmoth', pm='GiantMothModel', W=128, H0=64, sty=dict(fc=dict(c=(196, 160, 140), n=0.08, d='face', hc=(60, 40, 30), mc=(110, 30, 30)), hr=HR, lg=DK, sc=SC),
  parts=[A('body/head/face', Z, (-2.2, -2.2, -4.4, 4.4, 4.4, 1, 'fc'), (-2.7, -3, -2, 5.4, 3, 5, 'hr')), A('body/abdomen/sac', Z, (-4, -4, 5, 8, 8, 6, 'sc'), (-3, -3, -1, 6, 6, 2, 'sc'))] + [leg(sg, i, sd) for sg, sd in ((1, 'L'), (-1, 'R')) for i in range(4)])
spec(id='kipepeo', pkg='kipepeo', C='Kipepeo', ppkg='giantmoth', pm='GiantMothModel', W=128, H0=64, sty=dict(fz=FZ, wg=WG, sp=SP),
  parts=[A('body/thorax', Z, (-3, -3, -3.5, 6, 6, 7, 'fz')), A('body/wingL/wingOutL/tail', Z, (8, -0.5, 3, 2, 1, 10, 'wg')), A('body/wingR/wingOutR/tail', Z, (-10, -0.5, 3, 2, 1, 10, 'wg')),
         A('body/wingL/spotL', Z, (3, -0.7, -2, 5, 1, 5, 'sp')), A('body/wingR/spotR', Z, (-8, -0.7, -2, 5, 1, 5, 'sp'))])
BN = dict(c=(200, 190, 160), n=0.06, d='spike')
spec(id='novistador', pkg='novistador', C='Novistador', ppkg='giantmoth', pm='GiantMothModel', W=128, H0=64, sty=dict(bn=BN, d=DK),
  parts=[A('body/spines', Z, (-0.5, -5, -3, 1, 3, 1, 'bn'), (-0.5, -5, 0, 1, 4, 1, 'bn'), (-0.5, -4, 3, 1, 3, 1, 'bn')), A('body/head/mand', Z, (-2.5, 0, -5.5, 1, 1, 4, 'bn'), (1.5, 0, -5.5, 1, 1, 4, 'bn')),
         A('body/clawL', 'PartPose.offsetAndRotation(2, 2, -3, -0.6f, 0.2f, 0)', (-0.5, 0, -0.5, 1, 8, 1, 'bn'), (-1, 7, -1, 2, 1, 3, 'bn')), A('body/clawR', 'PartPose.offsetAndRotation(-2, 2, -3, -0.6f, -0.2f, 0)', (-0.5, 0, -0.5, 1, 8, 1, 'bn'), (-1, 7, -1, 2, 1, 3, 'bn'))])
SK = dict(c=(60, 76, 60), n=0.12, d='bruise'); SPN = dict(c=(40, 50, 40), n=0.1, d='spike')
def limb(side, sg, z): return A('body/limb%s%d' % (side, int(z)), 'PartPose.offsetAndRotation(%s, 3, %s, 0, 0, %sf)' % (7 * sg, z, 0.5 * sg), (0 if sg > 0 else -10, -2, -2, 10, 4, 4, 'sk'), (9 if sg > 0 else -12, -1, -3, 3, 4, 6, 'sk'))
spec(id='del_lago', pkg='dellago', C='DelLago', ppkg='neptune', pm='NeptuneModel', W=128, H0=128, sty=dict(sk=SK, sp=SPN, ey=EYC),
  parts=[limb('L', 1, -6), limb('R', -1, -6), limb('L', 1, 7), limb('R', -1, 7), A('body/head/barbel', Z, (-6.5, 1, -13, 1, 1, 8, 'sk'), (5.5, 1, -13, 1, 1, 8, 'sk'), (-6.5, -1, -10, 1, 1, 8, 'sk'), (5.5, -1, -10, 1, 1, 8, 'sk')),
         A('body/head/eyes', Z, (-6.5, -7, -7, 3, 3, 3, 'ey'), (3.5, -7, -7, 3, 3, 3, 'ey')), A('body/spikes', Z, (-1, -9, -8, 2, 3, 2, 'sp'), (-1, -9, -4, 2, 3, 2, 'sp'), (-1, -9, 0, 2, 3, 2, 'sp'), (-1, -9, 4, 2, 3, 2, 'sp'), (-1, -9, 8, 2, 3, 2, 'sp'))])
FSK = dict(c=(120, 130, 100), n=0.12, d='bruise'); HND = dict(c=(190, 150, 130), n=0.1, d='bruise')
def arm(side, sg): return A('body/arm' + side, 'PartPose.offsetAndRotation(%s, 0, -6, 0, 0, %sf)' % (7 * sg, 0.4 * sg), (0 if sg > 0 else -4, -2, -2, 4, 13, 4, 'hd'), (0 if sg > 0 else -5, 10, -2.5, 5, 3, 5, 'hd'), (1 if sg > 0 else -5, 12, -2, 1, 5, 1, 'hd'), (3 if sg > 0 else -3, 12, -2, 1, 5, 1, 'hd'))
spec(id='moreau', pkg='moreau', C='Moreau', ppkg='neptune', pm='NeptuneModel', W=128, H0=128, sty=dict(hd=HND, sc=SC, ey=EYC, f=FSK),
  parts=[arm('L', 1), arm('R', -1), A('body/head/sac', Z, (-4, 2, -9, 8, 6, 6, 'sc')), A('body/head/eyes', Z, (-7, -7, -8, 4, 4, 4, 'ey'), (3, -7, -8, 4, 4, 4, 'ey')), A('body/pust', Z, (-5, -8, -4, 3, 3, 3, 'sc'), (3, -8, 0, 4, 3, 3, 'sc'), (-2, -8, 5, 3, 3, 3, 'sc'), (-6, -4, 8, 3, 3, 3, 'sc')),
         A('body/tail1/legs', Z, (-6, 4, -2, 3, 9, 3, 'f'), (3, 4, -2, 3, 9, 3, 'f'))])
spec(id='colmillos', pkg='colmillos', C='Colmillos', ppkg='cerberus', pm='CerberusModel', W=64, H0=64, sty=dict(bn=BN, tn=dict(c=(150, 50, 60), n=0.1, d='veins')),
  parts=[A('body/chest/spines', Z, (-1, -6, -9, 2, 3, 2, 'bn'), (-1, -6, -6, 2, 4, 2, 'bn'), (-1, -6, -3, 2, 3, 2, 'bn')), A('body/hips/spines2', Z, (-1, -5, 0, 2, 3, 2, 'bn'), (-1, -5, 3, 2, 3, 2, 'bn')),
         A('body/chest/neck/head/horns', Z, (-2.5, -5, -3, 1, 3, 1, 'bn'), (1.5, -5, -3, 1, 3, 1, 'bn')), A('body/chest/neck/head/tent', Z, (-2, 1, -9, 1, 1, 6, 'tn'), (1, 1, -9, 1, 1, 6, 'tn'))])
spec(id='gnezdo', pkg='gnezdo', C='Gnezdo', ppkg='larva', pm='LarvaModel', W=64, H0=64, sty=dict(bn=BN, c=dict(c=(150, 60, 40), n=0.1)),
  parts=[A('head/horn', Z, (-0.5, -6, -3, 1, 3, 1, 'bn')), A('head/mandx', Z, (-2, -2, -8, 1, 1, 4, 'bn'), (1, -2, -8, 1, 1, 4, 'bn'))] + [A('head/' + '/'.join('s%d' % j for j in range(1, k + 1)) + '/bristle', Z, (-3.5, -5, 2, 1, 2, 1, 'bn'), (2.5, -5, 2, 1, 2, 1, 'bn'), (-0.5, -6, 3, 1, 3, 1, 'bn')) for k in (1, 2, 3)])
spec(id='grave_digger', pkg='gravedigger', C='GraveDigger', ppkg='yawn', pm='YawnModel', W=256, H0=64, sty=dict(bn=BN),
  parts=[A('body/neck1/neck2/neck3/head/hornsX', Z, (-3, -5, -6, 1, 3, 1, 'bn'), (2, -5, -6, 1, 3, 1, 'bn'), (-0.5, -6, -3, 1, 4, 1, 'bn')), A('body/spk', Z, (-1, -8, -4, 2, 3, 2, 'bn'), (-1, -8, 0, 2, 3, 2, 'bn'), (-1, -8, 4, 2, 3, 2, 'bn'))])


# ---- batch 7: mutation parts (shown by CanonVisuals while BioMonster.getMut() matches)
MB = dict(c=(200, 205, 210), n=0.06); MF = dict(c=(120, 60, 60), n=0.12, d='veins'); MW = dict(c=(60, 40, 70), n=0.1, d='veins'); MS = dict(c=(190, 160, 70), n=0.08, d='spike')
SPECS['javo']['sty'].update(dict(mb=MB, mf=MF, mw=MW, ms=MS))
SPECS['javo']['parts'] += [
  A('base/torso/armR/foreR/mutBlade', Z, (-1, 2, -2, 2, 22, 4, 'mb'), (-0.5, 20, -3, 1, 6, 2, 'mb')),
  A('base/torso/mutWingL', 'PartPose.offsetAndRotation(3, -8, 2, 0.3f, 0, -0.5f)', (0, -1, 0, 14, 1, 9, 'mw')), A('base/torso/mutWingR', 'PartPose.offsetAndRotation(-3, -8, 2, 0.3f, 0, 0.5f)', (-14, -1, 0, 14, 1, 9, 'mw')),
  A('base/mutTail', 'PartPose.offsetAndRotation(0, -1, 2, 0.9f, 0, 0)', (-1, -1, 0, 2, 2, 9, 'ms')), A('base/mutTail/t2', 'PartPose.offsetAndRotation(0, 0, 9, -0.6f, 0, 0)', (-1, -1, 0, 2, 2, 8, 'ms')),
  A('base/mutTail/t2/t3', 'PartPose.offsetAndRotation(0, 0, 8, -0.9f, 0, 0)', (-0.5, -0.5, 0, 1, 1, 4, 'ms'), (-1, -1.5, 4, 2, 3, 2, 'mb')),
  A('base/torso/armL/foreL/mutClaw', Z, (-3, 2, -3, 6, 9, 6, 'mf'), (-3, 10, -3, 1, 6, 1, 'mb'), (-1, 10, -3, 1, 7, 1, 'mb'), (1, 10, -3, 1, 6, 1, 'mb'))]
URO = dict(c=(130, 30, 26), n=0.1, d='veins'); GLOW = dict(c=(250, 170, 50), n=0.05)
def ut(n, x, rz, ry): return A('base/torso/uroT%d' % n, 'PartPose.offsetAndRotation(%s, -9, 3, 0.7f, %sf, %sf)' % (x, ry, rz), (-1.2, -1.2, 0, 2.4, 2.4, 14, 'u'), (-1, -1, 14, 2, 2, 3, 'g'))
spec(clipfn='wesker', id='wesker', pkg='wesker', C='Wesker', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=dict(u=URO, g=GLOW),
  parts=[ut(1, -3, 0.3, 0.5), ut(2, 3, -0.3, -0.5), ut(3, -1, 0.1, 0.1), ut(4, 1, -0.1, -0.1)])

if __name__ == '__main__':
    for k in (sys.argv[1:] or SPECS): gen(SPECS[k])
