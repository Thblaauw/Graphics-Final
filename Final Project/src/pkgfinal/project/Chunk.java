package pkgfinal.project;

/**
 * *************************************************************
 * file: Chunk.java
 *
 * author: Thomas Blaauw Barbosa, Bryce Callender, Jordan Laidig class: CS 4450
 * - Computer Graphics
 *
 * assignment: Final_Project 
 * date last modified: April 26, 2020
 *
 * purpose: This class is responsible for rendering the chunks. The chunks have
 * a defined size specified in the class and will texture the blocks once they
 * are made. If any changes happen to the chunk the class is responsible for
 * rebuilding the chunk
 *
 ***************************************************************
 */
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {

    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;

    static final int SEA_LEVEL = 10;
    static final int MAX_LAKE_RADIUS = 10;
    static final int MIN_LAKE_RADIUS = 5;

    private Block[][][] blocks;
    private int[][][] textureLocation;
    private int textureStartLocation;
    //vbo = vertex buffer object
    private int vboVertexHandle;
    private int vboColorHandle;
    private int vboTextureHandle;
    private int vboNormalHandle;
    private int startX, startY, startZ;
    private Texture texture;

    private Random random;

    //method: Chunk
    //purpose: constructs a chunk at the specified x,y, and z value.
    //Will load in the texture png file to wrap the blocks in as well as generate 
    //the vertex buffer objets
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.println("Error loading terrain texture");
        }

        random = new Random();
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        textureLocation = new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        textureStartLocation = 0;

        // Fill each row with -1.  
        for (int[][] row : textureLocation) {
            for (int[] column : row) {
                Arrays.fill(column, -1);
            }
        }

        vboVertexHandle = glGenBuffers();
        vboColorHandle = glGenBuffers();
        vboTextureHandle = glGenBuffers();
        vboNormalHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }

    //method: render
    //purpose: Binds all the buffers and supplies the data so opengl knows what 
    //it needs to do when the drawcall is invoked at the end.
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

        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L); //2 since u and v coordinates

        glNormalPointer(GL_FLOAT, 0, 0L);
        //glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);

        //The actual drawcall
        //We tell it to draw quads from index to index
        //24 is from a cube having 6 sides with 4 vertices.
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }

    //method: rebuildMesh
    //purpose: Rebuilds the mesh at the position specified if something changes
    //with the mesh like object destroyed or possibly placed.
    public void rebuildMesh(float startX, float startY, float startZ) {
        vboVertexHandle = glGenBuffers();
        vboColorHandle = glGenBuffers();
        vboTextureHandle = glGenBuffers();
        int seed = random.nextInt(Integer.MAX_VALUE);
        float persistence = random.nextFloat() * 0.1f;
        int largestFeature = 30;
        SimplexNoise noise = new SimplexNoise(largestFeature, persistence, seed);

        //Create a float buffer.
        //6 for each face 
        //12 is for each vertex and each vertex has an x,y,z. 4 * 3 => 12
        FloatBuffer vertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer vertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer vertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer normalData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

        //Since we are on the XZ plane they are first and then we can go 
        //layer by layer thus y being last inner loop
        float xResolution = 50f;
        float zResolution = 50f;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int i = (int) (x * ((30) / xResolution));
                int j = (int) (z * ((30) / zResolution));
                float maxHeight = ((startY + (int) (100 * noise.getNoise(i, j, 25)) * CUBE_LENGTH) % 30) / 2;
                //System.out.println("Max height: " + maxHeight);
                for (int y = 0; y <= maxHeight + 10; y++) {
                    vertexPositionData.put(
                            createCube(
                                    (float) (startX + x * CUBE_LENGTH),
                                    (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * 0.8)), //Get rid of + to put at origin
                                    (float) (startZ + z * CUBE_LENGTH)
                            )
                    );

                    normalData.put(createNormalData());

                    textureLocation[x][y][z] = textureStartLocation;
                    textureStartLocation += 48;
                    Vector3f position = new Vector3f((startX + x * CUBE_LENGTH) * -1, (y * CUBE_LENGTH + (int) (CHUNK_SIZE * 0.8)) * -1, (startZ + z * CUBE_LENGTH) * -1);

                    //Base ground
                    if (y == 0) {
                        blocks[x][y][z] = new Block(Block.BlockType.Bedrock, position);
                    } else if (y == 1) { //If we one above ground we can do mix of bedrock and stone since they cant break through the bottom
                        if (random.nextFloat() > 0.7f) {
                            blocks[x][y][z] = new Block(Block.BlockType.Bedrock, position);
                        } else {
                            blocks[x][y][z] = new Block(Block.BlockType.Stone, position);
                        }
                    } else if (y > 1 && y < maxHeight + 10) {
                        if (random.nextFloat() > 0.5f) {
                            blocks[x][y][z] = new Block(Block.BlockType.Dirt, position);
                        } else {
                            blocks[x][y][z] = new Block(Block.BlockType.Stone, position);
                        }
                    } else {
                        blocks[x][y][z] = new Block(Block.BlockType.Grass, position);
                    }

                    vertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(int) x][(int) y][(int) z])));

                    vertexTextureData.put(createTexCube((float) 0, (float) 0, blocks[(int) x][(int) y][(int) z]));
                }
            }
        }

        generateWater(vertexPositionData, vertexColorData, vertexTextureData);

        vertexPositionData.flip();
        vertexColorData.flip();
        vertexTextureData.flip();
        normalData.flip();

        //Bind the buffer and then bind the data into the buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionData, GL_STATIC_DRAW);
        //Bind the buffer to channel 0 freeing it
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        //Do the same with the color handle
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexColorData, GL_STATIC_DRAW);
        //free it
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexTextureData, GL_STATIC_DRAW);
        //free it
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        //glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
        //glBufferData(GL_ARRAY_BUFFER, normalData, GL_STATIC_DRAW);
        //free it
        //glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    //method: createCubeVertexCol
    //purpose: colors the cube vertices based on the data supplied
    private float[] createCubeVertexCol(float[] cubeColorArray) {
        float[] cubeColors = new float[cubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = cubeColorArray[i % cubeColorArray.length];
        }
        return cubeColors;
    }

    //method: getBlock
    //purpose: returns the reference of the block at the specific indices
    public Block getBlock(int i, int j, int k) {
        return blocks[i][j][k];
    }

    //method: createCube
    //purpose: creates a cube at the specified coordinates
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

    //method: getCubeColor
    //purpose: returns white since the texture is being applied to it and we dont
    //want the texture changing colors
    private float[] getCubeColor(Block block) {
        return new float[]{1, 1, 1};
    }

    //method: createNormalData
    //purpose: returns the normals for the cube
    private float[] createNormalData() {
        return new float[]{
            //Top
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            //Bottom
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            //Front
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            //Back
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            //Left
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            //Right
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f
        };
    }

    //method: createTexCube
    //purpose: coordinates of where the texture that is needed for the block lies.
    //Will be specified in a clockwise order.
    private float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;

        switch (block.getBlockID()) {
            //Grass
            case 0:
                return new float[]{
                    // TOP
                    x + offset * 0, y + offset * 0,
                    x + offset * 0, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    // Bottom
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // Front
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // Back
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    // Left
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // Right
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1
                };
            //Sand
            case 1:
                return new float[]{
                    // Top
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // Bottom
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // Front
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // Back
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // Left
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // Right
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2
                };
            //Water
            case 2:
                return new float[]{
                    // Top
                    x + offset * 14, y + offset * 13,
                    x + offset * 13, y + offset * 13,
                    x + offset * 13, y + offset * 12,
                    x + offset * 14, y + offset * 12,
                    // Bottom
                    x + offset * 14, y + offset * 13,
                    x + offset * 13, y + offset * 13,
                    x + offset * 13, y + offset * 12,
                    x + offset * 14, y + offset * 12,
                    // Front
                    x + offset * 13, y + offset * 12,
                    x + offset * 14, y + offset * 12,
                    x + offset * 14, y + offset * 13,
                    x + offset * 13, y + offset * 13,
                    // Back
                    x + offset * 14, y + offset * 13,
                    x + offset * 13, y + offset * 13,
                    x + offset * 13, y + offset * 12,
                    x + offset * 14, y + offset * 12,
                    // Left
                    x + offset * 13, y + offset * 12,
                    x + offset * 14, y + offset * 12,
                    x + offset * 14, y + offset * 13,
                    x + offset * 13, y + offset * 13,
                    // Right
                    x + offset * 13, y + offset * 12,
                    x + offset * 14, y + offset * 12,
                    x + offset * 14, y + offset * 13,
                    x + offset * 13, y + offset * 13,};
            //Dirt
            case 3:
                return new float[]{
                    // Top
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // Bottom
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // Front
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // Back
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // Left
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // Right
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1
                };
            //Stone
            case 4:
                return new float[]{
                    // Top
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // Bottom
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // Front
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // Back
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    // Left
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // Right
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1
                };
            //Bedrock
            case 5:
                return new float[]{
                    // Top
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // Bottom
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // Front
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // Back
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // Left
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // Right
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2
                };
        }

        return new float[]{};
    }

    //method: generateWater
    //purpose: generates a lake if a random number generator is above 0.7 so a 30% chance to have a lake in the chunk
    private void generateWater(FloatBuffer vertexPositionData, FloatBuffer vertexColorData, FloatBuffer vertexTextureData) {
        float bodyOfWaterDecider = random.nextFloat();

        if (bodyOfWaterDecider > 0.7f) {
            int waterStartX = random.nextInt(CHUNK_SIZE - 5) + 1;
            int waterStartY = SEA_LEVEL;
            int waterStartZ = random.nextInt(CHUNK_SIZE - 5) + 1;

            int depth = random.nextInt((int) (CHUNK_SIZE * 0.30f));

            int radius = random.nextInt(MAX_LAKE_RADIUS) + MIN_LAKE_RADIUS;

            int endingX = waterStartX + radius >= CHUNK_SIZE ? CHUNK_SIZE - 1 : waterStartX + radius;
            int endingZ = waterStartZ + radius >= CHUNK_SIZE ? CHUNK_SIZE - 1 : waterStartZ + radius;

            for (int x = waterStartX; x < endingX; x++) {
                for (int z = waterStartZ; z < endingZ; z++) {
                    for (int y = waterStartY; y >= waterStartY - depth; y--) {
                        changeBlockInformation(x, y, z, Block.BlockType.Water, vertexPositionData, vertexColorData, vertexTextureData);
                    }
                }
            }

            generateSandyBorders(waterStartX, waterStartY, waterStartZ, radius, depth, vertexPositionData, vertexColorData, vertexTextureData);
        }
    }

    //method: generateSandyBorders
    //purpose: Since we generated a lake this is needed in order to contain in 
    //a body of sand. It will go around the lake and below the lake filing the blocks
    //with sand. If the block is null where a sand should be placed a new one will be
    //constructed and placed there
    private void generateSandyBorders(int waterStartX, int waterStartY, int waterStartZ,
            int radius, int depth,
            FloatBuffer vertexPositionData, FloatBuffer vertexColorData, FloatBuffer vertexTextureData) {

        int beginningX = waterStartX - 1;
        int beginningZ = waterStartZ - 1;

        int endingX = beginningX + radius + 2 >= CHUNK_SIZE ? CHUNK_SIZE : beginningX + radius + 2;
        int endingZ = beginningZ + radius + 2 >= CHUNK_SIZE ? CHUNK_SIZE : beginningZ + radius + 2;

        //Start 
        for (int x = beginningX; x < endingX; x++) {
            for (int z = beginningZ; z < endingZ; z++) {
                for (int y = waterStartY; y >= waterStartY - depth - 1; y--) {
                    if (blocks[x][y][z] == null || blocks[x][y][z].getBlockID() != Block.BlockType.Water.ordinal()) {
                        changeBlockInformation(x, y, z, Block.BlockType.Sand, vertexPositionData, vertexColorData, vertexTextureData);
                    }
                }
            }
        }
    }

    //method: changeBlockInformation
    //purpose: Changes the block information at indicies x,y,z with the block type
    //specified as a parameter. If the block is null at the indices a new one is constructed
    //and placed at the world coordinates needed and the color/texture generated based on type
    //as well.
    private void changeBlockInformation(int x, int y, int z, Block.BlockType blockType,
            FloatBuffer vertexPositionData, FloatBuffer vertexColorData, FloatBuffer vertexTextureData) {
        int index = textureLocation[x][y][z];

        if (index == -1) {
            vertexPositionData.put(
                    createCube(
                            (float) (startX + x * CUBE_LENGTH),
                            (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * 0.8)),
                            (float) (startZ + z * CUBE_LENGTH)
                    )
            );

            vertexColorData.put(createCubeVertexCol(getCubeColor(blocks[x][y][z])));
            Vector3f position = new Vector3f((startX + x * CUBE_LENGTH) * -1, (y * CUBE_LENGTH + (int) (CHUNK_SIZE * 0.8)) * -1, (startZ + z * CUBE_LENGTH) * -1);

            textureLocation[x][y][z] = textureStartLocation;

            textureStartLocation += 48;

            blocks[x][y][z] = new Block(blockType, position);
            vertexTextureData.put(createTexCube((float) 0, (float) 0, blocks[x][y][z]));

            return;
        }

        blocks[x][y][z] = new Block(blockType);
        vertexTextureData.put(index, createTexCube((float) 0, (float) 0, blocks[x][y][z]));
    }

}
