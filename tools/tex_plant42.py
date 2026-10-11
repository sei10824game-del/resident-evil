"""Plant 42: green stalk with thorns, magenta flesh-bulb, ivory-toothed petal maw, venom sacs, vine tentacles. plant42.png + plant42_phase2.png (maroon, withering)."""
from autotex import *
from texlib import Tex

CUBES = [('base', 0, 0, 24, 6, 24), ('root', 100, 0, 4, 3, 14), ('stem1', 0, 32, 10, 28, 10), ('stem2', 42, 32, 8, 24, 8), ('head', 0, 72, 20, 18, 20),
         ('petal', 84, 72, 16, 2, 14), ('tooth', 146, 72, 2, 4, 2), ('petalS', 156, 72, 2, 12, 14), ('sac', 190, 72, 6, 6, 6), ('leaf', 84, 92, 14, 1, 10),
         ('ts0', 0, 114, 4, 4, 18), ('ts1', 46, 114, 4, 4, 18), ('ts2', 92, 114, 3, 3, 16), ('ts3', 132, 114, 3, 3, 16), ('ts4', 172, 114, 2, 2, 14), ('thorn', 206, 114, 2, 2, 4)]

def build(sfx):
    p2 = sfx == '_phase2'
    GREEN = (92, 62, 48) if p2 else (56, 112, 48)
    BARK = (66, 56, 40) if p2 else (74, 72, 40)
    FLESH = (112, 36, 56) if p2 else (176, 64, 116)
    INNER = (150, 30, 44) if p2 else (206, 70, 90)
    LEAF = (96, 74, 46) if p2 else (84, 156, 62)
    SAC = (150, 150, 50) if p2 else (186, 208, 70)
    class P(Paint):
        def __init__(self):
            self.W = self.H = 256; self.skipped = []
            self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
            self.t = Tex(256, 256, 71 + p2); self.rng = self.t.rng
    p = P()
    def stalk(pt, f, r, c):
        pt.fill(r, GREEN, 0.08, 0.1); pt.streak(r, GREEN, True, 0.1); pt.veins(r, shade(GREEN, 0.6), 4)
        if p2: pt.blots(r, (60, 40, 30), 6, 3)
    def bark(pt, f, r, c): pt.fill(r, BARK, 0.14); pt.streak(r, BARK, True, 0.16)
    def head(pt, f, r, c):
        pt.fill(r, FLESH, 0.08, 0.1); pt.veins(r, shade(FLESH, 0.55), 7); pt.blots(r, shade(FLESH, 1.25), 5, 2); pt.blots(r, shade(FLESH, 0.7), 4, 2)
        if p2: pt.blots(r, (60, 30, 30), 8, 3)
        if f == 'front':  # the maw cavity shows between the petals
            x, y, w, h = r; pt.fill((x + 4, y + 5, w - 8, h - 10), (60, 6, 16), 0.1)
    def petal(pt, f, r, c):
        pt.fill(r, INNER if f in ('bottom', 'top') else GREEN, 0.08); pt.veins(r, shade(INNER, 0.6), 3)
    def tooth(pt, f, r, c): pt.fill(r, (232, 226, 200), 0.05)
    def sac(pt, f, r, c): pt.fill(r, SAC, 0.1); pt.blots(r, shade(SAC, 0.7), 3, 2); pt.blots(r, shade(SAC, 1.3), 2, 1)
    def leaf(pt, f, r, c):
        pt.fill(r, LEAF, 0.1)
        if f in ('top', 'bottom'): pt.t.line(r[0], r[1] + r[3] // 2, r[0] + r[2] - 1, r[1] + r[3] // 2, shade(LEAF, 0.6))
    def tent(pt, f, r, c):
        pt.bands(r, GREEN, shade(GREEN, 0.78), 3, vertical=True) if c['w'] > 1 else pt.fill(r, GREEN, 0.1)
        pt.veins(r, shade(GREEN, 0.55), 1)
    def thorn(pt, f, r, c): pt.fill(r, (46, 30, 24), 0.1)
    p.paint([('base|root', bark), ('stem1|stem2', stalk), ('head', head), ('petal|petalS', petal), ('tooth', tooth), ('sac', sac), ('leaf', leaf), ('thorn', thorn), ('ts[0-4]', tent)], stalk)
    p.save('plant42' + sfx)

build(''); build('_phase2')
