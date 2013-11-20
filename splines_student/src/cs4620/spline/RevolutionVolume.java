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
    }

	@Override
	public Object getYamlObjectRepresentation() {
		return null;
	}
}
