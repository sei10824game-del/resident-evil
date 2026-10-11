"""Neptune: mutated great white. Counter-shaded (slate back / white belly), gills, scars, dead eyes, bloody jaws."""
from autotex import *

BACK, BELLY, LINE = (70, 92, 112), (226, 230, 228), 0.5
def shade_body(p, f, r, c):
    x, y, w, h = r
    if f == 'top': p.fill(r, BACK, 0.07); return
    if f == 'bottom': p.fill(r, BELLY, 0.04); return
    for j in range(h):  # slate -> white counter-shade, wavy line
        t = j / max(1, h - 1)
        col = tuple(int(BACK[k] + (BELLY[k] - BACK[k]) * min(1, max(0, (t - 0.35) * 3))) for k in range(3))
        for i in range(w): p.t.px_set(x + i, y + j, shade(col, p.rng.uniform(0.93, 1.07)))
    if f in ('left', 'right') and w > 6:
        for gx in (x + 2, x + 4, x + 6): p.t.line(gx, y + 2, gx, y + min(h - 3, 6), (36, 46, 56))  # gill slits
    if p.rng.random() < 0.9:
        for _ in range(2):
            sx, sy = x + p.rng.randrange(max(1, w - 4)), y + p.rng.randrange(max(1, h - 3))
            p.t.line(sx, sy, sx + 3, sy + 2, (190, 190, 186))  # old scars

def head(p, f, r, c):
    shade_body(p, f, r, c)
    x, y, w, h = r
    if f in ('left', 'right'): p.fill((x + 2, y + 2, 2, 2), (8, 8, 10), 0); p.dot(x + 2, y + 2, (150, 150, 150))
    if f == 'front': p.fill((x + 2, y + h - 3, w - 4, 2), (150, 30, 36), 0)
    if f == 'bottom': p.fill(r, (170, 60, 66), 0.1)  # mouth roof
def jaw(p, f, r, c):
    if f == 'top': p.fill(r, (160, 40, 46), 0.1); p.drips(r, (110, 8, 10), 3)
    else: shade_body(p, f, r, c)
def teeth_c(p, f, r, c): p.fill(r, (236, 232, 214), 0.05)
def fin(p, f, r, c): p.fill(r, tuple(int(v * 0.85) for v in BACK), 0.08); p.blots(r, (60, 76, 92), 3, 2)

p = Paint('neptune/NeptuneModel.java', seed=41)
def pick(pt, f, r, c):
    if c['h'] == 2 and c['d'] == 1 and c['w'] == 10: teeth_c(pt, f, r, c)
    elif c['name'] == 'head': head(pt, f, r, c)
    elif c['name'] == 'jaw': jaw(pt, f, r, c)
    elif re.match(r'caudal|dorsal|pec|pelv', c['name']): fin(pt, f, r, c)
    else: shade_body(pt, f, r, c)
import re
p.paint([('.*', pick)], pick)
p.save('neptune')
