package io.github.jogo.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.jogo.Utils.FrameExtractor;
import io.github.jogo.game.World;

public class GameOverScreen implements Screen {

    private float stateTime = 0f;

    private final Stage stage;
    private final Skin skin;
    private final FrameExtractor extractor;
    private final Image animatedBackground;

    public GameOverScreen(final Game game) {

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        extractor = FrameExtractor.getInstance("GameOverSpriteSheet.png", 150, 768, 432);



        // Fundo animado como Image
        TextureRegion initialFrame = new TextureRegion(extractor.getFrame(0));
        animatedBackground = new Image(new TextureRegionDrawable(initialFrame));
        animatedBackground.setFillParent(true);
        stage.addActor(animatedBackground);

        // Botões
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(100);

        TextButton btnRestart = new TextButton("Reiniciar", skin);
        TextButton btnExit = new TextButton("Sair", skin);


        table.add(btnRestart).pad(10).row();
        table.add(btnExit).pad(10).row();
        table.center();
        stage.addActor(table);

        // Eventos
        btnRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new World(game));
                dispose();
            }
        });

        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int frameIndex = (int)(stateTime * 24) % extractor.getFrameCount();
        TextureRegion currentRegion = new TextureRegion(extractor.getFrame(frameIndex));
        animatedBackground.setDrawable(new TextureRegionDrawable(currentRegion));

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        extractor.dispose();
        stage.dispose();
        skin.dispose();
        FrameExtractor.reset();
    }
}
