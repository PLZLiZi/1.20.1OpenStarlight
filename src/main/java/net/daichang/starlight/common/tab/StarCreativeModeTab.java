package net.daichang.starlight.common.tab;

import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.server.mc.Items.StarItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class StarCreativeModeTab extends CreativeModeTab {

    public StarCreativeModeTab() {
        super(StarCreativeModeTab.builder()
                .noScrollBar()
                .title(Component.literal("Elyterminal Sword"))
                .icon(()-> StarItemStack.INSTANCE)
        );
    }

    //new ResourceLocation(StarlightMod.MOD_ID, "textures/screen/tab_items.png");
    @Override
    public @NotNull ResourceLocation getBackgroundLocation() {
        return switch (StarlightMod.toolTip) {
            case 1, 2 -> new ResourceLocation("minecraft/textures/gui/accessibility.png");
            case 3, 4 -> new ResourceLocation("minecraft/textures/gui/spectator_widgets.png");
            default -> new ResourceLocation(StarlightMod.MOD_ID, "textures/screen/tab_items.png");
        };
    }

    @Override
    public @NotNull ItemStack getIconItem() {
        return StarItemStack.INSTANCE;
    }
}
