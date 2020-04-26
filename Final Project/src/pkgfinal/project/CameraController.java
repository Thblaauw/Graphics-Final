/** *************************************************************
 * file: CameraController.java
 *
 * authors: Thomas Blaauw Barbosa, Bryce Callender, Jordan Laidig
 * class: CS 4450 – Intro to Computer Graphics
 *
 * assignment: Final Project
 * date last modified: 4/26/2020
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
    private Chunk viewableChunks[][]; //creates the array of chunks.
    private DayCycle dayCycle;
    private Vector3f colliderSize; //the dimensions of thecollider for the player
    private boolean moveableUp = true, moveableDown = true, moveableLeft = true, moveableRight = true, moveableFront = true, moveableBack = true, waterJump = true; //allows for movement.
    private final int NUM_CHUNKS = 4; //this number squared is the number of chunks that exist in the world.
    private final float X_BOUND_MIN = 0, X_BOUND_MAX = (-1 * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH * NUM_CHUNKS) + 1, Z_BOUND_MIN = 0, Z_BOUND_MAX = (-1 * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH * NUM_CHUNKS) + 1;
    //these constants dictate the boundaries of the world so the player cant leave.
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

    //method: lookAt
    //purpose: moves the camera into the direction the player wants to face.
    public void lookAt() {
        glRotatef(pitch, 1f, 0f, 0f);
        glRotatef(yaw, 0f, 1f, 0f);
        glTranslatef(position.x, position.y, position.z);
    }
   
    //method: checkCollision
    //purpose: checks to see if the player is actually touching a block.
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
    
    //method: broadPhase
    //purpose: goes through every block in the world to find which ones might actually be colliding with the player.
    public ArrayList<Block> broadPhase(){
        ArrayList<Block> SAP = new ArrayList();
        for(int m = 0; m < NUM_CHUNKS; m++){
            for(int n = 0; n < NUM_CHUNKS; n++){ 
                for(int i = 0; i < Chunk.CHUNK_SIZE; i++){
                    for(int j = 0; j < Chunk.CHUNK_SIZE; j++){
                        for(int k = 0; k < Chunk.CHUNK_SIZE; k++){
                            if(viewableChunks[m][n].getBlock(i, j, k) != null){
                                if(checkCollision(viewableChunks[m][n].getBlock(i, j, k))){
                                    SAP.add(viewableChunks[m][n].getBlock(i, j, k));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return SAP;
    }
    
    //method: doCollisions
    //purpose: finds out where the player can or cant move.
    public void doCollisions(){        
        moveableLeft = true;
        moveableRight = true;
        moveableFront = true;
        moveableBack = true;
        moveableUp = true;
        moveableDown = true;
        waterJump = false;
        ArrayList<Block> SAP = broadPhase(); //gets the blocks that are colliding with the player.
        if(((yaw % 360 >= 0 && yaw % 360 < 22.5) || (yaw % 360 >=337.5 && yaw % 360 < 360)) || ((yaw % 360 >= -22.5 && yaw % 360 < 0) || (yaw % 360 <= -337.5 && yaw % 360 > -360))){
            //each if statement checks for the players rotation in the world. 
            if(position.x + colliderSize.x >= X_BOUND_MIN){ //makes sure the player is within the world border.
                moveableLeft = false;
            }
            if(position.x - colliderSize.x <= X_BOUND_MAX){
                moveableRight = false;
            }
            if(position.z + colliderSize.z >= Z_BOUND_MIN){
                moveableFront = false;
            }
            if(position.z - colliderSize.z <= Z_BOUND_MAX){
                moveableBack = false;
            }
        }
        else if ((yaw % 360 >= 22.5 && yaw % 360 < 67.5) || (yaw % 360 <= -292.5 && yaw % 360 > -337.5)){
            if(position.x + colliderSize.x >= X_BOUND_MIN){
                moveableLeft = false;
                moveableBack = false;
            }
            if(position.x - colliderSize.x <= X_BOUND_MAX){
                moveableRight = false;
                moveableFront = false;
            }
            if(position.z + colliderSize.z >= Z_BOUND_MIN){
                moveableFront = false;
                moveableLeft = false;
            }
            if(position.z - colliderSize.z <= Z_BOUND_MAX){
                moveableBack = false;
                moveableRight = false;
            }
        }
        else if((yaw % 360 >= 67.5 && yaw % 360 < 112.5)|| (yaw % 360 <= -247.5 && yaw % 360 > -292.5)){
            if(position.x + colliderSize.x >= X_BOUND_MIN){
                moveableBack = false;
            }
            if(position.x - colliderSize.x <= X_BOUND_MAX){
                moveableFront = false;
            }
            if(position.z + colliderSize.z >= Z_BOUND_MIN){
                moveableLeft = false;
            }
            if(position.z - colliderSize.z <= Z_BOUND_MAX){
                moveableRight = false;
            }
        }
        else if((yaw % 360 >= 112.5 && yaw % 360 < 157.5)||(yaw % 360 <= -202.5 && yaw % 360 > -247.5)){
            if(position.x + colliderSize.x >= X_BOUND_MIN){
                moveableBack = false;
                moveableRight = false;
            }
            if(position.x - colliderSize.x <= X_BOUND_MAX){
                moveableFront = false;
                moveableLeft = false;
            }
            if(position.z + colliderSize.z >= Z_BOUND_MIN){
                moveableLeft = false;
                moveableBack = false;
            }
            if(position.z - colliderSize.z <= Z_BOUND_MAX){
                moveableRight = false;
                moveableFront = false;
            }
        }
        else if((yaw % 360 >= 157.5 && yaw % 360 < 202.5)||(yaw % 360 <= -157.5 && yaw % 360 > -202.5)){
            if(position.x + colliderSize.x >= X_BOUND_MIN){
                moveableRight = false;
            }
            if(position.x - colliderSize.x <= X_BOUND_MAX){
                moveableLeft = false;
            }
            if(position.z + colliderSize.z >= Z_BOUND_MIN){
                moveableBack = false;
            }
            if(position.z - colliderSize.z <= Z_BOUND_MAX){
                moveableFront = false;
            }
        }
        else if((yaw % 360 >= 202.5 && yaw % 360 < 247.5)||(yaw % 360 <= -112.5 && yaw % 360 > -157.5)){
            if(position.x + colliderSize.x >= X_BOUND_MIN){
                moveableRight = false;
                moveableFront = false;
            }
            if(position.x - colliderSize.x <= X_BOUND_MAX){
                moveableLeft = false;
                moveableBack = false;
            }
            if(position.z + colliderSize.z >= Z_BOUND_MIN){
                moveableBack = false;
                moveableRight = false;
            }
            if(position.z - colliderSize.z <= Z_BOUND_MAX){
                moveableFront = false;
                moveableLeft = false;
            }
        }
        else if((yaw % 360 >= 247.5 && yaw % 360 < 292.5)||(yaw % 360 <= -67.5 && yaw % 360 > -112.5)){
            if(position.x + colliderSize.x >= X_BOUND_MIN){
                moveableFront = false;
            }
            if(position.x - colliderSize.x <= X_BOUND_MAX){
                moveableBack = false;
            }
            if(position.z + colliderSize.z >= Z_BOUND_MIN){
                moveableRight = false;
            }
            if(position.z - colliderSize.z <= Z_BOUND_MAX){
                moveableLeft = false;
            }
        }
        else if((yaw % 360 >= 292.5 && yaw % 360 < 337.5)||(yaw % 360 <= -22.5 && yaw % 360 > -67.5)){
            if(position.x + colliderSize.x >= X_BOUND_MIN){
                moveableFront = false;
                moveableLeft = false;
            }
            if(position.x - colliderSize.x <= X_BOUND_MAX){
                moveableBack = false;
                moveableRight = false;
            }
            if(position.z + colliderSize.z >= Z_BOUND_MIN){
                moveableRight = false;
                moveableFront = false;
            }
            if(position.z - colliderSize.z <= Z_BOUND_MAX){
                moveableLeft = false;
                moveableBack = false;
            }
        }
        if(SAP.isEmpty() != true){ //only does the following if theres something to collide with.
            for(int i = 0; i < SAP.size(); i++){ 
                Block block = SAP.get(i);
                if(block.getBlockID() != 2){ //makes sure they dont collide with water.
                    if((position.y + colliderSize.y - (block.getBlockCoordinates().y - block.colliderSize.y) >= 0) && (position.y + colliderSize.y - (block.getBlockCoordinates().y - block.colliderSize.y) <= 2)){
                        moveableDown = false;
                    }
                    if((position.y - colliderSize.y - (block.getBlockCoordinates().y + block.colliderSize.y) <= 0) & (position.y - colliderSize.y - (block.getBlockCoordinates().y + block.colliderSize.y) >= -2)){
                        moveableUp = false;
                    }
                    if(!((position.y + colliderSize.y - (block.getBlockCoordinates().y - block.colliderSize.y) >= 0) && (position.y + colliderSize.y - (block.getBlockCoordinates().y - block.colliderSize.y) <= 2))){  
                        if(((yaw % 360 >= 0 && yaw % 360 < 22.5) || (yaw % 360 >=337.5 && yaw % 360 < 360)) || ((yaw % 360 >= -22.5 && yaw % 360 < 0) || (yaw % 360 <= -337.5 && yaw % 360 > -360))){
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
                        }
                        else if((yaw % 360 >= 22.5 && yaw % 360 < 67.5) || (yaw % 360 <= -292.5 && yaw % 360 > -337.5))
                        {
                            if((position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) >= 0) && (position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) <= 1)){
                                moveableLeft = false;
                                moveableBack = false;
                            }
                            if((position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) <= 0) && (position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) >= -1)){
                                moveableRight = false;
                                moveableFront = false;
                            }
                            if((position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) >= 0) && (position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) <= 1)){
                                moveableFront = false;
                                moveableLeft = false;
                            }
                            if((position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) <= 0) && (position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) >= -1)){
                                moveableBack = false;
                                moveableRight = false;
                            }
                        }
                        else if((yaw % 360 >= 67.5 && yaw % 360 < 112.5)||(yaw % 360 <= -247.5 && yaw % 360 > -292.5)){
                            if((position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) >= 0) && (position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) <= 1)){
                                moveableBack = false;
                            }
                            if((position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) <= 0) && (position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) >= -1)){
                                moveableFront = false;
                            }
                            if((position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) >= 0) && (position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) <= 1)){
                                moveableLeft = false;
                            }
                            if((position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) <= 0) && (position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) >= -1)){
                                moveableRight = false;
                            }
                        }
                        else if((yaw % 360 >= 112.5 && yaw % 360 < 157.5)||(yaw % 360 <= -202.5 && yaw % 360 > -247.5)){
                            if((position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) >= 0) && (position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) <= 1)){
                                moveableBack = false;
                                moveableRight = false;
                            }
                            if((position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) <= 0) && (position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) >= -1)){
                                moveableFront = false;
                                moveableLeft = false;
                            }
                            if((position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) >= 0) && (position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) <= 1)){
                                moveableLeft = false;
                                moveableBack = false;
                            }
                            if((position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) <= 0) && (position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) >= -1)){
                                moveableRight = false;
                                moveableFront = false;
                            }
                        }
                        else if((yaw % 360 >= 157.5 && yaw % 360 < 202.5)||(yaw % 360 <= -157.5 && yaw % 360 > -202.5))
                        {
                            if((position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) >= 0) && (position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) <= 1)){
                                moveableRight = false;
                            }
                            if((position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) <= 0) && (position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) >= -1)){
                                moveableLeft = false;
                            }
                            if((position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) >= 0) && (position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) <= 1)){
                                moveableBack = false;
                            }
                            if((position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) <= 0) && (position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) >= -1)){
                                moveableFront = false;
                            }
                        }
                        else if((yaw % 360 >= 202.5 && yaw % 360 < 247.5)||(yaw % 360 <= -112.5 && yaw % 360 > -157.5)){
                            if((position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) >= 0) && (position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) <= 1)){
                                moveableRight = false;
                                moveableFront = false;
                            }
                            if((position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) <= 0) && (position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) >= -1)){
                                moveableLeft = false;
                                moveableBack = false;
                            }
                            if((position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) >= 0) && (position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) <= 1)){
                                moveableBack = false;
                                moveableRight = false;
                            }
                            if((position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) <= 0) && (position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) >= -1)){
                                moveableFront = false;
                                moveableLeft = false;
                            }
                        }
                        else if((yaw % 360 >= 247.5 && yaw % 360 < 292.5)||(yaw % 360 <= -67.5 && yaw % 360 > -112.5)){
                            if((position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) >= 0) && (position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) <= 1)){
                                moveableFront = false;
                            }
                            if((position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) <= 0) && (position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) >= -1)){
                                moveableBack = false;
                            }
                            if((position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) >= 0) && (position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) <= 1)){
                                moveableRight = false;
                            }
                            if((position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) <= 0) && (position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) >= -1)){
                                moveableLeft = false;
                            }
                        }
                        else if((yaw % 360 >= 292.5 && yaw % 360 < 337.5)||(yaw % 360 <= -22.5 && yaw % 360 > -67.5))
                        {
                            if((position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) >= 0) && (position.x + colliderSize.x - (block.getBlockCoordinates().x - block.colliderSize.x) <= 1)){
                                moveableFront = false;
                                moveableLeft = false;
                            }
                            if((position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) <= 0) && (position.x - colliderSize.x - (block.getBlockCoordinates().x + block.colliderSize.x) >= -1)){
                                moveableBack = false;
                                moveableRight = false;
                            }
                            if((position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) >= 0) && (position.z + colliderSize.z - (block.getBlockCoordinates().z - block.colliderSize.z) <= 1)){
                                moveableRight = false;
                                moveableFront = false;
                            }
                            if((position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) <= 0) && (position.z - colliderSize.z - (block.getBlockCoordinates().z + block.colliderSize.z) >= -1)){
                                moveableLeft = false;
                                moveableBack = false;
                            }
                        }
                    
                    }
                } 
                else{
                    waterJump = true; //sets the water physics.
                }
            }
        }       
    }

    //method: gameLoop
    //purpose: The loop for what the camera sees.
    public void gameLoop() {
        dayCycle = new DayCycle();
        dayCycle.initDayCycle();
        viewableChunks = new Chunk[NUM_CHUNKS][NUM_CHUNKS];
        for(int i = 0; i < NUM_CHUNKS; i++){
            for(int j = 0; j < NUM_CHUNKS; j++){
                viewableChunks[i][j] = new Chunk(i * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH, 0, j * Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH);
            }
        }
        

        float dx;
        float dy;
        float deltaTime;
        float previousTime = 0;
        long time;
        
        final float GRAVITY_ACCELERATION = -0.01f;
        final float JUMP_STRENGTH = 0.3f;
        float yVelocity = 0.0f;

        float mouseSensitivity = 0.1f; //used to see how fast the camera view moves.
        float movementSpeed = 0.3f; //used to see how fast the player can move.
        Mouse.setGrabbed(true); //makes sure the cursor isnt in the way.

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            deltaTime = time - previousTime;
            previousTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();

            updateYaw(dx * mouseSensitivity); //rotates the camera in correspondence with the mouse movement.
            updatePitch(dy * mouseSensitivity);
            System.out.println(yaw % 360);
            if(previousPos != position){ //makes it so collision is only calculated if the player moves.  
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
            if ((Keyboard.isKeyDown(Keyboard.KEY_SPACE)) && waterJump) {
                moveDown(movementSpeed);
                yVelocity = 0.0f;
            }
            //jump
            //updates the yVelocity
            
            
            if(moveableDown && !waterJump){
                yVelocity += GRAVITY_ACCELERATION;
                moveDown(yVelocity);
            }
            else if(waterJump){
                yVelocity = 3*GRAVITY_ACCELERATION;
                moveDown(yVelocity);
            }
            else{
                yVelocity = 0.0f;
                moveDown(yVelocity);
            }
            //
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !waterJump){
                
                if(!moveableDown && !waterJump){
                    yVelocity = JUMP_STRENGTH;
                    moveDown(yVelocity);
                }
            }
                    
            try {
                glLoadIdentity();
                lookAt(); //updates the look vector.
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                
                for(int i = 0; i < NUM_CHUNKS; i++){
                    for(int j = 0; j < NUM_CHUNKS; j++){
                        viewableChunks[i][j].render();
                    }
                }
                
                dayCycle.updateLightPositions(0.0005f);

                Display.update();
                Display.sync(60);
            } catch (Exception e) {
            }
        }
        Display.destroy();
    }
}
