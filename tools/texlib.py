"""Texture helper for Minecraft cube UV layouts (PIL). Use: t=Tex(128,64); t.box(u,v,w,h,d, base, noise=..); t.save('name')"""
import random, os
from PIL import Image, ImageDraw

OUT = os.path.join(os.path.dirname(__file__), '..', 'src/main/resources/assets/reevil/textures/entity')

def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + ((c[3],) if len(c) > 3 else (255,))

class Tex:
    def __init__(s, w, h, seed=1):
        s.w, s.h = w, h
        s.img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
        s.px = s.img.load()
        s.rng = random.Random(seed)

    def faces(s, u, v, w, h, d):
        """Minecraft cube UV: returns dict face -> (x,y,w,h). Matches ModelPart.texOffs(u,v).addBox(w,h,d)."""
        return {
            'top': (u + d, v, w, d), 'bottom': (u + d + w, v, w, d),
            'right': (u, v + d, d, h), 'front': (u + d, v + d, w, h),
            'left': (u + d + w, v + d, d, h), 'back': (u + d + w + d, v + d, w, h),
        }

    def rect(s, x, y, w, h, base, noise=0.08, grad=0.0):
        for j in range(h):
            for i in range(w):
                f = 1 + s.rng.uniform(-noise, noise) - grad * (j / max(1, h - 1))
                if 0 <= x + i < s.w and 0 <= y + j < s.h:
                    s.px[x + i, y + j] = shade(base, f)

    def box(s, u, v, w, h, d, base, noise=0.08, top=None, bottom=None, front=None, grad=0.15):
        """Fill all 6 faces; top lighter, bottom darker, optional per-face colors."""
        for name, (x, y, fw, fh) in s.faces(u, v, w, h, d).items():
            c = base
            if name == 'top': c = top or shade(base, 1.15)
            if name == 'bottom': c = bottom or shade(base, 0.7)
            if name == 'front' and front: c = front
            s.rect(x, y, fw, fh, c, noise, grad if name not in ('top', 'bottom') else 0)

    def px_set(s, x, y, c):
        if 0 <= x < s.w and 0 <= y < s.h: s.px[x, y] = c if len(c) == 4 else c + (255,)

    def line(s, x0, y0, x1, y1, c):
        ImageDraw.Draw(s.img).line([(x0, y0), (x1, y1)], fill=c if len(c) == 4 else c + (255,))

    def blots(s, x, y, w, h, c, n, size=2):
        """Random wound/blood/stain blots inside a rect."""
        for _ in range(n):
            bx, by = x + s.rng.randrange(w), y + s.rng.randrange(h)
            for i in range(s.rng.randint(1, size)):
                for j in range(s.rng.randint(1, size)):
                    s.px_set(bx + i, by + j, c)

    def save(s, name):
        os.makedirs(OUT, exist_ok=True)
        p = os.path.join(OUT, name + '.png')
        s.img.save(p)
        return p
