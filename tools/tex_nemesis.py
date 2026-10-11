"""Nemesis: black coat, bald scarred head with stitched mouth, tentacles. nemesis.png, nemesis_mutated.png (torn coat, flesh), nemesis_final.png."""
from autotex import *
from texlib import Tex

CUBES = [('hips', 0, 0, 9, 3, 4), ('torso', 0, 8, 10, 11, 5), ('neck', 30, 0, 3, 1, 3), ('head', 30, 6, 7, 7, 7), ('arm', 0, 24, 4, 10, 4), ('arm', 18, 24, 4, 10, 4), ('hand', 36, 24, 4, 3, 4),
         ('launcher', 54, 24, 4, 4, 14), ('thigh', 0, 40, 4, 7, 4), ('shin', 18, 40, 4, 7, 4), ('foot', 36, 40, 4, 2, 6), ('coat', 58, 40, 5, 11, 6), ('tent', 82, 24, 3, 3, 9), ('brim', 60, 0, 1, 1, 1)]

def build(sfx):
    lvl = {'': 0, '_mutated': 1, '_final': 2}[sfx]
    COAT = (24, 22, 24); SKIN = [(170, 164, 156), (160, 130, 130), (150, 100, 108)][lvl]; FLESH = [(150, 70, 74), (170, 60, 70), (150, 40, 60)][lvl]
    class P(Paint):
        def __init__(self):
            self.W, self.H, self.skipped = 128, 64, []
            self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
            self.t = Tex(128, 64, 141 + lvl); self.rng = self.t.rng
    p = P()
    def coat(pt, f, r, c):
        pt.fill(r, COAT, 0.1, 0.1); pt.streak(r, COAT, True, 0.08); pt.blots(r, (42, 40, 44), 3, 2)
        if f in ('front', 'back') and c['name'] == 'torso': pt.t.line(r[0] + r[2] // 2, r[1], r[0] + r[2] // 2, r[1] + r[3] - 1, (8, 8, 10))
        if lvl and pt.rng.random() < 0.4 * lvl + 0.3: pt.wound(r, shade(FLESH, 1.1), (80, 8, 14))
    def flesh(pt, f, r, c):
        muscle(pt, r, FLESH, shade(FLESH, 1.3), shade(FLESH, 0.6)); pt.veins(r, shade(FLESH, 0.5), 3); pt.blots(r, (70, 6, 12), 2, 2)
    def torso(pt, f, r, c):
        if lvl and f in ('front', 'top', 'left'): flesh(pt, f, r, c); x, y, w, h = r; pt.fill((x, y, 2, h), COAT, 0.1); pt.fill((x + w - 2, y, 2, h), COAT, 0.1)
        else: coat(pt, f, r, c)
    def head(pt, f, r, c):
        pt.fill(r, SKIN, 0.06, 0.1); pt.veins(r, shade(SKIN, 0.55), 2 + lvl * 3); x, y, w, h = r
        if f == 'front':
            pt.fill((x + 1, y + 2, 2, 1), (220, 30, 20) if lvl else (14, 14, 16), 0); pt.fill((x + w - 3, y + 2, 2, 1), (14, 14, 16), 0)
            for i in range(1, w - 1, 2): pt.dot(x + i, y + h - 2, (20, 14, 14))                     # stitched mouth
            pt.t.line(x + 1, y + h - 2, x + w - 2, y + h - 2, (50, 20, 24))
        if f == 'top': pt.fill(r, shade(SKIN, 0.9), 0.1)
    def hand(pt, f, r, c): pt.fill(r, SKIN, 0.06); pt.veins(r, shade(SKIN, 0.5), 1)
    def launcher(pt, f, r, c): pt.fill(r, (58, 62, 56), 0.1); pt.blots(r, (30, 32, 28), 3, 2); pt.blots(r, (150, 140, 60), 1, 2)
    def tent(pt, f, r, c): flesh(pt, f, r, c); pt.blots(r, (210, 150, 160), 2, 1)
    def arm(pt, f, r, c): flesh(pt, f, r, c) if lvl == 2 else coat(pt, f, r, c)
    def legs(pt, f, r, c): coat(pt, f, r, c) if c['name'] in ('thigh', 'coat', 'hips') else pt.fill(r, (14, 14, 16), 0.08)
    p.paint([('torso', torso), ('head|neck', head), ('hand', hand), ('launcher', launcher), ('tent', tent), ('arm', arm), ('thigh|shin|foot|coat|hips', legs), ('brim', lambda pt, f, r, c: pt.fill(r, SKIN, 0))], coat)
    p.save('nemesis' + sfx)

for s in ('', '_mutated', '_final'): build(s)
