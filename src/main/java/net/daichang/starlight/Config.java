package net.daichang.starlight;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = StarlightMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public  static final ForgeConfigSpec SPEC = BUILDER.build();
    private static final ForgeConfigSpec.BooleanValue STARLIGHT_FONT = BUILDER.comment("设置Minecraft字体为逐梦星光的字体").define("全局彩字", false);

    public static boolean isSetFont;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        isSetFont = STARLIGHT_FONT.get();
    }
}
