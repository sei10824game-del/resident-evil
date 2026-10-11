"""Parse texOffs(u,v).addBox(x,y,z,w,h,d) cubes out of a *Model.java so painted UVs always match the model.
Cubes are attributed to the nearest preceding quoted part name. Cubes whose arguments are not numeric literals are skipped (list in .skipped)."""
import re, os
from texlib import Tex, shade

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/java/com/reevil/client')

def _num(s):
    s = s.strip()
    s = re.sub(r'(?<=[0-9.])[fFdD]\b', '', s)
    s = s.replace('F', '').replace('f', '') if re.fullmatch(r'[-+0-9.fF*/() ]+', s) else s
    return float(eval(s, {'__builtins__': {}}, {}))

def parse(path):
    src = open(os.path.join(ROOT, path)).read()
    m = re.search(r'LayerDefinition\.create\(\s*\w+\s*,\s*(\d+)\s*,\s*(\d+)\s*\)', src)
    W, H = int(m.group(1)), int(m.group(2))
    cubes, skipped = [], []
    name_pos = [(mm.start(), mm.group(1)) for mm in re.finditer(r'(?:addOrReplaceChild|reg)\(\s*"([^"]+)"', src)]
    for mm in re.finditer(r'texOffs\(([^()]*)\)\s*(?:\.mirror\(\))?\s*\.addBox\(([^()]*)\)', src):
        owner = 'root'
        for pos, nm in name_pos:
            if pos < mm.start(): owner = nm
        try:
            u, v = [int(_num(x)) for x in mm.group(1).split(',')]
            a = [_num(x) for x in mm.group(2).split(',')]
            x, y, z, w, h, d = a[:6]
            cubes.append(dict(name=owner, u=u, v=v, w=int(w), h=int(h), d=int(d)))
        except Exception:
            skipped.append((owner, mm.group(0)))
    return W, H, cubes, skipped

class Paint:
    def __init__(self, model_path, seed=1):
        self.W, self.H, self.cubes, self.skipped = parse(model_path)
        self.t = Tex(self.W, self.H, seed)
        self.rng = self.t.rng

    def add(self, name, u, v, w, h, d):
        self.cubes.append(dict(name=name, u=u, v=v, w=w, h=h, d=d))

    def faces(self, c):
        return self.t.faces(c['u'], c['v'], c['w'], c['h'], c['d'])

    # ---- surface primitives (x,y,w,h = pixel rect)
    def fill(self, r, base, noise=0.08, grad=0.0):
        x, y, w, h = r; self.t.rect(x, y, w, h, base, noise, grad)

    def streak(self, r, base, vertical=True, amt=0.12):
        x, y, w, h = r
        n = w if vertical else h
        for k in range(n):
            col = self.rng.uniform(-amt, amt)
            for j in range(h if vertical else w):
                f = 1 + col + self.rng.uniform(-0.05, 0.05)
                px, py = (x + k, y + j) if vertical else (x + j, y + k)
                self.t.px_set(px, py, shade(base, f))

    def scales(self, r, base, size=3, hi=1.18, lo=0.78):
        x, y, w, h = r
        for j in range(h):
            for i in range(w):
                off = (size // 2) if (j // size) % 2 else 0
                cx, cy = (i + off) % size, j % size
                f = hi if (cx == 0 or cy == 0) is False and cx == 1 and cy == 1 else (lo if (cx == size - 1 or cy == size - 1) else 1.0)
                self.t.px_set(x + i, y + j, shade(base, f * self.rng.uniform(0.94, 1.06)))

    def bands(self, r, c1, c2, period=4, vertical=False):
        x, y, w, h = r
        for j in range(h):
            for i in range(w):
                k = (j if not vertical else i) // period % 2
                self.t.px_set(x + i, y + j, shade(c1 if k == 0 else c2, self.rng.uniform(0.92, 1.08)))

    def blots(self, r, c, n, size=2):
        self.t.blots(r[0], r[1], r[2], r[3], c if len(c) == 4 else c + (255,), n, size)

    def veins(self, r, c, n=4):
        x, y, w, h = r
        for _ in range(n):
            px, py = x + self.rng.randrange(w), y + self.rng.randrange(h)
            for _ in range(self.rng.randint(3, max(4, h // 2))):
                self.t.px_set(px, py, c + (255,) if len(c) == 3 else c)
                px = min(max(px + self.rng.choice((-1, 0, 0, 1)), x), x + w - 1)
                py = min(max(py + self.rng.choice((0, 1, 1)), y), y + h - 1)

    def wound(self, r, flesh, blood, bone=None):
        x, y, w, h = r
        if w < 3 or h < 3: return
        cx, cy = x + self.rng.randrange(1, w - 1), y + self.rng.randrange(1, h - 1)
        rw, rh = self.rng.randint(1, max(1, w // 3)), self.rng.randint(1, max(1, h // 3))
        for j in range(-rh - 1, rh + 2):
            for i in range(-rw - 1, rw + 2):
                px, py = cx + i, cy + j
                if not (x <= px < x + w and y <= py < y + h): continue
                d = (i / (rw + 1)) ** 2 + (j / (rh + 1)) ** 2
                if d <= 0.45 and bone: self.t.px_set(px, py, shade(bone, self.rng.uniform(0.9, 1.05)))
                elif d <= 0.85: self.t.px_set(px, py, shade(flesh, self.rng.uniform(0.85, 1.15)))
                elif d <= 1.2: self.t.px_set(px, py, shade(blood, self.rng.uniform(0.8, 1.1)))

    def drips(self, r, c, n=3):
        x, y, w, h = r
        for _ in range(n):
            px, py = x + self.rng.randrange(w), y + self.rng.randrange(max(1, h // 2))
            for j in range(self.rng.randint(2, max(3, h // 2))):
                self.t.px_set(px, py + j, shade(c, 1 - j * 0.04))

    def dot(self, x, y, c, s=1):
        for j in range(s):
            for i in range(s): self.t.px_set(x + i, y + j, c if len(c) == 4 else c + (255,))

    def paint(self, rules, default):
        """rules: list of (regex, fn(paint, face_name, rect, cube)); first match wins; default fn otherwise."""
        for c in self.cubes:
            fn = default
            for pat, f in rules:
                if re.fullmatch(pat, c['name']): fn = f; break
            for face, rect in self.faces(c).items():
                if rect[2] > 0 and rect[3] > 0: fn(self, face, rect, c)

    def save(self, name):
        return self.t.save(name)

    def report(self):
        print(f'{len(self.cubes)} cubes, {len(self.skipped)} skipped (non-literal)')
        for s in self.skipped[:12]: print('  skipped:', s[0], s[1][:70])


def muscle(p, r, base=(150, 30, 32), hi=(190, 62, 58), lo=(96, 14, 18), vertical=True):
    """Striated raw muscle: fibre columns, wet highlights, dark seams."""
    x, y, w, h = r
    for i in range(w if vertical else h):
        k = p.rng.random()
        c = hi if k > 0.78 else (lo if k < 0.22 else base)
        for j in range(h if vertical else w):
            px, py = (x + i, y + j) if vertical else (x + j, y + i)
            p.t.px_set(px, py, shade(c, p.rng.uniform(0.86, 1.1)))


def teeth(p, r, c=(226, 214, 176), gap=2):
    x, y, w, h = r
    for i in range(0, w, gap):
        for j in range(h): p.t.px_set(x + i, y + j, shade(c, 1 - 0.05 * j))


def front_of(p, name):
    """face rects of the first cube with this part name."""
    for c in p.cubes:
        if c['name'] == name: return p.faces(c)
    raise KeyError(name)
