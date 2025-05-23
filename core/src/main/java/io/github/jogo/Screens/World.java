package io.github.jogo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dongbat.jbump.Point;
import io.github.jogo.Enums.ETileType;
import io.github.jogo.Interfaces.*;
import io.github.jogo.Objects.*;
import io.github.jogo.Scenes.*;

import java.sql.Ref;
import java.util.*;

public class World {
    public static final int TILE_SIZE = 64;
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    public static final com.badlogic.gdx.audio.Music damageSound = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/damage.wav"));
    public static final  com.badlogic.gdx.audio.Music music  = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/musica.mp3"));
    public static final  com.badlogic.gdx.audio.Music LifeMusic  = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/energy-drink.mp3"));

    public boolean soundEnabled;

    private ETileType[][] map;
    private boolean[][] visited;
    private Random random = new Random();

    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public Player player;
    private List<IRenderable> renderables = new ArrayList<>();
    private List<IUpdatable> updatables = new ArrayList<>();
    private List<IUpdatable> ObjToDelete = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();

    Texture floortext = new Texture("assets/v01/floor.png"); // ou outra apropriada
    private float restartTimer = 0f;
    private boolean showMenu = true;
    private boolean gameOver = false;
    public boolean damageSoundPlaying = false;
    public boolean LifeSoundPlaying = false;

    Preferences prefs = Gdx.app.getPreferences("GameSettings");

    private Hud hud;
    private PIPRenderer pipRenderer;




    public World() {
        this.soundEnabled = prefs.getBoolean("soundEnabled", true);
        //this.soundEnabled =false;
        map = new ETileType[WIDTH][HEIGHT];
        visited = new boolean[WIDTH][HEIGHT];
        generateMaze();


        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch = new SpriteBatch();

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
        int fimX2 = WIDTH-3;
        int fimY2 = 1;

        EnemyFinalBoss boss = new EnemyFinalBoss(fimX * TILE_SIZE, fimY * TILE_SIZE, WIDTH, HEIGHT, this);
        addObject(boss);
        EnemyFinalBoss boss1 = new EnemyFinalBoss(fimX1 * TILE_SIZE, fimY1 * TILE_SIZE, WIDTH, HEIGHT, this);
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




    private void carve(int x, int y) {
        visited[x][y] = true;
        map[x][y] = ETileType.FLOOR;

        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};
        int[] dirs = {0, 1, 2, 3};
        shuffle(dirs);

        for (int i = 0; i < 4; i++) {
            int nx = x + dx[dirs[i]] * 2;
            int ny = y + dy[dirs[i]] * 2;

            if (inBounds(nx, ny) && !visited[nx][ny]) {
                map[x + dx[dirs[i]]][y + dy[dirs[i]]] = ETileType.FLOOR;
                carve(nx, ny);
            }
        }
    }

    private void shuffle(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int r = random.nextInt(array.length);
            int tmp = array[i];
            array[i] = array[r];
            array[r] = tmp;
        }
    }


    private boolean inBounds(int x, int y) {
        return x > 0 && y > 0 && x < WIDTH - 1 && y < HEIGHT - 1;
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

    public void update(float delta) {
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
        if (ObjToDelete.size()>0) {
            for (IUpdatable obj : ObjToDelete) {
                this.removeObject((AObject) obj);
            }
            ObjToDelete.clear();
        }
    }

    public List<IUpdatable> getupdatables(){
        return updatables;
    }


    public void render() {
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
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
                spriteBatch.draw(floortext, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        for (IRenderable obj : renderables)
            obj.render(spriteBatch);

        spriteBatch.end();
        hud.render(spriteBatch);

        pipRenderer.capture(() -> {
            renderMini(); // este método só desenha o mapa, jogador e inimigos (sem HUD)
        });
        pipRenderer.drawPIP();


    }

    public void renderMini() {
        // Configura uma câmara que mostra o mapa inteiro
        OrthographicCamera miniCam = new OrthographicCamera();
        miniCam.setToOrtho(false, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        miniCam.update();

        spriteBatch.setProjectionMatrix(miniCam.combined);

        spriteBatch.begin();

        // 1. Desenha o background (se aplicável)
       /* if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        }*/

        // 2. Desenha o mapa do mundo
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                ETileType tile = this.getTile(x, y);
                if (tile != null ) {
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
                    spriteBatch.draw(floortext, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

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

    public boolean isOccupied(float tileX, float tileY, AObject requester) {
        for (IRenderable obj : renderables) {
            if (obj instanceof AObject) {
                AObject other = (AObject) obj;
                if (other != requester) {
                    int ox = (int)(other.getX() / TILE_SIZE);
                    int oy = (int)(other.getY() / TILE_SIZE);
                    if (ox == tileX && oy == tileY) return true;
                }
            }
        }
        return false;
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

            if (isWalkable(tx, ty) && !isOccupied(tx, ty, null)) {
                Enemy enemy = new Enemy(tx * TILE_SIZE, ty * TILE_SIZE, WIDTH, HEIGHT, this);
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

            if (isWalkable(tx, ty) && !isOccupied(tx, ty, null)) {
                ReforcoVida reforco = new ReforcoVida(tx * TILE_SIZE, ty * TILE_SIZE, WIDTH, HEIGHT, this);
                addObject(reforco);
                quantidade--;
            }
            tentativas++;
        }
    }
    public void removeObject(AObject object) {
        if (object instanceof IRenderable) {
            renderables.remove(object);
        }
        if (object instanceof IUpdatable) {
            updatables.remove(object);
        }
        object.dispose(); // limpa recursos

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
        enemies.clear();
        spawnEnemies(5);
        spawnReforcosVida(3);
        gameOver = false;
        restartTimer = 0f;
    }

}






