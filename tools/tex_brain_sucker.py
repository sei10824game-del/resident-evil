"""Brain Sucker: grey-violet leathery skin, pink veined wing membranes, red needle proboscis. brain_sucker.png"""
from autotex import *
from texlib import Tex

CUBES = [('body', 0, 0, 4, 4, 8), ('head', 28, 0, 4, 4, 5), ('prob', 48, 0, 1, 1, 5), ('prob', 48, 8, 1, 1, 4), ('wing', 0, 14, 10, 1, 8), ('wing', 38, 14, 12, 1, 10), ('leg', 84, 0, 1, 5, 1), ('ear', 92, 0, 1, 3, 1)]
class P(Paint):
    def __init__(self):
        self.W, self.H, self.skipped = 128, 64, []
        self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
        self.t = Tex(128, 64, 151); self.rng = self.t.rng
p = P()
SK, DK = (110, 96, 120), (60, 48, 70)
def skin(pt, f, r, c): pt.fill(r, SK, 0.1, 0.1); pt.streak(r, SK, True, 0.1); pt.veins(r, DK, 2)
def head(pt, f, r, c):
    skin(pt, f, r, c)
    if f == 'front': pt.fill((r[0] + 1, r[1] + 1, 1, 1), (230, 40, 60), 0); pt.fill((r[0] + r[2] - 2, r[1] + 1, 1, 1), (230, 40, 60), 0)
def wing(pt, f, r, c):
    x, y, w, h = r
    if f in ('top', 'bottom'):
        pt.fill(r, (170, 100, 130), 0.1)
        for j in range(0, h, 2): pt.t.line(x, y + j, x + w - 1, y + h // 2, DK)
        pt.veins(r, (130, 40, 70), 4)
    else: pt.fill(r, DK, 0.1)
p.paint([('body', skin), ('head', head), ('prob', lambda pt, f, r, c: pt.fill(r, (190, 40, 60), 0.12)), ('wing', wing), ('leg|ear', lambda pt, f, r, c: pt.fill(r, DK, 0.1))], skin)
p.save('brain_sucker')
