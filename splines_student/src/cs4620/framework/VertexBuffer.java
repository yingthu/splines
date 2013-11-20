package cs4620.framework;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/*
 * Encapsulates an OpenGL (vertex) buffer. The buffer is simply an
 * array of values of some data type that is managed by OpenGL. In the
 * framework, these buffers will be used to specify (1) vertex attributes
 * such as position or texture coordinate and (2) vertex indices for use
 * with vertex array objects.
 * 
 * For simplicity's sake this abstraction limits the use of the buffer, e.g.
 * by ignoring stride/offset. The abstraction also hides the fact that
 * OpenGL has a global state of the currently bound array buffer; buffers
 * are bound and immediately unbound after use in the implementations below.
 */

public class VertexBuffer {
	
	private int id;            // GL id of object
	private int format;        // e.g. GL2.GL_FLOAT
	private int numComponents; // number of components in vector, e.g. 3
	// Number of indexable, ready-to-draw elements in buffer, given number of components.
	// The buffer may have more capacity than this; see numAllocatedElements.
	private int numElements;
	
	// Amount of space allocated to buffer, whether or not it all has meaningful data in it.
	// Writing to previously-allocated memory is faster than always re-allocating the exact
	// amount of memory needed.
	private int numAllocatedElements;
	
	private static int DRAW_MODE = GL2.GL_STATIC_DRAW;
	
	public VertexBuffer(GL2 gl, float [] data, int in_numComponents)
	{
		format = GL2.GL_FLOAT;
		numComponents = in_numComponents;
		
		// gen buffer
		int [] idBuf = {-1};
		gl.glGenBuffers(1, idBuf, 0);                                        GLError.get(gl, "VBO.init gen");
		id = idBuf[0];
		
		setData(gl, data);
	}
	
	public VertexBuffer(GL2 gl, int [] data, int in_numComponents)
	{
		format = GL2.GL_UNSIGNED_INT;
		numComponents = in_numComponents;
		
		// gen buffer
		int [] idBuf = {-1};
		gl.glGenBuffers(1, idBuf, 0);                                        GLError.get(gl, "VBO.init gen");
		id = idBuf[0];
		
		setData(gl, data);
	}
	
	public void allocateSpace(GL2 gl, int numBytes)
	{
		numElements = 0;
		numAllocatedElements = numBytes / (numComponents * numBytesFor(format));
		
		// bind / fill buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);                            GLError.get(gl, "VBO.aS bind");
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numBytes, null, DRAW_MODE);     GLError.get(gl, "VBO.aS data");
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);                             GLError.get(gl, "VBO.aS unbind");
	}
	
	public void allocateElements(GL2 gl, int numDesiredElements)
	{
		allocateSpace(gl, numDesiredElements * numComponents * numBytesFor(format));
	}
	
	public void resizeIfNeeded(GL2 gl, int numDesiredElements)
	{
		if (numDesiredElements > numAllocatedElements)
		{
			// not enough space -- allocate twice as much as this needs
			allocateElements(gl, 2 * numDesiredElements);
		}
		else if (numDesiredElements > 0 && (numAllocatedElements / numDesiredElements) >= 4)
		{
			// way too much space -- bring down to just twice as many as needed
			allocateElements(gl, 2 * numDesiredElements);
		}
	}
	
	/**
	 * Set data, reusing the previously allocated memory if possible.
	 */
	
	public void smartSetData(GL2 gl, float [] data)
	{
		if(format != GL2.GL_FLOAT)
		{
			System.err.println("WARNING: setting float data to non-float buffer!");
		}
		
		int numDesiredElements = data.length / numComponents;
		resizeIfNeeded(gl, numDesiredElements);
		
		setSubData(gl, data);
	}
	
	public void setData(GL2 gl, float [] data)
	{
		if(format != GL2.GL_FLOAT)
		{
			System.err.println("WARNING: setting float data to non-float buffer!");
		}
		
		numElements = data.length / numComponents;
		numAllocatedElements = numElements;
		
		if (data.length % numComponents != 0)
		{
			System.err.println("WARNING: Input data is not multiple of numComponents");
		}
		int byteLength = data.length * numBytesFor(format);
		
		// contents for buffer
		FloatBuffer bufData = Buffers.newDirectFloatBuffer(data.length);
		bufData.put(data);
		bufData.rewind();
		
		// bind / fill buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);                            GLError.get(gl, "VBO.sD bind");
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, byteLength, bufData, DRAW_MODE);
		                                                                     GLError.get(gl, "VBO.sD data");
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);                             GLError.get(gl, "VBO.sD unbind");
	}
	
	public void setSubData(GL2 gl, float [] data)
	{
		if(format != GL2.GL_FLOAT)
		{
			System.err.println("WARNING: setting float data to non-float buffer!");
		}
		
		if (data.length > numAllocatedElements * numComponents)
		{
			System.err.println("WARNING: tried to write more data than buffer can fit");
			return;
		}
		
		numElements = data.length / numComponents;
		
		if (data.length % numComponents != 0)
		{
			System.err.println("WARNING: Input data is not multiple of numComponents");
		}
		int byteLength = data.length * numBytesFor(format);
		
		// contents for buffer
		FloatBuffer bufData = Buffers.newDirectFloatBuffer(data.length);
		bufData.put(data);
		bufData.rewind();
		
		// bind / fill buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);                            GLError.get(gl, "VBO.sSD bind");
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, byteLength, bufData);
		                                                                     GLError.get(gl, "VBO.sSD subdata");
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);                             GLError.get(gl, "VBO.sSD unbind");
	}
	
	/**
	 * Set data, reusing the previously allocated memory if possible.
	 */
	
	public void smartSetData(GL2 gl, int [] data)
	{
		if(format != GL2.GL_UNSIGNED_INT)
		{
			System.err.println("WARNING: setting int data to non-int buffer!");
		}
		
		int numDesiredElements = data.length / numComponents;
		resizeIfNeeded(gl, numDesiredElements);
		
		setSubData(gl, data);
	}
	
	public void setData(GL2 gl, int [] data)
	{
		if(format != GL2.GL_UNSIGNED_INT)
		{
			System.err.println("WARNING: setting int data to non-int buffer!");
		}
		
		numElements = data.length / numComponents;
		numAllocatedElements = numElements;
		
		if (data.length % numComponents != 0)
		{
			System.err.println("WARNING: Input data is not multiple of numComponents");
		}
		int byteLength = data.length * numBytesFor(format);
		
		// contents for buffer
		IntBuffer bufData = Buffers.newDirectIntBuffer(data.length);
		bufData.put(data);
		bufData.rewind();
		
		// bind / fill buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);                            GLError.get(gl, "VBO.sD bind");
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, byteLength, bufData, DRAW_MODE);
		                                                                     GLError.get(gl, "VBO.sD data");
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);                             GLError.get(gl, "VBO.sD unbind");
	}
	
	public void setSubData(GL2 gl, int [] data)
	{
		if(format != GL2.GL_UNSIGNED_INT)
		{
			System.err.println("WARNING: setting int data to non-int buffer!");
		}
		
		if (data.length > numAllocatedElements * numComponents)
		{
			System.err.println("WARNING: tried to write more data than buffer can fit");
			return;
		}
		
		numElements = data.length / numComponents;
		
		if (data.length % numComponents != 0)
		{
			System.err.println("WARNING: Input data is not multiple of numComponents");
		}
		int byteLength = data.length * numBytesFor(format);
		
		// contents for buffer
		IntBuffer bufData = Buffers.newDirectIntBuffer(data.length);
		bufData.put(data);
		bufData.rewind();
		
		// bind / fill buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);                            GLError.get(gl, "VBO.sSD bind");
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, byteLength, bufData);
		                                                                     GLError.get(gl, "VBO.sSD subdata");
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);                             GLError.get(gl, "VBO.sSD unbind");
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getFormat()
	{
		return format;
	}
	
	public int getNumComponents()
	{
		return numComponents;
	}
	
	public int getNumElements()
	{
		return numElements;
	}
	
	protected static int numBytesFor(int f)
	{
		switch(f)
		{
		case GL2.GL_FLOAT:
		case GL2.GL_UNSIGNED_INT:
			return 4;
		default:
			System.err.println("FAIL: requested num bytes for unknown type");
			System.exit(1);
			return 1;
		}
	}
}
