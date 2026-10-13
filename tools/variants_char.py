"""Character-specific bodies for the humanoid creatures (Krauser, Saddler, Mendez, El Gigante, Ogroman, Executioner, Wesker, Jill, Simmons, Dimitrescu).
Run after variants_canon.py (it re-registers specs): python3 tools/variants_char.py [id ...]; then canon_moves.py."""
import os, sys, json
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import variants_canon as vc
import variantgen as vg
from variantgen import spec, A, Z, SPECS, gen as vgen
import canon_moves as cm
D = dict
def face(skin, hc, ec=(16, 10, 10), **kw): return D(c=skin, n=0.08, d='face', hc=hc, ec=ec, **kw)
def fur(c, n=0.1): return D(c=c, n=n, d='fur')
def P(c, n=0.08, d=None): return D(c=c, n=n, d=d) if d else D(c=c, n=n)

RIG = {  # base, torso, neck, head, arm(x), arm y, fore rot, forearm len, thigh y, shin y, foot y, coat
  'mrx': dict(base='0, 6.5, 0', neck='0, -10, 0', head='0, -1, 0', ax=6.5, ay=-9, fy=9, fr='-0.1f', az=0.04, th='1.5', shin='7', foot='7', coat=True),
  'nem': dict(base='0, 6, 0', neck='0, -11, 0', head='0, -1, 0', ax=7, ay=-10, fy=10, fr='-0.1f', az=0.04, th='1.5', shin='7', foot='7', coat=True),
  'gan': dict(base='0, 11.5, 0', neck='0, -10, 0', head='0, -1.5, 0', ax=5.5, ay=-9, fy=9, fr='-0.15f', az=0.05, th='1.5', shin='8', foot='8', coat=False),
}
def hum(rig, torso, head, arm, fore, hand, thigh, shin, foot, coat=None, neck=None, hips=None):
    g = RIG[rig]; out = []
    if hips: out.append(A('base', 'PartPose.offset(%s)' % g['base'], *hips))
    out.append(A('base/torso', 'PartPose.offset(0, -1.5, 0)', *torso))
    if neck: out.append(A('base/torso/neck', 'PartPose.offset(%s)' % g['neck'], *neck))
    out.append(A('base/torso/neck/head', 'PartPose.offset(%s)' % g['head'], *head))
    for n, sg in (('L', 1), ('R', -1)):
        out += [A('base/torso/arm' + n, 'PartPose.offsetAndRotation(%sf, %s, 0, 0, 0, %sf)' % (g['ax'] * sg, g['ay'], g['az'] * sg), *arm),
                A('base/torso/arm%s/fore%s' % (n, n), 'PartPose.offsetAndRotation(0, %s, 0, %s, 0, 0)' % (g['fy'], g['fr']), *fore),
                A('base/torso/arm%s/fore%s/hand%s' % (n, n, n), 'PartPose.offset(0, %s, 0)' % g['fy'], *hand),
                A('base/thigh' + n, 'PartPose.offset(%sf, %sf, 0)' % (2 * sg, g['th']), *thigh),
                A('base/thigh%s/shin%s' % (n, n), 'PartPose.offset(0, %s, 0)' % g['shin'], *shin),
                A('base/thigh%s/shin%s/foot%s' % (n, n, n), 'PartPose.offset(0, %s, 0)' % g['foot'], *foot)]
        if g['coat']: out.append(A('base/thigh%s/coat%s' % (n, n), 'PartPose.ZERO', *(coat or [(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'sk')])))
    return out

def rebuild(sid, body, sty, clear=(), keep_old=True, **kw):
    """swap an existing spec's body parts for `body`, keeping its extra (non-body) parts and merging styles."""
    old = SPECS[sid]; paths = {p['path'] for p in body}
    extras = [p for p in old['parts'] if p['path'] not in paths] if keep_old else []
    new = dict(old); new['sty'] = dict(old['sty']); new['sty'].update(sty); new['parts'] = body + extras
    new['clear'] = list(old.get('clear', [])) + [c for c in clear if c not in old.get('clear', [])]
    new.update(kw); SPECS[sid] = new

HAT = ['base/torso/neck/head/hatBrim/hatCrown', 'base/torso/neck/head/hatBrim']
G_CLEAR = ['base/torso/neck/head/hat', 'base/torso/neck/bulb']
N_CLEAR = ['base/torso/neck/head/hatBrim', 'base/torso/armR/foreR/handR/launcher']

# ===================================================================== KRAUSER (RE4): red beret, black tactical vest, grey cargo pants, knee pads, combat knife
SKT = P((205, 170, 140), d='bruise'); SHG = P((98, 100, 104), 0.1); VST = P((26, 28, 32), 0.1); VST2 = P((44, 46, 52), 0.1); CRG = P((86, 88, 92), 0.1); KNP = P((34, 36, 40), 0.06); BOOT = P((30, 26, 24), 0.08); BER = P((160, 28, 36), 0.1); BLD = P((220, 200, 120), 0.1, 'fur'); GLV = P((26, 26, 28), 0.05); STL = P((184, 190, 198), 0.06)
body = hum('gan', hips=[(-4, -1.5, -2, 8, 3, 4, 'cg'), (-4.3, -2.2, -2.3, 8.6, 1.4, 4.6, 'vt2')],
  torso=[(-4, -10, -2, 8, 10, 4, 'sh'), (-4.5, -10.2, -2.5, 9, 9.4, 5, 'vt'), (-3.8, -5.5, -3.1, 2.6, 3, 1.1, 'vt2'), (1.2, -5.5, -3.1, 2.6, 3, 1.1, 'vt2'), (-4.7, -10.8, -1.5, 1.6, 1.8, 3, 'vt2'), (3.1, -10.8, -1.5, 1.6, 1.8, 3, 'vt2'), (-2, -3, -3.1, 4, 1.5, 1.1, 'vt2')],
  neck=[(-1.6, -2, -1.6, 3.2, 3, 3.2, 'sk')],
  head=[(-3.5, -7, -3.5, 7, 7, 7, 'fc'), (-3.9, -8.8, -3.9, 7.8, 2.2, 7.8, 'ber'), (-4, -9.4, -2.6, 8, 1.2, 5, 'ber'), (2.4, -8, -1, 2, 2, 5, 'ber'), (-3.7, -6.5, 1.5, 7.4, 3, 2.4, 'bl')],
  arm=[(-2, 0, -2, 4, 5, 4, 'sh'), (-1.8, 5, -1.8, 3.6, 4, 3.6, 'sk')], fore=[(-1.8, 0, -1.8, 3.6, 6, 3.6, 'sk'), (-2, 5.5, -2, 4, 3.5, 4, 'gl')], hand=[(-2, 0, -2, 4, 3, 4, 'gl')],
  thigh=[(-2.3, 0, -2.3, 4.6, 8, 4.6, 'cg'), (-2.7, 3, -2.1, 1, 3, 4.2, 'vt2')], shin=[(-2.2, 0, -2.2, 4.4, 8, 4.4, 'cg'), (-2.5, 0.2, -2.9, 5, 4.2, 1.2, 'kp')], foot=[(-2.3, 0, -4.6, 4.6, 3, 6.6, 'bt')])
rebuild('krauser', body + [A('base/torso/armR/foreR/handR/knife', 'PartPose.offsetAndRotation(0, 2, -1, -0.9f, 0, 0)', (-0.5, -1, -1, 1, 3, 2, 'gl'), (-0.4, 0, -6, 0.8, 1.6, 6, 'st')), A('base/torso/neck/head/scar', Z, (-3.4, -5, -3.7, 1, 4, 0.5, 'sc'))],
  D(sk=SKT, sh=SHG, vt=VST, vt2=VST2, cg=CRG, kp=KNP, bt=BOOT, ber=BER, bl=BLD, gl=GLV, st=STL, sc=P((140, 60, 60)), fc=face((205, 170, 140), (220, 200, 120), (120, 150, 190), mc=(120, 50, 50), fringe=False)), clear=G_CLEAR, texfrom=None)
SPECS['krauser']['clear'] = list(dict.fromkeys(SPECS['krauser'].get('clear', []) + G_CLEAR))

# ===================================================================== SADDLER (RE4): hooded purple robe, white lace trim, gold pectoral, rope belt, Plaga-tentacle staff
RB = P((74, 62, 112), 0.1); RB2 = P((52, 44, 82), 0.1); LACE = P((226, 224, 232), 0.06); GOLD = P((200, 160, 70), 0.08); OLDF = P((196, 184, 176), 0.08); PLF = P((176, 140, 130), 0.1, 'veins')
body = hum('nem', hips=[(-5.5, -1.5, -3, 11, 4, 6, 'rb2')],
  torso=[(-5, -11, -2.5, 10, 11, 5, 'rb2'), (-5.6, -12, -3.2, 11.2, 4, 6.4, 'rb'), (-1.5, -11, -2.7, 3, 5, 0.4, 'sk'), (-2.2, -9.5, -2.9, 4.4, 4.5, 0.5, 'gd'), (-5.3, -4, -3, 10.6, 1.4, 6, 'gd'), (-0.8, -3.5, -3.3, 1.6, 6, 1, 'gd')],
  neck=[(-1.5, -1, -1.5, 3, 2, 3, 'sk')],
  head=[(-3.5, -7, -3.5, 7, 7, 7, 'fc'), (-4.6, -8.8, -4.6, 9.2, 2.2, 9.2, 'rb'), (-4.6, -8.8, 0.6, 9.2, 10, 4.4, 'rb'), (-4.6, -8.8, -4.6, 1.6, 10, 5.2, 'rb'), (3, -8.8, -4.6, 1.6, 10, 5.2, 'rb'), (-4.7, -8.8, -4.7, 0.5, 10, 5.4, 'lc'), (4.2, -8.8, -4.7, 0.5, 10, 5.4, 'lc')],
  arm=[(-2.8, 0, -2.8, 5.6, 10, 5.6, 'rb')], fore=[(-3.3, 0, -3.3, 6.6, 10, 6.6, 'rb'), (-3.4, 9, -3.4, 6.8, 1.4, 6.8, 'lc')], hand=[(-2, 0, -2, 4, 3, 4, 'sk')],
  thigh=[(-2.4, 0, -2.4, 4.8, 7, 4.8, 'rb2')], shin=[(-2.3, 0, -2.3, 4.6, 7, 4.6, 'rb2')], foot=[(-2.3, 0, -5, 4.6, 2, 6.4, 'sh')],
  coat=[(-3.8, -1, -3.9, 7.6, 17, 7.8, 'rb'), (-3.9, 15, -4, 7.8, 1.4, 8, 'lc'), (-3.9, 0, -4, 7.8, 1.2, 8, 'gd')])
body.append(A('base/torso/armR/foreR/handR/staff', 'PartPose.offsetAndRotation(0, 2, -1, 0, 0, 0)', (-0.5, -14, -0.5, 1, 40, 1, 'gd'), (-1, -10, -1, 2, 1.2, 2, 'gd'), (-1.5, -19, -1.5, 3, 6, 3, 'pf'), (-3, -22, -0.5, 1, 7, 1, 'pf'), (2, -22, -0.5, 1, 7, 1, 'pf'), (-0.5, -26, -0.5, 1, 6, 1, 'pf'), (-2, -25, 1, 1, 6, 1, 'pf'), (1, -24, -2, 1, 6, 1, 'pf'), (-1, -21, -2.5, 1, 5, 1, 'pf')))
rebuild('saddler', body, D(rb=RB, rb2=RB2, lc=LACE, gd=GOLD, sk=OLDF, sh=P((30, 26, 30)), pf=PLF, fc=face((196, 184, 176), (200, 196, 196), (90, 70, 70), mc=(110, 60, 60))), clear=N_CLEAR)

# ===================================================================== MENDEZ (RE4): bald priest-chief in a long brown trench coat, glass left eye
CO = P((78, 54, 38), 0.1); CO2 = P((60, 40, 28), 0.1); BELT = P((34, 24, 18), 0.08); SKM = P((196, 168, 146), 0.1, 'bruise')
body = hum('mrx', hips=[(-4.2, -1.5, -2.4, 8.4, 3, 4.8, 'co2')],
  torso=[(-5.6, -10, -3.2, 11.2, 10, 6.4, 'co'), (-6.6, -12, -3.8, 13.2, 3.5, 7.6, 'co2'), (-5.8, -3.4, -3.5, 11.6, 1.6, 7, 'bl'), (-1, -10, -3.5, 2, 7, 0.4, 'sk')],
  neck=[(-2, -1, -2, 4, 2, 4, 'sk')],
  head=[(-3.8, -7.5, -3.8, 7.6, 7.5, 7.6, 'fc'), (-4.1, -7.8, -4.1, 8.2, 2, 8.2, 'sg'), (-1.2, -4.5, -4.1, 2.4, 2.4, 0.6, 'ey')],
  arm=[(-3, 0, -3, 6, 9, 6, 'co')], fore=[(-2.8, 0, -2.8, 5.6, 9, 5.6, 'co')], hand=[(-2.5, 0, -2.5, 5, 4, 5, 'sk')],
  thigh=[(-2.5, 0, -2.5, 5, 7, 5, 'co2')], shin=[(-2.4, 0, -2.4, 4.8, 7, 4.8, 'co2')], foot=[(-2.5, 0, -5, 5, 2.5, 7, 'bl')], coat=[(-3.6, -1, -4, 7.2, 14, 8, 'co')])
rebuild('mendez', body, D(co=CO, co2=CO2, bl=BELT, sk=SKM, sg=P((150, 150, 150), 0.1, 'fur'), ey=P((236, 236, 226), 0.04), fc=face((196, 168, 146), (150, 150, 150), (60, 40, 30), mc=(90, 40, 40))), clear=HAT)

# ===================================================================== EL GIGANTE (RE4): hulking bald brute, bare scarred chest, ragged trousers, bare feet
SKG = P((176, 150, 128), 0.12, 'bruise'); TRG = P((84, 62, 44), 0.12, 'bruise')
body = hum('mrx', hips=[(-4.8, -1.5, -2.6, 9.6, 3, 5.2, 'tr')],
  torso=[(-6.2, -10, -3.6, 12.4, 10, 7.2, 'sk'), (-7.6, -11.5, -3.4, 3.4, 4.5, 6.8, 'sk'), (4.2, -11.5, -3.4, 3.4, 4.5, 6.8, 'sk'), (-5, -9.5, -3.9, 4.6, 3.5, 0.6, 'sk2'), (0.4, -9.5, -3.9, 4.6, 3.5, 0.6, 'sk2'), (-5.4, -2, -3.8, 10.8, 2, 7.6, 'bl')],
  neck=[(-2.6, -2, -2.4, 5.2, 3, 4.8, 'sk')],
  head=[(-3.6, -7, -3.8, 7.2, 6.5, 7.6, 'fc'), (-3, -2.5, -5.2, 6, 2.6, 2, 'sk'), (-4, -7.8, -4, 8, 1.4, 3, 'sk2')],
  arm=[(-3.6, 0, -3.6, 7.2, 9, 7.2, 'sk')], fore=[(-3.4, 0, -3.4, 6.8, 9, 6.8, 'sk')], hand=[(-3.2, 0, -3.2, 6.4, 5, 6.4, 'sk')],
  thigh=[(-3, 0, -3, 6, 7, 6, 'tr')], shin=[(-2.8, 0, -2.8, 5.6, 5, 5.6, 'tr'), (-2.9, 5, -2.9, 5.8, 2, 5.8, 'sk')], foot=[(-3, 0, -5.8, 6, 2, 8, 'sk')], coat=[(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'sk')])
SPECS.setdefault('el_gigante', D(id='el_gigante', pkg='elgigante', C='ElGigante', ppkg='mrx', pm='MrXModel', W=128, H0=64, sty=D(), parts=[], clear=[]))
rebuild('el_gigante', body + [A('base/torso/plaga', Z, (-3, -9, 3.6, 6, 6, 3, 'pl'), (-1.5, -11, 4.2, 3, 3, 2, 'pl'))], D(sk=SKG, sk2=P((150, 120, 100), 0.12, 'bruise'), tr=TRG, bl=P((40, 30, 22)), pl=P((150, 60, 60), 0.1, 'veins'), fc=face((176, 150, 128), (176, 150, 128), (30, 20, 20), mc=(80, 30, 30))), clear=HAT)

# ===================================================================== OGROMAN (RE6): huge grey-teal brute with bone spurs and glowing C-virus nodules
OSK = P((128, 150, 150), 0.12, 'veins'); OTR = P((58, 54, 50), 0.12, 'bruise'); NOD = P((236, 150, 40), 0.08)
body = hum('mrx', hips=[(-5, -1.5, -3, 10, 3, 6, 'tr')],
  torso=[(-6.8, -10, -4.2, 13.6, 10, 8.4, 'sk'), (-10, -12, -4, 6.4, 6, 8, 'sk'), (4.2, -11, -3.4, 4, 4.5, 6.8, 'sk'), (-4, -13, 0.5, 8, 4, 5, 'sk'), (-2.5, -8, -4.5, 1.6, 1.6, 0.6, 'nd'), (3, -5, -4.5, 1.6, 1.6, 0.6, 'nd'), (-5, -4, -4.5, 1.4, 1.4, 0.6, 'nd')],
  neck=[(-2, -1, -2, 4, 2, 4, 'sk')], head=[(-3, -6, -3, 6, 6, 6, 'fc'), (-3.2, -2.2, -4, 6.4, 2, 2, 'bn')],
  arm=[(-4, 0, -4, 8, 9, 8, 'sk')], fore=[(-4.4, 0, -4.4, 8.8, 9, 8.8, 'sk')], hand=[(-4, 0, -4, 8, 6, 8, 'sk')],
  thigh=[(-3.2, 0, -3.2, 6.4, 7, 6.4, 'tr')], shin=[(-3, 0, -3, 6, 7, 6, 'tr')], foot=[(-3.2, 0, -6, 6.4, 2.5, 8.4, 'sk')], coat=[(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'sk')])
rebuild('ogroman', body, D(sk=OSK, tr=OTR, nd=NOD, bn=P((214, 204, 172), 0.08), fc=face((128, 150, 150), (128, 150, 150), (240, 200, 60), mc=(90, 30, 30))), clear=HAT)

# ===================================================================== EXECUTIONER MAJINI (RE5): towering muscular man, bare chest, black trousers, bloodied apron + hood + axe (kept)
ESK = P((120, 84, 64), 0.12, 'bruise'); ETR = P((40, 36, 34), 0.1)
body = hum('mrx', hips=[(-4.6, -1.5, -2.6, 9.2, 3, 5.2, 'tr')],
  torso=[(-6, -10, -3.4, 12, 10, 6.8, 'sk'), (-7.4, -11.5, -3.2, 3.2, 4.5, 6.4, 'sk'), (4.2, -11.5, -3.2, 3.2, 4.5, 6.4, 'sk'), (-5.8, -2.6, -3.7, 11.6, 1.8, 7.4, 'bl')],
  neck=[(-2.4, -2, -2.2, 4.8, 3, 4.4, 'sk')], head=[(-3.4, -7, -3.4, 6.8, 7, 6.8, 'sk')],
  arm=[(-3.4, 0, -3.4, 6.8, 9, 6.8, 'sk')], fore=[(-3.3, 0, -3.3, 6.6, 9, 6.6, 'sk'), (-3.5, 6, -3.5, 7, 3, 7, 'bl')], hand=[(-3, 0, -3, 6, 4.5, 6, 'sk')],
  thigh=[(-2.9, 0, -2.9, 5.8, 7, 5.8, 'tr')], shin=[(-2.7, 0, -2.7, 5.4, 7, 5.4, 'tr')], foot=[(-2.9, 0, -5.4, 5.8, 2.5, 7.4, 'bt')], coat=[(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'sk')])
rebuild('executioner_majini', body, D(sk=ESK, tr=ETR, bl=P((30, 24, 20)), bt=P((34, 28, 26))), clear=HAT)

# ===================================================================== WESKER (RE5): long black leather coat, white shirt, sunglasses, slicked-back blond hair
CT = P((20, 20, 24), 0.08); WSH = P((226, 226, 230), 0.05); WSK = P((226, 202, 186), 0.06); WHR = P((222, 200, 120), 0.08, 'fur'); GLV2 = P((14, 14, 16), 0.04)
body = hum('mrx', hips=[(-4, -1.5, -2.2, 8, 3, 4.4, 'ct')],
  torso=[(-4.6, -10, -2.6, 9.2, 10, 5.2, 'ct'), (-1.6, -10, -2.8, 3.2, 7, 0.5, 'wh'), (-5.2, -11.2, -3, 10.4, 2.4, 6, 'ct')],
  neck=[(-1.5, -1, -1.5, 3, 2, 3, 'sk')],
  head=[(-3.5, -7, -3.5, 7, 7, 7, 'fc'), (-3.8, -8.2, -3.8, 7.6, 2.2, 7.6, 'hr'), (-3.7, -6.5, 1.6, 7.4, 5, 2.2, 'hr')],
  arm=[(-2.2, 0, -2.2, 4.4, 9, 4.4, 'ct')], fore=[(-2.1, 0, -2.1, 4.2, 9, 4.2, 'ct')], hand=[(-2, 0, -2, 4, 3, 4, 'gl')],
  thigh=[(-2.2, 0, -2.2, 4.4, 7, 4.4, 'ct')], shin=[(-2.1, 0, -2.1, 4.2, 7, 4.2, 'ct')], foot=[(-2.2, 0, -4.8, 4.4, 2, 6.4, 'ct')], coat=[(-3, -1, -3.5, 6, 17, 7, 'ct'), (-3.1, 15, -3.6, 6.2, 1, 7.2, 'gl')])
rebuild('wesker', body, D(ct=CT, wh=WSH, sk=WSK, hr=WHR, gl=GLV2, fc=face((226, 202, 186), (222, 200, 120), shades=True)), clear=HAT)

# ===================================================================== JILL (RE5): black bodysuit, blond bob, chest device, thigh-high boots
BS = P((24, 24, 30), 0.08); JSK = P((226, 202, 186), 0.06); JHR = P((220, 198, 120), 0.08, 'fur')
body = hum('mrx', hips=[(-3.8, -1.5, -2.2, 7.6, 3, 4.4, 'bs')],
  torso=[(-3.8, -10, -2.2, 7.6, 10, 4.4, 'bs'), (-4, -10.4, -2.4, 8, 2, 4.8, 'sk')],
  neck=[(-1.4, -1, -1.4, 2.8, 2, 2.8, 'sk')],
  head=[(-3.3, -7, -3.3, 6.6, 7, 6.6, 'fc')],
  arm=[(-1.7, 0, -1.7, 3.4, 5, 3.4, 'sk'), (-1.8, 5, -1.8, 3.6, 4, 3.6, 'bs')], fore=[(-1.7, 0, -1.7, 3.4, 9, 3.4, 'bs')], hand=[(-1.7, 0, -1.7, 3.4, 3, 3.4, 'bs')],
  thigh=[(-2, 0, -2, 4, 7, 4, 'bs')], shin=[(-1.9, 0, -1.9, 3.8, 7, 3.8, 'bs')], foot=[(-2, 0, -4.4, 4, 2.4, 6, 'bs')], coat=[(-0.1, 0, -0.1, 0.2, 0.2, 0.2, 'bs')])
rebuild('jill', body, D(bs=BS, sk=JSK, h=JHR, fc=face((226, 202, 186), (220, 198, 120), (90, 130, 180), mc=(160, 70, 70))), clear=HAT)

# ===================================================================== SIMMONS (RE6): grey business suit, tie, short grey hair; mutation mass only in mutated forms
SU = P((52, 54, 62), 0.08); SHI = P((226, 226, 232), 0.05); TIE = P((150, 30, 36), 0.06); SSK = P((206, 176, 156), 0.08)
body = hum('nem', hips=[(-4.5, -1.5, -2, 9, 3, 4, 'su')],
  torso=[(-5, -11, -2.5, 10, 11, 5, 'su'), (-1.5, -11, -2.7, 3, 8, 0.4, 'shi'), (-0.7, -10, -2.9, 1.4, 7, 0.4, 'ti')],
  neck=[(-1.5, -1, -1.5, 3, 2, 3, 'sk')], head=[(-3.5, -7, -3.5, 7, 7, 7, 'fc'), (-3.8, -8, -3.8, 7.6, 1.8, 7.6, 'hr'), (-3.7, -6.5, 1.5, 7.4, 4, 2.2, 'hr')],
  arm=[(-2, 0, -2, 4, 10, 4, 'su')], fore=[(-2, 0, -2, 4, 10, 4, 'su')], hand=[(-2, 0, -2, 4, 3, 4, 'sk')],
  thigh=[(-2.2, 0, -2.2, 4.4, 7, 4.4, 'su')], shin=[(-2.1, 0, -2.1, 4.2, 7, 4.2, 'su')], foot=[(-2.1, 0, -5, 4.2, 2, 6.4, 'sh2')], coat=[(-2.6, -1, -3, 5.2, 9, 6, 'su')])
rebuild('simmons', body, D(su=SU, shi=SHI, ti=TIE, sk=SSK, hr=P((150, 150, 154), 0.08, 'fur'), sh2=P((20, 20, 22)), fc=face((206, 176, 156), (150, 150, 154), (60, 80, 120), mc=(120, 60, 60))), clear=N_CLEAR)
cm.VIS['simmons'] = [(p, 'form >= 1') for p in ('base/torso/mass', 'base/torso/armR/lump', 'base/torso/spines', 'base/torso/neck/head/jawmass')]

# ===================================================================== DIMITRESCU (RE8): towering woman in a white gown, black gloves, plunging neckline, wide hat (kept)
WD = P((236, 232, 228), 0.04); DSK = P((226, 206, 196), 0.06); DGL = P((20, 18, 22), 0.04)
body = hum('mrx', hips=[(-4, -1.5, -2.4, 8, 3, 4.8, 'wd')],
  torso=[(-4.2, -10, -2.4, 8.4, 10, 4.8, 'wd'), (-1.6, -10, -2.6, 3.2, 4.5, 0.5, 'sk'), (-4.4, -4, -2.6, 8.8, 1.4, 5.2, 'dk')],
  neck=[(-1.3, -1, -1.3, 2.6, 2, 2.6, 'sk')], head=[(-3.2, -7, -3.2, 6.4, 7, 6.4, 'fc')],
  arm=[(-1.8, 0, -1.8, 3.6, 9, 3.6, 'wd')], fore=[(-1.8, 0, -1.8, 3.6, 9, 3.6, 'gl')], hand=[(-1.8, 0, -1.8, 3.6, 3, 3.6, 'gl')],
  thigh=[(-2, 0, -2, 4, 7, 4, 'sk')], shin=[(-1.8, 0, -1.8, 3.6, 7, 3.6, 'sk')], foot=[(-2, 0, -4.4, 4, 2, 6, 'dk')], coat=[(-3.6, -1, -4, 7.2, 16, 8, 'wd'), (-3.7, 14, -4.1, 7.4, 1.4, 8.2, 'dk')])
rebuild('dimitrescu', body, D(wd=WD, sk=DSK, gl=DGL, dk=P((30, 26, 30)), fc=face((226, 206, 196), (30, 24, 28), (90, 60, 50), mc=(130, 40, 50))), clear=HAT)

if __name__ == '__main__':
    ids = sys.argv[1:] or ['krauser', 'saddler', 'mendez', 'el_gigante', 'ogroman', 'executioner_majini', 'wesker', 'jill', 'simmons', 'dimitrescu']
    for i in ids: vgen(SPECS[i])
    json.dump({'VIS': cm.VIS, 'SPIN': cm.SPIN}, open(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'canon_vis.json'), 'w'), indent=1)
