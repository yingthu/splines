#!/usr/bin/env python

# objToMshNormals.py
# Daniel Schroeder, 2012-10-whenever it was

import sys

def intOrNone(string):
    outVal = None
    try:
        outVal = int(string)
    except:
        pass
    return outVal

def getNumComponents(listOfLists):
    lengths = [len(l) for l in listOfLists]
    m = min(lengths)
    M = max(lengths)
    assert m == M
    return m

def assertSameTextureIndices(listOfLists):
    assert sum([l[0] == l[1] for l in listOfLists]) == len(listOfLists)

def assertSameNormalIndices(listOfLists):
    assert sum([l[0] == l[2] for l in listOfLists]) == len(listOfLists)

def loadOBJ(objFile):

    vertices = []
    normals = []
    texcoords = []
    triangles = []

    readFace = False
    expectingNormals = False

    for line in objFile:
        splitLine = line.strip().split()
        if len(splitLine) == 0:
            continue

        if splitLine[0] == "v":
            # process vert
            assert len(splitLine) >= 4
            vertices.append(tuple([float(a) for a in splitLine[1:4]]))
        elif splitLine[0] == "vn":
            # process normal
            assert len(splitLine) >= 4
            normals.append(tuple([float(a) for a in splitLine[1:4]]))
        elif splitLine[0] == "vt":
            # process texcoord
            assert len(splitLine) >= 3
            normals.append(tuple([float(a) for a in splitLine[1:3]]))
        elif splitLine[0] == "f":
            # process face
            assert len(splitLine) >= 4
            vertIndices = [[intOrNone(s) for s in a.split("/")] for a in splitLine[1:]]
            numComponents = getNumComponents(vertIndices)
            if not readFace:
                expectingTexcoords = (numComponents > 1)
                expectingNormals = (numComponents > 2)
                readFace = True
            if expectingTexcoords:
                assert numComponents > 1
                assertSameTextureIndices(vertIndices)
            if expectingNormals:
                assert numComponents > 2
                assertSameNormalIndices(vertIndices)

            #faceVerts = [int(a.split("/")[0]) for a in splitLine[1:]]
            faceVerts = [l[0] for l in vertIndices]
            triangles.append(tuple([v - 1 for v in faceVerts[0:3]]))
            if len(faceVerts) == 4: # triangulate quads into 2 triangles
                triangles.append(tuple([v - 1 for v in [faceVerts[i] for i in [0,2,3]]]))

    if not normals:
        normals = None
    if not texcoords:
        texcoords = None

    if expectingNormals:
        return vertices, normals, triangles
    else:
        return vertices, None, triangles

def writeMSH(mshFile, vertices, normals, triangles):
    mshFile.write("%d\n" % len(vertices))
    mshFile.write("%d\n" % len(triangles))

    mshFile.write("vertices\n")
    for v in vertices:
        mshFile.write("%f\n%f\n%f\n" % v)

    if normals is not None:
        assert len(normals) == len(vertices)
        mshFile.write("normals\n")
        for n in normals:
            mshFile.write("%f\n%f\n%f\n" % n)

    if texcoords is not None:
        assert len(texcoords) == 2 * len(vertices) / 3
        mshFile.write("texcoords\n")
        for c in texcoords:
            mshFile.write("%f\n%f\n" % c)

    mshFile.write("triangles\n")
    for t in triangles:
        mshFile.write("%d\n%d\n%d\n" % t)

def rescaleVertices(vertices):
    # shift and uniformly scale the object so that it fills
    # the space [-1, 1]^3. Does not shift the origin.
    minMax = [[sys.float_info.max, -sys.float_info.max],\
              [sys.float_info.max, -sys.float_info.max],\
              [sys.float_info.max, -sys.float_info.max]]
    
    for v in vertices:
        for k in range(len(v)):
            minMax[k][0] = min(minMax[k][0], v[k])
            minMax[k][1] = max(minMax[k][1], v[k])

    center = [(mM[0] + mM[1]) / 2 for mM in minMax]

    maxAxisScale = max([(mM[1] - mM[0]) / 2 for mM in minMax])

    for i in range(len(vertices)):
        vertices[i] = tuple([(vertices[i][j] - center[j]) / maxAxisScale for j in range(3)])

def main():
    if len(sys.argv) != 3:
        sys.stderr.write("Usage: %s in.obj out.msh\n" % sys.argv[0])
        sys.exit(1)

    with open(sys.argv[1], 'r') as objFile:
        vertices, normals, texcoords, triangles = loadOBJ(objFile)

    rescaleVertices(vertices)

    with open(sys.argv[2], 'w') as mshFile:
        writeMSH(mshFile, vertices, normals, texcoords, triangles)

if __name__ == "__main__":
    main()
