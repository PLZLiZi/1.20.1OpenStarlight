package net.daichang.starlightbyte.mixins;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;

import net.daichang.starlight.client.MinecraftClientMixinInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements MinecraftClientMixinInterface {

    @Shadow
    @Nullable
    public Screen screen;
    @Unique
    private boolean starlight$currentIsInGame = true;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void setScreen(Screen screen, CallbackInfo ci) {
        Screen currentScreen = this.screen;
        // 如果当前屏幕为空并且不是以下几种情况, 则认为之前在游戏中, 需要重置动画状态
        if (screen == null &&
                !(
                        currentScreen instanceof CreateWorldScreen ||
                                currentScreen instanceof JoinMultiplayerScreen ||
                                currentScreen instanceof SafetyScreen ||
                                currentScreen instanceof RealmsMainScreen ||
                                currentScreen instanceof OptionsScreen ||
                                currentScreen instanceof RealmsGenericErrorScreen
                )
        ) {
            if (!starlight$currentIsInGame) {
                this.starlight$currentIsInGame = true;
            }
        }
    }

    @Override
    public boolean getCurrentIsInGame() {
        return starlight$currentIsInGame;
    }

    @Override
    public void setCurrentIsInGame(boolean isInGame) {
        this.starlight$currentIsInGame = isInGame;
    }
}
