"""Remake-only moves + their keyframed clips. Authoring is in TICKS; emits entity code (for variants_remake) and client/remake/RemakeClips.java."""
import os
J = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/java/com/reevil/')
SM, SNAP, IN, OUT, LIN = 'SM', 'SNAP', 'IN', 'OUT', 'LIN'
EASE = {'LIN': 0, 'SM': 1, 'IN': 2, 'OUT': 3, 'SNAP': 4}

class M:
    def __init__(s, id, name, dur, hits, mn, mx, cd, w, dmg, kb, face=None, move=False, tick=(), hit=(), clip=None, pos=None):
        s.id, s.name, s.dur, s.hits, s.mn, s.mx, s.cd, s.w, s.dmg, s.kb, s.face, s.move = id, name, dur, hits, mn, mx, cd, w, dmg, kb, face, move
        s.tick, s.hit, s.clip, s.pos = tick, hit, clip or {}, pos or {}

# tick effects: (tickFrom, tickTo, h, v) -> lunge each tick in range   |  hit effects: ('launch', i, vy) / ('aoe', i, radius, dmg, kb) / ('slow', i, ticks, amp) / ('pull', i) / ('hold', i)
def R(*rows): return list(rows)

MOV = {}
def reg(cid, *moves): MOV[cid] = list(moves)

# ================= zombies (rig: body waist chest neck head jaw rightArm rightForearm leftArm leftForearm thighs shins; arms: -x = forward/up)
reg('re1r_zombie', M(30, 'lunge_grapple', 36, [18], 0.4, 3.2, 40, 4, 4.5, 0.6, 30, tick=[(12, 17, 0.32, 0.0)],
  clip={'chest': R((0, 0, 0, 0), (10, -14, 0, 0), (18, 26, 0, 0, SNAP), (30, 10, 0, 0), (36, 0, 0, 0)), 'head': R((0, 0, 0, 0), (10, -18, 0, 0), (18, 22, 0, 0, SNAP), (36, 0, 0, 0)), 'jaw': R((0, 0, 0, 0), (12, 40, 0, 0), (18, 8, 0, 0, SNAP), (36, 0, 0, 0)),
        'rightArm': R((0, 0, 0, 0), (10, -100, 0, -14), (18, -70, 0, 10, SNAP), (30, -30, 0, 0), (36, 0, 0, 0)), 'leftArm': R((0, 0, 0, 0), (10, -100, 0, 14), (18, -70, 0, -10, SNAP), (30, -30, 0, 0), (36, 0, 0, 0)),
        'rightThigh': R((0, 0, 0, 0), (12, 30, 0, 0), (18, -34, 0, 0), (36, 0, 0, 0)), 'leftThigh': R((0, 0, 0, 0), (12, -14, 0, 0), (18, 20, 0, 0), (36, 0, 0, 0))}))
reg('re2r_zombie', M(31, 'double_shove', 34, [12, 22], 0, 2.0, 30, 4, 3.5, 1.1, 24,
  clip={'chest': R((0, 0, 0, 0), (8, -12, 0, 0), (12, 14, 0, 0, SNAP), (16, -8, 0, 0), (22, 16, 0, 0, SNAP), (34, 0, 0, 0)), 'rightArm': R((0, 0, 0, 0), (8, -60, 0, 0), (12, -95, 0, 0, SNAP), (16, -50, 0, 0), (22, -95, 0, 0, SNAP), (34, 0, 0, 0)),
        'leftArm': R((0, 0, 0, 0), (8, -60, 0, 0), (12, -95, 0, 0, SNAP), (16, -50, 0, 0), (22, -95, 0, 0, SNAP), (34, 0, 0, 0)), 'head': R((0, 0, 0, 0), (12, 14, 0, 0), (22, 14, 0, 0), (34, 0, 0, 0)),
        'rightThigh': R((0, 0, 0, 0), (12, -24, 0, 0), (22, -24, 0, 0), (34, 0, 0, 0)), 'leftThigh': R((0, 0, 0, 0), (12, 14, 0, 0), (34, 0, 0, 0))}))
reg('re3r_zombie', M(32, 'frenzied_flail', 40, [10, 18, 26], 0, 1.9, 36, 3, 2.8, 0.5, 30,
  clip={'rightArm': R((0, 0, 0, 0), (6, -120, 0, 20), (10, -40, 0, -20, SNAP), (14, -120, 0, 20), (26, -40, 0, -25, SNAP), (40, 0, 0, 0)), 'leftArm': R((0, 0, 0, 0), (10, -120, 0, -20), (18, -40, 0, 20, SNAP), (22, -120, 0, -20), (40, 0, 0, 0)),
        'chest': R((0, 0, 0, 0), (10, 8, -20, 0, SNAP), (18, 8, 22, 0, SNAP), (26, 8, -24, 0, SNAP), (40, 0, 0, 0)), 'head': R((0, 0, 0, 0), (10, 10, -16, 0), (18, 10, 18, 0), (26, 12, -18, 0), (40, 0, 0, 0)), 'jaw': R((0, 0, 0, 0), (6, 30, 0, 0), (28, 30, 0, 0), (40, 0, 0, 0))}))
reg('re3r_pale_head', M(30, 'rake_down', 36, [16], 0.5, 3.6, 34, 4, 6.5, 0.8, 22, tick=[(10, 15, 0.14, 0)],
  clip={'rightArm': R((0, 0, 0, 0), (10, -170, 0, 10), (16, 10, 0, -6, SNAP), (24, -10, 0, 0), (36, 0, 0, 0)), 'leftArm': R((0, 0, 0, 0), (10, -170, 0, -10), (16, 10, 0, 6, SNAP), (24, -10, 0, 0), (36, 0, 0, 0)),
        'chest': R((0, 0, 0, 0), (10, -18, 0, 0), (16, 32, 0, 0, SNAP), (28, 14, 0, 0), (36, 0, 0, 0)), 'head': R((0, 0, 0, 0), (10, -22, 0, 0), (16, 28, 0, 0, SNAP), (36, 0, 0, 0)), 'jaw': R((0, 0, 0, 0), (10, 36, 0, 0), (16, 4, 0, 0, SNAP), (36, 0, 0, 0))}))
reg('re4r_regenerador', M(30, 'maw_chomp', 32, [14], 0, 2.2, 34, 4, 8, 0.6, 24, tick=[(8, 13, 0.2, 0)], hit=[('slow', 0, 40, 1)],
  clip={'chest': R((0, 0, 0, 0), (8, -20, 0, 0), (14, 30, 0, 0, SNAP), (24, 10, 0, 0), (32, 0, 0, 0)), 'head': R((0, 0, 0, 0), (8, -22, 0, 0), (14, 20, 0, 0, SNAP), (32, 0, 0, 0)), 'rightArm': R((0, 0, 0, 0), (8, -70, 0, 20), (14, -100, 0, 10, SNAP), (32, 0, 0, 0)), 'leftArm': R((0, 0, 0, 0), (8, -70, 0, -20), (14, -100, 0, -10, SNAP), (32, 0, 0, 0))}),
  M(31, 'iron_shove', 30, [13], 0, 2.2, 32, 3, 5, 2.2, 18,
  clip={'chest': R((0, 0, 0, 0), (7, -14, 0, 0), (13, 20, 0, 0, SNAP), (30, 0, 0, 0)), 'rightArm': R((0, 0, 0, 0), (7, -50, 0, 0), (13, -96, 0, 0, SNAP), (30, 0, 0, 0)), 'leftArm': R((0, 0, 0, 0), (7, -50, 0, 0), (13, -96, 0, 0, SNAP), (30, 0, 0, 0)), 'rightThigh': R((0, 0, 0, 0), (13, -26, 0, 0), (30, 0, 0, 0))}))
reg('re4r_armadura', M(30, 'halberd_thrust', 36, [18], 0.8, 5.2, 36, 4, 11, 1.4, 22, tick=[(12, 17, 0.28, 0)],
  clip={'rightArm': R((0, 0, 0, 0), (12, -40, 20, 0), (18, -86, -6, 0, SNAP), (28, -50, 0, 0), (36, 0, 0, 0)), 'leftArm': R((0, 0, 0, 0), (12, -50, -20, 0), (18, -66, -10, 0, SNAP), (36, 0, 0, 0)), 'chest': R((0, 0, 0, 0), (12, -4, 28, 0), (18, 18, -24, 0, SNAP), (36, 0, 0, 0)),
        'rightThigh': R((0, 0, 0, 0), (12, 28, 0, 0), (18, -30, 0, 0), (36, 0, 0, 0)), 'leftThigh': R((0, 0, 0, 0), (12, -24, 0, 0), (18, 22, 0, 0), (36, 0, 0, 0))}),
  M(31, 'shield_bash', 28, [14], 0, 2.5, 30, 3, 6, 2.4, 16, tick=[(9, 13, 0.24, 0)],
  clip={'leftArm': R((0, 0, 0, 0), (8, -40, 0, 20), (14, -92, 0, -10, SNAP), (28, 0, 0, 0)), 'chest': R((0, 0, 0, 0), (8, -8, -24, 0), (14, 14, 18, 0, SNAP), (28, 0, 0, 0)), 'rightArm': R((0, 0, 0, 0), (8, 20, 0, 0), (14, -30, 0, 0), (28, 0, 0, 0))}))
reg('re4r_iron_maiden', M(30, 'spike_embrace', 56, [16, 30, 44], 0, 2.0, 50, 3, 5, 0, 52, hit=[('slow', 0, 70, 2)],
  clip={'rightArm': R((0, 0, 0, 0), (10, -90, 0, -25), (16, -85, 0, 30, SNAP), (44, -85, 0, 34, SNAP), (56, 0, 0, 0)), 'leftArm': R((0, 0, 0, 0), (10, -90, 0, 25), (16, -85, 0, -30, SNAP), (44, -85, 0, -34, SNAP), (56, 0, 0, 0)), 'chest': R((0, 0, 0, 0), (10, -10, 0, 0), (16, 18, 0, 0, SNAP), (30, 10, 0, 0), (44, 22, 0, 0, SNAP), (56, 0, 0, 0)), 'head': R((0, 0, 0, 0), (16, 14, 0, 0), (56, 0, 0, 0))}),
  M(31, 'cage_charge', 44, [26], 3, 8, 70, 3, 9, 2.4, 18, move=True, tick=[(8, 26, 0.34, 0)],
  clip={'chest': R((0, 0, 0, 0), (10, 22, 0, 0), (26, 30, 0, 0, SNAP), (44, 0, 0, 0)), 'rightArm': R((0, 0, 0, 0), (10, 40, 0, 0), (26, -50, 0, 0), (44, 0, 0, 0)), 'leftArm': R((0, 0, 0, 0), (10, 40, 0, 0), (26, -50, 0, 0), (44, 0, 0, 0)),
        'rightThigh': R((0, 0, 0, 0), (10, -30, 0, 0), (18, 30, 0, 0), (26, -30, 0, 0), (34, 20, 0, 0), (44, 0, 0, 0)), 'leftThigh': R((0, 0, 0, 0), (10, 30, 0, 0), (18, -30, 0, 0), (26, 30, 0, 0), (34, -20, 0, 0), (44, 0, 0, 0))}))

# ================= hunters (rig: base hips chest neck head jaw tail armL/R foreL/R handL/R thighL/R shinL/R footL/R)
reg('re1r_hunter', M(30, 'pounce_rake', 34, [16, 22], 2, 6.5, 50, 4, 8, 1.2, 24, move=True, tick=[(8, 9, 1.05, 0.55)],
  clip={'hips': R((0, 0, 0, 0), (8, 14, 0, 0), (16, -12, 0, 0, SNAP), (34, 0, 0, 0)), 'chest': R((0, 0, 0, 0), (8, 22, 0, 0), (16, -30, 0, 0, SNAP), (24, -10, 0, 0), (34, 0, 0, 0)),
        'armL': R((0, 0, 0, 0), (8, 70, 0, 14), (16, -80, 0, -10, SNAP), (22, 20, 0, 10), (28, -60, 0, 0, SNAP), (34, 0, 0, 0)), 'armR': R((0, 0, 0, 0), (8, 70, 0, -14), (16, -80, 0, 10, SNAP), (22, -60, 0, 0, SNAP), (34, 0, 0, 0)),
        'head': R((0, 0, 0, 0), (8, 14, 0, 0), (16, -22, 0, 0, SNAP), (34, 0, 0, 0)), 'jaw': R((0, 0, 0, 0), (12, 34, 0, 0), (34, 0, 0, 0)), 'tail': R((0, 0, 0, 0), (8, -30, 0, 0), (16, 40, 0, 0, SNAP), (34, 0, 0, 0))},
  pos={'base': R((0, 0, 0, 0), (8, 0, 3, 3), (12, 0, -3, -6), (16, 0, -1, -8), (34, 0, 0, 0))}))
reg('re3r_hunter_beta', M(31, 'decapitation_slash', 30, [13], 0, 3.0, 40, 4, 13, 1.6, 16, tick=[(8, 12, 0.3, 0)],
  clip={'armR': R((0, 0, 0, 0), (7, -150, 0, 60), (13, -40, 0, -70, SNAP), (22, -10, 0, -20), (30, 0, 0, 0)), 'foreR': R((0, 0, 0, 0), (7, -30, 0, 0), (13, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (7, -60, 0, -30), (13, -30, 0, -10), (30, 0, 0, 0)),
        'chest': R((0, 0, 0, 0), (7, -6, 40, 0), (13, 18, -46, 0, SNAP), (30, 0, 0, 0)), 'hips': R((0, 0, 0, 0), (7, 0, 22, 0), (13, 0, -24, 0, SNAP), (30, 0, 0, 0)), 'head': R((0, 0, 0, 0), (7, -6, 20, 0), (13, 10, -24, 0), (30, 0, 0, 0)), 'tail': R((0, 0, 0, 0), (13, 0, 40, 0), (30, 0, 0, 0))}))
# ================= licker (rig: base torso neck1 neck2 head jaw t1..t6 armL/R foreL/R handL/R thighs)
reg('re2r_licker', M(30, 'frenzy_claws', 34, [8, 14, 20, 26], 0, 3.0, 36, 4, 4.5, 0.4, 30, move=True, tick=[(4, 7, 0.4, 0), (11, 13, 0.3, 0), (17, 19, 0.3, 0), (23, 25, 0.3, 0)],
  clip={'armL': R((0, 0, 0, 0), (4, 60, 0, 10), (8, -100, 0, -10, SNAP), (14, 40, 0, 0), (20, -100, 0, -10, SNAP), (26, 40, 0, 0), (34, 0, 0, 0)), 'armR': R((0, 0, 0, 0), (4, 20, 0, 0), (8, 60, 0, 0), (14, -100, 0, 10, SNAP), (20, 40, 0, 0), (26, -100, 0, 10, SNAP), (34, 0, 0, 0)),
        'torso': R((0, 0, 0, 0), (8, 0, -20, 0), (14, 0, 20, 0), (20, 0, -20, 0), (26, 0, 22, 0), (34, 0, 0, 0)), 'head': R((0, 0, 0, 0), (8, 6, 14, 0), (14, 6, -14, 0), (26, 6, -14, 0), (34, 0, 0, 0)), 'jaw': R((0, 0, 0, 0), (4, 28, 0, 0), (28, 28, 0, 0), (34, 0, 0, 0))}))
# ================= Mr.X rig (base torso neck head armL/R foreL/R handL/R thighL/R shinL/R)
reg('re2r_mr_x', M(30, 'door_smash_uppercut', 34, [16], 0, 3.4, 40, 4, 14, 1.0, 22, tick=[(10, 15, 0.32, 0)], hit=[('launch', 0, 0.85)],
  clip={'armR': R((0, 0, 0, 0), (10, 70, 0, 10), (16, -130, 0, -10, SNAP), (26, -60, 0, 0), (34, 0, 0, 0)), 'foreR': R((0, 0, 0, 0), (10, -80, 0, 0), (16, -20, 0, 0, SNAP), (34, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (10, -40, 0, -10), (16, 20, 0, 0), (34, 0, 0, 0)),
        'torso': R((0, 0, 0, 0), (10, 10, 24, 0), (16, -16, -26, 0, SNAP), (28, -4, -8, 0), (34, 0, 0, 0)), 'base': R((0, 0, 0, 0), (10, 4, 14, 0), (16, -6, -18, 0, SNAP), (34, 0, 0, 0)), 'head': R((0, 0, 0, 0), (16, -14, -10, 0), (34, 0, 0, 0))},
  pos={'base': R((0, 0, 0, 0), (10, 0, 3, 2), (16, 0, -2, -5, SNAP), (34, 0, 0, 0))}),
  M(31, 'shoulder_charge', 42, [22], 3, 9, 70, 3, 12, 2.4, 18, move=True, tick=[(8, 22, 0.42, 0)], hit=[('launch', 0, 0.45)],
  clip={'torso': R((0, 0, 0, 0), (10, 30, 0, 0), (22, 34, 0, 0, SNAP), (42, 0, 0, 0)), 'head': R((0, 0, 0, 0), (10, -20, 0, 0), (22, -24, 0, 0), (42, 0, 0, 0)), 'armR': R((0, 0, 0, 0), (10, 40, 0, 20), (22, -30, 0, 20), (42, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (10, -40, 0, -20), (22, 30, 0, -20), (42, 0, 0, 0)),
        'thighL': R((0, 0, 0, 0), (10, -34, 0, 0), (16, 34, 0, 0), (22, -34, 0, 0), (30, 30, 0, 0), (42, 0, 0, 0)), 'thighR': R((0, 0, 0, 0), (10, 34, 0, 0), (16, -34, 0, 0), (22, 34, 0, 0), (30, -30, 0, 0), (42, 0, 0, 0))}))
reg('re4r_el_gigante', M(30, 'club_sweep', 44, [24], 0, 6.0, 50, 4, 16, 2.6, 28, hit=[('aoe', 0, 4.5, 9, 1.6)],
  clip={'armR': R((0, 0, 0, 0), (14, -150, 0, 70), (24, -70, 0, -80, SNAP), (34, -30, 0, -20), (44, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (14, -60, 0, -40), (24, -30, 0, -20), (44, 0, 0, 0)), 'torso': R((0, 0, 0, 0), (14, -10, 50, 0), (24, 14, -60, 0, SNAP), (44, 0, 0, 0)), 'base': R((0, 0, 0, 0), (14, 0, 30, 0), (24, 0, -40, 0, SNAP), (44, 0, 0, 0)), 'head': R((0, 0, 0, 0), (14, -6, 28, 0), (24, 8, -30, 0), (44, 0, 0, 0))}),
  M(31, 'ground_quake', 44, [24], 0, 6.0, 80, 3, 12, 1.8, 20, hit=[('aoe', 0, 6, 12, 2.0)],
  clip={'armR': R((0, 0, 0, 0), (14, -170, 0, 20), (24, -10, 0, 8, SNAP), (44, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (14, -170, 0, -20), (24, -10, 0, -8, SNAP), (44, 0, 0, 0)), 'torso': R((0, 0, 0, 0), (14, -26, 0, 0), (24, 44, 0, 0, SNAP), (44, 0, 0, 0)), 'head': R((0, 0, 0, 0), (14, -20, 0, 0), (24, 22, 0, 0), (44, 0, 0, 0)),
        'thighL': R((0, 0, 0, 0), (24, -30, 0, 0), (44, 0, 0, 0)), 'thighR': R((0, 0, 0, 0), (24, -30, 0, 0), (44, 0, 0, 0))}, pos={'base': R((0, 0, 0, 0), (14, 0, -2, 2), (24, 0, 6, -3, SNAP), (44, 0, 0, 0))}))
reg('re4r_verdugo', M(30, 'blade_cross_slash', 34, [12, 22], 0, 3.4, 36, 4, 9, 1.0, 26, tick=[(7, 11, 0.3, 0), (17, 21, 0.3, 0)],
  clip={'armR': R((0, 0, 0, 0), (7, -140, 0, 40), (12, -40, 0, -60, SNAP), (17, -120, 0, -30), (22, -50, 0, 50, SNAP), (34, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (7, -120, 0, -40), (12, -130, 0, 20), (17, -140, 0, 30), (22, -40, 0, -50, SNAP), (34, 0, 0, 0)), 'torso': R((0, 0, 0, 0), (7, -4, 36, 0), (12, 14, -40, 0, SNAP), (17, 6, -30, 0), (22, 16, 40, 0, SNAP), (34, 0, 0, 0)), 'head': R((0, 0, 0, 0), (12, 8, -16, 0), (22, 8, 16, 0), (34, 0, 0, 0))}),
  M(31, 'phantom_dash', 34, [16], 3, 8.5, 60, 3, 10, 1.6, 20, move=True, tick=[(6, 15, 0.7, 0)],
  clip={'torso': R((0, 0, 0, 0), (6, 30, 0, 0), (16, 36, 0, 0, SNAP), (34, 0, 0, 0)), 'armR': R((0, 0, 0, 0), (6, 60, 0, 10), (16, -90, 0, 0, SNAP), (34, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (6, 60, 0, -10), (16, -90, 0, 0, SNAP), (34, 0, 0, 0)), 'head': R((0, 0, 0, 0), (6, -24, 0, 0), (34, 0, 0, 0)),
        'thighL': R((0, 0, 0, 0), (6, -40, 0, 0), (12, 40, 0, 0), (16, -34, 0, 0), (34, 0, 0, 0)), 'thighR': R((0, 0, 0, 0), (6, 40, 0, 0), (12, -40, 0, 0), (16, 34, 0, 0), (34, 0, 0, 0))}))
# ================= nemesis rig (base torso neck head armL/R foreL/R handL/R thighs)
reg('re3r_nemesis', M(30, 'crushing_grab_slam', 50, [18, 34], 0, 2.8, 60, 3, 15, 1.2, 40, tick=[(10, 17, 0.2, 0)], hit=[('launch', 1, -0.6)],
  clip={'armL': R((0, 0, 0, 0), (10, -90, 0, -10), (18, -100, 0, 20, SNAP), (26, -170, 0, 0), (34, -20, 0, 0, SNAP), (50, 0, 0, 0)), 'armR': R((0, 0, 0, 0), (10, -90, 0, 10), (18, -100, 0, -20, SNAP), (26, -170, 0, 0), (34, -20, 0, 0, SNAP), (50, 0, 0, 0)), 'foreL': R((0, 0, 0, 0), (18, -20, 0, 0), (26, -30, 0, 0), (50, 0, 0, 0)), 'foreR': R((0, 0, 0, 0), (18, -20, 0, 0), (26, -30, 0, 0), (50, 0, 0, 0)),
        'torso': R((0, 0, 0, 0), (10, 16, 0, 0), (26, -22, 0, 0), (34, 40, 0, 0, SNAP), (50, 0, 0, 0)), 'head': R((0, 0, 0, 0), (26, -18, 0, 0), (34, 22, 0, 0), (50, 0, 0, 0))}, pos={'base': R((0, 0, 0, 0), (34, 0, 5, -4, SNAP), (50, 0, 0, 0))}),
  M(31, 'barrage_swing', 40, [14, 26], 0, 3.4, 40, 4, 11, 1.8, 30, hit=[('aoe', 1, 3.2, 6, 1.4)],
  clip={'armR': R((0, 0, 0, 0), (8, -60, 0, 90), (14, -50, 0, -80, SNAP), (20, -60, 0, 90), (26, -50, 0, -90, SNAP), (40, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (8, -60, 0, -50), (14, -40, 0, 10), (26, -40, 0, 30), (40, 0, 0, 0)), 'torso': R((0, 0, 0, 0), (8, 6, 50, 0), (14, 12, -56, 0, SNAP), (20, 6, 50, 0), (26, 12, -60, 0, SNAP), (40, 0, 0, 0)), 'head': R((0, 0, 0, 0), (14, 6, -20, 0), (26, 6, -22, 0), (40, 0, 0, 0))}))
# ================= ganado rig (base torso neck head armA... armL/R foreL/R handL/R thighs)
reg('re4r_ganado', M(30, 'overhead_chop', 30, [15], 0, 2.6, 30, 4, 7, 0.9, 20, tick=[(9, 14, 0.18, 0)],
  clip={'armR': R((0, 0, 0, 0), (9, -165, 0, 10), (15, -30, 0, -6, SNAP), (24, -10, 0, 0), (30, 0, 0, 0)), 'foreR': R((0, 0, 0, 0), (9, -30, 0, 0), (15, -4, 0, 0, SNAP), (30, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (9, -60, 0, -20), (15, -30, 0, -10), (30, 0, 0, 0)), 'torso': R((0, 0, 0, 0), (9, -16, 0, 0), (15, 34, 0, 0, SNAP), (24, 14, 0, 0), (30, 0, 0, 0)), 'head': R((0, 0, 0, 0), (9, -14, 0, 0), (15, 22, 0, 0), (30, 0, 0, 0))}),
  M(31, 'wrestle_bite', 44, [14, 28, 38], 0, 1.7, 44, 3, 4, 0.2, 40, hit=[('slow', 0, 50, 2)],
  clip={'armR': R((0, 0, 0, 0), (10, -100, 0, -20), (14, -90, 0, 30, SNAP), (38, -90, 0, 34), (44, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (10, -100, 0, 20), (14, -90, 0, -30, SNAP), (38, -90, 0, -34), (44, 0, 0, 0)), 'torso': R((0, 0, 0, 0), (10, -8, 0, 0), (14, 24, 0, 0, SNAP), (28, 18, 0, 0), (38, 26, 0, 0, SNAP), (44, 0, 0, 0)), 'head': R((0, 0, 0, 0), (10, -20, 0, 0), (14, 24, 0, 0, SNAP), (28, 22, 0, 0), (38, 28, 0, 0, SNAP), (44, 0, 0, 0))}))
reg('re4r_krauser', M(30, 'blade_flurry', 36, [10, 18, 26], 0, 3.0, 36, 4, 6.5, 0.8, 32, tick=[(6, 9, 0.3, 0), (14, 17, 0.25, 0), (22, 25, 0.25, 0)],
  clip={'armR': R((0, 0, 0, 0), (5, -110, 0, 50), (10, -40, 0, -60, SNAP), (14, -110, 0, -40), (18, -40, 0, 60, SNAP), (22, -140, 0, 20), (26, -20, 0, -20, SNAP), (36, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (10, -50, 0, -30), (26, -40, 0, 20), (36, 0, 0, 0)), 'torso': R((0, 0, 0, 0), (5, 4, 36, 0), (10, 14, -40, 0, SNAP), (18, 14, 44, 0, SNAP), (26, 22, -20, 0, SNAP), (36, 0, 0, 0)), 'head': R((0, 0, 0, 0), (10, 6, -14, 0), (18, 6, 14, 0), (36, 0, 0, 0))}),
  M(31, 'knife_lunge', 30, [14], 2, 6.2, 44, 3, 11, 1.2, 20, move=True, tick=[(6, 13, 0.7, 0)],
  clip={'armR': R((0, 0, 0, 0), (6, -50, 20, 0), (14, -88, 0, 0, SNAP), (30, 0, 0, 0)), 'torso': R((0, 0, 0, 0), (6, 8, 30, 0), (14, 26, -24, 0, SNAP), (30, 0, 0, 0)), 'armL': R((0, 0, 0, 0), (6, -60, 0, -30), (14, 20, 0, -20), (30, 0, 0, 0)), 'thighR': R((0, 0, 0, 0), (6, 30, 0, 0), (14, -50, 0, 0, SNAP), (30, 0, 0, 0)), 'thighL': R((0, 0, 0, 0), (6, -20, 0, 0), (14, 30, 0, 0), (30, 0, 0, 0))}))
# ================= salazar / plant42 rig (base stem1 stem2 head)
reg('re4r_salazar', M(30, 'royal_crush', 40, [22], 0, 6.5, 60, 4, 14, 1.6, 24, hit=[('aoe', 0, 4.5, 8, 1.4)],
  clip={'stem1': R((0, 0, 0, 0), (12, -24, 0, 0), (22, 36, 0, 0, SNAP), (40, 0, 0, 0)), 'stem2': R((0, 0, 0, 0), (12, -30, 0, 0), (22, 46, 0, 0, SNAP), (40, 0, 0, 0)), 'head': R((0, 0, 0, 0), (12, -20, 0, 0), (22, 30, 0, 0, SNAP), (40, 0, 0, 0))}),
  M(31, 'sweeping_tendrils', 44, [14, 28], 0, 6.5, 50, 3, 8, 1.4, 30, hit=[('aoe', 0, 5.5, 6, 1.2), ('aoe', 1, 5.5, 6, 1.2)],
  clip={'stem1': R((0, 0, 0, 0), (10, 0, 40, 0), (14, 6, -50, 0, SNAP), (24, 0, 50, 0), (28, 6, -60, 0, SNAP), (44, 0, 0, 0)), 'stem2': R((0, 0, 0, 0), (10, 0, 36, 0), (14, 8, -44, 0, SNAP), (28, 8, -50, 0, SNAP), (44, 0, 0, 0)), 'head': R((0, 0, 0, 0), (14, 0, -30, 0), (28, 0, -34, 0), (44, 0, 0, 0))}))

ENT = {'re1r_zombie': 'zombie.ZombieEntity', 're2r_zombie': 'zombie.ZombieEntity', 're3r_zombie': 'zombie.ZombieEntity', 're3r_pale_head': 'zombie.ZombieEntity', 're4r_regenerador': 'zombie.ZombieEntity', 're4r_armadura': 'zombie.ZombieEntity', 're4r_iron_maiden': 'zombie.ZombieEntity',
  're1r_hunter': 'hunter.HunterEntity', 're3r_hunter_beta': 'hunter.HunterEntity', 're2r_licker': 'licker.LickerEntity', 're2r_mr_x': 'mrx.MrXEntity', 're4r_el_gigante': 'mrx.MrXEntity', 're4r_verdugo': 'mrx.MrXEntity',
  're3r_nemesis': 'nemesis.NemesisEntity', 're4r_ganado': 'ganado.GanadoEntity', 're4r_krauser': 'ganado.GanadoEntity', 're4r_salazar': 'plant42.Plant42Entity'}

def f(x): return ('%sf' % x) if not float(x).is_integer() else ('%d' % x if False else '%sf' % x)

def entity_extra(cid):
    out = ['\n    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)']
    names = []
    for m in MOV[cid]:
        n = 'X%d' % m.id; names.append(n)
        out.append('    private static final AttackMove %s = new AttackMove(%d, "%s", %d, new int[]{%s}, %s, %s, %d, %d).dmg(%sf).kb(%s)%s%s;' % (
            n, m.id, m.name, m.dur, ', '.join(map(str, m.hits)), f(m.mn), f(m.mx), m.cd, m.w, m.dmg, m.kb, '.face(%d)' % m.face if m.face else '', '.move()' if m.move else ''))
    out.append('    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(%s);' % ', '.join(names))
    out.append('    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }')
    tk = ['        if (m.id == %d && t >= %d && t <= %d) lunge(%s, %s);' % (m.id, a, b, f(h), f(v)) for m in MOV[cid] for (a, b, h, v) in m.tick]
    out.append('    @Override\n    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {\n%s\n        super.onMoveTick(m, t, target);\n    }' % '\n'.join(tk))
    ht = []
    for m in MOV[cid]:
        for e in m.hit:
            if e[0] == 'launch': ht.append('        if (m.id == %d && hitIndex == %d) com.reevil.entity.RemakeMoves.launch(target, %s);' % (m.id, e[1], f(e[2])))
            elif e[0] == 'aoe': ht.append('        if (m.id == %d && hitIndex == %d) com.reevil.entity.RemakeMoves.aoe(this, %s, %sf, %s);' % (m.id, e[1], f(e[2]), e[3], e[4]))
            elif e[0] == 'slow': ht.append('        if (m.id == %d && hitIndex == %d && target != null && distanceTo(target) < m.maxRange + 1.2) target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, %d, %d), this);' % (m.id, e[1], e[2], e[3]))
    # aoe moves: replace default single hit
    skip = ' || '.join('m.id == %d' % m.id for m in MOV[cid] if any(e[0] == 'aoe' for e in m.hit))
    body = '        %s\n' % ('if (!(%s)) super.onMoveHit(m, hitIndex, target);' % skip if skip else 'super.onMoveHit(m, hitIndex, target);')
    out.append('    @Override\n    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {\n%s%s\n    }' % (body, '\n'.join(ht)))
    return '\n'.join(out) + '\n'

def rows_java(bone, rows, dur, kind):
    rr = list(rows)
    if rr[0][0] != 0: rr.insert(0, (0, 0, 0, 0))
    if rr[-1][0] != dur: rr.append((dur, 0, 0, 0))
    cells = []
    for r in rr:
        t = r[0] / dur; e = EASE[r[4]] if len(r) > 4 else None
        cells.append('k(%sf, %s, %s, %s%s)' % (round(t, 4), r[1], r[2], r[3], (', %s' % ['LIN', 'SM', 'IN', 'OUT', 'SNAP'][e]) if e is not None else ''))
    return '        %s(b, "%s", %s);' % (kind, bone, ', '.join(cells))

def clips_java():
    L = ['package com.reevil.client.remake;', '', 'import com.reevil.anim.AnimClip;', '', 'import static com.reevil.anim.K.*;', '',
         '/** Generated by tools/remake_moves.py: dedicated clips for the remake creatures\' own moves (ids 30+). */', 'public final class RemakeClips {', '    private RemakeClips() {}']
    for cid, ms in MOV.items():
        for m in ms:
            L.append('    private static final AnimClip %s_%d = %s_%d();' % (cid.upper(), m.id, cid, m.id) if False else '')
    for cid, ms in MOV.items():
        for m in ms:
            L.append('    private static AnimClip %s_%d() {\n        AnimClip.Builder b = AnimClip.create();' % (cid, m.id))
            for bone, rows in m.clip.items(): L.append(rows_java(bone, rows, m.dur, 'rot'))
            for bone, rows in m.pos.items(): L.append(rows_java(bone, rows, m.dur, 'pos'))
            L.append('        return b.build();\n    }')
    for cid, ms in MOV.items():
        L.append('    private static final AnimClip[] %s = {%s};' % (cid.upper(), ', '.join('%s_%d()' % (cid, m.id) for m in ms)))
        L.append('    private static final int[] %s_IDS = {%s};' % (cid.upper(), ', '.join(str(m.id) for m in ms)))
        L.append('    public static AnimClip %s(int id) { for (int i = 0; i < %s_IDS.length; i++) if (%s_IDS[i] == id) return %s[i]; return null; }' % (cid, cid.upper(), cid.upper(), cid.upper()))
    L.append('}')
    return '\n'.join(l for l in L if l is not None) + '\n'

def write_clips():
    os.makedirs(J + 'client/remake', exist_ok=True)
    open(J + 'client/remake/RemakeClips.java', 'w').write(clips_java())
