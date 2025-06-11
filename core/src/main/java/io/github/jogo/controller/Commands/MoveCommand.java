package io.github.jogo.controller.Commands;

import com.badlogic.gdx.Gdx;
import io.github.jogo.Interfaces.ICommand;
import io.github.jogo.game.World;
import io.github.jogo.model.AObject;

public class MoveCommand implements ICommand {
    private final AObject object;
    private final World world;
    private final float dx;
    private final float dy;
    private final float speed;

    public MoveCommand(World world,AObject obj, float dx, float dy, float speed) {
        this.object = obj;
        this.world = world;
        this.dx = dx;
        this.dy = dy;
        this.speed = speed;
    }

    @Override
    public void execute() {
        float delta = Gdx.graphics.getDeltaTime();
        float nextX = object.getX() + dx * speed * delta;
        float nextY = object.getY() + dy * speed * delta;

        // Exemplo de verificação de colisão nas duas direções:
        if (world.maze.isObjWalkable(nextX, object.getY(),this.object.getWidth(),  this.object.getHeight())) {
            object.setX(nextX);
        }
        if (world.maze.isObjWalkable(object.getX(), nextY,this.object.getWidth(),  this.object.getHeight())) {
            object.setY(nextY);
        }
    }

}

