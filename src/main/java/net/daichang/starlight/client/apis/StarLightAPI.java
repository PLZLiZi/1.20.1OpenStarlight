package net.daichang.starlight.client.apis;

import com.sun.jna.Library;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface StarLightAPI extends Library {
    default void entityKill(Entity entity) {
        entity.kill();
        entity.remove(Entity.RemovalReason.DISCARDED);
        entity.setRemoved(Entity.RemovalReason.KILLED);
    }

    default void itemStat(Player player, InteractionHand hand){
        player.startUsingItem(hand);
    }
}
