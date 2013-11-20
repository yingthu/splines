package cs4620.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.vecmath.Vector3f;

public class Util
{
	public static Vector3f getVector3ffromYamlObject(Object yamlObject)
	{
		if (!(yamlObject instanceof List))
			throw new RuntimeException("yamlObject not a List");
		List<?> yamlList = (List<?>)yamlObject;
		return new Vector3f(
				Float.valueOf(yamlList.get(0).toString()),
				Float.valueOf(yamlList.get(1).toString()),
				Float.valueOf(yamlList.get(2).toString()));
	}
	
	public static void assign3ElementArrayFromYamlObject(float[] output, Object yamlObject)
	{
		if (!(yamlObject instanceof List))
			throw new RuntimeException("yamlObject not a List");
		List<?> yamlList = (List<?>)yamlObject;

		output[0] = Float.valueOf(yamlList.get(0).toString());
		output[1] = Float.valueOf(yamlList.get(1).toString());
		output[2] = Float.valueOf(yamlList.get(2).toString());
	}

	public static void assign4ElementArrayFromYamlObject(float[] output, Object yamlObject)
	{
		if (!(yamlObject instanceof List))
			throw new RuntimeException("yamlObject not a List");
		List<?> yamlList = (List<?>)yamlObject;

		output[0] = Float.valueOf(yamlList.get(0).toString());
		output[1] = Float.valueOf(yamlList.get(1).toString());
		output[2] = Float.valueOf(yamlList.get(2).toString());
		output[3] = Float.valueOf(yamlList.get(3).toString());
	}
	
	/**
	 * Stolen from http://snippets.dzone.com/posts/show/1335
	 */
	public static String readFileAsString(String filePath) throws IOException{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(filePath));
	        f.read(buffer);
	    } finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    return new String(buffer);
	}
}
