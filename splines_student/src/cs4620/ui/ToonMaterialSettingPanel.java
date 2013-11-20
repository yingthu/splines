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
import cs4620.material.Material;
import cs4620.material.ToonMaterial;

public class ToonMaterialSettingPanel extends MaterialSettingPanel implements ChangeListener
{
	private static final long	serialVersionUID	= 1L;

	private ToonMaterial material = null;

	private JSpinner aR, aG, aB;
	private JSpinner dR, dG, dB;
	private JSpinner dS;

	private boolean changeMaterialValues = true;

	public ToonMaterialSettingPanel()
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

		setBorder(BorderFactory.createTitledBorder("Toon Material"));

		initLabels();
		initTextFields();
	}

	private void initLabels()
	{
		JLabel RLabel = new JLabel("R");
		add(RLabel, "3, 1, c, c");
		JLabel GLabel = new JLabel("G");
		add(GLabel, "5, 1, c, c");
		JLabel BLabel = new JLabel("B");
		add(BLabel, "7, 1, c, c");

		JLabel ambientLabel = new JLabel("ambient = ");
		add(ambientLabel, "1, 3, r, c");
		JLabel diffuseLabel = new JLabel("diffuse = ");
		add(diffuseLabel, "1, 5, r, c");
		
		JLabel displaceLabel = new JLabel("displaceScale = ");
		add(displaceLabel, "1, 7, r, c");
	}

	private void initTextFields()
	{
	    aR = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    aG = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    aB = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dR = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dG = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dB = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dS = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));

	    add(aR, "3, 3, 3, 3");
	    add(aG, "5, 3, 5, 3");
	    add(aB, "7, 3, 7, 3");

	    add(dR, "3, 5, 3, 5");
	    add(dG, "5, 5, 5, 5");
	    add(dB, "7, 5, 7, 5");
	    
	    add(dS, "3, 7, 3, 7");

	    aR.addChangeListener(this);
	    aG.addChangeListener(this);
	    aB.addChangeListener(this);
	    dR.addChangeListener(this);
	    dG.addChangeListener(this);
	    dB.addChangeListener(this);
	    dS.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		if (changeMaterialValues && material != null)
		{
			material.ambient[0] = ((Double)aR.getValue()).floatValue();
			material.ambient[1] = ((Double)aG.getValue()).floatValue();
			material.ambient[2] = ((Double)aB.getValue()).floatValue();

			material.diffuse[0] = ((Double)dR.getValue()).floatValue();
			material.diffuse[1] = ((Double)dG.getValue()).floatValue();
			material.diffuse[2] = ((Double)dB.getValue()).floatValue();
			
			material.displaceScale = ((Double)dS.getValue()).floatValue();
		}
	}

	@Override
	public void setMaterial(Material material)
	{
		changeMaterialValues = false;

		this.material = (ToonMaterial) material;

		aR.setValue(new Double(this.material.ambient[0]));
		aG.setValue(new Double(this.material.ambient[1]));
		aB.setValue(new Double(this.material.ambient[2]));

		dR.setValue(new Double(this.material.diffuse[0]));
		dG.setValue(new Double(this.material.diffuse[1]));
		dB.setValue(new Double(this.material.diffuse[2]));
		
		dS.setValue(new Double(this.material.displaceScale));

		changeMaterialValues = true;
	}
}
