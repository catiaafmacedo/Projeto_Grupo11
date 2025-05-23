package io.github.jogo.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputManager {

    public static void init() {
        // Placeholder se quiseres configurar InputProcessor no futuro
    }

    public static boolean isLeft() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
    }

    public static boolean isRight() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
    }

    public static boolean isUp() {
        return Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
    }

    public static boolean isDown() {
        return Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
    }
}
