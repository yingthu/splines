package cs4620.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector3f;

import layout.TableLayout;
import cs4620.scene.LightNode;

public class LightSettingPanel extends JPanel implements ChangeListener
{
	private static final long	serialVersionUID	= 1L;

	LightNode lightNode = null;
	boolean changeLightNode = false;

	private JSpinner aR, aG, aB;
	private JSpinner dR, dG, dB;

	public LightSettingPanel()
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
					5
				}
		};

		TableLayout tableLayout = new TableLayout(tableLayoutSize);
		setLayout(tableLayout);

		setBorder(BorderFactory.createTitledBorder("Light"));

		initLabels();
		initTextFields();
	}

	private void initLabels()
	{
		JLabel RLabel = new JLabel("R/X");
		add(RLabel, "3, 1, c, c");
		JLabel GLabel = new JLabel("G/Y");
		add(GLabel, "5, 1, c, c");
		JLabel BLabel = new JLabel("B/Z");
		add(BLabel, "7, 1, c, c");

		JLabel diffuseLabel = new JLabel("intensity = ");
		add(diffuseLabel, "1, 3, r, c");
		JLabel ambientLabel = new JLabel("ambient = ");
		add(ambientLabel, "1, 5, r, c");
	}

	private void initTextFields()
	{
	    aR = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    aG = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    aB = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dR = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dG = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dB = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));

	    add(dR, "3, 3, 3, 3");
	    add(dG, "5, 3, 5, 3");
	    add(dB, "7, 3, 7, 3");
	    
	    add(aR, "3, 5, 3, 5");
	    add(aG, "5, 5, 5, 5");
	    add(aB, "7, 5, 7, 5");

	    dR.addChangeListener(this);
	    dG.addChangeListener(this);
	    dB.addChangeListener(this);
	    aR.addChangeListener(this);
	    aG.addChangeListener(this);
	    aB.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		if (lightNode != null && changeLightNode)
		{
			LightNode.ambient[0] = ((Double)aR.getValue()).floatValue();
			LightNode.ambient[1] = ((Double)aG.getValue()).floatValue();
			LightNode.ambient[2] = ((Double)aB.getValue()).floatValue();

			lightNode.intensity[0] = ((Double)dR.getValue()).floatValue();
			lightNode.intensity[1] = ((Double)dG.getValue()).floatValue();
			lightNode.intensity[2] = ((Double)dB.getValue()).floatValue();
		}
	}

	public void setLightNode(LightNode lightNode)
	{
		changeLightNode = false;

		this.lightNode = lightNode;

		aR.setValue(new Double(LightNode.ambient[0]));
		aG.setValue(new Double(LightNode.ambient[1]));
		aB.setValue(new Double(LightNode.ambient[2]));

		dR.setValue(new Double(this.lightNode.intensity[0]));
		dG.setValue(new Double(this.lightNode.intensity[1]));
		dB.setValue(new Double(this.lightNode.intensity[2]));
		
		changeLightNode = true;
	}
	
	public Vector3f getAmbient()
	{
		return new Vector3f(LightNode.ambient[0], LightNode.ambient[1], LightNode.ambient[2]);
	}
}
