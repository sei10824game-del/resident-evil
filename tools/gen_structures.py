"""Generates worldgen structures (NBT templates + jigsaw JSON + biome tags). Run: python3 tools/gen_structures.py"""
import json, os, random, re
import nbtlib
from nbtlib import Compound, List, Int, String, Byte, Short, IntArray

R = os.path.join(os.path.dirname(__file__), '..', 'src/main/resources/data/reevil')
BAR = 3  # foundation layers below floor

class T:
    def __init__(s, w, h, d): s.w, s.h, s.d = w, h, d; s.b = {}; s.nbt = {}
    def set(s, x, y, z, blk):
        if 0 <= x < s.w and 0 <= y < s.h and 0 <= z < s.d: s.b[(x, y, z)] = blk
    def air(s, x, y, z): s.b.pop((x, y, z), None); s.set(x, y, z, 'minecraft:air')
    def fill(s, x0, y0, z0, x1, y1, z1, blk):
        for x in range(min(x0, x1), max(x0, x1) + 1):
            for y in range(min(y0, y1), max(y0, y1) + 1):
                for z in range(min(z0, z1), max(z0, z1) + 1): s.set(x, y, z, blk)
    def shell(s, x0, y0, z0, w, h, d, wall, floor=None, roof=None):
        s.fill(x0, y0, z0, x0 + w - 1, y0 + h - 1, z0 + d - 1, wall)
        s.fill(x0 + 1, y0 + 1, z0 + 1, x0 + w - 2, y0 + h - 2, z0 + d - 2, 'minecraft:air')
        if floor: s.fill(x0, y0, z0, x0 + w - 1, y0, z0 + d - 1, floor)
        if roof: s.fill(x0, y0 + h - 1, z0, x0 + w - 1, y0 + h - 1, z0 + d - 1, roof)
    def found(s, x0, z0, w, d, y, blk='minecraft:cobblestone'):
        s.fill(x0, 0, z0, x0 + w - 1, y - 1, z0 + d - 1, blk)
    def door(s, x, y, z, axis='x', blk='minecraft:dark_oak_door'):
        for dy in (0, 1): s.air(x, y + dy, z); s.air(x + (1 if axis == 'x' and False else 0), y + dy, z)
    def hole(s, x0, y0, z0, x1, y1, z1): s.fill(x0, y0, z0, x1, y1, z1, 'minecraft:air')
    def windows(s, x0, y, z0, w, d, step=4, blk='minecraft:glass_pane'):
        for x in range(x0 + 2, x0 + w - 2, step):
            for z in (z0, z0 + d - 1): s.set(x, y, z, blk); s.set(x, y + 1, z, blk)
        for z in range(z0 + 2, z0 + d - 2, step):
            for x in (x0, x0 + w - 1): s.set(x, y, z, blk); s.set(x, y + 1, z, blk)
    def gable(s, x0, y, z0, w, d, stair, slab=None):
        """pitched roof along z (ridge runs along z axis), over x range."""
        n = (w + 1) // 2
        for i in range(n):
            for z in range(z0 - 1, z0 + d + 1):
                s.set(x0 - 1 + i, y + i, z, f'{stair}[facing=east,half=bottom]')
                s.set(x0 + w - i, y + i, z, f'{stair}[facing=west,half=bottom]')
            if i > 0:
                for z in range(z0, z0 + d):
                    for x in range(x0 + i, x0 + w - i): s.set(x, y + i - 1, z, 'minecraft:air') if False else None
        # ridge fill
        for z in range(z0 - 1, z0 + d + 1):
            for i in range(n):
                for x in range(x0 + i, x0 + w - i): s.set(x, y + i, z, slab or stair.replace('_stairs', '_planks'))
    def torch(s, x, y, z): s.set(x, y, z, 'minecraft:lantern[hanging=false]')
    def remap(s, m):
        for k, v in list(s.b.items()):
            b = v.split('[')[0]
            if b in m: s.b[k] = m[b] + v[len(b):]
    def spawner(s, x, y, z, mobs, nearby=6):
        s.set(x, y, z, 'minecraft:spawner')
        pot = List[Compound]([Compound({'weight': Int(1), 'data': Compound({'entity': Compound({'id': String(m)})})}) for m in mobs])
        s.nbt[(x, y, z)] = Compound({'id': String('minecraft:mob_spawner'), 'SpawnPotentials': pot,
            'SpawnData': Compound({'entity': Compound({'id': String(mobs[0])})}),
            'MaxNearbyEntities': Short(nearby), 'SpawnCount': Short(2), 'SpawnRange': Short(5), 'RequiredPlayerRange': Short(14),
            'MinSpawnDelay': Short(300), 'MaxSpawnDelay': Short(900), 'Delay': Short(40)})
    def chest(s, x, y, z, facing='north'):
        s.set(x, y, z, f'minecraft:chest[facing={facing}]')
        s.nbt[(x, y, z)] = Compound({'id': String('minecraft:chest'), 'LootTable': String('minecraft:chests/simple_dungeon')})
    def save(s, name):
        pal, idx = [], {}
        for blk in s.b.values():
            if blk not in idx:
                idx[blk] = len(pal)
                m = re.match(r'([^\[]+)(?:\[(.*)\])?$', blk)
                c = Compound({'Name': String(m.group(1))})
                if m.group(2): c['Properties'] = Compound({k: String(v) for k, v in (p.split('=') for p in m.group(2).split(','))})
                pal.append(c)
        blocks = []
        for pos, blk in s.b.items():
            e = Compound({'pos': List[Int]([Int(p) for p in pos]), 'state': Int(idx[blk])})
            if pos in s.nbt: e['nbt'] = s.nbt[pos]
            blocks.append(e)
        f = nbtlib.File(Compound({'size': List[Int]([Int(s.w), Int(s.h), Int(s.d)]), 'palette': List[Compound](pal),
            'blocks': List[Compound](blocks), 'entities': List[Compound](), 'DataVersion': Int(3465)}), gzipped=True)
        f.save(os.path.join(R, 'structures', name + '.nbt'))

def ruin(t, rnd, chance, x0, y0, z0, x1, y1, z1):
    """knock random blocks out of walls"""
    for x in range(x0, x1 + 1):
        for y in range(y0, y1 + 1):
            for z in range(z0, z1 + 1):
                if rnd.random() < chance: t.b.pop((x, y, z), None)

# ---------------------------------------------------------------- structures
def spencer():
    W, D, H = 31, 25, 26; t = T(W, H, D); f = BAR
    t.fill(0, 0, 0, W - 1, f - 1, D - 1, 'minecraft:stone_bricks')
    t.fill(0, f, 0, W - 1, f, D - 1, 'minecraft:polished_andesite')
    # main block (2 storeys) + two wings
    t.shell(3, f, 3, 25, 11, 19, 'minecraft:stone_bricks', 'minecraft:dark_oak_planks', 'minecraft:dark_oak_planks')
    t.fill(4, f + 5, 4, 26, f + 5, 20, 'minecraft:dark_oak_planks')  # second floor
    t.fill(11, f + 5, 8, 19, f + 5, 16, 'minecraft:air')              # atrium void
    t.fill(5, f + 1, 5, 25, f + 4, 5, 'minecraft:air') if False else None
    # interior walls
    t.fill(10, f + 1, 4, 10, f + 4, 20, 'minecraft:dark_oak_planks'); t.fill(20, f + 1, 4, 20, f + 4, 20, 'minecraft:dark_oak_planks')
    for z in (8, 14): t.hole(10, f + 1, z, 10, f + 3, z + 1); t.hole(20, f + 1, z, 20, f + 3, z + 1)
    t.fill(10, f + 6, 4, 10, f + 9, 20, 'minecraft:dark_oak_planks'); t.fill(20, f + 6, 4, 20, f + 9, 20, 'minecraft:dark_oak_planks')
    for z in (8, 14): t.hole(10, f + 6, z, 10, f + 8, z + 1); t.hole(20, f + 6, z, 20, f + 8, z + 1)
    # grand door + front steps
    t.hole(14, f + 1, 3, 16, f + 3, 3)
    for i in range(3): t.fill(13 - i, f + 0, 2 - i if 2 - i >= 0 else 0, 17 + i, f + 0, 2 - i if 2 - i >= 0 else 0, 'minecraft:stone_brick_stairs[facing=south]')
    t.windows(3, f + 2, 3, 25, 19, 4); t.windows(3, f + 7, 3, 25, 19, 4)
    # stairs up in atrium
    for i in range(5): t.set(12 + i, f + 1 + i, 9, 'minecraft:dark_oak_stairs[facing=east,half=bottom]')
    # roof
    t.gable(3, f + 10, 3, 25, 19, 'minecraft:dark_oak_stairs')
    # chimney / pillars
    for x in (3, 27):
        for z in (3, 21): t.fill(x, f + 1, z, x, f + 9, z, 'minecraft:dark_oak_log')
    for x in range(6, 25, 6):
        for z in (6, 18): t.torch(x, f + 1, z) if False else t.set(x, f + 4, z, 'minecraft:lantern[hanging=true]')
    # content
    for (x, z, m) in [(6, 8, 'zombie'), (6, 16, 'crimson_head'), (24, 8, 'zombie'), (24, 16, 'cerberus'), (15, 12, 'zombie')]:
        t.spawner(x, f + 1, z, ['reevil:' + m, 'reevil:zombie'])
    t.spawner(15, f + 6, 6, ['reevil:hunter', 'reevil:crow']); t.spawner(6, f + 6, 12, ['reevil:giant_spider', 'reevil:web_spinner']); t.spawner(24, f + 6, 12, ['reevil:crow', 'reevil:zombie'])
    t.chest(13, f + 1, 19, 'north'); t.chest(23, f + 6, 19, 'north')
    return t, 'dark_oak', None

def raccoon():
    W, D, H = 41, 33, 18; t = T(W, H, D); f = BAR; rnd = random.Random(7)
    t.fill(0, 0, 0, W - 1, f - 1, D - 1, 'minecraft:stone'); t.fill(0, f, 0, W - 1, f, D - 1, 'minecraft:gray_concrete')
    for x in range(0, W): t.set(x, f, 4, 'minecraft:yellow_concrete') if x % 4 < 2 else None
    # police station
    t.shell(8, f, 10, 25, 12, 20, 'minecraft:light_gray_concrete', 'minecraft:smooth_stone', 'minecraft:smooth_stone')
    t.fill(9, f + 5, 11, 31, f + 5, 28, 'minecraft:smooth_stone'); t.windows(8, f + 2, 10, 25, 20, 3, 'minecraft:glass_pane'); t.windows(8, f + 7, 10, 25, 20, 3, 'minecraft:glass_pane')
    t.hole(18, f + 1, 10, 22, f + 3, 10); t.fill(17, f + 4, 10, 23, f + 4, 10, 'minecraft:red_concrete')
    t.fill(19, f + 1, 15, 21, f + 1, 15, 'minecraft:quartz_slab'); t.fill(9, f + 1, 20, 31, f + 4, 20, 'minecraft:light_gray_concrete'); t.hole(18, f + 1, 20, 21, f + 3, 20)
    # ruined side buildings
    t.shell(1, f, 12, 6, 9, 14, 'minecraft:bricks', 'minecraft:stone_bricks', 'minecraft:stone_bricks')
    t.shell(33, f, 12, 7, 8, 16, 'minecraft:bricks', 'minecraft:stone_bricks', 'minecraft:stone_bricks')
    ruin(t, rnd, 0.22, 1, f + 1, 12, 39, f + 11, 30)
    # wrecked cars
    for cx in (4, 13, 27, 34):
        t.fill(cx, f + 1, 6, cx + 3, f + 1, 7, 'minecraft:black_concrete'); t.fill(cx + 1, f + 2, 6, cx + 2, f + 2, 7, 'minecraft:gray_stained_glass')
    for x in range(0, W, 2): t.set(x, f + 1, 1, 'minecraft:iron_bars')
    t.set(20, f + 1, 8, 'minecraft:lantern[hanging=false]') if False else None
    for m, (x, z) in zip(['zombie', 'licker', 'cerberus', 'zombie', 'crow'], [(12, 14), (28, 14), (20, 3), (30, 24), (16, 24)]):
        t.spawner(x, f + 1, z, ['reevil:' + m, 'reevil:zombie'])
    t.spawner(15, f + 6, 15, ['reevil:licker', 'reevil:pale_head'])
    t.chest(12, f + 1, 28, 'north')
    return t, 'raccoon', None

def castle(name, W, D, H, wall, trim, floor, mobs, up_mobs, roofblk, towerh):
    t = T(W, H, D); f = BAR; rnd = random.Random(3)
    t.fill(0, 0, 0, W - 1, f - 1, D - 1, wall); t.fill(0, f, 0, W - 1, f, D - 1, floor)
    # curtain wall
    t.shell(2, f, 2, W - 4, 9, D - 4, wall); t.fill(3, f + 1, 3, W - 5, f + 7, D - 5, 'minecraft:air')
    for x in range(2, W - 2, 2):
        for z in (2, D - 3): t.set(x, f + 9, z, trim)
    for z in range(2, D - 2, 2):
        for x in (2, W - 3): t.set(x, f + 9, z, trim)
    # gate
    gx = W // 2; t.hole(gx - 2, f + 1, 2, gx + 2, f + 5, 2)
    for dx in range(-2, 3): t.set(gx + dx, f + 6, 2, trim)
    # corner towers
    for (x, z) in [(0, 0), (W - 7, 0), (0, D - 7), (W - 7, D - 7)]:
        t.shell(x, f, z, 7, towerh, 7, wall, floor)
        t.fill(x + 3, f + 1, z + 3, x + 3, towerh - 2, z + 3, 'minecraft:air')
        for i in range(0, 7, 2):
            for (a, b) in [(x + i, z), (x + i, z + 6), (x, z + i), (x + 6, z + i)]: t.set(a, f + towerh, b, trim)
        for yy in range(f + 2, f + towerh - 2, 4): t.set(x + 3, yy, z, 'minecraft:iron_bars'); t.set(x + 3, yy, z + 6, 'minecraft:iron_bars')
        for yy in range(f + 4, f + towerh - 3, 5): t.fill(x + 1, yy, z + 1, x + 5, yy, z + 5, floor)
        # roof cone
        for i in range(4): t.fill(x + i, f + towerh + 1 + i, z + i, x + 6 - i, f + towerh + 1 + i, z + 6 - i, roofblk)
        t.spawner(x + 3, f + 1, z + 4, up_mobs)
    # keep
    kx, kz, kw, kd = W // 2 - 8, D // 2 - 6, 17, 13
    t.shell(kx, f, kz, kw, 15, kd, wall, floor); t.fill(kx + 1, f + 6, kz + 1, kx + kw - 2, f + 6, kz + kd - 2, floor)
    t.hole(kx + kw // 2 - 1, f + 1, kz + kd - 1, kx + kw // 2 + 1, f + 3, kz + kd - 1)
    t.windows(kx, f + 2, kz, kw, kd, 4, 'minecraft:iron_bars'); t.windows(kx, f + 8, kz, kw, kd, 4, 'minecraft:iron_bars')
    for i in range(0, kw, 2):
        for z in (kz, kz + kd - 1): t.set(kx + i, f + 15, z, trim)
    for i in range(0, kd, 2):
        for x in (kx, kx + kw - 1): t.set(x, f + 15, kz + i, trim)
    for i in range(4): t.set(kx + 2 + i, f + 1 + i, kz + 2, 'minecraft:stone_brick_stairs[facing=east,half=bottom]')
    for (dx, dz, m) in [(3, 4, 0), (8, 6, 1), (13, 4, 2), (8, 9, 3)]:
        t.spawner(kx + dx, f + 1, kz + dz, [mobs[m % len(mobs)], mobs[(m + 1) % len(mobs)]])
    t.spawner(kx + 8, f + 7, kz + 6, mobs[-3:])
    # courtyard spawners + chests
    t.spawner(6, f + 1, 6, mobs[:2]); t.spawner(W - 7, f + 1, D - 7, mobs[1:3])
    t.chest(kx + 15, f + 1, kz + 11, 'west'); t.chest(kx + 2, f + 7, kz + 11, 'east')
    ruin(t, rnd, 0.04, 2, f + 1, 2, W - 3, f + 9, D - 3)
    return t

def salazar(): return castle('salazar', 47, 47, 40, 'minecraft:stone_bricks', 'minecraft:stone_brick_wall', 'minecraft:polished_andesite',
    ['reevil:ganado', 'reevil:armadura', 'reevil:zealot', 'reevil:verdugo', 'reevil:garrador', 'reevil:iron_maiden', 'reevil:novistador'],
    ['reevil:colmillos', 'reevil:novistador', 'reevil:ganado'], 'minecraft:mossy_cobblestone', 14), 'mountain', None
def dimitrescu(): return castle('dimitrescu', 47, 47, 46, 'minecraft:blackstone', 'minecraft:blackstone_wall', 'minecraft:polished_blackstone',
    ['reevil:lycan', 'reevil:daughter', 'reevil:soldat', 'reevil:lycan', 'reevil:daughter', 'reevil:soldat', 'reevil:lycan'],
    ['reevil:daughter', 'reevil:lycan'], 'minecraft:crimson_planks', 20), 'snowy', None

def village(name, mat_wall, mat_roof, mat_floor, mobs, n=6, seed=1, cross=False):
    W, D, H = 41, 41, 14; t = T(W, H, D); f = BAR; rnd = random.Random(seed)
    t.fill(0, 0, 0, W - 1, f - 1, D - 1, 'minecraft:dirt'); t.fill(0, f, 0, W - 1, f, D - 1, mat_floor)
    for z in range(0, D): t.fill(18, f, z, 22, f, z, 'minecraft:dirt_path'); 
    spots = [(3, 3), (3, 18), (3, 31), (28, 3), (28, 18), (28, 31)]
    for i, (x, z) in enumerate(spots[:n]):
        w, d = 9, 8
        t.shell(x, f, z, w, 5, d, mat_wall, mat_floor)
        t.fill(x - 1, f + 5, z - 1, x + w, f + 5, z + d, mat_roof)
        t.fill(x, f + 6, z, x + w - 1, f + 6, z + d - 1, mat_roof)
        t.hole(x + (w // 2 if x < 18 else w // 2), f + 1, z + (d - 1 if x < 18 else 0), x + w // 2, f + 2, z + (d - 1 if x < 18 else 0)) if False else None
        dxn = x + w - 1 if x < 18 else x
        t.hole(dxn, f + 1, z + 3, dxn, f + 3, z + 4)
        t.set(x + 2, f + 2, z, 'minecraft:glass_pane'); t.set(x + 2, f + 2, z + d - 1, 'minecraft:glass_pane')
        t.set(x + 4, f + 1, z + 3, 'minecraft:lantern[hanging=false]') if False else None
        t.spawner(x + 4, f + 1, z + 3, [mobs[i % len(mobs)], mobs[(i + 1) % len(mobs)]])
        if i == 1: t.chest(x + 1, f + 1, z + 1, 'south')
    # central well / pyre / cross
    t.fill(19, f + 1, 19, 21, f + 1, 21, mat_wall); t.fill(20, f + 2, 20, 20, f + 2, 20, 'minecraft:water') if not cross else t.fill(20, f + 1, 20, 20, f + 6, 20, 'minecraft:spruce_log')
    if cross: t.fill(19, f + 5, 20, 21, f + 5, 20, 'minecraft:spruce_log')
    for x in (16, 24):
        for z in (14, 26): t.fill(x, f + 1, z, x, f + 3, z, 'minecraft:oak_fence'); t.set(x, f + 4, z, 'minecraft:lantern[hanging=false]')
    t.spawner(10, f + 1, 14, mobs[:2]); t.spawner(30, f + 1, 27, mobs[-2:])
    return t

def ganado_village(): return village('ganado', 'minecraft:stripped_spruce_log', 'minecraft:spruce_planks', 'minecraft:coarse_dirt',
    ['reevil:ganado', 'reevil:ganado', 'reevil:colmillos', 'reevil:salvador'], 6, 11, True), 'forest', None
def majini_village(): return village('majini', 'minecraft:smooth_sandstone', 'minecraft:cut_sandstone', 'minecraft:sand',
    ['reevil:majini', 'reevil:majini', 'reevil:chainsaw_majini', 'reevil:executioner_majini', 'reevil:kipepeo', 'reevil:gatling_majini', 'reevil:adjule'], 6, 5), 'desert', None
def edonia(): return village('edonia', 'minecraft:gray_concrete', 'minecraft:black_concrete', 'minecraft:stone',
    ['reevil:javo', 'reevil:napad', 'reevil:gnezdo', 'reevil:javo'], 6, 9), 'ruin', None

def baker():
    W, D, H = 35, 31, 20; t = T(W, H, D); f = BAR
    t.fill(0, 0, 0, W - 1, f - 1, D - 1, 'minecraft:mud'); t.fill(0, f, 0, W - 1, f, D - 1, 'minecraft:coarse_dirt')
    t.shell(3, f, 4, 21, 14, 17, 'minecraft:spruce_planks', 'minecraft:dark_oak_planks', 'minecraft:dark_oak_planks')
    t.fill(4, f + 6, 5, 22, f + 6, 19, 'minecraft:dark_oak_planks'); t.hole(10, f + 6, 9, 15, f + 6, 14)
    t.windows(3, f + 2, 4, 21, 17, 3, 'minecraft:oak_trapdoor[facing=north,half=bottom,open=true]'); t.windows(3, f + 8, 4, 21, 17, 3, 'minecraft:glass_pane')
    t.fill(10, f + 1, 4, 11, f + 3, 4, 'minecraft:air'); t.fill(8, f + 0, 1, 14, f + 0, 3, 'minecraft:spruce_planks')
    for x in (8, 14):  # porch posts
        t.fill(x, f + 1, 1, x, f + 4, 1, 'minecraft:spruce_log')
    t.fill(8, f + 5, 1, 14, f + 5, 3, 'minecraft:spruce_slab[type=bottom]')
    t.gable(3, f + 13, 4, 21, 17, 'minecraft:spruce_stairs')
    ruin(t, random.Random(2), 0.07, 3, f + 1, 4, 24, f + 13, 20)
    # interior walls, basement hint, guest shack
    t.fill(13, f + 1, 8, 13, f + 4, 20, 'minecraft:spruce_planks'); t.hole(13, f + 1, 12, 13, f + 3, 13)
    t.shell(26, f, 6, 7, 6, 8, 'minecraft:oak_planks', 'minecraft:oak_planks', 'minecraft:oak_planks'); t.hole(26, f + 1, 9, 26, f + 3, 10)
    t.shell(26, f, 20, 7, 5, 6, 'minecraft:mossy_cobblestone', 'minecraft:cobblestone', 'minecraft:oak_planks')  # old house
    t.spawner(7, f + 1, 8, ['reevil:molded']); t.spawner(7, f + 1, 17, ['reevil:molded', 'reevil:fat_molded']); t.spawner(18, f + 1, 10, ['reevil:molded'])
    t.spawner(18, f + 1, 17, ['reevil:fat_molded', 'reevil:molded']); t.spawner(12, f + 7, 8, ['reevil:molded_fang', 'reevil:quick_molded', 'reevil:bug_small']); t.spawner(4, f + 1, 4, ['reevil:insect_nest']); t.spawner(26, f + 1, 24, ['reevil:arm_molded', 'reevil:blade_molded', 'reevil:molded_eyeless']); t.spawner(29, f + 1, 10, ['reevil:molded'])
    t.chest(5, f + 1, 19, 'east'); t.chest(29, f + 1, 23, 'north')
    return t, 'swamp', None

# name -> (builder, game tag, biome tag name, biomes, mob list for spawn_overrides, spacing, separation)
S = {
  'spencer_mansion': (spencer, ['#minecraft:is_forest', 'minecraft:dark_forest'], ['zombie', 'crimson_head', 'cerberus', 'crow', 'hunter', 'giant_spider'], 41),
  'raccoon_city': (raccoon, ['minecraft:plains', 'minecraft:sunflower_plains', 'minecraft:meadow'], ['zombie', 'licker', 'cerberus', 'crow', 'pale_head'], 43),
  'salazar_castle': (salazar, ['#minecraft:is_mountain', 'minecraft:windswept_hills', 'minecraft:stony_peaks'], ['ganado', 'armadura', 'zealot', 'verdugo', 'garrador', 'colmillos'], 47),
  'ganado_village': (ganado_village, ['minecraft:taiga', 'minecraft:old_growth_pine_taiga', 'minecraft:birch_forest'], ['ganado', 'colmillos', 'salvador'], 53),
  'majini_village': (majini_village, ['minecraft:desert', 'minecraft:savanna', '#minecraft:is_badlands'], ['majini', 'chainsaw_majini', 'executioner_majini', 'kipepeo', 'gatling_majini'], 59),
  'baker_house': (baker, ['minecraft:swamp', 'minecraft:mangrove_swamp'], ['molded', 'fat_molded', 'quick_molded', 'blade_molded', 'arm_molded', 'molded_fang', 'molded_eyeless', 'bug_large', 'small_spider', 'insect_nest', 'mia'], 37),
  'dimitrescu_castle': (dimitrescu, ['minecraft:snowy_taiga', 'minecraft:snowy_plains', 'minecraft:grove', 'minecraft:snowy_slopes'], ['lycan', 'daughter', 'soldat'], 61),
  'edonia_ruins': (edonia, ['minecraft:jungle', 'minecraft:sparse_jungle', 'minecraft:bamboo_jungle', 'minecraft:river'], ['javo', 'napad', 'gnezdo'], 67),
}


def mk_spencer_remake():
    t, _, _ = spencer()
    t.remap({'minecraft:stone_bricks': 'minecraft:mossy_stone_bricks', 'minecraft:polished_andesite': 'minecraft:polished_diorite', 'minecraft:dark_oak_planks': 'minecraft:spruce_planks',
        'minecraft:dark_oak_log': 'minecraft:stripped_dark_oak_log', 'minecraft:dark_oak_stairs': 'minecraft:deepslate_tile_stairs'})
    for b in list(t.b):  # swap mobs to remake zombies/hunters
        pass
    for pos, nbt in t.nbt.items():
        if nbt.get('id') == 'minecraft:mob_spawner':
            ms = [e['data']['entity']['id'] for e in nbt['SpawnPotentials']]
            ms = [{'reevil:zombie': 'reevil:re1r_zombie', 'reevil:hunter': 'reevil:re1r_hunter'}.get(m, m) for m in ms]
            t.spawner(pos[0], pos[1], pos[2], ms)
    for x in range(0, 31, 2): t.set(x, BAR + 1, 0, 'minecraft:iron_bars'); t.set(x, BAR + 1, 24, 'minecraft:iron_bars') if False else None
    return t, 'remake', None

def mk_police():
    W, D, H = 37, 31, 30; t = T(W, H, D); f = BAR
    t.fill(0, 0, 0, W - 1, f - 1, D - 1, 'minecraft:stone_bricks'); t.fill(0, f, 0, W - 1, f, D - 1, 'minecraft:smooth_stone')
    t.shell(4, f, 5, 29, 24, 21, 'minecraft:stone_bricks', 'minecraft:polished_andesite', 'minecraft:stone_brick_slab')
    for y in (f + 8, f + 16): t.fill(5, y, 6, 31, y, 24, 'minecraft:polished_andesite')
    t.fill(13, f + 8, 10, 23, f + 16, 20, 'minecraft:air'); t.fill(13, f + 9, 10, 23, f + 9, 10, 'minecraft:air')   # atrium void
    for y in (f + 8, f + 16): t.fill(14, y, 11, 22, y, 19, 'minecraft:air')
    t.fill(4, f + 20, 5, 32, f + 20, 25, 'minecraft:deepslate_brick_slab')
    for x in range(4, 33, 4): t.fill(x, f + 1, 5, x, f + 20, 5, 'minecraft:deepslate_bricks'); t.fill(x, f + 1, 25, x, f + 20, 25, 'minecraft:deepslate_bricks')
    t.hole(15, f + 1, 5, 21, f + 5, 5); t.fill(15, f + 6, 5, 21, f + 6, 5, 'minecraft:deepslate_bricks')
    for x in range(7, 31, 3):
        for y in (f + 3, f + 11, f + 19 - 4): t.set(x, y, 5, 'minecraft:oak_planks'); t.set(x, y + 1, 5, 'minecraft:oak_planks')
    t.fill(17, f + 1, 15, 19, f + 1, 15, 'minecraft:quartz_block'); t.fill(18, f + 2, 15, 18, f + 8, 15, 'minecraft:quartz_pillar')   # statue
    t.fill(16, f + 1, 8, 20, f + 1, 24, 'minecraft:red_carpet')
    for i in range(7): t.fill(7 + i, f + 1 + i, 22, 7 + i, f + 1 + i, 24, 'minecraft:stone_brick_stairs[facing=east,half=bottom]')
    t.set(18, f + 22, 4, 'minecraft:gold_block'); t.set(17, f + 21, 4, 'minecraft:gold_block'); t.set(19, f + 21, 4, 'minecraft:gold_block')   # clock
    for (x, y, z, m, n) in [(8, 1, 10, 're2r_zombie', 6), (28, 1, 10, 're2r_zombie', 6), (10, 9, 20, 're2r_licker', 4), (26, 9, 20, 're2r_zombie', 6), (18, 17, 12, 're2r_licker', 4), (8, 17, 8, 're2r_zombie', 6)]:
        t.spawner(x, f + y, z, ['reevil:' + m, 'reevil:re2r_zombie'], n)
    t.spawner(18, f + 1, 22, ['reevil:re2r_mr_x'], 1)
    t.chest(6, f + 1, 24, 'north'); t.chest(30, f + 9, 24, 'north')
    ruin(t, random.Random(11), 0.05, 4, f + 1, 5, 32, f + 20, 25)
    for cx in (4, 26): t.fill(cx, f + 1, 2, cx + 4, f + 1, 3, 'minecraft:black_concrete')
    return t, 'rpd', None

def mk_downtown():
    W, D, H = 45, 37, 22; t = T(W, H, D); f = BAR; rnd = random.Random(21)
    t.fill(0, 0, 0, W - 1, f - 1, D - 1, 'minecraft:stone'); t.fill(0, f, 0, W - 1, f, D - 1, 'minecraft:black_concrete')
    for z in range(0, D): t.fill(0, f, z, W - 1, f, z, 'minecraft:gray_concrete') if 14 <= z <= 22 else None
    for x in range(0, W, 4): t.set(x, f, 18, 'minecraft:white_concrete'); t.set(x + 1, f, 18, 'minecraft:white_concrete')
    for (x0, w, h, mat) in [(1, 12, 15, 'minecraft:bricks'), (15, 12, 18, 'minecraft:gray_concrete'), (30, 14, 14, 'minecraft:red_terracotta')]:
        t.shell(x0, f, 24, w, h, 12, mat, 'minecraft:stone', 'minecraft:stone')
        for y in range(f + 5, f + h - 2, 5): t.fill(x0 + 1, y, 25, x0 + w - 2, y, 34, 'minecraft:stone')
        t.windows(x0, f + 2, 24, w, 12, 3); t.windows(x0, f + 7, 24, w, 12, 3)
        t.hole(x0 + w // 2 - 1, f + 1, 24, x0 + w // 2 + 1, f + 3, 24)
        t.fill(x0 + 2, f + 1, 23, x0 + w - 3, f + 1, 23, 'minecraft:red_concrete') if mat == 'minecraft:gray_concrete' else None
    t.shell(2, f, 2, 14, 7, 10, 'minecraft:light_gray_concrete', 'minecraft:smooth_stone', 'minecraft:smooth_stone'); t.fill(0, f + 7, 1, 17, f + 7, 13, 'minecraft:red_concrete')  # gas-station roof
    t.hole(8, f + 1, 2, 10, f + 3, 2)
    for cx in (20, 28, 36): t.fill(cx, f + 1, 15, cx + 3, f + 1, 16, 'minecraft:black_concrete'); t.fill(cx + 1, f + 2, 15, cx + 2, f + 2, 16, 'minecraft:gray_stained_glass')
    for x in range(18, 36): t.set(x, f + 1, 20, 'minecraft:iron_bars') if x % 3 else None
    ruin(t, rnd, 0.1, 1, f + 1, 24, 43, f + 14, 35)
    for (x, z, m, n) in [(6, 28, 're3r_zombie', 6), (20, 28, 're3r_zombie', 6), (36, 28, 're3r_hunter_beta', 3), (8, 6, 're3r_zombie', 6), (24, 17, 're3r_zombie', 6), (40, 18, 're3r_pale_head', 3)]:
        t.spawner(x, f + 1, z, ['reevil:' + m, 'reevil:re3r_zombie'], n)
    t.spawner(22, f + 1, 8, ['reevil:re3r_nemesis'], 1); t.chest(5, f + 1, 33, 'north'); t.chest(16, f + 6, 33, 'north')
    return t, 'downtown', None

def mk_village_remake():
    t = village('ganado', 'minecraft:stripped_spruce_log', 'minecraft:spruce_planks', 'minecraft:coarse_dirt',
        ['reevil:re4r_ganado', 'reevil:re4r_ganado', 'reevil:re4r_krauser', 'reevil:re4r_regenerador'], 6, 31, True)
    t.remap({'minecraft:stripped_spruce_log': 'minecraft:stripped_dark_oak_log', 'minecraft:spruce_planks': 'minecraft:dark_oak_planks'})
    t.shell(16, BAR, 33, 9, 5, 7, 'minecraft:cobblestone', 'minecraft:stone', 'minecraft:stone')
    t.fill(16, BAR + 5, 33, 24, BAR + 5, 39, 'minecraft:stone_brick_slab'); t.fill(19, BAR + 6, 35, 21, BAR + 15, 37, 'minecraft:cobblestone'); t.fill(20, BAR + 8, 36, 20, BAR + 9, 36, 'minecraft:air')   # church-ish tower
    t.hole(19, BAR + 1, 33, 21, BAR + 3, 33); t.set(20, BAR + 16, 36, 'minecraft:bell')
    t.spawner(20, BAR + 1, 36, ['reevil:re4r_ganado', 'reevil:re4r_ganado'])
    t.spawner(36, BAR + 1, 36, ['reevil:re4r_el_gigante'], 1)
    return t, 'village_r', None

def mk_castle_remake():
    t = castle('salazar_r', 51, 51, 44, 'minecraft:deepslate_bricks', 'minecraft:deepslate_brick_wall', 'minecraft:polished_deepslate',
      ['reevil:re4r_ganado', 'reevil:re4r_armadura', 'reevil:re4r_iron_maiden', 'reevil:re4r_krauser', 'reevil:re4r_regenerador', 'reevil:re4r_verdugo', 'reevil:re4r_armadura'],
      ['reevil:re4r_ganado', 'reevil:re4r_regenerador'], 'minecraft:cut_copper_stairs', 16)
    t.set(25, BAR + 17, 25, 'minecraft:gold_block')
    t.spawner(25, BAR + 1, 12, ['reevil:re4r_salazar'], 1)
    return t, 'castle_r', None

S.update({
  'spencer_mansion_remake': (mk_spencer_remake, ['minecraft:birch_forest', 'minecraft:old_growth_birch_forest', 'minecraft:flower_forest'], ['re1r_zombie', 're1r_hunter', 'crimson_head', 'cerberus'], 71),
  'raccoon_police_station': (mk_police, ['minecraft:plains', 'minecraft:sunflower_plains', 'minecraft:forest'], ['re2r_zombie', 're2r_licker'], 73),
  'raccoon_downtown': (mk_downtown, ['minecraft:beach', 'minecraft:stony_shore', 'minecraft:savanna_plateau'], ['re3r_zombie', 're3r_hunter_beta', 're3r_pale_head'], 79),
  'village_remake': (mk_village_remake, ['minecraft:old_growth_spruce_taiga', 'minecraft:dark_forest'], ['re4r_ganado', 're4r_krauser', 're4r_regenerador'], 83),
  'salazar_castle_remake': (mk_castle_remake, ['minecraft:windswept_forest', 'minecraft:jagged_peaks', 'minecraft:frozen_peaks'], ['re4r_ganado', 're4r_armadura', 're4r_iron_maiden', 're4r_verdugo'], 89),
})

def main():
    for sub in ('structure', 'template_pool', 'structure_set'): os.makedirs(f'{R}/worldgen/{sub}', exist_ok=True)
    os.makedirs(f'{R}/tags/worldgen/biome', exist_ok=True); os.makedirs(f'{R}/structures', exist_ok=True)
    for i, (n, (fn, biomes, mobs, spacing)) in enumerate(S.items()):
        t = fn()[0] if isinstance(fn(), tuple) else fn()
        t.save(n)
        json.dump({'values': biomes}, open(f'{R}/tags/worldgen/biome/has_{n}.json', 'w'), indent=1)
        json.dump({'type': 'minecraft:jigsaw', 'biomes': f'#reevil:has_{n}', 'step': 'surface_structures', 'terrain_adaptation': 'beard_thin',
            'start_pool': f'reevil:{n}', 'size': 1, 'start_height': {'absolute': -BAR}, 'project_start_to_heightmap': 'WORLD_SURFACE_WG',
            'max_distance_from_center': 80, 'use_expansion_hack': False,
            'spawn_overrides': {'monster': {'bounding_box': 'full', 'spawns': [{'type': 'reevil:' + m, 'weight': 1, 'minCount': 1, 'maxCount': 2} for m in mobs]}}},
            open(f'{R}/worldgen/structure/{n}.json', 'w'), indent=1)
        json.dump({'fallback': 'minecraft:empty', 'elements': [{'weight': 1, 'element': {'element_type': 'minecraft:single_pool_element',
            'location': f'reevil:{n}', 'processors': 'minecraft:empty', 'projection': 'rigid'}}]}, open(f'{R}/worldgen/template_pool/{n}.json', 'w'), indent=1)
        json.dump({'structures': [{'structure': f'reevil:{n}', 'weight': 1}], 'placement': {'type': 'minecraft:random_spread',
            'spacing': 32, 'separation': 12, 'salt': 1000 + i * 37}}, open(f'{R}/worldgen/structure_set/{n}.json', 'w'), indent=1)
    print('ok', len(S))
main()
