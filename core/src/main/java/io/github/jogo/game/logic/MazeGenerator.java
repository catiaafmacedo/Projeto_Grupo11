package io.github.jogo.game.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import io.github.jogo.Enums.ETileType;
import io.github.jogo.game.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MazeGenerator {
    private final int WIDTH;
    private final int HEIGHT;
    private final int TILE_SIZE;
    public ETileType[][] map;
    public Rectangle mazeend ;

    private final Texture floortext = new Texture("assets/v01/floor.png"); // ou outra apropriada

    public MazeGenerator(World w){
        this.WIDTH = w.MAZE_WIDTH;
        this.HEIGHT = w.MAZE_HEIGHT;
        this.TILE_SIZE = World.TILE_SIZE;
        //this.mazeend=new Rectangle(0,0,WIDTH,HEIGHT);
        map = new ETileType[WIDTH][HEIGHT];
        generateMaze();
    }
    private void generateMaze() {
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                map[x][y] = ETileType.WALL;

        int startX = 1, startY = 1;
        int[][] goals = {
            {WIDTH - 2, HEIGHT - 2},
            {WIDTH - 2, HEIGHT / 2},
            {WIDTH / 2, HEIGHT - 2}
        };
//mazeend  = new Rectangle((WIDTH-2)*TILE_SIZE, (HEIGHT-2)*TILE_SIZE,WIDTH,HEIGHT );

        map[startX][startY] = ETileType.FLOOR;


        for (int[] goal : goals) {
            List<int[]> path = generatePath(startX, startY, goal[0], goal[1]);
            for (int[] pos : path) {
                map[pos[0]][pos[1]] = ETileType.FLOOR;
            }
        }

        map[WIDTH-2][HEIGHT-3] = ETileType.FLOOR;
        map[WIDTH-1][HEIGHT-3] = ETileType.FLOOR;

    }

    private List<int[]> generatePath(int startX, int startY, int endX, int endY) {
        List<int[]> path = new ArrayList<>();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        dfs(startX, startY, endX, endY, path, visited);
        return path;
    }
    private boolean dfs(int x, int y, int endX, int endY, List<int[]> path, boolean[][] visited) {
        if (x < 1 || y < 1 || x >= WIDTH - 1 || y >= HEIGHT - 1 || visited[x][y])
            return false;

        visited[x][y] = true;
        path.add(new int[]{x, y});
        map[x][y] = ETileType.FLOOR;

        if (x == endX && y == endY)
            return true;

        int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
        List<int[]> dirList = Arrays.asList(dirs);
        Collections.shuffle(dirList);

        for (int[] d : dirList) {
            int nx = x + d[0];
            int ny = y + d[1];
            int wallX = x + d[0] / 2;
            int wallY = y + d[1] / 2;

            if (nx >= 1 && ny >= 1 && nx < WIDTH - 1 && ny < HEIGHT - 1 && !visited[nx][ny]) {
                map[wallX][wallY] = ETileType.FLOOR; // Abre parede
                if (dfs(nx, ny, endX, endY, path, visited))
                    return true;
            }
        }

        path.remove(path.size() - 1);
        return false;
    }
    public ETileType getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) return ETileType.WALL;
        return map[x][y];
    }

    public boolean isWalkable(int x, int y) {
        ETileType type = getTile(x, y);
        return type != ETileType.WALL;
    }

    public boolean isObjWalkable(float px, float py,int width, int height) {
        int left   = (int)(px / TILE_SIZE);
        int right  = (int)((px + width - 1) / TILE_SIZE);
        int top    = (int)((py + height - 1) /TILE_SIZE);
        int bottom = (int)(py / TILE_SIZE);

        return this.getTile(left, top) != ETileType.WALL &&
            this.getTile(right, top) != ETileType.WALL &&
            this.getTile(left, bottom) != ETileType.WALL &&
            this.getTile(right, bottom) != ETileType.WALL;
    }



    public void printfloor(SpriteBatch batch){
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                switch (map[x][y]) {
                    case WALL:
                        batch.setColor(Color.DARK_GRAY);
                        break;
                    case FLOOR:
                        batch.setColor(Color.LIGHT_GRAY);
                        break;
                    case COLLECTABLE:
                        batch.setColor(Color.YELLOW);
                        break;
                    case ENEMY:
                        batch.setColor(Color.RED);
                        break;
                }
                //spriteBatch.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                batch.draw(floortext, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }
}
