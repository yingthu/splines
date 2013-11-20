package cs4620.framework;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/*
 * Represents a uniform variable found in a shader program. The main
 * purpose of this class is to update the values of uniforms in a program.
 * For example, calling
 *   shaderProgram.getUniform("some_uniform").set1Float(1.0f);
 * will update the state of the shader program shaderProgram so that
 * future drawing done with that program will set the uniform
 * some_uniform to 1.0 when the program is run. The program will
 * continue to use this value for some_uniform each time it draws
 * geometry until the value is explicitly changed again.
 * The shader's uniform values can be updated at any time, even if it's
 * not currently the one set to be used for drawing by Program.use().* 
 * 
 * (* Normally, the underlying API call gl.glUniform*(), which updates
 *    the value of a uniform variable, updates the uniform of *the
 *    currently used shader program in the OpenGL context*. Thus, when
 *    the framework code sets a uniform's value for a program other than
 *    the currently-used program, the framework silently switches the
 *    currently-bound program in the background before and after calling
 *    gl.glUniform*() so that the correct program's uniform is set. )
 * 
 */

public class Uniform {
	
	private Program program;
	
	private String name;

	private int size;
	private int type;
	private int location;
	
	private Boolean isRowMajor;
	
	public Uniform(GL2 gl, Program prog, int index) {
		this.program = prog;
		//gl = glContext;
		
		byte[] uniformName = new byte[512];
		
		int[] uniformLength = new int[1];
		int[] uniformSize = new int[1];
		int[] uniformType = new int[1];				
		
		// Get the uniform info (name, type, size)
		gl.glGetActiveUniform(this.program.getId(), index, 
				uniformName.length, uniformLength, 0, uniformSize, 0, 
				uniformType, 0,  uniformName, 0);                            GLError.get(gl, "UNF.init get unif");
										
		this.name = new String(uniformName, 0, uniformLength[0]);
		this.size = uniformSize[0];
		this.type = uniformType[0];		
		
		// remove array notation if present -- uniform arrays are reported as uniformName[0]
		int bracketIndex = this.name.indexOf('[');
		if (bracketIndex >= 0)
		{
			this.name = this.name.substring(0, bracketIndex);
		}
		
		// Get the uniform location within the program
		this.location =
			gl.glGetUniformLocation(this.program.getId(), this.name);        GLError.get(gl, "UNF.init get loc");
		
		this.isRowMajor = false; // Default is column major format
		
		// Some Intel drivers return -1 for "gl_" variables.
		// Since we shouldn't be setting "gl_" variables anyway, just skip this part
		// and return an incomplete Uniform with a -1 location.
		if(this.location > -1)
		{
			// We should provide the index as an int[] buffer
			int[] uniformIndex = new int[] {index};
			int[] uniformIsRowMajor = new int[1];		
			
			// Get the uniform storage format (column VS row major)
			try {
				// Seems like some OpenGL drivers do not implement this function 
				gl.glGetActiveUniformsiv(this.program.getId(), 1, uniformIndex, 0, 
					GL2.GL_UNIFORM_IS_ROW_MAJOR, uniformIsRowMajor, 0);      GLError.get(gl, "UNF.init check RM");
				
				this.isRowMajor = (uniformIsRowMajor[0] != 0);
			} catch (GLException ex) { 
				// NOP
			}
		}
		
	}	

	public Boolean getIsRowMajor() {
		return isRowMajor;
	}

	public int getLocation() {
		return location;
	}

	public int getType() {
		return type;
	}

	public int getSize() {
		return size;
	}
	
	public String getName() {
		return name;
	}
	
	public void set1Int(GL2 gl, int x) {
		Program other = Program.swap(gl, program);
        gl.glUniform1i(this.location, x);                               GLError.get(gl, "UNF.s1I");
        Program.swap(gl, other);
    }
    
    public void set2Int(GL2 gl, int x, int y) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform2i(this.location, x, y);    	                     GLError.get(gl, "UNF.s2I");
    	Program.swap(gl, other);
    }
    
    public void set3Int(GL2 gl, int x, int y, int z) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform3i(this.location, x, y, z);                         GLError.get(gl, "UNF.s3I");
    	Program.swap(gl, other);
    }
    
    public void set4Int(GL2 gl, int x, int y, int z, int w) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform4i(this.location, x, y, z, w);                      GLError.get(gl, "UNF.s4I");
    	Program.swap(gl, other);
    }
    
    public void setIntArray(GL2 gl, int [] va)
    {
    	Program other = Program.swap(gl, program);
 
    	//gl.glUniform1iv(this.location, va.length, va, 0);       GLError.get(gl, "UNF.sV3A");
    	gl.glUniform1ivARB(this.location, va.length, va, 0);    GLError.get(gl, "UNF.sV1I");
    	Program.swap(gl, other);
    }

    public void set1Float(GL2 gl, float x) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform1f(this.location, x);                               GLError.get(gl, "UNF.s1F");
    	Program.swap(gl, other);
    }
    
    public void set2Float(GL2 gl, float x, float y) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform2f(this.location, x, y);                            GLError.get(gl, "UNF.s2F");
    	Program.swap(gl, other);
    }
    
    public void set3Float(GL2 gl, float x, float y, float z) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform3f(this.location, x, y, z);                         GLError.get(gl, "UNF.s3F");
    	Program.swap(gl, other);
    }
    
    public void set4Float(GL2 gl, float x, float y, float z, float w) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform4f(this.location, x, y, z, w);                      GLError.get(gl, "UNF.s4F");
    	Program.swap(gl, other);
    }
    
    public void setVector2(GL2 gl, Vector2f v) {
    	Program other = Program.swap(gl, program);
        gl.glUniform2f(this.location, v.x, v.y);                        GLError.get(gl, "UNF.sV2");
        Program.swap(gl, other);
    }
    
    public void setVector2Array(GL2 gl, Vector2f [] va)
    {
    	Program other = Program.swap(gl, program);
    	float [] vectorData = new float [2 * va.length];
    	for (int i = 0; i < va.length; i++)
    	{
    		vectorData[3*i + 0] = va[i].x;
    		vectorData[3*i + 1] = va[i].y;
    	}
    	//gl.glUniform3fv(this.location, va.length, vectorData, 0);       GLError.get(gl, "UNF.sV3A");
    	gl.glUniform2fvARB(this.location, va.length, vectorData, 0);    GLError.get(gl, "UNF.sV2A");
    	Program.swap(gl, other);
    }
    
    public void setVector3(GL2 gl, Vector3f v) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform3f(this.location, v.x, v.y, v.z);                   GLError.get(gl, "UNF.sV3");
    	Program.swap(gl, other);
    }
    
    public void setVector3Array(GL2 gl, Vector3f [] va)
    {
    	Program other = Program.swap(gl, program);
    	float [] vectorData = new float [3 * va.length];
    	for (int i = 0; i < va.length; i++)
    	{
    		vectorData[3*i + 0] = va[i].x;
    		vectorData[3*i + 1] = va[i].y;
    		vectorData[3*i + 2] = va[i].z;
    	}
    	//gl.glUniform3fv(this.location, va.length, vectorData, 0);       GLError.get(gl, "UNF.sV3A");
    	gl.glUniform3fvARB(this.location, va.length, vectorData, 0);    GLError.get(gl, "UNF.sV3A");
    	Program.swap(gl, other);
    }
    
    public void setVector4(GL2 gl, Vector4f v) {
    	Program other = Program.swap(gl, program);
    	gl.glUniform4f(this.location, v.x, v.y, v.z, v.w);              GLError.get(gl, "UNF.sV4");
    	Program.swap(gl, other);
    }
    
    public void setMatrix3(GL2 gl, Matrix3f mat) {
    	Program other = Program.swap(gl, program);
    	FloatBuffer buf = FloatBuffer.allocate(9);
    	
    	// We will pass the matrix elements in column major order
    	for (int c = 0; c < 3; ++c) {
    		for (int r = 0; r < 3; ++r) {
    			buf.put(mat.getElement(r, c));
    		}
    	}
    	
    	buf.flip();
    	
    	gl.glUniformMatrix3fv(this.location, 1, false, buf);            GLError.get(gl, "UNF.sM3");
    	Program.swap(gl, other);
    }
    
    public void setMatrix4(GL2 gl, Matrix4f mat) {
    	Program other = Program.swap(gl, program);
    	FloatBuffer buf = FloatBuffer.allocate(16);
    	
    	// We will pass the matrix elements in column major order
    	for (int c = 0; c < 4; ++c) {
    		for (int r = 0; r < 4; ++r) {
    			buf.put(mat.getElement(r, c));
    		}
    	}
    	
    	buf.flip();
    	
    	gl.glUniformMatrix4fv(this.location, 1, false, buf);            GLError.get(gl, "UNF.sM4");
    	Program.swap(gl, other);
    }
}
