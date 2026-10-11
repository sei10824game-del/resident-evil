import sys, os; sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from genfam import gen
def A(hp, sp, dm=6.0, fr=28, kb=None, ar=None):
    return '.add(Attributes.MAX_HEALTH, %s).add(Attributes.MOVEMENT_SPEED, %s).add(Attributes.ATTACK_DAMAGE, %s).add(Attributes.FOLLOW_RANGE, %s)' % (hp, sp, dm, fr) + (f'.add(Attributes.KNOCKBACK_RESISTANCE, {kb})' if kb else '') + (f'.add(Attributes.ARMOR, {ar})' if ar else '')
NEM = dict(pent='nemesis.NemesisEntity', pe='NemesisEntity', pmod='nemesis.NemesisModel', pm='NemesisModel')
IMP_MOVES = 'import com.reevil.entity.AttackMove;\nimport java.util.List;\n'
SADDLER_MOVES = '''
    protected static final AttackMove S_PUNCH = new AttackMove(PUNCH, "staff_punch", 30, new int[]{15}, 0, 3.0, 18, 6).dmg(9f).kb(1.2).face(14);
    protected static final AttackMove S_KICK = new AttackMove(KICK, "kick", 24, new int[]{12}, 0, 2.5, 16, 4).dmg(7f).kb(1.6).face(11);
    protected static final AttackMove S_WHIP = new AttackMove(WHIP, "tentacle_lash", 28, new int[]{13}, 2.5, 9.5, 20, 6).dmg(9f).kb(1.0).face(12);
    protected static final AttackMove S_SNARE = new AttackMove(SNARE, "tentacle_snare", 58, new int[]{14, 40}, 3.5, 12, 70, 4).dmg(4f).kb(0).face(14);
    protected static final AttackMove S_BARRAGE = new AttackMove(BARRAGE, "tentacle_barrage", 50, new int[]{14, 22, 30, 38}, 0, 9, 50, 5).dmg(8f).kb(1.0).face(10);
    private static final List<AttackMove> SF0 = List.of(S_PUNCH, S_WHIP, S_KICK);
    private static final List<AttackMove> SF1 = List.of(S_PUNCH, S_WHIP, S_SNARE, S_KICK);
    private static final List<AttackMove> SF2 = List.of(S_WHIP, S_SNARE, S_BARRAGE, S_PUNCH);
    @Override public List<AttackMove> moves() { return getForm() == 0 ? SF0 : getForm() == 1 ? SF1 : SF2; }
'''
fams = [
 dict(id='saddler', C='Saddler', pk='saddler', **NEM, ret='NemesisEntity', retimp='nemesis.NemesisEntity', attrs=A(300.0, 0.25, 9.0, 48, 0.8, 6.0), scale='1.0f', shadow='0.7f', xp=200, imports=IMP_MOVES,
      extra=SADDLER_MOVES, doc='Osmund Saddler (RE4): the robed cult leader. The Nemesis frame without the rocket launcher - staff blows, tentacle lashes, snare-and-drag and (final form) the all-round tentacle barrage, mutating twice as he is hurt.'),
 dict(id='simmons', C='Simmons', pk='simmons', pent='saddler.SaddlerEntity', pe='SaddlerEntity', pmod='nemesis.NemesisModel', pm='NemesisModel', ret='NemesisEntity', retimp='nemesis.NemesisEntity', attrs=A(380.0, 0.27, 10.0, 48, 0.85, 6.0), scale='1.1f', shadow='0.8f', xp=250,
      doc='Derek Simmons (RE6): the mutating senator - Saddler behaviour with more health and a faster, heavier frame.'),
 dict(id='salazar', C='Salazar', pk='salazar', pent='plant42.Plant42Entity', pe='Plant42Entity', pmod='plant42.Plant42Model', pm='Plant42Model', attrs=A(360.0, 0.0, 8.0, 24, 1.0, 5.0), scale='1.15f', shadow='1.8f', xp=250,
      doc='Ramon Salazar (RE4, final form): the immense rooted mass - Plant 42 behaviour (tentacle whip, snare-and-devour, spore cloud, ground slam, weak base, withering second phase) on a larger body.'),
 dict(id='uroboros', C='Uroboros', pk='uroboros', pent='plant42.Plant42Entity', pe='Plant42Entity', pmod='plant42.Plant42Model', pm='Plant42Model', attrs=A(450.0, 0.0, 10.0, 28, 1.0, 6.0), scale='1.4f', shadow='2.2f', xp=300,
      doc='Uroboros (RE5): the colossal tentacle mass - Plant 42 behaviour at an enormous scale.'),
 dict(ret='MrXEntity', retimp='mrx.MrXEntity', id='mendez', C='Mendez', pk='mendez', pent='elgigante.ElGiganteEntity', pe='ElGiganteEntity', pmod='elgigante.ElGiganteModel', pm='ElGiganteModel', attrs=A(300.0, 0.23, 11.0, 36, 1.0, 5.0), scale='1.3f', shadow='1.2f', xp=150,
      extra='''
    @Override public String getTextureSuffix() { return ""; }
''', doc='Bitores Mendez (RE4): the village chief - a huge brute with El Gigante reach, throws and stomps.'),
 dict(ret='MrXEntity', retimp='mrx.MrXEntity', id='ogroman', C='Ogroman', pk='ogroman', pent='elgigante.ElGiganteEntity', pe='ElGiganteEntity', pmod='elgigante.ElGiganteModel', pm='ElGiganteModel', attrs=A(480.0, 0.21, 14.0, 36, 1.0, 7.0), scale='1.85f', shadow='1.6f', xp=250,
      extra='''
    @Override public String getTextureSuffix() { return ""; }
''', doc='Ogroman (RE6): the mutated giant - El Gigante moves on a far bigger, tougher body.'),
 dict(id='jill', C='Jill', pk='jill', pent='wesker.WeskerEntity', pe='WeskerEntity', pmod='mrx.MrXModel', pm='MrXModel', ret='MrXEntity', retimp='mrx.MrXEntity', attrs=A(240.0, 0.38, 8.0, 44, 0.7, 5.0), scale='0.95f', shadow='0.5f', xp=150,
      doc='Jill Valentine (RE5, brainwashed): Wesker-style fast fighter - flurries, dash strikes, snap kicks and a throw - lighter and a little slower.'),
 dict(id='ustanak', C='Ustanak', pk='ustanak', pent='mrx.MrXEntity', pe='MrXEntity', pmod='mrx.MrXModel', pm='MrXModel', attrs=A(420.0, 0.3, 12.0, 52, 0.95, 8.0), scale='1.2f', shadow='1.0f', xp=200,
      imports=IMP_MOVES,
      extra='''
    private static final AttackMove U_PUNCH = new AttackMove(HAYMAKER, "punch", 30, new int[]{15}, 0, 3.0, 16, 5).dmg(11f).kb(1.4).face(14);
    private static final AttackMove U_STAB = new AttackMove(CLAW_STAB, "claw_stab", 30, new int[]{15}, 1.5, 4.8, 22, 6).dmg(15f).kb(0.8).face(12).move();
    private static final AttackMove U_SWEEP = new AttackMove(CLAW_SWEEP, "claw_sweep", 36, new int[]{11, 24}, 0, 4.2, 20, 5).dmg(10f).kb(1.0).face(24);
    private static final AttackMove U_POUND = new AttackMove(POUND, "ground_pound", 42, new int[]{23}, 0, 5.5, 80, 3).dmg(11f).kb(1.4).face(12);
    private static final AttackMove U_CHOKE = new AttackMove(CHOKE, "claw_grab", 72, new int[]{14, 30, 44, 60}, 0, 2.6, 140, 3).dmg(3f).kb(0).face(14);
    private static final List<AttackMove> MOVES = List.of(U_PUNCH, U_STAB, U_SWEEP, U_POUND, U_CHOKE);

    {{ setForm(1); }}   // always the clawed Super Tyrant frame
    @Override public List<AttackMove> moves() { return MOVES; }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
'''.replace('{{ setForm(1); }}', '@Override public void aiStep() { if (getForm() != 1 && !level().isClientSide) setForm(1); super.aiStep(); }'),
      doc='Ustanak (RE6): the relentless bounty hunter with the great claw. Born in the clawed Super Tyrant frame - punch, claw stab, claw sweep, ground pound and claw grab - and never stops hunting.'),
 dict(id='napad', C='Napad', pk='napad', pent='reaper.ReaperEntity', pe='ReaperEntity', pmod='huntergamma.HunterGammaModel', pm='HunterGammaModel', ret='LickerEntity', retimp='licker.LickerEntity', attrs='.add(Attributes.MAX_HEALTH, 60.0).add(Attributes.MOVEMENT_SPEED, 0.42).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 30.0)', scale='1.0f', shadow='0.6f', xp=30,
      doc='Napad (RE6): the scuttling insectoid - a fast, light Reaper-type crawler with the tongue lash / snare / claw / pounce set.'),
 dict(id='javo', C='Javo', pk='javo', pent='ganado.GanadoEntity', pe='GanadoEntity', pmod='ganado.GanadoModel', pm='GanadoModel', attrs=A(40.0, 0.3, 6.0, 30), scale='1.0f', shadow='0.5f', xp=15,
      extra='''
    /** the C-virus mutation is quick: once hurt it forces the limb eruption (whip / scythe / spider) on its own. */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isAlive() && getForm() == 0 && getHealth() < getMaxHealth() * 0.75f && random.nextInt(60) == 0) triggerBurst();
    }
''', doc='Javo (RE6): the C-virus soldier. Ganado moves and grabs, but faster, and its limbs mutate (whip / scythe / spider) soon after it is hurt.'),
 dict(id='gatling_majini', C='GatlingMajini', pk='gatlingmajini', pent='krauser.KrauserEntity', pe='KrauserEntity', pmod='krauser.KrauserModel', pm='KrauserModel', ret='GanadoEntity', retimp='ganado.GanadoEntity', attrs=A(70.0, 0.22, 6.0, 34, 0.6, 3.0), scale='1.1f', shadow='0.6f', xp=40, imports=IMP_MOVES,
      extra='''
    private static final AttackMove G_BASH = new AttackMove(SWING, "butt_strike", 26, new int[]{12}, 0, 2.8, 12, 5).dmg(6f).kb(0.8).face(11);
    private static final AttackMove G_SPRAY = new AttackMove(SHOOT, "gatling_spray", 34, new int[]{12, 15, 18, 21, 24, 27, 30}, 4, 26, 50, 8).face(30);
    private static final List<AttackMove> MOVES = List.of(G_BASH, G_SPRAY);
    @Override public List<AttackMove> moves() { return MOVES; }
''', doc='Gatling Majini (RE5): the heavy gunner - seven-round arrow bursts at range (drawn through the bow animation) and a rifle-butt bash up close.'),
]
for f in fams: gen(f)
