package cs4620.shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector2f;

import cs4620.spline.BSpline;
import cs4620.spline.RevolutionVolume;
import cs4620.spline.SplineDrawer;

/*
 * A small wrapper around the RevolutionVolume class allowing
 * interfacing with the Mesh hierarchy.
 */
public class RevMesh extends Mesh {

	public static final float INITIAL_EPS = 0.018f;
	public static final float INITIAL_TOL = 0.158f;
	private BSpline spline = null;
	private RevolutionVolume mesh = null;
	private boolean initialized = false;

	public RevMesh() {
	}

	@Override
	public void draw(GL2 gl) {
		mesh.draw(gl);
	}
	
	@Override
	public void drawWireframe(GL2 gl) {
		mesh.drawWireframe(gl);
	}

	public void buildMesh(GL2 gl, float tolerance) {
		if (!initialized) {
			spline = new BSpline(gl, false, 1.0f);
			mesh = new RevolutionVolume(gl, spline);
			SplineDrawer.initControlPoints(spline);
			SplineDrawer.buildCurve(gl, spline, INITIAL_EPS);
			
			initialized = true;
		}
		mesh.buildMesh(gl, tolerance);
	}
	
	@SuppressWarnings("unchecked")
	public static RevMesh fromYamlObject(GL2 gl, Object yamlObject) {
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<String, Object> yamlMap = (Map<String, Object>)yamlObject;
		
		RevMesh spline  = new RevMesh();
		spline.buildMesh(gl, 0.5f);
		BSpline bspline = spline.getBspline();
		
		bspline.fromYamlObject(yamlMap.get("spline"));
		
		return spline;
	}

	public Object getYamlObjectRepresentation() {
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "RevMesh");
		result.put("spline", spline.getYamlObjectRepresentation());
		return result;
	}

	public void setCurve(ArrayList<Vector2f> pts) {
		spline.setCtrlPts(pts);
	}

	public ArrayList<Vector2f> getCurve() {
		return spline.getCtrlPts();
	}

	public BSpline getBspline() {
		return spline;
	}
	
	public RevolutionVolume getMesh() {
		return mesh;
	}

}
