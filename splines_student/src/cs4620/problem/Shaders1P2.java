package cs4620.problem;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4620.framework.CameraController;
import cs4620.framework.GLSceneDrawer;
import cs4620.framework.Transforms;
import cs4620.material.DiffuseMaterial;
import cs4620.material.FlowerMaterial;
import cs4620.material.PhongMaterial;
import cs4620.shape.Sphere;
import cs4620.scene.MeshNode;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneProgram;
import cs4620.shape.Cube;
import cs4620.shape.CustomTriangleMesh;
import cs4620.shape.Mesh;
import cs4620.ui.SceneViewPanel;
import cs4620.ui.ToleranceSliderPanel;

@SuppressWarnings("rawtypes")
public class Shaders1P2 extends JFrame implements GLSceneDrawer, ChangeListener {
	private static final long serialVersionUID = 1L;

	ArrayList<Mesh> meshes;

	SceneViewPanel sceneViewPanel;
	ToleranceSliderPanel sliderPanel;

	boolean sliderChanged = true; // force initial building of meshes
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
	
	FlowerMaterial flowerGreenMaterial;
	FlowerMaterial flowerCenterMaterial;
	FlowerMaterial flowerPetalsMaterial;
	
	DiffuseMaterial groundMaterial;
	
	PhongMaterial lightMaterial;
	MeshNode flowerGreenNode;
	MeshNode flowerCenterNode;
	MeshNode flowerPetalsNode;
	MeshNode lightSourceNode;
	MeshNode groundNode;


	public Shaders1P2() {
		super("CS 4620 Shaders Assignment 1 / Problem 2");
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
		new Shaders1P2().run();
	}
	
	private void initMaterial()
	{	
		flowerGreenMaterial = new FlowerMaterial();
		flowerGreenMaterial.setAmbient(0.1f, 0.1f, 0.1f);
		flowerGreenMaterial.setDiffuse(0.1f, 0.6f, 0.0f);
		flowerGreenMaterial.setSpecular(0.1f, 0.2f, 0.05f);
		flowerGreenMaterial.setShininess(10);
		
		flowerCenterMaterial = new FlowerMaterial();
		flowerCenterMaterial.setAmbient(0.05f, 0.05f, 0.05f);
		flowerCenterMaterial.setDiffuse(0.4f, 0.2f, 0.1f);
		flowerCenterMaterial.setSpecular(0.0f, 0.0f, 0.0f);
		flowerCenterMaterial.setShininess(10);
		
		flowerPetalsMaterial = new FlowerMaterial();
		flowerPetalsMaterial.setAmbient(0.05f, 0.05f, 0.05f);
		flowerPetalsMaterial.setDiffuse(0.7f, 0.7f, 0.05f);
		flowerPetalsMaterial.setSpecular(0.3f, 0.2f, 0.005f);
		flowerPetalsMaterial.setShininess(10);
		
		lightMaterial = new PhongMaterial();
		lightMaterial.setAmbient(1f, 1f, 1f);
		lightMaterial.setDiffuse(0,0,0);
		lightMaterial.setSpecular(0,0,0);
		
		groundMaterial = new DiffuseMaterial();
		groundMaterial.setAmbient(0f, 0f, 0f);
		groundMaterial.setDiffuse(0.02f, 0.02f, 0.02f);
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
		lightPositions[0] = new Vector3f(5.0f, 5.0f, 0f);
		lightIntensities[0] = new Vector3f(1.0f, 1.0f, 1.0f);
	}

	@SuppressWarnings("unchecked")
	private void initMeshes(GL2 gl)
	{
		if(meshesInitialized)
			return;
		
		
		
		// create flower components and light source ball
		try{
			flowerGreenNode = new MeshNode("Flower Green", new CustomTriangleMesh(gl, new File("data/meshes/flower_green.msh")), flowerGreenMaterial);
			flowerCenterNode = new MeshNode("Flower Center", new CustomTriangleMesh(gl, new File("data/meshes/flower_center.msh")), flowerCenterMaterial);
			flowerPetalsNode = new MeshNode("Flower Petals", new CustomTriangleMesh(gl, new File("data/meshes/flower_petals.msh")), flowerPetalsMaterial);
			
			groundNode = new MeshNode("Ground", new Cube(gl), groundMaterial);
			groundNode.setScaling(5.0f, 0.1f, 5.0f);
			groundNode.setTranslation(0.0f, -.05f, 0.0f);
			groundNode.getMesh().buildMesh(gl, 0.133f);
			
			lightSourceNode = new MeshNode("Light Ball", new Sphere(gl), lightMaterial);
			lightSourceNode.getMesh().buildMesh(gl, 0.133f);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		meshesInitialized = true;
	}

	public void init(GLAutoDrawable drawable, CameraController cameraController) {
		final GL2 gl = drawable.getGL().getGL2();
		
		initShaders(gl);
		initMeshes(gl);
		
		setLightFromSlider();

		gl.glEnable(GL2.GL_FRAMEBUFFER_SRGB);
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		sceneViewPanel.setBackgroundColor(new Vector3f(0.5f, 0.5f, 0.5f));

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
		
		shadersInitialized = true;
	}
	
	private void setLightsInfo(GL2 gl, ProgramInfo info, Matrix4f worldToCamera)
	{
		info.un_LightAmbientIntensity = lightAmbientIntensity;
		
		// Only using one light in this problem
		
		// Transform lights to eye space and set them as uniforms for program.
		// (Lighting will be done on eye-space geometry.)
		
		Vector4f positionH = new Vector4f(lightPositions[0].x, lightPositions[0].y, lightPositions[0].z, 1.0f);
		worldToCamera.transform(positionH);
		Vector3f transformedPosition = new Vector3f(positionH.x, positionH.y, positionH.z);
		
		info.un_LightIntensities = lightIntensities;
		info.un_LightPositions = new Vector3f[16];
		for (int i = 0; i < 16; ++i)
		{
			info.un_LightPositions[i] = new Vector3f(0f, 0f, 0f);
		}
		
		info.un_LightPositions[0] = transformedPosition;
	}
	
	protected ProgramInfo constructProgramInfo(GL2 gl, CameraController cameraController)
	{
		ProgramInfo info = new ProgramInfo();
		
		info.un_Projection = cameraController.getProjection();
		info.un_ModelView = cameraController.getView();
		setLightsInfo(gl, info, cameraController.getView());
		return info;
	}
	
	public Matrix4f getModelMatrix(MeshNode node)
	{
		Matrix4f out = Transforms.identity3DH();
		
		out.mul(Transforms.translate3DH(node.translation.x,
				node.translation.y,
				node.translation.z));
		out.mul(Transforms.rotateAxis3DH(2, node.rotation.z));
		out.mul(Transforms.rotateAxis3DH(1, node.rotation.y));
		out.mul(Transforms.rotateAxis3DH(0, node.rotation.x));
		out.mul(Transforms.scale3DH(node.scaling.x,
				node.scaling.y,
				node.scaling.z));
		
		return out;
	}
	
	public void draw(GLAutoDrawable drawable, CameraController cameraController) {
		// we don't need to clear the screen at the beginning or swap buffers at the end of this function;
		// caller takes care of this
		final GL2 gl = drawable.getGL().getGL2();

		ProgramInfo info = constructProgramInfo(gl, cameraController);
		ProgramInfo lightInfo = constructProgramInfo(gl, cameraController);
		lightInfo.un_ModelView.mul(info.un_ModelView, Transforms.translate3DH(lightPositions[0]));

		if (sceneViewPanel.isWireframeMode())
		{
			lightSourceNode.drawWireframe(gl, lightInfo);
			flowerGreenNode.drawWireframe(gl, info);
			flowerCenterNode.drawWireframe(gl, info);
			flowerPetalsNode.drawWireframe(gl, info);
			
			info.un_ModelView.mul(getModelMatrix(groundNode));
			groundNode.drawWireframe(gl, info);
		}
		else
		{
			lightSourceNode.draw(gl, lightInfo);
			flowerGreenNode.draw(gl, info);
			flowerCenterNode.draw(gl, info);
			flowerPetalsNode.draw(gl, info);
			
			info.un_ModelView.mul(getModelMatrix(groundNode));
			groundNode.draw(gl, info);
		}
	}
	
	public void setLightPosition(float x, float y, float z)
	{
		lightPositions[0].x = x;
		lightPositions[0].y = y;
		lightPositions[0].z = z;
		
		flowerGreenMaterial.setUniforms(lightPositions[0].x, lightPositions[0].y, lightPositions[0].z);
		flowerCenterMaterial.setUniforms(lightPositions[0].x, lightPositions[0].y, lightPositions[0].z);
		flowerPetalsMaterial.setUniforms(lightPositions[0].x, lightPositions[0].y, lightPositions[0].z);
	}
	
	private void setLightFromSlider()
	{
		// move the light source around in a sinusoidal pattern
		float coeff = sliderPanel.getTolerance();//sliderPanel.getRawValue();
		setLightPosition(10 * (float) Math.sin(10 * Math.PI * coeff), 7 + 2 * (float) Math.cos(5 * Math.PI * coeff), 6 * (float) Math.cos(5 * Math.PI * coeff));
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == sliderPanel.getSlider())
		{
			//sliderChanged = true;
			setLightFromSlider();
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
