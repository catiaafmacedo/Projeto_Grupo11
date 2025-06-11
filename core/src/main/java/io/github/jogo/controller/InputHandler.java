package io.github.jogo.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.jogo.Interfaces.ICommand;

public class InputHandler {
    private final ICommand leftCommand;
    private final ICommand rightCommand;
    private final ICommand upCommand;
    private final ICommand downCommand;

    public InputHandler(ICommand left, ICommand right, ICommand up, ICommand down) {
        this.leftCommand = left;
        this.rightCommand = right;
        this.upCommand = up;
        this.downCommand = down;
    }

    public void handleInput() {
        if (isLeft())  leftCommand.execute();
        if (isRight()) rightCommand.execute();
        if (isUp())    upCommand.execute();
        if (isDown())  downCommand.execute();
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

