package cs4620.framework;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.Program;

public class TwoDimColorProgram extends TwoDimProgram {
	
	// Attribute indices and names. Programs are not required to have all of
	// these; non-existent ones are silently ignored.
	public static final int VERTEX_INDEX = 0;
	public static final int COLOR_INDEX = 1;
	
	public static final String VERTEX_STRING = "in_Vertex";                   // vec2
	public static final String COLOR_STRING = "in_Color";                     // vec3
	
	// Uniform names. Again, programs do not need to have all of these;
	// non-existent ones will be silently ignored.
	public static final String PROJECTION_UNIFORM      = "un_Projection";     // mat3
	public static final String MODELVIEW_UNIFORM       = "un_ModelView";      // mat3
	
	public static final String COLOR_UNIFORM           = "un_Color";          // vec3
	
	/**
	 * Returns the map of attribute indices to attribute variables that is expected
	 * by shaders written for this class.
	 */
	public static HashMap<Integer, String> getAttributeMap() {
		HashMap<Integer, String> attributeMap = new HashMap<Integer, String>();
		
		attributeMap.put(VERTEX_INDEX, VERTEX_STRING);
		attributeMap.put(COLOR_INDEX, COLOR_STRING);
		
		return attributeMap;
	}

	public TwoDimColorProgram(GL2 glContext, String vertexSrcFile,
			String fragmentSrcFile) throws GlslException {
		super(glContext, vertexSrcFile, fragmentSrcFile, getAttributeMap());
	}
	
	/**
	 * Should only be invoked by subclasses that want to supply an extended attribute map.
	 */
	public TwoDimColorProgram(GL2 glContext, String vertexSrcFile, 
			String fragmentSrcFile, Map<Integer, String> attributeMap) throws GlslException {
		super(glContext, vertexSrcFile, fragmentSrcFile, attributeMap);
	}
	
	// setters for expected uniforms, to avoid annoyance
	// of program.getUniform("un_SomeLongName").setSomeType(someOtherName)
	// and of having to query for whether specific shader uses this or that
	// uniform in practice.
	
	public void setProjection(GL2 gl, Matrix3f projection)
	{
		if(hasUniform(PROJECTION_UNIFORM))
		{
			getUniform(PROJECTION_UNIFORM).setMatrix3(gl, projection);
		}
	}
	
	public void setModelView(GL2 gl, Matrix3f modelview)
	{
		if(hasUniform(MODELVIEW_UNIFORM))
		{
			getUniform(MODELVIEW_UNIFORM).setMatrix3(gl, modelview);
		}
	}
	
	public void setColor(GL2 gl, Vector3f color)
	{
		if(hasUniform(COLOR_UNIFORM))
		{
			getUniform(COLOR_UNIFORM).setVector3(gl, color);
		}
	}
	
	public void setColor(GL2 gl, float r, float g, float b)
	{
		if(hasUniform(COLOR_UNIFORM))
		{
			getUniform(COLOR_UNIFORM).set3Float(gl, r, g, b);
		}
	}
}

