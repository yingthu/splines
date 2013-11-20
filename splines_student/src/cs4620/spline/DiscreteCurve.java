package cs4620.spline;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.IndexBuffer;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneProgram;
import cs4620.shape.Mesh;

/*
 * The main base class for curves. Holds and renders a set of vertices and normals.
 */
public abstract class DiscreteCurve {
	// For communicating with the outside world.
	public abstract boolean isClosed();
	public abstract void setClosed(boolean value);
	public abstract ArrayList<Vector2f> getCtrlPts();
    public abstract void build(GL2 gl, float[] controlPoints, float epsilon);
    
    // Needed for loading/saving of curves alone and in larger files.
    public abstract Object getYamlObjectRepresentation();
    public abstract void fromYamlObject(Object yamlObject);
    public abstract void load(String filename) throws IOException;
    public abstract void save(String filename) throws IOException;
    
    // GL resources
 	protected VertexArray lineArray;
 	protected VertexArray normalArray;
 	
 	protected VertexBuffer verticesBuffer;
 	protected VertexBuffer normalsBuffer; // Stores the regular normal attributes
 	protected VertexBuffer normalBuffer; // Used to draw the normals in the spline editor
 	
 	protected IndexBuffer linesIndicesBuffer;
 	protected IndexBuffer normalsIndicesBuffer;
 	
 	protected SceneProgram flatColorProgram = null;
 	
 	protected float canvasSize = 0f;
 	
 	public DiscreteCurve(GL2 gl, float canvasSize) {
 		this.canvasSize = canvasSize;

		// initialize buffers to empty -- will get filled by setVertices/setNormals/etc.
		verticesBuffer = new VertexBuffer(gl, new float [0], 3);
		normalsBuffer = new VertexBuffer(gl, new float [0], 3);
		normalBuffer = new VertexBuffer(gl, new float [0], 3);

		linesIndicesBuffer = new IndexBuffer(gl, new int [0]);
		normalsIndicesBuffer = new IndexBuffer(gl, new int [0]);
		
		// Discrete curves are only drawn in "wireframe mode"
		lineArray = new VertexArray(gl, GL2.GL_LINE_STRIP);
		lineArray.setAttributeBuffer(gl, SceneProgram.VERTEX_INDEX, verticesBuffer);
		lineArray.setAttributeBuffer(gl, SceneProgram.NORMAL_INDEX, normalsBuffer);
		lineArray.setIndexBuffer(gl, linesIndicesBuffer);
		
		normalArray = new VertexArray(gl, GL2.GL_LINES);
		normalArray.setAttributeBuffer(gl, SceneProgram.VERTEX_INDEX, normalBuffer);
		normalArray.setIndexBuffer(gl, normalsIndicesBuffer);
		
		try {
			flatColorProgram = new SceneProgram(gl, "flatcolor.vs", "flatcolor.fs");
		} catch (GlslException e) {
			e.printStackTrace();
			System.exit(1);
		}
 	}
 	
 	public void setCanvasSize(float canvasSize)
 	{
 		this.canvasSize = canvasSize;
 	}

 	/*
 	 * Renders the curve with an offset, for drawing on the canvas.
 	 */
    public void render(GL2 gl, ProgramInfo info, boolean showNormals) {
    	
    	Matrix4f shiftedModelView = new Matrix4f(info.un_ModelView);
    	shiftedModelView.mul(Transforms.translate3DH(-canvasSize, 0f, 0f));
    	shiftedModelView.mul(Transforms.scale3DH(canvasSize));
    	
    	Program p = Program.swap(gl, flatColorProgram);
    	flatColorProgram.setProjection(gl, info.un_Projection);
    	flatColorProgram.setModelView(gl, shiftedModelView);
    	flatColorProgram.setDiffuseColor(gl, new Vector3f(1.0f, 1.0f, 0.0f));
    	
    	gl.glLineWidth(3.0f);
    	
    	if (vertices != null) {
    		lineArray.draw(gl);
    	}
    	
    	if (showNormals && normals != null && vertices != null) {
    		flatColorProgram.setDiffuseColor(gl, new Vector3f(0.0f, 0.0f, 0.6f));
    		
    		normalArray.draw(gl);
    	}
    	
    	Program.use(gl, p);
    }
    
    /*
     * Renders the curve without an offset.
     */
    public void draw(GL2 gl) {
    	gl.glLineWidth(3.0f);
    	
    	if (vertices != null) {
    		lineArray.draw(gl);
    	}
    }
    
    // Assumes that 2D points are passed in
    public void setVertices(GL2 gl, float [] vertices) {
    	if (vertices == null || vertices.length == 0) return;
    	
    	this.vertices = vertices;
    	
    	if (this.vertices3D == null || this.vertices3D.length != vertices.length + vertices.length / 2) {
    		this.vertices3D = new float[vertices.length + vertices.length / 2];
    	}
    	
    	for (int i = 0; i < vertices.length/2; ++i)
    	{
    		this.vertices3D[3*i] = vertices[2*i];
    		this.vertices3D[3*i + 1] = vertices[2*i + 1];
    		this.vertices3D[3*i + 2] = 0.0f;
    	}
    	
    	verticesBuffer.smartSetData(gl, this.vertices3D);
    	
    	int [] indices = new int[this.vertices3D.length / 3];
		for (int i = 0; i < this.vertices3D.length / 3; ++i)
		{
			indices[i] = i;
		}
		
		linesIndicesBuffer.smartSetData(gl, indices);
    	
    	// Update the normal lines buffer
    	setNormalsBuffer(gl);
    }
    
    public void setNormals(GL2 gl, float [] normals) {
    	if (normals == null || normals.length == 0) return;
    	
    	this.normals = normals;
    	
    	if (this.normals3D == null || this.normals3D.length != normals.length + normals.length / 2) {
    		this.normals3D = new float[normals.length + normals.length / 2];
    	}
    	
    	for (int i = 0; i < normals.length / 2; ++i)
    	{
    		this.normals3D[3*i] = normals[2*i];
    		this.normals3D[3*i + 1] = normals[2*i + 1];
    		this.normals3D[3*i + 2] = 0.0f;
    	}
    	
    	normalsBuffer.smartSetData(gl,  this.normals3D);
    	
    	setNormalsBuffer(gl);
    }
    
    private void setNormalsBuffer(GL2 gl) {
    	if (normals3D != null && vertices3D != null && vertices3D.length == normals3D.length) {
    		float [] diffs = new float[2 * vertices3D.length];
    		
    		float normalScale = 0.5f;
    		
    		for (int i = 0; i < vertices3D.length / 3; ++i)
    		{
    			diffs[6*i    ] = vertices3D[3*i    ];
    			diffs[6*i + 1] = vertices3D[3*i + 1];
    			diffs[6*i + 2] = vertices3D[3*i + 2];
    			
    			diffs[6*i + 3] = vertices3D[3*i] + normalScale * normals3D[3*i    ];
    			diffs[6*i + 4] = vertices3D[3*i + 1] + normalScale * normals3D[3*i + 1];
    			diffs[6*i + 5] = vertices3D[3*i + 2] + normalScale * normals3D[3*i + 2];
    		}
    		
    		int [] indices = new int[2 * vertices3D.length / 3];
    		for (int i = 0; i < vertices3D.length / 3; ++i)
    		{
    			indices[2*i     ] = 2*i;
    			indices[2*i  + 1] = 2*i + 1;
    		}
    		
    		normalBuffer.smartSetData(gl, diffs);
    		normalsIndicesBuffer.smartSetData(gl, indices);
    	}
    }
    
    private float[] vertices3D = null;
    private float[] normals3D = null;

    public float[] vertices = null;
    public float[] normals = null;
}
