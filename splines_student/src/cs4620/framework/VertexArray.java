package cs4620.framework;

import javax.media.opengl.GL2;

/*
 * Encapsulates an OpenGL vertex array object. A vertex array is used
 * to describe a piece of geometry that can be drawn. It specifies
 * the vertices that make up the geometry and describes how the
 * vertices should be grouped into triangles, lines, or other geometric
 * primitives to draw the desired geometry.
 * 
 * The vertex array stores the state of what buffers are bound to it*.
 * Note that multiple vertex arrays are allowed to use the same
 * vertex buffer, and that vertex buffers are used "by reference"
 * in vertex arrays (changes made to the buffer are seen by the array).
 * Technically, the OpenGL context has global state of which vertex
 * array is currently bound; this is abstracted away within draw().
 * 
 * (* If machine doesn't support VAOs, the class keeps track of buffers 
 *    itself and sets up the required vertex array state at each draw(). 
 *    Also, while the bound element array (index array) is part of the
 *    vertex array's state, this code rebinds it each time because
 *    of rumors that some drivers don't save this state with the
 *    vertex array correctly. )
 */

public class VertexArray {
	
	private int id;           // GL id of object
	private int geometryType; // e.g. GL2.GL_TRIANGLES
	//private boolean hasIndex; // does VAO have an index array?
	
	private static boolean checkedSupport = false;
	private static boolean supportsVAOs = true; // if false, can emulate functionality
	private static final boolean RESET_INDICES = true; // re-send indices / element array every time, even if using VAO
	
	// can lookup via glGetInteger(GL_MAX_VERTEX_ATTRIB_BINDINGS), but "will almost certainly be 16"
	// ( http://www.opengl.org/wiki/Vertex_Specification (Separate_attribute_format section))
	private static final int MAX_ATTRIBUTES = 16;
	
	private IndexBuffer indexBuffer;
	private VertexBuffer [] vertexBuffers = new VertexBuffer[MAX_ATTRIBUTES];
	
	public VertexArray(GL2 gl, int in_geometryType)
	{
		staticInitialization(gl);
		
		indexBuffer = null;
		geometryType = in_geometryType;
		
		if(supportsVAOs)
		{
			// generate VAO
			int [] idBuf = {-1};
			gl.glGenVertexArrays(1, idBuf, 0);                               GLError.get(gl, "VAO.init gen");
			id = idBuf[0];
		}
		
		// that's all at this point
	}
	
	private static void staticInitialization(GL2 gl)
	{
		// perform one-time miscellaneous stuff
		if(!checkedSupport)
		{
			checkedSupport = true;
			
			// query for VAO support -- can emulate otherwise
			supportsVAOs = gl.isExtensionAvailable("GL_ARB_vertex_array_object");
			// WARNING: there's an Apple-specific vertex array object extension, but don't trust it!
			// JOGL doesn't load that extension by default.
		}
	}
	
	public void setAttributeBuffer(GL2 gl, int index, VertexBuffer buffer)
	{
		vertexBuffers[index] = buffer;
		
		if(supportsVAOs)
		{
			// bind
			gl.glBindVertexArray(id);                                        GLError.get(gl, "VAO.sAB bind");
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffer.getId());            GLError.get(gl, "VAO.sAB bind buffer");
			
			// configure
			gl.glEnableVertexAttribArray(index);                             GLError.get(gl, "VAO.sAB enable attr");
			                                                                                  //stride, offset
			gl.glVertexAttribPointer(index, buffer.getNumComponents(), buffer.getFormat(), false, 0, (long) 0);
			                                                                 GLError.get(gl, "VAO.sAB ptr");
			
			// unbind
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);                         GLError.get(gl, "VAO.sAB unbind buffer");
			gl.glBindVertexArray(0);                                         GLError.get(gl, "VAO.sAB unbind");
		}
	}
	
	public VertexBuffer getAttributeBuffer(int index)
	{
		return vertexBuffers[index];
	}
	
	public void unsetAttributeBuffer(GL2 gl, int index)
	{
		vertexBuffers[index] = null;
		
		if(supportsVAOs)
		{
			// bind
			gl.glBindVertexArray(id);                                        GLError.get(gl, "VAO.uAB bind");
			
			// configure
			gl.glDisableVertexAttribArray(index);                            GLError.get(gl, "VAO.uAB disable attr");
			
			// unbind
			gl.glBindVertexArray(0);                                         GLError.get(gl, "VAO.uAB unbind");
		}
	}
	
	public void setIndexBuffer(GL2 gl, IndexBuffer in_indexBuffer)
	{
		indexBuffer = in_indexBuffer;
		
		// The joys of driver support -- in theory, the GL_ELEMENT_ARRAY_BUFFER binding is supposed
		// to be a part of the state of the current VAO. Unfortunately, this behavior doesn't happen
		// on some drivers, apparently
		// ( http://stackoverflow.com/questions/8973690/vao-and-element-array-buffer-state )
		// so in an abundance of caution I'm disabling the code below.
		
		if(supportsVAOs && !RESET_INDICES)
		{
			// bind
			gl.glBindVertexArray(id);                                        GLError.get(gl, "VAO.sIB bind");
			
			// configure
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getId());
			                                                                 GLError.get(gl, "VAO.sIB bind ibuf");
	
			// unbind
			// DO NOT UNBIND INDEX BUFFER! Unlike GL_ARRAY_BUFFER, GL_ELEMENT_ARRAY_BUFFER binding
			// is part of the vertex array's state, not the overall GL state
			gl.glBindVertexArray(0);                                         GLError.get(gl, "VAO.sIB unbind");
		}
	}
	
	public IndexBuffer getIndexBuffer()
	{
		return indexBuffer;
	}
	
	public void unsetIndexBuffer(GL2 gl)
	{
		indexBuffer = null;
		
		// see comment in setIndexBuffer().
		
		if(supportsVAOs && !RESET_INDICES)
		{
			// bind
			gl.glBindVertexArray(id);                                        GLError.get(gl, "VAO.uIB bind");
			
			// configure
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);                 GLError.get(gl, "VAO.uIB unbind ibuf");
	
			// unbind
			gl.glBindVertexArray(0);                                         GLError.get(gl, "VAO.uIB unbind");
		}
	}
	
	private void setupAllBuffers(GL2 gl) {
		// This function should only be called when supportsVAOs is false
		
		VertexBuffer buf;
		for (int i = 0; i < MAX_ATTRIBUTES; i++)
		{
			buf = vertexBuffers[i];
			if (buf == null)
				continue;
			
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buf.getId());
			gl.glEnableVertexAttribArray(i);                                 GLError.get(gl, "VAO.sAllB enable attr");
                                                                                     //stride, offset
			gl.glVertexAttribPointer(i, buf.getNumComponents(), buf.getFormat(), false, 0, (long) 0);
			                                                                 GLError.get(gl, "VAO.sAllB ptr");
		}
	}
	
	private void teardownAllBuffers(GL2 gl) {
		// This function should only be called when supportsVAOs is false
		
		VertexBuffer buf;
		for (int i = 0; i < MAX_ATTRIBUTES; i++)
		{
			buf = vertexBuffers[i];
			if (buf == null)
				continue;
			
			gl.glDisableVertexAttribArray(i);                                GLError.get(gl, "VAO.tAllB disable attr");
		}
	}
	
	public void draw(GL2 gl)
	{
		// NOTE: the lines that bind and unbind to the GL_ELEMENT_ARRAY_BUFFER target would not normally
		// be needed when VAOs are supported -- see comment in setIndexBuffer().
		
		if(!Program.isAProgramInUse())
		{
			System.err.println("WARNING: running VertexArray.draw() with no bound shader program!");
		}
		
		// bind
		if(supportsVAOs)
		{
			gl.glBindVertexArray(id);                                        GLError.get(gl, "VAO.draw bind");
		}
		else
			setupAllBuffers(gl);
		
		// draw
		if(indexBuffer != null)
		{
			// confirm that buffers have enough elements to satisfy index buffer
			if (getMinVertexCount(true) < indexBuffer.getExpectedNumElements())
			{
				System.err.println("WARNING: Buffer(s) have insufficient elements for index buffer");
			}
			
			if(!supportsVAOs || RESET_INDICES)
			{
				gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getId());
				                                                             GLError.get(gl, "VAO.draw bind ibuf");
			}
			//                                                                                     offset
			gl.glDrawElements(geometryType, indexBuffer.getNumElements(), indexBuffer.getFormat(), 0);
			                                                                 GLError.get(gl, "VAO.draw indexed");
			if(!supportsVAOs || RESET_INDICES)
			{
				gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);             GLError.get(gl, "VAO.draw unbind ibuf");
			}
		}
		else
		{
			// by default, use the minimum of the numbers of vertices of the buffers
			//                           start   stop
			gl.glDrawArrays(geometryType, 0, getMinVertexCount(true));       GLError.get(gl, "VAO.draw draw");
		}
		
		// unbind
		if(supportsVAOs)
		{
			gl.glBindVertexArray(0);                                             GLError.get(gl, "VAO.draw unbind");
		}
		else
			teardownAllBuffers(gl);
	}
	
	private int getMinVertexCount(boolean warnInconsistent) {
		// get the smallest number of vertices found in any of the bound vertex buffers
		boolean foundBuffer = false;
		boolean warned = false;
		int minCount = 0;
		
		for (VertexBuffer vb : vertexBuffers)
		{
			if (vb != null)
			{
				if(foundBuffer)
				{
					int count = vb.getNumElements();
					if (warnInconsistent && !warned && count != minCount)
					{
						System.err.println("WARNING: Vertex array's buffers have inconsistent vertex counts!");
						warned = true;
					}
					if(count < minCount)
						minCount = count;
				}
				else
				{
					minCount = vb.getNumElements();
					foundBuffer = true;
				}
			}
		}
		return minCount;
	}
}
