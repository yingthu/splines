package cs4620.material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.Program;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneProgram;
import cs4620.shape.Mesh;

/*
 * A Diffuse material.
 */
public class DiffuseMaterial extends PhongMaterial {
	
	private static SceneProgram program;

	public DiffuseMaterial()
	{
		super();
	}
	
	@Override
	public void draw(GL2 gl, ProgramInfo info, Mesh mesh, boolean wireframe) {
		if(program == null) {
			try {
				program = new SceneProgram(gl, "diffuse.vs", "diffuse.fs");
			} catch (GlslException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		// Store the previously used program
		Program p = Program.swap(gl,  program);
		
		program.setAllInfo(gl, info);
		
		program.setAmbientColor(gl, new Vector3f( ambient[0],  ambient[1],  ambient[2]));
		program.setDiffuseColor(gl, new Vector3f( diffuse[0],  diffuse[1],  diffuse[2]));
		
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
		program.setAmbientColor(gl, new Vector3f( ambient[0],  ambient[1],  ambient[2]));
		program.setDiffuseColor(gl, new Vector3f( diffuse[0],  diffuse[1],  diffuse[2]));
		
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
		result.put("type", "DiffuseMaterial");

		result.put("ambient", convertFloatArrayToList(ambient));
		result.put("diffuse", convertFloatArrayToList(diffuse));

		return result;
	}
}
