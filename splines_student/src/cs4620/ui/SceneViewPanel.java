package cs4620.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GLRunnable;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.vecmath.Vector3f;

import layout.TableLayout;
import cs4620.framework.GLSceneDrawer;
import cs4620.framework.PerspectiveCameraController;

/**
 * A panel containing a one/four-way view of a scene and toggles to
 * enable/disable wireframe rendering and lights.
 */

public class SceneViewPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	JPanel optionPanel;
	OneFourViewPanel glPanel;

	ButtonGroup windowModeButtonGroup;
	JRadioButton fourViewRadioButton;
	JRadioButton oneViewRadioButton;

	JCheckBox wireframeCheckBox;
	JCheckBox lightingCheckBox;

	GLSceneDrawer drawer;

	TableLayout tableLayout;

	boolean showFourView = true;
	boolean showOneView = false;

	public SceneViewPanel(GLSceneDrawer drawer)
	{
		tableLayout = new TableLayout(new double[][] {
			{ TableLayout.FILL },
			{ TableLayout.MINIMUM, TableLayout.FILL, 0 }
		});

		setLayout(tableLayout);

		glPanel = new OneFourViewPanel(drawer);
		add(glPanel, "0, 1, 0, 1");

		initOptionPanel();
		add(optionPanel, "0, 0, 0, 0");
	}

	private void initOptionPanel() {
		optionPanel = new JPanel();
		optionPanel.setLayout(new BorderLayout());

		JPanel radioButtonPanel = new JPanel();
		radioButtonPanel.setLayout(new FlowLayout());

		JPanel displayModePanel = new JPanel();

		windowModeButtonGroup = new ButtonGroup();

		oneViewRadioButton = new JRadioButton("1 View", false);
	    radioButtonPanel.add(oneViewRadioButton);
	    oneViewRadioButton.addActionListener(this);
	    windowModeButtonGroup.add(oneViewRadioButton);

	    fourViewRadioButton = new JRadioButton("4 Views", true);
	    radioButtonPanel.add(fourViewRadioButton);
	    fourViewRadioButton.addActionListener(this);
	    windowModeButtonGroup.add(fourViewRadioButton);

	    displayModePanel.add(radioButtonPanel, BorderLayout.LINE_START);

		displayModePanel.setLayout(new FlowLayout());
		optionPanel.add(displayModePanel, BorderLayout.LINE_END);

		wireframeCheckBox = new JCheckBox("Wireframe");
		wireframeCheckBox.setSelected(false);
		displayModePanel.add(wireframeCheckBox);

		lightingCheckBox = new JCheckBox("Lighting");
		lightingCheckBox.setSelected(true);
		displayModePanel.add(lightingCheckBox);
	}

	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == oneViewRadioButton)
		{
			glPanel.setShowFour(false);
		}
		else if (e.getSource() == fourViewRadioButton)
		{
			glPanel.setShowFour(true);
		}
	}

	public boolean isWireframeMode()
	{
		return wireframeCheckBox.isSelected();
	}

	public boolean isLightingMode()
	{
		return lightingCheckBox.isSelected();
	}
	
	public void setBackgroundColor(Vector3f color)
	{
		glPanel.setBackgroundColor(color);
	}

	public void startAnimation()
	{
		glPanel.startAnimation();
	}

	public void stopAnimation()
	{
		glPanel.stopAnimation();
	}
	
	public boolean invoke(boolean wait, GLRunnable runnable)
	{
		return glPanel.invoke(wait, runnable);
	}
}
