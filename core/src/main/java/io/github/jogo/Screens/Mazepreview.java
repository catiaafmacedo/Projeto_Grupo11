package io.github.jogo.Screens;

import java.util.Random;

public class Mazepreview {
    private int width, height;
    private boolean[][] maze;
    private Random rand = new Random();

    public Mazepreview(int width, int height) {
        this.width = width;
        this.height = height;
        this.maze = new boolean[width][height];
        generateMaze();
    }

    private void generateMaze() {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                maze[x][y] = rand.nextBoolean(); // aleatório: true = parede

        maze[1][1] = false; // ponto de entrada
    }

    public boolean isWall(int x, int y) {
        return maze[x][y];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
