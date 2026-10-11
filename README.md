# Resident Evil Creatures (Minecraft 1.20.1 / Forge 47.3.0)

RE1: Zombie (+revive into Crimson Head), Crimson Head, Cerberus, Infected Crow, Hunter, Chimera, Giant Spider, Web Spinner, Neptune, Yawn, Plant 42, Tyrant, Lisa Trevor.
RE2: Licker (blind, ceiling ambush, beta form), Mr. X / Super Tyrant (door-smashing stalker, stagger kneel), G-Birkin (G1 -> G2 -> G3), Giant Alligator, Ivy, Giant Moth, Moth Larva.
RE3: Nemesis (3 forms, rocket launcher, tentacles, door-smashing pursuit), Grave Digger (Yawn serpent rig/behaviour, reskinned), Drain Deimos (Giant Moth rig/behaviour + blood drain, reskinned).
Spawn eggs for all (creative tab: Spawn Eggs). New creatures: `tools/addentity.py` registers one; see an existing creature for the pattern.

- Build: `./gradlew build` (JDK 17). Run: `./gradlew runClient`.
- Animation: `anim/AnimClip` keyframes (anticipation / impact / follow-through / recovery) added on top of walk/idle in each `client/<creature>/*Model`.
  Each `AttackMove` hit tick matches the clip's impact key (`hitTick/duration`).
- Textures are generated: `python3 tools/tex_*.py` (UVs are parsed from the model sources by `tools/autotex.py`).
- Dev: `/rmove <id>`, `/rform <n>`, `/rstop` (dev runs only) play a move of the nearest creature for animation review.
- Checks: `tools/jc.sh` fast javac; dev GameTests in `com.reevil.gametest` (`/test runall`, excluded from the jar);
  `tools/ModelCheck.java` bakes every model and verifies clip bones.
- Sounds reuse vanilla events. No natural spawning yet.
