package io.github.jogo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.jogo.Screens.*;

import com.badlogic.gdx.Game;

public class Main extends Game  {


    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(); // ESSENCIAL: chama render() do ecrã atual

    }


    @Override
    public void dispose() {}
}
