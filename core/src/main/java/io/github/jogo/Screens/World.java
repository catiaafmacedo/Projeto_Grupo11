package io.github.jogo.Screens;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.jogo.Enums.EEnemyTypes;
import io.github.jogo.Enums.ETileType;
import io.github.jogo.Interfaces.*;
import io.github.jogo.Objects.*;
import io.github.jogo.Scenes.*;
import java.util.*;

public class World implements Screen {
    public static final int TILE_SIZE = 64;
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    public static final com.badlogic.gdx.audio.Music damageSound = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/damage.wav"));
    public static final  com.badlogic.gdx.audio.Music music  = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/musica.mp3"));
    public static final  com.badlogic.gdx.audio.Music LifeMusic  = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/energy-drink.mp3"));

    public boolean soundEnabled;

    private final ETileType[][] map;

    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch = new SpriteBatch();

    public Player player;
    private final List<IRenderable> renderables = new ArrayList<>();
    private final List<IUpdatable> updatables = new ArrayList<>();
    private final List<IUpdatable> ObjToDelete = new ArrayList<>();

    Texture floortext = new Texture("assets/v01/floor.png"); // ou outra apropriada
    private float restartTimer = 0f;
    private boolean showMenu = true;
    private boolean gameOver = false;
    public boolean damageSoundPlaying = false;
    public boolean LifeSoundPlaying = false;

    Preferences prefs = Gdx.app.getPreferences("GameSettings");

    private final Hud hud;
    private final PIPRenderer pipRenderer;
    public Game game;



    public World(Game game) {

        InputManager.init();
        this.soundEnabled = prefs.getBoolean("soundEnabled", true);
        //this.soundEnabled =false;
        map = new ETileType[WIDTH][HEIGHT];
        generateMaze();
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        damageSound.setOnCompletionListener(music -> damageSoundPlaying = false);
        LifeMusic.setOnCompletionListener(LifeMusic -> LifeSoundPlaying = false);
        music.setLooping(true);
        music.play();
        if(soundEnabled) {
            music.setVolume(1f);  // ou ativa o som
        }else{
            music.setVolume(0f);  // ou pausa
        }

        player = new Player(TILE_SIZE, TILE_SIZE,WIDTH,HEIGHT, this);
        addObject(player);

        spawnEnemies(5);
        spawnReforcosVida(10);

        // No fim do labirinto
        int fimX = WIDTH - 3;
        int fimY = HEIGHT - 3;
        int fimX1 = 1;
        int fimY1 = HEIGHT - 3;

        Enemy boss = new Enemy(fimX * TILE_SIZE, fimY * TILE_SIZE, WIDTH, HEIGHT, this, EEnemyTypes.Boss);
        addObject(boss);
        Enemy boss1 = new Enemy(fimX1 * TILE_SIZE, fimY1 * TILE_SIZE, WIDTH, HEIGHT, this, EEnemyTypes.Boss);
        addObject(boss1);



        hud = new Hud(spriteBatch,this);
        addObject(hud);
        Gdx.input.setInputProcessor(hud.stage);

        pipRenderer = new PIPRenderer(200, 150, 10, 10); // 200x150 no canto inferior direito

    }

    public void playdamage(){

        if(soundEnabled) {
            if (!damageSoundPlaying) {
                damageSound.play();
                damageSoundPlaying = true;
            }
        }
    }

    public void playItem(){

        if(soundEnabled) {
            if (!LifeSoundPlaying) {
                LifeMusic.play();
                LifeSoundPlaying = true;
            }
        }
    }


    private void generateMaze() {
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                map[x][y] = ETileType.WALL;

        int startX = 1, startY = 1;
        int[][] goals = {
            {WIDTH - 2, HEIGHT - 2},
            {WIDTH - 2, HEIGHT / 2},
            {WIDTH / 2, HEIGHT - 2}
        };

        map[startX][startY] = ETileType.FLOOR;


        for (int[] goal : goals) {
            List<int[]> path = generatePath(startX, startY, goal[0], goal[1]);
            for (int[] pos : path) {
                map[pos[0]][pos[1]] = ETileType.FLOOR;
            }
        }

        map[WIDTH-2][HEIGHT-3] = ETileType.FLOOR;
        map[WIDTH-1][HEIGHT-3] = ETileType.FLOOR;

    }

    private List<int[]> generatePath(int startX, int startY, int endX, int endY) {
        List<int[]> path = new ArrayList<>();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        dfs(startX, startY, endX, endY, path, visited);
        return path;
    }
    private boolean dfs(int x, int y, int endX, int endY, List<int[]> path, boolean[][] visited) {
        if (x < 1 || y < 1 || x >= WIDTH - 1 || y >= HEIGHT - 1 || visited[x][y])
            return false;

        visited[x][y] = true;
        path.add(new int[]{x, y});
        map[x][y] = ETileType.FLOOR;

        if (x == endX && y == endY)
            return true;

        int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
        List<int[]> dirList = Arrays.asList(dirs);
        Collections.shuffle(dirList);

        for (int[] d : dirList) {
            int nx = x + d[0];
            int ny = y + d[1];
            int wallX = x + d[0] / 2;
            int wallY = y + d[1] / 2;

            if (nx >= 1 && ny >= 1 && nx < WIDTH - 1 && ny < HEIGHT - 1 && !visited[nx][ny]) {
                map[wallX][wallY] = ETileType.FLOOR; // Abre parede
                if (dfs(nx, ny, endX, endY, path, visited))
                    return true;
            }
        }

        path.remove(path.size() - 1);
        return false;
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
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        printfloor(spriteBatch);

        for (IRenderable obj : renderables)
            obj.render(spriteBatch);

        spriteBatch.end();
        hud.render(spriteBatch);

        // este método só desenha o mapa, jogador e inimigos (sem HUD)
        pipRenderer.capture(this::renderMini);
        pipRenderer.drawPIP();

        // Implementa o setscreen

        if (showMenu && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            showMenu = false;
        }

        if (gameOver) {
            restartTimer += delta;
            if (restartTimer >= 3f) {
                restartGame();
                return;
            }
        }

        if (gameOver) return;

        for (IUpdatable obj : updatables) {
            obj.update(delta,this);
        }
        if (!ObjToDelete.isEmpty()) {
            for (IUpdatable obj : ObjToDelete) {
                this.removeObject((AObject) obj);
            }
            ObjToDelete.clear();
        }


    }
    void printfloor(SpriteBatch batch){
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                switch (map[x][y]) {
                    case WALL:
                        spriteBatch.setColor(Color.DARK_GRAY);
                        break;
                    case FLOOR:
                        spriteBatch.setColor(Color.LIGHT_GRAY);
                        break;
                    case COLLECTABLE:
                        spriteBatch.setColor(Color.YELLOW);
                        break;
                    case ENEMY:
                        spriteBatch.setColor(Color.RED);
                        break;
                }
                //spriteBatch.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                batch.draw(floortext, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }
    public void renderMini() {
        // Configura uma câmara que mostra o mapa inteiro
        OrthographicCamera miniCam = new OrthographicCamera();
        miniCam.setToOrtho(false, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        miniCam.update();

        spriteBatch.setProjectionMatrix(miniCam.combined);

        spriteBatch.begin();


        // 2. Desenha o mapa do mundo
        printfloor(spriteBatch);

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

    public ETileType getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) return ETileType.WALL;
        return map[x][y];
    }

    public boolean isWalkable(int x, int y) {
        ETileType type = getTile(x, y);
        return type != ETileType.WALL;
    }

    public void spawnEnemies(int quantidade) {
        Random rand = new Random();
        int tentativas = 0;

        while (quantidade > 0 && tentativas < 1000) {
            int tx = rand.nextInt(WIDTH);
            int ty = rand.nextInt(HEIGHT);

            if (isWalkable(tx, ty) && isNotOccupied(tx, ty, null)) {
                Enemy enemy = new Enemy(tx * TILE_SIZE, ty * TILE_SIZE, WIDTH, HEIGHT, this, EEnemyTypes.Standard);
                addObject(enemy);
                quantidade--;
            }
            tentativas++;
        }
    }

    public void spawnReforcosVida(int quantidade) {
        Random rand = new Random();
        int tentativas = 0;

        while (quantidade > 0 && tentativas < 1000) {
            int tx = rand.nextInt(WIDTH);
            int ty = rand.nextInt(HEIGHT);

            if (isWalkable(tx, ty) && isNotOccupied(tx, ty, null)) {
                Collectables reforco = new Collectables(tx * TILE_SIZE, ty * TILE_SIZE, WIDTH, HEIGHT, this);
                addObject(reforco);
                quantidade--;
            }
            tentativas++;
        }
    }
    public void removeObject(AObject object) {
        if (object != null) {
            renderables.remove(object);
        }
        if (object != null) {
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

    private void restartGame() {
        //player = new Player(64, 64, TILE_SIZE, TILE_SIZE);
        player = new Player(TILE_SIZE, TILE_SIZE,WIDTH,HEIGHT,this);

        spawnEnemies(5);
        spawnReforcosVida(3);
        gameOver = false;
        restartTimer = 0f;
    }
    public void GameOver() {
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
    public void resize(int width, int height) {}
}






