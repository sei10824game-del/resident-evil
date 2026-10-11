"""Licker: skinless red muscle, exposed brain, pink-purple tongue. licker.png and licker_beta.png (pale). Cube table mirrors LickerModel."""
from autotex import *
from texlib import Tex

CUBES = [('pelvis', 0, 0, 6, 6, 6), ('torso', 0, 18, 8, 7, 10), ('neck', 38, 18, 4, 4, 5), ('neck', 58, 18, 4, 4, 4), ('head', 26, 0, 8, 6, 8), ('brain', 60, 0, 6, 2, 5),
         ('jaw', 84, 0, 6, 2, 9), ('teeth', 116, 0, 5, 1, 1), ('limb', 78, 18, 3, 8, 3), ('limb', 92, 18, 3, 8, 3), ('hand', 106, 18, 4, 2, 4), ('claw', 106, 26, 1, 5, 1),
         ('thigh', 0, 38, 4, 7, 4), ('shin', 18, 38, 3, 8, 3), ('foot', 32, 38, 4, 2, 5), ('tongue', 46, 38, 2, 2, 6)]

def build(sfx):
    beta = sfx == '_beta'
    base, hi, lo = ((176, 150, 150), (206, 180, 176), (128, 100, 104)) if beta else ((84, 128, 44), (128, 176, 70), (46, 78, 30))
    class P(Paint):
        def __init__(self):
            self.W, self.H, self.skipped = 128, 64, []
            self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
            self.t = Tex(128, 64, 81 + beta); self.rng = self.t.rng
    p = P()
    def mus(pt, f, r, c):
        muscle(pt, r, base, hi, lo, vertical=(f not in ('top', 'bottom')))
        pt.veins(r, shade(lo, 0.7), 2); pt.blots(r, (70, 6, 10) if not beta else (110, 70, 76), 2, 2)
    def torso(pt, f, r, c):
        mus(pt, f, r, c)
        if f in ('front', 'left', 'right', 'back'):  # ribs showing
            for j in range(1, r[3] - 1, 2): pt.t.line(r[0] + 1, r[1] + j, r[0] + r[2] - 2, r[1] + j, shade((226, 210, 190), 0.9))
    def head(pt, f, r, c):
        pt.fill(r, shade(base, 0.9), 0.1); pt.veins(r, lo, 3)
        x, y, w, h = r
        if f == 'front': pt.fill((x + 1, y + 2, w - 2, 1), (30, 6, 8), 0)   # blind: eyes sewn/ruined into slits
        if f == 'bottom': pt.fill(r, (120, 30, 40), 0.1)
    def brain(pt, f, r, c):
        pt.fill(r, (110, 150, 50) if not beta else (200, 190, 196), 0.1); pt.veins(r, (170, 70, 90), 4)
        for i in range(0, r[2], 2): pt.t.line(r[0] + i, r[1], r[0] + i, r[1] + r[3] - 1, shade((222, 160, 170), 0.78))
    def jaw(pt, f, r, c):
        if f == 'top': pt.fill(r, (130, 28, 44), 0.1)
        else: mus(pt, f, r, c)
    def tongue(pt, f, r, c):
        pt.fill(r, (220, 110, 140) if not beta else (210, 130, 160), 0.1); pt.streak(r, (190, 70, 120), False, 0.1); pt.blots(r, (240, 150, 190), 2, 1)
    p.paint([('torso|pelvis', torso), ('head', head), ('brain', brain), ('jaw', jaw), ('teeth', lambda pt, f, r, c: pt.fill(r, (230, 222, 196), 0.04)),
             ('claw', lambda pt, f, r, c: pt.fill(r, (214, 200, 160), 0.08)), ('tongue', tongue)], mus)
    p.save('hunter_gamma' + sfx)

build('')
