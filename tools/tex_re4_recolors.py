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
