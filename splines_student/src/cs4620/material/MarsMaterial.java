package cs4620.material;

import java.util.HashMap;
import java.util.Map;

import cs4620.util.Util;

public class MarsMaterial extends TextureMaterial {

	public MarsMaterial() {
		super("data/textures/mars.jpg");
		
		// Change default colors
		diffuse[0] = 1; diffuse[1] = 1; diffuse[2] = 1;
	}
	
	@Override
	public Object getYamlObjectRepresentation() {
		Map<Object, Object> result = new HashMap<Object,Object>();
		result.put("type", "MarsMaterial");

		result.put("ambient", convertFloatArrayToList(ambient));
		result.put("diffuse", convertFloatArrayToList(diffuse));
		result.put("specular", convertFloatArrayToList(specular));
		result.put("shininess", shininess);
		
		return result;
	}
	
	public static MarsMaterial fromYamlObject(Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?, ?> matMap = (Map<?, ?>)yamlObject;
		
		MarsMaterial mat = new MarsMaterial();
		Util.assign4ElementArrayFromYamlObject(mat.ambient, matMap.get("ambient"));
		Util.assign4ElementArrayFromYamlObject(mat.diffuse, matMap.get("diffuse"));
		Util.assign4ElementArrayFromYamlObject(mat.specular, matMap.get("specular"));
		mat.shininess = Float.valueOf(matMap.get("shininess").toString());
		
		return mat;
	}
	
}
