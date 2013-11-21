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
    		// use 6 here to help divide the segments
    		int segments = (int) (6 / tolerance);
    		// number of vertices of the spline
    		int nvertices = ((BSpline)curve).getLengthBuffer().length;
    		// angle step for iteration
    		float angleDelta = 2f * (float)Math.PI / segments;
    		segments++;
    		// total points number
    		int numPts = segments * nvertices;
    		// initialize
    		float[] positions = new float[numPts*3];
    		float[] normals = new float[numPts*3];
    		float[] texCoords = new float[numPts*2];
    		int[] triangles = new int[(segments-1)*(nvertices-1)*6];
    		int[] lines = new int[(segments-1)*(nvertices-1)*4];

    		float angle = 0;
    		int triangleIdx = 0;
    		int lineIdx = 0;
    		BSpline bSpline = ((BSpline)curve);
    		
    		// loop from top to bottom
    		for (int i = 0; i < nvertices; i++)
    		{
    			angle = 0;
    			float x = curve.vertices[2*i];
    			float y = curve.vertices[2*i+1];
    			float xn = curve.normals[2*i];
    			float yn = curve.normals[2*i+1];
    			// texture coordinate from 1 to 0
    			float texY = 1f - (bSpline.getLengthBuffer()[i] / bSpline.getTotalLength());
    			
    			// loop around the axis
    			for (int j = 0; j < segments; j++)
    			{
    				float texX = angle / (2f * (float)Math.PI);
    				// compute the 3d positions
    				positions[3*(i*segments+j)] = (float)Math.cos(angle) * x;
    				positions[3*(i*segments+j)+1] = y;
    				positions[3*(i*segments+j)+2] = -(float)Math.sin(angle) * x;
    				// compute corresponding normals
    				normals[3*(i*segments+j)] = (float)Math.cos(angle) * xn;
    				normals[3*(i*segments+j)+1] = yn;
    				normals[3*(i*segments+j)+2] = -(float)Math.sin(angle) * xn;
    				// save texture coordinates
    				texCoords[2*(i*segments+j)] = texX;
    				texCoords[2*(i*segments+j)+1] = texY;
    				
    				// set triangles and wireframe
    				if (i > 0 && j < segments-1)
    				{
    					// points to be used
    					int a0 = (i-1) * segments + j;
    					int a1 = (i-1) * segments + j+1;
    					int b0 = i * segments + j;
    					int b1 = i * segments + j+1;
    					
    					// set lines
    					// horizontal line
    					lines[2*lineIdx] = a0;
    					lines[2*lineIdx+1] = a1;
    					lineIdx ++;
    					// vertical line
    					lines[2*lineIdx] = a0;
    					lines[2*lineIdx+1] = b0;
    					lineIdx ++;
    					
    					// set triangles
    					triangles[3*triangleIdx] = a0;
    					triangles[3*triangleIdx+1] = a1;
    					triangles[3*triangleIdx+2] = b1;
    					triangleIdx ++;
    					triangles[3*triangleIdx] = a0;
    					triangles[3*triangleIdx+1] = b1;
    					triangles[3*triangleIdx+2] = b0;
    					triangleIdx ++;
    				}
    				// modified for next loop
    				angle += angleDelta;
    			}
    		}
    		// set all the values
    		setVertices(gl, positions);
    		setNormals(gl, normals);
    		setTriangleIndices(gl, triangles);
    		setWireframeIndices(gl, lines);
    		setTexCoords(gl, texCoords);
    }

	@Override
	public Object getYamlObjectRepresentation() {
		return null;
	}
}
