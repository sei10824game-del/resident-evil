"""RE4 creatures that share another creature's UV layout: recoloured from the base texture (colmillos, novistador, regenerador, iron_maiden, el_gigante, del_lago)."""
import os
from PIL import Image, ImageOps, ImageEnhance, ImageDraw

D = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/resources/assets/reevil/textures/entity/')
def load(n): return Image.open(D + n + '.png').convert('RGBA')
def colorize(im, black, white, mid=None):
    a = im.split()[3]
    g = ImageOps.grayscale(im.convert('RGB'))
    out = ImageOps.colorize(g, black, white, mid=mid).convert('RGBA'); out.putalpha(a); return out
def save(im, n): im.save(D + n + '.png'); print(n)

# Colmillos: grey-brown wolf, no glow
save(colorize(load('cerberus'), (24, 22, 20), (176, 168, 150), (96, 86, 74)), 'colmillos')
# Novistador: translucent-looking pale insect
save(colorize(load('giant_moth'), (30, 44, 40), (190, 214, 200), (110, 140, 128)), 'novistador')
# Regenerador: sallow stitched skin, dark straps
z = colorize(load('zombie_civilian'), (30, 26, 28), (214, 200, 184), (130, 118, 108)); d = ImageDraw.Draw(z)
for y in range(0, 64, 6): d.line([(0, y), (40, y)], fill=(70, 50, 50, 120))
save(z, 'regenerador')
# Iron Maiden: rusty-grey with spike studs scattered
m = colorize(load('zombie_civilian'), (24, 20, 20), (180, 150, 130), (100, 80, 70)); d = ImageDraw.Draw(m)
import random; r = random.Random(7)
for _ in range(220): x, y = r.randrange(128), r.randrange(64); d.point((x, y), fill=(200, 200, 205, 255))
save(m, 'iron_maiden')
# El Gigante: bare grey-brown hide, no hat (hat UV regions made transparent)
g = colorize(load('mr_x'), (66, 52, 44), (196, 164, 136), (130, 106, 90)); px = g.load()
for (x0, y0, w, h) in ((60, 0, 40, 11), (60, 12, 28, 10)):
    for y in range(y0, y0 + h):
        for x in range(x0, x0 + w): px[x, y] = (0, 0, 0, 0)
save(g, 'el_gigante')
# Del Lago: murky teal hide, ochre belly
save(colorize(load('neptune'), (14, 28, 26), (206, 190, 120), (64, 96, 84)), 'del_lago')

# ---- RE4/RE5 extra variants (same UVs as their base creature)
def nohat(im):  # clear the stalker rig's hat UV regions
    px = im.load()
    for (x0, y0, w, h) in ((60, 0, 40, 11), (60, 12, 28, 10)):
        for y in range(y0, y0 + h):
            for x in range(x0, x0 + w): px[x, y] = (0, 0, 0, 0)
    return im
mx = load('mr_x')
# Verdugo: bandaged pale-grey executioner (hat removed)
save(nohat(colorize(mx, (40, 36, 36), (206, 200, 190), (120, 112, 106))), 'verdugo')
# Adjule: grey-green leathery skin
save(nohat(colorize(mx, (30, 40, 34), (176, 190, 160), (100, 120, 96))), 'adjule')
# Ndesu: bloated brown-green with spore-pink
save(nohat(colorize(mx, (36, 34, 22), (200, 170, 120), (110, 120, 70))), 'ndesu')
# Executioner Majini: dark hide + black hood feel
save(nohat(colorize(mx, (30, 22, 18), (170, 130, 100), (96, 70, 56))), 'executioner_majini')
# Wesker: keep the black coat; paint platinum-blond hair and black shades on the head cube (UV 30,6 7x7x7)
w = nohat(mx.copy()); px = w.load()
for y in range(6, 6 + 7):  # top + upper rim of the head sides
    for x in range(37, 51): px[x, y] = (226, 214, 150, 255)
for x in range(30, 58):
    for y in range(13, 15): px[x, y] = (226, 214, 150, 255)   # hair line around the sides
for x in range(37, 44): px[x, 15] = (12, 12, 14, 255); px[x, 16] = (12, 12, 14, 255)  # sunglasses band on the front
save(w, 'wesker')
# Armadura: steel plate over the zombie layout
a = colorize(load('zombie_civilian'), (36, 38, 44), (200, 204, 212), (112, 118, 128)); d = ImageDraw.Draw(a)
for y in range(0, 64, 4): d.line([(0, y), (110, y)], fill=(70, 74, 82, 140))
save(a, 'armadura')
# Kipepeo: bronze-green moth
save(colorize(load('giant_moth'), (30, 30, 14), (226, 210, 120), (120, 120, 50)), 'kipepeo')
# Reaper: sinewy dark purple-grey crawler
save(colorize(load('hunter_gamma'), (16, 12, 22), (176, 150, 190), (70, 56, 84)), 'reaper')

# ---- RE4/5/6 bosses and variants
nem = load('nemesis')
for sfx, (b, w, m) in {'': ((70, 66, 70), (246, 244, 240), (170, 164, 160)), '_mutated': ((60, 36, 44), (226, 196, 190), (150, 90, 100)), '_final': ((50, 24, 40), (210, 170, 190), (130, 60, 90))}.items():
    save(colorize(load('nemesis' + sfx), b, w, m), 'saddler' + sfx)
    save(colorize(load('nemesis' + sfx), (18, 16, 20), (170, 160, 170), (84, 70, 90)), 'simmons' + sfx)
for sfx in ('', '_phase2'):
    save(colorize(load('plant42' + sfx), (40, 22, 30), (236, 200, 190), (150, 90, 100)), 'salazar' + sfx)
    save(colorize(load('plant42' + sfx), (14, 8, 10), (230, 150, 40), (90, 24, 24)), 'uroboros' + sfx)
eg = load('el_gigante')
save(colorize(eg, (50, 40, 30), (214, 180, 120), (130, 96, 64)), 'mendez')
save(colorize(eg, (34, 40, 40), (168, 190, 170), (84, 110, 96)), 'ogroman')
# Jill: Wesker frame, brown hair, no shades
jw = load('wesker'); px = jw.load()
for y in range(6, 17):
    for x in range(30, 58):
        r, g, b, a = px[x, y]
        if (r, g, b) == (226, 214, 150): px[x, y] = (96, 70, 46, 255)
        elif (r, g, b) == (12, 12, 14) and y >= 15: px[x, y] = (178, 150, 130, 255)
save(jw, 'jill')
# Ustanak: super-frame with steel claws, dark leather
save(nohat(colorize(load('mr_x_super'), (28, 26, 26), (190, 180, 168), (92, 86, 80))), 'ustanak')
save(colorize(load('reaper'), (40, 26, 12), (216, 170, 90), (120, 84, 40)), 'napad')

# ---- RE7 / RE8
save(colorize(load('ganado'), (8, 8, 10), (110, 112, 120), (40, 40, 46)), 'molded')
save(colorize(load('ganado_plaga'), (8, 8, 10), (120, 120, 130), (44, 44, 50)), 'molded_plaga')
save(colorize(load('zombie_civilian'), (6, 6, 8), (96, 98, 106), (34, 34, 40)), 'fat_molded')
save(nohat(colorize(mx, (46, 36, 28), (196, 168, 126), (118, 94, 70))), 'jack_baker')
for sfx in ('', '_mutated', '_final'):
    save(colorize(load('saddler' + sfx), (10, 14, 24), (176, 196, 220), (60, 80, 110)), 'eveline' + sfx)
save(colorize(load('hunter'), (24, 18, 14), (186, 170, 146), (96, 82, 66)), 'lycan')
save(nohat(colorize(load('ustanak'), (40, 24, 30), (240, 232, 230), (170, 130, 130))), 'dimitrescu')
save(colorize(load('reaper'), (30, 16, 20), (230, 224, 222), (150, 120, 124)), 'daughter')
save(nohat(colorize(mx, (38, 28, 24), (176, 150, 124), (112, 90, 72))), 'heisenberg')
save(colorize(load('del_lago'), (10, 30, 16), (190, 214, 120), (70, 110, 50)), 'moreau')
save(colorize(load('ogroman'), (30, 30, 34), (190, 192, 196), (110, 110, 116)), 'sturm')
save(colorize(load('ustanak'), (30, 12, 10), (200, 120, 100), (120, 46, 36)), 'urias')
save(colorize(load('krauser'), (26, 28, 26), (196, 198, 192), (110, 114, 106)), 'soldat')

# ---- remaining RE6/7/8
save(colorize(load('giant_moth'), (30, 18, 10), (232, 208, 150), (130, 100, 60)), 'marguerite')
for sfx in ('', '_mutated', '_final'): save(colorize(load('saddler' + sfx), (14, 8, 28), (206, 196, 232), (80, 60, 120)), 'miranda' + sfx)
for sfx in ('', '_phase2'): save(colorize(load('plant42' + sfx), (30, 12, 16), (210, 150, 90), (110, 50, 40)), 'haos' + sfx)
save(colorize(load('larva'), (24, 28, 10), (196, 214, 120), (100, 120, 40)), 'gnezdo')
