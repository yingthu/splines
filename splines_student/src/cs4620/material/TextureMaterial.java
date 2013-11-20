package cs4620.material;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;

import cs4620.framework.GlslException;
import cs4620.framework.Program;
import cs4620.framework.Texture2D;
import cs4620.framework.Texture2DMipmapped;
import cs4620.framework.TextureUnit;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneProgram;
import cs4620.shape.Mesh;

/*
 * A material that draws an earth texture on the object.
 */
public class TextureMaterial extends PhongMaterial {
	
	private static SceneProgram program = null;
	
	private Texture2D texture = null;
	
	private String fileName = null;
	
	public TextureMaterial() 
	{
		super();
	}
	
	public TextureMaterial(String name)
	{
		super();
		fileName = name;
	}

	@Override
	public void draw(GL2 gl, ProgramInfo info, Mesh mesh, boolean wireframe) 
	{
		if(program == null)
		{
			try
			{
				program = new SceneProgram(gl, "textureShader.vs", "textureShader.fs");
			}
			catch(GlslException e)
			{
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		
		if (texture == null) {
			try {
				if (fileName == null)
				{
					texture = new Texture2D(gl, "data/textures/earth.jpg");
				}
				else
				{
					texture = new Texture2D(gl, fileName);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		// Store the previously used program
		Program p = Program.swap(gl, program);
		program.setAllInfo(gl, info);
		
		program.setAmbientColor(gl, new Vector3f( ambient[0],  ambient[1],  ambient[2]));
		program.setDiffuseColor(gl, new Vector3f( diffuse[0],  diffuse[1],  diffuse[2]));
		program.setSpecularColor(gl, new Vector3f(specular[0], specular[1], specular[2]));
		program.setShininess(gl, shininess);
		
		if (program.getUniform("un_Texture") != null)
		{
			TextureUnit.getActiveTextureUnit(gl).bindToUniform(gl, program.getUniform("un_Texture"));
			texture.use();
		}
		
		if(wireframe) {
			mesh.drawWireframe(gl);
		} else {
			mesh.draw(gl);
		}
		
		// Use the previous program
		Program.use(gl, p);
	}

	@Override
	public Object getYamlObjectRepresentation() {
		// TODO Auto-generated method stub
		Map<Object, Object> result = new HashMap<Object,Object>();
		result.put("type", "TextureMaterial");

		result.put("ambient", convertFloatArrayToList(ambient));
		result.put("diffuse", convertFloatArrayToList(diffuse));
		result.put("specular", convertFloatArrayToList(specular));
		result.put("shininess", shininess);
		
		return result;
	}

}
