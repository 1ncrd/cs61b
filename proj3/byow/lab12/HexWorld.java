package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Arrays;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);
    public int size = 3;
    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();

        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        /* Drawing A Tesselation of Hexagons. */
        int size = 3;
        int hexSize = 3;

        int[][] posOfEachHex = getPosOfEachHex(size, hexSize);

        System.out.println(Arrays.deepToString(posOfEachHex));

        for (int[] pos : posOfEachHex) {
            TETile tile = randomTile();
            if (pos != null) addHexagon(world, tile, 3, pos[0], pos[1]);
        }

        // draws the world to the screen
        ter.renderFrame(world);
    }

    /**
     * Add a hexagon to the world at the lower left point (x, y).
     */
    private static void addHexagon(TETile[][] world, TETile filler, int size, int x, int y) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;

        /* lower part */
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size + i * 2; j++) {
                world[size - i - 1 + j + x][i + y] = filler;
            }
        }

        /* upper part */
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size * 3 - 2 - i * 2; j++) {
                world[i + j + x][i + y + size] = filler;
            }
        }
    }

    /**
     *
     * @param size size of the big hexagon.
     * @param hexSize size of the small hexagon.
     * @return The lower left point of each hexagon.
     */
    private static int[][] getPosOfEachHex(int size, int hexSize) {
        int count = ((size + 2 * size - 1) * size) / 2 * 2 - (2 * size - 1);
        int[][] result = new int[count][];

        int col = 0;
        int indexCol = 0;
        int indexAll = 0;
        while (col <= size / 2 + 1) {
            while (indexCol < col + size) {
                int initialY = (size - 1 - col) * hexSize;
                result[indexAll] = new int[] {col * (hexSize * 2 - 1), initialY + indexCol * hexSize * 2};
                indexCol += 1;
                indexAll += 1;
            }
            col += 1;
            indexCol = 0;
        }

        int maxX = (size * 2 - 1 - 1) * (size * 2 - 1);
        System.out.println("maxX = " + maxX);

        for (int i = count - (size + 2 * size - 3) * (size - 1) / 2 - 1; i < count; i++) {
            result[i] = new int[] {maxX - result[count - i - 1][0], result[count - i - 1][1]};
        }

        return result;
    }

    private static TETile randomTile() {
        TETile[] randomTileSet = new TETile[] {Tileset.FLOWER, Tileset.MOUNTAIN, Tileset.TREE, Tileset.GRASS, Tileset.SAND};
        int tileNum = RANDOM.nextInt(randomTileSet.length);
        return randomTileSet[tileNum];
    }

}
