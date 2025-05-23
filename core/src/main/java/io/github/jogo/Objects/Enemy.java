package io.github.jogo.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
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

    public Enemy(float x, float y, int width, int height, World world) {
        super(x, y, width, height,world);


        this.spawnPoint = new Vector2(x, y);
        this.texture = new Texture("assets/v01/vilan.png"); // Substitui conforme necessário

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
}


