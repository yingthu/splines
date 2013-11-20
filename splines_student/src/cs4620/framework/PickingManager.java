package cs4620.framework;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class PickingManager implements GLSceneDrawer {
	
	// general mesh IDs start at UNSELECTED_ID + 1
	public static final int UNSELECTED_ID = 16;
	
	GLPickingDrawer pickingDrawer;
	boolean isPicking; // look for picking events?
	
	// at next draw of this controller, find out what is at this controller's last clicked position
	CameraController controllerToCapture;
	
	public static final int VALUES_PER_CHANNEL = 64; // MUST be a power of two
	public static final float CHANNEL_OFFSET = 1.0f / 256.0f;
	
	protected static Vector3f tempColor = new Vector3f();
	
	public static void setId(GL2 gl, PickingProgram pickingProgram, int id)
	{
		encodeId(id, tempColor);
		pickingProgram.setPickingColor(gl, tempColor);
	}
	
	public static void clearBackgroundToId(GL2 gl, int id)
	{
		encodeId(id, tempColor);
		gl.glClearColor(tempColor.x, tempColor.y, tempColor.z, 1.0f);
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT);
	}
	
	public static void encodeId(int id, Tuple3f out)
	{
		int modId = id;
		int b = modId % VALUES_PER_CHANNEL;
		modId = modId / VALUES_PER_CHANNEL;
		int g = modId % VALUES_PER_CHANNEL;
		modId = modId / VALUES_PER_CHANNEL;
		int r = modId % VALUES_PER_CHANNEL;
		if (r != modId)
		{
			throw new Error("Provided id is too large!");
		}
		
		float den = (float) VALUES_PER_CHANNEL;
		out.set(r / den + CHANNEL_OFFSET, g / den + CHANNEL_OFFSET, b / den + CHANNEL_OFFSET);
	}
	
	public static void encodeId(int id, float [] out)
	{
		int modId = id;
		int b = modId % VALUES_PER_CHANNEL;
		modId = modId / VALUES_PER_CHANNEL;
		int g = modId % VALUES_PER_CHANNEL;
		modId = modId / VALUES_PER_CHANNEL;
		int r = modId % VALUES_PER_CHANNEL;
		if (r != modId)
		{
			throw new Error("Provided id is too large!");
		}
		
		float den = (float) VALUES_PER_CHANNEL;
		out[0] = r / den + CHANNEL_OFFSET;
		out[1] = g / den + CHANNEL_OFFSET;
		out[2] = b / den + CHANNEL_OFFSET;
	}
	
	public static int decodeId(int [] in)
	{
		int div = 256 / VALUES_PER_CHANNEL;
		return ((in[0] / div) * VALUES_PER_CHANNEL + (in[1] / div)) * VALUES_PER_CHANNEL + (in[2] / div);
	}
	
	public PickingManager(GLPickingDrawer pickingDrawer)
	{
		this.pickingDrawer = pickingDrawer;
		isPicking = true;
		controllerToCapture = null;
	}

	@Override
	public void init(GLAutoDrawable drawable, CameraController controller) {
		// pass through
		pickingDrawer.init(drawable, controller);
	}

	@Override
	public void draw(GLAutoDrawable drawable, CameraController controller) {
		GL2 gl = drawable.getGL().getGL2();
		
		if (controller == controllerToCapture)
		{
			// grab old clear color
			float [] oldClearColor = new float [4];
			gl.glGetFloatv(GL2.GL_COLOR_CLEAR_VALUE, oldClearColor, 0);
			
			// clear to color for nothing-selected ID
			clearBackgroundToId(gl, UNSELECTED_ID);
			
			// draw for picking
			pickingDrawer.drawPicking(drawable, controller);
			
			// grab screenshot and get picked value
			Screenshot screenshot = new Screenshot(gl, controller);
			int [] capturedColor = new int [3];
			Vector2f lmp = controller.getLastMousePosition();
			screenshot.getColorAtNDC(lmp, capturedColor);
			//System.out.println("PickingManager: at (" + lmp.x + ", " + lmp.y + "), got "
			//		+ capturedColor[0] + ", " + capturedColor[1] + ", " + capturedColor[2]);
			
			// decode picked value and report
			int decodedId = decodeId(capturedColor);
			pickingDrawer.handlePicking(drawable, controller, decodedId);
			
			// clear to desired background color
			gl.glClearColor(oldClearColor[0], oldClearColor[1], oldClearColor[2], oldClearColor[3]);
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
			
			// draw version we want to see
			 pickingDrawer.draw(drawable, controller);
			
			controllerToCapture = null;
		}
		else
		{
			pickingDrawer.draw(drawable, controller);
		}
	}
	
	protected boolean isFlagSet(MouseEvent e, int flag) {
		return (e.getModifiersEx() & flag) == flag;
	}
	
	/**
	 * Returns whether e is the type of click that means the user wanted to pick something.
	 */
	protected boolean isPickingClick(MouseEvent e)
	{
		return isFlagSet(e, MouseEvent.BUTTON1_DOWN_MASK) &&
				!isFlagSet(e, MouseEvent.BUTTON2_DOWN_MASK) &&
				!isFlagSet(e, MouseEvent.BUTTON3_DOWN_MASK);
	}

	@Override
	public void mousePressed(MouseEvent e, CameraController controller) {
		if (isPicking && isPickingClick(e))
		{
			controllerToCapture = controller; // capture for this controller at next draw
		}
		pickingDrawer.mousePressed(e, controller);
	}

	@Override
	public void mouseReleased(MouseEvent e, CameraController controller) {
		// just pass through
		pickingDrawer.mouseReleased(e, controller);
	}

	@Override
	public void mouseDragged(MouseEvent e, CameraController controller) {
		// just pass through
		pickingDrawer.mouseDragged(e, controller);
	}

}
