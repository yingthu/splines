package cs4620.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import cs4620.scene.LightNode;
import cs4620.scene.MeshNode;
import cs4620.scene.SceneNode;

/**
 * Use this to get the appropriate icon for a given node.
 * This will lazily load icons and cache them.
 * @author stevenan, pramook
 */
public class NodeIconCache {
	Icon shapeIcon;
	Icon camIcon;
	Icon lightIcon;
	Icon transIcon;

	public Icon getIconFor( SceneNode node )
	{
		if( node instanceof LightNode ) {
			if( lightIcon == null ) {
				lightIcon = new ImageIcon("icons/light.gif");
			}
			return lightIcon;
		}
		else if( node instanceof MeshNode ) {
			if( shapeIcon == null ) {
				shapeIcon = new ImageIcon("icons/thingy.gif");
			}
			return shapeIcon;
		}
		else {
			return transIcon;
		}
	}
}
