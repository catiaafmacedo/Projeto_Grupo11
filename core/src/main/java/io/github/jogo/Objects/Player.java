
package io.github.jogo.Objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dongbat.jbump.Item;
import io.github.jogo.Enums.ETileType;
import io.github.jogo.Interfaces.*;
import io.github.jogo.Scenes.*;
import io.github.jogo.Screens.World;

public class Player extends AObject implements IUpdatable, IRenderable {
    public final int MaxHealth = 100;
    private int health = MaxHealth;
    private Texture texture;
    private World world;
    public boolean damagable = true;
    private float moveCooldown = 0.5f; // tempo entre passos
    private float elapsed = 0f;

    public Player(float x, float y,int width,int height, World world) {
        super(x, y, width, height,world);
        this.texture = new Texture("assets/v01/superhero.png"); // coloca esta imagem no assets
        this.world = world;
    }

    @Override
    public void update(float delta, World world) {
        float speed = 100;
        float nextX = this.getX();
        float nextY = this.getY();

        if (InputManager.isLeft())  nextX -= speed * delta;
        if (InputManager.isRight()) nextX += speed * delta;
        if (InputManager.isUp())    nextY += speed * delta;
        if (InputManager.isDown())  nextY -= speed * delta;

        if (!isWall(nextX, this.getY())) this.setX( nextX);
        if (!isWall(this.getX(), nextY)) this.setY( nextY);

        elapsed += delta;
        if (elapsed < moveCooldown) return;

        if (world != null) {
            if (this.getObjectrect().overlaps(world.player.getObjectrect()))
                world.player.onCollision(this);
        }
    }

    private boolean isWall(float px, float py) {
        int left   = (int)(px / World.TILE_SIZE);
        int right  = (int)((px + this.width - 1) / World.TILE_SIZE);
        int top    = (int)((py + this.height - 1) / World.TILE_SIZE);
        int bottom = (int)(py / World.TILE_SIZE);

        return world.getTile(left, top) == ETileType.WALL ||
               world.getTile(right, top) == ETileType.WALL ||
               world.getTile(left, bottom) == ETileType.WALL ||
               world.getTile(right, bottom) == ETileType.WALL;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.enableBlending();
        batch.setColor(Color.WHITE); // muda tudo
        batch.draw(texture, this.getX(), this.getY(), width, height);
        this.update = false;
    }

    @Override
    public void onCollision(AObject other) {
        if (damagable) {
            if ((other instanceof Enemy)||(other instanceof EnemyFinalBoss)) {
                world.playdamage();
                takeDamage(1);
            }
            if (other instanceof ReforcoVida) {
                world.playItem();
                takeItem(5);
                world.addDeleteObj(other);
            }
        }
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int amount) {
        health -= amount;
        System.out.println("⚠ Jogador perdeu " + amount + " de vida! Vida atual: " + health);
        if (health <= 0) {
            System.out.println("💀 Game Over!");
        }
    }


    public void takeItem(int amount) {
        health += amount;
        System.out.println("⚠ Jogador perdeu " + amount + " de vida! Vida atual: " + health);
        if (health <= 0) {
            System.out.println("💀 Game Over!");
        }

    }

    @Override
    public void notifyPositionChanged() {
        if (world != null) {
            for (IUpdatable obj : world.getupdatables()) {
                if (obj instanceof Enemy)
                    if (((Enemy) obj).getObjectrect().overlaps(this.getObjectrect()))
                        this.onCollision((Enemy) obj);
                if (obj instanceof ReforcoVida)
                    if (((ReforcoVida) obj).getObjectrect().overlaps(this.getObjectrect())) {
                        this.onCollision((ReforcoVida) obj);
                    }
            }
        }

    }

}
