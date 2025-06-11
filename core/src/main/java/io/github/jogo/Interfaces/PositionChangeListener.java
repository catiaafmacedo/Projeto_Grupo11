package io.github.jogo.Interfaces;

import io.github.jogo.model.AObject;

public interface PositionChangeListener {
    void onPositionChanged(AObject source);
    void positionChanged(AObject source);
}

