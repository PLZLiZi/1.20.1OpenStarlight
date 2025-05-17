package net.daichang.starlight.client.window.java;

import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.apis.DaiChangMaginc;
import net.daichang.starlight.client.window.java.starbutton.StarJButton;
import net.daichang.starlight.server.mc.DeathGui;
import net.daichang.starlight.server.mc.FuckGameRender;
import net.daichang.starlight.server.mc.FuckMouseHandler;
import net.daichang.starlight.server.mc.players.FuckDeathPlayer;
import net.daichang.starlight.server.mc.players.FuckLoalPlayer;
import net.daichang.starlight.server.util.Utils;
import net.daichang.starlight.server.util.fonts.StarJFont;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static net.daichang.starlight.StarlightMod.*;

public class HelperWindow extends JFrame {
    public HelperWindow() {
        Minecraft mc = Minecraft.getInstance();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setSize(400, 400);
        setTitle("[逐梦星光]帮助页面");

        getContentPane().setBackground(Color.LIGHT_GRAY);

        StarJButton renderTest = new StarJButton("Render Dead", "点击开启渲染测试");
        StarJButton blueScreen = new StarJButton("蓝屏打击");
        StarJButton killPlayer = new StarJButton("自杀");
        StarJButton closeMinecraft = new StarJButton("关闭游戏", "关闭游戏，可能有bug");
        StarJButton defPlayer = new StarJButton("玩家防护", "获取逐梦星光");
        StarJButton renderDeath = new StarJButton("渲染死亡界面", "测试[超级渲染]");
        killPlayer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HelperLib.setClass(mc.player, FuckDeathPlayer.class);
                HelperLib.setClass(mc.gui, DeathGui.class);
                HelperLib.setClass(mc.gameRenderer, FuckGameRender.class);
                HelperLib.setClass(mc.mouseHandler, FuckMouseHandler.class);
                DaiChangMaginc.INSTANCE.drawImg(gameDir + "\\death_screen.png", mc.getWindow().getWidth(), mc.getWindow().getHeight());
                Utils.unSafePlayer(mc.player);
                Utils.isDeath = true;
            }
        });
        renderTest.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HelperLib.setClass(mc.gameRenderer, FuckGameRender.class);
                StarlightMod.Debug("Render Dead Test");
            }
        });
        defPlayer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HelperLib.setClass(mc.player, FuckLoalPlayer.class);
                HelperLib.setClass(mc.gameRenderer, GameRenderer.class);
            }
        });
        closeMinecraft.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Process process = Runtime.getRuntime().exec("taskkill /F /PID " + ProcessHandle.current().pid());
                    INFO("Killed game");
                    INFO("game exit code " + process.exitValue());
                    INFO("game pid " + process.pid());
                    process.waitFor();
                    mc.close();
                } catch (Exception ignored) {}
            }
        });
        blueScreen.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DaiChangMaginc.INSTANCE.blueScreen(true);
                StarlightMod.Debug("蓝屏打击");
            }
        });
        add(renderTest);
        add(blueScreen);
        add(killPlayer);
        add(defPlayer);
        add(closeMinecraft);
    }

    @Override
    public void setTitle(String title) {
        super.setTitle("[逐梦星光]帮助页面");
    }

    @Override
    public Font getFont() {
        return StarJFont.getFont();
    }

    @Override
    public String getTitle() {
        return "[逐梦星光]帮助页面";
    }
}
