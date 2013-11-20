package cs4620.framework;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

/*
 * The main texture class. An instance of this class is responsible
 * for one texture. The steps for using it are:
 * 1) Call the use method. This will enable the texture,
 *    bind it to the active texture unit, and set the texture
 *    parameters.
 * 2) Draw stuff. You will need to set the texture uniforms for your shader
 *    program (most likely by using TextureUnit.bindToUniform)
 * 3) Call the unuse method, which undoes everything the use method did.
 */
public abstract class Texture {
	public Texture(GL2 gl, int target, int internalFormat)
	{
		int idv[] = new int[1];
		gl.glGenTextures(1, idv, 0);
		this.gl = gl;
		this.id = idv[0];
		this.target = target;
		this.boundTextureUnit = null;
		this.internalFormat = internalFormat;
		this.disposed = false;
		
		minFilter = GL2.GL_NEAREST;
	    magFilter = GL2.GL_NEAREST;
	    wrapS = GL2.GL_CLAMP;
	    wrapT = GL2.GL_CLAMP;
	    wrapR = GL2.GL_CLAMP;
	}
	
	public boolean isDisposed()
	{
		return disposed;
	}
	
	public boolean isBound()
	{
		return boundTextureUnit != null;
	}
	
	public int getId() {
		return id;
	}
	
	public int getTarget() {
		return target;
	}
	
	public void bind()
	{
		bindTo(TextureUnit.getActiveTextureUnit(gl));
	}
	
	public void bindTo(TextureUnit textureUnit)
	{
		if (isDisposed())
			throw new GLException("program tries to bind a disposed texture");
		
		textureUnit.bindTexture(this);
		boundTextureUnit = textureUnit;
	}
	
	public void unbind()
	{
		if (isBound())
		{
			if (isDisposed())
				throw new GLException("program tries to unbind a disposed texture");
			
			boundTextureUnit.unbindTexture(this);
			boundTextureUnit = null;
		}
	}
	
	protected void setTextureParameters()
	{
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_MAG_FILTER, magFilter);
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_MIN_FILTER, minFilter);
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_S, wrapS);
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_T, wrapT);
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_R, wrapR);
	}
	
	protected void setTextureParametersB4()
	{
		gl.glTexParameteri(target, GL2.GL_TEXTURE_MAG_FILTER, magFilter);
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_MIN_FILTER, minFilter);
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	    gl.glTexParameteri(target, GL2.GL_TEXTURE_WRAP_R, GL2.GL_REPEAT);
	}
	
	public void use()
	{
		enable();
		bind();
		setTextureParameters();
	}
	
	public void useB4()
	{
		enable();
		bind();
		setTextureParametersB4();
	}
	
	public void unuse()
	{
		unbind();
		disable();
	}
	
	protected void finalize()
	{
		if (isBound())
			unbind();
		dispose();
	}
	
	public void dispose()
	{
		if (isBound())
			throw new GLException("program tries to dispose a texture before unbinding it");
		
		if (!disposed)
		{
			int idv[] = new int[1];
			idv[0] = id;
			gl.glDeleteTextures(1, idv, 0);
			disposed = true;
		}
	}
		
	public void enable()
	{
		gl.glEnable(target);
	}
	
	public void disable()
	{
		gl.glDisable(target);
	}
	
	public int minFilter;
	public int magFilter;
	public int wrapS;
	public int wrapT;
	public int wrapR;
	
	private boolean disposed;
	protected int id;
	protected int target;
	protected GL2 gl;
	protected TextureUnit boundTextureUnit;
	protected int internalFormat;
}
