package cs4620.framework;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;

/**
 * An abstraction for classes that manipulate a view of
 * an OpenGL-capable window. Implementations are expected to abide by
 * the following conventions:
 * * Must only modify the contents of the viewport specified by
 *   most recent call to reshape().
 * * draw() must clear its viewport in addition to any drawing.
 * * draw() should not assume previous state of glViewport, glClearColor,
 *   or glScissor.
 * * reshape() should not rely on GLAutoDrawable argument being non-null.
 * 
 * The base class has a draw() method that simply clears the viewport to
 * the backgroundColor color. 
 */

public class ViewController implements GLController {

	// Is this the only controller? If so, will perform operations
	// such as clearing buffer at start of display()
	protected boolean onlyController = true;
	protected Vector3f backgroundColor;
	
	// viewport
	protected int bottom;
	protected int left;
	protected int width;
	protected int height;
	protected int windowWidth;
	protected int windowHeight;
	
	public static final Vector3f DEFAULT_BACKGROUND_COLOR = new Vector3f(0.0f, 0.0f, 0.0f);
	
	public ViewController()
	{
		bottom = 0;
		left = 0;
		width = 0;
		height = 0;
		windowWidth = 0;
		windowHeight = 0;
		backgroundColor = new Vector3f(DEFAULT_BACKGROUND_COLOR);
	}
	
	public void setViewport(GL2 gl)
	{
		gl.glViewport(left, bottom, width, height);
	}
	
	public void setBackgroundColor(Vector3f backgroundColor)
	{
		this.backgroundColor.set(backgroundColor);
	}
	
	public Vector3f getBackgroundColor()
	{
		return backgroundColor;
	}
	
	public void setAndClearView(GL2 gl)
	{
		// set scissor to constrain glClear to this viewport, and clear
		gl.glScissor(left, bottom, width, height);
		gl.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		setViewport(gl);
	}
	
	public int getBottom()
	{
		return bottom;
	}
	
	public int getLeft()
	{
		return left;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Returns whether the viewport contains the window coordinate (x,y) (origin at bottom left)
	 */
	public boolean contains(int x, int y)
	{
		return (x >= this.left) && (y >= this.bottom) && (x - this.left < this.width) && (y - this.bottom < this.height);
	}
	
	/**
	 * Map the given coordinates from the canvas's mouse*-callback coordinate
	 * space to the (x,y) pixel coordinates within the viewport, with the origin
	 * at the bottom-left of the viewport.
	 */
	
	public void windowToViewportPixels(Tuple2f p) {
		p.set(p.x - left, (windowHeight - p.y - 1) - bottom);
	}
	
	public void viewportPixelsToViewport(Tuple2f p) {
		p.set((2 * p.x - width) / width, (2 * p.y - height) / height);
	}
	
	/**
	 * Map the given coordinates from the canvas's mouse*-callback coordinate
	 * space to the (x,y) NDC for this viewport, given the size and location of
	 * the viewport in the window.  
	 */
	
	public void windowToViewport(Tuple2f p) {
		windowToViewportPixels(p);
		viewportPixelsToViewport(p);
	}
	
	public void setOnlyController(boolean onlyController)
	{
		this.onlyController = onlyController;
	}
	
	public boolean isOnlyController()
	{
		return onlyController;
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		// default implementation simply clears screen
		// can be useful if one wants to fill a region with a solid color
		setAndClearView(drawable.getGL().getGL2());
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// NOP

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// NOP

	}
	
	/**
	 * Reshape the viewport. MUST ALLOW drawable TO BE NULL.
	 */

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		if (width <= 0 || height <= 0)
			return;

		this.left = x;
		this.bottom = y;
		this.width = width;
		this.height = height;
		
		// if window dimensions not explicitly set, assume viewport is only one in window
		this.windowWidth = width;
		this.windowHeight = height;
	}
	
	public void reshape(int x, int y, int width, int height)
	{
		reshape(null, x, y, width, height);
	}
	
	public void reshape(int x, int y, int width, int height, int windowWidth, int windowHeight)
	{
		if (width <= 0 || height <= 0)
			return;

		this.left = x;
		this.bottom = y;
		this.width = width;
		this.height = height;
		
		// if window dimensions not explicitly set, assume viewport is only one in window
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// NOP

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// NOP

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// NOP

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// NOP

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// NOP

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// NOP

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// NOP

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// NOP

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// NOP

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// NOP

	}
}
