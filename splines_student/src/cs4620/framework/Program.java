package cs4620.framework;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

/*
 * Encapsulates an OpenGL shader program, consisting of a vertex shader
 * and a fragment shader. The shader program describes how OpenGL should
 * draw geometry. Constructing an instance of this class creates
 * a new shader program from the specified vertex and fragment shader
 * files, as well as from a mapping defining how the program will receive
 * vertex attributes from vertex arrays when drawing geometry. Once
 * constructed, the behavior of the program can be modified by changing
 * the values of its uniform variables (e.g. setting the color or
 * view transform).
 * 
 * In an OpenGL context, there is at most one shader program active at
 * a time; this shader will be used by all draw commands. The static
 * function Program.use(shaderProgram) sets the current shader program
 * for the GL context, and Program.unuse() causes no program to
 * be active (nothing can be drawn*).
 * 
 * (* Technically, since this is still running in an OpenGL 2 context,
 *    the "fixed-function" program is what would be used to draw
 *    when Program.unuse() is called. We are following the conventions
 *    of modern, GL3 and later OpenGL, though, which does not include
 *    the fixed-function program, so we will not use this.)
 */

public class Program {
	
	private static final String SHADERS_BASE_DIR = "src//cs4620//shaders//";
	private static Program current = null;
	
	private int id;
	private VertexShader vertexShader;
	private FragmentShader fragmentShader;
	
	private HashMap<String, Uniform> uniforms;
	public static final boolean PRINT_UNIFORMS = false;

	public static Boolean isAProgramInUse() {
		return current != null;
	}
	
	public static Program getCurrent() {
	    return current;
	}
	
	public static void unuse(GL2 gl) {
		use(gl, null);
	}
	
	public static void use(GL2 gl, Program program)
	{
		// tell the OpenGL context to use the specified program for draw commands
		
		if(program != null)
		{
			gl.glUseProgram(program.getId());                                GLError.get(gl, "PRG.use");
			current = program;
		}
		else
		{
			gl.glUseProgram(0);                                              GLError.get(gl, "PRG.unuse");
			current = null;
		}
	}
	
	public static Program swap(GL2 gl, Program program)
	{
		// set a new active program and return the one that was previously
		// being used (null if unuse() was called).
		Program retProgram = getCurrent();
		if(program != retProgram)
			use(gl, program);
		return retProgram;
	}
	
	public static void draw(GL2 gl, VertexArray array)
	{
		// draw the specified geometry with the current program.
		// (array.draw performs error checking to see if no program is in use)
		array.draw(gl);
	}
	
	public Program(GL2 gl, String vertexSrcFile, 
			String fragmentSrcFile) throws GlslException {
		
		this.vertexShader = null;
		this.fragmentShader = null;
		
		this.id = gl.glCreateProgram();                                      GLError.get(gl, "PRG.init create");
		
		// Attach shaders and link the program (may throw exception)
		buildProgram(gl, SHADERS_BASE_DIR + vertexSrcFile, 
				SHADERS_BASE_DIR + fragmentSrcFile, new HashMap<Integer, String>());
		
		// Create a hash map from all the 'active' uniform variables
		initializeUniforms(gl);
	}
	
	public Program(GL2 gl, String vertexSrcFile, 
			String fragmentSrcFile, Map<Integer, String> attributeMap) throws GlslException {
		
		this.vertexShader = null;
		this.fragmentShader = null;
		
		this.id = gl.glCreateProgram();                                      GLError.get(gl, "PRG.init create");
		
		// Attach shaders and link the program (may throw exception)
		buildProgram(gl, SHADERS_BASE_DIR + vertexSrcFile, 
				SHADERS_BASE_DIR + fragmentSrcFile, attributeMap);
		
		// Create a hash map from all the 'active' uniform variables
		initializeUniforms(gl);
	}
	
	public int getId() {
		return id;
	}
	
	public Boolean isUsed() {
		return current == this;
	}
	
	public HashMap<String, Uniform> GetUniforms() {
		return this.uniforms;
	}
	
	public Uniform getUniform(String name) {
		// Get the specified uniform. This uniform can then be set to have a new value.
		return uniforms.get(name);
	}
	
	public boolean hasUniform(String name)
	{
		return getUniform(name) != null;
	}
	
	protected void finalize() {
		// Deallocate the GLSL resources
	}
	
	protected void buildProgram(GL2 gl, String vertexSrcFile, String fragmentSrcFile, Map<Integer, String> attributeMap) throws GlslException {
		
		// Create the program -- load / create shaders, link program, and compile.
		
		this.vertexShader = new VertexShader(gl, vertexSrcFile);
		this.fragmentShader = new FragmentShader(gl, fragmentSrcFile);		
	    
	    // Attach the vertex shader
	    gl.glAttachShader(this.id, this.vertexShader.GetId());               GLError.get(gl, "PRG.bP attach vs");
	    
	    // Attach the fragment shader
	    gl.glAttachShader(this.id, this.fragmentShader.GetId());             GLError.get(gl, "PRG.bP attach fs");
	    
	    // bind attribute locations
	    for (int loc : attributeMap.keySet())
	    {
	    	gl.glBindAttribLocation(this.id, loc, attributeMap.get(loc));    GLError.get(gl, "PRG.bP bind attr");
	    }
	    
	    gl.glLinkProgram(this.id);                                           GLError.get(gl, "PRG.bP link");
	    
	    // Check the linking status
		int[] linkCheck = new int[1];
		gl.glGetObjectParameterivARB(this.id,
				GL2.GL_OBJECT_LINK_STATUS_ARB, linkCheck, 0);                GLError.get(gl, "PRG.bP link status");
		
		if (linkCheck[0] == GL2.GL_FALSE) {
			throw new GlslException("Link error " + 
					Shader.getInfoLog(gl, this.id));
		}
	}
	
	private void initializeUniforms(GL2 gl) {		  
		// Initialize map of all uniforms the program has
		this.uniforms = new HashMap<String, Uniform>();		   
	    
	    int[] uniformCount = new int[1];
	    gl.glGetProgramiv(this.id, GL2.GL_ACTIVE_UNIFORMS, uniformCount, 0); GLError.get(gl, "PRG.iU unif count");
	    
	    if (PRINT_UNIFORMS)
	    {
	    	System.out.print("GLSL uniforms: ");	
	    }
		for(int uniform_index = 0; uniform_index < uniformCount[0]; 
			uniform_index++) {
			Uniform currUniform = new Uniform(gl, this, uniform_index);
			
			String uniformName = currUniform.getName();
									
			if ( uniformName.startsWith("gl_") )
				continue;
			
			
			
			if (PRINT_UNIFORMS)
			{
				System.out.print(currUniform.getName() + " ");
			}
			this.uniforms.put(currUniform.getName(), currUniform);								
		}		
		System.out.println();
	} 
}
