"""Yawn (giant snake): dark green diamond-pattern back, pale belly, yellow slit eyes. yawn.png + yawn_enraged.png (blackened, red-scarred)."""
from autotex import *
from texlib import Tex

SEG_TAIL = [(40, 0, 12, 10), (118, 0, 11, 9), (156, 0, 11, 9), (0, 18, 10, 8), (112, 18, 9, 7), (146, 18, 8, 6), (216, 18, 7, 5), (0, 34, 6, 4), (28, 34, 4, 3), (52, 34, 3, 2)]
NECK = [(80, 0, 11, 9), (194, 0, 10, 8), (78, 18, 9, 7)]
EXTRA = [('body', 0, 0, 12, 10, 8), ('ridge', 74, 34, 1, 1, 6), ('head', 36, 18, 10, 4, 11), ('hornL', 104, 34, 3, 1, 4), ('fang', 118, 34, 1, 3, 1), ('jaw', 178, 18, 9, 3, 10),
         ('lowteeth', 130, 34, 7, 1, 1), ('upteeth', 146, 34, 8, 1, 1), ('tongue', 88, 34, 2, 1, 6), ('tonguetip', 122, 34, 1, 1, 3)]

def build(sfx):
    en = sfx == '_enraged'
    back, dia, belly = ((36, 44, 34), (14, 20, 14), (150, 120, 90)) if en else ((58, 98, 46), (30, 54, 28), (206, 194, 134))
    class P(Paint):
        def __init__(self):
            self.W, self.H, self.skipped = 256, 64, []
            self.cubes = [dict(name='seg', u=u, v=v, w=w, h=h, d=8) for u, v, w, h in SEG_TAIL] + [dict(name='seg', u=u, v=v, w=w, h=h, d=8) for u, v, w, h in NECK]
            self.cubes += [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in EXTRA]
            self.t = Tex(256, 64, 61 + en); self.rng = self.t.rng
    p = P()
    def scaly(pt, f, r, c, col=back):
        pt.scales(r, col, 3)
    def seg(pt, f, r, c):
        x, y, w, h = r
        if f == 'bottom': pt.bands(r, belly, shade(belly, 0.82), 2, vertical=True); return
        if f in ('top', 'left', 'right', 'front', 'back'):
            scaly(pt, f, r, c)
            if f == 'top':  # diamond saddle pattern
                for k in range(0, h if False else r[3], 6):
                    pass
                for j in range(0, r[3], 6):
                    cx = x + w // 2
                    for d in range(-min(w // 2, 3), min(w // 2, 3) + 1):
                        hh = 3 - abs(d)
                        for q in range(-hh // 2, hh // 2 + 1): pt.t.px_set(cx + d, y + j + 1 + q + hh // 2, dia + (255,))
            elif f in ('left', 'right'):  # lateral blotches fade into belly colour
                for j in range(r[3] - 3, r[3]): pt.t.line(x, y + j, x + w - 1, y + j, shade(belly, 0.9 if j == r[3] - 1 else 0.75))
            if en and p.rng.random() < 0.8: pt.wound(r, (150, 40, 40), (70, 8, 8))
    def head(pt, f, r, c):
        scaly(pt, f, r, c)
        x, y, w, h = r
        if f in ('left', 'right'): pt.fill((x + w - 4, y + 1, 2, 2), (240, 200, 30) if not en else (255, 60, 40), 0); pt.dot(x + w - 3, y + 1, (10, 10, 4), 1)
        if f == 'front': pt.dot(x + 2, y + 1, dia); pt.dot(x + w - 3, y + 1, dia)
        if f == 'bottom': pt.fill(r, (190, 80, 90), 0.08)
    def jaw(pt, f, r, c):
        if f == 'top': pt.fill(r, (190, 80, 90), 0.08)
        elif f == 'bottom': pt.fill(r, belly, 0.05)
        else: scaly(pt, f, r, c)
    p.paint([('seg|body', seg), ('head', head), ('jaw', jaw), ('ridge|hornL', lambda pt, f, r, c: pt.fill(r, dia, 0.1)),
             ('fang|upteeth|lowteeth', lambda pt, f, r, c: pt.fill(r, (236, 232, 210), 0.04)), ('tongue(tip)?', lambda pt, f, r, c: pt.fill(r, (200, 40, 70), 0.1))], seg)
    p.save('yawn' + sfx)

build(''); build('_enraged')
