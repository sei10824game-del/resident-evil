"""Cerberus (zombie dobermann) texture, 64x64. UV offsets MUST match CerberusModel texOffs.
Run: python3 tools/tex_cerberus.py  -> cerberus.png + cerberus_eyes.png (emissive layer)."""
from texlib import Tex, shade

# name: (u, v, w, h, d)  -- identical numbers to CerberusModel.createBodyLayer()
P = {
    'chest': (0, 0, 7, 7, 10), 'hips': (34, 0, 6, 6, 9), 'head': (0, 17, 6, 6, 5), 'neck': (22, 17, 4, 5, 5),
    'fUpper': (40, 17, 2, 6, 3), 'hThigh': (50, 17, 2, 6, 3), 'spine': (0, 28, 1, 1, 8), 'snout': (18, 28, 3, 3, 5),
    'jaw': (34, 28, 3, 2, 5), 'hShin': (50, 28, 2, 4, 2), 'ear': (58, 28, 2, 4, 1), 'fLower': (0, 37, 2, 3, 2),
    'tail1': (8, 37, 2, 2, 3), 'paw': (18, 37, 2, 1, 3), 'hHock': (28, 37, 2, 2, 2), 'tail2': (36, 37, 2, 2, 2),
    'tail3': (44, 37, 1, 1, 2), 'fang': (50, 37, 1, 1, 1),
}

FUR = (38, 32, 30)
TAN = (122, 72, 38)
SKIN = (128, 98, 92)
MUSCLE = (122, 30, 28)
MUSCLE_HI = (165, 52, 44)
BONE = (214, 202, 172)
BLOOD = (96, 8, 8)
BLOOD_WET = (150, 14, 14)
TOOTH = (226, 214, 176)

t = Tex(64, 64, seed=7)
F = lambda n: t.faces(*P[n])


def fur(x, y, w, h, base=FUR, streak=True):
    """Short-hair fur: vertical streak noise with darker roots."""
    for i in range(w):
        col = t.rng.uniform(-0.12, 0.12)
        for j in range(h):
            f = 1 + col + t.rng.uniform(-0.07, 0.07) - (0.12 * j / max(1, h - 1) if streak else 0)
            t.px_set(x + i, y + j, shade(base, f))


def patch(x, y, w, h, c, n, r=2):
    """Irregular blob patch (missing fur / exposed flesh)."""
    for _ in range(n):
        cx, cy = x + t.rng.randrange(w), y + t.rng.randrange(h)
        rr = t.rng.randint(1, r)
        for i in range(-rr, rr + 1):
            for j in range(-rr, rr + 1):
                if i * i + j * j <= rr * rr + t.rng.randint(0, 1) and x <= cx + i < x + w and y <= cy + j < y + h:
                    t.px_set(cx + i, cy + j, shade(c, t.rng.uniform(0.85, 1.1)))


def muscle(x, y, w, h):
    """Striated exposed muscle."""
    for j in range(h):
        for i in range(w):
            c = MUSCLE_HI if (i + j // 2) % 3 == 0 else MUSCLE
            t.px_set(x + i, y + j, shade(c, t.rng.uniform(0.8, 1.1)))


def ribs(x, y, w, h, gap=2):
    """Exposed rib cage: flesh-dark background with pale curved bones."""
    muscle(x, y, w, h)
    for i in range(0, w, gap):
        for j in range(h):
            bend = 1 if j > h * 0.6 else 0
            t.px_set(x + i + bend, y + j, shade(BONE, t.rng.uniform(0.8, 1.05)))
            if j < h - 1 and t.rng.random() < 0.3:
                t.px_set(x + i + bend + 1, y + j, shade(BONE, 0.7))
    for _ in range(4):
        t.px_set(x + t.rng.randrange(w), y + t.rng.randrange(h), BLOOD_WET + (255,))


def fill_box(n, base=FUR, belly=TAN):
    for face, (x, y, w, h) in F(n).items():
        if face == 'bottom':
            fur(x, y, w, h, shade(belly, 0.8), False)
        elif face == 'top':
            fur(x, y, w, h, shade(base, 1.1), False)
        else:
            fur(x, y, w, h, base)


# ---- torso
fill_box('chest')
f = F('chest')
for side in ('right', 'left'):
    x, y, w, h = f[side]  # 10 x 7 side faces
    ribs(x + 2, y + 1, 6, 5)
    patch(x, y, w, h, SKIN, 3, 1)
    for i in range(w):  # tan belly line
        t.px_set(x + i, y + h - 1, shade(TAN, t.rng.uniform(0.7, 0.95)))
x, y, w, h = f['front']  # brisket: tan chest marking + gore
fur(x, y, w, h, TAN)
patch(x + 1, y + 3, 5, 4, MUSCLE, 3, 1)
t.blots(x, y + 4, w, 3, BLOOD + (255,), 6, 1)
x, y, w, h = f['bottom']
muscle(x + 1, y + 2, 5, 6)
t.blots(x, y, w, h, BLOOD_WET + (255,), 8, 1)

fill_box('hips')
f = F('hips')
for side in ('right', 'left'):
    x, y, w, h = f[side]  # 9 x 6
    patch(x + 1, y + 1, 7, 4, SKIN, 3, 2)
    muscle(x + 4, y + 1, 4, 3)
    t.px_set(x + 4, y + 1, BONE + (255,))  # hip bone poking out
    t.px_set(x + 5, y + 1, BONE + (255,))
x, y, w, h = f['top']
patch(x, y, w, h, SKIN, 4, 1)
x, y, w, h = f['back']
fur(x, y, w, h, FUR)
t.blots(x + 1, y + 2, 4, 3, MUSCLE + (255,), 4, 1)

# spine ridge: vertebra knobs
for face, (x, y, w, h) in F('spine').items():
    for i in range(w):
        for j in range(h):
            k = (i + j) % 2
            t.px_set(x + i, y + j, shade(BONE if k == 0 else MUSCLE, t.rng.uniform(0.8, 1.05)))

# ---- neck / head
fill_box('neck')
f = F('neck')
for side in ('right', 'left'):
    x, y, w, h = f[side]
    muscle(x + 1, y + 2, 3, 2)
    patch(x, y, w, h, SKIN, 2, 1)
x, y, w, h = f['bottom']
fur(x, y, w, h, TAN)
t.blots(x, y, w, h, BLOOD_WET + (255,), 5, 1)

fill_box('head')
f = F('head')
x, y, w, h = f['front']  # face around muzzle: tan brows, red eyes
fur(x, y, w, h, FUR)
for ex in (0, 4):
    t.px_set(x + ex, y + 1, (70, 0, 0, 255))
    t.px_set(x + ex + 1, y + 1, (230, 30, 20, 255))
    t.px_set(x + ex + (1 if ex == 0 else 0), y + 0, shade(TAN, 1.0))  # tan brow spot
    t.px_set(x + ex, y + 2, shade(TAN, 0.9))
t.px_set(x + 1, y + 2, (120, 10, 10, 255))  # blood tear streaks
t.px_set(x + 4, y + 3, (120, 10, 10, 255))
for side in ('right', 'left'):
    x, y, w, h = f[side]
    t.px_set(x + 1, y + 3, shade(TAN, 0.9))
    patch(x + 2, y + 1, 3, 4, SKIN, 2, 1)
    t.px_set(x + 3, y + 2, BONE + (255,))  # cheekbone through rotten skin
x, y, w, h = f['top']
patch(x, y, w, h, SKIN, 2, 1)
t.px_set(x + 2, y + 2, MUSCLE_HI + (255,))

# snout: tan muzzle, black nose, bloody lips; bottom face = upper gums/teeth
for face, (x, y, w, h) in F('snout').items():
    fur(x, y, w, h, TAN if face in ('right', 'left', 'bottom') else shade(FUR, 1.05), False)
f = F('snout')
x, y, w, h = f['front']
t.rect(x, y, w, 1, (18, 14, 14), 0.1)
t.px_set(x + 1, y + 1, (10, 8, 8, 255))
for i in range(w):
    t.px_set(x + i, y + h - 1, BLOOD_WET + (255,))
for side in ('right', 'left'):
    x, y, w, h = f[side]
    for i in range(w):  # lip line + teeth
        t.px_set(x + i, y + h - 1, TOOTH + (255,) if i % 2 == 0 else BLOOD + (255,))
    t.blots(x, y, w, h - 1, BLOOD + (255,), 3, 1)
x, y, w, h = f['bottom']  # roof of mouth
t.rect(x, y, w, h, (110, 24, 30), 0.15)
for j in range(h):
    t.px_set(x, y + j, TOOTH + (255,))
    t.px_set(x + w - 1, y + j, TOOTH + (255,))

# lower jaw: top face = inside of mouth (tongue + teeth), front = bloody chin
for face, (x, y, w, h) in F('jaw').items():
    fur(x, y, w, h, TAN, False)
f = F('jaw')
x, y, w, h = f['top']
t.rect(x, y, w, h, (140, 40, 48), 0.15)
for j in range(h):
    t.px_set(x, y + j, TOOTH + (255,))
    t.px_set(x + w - 1, y + j, TOOTH + (255,))
t.px_set(x + 1, y + h - 1, BLOOD_WET + (255,))
for face in ('front', 'right', 'left', 'bottom'):
    x, y, w, h = f[face]
    t.blots(x, y, w, h, BLOOD_WET + (255,), 3, 1)
    t.px_set(x, y, TOOTH + (255,))

for face, (x, y, w, h) in F('fang').items():
    t.rect(x, y, w, h, TOOTH, 0.05)

# ears: cropped dobermann ears, torn
fill_box('ear')
x, y, w, h = F('ear')['front']
t.rect(x, y + 1, w, h - 1, SKIN, 0.1)
t.px_set(x + 1, y + 2, MUSCLE + (255,))

# ---- legs: black upper, tan lower, rotten patches
for n in ('fUpper', 'hThigh'):
    fill_box(n)
    f = F(n)
    for side in ('right', 'left'):
        x, y, w, h = f[side]
        patch(x, y + 1, w, h - 2, SKIN, 1, 1)
    x, y, w, h = f['front' if n == 'fUpper' else 'back']
    muscle(x, y + 2, w, 3)
x, y, w, h = F('hThigh')['left']
muscle(x, y + 1, 3, 4)  # stripped thigh
t.px_set(x + 1, y + 2, BONE + (255,))
for n in ('fLower', 'hShin', 'hHock'):
    for face, (x, y, w, h) in F(n).items():
        fur(x, y, w, h, TAN if face != 'top' else FUR)
x, y, w, h = F('hShin')['right']
t.px_set(x + 1, y + 1, BONE + (255,))
t.px_set(x + 1, y + 2, BONE + (255,))
for face, (x, y, w, h) in F('paw').items():
    fur(x, y, w, h, shade(TAN, 0.75), False)
x, y, w, h = F('paw')['front']
for i in range(w):
    t.px_set(x + i, y, (30, 26, 22, 255))  # claws
t.blots(*F('paw')['top'], BLOOD + (255,), 2, 1)

# ---- tail (docked stub + exposed bone at tip)
for n in ('tail1', 'tail2'):
    fill_box(n)
    patch(*F(n)['top'], SKIN, 1, 1)
for face, (x, y, w, h) in F('tail3').items():
    t.rect(x, y, w, h, MUSCLE, 0.15)
t.px_set(*F('tail3')['back'][:2], BONE + (255,))

t.save('cerberus')

# ---- emissive eye layer (only the eye pixels, glowing)
e = Tex(64, 64, seed=1)
x, y, w, h = Tex(64, 64).faces(*P['head'])['front']
for ex in (0, 4):
    e.px_set(x + ex, y + 1, (150, 10, 0, 255))
    e.px_set(x + ex + 1, y + 1, (255, 60, 30, 255))
e.save('cerberus_eyes')
print('ok')
