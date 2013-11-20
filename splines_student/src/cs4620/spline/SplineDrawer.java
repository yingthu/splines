package cs4620.spline;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JPanel;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import layout.TableLayout;
import cs4620.framework.CameraController;
import cs4620.framework.GLSceneDrawer;
import cs4620.framework.GLPickingDrawer;
import cs4620.framework.GlslException;
import cs4620.framework.IndexBuffer;
import cs4620.framework.MultiViewPanel;
import cs4620.framework.OrthographicCamera;
import cs4620.framework.OrthographicCameraController;
import cs4620.framework.PickingManager;
import cs4620.framework.PickingProgram;
import cs4620.framework.Program;
import cs4620.framework.Transforms;
import cs4620.framework.VertexArray;
import cs4620.framework.VertexBuffer;
import cs4620.framework.ViewController;
import cs4620.material.FlatColorMaterial;
import cs4620.scene.PickingSceneProgram;
import cs4620.scene.ProgramInfo;
import cs4620.scene.SceneProgram;
import cs4620.shape.Mesh;
import cs4620.shape.Square;
import cs4620.shape.TriangleMesh;
import cs4620.spline.BSpline;
import cs4620.spline.DiscreteCurve;
import cs4620.spline.RevolutionVolume;
import cs4620.ui.SceneViewPanel;
import cs4620.ui.SplinePanel;

/*
 * Class responsible for drawing the bspline and control points
 * on the curve editor panel. Also keeps the associated revolution mesh
 * in sync with the curve.
 */
public class SplineDrawer implements GLPickingDrawer {
    private static final long serialVersionUID = 1L;

    private static final float pointSize = 0.1f;
    private static final float canvasSize = 1.5f;

    private static final int minControlPoints = 5;
    private static final int maxControlPoints = 20;
    
    private boolean isClosed = false;

    protected DiscreteCurve curve = null;
    protected TriangleMesh mesh = null;

    protected Vector2f pickDelta = new Vector2f(0, 0);
    protected int selected;
    
    protected VertexArray cpArray = null;
    protected VertexBuffer cpBuf = null;
    protected IndexBuffer cpInd = null;
    
    OrthographicCameraController controller = null;
    OrthographicCamera camera = null;
    
    FlatColorMaterial flatMat = null;
    SceneProgram flatColorProgram = null;
    Square square = null;
    
    SplinePanel objectPanel = null;
    
    PickingSceneProgram pickProgram = null;
    
    protected boolean rebuildNeeded = false;
    protected boolean rebuildMeshNeeded = false;
    
    private boolean interactive = true;
    private boolean normals = true;

    protected float epsilon = 0.3f;
    protected float tolerance = 0.5f;

    public SplineDrawer(boolean isClosed) {
    	this.isClosed = isClosed;
    	
    	float fovy = 45.0f;
    	
    	camera = new OrthographicCamera(
        		new Point3f(0, 0, 10),
        		new Point3f(0, 0, 0),
        		new Vector3f(0, 1, 0),
        		0.1f, 100.0f, 45.0f);
        
        controller = new OrthographicCameraController(camera, new PickingManager(this));
    }
    
    public SplineDrawer(SplinePanel objectPanel, boolean isClosed) {
    	this(isClosed);
    	
        this.objectPanel = objectPanel;
    }
    
    public ViewController getController() {
    	return controller;
    }
    
    protected Vector2f getLocation(Vector2f ndc, float z) {
    	// Returns the x,y coordinates along this ray at z
    	
        Vector3f p0 = new Vector3f();
        Vector3f p1 = new Vector3f();
        camera.getRayNDC(ndc, p0, p1);
        
        // Solve p0 + t * p1 = z
        float t = (z - p0.z)/ p1.z; 
        p1.scaleAdd(t, p0);
        return new Vector2f(p1.x, p1.y);
    }
    
    public Mesh getMesh() {
    	return mesh;
    }

    public void initControlPoints() {
    	if (curve == null) return;
    	
    	initControlPoints(curve);
        selected = 0;
        rebuildNeeded = true;
    }
    
    public static void initControlPoints(DiscreteCurve c) {
    	ArrayList<Vector2f> controlPoints = c.getCtrlPts();
    	controlPoints.clear();
    	controlPoints.add(new Vector2f(-canvasSize,  canvasSize));
    	controlPoints.add(new Vector2f( canvasSize,  canvasSize));
    	controlPoints.add(new Vector2f( canvasSize,  0.0f));
    	controlPoints.add(new Vector2f( canvasSize, -canvasSize));
    	controlPoints.add(new Vector2f(-canvasSize, -canvasSize));
    }

    public void addControlPoint() {
    	if (curve == null) return;
    	
    	ArrayList<Vector2f> controlPoints = curve.getCtrlPts();
    	boolean isClosed = curve.isClosed();
    	int n = controlPoints.size();
    	
    	if (n < maxControlPoints) {
	        if (selected > 0 && selected < n) {
	            Vector2f p = new Vector2f();
	            p.add(controlPoints.get(selected - 1), controlPoints.get(selected));
	            p.scale(0.5f);
	            
	            controlPoints.add(selected, p);
	            rebuildNeeded = true;
	        }
	        else if (isClosed && selected == n)
	        {
	        	// Add a point between the imaginary 'start' and 'end' with a closed curve.
	        	Vector2f p = new Vector2f();
	            p.add(controlPoints.get(0), controlPoints.get(n - 1));
	            p.scale(0.5f);
	            
	            controlPoints.add(p);
	            rebuildNeeded = true;
	        }
    	}
    }

    public void deleteControlPoint() {
    	if (curve == null) return;
    	
    	ArrayList<Vector2f> controlPoints = curve.getCtrlPts();
    	int n = controlPoints.size();
    	
    	// We can only delete points that exist and that aren't boundaries.
    	boolean isInternal = selected > 1  && selected < n;
    	boolean isExternal = selected == 1 || selected == n;
    	boolean canDelete  = isInternal || (isExternal && curve.isClosed());
    	
        if (canDelete && n > minControlPoints) {
            controlPoints.remove(selected - 1);
            selected = 0;
            rebuildNeeded = true;
        }
    }
    
    public DiscreteCurve getCurve() {
    	return curve;
    }
    
    public static float getCanvasSize() {
    	return canvasSize;
    }
    
    public void setCurve(BSpline c) {
    	curve = c;
    	
    	if (curve != null)
    	{
    		curve.setCanvasSize(canvasSize);
    	}
    	
    	rebuildNeeded = true;
    }
    
    public void setMesh(RevolutionVolume c) {
    	mesh = c;
    	rebuildNeeded = true;
    }
    
    public void setView(SplinePanel v) {
    	objectPanel = v;
    }
    
    public void clearSelected() {
    	selected = 0;
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
        rebuildNeeded = true;
    }

    public void setTolerance(float tolerance) {
        this.tolerance = tolerance;
        rebuildNeeded = true;
    }
    
    public void setInteractive(boolean interactive) {
    	this.interactive = interactive;
    	rebuildNeeded = true;
    }
    
    public void setNormals(boolean normals) {
    	this.normals = normals;
    }

    public void rebuildCurve(GL2 gl) {
    	buildCurve(gl, curve, epsilon);
        
        if (objectPanel != null && interactive)
            rebuildMesh(gl);
    }
    
    public static void buildCurve(GL2 gl, DiscreteCurve c, float epsilon) {
    	ArrayList<Vector2f> controlPoints = c.getCtrlPts();
        float[] cage = new float[controlPoints.size() * 2];
        int i = 0;
        for (Vector2f p : controlPoints) {
            cage[i++] = (p.x + canvasSize) / canvasSize;
            cage[i++] = p.y / canvasSize;
        }
        c.build(gl, cage, epsilon);
    }
    
    public void setRebuildNeeded() {
    	rebuildNeeded = true;
    }
    
    public void setRebuildMeshNeeded() {
    	rebuildMeshNeeded = true;
    }

    public void rebuildMesh(GL2 gl) {
    	if (mesh != null) {
    		mesh.buildMesh(gl, tolerance);
    	}
    }
    
    public void rebuildIfNeeded(GL2 gl) {
    	if (rebuildNeeded) {
    		rebuildCurve(gl);
    		
    		rebuildNeeded = false;
    	}
    	
    	if (rebuildMeshNeeded) {
    		rebuildMesh(gl);
    		rebuildMeshNeeded = false;
    	}
    }

    public void renderMesh(GL2 gl) {
    	if (mesh != null) {
			mesh.draw(gl);
    	}
    }

    @Override
    public void init(GLAutoDrawable drawable, CameraController controller) {
        final GL2 gl = drawable.getGL().getGL2();

        gl.glClearColor(0.25f, 0.25f, 0.25f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glDepthFunc(GL2.GL_LESS);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glLineStipple(2, (short) 0x0F0F);
        gl.glDisable(GL2.GL_LINE_STIPPLE);
        
        curve = new BSpline(gl, isClosed, canvasSize);
    	
        flatMat = new FlatColorMaterial();
        square = new Square(gl);
        
        // Square doesn't use tolerance
        square.buildMesh(gl, 0f);
        
        cpArray = new VertexArray(gl, GL2.GL_LINE_STRIP);
        cpBuf = new VertexBuffer(gl, new float[0], 3);
        cpInd = new IndexBuffer(gl, new int[0]);
        
        cpArray.setAttributeBuffer(gl, SceneProgram.VERTEX_INDEX, cpBuf);
        cpArray.setIndexBuffer(gl, cpInd);
        
        try {
        	flatColorProgram = new SceneProgram(gl, "flatcolor.vs", "flatcolor.fs");
		} catch (GlslException e) {
			e.printStackTrace();
			System.exit(1);
		}
        
        try {
			pickProgram = new PickingSceneProgram(gl, "picking.vs", "picking.fs");
		} catch (GlslException e) {
			System.err.println("FAIL: making shader programs");
			e.printStackTrace();
			System.exit(1);
		}

        this.mesh = new RevolutionVolume(gl, curve);
        initControlPoints();
    }
    
    protected void setIdIfPicking(GL2 gl, SceneProgram program, int id)
	{
		if (program instanceof PickingProgram)
		{
			PickingProgram pickingProgram = (PickingProgram) program;
			PickingManager.setId(gl, pickingProgram, id);
		}
	}
    
    protected ProgramInfo constructProgramInfo(GL2 gl, CameraController cameraController)
	{
		ProgramInfo info = new ProgramInfo();
		
		info.un_Projection = cameraController.getProjection();
		info.un_ModelView = cameraController.getView();
		
		// No lighting info used here
		return info;
	}

    @Override
    public void draw(GLAutoDrawable drawable, CameraController controller) {
        final GL2 gl = drawable.getGL().getGL2();
        
        if (curve == null) {
        	return;
        }
        
        gl.glEnable(GL2.GL_FRAMEBUFFER_SRGB);
        
        rebuildIfNeeded(gl);
        
        ArrayList<Vector2f> controlPoints = curve.getCtrlPts();
        int i;
        
        ProgramInfo info = constructProgramInfo(gl, controller);
        
        info.un_ModelView.mul(Transforms.translate3DH(0, 0, -1.0f));
        info.un_ModelView.mul(Transforms.scale3DH(canvasSize));
        
        flatMat.setColor(1.0f, 1.0f, 1.0f);

        // Draw the cage boundary
        gl.glLineWidth(1.0f);
        flatMat.draw(gl, info, square, true);

        // Draw the control point polygon
        gl.glLineWidth(2.0f);
        gl.glEnable(GL2.GL_LINE_STIPPLE);

        int extra = curve.isClosed() ? 1:0;
        float[] array = new float[controlPoints.size() * 3];
        for (int j = 0; j < controlPoints.size(); ++j)
        {
        	array[3*j    ] = controlPoints.get(j).x;
        	array[3*j + 1] = controlPoints.get(j).y;
        	array[3*j + 2] = 0f;
        }
        
        cpBuf.smartSetData(gl, array);
        
        int[] indices = new int[controlPoints.size() + extra];
        for(int j = 0; j < controlPoints.size(); ++j)
        {
        	indices[j] = j;
        }
        
        if (curve.isClosed()){
        	indices[controlPoints.size()] = 0;
        }
        
        cpInd.smartSetData(gl, indices);
        
        Program oldP = Program.swap(gl, flatColorProgram);
        
        info = constructProgramInfo(gl, controller);
        info.un_ModelView.mul(Transforms.translate3DH(0, 0, 0.25f));
        flatColorProgram.setDiffuseColor(gl, new Vector3f(0.5f, 0.5f, 1.0f));
        flatColorProgram.setProjection(gl, info.un_Projection);
        flatColorProgram.setModelView(gl, info.un_ModelView);
        
        cpArray.draw(gl);
        
        Program.use(gl, oldP);
        
        gl.glDisable(GL2.GL_LINE_STIPPLE);

        // Draws the subdivided curve
        curve.render(gl, info, normals);

        i = 0;
        for (Vector2f p : controlPoints) {
            float z = 0.5f;

            if (++i == selected) {
                flatMat.setColor(1.0f, 0.0f, 0.0f);
            } else {
                flatMat.setColor(0.0f, 1.0f, 0.0f);
            }
            
            info = constructProgramInfo(gl, controller);
            info.un_ModelView.mul(Transforms.translate3DH(p.x, p.y, z));
            info.un_ModelView.mul(Transforms.scale3DH(pointSize));
            
            flatMat.draw(gl, info, square, false);
        }
        
        gl.glDisable(GL2.GL_FRAMEBUFFER_SRGB);
    }
    
    @Override
	public void drawPicking(GLAutoDrawable drawable, CameraController controller) {
        final GL2 gl = drawable.getGL().getGL2();
        
        if (curve == null) {
        	return;
        }
        
        rebuildIfNeeded(gl);
        
        ArrayList<Vector2f> controlPoints = curve.getCtrlPts();
        int i;
        
        ProgramInfo info = constructProgramInfo(gl, controller);

        info.un_ModelView.mul(Transforms.translate3DH(0, 0, -1.0f));
        
        Program oldP = Program.swap(gl, pickProgram);

        // Can only pick the control points
        i = 0;
        for (Vector2f p : controlPoints) {
            float z;

            if (++i == selected) {
                flatMat.setColor(1.0f, 0.0f, 0.0f);
                //z = 1.0f;
                z = 0.5f;
            } else {
                flatMat.setColor(0.0f, 1.0f, 0.0f);
                z = 0.5f;
            }
            
            info = constructProgramInfo(gl, controller);
            info.un_ModelView.mul(Transforms.translate3DH(p.x, p.y, z));
            info.un_ModelView.mul(Transforms.scale3DH(pointSize));
            
            setIdIfPicking(gl, pickProgram, i);
            pickProgram.setAllInfo(gl, info);
            flatMat.drawUsingProgram(gl, pickProgram, square, false);
        }
        
        Program.use(gl, oldP);
	}

    @Override
    public void mousePressed(MouseEvent e, CameraController controller) {
        if (selected > 0)
            selected = 0;
    }

    @Override
    public void mouseReleased(MouseEvent e, CameraController controller) {
    }

    @Override
    public void mouseDragged(MouseEvent e, CameraController controller) {
        if (selected > 0 && selected - 1 < curve.getCtrlPts().size()) {
            Vector2f p = curve.getCtrlPts().get(selected - 1);

            Vector2f l = getLocation(controller.getCurrentMousePosition(), 0.5f);
            p.sub(l, pickDelta);
            p.x = Math.max(-canvasSize, Math.min(canvasSize, p.x));
            p.y = Math.max(-canvasSize, Math.min(canvasSize, p.y));
           
            rebuildNeeded = true;
        }
    }

	@Override
	public void handlePicking(GLAutoDrawable drawable,
			CameraController controller, int pickedId) {
        selected = pickedId;
	}
}
