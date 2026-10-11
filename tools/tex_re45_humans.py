"""RE4/RE5 humanoid variants painted with tex_ganado.build (same UVs): majini(+plaga), zealot, chainsaw_majini, krauser."""
import sys, os; sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from tex_ganado import build
build('majini', (112, 78, 58), (150, 130, 100), (90, 76, 62), (190, 170, 110), False, 181)
build('majini_plaga', (124, 100, 90), (140, 120, 92), (84, 70, 58), (190, 170, 110), True, 182)
build('zealot', (172, 142, 122), (112, 28, 30), (92, 26, 28), (112, 28, 30), False, 183, sack=True, sack_col=(100, 22, 26))
build('chainsaw_majini', (110, 76, 56), (112, 102, 88), (70, 76, 86), (60, 50, 40), False, 184, sack=True, sack_col=(34, 30, 28))
build('krauser', (186, 150, 124), (70, 84, 64), (58, 60, 52), (60, 60, 50), False, 185)
build('javo', (170, 150, 140), (74, 82, 96), (56, 62, 70), (60, 60, 60), False, 191, sack=False)
build('javo_plaga', (160, 140, 140), (70, 78, 92), (54, 60, 68), (60, 60, 60), True, 192)
build('gatling_majini', (112, 78, 58), (96, 90, 74), (80, 76, 64), (60, 50, 40), False, 193, sack=True, sack_col=(60, 54, 44))
