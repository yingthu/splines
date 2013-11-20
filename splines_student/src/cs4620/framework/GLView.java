package cs4620.framework;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.awt.GLCanvas;

public class GLView extends GLCanvas {
	private static final long serialVersionUID = 1L;

	public GLView(GLCapabilities glCapabilities) {
		super(glCapabilities);
	}

	public GLView(GLCapabilities glCapabilities, GLContext sharedWith)
	{
		super(glCapabilities, sharedWith);
	}

	public void addGLController(GLController controller) {
		addGLEventListener(controller);
		addMouseListener(controller);
		addMouseMotionListener(controller);
		addKeyListener(controller);
	}
}
