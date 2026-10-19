"""RE7 roster additions (Molded types, insects, nest, spiders, Mia).  Run: python3 tools/variants_re7.py"""
import sys, os; sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import variants_remake as vr
from variants_remake import *
PM = P('ganado', 'Ganado', 'GanadoEntity', tex='molded'); PLV = P('larva', 'Larva', 'LarvaEntity', tex='larva')
PMO = P('giantmoth', 'GiantMoth', 'GiantMothEntity', tex='giant_moth'); PSP = P('giantspider', 'GiantSpider', 'GiantSpiderEntity', tex='giant_spider')
PIV = P('ivy', 'Ivy', 'Plant42Entity', 'plant42', tex='ivy'); PJ = P('jill', 'Jill', 'MrXEntity', 'mrx', tex='jill')
for p_ in (PM,):  # parent class lives in the ganado package
    pass
FORM = lambda n: '\n    { setForm(%d); }\n    @Override protected boolean canBurst() { return false; }\n' % n
MOLD = D(c=(20, 20, 24), n=0.12, d='veins'); TOOTH = D(c=(214, 206, 180), n=0.05)
fam('quick_molded', 'QuickMolded', PM, 're7', 'Quick Molded', 'クイックモールデッド', 0.6, 1.8, '0x18181c', '0x707078', hpx=0.5, spx=1.7, dmx=0.9, scale='0.95f', xp=10, extra=FORM(2), doc='RE7 Quick Molded: ripper-type, low HP, very fast blade arms.')
fam('blade_molded', 'BladeMolded', PM, 're7', 'Blade Molded', 'ブレードモールデッド', 0.6, 2.0, '0x18181c', '0xa0a0a8', hpx=1.5, spx=1.0, dmx=1.3, scale='1.05f', xp=18, extra=FORM(2), doc='RE7 Blade Molded: both arms are blades.')
fam('arm_molded', 'ArmMolded', PM, 're7', 'Arm Molded', '腕変異モールデッド', 0.9, 2.2, '0x18181c', '0x3a3a44', hpx=2.2, spx=0.85, dmx=1.6, scale='1.1f', xp=25, extra='\n    @Override protected boolean canBurst() { return false; }\n', doc='RE7 Molded with one giant arm.')
fam('molded_fang', 'MoldedFang', PM, 're7', 'Fang Molded', '牙モールデッド', 0.6, 2.0, '0x18181c', '0xd6ceb4', hpx=1.1, spx=1.1, doc='RE7 Molded with a fang-filled mouth.')
fam('molded_eyeless', 'MoldedEyeless', PM, 're7', 'Eyeless Molded', '無眼モールデッド', 0.6, 2.0, '0x18181c', '0x101014', hpx=1.1, doc='RE7 Molded without eyes; hunts by sound.')
fam('bug_larva', 'BugLarva', PLV, 're7', 'Cannibal Bug Larva', '人食い虫(幼虫)', 0.6, 0.4, '0xb8b070', '0x504830', hpx=0.5, scale='0.8f', xp=3, doc='RE7 cannibal bug larva.')
fam('bug_adult', 'BugAdult', PLV, 're7', 'Cannibal Bug', '人食い虫(成虫)', 0.9, 0.7, '0x60503a', '0xb89050', hpx=1.5, spx=1.2, dmx=1.3, scale='1.3f', xp=8, doc='RE7 adult cannibal bug.')
fam('bug_large', 'BugLarge', PMO, 're7', 'Large Flying Insect', '大型飛行昆虫', 0.8, 0.8, '0x4a5a30', '0xc8d080', hpx=0.9, scale='1.0f', xp=8, doc='RE7 large flying insect.')
fam('bug_small', 'BugSmall', PMO, 're7', 'Small Flying Insect', '小型飛行昆虫', 0.3, 0.3, '0x4a5a30', '0xc8d080', hpx=0.1, spx=1.3, dmx=0.3, scale='0.35f', shadow='0.2f', xp=1, doc='RE7 small flying insect.')
fam('small_spider', 'SmallSpider', PSP, 're7', 'Small Spider', '小型クモ', 0.6, 0.4, '0x20181a', '0x80302a', hpx=0.2, spx=1.2, dmx=0.5, scale='0.4f', shadow='0.3f', xp=3, doc='RE7 small spider.')
fam('insect_nest', 'InsectNest', PIV, 're7', 'Insect Nest', '虫の巣', 1.0, 1.6, '0x50402a', '0xc0b070', hpx=0.9, scale='0.8f', xp=20, doc='RE7 insect nest: releases bugs until destroyed.')
fam('mia', 'Mia', PJ, 're7', 'Mia Winters', 'ミア・ウィンターズ', 0.6, 1.7, '0xd8c8a0', '0x305080', hpx=0.5, spx=0.8, dmx=0.8, scale='0.95f', xp=40, doc='RE7 Mia: hostile when infected by Eveline.')
S('quick_molded', PM, 0, [A('base/torso/spikes', Z, (-1, -11, 2, 2, 4, 2, 'm'), (-1, -7, 2.5, 2, 4, 2, 'm'))], dict(m=MOLD), W=128, H0=64)
S('blade_molded', PM, 0, [A('base/torso/shL', Z, (-7, -12, -2, 3, 3, 4, 'm'), (-9, -14, -1, 2, 4, 2, 'm')), A('base/torso/shR', Z, (4, -12, -2, 3, 3, 4, 'm'), (7, -14, -1, 2, 4, 2, 'm'))], dict(m=MOLD), W=128, H0=64)
S('arm_molded', PM, 0, [A('base/torso/armR/giant', Z, (-4, 0, -4, 8, 14, 8, 'm'), (-5, 12, -5, 10, 9, 10, 'm'), (-5, 21, -4, 2, 4, 2, 'm'), (3, 21, -4, 2, 4, 2, 'm'))], dict(m=MOLD), W=128, H0=64)
S('molded_fang', PM, 0, [A('base/torso/neck/head/fangs', Z, (-3.4, -2, -3.9, 1, 2, 1, 't'), (-1.5, -2, -3.9, 1, 2, 1, 't'), (0.5, -2, -3.9, 1, 2, 1, 't'), (2.4, -2, -3.9, 1, 2, 1, 't'), (-3.5, -3, -4, 7, 1, 1, 'mo'))], dict(t=TOOTH, mo=MOUTH), W=128, H0=64)
S('molded_eyeless', PM, 0, [A('base/torso/neck/head/blank', Z, (-3.6, -6.5, -3.9, 7.2, 4, 0.5, 'm'))], dict(m=MOLD), W=128, H0=64)
from PIL import Image, ImageChops
TINT = {'bug_larva': (1.1, 1.0, 0.6), 'bug_adult': (0.8, 0.65, 0.45), 'bug_large': (0.7, 0.9, 0.5), 'bug_small': (0.6, 0.8, 0.45), 'small_spider': (0.5, 0.45, 0.5), 'insect_nest': (0.9, 0.75, 0.5), 'mia': (1.0, 1.0, 1.1)}
def tint():
    for i, c in TINT.items():
        for fn in os.listdir(OUT):
            if fn.startswith(i) and fn.endswith('.png') and (fn == i + '.png' or fn.startswith(i + '_')):
                im = Image.open(OUT + '/' + fn).convert('RGBA'); r, g, b, a = im.split()
                im = Image.merge('RGBA', (r.point(lambda v: min(255, int(v * c[0]))), g.point(lambda v: min(255, int(v * c[1]))), b.point(lambda v: min(255, int(v * c[2]))), a)); im.save(OUT + '/' + fn)
if __name__ == '__main__':
    ids = sys.argv[1:] or [f['id'] for f in R if f['tab'] == 're7']
    rm.write_clips()
    for f in R:
        if f['id'] in ids:
            build(f)
            if f['id'] in SPECS: vgen(SPECS[f['id']])
    tint()
