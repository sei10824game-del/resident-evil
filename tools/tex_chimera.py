"""Chimera texture (128x128): exposed-muscle bloated fly-human. UVs mirror ChimeraModel.createBodyLayer()."""
from texlib import Tex, shade

FLESH = (158, 72, 66)
MUSCLE = (176, 58, 52)
SINEW = (214, 170, 150)
WING = (206, 204, 192)
CHITIN = (72, 62, 52)
BLADE = (226, 222, 205)
EYE = (226, 210, 160)
VEIN = (150, 20, 30)


def muscle(t, x, y, w, h, base=MUSCLE, vertical=True):
    """Striated exposed muscle: fibres along the long axis with pale sinew bands and glistening highlights."""
    for j in range(h):
        for i in range(w):
            a = i if vertical else j
            f = 1 + t.rng.uniform(-0.08, 0.08)
            f *= 0.82 if a % 2 else 1.06
            if (j if vertical else i) % 6 == 5:
                c = shade(SINEW, 1 + t.rng.uniform(-0.05, 0.05))
            else:
                c = shade(base, f)
            if t.rng.random() < 0.04:
                c = shade(c, 1.35)  # wet highlight
            t.px_set(x + i, y + j, c)


def veins(t, x, y, w, h, n, c=VEIN):
    for _ in range(n):
        px, py = x + t.rng.randrange(w), y + t.rng.randrange(h)
        for _ in range(t.rng.randint(3, 7)):
            t.px_set(px, py, c)
            px += t.rng.choice((-1, 0, 1)); py += t.rng.choice((0, 1))
            px = min(max(px, x), x + w - 1); py = min(max(py, y), y + h - 1)


def flesh_box(t, u, v, w, h, d, vertical=True, nveins=3):
    f = t.faces(u, v, w, h, d)
    for name, (x, y, fw, fh) in f.items():
        if name in ('top', 'bottom'):
            muscle(t, x, y, fw, fh, shade(FLESH, 1.05 if name == 'top' else 0.8), not vertical)
        else:
            muscle(t, x, y, fw, fh, MUSCLE if name != 'back' else FLESH, vertical)
        veins(t, x, y, fw, fh, nveins)
    return f


def chitin_box(t, u, v, w, h, d, base=CHITIN, bands=3, tip=None):
    for name, (x, y, fw, fh) in t.faces(u, v, w, h, d).items():
        for j in range(fh):
            for i in range(fw):
                f = 1 + t.rng.uniform(-0.06, 0.06)
                if j % bands == bands - 1:
                    f *= 0.6  # segment joint
                elif j % bands == 0:
                    f *= 1.25
                c = shade(base, f)
                if tip and j >= fh - 2 and name not in ('top',):
                    c = tip
                t.px_set(x + i, y + j, c)


def main():
    t = Tex(128, 128, 7)

    # chest 9x8x8 : torn skin over muscle, ribcage on the sides, spine plates on top (back of a hunched body)
    f = flesh_box(t, 0, 0, 9, 8, 8, vertical=True, nveins=4)
    x, y, w, h = f['top']
    for j in range(h):
        t.px_set(x + w // 2, y + j, SINEW if j % 2 == 0 else shade(SINEW, 0.6))
    for side in ('left', 'right'):
        x, y, w, h = f[side]
        for j in range(1, h - 1, 2):
            t.line(x + 1, y + j, x + w - 2, y + j, shade(SINEW, 0.9))  # exposed ribs
    x, y, w, h = f['front']
    t.rect(x + 2, y + 1, w - 4, 3, shade(FLESH, 0.9), 0.1)  # sternum flesh
    for i in range(w):
        t.px_set(x + i, y + h - 1, shade(MUSCLE, 0.6))

    # abdomen 8x7x9 : bloated translucent sac with heavy veining and pustules
    f = t.faces(0, 18, 8, 7, 9)
    for name, (x, y, w, h) in f.items():
        for j in range(h):
            for i in range(w):
                cx, cy = (i - w / 2) / (w / 2), (j - h / 2) / (h / 2)
                g = 1.15 - 0.35 * (cx * cx + cy * cy)  # rounded bulge shading
                c = shade((190, 120, 110), g + t.rng.uniform(-0.05, 0.05))
                t.px_set(x + i, y + j, c)
        veins(t, x, y, w, h, 6, (120, 20, 40))
        veins(t, x, y, w, h, 3, (90, 60, 110))
        t.blots(x, y, w, h, (230, 210, 120), 3, 1)  # pustules
        for j in range(0, h, 3):
            t.line(x, y + j, x + w - 1, y + j, shade((190, 120, 110), 0.7))  # segment rings

    # neck 4x4x3
    flesh_box(t, 36, 0, 4, 4, 3, nveins=2)

    # head 6x6x5 : decayed human face, lipless mouth, mandible sockets; eyes are separate bulging boxes
    f = flesh_box(t, 52, 0, 6, 6, 5, nveins=2)
    x, y, w, h = f['front']
    t.rect(x, y, w, 2, shade(FLESH, 0.75), 0.1)  # brow
    t.rect(x + 1, y + 3, w - 2, 2, (40, 10, 10), 0.15)  # mouth hole
    for i in range(1, w - 1):
        t.px_set(x + i, y + 3, (220, 210, 170) if i % 2 else (180, 170, 140))  # teeth
    t.px_set(x + w // 2 - 1, y + 2, (60, 20, 20)); t.px_set(x + w // 2, y + 2, (60, 20, 20))  # nasal holes
    x, y, w, h = f['top']
    for i in range(w):
        for j in range(h):
            if (i + j) % 3 == 0:
                t.px_set(x + i, y + j, shade(SINEW, 0.8))  # exposed skull ridges

    # bulging compound eyes 3x3x3 : yellowed bulbs, red vein network, dark compound centre
    for v in (0, 8):
        for name, (x, y, w, h) in t.faces(76, v, 3, 3, 3).items():
            for j in range(h):
                for i in range(w):
                    c = shade(EYE, 1 + t.rng.uniform(-0.08, 0.08))
                    if (i + j) % 2 == 0 and name not in ('back',):
                        c = shade((90, 20, 20), 1 + t.rng.uniform(-0.1, 0.1))  # facets
                    if t.rng.random() < 0.25:
                        c = VEIN
                    t.px_set(x + i, y + j, c)
            if name in ('front', 'left', 'right'):
                t.px_set(x + 1, y + 1, (20, 5, 5))
                t.px_set(x, y, (255, 250, 230))  # glint

    # mandibles 1x1x3 : chitin with pale tips
    for v in (0, 6):
        chitin_box(t, 90, v, 1, 1, 3, base=(60, 40, 30), bands=2)
        x, y, w, h = t.faces(90, v, 1, 1, 3)['front']
        t.px_set(x, y, BLADE)

    # wings 7x1x11 : grey-white membrane, branching veins, torn edge
    for v in (18, 32):
        for name, (x, y, w, h) in t.faces(36, v, 7, 1, 11).items():
            for j in range(h):
                for i in range(w):
                    c = shade(WING, 1 + t.rng.uniform(-0.06, 0.06) - 0.12 * (j / max(1, h)))
                    t.px_set(x + i, y + j, c)
            if w >= 7 and h >= 7:
                for k in range(0, w, 2):  # radiating veins
                    t.line(x, y, x + k, y + h - 1, (130, 120, 105))
                for j in range(2, h, 4):
                    t.line(x, y + j, x + w - 1, y + j, (150, 140, 120))
                for j in range(h):  # tattered rim
                    if t.rng.random() < 0.35:
                        t.px_set(x + w - 1, y + j, (0, 0, 0, 0))
                t.blots(x, y, w, h, (190, 150, 140), 2, 1)

    for v in (36, 50):
        # upper arm 2x7x2, forearm 2x6x2 : flayed muscle
        flesh_box(t, 0, v, 2, 7, 2, nveins=1)
        flesh_box(t, 8, v, 2, 6, 2, nveins=1)
        # scythe blade 1x11x2 : bone-white, dark cutting edge on the front, serrations
        for name, (x, y, w, h) in t.faces(16, v, 1, 11, 2).items():
            for j in range(h):
                for i in range(w):
                    c = shade(BLADE, 1.05 - 0.25 * (j / max(1, h - 1)) + t.rng.uniform(-0.04, 0.04))
                    if name == 'front':
                        c = shade((60, 55, 50), 1.2 if j % 2 else 0.9)  # serrated edge
                    if j < 2 and name not in ('front',):
                        c = shade(MUSCLE, 0.9)  # flesh at the root
                    t.px_set(x + i, y + j, c)
        # hook claw 1x3x1
        chitin_box(t, 24, v, 1, 3, 1, base=(50, 40, 34), bands=3, tip=BLADE)

    # hind legs: insect-like segmented chitin with flesh at the hip
    for v in (18, 36):
        for name, (x, y, w, h) in t.faces(76, v, 2, 8, 2).items():
            muscle(t, x, y, w, h, MUSCLE)
            for j in range(h // 2, h):
                for i in range(w):
                    t.px_set(x + i, y + j, shade(CHITIN, 1.1 + t.rng.uniform(-0.06, 0.06)))
        chitin_box(t, 84, v, 2, 13, 2, bands=4)
        for name, (x, y, w, h) in t.faces(84, v, 2, 13, 2).items():
            if name in ('left', 'right', 'front', 'back'):
                for j in range(1, h, 3):
                    t.px_set(x, y + j, (30, 25, 20))  # spines
        chitin_box(t, 92, v, 1, 5, 1, base=(46, 40, 34), bands=2, tip=BLADE)

    print(t.save('chimera'))


if __name__ == '__main__':
    main()
