/** *************************************************************
 * file: Basic.java
 * author: Thomas Blaauw Barbosa
 * class: CS 4450 – Intro to Computer Graphics
 *
 * assignment: program 2
 * date last modified: 2/23/2020
 *
 * purpose: This program reads objects and their coordinates from a file named
 * coordinates.txt and draws them on a screen with specific colors, apply transformations and
 * shows them on the screen
 *
 *************************************************************** */
package project2;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Project2 {
    private int dx = 0;
    private int dy = 0;
    private int dz = 0;
    
    private int roll = 0;
    private int pitch = 0;
    private int yaw = 0;
    
    private final int WIDTH = 640;
    private final int HEIGHT = 480;

    //method: Start
    //purpose: This is used to start the window and run the program
    public void start() {
        try {
            createWindow();
            initGL();
            render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method: createWindow
    //purpose: This method creates a blank window with the speccified dimensions
    // setting position, name and mode.
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
        Display.setTitle("Project 2");
        Display.create();
    }

    //method: initGL
    //purpose: This method initilizes the GL and defines the coordinate system
    //used in the program
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        glOrtho(-WIDTH/2.0, WIDTH/2.0, -HEIGHT/2.0, HEIGHT/2.0, 100, -100);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    private void renderCube(float sideLength) {
        float halfLength = sideLength / 2.0f;
        try {
            glBegin(GL_QUADS);
            // Top face (y = halfLength)
            // Define vertices in counter-clockwise (CCW) order with normal pointing out
            glColor3f(0.0f, 1.0f, 0.0f);     // Green
            glVertex3f(halfLength, halfLength, -halfLength);
            glVertex3f(-halfLength, halfLength, -halfLength);
            glVertex3f(-halfLength, halfLength, halfLength);
            glVertex3f(halfLength, halfLength, halfLength);

            // Bottom face (y = -halfLength)
            glColor3f(1.0f, 0.5f, 0.0f);     // Orange
            glVertex3f(halfLength, -halfLength, halfLength);
            glVertex3f(-halfLength, -halfLength, halfLength);
            glVertex3f(-halfLength, -halfLength, -halfLength);
            glVertex3f(halfLength, -halfLength, -halfLength);

            // Front face  (z = halfLength)
            glColor3f(1.0f, 0.0f, 0.0f);     // Red
            glVertex3f(halfLength, halfLength, halfLength);
            glVertex3f(-halfLength, halfLength, halfLength);
            glVertex3f(-halfLength, -halfLength, halfLength);
            glVertex3f(halfLength, -halfLength, halfLength);

            // Back face (z = -halfLength)
            glColor3f(1.0f, 1.0f, 0.0f);     // Yellow
            glVertex3f(halfLength, -halfLength, -halfLength);
            glVertex3f(-halfLength, -halfLength, -halfLength);
            glVertex3f(-halfLength, halfLength, -halfLength);
            glVertex3f(halfLength, halfLength, -halfLength);

            // Left face (x = -halfLength)
            glColor3f(0.0f, 0.0f, 1.0f);     // Blue
            glVertex3f(-halfLength, halfLength, halfLength);
            glVertex3f(-halfLength, halfLength, -halfLength);
            glVertex3f(-halfLength, -halfLength, -halfLength);
            glVertex3f(-halfLength, -halfLength, halfLength);

            // Right face (x = halfLength)
            glColor3f(1.0f, 0.0f, 1.0f);     // Magenta
            glVertex3f(halfLength, halfLength, -halfLength);
            glVertex3f(halfLength, halfLength, halfLength);
            glVertex3f(halfLength, -halfLength, halfLength);
            glVertex3f(halfLength, -halfLength, -halfLength);
            glEnd();  // End of drawing color-cube
        } catch (Exception e) {}
    }

    //method: render
    //purpose: this method is responsible for reading the file and calling
    //the method to draw the correct object. Also responsible for controling the
    //render of the application
    private void render() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            try {
                processInput();
                glLoadIdentity(); 
                glEnable(GL_DEPTH_TEST);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                
                glPushMatrix();
                glTranslatef((WIDTH/2.0f)+dx,(HEIGHT/2.0f)+dy,0f+dz);
                
                glRotatef(0f+pitch,1f,0f,0f);
                glRotatef(0f+yaw,0f,1f,0f);
                glRotatef(0f+roll,0f,0f,1f);
                
                renderCube(100.0f);
                glPopMatrix();

                Display.update();
                Display.sync(60);
            } catch (Exception e) {}
        }
        Display.destroy();

    }

    //method:processInput
    //purpose: Takes user input and moves the camera according to the button pressed
    private void processInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            dz--;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            dx--;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            dz++;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            dx++;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            dy--;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            dy++;
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            roll--;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            roll++;
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            pitch--;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
            pitch++;
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            yaw--;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
            yaw++;
        }
        
    }
    
    //method: main
    //purpose: this method calls start so that all methods do not need to be static
    public static void main(String[] args) {
        Project2 basic = new Project2();
        basic.start();
    }
}
