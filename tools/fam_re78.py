import sys, os; sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from genfam import gen
def A(hp, sp, dm=6.0, fr=28, kb=None, ar=None):
    return '.add(Attributes.MAX_HEALTH, %s).add(Attributes.MOVEMENT_SPEED, %s).add(Attributes.ATTACK_DAMAGE, %s).add(Attributes.FOLLOW_RANGE, %s)' % (hp, sp, dm, fr) + (f'.add(Attributes.KNOCKBACK_RESISTANCE, {kb})' if kb else '') + (f'.add(Attributes.ARMOR, {ar})' if ar else '')
MRX = dict(pmod='mrx.MrXModel', pm='MrXModel', ret='MrXEntity', retimp='mrx.MrXEntity')
REGEN = '''
    /** knits itself back together: 2 HP a second when left alone. */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isAlive() && tickCount % 20 == 0 && getHealth() < getMaxHealth()) heal(tickCount - getLastHurtByMobTimestamp() > 60 ? 3f : 1f);
    }
'''
fams = [
 dict(id='molded', C='Molded', pk='molded', pent='ganado.GanadoEntity', pe='GanadoEntity', pmod='ganado.GanadoModel', pm='GanadoModel', ret='GanadoEntity', retimp='ganado.GanadoEntity', attrs=A(42.0, 0.3, 6.0, 30), scale='1.0f', shadow='0.5f', xp=12,
      extra='''
    @Override protected int defaultWeapon() { return W_NONE; }
    @Override public boolean hasHat() { return false; }
    /** the mould is unstable: it can erupt into whip, blade-arm or spider forms soon after being hurt. */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isAlive() && getForm() == 0 && getHealth() < getMaxHealth() * 0.8f && random.nextInt(80) == 0) triggerBurst();
    }
''', doc='Molded (RE7): the black-goo humanoid. Ganado grabs and strikes, and its body erupts into tendril, blade-arm or many-legged forms once wounded.'),
 dict(id='fat_molded', C='FatMolded', pk='fatmolded', pent='zombie.ZombieEntity', pe='ZombieEntity', pmod='zombie.ZombieModel', pm='ZombieModel', attrs=A(160.0, 0.15, 9.0, 24, 0.7, 2.0), scale='1.4f', shadow='1.0f', xp=35,
      extra='''
    @Override protected boolean revives() { return false; }
    @Override public String getTextureSuffix() { return ""; }
''', doc='Fat Molded (RE7): the bloated brute - slow zombie moves behind a mountain of mould and health; does not rise again.'),
 dict(id='jack_baker', C='JackBaker', pk='jackbaker', pent='mrx.MrXEntity', pe='MrXEntity', attrs=A(340.0, 0.3, 11.0, 48, 0.8, 4.0), scale='1.05f', shadow='0.6f', xp=150, **MRX,
      extra='''
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
''' + REGEN, doc='Jack Baker (RE7): the regenerating, relentless patriarch - the stalker move set and door smashing with a hardy self-healing body.'),
 dict(id='eveline', C='Eveline', pk='eveline', pent='saddler.SaddlerEntity', pe='SaddlerEntity', pmod='nemesis.NemesisModel', pm='NemesisModel', ret='NemesisEntity', retimp='nemesis.NemesisEntity', attrs=A(360.0, 0.26, 9.0, 48, 0.8, 5.0), scale='0.95f', shadow='0.6f', xp=250,
      doc='Eveline (RE7): the mould-queen. Saddler behaviour - lashes, snares, the all-round tentacle barrage - mutating as she is wounded.'),
 dict(id='lycan', C='Lycan', pk='lycan', pent='hunter.HunterEntity', pe='HunterEntity', pmod='hunter.HunterModel', pm='HunterModel', attrs=A(50.0, 0.38, 6.0, 32, 0.2, 1.0), scale='1.05f', shadow='0.6f', xp=18,
      extra='''
    @Override public String getTextureSuffix() { return ""; }
''', doc='Lycan (RE8): the village werewolf. Hunter behaviour - great leaps, double slashes, decapitation chance, dodge-hops - in a lighter, faster, furred body.'),
 dict(id='dimitrescu', C='Dimitrescu', pk='dimitrescu', pent='ustanak.UstanakEntity', pe='UstanakEntity', pmod='mrx.MrXModel', pm='MrXModel', ret='MrXEntity', retimp='mrx.MrXEntity', attrs=A(700.0, 0.27, 13.0, 48, 0.95, 6.0), scale='1.35f', shadow='1.2f', xp=300,
      doc='Lady Dimitrescu (RE8): the towering matriarch with bladed claws - the clawed stalker set (punch, stab, sweep, ground pound, claw grab) on a very tall, very tough body.'),
 dict(id='daughter', C='Daughter', pk='daughter', pent='reaper.ReaperEntity', pe='ReaperEntity', pmod='huntergamma.HunterGammaModel', pm='HunterGammaModel', ret='LickerEntity', retimp='licker.LickerEntity', attrs=A(80.0, 0.38, 7.0, 30, 0.1, 2.0), scale='1.1f', shadow='0.6f', xp=60,
      doc='Dimitrescu daughter (RE8): the pale clawed hunters - sighted tongue / claw / pounce crawling attacks.'),
 dict(id='heisenberg', C='Heisenberg', pk='heisenberg', pent='mrx.MrXEntity', pe='MrXEntity', attrs=A(480.0, 0.27, 12.0, 48, 0.9, 6.0), scale='1.1f', shadow='0.7f', xp=250, **MRX,
      extra='''
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
''', doc='Karl Heisenberg (RE8): the factory lord - heavy swings, combos, throws and kicks on the stalker frame.'),
 dict(id='moreau', C='Moreau', pk='moreau', pent='dellago.DelLagoEntity', pe='DelLagoEntity', pmod='neptune.NeptuneModel', pm='NeptuneModel', ret='NeptuneEntity', retimp='neptune.NeptuneEntity', attrs=A(320.0, 0.45, 12.0, 40, 1.0, 4.0), scale='1.6f', shadow='0f', xp=200,
      doc='Salvatore Moreau (RE8): the reservoir fish-horror - Neptune ambush, breach, death-roll and tail-slap behaviour.'),
 dict(id='sturm', C='Sturm', pk='sturm', pent='ogroman.OgromanEntity', pe='OgromanEntity', pmod='mrx.MrXModel', pm='MrXModel', ret='MrXEntity', retimp='mrx.MrXEntity', attrs=A(620.0, 0.26, 14.0, 40, 1.0, 7.0), scale='1.7f', shadow='1.5f', xp=300,
      doc='Sturm (RE8): the colossus - Ogroman reach and weight with a faster stride.'),
 dict(id='urias', C='Urias', pk='urias', pent='ustanak.UstanakEntity', pe='UstanakEntity', pmod='mrx.MrXModel', pm='MrXModel', ret='MrXEntity', retimp='mrx.MrXEntity', attrs=A(600.0, 0.3, 14.0, 52, 0.95, 8.0), scale='1.3f', shadow='1.1f', xp=280,
      doc='Urias (RE8): the clawed war-machine - the Ustanak claw set on a bulkier body.'),
 dict(id='soldat', C='Soldat', pk='soldat', pent='krauser.KrauserEntity', pe='KrauserEntity', pmod='krauser.KrauserModel', pm='KrauserModel', ret='GanadoEntity', retimp='ganado.GanadoEntity', attrs=A(60.0, 0.28, 6.0, 36, 0.3, 4.0), scale='1.05f', shadow='0.5f', xp=30,
      doc='Soldat (RE8): the masked rifleman - Krauser fire-and-melee behaviour (long-range shots, knife lunges).'),
]
for f in fams: gen(f)
