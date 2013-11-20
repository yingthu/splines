package cs4620.demos;

import java.awt.BorderLayout;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;
import javax.vecmath.Matrix3f;

import cs4620.framework.GlslException;
import cs4620.framework.IndexBuffer;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.framework.TwoDimProgram;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;


public class Sierpinski implements GLEventListener {
	
	// GL resources
	boolean initialized = false;
	VertexArray triangleArray;
	VertexArray triangleLinesArray;
	
	TwoDimProgram program;
	
	Matrix3f shiftLeftTransform;
	Matrix3f shiftRightTransform;
	
	public Sierpinski() {
		// NOP
	}
	
	public static void main(String[] args) {	
		Sierpinski app = new Sierpinski();
		
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(app);
		
		JFrame frame = new JFrame("CS 4620 Demo: Sierpinski Triangle");
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(512, 512);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	float sqrt(float val)
	{
		return (float) Math.sqrt(val);
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		if(initialized)
			return;
		
		GL2 gl = drawable.getGL().getGL2();
		
		// define vertex positions
		float [] vertexPositions = {
				 0.0f,  1.0f / sqrt(3.0f), // vertex 0
				-0.5f, -0.5f / sqrt(3.0f), // vertex 1
				 0.5f, -0.5f / sqrt(3.0f)  // vertex 2
		};
		
		VertexBuffer vertexBuffer = new VertexBuffer(gl, vertexPositions, 2);
		
		// organize vertices into primitives
		int [] linesIndices = {
			0, 1,
			1, 2,
			2, 0
		};
		
		IndexBuffer linesBuffer = new IndexBuffer(gl, linesIndices);
		
		int [] trianglesIndices = {
			0, 1, 2
		};
		
		IndexBuffer trianglesBuffer = new IndexBuffer(gl, trianglesIndices);
		
		// assemble vertex positions and indices into vertex arrays
		triangleArray = new VertexArray(gl, GL2.GL_TRIANGLES);
		triangleArray.setIndexBuffer(gl, trianglesBuffer);
		triangleArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, vertexBuffer);
		
		triangleLinesArray = new VertexArray(gl, GL2.GL_LINES);
		triangleLinesArray.setIndexBuffer(gl, linesBuffer);
		triangleLinesArray.setAttributeBuffer(gl, TwoDimProgram.VERTEX_INDEX, vertexBuffer);
		
		// create shader program for drawing
		try {
			program = new TwoDimProgram(gl, "flatcolor2d.vs", "flatcolor2d.fs");
		} catch (GlslException e) {
			System.err.println("FAIL: creating program");
			e.printStackTrace();
			System.exit(1);
		}
		
		// create transforms for two-box
		Matrix3f scale = Transforms.scale2DH(0.5f);
		
		shiftLeftTransform = Transforms.translate2DH(-0.25f - 0.5f/3, 0.0f);
		shiftLeftTransform.mul(scale);
		
		shiftRightTransform = Transforms.translate2DH(0.25f + 0.5f/3, 0.0f);
		shiftRightTransform.mul(scale);
	}
	
	void triangle(GL2 gl, Matrix3f modelView)
	{
		// update program's transform and draw
		program.setModelView(gl, modelView);
		triangleLinesArray.draw(gl);
	}
	
	void sierpinski(GL2 gl, Matrix3f modelView, int k)
	{
		if (k == 0)
			triangle(gl, modelView);
		else
		{
			Matrix3f next;
			
			// make next = modelView * (transform to top corner)
			next = new Matrix3f(modelView);
			next.mul(Transforms.translate2DH(0.0f, 0.5f / sqrt(3.0f)));
			next.mul(Transforms.scale2DH(0.5f));
			sierpinski(gl, next, k-1);

			// make next = modelView * (transform to bottom right corner)
			next = new Matrix3f(modelView);
			next.mul(Transforms.translate2DH(0.25f, -0.25f / sqrt(3.0f)));
			next.mul(Transforms.scale2DH(0.5f));
			sierpinski(gl, next, k-1);

			// make next = modelView * (transform to bottom left corner)
			next = new Matrix3f(modelView);
			next.mul(Transforms.translate2DH(-0.25f, -0.25f / sqrt(3.0f)));
			next.mul(Transforms.scale2DH(0.5f));
			sierpinski(gl, next, k-1);
		}
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		Program.use(gl, program);
		
		program.setProjection(gl, Transforms.ortho2DH(-1.0f, 1.0f, -1.0f, 1.0f));
		program.setColor(gl, 1.0f, 1.0f, 1.0f);
		
		sierpinski(gl, Transforms.identity2DH(), 8);
		
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
