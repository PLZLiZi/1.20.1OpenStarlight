package net.daichang.starlight.common.register;

import net.daichang.starlight.server.mc.Items.StarItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.daichang.starlight.StarlightMod.MOD_ID;

public class TabRegister {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> STARLIGHT_TAB = REGISTRY.register("snow_sword_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("Elyterminal Sword"))
            .icon(() -> StarItemStack.INSTANCE)
            .withBackgroundLocation(new ResourceLocation(MOD_ID, "textures/screen/tab_items.png"))
            .withTabsImage(new ResourceLocation(MOD_ID, "textures/screen/tabs.png"))
            .noScrollBar()
            .alignedRight()
            .displayItems((parameters, tabData) -> {
                tabData.accept(ItemRegister.STARLIGHT_ITEM.get());
                tabData.accept(ItemRegister.Item_LVING_ENTITY_DEBUG.get());
                tabData.accept(ItemRegister.DEATH.get());
                tabData.accept(ItemRegister.ENTITY_GETTER.get());
                tabData.accept(ItemRegister.GOD_SPAWN.get());
                tabData.accept(ItemRegister.TARGET_MODE_SWITCH.get());
            }).build());
}