package cs4620.manip;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs4620.scene.SceneNode;
import cs4620.scene.SceneProgram;
import cs4620.shape.Cube;
import cs4620.framework.PickingManager;
import cs4620.framework.Transforms;
import cs4620.manip.ManipUtils;
import cs4620.material.Material;
import cs4620.material.PhongMaterial;

public class ScaleManip extends Manip {
	private Vector3f xManipBasis = new Vector3f();
	private Vector3f yManipBasis = new Vector3f();
	private Vector3f zManipBasis = new Vector3f();
	private Vector3f manipOrigin = new Vector3f();
	
	Cube cube = null;
	PhongMaterial stickMaterial;
	PhongMaterial xMaterial;
	PhongMaterial yMaterial;
	PhongMaterial zMaterial;
	PhongMaterial centerMaterial;
	boolean resourcesInitialized = false;
	
	float stickRadius = 0.02f;
	float stickLength = 2.0f;
	float boxRadius = 0.1f;
	
	private void initResourcesGL(GL2 gl)
	{
		if (!resourcesInitialized)
		{
			cube = new Cube(gl);
			cube.buildMesh(gl, 1.0f);
			
			stickMaterial = new PhongMaterial();
			stickMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			stickMaterial.setDiffuse(1.0f, 1.0f, 1.0f);
			stickMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			
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
			
			centerMaterial = new PhongMaterial();
			centerMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			centerMaterial.setDiffuse(0.8f, 0.8f, 0.0f);
			centerMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			resourcesInitialized = true;
		}
	}

	private void initManipBasis()
	{
		Matrix4f toWorld = sceneNode.toWorld();
		
		// and apply this rotation, since scale happens before rotation
		xManipBasis.set(eX);
		yManipBasis.set(eY);
		zManipBasis.set(eZ);
		manipOrigin.set(e0);
		
		// origin can be transformed directly to world
		ManipUtils.transformPosition(toWorld, manipOrigin);
		
		// for axes, first account for this node's transform minus scaling
		ManipUtils.applyNodeRotation(sceneNode, xManipBasis);
		ManipUtils.applyNodeRotation(sceneNode, yManipBasis);
		ManipUtils.applyNodeRotation(sceneNode, zManipBasis);
		
		// then, apply parent-to-world if parent exists
		SceneNode parentNode = sceneNode.getSceneNodeParent();
		if (parentNode != null)
		{
			Matrix4f parentToWorld = parentNode.toWorld();
			
			ManipUtils.transformVector(parentToWorld, xManipBasis);
			ManipUtils.transformVector(parentToWorld, yManipBasis);
			ManipUtils.transformVector(parentToWorld, zManipBasis);
		}
		
		// normalize basis
		xManipBasis.normalize();
		yManipBasis.normalize();
		zManipBasis.normalize();
	}

	@Override
	public void dragged(Vector2f mousePosition, Vector2f mouseDelta)
	{
		// TODO (Manipulators P1): Implement this manipulator.
		// Get the previous mouse position
		Vector2f lastmousePosition = new Vector2f();
		lastmousePosition.sub(mousePosition, mouseDelta);
		// Generating the two eye rays
		Vector3f plastRay = new Vector3f();
		Vector3f vlastRay = new Vector3f();
		Vector3f pcurrRay = new Vector3f();
		Vector3f vcurrRay = new Vector3f();
		camera.getRayNDC(lastmousePosition, plastRay, vlastRay);
		camera.getRayNDC(mousePosition, pcurrRay, vcurrRay);
		
		Vector3f scaling = sceneNode.scaling;
		
		// Defining the axis;
		Vector3f p = new Vector3f();
		p.set(0.0f, 0.0f, 0.0f);
		Vector3f vX = new Vector3f();
		Vector3f vY = new Vector3f();
		Vector3f vZ = new Vector3f();
		vX.set(1.0f, 0.0f, 0.0f);
		vY.set(0.0f, 1.0f, 0.0f);
		vZ.set(0.0f, 0.0f, 1.0f);
		// X Dragged
		if (axisMode == PICK_X)
		{
			float t0 = ManipUtils.timeClosestToRay(p, vX, plastRay, vlastRay);
			float t1 = ManipUtils.timeClosestToRay(p, vX, pcurrRay, vcurrRay);
			scaling.x += t1 - t0;
		}
		// Y Dragged
		else if (axisMode == PICK_Y)
		{
			float t0 = ManipUtils.timeClosestToRay(p, vY, plastRay, vlastRay);
			float t1 = ManipUtils.timeClosestToRay(p, vY, pcurrRay, vcurrRay);
			scaling.y += t1 - t0;
		}
		// Z Dragged
		else if (axisMode == PICK_Z)
		{
			float t0 = ManipUtils.timeClosestToRay(p, vZ, plastRay, vlastRay);
			float t1 = ManipUtils.timeClosestToRay(p, vZ, pcurrRay, vcurrRay);
			scaling.z += t1 - t0;
		}
		// Center Dragged
		else if (axisMode == PICK_CENTER)
		{
			// (Sx',Sy',Sz') = 2^®yndc * (Sx,Sy,Sz)
			
			scaling.x *= Math.pow(2, mouseDelta.y);
			scaling.y *= Math.pow(2, mouseDelta.y);
			scaling.z *= Math.pow(2, mouseDelta.y);
		}
	}

	@Override
	public void glRender(GL2 gl, SceneProgram program, Matrix4f modelView, double scale)
	{
		initManipBasis();
		
		Matrix4f axisOriginToWorld = Transforms.translate3DH(manipOrigin);
		Matrix4f scaleMatrix = Transforms.scale3DH((float) scale);
		Matrix4f nextModelView;
		
		nextModelView = new Matrix4f(modelView);
		nextModelView.mul(axisOriginToWorld);
		nextModelView.mul(ManipUtils.rotateZTo(xManipBasis));
		nextModelView.mul(scaleMatrix);
		glRenderBoxOnAStick(gl, program, nextModelView, 0);
		
		nextModelView = new Matrix4f(modelView);
		nextModelView.mul(axisOriginToWorld);
		nextModelView.mul(ManipUtils.rotateZTo(yManipBasis));
		nextModelView.mul(scaleMatrix);
		glRenderBoxOnAStick(gl, program, nextModelView, 1);
		
		nextModelView = new Matrix4f(modelView);
		nextModelView.mul(axisOriginToWorld);
		nextModelView.mul(ManipUtils.rotateZTo(zManipBasis));
		nextModelView.mul(scaleMatrix);
		glRenderBoxOnAStick(gl, program, nextModelView, 2);
		
		nextModelView = new Matrix4f(modelView);
		nextModelView.mul(axisOriginToWorld);
		nextModelView.mul(scaleMatrix);
		nextModelView.mul(Transforms.scale3DH(boxRadius));
		program.setModelView(gl, nextModelView);
		setIdIfPicking(gl, program, Manip.PICK_CENTER);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		centerMaterial.drawUsingProgram(gl, program, cube, false); // cube should draw over other parts of manipulator
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}

	private void glRenderBoxOnAStick(GL2 gl, SceneProgram program, Matrix4f modelView, int axis) {
		initResourcesGL(gl);
		
		// render stick
		Matrix4f modelViewComponent = new Matrix4f(modelView);
		modelViewComponent.mul(Transforms.translate3DH(0.0f, 0.0f, stickLength / 2.0f + boxRadius));
		modelViewComponent.mul(Transforms.scale3DH(stickRadius, stickRadius, (stickLength - 2 * boxRadius) / 2.0f));
		
		setIdIfPicking(gl, program, PickingManager.UNSELECTED_ID);
		program.setModelView(gl, modelViewComponent);
		stickMaterial.drawUsingProgram(gl, program, cube, false);
		
		Material axisMaterial;
		if (axis == 0) // x
		{
			axisMaterial = xMaterial;
			setIdIfPicking(gl, program, Manip.PICK_X);
		}
		else if (axis == 1) // y
		{
			axisMaterial = yMaterial;
			setIdIfPicking(gl, program, Manip.PICK_Y);
		}
		else
		{
			axisMaterial = zMaterial;
			setIdIfPicking(gl, program, Manip.PICK_Z);
		}
		
		// render cube at end
		modelViewComponent.set(modelView);
		modelViewComponent.mul(Transforms.translate3DH(0.0f, 0.0f, stickLength));
		modelViewComponent.mul(Transforms.scale3DH(boxRadius));
		
		program.setModelView(gl, modelViewComponent);
		axisMaterial.drawUsingProgram(gl, program, cube, false);
	}
}
