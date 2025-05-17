package net.daichang.starlightbyte.mixins;

import net.daichang.starlight.common.register.SoundRegistry;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author : IMG
 * @create : 2024/10/25
 */
@Mixin(Musics.class)
public abstract class MusicsMixin {

    @Mutable
    @Shadow @Final public static Music MENU;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void init(CallbackInfo ci) {
        MENU = new Music(SoundRegistry.YUZU_TITLE_MUSIC.getHolder().get(), 50, 50, true);
    }
}
