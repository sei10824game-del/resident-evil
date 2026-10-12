"""Re-parent / re-dimension creatures whose wiki canon differs from the first implementation. Run before variants_canon.py."""
import os, re, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from genfam import gen
J = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src/main/java/com/reevil/')
MRX = dict(pent='mrx.MrXEntity', pe='MrXEntity', pmod='mrx.MrXModel', pm='MrXModel', ret='MrXEntity', retimp='mrx.MrXEntity')
NOTR = '    @Override protected boolean canTransform() { return false; }\n    @Override public String getTextureSuffix() { return ""; }\n'
def A(hp, sp, dm, fr, kb=None, ar=None): return '.add(Attributes.MAX_HEALTH, %s).add(Attributes.MOVEMENT_SPEED, %s).add(Attributes.ATTACK_DAMAGE, %s).add(Attributes.FOLLOW_RANGE, %s)' % (hp, sp, dm, fr) + ('.add(Attributes.KNOCKBACK_RESISTANCE, %s)' % kb if kb else '') + ('.add(Attributes.ARMOR, %s)' % ar if ar else '')
VARIANT_INIT = '''    /** one-time random variant, kept in getMut() (synced to the client) */
    @Override
    public void aiStep() {
        if (!level().isClientSide && getPersistentStage() == 0) { setPersistentStage(1); initVariant(); }
        super.aiStep();
    }
'''
ITEMS = [
 (dict(id='gnezdo', C='Gnezdo', pk='gnezdo', **MRX, attrs=A(70.0, 0.3, 6.0, 28), scale='1.0f', shadow='0.5f', xp=40, imports='import com.reevil.entity.AttackMove;\n', extra=NOTR,
       doc='Gnezdo (RE6): a swarm of insects merged into a humanoid female figure; a Queen insect hides inside and is exposed once the swarm is torn apart.'), (0.6, 1.8)),
 (dict(id='daughter', C='Daughter', pk='daughter', **MRX, attrs=A(90.0, 0.36, 7.0, 32, 0.1), scale='1.0f', shadow='0.5f', xp=80, extra=NOTR.replace('getTextureSuffix() { return ""; }', 'getTextureSuffix() { return new String[]{"_bela", "_cassandra", "_daniela"}[Math.max(0, Math.min(2, getMut()))]; }') +
       VARIANT_INIT + '    private void initVariant() { setMut(random.nextInt(3)); }\n',
       doc='Dimitrescu daughters (RE8: Bela/Cassandra/Daniela): human + blowfly; swarm form shrugs off damage and lunges at the neck; in the cold they fall back to a sickle frenzy.'), (0.6, 1.9)),
 (dict(id='marguerite', C='Marguerite', pk='marguerite', **MRX, attrs=A(100.0, 0.32, 8.0, 32, 0.2), scale='1.0f', shadow='0.5f', xp=120, extra=NOTR,
       doc='Marguerite Baker (RE7): frail-looking woman with demonic eyes who controls insects; mutated, she grows huge limbs and an insect-hive abdomen.'), (0.7, 1.9)),
 (dict(id='soldat', C='Soldat', pk='soldat', **MRX, attrs=A(70.0, 0.26, 8.0, 36, 0.3), scale='1.0f', shadow='0.5f', xp=40, imports='import com.reevil.entity.AttackMove;\n', extra=NOTR + VARIANT_INIT +
       '''    /** Eins / Zwei / Jet / Panzer stored in getMut(). */
    private void initVariant() {
        int r = random.nextInt(10); setMut(r < 5 ? 0 : r < 8 ? 1 : r < 9 ? 2 : 3);
        double hp = switch (getMut()) { case 0 -> 70; case 1 -> 90; case 2 -> 80; default -> 260; };
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp); setHealth((float) hp);
    }
''', doc='Soldat (RE8): cybernetic Cadou soldier with drill arms - Eins, Zwei, Jet (jetpack dash) and Panzer variants; the chest exhaust is the weak spot.'), (0.7, 2.0)),
 (dict(id='urias', C='Urias', pk='urias', pent='elgigante.ElGiganteEntity', pe='ElGiganteEntity', pmod='elgigante.ElGiganteModel', pm='ElGiganteModel', ret='MrXEntity', retimp='mrx.MrXEntity',
       attrs=A(600.0, 0.28, 14.0, 52, 0.95, 4.0), scale='1.3f', shadow='1.2f', xp=280, extra=NOTR,
       doc='Urias (RE8): troll-like Lycan with a war-hammer, enormous jumps and a habit of hurling columns.'), (1.2, 3.0)),
 (dict(id='adjule', C='Adjule', pk='adjule', pent='colmillos.ColmillosEntity', pe='ColmillosEntity', pmod='cerberus.CerberusModel', pm='CerberusModel', ret='CerberusEntity', retimp='cerberus.CerberusEntity',
       imports='import com.reevil.entity.AttackMove;\n', attrs=A(70.0, 0.34, 7.0, 30, 0.2), scale='1.1f', shadow='0.6f', xp=60, extra='',
       doc='Adjule (RE5): a dog parasitised by Type-2 Plagas; its head splits open so the parasite can devour prey.'), (0.8, 0.95)),
]
def run():
    me = open(J + 'registry/ModEntities.java').read()
    for f, (w, h) in ITEMS:
        gen(f)
        pat = re.compile(r'(ENTITIES\.register\("%s",\s*\(\) -> EntityType\.Builder\.of\([A-Za-z]+::new, MobCategory\.MONSTER\)\.sized\()[0-9.]+f, [0-9.]+f' % f['id'])
        me, n = pat.subn(r'\g<1>%sf, %sf' % (w, h), me); assert n == 1, f['id']
        print('reworked', f['id'])
    open(J + 'registry/ModEntities.java', 'w').write(me)
    # kipepeo: smaller flock butterflies
    kp = open(J + 'registry/ModEntities.java').read()
    kp = re.sub(r'(ENTITIES\.register\("kipepeo",\s*\(\) -> EntityType\.Builder\.of\([A-Za-z]+::new, MobCategory\.MONSTER\)\.sized\()[0-9.]+f, [0-9.]+f', r'\g<1>0.7f, 0.6f', kp)
    open(J + 'registry/ModEntities.java', 'w').write(kp)
    r = J + 'client/kipepeo/KipepeoRenderer.java'; s = open(r).read(); s = re.sub(r'ps\.scale\([0-9.]+f, [0-9.]+f, [0-9.]+f\)', 'ps.scale(0.6f, 0.6f, 0.6f)', s); open(r, 'w').write(s)
    r = J + 'client/soldat/SoldatRenderer.java'; s = open(r).read(); s = re.sub(r'ps\.scale\([^;]*\);', 'float k = e.getMut() == 3 ? 1.3f : 1.0f; ps.scale(k, k, k);', s); open(r, 'w').write(s)
run()
