package cs4620.problem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector3f;

import cs4620.framework.CameraController;
import cs4620.framework.GLSceneDrawer;
import cs4620.framework.Texture2D;
import cs4620.material.FlatColorMaterial;
import cs4620.material.PhongMaterial;
import cs4620.material.TextureMaterial;
import cs4620.scene.ProgramInfo;
import cs4620.shape.Mesh;
import cs4620.spline.SplineDrawer;
import cs4620.ui.BasicAction;
import cs4620.ui.OneFourViewPanel;
import cs4620.ui.SceneViewPanel;
import cs4620.ui.SplinePanel;
import cs4620.ui.ToleranceSliderPanel;

/**
 * Main window of Splines, problem 1.
 * 
 * @author langlois, szhao, pramook, arbree
 */

public class SplinesP1 extends JFrame implements ChangeListener,
        ActionListener, GLSceneDrawer {
    private static final long serialVersionUID = 1L;
    
    private static final boolean IS_CLOSED = false;

    // Menu commands
    private static final String SAVE_AS_MENU_TEXT = "Save As";
	private static final String OPEN_MENU_TEXT = "Open";
    private static final String EXIT_MENU_TEXT = "Exit";
    private static final String RESET_POINTS_TEXT = "Reset";
    private static final String DELETE_SELECTED_TEXT = "Delete Selected";
    private static final String REBUILD_MESH_TEXT = "Rebuild Mesh";
    private static final String ADD_POINT_TEXT = "Add Control Point";

    private float[] lightAmbient = new float[] { 0.05f, 0.05f, 0.05f, 0f };
    private float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private float[] lightPosition = new float[] { 5, 5, 5, 1.0f };

    private float[] ambient = new float[] { 0.1f, 0.1f, 0.1f, 0.1f };
    private float[] diffuse = new float[] { 0.8f, 0.8f, 0.0f, 0.0f };
    private float[] specular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private float shininess = 50.0f;
    
    PhongMaterial mat = null;
    FlatColorMaterial flatMat = null;
    TextureMaterial texMat = null;

    protected JFileChooser fileChooser;
    protected SplinePanel splinePanel;
    protected SplineDrawer splineDrawer;
    
	JPanel optionPanel;
	ButtonGroup windowModeButtonGroup;
	JRadioButton fourViewRadioButton;
	JRadioButton oneViewRadioButton;

	JCheckBox interactiveCheckBox;
	JCheckBox wireframeCheckBox;
	JCheckBox lightingCheckBox;
	JCheckBox textureCheckBox;
	JCheckBox normalsCheckBox;
	
    protected ToleranceSliderPanel epsilonPanel;
    protected ToleranceSliderPanel sliderPanel;
    protected Texture2D texture;

    public SplinesP1() {
        super("CS 4621/5621 Programming Assignment 2: Splines Problem 1");
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                terminate();
            }
        });
        
        fileChooser = new JFileChooser(new File("data"));

        epsilonPanel = new ToleranceSliderPanel(0.4f, -3.0f, -0.25f, this);
        getContentPane().add(epsilonPanel, BorderLayout.WEST);

        sliderPanel = new ToleranceSliderPanel(-1.35f, -0.25f, this);
        getContentPane().add(sliderPanel, BorderLayout.EAST);

        initMainPane();
        getContentPane().add(splinePanel, BorderLayout.CENTER);
        initActionsAndMenus();
        texture = null;
		
		optionPanel = new JPanel();
		optionPanel.setLayout(new BorderLayout());
		JPanel radioButtonPanel = new JPanel();
		radioButtonPanel.setLayout(new FlowLayout());

		JPanel displayModePanel = new JPanel();

		windowModeButtonGroup = new ButtonGroup();

		oneViewRadioButton = new JRadioButton("1 View", false);
	    radioButtonPanel.add(oneViewRadioButton);
	    oneViewRadioButton.addActionListener(this);
	    windowModeButtonGroup.add(oneViewRadioButton);

	    fourViewRadioButton = new JRadioButton("4 Views", true);
	    radioButtonPanel.add(fourViewRadioButton);
	    fourViewRadioButton.addActionListener(this);
	    windowModeButtonGroup.add(fourViewRadioButton);

	    displayModePanel.add(radioButtonPanel, BorderLayout.LINE_START);

		displayModePanel.setLayout(new FlowLayout());
		optionPanel.add(displayModePanel, BorderLayout.LINE_END);

		interactiveCheckBox = new JCheckBox("Interactive");
		interactiveCheckBox.setSelected(true);
		interactiveCheckBox.addActionListener(this);
		displayModePanel.add(interactiveCheckBox);
		
		wireframeCheckBox = new JCheckBox("Wireframe");
		wireframeCheckBox.setSelected(false);
		wireframeCheckBox.addActionListener(this);
		displayModePanel.add(wireframeCheckBox);

		lightingCheckBox = new JCheckBox("Lighting");
		lightingCheckBox.setSelected(true);
		lightingCheckBox.addActionListener(this);
		displayModePanel.add(lightingCheckBox);
		
		textureCheckBox = new JCheckBox("Texture");
		textureCheckBox.setSelected(false);
		textureCheckBox.addActionListener(this);
		displayModePanel.add(textureCheckBox);
		
		normalsCheckBox = new JCheckBox("Normals");
		normalsCheckBox.setSelected(true);
		normalsCheckBox.addActionListener(this);
		displayModePanel.add(normalsCheckBox);
		
		getContentPane().add(optionPanel, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        new SplinesP1().run();
    }

    public void run() {
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        splinePanel.startAnimation();
    }

    /**
     * Maps all GUI actions to listeners in this object and builds the menu
     */
    protected void initActionsAndMenus() {
        // Create all the actions
    	BasicAction saveAs = new BasicAction(SAVE_AS_MENU_TEXT, this);
		BasicAction open = new BasicAction(OPEN_MENU_TEXT, this);
        BasicAction exit = new BasicAction(EXIT_MENU_TEXT, this);

        BasicAction addPoint = new BasicAction(ADD_POINT_TEXT, this);
        BasicAction deleteSelected = new BasicAction(DELETE_SELECTED_TEXT, this);
        BasicAction reset = new BasicAction(RESET_POINTS_TEXT, this);
        BasicAction rebuildMesh = new BasicAction(REBUILD_MESH_TEXT, this);

        // Set shortcut keys
        exit.setAcceleratorKey(KeyEvent.VK_Q, KeyEvent.CTRL_MASK);
        addPoint.setAcceleratorKey(KeyEvent.VK_A, KeyEvent.CTRL_MASK);
        deleteSelected.setAcceleratorKey(KeyEvent.VK_D, KeyEvent.CTRL_MASK);
        reset.setAcceleratorKey(KeyEvent.VK_L, KeyEvent.CTRL_MASK);
        rebuildMesh.setAcceleratorKey(KeyEvent.VK_R, KeyEvent.CTRL_MASK);

        // Create the menu
        JMenuBar bar = new JMenuBar();
        JMenu menu;

        menu = new JMenu("File");
        menu.setMnemonic('F');
        menu.add(new JMenuItem(open));
		menu.add(new JMenuItem(saveAs));
		menu.addSeparator();
        menu.add(new JMenuItem(exit));
        bar.add(menu);

        menu = new JMenu("Action");
        menu.setMnemonic('A');

        menu.add(new JMenuItem(addPoint));
        menu.add(new JMenuItem(deleteSelected));
        menu.add(new JMenuItem(reset));
        menu.addSeparator();
        menu.add(new JMenuItem(rebuildMesh));
        bar.add(menu);

        setJMenuBar(bar);
    }

    private void initMainPane() {
        splineDrawer = new SplineDrawer(IS_CLOSED);
        splinePanel = new SplinePanel(this, splineDrawer.getController());
        splineDrawer.setView(splinePanel);
    }

    private float getEpsilon() {
        return epsilonPanel.getTolerance();
    }

    private float getTolerance() {
        return sliderPanel.getTolerance();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == epsilonPanel.getSlider()) {
            splineDrawer.setEpsilon(getEpsilon());
        } else if (e.getSource() == sliderPanel.getSlider()) {
            splineDrawer.setTolerance(getTolerance());
        }
    }

    public void terminate() {
        splinePanel.stopAnimation();
        dispose();
        System.exit(0);
    }

    protected void refresh() {
        splinePanel.repaint();
    }

    /**
     * Displays an exception in a window
     * 
     * @param e
     */
    protected void showExceptionDialog(Exception e) {
        String str = "The following exception was thrown: " + e.toString()
                + ".\n\n" + "Would you like to see the stack trace?";
        int choice = JOptionPane.showConfirmDialog(this, str,
                "Exception Thrown", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            e.printStackTrace();
        }
    }
    
    /**
	 * Loads a spline stored in a file
	 */
	protected void openSpline()
	{
		//Select a file
		int choice = fileChooser.showOpenDialog(this);
		if (choice != JFileChooser.APPROVE_OPTION)
		{
			refresh();
			return;
		}
		String filename = fileChooser.getSelectedFile().getAbsolutePath();

		//Load the spline
		try
		{
			splineDrawer.clearSelected();
			splineDrawer.getCurve().load(filename);
			splineDrawer.getCurve().setClosed(IS_CLOSED);
			splineDrawer.setRebuildNeeded();
			splineDrawer.setRebuildMeshNeeded();
		}
		catch (Exception e) {
			showExceptionDialog(e);
		}

		//Update the window
		refresh();
	}
    
    /**
	 * Save the current spline to a file.
	 */
	protected void saveSplineAs()
	{
		//Pick a file
		int choice = fileChooser.showSaveDialog(this);
		if (choice != JFileChooser.APPROVE_OPTION)
		{
			refresh();
			return;
		}
		String filename = fileChooser.getSelectedFile().getAbsolutePath();

		//Write the spline out
		try
		{
			splineDrawer.getCurve().save(filename);
		}
		catch (IOException ioe)
		{
			showExceptionDialog(ioe);
		}

		refresh();
	}

    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == fourViewRadioButton || e.getSource() == oneViewRadioButton) {
			splinePanel.setShowFour(fourViewRadioButton.isSelected());
			return;
    	}
    	else if (e.getSource() == interactiveCheckBox) {
    		splineDrawer.setInteractive(interactiveCheckBox.isSelected());
    		return;
    	}
    	else if (e.getSource() == normalsCheckBox) {
    		splineDrawer.setNormals(normalsCheckBox.isSelected());
    		return;
    	}
    	
        String cmd = e.getActionCommand();
        if (cmd == null)
            return;
        else if (cmd.equals(OPEN_MENU_TEXT))
			openSpline();
		else if (cmd.equals(SAVE_AS_MENU_TEXT))
			saveSplineAs();
        else if (cmd.equals(EXIT_MENU_TEXT))
            terminate();
        else if (cmd.equals(RESET_POINTS_TEXT)) {
            splineDrawer.initControlPoints();
        }
        else if (cmd.equals(DELETE_SELECTED_TEXT)) {
            splineDrawer.deleteControlPoint();
        }
        else if (cmd.equals(REBUILD_MESH_TEXT)) {
            splineDrawer.setRebuildNeeded();
            splineDrawer.setRebuildMeshNeeded();
        }
        else if (cmd.equals(ADD_POINT_TEXT)) {
            splineDrawer.addControlPoint();
        }
    }

    @Override
    public void init(GLAutoDrawable drawable, CameraController controller) {
        final GL2 gl = drawable.getGL().getGL2();

        gl.glClearColor(0.25f, 0.25f, 0.25f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glDepthFunc(GL2.GL_LESS);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

        if (texture == null) {
            try {
                texture = new Texture2D(gl, "data/textures/earth.jpg");
            } catch (IOException e) {
                System.out.print("Cannot load texture: ");
                System.out.println(e.getMessage());
                terminate();
            }
        }
        
        mat = new PhongMaterial();
        mat.setAmbient(ambient[0], ambient[1], ambient[2]);
        mat.setDiffuse(diffuse[0], diffuse[1], diffuse[2]);
        mat.setSpecular(specular[0], specular[1], specular[2]);
        mat.setShininess(shininess);
        
        flatMat = new FlatColorMaterial();
        flatMat.setColor(diffuse[0], diffuse[1], diffuse[2]);
        
        texMat = new TextureMaterial();
        texMat.setAmbient(ambient[0], ambient[1], ambient[2]);
        texMat.setDiffuse(1.0f, 1.0f, 1.0f);
        texMat.setSpecular(specular[0], specular[1], specular[2]);
        texMat.setShininess(shininess);
        
        
        splineDrawer.init(drawable, controller);
        
        splinePanel.setBackgroundColor(new Vector3f(0.4f, 0.4f, 0.4f));
        
        splineDrawer.setEpsilon(getEpsilon());
        splineDrawer.setTolerance(getTolerance());
    }
    
    private ProgramInfo constructProgramInfo(GL2 gl, CameraController controller) {
    	ProgramInfo info = new ProgramInfo();
    	
    	info.un_Projection = controller.getProjection();
    	info.un_ModelView = controller.getView();
    	
    	int maxNumLights = 16;
    	info.un_LightPositions = new Vector3f[maxNumLights];
    	info.un_LightIntensities = new Vector3f[maxNumLights];
    	for(int i = 0; i < maxNumLights; ++i)
    	{
    		info.un_LightPositions[i] = new Vector3f(0f, 0f, 0f);
    		info.un_LightIntensities[i] = new Vector3f(0f, 0f, 0f);
    	}
    	
    	info.un_LightPositions[0] = new Vector3f(lightPosition[0], lightPosition[1], lightPosition[2]);
    	info.un_LightIntensities[0] = new Vector3f(lightDiffuse[0], lightDiffuse[1], lightDiffuse[2]);
    	info.un_LightAmbientIntensity = new Vector3f(lightAmbient[0], lightAmbient[1], lightAmbient[2]);
    	return info;
    }

    @Override
    public void draw(GLAutoDrawable drawable, CameraController controller) {
        final GL2 gl = drawable.getGL().getGL2();
        
        gl.glEnable(GL2.GL_FRAMEBUFFER_SRGB);
        
        ProgramInfo info = constructProgramInfo(gl, controller);
        
        Mesh m = splineDrawer.getMesh();
        
        if (m != null) {
        	if (!lightingCheckBox.isSelected()) {
        		flatMat.draw(gl,  info,  m,  wireframeCheckBox.isSelected());
        	}
        	else if (textureCheckBox.isSelected()) {
        		texMat.draw(gl,  info,  m, wireframeCheckBox.isSelected());
        	}
        	else {
        		mat.draw(gl, info, m, wireframeCheckBox.isSelected());
        	}
        }
        
        gl.glDisable(GL2.GL_FRAMEBUFFER_SRGB);
    }

    @Override
    public void mousePressed(MouseEvent e, CameraController controller) {
        // NOP
    }

    @Override
    public void mouseReleased(MouseEvent e, CameraController controller) {
    	// NOP
    }

    @Override
    public void mouseDragged(MouseEvent e, CameraController controller) {
    	// NOP
    }
}
