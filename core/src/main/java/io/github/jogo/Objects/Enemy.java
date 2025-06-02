package io.github.jogo.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.jogo.Enums.EEnemyTypes;
import io.github.jogo.Interfaces.*;
import io.github.jogo.PathFinder;
import io.github.jogo.Screens.*;

import java.util.List;

public class Enemy extends AObject implements IRenderable, IUpdatable {
    private Vector2 spawnPoint;
    private float range = 6f;
    private float moveCooldown = 1.0f;
    private float timeSinceLastMove = 0f;
    private Texture texture;
    private EEnemyTypes type;
    private World world;

    public Enemy(float x, float y, int width, int height, World world,EEnemyTypes type) {
        super(x, y, width, height,world);
        this.type = type;
        this.world = world;

        this.spawnPoint = new Vector2(x, y);
        switch (this.type){
            case Boss:
                this.texture = new Texture("assets/v01/enemyboss.png"); // Substitui conforme necessário
                break;
            case Standard:
                this.texture = new Texture("assets/v01/vilan.png"); // Substitui conforme necessário
                break;

        }

    }
    public void repositionToValidTile(World world) {
        for (int tx = 1; tx < World.WIDTH - 1; tx++) {
            for (int ty = 1; ty < World.HEIGHT - 1; ty++) {
                if (world.isWalkable(tx, ty) && !world.isOccupied(tx, ty, this)) {
                    this.setX(tx * World.TILE_SIZE);
                    this.setY(ty * World.TILE_SIZE);
                    this.spawnPoint.set(this.getX(), this.getY());
                    return;
                }
            }
        }
    }
    @Override
    public void update(float delta, World world) {
        Player player = world.player;
        timeSinceLastMove += delta;
        if (timeSinceLastMove < moveCooldown) return;

        timeSinceLastMove = 0f;
        if(this.type == EEnemyTypes.Boss){
            Vector2 start = new Vector2(world.getTileX(this.getX()), world.getTileY(this.getY()));
            Vector2 end = new Vector2(world.getTileX(world.player.getX()), world.getTileY(world.player.getY()));

            List<Vector2> path = PathFinder.findPath(world, start, end);

            if (path != null && path.size() > 1) {
                Vector2 next = path.get(1); // ignora o ponto 0 (posição atual)
                float targetX = next.x * World.TILE_SIZE;
                float targetY = next.y * World.TILE_SIZE;
                // Movimento simples: teleporta passo a passo

                this.setPosition(targetX, targetY);

            }

            if (this.getObjectrect().overlaps(world.player.getObjectrect()))
                world.player.onCollision(this);
        }
    }

    private boolean isBlockingPlayer(Vector2 nextTile, Vector2 playerPos) {
        return nextTile.epsilonEquals(playerPos, 0.1f);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, this.getX(), this.getY(), width, height);
    }

    @Override
    public void onCollision(AObject other) {
     /*   if (other instanceof Player) {
            System.out.println("⚠ Inimigo colidiu com o jogador!");
        }*/
    }


    @Override
    public void notifyPositionChanged() {
        if (world != null) {
            if (this.getObjectrect().overlaps(world.player.getObjectrect()))
                world.player.onCollision(this);
        }

    }
}


