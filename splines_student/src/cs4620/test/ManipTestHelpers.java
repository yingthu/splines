package cs4620.test;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4620.framework.Camera;
import cs4620.framework.OrthographicCamera;
import cs4620.framework.PerspectiveCamera;
import cs4620.framework.Transforms;
import cs4620.manip.ManipUtils;

public class ManipTestHelpers {
	
	public static final float EPSILON = 1e-3f;
	
	// stats about tests
	
	public static int testCount = 0;
	public static int passCount = 0;
	
	// common tools
	
	public static void printOutcome(boolean succeeded)
	{
		if (succeeded)
		{
			System.out.println("PASS");
			passCount++;
		}
		else
		{
			System.out.println("FAIL");
		}
		System.out.println();
		testCount++;
	}
	
	public static void printTestStats()
	{
		System.out.println();
		System.out.println("TOTAL: passed " + passCount + "/" + testCount + " tests.");
	}
	
	public static boolean verboseIsInfNaN(float val)
	{
		if (Double.isInfinite(val) || Double.isNaN(val))
		{
			System.out.println("Got infinity or NaN (divide by zero?)");
			return true;
		}
		return false;
	}
	
	public static boolean verboseEqualsExpected(float result, float expected, boolean requireExact)
	{
		if (verboseIsInfNaN(result))
			return false;
		
		if (requireExact)
		{
			System.out.println("Got " + result + " vs. expected " + expected);
			
			return floatsWithinEpsilon(result, expected);
		}
		else
		{
			System.out.println("Anything goes");
			return true;
		}
	}
	
	public static boolean verboseEqualsExpected(float result, float expected)
	{
		return verboseEqualsExpected(result, expected, true);
	}
	
	public static boolean floatsWithinEpsilon(float a, float b)
	{
		return Math.abs(a - b) < EPSILON;
	}
	
	// specific tests
	
	public static boolean testTimeClosest(Vector3f p, Vector3f v, Vector3f pTarget, Vector3f vTarget, float expected, boolean requireExact)
	{
		System.out.println("Finding closest time for ray p=" + p + ", v=" + v);
		System.out.println("to ray pTarget=" + pTarget + ", vTarget=" + vTarget);
		
		float result = 0.0f;
		
		try {
			result = ManipUtils.timeClosestToRay(p, v, pTarget, vTarget);
		}
		catch (Exception e)
		{
			System.out.println("Method gave exception: " + e.getMessage());
			return false;
		}
		
		return verboseEqualsExpected(result, expected, requireExact);
	}
	
	public static boolean testIntersectRayPlane(Vector3f p, Vector3f v, Vector3f planeP, Vector3f planeN, float expected, boolean requireExact)
	{
		System.out.println("Intersecting ray p=" + p + ", v=" + v);
		System.out.println("with plane planeP=" + planeP + ", planeN=" + planeN);
		
		float result = 0.0f;
		
		try {
			result = ManipUtils.intersectRayPlane(p, v, planeP, planeN);
		}
		catch (Exception e)
		{
			System.out.println("Method gave exception: " + e.getMessage());
			return false;
		}
		
		return verboseEqualsExpected(result, expected, requireExact);
	}
	
	public static boolean testGetRay(Camera camera, Vector2f ndc)
	{
		System.out.println("Making ray for ndc=" + ndc);
		
		Vector3f p = new Vector3f();
		Vector3f v = new Vector3f();
		Vector4f p1 = new Vector4f();
		Vector4f p2 = new Vector4f();
		
		// get ray
		camera.getRayNDC(ndc, p, v);
		
		p1.set(p.x, p.y, p.z, 1.0f);
		p2.set(p.x + v.x, p.y + v.y, p.z + v.z, 1.0f);
		
		// transform back to NDC
		camera.getView().transform(p1);
		camera.getProjection().transform(p1);
		p1.scale(1.0f / p1.w);
		camera.getView().transform(p2);
		camera.getProjection().transform(p2);
		p2.scale(1.0f / p2.w);
		
		// these two points should have the same x and y NDC coordinates
		if (!floatsWithinEpsilon(0.0f, Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y)))
		{
			System.out.println("Ray doesn't project to single x,y in NDC");
			return false;
		}
		
		// should recover NDC coordinates from before
		if (!floatsWithinEpsilon(0.0f, Math.abs(p1.x - ndc.x) + Math.abs(p1.y - ndc.y)))
		{
			System.out.println("Ray doesn't project to correct x,y in NDC");
			return false;
		}
		
		return true;
	}
	
	// test runners
	
	public static void runTimeClosestTests()
	{
		Vector3f p = new Vector3f();
		Vector3f v = new Vector3f();
		Vector3f pTarget = new Vector3f();
		Vector3f vTarget = new Vector3f();
		float expected = 0.0f;
		
		System.out.println("Testing timeClosestToRay");
		
		// simple
		p.set(0,0,0);
		v.set(0,0,1);
		pTarget.set(0,2,5);
		vTarget.set(0,-1,0);
		expected = 5;
		printOutcome(testTimeClosest(p, v, pTarget, vTarget, expected, true));
		
		// less simple
		p.set(2,-9,-11);
		v.set(0,4,5);
		pTarget.set(3,9,6);
		vTarget.set(0,3,1);
		expected = 3;
		printOutcome(testTimeClosest(p, v, pTarget, vTarget, expected, true));
		
		// parallel -- just shouldn't spit out infinities or NaNs
		p.set(0,0,0);
		v.set(0,0,1);
		pTarget.set(3,4,5);
		vTarget.set(0,0,-22);
		expected = 31337; // doesn't matter
		printOutcome(testTimeClosest(p, v, pTarget, vTarget, expected, false));
	}
	
	public static void runRayPlaneTests()
	{
		Vector3f p = new Vector3f();
		Vector3f v = new Vector3f();
		Vector3f planeP = new Vector3f();
		Vector3f planeN = new Vector3f();
		float expected = 0.0f;
		
		System.out.println("Testing intersectRayPlane");
		
		// simple
		p.set(0,0,0);
		v.set(0,0,1);
		planeP.set(0,0,5);
		planeN.set(0,0,1);
		expected = 5;
		printOutcome(testIntersectRayPlane(p, v, planeP, planeN, expected, true));
		
		// off-axis
		p.set(1,2,3);
		v.set(0,1,2);
		planeP.set(0,5,6);
		planeN.set(0,5,5);
		expected = 2;
		printOutcome(testIntersectRayPlane(p, v, planeP, planeN, expected, true));
		
		// ray parallel to plane
		p.set(0,0,0);
		v.set(0,0,1);
		planeP.set(0,1,5);
		planeN.set(0,1,0);
		expected = 12345; // doesn't matter
		printOutcome(testIntersectRayPlane(p, v, planeP, planeN, expected, false));
	}
	
	public static void runGetRayTests(Camera camera)
	{
		System.out.println("Testing getRayNDC");
		
		Vector2f ndc = new Vector2f();
		
		ndc.set(-1, -1);
		printOutcome(testGetRay(camera, ndc));
		
		ndc.set(0.4234f, -0.873f);
		printOutcome(testGetRay(camera, ndc));
	}
	
	public static void main(String [] args)
	{
		
		runTimeClosestTests();
		
		PerspectiveCamera perspectiveCamera = new PerspectiveCamera(
				new Point3f(5,5,5), new Point3f(0,0,0), new Vector3f(0,1,0),
				0.1f, 100, 45);
		runGetRayTests(perspectiveCamera);
		
		OrthographicCamera orthographicCamera = new OrthographicCamera(
				new Point3f(10, 0, 0), new Point3f(0,0,0), new Vector3f(0,1,0),
				0.1f, 100.0f, 45);
		runGetRayTests(orthographicCamera);
		
		runRayPlaneTests();
		
		printTestStats();
	}
}
