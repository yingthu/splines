package cs4620.scene;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class ProgramInfo {
	public Matrix4f un_Projection;
	public Matrix4f un_ModelView;
	
	public Vector3f un_AmbientColor;
	public Vector3f un_DiffuseColor;
	public Vector3f un_SpecularColor;
	public float un_Shininess;
	
	public Vector3f [] un_LightPositions;
	public Vector3f [] un_LightIntensities;
	public Vector3f un_LightAmbientIntensity;
}
