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
mv('mendez', 'mrx', 40, 'tongue_lash', 36, [16], 3, 8, 50, 3, 7, 0, 'thrust', can='mut(1)', hit={0: ['pull(1.3)', SLOW(40, 1)]})
mv('mendez', 'mrx', 41, 'worm_burst', 40, [20], 0, 5, 60, 3, 9, 1.5, 'cast', can='mut(1)', hit={0: ['aoe(5, 9f, 1.5)', PO(80, 0)]}, nodef=True)
mv('verdugo', 'mrx', 40, 'rend_grab', 52, [14, 26, 38], 0, 2.2, 60, 3, 5, 0, 'grab', hit={0: [SLOW(60, 3)]})
# ---- RE5
mv('uroboros', 'plant', 40, 'tendril_mass_slam', 44, [24], 0, 7, 60, 4, 12, 1.8, 'slam', hit={0: ['aoe(5.5, 10f, 1.6)']}, nodef=True)
mv('uroboros', 'plant', 41, 'virus_burst', 48, [24], 0, 8, 90, 3, 4, 0, 'cast', hit={0: ['aoe(8, 4f, 0.8)', PO(100, 1)]}, nodef=True)
mv('uroboros', 'plant', 42, 'tendril_pull', 40, [18], 4, 12, 70, 3, 5, 0, 'thrust', hit={0: ['pull(2.0)']})
mv('wesker', 'mrx', 40, 'phantom_step_strike', 30, [16], 3, 9, 50, 3, 10, 1.3, 'thrust', tick=[(5, 5, 'blink()')])
mv('wesker', 'mrx', 41, 'lava_tentacle_lash', 40, [16, 26], 0, 7, 50, 4, 9, 1.2, 'sweep', can='mut(1)', hit={0: ['aoe(6, 8f, 1.4)', 'fire(4)'], 1: ['aoe(6, 8f, 1.4)']}, nodef=True)
mv('wesker', 'mrx', 42, 'charge_rush', 36, [20], 3, 10, 60, 3, 14, 2.4, 'dash', can='mut(1)', tick=[(6, 20, 'lunge(0.55, 0)')], hit={0: ['launch(0.45)']})
mv('jill', 'mrx', 40, 'flip_kick', 30, [14], 0, 3.4, 40, 3, 9, 1.6, 'kick', tick=[(8, 13, 'lunge(0.3, 0)')])
mv('ndesu', 'mrx', 40, 'mace_smash', 44, [24], 0, 5, 60, 4, 14, 2.0, 'slam', hit={0: ['aoe(5, 10f, 1.8)']}, nodef=True)
mv('reaper', 'licker', 40, 'carapace_charge', 40, [24], 3, 9, 70, 3, 12, 2.4, 'dash', tick=[(8, 24, 'lunge(0.5, 0)')])
mv('reaper', 'licker', 41, 'jaw_clamp', 48, [16, 28, 40], 0, 2.2, 60, 3, 5, 0, 'bite', hit={0: [SLOW(60, 2)]})
# ---- RE6
mv('javo', 'ganado', 40, 'blade_arm_slash', 30, [14], 0, 3.2, 34, 4, 9, 1.0, 'thrust', can='mut(1)')
mv('javo', 'ganado', 41, 'wing_dive', 38, [22], 3, 10, 60, 4, 10, 1.6, 'dash', can='mut(2)', tick=[(6, 22, 'lunge(0.6, 0.25)')])
mv('javo', 'ganado', 42, 'stinger_stab', 32, [16], 0, 5, 40, 4, 8, 0.5, 'thrust', can='mut(3)', hit={0: [PO(100, 1)]})
mv('javo', 'ganado', 43, 'claw_smash', 36, [20], 0, 3.4, 40, 4, 12, 1.8, 'slam', can='mut(4)')
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
mv('fat_molded', 'zombie', 40, 'bile_vomit', 40, [20], 0, 5, 60, 3, 3, 0, 'spit', hit={0: ['cone(5, 3f, 0)', PO(100, 1)]}, nodef=True)
mv('fat_molded', 'zombie', 41, 'belly_slam', 40, [22], 0, 3, 50, 3, 9, 2.0, 'slam', hit={0: ['aoe(3.5, 8f, 1.6)']}, nodef=True)
mv('jack_baker', 'mrx', 40, 'door_slam_rush', 40, [22], 3, 8, 60, 3, 12, 2.2, 'dash', tick=[(6, 22, 'lunge(0.5, 0)')], hit={0: ['launch(0.45)']})
mv('jack_baker', 'mrx', 41, 'headlock_grab', 56, [16, 30, 44], 0, 2, 60, 3, 4, 0, 'grab', hit={0: [SLOW(70, 3)]})
mv('marguerite', 'moth', 40, 'insect_swarm', 44, [22], 0, 6, 80, 3, 3, 0, 'cast', hit={0: ['aoe(6, 4f, 0.8)', PO(100, 1)]}, nodef=True)
mv('eveline', 'nemesis', 40, 'psychic_shove', 34, [16], 0, 9, 60, 3, 5, 0, 'cast', hit={0: ['aoe(8, 5f, 2.6)']}, nodef=True)
mv('eveline', 'nemesis', 41, 'summon_molded', 40, [20], 0, 20, 160, 2, 0, 0, 'cast', can='hpBelow(0.8f)', hit={0: ['summon("molded", 2)']}, nodef=True)
# ---- RE8
mv('lycan', 'hunter', 40, 'frenzy_rake', 36, [10, 16, 22, 28], 0, 3, 40, 4, 5, 0.5, 'flurry')
mv('lycan', 'hunter', 41, 'howl_summon', 48, [24], 0, 30, 220, 1, 0, 0, 'cast', can='hpBelow(0.7f)', hit={0: ['summon("lycan", 1)']}, nodef=True)
mv('daughter', 'licker', 40, 'bat_swarm_dash', 28, [16], 4, 12, 60, 3, 7, 0.8, 'dash', tick=[(4, 4, 'blink()')])
mv('daughter', 'licker', 41, 'ice_shard_volley', 34, [14, 20], 4, 14, 60, 3, 5, 0, 'spit', hit={0: ['shot("slow", 5f)'], 1: ['shot("slow", 5f)']}, nodef=True)
mv('dimitrescu', 'mrx', 40, 'blood_lunge', 36, [18], 3, 8, 50, 3, 11, 0.8, 'dash', tick=[(6, 18, 'lunge(0.6, 0)')], hit={0: ['heal(8f)']})
mv('dimitrescu', 'mrx', 41, 'vault_kick', 30, [14], 0, 3.6, 40, 3, 9, 1.8, 'kick', tick=[(8, 13, 'lunge(0.3, 0)')])
mv('heisenberg', 'mrx', 40, 'magnet_pull', 36, [16], 4, 12, 70, 3, 4, 0, 'cast', hit={0: ['pull(2.2)']})
mv('heisenberg', 'mrx', 41, 'hammer_toss', 40, [20], 4, 14, 70, 2, 13, 0.6, 'thrust', hit={0: ['shot("arrow", 13f)']}, nodef=True)
mv('sturm', 'mrx', 40, 'flamethrower', 50, [10, 16, 22, 28, 34, 40], 0, 7, 70, 4, 3, 0.1, 'aimhold', hit={i: ['cone(7, 3f, 5)'] for i in range(6)}, nodef=True)
mv('urias', 'mrx', 40, 'cadou_charge', 40, [22], 4, 10, 70, 3, 15, 2.5, 'dash', tick=[(6, 22, 'lunge(0.55, 0)')], hit={0: ['launch(0.5)']})
mv('moreau', 'neptune', 40, 'toxic_spit', 36, [16], 4, 14, 60, 4, 4, 0, 'spit', hit={0: ['shot("poison", 4f)']}, nodef=True)
mv('miranda', 'nemesis', 40, 'wing_dive', 40, [22], 4, 12, 70, 3, 13, 2.0, 'dash', tick=[(6, 22, 'lunge(0.6, 0.3)')], hit={0: ['aoe(3, 6f, 1.4)']}, nodef=True)
mv('miranda', 'nemesis', 41, 'shard_volley', 42, [14, 20, 26], 5, 16, 60, 3, 6, 0, 'cast', hit={i: ['shot("slow", 6f)'] for i in range(3)}, nodef=True)
mv('soldat', 'ganado', 40, 'rifle_burst', 40, [12, 18, 24], 5, 16, 60, 4, 5, 0, 'aimhold', hit={i: ['shot("arrow", 5f)'] for i in range(3)}, nodef=True)
# ---- RE1-3 gaps
mv('drain_deimos', 'moth', 40, 'sting_stab', 28, [14], 0, 4, 40, 4, 7, 0.5, 'bite', hit={0: [PO(120, 1)]})
mv('grave_digger', 'yawn', 40, 'earth_burst', 44, [24], 0, 6, 80, 3, 8, 1.6, 'bite', hit={0: ['aoe(5, 8f, 1.6)']}, nodef=True)

PASSIVES = {'javo': ['mutateAt(0.65f, 1, 2, 3, 4)'], 'krauser': ['mutateAt(0.5f, 1)'], 'wesker': ['mutateAt(0.3f, 1)'], 'mendez': ['mutateAt(0.5f, 1)'], 'regenerador': ['regen(0.01f)'], 're4r_regenerador': ['regen(0.01f)']}
# entity -> {mut stage: [part paths]} (parts exist in the model; shown only while getMut()==stage)
VIS = {'javo': {1: ['base/torso/armR/foreR/mutBlade'], 2: ['base/torso/mutWingL', 'base/torso/mutWingR'], 3: ['base/mutTail'], 4: ['base/torso/armL/foreL/mutClaw']},
       'wesker': {1: ['base/torso/uroT1', 'base/torso/uroT2', 'base/torso/uroT3', 'base/torso/uroT4']}}

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
    L = ['package com.reevil.client.canon;', '', 'import com.reevil.entity.BioMonster;', 'import net.minecraft.client.model.geom.ModelPart;', 'import net.minecraft.core.registries.BuiltInRegistries;', '', '/** Generated by tools/canon_moves.py: shows a creature\'s mutation parts only while its mutation stage matches. */',
         'public final class CanonVisuals {', '    private CanonVisuals() {}', '    private static ModelPart part(ModelPart root, String path) {', '        ModelPart p = root;', '        for (String s : path.split("/")) { if (!p.hasChild(s)) return null; p = p.getChild(s); }', '        return p;', '    }',
         '    public static void apply(ModelPart root, BioMonster e) {', '        String id = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath();', '        int mut = e.getMut();', '        switch (id) {']
    for ent, st in VIS.items():
        L.append('            case "%s" -> {' % ent)
        for k, paths in st.items():
            for pth in paths: L.append('                { ModelPart p = part(root, "%s"); if (p != null) p.visible = mut == %d; }' % (pth, k))
        L.append('            }')
    L += ['            default -> {}', '        }', '    }', '}']
    return '\n'.join(L) + '\n'

if __name__ == '__main__':
    os.makedirs(J + 'client/canon', exist_ok=True)
    open(J + 'entity/CanonData.java', 'w').write(data_java())
    open(J + 'client/canon/CanonClips.java', 'w').write(clips_java())
    open(J + 'client/canon/CanonVisuals.java', 'w').write(vis_java())
    print(len(DEFS), 'moves')
