package cs4620.material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.Program;
import cs4620.scene.SceneProgram;
import cs4620.scene.ProgramInfo;
import cs4620.shape.Mesh;

/*
 * A flat color material class. Simply colors an object the specified color.
 * No lighting calculations.
 */
public class FlatColorMaterial extends Material
{
	public final float[] color = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	
	private static SceneProgram program;

	public FlatColorMaterial()
	{
		super();
	}
	
	@Override
	public void draw(GL2 gl, ProgramInfo info, Mesh mesh, boolean wireframe) {
		if(program == null) {
			try {
				program = new SceneProgram(gl, "flatcolor.vs", "flatcolor.fs");
			} catch (GlslException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		// Store the previously used program
		Program p = Program.swap(gl,  program);
		
		program.setAllInfo(gl, info);
		
		// The flat color shader uses the diffuse color to draw
		program.setDiffuseColor(gl, new Vector3f( color[0],  color[1],  color[2]));
		
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
		program.setDiffuseColor(gl, new Vector3f( color[0],  color[1],  color[2]));
		
		if(wireframe) {
			mesh.drawWireframe(gl);
		} else {
			mesh.draw(gl);
		}
	}

	protected static void setArray(float[] x, float x0, float x1, float x2)
	{
		x[0] = x0;
		x[1] = x1;
		x[2] = x2;
	}

	public void setColor(float r, float g, float b)
	{
		setArray(color, r, g, b);
	}

	protected List<Object> convertFloatArrayToList(float[] a)
	{
		List<Object> result = new ArrayList<Object>();
		for(int i=0;i<a.length;i++)
			result.add(a[i]);
		return result;
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object, Object> result = new HashMap<Object,Object>();
		result.put("type", "FlatColorMaterial");

		result.put("color", convertFloatArrayToList(color));

		return result;
	}
}
