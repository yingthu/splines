package cs4620.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import cs4620.framework.Transforms;
import cs4620.util.Util;

public class SceneNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	protected String name;
	
	/**
	 * Rotation component of this transformation.
	 * Stored in degrees.
	 */
	public final Vector3f rotation = new Vector3f();

	/**
	 * Scaling component of this transformation.
	 */
	public final Vector3f scaling = new Vector3f();

	/**
	 * Translation component of this transformation.
	 */
	public final Vector3f translation = new Vector3f();

	public SceneNode()
	{
		setName("Unnamed");
		resetTransformation();
	}

	public SceneNode(String name)
	{
		setName(name);
		resetTransformation();
	}
	
	protected void resetTransformation()
	{
		scaling.set(1,1,1);
		rotation.set(0, 0, 0);
		translation.set(0, 0, 0);
	}
	
	
	/**
	 * Returns the transformation taking this node to world space.
	 */
	public Matrix4f toWorld()
	{
		// TODO (Manipulators P1): Return the transformation from this
		// node's local coordinates to world space.
		// WARNING: the handles drawn for the three manipulators will not display properly until
		// you implement this method.
		Matrix4f TranstoParent = new Matrix4f(Transforms.identity3DH());
		Matrix4f TranstoWorld = new Matrix4f(Transforms.identity3DH());
		// Scaling
		TranstoParent.mul(Transforms.translate3DH(translation.x, translation.y, translation.z));
		// Rotation
		TranstoParent.mul(Transforms.rotateAxis3DH(2, rotation.z));
		TranstoParent.mul(Transforms.rotateAxis3DH(1, rotation.y));
		TranstoParent.mul(Transforms.rotateAxis3DH(0, rotation.x));
		// Translation
		TranstoParent.mul(Transforms.scale3DH(scaling.x, scaling.y, scaling.z));
		
		// Multiply transformation for parent
		if (getSceneNodeParent() != null)
		{
			TranstoWorld = getSceneNodeParent().toWorld();
			TranstoWorld.mul(TranstoParent);
		}
		else
			TranstoWorld.mul(TranstoParent);
		
		return TranstoWorld;
	}
	
	/**
	 * Given the view matrix of the camera, returns the transformation
	 * taking this node to eye space.
	 */
	
	public Matrix4f toEye(Matrix4f view)
	{
		Matrix4f out = new Matrix4f(view);
		out.mul(toWorld());
		return out;
	}

	public String getName() {
		return name;
	}
	
	public SceneNode getSceneNodeParent() {
		return (SceneNode) getParent();
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
	
	public SceneNode getSceneNodeChild(int i)
	{
		return (SceneNode)getChildAt(i);
	}
	
	public void setRotation(float xAngle, float yAngle, float zAngle)
	{
		this.rotation.set(xAngle, yAngle, zAngle);
	}

	public void setScaling(float x, float y, float z)
	{
		this.scaling.set(x, y, z);
	}

	public void setTranslation(float x, float y, float z)
	{
		this.translation.set(x, y, z);
	}

	private Object convertVector3ToIntList(Vector3f v)
	{
		List<Object> result = new ArrayList<Object>();
		result.add(v.x);
		result.add(v.y);
		result.add(v.z);
		return result;
	}

	public Object getYamlObjectRepresentation()
	{
		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put("name", getName());
		result.put("type", "SceneNode");
		result.put("translation", convertVector3ToIntList(translation));
		result.put("rotation", convertVector3ToIntList(rotation));
		result.put("scaling", convertVector3ToIntList(scaling));
		List<Object> children = new ArrayList<Object>();
		for (int ctr = 0; ctr < getChildCount(); ctr++)
			  children.add(((SceneNode)getChildAt(ctr)).getYamlObjectRepresentation());
		result.put("children", children);
		return result;
	}

	public static SceneNode fromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;

		if (yamlMap.get("type").equals("SceneNode"))
			return SceneNode.sceneNodeFromYamlObject(gl, yamlObject);
		else if (yamlMap.get("type").equals("MeshNode"))
			return MeshNode.fromYamlObject(gl, yamlObject);
		else if (yamlMap.get("type").equals("LightNode"))
			return LightNode.fromYamlObject(gl, yamlObject);
		else if (yamlMap.get("type").equals("SplineNode"))
			return SplineNode.fromYamlObject(gl, yamlObject);
		else
			throw new RuntimeException("invalid SceneNode type: " + yamlMap.get("type").toString());
	}
	
	public void extractTransformationFromYamlObject(Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;
		translation.set(Util.getVector3ffromYamlObject(yamlMap.get("translation")));
		rotation.set(Util.getVector3ffromYamlObject(yamlMap.get("rotation")));
		scaling.set(Util.getVector3ffromYamlObject(yamlMap.get("scaling")));
	}

	public void addChildrenFromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;
		List<?> childrenList = (List<?>)yamlMap.get("children");
		for(Object o : childrenList)
			insert(SceneNode.fromYamlObject(gl, o),getChildCount());
	}

	public static SceneNode sceneNodeFromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;

		SceneNode result = new SceneNode();
		result.setName((String)yamlMap.get("name"));
		result.extractTransformationFromYamlObject(yamlObject);
		result.addChildrenFromYamlObject(gl, yamlObject);

		return result;
	}
}
