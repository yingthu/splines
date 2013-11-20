package cs4620.demos;

import java.awt.BorderLayout;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.IndexBuffer;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;
import cs4620.scene.SceneProgram;
import cs4620.demos.TwistingProgram;


public class TwistingDemo implements GLEventListener {
	
	// GL resources
	boolean initialized = false;
	VertexArray quadArray;
	
	TwistingProgram program;
	
	public TwistingDemo() {		
		// NOP
	}
	
	public static void main(String[] args) {	
		TwistingDemo app = new TwistingDemo();
		
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(app);
		
		JFrame frame = new JFrame("CS 4620 Demo: Twisting!");
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
		int count = 200; // Number of vertices in grid in each dimension
	    float size = 1.0f / (count-1);
		float [] vertexPositions = new float[count*count*3];
	    
	    for(int i=0;i<count;i++)
	    {
	    	for(int j=0;j<count;j++)	    
	    	{
	    		float x = -0.5f + j * size;
	    		float y = -0.5f + i * size;
	    		
	    		vertexPositions[i * count * 3 + j*3] = x;
	    		vertexPositions[i * count * 3 + j*3 + 1] = y;
	    		vertexPositions[i * count * 3 + j*3 + 2] = 0;
	    	}
	    }
		
		VertexBuffer vertexBuffer = new VertexBuffer(gl, vertexPositions, 3);
		
		int nBoxes = count - 1;
		
		int [] boxIndices = new int[nBoxes * nBoxes * 4];
		
		for (int i = 0; i < nBoxes; ++i)
		{
			for(int j = 0; j < nBoxes; ++j)
			{
				boxIndices[4 * (i * nBoxes + j)] = i * count + j;
				boxIndices[4 * (i * nBoxes + j) + 1] = i * count + j + 1;
				boxIndices[4 * (i * nBoxes + j) + 2] = (i + 1) * count + j + 1;
				boxIndices[4 * (i * nBoxes + j) + 3] = (i + 1) * count + j;
			}
		}
		
		IndexBuffer quadBuffer = new IndexBuffer(gl, boxIndices);
		
		// assemble vertex positions and indices into vertex arrays
		quadArray = new VertexArray(gl, GL2.GL_QUADS);
		quadArray.setIndexBuffer(gl, quadBuffer);
		quadArray.setAttributeBuffer(gl, SceneProgram.VERTEX_INDEX, vertexBuffer);
		
		// create shader program for drawing
		try {
			program = new TwistingProgram(gl, "twisting.vs", "twisting.fs");
		} catch (GlslException e) {
			System.err.println("FAIL: creating program");
			e.printStackTrace();
			System.exit(1);
		}
		
		// initialize transforms to identity matrix
		program.setProjection(gl, Transforms.ortho3DH(-1f, 1f, -1f, 1f, .5f, -.5f));
		program.setModelView(gl, Transforms.identity3DH());
		program.setDiffuseColor(gl, new Vector3f(0.8f, 0.2f, 0.2f));
		program.setTwist(gl, 5.0f);
	}	

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		Program.use(gl, program);
		
		quadArray.draw(gl);
		
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
