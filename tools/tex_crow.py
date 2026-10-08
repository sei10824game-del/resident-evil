"""Infected crow texture, 64x32. UV offsets MUST match CrowModel texOffs.
Transparent pixels are cut out (entityCutoutNoCull) -> ragged feather edges.
Run: python3 tools/tex_crow.py -> crow.png + crow_eyes.png (emissive layer)."""
from texlib import Tex, shade

P = {
    'body': (0, 0, 4, 4, 8), 'head': (24, 0, 3, 3, 3), 'wing1': (36, 0, 4, 1, 5), 'neck': (54, 0, 3, 3, 2),
    'wing2': (0, 12, 4, 1, 4), 'tail': (16, 12, 3, 1, 4), 'beak': (30, 12, 1, 1, 3), 'tailTip': (38, 12, 4, 1, 3),
    'shin': (52, 12, 1, 3, 1), 'foot': (0, 17, 2, 1, 3), 'jaw': (10, 17, 1, 1, 2), 'feather': (16, 17, 4, 1, 2),
    'thigh': (28, 17, 1, 2, 1),
}

BLACK = (20, 20, 26)
SHEEN = (44, 46, 78)
FLESH = (150, 62, 66)
RAW = (110, 18, 22)
BEAK = (36, 34, 36)
LEG = (52, 50, 52)

t = Tex(64, 32, seed=11)
F = lambda n: t.faces(*P[n])
CLEAR = (0, 0, 0, 0)


def plumage(x, y, w, h, base=BLACK, ruffle=0.15):
    """Glossy black feathers with blue-purple sheen streaks and feather-edge rows."""
    for j in range(h):
        for i in range(w):
            c = SHEEN if t.rng.random() < 0.12 or (i + 2 * j) % 7 == 0 else base
            f = 1 + t.rng.uniform(-ruffle, ruffle) - (0.25 if j % 3 == 2 else 0)
            t.px_set(x + i, y + j, shade(c, f))


def flesh(x, y, w, h, n, r=1):
    for _ in range(n):
        cx, cy = x + t.rng.randrange(w), y + t.rng.randrange(h)
        for i in range(-r, r + 1):
            for j in range(-r, r + 1):
                if abs(i) + abs(j) <= r and x <= cx + i < x + w and y <= cy + j < y + h:
                    t.px_set(cx + i, cy + j, shade(FLESH if (i or j) else RAW, t.rng.uniform(0.85, 1.1)))


def ragged(x, y, w, h, n):
    """Punch transparent notches into a feather face edge (torn feathers)."""
    for _ in range(n):
        i = t.rng.randrange(w)
        t.px_set(x + i, y + h - 1, CLEAR)
        if t.rng.random() < 0.4 and h > 2:
            t.px_set(x + i, y + h - 2, CLEAR)


def box(n, base=BLACK):
    for face, (x, y, w, h) in F(n).items():
        plumage(x, y, w, h, shade(base, 1.1) if face == 'top' else base)


# body: mangy, flesh showing on flanks and breast
box('body')
f = F('body')
for side in ('right', 'left'):
    x, y, w, h = f[side]  # 8 x 4
    flesh(x + 1, y + 1, 6, 3, 2)
    t.px_set(x + 4, y + 2, (210, 200, 180, 255))  # rib tip through skin
x, y, w, h = f['bottom']
flesh(x, y, w, h, 3)
t.blots(x, y, w, h, RAW + (255,), 3, 1)
x, y, w, h = f['top']
flesh(x, y + 2, w, 4, 1)

box('neck')
flesh(*F('neck')['left'], 1)
box('head')
f = F('head')
for side, ex in (('right', 0), ('left', 2)):  # side eyes (crows see sideways), bloodshot
    x, y, w, h = f[side]
    t.px_set(x + 1, y + 1, (255, 40, 30, 255))
    t.px_set(x + 1, y + 2, (90, 0, 0, 255))
x, y, w, h = f['front']
for i in range(w):
    t.px_set(x + i, y + 2, shade(BLACK, 0.6))
for i in (0, 2):  # forward-facing glints of the red eyes
    t.px_set(x + i, y + 0, (200, 20, 20, 255))
x, y, w, h = f['top']
flesh(x, y, w, h, 1)  # bald scalp patch

for n, col in (('beak', BEAK), ('jaw', BEAK)):
    for face, (x, y, w, h) in F(n).items():
        t.rect(x, y, w, h, col, 0.1)
x, y, w, h = F('beak')['front']
t.px_set(x, y, (120, 10, 10, 255))  # bloody tip
x, y, w, h = F('beak')['bottom']
t.rect(x, y, w, h, RAW, 0.1)
x, y, w, h = F('jaw')['top']
t.rect(x, y, w, h, (150, 50, 60), 0.1)  # mouth interior

# wings: top is outer plumage, bottom underside a bit greyer; ragged trailing edge
for n in ('wing1', 'wing2', 'feather'):
    for face, (x, y, w, h) in F(n).items():
        plumage(x, y, w, h, shade(BLACK, 1.25) if face == 'bottom' else BLACK)
for n in ('wing1', 'wing2'):
    for face in ('top', 'bottom'):
        x, y, w, h = F(n)[face]  # w x d (chord)
        ragged(x, y, w, h, 2)
x, y, w, h = F('wing1')['top']
flesh(x + 1, y + 1, 2, 2, 1)  # bare patch where feathers fell out
x, y, w, h = F('wing1')['bottom']
t.rect(x, y, 2, 2, FLESH, 0.15)
t.px_set(x + 1, y + 1, (210, 200, 180, 255))  # wing bone
for face in ('top', 'bottom'):
    x, y, w, h = F('feather')[face]
    t.px_set(x + w - 1, y, CLEAR)  # split feather tip
    t.px_set(x + w - 1, y + h - 1, (30, 30, 40, 255))

for n in ('tail', 'tailTip'):
    for face, (x, y, w, h) in F(n).items():
        plumage(x, y, w, h)
x, y, w, h = F('tailTip')['top']
ragged(x, y, w, h, 2)
x, y, w, h = F('tailTip')['bottom']
ragged(x, y, w, h, 2)

for n in ('thigh', 'shin', 'foot'):
    for face, (x, y, w, h) in F(n).items():
        for j in range(h):
            for i in range(w):
                t.px_set(x + i, y + j, shade(LEG, 1 + (0.15 if (i + j) % 2 else -0.1)))
x, y, w, h = F('thigh')['front']
plumage(x, y, w, h)
x, y, w, h = F('foot')['front']
for i in range(w):
    t.px_set(x + i, y, (15, 15, 15, 255))  # talons

t.save('crow')

e = Tex(64, 32)
f = e.faces(*P['head'])
for side in ('right', 'left'):
    x, y, w, h = f[side]
    e.px_set(x + 1, y + 1, (255, 60, 40, 255))
x, y, w, h = f['front']
for i in (0, 2):
    e.px_set(x + i, y, (220, 30, 20, 255))
e.save('crow_eyes')
print('ok')
