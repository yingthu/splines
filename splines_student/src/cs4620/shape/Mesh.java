package cs4620.shape;

import java.util.ArrayList;
import java.util.Map;

import javax.media.opengl.GL2;

public abstract class Mesh {
	private static ArrayList<Mesh> meshes = new ArrayList<Mesh>();
	private int id;

	public Mesh()
	{
		id = meshes.size() + 1024;
		meshes.add(this);
	}
	
	public Mesh(GL2 gl)
	{
		id = meshes.size() + 1024;
		meshes.add(this);
	}

	public int getId() {
		return id;
	}

	public abstract void draw(GL2 gl);
	
	public abstract void drawWireframe(GL2 gl);

	public abstract void buildMesh(GL2 gl, float tolerance);

	public abstract Object getYamlObjectRepresentation();

	public static Mesh fromYamlObject(GL2 gl, Object yamlObject)
	{
		if(gl == null)
			throw new RuntimeException("parsing XML before Mesh has received gl");
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> meshMap = (Map<?, ?>)yamlObject;

		if (meshMap.get("type").equals("Sphere"))
			return new Sphere(gl);
		else if (meshMap.get("type").equals("Cube"))
			return new Cube(gl);
		else if (meshMap.get("type").equals("Cylinder"))
			return new Cylinder(gl);
		else if (meshMap.get("type").equals("Torus"))
			return new Torus(gl);
		else if (meshMap.get("type").equals("Teapot"))
		{
			Teapot teapot = null;
			try {
				teapot = new Teapot(gl);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			return teapot;
		}
		else if (meshMap.get("type").equals("Spline"))
		{
			return Spline.fromYamlObject(gl, yamlObject);
		}
		else if (meshMap.get("type").equals("RevMesh"))
		{
			return RevMesh.fromYamlObject(gl, yamlObject);
		}
		else
			throw new RuntimeException("invalid mesh type");
	}

}
