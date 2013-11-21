package cs4620.problem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLRunnable;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import layout.TableLayout;
import cs4620.framework.CameraController;
import cs4620.framework.GLPickingDrawer;
import cs4620.framework.GlslException;
import cs4620.framework.PickingManager;
import cs4620.framework.Program;
import cs4620.framework.VerticalScrollPanel;
import cs4620.manip.Manip;
import cs4620.manip.RotateManip;
import cs4620.manip.ScaleManip;
import cs4620.manip.TranslateManip;
import cs4620.material.CometMaterial;
import cs4620.material.DiffuseMaterial;
import cs4620.material.EarthMaterial;
import cs4620.material.FireMaterial;
import cs4620.material.GreenMaterial;
import cs4620.material.MarsMaterial;
import cs4620.material.Material;
import cs4620.material.MoonMaterial;
import cs4620.material.NormalMaterial;
import cs4620.material.PhongMaterial;
import cs4620.material.TexCoordMaterial;
import cs4620.material.TextureMaterial;
import cs4620.material.ToonMaterial;
import cs4620.scene.LightNode;
import cs4620.scene.MeshNode;
import cs4620.scene.PickingSceneProgram;
import cs4620.scene.ProgramInfo;
import cs4620.scene.Scene;
import cs4620.scene.SceneNode;
import cs4620.scene.SceneProgram;
import cs4620.scene.SceneTraverser;
import cs4620.scene.SplineNode;
import cs4620.shape.Cube;
import cs4620.shape.Cylinder;
import cs4620.shape.Mesh;
import cs4620.shape.RevMesh;
import cs4620.shape.Sphere;
import cs4620.shape.Spline;
import cs4620.shape.Teapot;
import cs4620.shape.Torus;
import cs4620.spline.SplineDrawer;
import cs4620.ui.BasicAction;
import cs4620.ui.DiffuseMaterialSettingPanel;
import cs4620.ui.FireMaterialSettingPanel;
import cs4620.ui.LightSettingPanel;
import cs4620.ui.MaterialSettingPanel;
import cs4620.ui.NameSettingPanel;
import cs4620.ui.PhongMaterialSettingPanel;
import cs4620.ui.PopupListener;
import cs4620.ui.SceneViewPanel;
import cs4620.ui.SliderPanel;
import cs4620.ui.SpeedSettingPanel;
import cs4620.ui.SplinePanel;
import cs4620.ui.ToleranceSliderPanel;
import cs4620.ui.ToonMaterialSettingPanel;
import cs4620.ui.TransformSettingPanel;
import cs4620.ui.TreeRenderer;

class HouseAnimatorP3 extends SceneTraverser
{
	float time;
	
	public HouseAnimatorP3()
	{
		time = 0.0f;
	}
	
	public void setTime(float time)
	{
		this.time = time;
	}
	
	public void buildInitialScene(GL2 gl, Scene scene)
	{
		try {
			scene.load(gl, "data/scenes/manip/house.txt");
		} catch (IOException e) {
			System.err.println("FAIL: loading house scene");
			e.printStackTrace();
		}
	}

	@Override
	public void traverseNode(SceneNode node, Matrix4f toEye) {
		if (node.getName().equals("Window"))
		{
			node.rotation.x = time;
		}
		else if (node.getName().equals("DoorHinge"))
		{
			node.rotation.y = 22.5f * ((float) Math.cos(time * Math.PI / 180.0) - 1.0f);
		}
	}
}

class SolarSystemAnimatorP3 extends SceneTraverser
{
	float time;
	
	
	public SolarSystemAnimatorP3()
	{
		time = 0.0f;
	}
	
	public void setTime(float time)
	{
		this.time = time;
	}
	
	public void buildInitialScene(GL2 gl, Scene scene)
	{
		// TODO (Manipulators P3): Initialize scene by loading your solar system from Problem 2.
		try {
			scene.load(gl, "data/scenes/manip/solar_system_2.txt");
		} catch (IOException e) {
			System.err.println("FAIL: loading house scene");
			e.printStackTrace();
		}
	}
	

	@Override
	public void traverseNode(SceneNode node, Matrix4f toEye) {
		// TODO (Manipulators P3): You will need to copy your code from ManipP3.java to here.
		// Update the scene given the current time
		// in this.time. This method will be called once per node in the scene
		// to update the whole scene.
		if (node.getName().equals("EarthGroup"))
		{
			node.rotation.y = time * 2.0f;
		}
		else if (node.getName().equals("Earth"))
		{
			node.rotation.y = time;
		}
		else if (node.getName().equals("MoonGroup"))
		{
			node.rotation.y = time * 3.0f;
		}
		else if (node.getName().equals("Moon")) 
		{
			node.rotation.y = time;
		}
		else if (node.getName().equals("MarsGroup"))
		{
			node.rotation.y = time;
		}
		else if (node.getName().equals("Mars"))
		{
			node.rotation.y = time;
		}
		// TODO: PPA2 Problem 3, Section 5.3: For spline nodes, set the node
		// time, and use the calculated spline offset to update the child nodes.
		// Note that the time for the solar system is set between 0 and 360, but for splines
		// it works better to normalize it to [0, 1].
		if (node.getName().equals("Spline"))
		{
			float splineTime = time*1.0f/360.0f;
			SplineNode splineNode = (SplineNode)node;
			splineNode.setTime(splineTime);
			Enumeration<SceneNode> childEnum = splineNode.children();
			while (childEnum.hasMoreElements()) {
//				System.out.println("Spline Offset is "+splineNode.splineOffset.toString());
				SceneNode sceneNode = (SceneNode) childEnum.nextElement();
				sceneNode.translation.set(splineNode.splineOffset.x, splineNode.splineOffset.y, splineNode.splineOffset.z);
			}	
		}
	}
}

/**
 * Main window of Problem 3.
 *
 * This application can:
 * 1) Load, display, and save scene graph.
 * 2) Let the user manipulate scene graph by elements by entering text.
 *
 * @author pramook, arbree
 */
public class SplinesP3 extends JFrame implements GLPickingDrawer,
	ChangeListener, ActionListener, TreeSelectionListener

{
	private static final long serialVersionUID = 1L;

	//Menu commands
	public static final String SAVE_AS_MENU_TEXT = "Save As";
	public static final String OPEN_MENU_TEXT = "Open";
	public static final String EXIT_MENU_TEXT = "Exit";
	public static final String CLEAR_SELECTED_TEXT = "Clear selection";
	public static final String GROUP_MENU_TEXT = "Group selected";
	public static final String REPARENT_MENU_TEXT = "Reparent selected";
	public static final String DELETE_MENU_TEXT = "Delete selected";
	public static final String PICK_MENU_TEXT = "Select";
	public static final String ROTATE_MENU_TEXT = "Rotate selected";
	public static final String TRANSLATE_MENU_TEXT = "Translate selected";
	public static final String SCALE_MENU_TEXT = "Scale selected";
	private static final String SOLAR_SCENE_MENU_TEXT = "Solar System Scene";
	private static final String HOUSE_SCENE_MENU_TEXT = "House Scene";
	public static final String ADD_LIGHT_MENU_TEXT = "Add Light";
	public static final String ADD_SPHERE_MENU_TEXT = "Add Sphere";
	public static final String ADD_CUBE_MENU_TEXT = "Add Cube";
	public static final String ADD_CYLINDER_MENU_TEXT = "Add Cylinder";
	public static final String ADD_TORUS_MENU_TEXT = "Add Torus";
	public static final String ADD_TEAPOT_MENU_TEXT = "Add Teapot";
	private static final String ADD_SPLINE_MENU_TEXT = "Add Spline";
	private static final String ADD_SPLINEVOL_MENU_TEXT = "Add Spline Volume";
    private static final String RESET_POINTS_TEXT = "Reset";
    private static final String DELETE_SELECTED_TEXT = "Delete Selected";
    private static final String REBUILD_MESH_TEXT = "Rebuild Mesh";
    private static final String ADD_POINT_TEXT = "Add Control Point";
    
    private static final String SHADER_COMBO_BOX_COMMAND = "shaderComboBoxCommand";
	private static final String GREEN_SHADER_TEXT = "Green Shader";
	private static final String NORMAL_SHADER_TEXT = "Normal Shader";
	private static final String BLINN_PHONG_TEXT = "Blinn-Phong Shader";
	private static final String DIFFUSE_SHADER_TEXT = "Diffuse Shader";
	private static final String TOON_SHADER_TEXT = "Toon Shader";
	private static final String FIRE_SHADER_TEXT = "Fire Shader";
	private static final String EARTH_SHADER_TEXT = "Earth Shader";
	private static final String MARS_SHADER_TEXT = "Mars Shader";
	private static final String MOON_SHADER_TEXT = "Moon Shader";
	private static final String COMET_SHADER_TEXT = "Comet Shader";
	private static final String TEXCOORD_SHADER_TEXT = "Texture Coordinate Shader";

	JSplitPane mainSplitPane;
	JSplitPane leftSplitPane;
	SplinePanel splinePanel;
	SplineDrawer splineDrawer;
	ToleranceSliderPanel toleranceSliderPanel;
	ToleranceSliderPanel epsilonSliderPanel;
	SliderPanel timeSliderPanel;
	JFileChooser fileChooser;
	JTree treeView;
	MaterialSettingPanel materialPanel;
	DiffuseMaterialSettingPanel diffuseMaterialPanel;
	PhongMaterialSettingPanel phongMaterialPanel;
	ToonMaterialSettingPanel toonMaterialPanel;
	FireMaterialSettingPanel fireMaterialPanel;
	TransformSettingPanel transformSettingPanel;
	LightSettingPanel lightSettingPanel;
	NameSettingPanel nameSettingPanel;
	SpeedSettingPanel speedSettingPanel;
	JPanel nodeSettingPanel;
	JComboBox shaderComboBox;
	
	TranslateManip translateManip;
	RotateManip rotateManip;
	ScaleManip scaleManip;
	Manip currentManip;
	boolean showManip;
	boolean isManipulatingManip;

	Scene scene;
	
	Scene solarSystemScene;
	Scene houseScene;
	SolarSystemAnimatorP3 solarSystemAnimator;
	HouseAnimatorP3 houseAnimator;
	
	// GL resources
	SceneProgram flatColorProgram;
	PickingSceneProgram pickingProgram;
	
	boolean sliderChanged = true;

	boolean drawForPicking = false;
	SceneNode[] nodesToReparent = null;
	boolean isReparenting = false;
	
	protected boolean initialized = false;
	boolean shadersInitialized = false;
	
	JPanel optionPanel;
	ButtonGroup windowModeButtonGroup;
	JRadioButton fourViewRadioButton;
	JRadioButton oneViewRadioButton;

	JCheckBox interactiveCheckBox;
	JCheckBox wireframeCheckBox;
	JCheckBox lightingCheckBox;
	JCheckBox normalsCheckBox;
	
    private float[] ambient = new float[] { 0.1f, 0.1f, 0.1f, 0.1f };
    private float[] diffuse = new float[] { 0.8f, 0.8f, 0.0f, 0.0f };
    private float[] specular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private float shininess = 50.0f;

	public SplinesP3() {
		super("CS 4621 Splines Assignment / Problem 3");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent windowevent ) {
				terminate();
			}
		});
		
		toleranceSliderPanel = new ToleranceSliderPanel(this);
		epsilonSliderPanel = new ToleranceSliderPanel(0.4f, -3.0f, -0.25f, this);
		timeSliderPanel = new SliderPanel(this, 0.0f, 360.0f, 0.0f, false, 360, "0");
		timeSliderPanel.getSlider().setOrientation(JSlider.HORIZONTAL);

		initMainSplitPane();
		initOptionPane();
		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		getContentPane().add(toleranceSliderPanel, BorderLayout.EAST);
		getContentPane().add(timeSliderPanel, BorderLayout.SOUTH);
		getContentPane().add(epsilonSliderPanel, BorderLayout.WEST);
		getContentPane().add(optionPanel, BorderLayout.NORTH);

		initActionsAndMenus();
		initManip();

		fileChooser = new JFileChooser(new File("data"));
	}

	public static void main(String[] args)
	{
		new SplinesP3().run();
	}

	public void run()
	{
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		splinePanel.startAnimation();
	}

	/**
	 * Maps all GUI actions to listeners in this object and builds the menu
	 */
	protected void initActionsAndMenus()
	{
		//Create all the actions
		BasicAction group = new BasicAction(GROUP_MENU_TEXT, this);
		BasicAction reparent = new BasicAction(REPARENT_MENU_TEXT, this);
		BasicAction delete = new BasicAction(DELETE_MENU_TEXT, this);
		BasicAction clear = new BasicAction(CLEAR_SELECTED_TEXT, this);

		BasicAction solarScene = new BasicAction(SOLAR_SCENE_MENU_TEXT, this);
		BasicAction houseScene = new BasicAction(HOUSE_SCENE_MENU_TEXT, this);
		BasicAction addLight = new BasicAction(ADD_LIGHT_MENU_TEXT, this);
		BasicAction addSphere = new BasicAction(ADD_SPHERE_MENU_TEXT, this);
		BasicAction addCube = new BasicAction(ADD_CUBE_MENU_TEXT, this);
		BasicAction addCylinder = new BasicAction(ADD_CYLINDER_MENU_TEXT, this);
		BasicAction addTorus = new BasicAction(ADD_TORUS_MENU_TEXT, this);
		BasicAction addTeapot = new BasicAction(ADD_TEAPOT_MENU_TEXT, this);
		BasicAction addSpline = new BasicAction(ADD_SPLINE_MENU_TEXT, this);
		BasicAction addSplineVol = new BasicAction(ADD_SPLINEVOL_MENU_TEXT, this);

		BasicAction saveAs = new BasicAction(SAVE_AS_MENU_TEXT, this);
		BasicAction open = new BasicAction(OPEN_MENU_TEXT, this);
		BasicAction exit = new BasicAction(EXIT_MENU_TEXT, this);
		
		BasicAction addPoint = new BasicAction(ADD_POINT_TEXT, this);
        BasicAction deleteSelected = new BasicAction(DELETE_SELECTED_TEXT, this);
        BasicAction reset = new BasicAction(RESET_POINTS_TEXT, this);
        BasicAction rebuildMesh = new BasicAction(REBUILD_MESH_TEXT, this);
		
		BasicAction pickTool = new BasicAction(PICK_MENU_TEXT, this);
		BasicAction rotateTool = new BasicAction(ROTATE_MENU_TEXT, this);
		BasicAction translateTool = new BasicAction(TRANSLATE_MENU_TEXT, this);
		BasicAction scaleTool = new BasicAction(SCALE_MENU_TEXT, this);

		//Set shortcut keys
		group.setAcceleratorKey(KeyEvent.VK_G, KeyEvent.CTRL_MASK);
		reparent.setAcceleratorKey(KeyEvent.VK_R, KeyEvent.CTRL_MASK);
		delete.setAcceleratorKey(KeyEvent.VK_DELETE, 0);
		
		pickTool.setAcceleratorKey(KeyEvent.VK_Q, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_DOWN_MASK);
		translateTool.setAcceleratorKey(KeyEvent.VK_W, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_DOWN_MASK);
		rotateTool.setAcceleratorKey(KeyEvent.VK_E, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_DOWN_MASK);
		scaleTool.setAcceleratorKey(KeyEvent.VK_R, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_DOWN_MASK);

		saveAs.setAcceleratorKey(KeyEvent.VK_A, KeyEvent.CTRL_MASK);
		open.setAcceleratorKey(KeyEvent.VK_O, KeyEvent.CTRL_MASK);
		exit.setAcceleratorKey(KeyEvent.VK_Q, KeyEvent.CTRL_MASK);

		//Create the menu
		JMenuBar bar = new JMenuBar();
		JMenu menu;

		menu = new JMenu("File");
		menu.setMnemonic('F');
		menu.add(new JMenuItem(open));
		menu.add(new JMenuItem(saveAs));
		menu.addSeparator();
		menu.add(new JMenuItem(exit));
		bar.add(menu);

		menu = new JMenu("Edit");
		menu.setMnemonic('E');
		menu.add(new JMenuItem(group));
		menu.add(new JMenuItem(reparent));
		menu.add(new JMenuItem(delete));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(pickTool));
		menu.add(new JMenuItem(translateTool));
		menu.add(new JMenuItem(rotateTool));
		menu.add(new JMenuItem(scaleTool));
		bar.add(menu);
		
		menu = new JMenu("Action");
		menu.setMnemonic('A');
		menu.add(new JMenuItem(addPoint));
        menu.add(new JMenuItem(deleteSelected));
        menu.add(new JMenuItem(reset));
        menu.addSeparator();
        menu.add(new JMenuItem(rebuildMesh));
        bar.add(menu);

		menu = new JMenu("Scene");
		menu.setMnemonic('S');
		menu.add(new JMenuItem(solarScene));
		menu.add(new JMenuItem(houseScene));
		menu.addSeparator();
		menu.add(new JMenuItem(addLight));
		menu.add(new JMenuItem(addSphere));
		menu.add(new JMenuItem(addCube));
		menu.add(new JMenuItem(addCylinder));
		menu.add(new JMenuItem(addTorus));
		menu.add(new JMenuItem(addTeapot));
		menu.add(new JMenuItem(addSpline));
		menu.add(new JMenuItem(addSplineVol));
		bar.add(menu);

		setJMenuBar(bar);

		JPopupMenu p = new JPopupMenu();
		p.add(new JMenuItem(group));
		p.add(new JMenuItem(reparent));
		p.add(new JMenuItem(delete));
		p.add(new JMenuItem(clear));
		p.addSeparator();
		p.add(new JMenuItem(addLight));
		p.add(new JMenuItem(addSphere));
		p.add(new JMenuItem(addCube));
		p.add(new JMenuItem(addCylinder));
		p.add(new JMenuItem(addTorus));
		p.add(new JMenuItem(addTeapot));
		p.add(new JMenuItem(addSpline));
		p.add(new JMenuItem(addSplineVol));

		treeView.addMouseListener(new PopupListener(p));
		treeView.addTreeSelectionListener(this);
	}
	
	private void initOptionPane()
	{
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
		
		normalsCheckBox = new JCheckBox("Normals");
		normalsCheckBox.setSelected(true);
		normalsCheckBox.addActionListener(this);
		displayModePanel.add(normalsCheckBox);
	}

	private void initMainSplitPane()
	{
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		initLeftSplitPane();
		mainSplitPane.setLeftComponent(leftSplitPane);

		splineDrawer = new SplineDrawer(true);
		splinePanel = new SplinePanel(new PickingManager(this), splineDrawer.getController());
		splineDrawer.setView(splinePanel);
		mainSplitPane.setRightComponent(splinePanel);

		mainSplitPane.resetToPreferredSizes();
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setResizeWeight(0.2);
	}

	private void initLeftSplitPane()
	{
		leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		initTreeView();
		VerticalScrollPanel verticalScrollPanel = new VerticalScrollPanel(new BorderLayout());
		verticalScrollPanel.add(treeView, BorderLayout.CENTER);
		leftSplitPane.setTopComponent(new JScrollPane(verticalScrollPanel));

		nodeSettingPanel = new JPanel();
		nodeSettingPanel.setLayout(new TableLayout(new double[][] {
			{
				TableLayout.FILL
			},
			{
				TableLayout.MINIMUM,
				TableLayout.MINIMUM,
				TableLayout.MINIMUM,
				TableLayout.MINIMUM,
				TableLayout.MINIMUM
			}
		}));
		leftSplitPane.setBottomComponent(nodeSettingPanel);
		
		nameSettingPanel = new NameSettingPanel();
		//nodeSettingPanel.add(nameSettingPanel, "0,0,0,0");
		//nameSettingPanel.setVisible(false);

		transformSettingPanel = new TransformSettingPanel();
		//nodeSettingPanel.add(transformSettingPanel, "0,0,0,0");
		//transformSettingPanel.setVisible(false);

		diffuseMaterialPanel = new DiffuseMaterialSettingPanel();
		phongMaterialPanel = new PhongMaterialSettingPanel();
		toonMaterialPanel = new ToonMaterialSettingPanel();
		fireMaterialPanel = new FireMaterialSettingPanel();
		materialPanel = diffuseMaterialPanel;
		//nodeSettingPanel.add(phongMaterialPanel, "0,1,0,1");
		//phongMaterialPanel.setVisible(false);
		
		shaderComboBox = new JComboBox();
		shaderComboBox.setActionCommand(SHADER_COMBO_BOX_COMMAND);
		
		shaderComboBox.addItem(GREEN_SHADER_TEXT);
		shaderComboBox.addItem(NORMAL_SHADER_TEXT);
		shaderComboBox.addItem(BLINN_PHONG_TEXT);
		shaderComboBox.addItem(DIFFUSE_SHADER_TEXT);
		shaderComboBox.addItem(TOON_SHADER_TEXT);
		shaderComboBox.addItem(FIRE_SHADER_TEXT);
		shaderComboBox.addItem(TEXCOORD_SHADER_TEXT);
		shaderComboBox.addItem(EARTH_SHADER_TEXT);
		shaderComboBox.addItem(MARS_SHADER_TEXT);
		shaderComboBox.addItem(MOON_SHADER_TEXT);
		shaderComboBox.addItem(COMET_SHADER_TEXT);
		
		shaderComboBox.addActionListener(this);
		
		speedSettingPanel = new SpeedSettingPanel();

		lightSettingPanel = new LightSettingPanel();
		//nodeSettingPanel.add(lightSettingPanel, "0,2,0,2");
		//lightSettingPanel.setVisible(false);

		leftSplitPane.resetToPreferredSizes();
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setResizeWeight(0.95);
		leftSplitPane.setMinimumSize(new Dimension(300, leftSplitPane.getMinimumSize().height));
		
		mainSplitPane.resetToPreferredSizes();
		nodeSettingPanel.removeAll();
		leftSplitPane.resetToPreferredSizes();
	}

	private void initTreeView()
	{
		// Create the tree views
		treeView = new JTree();
		DefaultTreeCellRenderer renderer = new TreeRenderer();
		treeView.setCellRenderer(renderer);
		treeView.setEditable(true);
		treeView.setCellEditor(new DefaultTreeCellEditor(treeView, renderer));
		treeView.setShowsRootHandles(true);
		treeView.setRootVisible(true);

		KeyListener[] kls = treeView.getKeyListeners();
		for (int i=0; i<kls.length; i++)
			treeView.removeKeyListener(kls[i]);
	}
	
	private void initManip()
	{
		translateManip = new TranslateManip();
		rotateManip = new RotateManip();
		scaleManip = new ScaleManip();

		translateManip.addChangeListener(this);
		rotateManip.addChangeListener(this);
		scaleManip.addChangeListener(this);

		currentManip = null;
		showManip = false;
		isManipulatingManip = false;
	}
	
	private float getEpsilon() {
		return epsilonSliderPanel.getTolerance();
	}

	private float getTolerance()
	{
		return toleranceSliderPanel.getTolerance();
	}
	
	private float getTime()
	{
		return timeSliderPanel.getValue();
	}
	
	protected void initShaders(GL2 gl) {
		if(shadersInitialized)
			return;
		
		try {
			flatColorProgram = new SceneProgram(gl, "flatcolor.vs", "flatcolor.fs");
			pickingProgram = new PickingSceneProgram(gl, "picking.vs", "picking.fs");
		} catch (GlslException e) {
			System.err.println("FAIL: making shader programs");
			e.printStackTrace();
			System.exit(1);
		}
		
		shadersInitialized = true;
	}

	public void init(GLAutoDrawable drawable, CameraController cameraController) {
		if(initialized)
			return;
		
		final GL2 gl = drawable.getGL().getGL2();
		
		solarSystemScene = new Scene(gl);
		houseScene = new Scene(gl);
		solarSystemAnimator = new SolarSystemAnimatorP3();
		solarSystemAnimator.buildInitialScene(gl, solarSystemScene);
		houseAnimator = new HouseAnimatorP3();
		houseAnimator.buildInitialScene(gl, houseScene);
		
		scene = solarSystemScene;
		treeView.setModel(scene.getTreeModel());
		
		rotateManip.setScene(scene);
		scaleManip.setScene(scene);
		translateManip.setScene(scene);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Set depth buffer.
		gl.glClearDepth(1.0f);
		gl.glDepthFunc(GL2.GL_LESS);
		gl.glEnable(GL2.GL_DEPTH_TEST);

		// Set blending mode.
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL2.GL_BLEND);

		// Forces OpenGL to normalize transformed normals to be of
		// unit length before using the normals in OpenGL's lighting equations.
		gl.glEnable(GL2.GL_NORMALIZE);

		// Cull back faces.
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
		gl.glFrontFace(GL2.GL_CCW);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		splineDrawer.init(drawable, cameraController);
		splineDrawer.setTolerance(getTolerance());
		splineDrawer.setEpsilon(getEpsilon());
		splineDrawer.setCurve(null);
		splineDrawer.setMesh(null);

		initShaders(gl);
		rebuildMeshes(gl);
		animateScene();
		splinePanel.startAnimation();
		
		initialized = true;
	}

	protected void setProjectionAndLighting(GL2 gl, SceneProgram program, CameraController cameraController)
	{
		program.setProjection(gl, cameraController.getProjection());
		
		// give program info about all lights in the scene
		scene.setupLighting(gl, program, cameraController.getView());
		program.setLightAmbientIntensity(gl, lightSettingPanel.getAmbient());
	}
	
	protected ProgramInfo constructProgramInfo(GL2 gl, CameraController cameraController)
	{
		ProgramInfo info = new ProgramInfo();
		
		info.un_Projection = cameraController.getProjection();
		scene.getLightingInfo(gl, info, cameraController.getView());
		info.un_LightAmbientIntensity = lightSettingPanel.getAmbient();
		return info;
	}

	public void draw(GLAutoDrawable drawable, CameraController cameraController)
	{
		final GL2 gl = drawable.getGL().getGL2();
		
		gl.glEnable(GL2.GL_FRAMEBUFFER_SRGB);
		
		rebuildMeshes(gl);

		if (!lightingCheckBox.isSelected())
		{
			// Render with flatColorProgram
			Program.use(gl, flatColorProgram);
			
			setProjectionAndLighting(gl, flatColorProgram, cameraController);
			
			if (wireframeCheckBox.isSelected())
			{
				scene.renderWireframeWithProgram(gl, flatColorProgram, cameraController.getView());
			}
			else
			{
				scene.renderWithProgram(gl, flatColorProgram, cameraController.getView());
			}
			
			Program.unuse(gl);
		}
		else
		{
			// Let each node use its own material
			// Get ProgramInfo to use
			ProgramInfo info = constructProgramInfo(gl, cameraController);
			
			if (wireframeCheckBox.isSelected())
			{
				scene.renderWireframe(gl, info, cameraController.getView());
			}
			else
			{
				scene.render(gl, info, cameraController.getView());
			}
		}
		
		if (showManip && currentManip != null)
		{
			Program.use(gl, flatColorProgram);
			setProjectionAndLighting(gl, flatColorProgram, cameraController);
			
			currentManip.renderConstantSize(gl, flatColorProgram, cameraController.getCamera());
		}
		
		Program.unuse(gl);
		
		gl.glDisable(GL2.GL_FRAMEBUFFER_SRGB);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == toleranceSliderPanel.getSlider())
		{
			sliderChanged = true;
			splineDrawer.setTolerance(getTolerance());
		}
		else if (e.getSource() == epsilonSliderPanel.getSlider())
		{
			sliderChanged = true;
			splineDrawer.setEpsilon(getEpsilon());
		}
		else if (e.getSource() == timeSliderPanel.getSlider())
		{
			animateScene();
			transformSettingPanel.updateStateFromNode();
		}
		else if (e.getSource() == currentManip)
		{
			SceneNode node = currentManip.getSceneNode();
			transformSettingPanel.setTransformationNode(node);
			transformSettingPanel.repaint();
		}
	}

	protected void rebuildMeshes(GL2 gl)
	{
		if (sliderChanged)
		{
			scene.rebuildMeshes(gl, getTolerance(), getEpsilon());
			sliderChanged = false;
		}
	}
	
	protected void animateScene()
	{
		houseAnimator.setTime(getTime());
		houseScene.traverse(houseAnimator, new Matrix4f());
		solarSystemAnimator.setTime(getTime());
		solarSystemScene.traverse(solarSystemAnimator, new Matrix4f());
	}

	public void terminate()
	{
		splinePanel.stopAnimation();
		dispose();
		System.exit(0);
	}

	protected void refresh()
	{
		splinePanel.repaint();
	}

	/**
	 * Save the current tree to a file.
	 */
	protected void saveTreeAs(String filename)
	{
		//Write the tree out
		try
		{
			scene.save(filename);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			System.exit(1);
		}

		refresh();
	}

//	/**
//	 * Displays an exception in a window
//	 * @param e
//	 */
//	protected void showExceptionDialog(Exception e)
//	{
//		String str = "The following exception was thrown: " + e.toString() + ".\n\n" + "Would you like to see the stack trace?";
//		int choice = JOptionPane.showConfirmDialog(this, str, "Exception Thrown", JOptionPane.YES_NO_OPTION);
//
//		if (choice == JOptionPane.YES_OPTION) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Loads a tree stored in a file
	 */
	protected void openTree(GL2 gl, String filename)
	{
		//Load the tree
		try
		{
			scene.load(gl, filename);
			sliderChanged = true;
			animateScene();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Update the window
		refresh();
	}
	
	/**
	 * Switches to a new animated scene
	 */
	protected void openAnimatedScene(GL2 gl, Scene newScene)
	{
		scene = newScene;
		sliderChanged = true;
		rebuildMeshes(gl);
		
		treeView.setModel(scene.getTreeModel());
		animateScene();

		//Update the window
		refresh();
	}
	
	protected void addNewShape(Mesh mesh, String name)
	{
		TreePath path = treeView.getSelectionPath();
		
		try
		{
			if (mesh instanceof Spline)
			{
				PhongMaterial mat = new PhongMaterial();
		        mat.setAmbient(ambient[0], ambient[1], ambient[2]);
		        mat.setDiffuse(diffuse[0], diffuse[1], diffuse[2]);
		        mat.setSpecular(specular[0], specular[1], specular[2]);
		        mat.setShininess(shininess);
		        
				scene.addNewSpline(path, (Spline)mesh, name, mat);
			}
			else
			{
				scene.addNewShape(path, mesh, name);
			}
			sliderChanged = true;
			refresh();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	protected SceneNode[] getSelection()
	{
		TreePath[] paths = treeView.getSelectionPaths();
		if (paths == null) {
			return new SceneNode[] {};
		}
		SceneNode[] ts = new SceneNode[paths.length];
		for (int i = 0; i < paths.length; i++) {
			ts[i] = (SceneNode) paths[i].getLastPathComponent();
		}
		return ts;
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
		String filename = null;
		if (cmd != null && cmd.equals(SAVE_AS_MENU_TEXT)) {
			// get name of scene to be saved after next draw
			int choice = fileChooser.showSaveDialog(this);
			if (choice != JFileChooser.APPROVE_OPTION)
			{
				refresh();
				return;
			}
			filename = fileChooser.getSelectedFile().getAbsolutePath();
		}
		else if (cmd != null && cmd.equals(OPEN_MENU_TEXT)) {
			// get name of scene to be opened after next draw
			int choice = fileChooser.showOpenDialog(this);
			if (choice != JFileChooser.APPROVE_OPTION)
			{
				refresh();
				return;
			}
			filename = fileChooser.getSelectedFile().getAbsolutePath();
		}
		
		// queue up this action for right after the next display()
		ActionPerformedSplinesP3Runnable runnable = new ActionPerformedSplinesP3Runnable(this, e, filename);
		splinePanel.invoke(false, runnable);
	}
	
	/**
	 * A deferred version of actionPerformed, called with the appropriate
	 * GL context just after a display() has happened and while the running
	 * thread is guaranteed not to run concurrently with any GL commands
	 */
	
	public void processAction(GL2 gl, ActionEvent e, String filename)
	{
		String cmd = e.getActionCommand();
		if (cmd == null) {
			return;
		}
		else if (cmd.equals(GROUP_MENU_TEXT)) {
			SceneNode groupNode = scene.groupNodes(getSelection(), "Group");
			treeView.expandPath(new TreePath(groupNode.getPath()));
			refresh();
		}
		else if (cmd.equals(CLEAR_SELECTED_TEXT)) {
			treeView.clearSelection();
		}
		else if (cmd.equals(REPARENT_MENU_TEXT)) {
			nodesToReparent = getSelection();
			isReparenting = true;
		}
		else if (cmd.equals(DELETE_MENU_TEXT)) {
			scene.deleteNodes(getSelection());
			refresh();
		}
		else if (cmd.equals(PICK_MENU_TEXT)) {
			currentManip = null;
			refresh();
		}
		else if (cmd.equals(TRANSLATE_MENU_TEXT)) {
			SceneNode ts[] = getSelection();
			showManip = ts.length == 1;
			if (showManip)
			{
				currentManip = translateManip;
				currentManip.setSceneNode(ts[0]);
			}
			refresh();
		}
		else if (cmd.equals(ROTATE_MENU_TEXT)) {
			SceneNode ts[] = getSelection();
			showManip = ts.length == 1;
			if (showManip)
			{
				currentManip = rotateManip;
				currentManip.setSceneNode(ts[0]);
			}
			refresh();
		}
		else if (cmd.equals(SCALE_MENU_TEXT)) {
			SceneNode ts[] = getSelection();
			showManip = ts.length == 1;
			if (showManip)
			{
				currentManip = scaleManip;
				currentManip.setSceneNode(ts[0]);
			}
			refresh();
		}
		else if (cmd.equals(SOLAR_SCENE_MENU_TEXT)) {
			openAnimatedScene(gl, solarSystemScene);
		}
		else if (cmd.equals(HOUSE_SCENE_MENU_TEXT)) {
			openAnimatedScene(gl, houseScene);
		}
		else if (cmd.equals(ADD_LIGHT_MENU_TEXT)) {
			scene.addNewLight(treeView.getSelectionPath());
		}
		else if (cmd.equals(ADD_SPHERE_MENU_TEXT)) {
			addNewShape(new Sphere(gl), "Sphere");
		}
		else if (cmd.equals(ADD_CUBE_MENU_TEXT)) {
			addNewShape(new Cube(gl), "Cube");
		}
		else if (cmd.equals(ADD_CYLINDER_MENU_TEXT)) {
			addNewShape(new Cylinder(gl), "Cylinder");
		}
		else if (cmd.equals(ADD_TORUS_MENU_TEXT)) {
			addNewShape(new Torus(gl), "Torus");
		}
		else if (cmd.equals(ADD_TEAPOT_MENU_TEXT)) {
			try {
				addNewShape(new Teapot(gl), "Teapot");
			} catch (Exception e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		}
		else if (cmd.equals(ADD_SPLINE_MENU_TEXT)) {
			addNewShape(new Spline(), "Spline");
		}
		else if (cmd.equals(ADD_SPLINEVOL_MENU_TEXT)) {
			addNewShape(new RevMesh(), "RevMesh");
		}
		else if (cmd.equals(OPEN_MENU_TEXT)) {
			openTree(gl, filename);
		}
		else if (cmd.equals(SAVE_AS_MENU_TEXT)) {
			saveTreeAs(filename);
		}
		else if (cmd.equals(EXIT_MENU_TEXT)) {
			terminate();
		}
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
        else if (cmd.equals(SHADER_COMBO_BOX_COMMAND))
		{
			// get current node
			SceneNode [] nodes = getSelection();
			String selectedShader = (String) shaderComboBox.getSelectedItem();
			
			if(nodes[0] instanceof MeshNode)
			{
				MeshNode meshNode = (MeshNode) nodes[0];
				Material meshMaterial = meshNode.getMaterial();
				boolean changedMaterial = false;
				
				PhongMaterial newLightableMaterial = null;
				
				if(selectedShader.equals(BLINN_PHONG_TEXT) && (!(meshMaterial instanceof PhongMaterial) ||
															   (meshMaterial instanceof ToonMaterial) ||
															   (meshMaterial instanceof DiffuseMaterial) ||
															   (meshMaterial instanceof TextureMaterial)))
				{
					newLightableMaterial = new PhongMaterial();
					newLightableMaterial.setDiffuse(1f, 1f, 1f);
					meshNode.setMaterial(newLightableMaterial);
					changedMaterial = true;
				}
				else if(selectedShader.equals(GREEN_SHADER_TEXT) && !(meshMaterial instanceof GreenMaterial))
				{
					meshNode.setMaterial(new GreenMaterial());
					changedMaterial = true;
				}
				else if(selectedShader.equals(NORMAL_SHADER_TEXT) && !(meshMaterial instanceof NormalMaterial))
				{
					meshNode.setMaterial(new NormalMaterial());
					changedMaterial = true;
				}
				
				else if(selectedShader.equals(TOON_SHADER_TEXT) && !(meshMaterial instanceof ToonMaterial))
				{
					newLightableMaterial = new ToonMaterial();
					newLightableMaterial.setDiffuse(1f, 1f, 1f);
					meshNode.setMaterial(newLightableMaterial);
					changedMaterial = true;
				}
				else if(selectedShader.equals(DIFFUSE_SHADER_TEXT) && !(meshMaterial instanceof DiffuseMaterial))
				{
					newLightableMaterial = new DiffuseMaterial();
					newLightableMaterial.setDiffuse(1f, 1f, 1f);
					meshNode.setMaterial(newLightableMaterial);
					changedMaterial = true;
				}
				else if(selectedShader.equals(FIRE_SHADER_TEXT) && !(meshMaterial instanceof FireMaterial))
				{
					meshNode.setMaterial(new FireMaterial());
					changedMaterial = true;
				}
				else if(selectedShader.equals(TEXCOORD_SHADER_TEXT) && !(meshMaterial instanceof TexCoordMaterial))
				{
					meshNode.setMaterial(new TexCoordMaterial());
					changedMaterial = true;
				}
				else if(selectedShader.equals(EARTH_SHADER_TEXT) && !(meshMaterial instanceof EarthMaterial))
				{
					newLightableMaterial = new EarthMaterial();
					newLightableMaterial.setDiffuse(1f, 1f, 1f);
					meshNode.setMaterial(newLightableMaterial);
					changedMaterial = true;
				}
				else if(selectedShader.equals(MARS_SHADER_TEXT) && !(meshMaterial instanceof MarsMaterial))
				{
					newLightableMaterial = new MarsMaterial();
					newLightableMaterial.setDiffuse(1f, 1f, 1f);
					meshNode.setMaterial(newLightableMaterial);
					changedMaterial = true;
				}
				else if(selectedShader.equals(MOON_SHADER_TEXT) && !(meshMaterial instanceof MoonMaterial))
				{
					newLightableMaterial = new MoonMaterial();
					newLightableMaterial.setDiffuse(1f, 1f, 1f);
					meshNode.setMaterial(newLightableMaterial);
					changedMaterial = true;
				}
				else if(selectedShader.equals(COMET_SHADER_TEXT) && !(meshMaterial instanceof CometMaterial))
				{
					newLightableMaterial = new CometMaterial();
					newLightableMaterial.setDiffuse(1f, 1f, 1f);
					meshNode.setMaterial(newLightableMaterial);
					changedMaterial = true;
				}
				
				if(changedMaterial)
				{
					if(meshMaterial instanceof PhongMaterial && newLightableMaterial != null)
					{
						// copy over material settings
						PhongMaterial old = (PhongMaterial) meshMaterial;
						newLightableMaterial.setAmbient(old.ambient[0], old.ambient[1], old.ambient[2]);
						newLightableMaterial.setDiffuse(old.diffuse[0], old.diffuse[1], old.diffuse[2]);
						newLightableMaterial.setSpecular(old.specular[0], old.specular[1], old.specular[2]);
						newLightableMaterial.setShininess(old.shininess);
					}
					showHideSettingPanels(nodes);
				}
			}
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		SceneNode[] selection = getSelection();

		// Handle reparenting.
		if (isReparenting)
		{
			// Invalid number of new parents selected?
			if (selection.length != 1) return;
			SceneNode parent = selection[0];
			scene.reparent(nodesToReparent, parent);
			isReparenting = false;
		}
		
		showManip = selection.length == 1;
		if (showManip && currentManip != null)
		{
			currentManip.setSceneNode( selection[0]);
		}

		showHideSettingPanels(selection);
		
		splineDrawer.setCurve(null);
		splineDrawer.setMesh(null);

		if(selection.length > 0 && selection[0] instanceof MeshNode)
		{
			MeshNode meshNode = (MeshNode) selection[0];

			if(meshNode.getMesh() instanceof Spline)
			{
				Spline spline = (Spline) meshNode.getMesh();
				splineDrawer.setCurve(spline.getBspline());
				splineDrawer.setMesh(null);
			}
			else if(meshNode.getMesh() instanceof RevMesh)
			{
				RevMesh spline = (RevMesh) meshNode.getMesh();
				splineDrawer.setCurve(spline.getBspline());
				splineDrawer.setMesh(spline.getMesh());
			}
		}
		
		refresh();
	}

	private void showHideSettingPanels(SceneNode[] selection)
	{
		nodeSettingPanel.removeAll();
		if (selection.length == 1)
		{
			SceneNode node = selection[0];

			int visibleCount = 0;
			
			nameSettingPanel.setNode(node);
			nameSettingPanel.setVisible(true);
			
			nodeSettingPanel.add(nameSettingPanel, "0,"+Integer.toString(visibleCount)+",0,"+Integer.toString(visibleCount));
			visibleCount += 1;

			transformSettingPanel.setTransformationNode(node);
			transformSettingPanel.setVisible(true);

			nodeSettingPanel.add(transformSettingPanel, "0,"+Integer.toString(visibleCount)+",0,"+Integer.toString(visibleCount));
			visibleCount += 1;
			
			if (node instanceof SplineNode)
			{
				SplineNode splineNode = (SplineNode)node;
				speedSettingPanel.setNode(splineNode);
				speedSettingPanel.setVisible(true);
				nodeSettingPanel.add(speedSettingPanel, "0,"+Integer.toString(visibleCount)+",0,"+Integer.toString(visibleCount));
				visibleCount += 1;
			}
			else
				speedSettingPanel.setVisible(false);

			if (node instanceof MeshNode)
			{
				MeshNode meshNode = (MeshNode)node;
				
				Material mat = meshNode.getMaterial();
				
				if(mat instanceof DiffuseMaterial)
				{
					shaderComboBox.setSelectedItem(DIFFUSE_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel = diffuseMaterialPanel;
					materialPanel.setVisible(true);
				}
				else if( (mat instanceof PhongMaterial) && !(mat instanceof ToonMaterial) && !(mat instanceof DiffuseMaterial) && !(mat instanceof TextureMaterial))
				{
					shaderComboBox.setSelectedItem(BLINN_PHONG_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel = phongMaterialPanel;
					materialPanel.setVisible(true);
				}
				else if(mat instanceof ToonMaterial)
				{
					shaderComboBox.setSelectedItem(TOON_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel = toonMaterialPanel;
					materialPanel.setVisible(true);
				}
				else if(mat instanceof FireMaterial)
				{
					shaderComboBox.setSelectedItem(FIRE_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel = fireMaterialPanel;
					materialPanel.setVisible(true);
				}
				else if(mat instanceof EarthMaterial)
				{
					shaderComboBox.setSelectedItem(EARTH_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel = phongMaterialPanel;
					materialPanel.setVisible(true);
				}
				else if(mat instanceof MarsMaterial)
				{
					shaderComboBox.setSelectedItem(MARS_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel = phongMaterialPanel;
					materialPanel.setVisible(true);
				}
				else if(mat instanceof MoonMaterial)
				{
					shaderComboBox.setSelectedItem(MOON_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel = phongMaterialPanel;
					materialPanel.setVisible(true);
				}
				else if(mat instanceof CometMaterial)
				{
					shaderComboBox.setSelectedItem(COMET_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel = phongMaterialPanel;
					materialPanel.setVisible(true);
				}
				else if(mat instanceof TexCoordMaterial)
				{
					shaderComboBox.setSelectedItem(TEXCOORD_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel.setVisible(false);
				}
				else if(mat instanceof GreenMaterial)
				{
					shaderComboBox.setSelectedItem(GREEN_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel.setVisible(false);
				}
				else if(mat instanceof NormalMaterial)
				{
					shaderComboBox.setSelectedItem(NORMAL_SHADER_TEXT);
					shaderComboBox.setVisible(true);
					materialPanel.setVisible(false);
				}
				else
				{
					shaderComboBox.setVisible(false);
					materialPanel.setVisible(false);
				}
				
				if(shaderComboBox.isVisible())
				{
					nodeSettingPanel.add(shaderComboBox, "0,"+Integer.toString(visibleCount)+",0,"+Integer.toString(visibleCount));
					visibleCount += 1;
				}
				
				if(materialPanel.isVisible())
				{
					materialPanel.setMaterial(mat);
					nodeSettingPanel.add(materialPanel, "0,"+Integer.toString(visibleCount)+",0,"+Integer.toString(visibleCount));
					visibleCount += 1;
				}
			}
			else
				materialPanel.setVisible(false);

			if (node instanceof LightNode)
			{
				LightNode lightNode = (LightNode)node;
				lightSettingPanel.setLightNode(lightNode);
				lightSettingPanel.setVisible(true);

				nodeSettingPanel.add(lightSettingPanel, "0,"+Integer.toString(visibleCount)+",0,"+Integer.toString(visibleCount));
				visibleCount += 1;
			}
			else
				lightSettingPanel.setVisible(false);
		}
		else
		{
			speedSettingPanel.setVisible(false);
			nameSettingPanel.setVisible(false);
			materialPanel.setVisible(false);
			transformSettingPanel.setVisible(false);
			lightSettingPanel.setVisible(false);
		}
		leftSplitPane.resetToPreferredSizes();
		nodeSettingPanel.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e, CameraController controller) {
		// NOP
	}

	@Override
	public void mouseReleased(MouseEvent e, CameraController controller) {
		if (currentManip != null && showManip && isManipulatingManip)
		{
			currentManip.released();
		}
		isManipulatingManip = false;
	}

	@Override
	public void mouseDragged(MouseEvent e, CameraController controller) {
		if (currentManip != null && showManip && isManipulatingManip)
		{
			currentManip.dragged(controller.getCurrentMousePosition(),
					controller.getMouseDelta());
		}
	}

	@Override
	public void drawPicking(GLAutoDrawable drawable, CameraController controller) {
		final GL2 gl = drawable.getGL().getGL2();
		rebuildMeshes(gl);
		
		Program.use(gl, pickingProgram);
		
		setProjectionAndLighting(gl, pickingProgram, controller);

		scene.renderPicking(gl, pickingProgram, controller.getView());
		
		if (showManip && currentManip != null)
		{
			currentManip.renderConstantSize(gl, pickingProgram, controller.getCamera());
		}
		
		Program.unuse(gl);
	}

	@Override
	public void handlePicking(GLAutoDrawable drawable, CameraController controller, int pickedId) {
		// if node was selected, indicate this in sidebar
		SceneNode node = scene.getNodeById(pickedId);
		if (node != null)
		{
			treeView.setSelectionPath(new TreePath(node.getPath()));
		}
		
		// notify active manipulator if selected
		isManipulatingManip = false;
		if (pickedId == Manip.PICK_CENTER ||
		    pickedId == Manip.PICK_X ||
		    pickedId == Manip.PICK_Y ||
		    pickedId == Manip.PICK_Z)
		{
			if (currentManip != null)
			{
				currentManip.setPickedInfo(pickedId, controller.getCamera(), controller.getLastMousePosition());
				isManipulatingManip = true;
			}
		}
	}
}

/**
 * A class to help with deferring response to interface callbacks until
 * the next GL drawing cycle.
 */

class ActionPerformedSplinesP3Runnable implements GLRunnable {
	
	SplinesP3 problem;
	ActionEvent e;
	String filename;
	
	public ActionPerformedSplinesP3Runnable(SplinesP3 problem, ActionEvent e)
	{
		this.problem = problem;
		this.e = e;
		this.filename = null;
	}
	
	public ActionPerformedSplinesP3Runnable(SplinesP3 problem, ActionEvent e, String filename)
	{
		this.problem = problem;
		this.e = e;
		this.filename = filename;
	}

	@Override
	public boolean run(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		problem.processAction(gl, e, filename);
		
		return true;
	}
	
}
