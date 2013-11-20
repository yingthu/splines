package cs4620.manip;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs4620.framework.Transforms;
import cs4620.manip.ManipUtils;
import cs4620.material.Material;
import cs4620.material.PhongMaterial;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneNode;
import cs4620.scene.SceneProgram;
import cs4620.shape.Cube;
import cs4620.shape.Torus;

public class RotateManip extends Manip
{
	
	protected Torus torus;
	PhongMaterial xMaterial;
	PhongMaterial yMaterial;
	PhongMaterial zMaterial;
	boolean resourcesInitialized = false;
	
	private void initResourcesGL(GL2 gl)
	{
		if (!resourcesInitialized)
		{
			torus = new Torus(gl, 0.05f, 1.0f);
			torus.buildMesh(gl, 0.133f);
			
			xMaterial = new PhongMaterial();
			xMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			xMaterial.setDiffuse(0.8f, 0.0f, 0.0f);
			xMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			
			yMaterial = new PhongMaterial();
			yMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			yMaterial.setDiffuse(0.0f, 0.8f, 0.0f);
			yMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			
			zMaterial = new PhongMaterial();
			zMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			zMaterial.setDiffuse(0.0f, 0.0f, 0.8f);
			zMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			resourcesInitialized = true;
		}
	}
	
	@Override
	public void dragged(Vector2f mousePosition, Vector2f mouseDelta)
	{
		// TODO (Manipulators P1): Implement this manipulator.
		float deltaY = mouseDelta.y;
		Vector3f rotation = sceneNode.rotation;
		// Choose 90 here to get a proper amount of rotation
		if (axisMode == PICK_X)
			rotation.x += 90.f * deltaY;
		else if (axisMode == PICK_Y)
			rotation.y += 90.f * deltaY;
		else if (axisMode == PICK_Z)
			rotation.z += 90.f * deltaY;
	}

	@Override
	public void glRender(GL2 gl, SceneProgram program, Matrix4f modelView, double scale)
	{
		
		// NOTE: the ring in the rotation manipulator for one of the three rotations can be set
		// 1. to be orthogonal to the axis that is preserved under the corresponding rotation
		// or
		// 2. to be parallel to the plane that is preserved under the corresponding rotation.
		// If the frame the node's parent maps to in world space is orthonormal, these are the same thing.
		// In more complicated cases, they are not, so you have to pick one property to preserve.
		// Both versions are available below; the code that differs is labeled.
		
		initResourcesGL(gl);
		
		// get transformation from parent's frame to eye
		SceneNode parent = sceneNode.getSceneNodeParent();
		Matrix4f parentModelView;
		if (parent != null)
		{
			parentModelView = parent.toEye(modelView);
		}
		else
		{
			parentModelView = new Matrix4f(modelView);
		}
		
		// translation taking origin of rotations to eye
		Matrix4f translateToEye = Transforms.copyTranslate3DH(parentModelView);
		Vector3f eyeNodeTranslation = new Vector3f(sceneNode.translation);
		ManipUtils.transformVector(parentModelView, eyeNodeTranslation);
		translateToEye.mul(Transforms.translate3DH(eyeNodeTranslation));
		
		// scale to be applied to all rings
		float ringRadius = 2.0f;
		Matrix4f scaleRing = Transforms.scale3DH((float) scale * ringRadius);
		
		//Vector3f xBasis = new Vector3f();                                                // parallel to plane
		//Vector3f yBasis = new Vector3f();                                                // parallel to plane
		//Vector3f zBasis = new Vector3f();                                                // parallel to plane
		
		Vector3f axisVector = new Vector3f();
		Matrix4f nextModelView = new Matrix4f();
		
		// z axis of parent's to-eye transform is axis that R_z of sceneNode rotates around
		//Transforms.toColumns3DH(parentModelView, xBasis, yBasis, zBasis);                // parallel to plane
		//axisVector.cross(xBasis, yBasis);                                                // parallel to plane
		axisVector.set(parentModelView.m02, parentModelView.m12, parentModelView.m22); // ortho. to axis
		nextModelView.set(translateToEye);
		nextModelView.mul(ManipUtils.rotateZTo(axisVector));
		nextModelView.mul(scaleRing);
		setIdIfPicking(gl, program, Manip.PICK_Z);
		glRenderRing(gl, program, nextModelView, 2);
		
		// modify parentModelView to include effect of z rotation
		parentModelView.mul(Transforms.rotateAxis3DH(2, sceneNode.rotation.z));
		
		// y axis of parent's to-eye transform is axis that R_y of sceneNode rotates around
		//Transforms.toColumns3DH(parentModelView, xBasis, yBasis, zBasis);                // parallel to plane
		//axisVector.cross(zBasis, xBasis);                                                // parallel to plane
		axisVector.set(parentModelView.m01, parentModelView.m11, parentModelView.m21); // ortho. to axis
		nextModelView.set(translateToEye);
		nextModelView.mul(ManipUtils.rotateZTo(axisVector));
		nextModelView.mul(scaleRing);
		setIdIfPicking(gl, program, Manip.PICK_Y);
		glRenderRing(gl, program, nextModelView, 1);
		
		// modify parentModelView to include effect of y rotation
		parentModelView.mul(Transforms.rotateAxis3DH(1, sceneNode.rotation.y));
		
		// x axis of parent's to-eye transform is axis that R_x of sceneNode rotates around
		//Transforms.toColumns3DH(parentModelView, xBasis, yBasis, zBasis);                // parallel to plane
		//axisVector.cross(yBasis, zBasis);                                                // parallel to plane
		axisVector.set(parentModelView.m00, parentModelView.m10, parentModelView.m20); // ortho. to axis
		nextModelView.set(translateToEye);
		nextModelView.mul(ManipUtils.rotateZTo(axisVector));
		nextModelView.mul(scaleRing);
		setIdIfPicking(gl, program, Manip.PICK_X);
		glRenderRing(gl, program, nextModelView, 0);
	}
	
	protected void glRenderRing(GL2 gl, SceneProgram program, Matrix4f modelView, int axis) {
		Material axisMaterial = zMaterial;
		if (axis == 0) // x
		{
			axisMaterial = xMaterial;
		}
		else if (axis == 1) // y
		{
			axisMaterial = yMaterial;
		}
		
		//program.setMaterial(gl, axisMaterial);
		program.setModelView(gl, modelView);
		axisMaterial.drawUsingProgram(gl, program, torus, false);
		//torus.draw(gl);
	}
}
