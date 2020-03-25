package pkgfinal.project;


/***************************************************************
* file: Block.java
* 
* author: Bryce Callender
* class: CS 4450 - Computer Graphics
*
* assignment: Final_Project
* date last modified: Mar 25, 2020 at 9:21:18 AM
*
* purpose: ENTER PURPOSE HERE
*
****************************************************************/ 

import org.lwjgl.util.vector.Vector3f;

public class Block {
    private boolean isActive;
    private BlockType blockType;
    private float x,y,z;
    
    public enum BlockType {
        Grass(0), 
        Sand(1),
        Water(2),
        Dirt(3),
        Stone(4),
        Bedrock(5),
        Default(6);
        
        private int blockID;
        
        BlockType(int id) {
            this.blockID = id;
        }
        
        public int getBlockID() {
            return blockID;
        }
        
        public void setBlockID(int id) {
            this.blockID = id;
        }
    }
    
    public Block(BlockType blockType) {
        this.blockType = blockType;
    }
    
    public void setBlockCoordinates(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void setBlockCoordinates(Vector3f coordinates) {
        this.x = coordinates.x;
        this.y = coordinates.y;
        this.z = coordinates.z;
    }
    
    public boolean isActive() {
        return this.isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public int getBlockID() {
        return blockType.getBlockID();
    }
}