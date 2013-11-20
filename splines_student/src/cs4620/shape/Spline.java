package cs4620.shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector2f;

import cs4620.spline.BSpline;
import cs4620.spline.SplineDrawer;

/*
 * A small wrapper around the BSpline class allowing
 * interfacing with the Mesh hierarchy.
 */
public class Spline extends Mesh {
	
	public static final float INITIAL_EPS = 0.018f;

	private BSpline spline = null;
	
	private boolean initialized = false;

	public Spline() {
	}

	@Override
	public void draw(GL2 gl) {
		if (spline != null) {
			spline.draw(gl);
		}
	}
	
	@Override
	public void drawWireframe(GL2 gl) {
		// Splines are just lines, so wireframe and normal drawing
		// are the same
		draw(gl);
	}

	@Override
	public void buildMesh(GL2 gl, float tolerance) {
		if (!initialized)
		{
			spline = new BSpline(gl, true, SplineDrawer.getCanvasSize());
			SplineDrawer.initControlPoints(spline);
			
			initialized = true;
		}
		SplineDrawer.buildCurve(gl, spline, tolerance);
	}
	
	@SuppressWarnings("unchecked")
	public static Spline fromYamlObject(GL2 gl, Object yamlObject) {
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<String, Object> yamlMap = (Map<String, Object>)yamlObject;
		
		Spline  spline  = new Spline();
		spline.buildMesh(gl, 0.5f);
		BSpline bspline = spline.getBspline();
		
		bspline.fromYamlObject(yamlMap.get("spline"));
		
		return spline;
	}

	public Object getYamlObjectRepresentation() {
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Spline");
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

}
