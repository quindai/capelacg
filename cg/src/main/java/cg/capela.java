package cg;

import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_POSITION;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

public class capela extends JFrame implements GLEventListener {

	private static GraphicsEnvironment graphicsEnvironment;
	private GraphicsDevice[] devices;
	private static boolean isFullScreen = false;
	private static Dimension xgraphic;
	private static Point point = new Point(0, 0);
	final GLProfile profile = GLProfile.get(GLProfile.GL2);
	GLCapabilities capabilities = new GLCapabilities(profile);
	final GLCanvas glcanvas = new GLCanvas(capabilities);
	private GLU glu = new GLU();
	private GLUT glut = new GLUT();
	private GLUquadric q;
	private boolean showOuterWalls = true;
	static int roda = -45;

	//private float textureTop, textureBottom, textureLeft, textureRight;
	float door_angle = 0.0f, zoom = -40.0f, rollup = 0.0f;
	FPSAnimator animator;
	
	
	private String[] textureFileNames = { 
	         "images/wall4.jpg", 
	         "images/window1.jpg", 
	         "images/wall1.jpg", 
	         "images/jesus.jpeg",
	         "images/inwallback.jpg",
	         "images/roof1.jpg",
	         "images/wood.jpg"};

	private Texture[] textures = new Texture[ textureFileNames.length ];
    private float[] textureTops    = new float[ textureFileNames.length ];
    private float[] textureBottoms = new float[ textureFileNames.length ];
    private float[] textureLefts   = new float[ textureFileNames.length ];
    private float[] textureRights  = new float[ textureFileNames.length ];

	public capela() {
		super("Capela");
		init(); // not opengl init, this my method to initialize Class variables
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		thisFullScreen();
		/*
		 * ***** LISTENERS
		 */
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Use a dedicated thread to run the stop() to ensure that the
				// animator stops before program exits.
				new Thread() {
					@Override
					public void run() {
						if (animator.isStarted())
							animator.stop();
						System.exit(0);
					}
				}.start();
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				switch (key) {
				case KeyEvent.VK_RIGHT:
					roda -= 5 % 360;
					break;
				case KeyEvent.VK_LEFT:
					roda += 10 % 360;
					break;
				case KeyEvent.VK_DOWN:
					rollup += 2.0f;
					break;
				case KeyEvent.VK_UP:
					rollup -= 2.0f;
					break;
				case KeyEvent.VK_F1:
					thisFullScreen();
					break;
				case KeyEvent.VK_O:
					if (door_angle <= 118.0f)
						door_angle += 2.0f;
					break;
				case KeyEvent.VK_C:
					if (door_angle >= 2.0f)
						door_angle -= 2.0f;
					break;
				case KeyEvent.VK_W:
					showOuterWalls = !showOuterWalls;
					break;
				case KeyEvent.VK_ESCAPE:
					System.exit(0);
					// glutPostRedisplay();
				}
			}
		});

		addMouseWheelListener(new MouseAdapter() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				if (notches < 0) {
					zoom += 0.3f;
					/*
					 * System.out.printf("UP %d\tAmount %d\tUnits Scroll %d\n", notches,
					 * e.getScrollAmount(), e.getUnitsToScroll());
					 */
				} else {
					zoom -= 0.3f;
					/*
					 * System.out.printf("DOWN %d\tAmount %d\tUnits Scroll %d\n", notches,
					 * e.getScrollAmount(), e.getUnitsToScroll());
					 */
				}
			}
		});

	}

	public static void main(String[] args) {
		new capela().setVisible(true);
	}
	
	private void init() {
		graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		devices = graphicsEnvironment.getScreenDevices();
		glcanvas.setSize(600, 600);
		glcanvas.addGLEventListener(this);
		getContentPane().add(glcanvas);
		animator = new FPSAnimator(glcanvas, 60, true);
		animator.start();
	}

	private void thisFullScreen() {
		if (!isFullScreen) {
			this.dispose();
			this.setUndecorated(true);
			this.setVisible(true);
			this.setResizable(false);
			xgraphic = this.getSize();
			point = this.getLocation();
			this.setLocation(0, 0);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
			isFullScreen = true;
		} else {
			this.dispose();
			this.setUndecorated(false);
			this.setVisible(true);
			this.setResizable(true);
			this.setLocation(point);
			this.setSize(xgraphic);
			isFullScreen = false;
		}
	}
	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();

		// iluminacao
		/*
		 * gl.glEnable(GL2.GL_LIGHTING); gl.glEnable(GL2.GL_LIGHT0);
		 * gl.glEnable(GL2.GL_NORMALIZE); gl.glEnable(GL2.GL_COLOR_MATERIAL);
		 */
		// **

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black and opaque
		gl.glClearDepth(1.0f); // Set background depth to farthest
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enable depth testing for z-culling
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDepthFunc(GL2.GL_LEQUAL); // Set the type of depth-test
		
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); // Nice perspective corrections
		gl.glShadeModel(GL2.GL_SMOOTH); // Enable smooth shading
		
		try {
	         // Create a OpenGL Texture object from (URL, mipmap, file suffix)
	         // Use URL so that can read from JAR and disk file.
			for(int i=0; i<textures.length; i++) {
				File im = new File(textureFileNames[i]);
		        textures[i] = TextureIO.newTexture(im, // relative to project root 
		               true);
		        // Use linear filter for texture if image is larger than the original texture
	            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	            // Use linear filter for texture if image is smaller than the original texture
	            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	            TextureCoords textureCoords = textures[i].getImageTexCoords();
	            
	            textureTops[i] = textureCoords.top();
	            textureBottoms[i] = textureCoords.bottom();
	            textureLefts[i] = textureCoords.left();
	            textureRights[i] = textureCoords.right();
	            
	            System.out.printf("Textura %d\n Top:%f\n Left:%f\n Right:%f\n Bottom:%f\n",
	            				i, textureTops[i], textureLefts[i], textureRights[i],textureBottoms[i] );
			}
	      } catch (GLException e) {
	          e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
		
		// Set up the lighting for light named GL_LIGHT1

	      // Ambient light does not come from a particular direction. Need some ambient
	      // light to light up the scene. Ambient's value in RGBA
	      float[] lightAmbientValue = { 0.5f, 0.5f, 0.5f, 1.0f };
	      // Diffuse light comes from a particular location. Diffuse's value in RGBA
	      float[] lightDiffuseValue = { 1.0f, 1.0f, 1.0f, 1.0f };
	      // Diffuse light location xyz (in front of the screen).
	      float[] lightDiffusePosition = { 0.0f, 0.0f, 2.0f, 1.0f };
	      
	      gl.glLightfv(GL2.GL_LIGHT2, GL_AMBIENT, lightAmbientValue, 0);
	      gl.glLightfv(GL2.GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
	      gl.glLightfv(GL2.GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
	      gl.glEnable(GL2.GL_LIGHT1);
	      
		q = glu.gluNewQuadric();
		glu.gluQuadricNormals(q, GLU.GLU_SMOOTH);
		glu.gluQuadricTexture(q, true);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(0, 0, width, height);

		if (height <= 0)
			height = 1; // To prevent divide by 0
		float aspect = (float) width / (float) height;

		// Set the viewport to cover the new vision window
		gl.glViewport(0, 0, width, height);

		// Set the aspect ratio of the clipping volume to match the viewport
		gl.glMatrixMode(GL2.GL_PROJECTION); // To operate on the Projection matrix
		gl.glLoadIdentity(); // Reset
		// Enable perspective projection with fovy, aspect, zNear and zFar
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(gl.GL_MODELVIEW);
		
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, -0.8f, zoom);
		gl.glRotatef((float) roda, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rollup, 1.0f, 0.0f, 0.0f);

		gl.glEnable(gl.GL_LIGHTING);
		
//		texture.enable(gl);
//		texture.bind(gl);
		textures[0].enable(gl);
		textures[0].bind(gl);
		//gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glBegin(GL2.GL_QUADS);
		{ // this bracket is not necessary
			if (showOuterWalls) {
				gl.glNormal3f(0.0f, 0.0f, 1.0f);
				gl.glColor3f((float) 242 / 255, (float) 243 / 255, (float) 244 / 255);
				// front wall above door
				//gl.glTexCoord2f(textureLeft, textureBottom);
				gl.glTexCoord2f(textureLefts[0], textureBottoms[0]);
				gl.glVertex3f(-1.25f, 9.0f, 7.0f); // wall left
				//gl.glTexCoord2f(textureRight, textureBottom);
				gl.glTexCoord2f(textureRights[0], textureBottoms[0]);
				gl.glVertex3f(-7.0f, 9.0f, 7.0f);
				//gl.glTexCoord2f(textureRight, textureTop);
				gl.glTexCoord2f(textureRights[0], textureTops[0]);
				gl.glVertex3f(-7.0f, -6.0f, 7.0f);
				//gl.glTexCoord2f(textureLeft, textureTop);
				gl.glTexCoord2f(textureLefts[0], textureTops[0]);
				gl.glVertex3f(-1.25f, -6.0f, 7.0f); // coords plus 5
//texture.disable(gl);

				gl.glTexCoord2f(textureLefts[0], textureBottoms[0]);
				gl.glVertex3f(1.25f, 9.0f, 7.0f); // wall right
				gl.glTexCoord2f(textureRights[0], textureBottoms[0]);
				gl.glVertex3f(7.0f, 9.0f, 7.0f);
				gl.glTexCoord2f(textureRights[0], textureTops[0]);
				gl.glVertex3f(7.0f, -6.0f, 7.0f);
				gl.glTexCoord2f(textureLefts[0], textureTops[0]);
				gl.glVertex3f(1.25f, -6.0f, 7.0f);

				gl.glTexCoord2f(textureLefts[0], textureBottoms[0]);
				gl.glVertex3f(-5.45f, 9.0f, 7.0f); // wall up
				gl.glTexCoord2f(textureRights[0], textureBottoms[0]);
				gl.glVertex3f(5.45f, 9.0f, 7.0f);
				gl.glTexCoord2f(textureRights[0], textureTops[0]);
				gl.glVertex3f(5.45f, -1.69f, 7.0f);
				gl.glTexCoord2f(textureLefts[0], textureTops[0]);
				gl.glVertex3f(-5.45f, -1.69f, 7.0f);


				// face esquerda
				
				// Almond RGB Color Code: #EFDECD
				gl.glColor3f((float) 239 / 255, (float) 222 / 255, (float) 205 / 255);
				gl.glNormal3f(-1.0f, 0.0f, 0.0f);
				gl.glTexCoord2f(textureLefts[0], textureBottoms[0]);
				gl.glVertex3f(-7.0f, 9.0f, 7.0f);
				gl.glTexCoord2f(textureRights[0], textureBottoms[0]);
				gl.glVertex3f(-7.0f, 9.0f, -7.0f);
				gl.glTexCoord2f(textureRights[0], textureTops[0]);
				gl.glVertex3f(-7.0f, -6.0f, -7.0f);
				gl.glTexCoord2f(textureLefts[0], textureTops[0]);
				gl.glVertex3f(-7.0f, -6.0f, 7.0f);
				
				// face direita
				gl.glNormal3f(1.0f, 0.0f, 0.0f);
				gl.glTexCoord2f(textureRights[0], textureBottoms[0]);
				gl.glVertex3f(7.0f, 9.0f, -7.0f);
				gl.glTexCoord2f(textureRights[0], textureTops[0]);
				gl.glVertex3f(7.0f, 9.0f, 7.0f);
				gl.glTexCoord2f(textureLefts[0], textureTops[0]);
				gl.glVertex3f(7.0f, -6.0f, 7.0f);
				gl.glTexCoord2f(textureLefts[0], textureBottoms[0]);
				gl.glVertex3f(7.0f, -6.0f, -7.0f);

				textures[0].disable(gl);
				
				// face fundo
				// Alice Blue RGB Color Code: #F0F8FF
				gl.glNormal3f(0.0f, -1.0f, 0.0f);
				gl.glColor3f((float) 240 / 255, (float) 248 / 255, (float) 255 / 255);
				gl.glVertex3f(7.0f, -6.0f, 7.0f);
				gl.glVertex3f(-7.0f, -6.0f, 7.0f);
				gl.glVertex3f(-7.0f, -6.0f, -7.0f);
				gl.glVertex3f(7.0f, -6.0f, -7.0f);

				// back
				// Antique Brass RGB Color Code: #CD9575
				gl.glNormal3f(0.0f, 0.0f, -1.0f);
				gl.glColor3f((float) 205 / 255, (float) 149 / 255, (float) 117 / 255);
				gl.glVertex3f(7.0f, 9.0f, -7.0f);
				gl.glVertex3f(-7.0f, 9.0f, -7.0f);
				gl.glVertex3f(-7.0f, -6.0f, -7.0f);
				gl.glVertex3f(7.0f, -6.0f, -7.0f);

				// draw small house on the right
				// Anti-Flash White RGB Color Code: #F2F3F4
				gl.glNormal3f(0.0f, 0.0f, 1.0f);
				gl.glColor3f((float) 242 / 255, (float) 243 / 255, (float) 244 / 255);
				gl.glVertex3f(7.0f, 2.0f, 5.0f); // wall front
				gl.glVertex3f(10.0f, 2.0f, 5.0f);
				gl.glVertex3f(10.0f, -6.0f, 5.0f);
				gl.glVertex3f(7.0f, -6.0f, 5.0f);

				gl.glNormal3f(1.0f, 0.0f, 0.0f);
				gl.glVertex3f(10.0f, 2.0f, -7.0f); // wall right
				gl.glVertex3f(10.0f, 2.0f, 5.0f);
				gl.glVertex3f(10.0f, -6.0f, 5.0f);
				gl.glVertex3f(10.0f, -6.0f, -7.0f);

				gl.glNormal3f(0.0f, 0.0f, -1.0f);
				gl.glVertex3f(7.0f, 4.0f, -7.0f); // wall back
				gl.glVertex3f(10.0f, 2.0f, -7.0f);
				gl.glVertex3f(10.0f, -6.0f, -7.0f);
				gl.glVertex3f(7.0f, -6.0f, -7.0f);

			}
		}
		gl.glEnd();

		// correcting roof small house
		textures[5].enable(gl);
		textures[5].bind(gl);
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f((float) 215 / 255, (float) 180 / 255, (float) 145 / 255);
			gl.glNormal3f(1.0f, 0.5f, 0.0f);
			gl.glTexCoord2f(textureRights[5]/2, textureBottoms[5]);
			gl.glVertex3f(10.0f, 2.0f, -7.0f); // wall right
			gl.glTexCoord2f(textureRights[5], textureTops[5]);
			gl.glVertex3f(7.0f, 4.0f, -7.0f);
			gl.glTexCoord2f(textureLefts[5], textureTops[5]);
			gl.glVertex3f(7.0f, 4.0f, 2.0f);
			gl.glTexCoord2f(textureLefts[5], textureBottoms[5]);
			gl.glVertex3f(10.0f, 2.0f, 5.0f);
		gl.glEnd();
		textures[5].disable(gl);
		
		// monumental wall inside back
		textures[4].enable(gl);
		textures[4].bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f((float) 205 / 255, (float) 149 / 255, (float) 117 / 255);
			gl.glNormal3f(0.0f, 0.0f, 1.0f);
			gl.glTexCoord2f(textureLefts[4], textureBottoms[4]);
			gl.glVertex3f(-7.0f, -6.0f, -6.7f); // wall back
			gl.glTexCoord2f(textureRights[4], textureBottoms[4]);
			gl.glVertex3f(7.0f, -6.0f, -6.7f);
			gl.glTexCoord2f(textureRights[4], textureTops[4]);
			gl.glVertex3f(7.0f, 9.0f, -6.7f);
			gl.glTexCoord2f(textureLefts[4], textureTops[4]);
			gl.glVertex3f(-7.0f, 9.0f, -6.7f);
		gl.glEnd();
		textures[4].disable(gl);
		
		textures[1].enable(gl);
		textures[1].bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f((float) 111 / 255, (float) 123 / 255, (float) 137 / 255);
		// PEQUENAS JANELAS DE FORA
		//right windows small house up
		drawSmallWindowsR(gl, 10.1f, 1.0f, 3.0f);
		drawSmallWindowsR(gl, 10.1f, 1.0f, -1.0f);
		drawSmallWindowsR(gl, 10.1f, 1.0f, -5.0f);
		
		//right windows small house down
		drawSmallWindowsR(gl, 10.1f, -3.0f, 3.0f);
		drawSmallWindowsR(gl, 10.1f, -3.0f, -1.0f);
		drawSmallWindowsR(gl, 10.1f, -3.0f, -5.0f);
		
		//big house
		drawSmallWindowsR(gl, 7.2f, 7.0f, 2.3f);
		/*gl.glVertex3f(7.2f, 7.0f, 1.0f); // right
		gl.glVertex3f(7.2f, 7.0f, 2.3f);
		gl.glVertex3f(7.2f, 5.0f, 2.3f);
		gl.glVertex3f(7.2f, 5.0f, 1.0f);*/

		drawSmallWindowsL(gl, -7.2f, 7.0f, 2.3f);
		/*gl.glVertex3f(-7.2f, 7.0f, 1.0f); // left
		gl.glVertex3f(-7.2f, 7.0f, 2.3f);
		gl.glVertex3f(-7.2f, 5.0f, 2.3f);
		gl.glVertex3f(-7.2f, 5.0f, 1.0f);
		*/
		
		// front small house window
		drawSmallWindowsF(gl, 8.2f, 1f, 5.05f);
		/*gl.glVertex3f(8.2f, 1, 5.05f);
		gl.glVertex3f(7.2f, 1, 5.05f);
		gl.glVertex3f(7.2f, -1.0f, 5.05f);
		gl.glVertex3f(8.2f, -1.0f, 5.05f);*/
					
		gl.glEnd();
		textures[1].disable(gl);
		
		// TELHADO
		drawRoof(gl);
		
		// PORTAS
		drawFrontDoor(gl);
		drawSmallFrontDoor(gl);
		
		drawFrontWindow(gl);

		drawViga(gl, 0.3f, 15.05f, 0.3f, -7.4f, 1.5f, 7); // esquerda frente
		drawViga(gl, 0.3f, 15.05f, 0.3f, 6.6f, 1.5f, 7); // direita frente
		drawViga(gl, 0.3f, 15.05f, 0.3f, 6.6f, 1.5f, -7); // esquerda tras
		drawViga(gl, 0.3f, 15.05f, 0.3f, -7.4f, 1.5f, -7); // esquerda tras

		// small house
		drawSmallHouseRoof(gl);
		drawViga(gl, 0.2f, 8.07f, 0.3f, 9.6f, -2.01f, 5); // frente
		drawViga(gl, 0.2f, 8.07f, 0.3f, 9.6f, -2.01f, -7); // tras

		// desenha a base da fachada
		drawBase(gl);

		drawChair(gl, 1.5f, -6f, -2f);
		drawChair(gl, 1.5f, -6f, 3f);
		drawChair(gl, -5.5f, -6f, -2f);
		drawChair(gl, -5.5f, -6f, 3f);
		drawSmallTable(gl, 5.5f, -6f, -4f);
		drawAltar(gl);
		verticalCylinder(gl, -1.4f, -7f, 7.1f);
		verticalCylinder(gl, 1.4f, -7f, 7.1f);
		
		drawLamp(gl, 0, 0);
		drawCross(gl);
		// verticalCylinder(gl);
		gl.glFlush();
	}

	public void drawSmallWindowsR(GL2 gl, float x, float y, float z) {
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(textureRights[1], textureBottoms[1]);
		gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(textureRights[1], textureTops[1]);
		gl.glVertex3f(x, y, z-1.3f);
		gl.glTexCoord2f(textureLefts[1], textureTops[1]);
		gl.glVertex3f(x, y-2, z-1.3f);
		gl.glTexCoord2f(textureLefts[1], textureBottoms[1]);
		gl.glVertex3f(x, y-2, z);
	}
	
	public void drawSmallWindowsL(GL2 gl, float x, float y, float z) {
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(textureLefts[1], textureBottoms[1]);
		gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(textureRights[1], textureBottoms[1]);
		gl.glVertex3f(x, y, z-1.3f);
		gl.glTexCoord2f(textureRights[1], textureTops[1]);
		gl.glVertex3f(x, y-2, z-1.3f);
		gl.glTexCoord2f(textureLefts[1], textureTops[1]);
		gl.glVertex3f(x, y-2, z);
	}
	
	public void drawSmallWindowsF(GL2 gl, float x, float y, float z) {
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(textureLefts[1], textureBottoms[1]);
		gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(textureRights[1], textureBottoms[1]);
		gl.glVertex3f(x-1, y, z);
		gl.glTexCoord2f(textureRights[1], textureTops[1]);
		gl.glVertex3f(x-1, y-2, z);
		gl.glTexCoord2f(textureLefts[1], textureTops[1]);
		gl.glVertex3f(x, y-2, z);
	}
	
	public void drawCross(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(7f, 0.09f, -4f);
		gl.glRotatef(90, 0, 1.0f, 0);
		gl.glScalef(0.60f, 0.05f, 0.5f);
		glut.glutSolidCube(1);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(7f, 0, -4f);
		gl.glScalef(0.2f, 1.02f, 0.1f);
		glut.glutSolidCube(1);
		gl.glPopMatrix();
	}

	public void drawLamp(GL2 gl, float x, float z) {
		gl.glPushMatrix();// hanging lamp
		gl.glRotatef(-90f, 1f, 0f, 0f);
		gl.glTranslated(x, -z, 9);
		gl.glColor3f(.7f, .7f, .7f);
		glu.gluCylinder(q, .1, .1, 2, 10, 10);
		gl.glColor4ub((byte)222, (byte)111, (byte)100, (byte)25);
		glu.gluSphere(q, .8, 10, 10);
		gl.glTranslated(0, 0, -1);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(.7f, .1f, .1f, .9f); // dimmer yellow, translucent
		textures[3].enable(gl);
		textures[3].bind(gl);
		glu.gluCylinder(q, 1, 1, 2, 10, 10);
		textures[3].disable(gl);
		gl.glDisable(GL2.GL_BLEND);
		gl.glPopMatrix();
	}

	private void drawRoof(GL2 gl) {
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, -0.8f, zoom);
		gl.glRotatef((float) roda, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rollup, 1.0f, 0.0f, 0.0f);

		gl.glColor3f((float) 215 / 255, (float) 180 / 255, (float) 145 / 255);
		textures[5].enable(gl);
		textures[5].bind(gl);
		gl.glBegin(GL2.GL_TRIANGLES);
		{
			// LIGHTING
			gl.glNormal3f(0.0f, 0.5f, 1.0f);
			gl.glTexCoord2f(textureRights[5]/2, textureTops[5]);
			gl.glVertex3f(0.0f, 13.0f, 0.0f); // topo -- nao muda
			gl.glTexCoord2f(textureLefts[5], textureBottoms[5]);
			gl.glVertex3f(-7.0f, 9.0f, 7.0f); // esquerda
			gl.glTexCoord2f(textureRights[5], textureBottoms[5]);
			gl.glVertex3f(7.0f, 9.0f, 7.0f); // direita

			gl.glNormal3f(0.0f, 0.5f, -1.0f);
			gl.glTexCoord2f(textureRights[5]/2, textureTops[5]);
			gl.glVertex3f(0.0f, 13.0f, 0.0f); // fundo -- nao muda
			gl.glTexCoord2f(textureLefts[5], textureBottoms[5]);
			gl.glVertex3f(-7.0f, 9.0f, -7.0f); // esquerda
			gl.glTexCoord2f(textureRights[5], textureBottoms[5]);
			gl.glVertex3f(7.0f, 9.0f, -7.0f); // direita

			gl.glNormal3f(1.0f, 0.5f, 0.0f);
			gl.glTexCoord2f(textureRights[5]/2, textureTops[5]);
			gl.glVertex3f(0.0f, 13.0f, 0.0f); // direita -- nao muda
			gl.glTexCoord2f(textureRights[5], textureBottoms[5]);
			gl.glVertex3f(7.0f, 9.0f, -7.0f); // esquerda
			gl.glTexCoord2f(textureLefts[5], textureBottoms[5]);
			gl.glVertex3f(7.0f, 9.0f, 7.0f); // direita

			gl.glNormal3f(-1.0f, 0.5f, 0.0f);
			gl.glTexCoord2f(textureRights[5]/2, textureTops[5]);
			gl.glVertex3f(0.0f, 13.0f, 0.0f); // esquerda -- nao muda
			gl.glTexCoord2f(textureLefts[5], textureBottoms[5]);
			gl.glVertex3f(-7.0f, 9.0f, 7.0f); // esquerda
			gl.glTexCoord2f(textureRights[5], textureBottoms[5]);
			gl.glVertex3f(-7.0f, 9.0f, -7.0f); // direita
		}
		gl.glEnd();
		textures[5].disable(gl);
	}

	private void drawSmallFrontDoor(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(6.6f, -4.46f, 5);
		//gl.glRotatef(door_angle, 0, 1, 0);
		gl.glTranslatef(1.2f, 0, 0);
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glScalef(1.5f, 4.51f, 0.2f);
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();
	}
	
	private void drawFrontDoor(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(-1.2f, -4.46f, 7);
		gl.glRotatef(door_angle, 0, 1, 0);
		gl.glTranslatef(1.2f, 0, 0);
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glScalef(2.5f, 5.51f, 0.2f);
		/*gl.glEnable(GL2.GL_TEXTURE_GEN_S);
		gl.glEnable(GL2.GL_TEXTURE_GEN_T);
		textures[1].enable(gl);
		textures[1].bind(gl);*/
		glut.glutSolidCube(1.0f);
	//	textures[1].disable(gl);
		gl.glDisable(GL2.GL_TEXTURE_GEN_S);
		gl.glDisable(GL2.GL_TEXTURE_GEN_T);
		
		gl.glPopMatrix();
	}

	public void drawSmallTable(GL2 gl, float lx, float y, float lz) {
		GLUquadric quadric = glu.gluNewQuadric();
		gl.glEnable(GL2.GL_TEXTURE_2D);
		// chairTex.bind(gl);
		gl.glPushMatrix();
		gl.glTranslatef(lx, y, lz);
		gl.glRotatef(-90f, 1f, 0f, 0f);
		glu.gluCylinder(quadric, 0.5f, 0f, 2f, 10, 10);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(lx, y + 2, lz);
		gl.glRotatef(-90f, 1f, 0f, 0f);
		glu.gluDisk(quadric, 1f, 0f, 10, 10);
		gl.glPopMatrix();
		gl.glDisable(GL2.GL_TEXTURE_2D);

	}

	public void drawChair(GL2 gl, float xpos, float y, float zpos) {
		
		textures[6].enable(gl);
		textures[6].bind(gl);
		gl.glColor3d(.55, .2, .2);
		gl.glPushMatrix();
		gl.glTranslated(xpos, y, zpos+0.5);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(q, .25, .25, 1, 5, 5);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(xpos + 4, y, zpos+0.5);
		gl.glRotated(-90, 1, 0, 0);
		//quadric, base (radius), top(radius), height, slices, stacks
		glu.gluCylinder(q, .25, .25, 1, 5, 5);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(xpos + 4, y, zpos + 2);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(q, .25, .25, 3, 5, 6);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(xpos, y, zpos + 2);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(q, .25, .25, 3, 5, 5);
		gl.glPopMatrix();

		gl.glColor3d(0, 0, 1);
		gl.glPushMatrix();
		gl.glTranslated(xpos + 2, y + 1, zpos + 1);
		// gl.glRotated(-90,1,0,0);
		gl.glScalef(4.5f, 0.2f, -1.5f);
		// glu.gluPartialDisk(q, .01, 2.5, 10, 10, -90, 180);
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();

		gl.glColor3d(0, .5, .5);
		gl.glPushMatrix(); 
		gl.glTranslated(xpos+2, y + 1.8, zpos + 2);
		// gl.glRotated(-90,1,0,0);
		glu.gluQuadricNormals(q, glu.GLU_SMOOTH);
		// quadric, inner, outer, slices, loops, start, sweep
		glu.gluPartialDisk(q, .01, 2, 10, 10, -90, 180);
		gl.glPopMatrix();
		textures[6].disable(gl);
	}

	public void drawTable(GL2 gl, double xpos, double zpos) {
		
		gl.glColor3d(.5, .2, .2);
		gl.glPushMatrix();
		gl.glTranslated(xpos, 0, zpos);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(q, .25, .25, 3, 5, 5);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(xpos + 4, 0, zpos);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(q, .25, .25, 3, 5, 5);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(xpos + 4, 0, zpos + 4);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(q, .25, .25, 3, 5, 6);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(xpos, 0, zpos + 4);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(q, .25, .25, 3, 5, 5);
		gl.glPopMatrix();

		gl.glColor3d(.7, .4, .4);
		gl.glPushMatrix();
		gl.glTranslated(xpos + 2.5, 3, zpos + 2.5);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluDisk(q, .001, 5, 10, 10);
		gl.glPopMatrix();
	}

	private void drawChair(GL2 gl) {
		// g = glu.gluNewQuadric();
		gl.glPushMatrix();
		// encosto
		gl.glTranslatef(-0.1f, 4.0f, 9.06f); // translate is cumulative
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glScalef(2.5f, 0.5f, 0.1f);
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(-0.2f, -0.5f, 0.0f);
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glScalef(0.08f, 0.85f, 0.2f);
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.2f, -0.5f, 0.0f);
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glScalef(0.08f, 0.85f, 0.2f);
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();
	}

	private void drawFrontWindow(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(-0.1f, 4.0f, 7.06f); // posicao canto esquerdo
		gl.glColor3f((float) 111 / 255, (float) 123 / 255, (float) 137 / 255);
		gl.glScalef(4f, 5.5f, 0.3f);
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();
	}

	private void drawViga(GL2 gl, float scalex, float scaley, float scalez, float tr_x, float tr_y, float tr_z) {
		gl.glPushMatrix();
		gl.glTranslatef(tr_x, tr_y, tr_z);
		// glRotatef (door_angle, 0,1,0);
		gl.glTranslatef(0.4f, 0f, 0f);
		gl.glColor3f((float) 115 / 255, (float) 99 / 255, (float) 86 / 255);
		gl.glScalef(scalex, scaley, scalez);
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();
	}

	private void drawSmallHouseRoof(GL2 gl) {
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, -0.8f, zoom);
		gl.glRotatef((float) roda, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rollup, 1.0f, 0.0f, 0.0f);

		gl.glNormal3f(0.0f, 0.5f, 1.0f);
		gl.glColor3f((float) 215 / 255, (float) 180 / 255, (float) 145 / 255);
		
		textures[5].enable(gl);
		textures[5].bind(gl);
		gl.glBegin(GL2.GL_TRIANGLES);
		gl.glTexCoord2f(textureRights[5]/2, textureTops[5]);
		gl.glVertex3f(7.0f, 4.0f, 2.0f); // topo -- nao muda
		gl.glTexCoord2f(textureLefts[5], textureBottoms[5]);
		gl.glVertex3f(7.0f, 2.0f, 5.0f); // esquerda
		gl.glTexCoord2f(textureRights[5], textureBottoms[5]);
		gl.glVertex3f(10.0f, 2.0f, 5.0f); // direita
		gl.glEnd();
		textures[5].disable(gl);
	}

	private void drawBase(GL2 gl) {
		// center
		gl.glPushMatrix();
		gl.glTranslatef(-0.4f, -6.20f, 0.06f); // move x,y,z
		gl.glTranslatef(0.4f, 0, 0);
		gl.glColor3f((float) 87 / 255, (float) 33 / 255, (float) 14 / 255);
		gl.glScalef(0.9f, 0.6f, 13.7f); // largura, altura, profundidade
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();

		gl.glPushMatrix(); // base
		gl.glTranslatef(0.6f, -7.15f, 0.06f); // move x,y,z
		gl.glTranslatef(0.9f, 0, 4.95f);
		gl.glColor3f((float) 59 / 255, (float) 37 / 255, (float) 23 / 255);
		gl.glScalef(17, 2.0f, 25.0f); // largura, altura, profundidade
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();
	}
	
	private void drawAltar(GL2 gl){
		gl.glPushMatrix(); // base
		gl.glTranslatef(0.0f, -4.0f, -5.5f); // move x,y,z
		//gl.glTranslatef(0.9f, 0, 4.95f);
		gl.glColor3d(.55, .2, .2);
		gl.glScalef(5, 0.5f, 2.0f); // largura, altura, profundidade
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();
		
		gl.glPushMatrix(); 
		gl.glTranslatef(0.0f, -5.0f, -5.5f); // move x,y,z
		//gl.glTranslatef(0.9f, 0, 4.95f);
		gl.glColor3d(.55, .2, .2);
		gl.glScalef(1, 1.5f, 0.5f); // largura, altura, profundidade
		glut.glutSolidCube(1.0f);
		gl.glPopMatrix();
	}

	private void verticalCylinder(GL2 gl, float x, float y, float z) {
		textures[2].enable(gl);
		textures[2].bind(gl);
			gl.glPushMatrix();
			gl.glTranslated(x, y, z);
			gl.glRotated(-90, 1, 0, 0);
			gl.glColor3f((float) 111 / 255, (float) 123 / 255, (float) 137 / 255);
			//quadric, base (radius), top(radius), height, slices, stacks
			glu.gluCylinder(q, .25, .25, 5.3, 10, 10);
			gl.glPopMatrix();
		textures[2].disable(gl);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

		glu.gluDeleteQuadric(q);
	}

	
}