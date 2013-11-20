package cs4620.framework;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

/**
 * Interface for programs that accept picking data
 * @author daniel
 *
 */

public interface PickingProgram {
	
	// set the color used for picking 
	public void setPickingColor(GL2 gl, Vector3f pickingColor);
}
