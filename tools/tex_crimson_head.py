"""Crimson Head: flayed raw muscle, bone claws, glowing yellow eyes (crimson_head.png + crimson_head_eyes.png)."""
from autotex import *

p = Paint('crimsonhead/CrimsonHeadModel.java', seed=11)
for name, u, v, w, h, d in [('rightArm', 0, 26, 3, 6, 3), ('rightForearm', 12, 26, 3, 6, 3), ('rightHand', 24, 26, 3, 2, 3), ('rightClaws', 36, 26, 1, 4, 1),
                            ('leftArm', 0, 35, 3, 6, 3), ('leftForearm', 12, 35, 3, 6, 3), ('leftHand', 24, 35, 3, 2, 3), ('leftClaws', 36, 35, 1, 4, 1)]:
    p.add(name, u, v, w, h, d)
BONE, BLOOD, TENDON = (214, 200, 160), (92, 8, 10), (214, 160, 150)

def limb(p, face, r, c):
    muscle(p, r)
    if r[2] > 3 and r[3] > 3 and p.rng.random() < 0.7: p.veins(r, (110, 20, 30), 2)
    if p.rng.random() < 0.5: p.blots(r, BLOOD, 2, 2)
    if face in ('top',): p.fill(r, (120, 24, 26), 0.1)

def torso(p, face, r, c):
    muscle(p, r, vertical=(face != 'top'))
    if face == 'front' and c['name'] in ('chest', 'waist'):  # exposed ribcage showing through
        for j in range(1, r[3] - 1, 2): p.t.line(r[0] + 1, r[1] + j, r[0] + r[2] - 2, r[1] + j, shade(TENDON, 0.8))
    p.blots(r, BLOOD, 3, 2); p.drips(r, (140, 14, 16), 2)

def head(p, face, r, c):
    muscle(p, r, base=(130, 24, 28), hi=(176, 56, 52))
    if face == 'front':  # skull face: sockets, nose hole, bare teeth
        x, y, w, h = r
        for ex in (x + 1, x + w - 3):
            p.fill((ex, y + 1, 2, 2), (28, 6, 6), 0)
        p.fill((x + w // 2 - 1, y + 3, 2, 1), (40, 8, 8), 0)
        teeth(p, (x + 1, y + h - 1, w - 2, 1), BONE, 1)
    if face in ('top', 'back'): p.blots(r, (70, 6, 8), 6, 2)

def jaw(p, face, r, c):
    muscle(p, r, base=(120, 22, 26))
    if face == 'top': teeth(p, r, BONE, 1)
    p.drips(r, (160, 14, 16), 2)

def claws(p, face, r, c): p.fill(r, BONE, 0.08)
def hand(p, face, r, c): p.fill(r, (118, 24, 26), 0.1); p.blots(r, BLOOD, 1, 1)
p.paint([(r'.*Claws', claws), (r'.*Hand', hand), ('head', head), ('jaw', jaw), ('chest|waist|neck', torso)], limb)
p.report(); p.save('crimson_head')

# glow layer: only the eyes
e = Tex(p.W, p.H); f = front_of(p, 'head')['front']
for ex in (f[0] + 1, f[0] + f[2] - 3):
    for j in range(2):
        for i in range(2): e.px_set(ex + i, f[1] + 1 + j, (255, 220, 40, 255))
e.save('crimson_head_eyes')
