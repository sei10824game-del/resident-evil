"""Giant Moth: furry cream-brown thorax/abdomen, red compound eyes, eye-spot patterned wings. giant_moth.png"""
from autotex import *
from texlib import Tex

CUBES = [('abdomen', 0, 0, 6, 6, 12), ('thorax', 38, 0, 5, 5, 6), ('head', 62, 0, 4, 4, 4), ('eye', 80, 0, 1, 2, 2), ('ant', 88, 0, 1, 1, 6), ('ant', 88, 8, 1, 1, 5),
         ('prob', 104, 0, 1, 1, 4), ('wing', 0, 20, 12, 1, 10), ('wing', 46, 20, 12, 1, 12), ('leg', 96, 20, 1, 5, 1)]
class P(Paint):
    def __init__(self):
        self.W, self.H, self.skipped = 128, 64, []
        self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
        self.t = Tex(128, 64, 121); self.rng = self.t.rng
p = P()
FUR, CREAM, BROWN = (46, 70, 52), (96, 130, 100), (24, 36, 30)
def fur(pt, f, r, c):
    pt.fill(r, FUR, 0.1, 0.08); pt.streak(r, FUR, True, 0.15); pt.blots(r, CREAM, 3, 1)
def abd(pt, f, r, c):
    pt.bands(r, FUR, shade(BROWN, 1.2), 3, vertical=False); pt.blots(r, CREAM, 3, 1)
def wing(pt, f, r, c):
    x, y, w, h = r
    if f in ('top', 'bottom'):
        pt.fill(r, shade(FUR, 0.9), 0.1)
        for j in range(0, h, 2):  # veins radiating
            pt.t.line(x, y + j, x + w - 1, y + h // 2, shade(BROWN, 1.1))
        cx, cy = x + w * 2 // 3, y + h // 2   # big eye-spot
        for rr, col in ((4, (30, 20, 16)), (3, (230, 190, 60)), (2, (160, 30, 30)), (1, (10, 10, 10))):
            for i in range(-rr, rr + 1):
                for j in range(-rr, rr + 1):
                    if i * i + j * j <= rr * rr: pt.t.px_set(cx + i, cy + j, col + (255,))
        pt.fill((x, y, w, 1), BROWN, 0.1); pt.fill((x, y + h - 1, w, 1), BROWN, 0.1)
    else: pt.fill(r, BROWN, 0.1)
def head(pt, f, r, c): fur(pt, f, r, c)
def eye(pt, f, r, c): pt.fill(r, (230, 40, 20), 0.15); pt.dot(r[0], r[1], (255, 120, 120))
def thin(pt, f, r, c): pt.fill(r, BROWN, 0.1)
p.paint([('abdomen', abd), ('thorax', fur), ('head', head), ('eye', eye), ('wing', wing), ('ant|prob|leg', thin)], fur)
p.save('drain_deimos')
