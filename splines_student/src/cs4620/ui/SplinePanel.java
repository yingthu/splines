package cs4620.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import layout.TableLayout;
import cs4620.framework.GLSceneDrawer;
import cs4620.framework.MultiViewPanel;
import cs4620.framework.OrthographicCamera;
import cs4620.framework.OrthographicCameraController;
import cs4620.framework.PerspectiveCamera;
import cs4620.framework.PerspectiveCameraController;
import cs4620.framework.ViewController;

/**
 * An extended version of the OneFourViewPanel class. This class draws a spline control
 * panel, as well as the normal one-four views for the scene.
 */
public class SplinePanel extends MultiViewPanel {
	private static final long serialVersionUID = 7452148043515198995L;
	
	ViewController topController;
	ViewController frontController;
	ViewController rightController;
	ViewController perspectiveController;
	ViewController fifthController;
	
	ViewController horizontalBar;
	ViewController verticalBar;
	ViewController fifthBar;
	
	boolean showFour = true;
	
	ArrayList<ViewController> fourControllerList;
	ArrayList<ViewController> oneControllerList;
	
	public static final Vector3f BAR_COLOR = new Vector3f(0.5f, 0.5f, 0.5f);
	
	public SplinePanel(GLSceneDrawer drawer, ViewController fifthController) {
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
		this.fifthController = fifthController;
		
		horizontalBar = new ViewController();
		verticalBar = new ViewController();
		fifthBar = new ViewController();
		horizontalBar.setBackgroundColor(BAR_COLOR);
		verticalBar.setBackgroundColor(BAR_COLOR);
		fifthBar.setBackgroundColor(BAR_COLOR);
		
		// populate view lists
		addViewController(this.fifthController);
		addViewController(topController);
		addViewController(frontController);
		addViewController(rightController);
		addViewController(perspectiveController);
		
		addViewController(horizontalBar);
		addViewController(verticalBar);
		addViewController(fifthBar);
		fourControllerList = viewControllers;
		
		viewControllers = new ArrayList<ViewController>();
		addViewController(this.fifthController);
		addViewController(perspectiveController);
		addViewController(fifthBar);
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
		fifthController.setBackgroundColor(color);
		topController.setBackgroundColor(color);
		frontController.setBackgroundColor(color);
		rightController.setBackgroundColor(color);
		perspectiveController.setBackgroundColor(color);
	}
	
	public void setBarColor(Vector3f color)
	{
		horizontalBar.setBackgroundColor(color);
		verticalBar.setBackgroundColor(color);
		fifthBar.setBackgroundColor(color);
	}
	
	@Override
	public void reshapeViews()
	{
		// Fifth controller always takes up 1/3 of the width
		int fw = width / 3;
		fifthController.reshape(0, 0, fw, height);
		fifthBar.reshape(fw - 1, 0, 2, height, width, height);
		
		if (showFour)
		{
			int halfWidth = (width - fw) / 2;
			int halfHeight = height / 2;
			
			topController.reshape(fw, halfHeight, halfWidth, height - halfHeight, width, height);
			frontController.reshape(halfWidth + fw, halfHeight, width - halfWidth - fw, height - halfHeight, width, height);
			rightController.reshape(fw, 0, halfWidth, halfHeight, width, height);
			perspectiveController.reshape(halfWidth + fw, 0, width - halfWidth - fw, halfHeight, width, height);
			
			horizontalBar.reshape(fw, halfHeight - 1, width, 2, width, height);
			verticalBar.reshape(halfWidth - 1 + fw, 0, 2, height, width, height);
		}
		else
		{
			perspectiveController.reshape(fw, 0, width - fw, height, width, height);
		}
	}

}
