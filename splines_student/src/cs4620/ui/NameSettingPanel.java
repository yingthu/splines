package cs4620.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import layout.TableLayout;
import cs4620.scene.SceneNode;

public class NameSettingPanel extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;

	private JTextField nameField;
	private SceneNode node;
	private boolean changeName = true;

	public NameSettingPanel() {
		double[][] tableLayoutSize = {
			{ 5, TableLayout.FILL,    5 },
			{ 5, TableLayout.MINIMUM, 5 }
		};

		TableLayout tableLayout = new TableLayout(tableLayoutSize);
		setLayout(tableLayout);
		
		setBorder(BorderFactory.createTitledBorder("Name"));
		
		nameField = new JTextField();
		nameField.addKeyListener(this);
		add(nameField, "1,1,1,1");
	}
	public void setNode(SceneNode node) {
		changeName = false;

		this.node = node;
		nameField.setText(node.getName());

		changeName = true;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// Nothing.
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// Nothing.
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (changeName && node != null)
			node.setName(nameField.getText());
	}
}
