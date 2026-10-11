"""Tyrant T-002 textures: tyrant.png (trenchcoat), _dormant, _super (coat shed, pulsing heart), _enraged. UVs parsed from TyrantModel."""
from autotex import *

SKIN = {'': (146, 152, 148), '_dormant': (168, 172, 166), '_super': (150, 128, 126), '_enraged': (126, 112, 112)}
VEIN = {'': (96, 104, 120), '_dormant': (130, 136, 140), '_super': (150, 40, 44), '_enraged': (170, 30, 30)}
COAT = (38, 36, 36)
BLOOD, BONE, BRAIN = (110, 10, 12), (206, 198, 170), (196, 140, 150)

def build(sfx):
    p = Paint('tyrant/TyrantModel.java', seed=21 + len(sfx))
    skin, vein = SKIN[sfx], VEIN[sfx]
    coat_on = sfx in ('', '_dormant')

    def flesh(p, face, r, c):
        p.fill(r, skin, 0.07, 0.12)
        p.veins(r, vein, 4 if sfx else 2)
        if sfx in ('_super', '_enraged') and r[2] > 3: p.blots(r, (150, 40, 44), 3, 2)
        if sfx and r[2] > 4: p.wound(r, (120, 40, 44), BLOOD)

    def cloth(p, face, r, c):
        p.fill(r, COAT, 0.1, 0.1)
        if p.rng.random() < 0.6: p.blots(r, (60, 56, 54), 4, 2)

    def torso(p, face, r, c):
        if coat_on and face != 'top': cloth(p, face, r, c)
        else:
            flesh(p, face, r, c)
            if face == 'front' and c['name'] == 'chest' and sfx:  # ribcage ridges of the stripped torso
                for j in range(1, r[3] - 1, 3): p.t.line(r[0] + 1, r[1] + j, r[0] + r[2] - 2, r[1] + j, shade(skin, 0.7))

    def heart(p, face, r, c):
        p.fill(r, (170, 20, 28) if sfx in ('_super', '_enraged') else skin, 0.1)
        p.veins(r, (110, 8, 14), 2)

    def head(p, face, r, c):
        flesh(p, face, r, c)
        x, y, w, h = r
        if face == 'front':
            if sfx == '_dormant': p.fill((x + 1, y + 2, w - 2, 1), (60, 60, 62), 0)  # eyes shut
            else:
                for ex in (x + 1, x + w - 3): p.fill((ex, y + 2, 2, 2), (232, 232, 220) if sfx != '_enraged' else (255, 60, 40), 0); p.dot(ex + 1, y + 3, (20, 0, 0))
            p.fill((x + 2, y + h - 2, w - 4, 1), (70, 40, 44), 0)
    def brain(p, face, r, c):
        p.fill(r, BRAIN, 0.12); p.veins(r, (150, 50, 70), 5)
        if face in ('top', 'front'):
            for i in range(0, r[2], 2): p.t.line(r[0] + i, r[1], r[0] + i, r[1] + r[3] - 1, shade(BRAIN, 0.75))
    def claws(p, face, r, c): p.fill(r, (30, 28, 30) if c['h'] > 4 else BONE, 0.08)
    def hand(p, face, r, c): flesh(p, face, r, c); p.fill((r[0], r[1], r[2], 1), (50, 48, 50), 0)
    def foot(p, face, r, c): p.fill(r, (30, 28, 28), 0.1)
    def pants(p, face, r, c): cloth(p, face, r, c) if coat_on or True else None
    p.paint([('heart', heart), ('head', head), ('brain', brain), ('jaw', head), ('r?Hand', lambda p, f, r, c: claws(p, f, r, c) if c['w'] == 1 else hand(p, f, r, c)),
             ('lThumb', claws), ('coat[LR]', cloth), ('[rl]Foot', foot), ('[rl](Thigh|Shin)|hips', pants), ('chest|abdomen|rTrap', torso)], flesh)
    p.save('tyrant' + sfx)

for s in SKIN: build(s)
