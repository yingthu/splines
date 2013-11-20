package cs4620.shape;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

public class Torus extends TriangleMesh
{
	private static float DEFAULT_MAJOR_RADIUS = 0.75f;
	private static float DEFAULT_MINOR_RADIUS = 0.25f;
	
	protected float majorRadius = DEFAULT_MAJOR_RADIUS;
	protected float minorRadius = DEFAULT_MINOR_RADIUS;

	public Torus(GL2 gl)
	{
		super(gl);
	}
	
	public Torus(GL2 gl, float minRad, float majRad)
	{
		super(gl);
		majorRadius = majRad;
		minorRadius = minRad;
	}

	private void torusVertex(
    		float R, float r,
    		int bigDiv, int smallDiv,
    		int bigIndex, int smallIndex,
    		float[] vertices, float[] normals, float[] texCoords, int pos)
    {
        float theta = (float)(bigIndex * 1.0f / bigDiv * 2 * Math.PI);
        float phi = (float)(smallIndex * 1.0f / smallDiv * 2 * Math.PI);
        
        float texCoordX = bigIndex * 1.0f / bigDiv;
        float texCoordY = smallIndex * 1.0f / smallDiv;

        float cosTheta = (float)Math.cos(theta);
        float sinTheta = (float)Math.sin(theta);
        float cosPhi = (float)Math.cos(phi);
        float sinPhi = (float)Math.sin(phi);

        vertices[3*pos]   = R*cosTheta + r*cosTheta*cosPhi;
        vertices[3*pos+1] = R*sinTheta + r*sinTheta*cosPhi;
        vertices[3*pos+2] = r*sinPhi;

        normals[3*pos]   = cosTheta*cosPhi;
        normals[3*pos+1] = sinTheta*cosPhi;
        normals[3*pos+2] = sinPhi;
        
        texCoords[2*pos] = texCoordX;
        texCoords[2*pos+1] = texCoordY;
    }

	@Override
	public void buildMesh(GL2 gl, float tolerance) {
		int bigDiv = (int)Math.ceil(4*Math.PI*0.75 / tolerance);
		int smallDiv = (int) Math.ceil(4*Math.PI*0.25 / tolerance);

		int vertexCount = bigDiv * smallDiv;
		float[] vertices = new float[3*vertexCount];
		float[] normals = new float[3*vertexCount];
		float[] texCoords = new float[2*vertexCount];

		for(int i0=0;i0<bigDiv;i0++)
			for(int j0=0;j0<smallDiv;j0++)
			{
				torusVertex(majorRadius, minorRadius,
						bigDiv, smallDiv, i0, j0, vertices, normals, texCoords, i0*smallDiv+j0);
			}

		int triangleCount = bigDiv * smallDiv * 2;
		int[] triangles = new int[3*triangleCount];
		for(int i0=0;i0<bigDiv;i0++)
		{
			int i1 = (i0+1) % bigDiv;
			for(int j0=0;j0<smallDiv;j0++)
			{
				int j1 = (j0+1) % smallDiv;

				triangles[6*(i0*smallDiv+j0)  ] = i0*smallDiv+j0;
				triangles[6*(i0*smallDiv+j0)+1] = i1*smallDiv+j0;
				triangles[6*(i0*smallDiv+j0)+2] = i1*smallDiv+j1;

				triangles[6*(i0*smallDiv+j0)+3] = i0*smallDiv+j0;
				triangles[6*(i0*smallDiv+j0)+4] = i1*smallDiv+j1;
				triangles[6*(i0*smallDiv+j0)+5] = i0*smallDiv+j1;
			}
		}
		
		int[] lines = new int[4 * smallDiv * bigDiv];
		int toSmall = 2 * smallDiv * bigDiv;
		for(int i0=0;i0<bigDiv;i0++)
		{
			int i1 = (i0+1) % bigDiv;
			for(int j0=0;j0<smallDiv;j0++)
			{
				int j1 = (j0+1) % smallDiv;
				
				lines[2 * (j0 * bigDiv + i0) + 0] = i0*smallDiv+j0;
				lines[2 * (j0 * bigDiv + i0) + 1] = i1*smallDiv+j0;
				lines[toSmall + 2 * (i0 * smallDiv + j0) + 0] = i0*smallDiv+j0;
				lines[toSmall + 2 * (i0 * smallDiv + j0) + 1] = i0*smallDiv+j1;
			}
		}

		setVertices(gl, vertices);
		setNormals(gl, normals);
		setTexCoords(gl, texCoords);
		setTriangleIndices(gl, triangles);
		setWireframeIndices(gl, lines);
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Torus");
		return result;
	}
}
