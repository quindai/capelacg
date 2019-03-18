package cg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

//jogl gl2 : google
//http://www.cs.cornell.edu/courses/cs4620/2012fa/lectures/09opengl01.pdf
public class PolygonTextures implements GLEventListener{
	private static GraphicsEnvironment graphicsEnvironment;
	private static boolean isFullScreen = false;
	public static DisplayMode dm,dm_old;
	private static Dimension xgraphic;
	private static Point point = new Point(0,0);
	private GLU glu  = new GLU();
	private float xrot=0.0f;
	private float yrot=0.0f;
	private float zrot=0.0f;
	private int texture;
	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		final GL2 gl = drawable.getGL().getGL2();
		gl.glShadeModel(GL2.GL_SMOOTH);
		
		gl.glClearColor(0f,0f,0f,1.0f);
		gl.glClearDepth(1.0f);
		
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT,GL2.GL_NICEST);
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		try{
			File im = new File("images/box.jpg");
			Texture t = TextureIO.newTexture(im,true);
			texture = t.getTextureObject(gl);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		final GL2 gl = drawable.getGL().getGL2();
	}
	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();//important
		gl.glTranslatef(0f,0f,-5.0f);
		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
		gl.glBegin(GL2.GL_QUADS);
		//front
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		//back face
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f, -1.0f);
		//top face
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f, -1.0f);
		//bottom face
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		//right face
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f, 1.0f);
		//left face
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		
		gl.glEnd();
		gl.glFlush();
		xrot+=.1f;
		yrot+=.1f;
		//zrot+=.08f;
	}
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		final GL2 gl = drawable.getGL().getGL2();
		
		if(height<=0) height = 1;
		final float h = (float) width/(float)height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f,h,1.0,20.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	public static void main(String[] args){
		
		final GLProfile profile;
		profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities (profile);
		
		final GLCanvas glcanvas = new GLCanvas(capabilities);
		PolygonTextures r = new PolygonTextures();
		glcanvas.addGLEventListener(r);
		glcanvas.setSize(400,400);
		final FPSAnimator animator = new FPSAnimator (glcanvas,300,true);
		final JFrame frame = new JFrame ("textures");
		frame.getContentPane().add(glcanvas);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(animator.isStarted()) animator.stop();
				System.exit(0);
			}
		});
		frame.setSize(frame.getContentPane().getPreferredSize());
		graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = graphicsEnvironment.getScreenDevices();
		dm_old = devices[0].getDisplayMode();
		dm = dm_old;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int windowX = Math.max(0, (screenSize.width - frame.getWidth())/2);
		int windowY = Math.max(0, (screenSize.height - frame.getWidth())/2);
		frame.setLocation(windowX,windowY);
		frame.setVisible(true);
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(0,0));
		frame.add(p,BorderLayout.SOUTH);
		keyBindings(p,frame,r);
		animator.start();
		
	}
	
	private static void keyBindings(JPanel p, final JFrame frame, PolygonTextures r){
		ActionMap actionMap = p.getActionMap();
		InputMap inputMap = p.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0), "F1");
		actionMap.put("F1", new AbstractAction(){

			//private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fullScreen(frame);
			}
			
		});
	}
	protected static void fullScreen(JFrame f){
		if(!isFullScreen){
			f.dispose();
			f.setUndecorated(true);
			f.setVisible(true);
			f.setResizable(false);
			xgraphic = f.getSize();
			point = f.getLocation();
			f.setLocation(0,0);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			f.setSize((int)screenSize.getWidth(), (int)screenSize.getHeight());
			isFullScreen = true;
		}
		else{
			f.dispose();
			f.setUndecorated(false);
			f.setVisible (true);
			f.setResizable(true);
			f.setLocation(point);
			f.setSize(xgraphic);
			isFullScreen= false;
		}
		
	}
}
