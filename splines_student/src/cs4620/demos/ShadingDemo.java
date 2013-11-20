package cs4620.demos;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import cs4620.framework.CameraController;
import cs4620.framework.GLSceneDrawer;
import cs4620.framework.GlslException;
import cs4620.framework.Program;
import cs4620.scene.SceneProgram;
import cs4620.shape.Mesh;
import cs4620.shape.Teapot;
import cs4620.ui.SceneViewPanel;
import cs4620.ui.ToleranceSliderPanel;

@SuppressWarnings("rawtypes")
public class ShadingDemo extends JFrame implements GLSceneDrawer, ChangeListener {
	private static final long serialVersionUID = 1L;

	SceneViewPanel sceneViewPanel;
	ToleranceSliderPanel sliderPanel;
	JComboBox shaderComboBox;

	boolean sliderChanged = true; // force initial building of meshes
	boolean showFourView = true;
	boolean showOneView = false;
	
	// GL resources
	SceneProgram flatColorProgram;
	SceneProgram diffuseProgram;
	boolean shadersInitialized = false;
	boolean meshesInitialized = false;
	
	Mesh teapotMesh;
	ArrayList<SceneProgram> programs;
	
	public ShadingDemo() {
		super("CS 4620 Demo: Shading");
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

		shaderComboBox = new JComboBox();
		getContentPane().add(shaderComboBox, BorderLayout.NORTH);
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
		new ShadingDemo().run();
	}
	
	@SuppressWarnings("unchecked")
	private void initMeshes(GL2 gl)
	{
		if(meshesInitialized)
			return;
		
		try {
			teapotMesh = new Teapot(gl);
		} catch (Exception e) {
			System.err.println("FAIL: creating teapot");
			e.printStackTrace();
		}
		
		meshesInitialized = true;
	}

	public void init(GLAutoDrawable drawable, CameraController cameraController) {
		final GL2 gl = drawable.getGL().getGL2();
		
		initShaders(gl);
		initMeshes(gl);
		
		gl.glEnable(GL2.GL_FRAMEBUFFER_SRGB);
		
		sceneViewPanel.setBackgroundColor(new Vector3f(0.3f, 0.3f, 0.3f));

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

		sceneViewPanel.startAnimation();
	}
	
	protected void initShaders(GL2 gl) {
		if(shadersInitialized)
			return;
		
		try {
			programs = new ArrayList<SceneProgram>();
			programs.add(new SceneProgram(gl, "flatcolor.vs", "flatcolor.fs"));
			programs.add(new SceneProgram(gl, "demo.vs", "demo.fs"));
			programs.add(new SceneProgram(gl, "lambertianVS.vs", "lambertianVS.fs"));
			programs.add(new SceneProgram(gl, "lambertianFS.vs", "lambertianFS.fs"));
			programs.add(new SceneProgram(gl, "phongVS.vs", "phongVS.fs"));
			programs.add(new SceneProgram(gl, "phongFS.vs", "phongFS.fs"));
			
			shaderComboBox.addItem("Flat Color");
			shaderComboBox.addItem("Demo Shader");
			shaderComboBox.addItem("Lambertian Vertex");
			shaderComboBox.addItem("Lambertian Fragment");
			shaderComboBox.addItem("Phong Vertex");
			shaderComboBox.addItem("Phong Fragment");
		} catch (GlslException e) {
			System.err.println("FAIL: making shader programs");
			e.printStackTrace();
			System.exit(1);
		}
		
		shadersInitialized = true;
	}
	
	protected SceneProgram getSelectedProgram()
	{
		int programIndex = shaderComboBox.getSelectedIndex();
		if (programIndex < 0)
			return null;
		else
			return programs.get(programIndex);
	}

	public void drawMesh(GL2 gl, Mesh shape)
	{
		if(sceneViewPanel.isWireframeMode())
		{
			shape.drawWireframe(gl);
		}
		else
		{
			shape.draw(gl);
		}
	}
	
	public void draw(GL2 gl, SceneProgram selectedProgram, Matrix4f view)
	{
		// selectedProgram is program chosen in combo box
		Program.use(gl, selectedProgram);

		selectedProgram.setModelView(gl, view);
		
		Vector3f ambientColor = new Vector3f(0.02f, 0.02f, 0.02f);
		Vector3f diffuseColor = new Vector3f(0.2f, 0.4f, 0.8f);
		Vector3f specularColor = new Vector3f(1.0f, 1.0f, 1.0f);
		float shininess = 50.0f;
		
		selectedProgram.setAmbientColor(gl, ambientColor);
		selectedProgram.setDiffuseColor(gl, diffuseColor);
		selectedProgram.setSpecularColor(gl, specularColor);
		selectedProgram.setShininess(gl, shininess);

		drawMesh(gl, teapotMesh);
		
		Program.unuse(gl);
	}
	
	public void draw(GLAutoDrawable drawable, CameraController cameraController) {
		// we don't need to clear the screen at the beginning or swap buffers at the end of this function;
		// caller takes care of this
		final GL2 gl = drawable.getGL().getGL2();
		
		// update meshes if tolerance slider has been changed
		updateMeshTolerance(gl);
		
		SceneProgram selectedProgram = getSelectedProgram();
		
		// set projection out here
		selectedProgram.setProjection(gl, cameraController.getProjection());
		
		draw(gl, selectedProgram, cameraController.getView());
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
			teapotMesh.buildMesh(gl, tolerance);
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
