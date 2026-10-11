"""Giant Alligator: armoured olive-green scutes, pale belly, bloodshot yellow eyes, bony teeth. alligator.png"""
from autotex import *
from texlib import Tex

CUBES = [('body', 0, 0, 10, 6, 24), ('head', 70, 0, 7, 4, 14), ('jaw', 70, 20, 6, 2, 13), ('teeth', 110, 20, 6, 1, 1), ('eye', 110, 24, 2, 2, 2),
         ('tail', 0, 32, 8, 5, 10), ('tail', 38, 32, 6, 4, 10), ('tail', 72, 38, 3, 3, 10), ('leg', 0, 50, 3, 4, 3), ('leg', 14, 50, 3, 3, 3), ('foot', 28, 50, 4, 1, 4), ('spike', 46, 50, 1, 2, 2)]
BACK, BELLY, DARK = (62, 84, 44), (206, 196, 150), (34, 50, 26)
class P(Paint):
    def __init__(self):
        self.W, self.H, self.skipped = 128, 64, []
        self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
        self.t = Tex(128, 64, 111); self.rng = self.t.rng
p = P()
def scute(pt, f, r, c):
    x, y, w, h = r
    if f == 'bottom': pt.bands(r, BELLY, shade(BELLY, 0.88), 3, vertical=False); return
    pt.scales(r, BACK, 3)
    if f == 'top':  # rows of raised dorsal plates
        for j in range(1, h, 4):
            for i in range(1, w, 3): pt.dot(x + i, y + j, shade(BACK, 0.55), 1)
    else:
        for j in range(r[3] - 3, r[3]):
            pt.t.line(x, y + j, x + w - 1, y + j, shade(BELLY, 0.8 + 0.07 * (j - (h - 3))))  # flank fades to belly
    if p.rng.random() < 0.5: pt.wound(r, (150, 90, 70), (90, 16, 16))
def head(pt, f, r, c):
    scute(pt, f, r, c); x, y, w, h = r
    if f == 'front': pt.dot(x + 1, y + 1, DARK, 1); pt.dot(x + w - 2, y + 1, DARK, 1)  # nostrils
    if f == 'bottom': pt.fill(r, (170, 90, 90), 0.1)
def jaw(pt, f, r, c):
    if f == 'top': pt.fill(r, (170, 90, 90), 0.1); pt.drips(r, (120, 14, 14), 2)
    else: scute(pt, f, r, c)
p.paint([('head', head), ('jaw', jaw), ('teeth', lambda pt, f, r, c: pt.fill(r, (232, 226, 196), 0.04)), ('eye', lambda pt, f, r, c: (pt.fill(r, (220, 190, 40), 0.05), pt.dot(r[0], r[1], (10, 10, 4), 1))),
         ('spike', lambda pt, f, r, c: pt.fill(r, DARK, 0.1)), ('foot', lambda pt, f, r, c: pt.fill(r, DARK, 0.1))], scute)
p.save('alligator')
