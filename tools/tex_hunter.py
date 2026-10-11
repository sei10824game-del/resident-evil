"""Hunter textures (128x128): hunter.png (green MA-121) and hunter_red.png (enraged REmake variant).
UVs mirror HunterModel.createBodyLayer() texOffs/addBox sizes."""
from texlib import Tex, shade


def scales(t, x, y, w, h, base, dark=0.72, hi=1.18, cw=3, ch=2):
    """Reptile scale pattern: staggered cells with dark seams and a highlight corner."""
    for j in range(h):
        for i in range(w):
            row = j // ch
            off = (row % 2) * (cw // 2 + 1)
            ci, cj = (i + off) % cw, j % ch
            f = 1 + t.rng.uniform(-0.07, 0.07)
            if cj == ch - 1 or ci == cw - 1:
                f *= dark
            elif ci == 0 and cj == 0:
                f *= hi
            t.px_set(x + i, y + j, shade(base, f))


def belly(t, x, y, w, h, c):
    """Ribbed underbelly plates: horizontal bands."""
    for j in range(h):
        for i in range(w):
            f = 1 + t.rng.uniform(-0.05, 0.05)
            if j % 2 == 1:
                f *= 0.8
            if i == 0 or i == w - 1:
                f *= 0.85
            t.px_set(x + i, y + j, shade(c, f))


def part(t, u, v, w, h, d, pal, belly_front=False, belly_bottom=True):
    fc = t.faces(u, v, w, h, d)
    for name, (x, y, fw, fh) in fc.items():
        base = pal['skin']
        if name == 'top':
            base = shade(pal['skin'], 1.08)
        if name == 'bottom':
            if belly_bottom:
                belly(t, x, y, fw, fh, pal['belly'])
                continue
            base = shade(pal['skin'], 0.75)
        if name == 'front' and belly_front:
            # skin edges, belly plates in the middle
            scales(t, x, y, fw, fh, base)
            bw = max(1, fw - 4)
            belly(t, x + (fw - bw) // 2, y, bw, fh, pal['belly'])
            continue
        scales(t, x, y, fw, fh, base)
    return fc


def claw(t, u, v, w, h, d, pal):
    """Long dark claw: black-brown, lighter keratin edge, pale sharp tip."""
    for name, (x, y, fw, fh) in t.faces(u, v, w, h, d).items():
        for j in range(fh):
            for i in range(fw):
                g = j / max(1, fh - 1)
                c = shade(pal['claw'], 1 + 0.6 * g + t.rng.uniform(-0.05, 0.05))
                if name in ('top',):
                    c = shade(pal['claw'], 0.9)
                if name == 'bottom' or (name != 'top' and j == fh - 1):
                    c = pal['tip']
                t.px_set(x + i, y + j, c)


def paint(pal, out_name, seed):
    t = Tex(128, 128, seed)
    sk, bl = pal['skin'], pal['belly']

    # hips 9x5x6
    f = part(t, 0, 0, 9, 5, 6, pal, belly_front=True)
    x, y, w, h = f['back']
    for i in range(w):  # bony sacral ridge
        if i in (3, 4, 5):
            t.px_set(x + i, y, pal['bone'])

    # chest 11x13x8 : belly plates in front, bony ridges on the back, muscle seams
    f = part(t, 0, 12, 11, 13, 8, pal, belly_front=True)
    x, y, w, h = f['front']
    for j in range(h):  # pectoral split
        t.px_set(x + w // 2, y + j, shade(bl, 0.6))
    for i in range(2, w - 2):
        t.px_set(x + i, y + 3, shade(sk, 0.7))
    x, y, w, h = f['back']
    for j in range(0, h, 2):  # vertebral knobs
        t.px_set(x + w // 2, y + j, pal['bone'])
        t.px_set(x + w // 2 - 1, y + j, shade(pal['bone'], 0.8))
    for j in range(h):  # shoulder blades shading
        for i in (1, w - 2):
            t.px_set(x + i, y + j, shade(sk, 0.78 + 0.04 * (j % 3)))
    for side in ('left', 'right'):  # rib lines
        x, y, w, h = f[side]
        for j in range(4, h - 2, 2):
            t.line(x + 1, y + j, x + w - 2, y + j + 1, shade(sk, 0.68))
    # wounds / old scars
    t.blots(*f['left'][:2], 6, 10, shade(bl, 0.55), 3, 1)

    # back ridge 2x11x2 : bone plates
    for name, (x, y, w, h) in t.faces(40, 0, 2, 11, 2).items():
        for j in range(h):
            for i in range(w):
                c = pal['bone'] if j % 3 != 2 else shade(pal['bone'], 0.6)
                t.px_set(x + i, y + j, shade(c, 1 + t.rng.uniform(-0.06, 0.06)))

    # neck 5x5x5
    part(t, 50, 0, 5, 5, 5, pal, belly_front=True)

    # skull 7x5x7 : brow ridge, red reptile eyes on the sides and front, ear holes
    f = part(t, 72, 0, 7, 5, 7, pal)
    x, y, w, h = f['front']
    for i in range(w):
        t.px_set(x + i, y, shade(sk, 0.6))  # brow
    for ex in (0, w - 2):
        t.px_set(x + ex, y + 1, pal['eye']); t.px_set(x + ex + 1, y + 1, pal['eye_hi'])
        t.px_set(x + ex, y + 2, shade(pal['eye'], 0.5)); t.px_set(x + ex + 1, y + 2, shade(pal['eye'], 0.6))
    x, y, w, h = f['right']   # front edge is at the right end of this face
    t.rect(x + w - 3, y + 1, 3, 2, pal['eye'], 0.05)
    t.px_set(x + w - 2, y + 1, pal['eye_hi']); t.px_set(x + w - 2, y + 2, (20, 0, 0))
    t.px_set(x + w - 4, y + 1, shade(sk, 0.55)); t.px_set(x + 1, y + 2, (30, 25, 20))
    x, y, w, h = f['left']    # front edge is at the left end
    t.rect(x, y + 1, 3, 2, pal['eye'], 0.05)
    t.px_set(x + 1, y + 1, pal['eye_hi']); t.px_set(x + 1, y + 2, (20, 0, 0))
    t.px_set(x + 3, y + 1, shade(sk, 0.55)); t.px_set(x + w - 2, y + 2, (30, 25, 20))
    x, y, w, h = f['top']
    for j in range(h):
        t.px_set(x + w // 2, y + j, shade(sk, 0.8))

    # snout 5x3x5 : nostrils, upper teeth row along bottom edge of front and sides
    f = part(t, 100, 0, 5, 3, 5, pal)
    x, y, w, h = f['front']
    t.px_set(x + 1, y, (25, 15, 12)); t.px_set(x + w - 2, y, (25, 15, 12))
    for i in range(w):
        t.px_set(x + i, y + h - 1, pal['teeth'] if i % 2 == 0 else shade(pal['teeth'], 0.75))
    for side in ('left', 'right'):
        x, y, w, h = f[side]
        for i in range(w):
            t.px_set(x + i, y + h - 1, pal['teeth'] if i % 2 == 0 else (60, 20, 20))
    x, y, w, h = f['bottom']  # palate
    t.rect(x, y, w, h, pal['mouth'], 0.1)

    # crest 2x1x6 : bone
    for name, (x, y, w, h) in t.faces(72, 14, 2, 1, 6).items():
        t.rect(x, y, w, h, pal['bone'], 0.1)

    # jaw 5x2x6 : lower teeth on top face edge, pink mouth inside, pale throat
    f = part(t, 100, 10, 5, 2, 6, pal)
    x, y, w, h = f['top']
    t.rect(x, y, w, h, pal['mouth'], 0.12)
    for j in range(h):
        t.px_set(x, y + j, pal['teeth'] if j % 2 else shade(pal['teeth'], 0.7))
        t.px_set(x + w - 1, y + j, pal['teeth'] if j % 2 else shade(pal['teeth'], 0.7))
    for i in range(w):
        t.px_set(x + i, y + h - 1, pal['teeth'] if i % 2 == 0 else shade(pal['teeth'], 0.7))
    t.rect(x + 1, y + 1, w - 2, h - 3, shade(pal['mouth'], 0.8), 0.1)  # tongue groove
    x, y, w, h = f['front']
    for i in range(w):
        t.px_set(x + i, y, pal['teeth'] if i % 2 else shade(pal['teeth'], 0.7))

    for v in (36, 50):
        # upper arm 3x8x3, forearm 3x8x3 (+ spur 1x3x2), hand 4x2x4
        f = part(t, 0, v, 3, 8, 3, pal)
        for side in ('left', 'right'):
            x, y, w, h = f[side]
            for j in range(1, h, 3):
                t.px_set(x + 1, y + j, shade(sk, 0.65))  # muscle striation
        f = part(t, 12, v, 3, 8, 3, pal, belly_front=False)
        x, y, w, h = f['front']
        belly(t, x, y + 2, w, h - 2, shade(bl, 0.9))  # pale inner forearm
        for name, (x, y, w, h) in t.faces(52, v, 1, 3, 2).items():
            t.rect(x, y, w, h, pal['bone'], 0.1)
        f = part(t, 24, v, 4, 2, 4, pal)
        x, y, w, h = f['bottom']
        t.rect(x, y, w, h, shade(bl, 0.8), 0.1)  # palm
        x, y, w, h = f['front']
        for i in range(0, w, 2):
            t.px_set(x + i, y, shade(sk, 1.25))  # knuckles
        # claws 1x5x1
        for i in range(3):
            claw(t, 40 + i * 4, v, 1, 5, 1, pal)

        # thigh 4x8x5 : heavy muscle, belly-coloured inner side
        f = part(t, 64, v, 4, 8, 5, pal, belly_front=True)
        x, y, w, h = f['left' if v == 50 else 'right']
        for j in range(h):
            for i in range(w):
                if (i + j) % 4 == 0:
                    t.px_set(x + i, y + j, shade(sk, 0.7))
        # shin 3x7x3, metatarsus 3x5x3 : scutes on front
        for (u, hh) in ((82, 7), (94, 5)):
            f = part(t, u, v, 3, hh, 3, pal)
            x, y, w, h = f['front']
            for j in range(0, h, 2):
                t.line(x, y + j, x + w - 1, y + j, shade(sk, 1.2))
                t.line(x, y + j + 1, x + w - 1, y + j + 1, shade(sk, 0.7))
        # toes 4x1x5 : 3 toes with dark talons at the front edge
        f = part(t, 106, v, 4, 1, 5, pal)
        x, y, w, h = f['top']
        for j in range(h):
            for i in range(w):
                c = shade(sk, 0.95) if i in (0, 2) else shade(sk, 0.65)
                t.px_set(x + i, y + j, c)
        for i in (0, 2, 3):
            t.px_set(x + i, y + h - 1, pal['claw'])
        x, y, w, h = f['front']
        for i in (0, 2, 3):
            t.px_set(x + i, y, pal['tip'])

    # tail 3x3x8 and 3 tail2 2x2x8 : ringed, ridge on top
    for (u, w, h, d) in ((0, 3, 3, 8), (24, 2, 2, 8)):
        f = part(t, u, 66, w, h, d, pal)
        x, y, fw, fh = f['top']
        for j in range(fh):
            t.px_set(x + fw // 2, y + j, pal['bone'] if j % 2 == 0 else shade(sk, 0.7))
        for side in ('left', 'right'):
            x, y, fw, fh = f[side]
            for i in range(0, fw, 2):
                t.line(x + i, y, x + i, y + fh - 1, shade(sk, 0.75))

    return t.save(out_name)


GREEN = dict(skin=(70, 104, 58), belly=(196, 96, 52), bone=(205, 192, 150), eye=(220, 20, 18), eye_hi=(255, 140, 90),
             claw=(34, 28, 26), tip=(200, 196, 180), teeth=(232, 226, 196), mouth=(150, 50, 60))
RED = dict(skin=(128, 38, 34), belly=(214, 150, 70), bone=(216, 196, 160), eye=(255, 230, 60), eye_hi=(255, 255, 200),
           claw=(26, 18, 18), tip=(220, 210, 190), teeth=(236, 230, 200), mouth=(110, 25, 30))

if __name__ == '__main__':
    print(paint(GREEN, 'hunter', 11))
    print(paint(RED, 'hunter_red', 12))
