package cg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;

public class Quadrics extends JFrame implements GLEventListener{

	final GLProfile profile = GLProfile.get(GLProfile.GL2);
	GLCapabilities capabilities = new GLCapabilities(profile);
	final GLCanvas glcanvas = new GLCanvas(capabilities);
	private GLU glu  = new GLU();
	FPSAnimator animator;
	int startList;
	
	public static void main(String[] args) {
		new Quadrics().setVisible(true);
	}
	
	public Quadrics() {
		super("Torus");
		glcanvas.setSize(200, 200);
		glcanvas.addGLEventListener(this);
		getContentPane().add(glcanvas);
		animator = new FPSAnimator(glcanvas, 60, true);
		animator.start();	
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			 @Override
	            public void windowClosing(WindowEvent e) {
	               // Use a dedicated thread to run the stop() to ensure that the
	               // animator stops before program exits.
	               new Thread() {
	                  @Override
	                  public void run() {
	                     if (animator.isStarted()) animator.stop();
	                     System.exit(0);
	                  }
	               }.start();
	            }
		});
	}
	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
	   gl.glClear (GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	   gl.glPushMatrix();

	   gl.glEnable(GL2.GL_LIGHTING);
	   gl.glShadeModel (GL2.GL_SMOOTH);
	   gl.glTranslatef(-1.0f, -1.0f, 0.0f);
	   gl.glCallList(startList);

	   gl.glShadeModel (GL2.GL_FLAT);
	   gl.glTranslatef(0.0f, 2.0f, 0.0f);
	   gl.glPushMatrix();
	   gl.glRotatef(300.0f, 1.0f, 0.0f, 0.0f);
	   gl.glCallList(startList+1);
	   gl.glPopMatrix();

	   gl.glDisable(GL2.GL_LIGHTING);
	   gl.glColor3f(0.0f, 1.0f, 1.0f);
	   gl.glTranslatef(2.0f, -2.0f, 0.0f);
	   gl.glCallList(startList+2);

	   gl.glColor3f(1.0f, 1.0f, 0.0f);
	   gl.glTranslatef(0.0f, 2.0f, 0.0f);
	   gl.glCallList(startList+3);

	   gl.glPopMatrix();
	   gl.glFlush();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
	   final GL2 gl = drawable.getGL().getGL2();
	   GLUquadric qobj;
	   FloatBuffer mat_ambient =  FloatBuffer.wrap( 
			   			new float[] { 0.5f, 0.5f, 0.5f, 1.0f });
	   FloatBuffer mat_specular = FloatBuffer.wrap( 
	   			new float[]{ 1.0f, 1.0f, 1.0f, 1.0f });
	   FloatBuffer mat_shininess =  FloatBuffer.wrap( 
	   			new float[]{ 50.0f });
	   FloatBuffer light_position =  FloatBuffer.wrap( 
	   			new float[]{ 1.0f, 1.0f, 1.0f, 0.0f });
	   FloatBuffer model_ambient =  FloatBuffer.wrap( 
	   			new float[]{ 0.5f, 0.5f, 0.5f, 1.0f });

	   gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

	   gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient);
	   gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular);
	   gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, mat_shininess);
	   gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position);
	   gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, model_ambient);

	   gl.glEnable(GL2.GL_LIGHTING);
	   gl.glEnable(GL2.GL_LIGHT0);
	   gl.glEnable(GL2.GL_DEPTH_TEST);

	/*  Create 4 display lists, each with a different quadric object.
	 *  Different drawing styles and surface normal specifications
	 *  are demonstrated.
	 */
	   startList = gl.glGenLists(4);
	   qobj = glu.gluNewQuadric();
	   //glu.gluQuadricCallback(qobj, glu.GLU_ERROR, 
	    //                  this);

	   glu.gluQuadricDrawStyle(qobj, glu.GLU_FILL); /* smooth shaded */
	   glu.gluQuadricNormals(qobj, glu.GLU_SMOOTH);
	   gl.glNewList(startList, gl.GL_COMPILE);
	      glu.gluSphere(qobj, 0.75, 15, 10);
	   gl.glEndList();

	   glu.gluQuadricDrawStyle(qobj, glu.GLU_FILL); /* flat shaded */
	   glu.gluQuadricNormals(qobj, glu.GLU_FLAT);
	   gl.glNewList(startList+1, GL2.GL_COMPILE);
	      glu.gluCylinder(qobj, 0.5, 0.3, 1.0, 15, 5);
	   gl.glEndList();

	   glu.gluQuadricDrawStyle(qobj, glu.GLU_LINE); /* all polygons wireframe */
	   glu.gluQuadricNormals(qobj, glu.GLU_NONE);
	   gl.glNewList(startList+2, gl.GL_COMPILE);
	      glu.gluDisk(qobj, 0.25, 1.0, 20, 4);
	   gl.glEndList();

	   glu.gluQuadricDrawStyle(qobj, glu.GLU_SILHOUETTE); /* boundary only  */
	   glu.gluQuadricNormals(qobj, glu.GLU_NONE);
	   gl.glNewList(startList+3, gl.GL_COMPILE);
	      glu.gluPartialDisk(qobj, 0.0, 1.0, 20, 4, 0.0, 225.0);
	   gl.glEndList();
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(0, 0, width, height);
	   gl.glMatrixMode(GL2.GL_PROJECTION);
	   gl.glLoadIdentity();
	   if (width <= height)
	      gl.glOrtho(-2.5, 2.5, -2.5*(float)height/(float)width,
	         2.5*(float)height/(float)width, -10.0, 10.0);
	   else
	      gl.glOrtho(-2.5*(float)width/(float)height,
	         2.5*(float)width/(float)height, -2.5, 2.5, -10.0, 10.0);
	   gl.glMatrixMode(GL2.GL_MODELVIEW);
	   gl.glLoadIdentity();		
	}
}
