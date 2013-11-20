package cs4620.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.TableLayout;
import cs4620.material.DiffuseMaterial;
import cs4620.material.FireMaterial;
import cs4620.material.Material;

public class FireMaterialSettingPanel extends MaterialSettingPanel implements ChangeListener
{

	private FireMaterial material = null;

	private JSpinner sX, sY, sZ;

	private boolean changeMaterialValues = true;

	public FireMaterialSettingPanel()
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
					5, TableLayout.MINIMUM,
					5
				}
		};

		TableLayout tableLayout = new TableLayout(tableLayoutSize);
		setLayout(tableLayout);

		setBorder(BorderFactory.createTitledBorder("Diffuse Material"));

		initLabels();
		initTextFields();
	}

	private void initLabels()
	{
		JLabel RLabel = new JLabel("1");
		add(RLabel, "3, 1, c, c");
		JLabel GLabel = new JLabel("2");
		add(GLabel, "5, 1, c, c");
		JLabel BLabel = new JLabel("3");
		add(BLabel, "7, 1, c, c");

		JLabel color1Label = new JLabel("speed = ");
		add(color1Label, "1, 3, r, c");
	}

	private void initTextFields()
	{
	    sX = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1));
	    sY = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1));
	    sZ = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1));
	    
	    add(sX, "3, 3, 3, 3");
	    add(sY, "5, 3, 5, 3");
	    add(sZ, "7, 3, 7, 3");
	    
	    sX.addChangeListener(this);
	    sY.addChangeListener(this);
	    sZ.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		if (changeMaterialValues && material != null)
		{
			material.scrollSpeeds[0] = ((Double)sX.getValue()).floatValue();
			material.scrollSpeeds[1] = ((Double)sY.getValue()).floatValue();
			material.scrollSpeeds[2] = ((Double)sZ.getValue()).floatValue();
		}
	}

	@Override
	public void setMaterial(Material material)
	{
		changeMaterialValues = false;

		this.material = (FireMaterial) material;

		sX.setValue(new Double(this.material.scrollSpeeds[0]));
		sY.setValue(new Double(this.material.scrollSpeeds[1]));
		sZ.setValue(new Double(this.material.scrollSpeeds[2]));

		changeMaterialValues = true;
	}
}
