package net.daichang.starlightbyte.mixins;

import net.daichang.starlight.server.util.DeathList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ResourceLocation.class, priority = 0x7fffffff)
public abstract class ResourceLocationMixin {
    @Unique
    public String starlight$var = "";

    @Inject(method = "<init>(Ljava/lang/String;)V", at = @At("RETURN"))
    private void init(String p_135809_, CallbackInfo ci) {
        this.starlight$var = p_135809_;
    }

    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"))
    private void init(String p_135811_, String p_135812_, CallbackInfo ci) {
        this.starlight$var = p_135811_ + ":" + p_135812_;
    }

    @Inject(method = "decompose", at = @At("RETURN"), cancellable = true)
    private static void decompose(String p_135833_, char p_135834_, CallbackInfoReturnable<String[]> cir) {
        if (DeathList.isDead(p_135833_)) cir.setReturnValue(new String[]{cir.getReturnValue()[0], "null"});
    }
}
