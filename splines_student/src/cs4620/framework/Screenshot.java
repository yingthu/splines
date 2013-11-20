package cs4620.framework;
import java.nio.ByteBuffer;

import javax.media.opengl.GL2;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;

import com.jogamp.common.nio.Buffers;

/**
 * Provides machinery for taking screenshots.
 * @author daniel
 *
 */

public class Screenshot {
	
	protected int left;
	protected int bottom;
	protected int width;
	protected int height;
	
	protected ByteBuffer buffer;
	
	byte [] tempRGB = new byte[3];
	
	public Screenshot()
	{
		left = 0;
		bottom = 0;
		width = 0;
		height = 0;
		buffer = null;
	}
	
	public Screenshot(GL2 gl, int left, int bottom, int width, int height)
	{
		captureViewport(gl, left, bottom, width, height);
	}
	
	public Screenshot(GL2 gl, ViewController controller)
	{
		captureViewport(gl, controller);
	}
	
	public void captureViewport(GL2 gl, int left, int bottom, int width, int height)
	{
		gl.glFinish();
		
		this.left = left;
		this.bottom = bottom;
		this.width = width;
		this.height = height;
		
		int elementsToRead = width * height * 3;
		buffer = Buffers.newDirectByteBuffer(elementsToRead);
		
		gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);
		gl.glReadPixels(left, bottom, width, height, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, buffer);
	}
	
	public void captureViewport(GL2 gl, ViewController controller)
	{
		captureViewport(gl, controller.getLeft(), controller.getBottom(), controller.getWidth(), controller.getHeight());
	}
	
	public ByteBuffer getBuffer()
	{
		return buffer;
	}
	
	public void ndcToViewportPixels(Tuple2f ndc)
	{
		ndc.set((width * ndc.x + width) / 2.0f, (height * ndc.y + height) / 2.0f);
	}
	
	public void getColorAtNDC(Tuple2f ndc, int [] out)
	{
		Tuple2f xy = new Vector2f(ndc);
		ndcToViewportPixels(xy);
		
		getColorAtViewportPixel((int) xy.x, (int) xy.y, out);
	}
	
	public void getColorAtViewportPixel(int x, int y, int [] out)
	{
		// FWDO OOB behavior?
		if (x < 0 || y < 0 || x >= width || y >= height)
		{
			System.err.println("Warning: requested OOB " + x + ", " + y);
			out[0] = 0;
			out[1] = 0;
			out[2] = 0;
			return;
		}
		
		// set position to read
		int startIndex = 3 * (y * width + x);
		buffer.position(startIndex);
		
		// get values and account for Java's bytes being signed
		buffer.get(tempRGB, 0, 3);
		out[0] = tempRGB[0]; if (out[0] < 0) out[0] = 256 + out[0];
		out[1] = tempRGB[1]; if (out[1] < 0) out[1] = 256 + out[1];
		out[2] = tempRGB[2]; if (out[2] < 0) out[2] = 256 + out[2];
	}
}
