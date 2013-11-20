package cs4620.ui;

import java.util.ArrayList;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cs4620.framework.GLSceneDrawer;
import cs4620.framework.MultiViewPanel;
import cs4620.framework.OrthographicCamera;
import cs4620.framework.OrthographicCameraController;
import cs4620.framework.PerspectiveCamera;
import cs4620.framework.PerspectiveCameraController;
import cs4620.framework.ViewController;

/**
 * A panel containing a GL canvas that can toggle between showing one
 * or four views of an object. Receives as an argument a class containing
 * the draw method used to populate each view.
 */

public class OneFourViewPanel extends MultiViewPanel {
	private static final long serialVersionUID = 7452148043515198995L;
	
	ViewController topController;
	ViewController frontController;
	ViewController rightController;
	ViewController perspectiveController;
	
	ViewController horizontalBar;
	ViewController verticalBar;
	
	boolean showFour = true;
	
	ArrayList<ViewController> fourControllerList;
	ArrayList<ViewController> oneControllerList;
	
	public static final Vector3f BAR_COLOR = new Vector3f(0.5f, 0.5f, 0.5f);
	
	public OneFourViewPanel(GLSceneDrawer drawer) {
		super(drawer);
		
		// init cameras
		float cameraFOV = 30.0f;
		OrthographicCamera topCamera = new OrthographicCamera(
				new Point3f(0, 10, 0), new Point3f(0,0,0), new Vector3f(0,0,-1),
				0.1f, 100.0f, cameraFOV);
		OrthographicCamera frontCamera = new OrthographicCamera(
				new Point3f(0, 0, 10), new Point3f(0,0,0), new Vector3f(0,1,0),
				0.1f, 100.0f, cameraFOV);
		OrthographicCamera rightCamera = new OrthographicCamera(
				new Point3f(10, 0, 0), new Point3f(0,0,0), new Vector3f(0,1,0),
				0.1f, 100.0f, cameraFOV);
		PerspectiveCamera perspectiveCamera = new PerspectiveCamera(
				new Point3f(5,5,5), new Point3f(0,0,0), new Vector3f(0,1,0),
				0.1f, 100, cameraFOV);
		
		// init controllers
		topController = new OrthographicCameraController(topCamera, drawer);
		frontController = new OrthographicCameraController(frontCamera, drawer);
		rightController = new OrthographicCameraController(rightCamera, drawer);
		perspectiveController = new PerspectiveCameraController(perspectiveCamera, drawer);
		
		horizontalBar = new ViewController();
		verticalBar = new ViewController();
		horizontalBar.setBackgroundColor(BAR_COLOR);
		verticalBar.setBackgroundColor(BAR_COLOR);
		
		// populate view lists
		addViewController(topController);
		addViewController(frontController);
		addViewController(rightController);
		addViewController(perspectiveController);
		
		addViewController(horizontalBar);
		addViewController(verticalBar);
		fourControllerList = viewControllers;
		
		viewControllers = new ArrayList<ViewController>();
		addViewController(perspectiveController);
		oneControllerList = viewControllers;
		showFour = false;  // current viewControllers has one, not four
		
		setShowFour(true);
	}
	
	public void setShowFour(boolean showFour)
	{
		boolean valueChanged = (this.showFour != showFour);
		this.showFour = showFour;
		if (valueChanged)
		{
			if (this.showFour)
			{
				viewControllers = fourControllerList;
			}
			else
			{
				viewControllers = oneControllerList;
			}
			reshapeViews();
		}
	}
	
	public void setBackgroundColor(Vector3f color)
	{
		topController.setBackgroundColor(color);
		frontController.setBackgroundColor(color);
		rightController.setBackgroundColor(color);
		perspectiveController.setBackgroundColor(color);
	}
	
	public void setBarColor(Vector3f color)
	{
		horizontalBar.setBackgroundColor(color);
		verticalBar.setBackgroundColor(color);
	}
	
	@Override
	public void reshapeViews()
	{
		
		if (showFour)
		{
			int halfWidth = width / 2;
			int halfHeight = height / 2;
			
			topController.reshape(0, halfHeight, halfWidth, height - halfHeight, width, height);
			frontController.reshape(halfWidth, halfHeight, width - halfWidth, height - halfHeight, width, height);
			rightController.reshape(0, 0, halfWidth, halfHeight, width, height);
			perspectiveController.reshape(halfWidth, 0, width - halfWidth, halfHeight, width, height);
			
			horizontalBar.reshape(0, halfHeight - 1, width, 2, width, height);
			verticalBar.reshape(halfWidth - 1, 0, 2, height, width, height);
		}
		else
		{
			perspectiveController.reshape(0, 0, width, height, width, height);
		}
	}

}
