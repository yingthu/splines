package cs4620.shape;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

public class Sphere extends TriangleMesh {

	public Sphere(GL2 gl) {
		super(gl);
	}


	@Override
	public void buildMesh(GL2 gl, float tolerance)
	{
		// TODO (Scene P2): Implement mesh generation for Sphere. Your code should
		// fill arrays of vertex positions/normals and vertex indices for triangles/lines
		// and put this information in the GL buffers using the
		//   set*()
		// methods from TriangleMesh.
		int latitudes = (int) Math.ceil(Math.PI / (tolerance/2.0));
        int longitudes = (int) Math.ceil(2.0*Math.PI / (tolerance/2.0));

        // Create all the vertex data
        int size = (latitudes + 1) * (longitudes + 1);

        float[] vertices = new float[3*size];
        float[] normals = new float[3*size];
        float[] texCoords = new float[2*size];

        // Create vertices
        // Duplicate the poles segments-many times for simplicity.  
        int pos = 0;
        for (int lat = 0; lat <= latitudes; lat++) {
        	// Must duplicate the vertices on the wrapped edge to get the textures to come out right
            for (int longi = 0; longi <= longitudes; longi++) {
                		
            	// Radius = 1.0
                float x = (float) Math.cos(longi * 2 * Math.PI / longitudes) * (float) Math.sin(lat * Math.PI / latitudes);
                float y = (float) Math.cos(lat * Math.PI / latitudes);
                float z = (float) Math.sin(longi * 2 * Math.PI / longitudes) * (float) Math.sin(lat * Math.PI / latitudes);

                vertices[3*pos] = x;
                vertices[3*pos+1] = y;
                vertices[3*pos+2] = z;
                
                normals[3*pos] = x;
                normals[3*pos+1] = y;
                normals[3*pos+2] = z;
                
                texCoords[2*pos] = 1.0f - (longi / ((float) longitudes));
                texCoords[2*pos+1] = 1.0f - (lat / ((float) latitudes));
                pos++;
            }
        }

        // Set triangles
        int tris = 0;
        size = 2 * longitudes * latitudes;
        int[] triangles = new int[3*size];
        int topL, topR, botL, botR;
        for (int j = 0; j < latitudes; j++) {
        	for (int i = 0; i < longitudes; i++) {
        		topL = j * (longitudes + 1) + i;
                topR = topL + 1;
                botL = (j + 1) * (longitudes + 1) + i;
                botR = botL + 1;

                triangles[3*tris] = topL;
                triangles[3*tris+1] = topR;
                triangles[3*tris+2] = botL;
                tris++;
                triangles[3*tris] = botL;
                triangles[3*tris+1] = topR;
                triangles[3*tris+2] = botR;
                tris++;
        	}
        }
        
        // Set lines
        int lineCount = 2 * latitudes * longitudes;
 		int[] lines = new int[lineCount*2];
 		int toLong = 2 * latitudes * longitudes;
 		
        for (int j = 0; j < latitudes; j++) {
        	for (int i = 0; i < longitudes; i++) {
                topL = j * (longitudes + 1) + i;
                topR = topL + 1;
                botL = (j + 1) * (longitudes + 1) + i;
                lines[2*(i*latitudes+j)+0] = topL;
                lines[2*(i*latitudes+j)+1] = botL;
                lines[toLong+2*(j*longitudes+i)+0] = topL;
                lines[toLong+2*(j*longitudes+i)+1] = topR;
        	}
        }	
        setVertices(gl, vertices);
		setNormals(gl, normals);
		setTriangleIndices(gl, triangles);
		setWireframeIndices(gl, lines);
		// TODO (Shaders 2 P2): Generate texture coordinates for the sphere also.
		setTexCoords(gl, texCoords);
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Sphere");
		return result;
	}


}
