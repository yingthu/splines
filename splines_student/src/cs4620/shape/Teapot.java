package cs4620.shape;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

// Wrap up the GLUT teapot object in our Mesh class
public class Teapot extends CustomTriangleMesh {

	float scale = 1.0f; // Defines the scale of the teapot

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public Teapot(GL2 gl) throws Exception {
		super(gl, new File("data/meshes/teapot2.msh"));
	}

	@Override
	public void buildMesh(GL2 gl, float tolerance) {
		// GLUT is in control of that
	}

	public Object getYamlObjectRepresentation() {
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Teapot");
		return result;
	}

}
