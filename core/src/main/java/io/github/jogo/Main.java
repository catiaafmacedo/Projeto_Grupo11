package io.github.jogo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.jogo.Scenes.*;
import io.github.jogo.Screens.*;

import com.badlogic.gdx.Game;

public class Main extends Game  {
    private World world;

    @Override
    public void create() {

        this.setScreen(new MainMenuScreen(this));
        //world = new World();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(); // ESSENCIAL: chama render() do ecrã atual
      //  world.update(delta);
      //  world.render(delta);
    }


    @Override
    public void dispose() {}
}
