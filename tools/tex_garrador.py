"""Garrador: pale blind brute in rags, caged blindfolded head, steel blade claws, exposed back plaga. garrador.png"""
from autotex import *
from texlib import Tex

CUBES = [('hips', 0, 0, 10, 4, 6), ('torso', 0, 10, 12, 12, 7), ('neck', 40, 0, 4, 2, 4), ('head', 40, 6, 7, 7, 7), ('cage', 70, 0, 8, 8, 8), ('plaga', 94, 0, 6, 6, 4),
         ('arm', 0, 30, 5, 11, 5), ('arm', 22, 30, 6, 10, 6), ('hand', 48, 30, 5, 4, 5), ('claw', 70, 30, 1, 14, 2), ('thigh', 0, 48, 5, 8, 5), ('shin', 22, 48, 5, 8, 5), ('foot', 44, 48, 6, 2, 7)]
class P(Paint):
    def __init__(self):
        self.W, self.H, self.skipped = 128, 64, []
        self.cubes = [dict(name=n, u=u, v=v, w=w, h=h, d=d) for n, u, v, w, h, d in CUBES]
        self.t = Tex(128, 64, 171); self.rng = self.t.rng
p = P()
SKIN, RAG = (176, 160, 146), (96, 80, 62)
def sk(pt, f, r, c): pt.fill(r, SKIN, 0.07, 0.1); pt.veins(r, (120, 96, 110), 3); pt.blots(r, (110, 30, 30), 2, 2)
def rag(pt, f, r, c): pt.fill(r, RAG, 0.12, 0.1); pt.streak(r, RAG, True, 0.12); pt.blots(r, (60, 46, 36), 4, 2); pt.blots(r, (110, 20, 20), 2, 2)
def torso(pt, f, r, c):
    if f == 'back': sk(pt, f, r, c); pt.fill((r[0] + r[2] // 2 - 2, r[1] + 2, 4, 5), (160, 60, 80), 0.1); pt.veins(r, (110, 20, 40), 6)   # open back plate
    else: rag(pt, f, r, c)
def cage(pt, f, r, c):
    x, y, w, h = r
    pt.fill(r, (90, 94, 100), 0.1)
    for i in range(0, w, 2): pt.t.line(x + i, y, x + i, y + h - 1, (40, 42, 46))
    for j in range(0, h, 3): pt.t.line(x, y + j, x + w - 1, y + j, (40, 42, 46))
def head(pt, f, r, c):
    sk(pt, f, r, c)
    if f == 'front': pt.fill((r[0], r[1] + 2, r[2], 2), (214, 200, 170), 0)   # blindfold
def plaga(pt, f, r, c): pt.fill(r, (170, 64, 86), 0.12); pt.veins(r, (100, 16, 40), 4); pt.blots(r, (240, 190, 100), 2, 1)
def claw(pt, f, r, c): pt.fill(r, (150, 154, 160), 0.1); pt.blots(r, (96, 20, 20), 2, 1)
def arm(pt, f, r, c): sk(pt, f, r, c) if c['u'] == 0 else rag(pt, f, r, c)
p.paint([('torso|hips', torso), ('head|neck', head), ('cage', cage), ('plaga', plaga), ('claw', claw), ('arm', arm), ('hand', sk), ('thigh|shin', rag), ('foot', lambda pt, f, r, c: pt.fill(r, (40, 34, 30), 0.1))], rag)
p.save('garrador')
