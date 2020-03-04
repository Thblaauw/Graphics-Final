/** *************************************************************
 * file: FinalProject.java
 * authors: Thomas Blaauw Barbosa, Bryce Callender, Jordan Laidig
 * class: CS 4450 – Intro to Computer Graphics
 *
 * assignment: Final Project
 * date last modified: 3/4/2020
 *
 * purpose: creates a cube and camera to move around and view it
 *
 *************************************************************** */
package pkgfinal.project;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.lwjgl.util.glu.GLU;

public class FinalProject {

    private final int WIDTH = 640;
    private final int HEIGHT = 480;

    private CameraController camera = new CameraController(0f, 0f, -15f); //creates the camera
    private DisplayMode displayMode;

    //method: Start
    //purpose: This is used to start the window and run the program
    public void start() {
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
        glEnable(GL_DEPTH_TEST); //allows objects to be seen in 3D.
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