package cs4620.manip;

import javax.media.opengl.GL2;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs4620.framework.Camera;
import cs4620.framework.PickingManager;
import cs4620.framework.PickingProgram;
import cs4620.scene.Scene;
import cs4620.scene.SceneNode;
import cs4620.scene.SceneProgram;

public abstract class Manip
{
	//Constant picking names for the manipulator handle
	public static final int PICK_X = 10;
	public static final int PICK_Y = 11;
	public static final int PICK_Z = 12;
	public static final int PICK_CENTER = 13;

	public static final byte X_AXIS = 0;
	public static final byte Y_AXIS = 1;
	public static final byte Z_AXIS = 2;

	public static boolean isManipId(int id)
	{
		return id == PICK_X || id == PICK_Y || id == PICK_Z || id == PICK_CENTER;
	}

	Vector3f e0 = new Vector3f(0,0,0);
	Vector3f eX = new Vector3f(1,0,0);
	Vector3f eY = new Vector3f(0,1,0);
	Vector3f eZ = new Vector3f(0,0,1);

	protected EventListenerList changeListenerList = new EventListenerList();

	/**
	 * The mouse position that led to this manipulator being picked.
	 */
	protected Vector2f pickedMousePoint = new Vector2f();

	/**
	 * The node that this manipulator modifies.
	 */
	protected SceneNode sceneNode;
	
	/**
	 * The scene that the sceneNode is from.
	 */
	protected Scene scene;

	/**
	 * The camera associated with the active viewport.
	 */
	protected Camera camera;

	/**
	 * Which axis is currently begin dragged.
	 * The value is either PICK_X, PICK_Y, PICK_Z, or PICK_CENTER.
	 */
	protected int axisMode;

	/**
	 * Determines the action of the manipulator when it it dragged.  Responsible
	 * for computing the change the manipulators transformation from the
	 * supplied mousePosition and the current mouseDelta.
	 * @param mousePosition
	 * @param mouseDelta
	 */
	public abstract void dragged(Vector2f mousePosition, Vector2f mouseDelta);

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Various methods for controlling manipulators
	////////////////////////////////////////////////////////////////////////////////////////////////////

	public void released() {
		axisMode = 0;
		fireStateChanged();
	}

	public void setPickedInfo(int newAxis, Camera camera, Vector2f lastMousePoint) {
		this.pickedMousePoint.set(lastMousePoint);
		this.camera = camera;
		axisMode = newAxis;
	}
	
	public void setScene(Scene scene)
	{
		this.scene = scene;
	}

	public void setSceneNode(SceneNode node) {
		this.sceneNode = node;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods for rendering the manipulators
	////////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean drawEnabled = true;

	/**
	 * Render the manipulator.
	 */
	public abstract void glRender(GL2 gl, SceneProgram program, Matrix4f modelView, double scale);

	private double constantSize = 0.3;

	public void renderConstantSize(GL2 gl, SceneProgram program, Camera camera) {
		if (!drawEnabled || (sceneNode == null)) return;
		double scale = camera.getHeight() * constantSize;
		
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT); // make sure manipulator appears over everything
		glRender(gl, program, camera.getView(), scale);
	}
	
	protected void setIdIfPicking(GL2 gl, SceneProgram program, int id)
	{
		if (program instanceof PickingProgram)
		{
			PickingProgram pickingProgram = (PickingProgram) program;
			PickingManager.setId(gl, pickingProgram, id);
		}
	}

	public void addChangeListener(ChangeListener listener)
	{
		changeListenerList.add(ChangeListener.class, listener);
	}

	public void removeChangeListener(ChangeListener listener)
	{
		changeListenerList.remove(ChangeListener.class, listener);
	}

	protected void fireStateChanged()
	{
		Object[] listeners = changeListenerList.getListenerList();
		ChangeEvent event = new ChangeEvent(this);
		for (int i = 0; i < listeners.length; i += 2)
		{
			if (listeners[i] == ChangeListener.class)
			{
				((ChangeListener) listeners[i + 1]).stateChanged(event);
			}
		}
	}

	public SceneNode getSceneNode()
	{
		return sceneNode;
	}
}
