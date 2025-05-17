package net.daichang.starlight.common.items;

import net.daichang.starlight.client.apis.DCInter;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.daichang.starlight.server.mc.Items.StarItemStack;
import net.daichang.starlight.server.util.render.ScreenHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static net.daichang.starlight.server.util.Utils.*;

public class AutoAttackItem extends Item implements DCInter {
    public AutoAttackItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand p_41434_) {
        player.startUsingItem(p_41434_);
        level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 1, false);
        if (level.isClientSide() && player.getInventory().contains(StarItemStack.INSTANCE)){
            if (player.isShiftKeyDown()){
                isAutoAttack = !isAutoAttack;
                player.displayClientMessage(Component.literal("Auto Attack: " + isAutoAttack), false);
            }else {
                isGod = !isGod;
                ScreenHelper.isGod = !ScreenHelper.isGod;
                player.displayClientMessage(Component.literal("Super Render Defense: " + isGod), false);
            }
        }
        return super.use(level, player, p_41434_);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> list, TooltipFlag p_41424_) {
        list.add(Component.literal("超级渲染防御: " + isGod));
        list.add(Component.literal("自动攻击： " + isAutoAttack));
        list.add(Component.literal("All Return： " +allReturn));
        super.appendHoverText(p_41421_, p_41422_, list, p_41424_);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull Font getFont(ItemStack stack, FontContext context) {
                return FuckFont.getFont();
            }
        });
        super.initializeClient(consumer);
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return Component.literal("[逐梦星光] 模式选择器");
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack p_41454_) {
        return 72000;
    }
}
