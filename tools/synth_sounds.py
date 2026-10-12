"""Synthesises the creature voice set (mono ogg) + assets/reevil/sounds.json. Archetypes: groan, growl, hiss, roar, screech, squelch.
Kinds: ambient (2 variants), hurt (2), death, attack. Run: python3 tools/synth_sounds.py  (needs numpy + ffmpeg)."""
import numpy as np, subprocess, os, json, tempfile, wave
SR = 22050
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/resources/assets/reevil/')
rng = np.random.default_rng(42)

def t_(d): return np.arange(int(SR * d)) / SR
def env(n, a=0.05, r=0.3, curve=1.0):
    e = np.ones(n); na, nr = max(1, int(n * a)), max(1, int(n * r))
    e[:na] = np.linspace(0, 1, na) ** curve; e[-nr:] = np.linspace(1, 0, nr) ** curve; return e
def osc(f, shape='saw'):
    ph = np.cumsum(f) / SR; p = ph % 1.0
    if shape == 'saw': return 2 * p - 1
    if shape == 'sq': return np.sign(np.sin(2 * np.pi * ph)) * 0.8
    return np.sin(2 * np.pi * ph)
def noise(n): return rng.standard_normal(n)
def band(x, lo, hi):
    X = np.fft.rfft(x); fr = np.fft.rfftfreq(len(x), 1 / SR)
    m = np.exp(-((np.clip(fr, lo, hi) - fr) / (0.25 * max(lo, 40))) ** 2) * (fr > 0)
    return np.fft.irfft(X * m, len(x))
def formant(x, centers, bw=0.35):
    X = np.fft.rfft(x); fr = np.fft.rfftfreq(len(x), 1 / SR); m = np.zeros_like(fr)
    for c in centers: m += np.exp(-((fr - c) / (c * bw)) ** 2)
    return np.fft.irfft(X * m, len(x))
def norm(x, peak=0.85): return x / (np.max(np.abs(x)) + 1e-9) * peak
def lin(d, a, b): return np.linspace(a, b, int(SR * d))
def vib(n, rate, depth): return 1 + depth * np.sin(2 * np.pi * rate * np.arange(n) / SR)

def groan(d, base, variant=0):
    n = int(SR * d); f = lin(d, base * 1.15, base * 0.8) * vib(n, 5 + variant, 0.05)
    x = osc(f) * 0.7 + noise(n) * 0.25; x = formant(x, [500 + 80 * variant, 900, 2400], 0.3)
    return norm(x * env(n, 0.25, 0.45) * (1 + 0.3 * np.sin(2 * np.pi * 7 * np.arange(n) / SR)))
def growl(d, base, variant=0):
    n = int(SR * d); f = lin(d, base, base * 0.9) * vib(n, 28, 0.15)
    am = 0.55 + 0.45 * np.sin(2 * np.pi * (30 + 4 * variant) * np.arange(n) / SR)
    x = (osc(f) * 0.6 + noise(n) * 0.5) * am; x = formant(x, [250, 700, 1500], 0.4)
    return norm(x * env(n, 0.12, 0.4))
def hiss(d, lo, hi, variant=0):
    n = int(SR * d); x = band(noise(n), lo, hi) + 0.15 * band(noise(n), 200, 900)
    return norm(x * env(n, 0.3, 0.5, 1.5) * (1 + 0.2 * np.sin(2 * np.pi * 11 * np.arange(n) / SR)))
def roar(d, base, variant=0):
    n = int(SR * d); f = lin(d, base * 1.4, base * 0.7) * vib(n, 9, 0.08)
    x = osc(f) * 0.6 + osc(f * 1.5, 'sq') * 0.2 + noise(n) * 0.45; x = formant(x, [180, 520, 1100, 2200], 0.35)
    x = np.tanh(2.5 * x) * env(n, 0.08, 0.5)
    return norm(x)
def screech(d, f0, f1, variant=0):
    n = int(SR * d); f = lin(d, f0, f1) * vib(n, 14 + 3 * variant, 0.06)
    x = np.tanh(3 * osc(f, 'sin') + 1.0 * osc(f * 2, 'sin')) * env(n, 0.06, 0.4)
    return norm(x)
def squelch(d, variant=0):
    n = int(SR * d); x = band(noise(n), 150, 1400); am = 0.5 + 0.5 * np.sin(2 * np.pi * (18 + 6 * variant) * np.arange(n) / SR) ** 2
    x = x * am + 0.7 * osc(lin(d, 180, 55), 'sin') * env(n, 0.02, 0.6)
    return norm(x * env(n, 0.04, 0.5))

ARCH = {
 'groan':   dict(ambient=lambda v: groan(1.3 + 0.2 * v, 95 + 12 * v, v), hurt=lambda v: groan(0.45, 150 + 20 * v, v), death=lambda v: groan(1.6, 110, 2), attack=lambda v: groan(0.6, 130, 3)),
 'growl':   dict(ambient=lambda v: growl(1.1 + 0.2 * v, 110 + 10 * v, v), hurt=lambda v: growl(0.4, 260, v), death=lambda v: growl(1.2, 200, 2), attack=lambda v: growl(0.55, 170, 3)),
 'hiss':    dict(ambient=lambda v: hiss(1.0 + 0.2 * v, 3500, 9000, v), hurt=lambda v: hiss(0.35, 2500, 8000, v), death=lambda v: hiss(1.4, 1800, 7000, 2), attack=lambda v: hiss(0.5, 3000, 9000, 3)),
 'roar':    dict(ambient=lambda v: roar(1.2 + 0.2 * v, 70 + 8 * v, v), hurt=lambda v: roar(0.5, 110 + 15 * v, v), death=lambda v: roar(1.8, 80, 2), attack=lambda v: roar(0.9, 90, 3)),
 'screech': dict(ambient=lambda v: screech(0.45 + 0.1 * v, 2200 + 300 * v, 3400, v), hurt=lambda v: screech(0.25, 3600, 2000, v), death=lambda v: screech(0.8, 3200, 700, 2), attack=lambda v: screech(0.4, 1800, 4200, 3)),
 'squelch': dict(ambient=lambda v: squelch(0.9 + 0.2 * v, v), hurt=lambda v: squelch(0.3, v), death=lambda v: squelch(1.3, 2), attack=lambda v: squelch(0.5, 3)),
}
VARIANTS = {'ambient': 2, 'hurt': 2, 'death': 1, 'attack': 1}
snd = {}
os.makedirs(OUT + 'sounds/voice', exist_ok=True)
for arch, kinds in ARCH.items():
    for kind, fn in kinds.items():
        names = []
        for v in range(VARIANTS[kind]):
            x = fn(v); x = np.clip(x, -1, 1)
            fname = f'{arch}_{kind}{v}'
            with tempfile.NamedTemporaryFile(suffix='.wav', delete=False) as tf:
                w = wave.open(tf.name, 'wb'); w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes((x * 32000).astype('<i2').tobytes()); w.close()
            subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-i', tf.name, '-c:a', 'libvorbis', '-q:a', '3', OUT + f'sounds/voice/{fname}.ogg'], check=True)
            os.unlink(tf.name)
            names.append({'name': f'reevil:voice/{fname}', 'volume': 1.0})
        snd[f'voice.{arch}.{kind}'] = {'sounds': names, 'subtitle': f'subtitles.reevil.voice.{arch}'}
json.dump(snd, open(OUT + 'sounds.json', 'w'), indent=1)
lang_p = OUT + 'lang/en_us.json'; d = json.load(open(lang_p))
for arch in ARCH: d[f'subtitles.reevil.voice.{arch}'] = {'groan': 'Creature groans', 'growl': 'Creature growls', 'hiss': 'Creature hisses', 'roar': 'Creature roars', 'screech': 'Creature screeches', 'squelch': 'Creature squelches'}[arch]
json.dump(d, open(lang_p, 'w'), indent=1, ensure_ascii=False)
print('wrote', sum(len(v['sounds']) for v in snd.values()), 'sounds')
