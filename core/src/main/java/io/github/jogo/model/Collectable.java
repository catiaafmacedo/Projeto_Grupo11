package io.github.jogo.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import io.github.jogo.Collectables.ECollectableType;
import io.github.jogo.Collectables.ICollectStrategy;
import io.github.jogo.Interfaces.IRenderable;
import io.github.jogo.Interfaces.IUpdatable;
import io.github.jogo.game.World;

public class Collectable extends AObject implements Disposable,IRenderable, IUpdatable {
    private float timeSinceLastMove = 0f;
    private Texture texture;
    public final ICollectStrategy strategy;

    public Collectable(float x, float y, int width, int height, World world, ECollectableType type, ICollectStrategy strategy) {
        super(x, y, width, height, world);
        this.strategy = strategy;
        switch (type) {
            case HEART:
                this.texture = new Texture("assets/v01/heart.png");
                break;
            case KEY:
                this.texture = new Texture("assets/v01/key.png");
                break;
            case LEVEL:
                this.texture = new Texture("assets/v01/gate.jpg");
                break;
        }
    }

    @Override
    public void update(float delta, World world) {
        timeSinceLastMove += delta;
        float moveCooldown = 1.0f;
        if (timeSinceLastMove < moveCooldown) return;

        timeSinceLastMove = 0f;


    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, this.getX(), this.getY(), width, height);
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

}


