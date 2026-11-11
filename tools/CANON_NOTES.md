# Canon research notes (source: Resident Evil Wiki via residentevil.fandom.com/api.php?action=parse)
Corrections found when auditing against the wiki (fandom pages are blocked for curl but the MediaWiki api.php works):
- Adjule: Plaga-infected DOG (Type-2) that opens its head to let the parasite eat prey. (was: humanoid)
- Kipepeo: Type-2 Plaga butterfly-parasite that bursts out of Majini hosts, flies in flocks, grabs. (was: giant moth)
- Reaper: cockroach + Uroboros; sting, grab, claws. (jaw clamp not canon)
- Ndesu: giant human, spiked bracelets, belt of skull trophies, punches, slams, throws objects. (no mace)
- Gnezdo: swarm of insects merged into a humanoid FEMALE figure + a 'Queen' insect that hides in the swarm; hatch from chrysalids.
- Drain Deimos: mutated flea, Chimera-like (claws, grabbing, egg implantation RE3). Brain Sucker is a relative.
- Grave Digger: burrowing arthropod (white in sewers, brown in park), collapses ground.
- Sturm: Soldat with aircraft radial engine as torso + 3 chainsaw/propeller blades, NO arms; charge (needs rev-up), overheats -> flames, multiple charges, flamethrower-style propeller.
- Soldat: drill arms. Eins (left drill), Zwei (both), Jet (jetpack dash), Panzer (3 drills per arm, armor); weak red exhaust port.
- Urias: troll-like Lycan, war-hammer, huge jumps, throws columns.
- Lycan: canine-like, claws and weapons.
- Daughters (Bela/Cassandra/Daniela): human+blowfly; swarm form (can't be hurt, DoT, lunge neck bite, phase through doors); in cold -> sickle frenzy.
- Marguerite: human woman w/ demonic eyes, controls insects; mutated: huge limbs, wall crawl, swollen insect-hive abdomen discharging swarms, elongated arm.
- Dimitrescu: retractable claws, regeneration, grows huge; dragon-like winged form after poison.
- Heisenberg: 2nd form = massive cybernetic slug-like creature in his machine. Moreau: fish monster, green acid vomit, 2nd form w/ original body in the mouth.
- Mendez: 2nd form: elongated talons, segmented limbs from back, torso split by spinal structure with tiny legs; flings around with back limbs.
- Krauser: arm -> large blade (withstands rocket), TMP, flash grenades, drones, hand-to-hand.
- Executioner Majini: Slam, Double Swing, Berserk (low hp), Grab (QTE, throw, slam); can leap (Mercenaries).
- Fat Molded: projectile corrosive vomit, charge, arm swing, grapple+vomit, headless exploding variant, counterattack when shot idle, acid stuns other enemies, explodes on death.
- J'avo mutations: Glava-Sluz(web spit) -Smech(mandibles lift/bisect) -Begunats(berserk) -Dim(poison gas); Ruka-Srp(sickle arm) -Khvatanje(stretch grab) -Bedem(armour arm/shield); Noga-Trchanje(insect body pounce) -Let(moth wings+stinger) -Skakanje(insect legs kick) -Oklop(armoured legs jump kick); Telo-Eksplozija(cocoon, explodes) -Krljusht(scales, bullet immune) -Magla(immobile gas); Chrysalid (stasis then random).
- Garrador: claws, hearing; bell rage -> charges bell, exposes back; armoured variant. Verdugo: cloaked (red butler / black maid). Armadura: Plaga-possessed armour, swords+halberds, silver/black variants. Colmillos: need to spot you; Plaga emerges to attack. Novistador: camouflage.
- Gap audit, checked against the wiki /gameplay pages (canon_moves.py "gap audit" block):
  Novistador: acid is a close-range stationary vomit (not a ranged spit) + jump kick. Verdugo: tail stab through grates, 4x tail throw, leap slash.
  Napad: Ram (wind-up charge, knockdown), Throw (two swings to grab, throws down), weak flesh on the back. Reaper: vents gas = blur cloak, weak point on the back; instakill grab.
  Hunter Gamma: swallows prey whole. Iron Maiden: arms extend and pull onto the spikes; spikes extend with almost no warning. Regenerador: arms stretch out to pull the victim into a neck bite.
  Pale Head: regenerates, moves and attacks FASTER than a normal zombie (was slower). Brain Sucker: poisons at close range. Simmons: fires bone shards in mutated forms. Jill (RE5): machine pistols.
  Tyrant kneeling at spawn is intended (dormant until approached).
  Verdugo has a bladed tail (CanonVisuals SWING whips it on tail moves); one Lycan in four is an archer (bow instead of axe, by entity id).
- Known gaps left: Del Lago boat ram (uses Neptune's lunge/drag), Ustanak arm attachments and cage, Mia chainsaw, Garrador claws stuck in walls after a rush.
- Second wiki pass (~45 /gameplay pages): Colmillos' Plaga whips with tentacles (was a poison spike burst); Miranda raises mold spikes under a distant target and fires a fireball torrent;
  Plant 42 sprays acid (move renamed); Ogroman weak on its back; Uroboros weak to fire. Checked and already matching: Hunter, Chimera, Neptune, Yawn, El Gigante, Executioner, Krauser, Nemesis, Kipepeo, Wesker, Dimitrescu, Moreau, Crow, Mendez.
- Third pass: Garrador's missed rush leaves it stuck (claws in the wall) for 3 s, and the rush only connects within reach (was 15 blocks); RE4R Verdugo has the bladed tail and tail moves;
  Mia picks up the chainsaw once wounded (mut 1: chainsaw swing / lunge). Del Lago's boat attack is Neptune's tail slap (ejects boat passengers), inherited.
- Known gaps left: Ustanak arm attachments and cage.
