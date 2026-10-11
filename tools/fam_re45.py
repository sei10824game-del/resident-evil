import sys, os; sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from genfam import gen
def A(hp, sp, dm=6.0, fr=28, kb=None, ar=None):
    return '.add(Attributes.MAX_HEALTH, %s).add(Attributes.MOVEMENT_SPEED, %s).add(Attributes.ATTACK_DAMAGE, %s).add(Attributes.FOLLOW_RANGE, %s)' % (hp, sp, dm, fr) + (f'.add(Attributes.KNOCKBACK_RESISTANCE, {kb})' if kb else '') + (f'.add(Attributes.ARMOR, {ar})' if ar else '')
mrx = dict(pent='mrx.MrXEntity', pe='MrXEntity', pmod='mrx.MrXModel', pm='MrXModel')
IMP_FX = 'import com.reevil.entity.AttackMove;\nimport net.minecraft.world.effect.MobEffectInstance;\nimport net.minecraft.world.effect.MobEffects;\nimport net.minecraft.world.entity.LivingEntity;\n'
fams = [
 dict(id='majini', C='Majini', pk='majini', pent='ganado.GanadoEntity', pe='GanadoEntity', pmod='ganado.GanadoModel', pm='GanadoModel', attrs=A(34.0, 0.26, 5.0), scale='1.0f', shadow='0.5f', xp=8,
      doc='Majini (RE5): the African-village infected. Ganado behaviour - random tool, grab-and-choke, and the Plaga (Cephalo) bursting from the neck into whip / scythe / spider forms.'),
 dict(id='zealot', C='Zealot', pk='zealot', pent='ganado.GanadoEntity', pe='GanadoEntity', pmod='ganado.GanadoModel', pm='GanadoModel', attrs=A(30.0, 0.26, 5.0), scale='1.0f', shadow='0.5f', xp=8,
      extra='''
    @Override protected int defaultWeapon() { return random.nextBoolean() ? W_TORCH : W_NONE; }
    @Override public boolean hasHat() { return false; }
    @Override protected boolean canBurst() { return false; }
''', doc='Zealot (RE4): hooded Los Illuminados cultist - Ganado melee (torch or bare hands), never bursts into a Plaga.'),
 dict(ret='GanadoEntity', retimp='ganado.GanadoEntity', id='chainsaw_majini', C='ChainsawMajini', pk='chainsawmajini', pent='salvador.SalvadorEntity', pe='SalvadorEntity', pmod='salvador.SalvadorModel', pm='SalvadorModel', attrs=A(80.0, 0.27, 8.0, 28, 0.5, 3.0), scale='1.2f', shadow='0.6f', xp=35,
      doc='Chainsaw Majini (RE5): the Salvador sweep / rush / beheading with a hooded head instead of a sack.'),
 dict(id='armadura', C='Armadura', pk='armadura', pent='zombie.ZombieEntity', pe='ZombieEntity', pmod='zombie.ZombieModel', pm='ZombieModel', attrs=A(120.0, 0.15, 8.0, 22, 0.8, 14.0), scale='1.1f', shadow='0.6f', xp=25,
      extra='''
    @Override protected boolean revives() { return false; }
    @Override public String getTextureSuffix() { return ""; }
''', doc='Armadura (RE4): the haunted suit of armour. Slow zombie moves behind extreme armour that blunts everything but heavy hits.'),
 dict(id='kipepeo', C='Kipepeo', pk='kipepeo', pent='giantmoth.GiantMothEntity', pe='GiantMothEntity', pmod='giantmoth.GiantMothModel', pm='GiantMothModel',
      attrs='.add(Attributes.MAX_HEALTH, 38.0).add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.FLYING_SPEED, 0.6).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 30.0)', scale='1.1f', shadow='0.4f', xp=15,
      doc='Kipepeo (RE5): the hovering poison-dust moth - Giant Moth behaviour.'),
 dict(ret='LickerEntity', retimp='licker.LickerEntity', id='reaper', C='Reaper', pk='reaper', pent='huntergamma.HunterGammaEntity', pe='HunterGammaEntity', pmod='huntergamma.HunterGammaModel', pm='HunterGammaModel',
      attrs='.add(Attributes.MAX_HEALTH, 110.0).add(Attributes.MOVEMENT_SPEED, 0.36).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 30.0).add(Attributes.ARMOR, 4.0)', scale='1.35f', shadow='0.8f', xp=40,
      doc='Reaper (RE5): the lanky, fast, long-tongued stalker - a sighted tongue-lash / snare / claw / pounce crawler on the Hunter Gamma behaviour.'),
 dict(id='verdugo', C='Verdugo', pk='verdugo', pent=mrx['pent'], pe=mrx['pe'], pmod=mrx['pmod'], pm=mrx['pm'], attrs=A(230.0, 0.34, 10.0, 40, 0.8, 6.0), scale='1.15f', shadow='0.8f', xp=120,
      imports='import net.minecraft.tags.DamageTypeTags;\nimport net.minecraft.world.damagesource.DamageSource;\nimport net.minecraft.world.entity.projectile.Snowball;\n',
      extra='''
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    /** the executioner is brittle against cold: freezing damage and snowballs hit 4x. */
    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (src.is(DamageTypeTags.IS_FREEZING) || src.getDirectEntity() instanceof Snowball) amount = Math.max(amount, 1f) * 4f;
        return super.hurt(src, amount);
    }
''', doc='Verdugo (RE4): the nigh-unstoppable executioner - fast stalker with the Mr. X move set, weak to cold.'),
 dict(id='wesker', C='Wesker', pk='wesker', pent=mrx['pent'], pe=mrx['pe'], pmod=mrx['pmod'], pm=mrx['pm'], attrs=A(320.0, 0.42, 11.0, 48, 0.9, 8.0), scale='1.0f', shadow='0.5f', xp=200,
      imports='import com.reevil.entity.AttackMove;\nimport net.minecraft.world.entity.LivingEntity;\nimport java.util.List;\n',
      extra='''
    private static final AttackMove M_STRIKE = new AttackMove(HAYMAKER, "power_strike", 30, new int[]{15}, 0, 3.2, 8, 6).dmg(11f).kb(1.6).face(14);
    private static final AttackMove M_FLURRY = new AttackMove(JAB_HOOK, "super_speed_flurry", 28, new int[]{9, 19}, 0, 3.0, 6, 8).dmg(7f).kb(0.5).face(18);
    private static final AttackMove M_KICK = new AttackMove(STOMP, "snap_kick", 24, new int[]{12}, 0, 2.8, 10, 4).dmg(9f).kb(2.0).face(11);
    private static final AttackMove M_THROW = new AttackMove(CHOKE, "grab_and_throw", 72, new int[]{14, 30, 44, 60}, 0, 2.8, 110, 3).dmg(4f).kb(0).face(14);
    private static final List<AttackMove> MOVES = List.of(M_STRIKE, M_FLURRY, M_KICK, M_THROW);

    @Override public List<AttackMove> moves() { return MOVES; }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    /** super speed: the flurry and the strike close the gap in a blink. */
    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m.id == JAB_HOOK && (t == 3 || t == 8 || t == 18)) lunge(0.55, 0);
        else if (m.id == HAYMAKER && t == 8) lunge(0.8, 0);
        else super.onMoveTick(m, t, target);
    }
''', doc='Albert Wesker (RE5): the super-speed fighter - punch flurries, dash-in strikes, snap kicks and a grab-and-throw at triple the Mr. X pace.'),
 dict(id='adjule', C='Adjule', pk='adjule', pent=mrx['pent'], pe=mrx['pe'], pmod=mrx['pmod'], pm=mrx['pm'], attrs=A(160.0, 0.27, 9.0, 36, 0.6, 4.0), scale='1.1f', shadow='0.7f', xp=60, imports=IMP_FX,
      extra='''
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);
        if (target != null && target.isAlive() && random.nextFloat() < 0.35f) target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
    }
''', doc='Adjule (RE5): the tall, mouth-chested mutant - stalker move set with a poisonous touch.'),
 dict(id='ndesu', C='Ndesu', pk='ndesu', pent=mrx['pent'], pe=mrx['pe'], pmod=mrx['pmod'], pm=mrx['pm'], attrs=A(280.0, 0.2, 12.0, 32, 1.0, 6.0), scale='1.45f', shadow='1.2f', xp=90, imports=IMP_FX,
      extra='''
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);
        if (target != null && target.isAlive()) { target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0)); target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0)); }
    }
''', doc='Ndesu (RE5): the hulking spore-carrier - a slow giant on the stalker moves, each blow dosing the victim with poison and nausea.'),
 dict(ret='MrXEntity', retimp='mrx.MrXEntity', id='executioner_majini', C='ExecutionerMajini', pk='executionermajini', pent='elgigante.ElGiganteEntity', pe='ElGiganteEntity', pmod='elgigante.ElGiganteModel', pm='ElGiganteModel', attrs=A(330.0, 0.22, 13.0, 36, 1.0, 6.0), scale='1.6f', shadow='1.4f', xp=100,
      extra='''
    @Override public String getTextureSuffix() { return ""; }
''', doc='Executioner Majini (RE5): the towering masked axeman - El Gigante reach, throws and stomps, weak back and all.'),
]
for f in fams: gen(f)
