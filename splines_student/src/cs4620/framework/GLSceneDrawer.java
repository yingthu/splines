package cs4620.framework;

import java.awt.event.MouseEvent;

import javax.media.opengl.GLAutoDrawable;

public interface GLSceneDrawer {
	public void init(GLAutoDrawable drawable, CameraController controller);
	public void draw(GLAutoDrawable drawable, CameraController controller);

	public void mousePressed(MouseEvent e, CameraController controller);
	public void mouseReleased(MouseEvent e, CameraController controller);
	public void mouseDragged(MouseEvent e, CameraController controller);
}
