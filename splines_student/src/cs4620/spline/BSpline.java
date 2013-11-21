package cs4620.spline;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Vector2f;

import org.yaml.snakeyaml.Yaml;

import cs4620.util.Util;

/*
 * The BSpline class. Takes a set of control points and produces
 * a set of vertices along the smooth curve based on an epsilon
 * criteria. Also computes the accumulated length of the curve at
 * each vertex, which is used for animation.
 */
public class BSpline extends DiscreteCurve {
    protected boolean isClosed;

    protected Vector2f[] bezier = null;
    protected ArrayList<Vector2f> localControlPoints = new ArrayList<Vector2f>();
    protected float [] length_buffer;
    private float totLength = 0.0f;

    // Workspaces for spline tesselation routines
    // (caution, not a thread safe programming practice)
	private Vector2f[] bspPoints = new Vector2f[4];
	private Vector2f[] bezPoints = new Vector2f[4];
    
    public BSpline(GL2 gl, boolean isClosed, float canvasSize) {
    	super(gl, canvasSize);
        this.isClosed = isClosed;
		for (int i = 0; i < 4; i++) {
			bspPoints[i] = new Vector2f();
			bezPoints[i] = new Vector2f();
		}
}

    public ArrayList<Vector2f> getCtrlPts() {
    	return localControlPoints;
    }

    public void setCtrlPts( ArrayList<Vector2f> ctrlPts) {
    	localControlPoints = ctrlPts;
    }

    public boolean isClosed()
    {
    	return isClosed;
    }

    public void setClosed(boolean value)
    {
    	isClosed = value;
    }

    public float getTotalLength() {
    	return totLength;
    }

    public float[] getLengthBuffer() {
    	return length_buffer;
    }
    

    /*
     * Approximate a Bezier segment with a number of vertices, according to an appropriate
     * criterion for how many are needed.  The points on the curve are written into the
     * array outPoints, and the corresponding tangent vectors (not normalized) are written
     * into outTangents.  The last point (the 4th control point) is not included.
     */
    public void tessellate_bezier(Vector2f cp[], float epsilon, ArrayList<Vector2f> outPoints, ArrayList<Vector2f> outDerivs) {
    	 	
    	// TODO (Splines P1): Tesselate a bezier segment
        bezier_helper(cp, epsilon, outPoints, outDerivs, 1);
    }
    
    public void bezier_helper(Vector2f cp[], float epsilon, ArrayList<Vector2f> outPoints, ArrayList<Vector2f> outDerivs, int level) {
    		float angle1 = 0.0f, angle2 = 0.0f;
    		Vector2f p0p1 = new Vector2f();
    		Vector2f p1p2 = new Vector2f();
    		Vector2f p2p3 = new Vector2f();
    		p0p1.sub(cp[1], cp[0]);
    		p1p2.sub(cp[2], cp[1]);
    		p2p3.sub(cp[3], cp[2]);
    		p0p1.normalize();
    		p1p2.normalize();
    		p2p3.normalize();
    		angle1 = p0p1.dot(p1p2);
    		angle2 = p1p2.dot(p2p3);
    		angle1 = (float) Math.acos(angle1);
    		angle2 = (float) Math.acos(angle2);
    		angle1 = Math.abs(angle1);
    		angle2 = Math.abs(angle2);
    		
    		Vector2f p10 = new Vector2f();
    		Vector2f p11 = new Vector2f();
    		Vector2f p12 = new Vector2f();
    		Vector2f p20 = new Vector2f();
    		Vector2f p21 = new Vector2f();
    		Vector2f p30 = new Vector2f();
    		p10.interpolate(cp[0], cp[1], 0.5f);
    		p11.interpolate(cp[1], cp[2], 0.5f);
    		p12.interpolate(cp[2], cp[3], 0.5f);
    		p20.interpolate(p10, p11, 0.5f);
    		p21.interpolate(p11, p12, 0.5f);
    		p30.interpolate(p20, p21, 0.5f);
    		Vector2f deriv30 = new Vector2f();
    		deriv30.sub(p21, p20);
    		deriv30.normalize();
    		if (!(level > 10 || (angle1 < epsilon/2f && angle2 < epsilon/2f))) {
    			// left
    		    Vector2f cp_left[] = new Vector2f[4];
    		    cp_left[0] = cp[0];
    		    cp_left[1] = p10;
    		    cp_left[2] = p20;
    		    cp_left[3] = p30;
    		    bezier_helper(cp_left, epsilon, outPoints, outDerivs, level+1);
    			
    			// self
    			outPoints.add(p30);
    			outDerivs.add(deriv30);
    			//System.out.println(p30.toString());
    			// right
    			Vector2f cp_right[] = new Vector2f[4];
    		    cp_right[0] = p30;
    		    cp_right[1] = p21;
    		    cp_right[2] = p12;
    		    cp_right[3] = cp[3];
    		    bezier_helper(cp_right, epsilon, outPoints, outDerivs, level+1);
    			
    		}
    }
    /*
     * Approximate a single segment of a B-spline with a number of vertices, according to 
     * an appropriate criterion for how many are needed.  The points on the curve are written 
     * into the array outPoints, and the corresponding tangent vectors (not normalized) are 
     * written into outTangents.  The last point is not included.
     */
    public void tessellate_bspline(Vector2f bspPoints[], float epsilon, ArrayList<Vector2f> outPoints, ArrayList<Vector2f> outDerivs) {
    	// Strategy: convert the B-spline segment to a Bezier segment, then approximate that.
    	// TODO (Splines P1): Convert the B-spline control points to control points for an equivalent
    	// Bezier segment, then tesselate that segment, using the tesselate_bezier function.
    		bezPoints[0].x = (bspPoints[0].x + 4.0f * bspPoints[1].x + bspPoints[2].x) / 6.0f;
    		bezPoints[0].y = (bspPoints[0].y + 4.0f * bspPoints[1].y + bspPoints[2].y) / 6.0f;
    		bezPoints[1].x = (4.0f * bspPoints[1].x + 2.0f * bspPoints[2].x) / 6.0f;
    		bezPoints[1].y = (4.0f * bspPoints[1].y + 2.0f * bspPoints[2].y) / 6.0f;
    		bezPoints[2].x = (2.0f * bspPoints[1].x + 4.0f * bspPoints[2].x) / 6.0f;
    		bezPoints[2].y = (2.0f * bspPoints[1].y + 4.0f * bspPoints[2].y) / 6.0f;
    		bezPoints[3].x = (bspPoints[1].x + 4.0f * bspPoints[2].x + bspPoints[3].x) / 6.0f;
    		bezPoints[3].y = (bspPoints[1].y + 4.0f * bspPoints[2].y + bspPoints[3].y) / 6.0f;
    		tessellate_bezier(bezPoints, epsilon, outPoints, outDerivs);
    }
    
    @Override
    public void build(GL2 gl, float[] controlPoints, float epsilon) {
    	// copy control points into list of vectors
    	ArrayList<Vector2f> cp = new ArrayList<Vector2f>();
    	int N = controlPoints.length / 2;
    	for (int i = 0; i < N; i++)
    		cp.add(new Vector2f(controlPoints[2*i], controlPoints[2*i+1]));
    	
    	// Use the vertices array list to store your computed vertices
    	ArrayList<Vector2f> vertices = new ArrayList<Vector2f>();
    	// Computed derivatives for the spline
    	ArrayList<Vector2f> derivs = new ArrayList<Vector2f>();
    	
    	// For each segment of the spline, call tesselate_bspline to add its points
    	if (isClosed) {
			// TODO: Splines Problem 2, Section 4:
        	// Compute Bezier control points for a closed curve. Put the computed vertices
    		// into the vertices ArrayList declared above.
    	} else {
			// TODO: Splines Problem 1, Section 3.1:
        	// Compute Bezier control points for an open curve with boundary conditions.
    		// Put the computed vertices into the vertices ArrayList declared above.
    		// Number of interior segments
    		int numSegments = N - 3;
    		// Computed vertices and derivatives for the segment
    		ArrayList<Vector2f> segPoints = new ArrayList<Vector2f>();
		ArrayList<Vector2f> segDerivs = new ArrayList<Vector2f>();
		
		vertices.clear();
		derivs.clear();
		vertices.add(cp.get(0));
		Vector2f d0 = new Vector2f();
		d0.sub(cp.get(1),cp.get(0));
		d0.normalize();
		derivs.add(d0);
		// Segment 0
		segPoints.clear();
		segDerivs.clear();
		
		bspPoints[1] = cp.get(0);
		bspPoints[2] = cp.get(1);
		bspPoints[3] = cp.get(2);
        Vector2f bspZero = new Vector2f(2.0f * bspPoints[1].x - bspPoints[2].x, 2.0f * bspPoints[1].y - bspPoints[2].y);

		bspPoints[0] = bspZero;
		
		tessellate_bspline(bspPoints, epsilon, segPoints, segDerivs);
		vertices.addAll(segPoints);
		derivs.addAll(segDerivs);
		// Segment 1 to N-3
    		for (int i = 1; i <= numSegments; i++)
    		{
    			segPoints.clear();
    			segDerivs.clear();
    			bspPoints[0] = cp.get(i-1);
    			bspPoints[1] = cp.get(i);
    			bspPoints[2] = cp.get(i+1);
    			bspPoints[3] = cp.get(i+2);
    			tessellate_bspline(bspPoints, epsilon, segPoints, segDerivs);
    			vertices.addAll(segPoints);
    			derivs.addAll(segDerivs);
    		}
    		// Segment N-2
    		segPoints.clear();
    		segDerivs.clear();

    		bspPoints[0] = cp.get(N-3);
    		bspPoints[1] = cp.get(N-2);
    		bspPoints[2] = cp.get(N-1);
        Vector2f bspThree = new Vector2f(2f * bspPoints[2].x - 1f * bspPoints[1].x, 2f * bspPoints[2].y - 1f * bspPoints[1].y);
    		bspPoints[3] = bspThree;

    		tessellate_bspline(bspPoints, epsilon, segPoints, segDerivs);

    		vertices.addAll(segPoints);
    		derivs.addAll(segDerivs);

    		vertices.add(cp.get(N-1));
    		Vector2f d1 = new Vector2f();
    		d1.sub(cp.get(N-1),cp.get(N-2));
    		d1.normalize();
    		derivs.add(d1);
    	}
    	float[] flat_vertices = new float[2 * vertices.size()];
    	float[] flat_normals = new float[2 * vertices.size()];

        // TODO Splines Problem 1 and 2: Copy the vertices and normals into the flat arrays
        
        int nvertices = vertices.size();
        length_buffer = new float[nvertices];
        totLength = 0.0f;
        for (int i = 0; i < nvertices; i++)
        {
        		flat_vertices[2*i] = vertices.get(i).x;
        		flat_vertices[2*i+1] = vertices.get(i).y;
        		flat_normals[2*i] = -1.0f * derivs.get(i).y;
        		flat_normals[2*i+1] = derivs.get(i).x;
        }
        // TODO: PPA2 Problem 3, Section 5.1:
	    // Compute the 'normalized' total length values.
	
        setVertices(gl, flat_vertices);
        setNormals(gl, flat_normals);

        return;
    }

    /*
     * Computes the distance between two points.
     */
    private static float distance(Vector2f p, Vector2f q) {
        float dx = p.x - q.x, dy = p.y - q.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private List<Object> ctrlsToYamlList()
    {
    	List<Object> list = new ArrayList<Object>();
    	for(Vector2f p : localControlPoints) {
    		list.add(p.x);
    		list.add(p.y);
    	}

    	return list;
    }

    public Object getYamlObjectRepresentation()
    {
    	Map<String, Object> result = new HashMap<String, Object>();
		result.put("type", "BSpline");
		result.put("closed", Boolean.toString(isClosed));
		result.put("ctrls", ctrlsToYamlList());
		return result;
    }

    @SuppressWarnings("unchecked")
	public void fromYamlObject(Object yamlObject)
	{
    	// Validate the file.
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");

		Map<String, Object> yamlMap = (Map<String, Object>)yamlObject;
		Object ctrlObject = yamlMap.get("ctrls");

		if (!yamlMap.containsKey("closed"))
			throw new RuntimeException("Must have a 'closed' entry");

		if (!(ctrlObject instanceof List))
			throw new RuntimeException("ctrlObject not a List");

		List<Double> yamlList = (List<Double>)ctrlObject;

		if (yamlList.size() % 2 > 0)
			throw new RuntimeException("Must have an even number of control point values");

		// Validation complete, attempt to load.
		isClosed = Boolean.valueOf(yamlMap.get("closed").toString());

		localControlPoints.clear();
		for(int i = 0; i < yamlList.size(); i += 2) {
			localControlPoints.add(new Vector2f(
				yamlList.get(i + 0).floatValue(),
				yamlList.get(i + 1).floatValue()));
		}
	}

    public void save(String filename) throws IOException
	{
		Yaml yaml = new Yaml();
		Object rep = getYamlObjectRepresentation();
		String output = yaml.dump(rep);

		FileWriter fstream = new FileWriter(filename);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(output);
		out.close();
	}

	@SuppressWarnings("unchecked")
	public void load(String filename) throws IOException
	{
		String fileContent = Util.readFileAsString(filename);
		Yaml yaml = new Yaml();
		Object yamlObject = yaml.load(fileContent);

		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<String, Object> yamlMap = (Map<String, Object>)yamlObject;

		if (yamlMap.get("type").equals("BSpline"))
			fromYamlObject(yamlObject);
		else
			throw new RuntimeException("invalid BSpline type: " + yamlMap.get("type").toString());
	}
}
