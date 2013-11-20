package cs4620.shape;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

public class Cube extends TriangleMesh {

	private static final float[] cubeVertices;
	private static final float[] cubeNormals;
	private static final float[] cubeTexCoords;
	private static final int[]   cubeTriangles;
	private static final int[]   cubeLines;

	static {
		/* The eight vertices of the cube. n = negative, p = positive */
		Point3f nnn = new Point3f(-1.0f, -1.0f, -1.0f);
		Point3f nnp = new Point3f(-1.0f, -1.0f, +1.0f);
		Point3f npn = new Point3f(-1.0f, +1.0f, -1.0f);
		Point3f npp = new Point3f(-1.0f, +1.0f, +1.0f);
		Point3f pnn = new Point3f(+1.0f, -1.0f, -1.0f);
		Point3f pnp = new Point3f(+1.0f, -1.0f, +1.0f);
		Point3f ppn = new Point3f(+1.0f, +1.0f, -1.0f);
		Point3f ppp = new Point3f(+1.0f, +1.0f, +1.0f);
		
		Point2f tx1 = new Point2f(1.0f,0.0f);
		Point2f tx2 = new Point2f(1.0f,1.0f);
		Point2f tx3 = new Point2f(0.0f,0.0f);
		Point2f tx4 = new Point2f(0.0f,1.0f);

		/* Normals for the different faces of the cube */
		Vector3f lNormal = new Vector3f(-1, 0, 0);
		Vector3f rNormal = new Vector3f(+1, 0, 0);
		Vector3f dNormal = new Vector3f(0, -1, 0);
		Vector3f uNormal = new Vector3f(0, +1, 0);
		Vector3f bNormal = new Vector3f(0, 0, -1);
		Vector3f fNormal = new Vector3f(0, 0, +1);

		// Vertex matrix
		Point3f[] vertices = new Point3f[] {
				ppp, npp, nnp, pnp, // Front face
				ppn, pnn, nnn, npn, // Back face
				ppn, ppp, pnp, pnn, // Right face
				npp, npn, nnn, nnp, // Left face
				ppp, ppn, npn, npp, // Up face
				pnp, nnp, nnn, pnn }; // Down face

		// Normal matrix
		Vector3f[] normals = new Vector3f[] {
				fNormal, fNormal, fNormal, fNormal,
				bNormal, bNormal, bNormal, bNormal,
				rNormal, rNormal, rNormal, rNormal,
				lNormal, lNormal, lNormal, lNormal,
				uNormal, uNormal, uNormal, uNormal,
				dNormal, dNormal, dNormal, dNormal };
		
		// Texture Matrix
		Point2f[] texCoords = new Point2f[] {
			tx1, tx2, tx4, tx3,
			tx1, tx2, tx4, tx3,
			tx1, tx2, tx4, tx3,
			tx1, tx2, tx4, tx3,
			tx1, tx2, tx4, tx3,
			tx1, tx2, tx4, tx3
		};

		// Vertex indices
		Point3i[] triangles = new Point3i[] {
				new Point3i(0, 1, 2),
				new Point3i(2, 3, 0),
				new Point3i(4, 5, 6),
				new Point3i(6, 7, 4),
				new Point3i(8, 9, 10),
				new Point3i(10, 11, 8),
				new Point3i(12, 13, 14),
				new Point3i(14, 15, 12),
				new Point3i(16, 17, 18),
				new Point3i(18, 19, 16),
				new Point3i(20, 21, 22),
				new Point3i(22, 23, 20) };

		cubeVertices = new float[3*vertices.length];
		for(int i=0;i<vertices.length;i++)
		{
			cubeVertices[3*i  ] = vertices[i].x;
			cubeVertices[3*i+1] = vertices[i].y;
			cubeVertices[3*i+2] = vertices[i].z;
		}

		cubeNormals = new float[3*normals.length];
		for(int i=0;i<normals.length;i++)
		{
			cubeNormals[3*i  ] = normals[i].x;
			cubeNormals[3*i+1] = normals[i].y;
			cubeNormals[3*i+2] = normals[i].z;
		}
		
		cubeTexCoords = new float[2*texCoords.length];
		for(int i=0;i<texCoords.length;i++) 
		{
			cubeTexCoords[2*i] = texCoords[i].x;
			cubeTexCoords[2*i+1] = texCoords[i].y;
		}

		cubeTriangles = new int[3*triangles.length];
		for(int i=0;i<triangles.length;i++)
		{
			cubeTriangles[3*i  ] = triangles[i].x;
			cubeTriangles[3*i+1] = triangles[i].y;
			cubeTriangles[3*i+2] = triangles[i].z;
		}
		
		cubeLines = new int [48];
		for(int i = 0; i < 6; i++)
		{
			cubeLines[8*i + 0] = 4*i + 0;
			cubeLines[8*i + 1] = 4*i + 1;
			cubeLines[8*i + 2] = 4*i + 1;
			cubeLines[8*i + 3] = 4*i + 2;
			cubeLines[8*i + 4] = 4*i + 2;
			cubeLines[8*i + 5] = 4*i + 3;
			cubeLines[8*i + 6] = 4*i + 3;
			cubeLines[8*i + 7] = 4*i + 0;
		}
	}
	
	public Cube(GL2 gl)
	{
		super(gl);
	}

	@Override
	public void buildMesh(GL2 gl, float tolerance) {
		setVertices(gl, cubeVertices);
		setNormals(gl, cubeNormals);
		
		setTexCoords(gl, cubeTexCoords);
		setTriangleIndices(gl, cubeTriangles);
		setWireframeIndices(gl, cubeLines);
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Cube");
		return result;
	}
}
