package cs4620.scene;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4620.framework.GlslException;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.material.Material;

public class SceneProgram extends Program {
	
	/**
	 * A shader program for drawing meshes in a scene. The program
	 * accepts vertex positions and normal vectors, a single color
	 * for the object, projection and modelview matrices, and
	 * information about the light sources in the scene. It additionally
	 * derives a normal matrix from the provided transformation.
	 */
	
	// The positions in the array of vertex attributes where the shader
	// program expects to see vertex positions and normal data, respectively
	public static final int VERTEX_INDEX = 0;
	public static final int NORMAL_INDEX = 1;
	public static final int TEXTURE_INDEX = 2;
	
	// The names of the uniform variables corresponding to the two positions
	// in the array of vertex attributes.
	public static final String VERTEX_STRING = "in_Vertex";                   // vec3
	public static final String NORMAL_STRING = "in_Normal";                   // vec3
	public static final String TEXTURE_STRING = "in_TexCoord";                // vec2
	
	// Uniform names. Again, programs do not need to have all of these;
	// non-existent ones will be silently ignored.
	public static final String PROJECTION_UNIFORM      = "un_Projection";     // mat4
	public static final String MODELVIEW_UNIFORM       = "un_ModelView";      // mat4
	public static final String NORMAL_MATRIX_UNIFORM   = "un_NormalMatrix";   // mat3
	
	public static final String AMBIENT_COLOR_UNIFORM   = "un_AmbientColor";   // vec3
	public static final String DIFFUSE_COLOR_UNIFORM   = "un_DiffuseColor";   // vec3
	public static final String SPECULAR_COLOR_UNIFORM  = "un_SpecularColor";  // vec3
	public static final String SHININESS_UNIFORM       = "un_Shininess";      // float
	
	public static final int NUM_LIGHTS = 16;
	public static final String LIGHT_POSITIONS_UNIFORM = "un_LightPositions"; // vec3 * NUM_LIGHTS
	public static final String LIGHT_INTENSITIES_UNIFORM = "un_LightIntensities"; // vec3 * NUM_LIGHTS
	public static final String LIGHT_AMBIENT_INTENSITY_UNIFORM = "un_LightAmbientIntensity"; // vec3
	
	/**
	 * Returns the map of attribute indices to attribute variables that is expected
	 * by shaders written for this class.
	 */
	public static HashMap<Integer, String> getAttributeMap() {
		HashMap<Integer, String> attributeMap = new HashMap<Integer, String>();
		
		attributeMap.put(VERTEX_INDEX, VERTEX_STRING);
		attributeMap.put(NORMAL_INDEX, NORMAL_STRING);
		attributeMap.put(TEXTURE_INDEX, TEXTURE_STRING);
		
		return attributeMap;
	}

	public SceneProgram(GL2 glContext, String vertexSrcFile,
			String fragmentSrcFile) throws GlslException {
		super(glContext, vertexSrcFile, fragmentSrcFile, getAttributeMap());
	}
	
	/**
	 * Should only be invoked by subclasses that want to supply an extended attribute map.
	 */
	public SceneProgram(GL2 glContext, String vertexSrcFile, 
			String fragmentSrcFile, Map<Integer, String> attributeMap) throws GlslException {
		super(glContext, vertexSrcFile, fragmentSrcFile, attributeMap);
	}
	
	// setters for expected uniforms, to avoid annoyance
	// of program.getUniform("un_SomeLongName").setSomeType(someOtherName)
	// and of having to query for whether specific shader uses this or that
	// uniform in practice.
	
	public void setProjection(GL2 gl, Matrix4f projection)
	{
		if(hasUniform(PROJECTION_UNIFORM))
		{
			getUniform(PROJECTION_UNIFORM).setMatrix4(gl, projection);
		}
	}
	
	public void setModelView(GL2 gl, Matrix4f modelview)
	{
		if(hasUniform(MODELVIEW_UNIFORM))
		{
			getUniform(MODELVIEW_UNIFORM).setMatrix4(gl, modelview);
		}
		if(hasUniform(NORMAL_MATRIX_UNIFORM))
		{
			// calculate normal matrix (inverse transpose of upper 3x3 of modelview)
			Matrix3f normalMatrix = new Matrix3f(
					modelview.m00, modelview.m01, modelview.m02,
					modelview.m10, modelview.m11, modelview.m12,
					modelview.m20, modelview.m21, modelview.m22
					);
			normalMatrix.transpose();
			try {
				normalMatrix.invert();
			}
			catch (SingularMatrixException e)
			{
				normalMatrix = Transforms.identity3D();
			}
			getUniform(NORMAL_MATRIX_UNIFORM).setMatrix3(gl, normalMatrix);
		}
	}
	
//	public void setMaterial(GL2 gl, Material material)
//	{
//		material.applyTo(gl, this);
//	}
	
	public void setAmbientColor(GL2 gl, Vector3f ambientColor)
	{
		if(hasUniform(AMBIENT_COLOR_UNIFORM))
		{
			getUniform(AMBIENT_COLOR_UNIFORM).setVector3(gl, ambientColor);
		}
	}
	
	public void setDiffuseColor(GL2 gl, Vector3f diffuseColor)
	{
		if(hasUniform(DIFFUSE_COLOR_UNIFORM))
		{
			getUniform(DIFFUSE_COLOR_UNIFORM).setVector3(gl, diffuseColor);
		}
	}
	
	public void setSpecularColor(GL2 gl, Vector3f specularColor)
	{
		if(hasUniform(SPECULAR_COLOR_UNIFORM))
		{
			getUniform(SPECULAR_COLOR_UNIFORM).setVector3(gl, specularColor);
		}
	}
	
	public void setShininess(GL2 gl, float shininess)
	{
		if(hasUniform(SHININESS_UNIFORM))
		{
			getUniform(SHININESS_UNIFORM).set1Float(gl, shininess);
		}
	}
	
	public void setLightPositions(GL2 gl, Vector3f [] positions)
	{
		if(positions.length != NUM_LIGHTS)
		{
			System.err.println("WARNING: attempted to set " + positions.length + " light positions when " + NUM_LIGHTS + " desired.");
			return;
		}
		if(hasUniform(LIGHT_POSITIONS_UNIFORM))
		{
			getUniform(LIGHT_POSITIONS_UNIFORM).setVector3Array(gl, positions);
		}
	}
	
	public void setLightIntensities(GL2 gl, Vector3f [] intensities)
	{
		if(intensities.length != NUM_LIGHTS)
		{
			System.err.println("WARNING: attempted to set " + intensities.length + " light colors when " + NUM_LIGHTS + " desired.");
			return;
		}
		if(hasUniform(LIGHT_INTENSITIES_UNIFORM))
		{
			getUniform(LIGHT_INTENSITIES_UNIFORM).setVector3Array(gl, intensities);
		}
	}
	
	public void setLightAmbientIntensity(GL2 gl, Vector3f lightAmbientIntensity)
	{
		if(hasUniform(LIGHT_AMBIENT_INTENSITY_UNIFORM))
		{
			getUniform(LIGHT_AMBIENT_INTENSITY_UNIFORM).setVector3(gl, lightAmbientIntensity);
		}
	}
	
	public void setAllInfo(GL2 gl, ProgramInfo info)
	{
		if(info.un_Projection != null)
		{
			setProjection(gl, info.un_Projection);
		}
		
		if(info.un_ModelView != null)
		{
			setModelView(gl, info.un_ModelView);
		}
		
		if(info.un_AmbientColor != null)
		{
			setAmbientColor(gl, info.un_AmbientColor);
		}
		
		if(info.un_DiffuseColor != null)
		{
			setDiffuseColor(gl, info.un_DiffuseColor);
		}
		
		if(info.un_SpecularColor != null)
		{
			setSpecularColor(gl, info.un_SpecularColor);
		}
		
		setShininess(gl, info.un_Shininess);
		
		if(info.un_LightPositions != null)
		{
			setLightPositions(gl, info.un_LightPositions);
		}
		
		if(info.un_LightIntensities != null)
		{
			setLightIntensities(gl, info.un_LightIntensities);
		}
		
		if(info.un_LightAmbientIntensity != null)
		{
			setLightAmbientIntensity(gl, info.un_LightAmbientIntensity);
		}
	}

}
