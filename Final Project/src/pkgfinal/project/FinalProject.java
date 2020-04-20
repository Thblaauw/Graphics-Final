/** *************************************************************
 * file: FinalProject.java
 * 
 * authors: Thomas Blaauw Barbosa, Bryce Callender, Jordan Laidig
 * class: CS 4450 – Intro to Computer Graphics
 *
 * assignment: Final Project
 * date last modified: 3/4/2020
 *
 * purpose: creates a textured chunk and camera to move around and view it
 *
 *************************************************************** */
package pkgfinal.project;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class FinalProject {
    private final int WIDTH = 640;
    private final int HEIGHT = 480;

    private CameraController camera;
    private DisplayMode displayMode;

    //method: Start
    //purpose: This is used to start the window and run the program
    public void start() {
        camera = new CameraController(0f, 0f, -20f); //creates the camera
        try {
            createWindow();
            initGL();
            camera.gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method: createWindow
    //purpose: This method creates a blank window with the speccified dimensions
    // setting position, name and mode.
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) { //grabs the display mode with the desired ratio.
            if (d[i].getWidth() == WIDTH && d[i].getHeight() == HEIGHT && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("The Best Minecraft");
        Display.create();
    }

    //method: initGL
    //purpose: This method initilizes the GL and defines the coordinate system
    //used in the program
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        //glEnableClientState(GL_NORMAL_ARRAY);
        glEnable(GL_DEPTH_TEST); //allows objects to be seen in 3D.
        
        //Texture mapping necessities
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        glEnable(GL_LIGHTING); //enables our lighting
        glEnable(GL_LIGHT0);   //enables light0 (sun)
        
        //Allows the alpha channel to be exposed and shown to the user
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        
        glEnable(GL_COLOR_MATERIAL);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        GLU.gluPerspective(100f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300f); //gives a perspective view.

        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //method: main
    //purpose: this method calls start so that all methods do not need to be static
    public static void main(String[] args) {
        FinalProject basic = new FinalProject();
        basic.start();
    }
}
