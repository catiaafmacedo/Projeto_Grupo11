
package io.github.jogo.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import io.github.jogo.Enums.EGameWorldState;
import io.github.jogo.Interfaces.*;
import io.github.jogo.controller.Commands.MoveCommand;
import io.github.jogo.controller.InputHandler;
import io.github.jogo.game.World;

public class Player extends AObject implements IUpdatable, IRenderable, Disposable {
    public final int MaxHealth = 100;
    private int health = MaxHealth;
    private final Texture texture;
    private final World world;
    public boolean damagable = true;
    private boolean hasKey = false;

    public Player(float x, float y,int width,int height, World world) {
        super(x, y, width, height,world);
        this.texture = new Texture("assets/v01/superhero.png"); // coloca esta imagem no assets
        this.world = world;
        this.addPositionChangeListener();
        ICommand moveUp    = new MoveCommand(world,this,  0,  1, world.speed);
        ICommand moveDown  = new MoveCommand(world,this,  0, -1, world.speed);
        ICommand moveLeft  = new MoveCommand(world,this, -1,  0, world.speed);
        ICommand moveRight = new MoveCommand(world,this,  1,  0, world.speed);
        world.addInputHandlers(new InputHandler( moveLeft, moveRight,moveUp, moveDown));
    }


    @Override
    public void update(float delta, World world) {
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.enableBlending();
        batch.setColor(Color.WHITE);
        batch.draw(texture, this.getX(), this.getY(), width, height);
        this.update = false;
    }

    @Override
    public void onCollision(AObject other) {
        if (damagable) {
            if (other instanceof Enemy) {
                world.sound.playDamage();
                takeDamage(1);
            }
            if (other instanceof Collectable) {
                Collectable c = (Collectable)other;

                world.sound.playItem();
                if(c.strategy.onCollect(this, world)) {
                    world.addDeleteObj(other);
                }
            }
        }
    }

    public boolean hasKey(){
        return hasKey;
    }

    public void setKey(boolean hasKey){
        this.hasKey = hasKey;
        if (hasKey)
            world.showMessage("NÍVEL DESBLOQUEADO");
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int amount) {
        health -= amount;
        System.out.println("⚠ Jogador perdeu " + amount + " de vida! Vida atual: " + health);
        if (health <= 0) {
            world.setstate(EGameWorldState.GAME_OVER);
            System.out.println("💀 Game Over!");
        }
    }


    public void takeItem(int amount) {
        health += amount;
        System.out.println("⚠ Jogador ganhou " + amount + " de vida! Vida atual: " + health);
    }

    public void addPositionChangeListener(){
        this.addPositionChangeListener(new PositionChangeListener() {
            @Override
            public void onPositionChanged(AObject source) {
                positionChanged(source);
            }

            @Override
            public void positionChanged(AObject player) {
                for (IUpdatable obj : world.getupdatables()) {
                    if (obj instanceof Enemy)
                        if (((Enemy) obj).getObjectrect().overlaps(player.getObjectrect()))
                            player.onCollision((Enemy) obj);
                    if (obj instanceof Collectable)
                        if (((Collectable) obj).getObjectrect().overlaps(player.getObjectrect())) {
                            player.onCollision((Collectable) obj);
                        }

                }


            }
        });
    }



}
