package cs4620.framework;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

public class VerticalScrollPanel extends JPanel implements Scrollable {
	// design courtesy of http://stackoverflow.com/questions/1248710/how-to-layout-a-components-in-vertical-only-scrollpane
	
	public VerticalScrollPanel(LayoutManager layoutManager) {
		super(layoutManager);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return Math.max(visibleRect.height * 9 / 10, 1);
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		if (getParent() instanceof JViewport) {
		    JViewport viewport = (JViewport) getParent();
		    return getPreferredSize().height < viewport.getHeight();
		}
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return Math.max(visibleRect.height / 10, 1);
	}
	
	private static final long serialVersionUID = 850210047483077136L;
}
