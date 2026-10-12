"""Wiki-verified redesigns (see CANON_NOTES.md): gnezdo, daughter, marguerite, soldat, sturm, urias, adjule, javo, + forms for mendez/dimitrescu/heisenberg/moreau, reaper/ndesu tweaks. Run after rework_entities.py + canon_moves.py."""
import os, sys, re
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import variantgen as vg
from variantgen import spec, A, Z, SPECS, humanoid, gen as vgen
from PIL import Image, ImageOps
import canon_moves as cm
D = dict
HAT = ['base/torso/neck/head/hatBrim/hatCrown', 'base/torso/neck/head/hatBrim']
def face(skin, hc, ec=(16, 10, 10), **kw): return D(c=skin, n=0.08, d='face', hc=hc, ec=ec, **kw)
def fur(c, n=0.1): return D(c=c, n=n, d='fur')
MET = D(c=(120, 124, 132), n=0.1); MET2 = D(c=(80, 84, 92), n=0.1); STEELc = D(c=(184, 190, 198), n=0.06); DRILL = D(c=(150, 150, 156), n=0.06, d='spike'); RED = D(c=(230, 50, 30), n=0.05)
GREYFL = D(c=(150, 146, 140), n=0.1, d='bruise'); BONE = D(c=(214, 204, 172), n=0.08); WOOD = D(c=(120, 84, 52), n=0.1); DARK = D(c=(10, 8, 10), n=0.02)
MF = D(c=(130, 70, 70), n=0.12, d='veins'); MW = D(c=(62, 44, 76), n=0.1, d='veins'); MS = D(c=(190, 150, 60), n=0.08, d='spike'); MP = D(c=(96, 90, 84), n=0.1); MC = D(c=(156, 146, 116), n=0.1, d='veins')

# ------------------------------------------------------------------ Gnezdo
I1 = D(c=(36, 28, 22), n=0.2, d='fur'); I2 = D(c=(76, 84, 44), n=0.2, d='fur'); I3 = D(c=(20, 62, 52), n=0.12, d='veins'); QN = D(c=(26, 74, 62), n=0.1, d='veins')
gz = humanoid(hips=[(-3.8, -1.5, -2.2, 7.6, 3.5, 4.4, 'i1')], torso=[(-3.3, -10, -2, 6.6, 10, 4, 'i1'), (-3.5, -6, -2.2, 7, 3, 4.4, 'i2')], neck=[(-1.2, -1, -1.2, 2.4, 2, 2.4, 'i1')],
  head=[(-3, -7, -3, 6, 7, 6, 'fc'), (-3.4, -7.5, -3.4, 6.8, 2.5, 6.8, 'i1'), (-3.4, -6, 1, 6.8, 14, 2, 'i2')], arm=[(-1.4, 0, -1.4, 2.8, 9, 2.8, 'i1')], fore=[(-1.3, 0, -1.3, 2.6, 9, 2.6, 'i2')],
  hand=[(-1.5, 0, -1.5, 3, 3, 3, 'i1'), (-1.5, 2.5, -1.5, 1, 4, 1, 'i2'), (0.5, 2.5, -1.5, 1, 4, 1, 'i2')], thigh=[(-1.8, 0, -1.8, 3.6, 7, 3.6, 'i1')], shin=[(-1.6, 0, -1.6, 3.2, 7, 3.2, 'i2')], foot=[(-1.8, 0, -4, 3.6, 2, 5.5, 'i1')], coat=[(-2.4, -1, -2.6, 4.8, 10, 5.2, 'i1')])
spec(id='gnezdo', pkg='gnezdo', C='Gnezdo', ppkg='mrx', pm='MrXModel', W=128, H0=64, clear=HAT, sty=D(i1=I1, i2=I2, i3=I3, qn=QN, fc=face((40, 32, 26), (24, 20, 16), (255, 210, 40), mc=(10, 6, 6))),
  parts=gz + [A('base/torso/swarm', Z, (-6, -9, -1, 1.5, 1.5, 1.5, 'i3'), (5, -7, 1, 1.5, 1.5, 1.5, 'i3'), (-5, -3, -3, 1.5, 1.5, 1.5, 'i3'), (4, -4, 3, 1.5, 1.5, 1.5, 'i3'), (-4, -11, 2, 1.5, 1.5, 1.5, 'i3'), (6, -2, -1, 1.5, 1.5, 1.5, 'i3')),
              A('base/torso/queen', Z, (-2.5, -8, -3.4, 5, 5, 3, 'qn'), (-3.5, -7, -3.2, 1, 1, 3, 'qn'), (2.5, -7, -3.2, 1, 1, 3, 'qn'), (-1, -9.5, -3.3, 2, 2, 2, 'qn'))])
cm.VIS['gnezdo'] = [('base/torso/queen', 'mut == 1')]

# ------------------------------------------------------------------ Daughters (Bela / Cassandra / Daniela)
SK = D(c=(214, 196, 186), n=0.06); WH = D(c=(236, 232, 226), n=0.04); SH = D(c=(30, 24, 26), n=0.05); SCK = D(c=(176, 182, 190), n=0.06)
def dv(dress, hair): return D(dr=D(c=dress, n=0.08), hr=D(c=hair, n=0.08, d='fur'), fc=face((214, 196, 186), hair, (60, 40, 40), mc=(120, 30, 40)))
dg = humanoid(hips=[(-4, -1.5, -2.4, 8, 3.5, 4.8, 'dr')], torso=[(-3.8, -10, -2.3, 7.6, 10, 4.6, 'dr'), (-4, -9, -2.5, 8, 2, 5, 'sk')], neck=[(-1.3, -1, -1.3, 2.6, 2, 2.6, 'sk')],
  head=[(-3.2, -7, -3.2, 6.4, 7, 6.4, 'fc'), (-3.6, -7.5, -3.6, 7.2, 3, 7.2, 'hr'), (-3.6, -6, 0.5, 7.2, 10, 3, 'hr')], arm=[(-1.5, 0, -1.5, 3, 9, 3, 'sk')], fore=[(-1.4, 0, -1.4, 2.8, 9, 2.8, 'sk')], hand=[(-1.5, 0, -1.5, 3, 3, 3, 'sk')],
  thigh=[(-2, 0, -2, 4, 7, 4, 'sk')], shin=[(-1.8, 0, -1.8, 3.6, 7, 3.6, 'sk')], foot=[(-2, 0, -4.5, 4, 2, 6, 'sh')], coat=[(-3.2, -1, -3.6, 6.4, 15, 7.2, 'dr')])
spec(id='daughter', pkg='daughter', C='Daughter', ppkg='mrx', pm='MrXModel', W=128, H0=64, clear=HAT, sty=dv((46, 38, 60), (24, 18, 22)) | D(sk=SK, sh=SH, wh=WH, sc=SCK, wd=WOOD),
  svar={'_bela': dv((46, 38, 60), (24, 18, 22)), '_cassandra': dv((232, 226, 214), (214, 196, 120)), '_daniela': dv((140, 40, 58), (120, 56, 34))},
  parts=dg + [A('base/torso/armR/foreR/handR/sickle', 'PartPose.offsetAndRotation(0, 2, -1, -0.6f, 0, 0)', (-0.5, -2, -0.5, 1, 12, 1, 'wd'), (-0.5, 9, -7, 1, 2, 8, 'sc'), (-0.5, 7, -7.5, 1, 2, 2, 'sc')),
              A('base/torso/neck/head/fangs', Z, (-1.8, -1.5, -3.4, 0.8, 1.5, 0.5, 'wh'), (1, -1.5, -3.4, 0.8, 1.5, 0.5, 'wh'))])

# ------------------------------------------------------------------ Marguerite
MD = D(c=(118, 126, 108), n=0.1); MSK = D(c=(186, 170, 150), n=0.08); GH = fur((170, 170, 172)); HV = D(c=(120, 100, 60), n=0.15, d='veins'); HV2 = D(c=(150, 60, 60), n=0.12, d='veins'); SK2 = D(c=(176, 150, 130), n=0.1, d='bruise')
mg = humanoid(hips=[(-3.8, -1.5, -2.3, 7.6, 3.5, 4.6, 'md')], torso=[(-3.6, -10, -2.2, 7.2, 10, 4.4, 'md'), (-3.8, -10, -2.4, 7.6, 2, 4.8, 'sk')], neck=[(-1.2, -1, -1.2, 2.4, 2, 2.4, 'sk')],
  head=[(-3.1, -7, -3.1, 6.2, 7, 6.2, 'fc'), (-3.5, -7.5, -3.5, 7, 3, 7, 'gh'), (-2, -10.5, -0.5, 4, 3, 4, 'gh')], arm=[(-1.4, 0, -1.4, 2.8, 9, 2.8, 'sk')], fore=[(-1.3, 0, -1.3, 2.6, 9, 2.6, 'sk')], hand=[(-1.4, 0, -1.4, 2.8, 3, 2.8, 'sk')],
  thigh=[(-1.8, 0, -1.8, 3.6, 7, 3.6, 'sk')], shin=[(-1.7, 0, -1.7, 3.4, 7, 3.4, 'sk')], foot=[(-1.9, 0, -4.4, 3.8, 2, 5.8, 'sk')], coat=[(-3, -1, -3.5, 6, 14, 7, 'md')])
spec(id='marguerite', pkg='marguerite', C='Marguerite', ppkg='mrx', pm='MrXModel', W=128, H0=64, clear=HAT, sty=D(md=MD, sk=MSK, gh=GH, hv=HV, hv2=HV2, sk2=SK2, fc=face((186, 170, 150), (170, 170, 172), (240, 210, 30), mc=(110, 40, 40))),
  parts=mg + [A('base/hive', Z, (-5.5, -2, 1.5, 11, 10, 9, 'hv'), (-3, 0, 9, 6, 6, 3, 'hv'), (-6, 3, 4, 2, 3, 3, 'hv2'), (4, 2, 5, 2, 3, 3, 'hv2')), A('base/torso/armR/foreR/elong', Z, (-1.5, 8, -1.5, 3, 16, 3, 'sk2'), (-2, 22, -2, 4, 3, 4, 'sk2')),
              A('base/torso/lumps', Z, (-7, -11, -2, 4, 4, 4, 'hv2'), (3, -11, -2, 4, 4, 4, 'hv2'))])
cm.VIS['marguerite'] = [('base/hive', 'mut == 1'), ('base/torso/armR/foreR/elong', 'mut == 1'), ('base/torso/lumps', 'mut == 1')]

# ------------------------------------------------------------------ Soldat (Eins/Zwei/Jet/Panzer in mut 0..3)
def drill(path, ox=0.0, s=1.0):
    return A(path, 'PartPose.offset(%s, 8, 0)' % ox, (-2.8 * s, 0, -2.8 * s, 5.6 * s, 4, 5.6 * s, 'mt'), (-2.4 * s, 4, -2.4 * s, 4.8 * s, 4, 4.8 * s, 'dr'), (-1.8 * s, 8, -1.8 * s, 3.6 * s, 4, 3.6 * s, 'dr'), (-1.2 * s, 12, -1.2 * s, 2.4 * s, 4, 2.4 * s, 'dr'), (-0.6 * s, 16, -0.6 * s, 1.2 * s, 3, 1.2 * s, 'dr'))
sd = humanoid(hips=[(-4, -1.5, -2, 8, 3, 4, 'mt')], torso=[(-4.4, -10, -2.4, 8.8, 10, 4.8, 'fl'), (-4.6, -10, -2.5, 9.2, 3, 5, 'mt'), (-2, -7, -2.6, 4, 3, 1, 'ex')], neck=[(-1.5, -1, -1.5, 3, 2, 3, 'fl')],
  head=[(-3.5, -7, -3.5, 7, 7, 7, 'fc'), (-3.9, -8, -3.9, 7.8, 4, 7.8, 'hg'), (-2.5, -4.5, -3.9, 5, 1.5, 0.6, 'vz')], arm=[(-2, 0, -2, 4, 9, 4, 'fl')], fore=[(-2, 0, -2, 4, 9, 4, 'fl')], hand=[(-2, 0, -2, 4, 3, 4, 'fl')],
  thigh=[(-2.2, 0, -2.2, 4.4, 7, 4.4, 'mt')], shin=[(-2, 0, -2, 4, 7, 4, 'mt')], foot=[(-2.2, 0, -5, 4.4, 2, 6, 'mt')], coat=[(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'mt')])
spec(id='soldat', pkg='soldat', C='Soldat', ppkg='mrx', pm='MrXModel', W=128, H0=64, clear=HAT, sty=D(mt=MET, fl=GREYFL, ex=RED, hg=MET2, vz=D(c=(220, 60, 40), n=0.05), dr=DRILL, pz=MET2, jt=D(c=(220, 130, 40), n=0.08), fc=face((150, 146, 140), (70, 70, 74), (200, 40, 30))),
  parts=sd + [drill('base/torso/armL/foreL/drillL'), drill('base/torso/armR/foreR/drillR'), drill('base/torso/armL/foreL/drillL2', -3.2, 0.7), drill('base/torso/armR/foreR/drillR2', 3.2, 0.7),
              A('base/torso/pz', Z, (-5.4, -10.5, -3.2, 10.8, 9, 6.4, 'pz'), (-7.5, -12, -3, 3, 4, 6, 'pz'), (4.5, -12, -3, 3, 4, 6, 'pz')), A('base/torso/jet', Z, (-4, -9, 2.4, 8, 8, 3, 'mt'), (-3, -9, 5.4, 2.5, 6, 2.5, 'jt'), (0.5, -9, 5.4, 2.5, 6, 2.5, 'jt'))])
cm.VIS['soldat'] = [('base/torso/armR/foreR/drillR', 'mut == 1 || mut == 3'), ('base/torso/armL/foreL/drillL2', 'mut == 3'), ('base/torso/armR/foreR/drillR2', 'mut == 3'), ('base/torso/pz', 'mut == 3'), ('base/torso/jet', 'mut == 2')]

# ------------------------------------------------------------------ Sturm: armless, engine torso, propeller
EN = D(c=(70, 72, 80), n=0.1); CY = D(c=(110, 112, 120), n=0.08); BL = D(c=(190, 196, 204), n=0.05, d='spike'); HB = D(c=(150, 60, 40), n=0.1)
st_ = humanoid(hips=[(-4, -1.5, -2, 8, 3, 4, 'mt')], torso=[(-5.5, -10, -4.5, 11, 10, 9, 'en'), (-8.5, -8, -2.5, 3.5, 5, 5, 'cy'), (5, -8, -2.5, 3.5, 5, 5, 'cy'), (-2.5, -12, -2.5, 5, 3.5, 5, 'cy'), (-2.5, -1, -2.5, 5, 3, 5, 'cy'), (-1.5, -8, 4.6, 3, 3, 0.8, 'ex')],
  neck=[(-1.5, -1, -1.5, 3, 2, 3, 'fl')], head=[(-3, -7, -3, 6, 6, 6, 'hg'), (-2.5, -4, -3.2, 5, 1.5, 0.6, 'vz')], arm=[], fore=[], hand=[],
  thigh=[(-2.2, 0, -2.2, 4.4, 7, 4.4, 'mt')], shin=[(-2, 0, -2, 4, 7, 4, 'mt')], foot=[(-2.2, 0, -5, 4.4, 2, 6, 'mt')], coat=[(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'mt')])
spec(id='sturm', pkg='sturm', C='Sturm', ppkg='mrx', pm='MrXModel', W=128, H0=64, clear=HAT, sty=D(mt=MET, fl=GREYFL, en=EN, cy=CY, ex=RED, hg=MET2, vz=D(c=(220, 60, 40), n=0.05), hub=HB, bl=BL),
  parts=st_ + [A('base/torso/prop', 'PartPose.offset(0, -5, -4.8)', (-1.8, -1.8, -1.5, 3.6, 3.6, 3, 'hub')),
               A('base/torso/prop/b1', Z, (-1, -11, -0.5, 2, 11, 1, 'bl')), A('base/torso/prop/b2', 'PartPose.offsetAndRotation(0, 0, 0, 0, 0, 2.094f)', (-1, -11, -0.5, 2, 11, 1, 'bl')), A('base/torso/prop/b3', 'PartPose.offsetAndRotation(0, 0, 0, 0, 0, 4.189f)', (-1, -11, -0.5, 2, 11, 1, 'bl'))])
cm.SPIN['sturm'] = [('base/torso/prop', 'z', 0.9)]

# ------------------------------------------------------------------ Urias: troll-like Lycan with war-hammer
FR = fur((84, 76, 68)); FR2 = fur((58, 52, 46)); SKU = D(c=(150, 130, 116), n=0.1, d='bruise')
ur = humanoid(hips=[(-4.5, -1.5, -2.5, 9, 3, 5, 'fr')], torso=[(-5.5, -10, -3, 11, 10, 6, 'fr'), (-6.5, -11, -3.5, 13, 4, 7, 'fr2')], neck=[(-2, -1, -2, 4, 2, 4, 'fr')],
  head=[(-3.5, -7, -3.5, 7, 7, 7, 'fc'), (-1.8, -3, -7, 3.6, 3, 3.6, 'fr2'), (-3.5, -9, -1.5, 2, 3, 2, 'fr2'), (1.5, -9, -1.5, 2, 3, 2, 'fr2')], arm=[(-3, 0, -3, 6, 9, 6, 'fr')], fore=[(-2.8, 0, -2.8, 5.6, 9, 5.6, 'fr2')], hand=[(-2.6, 0, -2.6, 5.2, 4, 5.2, 'sk')],
  thigh=[(-2.8, 0, -2.8, 5.6, 7, 5.6, 'fr')], shin=[(-2.6, 0, -2.6, 5.2, 7, 5.2, 'fr2')], foot=[(-2.8, 0, -5.5, 5.6, 2, 7.5, 'sk')], coat=[(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'fr')])
spec(id='urias', pkg='urias', C='Urias', ppkg='elgigante', pm='ElGiganteModel', W=128, H0=64, clear=HAT, sty=D(fr=FR, fr2=FR2, sk=SKU, w=WOOD, st=STEELc, fc=face((150, 130, 116), (58, 52, 46), (240, 200, 60), mc=(80, 20, 20))),
  parts=ur + [A('base/torso/armR/foreR/handR/warhammer', 'PartPose.offsetAndRotation(0, 2, -1, -0.5f, 0, 0)', (-0.8, -3, -0.8, 1.6, 24, 1.6, 'w'), (-4, 20, -4, 8, 9, 8, 'st'), (-4.5, 21, -1.5, 9, 4, 3, 'st'))])

# ------------------------------------------------------------------ Adjule: dog; head splits open (mut 1)
def tan(im):
    g = ImageOps.grayscale(im.convert('RGB')); c = ImageOps.colorize(g, black=(40, 30, 20), white=(214, 176, 112)).convert('RGBA'); c.putalpha(im.split()[3]); return c
SPL = D(c=(150, 60, 60), n=0.12, d='veins'); PLG = D(c=(176, 40, 50), n=0.1, d='veins'); PK = D(c=(214, 150, 150), n=0.1)
spec(id='adjule', pkg='adjule', C='Adjule', ppkg='cerberus', pm='CerberusModel', W=64, H0=64, texfrom=('colmillos', tan), sty=D(sp=SPL, pl=PLG, pk=PK),
  parts=[A('body/chest/neck/head/splitL', 'PartPose.offsetAndRotation(1.5, -3, -3, 0, 0, -0.9f)', (0, -1, -6, 3, 1, 8, 'sp'), (0, 0, -6, 3, 1, 8, 'pk')), A('body/chest/neck/head/splitR', 'PartPose.offsetAndRotation(-1.5, -3, -3, 0, 0, 0.9f)', (-3, -1, -6, 3, 1, 8, 'sp'), (-3, 0, -6, 3, 1, 8, 'pk')),
         A('body/chest/neck/head/plaga', Z, (-1.5, -6, -7, 3, 5, 5, 'pl'), (-0.5, -9, -7, 1, 4, 1, 'pl'), (-3, -6, -4, 1, 5, 1, 'pl'), (2, -6, -4, 1, 5, 1, 'pl'))])
cm.VIS['adjule'] = [('body/chest/neck/head/splitL', 'mut == 1'), ('body/chest/neck/head/splitR', 'mut == 1'), ('body/chest/neck/head/plaga', 'mut == 1')]

# ------------------------------------------------------------------ J'avo: all wiki mutations (slots: arm bits 0-3, leg 4-7, head 8-11, torso 12-15)
EY = D(c=(236, 214, 120), n=0.05)
jv = [A('base/torso/neck/head/eyes', Z, (-3, -5, -3.6, 1, 1, 0.5, 'ey'), (1.5, -6, -3.6, 1.5, 1.5, 0.5, 'ey'), (-0.5, -7, -3.6, 1, 1, 0.5, 'ey'), (2.2, -3, -3.6, 0.8, 0.8, 0.5, 'ey'))]
H = 'base/torso/neck/head/'
jv += [A(H + 'mSluz', Z, (-2, 0, -4.2, 4, 3, 2, 'mf'), (-1, 2, -4.4, 2, 3, 1, 'mw')),
       A(H + 'mSmech', Z, (-2.5, -1, -4.8, 1, 1, 6, 'mb'), (1.5, -1, -4.8, 1, 1, 6, 'mb'), (-3.5, 0, -3, 1, 5, 1, 'mb'), (2.5, 0, -3, 1, 5, 1, 'mb')),
       A(H + 'mBeg', Z, (-3, -9, -3, 6, 3, 6, 'mf'), (-1, -10, -5, 2, 2, 4, 'mb')),
       A(H + 'mDim', Z, (-2.5, -9, 0, 5, 5, 7, 'ms'), (-1, -8, 6, 2, 2, 5, 'mb')),
       A('base/torso/armR/foreR/mSrp', Z, (-1, 2, -1.5, 2, 16, 3, 'mb'), (-0.5, 17, -6, 1, 2, 7, 'mb'), (-0.5, 16, -9, 1, 2, 4, 'mb')),
       A('base/torso/armL/foreL/mKhv', Z, (-1.5, 2, -1.5, 3, 14, 3, 'mf'), (-3, 15, -3, 2, 5, 2, 'mb'), (1, 15, -3, 2, 5, 2, 'mb'), (-0.5, 15, 1, 2, 5, 2, 'mb')),
       A('base/torso/armL/foreL/mBed', Z, (-3.5, -9, -3.5, 7, 19, 7, 'mp'), (-4, -9, -1, 1, 19, 2, 'mb')),
       A('base/mTrch', Z, (-4, -1, 2, 8, 5, 12, 'ms'), (-3, -1, 12, 6, 4, 6, 'ms'), (-5, 4, 4, 1, 8, 1, 'mb'), (4, 4, 4, 1, 8, 1, 'mb')),
       A('base/mLetL', 'PartPose.offsetAndRotation(2, -2, 2, 0.2f, 0, -0.4f)', (0, -0.5, 0, 16, 1, 9, 'mw')), A('base/mLetR', 'PartPose.offsetAndRotation(-2, -2, 2, 0.2f, 0, 0.4f)', (-16, -0.5, 0, 16, 1, 9, 'mw')),
       A('base/mLetT', 'PartPose.offsetAndRotation(0, 0, 3, 0.5f, 0, 0)', (-0.7, -0.7, 0, 1.4, 1.4, 12, 'mb'), (-1, -1.2, 11, 2, 2.5, 3, 'mb')),
       A('base/mSkak', Z, (-6, 6, -1, 2, 12, 2, 'mb'), (4, 6, -1, 2, 12, 2, 'mb'), (-6, 6, 2, 2, 10, 2, 'mb'), (4, 6, 2, 2, 10, 2, 'mb')),
       A('base/mOkl', Z, (-5, 0, -3, 3, 9, 6, 'mp'), (2, 0, -3, 3, 9, 6, 'mp')),
       A('base/torso/mEks', Z, (-5, -11, -3.5, 10, 12, 7, 'mf'), (-3, -9, -4.4, 6, 6, 1, 'mw')),
       A('base/torso/mKrl', Z, (-4.8, -10.5, -3, 9.6, 3, 6, 'mp'), (-4.8, -7, -3, 9.6, 3, 6, 'mp'), (-4.8, -3.5, -3, 9.6, 3, 6, 'mp')),
       A('base/torso/mMag', Z, (-9, -9, 0, 18, 1, 8, 'mw'), (-3, -2, 3, 6, 8, 5, 'mf')),
       A('base/mChr', Z, (-6, -26, -5, 12, 28, 10, 'mc'))]
spec(id='javo', pkg='javo', C='Javo', ppkg='ganado', pm='GanadoModel', W=128, H0=64, sty=D(ey=EY, mf=MF, mb=D(c=(204, 208, 212), n=0.06), mw=MW, ms=MS, mp=MP, mc=MC), parts=jv)
sl = lambda sh, v: '((mut >> %d) & 15) == %d' % (sh, v)
cm.VIS['javo'] = [(H + 'mSluz', sl(8, 1)), (H + 'mSmech', sl(8, 2)), (H + 'mBeg', sl(8, 3)), (H + 'mDim', sl(8, 4)), ('base/torso/armR/foreR/mSrp', sl(0, 1)), ('base/torso/armL/foreL/mKhv', sl(0, 2)), ('base/torso/armL/foreL/mBed', sl(0, 3)),
  ('base/mTrch', sl(4, 1)), ('base/mLetL', sl(4, 2)), ('base/mLetR', sl(4, 2)), ('base/mLetT', sl(4, 2)), ('base/mSkak', sl(4, 3)), ('base/mOkl', sl(4, 4)), ('base/torso/mEks', sl(12, 1)), ('base/torso/mKrl', sl(12, 2)), ('base/torso/mMag', sl(12, 3)), ('base/mChr', sl(12, 4))]

# ------------------------------------------------------------------ extra forms on existing specs
FL2 = D(c=(140, 70, 70), n=0.12, d='veins'); BNE = D(c=(214, 204, 172), n=0.08)
SPECS['mendez']['sty'].update(dict(f2=FL2, bn=BNE))
SPECS['mendez']['parts'] += [A('base/torso/bkL', 'PartPose.offsetAndRotation(3, -8, 3, 0.6f, 0.3f, 0)', (-1.2, -1.2, 0, 2.4, 2.4, 10, 'f2')), A('base/torso/bkL/s2', 'PartPose.offsetAndRotation(0, 0, 10, -0.9f, 0, 0)', (-1, -1, 0, 2, 2, 10, 'f2')),
  A('base/torso/bkR', 'PartPose.offsetAndRotation(-3, -8, 3, 0.6f, -0.3f, 0)', (-1.2, -1.2, 0, 2.4, 2.4, 10, 'f2')), A('base/torso/bkR/s2', 'PartPose.offsetAndRotation(0, 0, 10, -0.9f, 0, 0)', (-1, -1, 0, 2, 2, 10, 'f2')),
  A('base/torso/spineX', Z, (-1.5, -12, 2.5, 3, 14, 3, 'bn')), A('base/torso/armR/foreR/talonR', Z, (-1.5, 9, -1.5, 1, 6, 1, 'bn'), (0, 9, -1.5, 1, 7, 1, 'bn'), (1.5, 9, -1.5, 1, 6, 1, 'bn')),
  A('base/torso/armL/foreL/talonL', Z, (-1.5, 9, -1.5, 1, 6, 1, 'bn'), (0, 9, -1.5, 1, 7, 1, 'bn'), (1.5, 9, -1.5, 1, 6, 1, 'bn'))]
cm.VIS['mendez'] = [(p, 'mut == 1') for p in ('base/torso/worm', 'base/torso/bkL', 'base/torso/bkR', 'base/torso/spineX', 'base/torso/armR/foreR/talonR', 'base/torso/armL/foreL/talonL')]
DW = D(c=(120, 26, 40), n=0.12, d='veins')
SPECS['dimitrescu']['sty'].update(dict(dw=DW, bn=BNE))
SPECS['dimitrescu']['parts'] += [A('base/torso/wingL', 'PartPose.offsetAndRotation(3, -9, 3, 0.2f, 0.3f, -0.6f)', (0, -1, 0, 22, 1, 12, 'dw'), (8, -1, 12, 10, 1, 8, 'dw')), A('base/torso/wingR', 'PartPose.offsetAndRotation(-3, -9, 3, 0.2f, -0.3f, 0.6f)', (-22, -1, 0, 22, 1, 12, 'dw'), (-18, -1, 12, 10, 1, 8, 'dw')),
  A('base/torso/neck/head/dhorn', Z, (-3, -9, -2, 1.5, 5, 1.5, 'bn'), (1.5, -9, -2, 1.5, 5, 1.5, 'bn')), A('base/dtail', 'PartPose.offsetAndRotation(0, 0, 2, 0.4f, 0, 0)', (-1.5, -1.5, 0, 3, 3, 12, 'dw'), (-1, -1, 12, 2, 2, 10, 'dw'))]
cm.VIS['dimitrescu'] = [(p, 'mut == 1') for p in ('base/torso/wingL', 'base/torso/wingR', 'base/torso/neck/head/dhorn', 'base/dtail')]
SG = D(c=(90, 94, 104), n=0.1); SG2 = D(c=(130, 134, 144), n=0.08)
SPECS['heisenberg']['sty'].update(dict(sg=SG, sg2=SG2))
SPECS['heisenberg']['parts'] += [A('base/slug', Z, (-8, -4, -8, 16, 14, 22, 'sg'), (-6, -6, -6, 12, 3, 18, 'sg2')), A('base/slugGun', Z, (-9, 0, -14, 2, 2, 8, 'sg2'), (7, 0, -14, 2, 2, 8, 'sg2'))]
cm.VIS['heisenberg'] = [('base/slug', 'mut == 1'), ('base/slugGun', 'mut == 1')]
MWD = D(c=(70, 96, 70), n=0.1, d='bruise'); HD2 = D(c=(190, 150, 130), n=0.1, d='bruise')
SPECS['moreau']['sty'].update(dict(mw2=MWD, hd=HD2))
SPECS['moreau']['parts'] += [A('body/head/maw', Z, (-8, -4, -16, 16, 3, 10, 'mw2'), (-8, 3, -16, 16, 3, 10, 'mw2'), (-3, -1, -14, 6, 5, 5, 'hd'))]
cm.VIS['moreau'] = [('body/head/maw', 'mut == 1')]
STG = D(c=(110, 100, 90), n=0.1); 
SPECS['reaper']['sty'].update(dict(p=SPECS['reaper']['sty']['p'], b=SPECS['reaper']['sty']['b']))
SPECS['reaper']['parts'] += [A('base/stinger', 'PartPose.offsetAndRotation(0, -2, 3, -0.5f, 0, 0)', (-1, -1, 0, 2, 2, 10, 'p'), (-0.5, -0.5, 9, 1, 1, 5, 'b')), A('base/torso/neck1/neck2/head/ant', Z, (-2.5, -8, -9, 0.6, 0.6, 10, 'b'), (1.9, -8, -9, 0.6, 0.6, 10, 'b'))]
# Drain Deimos: flea - big jumping hind legs and grasping claws, no stinger
dd = SPECS['drain_deimos']; dd['sty'].update(dict(lg=D(c=(60, 44, 30), n=0.1), cl=BNE))
dd['parts'] = [A('body/hindL', 'PartPose.offsetAndRotation(2, 2, 3, 0.8f, 0, 0.3f)', (-1, 0, -1, 2, 8, 2, 'lg'), (-1, 7, -5, 2, 2, 7, 'lg')), A('body/hindR', 'PartPose.offsetAndRotation(-2, 2, 3, 0.8f, 0, -0.3f)', (-1, 0, -1, 2, 8, 2, 'lg'), (-1, 7, -5, 2, 2, 7, 'lg')),
               A('body/clawL', 'PartPose.offsetAndRotation(2, 2, -3, -0.7f, 0.2f, 0)', (-0.5, 0, -0.5, 1, 7, 1, 'lg'), (-1, 6, -3, 1, 1, 4, 'cl')), A('body/clawR', 'PartPose.offsetAndRotation(-2, 2, -3, -0.7f, -0.2f, 0)', (-0.5, 0, -0.5, 1, 7, 1, 'lg'), (0, 6, -3, 1, 1, 4, 'cl')),
               A('body/head/pinc', Z, (-3, -1, -5.5, 1, 1, 3, 'cl'), (2, -1, -5.5, 1, 1, 3, 'cl'))]
# Ndesu: spiked bracelets + skull-trophy belt, no mace
nd = SPECS['ndesu']; nd['sty'].update(dict(st=STEELc, sk=BONE, ld=D(c=(70, 50, 36), n=0.1)))
nd['parts'] = [p for p in nd['parts'] if 'mace' not in p['path']] + [
  A('base/torso/armR/foreR/brR', Z, (-2.6, 5, -2.6, 5.2, 2, 5.2, 'st'), (-3.5, 5.5, -0.5, 1.5, 1, 1, 'bn'), (2, 5.5, -0.5, 1.5, 1, 1, 'bn'), (-0.5, 5.5, -3.5, 1, 1, 1.5, 'bn'), (-0.5, 5.5, 2, 1, 1, 1.5, 'bn')),
  A('base/torso/armL/foreL/brL', Z, (-2.6, 5, -2.6, 5.2, 2, 5.2, 'st'), (-3.5, 5.5, -0.5, 1.5, 1, 1, 'bn'), (2, 5.5, -0.5, 1.5, 1, 1, 'bn'), (-0.5, 5.5, -3.5, 1, 1, 1.5, 'bn'), (-0.5, 5.5, 2, 1, 1, 1.5, 'bn')),
  A('base/belt', Z, (-5, 0, -3, 10, 2, 6, 'ld'), (-4, 2, -3.2, 2, 2, 1, 'sk'), (-1, 2, -3.2, 2, 2, 1, 'sk'), (2, 2, -3.2, 2, 2, 1, 'sk'))]
nd['sty']['bn'] = BNE
# Uroboros: the yellow cyst is its only organ
SPECS['uroboros']['sty']['r'] = D(c=(236, 200, 48), n=0.08, d='veins')


# ------------------------------------------------------------------ batch 2: Krauser blade arm, Miranda forms, Uroboros growth, Grave Digger arthropod look
KB = D(c=(204, 208, 212), n=0.06); KF = D(c=(130, 70, 70), n=0.12, d='veins')
spec(id='krauser', pkg='krauser', C='Krauser', ppkg='ganado', pm='GanadoModel', W=128, H0=64, sty=D(kb=KB, kf=KF),
  parts=[A('base/torso/armR/foreR/mutBlade', Z, (-1.5, 2, -3, 3, 26, 6, 'kb'), (-0.5, 24, -5, 1, 6, 2, 'kb')), A('base/torso/armR/mutLump', Z, (-3.5, -1, -3.5, 7, 7, 7, 'kf'))])
cm.VIS['krauser'] = [('base/torso/armR/foreR/mutBlade', 'mut == 1'), ('base/torso/armR/mutLump', 'mut == 1')]
MWG = D(c=(40, 28, 56), n=0.1, d='veins'); MTN = D(c=(120, 40, 90), n=0.12, d='veins')
SPECS['miranda']['sty'].update(dict(mwg=MWG, mtn=MTN))
SPECS['miranda']['parts'] += [A('base/torso/wingL', 'PartPose.offsetAndRotation(3, -9, 3, 0.2f, 0.3f, -0.5f)', (0, -1, 0, 24, 1, 14, 'mwg'), (10, -1, 14, 12, 1, 8, 'mwg')), A('base/torso/wingR', 'PartPose.offsetAndRotation(-3, -9, 3, 0.2f, -0.3f, 0.5f)', (-24, -1, 0, 24, 1, 14, 'mwg'), (-22, -1, 14, 12, 1, 8, 'mwg')),
  A('base/torso/mass', Z, (-9, -12, -5, 18, 14, 10, 'mtn'), (-5, -16, -3, 10, 5, 8, 'mtn')), A('base/torso/tentA', 'PartPose.offsetAndRotation(0, -8, 4, 0.5f, 0.6f, 0)', (-1.5, -1.5, 0, 3, 3, 14, 'mtn')), A('base/torso/tentB', 'PartPose.offsetAndRotation(0, -8, 4, 0.5f, -0.6f, 0)', (-1.5, -1.5, 0, 3, 3, 14, 'mtn'))]
cm.VIS['miranda'] = [('base/torso/wingL', 'mut >= 1'), ('base/torso/wingR', 'mut >= 1'), ('base/torso/mass', 'mut == 2'), ('base/torso/tentA', 'mut == 2'), ('base/torso/tentB', 'mut == 2')]
UG = D(c=(30, 24, 30), n=0.1, d='spike')
SPECS['uroboros']['sty']['ug'] = UG
SPECS['uroboros']['parts'] += [A('base/stem1/mutSpikes', Z, (-9, -20, -1, 18, 2, 2, 'ug'), (-1, -20, -9, 2, 2, 18, 'ug'), (-9, -10, -1, 18, 2, 2, 'ug'), (-1, -10, -9, 2, 2, 18, 'ug'), (-4, -26, -4, 3, 6, 3, 'ug'), (1, -22, 1, 3, 6, 3, 'ug')),
  A('base/mutRoots', Z, (-20, -3, -2, 40, 3, 4, 'ug'), (-2, -3, -20, 4, 3, 40, 'ug'))]
cm.VIS['uroboros'] = [('base/stem1/mutSpikes', 'mut >= 1'), ('base/mutRoots', 'mut >= 1')]
PALE = D(c=(206, 198, 176), n=0.1, d='bruise'); LGS = D(c=(120, 104, 80), n=0.1)
gd = SPECS['grave_digger']; gd['sty'].update(dict(lg=LGS, pale=PALE))
gd['parts'] += [A('body/arthLegs', Z, (-9, 0, -6, 2, 5, 1, 'lg'), (7, 0, -6, 2, 5, 1, 'lg'), (-9, 0, -2, 2, 5, 1, 'lg'), (7, 0, -2, 2, 5, 1, 'lg'), (-9, 0, 2, 2, 5, 1, 'lg'), (7, 0, 2, 2, 5, 1, 'lg')),
                A('body/neck1/neck2/neck3/head/mandibles', Z, (-5, 0, -9, 1, 1, 5, 'lg'), (4, 0, -9, 1, 1, 5, 'lg'), (-2.5, 1, -10, 1, 1, 4, 'lg'), (1.5, 1, -10, 1, 1, 4, 'lg'))]

if __name__ == '__main__':
    ids = sys.argv[1:] or ['gnezdo', 'daughter', 'marguerite', 'soldat', 'sturm', 'urias', 'adjule', 'javo', 'mendez', 'dimitrescu', 'heisenberg', 'moreau', 'reaper', 'ndesu', 'uroboros', 'drain_deimos', 'krauser', 'miranda', 'grave_digger']
    for i in ids: vgen(SPECS[i])
    # persist the visuals map for canon_moves.py
    import json; json.dump({'VIS': cm.VIS, 'SPIN': cm.SPIN}, open(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'canon_vis.json'), 'w'), indent=1)
