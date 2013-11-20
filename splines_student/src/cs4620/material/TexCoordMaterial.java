package cs4620.material;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.Program;
import cs4620.framework.Texture2D;
import cs4620.framework.TextureUnit;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneProgram;
import cs4620.shape.Mesh;


/*
 * A "material" that implements a simple debugging tool for texture coordinates: it uses a vertex shader
 * that takes each vertex's position from its texture coordinates instead of its (x,y,z) coordinates.
 * This produces a map of texture space where you can see the triangles appearing at the locations from
 * which they will sample the texture.
 */

public class TexCoordMaterial extends Material {

	public static final String FIXED_DEPTH_UNIFORM   = "un_FixedDepth";   // float

	private static SceneProgram program = null;
	
	private Texture2D texture = null;
	
	public TexCoordMaterial() {
		super();
	}

	@Override
	public void draw(GL2 gl, ProgramInfo info, Mesh mesh, boolean wireframe) {
		if(program == null)
		{
			try
			{
				program = new SceneProgram(gl, "texCoordAsPosn.vs", "texCoordAsPosn.fs");
			}
			catch(GlslException e)
			{
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		
		if (texture == null) {
			try {
				texture = new Texture2D(gl, "data/textures/earth.jpg");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		// Store the previously used program
		Program p = Program.swap(gl, program);
		program.setAllInfo(gl, info);
				
		if (program.getUniform("un_Texture") != null)
		{
			TextureUnit.getActiveTextureUnit(gl).bindToUniform(gl, program.getUniform("un_Texture"));
			texture.use();
		}
		
		// This "material" ignores the wireframe flag, and always draws both the triangles
		// (colored with the texture) and the wireframe (colored black).  The vertex shader
		// uses a fixed value for the depth; we pass in a slightly larger value for the 
		// triangles so that the wireframe will always be visible on top of the texture.
		program.getUniform(FIXED_DEPTH_UNIFORM).set1Float(gl, 0.0f);
		program.setDiffuseColor(gl, new Vector3f(0.0f, 0.0f, 0.0f));
		mesh.drawWireframe(gl);
		program.getUniform(FIXED_DEPTH_UNIFORM).set1Float(gl, 0.1f);
		program.setDiffuseColor(gl, new Vector3f(1.0f, 1.0f, 1.0f));
		mesh.draw(gl);
		
		// Use the previous program
		Program.use(gl, p);
	}
	


	@Override
	public void drawUsingProgram(GL2 gl, SceneProgram program, Mesh mesh,
			boolean wireframe) {
		if(wireframe) {
			mesh.drawWireframe(gl);
		} else {
			mesh.draw(gl);
		}
	}

	@Override
	public Object getYamlObjectRepresentation() {
		Map<Object, Object> result = new HashMap<Object,Object>();
		result.put("type", "TextureCoordinateMaterial");

		return result;
	}

}
