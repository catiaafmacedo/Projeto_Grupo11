package io.github.jogo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.jogo.Scenes.*;
import io.github.jogo.Screens.*;


public class Main extends ApplicationAdapter {
    private World world;

    @Override
    public void create() {
        InputManager.init();
        world = new World();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.update(delta);
        world.render();
    }


    @Override
    public void dispose() {}
}
