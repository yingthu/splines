package cs4620.framework;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.media.opengl.GL2;

/*
 * Encapsulates an OpenGL shader object. The shader is a piece of code describing
 * how OpenGL should perform a certain phase of the rendering pipeline.
 * Currently, the framework supports vertex shaders and fragment shaders.
 * 
 * The user will never create a subclass of a Shader object directly.
 * A Program object, representing a complete shader program, is constructed
 * given the desired vertex and fragment shader files, and handles creating
 * the two shader objects as well as linking and compiling them into a single
 * program that can be used to draw geometry.
 */

public abstract class Shader {
	
	private final int type; // GL2.GL_FRAGMENT_SHADER or GL2.GL_VERTEX_SHADER
	private int id;	
	
	// Check whether the GLSL vertex and fragment shaders are supported
	public static Boolean checkGlslSupport(GL2 gl) {
		if ( !gl.isExtensionAvailable("GL_ARB_vertex_shader") ||
			 !gl.isExtensionAvailable("GL_ARB_fragment_shader") ) {
			
			System.err.println("GLSL is not supported!");
			return false;
			
		} else {			
			System.out.println("GLSL is supported!");
			return true;
		}
	}
	
	public static String readFile(String filePath) throws GlslException {
        String content = null;
        
        try {
            File f = new File(filePath);
            FileReader fr = new FileReader(f);
            
            int size = (int) f.length();
            char buff[] = new char[size];
            int len =  fr.read(buff);
            
            content = new String(buff,0,len);
            
            fr.close();
        } catch(IOException e) {
            throw new GlslException(e.getMessage());
        }
        
        return content;
    }
	
	public static String getInfoLog(GL2 gl, int objectId) {
		int[] buf = new int[1];
		
		// Retrieve the log length
		gl.glGetObjectParameterivARB(objectId, 
				GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB, buf, 0);                  GLError.get(gl, "SHR.gIL length");
	
		int logLength = buf[0];
		
		if (logLength <= 1) {
			return "";
		} else {		
			// Retrieve the log message
			byte[] content = new byte[logLength + 1];
			gl.glGetInfoLogARB(objectId, logLength, buf,0, content, 0);      GLError.get(gl, "SHR.gIL log");
			
			return new String(content);
		}
    }
	
	public Shader(GL2 gl, int shaderType,
			String srcFile) throws GlslException {
		this.type = shaderType;
		
		id = gl.glCreateShader(this.type);                                   GLError.get(gl, "SHR.init create");
		
		String source = readFile(srcFile);				
		
		setSource(gl, source);
		
		if ( !compile(gl) ) {
			throw new GlslException("Compilation error " + 
					getInfoLog(gl, this.id));
		}
	}	
	
	public int GetId() {
		return this.id;
	}
	
// ************* Protected functions *************
	
	protected void finalize() {
		// Deallocate the GLSL resources
	}
	
	protected void setSource(GL2 gl, String source) {				
		// Attach the GLSL source code
		gl.glShaderSource(this.id, 1, 
				new String[] {source},
				new int[] {source.length()}, 0);                             GLError.get(gl, "SHR.sS");
    }
	
	protected Boolean compile(GL2 gl) {
		// Try to compile the GLSL source code
		gl.glCompileShader(this.id);                                         GLError.get(gl, "SHR.compile comp");
		
		// Check the compilation status
		int[] compileCheck = new int[1];
		gl.glGetShaderiv(this.id, GL2.GL_COMPILE_STATUS, compileCheck, 0);   GLError.get(gl, "SHR.compile check");
         
         return compileCheck[0] == GL2.GL_TRUE;    	 
	}			
}
