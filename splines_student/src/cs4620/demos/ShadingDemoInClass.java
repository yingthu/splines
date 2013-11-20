package cs4620.demos;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import cs4620.framework.Program;
import cs4620.scene.SceneProgram;

public class ShadingDemoInClass extends ShadingDemo {
	private static final long serialVersionUID = 8345601226363653471L;

	@Override
	public void draw(GL2 gl, SceneProgram selectedProgram, Matrix4f view)
	{
		// selectedProgram is program chosen in combo box
		Program.use(gl, selectedProgram);

		selectedProgram.setModelView(gl, view);
		
		Vector3f ambientColor = new Vector3f(0.02f, 0.02f, 0.02f);
		Vector3f diffuseColor = new Vector3f(0.2f, 0.4f, 0.8f);
		Vector3f specularColor = new Vector3f(1.0f, 1.0f, 1.0f);
		float shininess = 70.0f;
		
		selectedProgram.setAmbientColor(gl, ambientColor);
		selectedProgram.setDiffuseColor(gl, diffuseColor);
		selectedProgram.setSpecularColor(gl, specularColor);
		selectedProgram.setShininess(gl, shininess);

		drawMesh(gl, teapotMesh);
		
		Program.unuse(gl);
	}

	
	public static void main(String[] args)
	{
		new ShadingDemoInClass().run();
	}
	

}
