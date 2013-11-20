package cs4620.demos;

import java.awt.BorderLayout;
import java.io.File;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4f;

import cs4620.framework.GlslException;
import cs4620.framework.IndexBuffer;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;
import cs4620.scene.SceneProgram;
import cs4620.shape.CustomTriangleMesh;


public class PositionAsColorDemo implements GLEventListener {
	
	// GL resources
	boolean initialized = false;
	CustomTriangleMesh mesh;
	
	SceneProgram program;
	
	public PositionAsColorDemo() {		
		// NOP
	}
	
	public static void main(String[] args) {	
		PositionAsColorDemo app = new PositionAsColorDemo();
		
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(app);
		
		JFrame frame = new JFrame("CS 4620 Demo: Position As Color!");
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
		
		// Load teapot mesh
		try {
			mesh = new CustomTriangleMesh(gl, new File("data/meshes/teapot2.msh") );
		} catch (Exception e) {
			System.err.println("FAIL: loading teapot");
			e.printStackTrace();
			System.exit(1);
		}
		
		// create shader program for drawing
		try {
			program = new SceneProgram(gl, "positionAsColor.vs", "positionAsColor.fs");
		} catch (GlslException e) {
			System.err.println("FAIL: creating program");
			e.printStackTrace();
			System.exit(1);
		}
		
		// initialize transforms to identity matrix
		program.setProjection(gl, Transforms.ortho3DH(-1f, 1f, -1f, 1f, .5f, -.5f));
		program.setModelView(gl, Transforms.identity3DH());
		
		program.setProjection(gl, Transforms.perspective3DH(45f, 1f, 0.1f, 100f));
		
		Matrix4f trans = Transforms.lookAt3DH(new Point3f(0f, 0f, 5f), new Point3f(0f, 0f, 0f), new Vector3f(0f, 1f, 0f));
		trans.mul(Transforms.rotateAxis3DH(0, 30));
		trans.mul(Transforms.rotateAxis3DH(1, -30));
		trans.mul(Transforms.rotateAxis3DH(2, -30));
		program.setModelView(gl, trans);
	    
	}	

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		Program.use(gl, program);
		
		mesh.draw(gl);
		
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
