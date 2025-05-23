package io.github.jogo.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import io.github.jogo.Interfaces.IRenderable;
import io.github.jogo.Interfaces.IUpdatable;
import io.github.jogo.PathFinder;
import io.github.jogo.Screens.World;

import java.util.List;

public class ReforcoVida extends AObject implements Disposable,IRenderable, IUpdatable {
    private Vector2 spawnPoint;
    private float range = 6f;
    private float moveCooldown = 1.0f;
    private float timeSinceLastMove = 0f;
    private Texture texture;

    public ReforcoVida(float x, float y, int width, int height, World world) {
        super(x, y, width, height,world);


        this.spawnPoint = new Vector2(x, y);
        this.texture = new Texture("assets/v01/heart.png"); // Substitui conforme necessário

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
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

}


