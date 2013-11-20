package cs4620.framework;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * A view controller with associated camera and scene drawer.
 * The drawer class draws geometry to a previously-configured
 * viewport given projection and modelview matrices, and the
 * camera class specifies the matrices used by the drawer.
 * 
 * Provides machinery for using right mouse button to drag
 * cameras around; per-camera-type specifics are implemented
 * in subclasses.
 */

public abstract class CameraController extends ViewController {

	// drawing
	protected Camera camera;
	protected GLSceneDrawer drawer;
	protected ViewsCoordinator coordinator;
	protected int viewId;
	
	// mouse state
	protected final Vector2f lastMousePosition = new Vector2f();
	protected final Vector2f currentMousePosition = new Vector2f();
	protected final Vector2f mouseDelta = new Vector2f();
	protected final Vector3f worldMotion = new Vector3f();
	protected int mode;
	
	// static
	public static final int NO_MODE = 0;
	public static final int ROTATE_MODE = 1;
	public static final int TRANSLATE_MODE = 2;
	public static final int ZOOM_MODE = 3;

	public CameraController(Camera camera, GLSceneDrawer drawer)
	{
		this(camera, drawer, null, 0);
	}

	public CameraController(Camera camera, GLSceneDrawer drawer, ViewsCoordinator coordinator, int viewId)
	{
		super();
		this.camera = camera;
		this.drawer = drawer;
		this.coordinator = coordinator;
		this.viewId = viewId;
		
		camera.updateFrame();
		mode = NO_MODE;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		drawer.init(drawable, this);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		setAndClearView(gl);

		camera.updateFrame();
		drawer.draw(drawable, this);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		super.reshape(drawable, x, y, width, height);
		if(height > 0)
			camera.setAspect(width * 1.0f / height);
	}
	
	@Override
	public void reshape(int x, int y, int width, int height, int windowWidth, int windowHeight)
	{
		super.reshape(x, y, width, height, windowWidth, windowHeight);
		if(height > 0)
			camera.setAspect(width * 1.0f / height);
	}

	public boolean isFlagSet(MouseEvent e, int flag) {
		return (e.getModifiersEx() & flag) == flag;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastMousePosition.set(e.getX(), e.getY());
		windowToViewport(lastMousePosition);

		if (!isFlagSet(e, MouseEvent.BUTTON1_DOWN_MASK) &&
			!isFlagSet(e, MouseEvent.BUTTON2_DOWN_MASK) &&
			 isFlagSet(e, MouseEvent.BUTTON3_DOWN_MASK))
		{
			if (isFlagSet(e, MouseEvent.ALT_DOWN_MASK))	{
				mode = TRANSLATE_MODE;
			} else if (isFlagSet(e, MouseEvent.CTRL_DOWN_MASK)) {
				mode = ZOOM_MODE;
			} else if (isFlagSet(e, MouseEvent.SHIFT_DOWN_MASK)) {
				mode = ROTATE_MODE;
			} else {
				mode = NO_MODE;
			}
		}
		else
			mode = NO_MODE;

		drawer.mousePressed(e, this);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mode = NO_MODE;
		drawer.mouseReleased(e, this);
	}

	protected abstract void processMouseDragged(MouseEvent e);

	@Override
	public void mouseDragged(MouseEvent e) {
		currentMousePosition.set(e.getX(), e.getY());
		windowToViewport(currentMousePosition);
		mouseDelta.sub(currentMousePosition, lastMousePosition);

		processMouseDragged(e);
		drawer.mouseDragged(e, this);

		lastMousePosition.set(e.getX(), e.getY());
		windowToViewport(lastMousePosition);
	}

	public Camera getCamera() {
		return camera;
	}
	
	public Matrix4f getView() {
		return camera.getView();
	}
	
	public Matrix4f getProjection() {
		return camera.getProjection();
	}

	public GLSceneDrawer getDrawer()
	{
		return drawer;
	}

	public Vector2f getCurrentMousePosition()
	{
		return currentMousePosition;
	}

	public Vector2f getMouseDelta()
	{
		return mouseDelta;
	}

	public Vector2f getLastMousePosition()
	{
		return lastMousePosition;
	}

	public void setViewId(int viewId)
	{
		this.viewId = viewId;
	}

	public void setCoordinator(ViewsCoordinator coordinator)
	{
		this.coordinator = coordinator;
	}
}