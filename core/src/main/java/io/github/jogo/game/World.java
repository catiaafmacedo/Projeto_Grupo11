package io.github.jogo.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.jogo.Collectables.CollectableFactory;
import io.github.jogo.Enums.EGameWorldState;
import io.github.jogo.controller.InputHandler;
import io.github.jogo.game.logic.MazeGenerator;
import io.github.jogo.game.logic.SoundManager;
import io.github.jogo.model.*;
import io.github.jogo.Collectables.ECollectableType;
import io.github.jogo.Enums.EEnemyTypes;
import io.github.jogo.Interfaces.*;
import io.github.jogo.Screens.GameOverScreen;
import io.github.jogo.ui.PIPRenderer;
import io.github.jogo.ui.Hud;

import java.util.*;

public class World implements Screen {
    public static final int TILE_SIZE = 32;
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    public int MAZE_WIDTH = 64;
    public int MAZE_HEIGHT = 32;
    public int nivel = 1;

    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch = new SpriteBatch();

    private String message = "";
    private float messageTimer = 0f;
    private final BitmapFont font = new BitmapFont();

    public Player player;
    private List<IRenderable> renderables = new ArrayList<>();
    private List<IUpdatable> updatables = new ArrayList<>();
    private final List<IUpdatable> ObjToDelete = new ArrayList<>();
    private final List<InputHandler> inputHandlers = new ArrayList<>();

    public MazeGenerator maze=  new MazeGenerator(this) ;

    private EGameWorldState state = EGameWorldState.RUNNING;


    private final Hud hud;
    private PIPRenderer pipRenderer;
    public Game game;

    public final float speed = 100;

    public SoundManager sound =SoundManager.getInstance() ;

    public World(Game game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.player=new Player(TILE_SIZE, TILE_SIZE,TILE_SIZE-4,TILE_SIZE-4, this);
        addObject(this.player);

        createcharacters();


        hud = new Hud(spriteBatch,this);
        addObject(hud);
        Gdx.input.setInputProcessor(hud.stage);

        pipRenderer = new PIPRenderer.Builder()
            .setSize(200, 150)
            .setMargin(10, 10)
            .build();

    }

    public void setstate(EGameWorldState state) {
        this.state = state;
    }

    public void addInputHandlers(InputHandler handler){
        inputHandlers.add(handler);
    }

    public void addDeleteObj(AObject obj) {
        ObjToDelete.add(obj);
    }

    public void addObject(Object obj) {
        if (obj instanceof IRenderable) {
            renderables.add((IRenderable) obj);
        }
        if (obj instanceof IUpdatable) {
            updatables.add((IUpdatable) obj);
        }
    }

    public List<IUpdatable> getupdatables(){
        return updatables;
    }


    public void render(float delta) {
        switch (state) {
            case RUNNING:
                renderRunning(delta);
                break;
            case GAME_OVER:
                renderGameOver();
                break;
            case NEXT_LEVEL:
                renderNextLevel();
                break;
        }
    }


    public void renderRunning(float delta) {
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        maze.printfloor(spriteBatch);
        for (IRenderable obj : renderables)
            obj.render(spriteBatch);

        updatemessage(spriteBatch,delta);

        spriteBatch.end();

        hud.render(spriteBatch);

        // este método só desenha o mapa, jogador e inimigos (sem HUD)
        pipRenderer.capture(this::renderMini);
        pipRenderer.drawPIP();


        for(InputHandler inputHandler : inputHandlers) {
            inputHandler.handleInput();
        }

        for (IUpdatable obj : updatables) {
            obj.update(delta,this);
        }
        // Apaga os objetos marcados
        if (!ObjToDelete.isEmpty()) {
            for (IUpdatable obj : ObjToDelete) {
                this.removeObject((AObject) obj);
            }
            ObjToDelete.clear();
        }


    }

    public void renderMini() {
        // Configura uma câmara que mostra o mapa inteiro
        OrthographicCamera miniCam = new OrthographicCamera();
        miniCam.setToOrtho(false, MAZE_WIDTH * TILE_SIZE, MAZE_HEIGHT * TILE_SIZE);
        miniCam.update();

        spriteBatch.setProjectionMatrix(miniCam.combined);

        spriteBatch.begin();


        // 2. Desenha o mapa do mundo
        maze.printfloor(spriteBatch);

        // 3. Desenha todos os objetos do mundo (inimigos, reforços, etc.)
        for (IRenderable obj : renderables) {
            obj.render(spriteBatch);  // Assumindo que todos implementam IRenderable
        }

        // 4. Desenha o jogador
        if (player != null) {
            player.render(spriteBatch);
        }

        spriteBatch.end();
    }

    public boolean isNotOccupied(float tileX, float tileY, AObject requester) {
        for (IRenderable obj : renderables) {
            if (obj instanceof AObject) {
                AObject other = (AObject) obj;
                if (other != requester) {
                    int ox = (int)(other.getX() / TILE_SIZE);
                    int oy = (int)(other.getY() / TILE_SIZE);
                    if (ox == tileX && oy == tileY) return false;
                }
            }
        }
        return true;
    }


    public void createcharacters(){
        int nenemys=5,nheart=10;
        switch (nivel){
            case 2:
                int fimX = 1;
                int fimY = MAZE_HEIGHT-3;
                // Adiciona AI Enemy no outro canto
                addObject(EnemyFactory.createFixed(fimX * TILE_SIZE, fimY * TILE_SIZE,this,EEnemyTypes.Boss));
                nenemys=7;
                nheart=8;
                break;
            case 3:
                nenemys=10;
                nheart=6;
                break;
            case 4:
                nenemys=14;
                nheart=8;
                break;
        }

        int quantidade=nenemys;
        while (quantidade > 0 ) {
            addObject(EnemyFactory.create(this, EEnemyTypes.Standard));
            quantidade--;
        }

        quantidade = nheart;
        while (quantidade > 0) {
            addObject(CollectableFactory.create(this,  ECollectableType.HEART));
            quantidade--;
        }

        addObject(CollectableFactory.create( this, ECollectableType.KEY));
        // No fim do labirinto
        int fimX = MAZE_WIDTH - 3;
        int fimY = MAZE_HEIGHT - 3;
        int fimX1 = 1;
        int fimY1 = MAZE_HEIGHT - 3;

        // Adiciona AI Enemy No fim do labirinto
        addObject(EnemyFactory.createFixed(fimX * TILE_SIZE, fimY * TILE_SIZE,this,EEnemyTypes.Boss));
        // Adiciona AI Enemy no outro canto
        addObject(EnemyFactory.createFixed(fimX1 * TILE_SIZE, fimY1 * TILE_SIZE,this,EEnemyTypes.Boss));

        addObject(CollectableFactory.create(this,(MAZE_WIDTH-1)*TILE_SIZE, (MAZE_HEIGHT-3)*TILE_SIZE,ECollectableType.LEVEL));

    }


    public void renderNextLevel(){
        setstate(EGameWorldState.RUNNING);

        if (player != null) {
            player.setKey(false);
            player.setPosition(TILE_SIZE, TILE_SIZE);
        }

        clearcharacters();
        addObject(player);

        nivel++;
        this.MAZE_WIDTH=MAZE_WIDTH+2;
        maze = new MazeGenerator(this);
        createcharacters();

    }

    public void showMessage(String msg) {
        this.message = msg;
        this.messageTimer = 2.5f;
    }
    private void updatemessage(SpriteBatch spriteBatch,float delta) {
        messageTimer -= delta;

        if (message != null && !message.isEmpty() && messageTimer > 0) {
            font.setColor(Color.GOLD);
            font.draw(spriteBatch, message, player.getX()-message.length()/2*7, player.getY()+TILE_SIZE+10);
        }else{
            messageTimer=0;
            message = "";
        }

    }

    public void clearcharacters(){
        List<IRenderable> newrend = new ArrayList<>();
        List<IUpdatable> newupd = new ArrayList<>();
        for (IRenderable obj : renderables)
            if (obj instanceof AObject) {
                ((AObject)obj).dispose();
            }else{
                newrend.add(obj);
            }

        for (IUpdatable obj : updatables)
            if (obj instanceof AObject) {
                ((AObject) obj).dispose();
            }else{
                newupd.add(obj);
            }
        renderables.clear();
        updatables.clear();
        renderables=newrend;
        updatables=newupd;

        ObjToDelete.clear();

    }

    public void removeObject(AObject object) {
        if (object != null) {
            renderables.remove(object);
            updatables.remove(object);
        }
        Objects.requireNonNull(object).dispose(); // limpa recursos
    }

    public int getTileX(float x) {
        return (int)(x / World.TILE_SIZE);
    }

    public int getTileY(float y) {
        return (int)(y / World.TILE_SIZE);
    }



    public void renderGameOver() {
        game.setScreen(new GameOverScreen(game));
    }
    @Override
    public void show() {}
    @Override
    public void hide() {}
    @Override
    public void resume() {}
    @Override
    public void pause() {}
    @Override
    public void dispose() {}
    @Override
    public void resize(int width, int height) {
        if (camera != null) {
            camera.setToOrtho(false, width, height);
            camera.update();
        }
        // Se tiveres Stage ou Hud com viewport:
        if (hud != null && hud.stage != null) {
            hud.stage.getViewport().update(width, height, true); // true = centra os elementos
        }

        pipRenderer = new PIPRenderer.Builder()
            .setSize(200, 150)
            .setMargin(10, 10)
            .build();


    }
}






