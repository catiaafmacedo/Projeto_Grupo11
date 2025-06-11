package io.github.jogo.Collectables;

import io.github.jogo.Enums.EGameWorldState;
import io.github.jogo.game.World;
import io.github.jogo.model.Player;

public class LevelGameCollectStrategy implements ICollectStrategy {
        @Override
        public boolean onCollect(Player player, World world) {
            //world.NewLevel();

            if (player.hasKey()) {
                world.setstate(EGameWorldState.NEXT_LEVEL);
                return false;
            }else {
                world.showMessage("Não tens chave  :(");
                return false;
            }
        }

}
