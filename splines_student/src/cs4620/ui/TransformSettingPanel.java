package cs4620.ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.TableLayout;
import cs4620.scene.SceneNode;

public class TransformSettingPanel extends JPanel implements ChangeListener
{
	private static final long	serialVersionUID	= 1L;

	private SceneNode sceneNode = null;

	boolean changeTransformationNode = true;

	JSpinner tX, tY, tZ;
	JSpinner rX, rY, rZ;
	JSpinner sX, sY, sZ;

	public TransformSettingPanel()
	{
		double[][] tableLayoutSize = {
				{
					5, TableLayout.MINIMUM,
					5, TableLayout.FILL,
					5, TableLayout.FILL,
					5, TableLayout.FILL,
					5
				},
				{
					5, TableLayout.MINIMUM,
					5, TableLayout.MINIMUM,
					5, TableLayout.MINIMUM,
					5, TableLayout.MINIMUM,
					5
				}
		};

		TableLayout tableLayout = new TableLayout(tableLayoutSize);
		setLayout(tableLayout);

		setBorder(BorderFactory.createTitledBorder("Transformation"));

		initLabels();
		initTextFields();
	}

	private void initLabels()
	{
		JLabel XLabel = new JLabel("X");
		add(XLabel, "3, 1, c, c");
		JLabel GLabel = new JLabel("Y");
		add(GLabel, "5, 1, c, c");
		JLabel BLabel = new JLabel("Z");
		add(BLabel, "7, 1, c, c");

		JLabel ambientLabel = new JLabel("translation:");
		add(ambientLabel, "1, 3, r, c");
		JLabel diffuseLabel = new JLabel("rotation:");
		add(diffuseLabel, "1, 5, r, c");
		JLabel specularLabel = new JLabel("scaling:");
		add(specularLabel, "1, 7, r, c");
	}

	private void initTextFields()
	{
	    tX = new JSpinner(new SpinnerNumberModel(0.0, -10.0, 10.0, 0.1));
	    tY = new JSpinner(new SpinnerNumberModel(0.0, -10.0, 10.0, 0.1));
	    tZ = new JSpinner(new SpinnerNumberModel(0.0, -10.0, 10.0, 0.1));
	    rX = new JSpinner(new SpinnerNumberModel(0.0, -360.0, 360.0, 0.1));
	    rY = new JSpinner(new SpinnerNumberModel(0.0, -360.0, 360.0, 0.1));
	    rZ = new JSpinner(new SpinnerNumberModel(0.0, -360.0, 360.0, 0.1));
	    sX = new JSpinner(new SpinnerNumberModel(1.0, -10.0, 10.0, 0.1));
	    sY = new JSpinner(new SpinnerNumberModel(1.0, -10.0, 10.0, 0.1));
	    sZ = new JSpinner(new SpinnerNumberModel(1.0, -10.0, 10.0, 0.1));

	    add(tX, "3, 3, 3, 3");
	    add(tY, "5, 3, 5, 3");
	    add(tZ, "7, 3, 7, 3");

	    add(rX, "3, 5, 3, 5");
	    add(rY, "5, 5, 5, 5");
	    add(rZ, "7, 5, 7, 5");

	    add(sX, "3, 7, 3, 7");
	    add(sY, "5, 7, 5, 7");
	    add(sZ, "7, 7, 7, 7");

	    tX.addChangeListener(this);
	    tY.addChangeListener(this);
	    tZ.addChangeListener(this);
	    rX.addChangeListener(this);
	    rY.addChangeListener(this);
	    rZ.addChangeListener(this);
	    sX.addChangeListener(this);
	    sY.addChangeListener(this);
	    sZ.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		if (changeTransformationNode && sceneNode != null)
		{
			sceneNode.translation.x = ((Double)tX.getValue()).floatValue();
			sceneNode.translation.y = ((Double)tY.getValue()).floatValue();
			sceneNode.translation.z = ((Double)tZ.getValue()).floatValue();

			sceneNode.rotation.x = ((Double)rX.getValue()).floatValue();
			sceneNode.rotation.y = ((Double)rY.getValue()).floatValue();
			sceneNode.rotation.z = ((Double)rZ.getValue()).floatValue();

			sceneNode.scaling.x = ((Double)sX.getValue()).floatValue();
			sceneNode.scaling.y = ((Double)sY.getValue()).floatValue();
			sceneNode.scaling.z = ((Double)sZ.getValue()).floatValue();
		}
	}
	
	public void updateStateFromNode()
	{
		changeTransformationNode = false;

		if (sceneNode != null)
		{
			tX.setValue(new Double(this.sceneNode.translation.x));
			tY.setValue(new Double(this.sceneNode.translation.y));
			tZ.setValue(new Double(this.sceneNode.translation.z));
	
			rX.setValue(new Double(this.sceneNode.rotation.x));
			rY.setValue(new Double(this.sceneNode.rotation.y));
			rZ.setValue(new Double(this.sceneNode.rotation.z));
	
			sX.setValue(new Double(this.sceneNode.scaling.x));
			sY.setValue(new Double(this.sceneNode.scaling.y));
			sZ.setValue(new Double(this.sceneNode.scaling.z));
		}

		changeTransformationNode = true;
	}

	public void setTransformationNode(SceneNode sceneNode)
	{
		this.sceneNode = sceneNode;
		
		updateStateFromNode();
	}
}
