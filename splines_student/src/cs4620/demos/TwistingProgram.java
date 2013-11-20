package cs4620.demos;

import javax.media.opengl.GL2;

import cs4620.framework.GlslException;
import cs4620.scene.SceneProgram;

public class TwistingProgram extends SceneProgram {
	public static final String TWIST_UNIFORM       = "un_Twist";      // float
	
	public TwistingProgram(GL2 glContext, String vertexSrcFile,
			String fragmentSrcFile) throws GlslException {
		super(glContext, vertexSrcFile, fragmentSrcFile);
	}
	
	public void setTwist(GL2 gl, float twist)
	{
		if(hasUniform(TWIST_UNIFORM))
		{
			getUniform(TWIST_UNIFORM).set1Float(gl, twist);
		}
	}
}
