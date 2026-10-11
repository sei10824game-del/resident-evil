"""Lisa Trevor: pale emaciated body, tattered grey dress, matted hair, rusted shackles/chains, pulsing tumours. lisa_trevor.png + _mutated."""
from autotex import *

def build(sfx):
    mut = sfx == '_mutated'
    p = Paint('lisatrevor/LisaTrevorModel.java', seed=31 + mut)
    SKIN = (176, 150, 150) if mut else (206, 190, 180)
    DRESS, HAIR, IRON, FLESH = (96, 98, 100), (30, 24, 24), (88, 74, 62), (150, 56, 66)
    def skin(p, f, r, c):
        p.fill(r, SKIN, 0.07, 0.1); p.veins(r, (130, 100, 120) if not mut else (90, 40, 70), 3)
        if mut: p.wound(r, (120, 40, 50), (80, 10, 14))
        p.blots(r, (120, 70, 70), 1, 2)
    def dress(p, f, r, c):
        p.fill(r, DRESS, 0.1, 0.12); p.streak(r, DRESS, True, 0.1)
        for _ in range(4): p.blots(r, (50, 50, 54), 1, 3)
        p.blots(r, (84, 20, 22), 2, 2)
        if f in ('front', 'back', 'left', 'right') and r[3] > 4:  # ragged hem
            for i in range(0, r[2], 2): p.t.px_set(r[0] + i, r[1] + r[3] - 1, (0, 0, 0, 0))
    def hair(p, f, r, c): p.streak(r, HAIR, True, 0.2); p.blots(r, (60, 50, 50), 3, 1)
    def iron(p, f, r, c): p.fill(r, IRON, 0.12); p.blots(r, (140, 80, 40), 3, 1)
    def chain(p, f, r, c): p.fill(r, (110, 108, 106), 0.15)
    def tumor(p, f, r, c):
        p.fill(r, FLESH, 0.12); p.veins(r, (100, 20, 40), 5); p.blots(r, (210, 120, 130), 3, 2); p.blots(r, (80, 8, 20), 2, 2)
    def head(p, f, r, c):
        skin(p, f, r, c)
        if f == 'front': p.fill((r[0] + 1, r[1] + 4, r[2] - 2, 1), (110, 30, 36), 0); p.dot(r[0] + 1, r[1] + 2, (20, 10, 10)); p.dot(r[0] + r[2] - 2, r[1] + 2, (20, 10, 10))
    def claw(p, f, r, c): p.fill(r, (150, 140, 120), 0.1)
    p.paint([('tumor2?', tumor), ('head', head), ('hair.*', hair), ('.*Shackle', iron), ('.*Chain[12]', chain), ('.*Claw2[ab]', claw),
             ('skirt|hips|chest|spine', dress), ('.*Hand', lambda p, f, r, c: claw(p, f, r, c) if c['w'] == 1 else skin(p, f, r, c))], skin)
    p.save('lisa_trevor' + sfx)

build(''); build('_mutated')
