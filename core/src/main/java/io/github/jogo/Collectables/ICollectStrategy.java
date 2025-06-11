package io.github.jogo.Collectables;

import io.github.jogo.model.Player;
import io.github.jogo.game.World;

public interface ICollectStrategy {
    boolean onCollect(Player player, World world);
}
