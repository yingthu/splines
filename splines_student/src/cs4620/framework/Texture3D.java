package cs4620.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.media.opengl.GL2;

/*
 * A wrapper of TextureThreeDim which handles loading 3D
 * textures from files.
 */
public class Texture3D extends TextureThreeDim {
	public Texture3D(GL2 gl)
	{
		super(gl, GL2.GL_RGBA);
	}
	
	public Texture3D(GL2 gl, int internalFormat)
	{
		super(gl, internalFormat);
	}
	
	public Texture3D(GL2 gl, String filename, int width, int height, int depth, int internalFormat, int type) throws IOException
	{
		this(gl, new File(filename), width, height, depth, internalFormat, type);
	}
	
	public Texture3D(GL2 gl, File file, int width, int height, int depth, int internalFormat, int type) throws IOException
	{
		super(gl, internalFormat);
		
		FileInputStream stream = new FileInputStream(file);
		byte texdata[] = readEntireFile(stream);
				
		ByteBuffer buf = ByteBuffer.wrap(texdata);
		
		setImage(width, height, depth, internalFormat, type, buf);
	}
	
	private byte[] readEntireFile(InputStream inputStream) throws IOException
	{
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    
	    byte[] data = new byte[4096];
	    int count = inputStream.read(data);
	    
	    while(count != -1)
	    {
	        out.write(data, 0, count);
	        count = inputStream.read(data);
	    }

	    return out.toByteArray();
	}
	
	@Override
	public void allocate(int width, int height, int depth, int format, int type)
	{
		super.allocate(width, height, depth, format, type);		
	}
}
