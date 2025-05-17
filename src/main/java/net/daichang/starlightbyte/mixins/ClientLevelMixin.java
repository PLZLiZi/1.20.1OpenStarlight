package net.daichang.starlightbyte.mixins;

import net.daichang.starlight.server.util.DeathList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "addEntity", at= @At("HEAD"), cancellable = true)
    private void addEntity(int p_104740_, Entity p_104741_, CallbackInfo ci){
        if (DeathList.isDead(p_104741_)){
            ci.cancel();
        }
    }
}
