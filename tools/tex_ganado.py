"""Ganado villager + Plaga forms. ganado.png (human) and ganado_plaga.png (infected: fleshy tentacles/blades/bulb). Also used by the chainsaw man via tex_salvador."""
from autotex import *
from texlib import Tex

CUBES = [('hips', 0, 0, 8, 3, 4), ('torso', 0, 8, 8, 10, 4), ('neck', 26, 0, 3, 2, 3), ('head', 26, 6, 7, 7, 7), ('brim', 56, 0, 10, 1, 10), ('hatTop', 56, 12, 6, 3, 6), ('bulb', 100, 0, 5, 5, 5),
         ('arm', 0, 24, 3, 9, 3), ('arm', 14, 24, 3, 9, 3), ('hand', 28, 24, 3, 3, 3), ('thigh', 0, 38, 4, 8, 4), ('shin', 18, 38, 4, 8, 4), ('foot', 36, 38, 4, 2, 5),
         ('shaft', 54, 24, 1, 22, 1), ('tine', 60, 24, 1, 6, 1), ('axehead', 66, 24, 1, 5, 6), ('sickle', 82, 24, 1, 2, 8), ('flame', 66, 32, 3, 3, 3), ('sawbody', 84, 38, 3, 4, 12), ('sawblade', 100, 24, 1, 3, 12),
         ('tent', 0, 50, 2, 2, 7), ('blade', 22, 50, 1, 13, 3), ('spiderleg', 34, 50, 2, 2, 9)]

def build(name, skin, shirt, pants, hat, plaga, seed, saw_skin=None, sack=False):
    class P(Paint):
        def __init__(self):
            self.W, self.H, self.skipped = 128, 64, []
            self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
            self.t = Tex(128, 64, seed); self.rng = self.t.rng
    p = P()
    FLESH = (190, 70, 80)
    def sk(pt, f, r, c): pt.fill(r, skin, 0.07, 0.1); pt.veins(r, shade(skin, 0.65), 2); pt.blots(r, (110, 20, 24), 1, 2)
    def cloth(col):
        def fn(pt, f, r, c):
            pt.fill(r, col, 0.1, 0.1); pt.streak(r, col, True, 0.1); pt.blots(r, shade(col, 0.7), 4, 2); pt.blots(r, (96, 14, 16), 1, 2)
            if f in ('front', 'back') and c['name'] == 'torso' and col == shirt: pt.fill((r[0] + 2, r[1], 1, r[3]), shade(col, 0.6), 0); pt.fill((r[0] + r[2] - 3, r[1], 1, r[3]), shade(col, 0.6), 0)  # vest edges
        return fn
    def head(pt, f, r, c):
        if sack:
            pt.fill(r, (178, 150, 104), 0.1); pt.streak(r, (178, 150, 104), True, 0.12)
            if f == 'front': pt.fill((r[0] + 1, r[1] + 2, 2, 1), (20, 16, 12), 0); pt.fill((r[0] + r[2] - 3, r[1] + 2, 2, 1), (20, 16, 12), 0); pt.t.line(r[0], r[1] + r[3] - 2, r[0] + r[2] - 1, r[1] + r[3] - 2, (90, 70, 46))
            return
        sk(pt, f, r, c); x, y, w, h = r
        if f == 'front':
            pt.fill((x + 1, y + 2, 2, 1), (30, 12, 10), 0); pt.fill((x + w - 3, y + 2, 2, 1), (30, 12, 10), 0); pt.fill((x + 2, y + h - 2, w - 4, 1), (70, 30, 30), 0)
        if f == 'top': pt.fill(r, (50, 40, 34), 0.1)
    def hatf(pt, f, r, c): pt.fill(r, hat, 0.1); pt.streak(r, hat, False, 0.12)
    def bulb(pt, f, r, c):
        pt.fill(r, FLESH, 0.1); pt.veins(r, (110, 20, 40), 4); pt.blots(r, (240, 190, 90), 2, 1)
        if f == 'front': pt.fill((r[0] + 1, r[1] + r[3] // 2, r[2] - 2, 1), (60, 6, 14), 0); teeth(pt, (r[0] + 1, r[1] + r[3] // 2 + 1, r[2] - 2, 1), (230, 220, 190), 1)
    def tent(pt, f, r, c): pt.fill(r, shade(FLESH, 0.9), 0.1); pt.streak(r, FLESH, False, 0.1); pt.blots(r, (250, 200, 150), 2, 1)
    def blade(pt, f, r, c): pt.fill(r, (212, 200, 160), 0.08); pt.veins(r, (150, 110, 90), 2)
    def wood(pt, f, r, c): pt.fill(r, (110, 80, 50), 0.12)
    def metal(pt, f, r, c): pt.fill(r, (116, 120, 124), 0.1); pt.blots(r, (80, 60, 50), 2, 1)
    p.paint([('head', head), ('brim|hatTop', hatf), ('bulb', bulb), ('tent|spiderleg', tent), ('blade', blade), ('shaft', wood), ('tine|axehead|sickle|sawblade', metal),
             ('flame', lambda pt, f, r, c: pt.fill(r, (250, 160, 40), 0.15)), ('sawbody', lambda pt, f, r, c: pt.fill(r, (190, 120, 40), 0.1)),
             ('torso|hips', cloth(shirt)), ('thigh|shin', cloth(pants)), ('foot', cloth((46, 36, 28))), ('arm', cloth(shirt) if not plaga else sk), ('hand|neck', sk)], sk)
    p.save(name)

build('ganado', (196, 156, 124), (130, 100, 64), (78, 70, 60), (196, 176, 110), False, 161)
build('ganado_plaga', (186, 160, 150), (110, 84, 54), (70, 62, 54), (196, 176, 110), True, 162)
