package cs4620.material;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.Program;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneProgram;
import cs4620.shape.Mesh;

/*
 * A material that colors objects based on the vertex normals.
 */
public class NormalMaterial extends Material {
	private static SceneProgram program = null;

	public NormalMaterial()
	{
		super();
	}
	
	@Override
	public void draw(GL2 gl, ProgramInfo info, Mesh mesh, boolean wireframe) {
		if(program == null)
		{
			try
			{
				program = new SceneProgram(gl, "normalShader.vs", "normalShader.fs");
			}
			catch(GlslException e)
			{
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		
		// Store the previously used program
		Program p = Program.swap(gl,  program);
		
		program.setAllInfo(gl, info);
		
		if(wireframe) {
			mesh.drawWireframe(gl);
		} else {
			mesh.draw(gl);
		}
		
		// Use the previous program
		Program.use(gl, p);
	}
	
	@Override
	public void drawUsingProgram(GL2 gl, SceneProgram program, Mesh mesh, boolean wireframe) {
		if(wireframe) {
			mesh.drawWireframe(gl);
		} else {
			mesh.draw(gl);
		}
	}

	@Override
	public Object getYamlObjectRepresentation() {
		Map<Object, Object> result = new HashMap<Object,Object>();
		result.put("type", "NormalMaterial");

		return result;
	}

}