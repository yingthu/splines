package cs4620.shape;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

/*
 * Draws a square in the xy plane. z is set to zero.
 */

public class Square extends TriangleMesh {

	private static final float[] squareVertices;
	private static final float[] squareNormals;
	private static final float[] squareTexCoords;
	private static final int[]   squareTriangles;
	private static final int[]   squareLines;

	static {
		/* The four vertices of the square. n = negative, p = positive */
		Point3f nn = new Point3f(-1.0f, -1.0f, 0f);
		Point3f pn = new Point3f(+1.0f, -1.0f, 0f);
		Point3f pp = new Point3f(+1.0f, +1.0f, 0f);
		Point3f np = new Point3f(-1.0f, +1.0f, 0f);
		
		Point2f tx1 = new Point2f(1.0f,0.0f);
		Point2f tx2 = new Point2f(1.0f,1.0f);
		Point2f tx3 = new Point2f(0.0f,0.0f);
		Point2f tx4 = new Point2f(0.0f,1.0f);

		/* Normals face of the square */
		Vector3f normal = new Vector3f(0, 0, 1);

		// Vertex matrix
		Point3f[] vertices = new Point3f[] { nn, pn, pp, np };

		// Normal matrix
		Vector3f[] normals = new Vector3f[] { normal, normal, normal, normal };
		
		// Texture Matrix
		Point2f[] texCoords = new Point2f[] { tx3, tx4, tx2, tx1 };

		// Vertex indices
		Point3i[] triangles = new Point3i[] {
				new Point3i(0, 1, 2),
				new Point3i(2, 3, 0) };

		squareVertices = new float[3*vertices.length];
		for(int i=0;i<vertices.length;i++)
		{
			squareVertices[3*i  ] = vertices[i].x;
			squareVertices[3*i+1] = vertices[i].y;
			squareVertices[3*i+2] = vertices[i].z;
		}

		squareNormals = new float[3*normals.length];
		for(int i=0;i<normals.length;i++)
		{
			squareNormals[3*i  ] = normals[i].x;
			squareNormals[3*i+1] = normals[i].y;
			squareNormals[3*i+2] = normals[i].z;
		}
		
		squareTexCoords = new float[2*texCoords.length];
		for(int i=0;i<texCoords.length;i++) 
		{
			squareTexCoords[2*i] = texCoords[i].x;
			squareTexCoords[2*i+1] = texCoords[i].y;
		}

		squareTriangles = new int[3*triangles.length];
		for(int i=0;i<triangles.length;i++)
		{
			squareTriangles[3*i  ] = triangles[i].x;
			squareTriangles[3*i+1] = triangles[i].y;
			squareTriangles[3*i+2] = triangles[i].z;
		}
		
		squareLines = new int [8];
		squareLines[0] = 0;
		squareLines[1] = 1;
		squareLines[2] = 1;
		squareLines[3] = 2;
		squareLines[4] = 2;
		squareLines[5] = 3;
		squareLines[6] = 3;
		squareLines[7] = 0;
	}
	
	public Square(GL2 gl)
	{
		super(gl);
	}

	@Override
	public void buildMesh(GL2 gl, float tolerance) {
		setVertices(gl, squareVertices);
		setNormals(gl, squareNormals);
		
		setTexCoords(gl, squareTexCoords);
		setTriangleIndices(gl, squareTriangles);
		setWireframeIndices(gl, squareLines);
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Square");
		return result;
	}
}
