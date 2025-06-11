package io.github.jogo.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.jogo.Enums.EEnemyTypes;
import io.github.jogo.Interfaces.*;
import io.github.jogo.Utils.PathFinder;
import io.github.jogo.game.World;

import java.util.List;

public class Enemy extends AObject implements IRenderable, IUpdatable {


    private float timeSinceLastMove = 0f;
    private Texture texture;
    private final EEnemyTypes type;


    public Enemy(float x, float y, int width, int height, World world, EEnemyTypes type) {

        super(x, y, width, height,world);
        this.type = type;


        switch (this.type){
            case Boss:
                this.texture = new Texture("assets/v01/enemyboss.png");
                break;
            case Standard:
                this.texture = new Texture("assets/v01/vilan.png");
                break;

        }
        this.addPositionChangeListener();
    }

    public void addPositionChangeListener(){
        this.addPositionChangeListener(new PositionChangeListener() {
            @Override
            public void onPositionChanged(AObject source) {
                positionChanged(source);
            }

            @Override
            public void positionChanged(AObject obj) {
                if (obj.getObjectrect().overlaps(world.player.getObjectrect())) {
                    obj.onCollision(world.player);
                }
            }
        });
    }

    @Override
    public void update(float delta, World world) {
        timeSinceLastMove += delta;
        float moveCooldown = (1.0f/(world.speed/100));
        if (timeSinceLastMove < moveCooldown) return;

        timeSinceLastMove = 0f;
        if(this.type == EEnemyTypes.Boss){
            Vector2 start = new Vector2(world.getTileX(this.getX()), world.getTileY(this.getY()));
            Vector2 end = new Vector2(world.getTileX(world.player.getX()), world.getTileY(world.player.getY()));

            List<Vector2> path = PathFinder.findPath(world, start, end);

            if (path != null && !path.isEmpty()) {
                Vector2 next ;
                if(path.size() == 1) {
                    next = path.get(0); //chegamos ao player
                }else{
                    next = path.get(1); // ignora o ponto 0 (posição atual)
                }
                float targetX = next.x * World.TILE_SIZE;
                float targetY = next.y * World.TILE_SIZE;

                this.setPosition(targetX, targetY);

            }

            if (this.getObjectrect().overlaps(world.player.getObjectrect()))
                world.player.onCollision(this);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, this.getX(), this.getY(), width, height);
    }




}


