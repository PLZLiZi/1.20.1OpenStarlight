package net.daichang.starlight.server.mc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.apis.DCInter;
import net.daichang.starlight.client.apis.DaiChangMaginc;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.daichang.starlight.common.register.ItemRegister;
import net.daichang.starlight.server.mc.Items.FuckItem;
import net.daichang.starlight.server.mc.Items.FuckItemStack;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.Utils;
import net.daichang.starlight.server.util.daichangs.DCMethodTest;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.daichang.starlight.server.util.render.RenderUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.BusBuilderImpl;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import java.awt.*;
import java.util.Random;

@DCMethodTest(name = "DC Event Bus", clazz = FuckEventBus.class)
public class FuckEventBus extends EventBus implements DCInter {
    private static final BusBuilderImpl busBuilder = new BusBuilderImpl();

    public FuckEventBus() {
        super(busBuilder);
    }

    private static long milliTime() {
        return System.nanoTime() / 1000L;
    }

    public boolean post(Event event, IEventBusInvokeDispatcher wrapper) {
        Random random = new Random(milliTime());
        int colors = random.nextInt();
        if (event instanceof EntityJoinLevelEvent entityJoinLevelEvent) {
            if (DeathList.isDead(entityJoinLevelEvent.getEntity()))
                entityJoinLevelEvent.setCanceled(true);
        }
        if (event instanceof RenderTooltipEvent.Color renderTooltipEvent) {
            Item item = renderTooltipEvent.getItemStack().getItem();
            float index = 0.5F;
            float hueOffset = (float) Util.getMillis() / 16000.0F;
            float hue = hueOffset + index * index;
            float saturation = 1.0F;
            float brightness = 1.0F;
            int c = Color.HSBtoRGB((((hue * 720.0F + index) % 720.0F >= 360.0F) ? (720.0F - (hue * 720.0F + index) % 720.0F) : ((hue * 720.0F + index) % 720.0F)) / 256.0F, saturation, brightness);
            if (item.equals(ItemRegister.STARLIGHT_ITEM.get())) {
                renderTooltipEvent.setBackgroundStart(0);
                renderTooltipEvent.setBackgroundEnd(0);
                renderTooltipEvent.setBorderStart(c);
                renderTooltipEvent.setBorderEnd(c);
            }
            if (item.equals(ItemRegister.DEATH.get()) || item.equals(ItemRegister.TARGET_MODE_SWITCH.get())) {
                renderTooltipEvent.setBackgroundStart(c);
                renderTooltipEvent.setBackgroundEnd(c);
                renderTooltipEvent.setBorderStart(c);
                renderTooltipEvent.setBorderStart(c);
            }
            if (item.equals(ItemRegister.Item_LVING_ENTITY_DEBUG.get())) {
                renderTooltipEvent.setBackgroundStart(colors);
                renderTooltipEvent.setBackgroundEnd(colors);
                renderTooltipEvent.setBorderStart(colors);
                renderTooltipEvent.setBorderStart(colors);
            }
        }
        if (event instanceof RenderTooltipEvent.Pre event1) {
            ResourceLocation resourceLocation = new ResourceLocation(StarlightMod.MOD_ID, "textures/screen/background.png");
            int width = mc.getWindow().getWidth();
            int height = mc.getWindow().getHeight();
            GuiGraphics graphics = event1.getGraphics();
            Item item = event1.getItemStack().getItem();
            if (item.equals(ItemRegister.STARLIGHT_ITEM.get())) {
                graphics.blit(resourceLocation, 0, 0, 0.0F, 0.0F, width / 2, height / 2, width / 2, height / 2);
                event1.setFont(FuckFont.getFont());
                event1.setX(width / 2 - 40);
                event1.setY(50);
            }
        }
        if (event instanceof RegisterCommandsEvent event1) {
            CommandBuildContext context = event1.getBuildContext();
            event1.getDispatcher().register(Commands.literal("starlight").then(Commands.literal("window").then(Commands.literal("opacity").then(Commands.argument("float", FloatArgumentType.floatArg(0.0F, 1.0F)).executes(cs -> {
                StarlightMod.win = FloatArgumentType.getFloat(cs, "float");
                StarlightMod.Debug("Window Opacity Was set to " + StarlightMod.win);
                localPlayer.displayClientMessage(Component.literal("Window Opacity Was set to " + StarlightMod.win), false);
                return 0;
            }))).then(Commands.literal("opacity").executes(cs -> {
                DaiChangMaginc.INSTANCE.blueScreen(true);
                return 0;
            }))).then(Commands.literal("player").then(Commands.literal("renderDeath").executes(cs -> {
                HelperLib.setClass(mc.gameRenderer, FuckGameRender.class);
                return 0;
            })).then(Commands.literal("deathItemSuperMode").executes(cs -> {
                Utils.isSuperKill = true;
                StarlightMod.INFO("死亡物品模式已开启，无法关闭");
                return 0;
            })).then(Commands.literal("killPlayer").executes(cs -> {
                Utils.isDeath = true;
                System.out.println("Player Was Killed by StarLight");
                return 0;
            }))).then(Commands.literal("entity").then(Commands.literal("entitySuperMode").executes(cs -> {
                LocalPlayer player = Minecraft.getInstance().player;
                player.displayClientMessage(Component.literal("永雏塔菲 超级模式-开"), false);
                Utils.entityMode = 0;
                return 0;
            })).then(StarCommands.register()).then(Commands.literal("dataHealthSet").then(Commands.argument("float", FloatArgumentType.floatArg()).executes(cs -> {
                Entity target = cs.getSource().getEntity();
                Utils.dataSet(target, FloatArgumentType.getFloat(cs, "float"));
                return 0;
            })).then(Commands.argument("target", EntityArgument.entities()).then(Commands.argument("float", FloatArgumentType.floatArg()).executes(cs -> {
                Entity target = EntityArgument.getEntity(cs, "target");
                float health = FloatArgumentType.getFloat(cs, "float");
                Utils.dataSet(target, health);
                return 0;
            })))).then(Commands.literal("normalKillEntity").then(Commands.argument("entity", EntityArgument.entities()).executes(cs -> {
                for (Entity entity : EntityArgument.getEntities(cs, "entity")) {
                    Utils.normalKillEntity(entity);
                }
                return 0;
            }))).then(Commands.literal("killAllEntity").executes(cs -> {
                Level level = cs.getSource().getLevel();
                Utils.killLevelEntity(level);
                return 0;
            })).then(Commands.literal("superKillEntity").then(Commands.argument("entity", EntityArgument.entities()).executes(cs -> {
                for (Entity entity : EntityArgument.getEntities(cs, "entity")) {
                    Utils.killEntity(entity);
                }
                return 0;
            })))).then(Commands.literal("killItemClass").then(Commands.argument("item", ItemArgument.item(context)).executes(cs -> {
                ItemInput input = ItemArgument.getItem(cs, "item");
                killItemStack(input);
                return 0;
            }))));
        }
        if (event instanceof RenderLevelStageEvent event1) {
            // PoseStack poseStack = event1.getPoseStack();
            // MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            // Player player = Minecraft.getInstance().player;
            // RenderUtil.drawLine(poseStack, buffers, player, colors, colors, colors, 1.0F);
            // buffers.endBatch();
        }
        if (event.isCancelable()) {
            return false;
        }
        return (event.isCancelable() && event.isCanceled());
    }

    void killItemStack(ItemInput input) {
        Item item = input.getItem();
        System.out.println(item.getClass() + " 已被修改");
        HelperLib.setClass(item, FuckItem.class);
        ItemStack stack = item.getDefaultInstance();
        HelperLib.setClass(stack, FuckItemStack.class);
    }
}
