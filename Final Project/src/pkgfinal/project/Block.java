package pkgfinal.project;


/***************************************************************
* file: Block.java
* 
* author: Bryce Callender
* class: CS 4450 - Computer Graphics
*
* assignment: Final_Project
* date last modified: April 6, 2020
*
* purpose: A data container used for storing properties related
* to the building blocks of our minecraft game. Has the type,
* position, and active state.
*
****************************************************************/ 

import org.lwjgl.util.vector.Vector3f;

public class Block {
    private boolean isActive;
    private BlockType blockType;
    private float x,y,z;
    private int hitCount; //Used for breaking texture
    public Vector3f colliderSize; //collider for the Blocks
    //private boolean isCollidable; //Can we hit the block?
    
    public enum BlockType {
        Grass(0), 
        Sand(1),
        Water(2),
        Dirt(3),
        Stone(4),
        Bedrock(5),
        Default(6);
        
        private int blockID;
        
        //method: BlockType
        //purpose: constructor used to take in a id for the block which is its type
        BlockType(int id) {
            this.blockID = id;
        }
        
        //method: getBlockID
        //purpose: returns the blockid associated with the block
        public int getBlockID() {
            return blockID;
        }
        
        //method: setBlockID
        //purpose: sets the block id to the current block
        public void setBlockID(int id) {
            this.blockID = id;
        }
    }
    
    //method: Block
    //purpose: constructor used to take in a block type object to finalize the 
    //contents of this block made
    public Block(BlockType blockType) {
        this.blockType = blockType;
        this.hitCount = 0;
        this.isActive = true;
        this.colliderSize = new Vector3f(Chunk.CUBE_LENGTH/2, Chunk.CUBE_LENGTH/2, Chunk.CUBE_LENGTH/2);
    }
    
    //method: setBlockCoordinates
    //purpose: sets the current blocks coordinates in the world with specified x,y,z. (explicit)
    public void setBlockCoordinates(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //method: setBlockCoordinates
    //purpose: sets the current blocks coordinates in the world with a vector3f object 
    //with the x,y,z already set in the object. (implicit)
    public void setBlockCoordinates(Vector3f coordinates) {
        this.x = coordinates.x;
        this.y = coordinates.y;
        this.z = coordinates.z;
    }
    
    public Vector3f getBlockCoordinates(){
        Vector3f position = new Vector3f(x, y, z);

        return position;
        
    }
    
    //method: isActive
    //purpose: returns the state of the block whether it be on or off
    public boolean isActive() {
        return this.isActive;
    }
    
    //method: setActive
    //purpose: responsible for setting the block on or off to show or destroy
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    //method: getBlockID
    //purpose: gets the block id of the block
    public int getBlockID() {
        return blockType.getBlockID();
    }
}