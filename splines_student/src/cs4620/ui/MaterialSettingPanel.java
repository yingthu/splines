package cs4620.ui;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cs4620.material.Material;

public abstract class MaterialSettingPanel extends JPanel implements ChangeListener {

	private static final long	serialVersionUID	= 1L;
	
	public MaterialSettingPanel()
	{
	}
	
	public abstract void setMaterial(Material material);
}
