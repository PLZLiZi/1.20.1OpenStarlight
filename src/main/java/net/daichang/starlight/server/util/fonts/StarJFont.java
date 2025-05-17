package net.daichang.starlight.server.util.fonts;

import net.daichang.starlight.StarlightMod;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class StarJFont extends Font {
    public static final String FONT_NAME = "Starlight JFont";

    protected StarJFont(String name, int style, int size) throws FontFormatException, IOException {
        super(name, style, size);
        Font customFont = Font.createFont(Font.TYPE1_FONT, Objects.requireNonNull(StarlightMod.class.getResourceAsStream("/starlight/tenacitybold.pfa")));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(customFont.deriveFont(style, size));
    }

    public static Font getFont() {
        return StarJFont.getFont(FONT_NAME);
    }
}
