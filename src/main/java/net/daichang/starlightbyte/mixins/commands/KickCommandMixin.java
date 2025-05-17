package net.daichang.starlightbyte.mixins.commands;

import net.daichang.starlight.server.util.GodPlayerList;
import net.daichang.starlight.server.util.TextUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Collection;

@Mixin(KickCommand.class)
public class KickCommandMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static int kickPlayers(CommandSourceStack p_137802_, Collection<ServerPlayer> p_137803_, Component p_137804_) {
        for(ServerPlayer $$3 : p_137803_) {
            if (!GodPlayerList.isGod($$3)) {
                $$3.connection.disconnect(p_137804_);
                p_137802_.sendSuccess(() -> Component.translatable("commands.kick.success", new Object[]{$$3.getDisplayName(), p_137804_}), true);
            }else {
                $$3.displayClientMessage(Component.literal(TextUtil.GetColor("阻止了一次踢出")), false);
            }
        }
        return p_137803_.size();
    }
}