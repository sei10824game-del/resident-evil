"""RE1 zombie textures (128x64) -> zombie_civilian.png, zombie_labcoat.png, zombie_stars.png.
UV map must match client/zombie/ZombieModel.createBodyLayer():
  head (0,0) 8x6x8   jaw (32,0) 7x2x6   neck (58,0) 3x2x3
  chest (0,14) 9x6x5   waist (28,14) 8x6x4   flap (52,14) 4x5x0
  right arm row v=26 / left arm row v=34: arm (0) 3x5x3, forearm (12) 3x5x3, hand (24) 3x2x3, fingers (36) 3x2x1
  thighs/shins 4x6x4: rThigh (0,42) rShin (16,42) lThigh (32,42) lShin (48,42)
"""
import os, sys
sys.path.insert(0, os.path.dirname(__file__))
from texlib import Tex, shade

SKIN = (132, 146, 124)
SKIN_D = (96, 106, 90)
BRUISE = (92, 78, 98)
VEIN = (82, 92, 104)
BLOOD = (128, 12, 14)
BLOOD_D = (72, 10, 12)
FLESH = (168, 52, 48)
BONE = (222, 214, 188)
HAIR = (38, 30, 24)
MOUTH = (70, 14, 18)
TEETH = (206, 196, 150)


def faces(t, u, v, w, h, d):
    return t.faces(u, v, w, h, d)


def skin(t, x, y, w, h, base=SKIN):
    t.rect(x, y, w, h, base, noise=0.10, grad=0.12)
    t.blots(x, y, w, h, SKIN_D, max(1, w * h // 6), 2)
    t.blots(x, y, w, h, BRUISE, max(1, w * h // 18), 2)
    for _ in range(max(1, w * h // 20)):          # thin dark veins
        vx, vy = x + t.rng.randrange(w), y + t.rng.randrange(h)
        for i in range(t.rng.randint(2, 4)):
            t.px_set(vx + i * t.rng.choice((-1, 0, 1)) // 1, vy + i, VEIN)


def wound(t, x, y, r=1, bone=False):
    """Open wound: ragged red rim, dark core, optional bone showing through."""
    for j in range(-r - 1, r + 2):
        for i in range(-r - 1, r + 2):
            dd = i * i + j * j
            if dd <= r * r:
                t.px_set(x + i, y + j, BLOOD_D if (i + j) % 2 else MOUTH)
            elif dd <= (r + 1) * (r + 1) and t.rng.random() < 0.8:
                t.px_set(x + i, y + j, FLESH)
    if bone:
        t.px_set(x, y, BONE)
        if r > 1: t.px_set(x + 1, y, shade(BONE, 0.85))


def drip(t, x, y, n, c=BLOOD):
    for k in range(n):
        t.px_set(x, y + k, shade(c, 1.0 - 0.08 * k))


def smear(t, x, y, w, h, n):
    for _ in range(n):
        bx, by = x + t.rng.randrange(w), y + t.rng.randrange(h)
        drip(t, bx, by, t.rng.randint(1, 3), t.rng.choice((BLOOD, BLOOD_D)))


def cloth(t, x, y, w, h, base, plaid=None, creases=True):
    t.rect(x, y, w, h, base, noise=0.07, grad=0.1)
    if plaid:
        for i in range(x, x + w):
            if (i - x) % 3 == 0:
                for j in range(y, y + h): t.px_set(i, j, shade(plaid, t.rng.uniform(0.9, 1.05)))
        for j in range(y, y + h):
            if (j - y) % 3 == 1:
                for i in range(x, x + w): t.px_set(i, j, shade(plaid, t.rng.uniform(0.8, 0.95)))
    if creases:
        t.blots(x, y, w, h, shade(base, 0.75), max(1, w * h // 8), 1)
    t.blots(x, y, w, h, (70, 60, 44), max(1, w * h // 14), 2)   # grime


def tear(t, x, y, w, h, n, under=SKIN):
    """rips in clothes showing skin / wounds underneath"""
    for _ in range(n):
        bx, by = x + t.rng.randrange(w), y + t.rng.randrange(h)
        ln = t.rng.randint(1, 3)
        for k in range(ln):
            t.px_set(bx + k, by + (k // 2), shade(under, t.rng.uniform(0.85, 1.05)))
        if t.rng.random() < 0.5: t.px_set(bx, by, FLESH)


# ---------------------------------------------------------------- head

def paint_head(t):
    f = faces(t, 0, 0, 8, 6, 8)
    for name in ('right', 'left', 'back'):
        x, y, w, h = f[name]
        skin(t, x, y, w, h)
        for i in range(w):                       # patchy hair on the upper rows
            for j in range(t.rng.randint(1, 3) if name != 'back' else t.rng.randint(2, 5)):
                t.px_set(x + i, y + j, shade(HAIR, t.rng.uniform(0.8, 1.3)))
        if name != 'back':
            t.px_set(x + w // 2, y + 3, SKIN_D); t.px_set(x + w // 2, y + 4, shade(SKIN_D, 0.8))  # ear
    x, y, w, h = f['top']
    t.rect(x, y, w, h, HAIR, noise=0.25)
    t.blots(x, y, w, h, SKIN_D, 5, 2)            # balding scalp
    wound(t, x + 5, y + 3, 1)                    # scalp gash
    x, y, w, h = f['bottom']
    t.rect(x, y, w, h, MOUTH, noise=0.15)        # palate, seen when the jaw drops
    for i in range(1, 7): t.px_set(x + i, y + 7, TEETH)
    # face 8x6
    x, y, w, h = f['front']
    skin(t, x, y, w, h)
    for i in range(w): t.px_set(x + i, y, shade(HAIR, t.rng.uniform(0.8, 1.2)))   # hairline
    for i in (1, 2, 5, 6): t.px_set(x + i, y + 1, shade(SKIN_D, 0.75))             # brow
    for ex in (1, 5):                            # sunken sockets, milky dead eyes
        t.px_set(x + ex, y + 2, (34, 22, 22)); t.px_set(x + ex + 1, y + 2, (190, 196, 168))
        t.px_set(x + ex, y + 3, shade(BRUISE, 0.7)); t.px_set(x + ex + 1, y + 3, shade(BRUISE, 0.8))
    t.px_set(x + 3, y + 3, SKIN_D); t.px_set(x + 4, y + 3, SKIN_D)                   # nose
    t.px_set(x + 3, y + 4, (40, 26, 24)); t.px_set(x + 4, y + 4, (40, 26, 24))       # nostrils
    t.px_set(x + 6, y + 4, BONE); t.px_set(x + 7, y + 4, FLESH); t.px_set(x + 7, y + 3, FLESH)  # torn cheek, cheekbone
    for i in range(1, 7):                        # upper teeth
        t.px_set(x + i, y + 5, TEETH if i % 3 else (60, 40, 30))
    drip(t, x + 2, y + 3, 2, BLOOD_D)            # blood tear


def paint_jaw(t):
    f = faces(t, 32, 0, 7, 2, 6)
    for name in ('right', 'left', 'bottom', 'back'):
        x, y, w, h = f[name]; skin(t, x, y, w, h, SKIN_D if name == 'bottom' else SKIN)
    x, y, w, h = f['top']                        # mouth floor + tongue
    t.rect(x, y, w, h, MOUTH, noise=0.15)
    t.rect(x + 2, y + 1, 3, 4, (120, 50, 60), noise=0.1)
    for i in range(w): t.px_set(x + i, y, TEETH if i % 2 else shade(TEETH, 0.8))
    x, y, w, h = f['front']
    for i in range(w): t.px_set(x + i, y, TEETH if i not in (0, 3, 6) else (50, 30, 26))
    for i in range(w): t.px_set(x + i, y + 1, shade(SKIN, t.rng.uniform(0.85, 1.0)))
    for i in (1, 2, 4): drip(t, x + i, y + 1, 1, BLOOD)
    x, y, w, h = f['bottom']; smear(t, x, y, w, h, 6)


def paint_neck(t):
    f = faces(t, 58, 0, 3, 2, 3)
    for name, (x, y, w, h) in f.items(): skin(t, x, y, w, h, SKIN_D)
    x, y, w, h = f['right']; wound(t, x + 1, y + 1, 0)   # old bite mark
    x, y, w, h = f['front']; drip(t, x + 1, y, 2)


# ---------------------------------------------------------------- body by variant

VARIANT = {
    # name: (shirt, plaid, sleeves upper?, sleeves lower?, trousers, shoes, belt)
    'civilian': dict(shirt=(84, 98, 120), plaid=(140, 58, 48), up=True, low=False, pants=(58, 66, 84), shoes=(44, 32, 24), belt=(40, 30, 22)),
    'labcoat': dict(shirt=(196, 198, 190), plaid=None, up=True, low=True, pants=(70, 70, 74), shoes=(26, 24, 24), belt=None),
    'stars': dict(shirt=(70, 78, 52), plaid=None, up=False, low=False, pants=(38, 40, 44), shoes=(22, 22, 22), belt=(30, 30, 28)),
}


def paint_torso(t, var):
    c = VARIANT[var]
    shirt = c['shirt']
    # chest
    f = faces(t, 0, 14, 9, 6, 5)
    for name, (x, y, w, h) in f.items():
        if var == 'stars' and name == 'top':
            skin(t, x, y, w, h); continue             # bare shoulders (sleeveless vest)
        cloth(t, x, y, w, h, shirt, c['plaid'])
        tear(t, x, y, w, h, 2 if name != 'front' else 3)
        smear(t, x, y, w, h, 3)
    x, y, w, h = f['front']
    if var == 'labcoat':                              # open coat over grey shirt and tie, lapels
        t.rect(x + 3, y, 3, h, (110, 112, 118), noise=0.06)
        for j in range(h): t.px_set(x + 4, y + j, (90, 30, 34))
        t.px_set(x + 2, y + 1, shade(shirt, 0.7)); t.px_set(x + 6, y + 1, shade(shirt, 0.7))
        t.px_set(x + 1, y + 2, (40, 60, 120))         # pen in pocket
        t.blots(x, y + 2, w, 4, BLOOD, 5, 2)          # blood soaked front
    elif var == 'stars':                              # tactical vest, pouches, S.T.A.R.S. badge
        for i in range(w):
            t.px_set(x + i, y + 3, shade(shirt, 0.6))
        t.rect(x + 1, y + 4, 2, 2, shade(shirt, 0.8), noise=0.05); t.rect(x + 6, y + 4, 2, 2, shade(shirt, 0.8), noise=0.05)
        t.px_set(x + 6, y + 1, (200, 170, 40)); t.px_set(x + 7, y + 1, (180, 30, 30))   # badge
        t.px_set(x + 6, y + 2, (40, 60, 140)); t.px_set(x + 7, y + 2, (200, 170, 40))
    else:                                             # buttons, open collar
        for j in range(1, h, 2): t.px_set(x + 4, y + j, (200, 196, 180))
        t.px_set(x + 4, y, SKIN); t.px_set(x + 3, y, SKIN); t.px_set(x + 5, y, SKIN)
    # ripped-open rib cage on the front-left
    wound(t, x + 6 if var != 'labcoat' else x + 1, y + 3, 1, bone=True)
    for k in range(3): t.px_set((x + 6 if var != 'labcoat' else x + 1) - 1 + k, y + 2 + (k % 2) * 2, BONE)
    # back: big blood stain & bite
    x, y, w, h = f['back']
    t.blots(x + 2, y + 1, 5, 4, BLOOD_D, 6, 2); wound(t, x + 3, y + 2, 1)
    # waist
    f = faces(t, 28, 14, 8, 6, 4)
    for name, (x, y, w, h) in f.items():
        cloth(t, x, y, w, h, shirt, c['plaid'])
        tear(t, x, y, w, h, 2)
        smear(t, x, y, w, h, 2)
        if name not in ('top', 'bottom'):
            if c['belt']:
                for i in range(w): t.px_set(x + i, y + h - 1, c['belt'])
                if name == 'front': t.px_set(x + w // 2, y + h - 1, (150, 140, 90))   # buckle
            else:
                for j in range(h - 1, h):
                    for i in range(w): t.px_set(x + i, y + j, shade(shirt, 0.85))
    x, y, w, h = f['front']
    wound(t, x + 2, y + 2, 1)                         # gut wound
    drip(t, x + 2, y + 3, 3)
    # flap (torn shirt tail / coat skirt), ragged alpha edge
    for (x, y) in ((52, 14), (56, 14)):
        cloth(t, x, y, 4, 5, shirt, c['plaid'], creases=False)
        for i in range(4):
            cut = t.rng.randint(0, 2)
            for j in range(cut): t.px_set(x + i, y + 4 - j, (0, 0, 0, 0))
        t.px_set(x + 1, y + 2, BLOOD)


def paint_arm(t, var, v, side):
    c = VARIANT[var]
    shirt = c['shirt']
    # upper arm 3x5x3 @ (0,v)
    f = faces(t, 0, v, 3, 5, 3)
    for name, (x, y, w, h) in f.items():
        if c['up']:
            cloth(t, x, y, w, h, shirt, c['plaid'])
            tear(t, x, y, w, h, 1)
        else:
            skin(t, x, y, w, h)
    x, y, w, h = f['front' if side == 'r' else 'left']
    wound(t, x + 1, y + 2, 0 if c['up'] else 1, bone=not c['up'])
    # forearm 3x5x3 @ (12,v)
    f = faces(t, 12, v, 3, 5, 3)
    for name, (x, y, w, h) in f.items():
        if c['low']:
            cloth(t, x, y, w, h, shirt)
            if name not in ('top', 'bottom'):
                for i in range(w): t.px_set(x + i, y + h - 1, shade(shirt, 0.8))   # cuff
        else:
            skin(t, x, y, w, h)
            if name == 'top' and c['up']:
                t.rect(x, y, w, h, shade(shirt, 0.85), noise=0.05)                    # rolled sleeve edge
            elif name not in ('top', 'bottom') and c['up']:
                for i in range(w): t.px_set(x + i, y, shade(shirt, 0.85))
    x, y, w, h = f['back' if side == 'r' else 'front']
    if not c['low']:
        wound(t, x + 1, y + 2, 1, bone=True)          # bitten through to the ulna
        drip(t, x + 1, y + 3, 2)
    # hand 3x2x3 @ (24,v)
    f = faces(t, 24, v, 3, 2, 3)
    for name, (x, y, w, h) in f.items():
        skin(t, x, y, w, h, SKIN_D if name == 'bottom' else SKIN)
        smear(t, x, y, w, h, 1)
    # fingers 3x2x1 @ (36,v): knuckles, dark broken nails at the tips
    f = faces(t, 36, v, 3, 2, 1)
    for name, (x, y, w, h) in f.items():
        skin(t, x, y, w, h)
        if name in ('front', 'back'):
            for i in range(w): t.px_set(x + i, y + h - 1, (64, 50, 40) if i % 2 == 0 else BLOOD_D)
    x, y, w, h = f['bottom']
    t.rect(x, y, w, h, (60, 46, 38), noise=0.1)
    x, y, w, h = f['front']; t.px_set(x + 1, y, BLOOD)


def paint_leg(t, var, u, v, shin, side):
    c = VARIANT[var]
    f = faces(t, u, v, 4, 6, 4)
    for name, (x, y, w, h) in f.items():
        cloth(t, x, y, w, h, c['pants'], creases=True)
        tear(t, x, y, w, h, 2 if not shin else 1)
        smear(t, x, y, w, h, 2)
        if shin and name not in ('top', 'bottom'):
            boot = 3 if var == 'stars' else 2
            for j in range(h - boot, h):
                for i in range(w):
                    t.px_set(x + i, y + j, shade(c['shoes'], t.rng.uniform(0.85, 1.15)))
            if var == 'stars':
                for i in range(w): t.px_set(x + i, y + h - boot, shade(c['shoes'], 1.6))   # boot top
    if shin:
        x, y, w, h = f['bottom']
        t.rect(x, y, w, h, shade(c['shoes'], 0.6), noise=0.1)
    if not shin:
        x, y, w, h = f['front']
        if side == 'r':                               # the dragging leg: shredded thigh, exposed femur
            t.rect(x + 1, y + 2, 2, 3, FLESH, noise=0.15)
            wound(t, x + 1, y + 3, 1, bone=True)
            drip(t, x + 2, y + 4, 2)
        else:
            tear(t, x, y, w, h, 2)


def build(var, seed):
    t = Tex(128, 64, seed)
    paint_head(t)
    paint_jaw(t)
    paint_neck(t)
    paint_torso(t, var)
    paint_arm(t, var, 26, 'r')
    paint_arm(t, var, 34, 'l')
    paint_leg(t, var, 0, 42, False, 'r')
    paint_leg(t, var, 16, 42, True, 'r')
    paint_leg(t, var, 32, 42, False, 'l')
    paint_leg(t, var, 48, 42, True, 'l')
    return t.save('zombie_' + var)


if __name__ == '__main__':
    for i, var in enumerate(('civilian', 'labcoat', 'stars')):
        print(build(var, 11 + i))
