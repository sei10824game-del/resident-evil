"""Original-game abilities the base classes lacked: extra moves (ids 40+), mutations/passives, clips and mutation visuals.
Emits entity/CanonData.java, client/canon/CanonClips.java, client/canon/CanonVisuals.java.  Run: python3 tools/canon_moves.py"""
import os
J = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/java/com/reevil/')
EASE = ['LIN', 'SM', 'IN', 'OUT', 'SNAP']

RIGS = {
 'mrx':    dict(torso='torso', base='base', head='head', jaw=None, aL='armL', aR='armR', fL='foreL', fR='foreR', tL='thighL', tR='thighR'),
 'nemesis': dict(torso='torso', base='base', head='head', jaw=None, aL='armL', aR='armR', fL='foreL', fR='foreR', tL='thighL', tR='thighR'),
 'zombie': dict(torso='chest', base='body', head='head', jaw='jaw', aL='leftArm', aR='rightArm', fL='leftForearm', fR='rightForearm', tL='leftThigh', tR='rightThigh'),
 'licker': dict(torso='torso', base='base', head='head', jaw='jaw', aL='armL', aR='armR', fL='foreL', fR='foreR', tL='thighL', tR='thighR'),
 'hunter': dict(torso='chest', base='hips', head='head', jaw='jaw', aL='armL', aR='armR', fL='foreL', fR='foreR', tL='thighL', tR='thighR'),
 'moth':   dict(torso='body', base=None, head='head', jaw=None, aL='wingL', aR='wingR', fL=None, fR=None, tL=None, tR=None),
 'plant':  dict(torso='stem1', base=None, head='head', jaw=None, aL='stem2', aR=None, fL=None, fR=None, tL=None, tR=None),
 'neptune': dict(torso='body', base=None, head='head', jaw='jaw', aL=None, aR=None, fL=None, fR=None, tL=None, tR=None),
 'cerb':   dict(torso='chest', base='body', head='head', jaw='jaw', aL='flUpper', aR='frUpper', fL=None, fR=None, tL='hlThigh', tR='hrThigh'),
 'yawn':   dict(torso='body', base=None, head='head', jaw='jaw', aL=None, aR=None, fL=None, fR=None, tL=None, tR=None),
}
RIGS['ganado'] = RIGS['mrx']

def rows(*r): return list(r)

def arche(kind, rig, dur, hits):
    """clip rows (ticks) for an archetype on a rig. Bones the rig lacks are skipped."""
    g = RIGS[rig]; h = hits[0] if hits else dur // 2; c = {}
    def put(slot, rr):
        b = g.get(slot)
        if b: c[b] = c.get(b, []) + rr if False else rr
    def row(slot, *rr): put(slot, list(rr))
    S = 'SNAP'
    if kind == 'slam':
        row('aL', (0, 0, 0, 0), (h - 7, -165, 0, -14), (h, -12, 0, -6, S), (h + 10, -6, 0, 0), (dur, 0, 0, 0)); row('aR', (0, 0, 0, 0), (h - 7, -165, 0, 14), (h, -12, 0, 6, S), (h + 10, -6, 0, 0), (dur, 0, 0, 0))
        row('torso', (0, 0, 0, 0), (h - 7, -26, 0, 0), (h, 42, 0, 0, S), (dur, 0, 0, 0)); row('head', (0, 0, 0, 0), (h - 7, -20, 0, 0), (h, 22, 0, 0), (dur, 0, 0, 0))
        row('tL', (0, 0, 0, 0), (h, -28, 0, 0), (dur, 0, 0, 0)); row('tR', (0, 0, 0, 0), (h, -28, 0, 0), (dur, 0, 0, 0))
        if g['base']: c.setdefault('@pos', {})[g['base']] = [(0, 0, 0, 0), (h - 7, 0, -2, 2), (h, 0, 6, -3, S), (dur, 0, 0, 0)]
    elif kind == 'thrust':
        row('aR', (0, 0, 0, 0), (h - 6, -40, 24, 0), (h, -88, -6, 0, S), (h + 10, -50, 0, 0), (dur, 0, 0, 0)); row('aL', (0, 0, 0, 0), (h - 6, -50, -20, 0), (h, -20, 0, 0), (dur, 0, 0, 0))
        row('torso', (0, 0, 0, 0), (h - 6, -4, 30, 0), (h, 20, -26, 0, S), (dur, 0, 0, 0)); row('head', (0, 0, 0, 0), (h, 10, -14, 0), (dur, 0, 0, 0))
        row('tR', (0, 0, 0, 0), (h - 6, 26, 0, 0), (h, -30, 0, 0), (dur, 0, 0, 0)); row('tL', (0, 0, 0, 0), (h - 6, -20, 0, 0), (h, 22, 0, 0), (dur, 0, 0, 0))
    elif kind == 'sweep':
        row('aR', (0, 0, 0, 0), (h - 8, -50, 0, 85), (h, -40, 0, -85, S), (h + 12, -20, 0, -20), (dur, 0, 0, 0)); row('aL', (0, 0, 0, 0), (h - 8, -40, 0, -50), (h, -30, 0, 20), (dur, 0, 0, 0))
        row('torso', (0, 0, 0, 0), (h - 8, 4, 52, 0), (h, 12, -60, 0, S), (dur, 0, 0, 0)); row('head', (0, 0, 0, 0), (h, 6, -20, 0), (dur, 0, 0, 0)); row('base', (0, 0, 0, 0), (h - 8, 0, 26, 0), (h, 0, -30, 0, S), (dur, 0, 0, 0))
    elif kind == 'flurry':
        r1 = [(0, 0, 0, 0)]; r2 = [(0, 0, 0, 0)]; t1 = [(0, 0, 0, 0)]
        for i, t in enumerate(hits):
            (r1 if i % 2 == 0 else r2).extend([(t - 4, -30, 0, 10), (t, -98, 0, -10, S)])
            t1.extend([(t, 8, -22 if i % 2 == 0 else 22, 0, S)])
        r1.append((dur, 0, 0, 0)); r2.append((dur, 0, 0, 0)); t1.append((dur, 0, 0, 0))
        put('aR', r1); put('aL', r2); put('torso', t1)
        if len(hits) < 2: put('aL', [(0, 0, 0, 0), (dur, 0, 0, 0)])
        row('head', (0, 0, 0, 0), (hits[0], 6, 12, 0), (dur, 0, 0, 0))
        row('jaw', (0, 0, 0, 0), (hits[0] - 4, 26, 0, 0), (dur - 4, 26, 0, 0), (dur, 0, 0, 0))
    elif kind == 'dash':
        row('torso', (0, 0, 0, 0), (6, 28, 0, 0), (h, 34, 0, 0, S), (dur, 0, 0, 0)); row('head', (0, 0, 0, 0), (6, -20, 0, 0), (dur, 0, 0, 0))
        row('aR', (0, 0, 0, 0), (6, 40, 0, 16), (h, -86, 0, 0, S), (dur, 0, 0, 0)); row('aL', (0, 0, 0, 0), (6, 40, 0, -16), (h, -86, 0, 0, S), (dur, 0, 0, 0))
        q = max(2, (h - 6) // 2)
        row('tL', (0, 0, 0, 0), (6, -34, 0, 0), (6 + q, 34, 0, 0), (h, -30, 0, 0), (dur, 0, 0, 0)); row('tR', (0, 0, 0, 0), (6, 34, 0, 0), (6 + q, -34, 0, 0), (h, 30, 0, 0), (dur, 0, 0, 0))
    elif kind == 'cast':
        row('aL', (0, 0, 0, 0), (h - 8, -120, 0, -60), (h, -150, 0, -70, S), (dur - 6, -90, 0, -40), (dur, 0, 0, 0)); row('aR', (0, 0, 0, 0), (h - 8, -120, 0, 60), (h, -150, 0, 70, S), (dur - 6, -90, 0, 40), (dur, 0, 0, 0))
        row('torso', (0, 0, 0, 0), (h - 8, -16, 0, 0), (h, -24, 0, 0), (dur, 0, 0, 0)); row('head', (0, 0, 0, 0), (h - 8, -26, 0, 0), (h, 10, 0, 0, S), (dur, 0, 0, 0)); row('jaw', (0, 0, 0, 0), (h - 6, 40, 0, 0), (dur - 6, 40, 0, 0), (dur, 0, 0, 0))
        if g['base']: c.setdefault('@pos', {})[g['base']] = [(0, 0, 0, 0), (h - 8, 0, 1, 0), (h, 0, -2, 0), (dur, 0, 0, 0)]
    elif kind == 'aimhold':
        row('aR', (0, 0, 0, 0), (6, -88, -6, 0), (dur - 6, -88, -6, 0), (dur, 0, 0, 0)); row('aL', (0, 0, 0, 0), (6, -80, 14, 0), (dur - 6, -80, 14, 0), (dur, 0, 0, 0))
        row('torso', (0, 0, 0, 0), (6, 6, -14, 0), (dur - 6, 6, -14, 0), (dur, 0, 0, 0)); row('head', (0, 0, 0, 0), (6, 4, 10, 0), (dur - 6, 4, 10, 0), (dur, 0, 0, 0))
    elif kind == 'bite':
        row('torso', (0, 0, 0, 0), (h - 6, -18, 0, 0), (h, 28, 0, 0, S), (h + 10, 10, 0, 0), (dur, 0, 0, 0)); row('head', (0, 0, 0, 0), (h - 6, -22, 0, 0), (h, 20, 0, 0, S), (dur, 0, 0, 0)); row('jaw', (0, 0, 0, 0), (h - 6, 42, 0, 0), (h, 3, 0, 0, S), (dur, 0, 0, 0))
        row('aL', (0, 0, 0, 0), (h - 6, -70, 0, -20), (h, -100, 0, -10, S), (dur, 0, 0, 0)); row('aR', (0, 0, 0, 0), (h - 6, -70, 0, 20), (h, -100, 0, 10, S), (dur, 0, 0, 0))
    elif kind == 'spit':
        row('head', (0, 0, 0, 0), (h - 7, -34, 0, 0), (h, 18, 0, 0, S), (dur, 0, 0, 0)); row('jaw', (0, 0, 0, 0), (h - 7, 40, 0, 0), (h + 4, 40, 0, 0), (dur, 0, 0, 0)); row('torso', (0, 0, 0, 0), (h - 7, -20, 0, 0), (h, 16, 0, 0, S), (dur, 0, 0, 0))
        row('aL', (0, 0, 0, 0), (h - 7, -30, 0, -20), (dur, 0, 0, 0)); row('aR', (0, 0, 0, 0), (h - 7, -30, 0, 20), (dur, 0, 0, 0))
    elif kind == 'grab':
        ts = [(0, 0, 0, 0)]
        for t in hits: ts += [(t - 4, 6, 0, 0), (t, 22, 0, 0, S)]
        ts.append((dur, 0, 0, 0)); put('torso', ts)
        row('aL', (0, 0, 0, 0), (hits[0] - 4, -90, 0, -22), (hits[0], -92, 0, 26, S), (hits[-1], -92, 0, 30), (dur, 0, 0, 0)); row('aR', (0, 0, 0, 0), (hits[0] - 4, -90, 0, 22), (hits[0], -92, 0, -26, S), (hits[-1], -92, 0, -30), (dur, 0, 0, 0))
        row('head', (0, 0, 0, 0), (hits[0], 20, 0, 0), (hits[-1], 24, 0, 0), (dur, 0, 0, 0)); row('jaw', (0, 0, 0, 0), (hits[0] - 4, 30, 0, 0), (hits[-1], 6, 0, 0), (dur, 0, 0, 0))
    elif kind == 'kick':
        row('tR', (0, 0, 0, 0), (h - 6, -100, 0, 0), (h, -92, 0, 0, S), (h + 8, -30, 0, 0), (dur, 0, 0, 0)); row('tL', (0, 0, 0, 0), (h, 12, 0, 0), (dur, 0, 0, 0))
        row('torso', (0, 0, 0, 0), (h - 6, 4, 20, 0), (h, 18, -26, 0, S), (dur, 0, 0, 0)); row('aL', (0, 0, 0, 0), (h - 6, -20, 0, -60), (h, -10, 0, -85), (dur, 0, 0, 0)); row('aR', (0, 0, 0, 0), (h - 6, -10, 0, 70), (h, 0, 0, 85), (dur, 0, 0, 0))
    else: raise ValueError(kind)
    return c

class M:
    def __init__(s, ent, rig, id, name, dur, hits, mn, mx, cd, w, dmg, kb, a, tick=(), hit=None, nodef=False, can=None, move=False, start=None, end=None):
        s.__dict__.update(locals()); s.hit = hit or {}

DEFS = []
def mv(*a, **k): DEFS.append(M(*a, **k))
PO = lambda t=100, a=1: 'effect(net.minecraft.world.effect.MobEffects.POISON, %d, %d)' % (t, a)
SLOW = lambda t=60, a=2: 'effect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, %d, %d)' % (t, a)
INV = 'selfEffect(net.minecraft.world.effect.MobEffects.INVISIBILITY, %d, 0)'
SPD = 'selfEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, %d, 1)'

# ---- RE4
mv('novistador', 'moth', 40, 'cloak_vanish', 24, [], 0, 30, 180, 2, 0, 0, 'cast', end=[INV % 200, SPD % 200])
mv('colmillos', 'cerb', 40, 'spike_burst', 36, [18], 0, 4, 70, 3, 7, 1.2, 'bite', hit={0: ['aoe(4, 7f, 1.2)', PO(60, 0)]}, nodef=True)
mv('armadura', 'zombie', 40, 'sword_cleave', 36, [18], 0, 3.8, 44, 4, 12, 1.4, 'slam', tick=[(12, 17, 'lunge(0.2, 0)')])
mv('zealot', 'ganado', 40, 'sickle_rush', 34, [18], 2, 7, 50, 3, 7, 1.0, 'dash', tick=[(6, 18, 'lunge(0.5, 0)')])
mv('krauser', 'ganado', 40, 'arm_blade_cleave', 36, [18], 0, 4, 44, 4, 14, 1.4, 'sweep', can='mut(1)', tick=[(12, 17, 'lunge(0.25, 0)')])
mv('krauser', 'ganado', 41, 'stealth_cloak', 24, [], 0, 12, 150, 2, 0, 0, 'cast', can='mut(1)', end=[INV % 160, SPD % 160])
mv('salazar', 'plant', 40, 'spawn_larvae', 44, [22], 0, 10, 200, 2, 0, 0, 'cast', hit={0: ["summon(\"larva\", 3)"]}, nodef=True)
mv('verdugo', 'mrx', 40, 'rend_grab', 52, [14, 26, 38], 0, 2.2, 60, 3, 5, 0, 'grab', hit={0: [SLOW(60, 3)]})
# ---- RE5
mv('uroboros', 'plant', 40, 'tendril_mass_slam', 44, [24], 0, 7, 60, 4, 12, 1.8, 'slam', hit={0: ['aoe(5.5, 10f, 1.6)']}, nodef=True)
mv('uroboros', 'plant', 41, 'virus_burst', 48, [24], 0, 8, 90, 3, 4, 0, 'cast', hit={0: ['aoe(8, 4f, 0.8)', PO(100, 1)]}, nodef=True)
mv('uroboros', 'plant', 42, 'tendril_pull', 40, [18], 4, 12, 70, 3, 5, 0, 'thrust', hit={0: ['pull(2.0)']})
mv('wesker', 'mrx', 40, 'phantom_step_strike', 20, [11], 3, 9, 30, 3, 10, 1.3, 'thrust', tick=[(3, 3, 'blink()')])
mv('wesker', 'mrx', 41, 'lava_tentacle_lash', 27, [11, 18], 0, 7, 30, 4, 9, 1.2, 'sweep', can='mut(1)', hit={0: ['aoe(6, 8f, 1.4)', 'fire(4)'], 1: ['aoe(6, 8f, 1.4)']}, nodef=True)
mv('wesker', 'mrx', 42, 'charge_rush', 24, [13], 3, 10, 40, 3, 14, 2.4, 'dash', can='mut(1)', tick=[(4, 13, 'lunge(0.7, 0)')], hit={0: ['launch(0.45)']})
mv('jill', 'mrx', 40, 'flip_kick', 30, [14], 0, 3.4, 40, 3, 9, 1.6, 'kick', tick=[(8, 13, 'lunge(0.3, 0)')])
# ---- RE6
mv('napad', 'licker', 40, 'scythe_flurry', 36, [10, 16, 22, 28], 0, 3.2, 36, 4, 5, 0.4, 'flurry')
mv('ogroman', 'mrx', 40, 'charging_smash', 42, [24], 3, 9, 70, 3, 14, 2.4, 'dash', tick=[(8, 24, 'lunge(0.45, 0)')], hit={0: ['launch(0.5)']})
mv('ogroman', 'mrx', 41, 'overhead_pound', 44, [24], 0, 5.5, 60, 3, 12, 1.8, 'slam', hit={0: ['aoe(6, 11f, 1.8)']}, nodef=True)
mv('simmons', 'nemesis', 40, 'mutant_leap_slam', 40, [24], 3, 9, 70, 3, 13, 2.0, 'dash', tick=[(8, 22, 'lunge(0.55, 0.3)')], hit={0: ['aoe(4, 9f, 1.6)']}, nodef=True)
mv('haos', 'plant', 40, 'absorb_pull', 44, [20], 3, 10, 80, 3, 6, 0, 'cast', hit={0: ['pull(2.4)', SLOW(60, 1)]})
mv('haos', 'plant', 41, 'mass_slam', 44, [24], 0, 6, 60, 3, 10, 1.6, 'slam', hit={0: ['aoe(5, 9f, 1.5)']}, nodef=True)
mv('ustanak', 'mrx', 40, 'chain_hook_pull', 40, [18], 4, 10, 70, 3, 6, 0, 'thrust', hit={0: ['pull(1.8)', SLOW(40, 1)]})
# ---- RE7
mv('molded', 'ganado', 40, 'blade_arm_slash', 30, [14], 0, 3.2, 34, 3, 8, 1.0, 'thrust')
mv('molded', 'ganado', 41, 'crawl_lunge', 34, [18], 2, 7, 50, 3, 7, 1.0, 'dash', tick=[(6, 18, 'lunge(0.5, 0)')])
mv('jack_baker', 'mrx', 40, 'door_slam_rush', 40, [22], 3, 8, 60, 3, 12, 2.2, 'dash', tick=[(6, 22, 'lunge(0.5, 0)')], hit={0: ['launch(0.45)']})
mv('jack_baker', 'mrx', 41, 'headlock_grab', 56, [16, 30, 44], 0, 2, 60, 3, 4, 0, 'grab', hit={0: [SLOW(70, 3)]})
mv('eveline', 'nemesis', 40, 'psychic_shove', 34, [16], 0, 9, 60, 3, 5, 0, 'cast', hit={0: ['aoe(8, 5f, 2.6)']}, nodef=True)
mv('eveline', 'nemesis', 41, 'summon_molded', 40, [20], 0, 20, 160, 2, 0, 0, 'cast', can='hpBelow(0.8f)', hit={0: ['summon("molded", 2)']}, nodef=True)
# ---- RE8
mv('lycan', 'hunter', 40, 'frenzy_rake', 36, [10, 16, 22, 28], 0, 3, 40, 4, 5, 0.5, 'flurry')
mv('lycan', 'hunter', 41, 'howl_summon', 48, [24], 0, 30, 220, 1, 0, 0, 'cast', can='hpBelow(0.7f)', hit={0: ['summon("lycan", 1)']}, nodef=True)
mv('dimitrescu', 'mrx', 40, 'blood_lunge', 36, [18], 3, 8, 50, 3, 11, 0.8, 'dash', tick=[(6, 18, 'lunge(0.6, 0)')], hit={0: ['heal(8f)']})
mv('dimitrescu', 'mrx', 41, 'vault_kick', 30, [14], 0, 3.6, 40, 3, 9, 1.8, 'kick', tick=[(8, 13, 'lunge(0.3, 0)')])
mv('heisenberg', 'mrx', 40, 'magnet_pull', 36, [16], 4, 12, 70, 3, 4, 0, 'cast', hit={0: ['pull(2.2)']})
mv('heisenberg', 'mrx', 41, 'hammer_toss', 40, [20], 4, 14, 70, 2, 13, 0.6, 'thrust', hit={0: ['shot("arrow", 13f)']}, nodef=True)
mv('moreau', 'neptune', 40, 'toxic_spit', 36, [16], 4, 14, 60, 4, 4, 0, 'spit', hit={0: ['shot("poison", 4f)']}, nodef=True)
mv('miranda', 'nemesis', 40, 'wing_dive', 40, [22], 4, 12, 70, 3, 13, 2.0, 'dash', tick=[(6, 22, 'lunge(0.6, 0.3)')], hit={0: ['aoe(3, 6f, 1.4)']}, nodef=True)
mv('miranda', 'nemesis', 41, 'shard_volley', 42, [14, 20, 26], 5, 16, 60, 3, 6, 0, 'cast', hit={i: ['shot("slow", 6f)'] for i in range(3)}, nodef=True)
# ---- RE1-3 gaps
mv('grave_digger', 'yawn', 40, 'earth_burst', 44, [24], 0, 6, 80, 3, 8, 1.6, 'bite', hit={0: ['aoe(5, 8f, 1.6)']}, nodef=True)

PASSIVES0 = {'javo': ['mutateAt(0.65f, 1, 2, 3, 4)'], 'krauser': ['mutateAt(0.5f, 1)'], 'wesker': ['mutateAt(0.3f, 1)'], 'mendez': ['mutateAt(0.5f, 1)'], 'regenerador': ['regen(0.01f)'], 're4r_regenerador': ['regen(0.01f)']}
# entity -> {mut stage: [part paths]} (parts exist in the model; shown only while getMut()==stage)
VIS = {'wesker': [('base/torso/uroT%d' % i, 'mut == 1') for i in (1, 2, 3, 4)]}
SPIN = {}


# ===================================================== canon v2 (wiki-verified, see CANON_NOTES.md)
REPL = set(); DMGS = {}; HURTS = {}; DEATHS = {}
PASSIVES = dict(PASSIVES0); PASSIVES.pop('javo', None)
BLIND = 'effect(net.minecraft.world.effect.MobEffects.BLINDNESS, 80, 0)'
# ---- Gnezdo: swarm woman + hidden Queen
REPL.add('gnezdo')
mv('gnezdo', 'mrx', 40, 'swarm_slash', 30, [14], 0, 2.8, 30, 4, 5, 0.6, 'sweep', hit={0: [PO(60, 0)]})
mv('gnezdo', 'mrx', 41, 'insect_barrage', 40, [16], 0, 5, 60, 3, 3, 0, 'cast', hit={0: ['aoe(4, 4f, 0.8)', PO(80, 0)]}, nodef=True)
mv('gnezdo', 'mrx', 42, 'engulf_grab', 56, [16, 30, 44], 0, 2, 60, 3, 4, 0, 'grab', hit={0: [SLOW(60, 3)]})
mv('gnezdo', 'mrx', 43, 'swarm_lunge', 30, [16], 3, 9, 50, 3, 6, 0.8, 'dash', tick=[(6, 16, 'lunge(0.5, 0)')])
mv('gnezdo', 'mrx', 44, 'queen_bite', 24, [12], 0, 2.5, 24, 5, 9, 0.4, 'bite', can='mut(1)')
PASSIVES['gnezdo'] = ['mutateAt(0.35f, 1)']; DMGS['gnezdo'] = ['(s, src) -> s.getMut() == 0 ? 0.55f : 1f']
# ---- Daughters: swarm form (hard to hurt, DoT, lunge-bite) / cold -> sickle frenzy
REPL.add('daughter')
mv('daughter', 'mrx', 40, 'swarm_lunge_bite', 34, [18], 3, 10, 50, 4, 8, 0.8, 'dash', can='notCold()', tick=[(6, 18, 'lunge(0.6, 0)')], hit={0: ['heal(6f)']})
mv('daughter', 'mrx', 41, 'swarm_cloud', 40, [14, 26], 0, 4, 70, 3, 3, 0, 'cast', can='notCold()', hit={0: ['aoe(4, 3f, 0)'], 1: ['aoe(4, 3f, 0)']}, nodef=True)
mv('daughter', 'mrx', 42, 'sickle_frenzy', 44, [8, 14, 20, 26, 32], 0, 3, 36, 6, 6, 0.5, 'flurry', can='isCold()')
DMGS['daughter'] = ['(s, src) -> CanonMoves.cold(s) ? 1f : 0.15f']
# ---- Marguerite: woman controlling insects, mutated hive form
REPL.add('marguerite')
mv('marguerite', 'mrx', 40, 'neck_lift_choke', 48, [14, 26, 38], 0, 2.2, 50, 3, 5, 0, 'grab', hit={0: [SLOW(40, 3)]})
mv('marguerite', 'mrx', 41, 'leg_drag', 44, [16, 30], 0, 2.4, 50, 3, 4, 0, 'grab', hit={0: ['pull(1.2)']})
mv('marguerite', 'mrx', 42, 'swift_dash', 30, [16], 3, 10, 40, 3, 8, 1.0, 'dash', tick=[(6, 16, 'lunge(0.55, 0)')])
mv('marguerite', 'mrx', 43, 'swarm_discharge', 46, [22], 0, 8, 70, 4, 3, 0, 'cast', can='mut(1)', hit={0: ['aoe(8, 4f, 0.8)', PO(100, 1)]}, nodef=True)
mv('marguerite', 'mrx', 44, 'elongated_arm_snatch', 36, [16], 3, 10, 50, 3, 6, 0, 'thrust', can='mut(1)', hit={0: ['pull(2.2)']})
mv('marguerite', 'mrx', 45, 'wall_ambush_leap', 38, [22], 4, 10, 60, 3, 11, 1.2, 'dash', can='mut(1)', tick=[(8, 22, 'lunge(0.6, 0.35)')])
PASSIVES['marguerite'] = ['mutateAt(0.5f, 1)']
# ---- Soldat variants by form: 0 Eins, 1 Zwei, 2 Jet, 3 Panzer
REPL.add('soldat')
mv('soldat', 'mrx', 40, 'drill_impale', 30, [14], 0, 3, 32, 4, 9, 0.8, 'thrust')
mv('soldat', 'mrx', 41, 'drill_bludgeon', 34, [18], 0, 3, 32, 3, 8, 1.6, 'slam')
mv('soldat', 'mrx', 42, 'double_drill', 36, [14, 22], 0, 3, 36, 4, 8, 0.8, 'flurry', can='mutIn(1, 3)')
mv('soldat', 'mrx', 43, 'jet_dash', 36, [22], 4, 12, 60, 4, 14, 2.2, 'dash', can='mutIn(2)', tick=[(6, 22, 'lunge(0.8, 0.1)')])
mv('soldat', 'mrx', 44, 'drill_barrage', 50, [12, 18, 24, 30, 36, 42], 0, 3.4, 50, 3, 6, 0.6, 'flurry', can='mutIn(3)')
DMGS['soldat'] = ['rearWeak(1.5f)']
# ---- Sturm: armless engine torso with propeller
REPL.add('sturm')
mv('sturm', 'mrx', 40, 'propeller_charge', 56, [40], 4, 14, 90, 4, 18, 3.0, 'dash', tick=[(20, 40, 'lunge(0.85, 0)')], hit={0: ['launch(0.6)']})
mv('sturm', 'mrx', 41, 'body_ram', 30, [14], 0, 3, 30, 3, 10, 2.0, 'bite')
mv('sturm', 'mrx', 42, 'overheat_flame', 60, [10, 16, 22, 28, 34, 40, 46, 52], 0, 6, 80, 4, 4, 0.1, 'aimhold', can='mut(1)', hit={i: ['cone(6, 4f, 6)'] for i in range(8)}, nodef=True)
mv('sturm', 'mrx', 43, 'chain_charge', 70, [30, 56], 4, 12, 90, 3, 16, 2.4, 'dash', can='mut(1)', tick=[(14, 30, 'lunge(0.8, 0)'), (40, 56, 'lunge(0.8, 0)')], hit={0: ['launch(0.5)'], 1: ['launch(0.5)']})
PASSIVES['sturm'] = ['mutateAt(0.5f, 1)']; DMGS['sturm'] = ['rearWeak(1.6f)']
# ---- Urias: troll-like Lycan with war-hammer, huge jumps, column throwing (El Gigante base moves)
mv('urias', 'mrx', 40, 'giant_leap', 44, [28], 5, 14, 80, 3, 12, 2.0, 'dash', tick=[(8, 26, 'lunge(0.55, 0.45)')], hit={0: ['aoe(4, 8f, 1.8)']}, nodef=True)
# ---- Reaper: cockroach - stinging, grabbing, claws
REPL.add('reaper')
mv('reaper', 'licker', 40, 'claw_rake', 36, [10, 18, 26], 0, 3.2, 34, 4, 6, 0.6, 'flurry')
mv('reaper', 'licker', 41, 'tail_sting', 30, [16], 0, 4.5, 40, 3, 8, 0.5, 'thrust', hit={0: [PO(120, 1)]})
mv('reaper', 'licker', 42, 'claw_grab', 48, [16, 28, 40], 0, 2.2, 60, 3, 5, 0, 'grab', hit={0: [SLOW(60, 2)]})
# ---- Ndesu: giant human; punches, slams, throws objects
mv('ndesu', 'mrx', 40, 'object_throw', 40, [20], 4, 14, 80, 3, 11, 0.6, 'thrust', hit={0: ['shot("arrow", 11f)']}, nodef=True)
mv('ndesu', 'mrx', 41, 'ground_slam', 44, [24], 0, 5, 60, 3, 12, 1.8, 'slam', hit={0: ['aoe(5, 9f, 1.8)']}, nodef=True)
# ---- Adjule: Plaga dog, head splits open to devour
mv('adjule', 'cerb', 40, 'head_split_devour', 46, [20, 30, 40], 0, 2.4, 70, 3, 7, 0, 'bite', start=['mutate(1)'], end=['mutate(0)'], hit={0: [SLOW(50, 2), PO(60, 0)]})
# ---- Kipepeo: Plaga butterfly; grabs
REPL.add('kipepeo')
mv('kipepeo', 'moth', 40, 'plaga_grab', 40, [18], 0, 3, 40, 4, 4, 0, 'thrust', hit={0: [SLOW(50, 2)]})
mv('kipepeo', 'moth', 41, 'flutter_dive', 30, [16], 3, 9, 40, 3, 5, 0.5, 'dash', tick=[(6, 16, 'lunge(0.5, 0.2)')])
for m_ in ('majini', 'chainsaw_majini', 'gatling_majini', 'executioner_majini'): DEATHS.setdefault(m_, []).append('burstInto("kipepeo", 3, 0.12f)')
# ---- Executioner Majini: Slam, Double Swing, Berserk, Grab (+ leap)
REPL.add('executioner_majini')
mv('executioner_majini', 'mrx', 40, 'axe_slam', 44, [24], 0, 5.5, 40, 4, 18, 2.0, 'slam', hit={0: ['aoe(4, 12f, 1.5)']}, nodef=True)
mv('executioner_majini', 'mrx', 41, 'double_swing', 46, [14, 28], 0, 6, 40, 4, 14, 1.8, 'sweep', hit={0: ['aoe(5, 10f, 1.8)'], 1: ['aoe(5, 10f, 1.8)']}, nodef=True)
mv('executioner_majini', 'mrx', 42, 'berserk', 60, [10, 18, 26, 34, 42, 50], 0, 6, 60, 5, 9, 1.2, 'flurry', can='hpBelow(0.35f)', hit={i: ['aoe(5, 7f, 1.2)'] for i in range(6)}, nodef=True)
mv('executioner_majini', 'mrx', 43, 'grab_throw_slam', 64, [16, 40, 56], 0, 2.6, 70, 3, 8, 0, 'grab', hit={0: [SLOW(60, 3)], 1: ['launch(0.6)'], 2: ['strike(14f, 1.0, 3.5)']})
mv('executioner_majini', 'mrx', 44, 'leap', 40, [26], 5, 12, 90, 2, 12, 1.5, 'dash', tick=[(8, 24, 'lunge(0.55, 0.5)')], hit={0: ['aoe(3.5, 8f, 1.5)']}, nodef=True)
# ---- Fat Molded: projectile bile, charge, arm swing, grapple-vomit, headless exploding variant
REPL.add('fat_molded')
mv('fat_molded', 'zombie', 40, 'bile_vomit', 40, [20], 0, 7, 50, 4, 4, 0, 'spit', hit={0: ['cone(7, 4f, 0)', PO(80, 0), 'stunAoe(4, 60)']}, nodef=True)
mv('fat_molded', 'zombie', 41, 'acid_charge', 40, [24], 3, 9, 70, 3, 10, 2.0, 'dash', tick=[(8, 24, 'lunge(0.4, 0)')])
mv('fat_molded', 'zombie', 42, 'arm_swing', 30, [14], 0, 2.8, 30, 4, 8, 1.4, 'sweep')
mv('fat_molded', 'zombie', 43, 'grapple_vomit', 44, [16, 32], 0, 1.8, 60, 3, 5, 0, 'grab', hit={0: [SLOW(50, 3)], 1: ['cone(2, 6f, 0)', PO(80, 0)]})
mv('fat_molded', 'zombie', 44, 'headless_explosion', 34, [18], 0, 3, 10, 9, 14, 2.4, 'dash', can='mut(1)', tick=[(4, 17, 'lunge(0.45, 0)')], hit={0: ['aoe(4, 12f, 2.4)', 'suicide()']}, nodef=True)
HURTS['fat_molded'] = ['counter(40, 0.35f)']; DEATHS['fat_molded'] = ['explodeOnDeath(4, 5f)']
mv('jack_baker', 'mrx', 42, 'chainsaw_swing', 32, [14, 22], 0, 3.4, 30, 4, 11, 1.0, 'sweep', can='mutIn(1, 2)')
mv('jack_baker', 'mrx', 43, 'chainsaw_lunge', 30, [16], 2, 8, 50, 3, 14, 1.8, 'dash', can='mutIn(1, 2)', tick=[(5, 16, 'lunge(0.55, 0)')], hit={0: ['launch(0.35)']})
mv('jack_baker', 'mrx', 44, 'tendril_lash', 36, [18], 0, 6, 40, 4, 10, 1.4, 'sweep', can='mut(2)', hit={0: ['aoe(6, 8f, 1.4)']}, nodef=True)
mv('jack_baker', 'mrx', 45, 'maw_snatch', 40, [18], 3, 9, 60, 3, 8, 0, 'thrust', can='mut(2)', hit={0: ['pull(2.2)', PO(80, 1)]})
mv('jack_baker', 'mrx', 46, 'mutant_ground_slam', 44, [24], 0, 5.5, 70, 3, 14, 2.0, 'slam', can='mut(2)', hit={0: ['aoe(5.5, 11f, 1.8)']}, nodef=True)
PASSIVES['jack_baker'] = ['mutateAt(0.66f, 1)', 'mutStep(1, 2, 0.33f)']
VIS['jack_baker'] = [('base/torso/armR/foreR/handR/shovel', 'mut == 0'), ('base/torso/armR/foreR/handR/csaw', 'mut == 1'), ('base/torso/maw', 'mut == 2'), ('base/torso/tendL', 'mut == 2'), ('base/torso/tendR', 'mut == 2'), ('base/torso/lump', 'mut == 2')]
# ---- RE7 additions
mv('quick_molded', 'ganado', 40, 'ripper_flurry', 28, [8, 13, 18, 23], 0, 2.8, 24, 4, 4, 0.2, 'flurry')
mv('quick_molded', 'ganado', 41, 'ripper_pounce', 26, [14], 2, 8, 40, 3, 6, 0.6, 'dash', tick=[(4, 14, 'lunge(0.6, 0.15)')])
DMGS['quick_molded'] = ['weakSpots(1.5f, 1.3f)']
mv('blade_molded', 'ganado', 40, 'double_blade_cross', 34, [14, 22], 0, 3.2, 36, 4, 8, 0.8, 'flurry')
DMGS['blade_molded'] = ['weakSpots(1.5f, 1.3f)']
mv('arm_molded', 'ganado', 40, 'giant_arm_smash', 44, [24], 0, 5.5, 60, 4, 11, 1.8, 'slam', hit={0: ['aoe(5, 9f, 1.6)']}, nodef=True)
mv('arm_molded', 'ganado', 41, 'giant_arm_sweep', 36, [18], 0, 4, 40, 3, 9, 1.6, 'sweep')
DMGS['arm_molded'] = ['weakSpots(1.5f, 1.3f)']
mv('molded_fang', 'ganado', 40, 'fang_lunge_bite', 30, [16], 2, 7, 50, 3, 8, 0.6, 'dash', tick=[(5, 16, 'lunge(0.5, 0)')])
DMGS['molded_fang'] = ['weakSpots(1.5f, 1.3f)']
mv('molded_eyeless', 'ganado', 40, 'blind_swing', 34, [16], 0, 3.2, 40, 3, 8, 1.0, 'sweep')
DMGS['molded_eyeless'] = ['weakSpots(1.5f, 1.3f)']; PASSIVES['molded_eyeless'] = ['senseSound(16)']
DMGS['molded'] = ['weakSpots(1.5f, 1.3f)']
for b_ in ('bug_larva', 'bug_adult', 'bug_large', 'bug_small', 'small_spider'): DMGS[b_] = ['bugWeak(2f, 2.5f)']
REPL.add('insect_nest')
mv('insect_nest', 'plant', 40, 'release_flyers', 40, [20], 0, 14, 120, 3, 0, 0, 'cast', hit={0: ['summon("bug_small", 3)']}, nodef=True)
mv('insect_nest', 'plant', 41, 'release_bugs', 40, [20], 0, 14, 160, 2, 0, 0, 'cast', hit={0: ['summon("bug_larva", 2)']}, nodef=True)
mv('mia', 'mrx', 40, 'desperate_claw', 30, [14], 0, 2.6, 36, 3, 5, 0.6, 'thrust')
VIS['fat_molded'] = [('body/waist/chest/neck/head', 'mut != 1')]
# ---- Mendez: back limbs, talons (2nd form)
REPL.add('mendez') if False else None
mv('mendez', 'mrx', 40, 'back_limb_grab', 36, [16], 3, 9, 50, 3, 7, 0, 'thrust', can='mut(1)', hit={0: ['pull(1.5)', SLOW(40, 1)]})
mv('mendez', 'mrx', 41, 'talon_rake', 36, [10, 18, 26], 0, 3.4, 36, 4, 8, 1.0, 'flurry', can='mut(1)')
mv('mendez', 'mrx', 42, 'limb_fling_leap', 40, [24], 4, 11, 70, 3, 11, 1.6, 'dash', can='mut(1)', tick=[(8, 24, 'lunge(0.55, 0.5)')])
# ---- Dimitrescu dragon form
mv('dimitrescu', 'mrx', 42, 'dragon_wing_sweep', 40, [16], 0, 7, 60, 4, 10, 1.8, 'sweep', can='mut(1)', hit={0: ['aoe(7, 10f, 1.8)']}, nodef=True)
mv('dimitrescu', 'mrx', 43, 'dragon_dive', 44, [26], 4, 14, 80, 3, 15, 2.0, 'dash', can='mut(1)', tick=[(8, 26, 'lunge(0.7, 0.4)')], hit={0: ['aoe(4, 9f, 1.6)']}, nodef=True)
PASSIVES['dimitrescu'] = ['mutateAt(0.3f, 1)']
# ---- Heisenberg slug machine form
mv('heisenberg', 'mrx', 42, 'machine_gun_burst', 50, [10, 16, 22, 28, 34], 5, 16, 60, 4, 5, 0, 'aimhold', can='mut(1)', hit={i: ['shot("arrow", 5f)'] for i in range(5)}, nodef=True)
mv('heisenberg', 'mrx', 43, 'steel_slam', 44, [24], 0, 5, 60, 3, 13, 2.0, 'slam', can='mut(1)', hit={0: ['aoe(5, 11f, 1.8)']}, nodef=True)
PASSIVES['heisenberg'] = ['mutateAt(0.35f, 1)']
# ---- Moreau 2nd form
mv('moreau', 'neptune', 41, 'acid_vomit', 44, [22], 0, 8, 60, 4, 5, 0, 'spit', can='mut(1)', hit={0: ['cone(8, 5f, 0)', PO(100, 1)]}, nodef=True)
PASSIVES['moreau'] = ['mutateAt(0.5f, 1)']
# ---- J'avo: every mutation of the wiki list (slots: arm bits0-3, leg 4-7, head 8-11, torso 12-15)
SL = lambda sh, v: 'slot(%d, %d)' % (sh, v)
mv('javo', 'mrx', 40, 'web_spit', 36, [16], 3, 10, 50, 3, 2, 0, 'spit', can=SL(8, 1), hit={0: ['shot("slow", 2f)', SLOW(80, 3)]}, nodef=True)
mv('javo', 'mrx', 41, 'mandible_lift', 52, [18, 34], 0, 2.6, 60, 3, 10, 0, 'grab', can=SL(8, 2), hit={0: ['launch(0.4)', SLOW(40, 3)]})
mv('javo', 'mrx', 42, 'berserk_frenzy', 40, [8, 14, 20, 26, 32], 0, 3, 40, 4, 6, 0.6, 'flurry', can=SL(8, 3))
mv('javo', 'mrx', 43, 'poison_gas', 44, [20], 0, 6, 70, 3, 2, 0, 'cast', can=SL(8, 4), hit={0: ['aoe(6, 2f, 0)', PO(120, 1), BLIND]}, nodef=True)
mv('javo', 'mrx', 44, 'sickle_slash', 30, [14], 0, 4.2, 34, 4, 10, 1.0, 'sweep', can=SL(0, 1))
mv('javo', 'mrx', 45, 'stretch_grab', 36, [16], 3, 10, 50, 3, 5, 0, 'thrust', can=SL(0, 2), hit={0: ['pull(2.2)', SLOW(40, 2)]})
mv('javo', 'mrx', 46, 'shield_bash', 34, [14], 0, 2.6, 36, 3, 7, 1.8, 'slam', can=SL(0, 3))
mv('javo', 'mrx', 47, 'insect_pounce', 34, [18], 3, 9, 50, 4, 9, 1.0, 'dash', can=SL(4, 1), tick=[(6, 18, 'lunge(0.6, 0.35)')])
mv('javo', 'mrx', 48, 'wing_dive_sting', 38, [22], 3, 11, 60, 4, 10, 1.4, 'dash', can=SL(4, 2), tick=[(6, 22, 'lunge(0.6, 0.3)')], hit={0: [PO(100, 1)]})
mv('javo', 'mrx', 49, 'insect_leg_kick', 30, [14], 0, 3.6, 40, 4, 9, 2.0, 'kick', can=SL(4, 3))
mv('javo', 'mrx', 50, 'armored_jump_kick', 34, [16], 2, 8, 50, 4, 11, 2.2, 'kick', can=SL(4, 4), tick=[(6, 15, 'lunge(0.45, 0.5)')])
PASSIVES['javo'] = ['multiMutate(0.8f, 0.55f, 0.3f)', 'chrysalis()', 'gasEmitter()']
DMGS['javo'] = ['armorAndScales()']; DEATHS['javo'] = ['s -> { if (slotOf(s, 12) == 1) aoe(5, 10f, 2f).run(s, null); }']


# ---- Drain Deimos: mutated flea (claws, grabbing, egg implantation)
REPL.add('drain_deimos')
HUNGER = 'effect(net.minecraft.world.effect.MobEffects.HUNGER, 200, 1)'; WEAK = 'effect(net.minecraft.world.effect.MobEffects.WEAKNESS, 200, 1)'
mv('drain_deimos', 'moth', 40, 'claw_swipe', 26, [12], 0, 3, 30, 4, 6, 0.5, 'thrust')
mv('drain_deimos', 'moth', 41, 'grab_drain', 46, [16, 28, 40], 0, 2.4, 60, 3, 3, 0, 'thrust', hit={0: [SLOW(60, 3)], 1: ['heal(3f)'], 2: ['heal(3f)']})
mv('drain_deimos', 'moth', 42, 'egg_implant', 40, [20], 0, 2.4, 120, 2, 4, 0, 'thrust', hit={0: [HUNGER, WEAK, 'effect(net.minecraft.world.effect.MobEffects.CONFUSION, 160, 0)']})
mv('drain_deimos', 'moth', 43, 'leaping_pounce', 34, [18], 3, 9, 50, 3, 7, 0.8, 'dash', tick=[(6, 18, 'lunge(0.6, 0.4)')])


# ---- Hunter gamma: the mutated 'Glimmers' vomit acid
mv('hunter_gamma', 'licker', 40, 'acid_vomit', 40, [20], 2, 7, 60, 3, 4, 0, 'spit', hit={0: ['cone(7, 4f, 0)', PO(80, 1)]}, nodef=True)


PASSIVES['krauser'] = ['mutateAt(0.5f, 1)']
PASSIVES['uroboros'] = ['mutateAt(0.5f, 1)']
PASSIVES['miranda'] = ['mutateAt(0.6f, 1)', 'mutStep(1, 2, 0.25f)']


# ---- Wesker: Magnum in combat, bullet/blow dodging, faster everything
mv('wesker', 'mrx', 43, 'magnum_shot', 26, [10, 18], 5, 32, 24, 8, 13, 0, 'aimhold', hit={0: ['shot("magnum", 13f)'], 1: ['shot("magnum", 13f)']}, nodef=True)
DMGS['wesker'] = ['dodge(0.65f, 22)']
VIS['wesker'] = VIS.get('wesker', []) + [('base/torso/armR/foreR/handR/magnum', 'move == 43')]
# ---- Krauser: explosive-arrow bow (entity), knife parry, TMP and flash grenades
DMGS['krauser'] = ['parry(0.5f, 30, "knife_slash")']
mv('krauser', 'ganado', 42, 'tmp_burst', 34, [10, 13, 16, 19, 22, 25], 5, 18, 50, 3, 3, 0, 'aimhold', hit={i: ['shot("arrow", 3f)'] for i in range(6)}, nodef=True)
mv('krauser', 'ganado', 43, 'flash_grenade', 26, [14], 4, 14, 140, 2, 2, 0, 'cast', hit={0: ['aoe(5, 2f, 0)', BLIND, SLOW(60, 2)]}, nodef=True)
# ---- sound hunters: a sneaking player is not noticed beyond a few blocks
for e_ in ('licker', 're2r_licker', 'garrador'): PASSIVES.setdefault(e_, []).append('senseSound(3.5)')


# ---- Nemesis in the remakes: RE3R flamethrower, RE2R machine gun
mv('re3r_nemesis', 'nemesis', 40, 'flamethrower', 60, [10, 16, 22, 28, 34, 40, 46, 52], 0, 7, 70, 5, 4, 0.1, 'aimhold', hit={i: ['cone(7, 4f, 6)'] for i in range(8)}, nodef=True)
mv('re2r_nemesis', 'nemesis', 40, 'machine_gun_burst', 50, [10, 14, 18, 22, 26, 30, 34, 38, 42, 46], 5, 28, 50, 6, 3, 0, 'aimhold', hit={i: ['shot("arrow", 3.5f)'] for i in range(10)}, nodef=True)
VIS['re3r_nemesis'] = [('base/torso/armR/foreR/handR/flamer', 'move == 40'), ('base/torso/tank', 'true')]
VIS['re2r_nemesis'] = [('base/torso/armR/foreR/handR/mgun', 'move == 40')]

def f(x): return ('%sf' % x)
def data_java():
    L = ['package com.reevil.entity;', '', 'import static com.reevil.entity.CanonMoves.*;', '', '/** Generated by tools/canon_moves.py. */', 'final class CanonData {', '    private CanonData() {}', '    static void register() {']
    for m in DEFS:
        d = '        add("%s", new AttackMove(%d, "%s", %d, new int[]{%s}, %s, %s, %d, %d).dmg(%s).kb(%s)%s%s)' % (m.ent, m.id, m.name, m.dur, ', '.join(map(str, m.hits)), f(m.mn), f(m.mx), m.cd, m.w, f(m.dmg), m.kb, '.face(%d)' % (m.hits[0] if m.hits else 12), '')
        if m.can: d += '.can(%s)' % (m.can if m.can.startswith('mut') or m.can.startswith('hp') else m.can)
        for a, b, fx in m.tick: d += '.tick(%d, %d, %s)' % (a, b, fx)
        for i, fxs in m.hit.items():
            for fx in fxs: d += '.hit(%d, %s)' % (i, fx)
        if m.nodef: d += '.noDefault()'
        for fx in (m.start or []): d += '.onStart(%s)' % fx
        for fx in (m.end or []): d += '.onEnd(%s)' % fx
        L.append(d + ';')
    for ent in sorted(REPL): L.append('        replaceBase("%s");' % ent)
    for ent, fs in DMGS.items():
        for x in fs: L.append('        damage("%s", %s);' % (ent, x))
    for ent, fs in HURTS.items():
        for x in fs: L.append('        onHurt("%s", %s);' % (ent, x))
    for ent, fs in DEATHS.items():
        for x in fs: L.append('        onDeath("%s", %s);' % (ent, x))
    for ent, ps in PASSIVES.items():
        for p in ps: L.append('        passive("%s", %s);' % (ent, p))
    L += ['    }', '}']
    return '\n'.join(L) + '\n'

def rows_java(bone, rr, dur, kind):
    rr = list(rr)
    if rr[0][0] != 0: rr.insert(0, (0, 0, 0, 0))
    if rr[-1][0] != dur: rr.append((dur, 0, 0, 0))
    seen = {}
    for r in rr: seen[r[0]] = r
    rr = [seen[k] for k in sorted(seen)]
    cells = ['k(%sf, %s, %s, %s%s)' % (round(r[0] / dur, 4), r[1], r[2], r[3], (', ' + r[4]) if len(r) > 4 else '') for r in rr]
    return '        %s(b, "%s", %s);' % (kind, bone, ', '.join(cells))

def clips_java():
    L = ['package com.reevil.client.canon;', '', 'import com.reevil.anim.AnimClip;', 'import com.reevil.entity.BioMonster;', 'import net.minecraft.core.registries.BuiltInRegistries;', '', 'import java.util.HashMap;', 'import java.util.Map;', '', 'import static com.reevil.anim.K.*;', '',
         '/** Generated by tools/canon_moves.py: clips for CanonMoves (ids 40+), keyed by entity id. */', 'public final class CanonClips {', '    private CanonClips() {}', '    private static final Map<String, Map<Integer, AnimClip>> CLIPS = new HashMap<>();', '    static {']
    for m in DEFS:
        L.append('        CLIPS.computeIfAbsent("%s", k -> new HashMap<>()).put(%d, c_%s_%d());' % (m.ent, m.id, m.ent, m.id))
    L += ['    }', '    public static AnimClip get(BioMonster e, int id) {', '        Map<Integer, AnimClip> m = CLIPS.get(BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath());', '        return m == null ? null : m.get(id);', '    }']
    for m in DEFS:
        c = arche(m.a, m.rig, m.dur, m.hits)
        pos = c.pop('@pos', {})
        L.append('    private static AnimClip c_%s_%d() {\n        AnimClip.Builder b = AnimClip.create();' % (m.ent, m.id))
        for bone, rr in c.items(): L.append(rows_java(bone, rr, m.dur, 'rot'))
        for bone, rr in pos.items(): L.append(rows_java(bone, rr, m.dur, 'pos'))
        L.append('        return b.build();\n    }')
    L.append('}')
    return '\n'.join(L) + '\n'

def vis_java():
    L = ['package com.reevil.client.canon;', '', 'import com.reevil.entity.BioMonster;', 'import net.minecraft.client.model.geom.ModelPart;', 'import net.minecraft.core.registries.BuiltInRegistries;', '', '/** Generated by tools/canon_moves.py: shows mutation/form parts only while their condition holds; spins propellers. */',
         'public final class CanonVisuals {', '    private CanonVisuals() {}', '    private static ModelPart part(ModelPart root, String path) {', '        ModelPart p = root;', '        for (String s : path.split("/")) { if (!p.hasChild(s)) return null; p = p.getChild(s); }', '        return p;', '    }',
         '    public static void apply(ModelPart root, BioMonster e) {', '        String id = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath();', '        int mut = e.getMut(), form = e.getForm(), move = e.getMoveId();', '        float age = e.tickCount;', '        switch (id) {']
    for ent in sorted(set(list(VIS) + list(SPIN))):
        L.append('            case "%s" -> {' % ent)
        for pth, expr in VIS.get(ent, []):
            L.append('                { ModelPart p = part(root, "%s"); if (p != null) p.visible = %s; }' % (pth, expr))
        for pth, axis, rate in SPIN.get(ent, []):
            L.append('                { ModelPart p = part(root, "%s"); if (p != null) p.%sRot = age * %sf; }' % (pth, axis, rate))
        L.append('            }')
    L += ['            default -> {}', '        }', '    }', '}']
    return '\n'.join(L) + '\n'

if __name__ == '__main__':
    vj = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'canon_vis.json')
    if os.path.exists(vj):
        import json; d = json.load(open(vj)); VIS.update({k: [tuple(x) for x in v] for k, v in d['VIS'].items()}); SPIN.update({k: [tuple(x) for x in v] for k, v in d['SPIN'].items()})
    os.makedirs(J + 'client/canon', exist_ok=True)
    open(J + 'entity/CanonData.java', 'w').write(data_java())
    open(J + 'client/canon/CanonClips.java', 'w').write(clips_java())
    open(J + 'client/canon/CanonVisuals.java', 'w').write(vis_java())
    print(len(DEFS), 'moves')
