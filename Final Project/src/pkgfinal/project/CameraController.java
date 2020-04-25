/** *************************************************************
 * file: CameraController.java
 *
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
import java.util.ArrayList;

public class CameraController {

    private Vector3f position, previousPos;
    private Vector3f look;
    private Chunk viewableChunk;
    private DayCycle dayCycle;
    private Vector3f colliderSize;
    private boolean moveableUp = true, moveableDown = true, moveableLeft = true, moveableRight = true, moveableFront = true, moveableBack = true;

    private float pitch = 0;
    private float yaw = 0;

    //method: CameraController
    //purpose: the constructor that initilizes the camera.
    CameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        previousPos = new Vector3f(x-1, y-1, z-1);
        look = new Vector3f(0f, 15f, 0f);
        colliderSize = new Vector3f((3 * Chunk.CUBE_LENGTH)/4, (3 * Chunk.CUBE_LENGTH)/2, (3 * Chunk.CUBE_LENGTH)/4);
    }

    //method: updateYaw
    //purpose: changes the yaw
    public void updateYaw(float amount) {
        yaw += amount;
    };

    //method: updatePitch
    //purpose: changes the pitch and makes sure it cant cross a certain threshold.
    public void updatePitch(float amount) {
        if (pitch - amount >= -89f && pitch - amount <= 89f) //checks whether the pitch is in the correct moveable range
        {
            pitch -= amount;
        } else if (pitch - amount < -89) //forces the pitch to be in the correct range.
        {
            pitch = -89;
        } else {
            pitch = 89;
        }
    }

    //method: moveForward
    //purpose: allows the user to move the camera forward in relation to where its looking
    public void moveForward(float distance) {
        float dx = distance * (float) Math.sin(Math.toRadians(yaw));
        float dz = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x -= dx;
        position.z += dz;

    }

    //method: moveBackward
    //purpose: allows the user to move the camera backward in relation to where its looking
    public void moveBackward(float distance) {
        float dx = distance * (float) Math.sin(Math.toRadians(yaw));
        float dz = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += dx;
        position.z -= dz;
    }

    //method: moveLeft
    //purpose: allows the user to move the camera left in relation to where its looking
    public void moveLeft(float distance) {
        float dx = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float dz = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= dx;
        position.z += dz;
    }

    //method: moveRight
    //purpose: allows the user to move the camera right in relation to where its looking
    public void moveRight(float distance) {
        float dx = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float dz = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= dx;
        position.z += dz;
    }

    //method: moveUp
    //purpose: allows the user to move the camera up in relation to where its looking
    public void moveUp(float distance) {
        position.y += distance;
    }

    //method: moveDown
    //purpose: allows the user to move the camera down in relation to where its looking
    public void moveDown(float distance) {
        position.y -= distance;
    }

    public void lookAt() {
        glRotatef(pitch, 1f, 0f, 0f);
        glRotatef(yaw, 0f, 1f, 0f);
        glTranslatef(position.x, position.y, position.z);
    }
    
    //method: isOnFloor
    //purpose: verify if the player is on the floor
    boolean isOnFloor(){
        return false;
    }
   
    public boolean checkCollision(Block block){
        if(position.x + colliderSize.x >= block.getBlockCoordinates().x - block.colliderSize.x && position.x - colliderSize.x <= block.getBlockCoordinates().x + block.colliderSize.x){
            if((position.y + colliderSize.y >= block.getBlockCoordinates().y - block.colliderSize.y) && (position.y - colliderSize.y <= block.getBlockCoordinates().y + block.colliderSize.y)){
                if(position.z + colliderSize.z >= block.getBlockCoordinates().z - block.colliderSize.z && position.z - colliderSize.z <= block.getBlockCoordinates().z + block.colliderSize.z){
                    return true;
                }
            }
        }
        
        return false;
          
    }
    
    public ArrayList<Block> broadPhase(){
        ArrayList<Block> SAP = new ArrayList<Block>();
        
        for(int i = 0; i < Chunk.CHUNK_SIZE; i++){
            for(int j = 0; j < Chunk.CHUNK_SIZE; j++){
                for(int k = 0; k < Chunk.CHUNK_SIZE; k++){
                    if(viewableChunk.getBlock(i, j, k) != null){
                        if(checkCollision(viewableChunk.getBlock(i, j, k))){
                            SAP.add(viewableChunk.getBlock(i, j, k));
                        }
                    }
                }
            }
        }
        
        
        return SAP;
    }
    
    public void doCollisions(){        
        moveableLeft = true;
        moveableRight = true;
        moveableFront = true;
        moveableBack = true;
        moveableUp = true;
        moveableDown = true;
        ArrayList<Block> SAP = broadPhase();
        if(SAP.isEmpty() != true){
            for(int i = 0; i < SAP.size(); i++){
                Block block = SAP.get(i);
                if(block.getBlockID() != 2){
                    if((position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) >= 0) && (position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) <= 1)){
                        moveableLeft = false;
                    }
                    if((position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) <= 0) && (position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) >= -1)){
                        moveableRight = false;
                    }
                    if((position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) >= 0) && (position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) <= 1)){
                        moveableFront = false;
                    }
                    if((position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) <= 0) && (position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) >= -1)){
                        moveableBack = false;
                    }
                    if((position.y + colliderSize.y - (block.getBlockCoordinates().y - block.colliderSize.y) >= 0) && (position.y + colliderSize.y - (block.getBlockCoordinates().y - block.colliderSize.y) <= 2)){
                        moveableDown = false;
                    }
                    if((position.y - colliderSize.y - (block.getBlockCoordinates().y + block.colliderSize.y) <= 0) & (position.y - colliderSize.y - (block.getBlockCoordinates().y + block.colliderSize.y) >= -2)){
                        moveableUp = false;
                    }
                }
                
                
            }
        }
        
        
        
    }

    //method: gameLoop
    //purpose: The loop for what the camera sees.
    public void gameLoop() {
        dayCycle = new DayCycle();
        dayCycle.initDayCycle();
        viewableChunk = new Chunk(0, 0, 0);

        float dx;
        float dy;
        float deltaTime;
        float previousTime = 0;
        long time;
        
        final float GRAVITY_ACCELERATION = -0.01f;
        final float JUMP_STRENGTH = 0.4f;
        float yVelocity = 0.0f;

        float mouseSensitivity = 0.1f; //used to see how fast the camera view moves.
        float movementSpeed = 0.4f; //used to see how fast the player can move.
        Mouse.setGrabbed(true); //makes sure the cursor isnt in the way.

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            deltaTime = time - previousTime;
            previousTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();

            updateYaw(dx * mouseSensitivity); //rotates the camera in correspondence with the mouse movement.
            updatePitch(dy * mouseSensitivity);
            if(previousPos != position){              
                doCollisions();
                previousPos.set(position);
            }
            

            if ((Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) && moveableFront) {
                moveForward(movementSpeed);
            }
            if ((Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) && moveableLeft) {
                moveLeft(movementSpeed);
            }
            if ((Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) && moveableBack) {
                moveBackward(movementSpeed);
            }
            if ((Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) && moveableRight) {
                moveRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
                viewableChunk = new Chunk(0,0,0);
            }
            //jump
            //updates the yVelocity
            moveDown(yVelocity);
            
            if(moveableDown){
                yVelocity += GRAVITY_ACCELERATION;
            }
            else{
                yVelocity = 0.0f;
            }
            //
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                
                if(!moveableDown){
                    yVelocity = JUMP_STRENGTH;
                }
            }
                    
            try {
                glLoadIdentity();
                lookAt(); //updates the look vector.
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                viewableChunk.render();
                dayCycle.updateLightPositions(0.0005f);

                Display.update();
                Display.sync(60);
            } catch (Exception e) {
            }
        }
        Display.destroy();
    }
}
