package cg;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

public class Torus extends JFrame implements GLEventListener{

	final GLProfile profile = GLProfile.get(GLProfile.GL2);
	GLCapabilities capabilities = new GLCapabilities(profile);
	final GLCanvas glcanvas = new GLCanvas(capabilities);
	private GLU glu  = new GLU();
	FPSAnimator animator;
	int theTorus;
	float rodax = 30.0f, roday=30.0f;
	public static void main(String[] args) {
		new Torus().setVisible(true);;
	}
	
	public Torus() {
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
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				   case 'x':
				   case 'X':
				     rodax +=10;
				    
				      break;
				   case 'y':
				   case 'Y':
				     roday +=10;
				     break;
				   case 27:
				      System.exit(0);
				      break;
				   }
			}
		});
	}
	
	
	static void torus(GL2 gl, int numc, int numt)
	{
	   int i, j, k;
	   double s, t, x, y, z, twopi;

	   twopi = 2 * (double)Math.PI;
	   for (i = 0; i < numc; i++) {
	      gl.glBegin(GL2.GL_QUAD_STRIP);
	      for (j = 0; j <= numt; j++) {
	         for (k = 1; k >= 0; k--) {
	            s = (i + k) % numc + 0.5;
	            t = j % numt;

	            x = (1+.1*Math.cos(s*twopi/numc))*Math.cos(t*twopi/numt);
	            y = (1+.1*Math.cos(s*twopi/numc))*Math.sin(t*twopi/numt);
	            z = .1 * Math.sin(s * twopi / numc);
	            gl.glVertex3f((float)x, (float)y, (float)z);
	         }
	      }
	      gl.glEnd();
	   }
	}
	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();
	    glu.gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0);
	      
		gl.glRotatef(rodax,1.0f,0.0f,0.0f);
		gl.glRotatef(roday,0.0f,1.0f,0.0f);
		gl.glColor3f (1.0f, 1.0f, 1.0f);
		gl.glCallList(theTorus);
		gl.glFlush();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		theTorus = gl.glGenLists (1);
		gl.glNewList(theTorus, GL2.GL_COMPILE);
			torus(gl, 8, 25);
		gl.glEndList();

		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(30f, width/height, 1.0f, 100.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0);
	}

}
