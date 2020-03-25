package pkgfinal.project;

/**
 * *************************************************************
 * file: Chunk.java
 *
 * author: Bryce Callender class: CS 4450 - Computer Graphics
 *
 * assignment: Final_Project date last modified: Mar 25, 2020 at 9:29:30 AM
 *
 * purpose: ENTER PURPOSE HERE
 *
 ***************************************************************
 */
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Chunk {

    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    //vbo = vertex buffer object
    private int vboVertexHandle;
    private int vboColorHandle;
    private int startX, startY, startZ;

    private Random random;

    public Chunk(int startX, int startY, int startZ) {
        random = new Random();
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    if (random.nextFloat() > 0.7f) {
                        blocks[x][y][z] = new Block(Block.BlockType.Grass);
                    } else if (random.nextFloat() > 0.4f) {
                        blocks[x][y][z] = new Block(Block.BlockType.Dirt);
                    } else if (random.nextFloat() > 0.2f) {
                        blocks[x][y][z] = new Block(Block.BlockType.Water);
                    } else {
                        blocks[x][y][z] = new Block(Block.BlockType.Default);
                    }
                }
            }
        }

        vboVertexHandle = glGenBuffers();
        vboColorHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }

    public void render() {
        glPushMatrix();
        //Bind the handle to the buffer
        //Allows us to bind data to be sent to the graphics card easily
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        //tells opengl that we will have 3 vertices with the type float,
        //and we have a stride of 0 with it starting at the beginning (offset of 0)
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        //THe actual drawcall
        //We tell it to draw quads from index to index
        //24 is from a cube having 6 sides with 4 vertices.
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }

    public void rebuildMesh(float startX, float startY, float startZ) {
        vboVertexHandle = glGenBuffers();
        vboColorHandle = glGenBuffers();

        //Create a float buffer.
        //6 for each face 
        //12 is for each vertex and each vertex has an x,y,z. 4 * 3 => 12
        FloatBuffer vertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer vertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

        //Since we are on the XZ plane they are first and then we can go 
        //layer by layer thus y being last inner loop
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    vertexPositionData.put(
                            createCube(
                                    (float) (startX + x * CUBE_LENGTH),
                                    (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * 0.8)),
                                    (float) (startZ + z * CUBE_LENGTH)
                            )
                    );

                    vertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(int) x][(int) y][(int) z])));
                }
            }
        }

        vertexPositionData.flip();
        vertexColorData.flip();

        //Bind the buffer and then bind the data into the buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionData, GL_STATIC_DRAW);
        //Bind the buffer to channel 0 freeing it
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        //Do the same with the color handle
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER,
                vertexColorData,
                GL_STATIC_DRAW);
        //free it
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }

    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z};
    }

    private float[] getCubeColor(Block block) {
        switch (block.getBlockID()) {
            case 1:
                return new float[]{0, 1, 0};
            case 2:
                return new float[]{1, 0.5f, 0};
            case 3:
                return new float[]{0, 0f, 1f};
        }
        return new float[]{1, 1, 1};
    }
}
