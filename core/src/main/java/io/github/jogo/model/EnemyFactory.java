package io.github.jogo.model;

import io.github.jogo.Enums.EEnemyTypes;
import io.github.jogo.game.World;

import java.util.Random;

public class EnemyFactory {
    public static Enemy create(World world, EEnemyTypes tipo){
        Enemy enemy = null;
        Random rand = new Random();
        int tentativas = 0;

        while ( tentativas < 1000) {
            int tx = rand.nextInt(World.WIDTH);
            int ty = rand.nextInt(World.HEIGHT);

            if (world.maze.isWalkable(tx, ty) && world.isNotOccupied(tx, ty, null)) {
                enemy = new Enemy(tx * World.TILE_SIZE, ty * World.TILE_SIZE, World.TILE_SIZE-4, World.TILE_SIZE-4, world, tipo);
                break;
            }
            tentativas++;
        }
        return enemy;
    }
    public static Enemy createFixed(int x,int y,World world, EEnemyTypes tipo){
        return new Enemy(x , y , World.WIDTH -4, World.HEIGHT-4, world, tipo);
    }
}
