package cs4620.shape;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

public class Cylinder extends TriangleMesh
{
	public Cylinder(GL2 gl)
	{
		super(gl);
	}
	
	@Override
	public void buildMesh(GL2 gl, float tolerance)
	{
		// TODO (Scene P2): Implement mesh generation for Cylinder. Your code should
		// fill arrays of vertex positions/normals and vertex indices for triangles/lines
		// and put this information in the GL buffers using the
		//   set*()
		// methods from TriangleMesh.
		int num = (int) Math.floor(2.0*Math.PI / (tolerance/2.0));
		double dtheta = 2 * Math.PI / num;
		double theta = 0;
		float[] x = new float[num];
		float[] z = new float[num];
		for (int i = 0; i < num; i++) {
		// Adjust theta to -theta here to fit the axis
			x[i] = (float) (1.0 * Math.cos(-theta));
			z[i] = (float) (1.0 * Math.sin(-theta));
			theta += dtheta;
		}
		float[] vertices = new float[3 * (6*num+4)];
		float[] normals = new float[3 * (6*num+4)];
		float[] texCoords = new float[2 * (6*num+4)];
		int index, tex_index;
		for (int i = 0; i < num; i++) {
			// top center
			index = 3 * i;
			
			vertices[index] = 0.0f;
			vertices[index + 1] = 1.0f;
			vertices[index + 2] = 0.0f;
			normals[index] = 0.0f;
			normals[index + 1] = 1.0f;
			normals[index + 2] = 0.0f;
			
			tex_index = 2 * i;
			
			texCoords[tex_index] = 0.5f;
			texCoords[tex_index+1] = 0.5f;
			// top round
			index = 3 * i + 3 * num;
			vertices[index] = x[i];
			vertices[index + 1] = 1.0f;
			vertices[index + 2] = z[i];
			normals[index] = 0.0f;
			normals[index + 1] = 1.0f;
			normals[index + 2] = 0.0f;
			
			tex_index = 2 * i + 2 * num;
			
			texCoords[tex_index] = (float) (x[i]/2.0+0.5);
			texCoords[tex_index+1] = (float) (z[i]/2.0+0.5);
			// side top
			index = 3 * i + (3 * num) * 2 + 1 * 3;
			vertices[index] = x[i];
			vertices[index + 1] = 1.0f;
			vertices[index + 2] = z[i];
			normals[index] = x[i];
			normals[index + 1] = 0.0f;
			normals[index + 2] = z[i];
			
			tex_index = 2 * i + (2 * num) * 2 + 1 * 2;
			
			texCoords[tex_index] = (float) (i*1.0/num);
			texCoords[tex_index+1] = 1.0f;
			
			// side bottom
			index = 3 * i + (3 * num) * 3 + 2 * 3;
			vertices[index] = x[i];
			vertices[index + 1] = -1.0f;
			vertices[index + 2] = z[i];
			normals[index] = x[i];
			normals[index + 1] = 0.0f;
			normals[index + 2] = z[i];
			
			tex_index = 2 * i + (2 * num) * 3 + 2 * 2;
			
			texCoords[tex_index] = (float) (i*1.0/num);
			texCoords[tex_index+1] = 0.0f;
			
			// bottom round
			index = 3 * i + (3 * num) * 4 + 3 * 3;
			vertices[index] = x[i];
			vertices[index + 1] = -1.0f;
			vertices[index + 2] = z[i];
			normals[index] = 0.0f;
			normals[index + 1] = -1.0f;
			normals[index + 2] = 0.0f;
			
			tex_index = 2 * i + (2 * num) * 4 + 3 * 2;
			
			texCoords[tex_index] = /*1.0f -*/ (float) (-x[i]/2.0+0.5);
			texCoords[tex_index+1] = /*1.0f -*/ (float) (-z[i]/2.0+0.5);
			
			// bottom center
			index = 3 * i + (3 * num) * 5 + 4 * 3;
			vertices[index] = 0.0f;
			vertices[index + 1] = -1.0f;
			vertices[index + 2] = 0.0f;
			normals[index] = 0.0f;
			normals[index + 1] = -1.0f;
			normals[index + 2] = 0.0f;
			
			tex_index = 2 * i + (2 * num) * 5 + 4 * 2;
			texCoords[tex_index] = 0.5f;
			texCoords[tex_index+1] = 0.5f;
		}
		// top round add
		index = 3 * num + 3 * num;
		vertices[index] = x[0];
		vertices[index + 1] = 1.0f;
		vertices[index + 2] = z[0];
		normals[index] = 0.0f;
		normals[index + 1] = 1.0f;
		normals[index + 2] = 0.0f;
		tex_index = 2 * num + 2 * num;
		texCoords[tex_index] = (float) (x[0]/2.0+0.5);
		texCoords[tex_index+1] = (float) (z[0]/2.0+0.5);
		// side top add
		index = 3 * num + (3 * num) * 2 + 1 * 3;
		vertices[index] = x[0];
		vertices[index + 1] = 1.0f;
		vertices[index + 2] = z[0];
		normals[index] = x[0];
		normals[index + 1] = 0.0f;
		normals[index + 2] = z[0];
		tex_index = 2 * num + (2 * num) * 2 + 1 * 2;
		texCoords[tex_index] = 1.0f;
		texCoords[tex_index+1] = 1.0f;
		// side bottom add
		index = 3 * num + (3 * num) * 3 + 2 * 3;
		vertices[index] = x[0];
		vertices[index + 1] = -1.0f;
		vertices[index + 2] = z[0];
		normals[index] = x[0];
		normals[index + 1] = 0.0f;
		normals[index + 2] = z[0];
		tex_index = 2 * num + (2 * num) * 3 + 2 * 2;
		texCoords[tex_index] = 1.0f;
		texCoords[tex_index+1] = 0.0f;
		// bottom round add
		index = 3 * num + (3 * num) * 4 + 3 * 3;
		vertices[index] = x[0];
		vertices[index + 1] = -1.0f;
		vertices[index + 2] = z[0];
		normals[index] = 0.0f;
		normals[index + 1] = -1.0f;
		normals[index + 2] = 0.0f;
		tex_index = 2 * num + (2 * num) * 4 + 3 * 2;
		texCoords[tex_index] = (float) (-x[0]/2.0+0.5);
		texCoords[tex_index+1] = (float) (-z[0]/2.0+0.5);
		int[] triangles = new int[3 * 8 * num];
		for (int i = 0; i < num; i++)
		{
			// top center to top round
			index = 3 * i;
			triangles[index] = i;
			triangles[index + 1] = num + i;
			triangles[index + 2] = num + i + 1;
			// top round to side top
			index = 3 * (num + i);
			triangles[index] = num + i;
			triangles[index + 1] = 2 * num + i + 1;
			triangles[index + 2] = 2 * num + i + 2;
			// side top to side bottom
			index = 3 * (2 * num + 2 * i + 1);
			triangles[index] = 2 * num + i + 1;
			triangles[index + 1] = 3 * num + i + 2;
			triangles[index + 2] = 3 * num + i + 3;
			triangles[index + 3] = 3 * num + i + 3;
			triangles[index + 4] = 2 * num + i + 2;
			triangles[index + 5] = 2 * num + i + 1;
			// side bottom to bottom round
			index = 3 * (3 * num + i + 2);
			triangles[index] = 3 * num + i + 2;
			triangles[index + 1] = 4 * num + i + 3;
			triangles[index + 2] = 4 * num + i + 4;
			// bottom round to bottom center
			index = 3 * (4 * num + i + 3);
			triangles[index] = 4 * num + i + 3;
			triangles[index] = 5 * num + i + 4;
			triangles[index] = 4 * num + i + 4;
		}
		int[] lines = new int[2 * (9 * num + 5)];
		for (int i = 0; i < num; i++)
		{
			// top center to top round, left
			lines[i*2] = i;
			lines[i*2 + 1] = num + i;
			// top center to top round, right
			lines[2*num+i*2] = i;
			lines[2*num+i*2+1] = num + i + 1;
			// bottom center to bottom round, left
			lines[14*num+10+i*2] = 4*num+3 + i;
			lines[14*num+10+i*2+1] = 5*num+4 + i;
			// bottom center to bottom round, right
			lines[16*num+10+i*2] = 4*num+3 + i + 1;
			lines[16*num+10+i*2+1] = 5*num+4 + i;
		}
		for (int i = 0; i <= num; i++)
		{
			// top round to top round
			lines[4*num+i*2] = num + i;
			lines[4*num+i*2+1] = num + (i+1)%(num+1);
			// side top to side top
			lines[6*num+2+i*2] = 2*num+1 + i;
			lines[6*num+2+i*2+1] = 2*num+1 + (i+1)%(num+1);
			// side top to side bottom
			lines[8*num+4+i*2] = 2*num+1 + i;
			lines[8*num+4+i
			      *2+1] = 3*num+2 + i;
			// side bottom to side bottom
			lines[10*num+6+i*2] = 3*num+2 + i;
			lines[10*num+6+i*2+1] = 3*num+2 + (i+1)%(num+1);
			// bottom round to bottom round
			lines[12*num+8+i*2] = 4*num+3 + i;
			lines[12*num+8+i*2+1] = 4*num+3 + (i+1)%(num+1);
		}
		setVertices(gl, vertices);
		setNormals(gl, normals);
		setTriangleIndices(gl, triangles);
		setWireframeIndices(gl, lines);
		// TODO (Shaders 2 P2): Generate texture coordinates for the cylinder also
		setTexCoords(gl, texCoords);
	}
	

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Cylinder");
		return result;
	}
}
