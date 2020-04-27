package pkgfinal.project;

/**
 * *************************************************************
 * file: DayCycle.java
 *
 * author: Bryce Callender class: CS 4450 - Computer Graphics
 *
 * assignment: Final_Project 
 * date last modified: Apr 13, 2020
 *
 * purpose: ENTER PURPOSE HERE
 *
 ***************************************************************
 */
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

public class DayCycle {
    private final float DISTANCE = 200.0f;
    private final float SUN_DISTANCE = 50f;

    private FloatBuffer sunPosition;
    private FloatBuffer sunlight;

    private FloatBuffer moonPosition;
    private FloatBuffer moonlight;
    
    private double sunTheta;
    private double moonTheta;

    //method DayCycle
    //purpose: sets up the very basic sun position and the sunlight color
    DayCycle() {
//        sunPosition = BufferUtils.createFloatBuffer(4);
//        sunPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
//
        sunlight = BufferUtils.createFloatBuffer(4);
        sunlight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
    }

    //method: initDayCycle
    //purpose: init all the data needed for opengl such as the position, specular,
    //diffuse, and ambient lighting values. Will make the sun and moon opposite of 
    //each other in radians.
    void initDayCycle() {
//        glLight(GL_LIGHT0, GL_POSITION, sunPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, sunlight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, sunlight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, sunlight);//sets our ambient light
//         
        sunTheta = Math.toRadians(90); 
        moonTheta = Math.toRadians(-90);
    }
    
    //method: updateLightPositions
    //purpose: Updates the sun and moon positions moving them slowly over time
    void updateLightPositions(float deltaTime) {
        renderCube((float)(DISTANCE * Math.cos(sunTheta)), (float)(DISTANCE * Math.sin(sunTheta)), 0f, 10.0f);
        
//        glPushMatrix();
//        glBegin(GL_LINES);
//            glColor3f(1.0f,0.0f,0.0f);
//            glVertex2f((float)(DISTANCE * Math.cos(sunTheta)), (float)(DISTANCE * Math.sin(sunTheta)));
//            glVertex2f(0f, 0f);
//            
//            glVertex2f((float)(DISTANCE * Math.cos(moonTheta)), (float)(DISTANCE * Math.sin(moonTheta)));
//            glVertex2f(0f, 0f);
//        glEnd();
//        glPopMatrix();
            
        //glLight(GL_LIGHT0, GL_POSITION, sunPosition); //sets our light’s position
        
        //renderCube((float)(DISTANCE * Math.cos(moonTheta)), (float)(DISTANCE * Math.sin(moonTheta)), 0f, 10.0f);
        
        sunTheta += deltaTime;
        //moonTheta += deltaTime;
    }
    
    //method: renderCube
    //purpose: this method is responsible for creating the cube in the scene. (Sun and moon)
    private void renderCube(float x, float y, float z, float sideLength) {
        float halfLength = sideLength / 2.0f;
             
        glPushMatrix();
        
//        glLight(GL_LIGHT0, GL_POSITION, sunPosition); //sets our light’s position
        
        glTranslatef(x,y,z);
        
//        sunPosition = BufferUtils.createFloatBuffer(4);
//        sunPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
//        glLight(GL_LIGHT0, GL_POSITION, sunPosition); //sets our light’s position
        
        try {
            glBegin(GL_QUADS);
            // Top face (y = halfLength)
            // Define vertices in counter-clockwise (CCW) order with normal pointing out
            glColor3f(1.0f, 1.0f, 0.0f);     // Yellow
            glVertex3f(halfLength, halfLength, -halfLength);
            glVertex3f(-halfLength, halfLength, -halfLength);
            glVertex3f(-halfLength, halfLength, halfLength);
            glVertex3f(halfLength, halfLength, halfLength);

            // Bottom face (y = -halfLength)
            glColor3f(1.0f, 1.0f, 0.0f);     // Yellow
            glVertex3f(halfLength, -halfLength, halfLength);
            glVertex3f(-halfLength, -halfLength, halfLength);
            glVertex3f(-halfLength, -halfLength, -halfLength);
            glVertex3f(halfLength, -halfLength, -halfLength);

            // Front face  (z = halfLength)
            glColor3f(1.0f, 1.0f, 0.0f);     // Yellow
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
            glColor3f(1.0f, 1.0f, 0.0f);     // Yellow
            glVertex3f(-halfLength, halfLength, halfLength);
            glVertex3f(-halfLength, halfLength, -halfLength);
            glVertex3f(-halfLength, -halfLength, -halfLength);
            glVertex3f(-halfLength, -halfLength, halfLength);

            // Right face (x = halfLength)
            glColor3f(1.0f, 1.0f, 0.0f);     // Yellow
            glVertex3f(halfLength, halfLength, -halfLength);
            glVertex3f(halfLength, halfLength, halfLength);
            glVertex3f(halfLength, -halfLength, halfLength);
            glVertex3f(halfLength, -halfLength, -halfLength);
            glEnd();  // End of drawing color-cube
        } catch (Exception e) {}
        
        glPopMatrix();
    }
}
