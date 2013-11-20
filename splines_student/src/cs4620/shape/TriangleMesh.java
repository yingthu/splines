package cs4620.shape;

import javax.media.opengl.GL2;

import cs4620.framework.IndexBuffer;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;
import cs4620.scene.SceneProgram;

public abstract class TriangleMesh extends Mesh {
	
	// GL resources
	protected VertexArray trianglesArray;
	protected VertexArray wireframeArray;
	
	protected VertexBuffer verticesBuffer;
	protected VertexBuffer normalsBuffer;
	
	protected VertexBuffer texCoordsBuffer;
	
	protected IndexBuffer triangleIndicesBuffer;
	protected IndexBuffer linesIndicesBuffer;

	public TriangleMesh(GL2 gl)
	{
		super(gl);
		
		// TODO (Scene P1): Create buffers for vertices and assign them to vertex arrays for
		// triangle and wireframe drawing. Set the buffers to be empty initially (give them 
		// length-zero arrays); when subclasses of TriangleMesh build their meshes, they
		// will update the buffers' contents using the setter methods below.
		// 
		// Assume that these vertex arrays will be drawn by the SceneProgram shader program.
		float placeHolder[] = {};
		int placeHolder2[] = {};
		
		verticesBuffer = new VertexBuffer(gl, placeHolder, 3); //3 stands for 3D
		normalsBuffer = new VertexBuffer(gl, placeHolder, 3);
		
		triangleIndicesBuffer = new IndexBuffer(gl, placeHolder2);
		linesIndicesBuffer = new IndexBuffer(gl, placeHolder2);
		
		trianglesArray = new VertexArray(gl, GL2.GL_TRIANGLES);
		trianglesArray.setIndexBuffer(gl, triangleIndicesBuffer);
		trianglesArray.setAttributeBuffer(gl, SceneProgram.VERTEX_INDEX, verticesBuffer);
		trianglesArray.setAttributeBuffer(gl, SceneProgram.NORMAL_INDEX, normalsBuffer);
		
		wireframeArray = new VertexArray(gl, GL2.GL_LINES);
		wireframeArray.setIndexBuffer(gl, linesIndicesBuffer);
		wireframeArray.setAttributeBuffer(gl, SceneProgram.VERTEX_INDEX, verticesBuffer);
		wireframeArray.setAttributeBuffer(gl, SceneProgram.NORMAL_INDEX, normalsBuffer);
		// TODO (Shaders 2 P1): Add code to set up the texture coordinates buffer. Some
		// objects will not set texture coordinates, so make sure you can handle that
		texCoordsBuffer = new VertexBuffer(gl, placeHolder, 2);
	}
	
	protected void setVertices(GL2 gl, float [] vertices)
	{
		if (verticesBuffer == null) return; 
		
		if (vertices.length % 3 != 0)
			throw new Error("Vertex array's length is not a multiple of 3.");
		
		verticesBuffer.smartSetData(gl, vertices);
	}
	
	protected void setNormals(GL2 gl, float [] normals)
	{
		if (normalsBuffer == null) return;
		
		if (normals.length % 3 != 0)
			throw new Error("Normal array's length is not a multiple of 3");
		
		normalsBuffer.smartSetData(gl, normals);
	}
	
	protected void setTexCoords(GL2 gl, float [] texCoords)
	{
		if (texCoordsBuffer == null) return;
		
		if (texCoords.length % 2 != 0)
			throw new Error("Texture coords array's length is not a multiple of 2.");
		
		texCoordsBuffer.smartSetData(gl, texCoords);
		
		// TODO (Shaders 2 P1): Add any extra code necessary to use the texture coordinates
		gl.glEnable(GL2.GL_TEXTURE_2D);
		trianglesArray.setAttributeBuffer(gl, SceneProgram.TEXTURE_INDEX, texCoordsBuffer);
		wireframeArray.setAttributeBuffer(gl, SceneProgram.TEXTURE_INDEX, texCoordsBuffer);
	}
	
	protected void setTriangleIndices(GL2 gl, int [] triangleIndices)
	{
		if (triangleIndicesBuffer == null) return;
		
		if (triangleIndices.length % 3 != 0)
	        throw new Error("Triangle array's length is not a multiple of 3.");
		
		triangleIndicesBuffer.smartSetData(gl, triangleIndices);
	}
	
	protected void setWireframeIndices(GL2 gl, int [] wireframeIndices)
	{
		if (linesIndicesBuffer == null) return;
		
		if (wireframeIndices.length % 2 != 0)
	        throw new Error("Line array's length is not a multiple of 2.");
		
		linesIndicesBuffer.smartSetData(gl, wireframeIndices);
	}

	public final void draw(GL2 gl)
	{
		// TODO (Scene P1): Draw the triangle mesh.
		trianglesArray.draw(gl);
	}
	
	public final void drawWireframe(GL2 gl)
	{
		// TODO (Scene P1): Draw the wireframe mesh.
		wireframeArray.draw(gl);
	}
	
	public VertexArray getTrianglesArray()
	{
		return trianglesArray;
	}
	
	public VertexArray getWireframeArray()
	{
		return wireframeArray;
	}
}
