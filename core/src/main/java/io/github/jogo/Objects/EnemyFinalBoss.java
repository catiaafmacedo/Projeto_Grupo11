package io.github.jogo.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.jogo.Interfaces.IUpdatable;
import io.github.jogo.Interfaces.IRenderable;
import io.github.jogo.PathFinder;
import io.github.jogo.Screens.World;

import java.util.List;

public class EnemyFinalBoss extends AObject implements IUpdatable, IRenderable {

    private Texture texture;
    private World world;
    private float speed = 10f;
    private float moveCooldown = 1f; // tempo entre passos
    private float elapsed = 0f;
    private int minX=64,maxX=64,minY=64,maxY=64;


    public EnemyFinalBoss(float x, float y, int width, int height, World world) {
        super(x, y, width, height,world);
        this.world = world;
        //this.texture = new Texture("assets/v01/finalboss.png");
        this.texture = new Texture("assets/v01/enemyboss.png"); // Substitui conforme necessário
    }

    @Override
    public void update(float delta,World world) {
        elapsed += delta;
        if (elapsed < moveCooldown) return;
        elapsed = 0f;

        Vector2 start = new Vector2(world.getTileX(this.getX()), world.getTileY(this.getY()));
        Vector2 end = new Vector2(world.getTileX(world.player.getX()), world.getTileY(world.player.getY()));

        List<Vector2> path = PathFinder.findPath(world, start, end);

        if (path != null && path.size() > 1) {
            Vector2 next = path.get(1); // ignora o ponto 0 (posição atual)
            float targetX = next.x * World.TILE_SIZE;
            float targetY = next.y * World.TILE_SIZE;
            // Movimento simples: teleporta passo a passo

                this.setPosition(targetX, targetY);

        }

        if (this.getObjectrect().overlaps(world.player.getObjectrect()))
            world.player.onCollision(this);

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, this.getX(), this.getY(), width, height);
    }

    @Override
    public void notifyPositionChanged() {
        if (world != null) {
                    if (this.getObjectrect().overlaps(world.player.getObjectrect()))
                        world.player.onCollision(this);
        }

    }
}
