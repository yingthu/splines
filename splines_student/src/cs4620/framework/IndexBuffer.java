package cs4620.framework;

import javax.media.opengl.GL2;

/*
 * Encapsulates an OpenGL buffer of integer values, intended to be used
 * for specifying the draw order of vertices in a vertex array. This is
 * a very bare wrapper around VertexBuffer, simply fixing the data type
 * of the buffer and adding a few conveniences related to error-checking
 * the specification of geometry.
 */

public class IndexBuffer extends VertexBuffer {
	
	int expectedNumElements;
	
	public IndexBuffer(GL2 glContext, int [] data)
	{
		// A buffer suitable for using with GL_ELEMENT_ARRAY_BUFFER is a regular old
		// int buffer; this just ensures it's an *int* buffer
		super(glContext, data, 1);
		
		setExpectedNumElements(data);
	}
	
	@Override
	public void smartSetData(GL2 gl, int [] data)
	{
		super.smartSetData(gl, data);
		setExpectedNumElements(data);
	}
	
	@Override
	public void setData(GL2 gl, int [] data)
	{
		super.setData(gl, data);
		setExpectedNumElements(data);
	}
	
	@Override
	public void setSubData(GL2 gl, int [] data)
	{
		super.setSubData(gl, data);
		setExpectedNumElements(data);
	}
	
	private void setExpectedNumElements(int [] data)
	{
		// Given the contents of the index buffer, determine the minimum
		// number of elements that must be had by a vertex buffer indexed
		// by this buffer for it to be renderable.
		int maxIndex = 0;
		for (int i : data)
		{
			if (i > maxIndex)
				maxIndex = i;
		}
		expectedNumElements = maxIndex + 1;
	}
	
	public int getExpectedNumElements()
	{
		return expectedNumElements;
	}
}
