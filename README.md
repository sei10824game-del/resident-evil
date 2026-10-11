# Resident Evil Creatures (Minecraft 1.20.1 / Forge 47.3.0)

Resident Evil 1 roster: Zombie (+revive into Crimson Head), Crimson Head, Cerberus, Infected Crow, Hunter, Chimera,
Giant Spider, Web Spinner, Neptune, Yawn, Plant 42, Tyrant, Lisa Trevor. Spawn eggs for all (creative tab: Spawn Eggs).

- Build: `./gradlew build` (JDK 17). Run: `./gradlew runClient`.
- Animation: `anim/AnimClip` keyframes (anticipation / impact / follow-through / recovery) added on top of walk/idle in each `client/<creature>/*Model`.
  Each `AttackMove` hit tick matches the clip's impact key (`hitTick/duration`).
- Textures are generated: `python3 tools/tex_*.py` (UVs are parsed from the model sources by `tools/autotex.py`).
- Checks: `tools/jc.sh` fast javac; dev GameTests in `com.reevil.gametest` (`/test runall`, excluded from the jar);
  `tools/ModelCheck.java` bakes every model and verifies clip bones.
- Sounds reuse vanilla events. No natural spawning yet.
