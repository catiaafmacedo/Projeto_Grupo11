package io.github.jogo.Scenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.jogo.Interfaces.*;
import io.github.jogo.Screens.World;
import io.github.jogo.Utils.Settings;

public class Hud implements Disposable,IUpdatable {
    public Stage stage;
    public World world;
    public ExtendViewport viewport;
    public Table table;
    private Integer worldTimer;
    private float timeCount;
    private static Integer score;
    // Now we create our widgets. Our widgets will be labels, essentially text, that allow us to display Game Information
    private Label countdownLabel;
    static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;
    private BitmapFont white;
    private Label healthLabel;

    //progressbar
    private ProgressBar scoreBar;
    private Texture texFillGreen, texFillYellow, texFillRed;
    private TextureRegionDrawable fillDrawableGreen, fillDrawableYellow, fillDrawableRed;
    private int BAR_WIDTH = 200, BAR_HEIGHT = 30;
    private enum HealthColor { GREEN, YELLOW, RED }
    private HealthColor currentColor = null;

    private ImageButton soundButton;
    Preferences prefs = Gdx.app.getPreferences("GameSettings");

    private boolean soundOn = prefs.getBoolean("soundEnabled");


    public Hud(SpriteBatch sb,World world) {
        //worldTimer = 30;
        worldTimer = 30;
        timeCount = 0;

        this.world = world;

        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());
        stage = new Stage(viewport, sb); // We must create order by creating a table in our stage

        table = new Table();
        table.top(); // Will put it at the top of our stage
        table.setFillParent(true);


        BitmapFont defaultFont = new BitmapFont(); // Fonte padrão
        Label.LabelStyle labelStyle = new Label.LabelStyle(defaultFont, Color.WHITE);

        countdownLabel = new Label(String.format("%03d", worldTimer), labelStyle);
        scoreLabel = new Label(String.format("%06d",world.player.getHealth()), labelStyle);
        timeLabel = new Label("Tempo", labelStyle);
        levelLabel = new Label("Labirinto easy", labelStyle);
        worldLabel = new Label("Ronda 1", labelStyle);
        marioLabel = new Label("SCORE:", labelStyle);

        Pixmap bgPixmap = new Pixmap(BAR_WIDTH, BAR_HEIGHT, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.DARK_GRAY);
        bgPixmap.fill();
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new Texture(bgPixmap));

        texFillGreen = createColorTexture(Color.GREEN);
        texFillYellow = createColorTexture(Color.YELLOW);
        texFillRed = createColorTexture(Color.RED);

        fillDrawableGreen = new TextureRegionDrawable(new TextureRegion(texFillGreen));
        fillDrawableYellow = new TextureRegionDrawable(new TextureRegion(texFillYellow));
        fillDrawableRed = new TextureRegionDrawable(new TextureRegion(texFillRed));

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = backgroundDrawable;
        style.knobBefore = fillDrawableGreen;
        style.knob = new TextureRegionDrawable(new TextureRegion(texFillGreen));

        scoreBar = new ProgressBar(0, 100, 1, false, style);
        scoreBar.setAnimateDuration(0.1f);
        scoreBar.setValue(100);
        scoreBar.setSize(BAR_WIDTH, BAR_HEIGHT);

        BitmapFont font = new BitmapFont(); // ou um customizado
        Label.LabelStyle HealthlabelStyle = new Label.LabelStyle(font, Color.WHITE);

        healthLabel = new Label("100%", HealthlabelStyle);

        createSoundToggleButton();

        //Criar o Hud no eran
        Table soundBarTable = new Table();
        soundBarTable.add(soundButton).size(32, 32).padRight(10);
        soundBarTable.add(scoreBar).width(BAR_WIDTH).height(BAR_HEIGHT).expandX();
        table.add(soundBarTable).expandX().padTop(10).left();
        //table.add(soundButton).size(32 , 32).pad(10).top().right();
        //table.add(scoreBar).width(BAR_WIDTH).height(BAR_HEIGHT).expandX().padTop(10);

        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);

        table.row();
        //table.add();
        table.add(healthLabel).padTop(5).padLeft(25);
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);

    }

    private void createSoundToggleButton() {
        Texture soundOnTex = new Texture(Gdx.files.internal("assets/v01/sound-on.png"));
        Texture soundOffTex = new Texture(Gdx.files.internal("assets/v01/sound-off.png"));

        Drawable soundOnDrawable = new TextureRegionDrawable(new TextureRegion(soundOnTex));
        Drawable soundOffDrawable = new TextureRegionDrawable(new TextureRegion(soundOffTex));

        soundButton = new ImageButton(soundOnDrawable, soundOffDrawable);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = soundOn ? soundOnDrawable : soundOffDrawable;
        soundButton.setStyle(style);
        soundButton.setChecked(false); // evita estado "imageChecked"
        soundButton.setLayoutEnabled(true);

        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundOn = !soundOn;

                // Atualiza o Drawable visível
                Drawable newDrawable = soundOn ? soundOnDrawable : soundOffDrawable;
                soundButton.getStyle().imageUp = newDrawable;

                // Força o botão a redesenhar
                soundButton.invalidate();
                soundButton.invalidateHierarchy();

                // Atualiza a lógica
                if (soundOn) {
                    Gdx.app.log("Som", "Ligado");
                    Settings.setSoundEnabled(true);
                    world.soundEnabled = true;
                    world.music.setVolume(1f);
                } else {
                    Gdx.app.log("Som", "Desligado");
                    Settings.setSoundEnabled(false);
                    world.soundEnabled = false;
                    world.music.setVolume(0f);
                }
            }
        });

        //table.add(soundButton).size(64, 64).pad(10).top().right();
    }

    @Override
    public void update(float dt, World world) {
        timeCount += dt;
        if (timeCount >= 1) {
            worldTimer++;
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }

        int currentHealth = Math.max(0, Math.min(100, world.player.getHealth()));
        scoreBar.setValue(currentHealth);
        healthLabel.setText(String.format("%03d%%", world.player.getHealth()));

        Color barcolor;
        ProgressBar.ProgressBarStyle style = scoreBar.getStyle();

        if (currentHealth >= 70) {
            barcolor= Color.GREEN;
        } else if (currentHealth >= 30) {
            barcolor= Color.YELLOW;
        } else {
            barcolor= Color.RED;
        }

        Texture fillDrawableColor = createColorTextureHealth(barcolor);
        style.knobBefore = new TextureRegionDrawable(new TextureRegion(fillDrawableColor));;
        style.knob = new TextureRegionDrawable(new TextureRegion(fillDrawableColor));;
        scoreBar.setStyle(style);

    }



    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(BAR_WIDTH, BAR_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    private Texture createColorTextureHealth(Color color) {

        int vida = 0;
        if (world.player.getHealth() > 0)
         vida = Math.round(((float) world.player.getHealth() / world.player.MaxHealth) * BAR_WIDTH);
        if (world.player.getHealth() > world.player.MaxHealth) vida = BAR_WIDTH;

        Pixmap pixmap = new Pixmap(vida, BAR_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }



    public Integer getTime(){
        return worldTimer;
    }


    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        batch.setProjectionMatrix(this.stage.getCamera().combined);
        this.stage.draw();
    }


    @Override
    public void dispose() {
        stage.dispose();
        texFillGreen.dispose();
        texFillYellow.dispose();
        texFillRed.dispose();

    }

}
