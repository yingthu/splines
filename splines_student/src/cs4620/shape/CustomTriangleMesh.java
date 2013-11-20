package cs4620.shape;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

public class CustomTriangleMesh extends TriangleMesh
{
	public CustomTriangleMesh(GL2 gl, File meshFile) throws Exception
	{
		super(gl);
		BufferedReader fr = new BufferedReader(new FileReader(meshFile));
		try
		{
			int nPoints = Integer.parseInt(fr.readLine());
			int nPolys = Integer.parseInt(fr.readLine());

			float [] vertices = new float[nPoints * 3];
			float [] normals = new float[nPoints * 3];
			float [] texcoords = new float[nPoints * 2];
			int [] triangles = new int[nPolys * 3];

			boolean vertsRead = false;
			boolean trisRead = false;
			boolean normalsRead = false;
			boolean texCoordsRead = false;

			String line = fr.readLine();
			while(line != null) {
				if(line.equals("vertices")) {
					for (int i = 0; i < vertices.length; ++i) {
						vertices[i] = Float.parseFloat(fr.readLine());
					}
					vertsRead = true;
				}
				else if( line.equals("texcoords") ) {
					for (int i = 0; i < 2*vertices.length/3; ++i) {
						texcoords[i] = Float.parseFloat(fr.readLine());
					}
					texCoordsRead = true;
				}
				else if( line.equals("normals") ) {
					for (int i = 0; i < normals.length; ++i) {
						normals[i] = Float.parseFloat(fr.readLine());
					}
					normalsRead = true;
				}
				else if( line.equals("triangles")) {
					for (int i = 0; i < triangles.length; ++i) {
						triangles[i] = Integer.parseInt(fr.readLine());
					}
					trisRead = true;
				}
				line = fr.readLine();
			}

			// sanity checks
			if( !vertsRead )
				throw new Exception("Broken file - vertices expected");

			if( !trisRead )
				throw new Exception("Broken file - triangles expected.");

			if (!normalsRead)
				computeNormals(nPolys, vertices, normals, triangles);
			
			setVertices(gl, vertices);
			setNormals(gl, normals);
			setTriangleIndices(gl, triangles);
			if (texCoordsRead)
				setTexCoords(gl, texcoords);
			deriveWireframeIndices(gl, triangles);
		}
		finally
		{
			fr.close();
		}		
	}
	
	protected void deriveWireframeIndices(GL2 gl, int [] triangleIndices)
	{
		// derive wireframe indices from triangle indices
		// a, b, c --> a, b, b, c, c, a
		int numTris = triangleIndices.length / 3;
		int [] wireframeIndices = new int [numTris * 6];
		
		for(int t = 0; t < numTris; t++)
		{
			int triStart = 3 * t;
			int wireStart = 6 * t;
			wireframeIndices[wireStart + 0] = triangleIndices[triStart + 0];
			wireframeIndices[wireStart + 1] = triangleIndices[triStart + 1];
			wireframeIndices[wireStart + 2] = triangleIndices[triStart + 1];
			wireframeIndices[wireStart + 3] = triangleIndices[triStart + 2];
			wireframeIndices[wireStart + 4] = triangleIndices[triStart + 2];
			wireframeIndices[wireStart + 5] = triangleIndices[triStart + 0];
		}
		
		setWireframeIndices(gl, wireframeIndices);
	}

	@Override
	public void buildMesh(GL2 gl, float tolerance) {
		// NOP
	}

	@Override
	public Object getYamlObjectRepresentation() {
		throw new RuntimeException("saving custom triangle mesh is not supported (yet)");
	}


	private void computeNormals(int nPolys, float [] vertices, float [] normals, int [] triangles) {
		// compute normals

		for(int i=0;i<normals.length;i++)
			normals[i] = 0.0f;

		Vector3f v0 = new Vector3f();
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f normal = new Vector3f();

		int v0i, v1i, v2i;
		for (int i = 0; i < nPolys; ++i) {
			v0i = 3 * triangles[3 * i + 0];
			v1i = 3 * triangles[3 * i + 1];
			v2i = 3 * triangles[3 * i + 2];

			v0.set(vertices[v0i], vertices[v0i + 1], vertices[v0i + 2]);
			v1.set(vertices[v1i], vertices[v1i + 1], vertices[v1i + 2]);
			v2.set(vertices[v2i], vertices[v2i + 1], vertices[v2i + 2]);

			v1.sub(v1, v0);
			v2.sub(v2, v0);

			normal.cross(v1, v2);
			normal.normalize();

			normals[v0i + 0] += normal.x;
			normals[v0i + 1] += normal.y;
			normals[v0i + 2] += normal.z;

			normals[v1i + 0] += normal.x;
			normals[v1i + 1] += normal.y;
			normals[v1i + 2] += normal.z;

			normals[v2i + 0] += normal.x;
			normals[v2i + 1] += normal.y;
			normals[v2i + 2] += normal.z;
		}

		for(int i=0;i<normals.length / 3;i++)
		{
			float length = (float)Math.sqrt(normals[3*i]*normals[3*i] +
					normals[3*i+1]*normals[3*i+1] +
					normals[3*i+2]*normals[3*i+2]);
			normals[3*i  ] /= length;
			normals[3*i+1] /= length;
			normals[3*i+2] /= length;
		}
	}
}
