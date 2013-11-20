package cs4620.framework;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GLEventListener;

public interface GLController
	extends GLEventListener,
		MouseListener,
		MouseMotionListener,
		KeyListener {
	// NOP
}
