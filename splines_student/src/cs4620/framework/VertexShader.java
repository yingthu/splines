package cs4620.framework;

import javax.media.opengl.GL2;

public class VertexShader extends Shader {

	public VertexShader(GL2 gl, String srcFile)
			throws GlslException {
		super(gl, GL2.GL_VERTEX_SHADER, srcFile);
	}

}
