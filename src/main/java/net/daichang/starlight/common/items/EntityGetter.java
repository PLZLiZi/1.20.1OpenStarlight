package net.daichang.starlight.common.items;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityGetter extends Item {
    public EntityGetter() {
        super(new Properties().stacksTo(1));
    }

    void sendPlayerMessage(String printIn, Player player){
        player.displayClientMessage(Component.literal(printIn), false);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> list, @NotNull TooltipFlag p_41424_) {
        list.add(Component.literal("右键进行普通死亡[及打开一个GUI]"));
        list.add(Component.literal("左键检测客户端实体数量"));
        super.appendHoverText(p_41421_, p_41422_, list, p_41424_);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        Level level = entity.level();
        if (level instanceof ClientLevel client && entity instanceof Player player){
            int entityCount = client.getEntityCount();
            sendPlayerMessage("当前客户端实体个数：" + entityCount, player);
        }
        return super.onEntitySwing(stack, entity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand p_41434_) {
        player.addTag("isDead");
        return super.use(level, player, p_41434_);
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return Component.literal("Entity Getter");
    }
}
