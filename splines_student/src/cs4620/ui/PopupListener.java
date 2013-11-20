package cs4620.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * Class that handles the popup menu events in the display tree
 * @author arbree
 * Oct 21, 2005
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class PopupListener extends MouseAdapter {

	protected JPopupMenu menu;

	public PopupListener(JPopupMenu newMenu) {

		menu = newMenu;
	}

	public void mousePressed(MouseEvent e) {

		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {

		maybeShowPopup(e);
	}

	protected void maybeShowPopup(MouseEvent e) {

		if (e.isPopupTrigger()) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}