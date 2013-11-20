package cs4620.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import cs4620.util.Util;

public class LightNode extends SceneNode
{
	private static final long serialVersionUID = 1L;

	public static final float AMBIENT_CONSTANT = 1.0f;
	public static final float INTENSITY_CONSTANT = 0.4f;

	/**
	 * Diffuse intensity.
	 */
	public final float[] intensity = new float[] { INTENSITY_CONSTANT, INTENSITY_CONSTANT, INTENSITY_CONSTANT, 1 };

	/**
	 * Ambient intensity. A single value is used for the entire scene, hence it being static.
	 */
	public static float[] ambient = new float[] { AMBIENT_CONSTANT, AMBIENT_CONSTANT, AMBIENT_CONSTANT, 1 };

	public LightNode()
	{
		// NOP
	}

	public LightNode(String name)
	{
		super(name);
	}

	public void setIntensity(float r, float g, float b)
	{
		intensity[0] = r;
		intensity[1] = g;
		intensity[2] = b;
	}

	public static void setAmbient(float r, float g, float b)
	{
		ambient[0] = r;
		ambient[1] = g;
		ambient[2] = b;
	}

	private List<Object> convertFloatArrayToList(float[] a)
	{
		List<Object> result = new ArrayList<Object>();
		for(int i=0;i<a.length;i++)
			result.add(a[i]);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<String, Object> result = (Map<String, Object>)super.getYamlObjectRepresentation();
		result.put("type", "LightNode");
		result.put("ambient", convertFloatArrayToList(ambient));
		result.put("intensity", convertFloatArrayToList(intensity));
		return result;
	}

	public void extractLightFromYamlObject(Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;

		Util.assign4ElementArrayFromYamlObject(ambient, yamlMap.get("ambient"));
		Util.assign4ElementArrayFromYamlObject(intensity, yamlMap.get("intensity"));
	}

	public static SceneNode fromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> yamlMap = (Map<?, ?>)yamlObject;

		LightNode result = new LightNode();
		result.setName((String)yamlMap.get("name"));
		result.extractTransformationFromYamlObject(yamlObject);
		result.addChildrenFromYamlObject(gl, yamlObject);
		result.extractLightFromYamlObject(yamlObject);

		return result;
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
}
