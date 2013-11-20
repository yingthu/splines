package cs4620.scene;

import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;

import cs4620.material.DiffuseMaterial;
import cs4620.material.Material;
import cs4620.material.PhongMaterial;
import cs4620.shape.Mesh;
import cs4620.shape.Sphere;
import cs4620.util.Util;
import cs4620.material.CometMaterial;
import cs4620.material.EarthMaterial;
import cs4620.material.FireMaterial;
import cs4620.material.GreenMaterial;
import cs4620.material.MarsMaterial;
import cs4620.material.MoonMaterial;
import cs4620.material.NormalMaterial;
import cs4620.material.TextureMaterial;
import cs4620.material.ToonMaterial;
import cs4620.material.TexCoordMaterial;

public class MeshNode extends SceneNode
{
	private static final long	serialVersionUID	= 1L;

	private Mesh mesh;
	private Material material;

	/**
	 * Required for I/O
	 */
	public MeshNode()
	{
		this("", null, null);
	}

	public MeshNode(GL2 gl, String name)
	{
		this(name, new Sphere(gl), new DiffuseMaterial());
	}

	public MeshNode(String name, Mesh mesh)
	{
		this(name, mesh, new DiffuseMaterial());
	}

	public MeshNode(String name, Mesh mesh, Material material)
	{
		super(name);
		this.mesh = mesh;
		this.material = material;
	}

	public Mesh getMesh()
	{
		return mesh;
	}

	public void setMesh(Mesh mesh)
	{
		this.mesh = mesh;
	}

	public Material getMaterial()
	{
		return material;
	}
	
	public void draw(GL2 gl, ProgramInfo info)
	{
		//getMaterial().applyTo(gl, program);
		getMaterial().draw(gl, info, getMesh(), false);
	}
	
	public void drawWireframe(GL2 gl, ProgramInfo info)
	{
		getMaterial().draw(gl, info, getMesh(), true);
	}
	
	public void drawUsingProgram(GL2 gl, SceneProgram program, Matrix4f modelView)
	{
		program.setModelView(gl, modelView);
		getMaterial().drawUsingProgram(gl, program, getMesh(), false);
	}
	
	public void drawWireframeUsingProgram(GL2 gl, SceneProgram program, Matrix4f modelView)
	{
		program.setModelView(gl, modelView);
		getMaterial().drawUsingProgram(gl, program, getMesh(), true);
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<String, Object> result = (Map<String, Object>)super.getYamlObjectRepresentation();
		result.put("type", "MeshNode");
		result.put("mesh", mesh.getYamlObjectRepresentation());
		result.put("material", material.getYamlObjectRepresentation());
		return result;
	}

	public void extractMeshFromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;

		mesh = Mesh.fromYamlObject(gl, yamlMap.get("mesh"));
	}

	public void extractMaterialFromYamlObject(Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;

		if (!(yamlMap.get("material") instanceof Map))
			throw new RuntimeException("material field not a Map");
		Map<?, ?> materialMap = (Map<?, ?>)yamlMap.get("material");

		if (materialMap.get("type").equals("PhongMaterial"))
		{
			PhongMaterial glMaterial = new PhongMaterial();
			Util.assign4ElementArrayFromYamlObject(glMaterial.ambient, materialMap.get("ambient"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.diffuse, materialMap.get("diffuse"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.specular, materialMap.get("specular"));
			glMaterial.shininess = Float.valueOf(materialMap.get("shininess").toString());

			material = glMaterial;
		}
		else if (materialMap.get("type").equals("DiffuseMaterial"))
		{
			DiffuseMaterial glMaterial = new DiffuseMaterial();
			Util.assign4ElementArrayFromYamlObject(glMaterial.ambient, materialMap.get("ambient"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.diffuse, materialMap.get("diffuse"));

			material = glMaterial;
		}
		else if (materialMap.get("type").equals("NormalMaterial"))
		{
			NormalMaterial glMaterial = new NormalMaterial();
			
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("GreenMaterial"))
		{
			GreenMaterial glMaterial = new GreenMaterial();
			
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("TextureMaterial"))
		{
			TextureMaterial glMaterial = new TextureMaterial();
			Util.assign4ElementArrayFromYamlObject(glMaterial.ambient, materialMap.get("ambient"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.diffuse, materialMap.get("diffuse"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.specular, materialMap.get("specular"));
			glMaterial.shininess = Float.valueOf(materialMap.get("shininess").toString());
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("ToonMaterial"))
		{
			ToonMaterial glMaterial = new ToonMaterial();
			
			Util.assign4ElementArrayFromYamlObject(glMaterial.ambient, materialMap.get("ambient"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.diffuse, materialMap.get("diffuse"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.specular, materialMap.get("specular"));
			glMaterial.shininess = Float.valueOf(materialMap.get("shininess").toString());
			glMaterial.displaceScale = Float.valueOf(materialMap.get("displaceScale").toString());
			
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("TextureCoordinateMaterial"))
		{
			TexCoordMaterial glMaterial = new TexCoordMaterial();
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("FireMaterial"))
		{
			FireMaterial glMaterial = new FireMaterial();
			Util.assign3ElementArrayFromYamlObject(glMaterial.scrollSpeeds, materialMap.get("scroll_speeds"));
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("MoonMaterial"))
		{
			MoonMaterial glMaterial = new MoonMaterial();
			Util.assign4ElementArrayFromYamlObject(glMaterial.ambient, materialMap.get("ambient"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.diffuse, materialMap.get("diffuse"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.specular, materialMap.get("specular"));
			glMaterial.shininess = Float.valueOf(materialMap.get("shininess").toString());
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("EarthMaterial"))
		{
			EarthMaterial glMaterial = new EarthMaterial();
			Util.assign4ElementArrayFromYamlObject(glMaterial.ambient, materialMap.get("ambient"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.diffuse, materialMap.get("diffuse"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.specular, materialMap.get("specular"));
			glMaterial.shininess = Float.valueOf(materialMap.get("shininess").toString());
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("MarsMaterial"))
		{
			MarsMaterial glMaterial = new MarsMaterial();
			Util.assign4ElementArrayFromYamlObject(glMaterial.ambient, materialMap.get("ambient"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.diffuse, materialMap.get("diffuse"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.specular, materialMap.get("specular"));
			glMaterial.shininess = Float.valueOf(materialMap.get("shininess").toString());
			material = glMaterial;
		}
		else if (materialMap.get("type").equals("CometMaterial"))
		{
			CometMaterial glMaterial = new CometMaterial();
			Util.assign4ElementArrayFromYamlObject(glMaterial.ambient, materialMap.get("ambient"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.diffuse, materialMap.get("diffuse"));
			Util.assign4ElementArrayFromYamlObject(glMaterial.specular, materialMap.get("specular"));
			glMaterial.shininess = Float.valueOf(materialMap.get("shininess").toString());
			material = glMaterial;
		}
		else
		{
			throw new RuntimeException("Unsupported material: " + materialMap.get("type"));
		}

	}

	public static SceneNode fromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;

		MeshNode result = new MeshNode();
		result.setName((String)yamlMap.get("name"));
		result.extractTransformationFromYamlObject(yamlObject);
		result.addChildrenFromYamlObject(gl, yamlObject);
		result.extractMeshFromYamlObject(gl, yamlObject);
		result.extractMaterialFromYamlObject(yamlObject);

		return result;
	}
}
