package cs4620.demos;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import cs4620.framework.Program;
import cs4620.framework.Transforms;

public class DemoTransformsInClass extends DemoTransforms {

	public DemoTransformsInClass() { }
	
	void draw(GL2 gl) {
		// Set the background color
		clear_framebuffer(gl, BACKGROUND_COLOR);
		
		// Set up for straightforward drawing
		Program.use(gl, twoDimProgram);
		Matrix3f transform = Transforms.identity2DH();
		
		draw_grid(gl, GRID1_COLOR, transform);

		// multiply on more transformations
		Matrix3f house_xf = new Matrix3f(transform);
		house_xf.mul(getTranslate("T_house"));  // M = M * T
		house_xf.mul(getRotate("R_house"));  // M = M * R
		house_xf.mul(getScale("S_house"));  // M = M * S

		Matrix3f cat_xf = new Matrix3f(transform);
		cat_xf.mul(getTranslate("T_cat"));  // M = M * T
		cat_xf.mul(getRotate("R_cat"));  // M = M * R
		cat_xf.mul(getScale("S_cat"));  // M = M * S

		draw_grid(gl, GRID1_COLOR, viewportTransform);
		//draw_grid(gl, GRID2_COLOR, both_xf);
		draw_grid(gl, GRID3_COLOR, house_xf);
		draw_grid(gl, GRID4_COLOR, cat_xf);

		draw_house(gl, house_xf);
		draw_cat(gl, cat_xf);

		Program.unuse(gl);
	}
	
	// Set all pixel values to the same color
	void clear_framebuffer(GL2 gl, Vector3f color) {
		gl.glClearColor(color.x, color.y, color.z, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
	}
	
	// Draw a group consisting of a house and a cat together, each with its own transformation.
	void draw_house_with_cat(GL2 gl, Matrix3f transform) {
		Matrix3f house_xf = new Matrix3f(transform);
		house_xf.mul(Transforms.translate2DH(-0.5f, -0.3f));  // M = M * T
		Matrix3f cat_xf = new Matrix3f(transform);
		cat_xf.mul(Transforms.translate2DH(0.08f, -0.14f));  // M = M * T
		cat_xf.mul(Transforms.scale2DH(0.18f,  0.18f));  // M = M * S
		draw_house(gl, house_xf);
		draw_cat(gl, cat_xf);		
	}
	
	// Draw a house.  The house is 2/3 by 1 unit in size and
	// is centered in the unit square [0,1] x [0,1].
	void draw_house(GL2 gl, Matrix3f transform) {
		twoDimProgram.setModelView(gl, transform);

		// Draw some triangles
		twoDimProgram.setColor(gl, HOUSE_SHAPE_COLOR);
		houseShapeArray.draw(gl);

		// Draw some lines
		twoDimProgram.setColor(gl, HOUSE_OUTLINE_COLOR);
		houseOutlineArray.draw(gl);		
	}

	// Draw a cat.  The cat is centered in the unit square [0,1] x [0,1].
	void draw_cat(GL2 gl, Matrix3f transform) {
		twoDimProgram.setModelView(gl, transform);

		// Draw some triangles
		twoDimProgram.setColor(gl, CAT_SHAPE_COLOR);
		catShapeArray.draw(gl);

		// Draw some lines
		twoDimProgram.setColor(gl, CAT_OUTLINE_COLOR);
		catOutlineArray.draw(gl);		
	}
	
	// Draw a unit circle (from -1 to 1 on each axis)
/*	void draw_circle(GL2 gl, Vector3f color, Matrix3f transform) {
		solidColor2DProgram.setTransform(transform);
		solidColor2DProgram.setColor(color);
		circleArray.draw();
	}
*/
	// Draw a "unit" square (from -1 to 1 on each axis)
/*	void draw_square(GL2 gl, Vector3f color, Matrix3f transform) {
		solidColor2DProgram.setTransform(transform);
		solidColor2DProgram.setColor(color);
		squareArray.draw();
	}
*/
	// Draw a grid with one line per unit, and a fainter grid
	// with 5 lines per unit.  Apply the given transformation.
	void draw_grid(GL2 gl, Vector3f color, Matrix3f transform) {
		Matrix3f minorTransform = new Matrix3f(transform);
		minorTransform.mul(Transforms.scale2DH(0.2f));
		Vector3f minorColor = new Vector3f(color);
		minorColor.interpolate(BACKGROUND_COLOR, 0.5f);
		twoDimProgram.setModelView(gl, minorTransform);
		twoDimProgram.setColor(gl, minorColor);
		gridArray.draw(gl);
		twoDimProgram.setModelView(gl, transform);
		twoDimProgram.setColor(gl, color);
		gridArray.draw(gl);
		gl.glLineWidth(5.0f);
		twoDimProgram.setColor(gl, color);
		coordinateFrameArray.draw(gl);
		gl.glLineWidth(1.0f);
	}
	
	public static void main(String[] args) {	
		new DemoTransformsInClass().run();
	}
	
	private static final long serialVersionUID = -7363006874041766005L;
}























/****
		// Set the background color
		clear_framebuffer(gl, BACKGROUND_COLOR);
		
		// Set up for straightforward drawing
		Program.use(solidColor2DProgram);
		
		// M = identity
		Matrix3f transform = new Matrix3f();
		transform.setIdentity();
		transform.mul(Transforms.scale2DH(1.0f/3));
		System.out.println("M = ");
		System.out.print(transform);

		draw_grid(gl, GRID1_COLOR, transform);

		// multiply on more transformations
		transform.mul(getTranslate("T1"));  // M = M * T1
		//draw_grid(gl, GRID2_COLOR, transform);
		
		transform.mul(getRotate("R1"));     // M = M * R1
		//draw_grid(gl, GRID3_COLOR, transform);
		
		transform.mul(getScale("S1"));      // M = M * S1
		
		draw_grid(gl, GRID4_COLOR, transform);
		draw_house(gl, transform);

		Program.unuse();


****/