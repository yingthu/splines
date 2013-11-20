package cs4620.framework;

import java.nio.Buffer;

import javax.media.opengl.GL2;

/*
 * Represents a three dimensional texture.
 */
public class TextureThreeDim extends Texture {
	public TextureThreeDim(GL2 gl, int internalFormat)
	{
		super(gl, GL2.GL_TEXTURE_3D, internalFormat);
		allocated = false;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setImage(int width, int height, int depth, int format, int type, Buffer buffer)
	{
		this.width = width;
		this.height = height;
		this.depth = depth;
		
		Texture oldTexture = TextureUnit.getActiveTextureUnit(gl).getBoundTexture();
		if (oldTexture != this)
			bind();
		
		gl.glTexImage3D(target, 0, internalFormat, width, height, depth, 0, format, type, buffer);
		
		if (oldTexture == null)
			unbind();
		else if (oldTexture != this)
			oldTexture.bind();
		
		allocated = true;
	}
	
	protected void allocate(int width, int height, int depth, int format, int type)
	{
		setImage(width, height, depth, format, type, null);
	}
	
	public boolean isAllocated()
	{
		return allocated;
	}
	
	protected int width;
	protected int height;
	protected int depth;
	protected boolean allocated;
}
