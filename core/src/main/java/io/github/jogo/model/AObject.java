package io.github.jogo.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import io.github.jogo.Interfaces.PositionChangeListener;
import io.github.jogo.Interfaces.*;
import io.github.jogo.game.World;

import java.util.ArrayList;
import java.util.List;


public abstract class AObject implements IRenderable, IUpdatable, Disposable {
    private float x, y;
    public boolean update;
    protected int width, height;
    private final List<PositionChangeListener> listeners = new ArrayList<>();
    public final World world;

    //private final Vector2 spawnPoint;

    public AObject(float x, float y, int width, int height, World world) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        update = true;
        this.world = world;
        //this.spawnPoint = new Vector2(x, y);
    }

    public void onCollision(AObject other) {
        // Implementar em subclasses, se necessário
    }
    public Rectangle getObjectrect() {
        return new Rectangle(x, y, width, height);
    }
    // Getters e setters se necessário
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }


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

    public void notifyPositionChanged() {
        for (PositionChangeListener listener : listeners) {
            listener.onPositionChanged(this);
        }
    }

    @Override
    public void dispose() {

    }
}


