package cs4620.problem;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.IndexBuffer;
import cs4620.framework.Program;
import cs4620.framework.Texture2D;
import cs4620.framework.Texture2DMipmapped;
import cs4620.framework.TextureUnit;
import cs4620.framework.Transforms;
import cs4620.framework.TwoDimColorProgram;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;
import cs4620.scene.SceneProgram;


public class Shaders2P4 implements GLEventListener, KeyListener {
	
	// GL resources
	private boolean initialized = false;
	private VertexArray planeArray;
	
	private SceneProgram program;
	
	private Texture2DMipmapped mTexture;
	private Texture2D texture;
	
	private GLCanvas canvas;
	private JFrame frame;
	
	private boolean useMipmap = false;
	//private float dist = 5f;
	
	public Shaders2P4() {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		
		frame = new JFrame("CS 4620/5620 Shaders 2 P4: Mipmapping");
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(512, 512);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {	
		Shaders2P4 app = new Shaders2P4();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		if(initialized)
			return;
		
		GL2 gl = drawable.getGL().getGL2();
		
		// define vertex positions
		float [] vertexPositions = {
				100f, 0f, 100f,      // vertex 0
				-100f, 0f, 100f,      // vertex 1
				-100f, 0f, -100f,      // vertex 2
				100f,  0f, -100f       // vertex 3
		};
		
		VertexBuffer vertexBuffer = new VertexBuffer(gl, vertexPositions, 3);
		
		float [] vertexNormals = {
				0f, 1f, 0f,      // vertex 0
				0f, 1f, 0f,      // vertex 1
				0f, 1f, 0f,      // vertex 2
				0f, 1f, 0f,      // vertex 3
		};
		
		VertexBuffer normalBuffer = new VertexBuffer(gl, vertexNormals, 3);
		
		float [] texCoords = {
				10f, 10f,    // vertex 0
				0f, 10f,     // vertex 1
				0f, 0f,      // vertex 2
				10f, 0f      // vertex 3
		};
		
		VertexBuffer texBuffer = new VertexBuffer(gl, texCoords, 2);
		
		int [] trianglesIndices = {
			0, 1, 3,
			1, 2, 3
		};
		
		IndexBuffer trianglesBuffer = new IndexBuffer(gl, trianglesIndices);
		
		// assemble vertex positions and indices into vertex arrays
		planeArray = new VertexArray(gl, GL2.GL_TRIANGLES);
		planeArray.setIndexBuffer(gl, trianglesBuffer);
		planeArray.setAttributeBuffer(gl, SceneProgram.NORMAL_INDEX, normalBuffer);
		planeArray.setAttributeBuffer(gl, SceneProgram.VERTEX_INDEX, vertexBuffer);
		planeArray.setAttributeBuffer(gl, SceneProgram.TEXTURE_INDEX, texBuffer);
		
		// create shader program for drawing
		try {
			program = new SceneProgram(gl, "textureShader.vs", "textureShader.fs");
		} catch (GlslException e) {
			System.err.println("FAIL: creating program");
			e.printStackTrace();
			System.exit(1);
		}
		
		// initialize transforms to identity matrix
		program.setProjection(gl, Transforms.identity3DH());
		program.setModelView(gl, Transforms.lookAt3DH(new Point3f(20,  5,  0),
				  new Point3f(0, 0, 0),
				  new Vector3f(0, 1, 0)));
		
		program.setAmbientColor(gl, new Vector3f(0.3f, 0.3f, 0.3f));
		program.setDiffuseColor(gl, new Vector3f(0.5f, 0.5f, 0.5f));
		
		Vector3f[] lightPositions = new Vector3f[16];
		for(int i = 0; i < 16; ++i)
		{
			lightPositions[i] = new Vector3f(0f, 0f, 0f);
		}
		
		Vector3f[] lightIntensities = new Vector3f[16];
		for(int i = 0; i < 16; ++i)
		{
			lightIntensities[i] = new Vector3f(0f, 0f, 0f);
		}
		
		lightPositions[0] = new Vector3f(0f, 10f, 0f);
		lightIntensities[0] = new Vector3f(1f, 1f, 1f);
		
		program.setLightIntensities(gl, lightIntensities);
		program.setLightPositions(gl, lightPositions);
		program.setLightAmbientIntensity(gl, new Vector3f(0.75f, 0.75f, 0.75f));

		try {
			mTexture = new Texture2DMipmapped(gl, "data/textures/checkerboard.gif");
			texture = new Texture2D(gl, "data/textures/checkerboard.gif");
		}
		catch (IOException e) {
			System.out.print("Cannot load texture: ");
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		gl.glEnable(GL2.GL_FRAMEBUFFER_SRGB);
		
		// Set depth buffer.
//		gl.glClearDepth(1.0f);
//		gl.glDepthFunc(GL2.GL_LESS);
//		gl.glEnable(GL2.GL_DEPTH_TEST);

		// Set blending mode.
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL2.GL_BLEND);

		// Forces OpenGL to normalize transformed normals to be of
		// unit length before using the normals in OpenGL's lighting equations.
		gl.glEnable(GL2.GL_NORMALIZE);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		Program.use(gl, program);
		
		if (program.getUniform("un_Texture") != null)
		{
			if (useMipmap)
			{
				mTexture.use();
				//texture.useB4();
			}
			else
			{
				texture.useB4();
			}
			TextureUnit.getActiveTextureUnit(gl).bindToUniform(gl, program.getUniform("un_Texture"));
		}
		
		planeArray.draw(gl);
		
		if (useMipmap)
			//mTexture.unuse();
			texture.unuse();
		else
			texture.unuse();
		
		Program.unuse(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, 
			int x, int y, int width, int height) {
		final GL2 gl = drawable.getGL().getGL2();
		program.setProjection(gl, Transforms.perspective3DH(45.0f,
														    (float)((double) width / (double) height),
														    0.1f,
														    1000f));
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// NOP		
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void keyPressed(KeyEvent e) {
//		if (e.getKeyCode() == KeyEvent.VK_UP && dist > 1f)
//			dist-=0.1;
//		if (e.getKeyCode() == KeyEvent.VK_DOWN && dist < 21f)
//			dist+=0.1;
	}
	
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			useMipmap = !useMipmap;
			canvas.display();
		}
	}
}
