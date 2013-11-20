package cs4620.framework;

import javax.media.opengl.GLAutoDrawable;

public interface GLPickingDrawer extends GLSceneDrawer {
	public void drawPicking(GLAutoDrawable drawable, CameraController controller);
	
	public void handlePicking(GLAutoDrawable drawable, CameraController controller, int pickedId);
}
