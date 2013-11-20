package cs4620.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import cs4620.scene.SceneNode;

/**
 * Class that describes how the display tree is rendered
 * @author arbree
 * Oct 21, 2005
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class TreeRenderer extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = 1L;

	private NodeIconCache nodeIcons = new NodeIconCache();

	public TreeRenderer()
	{
		// NOP
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof SceneNode)
			setIcon( nodeIcons.getIconFor((SceneNode)value) );
		return this;
	}
}
