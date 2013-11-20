package cs4620.ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.TableLayout;
import cs4620.scene.SplineNode;

public class SpeedSettingPanel extends JPanel implements ChangeListener {
	private static final long serialVersionUID = 1L;

	private JSpinner speedField;
	private SplineNode node;
	private boolean changeSpeed = true;

	public SpeedSettingPanel() {
		double[][] tableLayoutSize = {
			{ 5, TableLayout.FILL,    5 },
			{ 5, TableLayout.MINIMUM, 5 }
		};

		TableLayout tableLayout = new TableLayout(tableLayoutSize);
		setLayout(tableLayout);
		
		setBorder(BorderFactory.createTitledBorder("Speed"));
		
		// Effectively make the x-size variable and the y-size fixed at the minimum height.
		setMaximumSize(new Dimension(1000, 0));
		
		speedField = new JSpinner(new SpinnerNumberModel(1.0, -10000.0, 10000.0, 0.1));
		speedField.addChangeListener(this);
		add(speedField, "1,1,1,1");
	}
	
	public void setNode(SplineNode node) {
		changeSpeed = false;

		this.node = node;
		speedField.setValue(new Double(node.getSpeed()));

		changeSpeed = true;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (changeSpeed && node != null)
		{
			float value = ((Double)speedField.getValue()).floatValue();
			node.setSpeed(value);
		}
			
	}
}
