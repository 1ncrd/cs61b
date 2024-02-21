package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static Random RANDOM = null;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        /* TODO: Fill out this method so that it run the engine using the input
             passed in as an argument, and return a 2D tile representation of the
             world that would have been drawn if the same inputs had been given
             to interactWithKeyboard().

             See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
             that works for many different input types.
        */
        char option = input.charAt(0);
        TETile[][] finalWorldFrame = null;
        switch (option) {
            /* Create new world. */
            case 'N' -> {
                long seed = Long.parseLong(input.substring(1, input.length() - 1));
                finalWorldFrame = createWorld(seed);
            }
        }

        return finalWorldFrame;
    }

    private TETile[][] createWorld(long seed) {
        RANDOM = new Random(seed);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        clearWorld(world);

        int walkNum = 30;

        int[] curPos = new int[] {WIDTH / 2, HEIGHT / 2};
        for (int i = 0; i < walkNum; i++) {
            curPos = randomWalk(world, curPos[0], curPos[1]);
        }

        createRooms(world);

        buildWalls(world);
        return world;
    }

    private void createRooms(TETile[][] world) {
        float roomProb = 0.1F;
        TETile[][] newWorld = new TETile[WIDTH][HEIGHT];
        clearWorld(newWorld);
        for (int i = 0; i < WIDTH; i++) {
            System.arraycopy(world, 0, newWorld, 0, HEIGHT);
        }
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                if (world[x][y] == Tileset.FLOOR && RandomUtils.bernoulli(RANDOM, roomProb)) {
                    createRoom(newWorld, x, y);
                }
            }
        }
        for (int i = 0; i < WIDTH; i++) {
            System.arraycopy(newWorld, 0, world, 0, HEIGHT);
        }
    }

    private void createRoom(TETile[][] world, int x, int y) {
        int width = randomInt(5, 9);
        int height = randomInt(5, 9);
        for (int curX = x - width / 2; curX < x + (width + 1) / 2; curX++) {
            for (int curY = y - height / 2; curY < y + (height + 1) / 2; curY++) {
                if (curX >= 1 && curX <= WIDTH - 2 && curY >= 1 && curY <= HEIGHT - 2) {
                    world[curX][curY] = Tileset.FLOOR;
                }
            }
        }
    }

    private int randomInt(int origin, int bound) {
        return RANDOM.nextInt(bound - origin) + origin;
    }

    private void buildWalls(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                if (world[x][y] == Tileset.FLOOR) {
                    surroundWall(world, x, y);
                }
            }
        }
    }

    private void surroundWall(TETile[][] world, int x, int y) {
        int[][] directions = new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        for (int i = 0; i < directions.length; i++) {
            int nextX = x - directions[i][0];
            int nextY = y - directions[i][1];
            if (world[nextX][nextY] == Tileset.NOTHING) {
                world[nextX][nextY] = Tileset.WALL;
            }
        }
    }

    private void clearWorld(TETile[][] world) {
        // initialize tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private int[] randomWalk(TETile[][] world, int x, int y) {
        int[][] directions = new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int[] direction = directions[RANDOM.nextInt(4)];
        int length = randomInt(5, 20);
        int curX = x;
        int curY = y;
        for (int i = 0; i < length; i++) {
            world[curX][curY] = Tileset.FLOOR;
            curX += direction[0];
            curY += direction[1];
            curX = clamp(curX, 1, WIDTH - 2);
            curY = clamp(curY, 1, HEIGHT - 2);
        }
        return new int[] {curX, curY};
    }

    int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
//        String input = "N53335S";
        String input = "N" + System.currentTimeMillis() + "S";
        TETile[][] world = engine.interactWithInputString(input);

        engine.ter.initialize(WIDTH, HEIGHT);
        engine.ter.renderFrame(world);
    }
}
