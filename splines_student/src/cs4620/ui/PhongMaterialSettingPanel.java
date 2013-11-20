package cs4620.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.TableLayout;
import cs4620.material.Material;
import cs4620.material.PhongMaterial;

public class PhongMaterialSettingPanel extends MaterialSettingPanel implements ChangeListener
{
	private static final long	serialVersionUID	= 1L;

	private PhongMaterial material = null;

	private JSpinner aR, aG, aB;
	private JSpinner dR, dG, dB;
	private JSpinner sR, sG, sB;
	private JSpinner alpha;

	private boolean changeMaterialValues = true;

	public PhongMaterialSettingPanel()
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

		setBorder(BorderFactory.createTitledBorder("Phong Material"));

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
		JLabel specularLabel = new JLabel("specular = ");
		add(specularLabel, "1, 7, r, c");
		JLabel shininessLabel = new JLabel("shininess = ");
		add(shininessLabel, "1, 9, r, c");
	}

	private void initTextFields()
	{
	    aR = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    aG = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    aB = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dR = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dG = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    dB = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    sR = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    sG = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    sB = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
	    alpha = new JSpinner(new SpinnerNumberModel(50.0, 0.0, 200.0, 1));

	    add(aR, "3, 3, 3, 3");
	    add(aG, "5, 3, 5, 3");
	    add(aB, "7, 3, 7, 3");

	    add(dR, "3, 5, 3, 5");
	    add(dG, "5, 5, 5, 5");
	    add(dB, "7, 5, 7, 5");

	    add(sR, "3, 7, 3, 7");
	    add(sG, "5, 7, 5, 7");
	    add(sB, "7, 7, 7, 7");

	    add(alpha, "3, 9, 3, 9");

	    aR.addChangeListener(this);
	    aG.addChangeListener(this);
	    aB.addChangeListener(this);
	    dR.addChangeListener(this);
	    dG.addChangeListener(this);
	    dB.addChangeListener(this);
	    sR.addChangeListener(this);
	    sG.addChangeListener(this);
	    sB.addChangeListener(this);
	    alpha.addChangeListener(this);
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

			material.specular[0] = ((Double)sR.getValue()).floatValue();
			material.specular[1] = ((Double)sG.getValue()).floatValue();
			material.specular[2] = ((Double)sB.getValue()).floatValue();

			material.shininess = ((Double)alpha.getValue()).floatValue();
		}
	}

	public void setMaterial(Material material)
	{
		changeMaterialValues = false;

		this.material = (PhongMaterial) material;

		aR.setValue(new Double(this.material.ambient[0]));
		aG.setValue(new Double(this.material.ambient[1]));
		aB.setValue(new Double(this.material.ambient[2]));

		dR.setValue(new Double(this.material.diffuse[0]));
		dG.setValue(new Double(this.material.diffuse[1]));
		dB.setValue(new Double(this.material.diffuse[2]));

		sR.setValue(new Double(this.material.specular[0]));
		sG.setValue(new Double(this.material.specular[1]));
		sB.setValue(new Double(this.material.specular[2]));

		alpha.setValue(new Double(this.material.shininess));

		changeMaterialValues = true;
	}
}
