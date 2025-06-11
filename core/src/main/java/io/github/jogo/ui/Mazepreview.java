package io.github.jogo.ui;

import java.util.Random;

public class Mazepreview {
    private final int width;
    private final int height;
    private final boolean[][] maze;
    private final Random rand = new Random();

    // Construtor privado (só pode ser chamado pelos métodos factory)
    private Mazepreview(int width, int height) {
        this.width = width;
        this.height = height;
        this.maze = new boolean[width][height];
    }

    // Método factory
    public static Mazepreview randomWithBorderMaze(int width, int height) {
        Mazepreview m = new Mazepreview(width, height);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                m.maze[x][y] = (x == 0 || y == 0 || x == width - 1 || y == height - 1) || m.rand.nextBoolean();
        m.maze[1][1] = false; // ponto de entrada
        return m;
    }

    public boolean isWall(int x, int y) {
        return maze[x][y];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
