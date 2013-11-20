package cs4620.framework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLRunnable;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public abstract class MultiViewPanel extends JPanel
	implements GLController, ActionListener{
	private static final long serialVersionUID = 8291039062923534105L;
	
	/**
	 * A Panel that displays multiple "logical views" inside a single GLCanvas.
	 * Each logical view occupies some rectangle in the view and is responsible
	 * for drawing and monitoring mouse inputs in that region.
	 */
	
	// where to draw, and what to draw it with
	protected GLView glView;
	protected GLSceneDrawer drawer;
	protected Vector3f backgroundColor;
	
	// dimensions of entire canvas
	protected int width;
	protected int height;
	
	// animation
	protected Timer timer;
	protected int frameRate;
	
	// views
	protected ArrayList<ViewController> viewControllers;
	
	protected ViewController lastClickedController;
	
	public static final Vector3f DEFAULT_BACKGROUND_COLOR = new Vector3f(0.0f, 0.0f, 0.0f);
	
	public MultiViewPanel(GLSceneDrawer drawer)
	{
		super(new BorderLayout());
		this.drawer = drawer;
		backgroundColor = new Vector3f(DEFAULT_BACKGROUND_COLOR);
		
		// initialize GL view
		GLProfile glProfile = GLProfile.getDefault();
		GLCapabilities glCapabilities = new GLCapabilities(glProfile);
		glView = new GLView(glCapabilities);
		
		// init logical views
		viewControllers = new ArrayList<ViewController>();
		lastClickedController = null;
		
		// GL events will come through this object first, then to ViewController objects, and then to whatever
		// drawers the controllers are backed by (usually the main class of some project)
		glView.addGLController(this);
		add(glView, BorderLayout.CENTER);
		
		// timer
		frameRate = 60;
		timer = new Timer(1000 / frameRate, this);
		
		
	}
	
	public void addViewController(ViewController view)
	{
		view.setOnlyController(false); // is part of a multi-view managed window
		viewControllers.add(view);
	}
	
	public void setCanvasBackgroundColor(Vector3f backgroundColor)
	{
		this.backgroundColor.set(backgroundColor);
	}
	
	public void setAllViewBackgroundColors(Vector3f backgroundColor)
	{
		for (ViewController view : viewControllers)
		{
			view.setBackgroundColor(backgroundColor);
		}
	}
	
	/**
	 * Return the first ViewController containing the window coordinate (x,y) (origin at bottom-left).
	 */
	
	protected ViewController getContainingController(int x, int y)
	{
		if (viewControllers.size() == 0)
			return null;
		
		// find first containing view
		for(int i = 0; i < viewControllers.size(); i++)
		{
			ViewController view = viewControllers.get(i);
			if (view.contains(x, y))
			{
				return view;
			}
		}
		
		// no containing view
		return null;
	}
	
	/**
	 * Return the first ViewController containing the point indicated by the Java MouseEvent e.
	 * Note that mouse events are in a different coordinate system; this performs the
	 * appropriate transformation.
	 */
	
	protected ViewController getContainingController(MouseEvent e)
	{
		// NOTE: converts from java mouse event coordinates (origin at top left of screen, +y goes down)
		// to coordinates used for specifying LogicalView objects
		// and viewports (origin at bottom left of screen, +y goes up)
		return getContainingController(e.getX(), height - e.getY() - 1);
	}
	
	public static MouseEvent spoofEventPosition(MouseEvent e, int x, int y)
	{
		// returns a MouseEvent that is the same except that its position is reset to the given x,y
		
		return new MouseEvent((Component) e.getSource(),
				e.getID(),
				e.getWhen(),
				e.getModifiersEx(),
				x,//e.getX(),
				y,//e.getY(),
				e.getClickCount(),
				e.isPopupTrigger(),
				e.getButton());
	}
	
	public abstract void reshapeViews();

	@Override
	public void display(GLAutoDrawable drawable) {
		// drawable corresponds to glView (the canvas of this panel)
		
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glScissor(0, 0, width, height);                                   GLError.get(gl, "MVP.d scissor");
		gl.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f);
		                                                                     GLError.get(gl, "MVP.d color");
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);       GLError.get(gl, "MVP.d clear");
		
		for (ViewController view : viewControllers)
		{
			view.display(drawable);
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// NOP
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		drawer.init(drawable, null);
		
		// enable scissor test for viewport drawing
		final GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL2.GL_SCISSOR_TEST);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		((Component)drawable).setMinimumSize(new Dimension(0,0));
		final GL2 gl = drawable.getGL().getGL2();

		// Sometimes we get an invalid width or height.
		// Quick fix: ignore it
		if (width <= 0 || height <= 0)
			return;

		this.width = width;
		this.height = height;
		
		reshapeViews();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		lastClickedController = getContainingController(e);
		if (lastClickedController != null)
		{
			lastClickedController.mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// for now not forwarding on
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// for now not forwarding on
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastClickedController = getContainingController(e);
		if (lastClickedController != null)
		{
			lastClickedController.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (lastClickedController != null)
		{
			lastClickedController.mouseReleased(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (lastClickedController != null)
		{
			lastClickedController.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		ViewController moveController = getContainingController(e);
		if (moveController != null)
		{
			moveController.mouseMoved(e);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// not forwarding for now
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// not forwarding for now
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// not forwarding for now
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer)
		{
			glView.repaint();
		}
	}
	
	public void startAnimation() {
		timer.start();
	}

	public void stopAnimation() {
		timer.stop();
	}
	
	public boolean invoke(boolean wait, GLRunnable runnable)
	{
		return glView.invoke(wait, runnable);
	}

}
