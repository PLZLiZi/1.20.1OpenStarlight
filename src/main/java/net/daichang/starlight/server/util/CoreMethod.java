package net.daichang.starlight.server.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("unused")
public class CoreMethod {
    public static float getHealth(LivingEntity living){
        if (GodPlayerList.isGod(living) && living instanceof Player){
            return 20.0F;
        }
        if (Utils.isDeath || DeathList.isDead(living)){
            return 0.0F;
        }
        return living.getHealth();
    }

    public static boolean isRemoved(Entity entity) {
        if (DeathList.isDead(entity)){
            return true;
        }
        return entity.isRemoved();
    }

    public static Entity.RemovalReason getRemovalReason(Entity entity) {
        if (DeathList.isDead(entity)){
            return Entity.RemovalReason.KILLED;
        }
        if (GodPlayerList.isGod(entity)){
            return null;
        }
        return entity.removalReason;
    }

    public static boolean isDeadOrDying(LivingEntity entity) {
        if (GodPlayerList.isGod(entity)){
            return false;
        } else if (DeathList.isDead(entity) || Utils.isDeath) {
            return true;
        } else {
            return entity.isDeadOrDying();
        }
    }

    public static boolean isAlive(Entity entity) {
        if (GodPlayerList.isGod(entity)){
            return true;
        } else if (DeathList.isDead(entity) || Utils.isDeath) {
            return false;
        } else {
            return entity.isAlive();
        }
    }
}
