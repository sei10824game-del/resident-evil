"""Remake-edition creatures: entities + textures (tinted parent copies) + silhouettes. Run: python3 tools/variants_remake.py [id ...]"""
import os, re, sys, glob, subprocess
from PIL import Image, ImageEnhance
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import variantgen as vg
import remake_moves as rm
from variantgen import spec, A, Z, SPECS, F, gen as vgen
from genfam import gen as famgen
from texlib import OUT

TOOLS = os.path.dirname(os.path.abspath(__file__)); J = TOOLS + '/../src/main/java/com/reevil/'
D = dict
# palettes
CAPN = D(c=(22, 30, 52), n=0.08); VEST = D(c=(30, 36, 44), n=0.1, d='bruise'); RIB = vg.BONE; MUS = D(c=(150, 52, 54), n=0.12, d='veins'); BRAIN = D(c=(214, 130, 150), n=0.1, d='veins')
LEAT = D(c=(24, 22, 26), n=0.1); SCAR = D(c=(176, 150, 130), n=0.1, d='face', hc=(40, 34, 30), mc=(70, 20, 20), ec=(200, 60, 40))
CLOTHB = D(c=(120, 100, 80), n=0.12, d='bruise'); WOOLC = D(c=(110, 70, 60), n=0.1); FLESHL = vg.FLESH2; PURP = D(c=(86, 40, 100), n=0.1); GOLD = vg.GOLD
STEEL = vg.STEEL; PLATE = vg.PLATE; DARK = vg.DARK; SPK = vg.SPKB; BLOOD = vg.BLOOD; WOOD = vg.WOOD; HAIRB = vg.HAIRB; HAIRBR = vg.HAIRBR; MOUTH = vg.MOUTH

R = []   # (id, C, parent dict, tab, en, ja, w, h, egg1, egg2, hpx, spx, dmx, faction)
def fam(id, C, par, tab, en, ja, w, h, e1, e2, hpx=1.2, spx=1.08, dmx=1.15, scale='1.0f', shadow='0.6f', files=None, doc=''):
    R.append(D(id=id, C=C, par=par, tab=tab, en=en, ja=ja, w=w, h=h, e1=e1, e2=e2, hpx=hpx, spx=spx, dmx=dmx, scale=scale, shadow=shadow, files=files, doc=doc or en))

# parent: (pk, ParentClass, ModelClass, rootEntity(ret))
P = lambda pk, C, ret, rk=None, tex=None: D(pk=pk, C=C, ret=ret, rk=rk or pk, tex=tex)
PZ = P('zombie', 'Zombie', 'ZombieEntity', tex='zombie'); PL = P('licker', 'Licker', 'LickerEntity', tex='licker'); PX = P('mrx', 'MrX', 'MrXEntity', tex='mr_x'); PN = P('nemesis', 'Nemesis', 'NemesisEntity', tex='nemesis')
PH = P('hunter', 'Hunter', 'HunterEntity', tex='hunter'); PHB = P('hunterbeta', 'HunterBeta', 'HunterEntity', 'hunter', tex='hunter_beta'); PG = P('ganado', 'Ganado', 'GanadoEntity', tex='ganado'); PEG = P('elgigante', 'ElGigante', 'MrXEntity', 'mrx', tex='el_gigante')
PV = P('verdugo', 'Verdugo', 'MrXEntity', 'mrx', tex='verdugo'); PRG = P('regenerador', 'Regenerador', 'ZombieEntity', 'zombie', tex='regenerador'); PK = P('krauser', 'Krauser', 'GanadoEntity', 'ganado', tex='krauser')
PS = P('salazar', 'Salazar', 'Plant42Entity', 'plant42', tex='salazar'); PA = P('armadura', 'Armadura', 'ZombieEntity', 'zombie', tex='armadura'); PIM = P('ironmaiden', 'IronMaiden', 'ZombieEntity', 'zombie', tex='iron_maiden'); PPH = P('palehead', 'PaleHead', 'ZombieEntity', 'zombie', tex='pale_head')
TEXFILES = {'zombie': ['zombie_civilian', 'zombie_labcoat', 'zombie_stars'], 'licker': ['licker', 'licker_beta'], 'mr_x': ['mr_x', 'mr_x_super'], 'nemesis': ['nemesis', 'nemesis_final', 'nemesis_mutated'],
  'hunter': ['hunter', 'hunter_red'], 'hunter_beta': ['hunter_beta', 'hunter_beta_red'], 'ganado': ['ganado', 'ganado_plaga'], 'el_gigante': ['el_gigante'], 'verdugo': ['verdugo'], 'regenerador': ['regenerador'],
  'krauser': ['krauser'], 'salazar': ['salazar', 'salazar_phase2'], 'armadura': ['armadura'], 'iron_maiden': ['iron_maiden'], 'pale_head': ['pale_head']}
PID = {'zombie': 'zombie', 'licker': 'licker', 'mr_x': 'mr_x', 'nemesis': 'nemesis', 'hunter': 'hunter', 'hunter_beta': 'hunter_beta', 'ganado': 'ganado', 'el_gigante': 'el_gigante', 'verdugo': 'verdugo',
  'regenerador': 'regenerador', 'krauser': 'krauser', 'salazar': 'salazar', 'armadura': 'armadura', 'iron_maiden': 'iron_maiden', 'pale_head': 'pale_head'}

# ---- RE1 remake
fam('re1r_zombie', 'Re1rZombie', PZ, 're1r', 'Zombie (REmake)', 'ゾンビ (REmakeリメイク)', 0.6, 1.9, '0x7a8a70', '0x3a2020', doc='REmake zombie: gaunt grey-green, ragged, exposed ribs.')
fam('re1r_hunter', 'Re1rHunter', PH, 're1r', 'Hunter (REmake)', 'ハンター (REmakeリメイク)', 0.8, 1.8, '0x2f6a3a', '0xa03020', doc='REmake Hunter: dorsal plates, bone claws.')
# ---- RE2 remake
fam('re2r_zombie', 'Re2rZombie', PZ, 're2r', 'Zombie (RE2 Remake)', 'ゾンビ (RE2リメイク)', 0.6, 1.9, '0x56624a', '0x1a2a4a', doc='RE2 Remake zombie: RPD cap and vest, torn flesh.')
fam('re2r_licker', 'Re2rLicker', PL, 're2r', 'Licker (RE2 Remake)', 'リッカー (RE2リメイク)', 1.0, 1.4, '0xa04050', '0xd08090', hpx=1.3, doc='RE2 Remake Licker: skinless bulk, back spikes, long claws.')
fam('re2r_mr_x', 'Re2rMrX', PX, 're2r', 'Mr. X (RE2 Remake)', 'ミスターX (RE2リメイク)', 0.9, 2.5, '0x2a2a2e', '0x8a7a60', doc='RE2 Remake Tyrant T-103: long leather coat, wide fedora, scarred face.', scale='1.1f')
# ---- RE3 remake
fam('re2r_nemesis', 'Re2rNemesis', PN, 're2r', 'Nemesis (RE2 Remake)', 'ネメシス (RE2リメイク)', 0.9, 2.5, '0x3a2e2a', '0xd0c090', hpx=1.1, doc='Nemesis-T in the RE2 Remake roster: rocket launcher plus a machine gun.', scale='1.1f')
fam('re3r_zombie', 'Re3rZombie', PZ, 're3r', 'Zombie (RE3 Remake)', 'ゾンビ (RE3リメイク)', 0.6, 1.9, '0x6a6050', '0x8a2020', doc='RE3 Remake zombie: torn civilian clothes, long hair.')
fam('re3r_nemesis', 'Re3rNemesis', PN, 're3r', 'Nemesis (RE3 Remake)', 'ネメシス (RE3リメイク)', 0.9, 2.5, '0x7a5048', '0xd0c090', hpx=1.2, doc='RE3 Remake Nemesis: skinless bulk, no coat, stitched mouth.', scale='1.1f')
fam('re3r_pale_head', 'Re3rPaleHead', PPH, 're3r', 'Pale Head (RE3 Remake)', 'ペイルヘッド (RE3リメイク)', 0.7, 2.2, '0xc8c0b8', '0x903030', doc='RE3 Remake Pale Head: gaunt, domed head, bone spines.')
fam('re3r_hunter_beta', 'Re3rHunterBeta', PHB, 're3r', 'Hunter Beta (RE3 Remake)', 'ハンターβ (RE3リメイク)', 0.8, 1.8, '0x2a4a6a', '0xc04030', doc='RE3 Remake Hunter Beta: armoured shoulders, head spines.')
# ---- RE4 remake
fam('re4r_ganado', 'Re4rGanado', PG, 're4r', 'Ganado (RE4 Remake)', 'ガナード (RE4リメイク)', 0.6, 1.9, '0x6a5a40', '0x303a50', doc='RE4 Remake villager: bandana, scruffy beard, wicker pack.')
fam('re4r_el_gigante', 'Re4rElGigante', PEG, 're4r', 'El Gigante (RE4 Remake)', 'エル・ヒガンテ (RE4リメイク)', 1.6, 4.4, '0x8a7a68', '0x4a3a30', doc='RE4 Remake El Gigante: shaggy hair and beard, back tumour mass, club.', scale='1.0f')
fam('re4r_verdugo', 'Re4rVerdugo', PV, 're4r', 'Verdugo (RE4 Remake)', 'ベルデューゴ (RE4リメイク)', 0.9, 2.5, '0x70402a', '0xd0d0c0', doc='RE4 Remake Verdugo: hooded exposed-bone frame.')
fam('re4r_regenerador', 'Re4rRegenerador', PRG, 're4r', 'Regenerador (RE4 Remake)', 'レヘネラドール (RE4リメイク)', 0.7, 2.0, '0x5a5a60', '0xa03030', doc='RE4 Remake Regenerador: breath mask, chest maw.')
fam('re4r_krauser', 'Re4rKrauser', PK, 're4r', 'Krauser (RE4 Remake)', 'クラウザー (RE4リメイク)', 0.7, 2.0, '0x3a4a36', '0xa0a0a0', doc='RE4 Remake Krauser: bandoliers, scarred face, blade arm.')
fam('re4r_salazar', 'Re4rSalazar', PS, 're4r', 'Salazar (RE4 Remake)', 'サラザール (RE4リメイク)', 1.6, 3.0, '0x56305a', '0xc0a040', doc='RE4 Remake Salazar: robed lord with a crown.', scale='1.15f', shadow='1.4f')
fam('re4r_armadura', 'Re4rArmadura', PA, 're4r', 'Armadura (RE4 Remake)', 'アルマドゥーラ (RE4リメイク)', 0.8, 2.2, '0x7a7a82', '0xa05050', doc='RE4 Remake Armadura: plumed helm, tower shield, halberd.', scale='1.1f')
fam('re4r_iron_maiden', 'Re4rIronMaiden', PIM, 're4r', 'Iron Maiden (RE4 Remake)', 'アイアンメイデン (RE4リメイク)', 0.8, 2.0, '0x9a9aa2', '0xa03030', doc='RE4 Remake Iron Maiden: spiked cage on both sides.')

# ---- silhouettes
def S(id, par, ppkg_pm, parts, sty, clear=None, **kw):
    f = next(x for x in R if x['id'] == id); pk = id.replace('_', '')
    spec(id=id, pkg=pk, C=f['C'], ppkg=f['par']['pk'], pm=f['par']['C'] + 'Model', W=kw.pop('W'), H0=kw.pop('H0'), sty=sty, parts=parts, clear=clear or [],
         clipfn=id if id in rm.MOV else None, clipcls='com.reevil.client.remake.RemakeClips', clipent='com.reevil.entity.' + rm.ENT.get(id, 'x.X'), **kw)

CH = 'body/waist/chest'; HD = CH + '/neck/head'
S('re1r_zombie', PZ, 0, [A(CH + '/ribs', Z, (-4, -5, -3, 8, 1, 1, 'b'), (-4, -3, -3, 8, 1, 1, 'b'), (-4, -1, -3, 8, 1, 1, 'b'), (-4.6, -6, -2.6, 9.2, 2, 5.2, 'c')), A(CH + '/rags', Z, (-4.7, -6, -2.7, 4, 6, 5.4, 'c'), (1.5, -3, -2.7, 3.2, 4, 5.4, 'c'), (-3, 0, -2.2, 6, 5, 4.4, 'c')),
   A(HD + '/wound', Z, (-4.2, -6, -4.2, 3, 3, 8.4, 'f'), (-1, -7.5, -2, 4, 2, 4, 'f'))], dict(b=RIB, c=CLOTHB, f=FLESHL), W=128, H0=64)
S('re1r_hunter', PH, 0, [A('base/hips/chest/plates', Z, (-6, -15, -3, 12, 3, 6, 'p'), (-4, -12, 4, 8, 3, 3, 'p'), (-3, -9, 4, 6, 3, 3, 'p'), (-2, -6, 4, 4, 3, 3, 'p'), (-8, -14, -2, 3, 3, 4, 's'), (5, -14, -2, 3, 3, 4, 's')),
   A('base/hips/chest/neck/head/crest', Z, (-1.5, -8, -3, 3, 4, 5, 'p'), (-4, -5.5, -5.5, 2, 2, 3, 's'), (2, -5.5, -5.5, 2, 2, 3, 's')),
   A('base/hips/chest/armL/foreL/handL/boneL', 'PartPose.offsetAndRotation(0, 3, -2, -0.35f, 0, 0)', (-0.5, 0, -1, 1, 8, 1, 'b'), (1, 0, -1, 1, 7, 1, 'b')), A('base/hips/chest/armR/foreR/handR/boneR', 'PartPose.offsetAndRotation(0, 3, -2, -0.35f, 0, 0)', (-0.5, 0, -1, 1, 8, 1, 'b'), (-2, 0, -1, 1, 7, 1, 'b'))],
   dict(p=D(c=(34, 74, 46), n=0.1, d='spike'), s=SPK, b=RIB), W=128, H0=128)
S('re2r_zombie', PZ, 0, [A(HD + '/cap', Z, (-4.5, -7.5, -4.5, 9, 2, 9, 'n'), (-4.5, -6, -6.5, 9, 1, 3, 'n'), (-1.5, -7, -4.7, 3, 1.5, 1, 'y')), A(CH + '/vest', Z, (-5, -6.2, -3, 10, 6.2, 6, 'v'), (-4, -4, -3.2, 3, 1.5, 1, 'y')), A(CH + '/ribs', Z, (-4, -1, -3.2, 8, 1, 1, 'b'))],
   dict(n=CAPN, v=VEST, y=D(c=(196, 170, 60), n=0.06), b=RIB), W=128, H0=64)
S('re2r_licker', PL, 0, [A('base/torso/spikes', Z, (-1, -7, -9, 2, 4, 2, 'b'), (-1, -7, -6, 2, 5, 2, 'b'), (-1, -7, -3, 2, 6, 2, 'b'), (-1, -6, 0, 2, 5, 2, 'b'), (-4.5, -5, -4, 2, 3, 2, 'b'), (2.5, -5, -4, 2, 3, 2, 'b')), A('base/torso/muscle', Z, (-5, -4.5, -10.5, 10, 8, 11, 'm')),
   A('base/torso/armL/foreL/handL/blade', 'PartPose.offsetAndRotation(0, 2, -1, 0.3f, 0, 0)', (-0.5, 0, -1, 1, 10, 1, 'b'), (-2, 0, -1, 1, 9, 1, 'b')), A('base/torso/armR/foreR/handR/blade', 'PartPose.offsetAndRotation(0, 2, -1, 0.3f, 0, 0)', (-0.5, 0, -1, 1, 10, 1, 'b'), (1, 0, -1, 1, 9, 1, 'b')),
   A('base/torso/neck1/neck2/head/brain', Z, (-3.5, -8, -7, 7, 3, 6, 'br'))], dict(b=RIB, m=MUS, br=BRAIN), W=128, H0=64)
S('re2r_mr_x', PX, 0, [A('base/torso/neck/head/hat', Z, (-6.5, -7.5, -6.5, 13, 1, 13, 'l'), (-4, -11, -4, 8, 4, 8, 'l'), (-4.1, -8, -4.1, 8.2, 1, 8.2, 'g')), A('base/torso/neck/head/scars', Z, (-3.6, -5, -3.9, 7.2, 1, 1, 'f'), (-1, -3, -3.9, 1, 3, 1, 'f')),
   A('base/torso/collar', Z, (-6, -11.5, -3.5, 12, 3, 7, 'l'), (-5.5, -9, -3, 11, 8, 6, 'l'))] + [A('base/thigh%s/coat%s' % (n, n), 'PartPose.ZERO', (-3.2, -1, -3.8, 6.4, 17, 7.6, 'l')) for n in 'LR'], dict(l=LEAT, f=SCAR, g=D(c=(120, 20, 24), n=0.06)), W=128, H0=64)
S('re3r_zombie', PZ, 0, [A(HD + '/hair', Z, (-4.5, -7, -4.5, 9, 2, 9, 'h'), (-4.5, -6, 1, 9, 12, 3, 'h'), (-4.8, -6, -4, 1.5, 9, 4, 'h'), (3.3, -6, -4, 1.5, 9, 4, 'h')), A('body/skirt', Z, (-4.8, -1, -2.8, 9.6, 6, 5.6, 'd'), (-5.2, 4, -3.2, 10.4, 5, 6.4, 'd')),
   A(CH + '/top', Z, (-4.8, -6, -2.8, 9.6, 4, 5.6, 'd'))], dict(h=HAIRBR, d=D(c=(130, 90, 100), n=0.14, d='bruise')), W=128, H0=64)
S('re3r_nemesis', PN, 0, [A('base/torso/muscle', Z, (-5.5, -11, -3, 11, 7, 6, 'm'), (-7.5, -12, -3, 3, 5, 6, 'm'), (4.5, -12, -3, 3, 5, 6, 'm')), A('base/torso/neck/head/jaw', Z, (-3.7, -3, -4, 7.4, 3, 1.5, 'mo'), (-3.7, -3.2, -4.6, 7.4, 1, 1, 'b')),
   A('base/torso/spikes', Z, (-1, -13, 2, 2, 4, 2, 'b'), (-4, -11, 2, 2, 4, 2, 'b'), (2, -11, 2, 2, 4, 2, 'b'), (-8.5, -13.5, -1, 2, 3, 2, 'b'), (6.5, -13.5, -1, 2, 3, 2, 'b'))], dict(m=MUS, mo=MOUTH, b=RIB), W=128, H0=64)
S('re3r_pale_head', PPH, 0, [A(HD + '/dome', Z, (-3, -13, -3, 6, 8, 6, 'p'), (-3.5, -9, -3.5, 7, 2, 7, 'v')), A(CH + '/spines', Z, (-1, -6, 2.5, 2, 3, 2, 'b'), (-1, -3, 2.5, 2, 3, 2, 'b'), (-4, -5, 2.5, 2, 3, 2, 'b'), (2, -5, 2.5, 2, 3, 2, 'b'), (-4.6, -5.5, -2.7, 9.2, 1, 5.4, 'b'), (-4.6, -3.5, -2.7, 9.2, 1, 5.4, 'b'))],
   dict(p=D(c=(212, 204, 196), n=0.05), v=D(c=(150, 60, 66), n=0.1, d='veins'), b=RIB), W=128, H0=64)
S('re3r_hunter_beta', PHB, 0, [A('base/hips/chest/armor', Z, (-8, -14, -4, 16, 5, 9, 'p'), (-9, -13, -3, 3, 7, 7, 'p'), (6, -13, -3, 3, 7, 7, 'p'), (-4, -9, 4, 8, 7, 3, 'p')), A('base/hips/chest/neck/head/spines', Z, (-1, -7, -3, 2, 3, 2, 's'), (-1, -6.5, 0, 2, 3, 2, 's'), (-3.5, -6.5, -2, 2, 3, 2, 's'), (1.5, -6.5, -2, 2, 3, 2, 's'))],
   dict(p=D(c=(40, 60, 90), n=0.1, d='spike'), s=SPK), W=128, H0=128)
S('re4r_ganado', PG, 0, [A('base/torso/neck/head/bandana', Z, (-4, -5.5, -4.2, 8, 2, 8.4, 'c'), (-1, -5.5, 3.5, 2, 4, 1, 'c')), A('base/torso/neck/head/beard', Z, (-3.6, -2, -3.8, 7.2, 3, 1, 'h')),
   A('base/torso/pack', Z, (-3.5, -9, 2.3, 7, 8, 3, 'w'), (-3, -10, 2.8, 6, 1, 2, 'w')), A('base/torso/vest', Z, (-4.5, -9.5, -2.5, 9, 8, 5, 'k'))], dict(c=D(c=(176, 52, 40), n=0.1), h=HAIRBR, w=D(c=(150, 112, 70), n=0.12), k=WOOLC), W=128, H0=64)
S('re4r_el_gigante', PEG, 0, [A('base/torso/neck/head/hair', Z, (-4, -8.5, -4, 8, 3, 8, 'h'), (-4.5, -7, 1.5, 9, 14, 3, 'h'), (-4.2, -3, -4, 8.4, 7, 2, 'h')), A('base/torso/tumor2', Z, (-6, -9, 2.5, 10, 8, 6, 'f'), (-3, -12, 3, 5, 4, 4, 'f')),
   A('base/loin', Z, (-5, 0, -3, 10, 6, 1, 'l'), (-5, 0, 2, 10, 6, 1, 'l')), A('base/torso/armR/foreR/handR/club', 'PartPose.offsetAndRotation(0, 2, -1, -0.5f, 0, 0)', (-0.8, -2, -0.8, 1.6, 20, 1.6, 'w'), (-2.5, 14, -2.5, 5, 10, 5, 'w'))], dict(h=D(c=(60, 54, 50), n=0.1, d='fur'), f=FLESHL, l=D(c=(100, 80, 60), n=0.1), w=WOOD), W=128, H0=64)
S('re4r_verdugo', PV, 0, [A('base/torso/neck/head/hood', Z, (-4, -8.5, -1, 8, 9, 5, 'k'), (-4, -8.5, -4, 1.5, 7, 4, 'k'), (2.5, -8.5, -4, 1.5, 7, 4, 'k'), (-3.5, -9.5, -4, 7, 1.5, 6, 'k')), A('base/torso/neck/head/maskb', Z, (-3, -6, -4.3, 6, 4, 1, 'b')),
   A('base/torso/ribs', Z, (-4.8, -9, -2.6, 9.6, 1, 5.2, 'b'), (-4.8, -6.5, -2.6, 9.6, 1, 5.2, 'b'), (-4.8, -4, -2.6, 9.6, 1, 5.2, 'b'), (-6.5, -11, -2, 3, 3, 4, 'm'), (3.5, -11, -2, 3, 3, 4, 'm')),
   A('base/torso/armR/foreR/blade', Z, (-0.5, 1, -3, 1, 14, 3, 's')), A('base/torso/armL/foreL/blade', Z, (-0.5, 1, -3, 1, 14, 3, 's'))], dict(k=D(c=(40, 30, 26), n=0.1), b=RIB, m=MUS, s=STEEL), W=128, H0=64)
S('re4r_regenerador', PRG, 0, [A(HD + '/mask', Z, (-3.8, -3.5, -4.3, 7.6, 3.5, 2, 's'), (-1, -3, -5.5, 2, 2, 2, 'd'), (-3, -7, -4.3, 6, 1, 1, 'd')), A(CH + '/maw', Z, (-3.5, -5, -3.3, 7, 5, 1, 'mo'), (-4, -6, -3.2, 8, 1, 1, 'b')),
   A(CH + '/spine', Z, (-1, -6, 2.5, 2, 3, 2, 'b'), (-1, -3, 2.5, 2, 3, 2, 'b'))], dict(s=D(c=(90, 92, 100), n=0.08), d=DARK, mo=MOUTH, b=RIB), W=128, H0=64)
S('re4r_krauser', PK, 0, [A('base/torso/bandolier', Z, (-4.6, -9.5, -2.6, 9.2, 2, 5.2, 'l'), (-4.4, -8.5, -2.8, 2, 7, 5.6, 'l'), (-3.5, -9, -2.9, 1, 2, 1, 'g'), (-1.5, -7, -2.9, 1, 2, 1, 'g'), (0.5, -5, -2.9, 1, 2, 1, 'g')), A('base/torso/neck/head/scar', Z, (-3.6, -5, -3.7, 1, 4, 0.8, 'f'), (-4, -8, -4, 8, 2, 8, 'h')),
   A('base/torso/armR/foreR/handR/blade', 'PartPose.offsetAndRotation(0, 2, -1, -0.4f, 0, 0)', (-0.8, -2, -1.5, 1.6, 4, 3, 'l'), (-0.4, 2, -1, 0.8, 22, 2, 's'), (-0.4, 20, -1.5, 0.8, 4, 1, 's'))], dict(l=D(c=(60, 50, 40), n=0.1), g=GOLD, f=SCAR, h=D(c=(70, 62, 50), n=0.08, d='fur'), s=STEEL), W=128, H0=64)
S('re4r_salazar', PS, 0, [A('base/stem1/stem2/head', 'PartPose.offset(0, -22, 0)', (-6, -12, -6, 12, 12, 12, 'fc'), (-7, -14, -7, 14, 3, 14, 'g'), (-5, -18, -1, 2, 5, 2, 'g'), (-1, -19, -1, 2, 6, 2, 'g'), (3, -18, -1, 2, 5, 2, 'g'), (-7, -6, -3, 2, 6, 6, 'p'), (5, -6, -3, 2, 6, 6, 'p')),
   A('base/stem1/stem2', 'PartPose.offset(0, -28, 0)', (-6, -24, -6, 12, 24, 12, 'p'), (-8, -12, -8, 16, 8, 16, 'p2')), A('base/stem1', 'PartPose.offset(0, -5, 0)', (-9, -28, -8, 18, 28, 16, 'p2'), (-1, -26, -8.3, 2, 26, 1, 'g'))],
   dict(fc=D(c=(176, 150, 130), n=0.08, d='face', hc=(40, 36, 40), ec=(200, 60, 40), mc=(110, 30, 30)), g=GOLD, p=PURP, p2=D(c=(60, 28, 70), n=0.1)),
   clear=['base/stem1/leaf0', 'base/stem1/leaf1', 'base/stem1/leaf2', 'base/stem1/leaf3', 'base/stem1/stem2/head/petalT', 'base/stem1/stem2/head/petalB', 'base/stem1/stem2/head/petalL', 'base/stem1/stem2/head/petalR'], W=256, H0=256)
S('re4r_armadura', PA, 0, [A(HD + '/helm', Z, (-4.5, -7, -4.5, 9, 8, 9, 'p'), (-1, -10, -4, 2, 3, 8, 'r'), (-3, -4, -4.6, 6, 1, 1, 'd')), A(CH + '/plate', Z, (-5, -6, -3, 10, 6, 1, 's'), (-5, -6, 2, 10, 6, 1, 's'), (-8, -7, -3, 3, 3, 6, 'p'), (5, -7, -3, 3, 3, 6, 'p')),
   A('body/waist/chest/leftArm/leftForearm/shield', Z, (2, -6, -5, 2, 14, 10, 's'), (4, -1, -2, 1, 4, 4, 'r')), A('body/waist/chest/rightArm/rightForearm/rightHand/halberd', 'PartPose.offsetAndRotation(0, 1, -1, -0.9f, 0, 0)', (-0.5, -8, -0.5, 1, 28, 1, 'w'), (-0.5, -14, -3, 1, 6, 6, 's'))], dict(p=PLATE, s=STEEL, d=DARK, r=D(c=(150, 30, 30), n=0.08), w=WOOD), W=128, H0=64)
S('re4r_iron_maiden', PIM, 0, [A(CH + '/cage', Z, (-6.5, -9, -4.5, 13, 11, 9, 's')) ] + [A(CH + '/sp%d' % i, Z, (x, y, z, 1, 1, 3, 'r')) for i, (x, y, z) in enumerate([(-5, -7, -7.5), (-1, -7, -7.5), (3, -7, -7.5), (-3, -3, -7.5), (1, -3, -7.5), (-5, -7, 4.5), (3, -7, 4.5), (-1, -3, 4.5)])] +
   [A(HD + '/helm', Z, (-4.5, -7, -4.5, 9, 7, 9, 's'), (-3, -4, -4.6, 6, 1, 1, 'd')), A(CH + '/drip', Z, (-6.6, -2, -4.6, 13.2, 3, 9.2, 'bl'))], dict(s=D(c=(150, 150, 158), n=0.08), r=D(c=(120, 34, 30), n=0.1), d=DARK, bl=BLOOD), W=128, H0=64)


MGUN = D(c=(40, 42, 48), n=0.06); MGUN2 = D(c=(90, 94, 102), n=0.06); TANK = D(c=(150, 60, 40), n=0.08); FLAME = D(c=(255, 170, 40), n=0.04)
S('re2r_nemesis', PN, 0, [A('base/torso/muscle', Z, (-5.5, -11, -3, 11, 7, 6, 'm')), A('base/torso/neck/head/jaw', Z, (-3.7, -3, -4, 7.4, 3, 1.5, 'mo'), (-3.7, -3.2, -4.6, 7.4, 1, 1, 'b')),
   A('base/torso/armR/foreR/handR/mgun', 'PartPose.offsetAndRotation(0, 2, -1, -1.5f, 0, 0)', (-1.2, -1.5, -9, 2.4, 3, 10, 'g'), (-0.8, 1.5, -3, 1.6, 4, 2, 'g'), (-0.5, -0.5, -14, 1, 1, 5, 'g2'), (-1.4, -1, -1, 2.8, 2, 3, 'g2'))],
   dict(m=MUS, mo=MOUTH, b=RIB, g=MGUN, g2=MGUN2), W=128, H0=64)
spec_ = vg.SPECS['re3r_nemesis']
spec_['sty'].update(dict(tk=TANK, mt=MGUN2, fl=FLAME))
spec_['parts'] += [A('base/torso/armR/foreR/handR/flamer', 'PartPose.offsetAndRotation(0, 2, -1, -1.5f, 0, 0)', (-1, -1, -11, 2, 2, 11, 'mt'), (-1.5, -1.5, -2, 3, 3, 4, 'mt'), (-0.7, -0.7, -13, 1.4, 1.4, 2, 'fl')), A('base/torso/tank', Z, (-3, -10, 2.6, 6, 10, 4, 'tk'), (-1, -11, 3.2, 2, 2, 2, 'mt'))]

# ---- driver
def parent_attrs(f):
    src = open(J + 'entity/%s/%sEntity.java' % (f['par']['pk'], f['par']['C'])).read()
    def g(k, dflt):
        m = re.search(r'Attributes\.%s, ([0-9.]+)' % k, src); return float(m.group(1)) if m else dflt
    hp, sp, dm, fr = g('MAX_HEALTH', 40), g('MOVEMENT_SPEED', 0.25), g('ATTACK_DAMAGE', 6), g('FOLLOW_RANGE', 32)
    a = '.add(Attributes.MAX_HEALTH, %.1f).add(Attributes.MOVEMENT_SPEED, %.3f).add(Attributes.ATTACK_DAMAGE, %.1f).add(Attributes.FOLLOW_RANGE, %.0f)' % (hp * f['hpx'], sp * f['spx'], dm * f['dmx'], fr)
    for k in ('KNOCKBACK_RESISTANCE', 'ARMOR', 'FLYING_SPEED'):
        m = re.search(r'Attributes\.%s, ([0-9.]+)' % k, src)
        if m: a += '.add(Attributes.%s, %s)' % (k, m.group(1))
    return a

def build(f):
    par = f['par']; pk = f['id'].replace('_', '')
    famgen(dict(id=f['id'], C=f['C'], pk=pk, pent='%s.%sEntity' % (par['pk'], par['C']), pe=par['C'] + 'Entity', pmod='%s.%sModel' % (par['pk'], par['C']), pm=par['C'] + 'Model',
        ret=par['ret'], retimp='%s.%s' % (par['rk'], par['ret']), attrs=parent_attrs(f), scale=f['scale'], shadow=f['shadow'], xp=60, doc=f['doc'],
        imports='import com.reevil.entity.AttackMove;\n', extra=rm.entity_extra(f['id']) if f['id'] in rm.MOV else ''))
    subprocess.run([sys.executable, TOOLS + '/addentity.py', f['id'], f['C'], str(f['w']), str(f['h']), f['e1'], f['e2'], f['en'], f['ja']], check=True, capture_output=True)
    # textures: tinted copy of the parent's files
    pid = par['tex']
    for tf in TEXFILES[pid]:
        suffix = tf[len(pid):]
        im = Image.open(OUT + '/' + tf + '.png').convert('RGBA')
        a = im.split()[3]; rgb = ImageEnhance.Brightness(ImageEnhance.Color(im.convert('RGB')).enhance(0.8)).enhance(0.86)
        rgb.putalpha(a); rgb.save(OUT + '/' + f['id'] + suffix + '.png')

if __name__ == '__main__':
    rm.write_clips()
    ids = sys.argv[1:] or [f['id'] for f in R]
    for f in R:
        if f['id'] in ids:
            build(f)
            if f['id'] in SPECS: vgen(SPECS[f['id']])
