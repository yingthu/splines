package cs4620.spline;

import javax.media.opengl.GL2;

import cs4620.shape.TriangleMesh;

/*
 * A revolution volume. Generates a mesh by revolving its control curve
 * around the y axis.
 */
public class RevolutionVolume extends TriangleMesh {
    protected DiscreteCurve curve;

    public RevolutionVolume(GL2 gl, DiscreteCurve curve) {
        super(gl);
        this.curve = curve;
    }

    @Override
    public void buildMesh(GL2 gl, float tolerance) {
        // TODO (Splines Problem 1, Section 3.4):
    	// Compute the positions, normals, and texture coordinates for the surface
    	// of revolution mesh. Start with the vertices in the DiscreteCurve.
    		int segments = (int) (4 / tolerance);
    		int nvertices = ((BSpline)curve).getLengthBuffer().length;
    		float angleDelta = 2f * (float) Math.PI / segments;
    		segments++;
    		int numPts = segments * nvertices;
    		float[] positions = new float[numPts*3];
    		float[] normals = new float[numPts*3];
    		float[] texCoords = new float[numPts*2];
    		int[] triangles = new int[(segments-1)*(nvertices)*6];
    		
    		float angle = 0;
    		int triangleIdx = 0;
    		BSpline bSpline = ((BSpline)curve);
    		for (int i = 0; i < nvertices; i++)
    		{
    			angle = 0;
    			float x = curve.vertices[2*i];
    			float y = curve.vertices[2*i+1];
    			float xn = curve.normals[2*i];
    			float yn = curve.normals[2*i+1];
    			float texY = 1f - bSpline.getLengthBuffer()[i];
    			
    			for (int j = 0; j < segments; j++)
    			{
    				float texX = 1f - angle / (float) Math.PI;
    				positions[3*(i*segments+j)] = (float)Math.cos(angle) * x;
    				positions[3*(i*segments+j)+1] = y;
    				positions[3*(i*segments+j)+2] = -(float)Math.sin(angle) * x;
    				normals[3*(i*segments+j)] = (float)Math.cos(angle) * xn;
    				normals[3*(i*segments+j)+1] = yn;
    				normals[3*(i*segments+j)+2] = -(float)Math.sin(angle) * xn;
    				texCoords[2*(i*segments+j)] = texX;
    				texCoords[2*(i*segments+j)+1] = texY;
    				if (i > 0 && j < segments-1)
    				{
    					int a0 = (i-1) * segments + j;
    					int a1 = (i-1) * segments + j+1;
    					int b0 = i * segments + j;
    					int b1 = i * segments + j+1;
    					
    					triangles[3*triangleIdx] = a0;
    					triangles[3*triangleIdx+1] = a1;
    					triangles[3*triangleIdx+2] = b1;
    					triangleIdx ++;
    					triangles[3*triangleIdx] = a0;
    					triangles[3*triangleIdx+1] = b1;
    					triangles[3*triangleIdx+2] = b0;
    					triangleIdx ++;
    				}
    				angle += angleDelta;
    			}
    		}
    		setVertices(gl, positions);
    		setNormals(gl, normals);
    		setTriangleIndices(gl, triangles);
    		setTexCoords(gl, texCoords);
    }

	@Override
	public Object getYamlObjectRepresentation() {
		return null;
	}
}
