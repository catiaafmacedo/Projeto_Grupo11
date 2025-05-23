package io.github.jogo.Objects;

import com.badlogic.gdx.math.Rectangle;
import io.github.jogo.Interfaces.PositionChangeListener;
import io.github.jogo.Interfaces.*;
import io.github.jogo.Screens.World;

import java.util.ArrayList;
import java.util.List;


public abstract class AObject implements IRenderable, IUpdatable {
    private float x, y;
    public boolean update;
    protected int width, height;
    private World world;
    private List<PositionChangeListener> listeners = new ArrayList<>();


    public AObject(float x, float y, int width, int height, World world) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.world = world;
        update = true;
    }

    public Rectangle getObjectrect() {
        return new Rectangle(x, y, width, height);
    }
    // Getters e setters se necessário
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void onCollision(AObject other) {
        // Implementar em subclasses, se necessário
    }

    public void setX(float x) {
        this.setPosition(x,this.y);
    }
    public void setY(float y) {
        this.setPosition(this.x,y);
    }
    public void setPosition(float x, float y) {
            this.x = x;
            this.y = y;
            notifyPositionChanged();

    }

    public void addPositionChangeListener(PositionChangeListener listener) {
        listeners.add(listener);
    }

    public void removePositionChangeListener(PositionChangeListener listener) {
        listeners.remove(listener);
    }

    public void notifyPositionChanged() {
        for (PositionChangeListener listener : listeners) {
            listener.onPositionChanged(this);
        }
    }

    public void dispose() {

    }
}


