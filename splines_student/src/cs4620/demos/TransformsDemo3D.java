package cs4620.demos;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
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
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4620.framework.CameraController;
import cs4620.framework.GLSceneDrawer;
import cs4620.framework.GlslException;
import cs4620.framework.MultiViewPanel;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.framework.VertexArray;
import cs4620.framework.VerticalScrollPanel;
import cs4620.material.Material;
import cs4620.material.PhongMaterial;
import cs4620.scene.SceneProgram;
import cs4620.shape.Cube;
import cs4620.shape.CustomTriangleMesh;
import cs4620.shape.Cylinder;
import cs4620.shape.Mesh;
import cs4620.shape.Sphere;
import cs4620.shape.Torus;
import cs4620.shape.TriangleMesh;
import cs4620.ui.OneFourViewPanel;
import cs4620.ui.SceneViewPanel;
import cs4620.ui.ToleranceSliderPanel;

@SuppressWarnings("rawtypes")
public class TransformsDemo3D extends JFrame implements GLSceneDrawer, ChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;

	ArrayList<Mesh> meshes;

	SceneViewPanel sceneViewPanel;
	//ToleranceSliderPanel sliderPanel;
	//JComboBox shapeComboBox;

	boolean sliderChanged = true;
	boolean showFourView = true;
	boolean showOneView = false;
	
	// GL resources
	SceneProgram flatColorProgram;
	SceneProgram diffuseProgram;
	boolean shadersInitialized = false;
	boolean meshesInitialized = false;
	
	Vector3f [] lightPositions = new Vector3f [SceneProgram.NUM_LIGHTS];
	Vector3f [] lightColors = new Vector3f [SceneProgram.NUM_LIGHTS];
	
	PhongMaterial material;
	PhongMaterial redMaterial;
	PhongMaterial greenMaterial;
	PhongMaterial blueMaterial;
	
	// vertex arrays available
	TriangleMesh sphereMesh;
	TriangleMesh cylinderMesh;
	TriangleMesh torusMesh;
	TriangleMesh cubeMesh;
	TriangleMesh teapotMesh;
	
	// sidebar controls for editing transforms
	int controlYIndex = 0;
	boolean sidebarAdded = false;
	JPanel rightPanel;
	JPanel rightContentPanel;
	JPanel lastPanel;
	ArrayList<TransformControl3D> transformControls;
	HashMap<String, TranslationControl3D> translationControls;
	HashMap<String, ScalingControl3D> scalingControls;
	HashMap<String, RotationControl3D> rotationControls;

	public TransformsDemo3D() {
		super("CS 4620 Demo: Transforms in 3D");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
            	terminate();
            }
        });

		sceneViewPanel = new SceneViewPanel(this);
		getContentPane().add(sceneViewPanel, BorderLayout.CENTER);

		//sliderPanel = new ToleranceSliderPanel(this);
		//getContentPane().add(sliderPanel, BorderLayout.EAST);

		//shapeComboBox = new JComboBox();
		//getContentPane().add(shapeComboBox, BorderLayout.NORTH);

		initMaterial();
		initLights();
		
		rightPanel = new VerticalScrollPanel(new BorderLayout());
		rightContentPanel = new JPanel(new GridBagLayout());
		rightPanel.add(rightContentPanel, BorderLayout.NORTH);
		createTopPanel(rightContentPanel);
		createLastPanel(rightContentPanel);
		
		translationControls = new HashMap<String, TranslationControl3D>();
		scalingControls = new HashMap<String, ScalingControl3D>();
		rotationControls = new HashMap<String, RotationControl3D>();
	}

	public void run()
	{
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		sceneViewPanel.startAnimation();
	}

	public static void main(String[] args)
	{
		new TransformsDemo3D().run();
	}
	
	private void initMaterial()
	{
		material = new PhongMaterial();
		//material.setAmbient(0.05f, 0.05f, 0.05f);
		material.setAmbient(0.0f, 0.0f, 0.0f);
		material.setDiffuse(1.0f, 1.0f, 1.0f); // formerly red
		material.setSpecular(1.0f, 1.0f, 1.0f);
		material.setShininess(50.0f);
		
		redMaterial = new PhongMaterial();
		//material.setAmbient(0.05f, 0.05f, 0.05f);
		redMaterial.setAmbient(0.0f, 0.0f, 0.0f);
		redMaterial.setDiffuse(1.0f, 0.0f, 0.0f);
		redMaterial.setSpecular(1.0f, 1.0f, 1.0f);
		redMaterial.setShininess(50.0f);
		
		greenMaterial = new PhongMaterial();
		//material.setAmbient(0.05f, 0.05f, 0.05f);
		greenMaterial.setAmbient(0.0f, 0.0f, 0.0f);
		greenMaterial.setDiffuse(0.0f, 1.0f, 0.0f);
		greenMaterial.setSpecular(1.0f, 1.0f, 1.0f);
		greenMaterial.setShininess(50.0f);
		
		blueMaterial = new PhongMaterial();
		//material.setAmbient(0.05f, 0.05f, 0.05f);
		blueMaterial.setAmbient(0.0f, 0.0f, 0.0f);
		blueMaterial.setDiffuse(0.0f, 0.0f, 1.0f);
		blueMaterial.setSpecular(1.0f, 1.0f, 1.0f);
		blueMaterial.setShininess(50.0f);
	}
	
	private void initLights() {
		// default all to be "off"
		for(int i = 0; i < SceneProgram.NUM_LIGHTS; i++)
		{
			lightPositions[i] = new Vector3f(0.0f, 0.0f, 0.0f);
			lightColors[i] = new Vector3f(0.0f, 0.0f, 0.0f);
		}
		
		// set a few to be interesting
		lightPositions[0] = new Vector3f(5.0f, 5.0f, 5.0f);
		lightColors[0] = new Vector3f(1.0f, 1.0f, 1.0f);
		
		//lightPositions[1] = new Vector3f(-2.0f, -5.0f, -2.0f);
		//lightColors[1] = new Vector3f(0.05f, 0.05f, 0.05f);
		lightPositions[1] = new Vector3f( 5.0f,  0.0f,  0.0f);
		lightPositions[2] = new Vector3f( 0.0f,  5.0f,  0.0f);
		lightPositions[3] = new Vector3f( 0.0f,  0.0f,  5.0f);
		lightPositions[4] = new Vector3f(-5.0f,  0.0f,  0.0f);
		lightPositions[5] = new Vector3f( 0.0f, -5.0f,  0.0f);
		lightPositions[6] = new Vector3f( 0.0f,  0.0f, -5.0f);
		
		float low = 0.05f;
		float med = 0.1f;
		float high = 0.2f;
		lightColors[1] = new Vector3f(high, high, high);
		lightColors[2] = new Vector3f(med, med, med);
		lightColors[3] = new Vector3f(med, med, med);
		lightColors[4] = new Vector3f(low, low, low);
		lightColors[5] = new Vector3f(low, low, low);
		lightColors[6] = new Vector3f(low, low, low);
	}
	
	private void createTopPanel(JPanel rightPanel) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		/*
		JLabel label = new JLabel("Output vertex =");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(label);
		label = new JLabel("Viewport transform");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(label);
		*/
		JLabel label = new JLabel("Transformation matrices:");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(label);
		
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
		/*
		JLabel label = new JLabel("  * Input vertex");
		lastPanel.add(label, BorderLayout.CENTER);
		lastPanel.add(new JLabel("    "), BorderLayout.SOUTH); // yay hacks
		*/
		
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

	@SuppressWarnings("unchecked")
	private void initMeshes(GL2 gl)
	{
		// build a few meshes
		if(meshesInitialized)
			return;
		
		meshes = new ArrayList<Mesh>();
		
		sphereMesh = new Sphere(gl);
		meshes.add(sphereMesh);
		
		cylinderMesh = new Cylinder(gl);
		meshes.add(cylinderMesh);
		
		torusMesh = new Torus(gl);
		meshes.add(torusMesh);
		
		cubeMesh = new Cube(gl);
		meshes.add(cubeMesh);
		
		try {
			teapotMesh = new CustomTriangleMesh(gl, new File("data/meshes/teapot.msh"));
			meshes.add(teapotMesh);
		} catch (Exception e) {
			System.err.println("FAIL: loading teapot");
			e.printStackTrace();
			System.exit(1);
		}
		
		updateMeshTolerance(gl);

		//shapeComboBox.addItem("Sphere");
		//shapeComboBox.addItem("Cylinder");
		//shapeComboBox.addItem("Torus");
		
		meshesInitialized = true;
	}

	public void init(GLAutoDrawable drawable, CameraController cameraController) {
		final GL2 gl = drawable.getGL().getGL2();
		
		initShaders(gl);
		initMeshes(gl);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Set depth buffer.
		gl.glClearDepth(1.0f);
		gl.glDepthFunc(GL2.GL_LESS);
		gl.glEnable(GL2.GL_DEPTH_TEST);

		// Set blending mode.
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL2.GL_BLEND);

		// Forces OpenGL to normalize transformed normals to be of
		// unit length before using the normals in OpenGL's lighting equations.
		gl.glEnable(GL2.GL_NORMALIZE);

		// Cull back faces.
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		//oneFourViewPanel.startAnimation();
		sceneViewPanel.startAnimation();
	}

	protected void initShaders(GL2 gl) {
		if(shadersInitialized)
			return;
		
		try {
			diffuseProgram = new SceneProgram(gl, "diffuse.vs", "diffuse.fs");
			flatColorProgram = new SceneProgram(gl, "flatcolor.vs", "flatcolor.fs");
		} catch (GlslException e) {
			System.err.println("FAIL: making shader programs");
			e.printStackTrace();
			System.exit(1);
		}
		
		shadersInitialized = true;
	}
	
	private void setLights(GL2 gl, SceneProgram program, Matrix4f worldToCamera)
	{
		// Transform lights to eye space and set them as uniforms for program.
		// (Lighting will be done on eye-space geometry.)
		Vector4f positionH = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
		Vector3f [] transformedPositions = new Vector3f [SceneProgram.NUM_LIGHTS];
		
		for(int i = 0; i < SceneProgram.NUM_LIGHTS; i++)
		{
			positionH = new Vector4f(lightPositions[i].x, lightPositions[i].y, lightPositions[i].z, 1.0f);
			worldToCamera.transform(positionH);
			transformedPositions[i] = new Vector3f(positionH.x, positionH.y, positionH.z);
		}
		
		program.setLightIntensities(gl, lightColors);
		program.setLightPositions(gl, transformedPositions);
	}
	
	protected Matrix4f getTranslate(String name)
	{
		// check for existence of transformation
		TranslationControl3D control;
		if(translationControls.containsKey(name))
		{
			control = translationControls.get(name);
		}
		else
		{
			// create control and add to map
			control = new TranslationControl3D(this, name);
			translationControls.put(name, control);
			addSidebarPanel(control.getPanel());
			//System.out.println("Supposedly added translation at name " + name);
		}
		return control.getTransform();
	}
	
	protected Matrix4f getScale(String name)
	{
		// check for existence of transformation
		ScalingControl3D control;
		if(scalingControls.containsKey(name))
		{
			control = scalingControls.get(name);
		}
		else
		{
			// create control and add to map
			control = new ScalingControl3D(this, name);
			scalingControls.put(name, control);
			addSidebarPanel(control.getPanel());
		}
		return control.getTransform();
	}
	
	protected Matrix4f getRotate(String name)
	{
		// check for existence of transformation
		RotationControl3D control;
		if(rotationControls.containsKey(name))
		{
			control = rotationControls.get(name);
		}
		else
		{
			// create control and add to map
			control = new RotationControl3D(this, this, name);
			rotationControls.put(name, control);
			addSidebarPanel(control.getPanel());
		}
		return control.getTransform();
	}
	
	public void drawMesh(GL2 gl, Mesh shape, Material mat, SceneProgram program)
	{
		if(sceneViewPanel.isWireframeMode())
		{
			mat.drawUsingProgram(gl, program, shape, true);
		}
		else
		{
			mat.drawUsingProgram(gl, program, shape, false);
		}
	}
	
	public void drawAxes(GL2 gl, SceneProgram program, Matrix4f transform)
	{
		Matrix4f axisTransf;
		
		// X
		//program.setMaterial(gl, redMaterial);
		axisTransf = new Matrix4f(transform);
		axisTransf.mul(Transforms.translate3DH(1.0f, 0.0f, 0.0f));
		axisTransf.mul(Transforms.scale3DH(1.0f, 0.05f, 0.05f));
		program.setModelView(gl, axisTransf);
		drawMesh(gl, cubeMesh, redMaterial, program);
		
		// Y
		//program.setMaterial(gl, greenMaterial);
		axisTransf = new Matrix4f(transform);
		axisTransf.mul(Transforms.translate3DH(0.0f, 1.0f, 0.0f));
		axisTransf.mul(Transforms.scale3DH(0.05f, 1.0f, 0.05f));
		program.setModelView(gl, axisTransf);
		drawMesh(gl, cubeMesh, greenMaterial, program);
		
		// Z
		//program.setMaterial(gl, blueMaterial);
		axisTransf = new Matrix4f(transform);
		axisTransf.mul(Transforms.translate3DH(0.0f, 0.0f, 1.0f));
		axisTransf.mul(Transforms.scale3DH(0.05f, 0.05f, 1.0f));
		program.setModelView(gl, axisTransf);
		drawMesh(gl, cubeMesh, blueMaterial, program);
		
		// reset material
		//setMaterial(gl, material);
	}
	
	public void draw(GL2 gl, SceneProgram program, Matrix4f transform)
	{
		Program.use(gl, program);
		
		transform.mul(getTranslate("T1"));
		transform.mul(getScale("S1"));
		transform.mul(getRotate("R1"));
		program.setModelView(gl, transform);
		
		//drawMesh(gl, sphereMesh);
		//drawMesh(gl, cubeMesh);
		drawMesh(gl, teapotMesh, material, program);
		
		Program.unuse(gl);
	}

	public void draw(GLAutoDrawable drawable, CameraController cameraController) {
		// we don't need to clear the screen at the beginning or swap buffers at the end of this function;
		// caller takes care of this
		final GL2 gl = drawable.getGL().getGL2();
		
		// update meshes if tolerance slider has been changed
		updateMeshTolerance(gl);
		
		SceneProgram programToUse = flatColorProgram;
		if(sceneViewPanel.isLightingMode())
		{
			programToUse = diffuseProgram;
		}
		
		// set projection transform here -- will do modelview in draw()
		programToUse.setProjection(gl, cameraController.getProjection());
		
		// set material
		//programToUse.setMaterial(gl, material);
		
		// set lighting
		setLights(gl, programToUse, cameraController.getView());
		
		draw(gl, programToUse, cameraController.getView());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		/*
		if (e.getSource() == sliderPanel.getSlider())
		{
			sliderChanged = true;
		}
		*/
		for (String key : translationControls.keySet())
		{
			translationControls.get(key).updateState();
		}
		for (String key : scalingControls.keySet())
		{
			scalingControls.get(key).updateState();
		}
		for (String key : rotationControls.keySet())
		{
			rotationControls.get(key).updateState();
		}
		//canvas.repaint();
	}

	protected void updateMeshTolerance(GL2 gl)
	{
		if(sliderChanged)
		{
			float tolerance = 0.5f;//= sliderPanel.getTolerance();
			for(Mesh mesh : meshes)
				mesh.buildMesh(gl, tolerance);
			sliderChanged = false;
		}
	}

	public void terminate()
	{
		sceneViewPanel.stopAnimation();
		dispose();
		System.exit(0);
	}

	@Override
	public void mousePressed(MouseEvent e, CameraController controller) {
		// NOP

	}

	@Override
	public void mouseReleased(MouseEvent e, CameraController controller) {
		// NOP

	}

	@Override
	public void mouseDragged(MouseEvent e, CameraController controller) {
		// NOP

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		stateChanged(null);
	}
}

abstract class TransformControl3D {
	
	// update the state of the control e.g. in response to user input 
	abstract public void updateState();
	
	// apply the glWhateverMatrixTransform() transform associated with the
	// current settings of this control
	abstract public void applyTransform(GL2 gl);
	
	// get the Matrix4f homogeneous transform specified by this control
	abstract public Matrix4f getTransform();
	
	// get the underlying JPanel
	abstract public JPanel getPanel();
	
	protected static String getNamePrefix() {
		if(controlMade)
		{
			return "";
			//return "* ";
		}
		else
		{
			controlMade = true;
			//return "* ";
			return "";
		}
	}
	
	public static Dimension DEFAULT_SIZE = new Dimension(200, 90);
	private static boolean controlMade = false; // has a control been made yet?
}

class TranslationControl3D extends TransformControl3D {
	String name;
	
	float translateX = 0;
	float translateY = 0;
	float translateZ = 0;
	
	JPanel controlPanel;
	
	JSpinner translateXSpinner;
	JSpinner translateYSpinner;
	JSpinner translateZSpinner;
	
	JLabel glTranslateCommandLabel;
	
	public TranslationControl3D(ChangeListener listener, String in_name)
	{
		name = in_name;
		// create panel for controls
		controlPanel = new JPanel();
		
		// lay out control panel
		controlPanel.setLayout(new GridBagLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder(getNamePrefix() + name));
		controlPanel.setPreferredSize(TransformControl3D.DEFAULT_SIZE);
		
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
		
		JLabel zLabel = new JLabel("z:");
		constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 2;
		constraint.insets = new Insets(3, 5, 0, 5);
		controlPanel.add(zLabel, constraint);
		
		translateZSpinner = new JSpinner(new SpinnerNumberModel(new Double(0.0), new Double(-40), new Double(40), new Double(0.05)));
		constraint.gridx = 1;
		constraint.gridy = 2;
		constraint.weightx = 1.0f;
		constraint.insets = new Insets(3, 5, 0, 5);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		controlPanel.add(translateZSpinner, constraint);
		translateZSpinner.addChangeListener(listener);          // note
	}

	@Override
	public void updateState() {
		// I've never had to triple-cast something before. I really never expected to...
		translateX = (float) (double) (Double) translateXSpinner.getValue();
		translateY = (float) (double) (Double) translateYSpinner.getValue();
		translateZ = (float) (double) (Double) translateZSpinner.getValue();
	}

	@Override
	public void applyTransform(GL2 gl) {
		//gl.glTranslated((Double)translateXSpinner.getValue(), (Double)translateYSpinner.getValue(), 0.0f);
	}
	
	@Override
	public Matrix4f getTransform()
	{
		return Transforms.translate3DH(translateX, translateY, translateZ);
	}

	@Override
	public JPanel getPanel() {
		return controlPanel;
	}
}

class ScalingControl3D extends TransformControl3D {
	String name;
	
	float scaleX = 1;
	float scaleY = 1;
	float scaleZ = 1;
	
	JPanel controlPanel;
	
	JSpinner scaleXSpinner;
	JSpinner scaleYSpinner;
	JSpinner scaleZSpinner;
	
	JLabel glScaleCommandLabel;
	
	public ScalingControl3D(ChangeListener listener, String in_name)
	{
		name = in_name;
		controlPanel = new JPanel();
		
		controlPanel.setLayout(new GridBagLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder(getNamePrefix() + name));
		controlPanel.setPreferredSize(TransformControl3D.DEFAULT_SIZE);
				
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
		
		JLabel zLabel = new JLabel("z:");
		constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 2;
		constraint.insets = new Insets(3, 5, 0, 5);
		controlPanel.add(zLabel, constraint);
		
		scaleZSpinner = new JSpinner(new SpinnerNumberModel(new Double(1.0), new Double(-40), new Double(40), new Double(0.1)));
		constraint.gridx = 1;
		constraint.gridy = 2;
		constraint.weightx = 1.0f;
		constraint.insets = new Insets(3, 5, 0, 5);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		controlPanel.add(scaleZSpinner, constraint);
		scaleZSpinner.addChangeListener(listener);
	}

	@Override
	public void updateState() {
		scaleX = (float) (double) (Double) scaleXSpinner.getValue();
		scaleY = (float) (double) (Double) scaleYSpinner.getValue();
		scaleZ = (float) (double) (Double) scaleZSpinner.getValue();
	}

	@Override
	public void applyTransform(GL2 gl) {
		//gl.glScaled((Double)scaleXSpinner.getValue(), (Double)scaleYSpinner.getValue(), 1.0);
	}
	
	@Override
	public Matrix4f getTransform()
	{
		return Transforms.scale3DH(scaleX, scaleY, scaleZ);
	}
	
	@Override
	public JPanel getPanel() {
		return controlPanel;
	}
}

class RotationControl3D extends TransformControl3D {
	String name;
	
	float angle = 0;
	int axis = 0;
	
	JPanel controlPanel;
	
	JSpinner angleSpinner;
	JComboBox axisComboBox;
	
	JLabel glRotateCommandLabel;
	
	public RotationControl3D(ChangeListener changeListener, ActionListener actionListener, String in_name)
	{
		name = in_name;
		controlPanel = new JPanel();
		
		controlPanel.setLayout(new GridBagLayout());
		controlPanel.setBorder(BorderFactory.createTitledBorder(getNamePrefix() + name));
		controlPanel.setPreferredSize(TransformControl3D.DEFAULT_SIZE);
		
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
		angleSpinner.addChangeListener(changeListener);
		
		JLabel axisLabel = new JLabel("Axis:");
		constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 1;
		constraint.insets = new Insets(3, 5, 0, 5);
		controlPanel.add(axisLabel, constraint);		
		
		axisComboBox = new JComboBox();
		axisComboBox.addItem("X Axis");
		axisComboBox.addItem("Y Axis");
		axisComboBox.addItem("Z Axis");
		constraint = new GridBagConstraints();
		constraint.gridx = 1;
		constraint.gridy = 1;
		constraint.weightx = 1.0f;
		constraint.insets = new Insets(3, 5, 0, 5);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		controlPanel.add(axisComboBox, constraint);
		axisComboBox.addActionListener(actionListener);
	}

	@Override
	public void updateState() {
		angle = (float) (double) (Double) angleSpinner.getValue();
		axis = axisComboBox.getSelectedIndex();
	}

	@Override
	public void applyTransform(GL2 gl) {
		//gl.glRotated((Double)angleSpinner.getValue(), 0.0, 0.0, 1.0);
	}
	
	@Override
	public Matrix4f getTransform()
	{
		float angleRadians = (float) (angle * Math.PI / 180.0f);
		return Transforms.rotateAxisRadians3DH(axis, angleRadians);
	}
	
	@Override
	public JPanel getPanel() {
		return controlPanel;
	}
}
