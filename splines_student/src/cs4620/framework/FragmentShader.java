package cs4620.framework;

import javax.media.opengl.GL2;

public class FragmentShader extends Shader {

	public FragmentShader(GL2 gl, String srcFile)
			throws GlslException {
		super(gl, GL2.GL_FRAGMENT_SHADER, srcFile);
	}

}
