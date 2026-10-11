import sys, os; sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from genfam import gen
def A(hp, sp, dm=6.0, fr=28, kb=None, ar=None):
    return '.add(Attributes.MAX_HEALTH, %s).add(Attributes.MOVEMENT_SPEED, %s).add(Attributes.ATTACK_DAMAGE, %s).add(Attributes.FOLLOW_RANGE, %s)' % (hp, sp, dm, fr) + (f'.add(Attributes.KNOCKBACK_RESISTANCE, {kb})' if kb else '') + (f'.add(Attributes.ARMOR, {ar})' if ar else '')
fams = [
 dict(id='marguerite', C='Marguerite', pk='marguerite', pent='giantmoth.GiantMothEntity', pe='GiantMothEntity', pmod='giantmoth.GiantMothModel', pm='GiantMothModel', attrs='.add(Attributes.MAX_HEALTH, 90.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FLYING_SPEED, 0.65).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 32.0)', scale='1.6f', shadow='0.7f', xp=100,
      doc='Marguerite Baker (RE7): the insect matriarch - a large hovering swarm-lady with the Giant Moth dust cloud, ramming dive, jab and wing buffet.'),
 dict(id='miranda', C='Miranda', pk='miranda', pent='saddler.SaddlerEntity', pe='SaddlerEntity', pmod='nemesis.NemesisModel', pm='NemesisModel', ret='NemesisEntity', retimp='nemesis.NemesisEntity', attrs=A(420.0, 0.28, 10.0, 52, 0.85, 6.0), scale='1.0f', shadow='0.6f', xp=300,
      doc='Mother Miranda (RE8): the cloaked matriarch. Saddler behaviour (lashes, snares, tentacle barrage) with a sturdier, faster frame; mutates twice.'),
 dict(id='haos', C='Haos', pk='haos', pent='plant42.Plant42Entity', pe='Plant42Entity', pmod='plant42.Plant42Model', pm='Plant42Model', attrs=A(420.0, 0.0, 10.0, 28, 1.0, 6.0), scale='1.3f', shadow='2.0f', xp=250,
      doc='Haos (RE6): the shifting mass - Plant 42 behaviour (tentacle whip, snare-devour, spores, slam, weak base, withering phase) at a large scale.'),
 dict(id='gnezdo', C='Gnezdo', pk='gnezdo', pent='larva.LarvaEntity', pe='LarvaEntity', pmod='larva.LarvaModel', pm='LarvaModel', attrs=A(50.0, 0.26, 7.0, 24), scale='1.3f', shadow='0.8f', xp=20,
      doc='Gnezdo (RE6): the C-virus grub - Larva bites, rearing lunges and acid spit with a tougher body.'),
]
for f in fams: gen(f)
