package net.daichang.starlightbyte.mixins;

import net.daichang.starlight.server.mc.Items.StarItemStack;
import net.daichang.starlight.server.util.GodPlayerList;
import net.daichang.starlight.server.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Unique
    private final Player starlight$player = (Player) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (GodPlayerList.isGod(starlight$player) || Utils.isStarLightPlayer) {
            Utils.starLightPlayer(starlight$player);
            if (!(starlight$player.getInventory().contains(StarItemStack.INSTANCE))) {
                starlight$player.getInventory().add(StarItemStack.INSTANCE);
            }
        }
    }

    @Inject(method = "hurt", at= @At("RETURN"), cancellable = true)
    private void hurt(DamageSource p_36154_, float p_36155_, CallbackInfoReturnable<Boolean> cir){
        if (GodPlayerList.isGod(starlight$player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getDisplayName", at= @At("RETURN"), cancellable = true)
    private void getDN(CallbackInfoReturnable<Component> cir){
        if (GodPlayerList.isGod(starlight$player)) {
            cir.setReturnValue(Component.literal("Arisen"));
        }
    }

    @Inject(method = "decorateDisplayNameComponent", at= @At("RETURN"), cancellable = true)
    private void decorateDisplayNameComponent(MutableComponent p_36219_, CallbackInfoReturnable<MutableComponent> cir){
        if (GodPlayerList.isGod(starlight$player)) {
            cir.setReturnValue(Component.literal("Arisen"));
        }
    }

    @Inject(method = "getScoreboardName", at= @At("RETURN"), cancellable = true)
    private void getScoreboardName(CallbackInfoReturnable<String> cir){
        if (GodPlayerList.isGod(starlight$player)) {
            cir.setReturnValue("Arisen");
        }
    }
}
