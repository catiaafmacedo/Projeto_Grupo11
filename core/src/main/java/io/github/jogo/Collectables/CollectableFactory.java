package io.github.jogo.Collectables;

import io.github.jogo.game.World;
import io.github.jogo.model.Collectable;

import java.util.Random;

public class CollectableFactory {
    public static Collectable create(World world, ECollectableType type) {
        ICollectStrategy strategy;
        Random rand = new Random();
        int x=0, y=0;

        switch (type) {
            case KEY:
                strategy = new KeyCollectStrategy();

                break;
            case HEART:
                strategy = new LifeCollectStrategy();
                break;
            // podes adicionar outros tipos aqui
            default:
                throw new IllegalArgumentException("Tipo desconhecido: " + type);
        }

        int tentativas = 0;
        while (tentativas < 1000) {
            int tx = rand.nextInt(World.WIDTH);
            int ty = rand.nextInt(World.HEIGHT);

            if (world.maze.isWalkable(tx, ty) && world.isNotOccupied(tx, ty, null)) {
                x= tx * World.TILE_SIZE;
                y= ty * World.TILE_SIZE;
            }
            tentativas++;
        }

        return new Collectable(x, y, World.TILE_SIZE -4, World.TILE_SIZE-4,world,type, strategy);
    }
    public static Collectable create(World world,int x,int y, ECollectableType type) {
        ICollectStrategy strategy;
        switch (type) {
            case KEY:
                strategy = new KeyCollectStrategy();
                break;
            case HEART:
                strategy = new LifeCollectStrategy();
                break;
            case LEVEL:
                strategy = new LevelGameCollectStrategy();
                break;
            // podes adicionar outros tipos aqui
            default:
                throw new IllegalArgumentException("Tipo desconhecido: " + type);
        }

        return new Collectable(x, y, World.TILE_SIZE , World.TILE_SIZE,world,type, strategy);

    }
}
