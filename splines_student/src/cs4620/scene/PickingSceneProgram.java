package cs4620.scene;

import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.PickingProgram;

public class PickingSceneProgram extends SceneProgram implements PickingProgram {
	
	// color to be used when drawing for picking
	public static final String PICKING_COLOR_UNIFORM   = "un_PickingColor";   // vec3
	
	public PickingSceneProgram(GL2 glContext, String vertexSrcFile,
			String fragmentSrcFile) throws GlslException {
		super(glContext, vertexSrcFile, fragmentSrcFile, SceneProgram.getAttributeMap());
	}
	
	/**
	 * Should only be invoked by subclasses that want to supply an extended attribute map.
	 */
	public PickingSceneProgram(GL2 glContext, String vertexSrcFile, 
			String fragmentSrcFile, Map<Integer, String> attributeMap) throws GlslException {
		super(glContext, vertexSrcFile, fragmentSrcFile, attributeMap);
	}

	@Override
	public void setPickingColor(GL2 gl, Vector3f pickingColor) {
		if(hasUniform(PICKING_COLOR_UNIFORM))
		{
			getUniform(PICKING_COLOR_UNIFORM).setVector3(gl, pickingColor);
		}
	}
}
