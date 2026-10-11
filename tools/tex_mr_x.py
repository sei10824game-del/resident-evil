"""Mr. X: black trenchcoat & fedora, grey scarred skin. mr_x.png and mr_x_super.png (coat shredded, exposed veined flesh, claw arm, tumour)."""
from autotex import *
from texlib import Tex

CUBES = [('hips', 0, 0, 8, 3, 4), ('torso', 0, 8, 9, 10, 5), ('neck', 30, 0, 3, 1, 3), ('head', 30, 6, 7, 7, 7), ('brim', 60, 0, 10, 1, 10), ('crown', 60, 12, 7, 3, 7),
         ('arm', 0, 24, 4, 9, 4), ('arm', 18, 24, 4, 9, 4), ('hand', 36, 24, 4, 3, 4), ('claw', 54, 24, 1, 7, 1),
         ('thigh', 0, 40, 4, 7, 4), ('shin', 18, 40, 4, 7, 4), ('foot', 36, 40, 4, 2, 6), ('coat', 58, 40, 5, 10, 6), ('tumor', 82, 24, 6, 6, 6)]

def build(sfx):
    sup = sfx == '_super'
    COAT, SKIN = (26, 26, 30), ((138, 128, 130) if sup else (160, 156, 146))
    class P(Paint):
        def __init__(self):
            self.W, self.H, self.skipped = 128, 64, []
            self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
            self.t = Tex(128, 64, 91 + sup); self.rng = self.t.rng
    p = P()
    def coat(pt, f, r, c):
        pt.fill(r, COAT, 0.1, 0.1); pt.streak(r, COAT, True, 0.08)
        if f in ('front', 'back') and c['name'] == 'torso':
            x, y, w, h = r
            pt.t.line(x + w // 2, y, x + w // 2, y + h - 1, (10, 10, 12))                   # front seam / back vent
            for j in (2, 5, 8):  pt.dot(x + w // 2 - 1, y + j, (70, 70, 74)); pt.dot(x + w // 2 + 1, y + j, (70, 70, 74))  # buttons
        pt.blots(r, (44, 44, 50), 3, 2)
        if sup and p.rng.random() < 0.8: pt.wound(r, (150, 120, 120), (80, 8, 10))        # torn open
    def skin(pt, f, r, c):
        pt.fill(r, SKIN, 0.06, 0.1); pt.veins(r, (90, 80, 100) if not sup else (150, 40, 50), 3 if sup else 1)
        if sup: pt.blots(r, (110, 40, 50), 3, 2)
    def torso(pt, f, r, c):
        if sup and f in ('front', 'top'):
            skin(pt, f, r, c); pt.wound(r, (170, 60, 60), (90, 8, 10), (230, 220, 200)); pt.veins(r, (170, 40, 50), 5)
            x, y, w, h = r
            pt.fill((x, y, 2, h), COAT, 0.1); pt.fill((x + w - 2, y, 2, h), COAT, 0.1)   # remnants of the lapels
        else: coat(pt, f, r, c)
    def head(pt, f, r, c):
        skin(pt, f, r, c); x, y, w, h = r
        if f == 'front':
            pt.fill((x + 1, y + 2, 2, 1), (10, 10, 12), 0); pt.fill((x + w - 3, y + 2, 2, 1), (10, 10, 12), 0)   # sunken eyes
            pt.fill((x + 2, y + h - 2, w - 4, 1), (60, 40, 44), 0)
            pt.t.line(x + 4, y + 1, x + 5, y + h - 2, (96, 70, 70))                                       # scar
        if f in ('top',): pt.fill(r, (24, 24, 26), 0.1)
    def hat(pt, f, r, c): pt.fill(r, (14, 14, 16), 0.08); pt.streak(r, (22, 22, 24), False, 0.06)
    def arm(pt, f, r, c): coat(pt, f, r, c)
    def hand(pt, f, r, c): skin(pt, f, r, c)
    def claw(pt, f, r, c): pt.fill(r, (46, 38, 42) if sup else (110, 108, 104), 0.1)
    def tumor(pt, f, r, c): pt.fill(r, (150, 50, 70), 0.12); pt.veins(r, (90, 10, 30), 5); pt.blots(r, (210, 120, 130), 3, 2)
    def leg(pt, f, r, c): coat(pt, f, r, c) if c['name'] in ('thigh', 'coat', 'hips') else pt.fill(r, (16, 16, 18), 0.08)
    p.paint([('torso', torso), ('head|neck', head), ('brim|crown', hat), ('arm', arm), ('hand', hand), ('claw', claw), ('tumor', tumor), ('thigh|shin|foot|coat|hips', leg)], coat)
    p.save('mr_x' + sfx)

build(''); build('_super')
