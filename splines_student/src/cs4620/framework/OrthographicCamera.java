package cs4620.framework;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class OrthographicCamera extends PerspectiveCamera {
	public OrthographicCamera() {
		super();
	}

	public OrthographicCamera(float near, float far, float fovy) {
		super(near, far, fovy);
	}

	public OrthographicCamera(Point3f eye, Point3f target, Vector3f up, float near, float far, float fovy) {
		super(eye, target, up, near, far, fovy);
	}
	
	public Matrix4f getProjection() {
		float height = getHeight();
		return Transforms.ortho3DH(-aspect*height, aspect*height, -height, height, near, far);
	}

	public void updateFrame() {
		Vector3f negGaze = new Vector3f(eye);
		negGaze.sub(target);
		negGaze.normalize();

		up.normalize();
		right.cross(up, negGaze);
		right.normalize();
		up.cross(negGaze, right);
	}
}
