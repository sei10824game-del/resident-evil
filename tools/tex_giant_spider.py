"""Shared 128x128 spider rig textures. giant_spider.png (Black Tiger: black body, yellow-banded abdomen & orange leg bands, 8 red eyes)
and web_spinner.png (small purple-grey spiderling with red hourglass). Cube table mirrors SpiderRigModel.createRig()."""
from autotex import *

CUBES = [('body', 0, 0, 10, 6, 10), ('head', 40, 0, 8, 5, 6), ('abdomen', 0, 16, 12, 10, 14), ('spinneret', 52, 16, 3, 3, 2), ('thread', 0, 64, 1, 1, 32),
         ('chel', 80, 0, 3, 4, 3), ('chel', 68, 0, 3, 4, 3), ('fang', 96, 0, 1, 3, 1), ('fang', 92, 0, 1, 3, 1), ('palp', 112, 0, 2, 2, 4), ('palp', 100, 0, 2, 2, 4),
         ('palp2', 60, 22, 1, 1, 3), ('palp2', 52, 22, 1, 1, 3), ('fem', 0, 40, 10, 3, 3), ('tib', 0, 46, 11, 2, 2), ('tar', 0, 50, 7, 1, 1)]

def build(name, base, band1, band2, eye, mark, seed):
    p = Paint('giant_spider_dummy', seed) if False else None
    from texlib import Tex
    class P(Paint):
        def __init__(self):
            self.W, self.H, self.skipped = 128, 128, []
            self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
            self.t = Tex(128, 128, seed); self.rng = self.t.rng
    p = P()
    def hairy(pt, f, r, c): pt.fill(r, base, 0.1, 0.1); pt.streak(r, base, True, 0.12)
    def body(pt, f, r, c):
        hairy(pt, f, r, c)
        if f == 'top': pt.blots(r, band2, 3, 2)
    def head(pt, f, r, c):
        hairy(pt, f, r, c)
        if f == 'front':
            x, y, w, h = r
            for i, (ex, ey, s) in enumerate([(1, 1, 2), (w - 3, 1, 2), (3, 1, 1), (w - 4, 1, 1), (2, 3, 1), (w - 3, 3, 1), (4, 2, 1), (w - 5, 2, 1)]):
                pt.dot(x + ex, y + ey, eye, s)
    def abdomen(pt, f, r, c):
        if f in ('top', 'left', 'right', 'back'):
            pt.bands(r, base, band1, 3, vertical=(f in ('top',)))
            pt.blots(r, (20, 16, 14), 4, 2)
            if mark and f == 'top':  # hourglass
                x, y, w, h = r
                pt.fill((x + w // 2 - 1, y + 3, 2, h - 6), mark, 0)
                pt.fill((x + w // 2 - 2, y + 2, 4, 2), mark, 0); pt.fill((x + w // 2 - 2, y + h - 4, 4, 2), mark, 0)
        else: hairy(pt, f, r, c)
    def leg(pt, f, r, c):
        pt.fill(r, base, 0.1); pt.streak(r, base, False, 0.1)
        if c['name'] in ('fem', 'tib'): pt.bands((r[0], r[1], r[2], r[3]), base, band2, 3, vertical=True)
    def fang(pt, f, r, c): pt.fill(r, (210, 196, 160), 0.06)
    def web(pt, f, r, c): pt.fill(r, (232, 232, 238), 0.05)
    p.paint([('body', body), ('head', head), ('abdomen', abdomen), ('thread', web), ('fang', fang), ('spinneret', web), ('fem|tib|tar', leg)], hairy)
    p.save(name)

build('giant_spider', (22, 18, 18), (214, 168, 28), (226, 130, 30), (255, 60, 40), None, 51)
build('web_spinner', (64, 50, 74), (96, 76, 108), (120, 100, 130), (255, 80, 60), (200, 30, 30), 52)
