package net.daichang.starlight.client.window.java;

import net.daichang.starlight.server.util.fonts.StarJFont;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class StarJFileChooser extends JFileChooser {
    public StarJFileChooser() {
        setDialogTitle("选择背景图片");
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        addChoosableFileFilter(filter);
        setVisible(true);
    }

    @Override
    public Font getFont() {
        return StarJFont.getFont();
    }

    @Override
    public Color getBackground() {
        return Color.GRAY;
    }
}
