package cs4620.scene;

import javax.vecmath.Matrix4f;

public abstract class SceneTraverser {
	
	/**
	 * Perform some action involving the SceneNode node. Also receives
	 * the complete transformation taking the node to eye coordinates,
	 * formed by a traversal of the Scene hierarchy.
	 */
	public abstract void traverseNode(SceneNode node, Matrix4f toEye);
	
	/**
	 * (optional) Notifies traverser that the entire scene has been traversed.
	 */
	
	public void traversalDone()
	{
		// NOP by default
	}
}
