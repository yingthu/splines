package cs4620.framework;

import java.nio.Buffer;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.TextureData;

/*
 * Represents a one dimensional texture.
 */
public class TextureOneDim extends Texture {
	public TextureOneDim(GL2 gl, int target, int internalFormat)
	{
		super(gl, target, internalFormat);
		allocated = false;
	}
	
	public int getWidth()
	{
		return width;
	}	
	
	public void setData(int width, int format, int type, Buffer buffer)
	{
		this.width = width;		
		
		Texture oldTexture = TextureUnit.getActiveTextureUnit(gl).getBoundTexture();
		if (oldTexture != this)
			bind();
		
		gl.glTexImage1D(target, 0, internalFormat, width, 0, format, type, buffer);
		
		if (oldTexture == null)
			unbind();
		else if (oldTexture != this)
			oldTexture.bind();
		
		allocated = true;
	}
	
	public void setData(TextureData data)
	{
		setData(data.getWidth(), data.getPixelFormat(), data.getPixelType(), data.getBuffer());
	}
	
	protected void allocate(int width, int format, int type)
	{
		setData(width, format, type, null);
	}
	
	public boolean isAllocated()
	{
		return allocated;
	}
	
	protected int width;	
	protected boolean allocated;
}
