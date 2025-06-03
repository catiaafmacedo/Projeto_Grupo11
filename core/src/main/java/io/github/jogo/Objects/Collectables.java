package io.github.jogo.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import io.github.jogo.Interfaces.IRenderable;
import io.github.jogo.Interfaces.IUpdatable;
import io.github.jogo.Screens.World;

public class Collectables extends AObject implements Disposable,IRenderable, IUpdatable {
    private float timeSinceLastMove = 0f;
    private Texture texture;

    public Collectables(float x, float y, int width, int height, World world) {
        super(x, y, width, height,world);


        this.texture = new Texture("assets/v01/heart.png"); // Substitui conforme necessário

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
