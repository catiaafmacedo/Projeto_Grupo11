package io.github.jogo.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.jogo.Utils.Settings;

public class MainMenuScreen implements Screen {

    private final Stage stage;
    private boolean soundOn;
    private final Mazepreview previewMaze;
    private final Texture wallTex;
    private final Texture floorTex;
    private final SpriteBatch batch;



    public MainMenuScreen(Game game) {
        this.soundOn = Settings.isSoundEnabled();

        previewMaze = new Mazepreview(20, 15); // pequeno labirinto
        wallTex = new Texture("assets/v01/wall.png");
        floorTex = new Texture("assets/v01/floor.png");

        this.batch = new SpriteBatch();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("assets/ui/uiskin.json"));

        Label title = new Label("Dungeon Krawler", skin);
        TextButton startButton = new TextButton("Iniciar Jogo", skin);
        TextButton soundButton = new TextButton("Som: " + (soundOn ? "Ligado" : "Desligado"), skin);
        TextButton exitButton = new TextButton("Sair", skin);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new World(game)); // substitui com tua classe World
                game.setScreen(new World(game)); // substitui com tua classe World
            }
        });

        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundOn = !soundOn;
                soundButton.setText("Som: " + (soundOn ? "Ligado" : "Desligado"));
                Settings.setSoundEnabled(soundOn); // se quiseres guardar globalmente
                if (soundOn) {
                    Gdx.app.log("Som", "Ligado");
                    Settings.setSoundEnabled(true);

                } else {
                    Gdx.app.log("Som", "Desligado");
                    Settings.setSoundEnabled(false);

                }

            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(title).padBottom(40);
        table.row();
        table.add(startButton).width(200).pad(10);
        table.row();
        table.add(soundButton).width(200).pad(10);
        table.row();
        table.add(exitButton).width(200).pad(10);
        System.out.println("Criei!");
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setColor(1, 1, 1, 0.2f); // opacidade baixa

        int tileSize = 64;
        int startX = 0;
        int startY = 0;

        for (int x = 0; x < previewMaze.getWidth(); x++) {
            for (int y = 0; y < previewMaze.getHeight(); y++) {
                Texture tex = previewMaze.isWall(x, y) ? wallTex : floorTex;
                batch.draw(tex, startX + x * tileSize, startY + y * tileSize, tileSize, tileSize);
            }
        }

        batch.setColor(1, 1, 1, 1); // repõe opacidade
        batch.end();

        stage.act(delta);
        stage.draw();

    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        batch.dispose();
        wallTex.dispose();
        floorTex.dispose();
    }
}
