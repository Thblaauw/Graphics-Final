/** *************************************************************
 * file: CameraController.java
 * authors: Thomas Blaauw Barbosa, Bryce Callender, Jordan Laidig
 * class: CS 4450 – Intro to Computer Graphics
 *
 * assignment: Final Project
 * date last modified: 3/4/2020
 *
 * purpose: The Camera that renders the scene and is able to move in a 1st person view.
 *
 *************************************************************** */
package pkgfinal.project;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class CameraController {
    private Vector3f position;
    private Vector3f look;
    
    private float pitch = 0;
    private float yaw = 0;
    
    //method: CameraController
    //purpose: the constructor that initilizes the camera.
    CameraController(float x, float y, float z){
        position = new Vector3f(x, y, z);
        look = new Vector3f(0f, 15f, 0f);
    }
    
    //method: updateYaw
    //purpose: changes the yaw
    public void updateYaw(float amount){
        yaw += amount;
    }
    
    //method: updatePitch
    //purpose: changes the pitch and makes sure it cant cross a certain threshold.
    public void updatePitch(float amount){
        if(pitch - amount >= -89f && pitch - amount <= 89f) //checks whether the pitch is in the correct moveable range
            pitch -= amount;
        else if(pitch - amount < -89) //forces the pitch to be in the correct range.
            pitch = -89;
        else
            pitch = 89;
    }
    
    //method: moveForward
    //purpose: allows the user to move the camera forward in relation to where its looking
    public void moveForward(float distance){
        float dx = distance * (float)Math.sin(Math.toRadians(yaw));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= dx;
        position.z += dz;
        
    }
    
    //method: moveBackward
    //purpose: allows the user to move the camera backward in relation to where its looking
    public void moveBackward(float distance){
        float dx = distance * (float)Math.sin(Math.toRadians(yaw));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += dx;
        position.z -= dz;
    }
    
    //method: moveLeft
    //purpose: allows the user to move the camera left in relation to where its looking
    public void moveLeft(float distance){
        float dx = distance * (float)Math.sin(Math.toRadians(yaw - 90));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw - 90));
        position.x -= dx;
        position.z += dz;
    }
    
    //method: moveRight
    //purpose: allows the user to move the camera right in relation to where its looking
    public void moveRight(float distance){
        float dx = distance * (float)Math.sin(Math.toRadians(yaw + 90));
        float dz = distance * (float)Math.cos(Math.toRadians(yaw + 90));
        position.x -= dx;
        position.z += dz;
    }
    
    //method: moveUp
    //purpose: allows the user to move the camera up in relation to where its looking
    public void moveUp(float distance){
        position.y += distance;
    }
    
    //method: moveDown
    //purpose: allows the user to move the camera down in relation to where its looking
    public void moveDown(float distance){
        position.y -= distance;
    }
    
    public void lookAt(){
        glRotatef(pitch, 1f, 0f, 0f);
        glRotatef(yaw, 0f, 1f, 0f);
        glTranslatef(position.x, position.y, position.z);
    }
    
    //method: gameLoop
    //purpose: The loop for what the camera sees.
    public void gameLoop(){
        CameraController camera = new CameraController(position.x, position.y, position.z); //initializes the cameras place.
        float dx;
        float dy;
        float deltaTime;
        float previousTime;
        long time;
        
        float mouseSensitivity = 0.1f; //used to see how fast the camera view moves.
        float movementSpeed = 0.4f; //used to see how fast the player can move.
        Mouse.setGrabbed(true); //makes sure the cursor isnt in the way.
        
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            
            time = Sys.getTime();
            previousTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            
            camera.updateYaw(dx * mouseSensitivity); //rotates the camer in correspondence with the mouse movement.
            camera.updatePitch(dy * mouseSensitivity);
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                camera.moveForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                camera.moveLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                camera.moveBackward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                camera.moveRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                camera.moveDown(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                camera.moveUp(movementSpeed);
            }
            try {
                glLoadIdentity(); 
                camera.lookAt(); //updates the look vector.
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                
                glPushMatrix();
                
                render();
                glPopMatrix();

                Display.update();
                Display.sync(60);
            } catch (Exception e) {}
        }
        Display.destroy();
    }
    //method: render
    //purpose: this method is responsible for creating the scene
    private void render() {
                
        renderCube(10f);

    }
    
    //method: renderCube
    //purpose: this method is responsible for creating the cube in the scene.
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

}
