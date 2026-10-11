"""Moth larva: bloated pale-green segmented grub with dark dorsal bands, orange mandibles. larva.png"""
from autotex import *
from texlib import Tex

CUBES = [('head', 0, 0, 5, 4, 5), ('mand', 22, 0, 1, 1, 3), ('eye', 30, 0, 1, 1, 1), ('seg', 0, 10, 6, 5, 7), ('seg', 28, 10, 6, 5, 7), ('seg', 0, 24, 5, 4, 7),
         ('seg', 26, 24, 5, 4, 6), ('seg', 0, 36, 4, 3, 6), ('nub', 22, 36, 1, 2, 1)]
class P(Paint):
    def __init__(self):
        self.W = self.H = 64; self.skipped = []
        self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
        self.t = Tex(64, 64, 131); self.rng = self.t.rng
p = P()
SK, DK, BEL = (170, 190, 100), (86, 100, 44), (214, 220, 160)
def seg(pt, f, r, c):
    if f == 'bottom': pt.fill(r, BEL, 0.06); return
    pt.bands(r, SK, shade(SK, 0.82), 3, vertical=True)
    if f == 'top': pt.fill((r[0], r[1] + r[3] // 2 - 1, r[2], 2), DK, 0.1)
    pt.blots(r, (120, 140, 60), 3, 1)
def head(pt, f, r, c):
    pt.fill(r, (120, 96, 56), 0.1); pt.streak(r, (120, 96, 56), True, 0.1)
    if f == 'front': pt.fill((r[0], r[1] + r[3] - 1, r[2], 1), (30, 18, 10), 0)
p.paint([('head', head), ('mand', lambda pt, f, r, c: pt.fill(r, (220, 130, 40), 0.08)), ('eye', lambda pt, f, r, c: pt.fill(r, (10, 10, 10), 0)), ('nub', lambda pt, f, r, c: pt.fill(r, DK, 0.1))], seg)
p.save('larva')
