"""G-Birkin: raw flesh, shoulder eyeball, bone claws. birkin.png (G1, labcoat remnants), birkin_g2.png (darker, veined), birkin_g3.png (bloated pale-purple)."""
from autotex import *
from texlib import Tex

CUBES = [('hips', 0, 0, 10, 5, 6), ('torso', 0, 12, 14, 13, 7), ('neck', 44, 0, 4, 3, 4), ('head', 44, 8, 6, 7, 6), ('jaw', 70, 0, 5, 2, 5), ('eye', 70, 8, 7, 7, 7),
         ('arm', 0, 34, 8, 13, 8), ('arm', 34, 34, 9, 13, 9), ('hand', 72, 34, 8, 5, 7), ('claw', 72, 48, 2, 10, 2),
         ('arm', 0, 58, 5, 12, 5), ('arm', 22, 58, 5, 11, 5), ('hand', 44, 58, 4, 4, 4), ('claw', 62, 58, 1, 5, 1),
         ('leg', 0, 78, 6, 11, 6), ('leg', 26, 78, 5, 10, 5), ('foot', 48, 78, 5, 2, 8), ('tendril', 0, 98, 3, 3, 9)]
PAL = {'': ((176, 74, 70), (212, 112, 100), (110, 34, 38)), '_g2': ((120, 44, 62), (162, 80, 92), (70, 20, 40)), '_g3': ((150, 110, 140), (190, 150, 176), (92, 54, 100))}

def build(sfx):
    base, hi, lo = PAL[sfx]
    class P(Paint):
        def __init__(self):
            self.W = self.H = 128; self.skipped = []
            self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
            self.t = Tex(128, 128, 101 + len(sfx)); self.rng = self.t.rng
    p = P()
    def flesh(pt, f, r, c):
        muscle(pt, r, base, hi, lo, vertical=(f not in ('top', 'bottom')))
        pt.veins(r, shade(lo, 0.6), 3 + len(sfx)); pt.blots(r, shade(lo, 0.7), 2, 2)
    def torso(pt, f, r, c):
        flesh(pt, f, r, c)
        if sfx == '' and f in ('back', 'left', 'right'):  # tattered white labcoat clinging to the back and flanks
            x, y, w, h = r
            for i in range(w):
                cut = pt.rng.randint(h // 2, h)
                pt.fill((x + i, y, 1, cut), (214, 210, 204), 0.06)
            pt.blots(r, (150, 20, 24), 4, 2)
        if sfx == '_g3': pt.blots(r, (210, 170, 200), 5, 3); pt.veins(r, (60, 20, 70), 6)
    def eye(pt, f, r, c):
        x, y, w, h = r
        pt.fill(r, (236, 220, 170) if sfx != '_g3' else (240, 230, 190), 0.05); pt.veins(r, (190, 60, 60), 5)
        if f in ('front', 'left', 'right'):
            pt.fill((x + w // 2 - 1, y + h // 2 - 1, 3, 3), (200, 30, 20), 0); pt.dot(x + w // 2, y + h // 2, (10, 0, 0))
    def head(pt, f, r, c):
        flesh(pt, f, r, c); x, y, w, h = r
        if f == 'front':
            pt.fill((x + 1, y + 2, 1, 1), (20, 0, 0), 0); pt.fill((x + w - 2, y + 2, 1, 1), (20, 0, 0), 0)
            teeth(pt, (x + 1, y + h - 2, w - 2, 1), (230, 220, 190), 1)
    def jaw(pt, f, r, c):
        if f == 'top': teeth(pt, r, (230, 220, 190), 1)
        else: flesh(pt, f, r, c)
    def claw(pt, f, r, c): pt.fill(r, (44, 38, 40) if c['h'] > 5 else (190, 180, 150), 0.1)
    def tend(pt, f, r, c): pt.fill(r, shade(base, 0.8), 0.1); pt.streak(r, shade(base, 0.8), False, 0.12); pt.blots(r, (230, 190, 220), 2, 1)
    def hand(pt, f, r, c): flesh(pt, f, r, c); pt.fill((r[0], r[1], r[2], 1), (60, 30, 34), 0)
    p.paint([('torso|hips', torso), ('head|neck', head), ('jaw', jaw), ('eye', eye), ('claw', claw), ('tendril', tend), ('hand', hand)], flesh)
    p.save('birkin' + sfx)

for s in PAL: build(s)
