# Resident Evil Creatures (Minecraft 1.20.1 / Forge 47.3.0)

RE1: Zombie (+revive into Crimson Head), Crimson Head, Cerberus, Infected Crow, Hunter, Chimera, Giant Spider, Web Spinner, Neptune, Yawn, Plant 42, Tyrant, Lisa Trevor.
RE2: Licker (blind, ceiling ambush, beta form), Mr. X / Super Tyrant (door-smashing stalker, stagger kneel), G-Birkin (G1 -> G2 -> G3), Giant Alligator, Ivy, Giant Moth, Moth Larva.
RE3: Nemesis (3 forms, rocket launcher, tentacles, door-smashing pursuit), Grave Digger (Yawn serpent rig/behaviour, reskinned), Drain Deimos (Giant Moth rig/behaviour + blood drain, reskinned), Brain Sucker (bat flier, head-cling drain), Pale Head (tanky non-reviving zombie, swollen skull), Hunter Beta (tougher sandy Hunter), Hunter Gamma (amphibious tongue Hunter, Licker rig).
RE4: Ganado (random tool; Plaga bursts into whip / scythe / spider forms), Dr. Salvador (chainsaw, beheading), Garrador (blind, weak back), Colmillos (wolves, Cerberus rig), Novistador (invisible insect, Moth rig), Regenerador and Iron Maiden (regenerating zombie rig), El Gigante (stalker rig, weak back, kneels), Del Lago (Neptune rig, giant).
RE4 (more): Zealot, Armadura, Verdugo (weak to cold), Krauser (bow + knife). RE5: Majini, Chainsaw Majini, Executioner Majini, Kipepeo, Reaper, Adjule, Ndesu, Wesker (super-speed). Most are variants on an existing rig/behaviour (tools/genfam.py, tools/fam_re45.py).
More bosses/variants (RE4-RE6): Saddler, Salazar, Bitores Mendez, Uroboros, Jill, Gatling Majini, J'avo, Ustanak, Napad, Ogroman, Simmons (tools/fam_re456.py).
Spawn eggs for all (creative tab: Spawn Eggs). New creatures: `tools/addentity.py` registers one; see an existing creature for the pattern.

- Build: `./gradlew build` (JDK 17). Run: `./gradlew runClient`.
- Animation: `anim/AnimClip` keyframes (anticipation / impact / follow-through / recovery) added on top of walk/idle in each `client/<creature>/*Model`.
  Each `AttackMove` hit tick matches the clip's impact key (`hitTick/duration`).
- Textures are generated: `python3 tools/tex_*.py` (UVs are parsed from the model sources by `tools/autotex.py`).
- Dev: `/rmove <id>`, `/rform <n>`, `/rstop` (dev runs only) play a move of the nearest creature for animation review.
- Checks: `tools/jc.sh` fast javac; dev GameTests in `com.reevil.gametest` (`/test runall`, excluded from the jar);
  `tools/ModelCheck.java` bakes every model and verifies clip bones.
- Sounds reuse vanilla events. No natural spawning yet.
