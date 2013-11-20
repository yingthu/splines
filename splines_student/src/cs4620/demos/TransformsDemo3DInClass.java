package cs4620.demos;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;

import cs4620.framework.Program;
import cs4620.scene.SceneProgram;

public class TransformsDemo3DInClass extends TransformsDemo3D {
	private static final long serialVersionUID = 5828539287043595478L;

	public TransformsDemo3DInClass()
	{
		super();
	}

	@Override
	public void draw(GL2 gl, SceneProgram program, Matrix4f transform)
	{
		Program.use(gl, program);
		
		drawAxes(gl, program, transform);
		
		transform.mul(getTranslate("T1"));
		transform.mul(getScale("S1"));
		transform.mul(getRotate("R1"));
		program.setModelView(gl, transform);
		
		//drawMesh(gl, sphereMesh);
		//drawMesh(gl, cubeMesh);
		drawMesh(gl, teapotMesh, material, program);
		
		Program.unuse(gl);
	}
	
	public static void main(String [] args)
	{
		new TransformsDemo3DInClass().run();
	}
}
