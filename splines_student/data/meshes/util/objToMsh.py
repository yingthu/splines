#!/usr/bin/env python

# objToMsh.py
# Daniel Schroeder, 2012-10-whenever it was

import sys

def loadOBJ(objFile):

    vertices = []
    triangles = []

    for line in objFile:
        splitLine = line.strip().split()
        if len(splitLine) == 0:
            continue

        if splitLine[0] == "v":
            # process vert
            assert len(splitLine) >= 4
            vertices.append(tuple([float(a) for a in splitLine[1:4]]))

        if splitLine[0] == "f":
            # process face
            assert len(splitLine) >= 4
            faceVerts = [int(a.split("/")[0]) for a in splitLine[1:]]
            triangles.append(tuple([v - 1 for v in faceVerts[:3]]))

    return vertices, triangles

def writeMSH(mshFile, vertices, triangles):
    mshFile.write("%d\n" % len(vertices))
    mshFile.write("%d\n" % len(triangles))

    mshFile.write("vertices\n")
    for v in vertices:
        mshFile.write("%f\n%f\n%f\n" % v)

    mshFile.write("triangles\n")
    for t in triangles:
        mshFile.write("%d\n%d\n%d\n" % t)

def main():
    if len(sys.argv) != 3:
        sys.stderr.write("Usage: %s in.obj out.msh\n" % sys.argv[0])
        sys.exit(1)
    objFile = open(sys.argv[1], 'r')

    vertices, triangles = loadOBJ(objFile)

    objFile.close()

    mshFile = open(sys.argv[2], 'w')

    writeMSH(mshFile, vertices, triangles)
    
    mshFile.close()

if __name__ == "__main__":
    main()
