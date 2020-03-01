/***************************************************************
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
****************************************************************/ 
package project2;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Project2 {
    
   
    
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
    private void createWindow() throws Exception{
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
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
        glOrtho(-320, 320, -240, 240, 100, -100);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //method: applytransformations
    //purpose: recieve a set of transformations and apply them in order
    private void applyTransformations(ArrayList<String> stack){
        for(int i = stack.size() - 1; i >= 0; i--){
            String transformation = stack.get(i);
            if(transformation.charAt(0) == 'r'){
                //parse into values
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                int angle = Integer.parseInt(transformation.substring(0, transformation.indexOf(" ")));
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                int x_pivot = Integer.parseInt(transformation.substring(0, transformation.indexOf(" ")));
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                int y_pivot = Integer.parseInt(transformation.substring(0, transformation.length()));
                
                glRotatef(angle, x_pivot, y_pivot, 1);
            }// end r
            else if(transformation.charAt(0) == 't'){
                //parse into values
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                int x_coord = Integer.parseInt(transformation.substring(0, transformation.indexOf(" ")));
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                int y_coord = Integer.parseInt(transformation.substring(0, transformation.length()));
                
                glTranslatef( x_coord, y_coord, 0);
            }//end t
            else if(transformation.charAt(0) == 's'){
                //parse into values
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                float x_value = Float.parseFloat(transformation.substring(0, transformation.indexOf(" ")));
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                float y_value = Float.parseFloat(transformation.substring(0, transformation.indexOf(" ")+1));
                
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                int x_pivot = Integer.parseInt(transformation.substring(0, transformation.indexOf(" ")));
                transformation = transformation.substring(transformation.indexOf(" ")+1);
                int y_pivot = Integer.parseInt(transformation.substring(0, transformation.length()));
                
                glTranslatef( -x_pivot, -y_pivot, 0);
                glScalef(x_value, y_value, 0);
                glTranslatef( x_pivot, y_pivot, 0);
            }//end s
        }
    }
    
   
    
    private void renderCube(float sideLength){
        float halfLength = sideLength / 2.0f;
        try{
            glBegin(GL_QUADS); 
                // Top face (y = halfLength)
                // Define vertices in counter-clockwise (CCW) order with normal pointing out
                glColor3f(0.0f, 1.0f, 0.0f);     // Green
                glVertex3f( halfLength, halfLength, -halfLength);
                glVertex3f(-halfLength, halfLength, -halfLength);
                glVertex3f(-halfLength, halfLength,  halfLength);
                glVertex3f( halfLength, halfLength,  halfLength);

                // Bottom face (y = -halfLength)
                glColor3f(1.0f, 0.5f, 0.0f);     // Orange
                glVertex3f( halfLength, -halfLength,  halfLength);
                glVertex3f(-halfLength, -halfLength,  halfLength);
                glVertex3f(-halfLength, -halfLength, -halfLength);
                glVertex3f( halfLength, -halfLength, -halfLength);

                // Front face  (z = halfLength)
                glColor3f(1.0f, 0.0f, 0.0f);     // Red
                glVertex3f( halfLength,  halfLength, halfLength);
                glVertex3f(-halfLength,  halfLength, halfLength);
                glVertex3f(-halfLength, -halfLength, halfLength);
                glVertex3f( halfLength, -halfLength, halfLength);

                // Back face (z = -halfLength)
                glColor3f(1.0f, 1.0f, 0.0f);     // Yellow
                glVertex3f( halfLength, -halfLength, -halfLength);
                glVertex3f(-halfLength, -halfLength, -halfLength);
                glVertex3f(-halfLength,  halfLength, -halfLength);
                glVertex3f( halfLength,  halfLength, -halfLength);

                // Left face (x = -halfLength)
                glColor3f(0.0f, 0.0f, 1.0f);     // Blue
                glVertex3f(-halfLength,  halfLength,  halfLength);
                glVertex3f(-halfLength,  halfLength, -halfLength);
                glVertex3f(-halfLength, -halfLength, -halfLength);
                glVertex3f(-halfLength, -halfLength,  halfLength);

                // Right face (x = halfLength)
                glColor3f(1.0f, 0.0f, 1.0f);     // Magenta
                glVertex3f(halfLength,  halfLength, -halfLength);
                glVertex3f(halfLength,  halfLength,  halfLength);
                glVertex3f(halfLength, -halfLength,  halfLength);
                glVertex3f(halfLength, -halfLength, -halfLength);
             glEnd();  // End of drawing color-cube
        }catch(Exception e ){}
    }
    
    //method: render
    //purpose: this method is responsible for reading the file and calling
    //the method to draw the correct object. Also responsible for controling the
    //render of the application
    private void render() {          
        while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            try{
                
                renderCube(100.0f);
                
                Display.update();
                Display.sync(60);
            }catch(Exception e){}
            
        }
        Display.destroy();
        
    }
    
    //method: main
    //purpose: this method calls start so that all methods do not need to be static
    public static void main(String[] args) {
        
        Project2 basic = new Project2();
        basic.start();
    }  
}


