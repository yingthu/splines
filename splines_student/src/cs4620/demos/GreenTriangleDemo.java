package cs4620.demos;

import java.awt.BorderLayout;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import cs4620.framework.GlslException;
import cs4620.framework.IndexBuffer;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;
import cs4620.scene.SceneProgram;


public class GreenTriangleDemo implements GLEventListener {
	
	// GL resources
	boolean initialized = false;
	VertexArray triangleArray;
	
	SceneProgram program;
	
	public GreenTriangleDemo() {		
		// NOP
	}
	
	public static void main(String[] args) {	
		GreenTriangleDemo app = new GreenTriangleDemo();
		
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(app);
		
		JFrame frame = new JFrame("CS 4620 Demo: Green Triangle!");
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(512, 512);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		if(initialized)
			return;
		
		GL2 gl = drawable.getGL().getGL2();
		
		// define vertex positions
		float [] vertexPositions = {
				-0.5f, -0.5f, 0f,    // vertex 0
				 0.5f, -0.5f, 0f,    // vertex 1
				   0f,  0.5f, 0f,    // vertex 2
		};
		
		VertexBuffer vertexBuffer = new VertexBuffer(gl, vertexPositions, 3);
		
		int [] trianglesIndices = {
			0, 1, 2
		};
		
		IndexBuffer trianglesBuffer = new IndexBuffer(gl, trianglesIndices);
		
		// assemble vertex positions and indices into vertex arrays
		triangleArray = new VertexArray(gl, GL2.GL_TRIANGLES);
		triangleArray.setIndexBuffer(gl, trianglesBuffer);
		triangleArray.setAttributeBuffer(gl, SceneProgram.VERTEX_INDEX, vertexBuffer);
		
		// create shader program for drawing
		try {
			program = new SceneProgram(gl, "greenShader.vs", "greenShader.fs");
		} catch (GlslException e) {
			System.err.println("FAIL: creating program");
			e.printStackTrace();
			System.exit(1);
		}
		
		// initialize transforms to identity matrix
		program.setProjection(gl, Transforms.ortho3DH(-1f, 1f, -1f, 1f, .5f, -.5f));
		program.setModelView(gl, Transforms.identity3DH());
	}	

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		Program.use(gl, program);
		
		triangleArray.draw(gl);
		
		Program.unuse(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawble, 
			int x, int y, int width, int height) {
		// NOP
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// NOP		
	}
}
