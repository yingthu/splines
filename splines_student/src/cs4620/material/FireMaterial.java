package cs4620.material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import cs4620.framework.*;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneProgram;
import cs4620.shape.Mesh;

/*
 * A fire material. Uses perlin noise to sample from a fire texture.
 */
public class FireMaterial extends Material{
	
	private static SceneProgram program = null;
	
	private Texture2D noiseTexture = null;
	private Texture2D fireTexture = null;
	private float time = 0f;
	
	public final float[] scrollSpeeds = new float[] {1f, 2f, 4f};
	
	
	public FireMaterial() {
		super();
	}

	@Override
	public void draw(GL2 gl, ProgramInfo info, Mesh mesh, boolean wireframe) {
		if (program == null) {
			try {
				program = new SceneProgram(gl, "fireShader.vs", "fireShader.fs");
			}
			catch (GlslException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		
		if (noiseTexture == null) {
			try {
				noiseTexture = new Texture2D(gl, "data/textures/noise.jpg");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if (fireTexture == null) {
			try {
				fireTexture = new Texture2D(gl, "data/textures/fire.jpg");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		// Store the previously used program
		Program p = Program.swap(gl, program);
		program.setAllInfo(gl, info);
		
		if (program.getUniform("un_Time") != null)
			program.getUniform("un_Time").set1Float(gl, time);
		
		if (program.getUniform("un_ScrollSpeeds") != null)
			program.getUniform("un_ScrollSpeeds").set3Float(gl, scrollSpeeds[0], scrollSpeeds[1], scrollSpeeds[2]);
		
		if (program.getUniform("un_NoiseTexture") != null)
		{
			TextureUnit.getActiveTextureUnit(gl).bindToUniform(gl, program.getUniform("un_NoiseTexture"));
			noiseTexture.use();
		}
		
		if (program.getUniform("un_FireTexture") != null)
		{
			TextureUnit.getActiveTextureUnit(gl).bindToUniform(gl, program.getUniform("un_FireTexture"));
			fireTexture.use();
		}
		
		if(wireframe) {
			mesh.drawWireframe(gl);
		} else {
			mesh.draw(gl);
		}
		
		// Use the previous program
		Program.use(gl, p);
		
		time+= 0.001f;
		
	}
	
	@Override
	public void drawUsingProgram(GL2 gl, SceneProgram program, Mesh mesh, boolean wireframe) {
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
	
	public void setSpeeds(float r, float g, float b) {
		setArray(scrollSpeeds,r,g,b);
	}

	protected List<Object> convertFloatArrayToList(float[] a)
	{
		List<Object> result = new ArrayList<Object>();
		for(int i=0;i<a.length;i++)
			result.add(a[i]);
		return result;
	}
	
	@Override
	public Object getYamlObjectRepresentation() {
		Map<Object, Object> result = new HashMap<Object,Object>();
		result.put("type", "FireMaterial");
		
		result.put("scroll_speeds", convertFloatArrayToList(scrollSpeeds));

		return result;
	}

}
