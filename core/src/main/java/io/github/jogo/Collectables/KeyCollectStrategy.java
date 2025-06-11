package io.github.jogo.Collectables;

import io.github.jogo.model.Player;
import io.github.jogo.game.World;

public class KeyCollectStrategy implements ICollectStrategy {
    @Override
    public boolean onCollect(Player player, World world) {
        player.setKey(true);
        return true;
    }

}
