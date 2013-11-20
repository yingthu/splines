package cs4620.problem;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4620.framework.CameraController;
import cs4620.framework.GLSceneDrawer;
import cs4620.framework.GlslException;
import cs4620.framework.MultiViewPanel;
import cs4620.framework.Program;
import cs4620.material.DiffuseMaterial;
import cs4620.scene.SceneProgram;
import cs4620.shape.Cylinder;
import cs4620.shape.Mesh;
import cs4620.shape.Sphere;
import cs4620.shape.Torus;
import cs4620.ui.OneFourViewPanel;
import cs4620.ui.SceneViewPanel;
import cs4620.ui.ToleranceSliderPanel;

@SuppressWarnings("rawtypes")
public class SceneP2 extends JFrame implements GLSceneDrawer, ChangeListener {
	private static final long serialVersionUID = 1L;

	ArrayList<Mesh> meshes;

	SceneViewPanel sceneViewPanel;
	ToleranceSliderPanel sliderPanel;
	JComboBox shapeComboBox;

	boolean sliderChanged = true;
	boolean showFourView = true;
	boolean showOneView = false;
	
	// GL resources
	SceneProgram flatColorProgram;
	SceneProgram diffuseProgram;
	boolean shadersInitialized = false;
	boolean meshesInitialized = false;
	
	Vector3f [] lightPositions = new Vector3f [SceneProgram.NUM_LIGHTS];
	Vector3f [] lightIntensities = new Vector3f [SceneProgram.NUM_LIGHTS];
	Vector3f lightAmbientIntensity;
	
	DiffuseMaterial material;

	public SceneP2() {
		super("CS 4620 Scene Assignment / Problem 2");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
            	terminate();
            }
        });

		sceneViewPanel = new SceneViewPanel(this);
		getContentPane().add(sceneViewPanel, BorderLayout.CENTER);

		sliderPanel = new ToleranceSliderPanel(this);
		getContentPane().add(sliderPanel, BorderLayout.EAST);

		shapeComboBox = new JComboBox();
		getContentPane().add(shapeComboBox, BorderLayout.NORTH);

		initMaterial();
		initLights();
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
		new SceneP2().run();
	}
	
	private void initMaterial()
	{
		material = new DiffuseMaterial();
		//material.setAmbient(0.05f, 0.05f, 0.05f);
		material.setAmbient(0.0f, 0.0f, 0.0f);
		material.setDiffuse(1.0f, 0.0f, 0.0f);
	}
	
	private void initLights() {
		// by default, use ambient color fully
		lightAmbientIntensity = new Vector3f(1.0f, 1.0f, 1.0f);
		
		// default all to be "off"
		for(int i = 0; i < SceneProgram.NUM_LIGHTS; i++)
		{
			lightPositions[i] = new Vector3f(0.0f, 0.0f, 0.0f);
			lightIntensities[i] = new Vector3f(0.0f, 0.0f, 0.0f);
		}
		
		// set a few to be interesting
		lightPositions[0] = new Vector3f(5.0f, 5.0f, 5.0f);
		lightIntensities[0] = new Vector3f(1.0f, 1.0f, 1.0f);
		
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
		lightIntensities[1] = new Vector3f(high, high, high);
		lightIntensities[2] = new Vector3f(med, med, med);
		lightIntensities[3] = new Vector3f(med, med, med);
		lightIntensities[4] = new Vector3f(low, low, low);
		lightIntensities[5] = new Vector3f(low, low, low);
		lightIntensities[6] = new Vector3f(low, low, low);
	}

	@SuppressWarnings("unchecked")
	private void initMeshes(GL2 gl)
	{
		// build a few meshes
		if(meshesInitialized)
			return;
		
		meshes = new ArrayList<Mesh>();
		meshes.add(new Sphere(gl));
		meshes.add(new Cylinder(gl));
		meshes.add(new Torus(gl));
		updateMeshTolerance(gl);

		shapeComboBox.addItem("Sphere");
		shapeComboBox.addItem("Cylinder");
		shapeComboBox.addItem("Torus");
		
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
		program.setLightAmbientIntensity(gl, lightAmbientIntensity);
		
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
		
		program.setLightIntensities(gl, lightIntensities);
		program.setLightPositions(gl, transformedPositions);
	}

	public void draw(GLAutoDrawable drawable, CameraController cameraController) {
		// we don't need to clear the screen at the beginning or swap buffers at the end of this function;
		// caller takes care of this
		final GL2 gl = drawable.getGL().getGL2();
		
		// update meshes if tolerance slider has been changed
		updateMeshTolerance(gl);

		int meshIndex = shapeComboBox.getSelectedIndex();
		
		SceneProgram programToUse = flatColorProgram;
		if(sceneViewPanel.isLightingMode())
		{
			programToUse = diffuseProgram;
		}
		
		Program.use(gl, programToUse);
		
		// set transforms
		programToUse.setProjection(gl, cameraController.getProjection());
		programToUse.setModelView(gl, cameraController.getView());
		
		// set material
		//programToUse.setMaterial(gl, material);
		
		// set lighting
		setLights(gl, programToUse, cameraController.getView());

		if (sceneViewPanel.isWireframeMode())
		{
			material.drawUsingProgram(gl, programToUse, meshes.get(meshIndex), true);
		}
		else
		{
			material.drawUsingProgram(gl, programToUse, meshes.get(meshIndex), false);
		}
		Program.unuse(gl);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == sliderPanel.getSlider())
		{
			sliderChanged = true;
		}
	}

	protected void updateMeshTolerance(GL2 gl)
	{
		if(sliderChanged)
		{
			float tolerance = sliderPanel.getTolerance();
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
}
