package cs4620.demos;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.IndexBuffer;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.framework.TwoDimProgram;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;
import cs4620.framework.VerticalScrollPanel;

public class DemoTransforms extends JFrame implements GLEventListener, ChangeListener {	
	private static final long serialVersionUID = 1L;
	
	// GL resources
	TwoDimProgram twoDimProgram;
	VertexArray houseOutlineArray;
	VertexArray houseShapeArray;
	VertexArray catOutlineArray;
	VertexArray catShapeArray;
	VertexArray squareArray;
	VertexArray circleArray;
	VertexArray coordinateFrameArray;
	VertexArray gridArray;
	
	Matrix3f viewportTransform = new Matrix3f();
	
	Vector3f RED = new Vector3f(1.0f, 0.0f, 0.0f);
	Vector3f GREEN = new Vector3f(0.0f, 1.0f, 0.0f);
	Vector3f BLUE = new Vector3f(0.0f, 0.0f, 1.0f);
	Vector3f DARK_BLUE = new Vector3f(0.1f, 0.1f, 0.2f);
	Vector3f LIGHT_BLUE = new Vector3f(0.6f, 0.6f, 1.0f);
	Vector3f YELLOW = new Vector3f(1.0f, 1.0f, 0.0f);
	Vector3f BLACK = new Vector3f(0.0f, 0.0f, 0.0f);
	Vector3f WHITE = new Vector3f(1.0f, 1.0f, 1.0f);
	Vector3f DARK_GRAY = new Vector3f(0.3f, 0.3f, 0.3f);
	Vector3f LIGHT_GRAY = new Vector3f(0.8f, 0.8f, 0.8f);
	
	Vector3f AXIS1 = new Vector3f(0.9f, 0.9f, 0.9f);
	Vector3f AXIS2 = new Vector3f(0.9f, 0.45f, 0.9f);
	Vector3f AXIS3 = new Vector3f(0.45f, 0.9f, 0.9f);
	Vector3f AXIS4 = new Vector3f(1.0f, 0.2f, 0.0f);
	
	Vector3f GRID1_COLOR = WHITE;
	Vector3f GRID2_COLOR = BLUE;
	Vector3f GRID3_COLOR = YELLOW;
	Vector3f GRID4_COLOR = DARK_GRAY;
	
	Vector3f HOUSE_OUTLINE_COLOR = BLACK;
	Vector3f HOUSE_SHAPE_COLOR = LIGHT_BLUE;
	Vector3f CAT_OUTLINE_COLOR = BLACK;
	Vector3f CAT_SHAPE_COLOR = WHITE;
	Vector3f BACKGROUND_COLOR = LIGHT_GRAY;
	
	
	
	// sidebar controls for editing transforms
	int controlYIndex = 0;
	boolean sidebarAdded = false;
	JPanel rightPanel;
	JPanel rightContentPanel;
	JPanel lastPanel;
	ArrayList<TransformControl2D> TransformControl2Ds;
	HashMap<String, TranslationControl2D> TranslationControl2Ds;
	HashMap<String, ScalingControl2D> ScalingControl2Ds;
	HashMap<String, RotationControl2D> RotationControl2Ds;	
	
	GLCanvas canvas;
	
	GridBagConstraints constraint;
	
	public DemoTransforms() {
		super("CS 4620 Demo: Transforms in 2D");
		
		// initialize GL canvas
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setSampleBuffers(true);
		capabilities.setNumSamples(8);
		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.setPreferredSize(new Dimension(600,600));
		viewportTransform.setIdentity();
		
		// add canvas to window
		getContentPane().add(canvas, BorderLayout.CENTER);
		
		// create/populate scrolling right control panel
		rightPanel = new VerticalScrollPanel(new BorderLayout());
		rightContentPanel = new JPanel(new GridBagLayout());
		rightPanel.add(rightContentPanel, BorderLayout.NORTH);
		createTopPanel(rightContentPanel);
		createLastPanel(rightContentPanel);
		
		TranslationControl2Ds = new HashMap<String, TranslationControl2D>();
		ScalingControl2Ds = new HashMap<String, ScalingControl2D>();
		RotationControl2Ds = new HashMap<String, RotationControl2D>();
				
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
	
	void draw(GL2 gl)
	{
		Program.use(gl, twoDimProgram);
		
		// M = transformation from world space to viewport (the GL window)
		Matrix3f transform = Transforms.identity2DH();
		
		// draw coordinate system for viewport transformation
		twoDimProgram.setModelView(gl, transform);
		
		gl.glLineWidth(1.0f);
		twoDimProgram.setColor(gl, BLUE);
		gridArray.draw(gl);
		gl.glLineWidth(2.0f);
		twoDimProgram.setColor(gl, GREEN);
		coordinateFrameArray.draw(gl);
		
		// multiply on more transformations
		transform.mul(getTranslate("T1"));  // M = M * T1
		transform.mul(getRotate("R1"));     // M = M * R1
		transform.mul(getScale("S1"));      // M = M * S1
		
		// send new transformation to program and draw house 
		twoDimProgram.setModelView(gl, transform);
		
		//solidColor2DProgram.setColor(DARK_BLUE);
		//houseShapeArray.draw();
		
		twoDimProgram.setColor(gl, YELLOW);
		houseOutlineArray.draw(gl);
		
		// draw local axes of house
		twoDimProgram.setColor(gl, RED);
		coordinateFrameArray.draw(gl);
		
		Program.unuse(gl);
	}
	
	private void initShaders(GL2 gl)
	{
		// create shader program used by the demo
		try {
			// load and build shader program
			twoDimProgram = new TwoDimProgram(gl, "flatcolor2d.vs", "flatcolor2d.fs");
			
			// set uniforms
			twoDimProgram.setColor(gl, 1.0f, 1.0f, 0.0f); // set color to yellow
			
			twoDimProgram.setProjection(gl, viewportTransform);
		} catch (GlslException e) {
			e.printStackTrace();
			System.err.println("FAIL: building shader program");
			System.exit(1);
		}
	}
	
	private void initVertexArrays(GL2 gl)
	{
		initHouseArrays(gl);
		initCatArrays(gl);
		initShapeArrays(gl);
		initCoordinateArrays(gl);
	}
	
	private void initHouseArrays(GL2 gl)
	{
		// vertices that make up the house, in 2D homogeneous coordinates
		float [] houseOutlineData = {
			    0.1667f,         0f,    1.0000f,  // 0  | outline
			    0.8333f,         0f,    1.0000f,  // 1  | first
			    0.8333f,    0.6667f,    1.0000f,  // 2  | loop
			    0.1667f,    0.6667f,    1.0000f,  // 3  |

			    0.1667f,    0.6667f,    1.0000f,  // 4  | outline
			    0.5000f,    1.0000f,    1.0000f,  // 5  | first strip
			    0.8333f,    0.6667f,    1.0000f,  // 6  |

			    0.2778f,         0f,    1.0000f,  // 7  | outline
			    0.2778f,    0.4444f,    1.0000f,  // 8  | second
			    0.5000f,    0.4444f,    1.0000f,  // 9  | strip
			    0.5000f,         0f,    1.0000f,  // 10 |
			    
			    0.5833f,    0.1667f,    1.0000f,  // 11 | outline
			    0.5833f,    0.3333f,    1.0000f,  // 12 | second loop
			    0.7500f,    0.3333f,    1.0000f,  // 13 |
			    0.7500f,    0.1667f,    1.0000f,  // 14 |
		};
		
		// pairs of (start point, end point) indices in vertex list above,
		// defining lines for an outline of the house
		int [] houseOutlineIndices = {
				0,1,   1,2,   2,3,   3,0,   // outline first loop
				4,5,   5,6,                 // outline first strip
				7,8,   8,9,   9,10,         // outline second strip
				11,12, 12,13, 13,14, 14,11  // outline second loop
		};
		
		int [] houseShapeIndices = {
				0,1,2,
				0,2,3,
				6,5,4
		};
		
		// set up vertex array for house outline
		
		// GL_LINES: this array specifies a sequences of (start point, end point) pairs for lines
		houseOutlineArray = new VertexArray(gl, GL2.GL_LINES);
		
		// set buffer for vertex positions
		// 3 indicates the number of floats in the data array per vertex
		houseOutlineArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, new VertexBuffer(gl, houseOutlineData, 3));
		
		// set buffer that holds the indices of vertex pairs defining the lines
		houseOutlineArray.setIndexBuffer(gl, new IndexBuffer(gl, houseOutlineIndices));
			
		// set up vertex array for house shape
		
		// GL_TRIANGLES: this array specifies a sequences of (v1, v2, v3) triples defining triangles
		houseShapeArray = new VertexArray(gl, GL2.GL_TRIANGLES);
		
		// use the same buffer of vertices as we used for the outline array
		houseShapeArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, new VertexBuffer(gl, houseOutlineData, 3));
		
		// use new set of indices specifying the triangles
		houseShapeArray.setIndexBuffer(gl, new IndexBuffer(gl, houseShapeIndices));
	}
	
	private void initCatArrays(GL2 gl)
	{
		// vertices that make up the house, in 2D homogeneous coordinates
		float [] catVertexData = {
				0.2539f, 0.7279f, 1.0f,
				0.0931f, 0.9277f, 1.0f,
				0.0931f, 0.3525f, 1.0f,
				0.1439f, 0.2096f, 1.0f,
				0.5000f, 0.0836f, 1.0f,
				0.8561f, 0.2096f, 1.0f,
				0.9068f, 0.3525f, 1.0f,
				0.9068f, 0.9277f, 1.0f,
				0.7461f, 0.7279f, 1.0f,
				0.5000f, 0.1774f, 1.0f,
				0.5000f, 0.2570f, 1.0f,
				0.3858f, 0.2088f, 1.0f,
				0.5000f, 0.1774f, 1.0f,
				0.6142f, 0.2088f, 1.0f,
				0.2400f, 0.5102f, 1.0f,
				0.2843f, 0.5600f, 1.0f,
				0.3999f, 0.5454f, 1.0f,
				0.4158f, 0.4879f, 1.0f,
				0.3822f, 0.4513f, 1.0f,
				0.2550f, 0.4674f, 1.0f,
				0.7600f, 0.5102f, 1.0f,
				0.7157f, 0.5600f, 1.0f,
				0.6001f, 0.5454f, 1.0f,
				0.5842f, 0.4879f, 1.0f,
				0.6179f, 0.4513f, 1.0f,
				0.7450f, 0.4674f, 1.0f,
				0.4158f, 0.4879f, 1.0f,
				0.4254f, 0.3525f, 1.0f,
				0.5842f, 0.4879f, 1.0f,
				0.5746f, 0.3525f, 1.0f,
				0.4254f, 0.3525f, 1.0f,
				0.5746f, 0.3525f, 1.0f,
				0.5398f, 0.2570f, 1.0f,
				0.4602f, 0.2570f, 1.0f,
				0.3279f, 0.5333f, 1.0f,
				0.3212f, 0.4832f, 1.0f,
				0.6721f, 0.5333f, 1.0f,
				0.6788f, 0.4832f, 1.0f,
				0.7265f, 0.7050f, 1.0f,
				0.8098f, 0.6672f, 1.0f,
				0.7894f, 0.8481f, 1.0f,
				0.7102f, 0.7676f, 1.0f,
				0.6412f, 0.7513f, 1.0f,
				0.5344f, 0.7879f, 1.0f,
				0.5970f, 0.6170f, 1.0f,
				0.6546f, 0.6881f, 1.0f,
				0.7251f, 0.7047f, 1.0f,
				};
		
		// pairs of (start point, end point) indices in vertex list above,
		// defining lines for an outline of the house
		int [] catOutlineIndices = {
				0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 0, // head
				14, 15, 15, 16, 16, 17, 17, 18, 18, 19, 19, 14, // eye
				20, 21, 21, 22, 22, 23, 23, 24, 24, 25, 25, 20, // eye
				30, 31, 31, 32, 32, 33, 33, 30,  // nose
				9, 10, 11, 12, 12, 13, 26, 27, 28, 29, 34, 35, 36, 37, // nose and mouth
				//38, 39, 39, 40, 40, 41, 41, 42, 42, 43, 43, 44, 44, 45, 45, 46, 46, 38  // bow
		};
		
		int [] catShapeIndices = {
				0,1,2, 0,2,3, 0,3,4, 8,6,7, 8,5,6, 8,4,5, 4,8,0,
		};
		
		// set up vertex array for cat outlines
		
		// GL_LINES: this array specifies a sequences of (start point, end point) pairs for lines
		catOutlineArray = new VertexArray(gl, GL2.GL_LINES);
		
		// set buffer for vertex positions
		// 3 indicates the number of floats in the data array per vertex
		catOutlineArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, new VertexBuffer(gl, catVertexData, 3));
		
		// set buffer that holds the indices of vertex pairs defining the lines
		catOutlineArray.setIndexBuffer(gl, new IndexBuffer(gl, catOutlineIndices));
			
		// set up vertex array for cat shape
		
		// GL_TRIANGLES: this array specifies a sequences of (v1, v2, v3) triples defining triangles
		catShapeArray = new VertexArray(gl, GL2.GL_TRIANGLES);
		
		// use the same buffer of vertices as we used for the outline array
		catShapeArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, new VertexBuffer(gl, catVertexData, 3));
		
		// use new set of indices specifying the triangles
		catShapeArray.setIndexBuffer(gl, new IndexBuffer(gl, catShapeIndices));
	}
	
	private void initShapeArrays(GL2 gl)
	{
		// vertices that make up the house, in 2D homogeneous coordinates
		float [] squareVertexData = {
				-1.0f, -1.0f, 1.0f,
				 1.0f, -1.0f, 1.0f,
				 1.0f,  1.0f, 1.0f,
				-1.0f,  1.0f, 1.0f,
				};
		
		// pairs of (start point, end point) indices in vertex list above,
		// defining lines for an outline of the house
		int [] squareOutlineIndices = {
				0, 1, 2, 3,
		};
				
		// set up vertex array for square outline
		
		// GL_LINE_LOOP: this array specifies a sequence of points, and lines connect them in a loop
		squareArray = new VertexArray(gl, GL2.GL_LINE_LOOP);
		
		// set buffer for vertex positions
		// 3 indicates the number of floats in the data array per vertex
		squareArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, new VertexBuffer(gl, squareVertexData, 3));
		
		// set buffer that holds the indices of vertex pairs defining the lines
		squareArray.setIndexBuffer(gl, new IndexBuffer(gl, squareOutlineIndices));
		
		// set up vertex array for circle outline
		
		// This one is computed rather than typed in.
		// SHOULD DO triangles to fill in the circle
		int CIRCLE_SEGMENTS = 36;
		float [] circleVertexData = new float[3 * CIRCLE_SEGMENTS];
		int [] circleOutlineIndices = new int[CIRCLE_SEGMENTS];
		for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
			double theta = 2*Math.PI * i / CIRCLE_SEGMENTS;
			circleVertexData[3*i+0] = (float)Math.cos(theta);
			circleVertexData[3*i+1] = (float)Math.sin(theta);
			circleVertexData[3*i+2] = 1.0f;
			circleOutlineIndices[i] = i;
		}
		// set up vertex array for square outline
		
		// GL_LINE_LOOP: this array specifies a sequence of points, and lines connect them in a loop
		circleArray = new VertexArray(gl, GL2.GL_LINE_LOOP);
					
		// set buffer for vertex positions
		// 3 indicates the number of floats in the data array per vertex
		circleArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, new VertexBuffer(gl, circleVertexData, 3));
		
		// set buffer that holds the indices of vertex pairs defining the lines
		circleArray.setIndexBuffer(gl, new IndexBuffer(gl, circleOutlineIndices));
	}
	
	private void initCoordinateArrays(GL2 gl)
	{
		float SCALE = 1.0f;
		int GRID_SIZE = 50;
		float [] coordinateFrameData = {
				0.0f, 0.0f, 1.0f,
				SCALE, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, SCALE, 1.0f
				// 0.8f, -0.0f, 1.0f, // arrow heads
				// 0.9f, -0.1f, 1.0f,
				// 0.9f, -0.1f, 1.0f,
				// 0.8f, -0.2f, 1.0f,
				// 0.0f,  0.8f, 1.0f,
				//-0.1f,  0.9f, 1.0f,
				//-0.1f,  0.9f, 1.0f,
				//-0.2f,  0.8f, 1.0f
		};
		
		int gridStart = -GRID_SIZE;
		int gridLines = 2 * GRID_SIZE + 1;
		int gridEnd = gridStart + gridLines - 1;
		float [] gridData = new float [3 * 4 * gridLines];
		for(int i = 0; i < gridLines; i++)
		{
			gridData[3*(4*i + 0) + 0] = (gridStart + i) * SCALE;
			gridData[3*(4*i + 0) + 1] = (gridStart) * SCALE;
			gridData[3*(4*i + 0) + 2] = 1;
			
			gridData[3*(4*i + 1) + 0] = (gridStart + i) * SCALE;
			gridData[3*(4*i + 1) + 1] = (gridEnd) * SCALE;
			gridData[3*(4*i + 1) + 2] = 1;
			
			gridData[3*(4*i + 2) + 0] = (gridStart) * SCALE;
			gridData[3*(4*i + 2) + 1] = (gridStart + i) * SCALE;
			gridData[3*(4*i + 2) + 2] = 1;
			
			gridData[3*(4*i + 3) + 0] = (gridEnd) * SCALE;
			gridData[3*(4*i + 3) + 1] = (gridStart + i) * SCALE;
			gridData[3*(4*i + 3) + 2] = 1;
		}
		
		coordinateFrameArray = new VertexArray(gl, GL2.GL_LINES);
		coordinateFrameArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, new VertexBuffer(gl, coordinateFrameData, 3));
		// if we don't specify an index buffer, the indices will be taken
		// in the order they appear in the attribute buffer(s).
		
		gridArray = new VertexArray(gl, GL2.GL_LINES);
		gridArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, new VertexBuffer(gl, gridData, 3));
	}
	
	private void createTopPanel(JPanel rightPanel) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		//JLabel label = new JLabel("Output vertex =");
		//label.setAlignmentX(Component.LEFT_ALIGNMENT);
		//panel.add(label);
		//label = new JLabel("Viewport transform");
		//label.setAlignmentX(Component.LEFT_ALIGNMENT);
		//panel.add(label);
		
		// set constraints for placement in sidebar and add
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.anchor = GridBagConstraints.WEST;
		constraint.weighty = 0;
		constraint.weightx = 1;
		constraint.gridx = 0;
		constraint.gridy = controlYIndex++;
		constraint.fill = GridBagConstraints.PAGE_START;
		constraint.ipadx = 0;
		constraint.ipady = 8;
		constraint.insets = new Insets(0,8,0,8);
		rightPanel.add(panel, constraint);
	}
	
	private void createLastPanel(JPanel rightPanel) {
		lastPanel = new JPanel(new BorderLayout());
		//JLabel label = new JLabel("  * Input vertex");
		//lastPanel.add(label, BorderLayout.CENTER);
		lastPanel.add(new JLabel("    "), BorderLayout.SOUTH); // yay hacks
		
		// set constraints for placement in sidebar and add
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.anchor = GridBagConstraints.PAGE_END;
		constraint.weighty = 0;
		constraint.weightx = 1;
		constraint.gridx = 0;
		constraint.gridy = controlYIndex + 1;
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.ipadx = 0;
		constraint.ipady = 8;
		rightPanel.add(lastPanel, constraint);
	}
	
	private void addSidebarPanel(JPanel panel) {
		if(!sidebarAdded)
		{
			// if we need at least one control, put the sidebar in the GUI
			getContentPane().add(new JScrollPane(rightPanel), BorderLayout.LINE_END);
			pack();
			//System.out.println("Supposedly added sidebar");
			sidebarAdded = true;
		}
		// remove last panel
		rightContentPanel.remove(lastPanel);
		
		// set constraints for placement in sidebar and add
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.anchor = GridBagConstraints.NORTH;
		constraint.weighty = 0;
		constraint.weightx = 1;
		constraint.gridx = 0;
		constraint.gridy = controlYIndex++;
		constraint.fill = GridBagConstraints.HORIZONTAL;
		rightContentPanel.add(panel, constraint);
		
		// re-add last panel
		constraint = new GridBagConstraints();
		constraint.anchor = GridBagConstraints.NORTH;
		constraint.weighty = 0;
		constraint.weightx = 1;
		constraint.gridx = 0;
		constraint.gridy = controlYIndex; // no ++
		constraint.fill = GridBagConstraints.HORIZONTAL;
		rightContentPanel.add(lastPanel, constraint);
	}
	
	public void run()
	{		
		setSize(800, 600);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {	
		new DemoTransforms().run();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		initShaders(gl);
		initVertexArrays(gl);
		gl.glEnable(GL2.GL_FRAMEBUFFER_SRGB);
		gl.glEnable(GL2.GL_MULTISAMPLE);
	}

	float[] firstLineColor = new float[] {0.3f, 0.3f, 0.3f};
	float[] firstXAxisColor = new float[] {0.3f, 0.3f, 0.3f};
	float[] firstYAxisColor = new float[] {0.3f, 0.3f, 0.3f};

	float[] secondLineColor = new float[] {0.6f, 0.3f, 0.6f};
	float[] secondXAxisColor = new float[] {0.6f, 0.3f, 0.6f};
	float[] secondYAxisColor = new float[] {0.6f, 0.3f, 0.6f};

	float[] thirdLineColor = new float[] {0.3f, 0.6f, 0.6f};
	float[] thirdXAxisColor = new float[] {0.3f, 0.6f, 0.6f};
	float[] thirdYAxisColor = new float[] {0.3f, 0.6f, 0.6f};

	float[] fourthLineColor = new float[] {0.5f, 0.5f, 1.0f};
	float[] fourthXAxisColor = new float[] {1.0f, 0.5f, 0.5f};
	float[] fourthYAxisColor = new float[] {0.5f, 1.0f, 0.5f};
	
	protected Matrix3f getTranslate(String name)
	{
		// check for existence of transformation
		TranslationControl2D control;
		if(TranslationControl2Ds.containsKey(name))
		{
			control = TranslationControl2Ds.get(name);
		}
		else
		{
			// create control and add to map
			control = new TranslationControl2D(this, name);
			TranslationControl2Ds.put(name, control);
			addSidebarPanel(control.getPanel());
			//System.out.println("Supposedly added translation at name " + name);
		}
		return control.getTransform();
	}
	
	protected Matrix3f getScale(String name)
	{
		// check for existence of transformation
		ScalingControl2D control;
		if(ScalingControl2Ds.containsKey(name))
		{
			control = ScalingControl2Ds.get(name);
		}
		else
		{
			// create control and add to map
			control = new ScalingControl2D(this, name);
			ScalingControl2Ds.put(name, control);
			addSidebarPanel(control.getPanel());
		}
		return control.getTransform();
	}
	
	protected Matrix3f getRotate(String name)
	{
		// check for existence of transformation
		RotationControl2D control;
		if(RotationControl2Ds.containsKey(name))
		{
			control = RotationControl2Ds.get(name);
		}
		else
		{
			// create control and add to map
			control = new RotationControl2D(this, name);
			RotationControl2Ds.put(name, control);
			addSidebarPanel(control.getPanel());
		}
		return control.getTransform();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		draw(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, 
			int x, int y, int w, int h) {
		final GL2 gl = drawable.getGL().getGL2();
		
		if (w == 0) w = 1;
		if (h == 0) h = 1;

		float aspectf = w * 1.0f / h;

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if (w > h)
		{
			viewportTransform = Transforms.ortho2DH(-aspectf, aspectf, -1.0f, 1.0f);
		}
		else
		{
			viewportTransform = Transforms.ortho2DH(-1.0f, 1.0f, -1/aspectf, 1/aspectf);
		}
		twoDimProgram.setProjection(gl, viewportTransform);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// NOP		
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		for (String key : TranslationControl2Ds.keySet())
		{
			TranslationControl2Ds.get(key).updateState();
		}
		for (String key : ScalingControl2Ds.keySet())
		{
			ScalingControl2Ds.get(key).updateState();
		}
		for (String key : RotationControl2Ds.keySet())
		{
			RotationControl2Ds.get(key).updateState();
		}
		canvas.repaint();
	}
}

abstract class TransformControl2D {
	
	// update the state of the control e.g. in response to user input 
	abstract public void updateState();
	
	// apply the glWhateverMatrixTransform() transform associated with the
	// current settings of this control
	abstract public void applyTransform(GL2 gl);
	
	// get the Matrix3f homogeneous transform specified by this control
	abstract public Matrix3f getTransform();
	
	// get the underlying JPanel
	abstract public JPanel getPanel();
	
	protected static String getNamePrefix() {
		return "";
		/*
		if(controlMade)
		{
			return "* ";
		}
		else
		{
			controlMade = true;
			return "* ";
			//return "";
		}
		*/
	}
	
	public static Dimension DEFAULT_SIZE = new Dimension(200, 90);
	private static boolean controlMade = false; // has a control been made yet?
}

class TranslationControl2D extends TransformControl2D {
	String name;
	
	float translateX = 0;
	float translateY = 0;
	
	JPanel controlPanel;
	
	JSpinner translateXSpinner;
	JSpinner translateYSpinner;
	
	JLabel glTranslateCommandLabel;
	
	public TranslationControl2D(ChangeListener listener, String in_name)
	{
		name = in_name;
		// create panel for controls
		controlPanel = new JPanel();
		
		// lay out control panel
		controlPanel.setLayout(new GridBagLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder(getNamePrefix() + name));
		controlPanel.setPreferredSize(TransformControl2D.DEFAULT_SIZE);
		
		// create and add labels/controls
		JLabel xLabel = new JLabel("x:");
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.insets = new Insets(0, 5, 0, 5);
		controlPanel.add(xLabel, constraint);		
		
		translateXSpinner = new JSpinner(new SpinnerNumberModel(new Double(0.0), new Double(-40), new Double(40), new Double(0.05)));
		constraint = new GridBagConstraints();
		constraint.gridx = 1;
		constraint.gridy = 0;
		constraint.weightx = 1.0f;
		constraint.insets = new Insets(0, 5, 0, 5);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		controlPanel.add(translateXSpinner, constraint);
		translateXSpinner.addChangeListener(listener);          // note
		
		JLabel yLabel = new JLabel("y:");
		constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 1;
		constraint.insets = new Insets(3, 5, 0, 5);
		controlPanel.add(yLabel, constraint);
		
		translateYSpinner = new JSpinner(new SpinnerNumberModel(new Double(0.0), new Double(-40), new Double(40), new Double(0.05)));
		constraint.gridx = 1;
		constraint.gridy = 1;
		constraint.weightx = 1.0f;
		constraint.insets = new Insets(3, 5, 0, 5);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		controlPanel.add(translateYSpinner, constraint);
		translateYSpinner.addChangeListener(listener);          // note
	}

	@Override
	public void updateState() {
		// I've never had to triple-cast something before. I really never expected to...
		translateX = (float) (double) (Double) translateXSpinner.getValue();
		translateY = (float) (double) (Double) translateYSpinner.getValue();
	}

	@Override
	public void applyTransform(GL2 gl) {
		gl.glTranslated((Double)translateXSpinner.getValue(), (Double)translateYSpinner.getValue(), 0.0f);
	}
	
	@Override
	public Matrix3f getTransform()
	{
		return Transforms.translate2DH(translateX, translateY);
	}

	@Override
	public JPanel getPanel() {
		return controlPanel;
	}
}

class ScalingControl2D extends TransformControl2D {
	String name;
	
	float scaleX = 1;
	float scaleY = 1;
	
	JPanel controlPanel;
	
	JSpinner scaleXSpinner;
	JSpinner scaleYSpinner;
	
	JLabel glScaleCommandLabel;
	
	public ScalingControl2D(ChangeListener listener, String in_name)
	{
		name = in_name;
		controlPanel = new JPanel();
		
		controlPanel.setLayout(new GridBagLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder(getNamePrefix() + name));
		controlPanel.setPreferredSize(TransformControl2D.DEFAULT_SIZE);
				
		JLabel xLabel = new JLabel("x:");
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.insets = new Insets(0, 5, 0, 5);
		controlPanel.add(xLabel, constraint);		
		
		scaleXSpinner = new JSpinner(new SpinnerNumberModel(new Double(1.0), new Double(-40), new Double(40), new Double(0.1)));
		constraint = new GridBagConstraints();
		constraint.gridx = 1;
		constraint.gridy = 0;
		constraint.weightx = 1.0f;
		constraint.insets = new Insets(0, 5, 0, 5);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		controlPanel.add(scaleXSpinner, constraint);
		scaleXSpinner.addChangeListener(listener);
		
		JLabel yLabel = new JLabel("y:");
		constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 1;
		constraint.insets = new Insets(3, 5, 0, 5);
		controlPanel.add(yLabel, constraint);
		
		scaleYSpinner = new JSpinner(new SpinnerNumberModel(new Double(1.0), new Double(-40), new Double(40), new Double(0.1)));
		constraint.gridx = 1;
		constraint.gridy = 1;
		constraint.weightx = 1.0f;
		constraint.insets = new Insets(3, 5, 0, 5);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		controlPanel.add(scaleYSpinner, constraint);
		scaleYSpinner.addChangeListener(listener);
	}

	@Override
	public void updateState() {
		scaleX = (float) (double) (Double) scaleXSpinner.getValue();
		scaleY = (float) (double) (Double) scaleYSpinner.getValue();		
	}

	@Override
	public void applyTransform(GL2 gl) {
		gl.glScaled((Double)scaleXSpinner.getValue(), (Double)scaleYSpinner.getValue(), 1.0);
	}
	
	@Override
	public Matrix3f getTransform()
	{
		return Transforms.scale2DH(scaleX, scaleY);
	}
	
	@Override
	public JPanel getPanel() {
		return controlPanel;
	}
}

class RotationControl2D extends TransformControl2D {
	String name;
	
	float angle = 0;
	
	JPanel controlPanel;
	
	JSpinner angleSpinner;
	
	JLabel glRotateCommandLabel;
	
	public RotationControl2D(ChangeListener listener, String in_name)
	{
		name = in_name;
		controlPanel = new JPanel();
		
		controlPanel.setLayout(new GridBagLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder(getNamePrefix() + name));
		controlPanel.setPreferredSize(TransformControl2D.DEFAULT_SIZE);
		
		JLabel angleLabel = new JLabel("Angle:");
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.insets = new Insets(0, 5, 0, 5);
		controlPanel.add(angleLabel, constraint);		
		
		angleSpinner = new JSpinner(new SpinnerNumberModel(new Double(0.0), new Double(-720.0), new Double(720.0), new Double(1)));
		constraint = new GridBagConstraints();
		constraint.gridx = 1;
		constraint.gridy = 0;
		constraint.weightx = 1.0f;
		constraint.insets = new Insets(0, 5, 0, 5);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		controlPanel.add(angleSpinner, constraint);
		angleSpinner.addChangeListener(listener);
	}

	@Override
	public void updateState() {
		angle = (float) (double) (Double) angleSpinner.getValue();
	}

	@Override
	public void applyTransform(GL2 gl) {
		gl.glRotated((Double)angleSpinner.getValue(), 0.0, 0.0, 1.0);
	}
	
	@Override
	public Matrix3f getTransform()
	{
		float angleRadians = (float) (angle * Math.PI / 180.0f);
		return Transforms.rotateRadians2DH(angleRadians);
	}
	
	@Override
	public JPanel getPanel() {
		return controlPanel;
	}
}
