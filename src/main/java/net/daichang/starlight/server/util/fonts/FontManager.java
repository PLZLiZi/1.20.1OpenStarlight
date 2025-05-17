package net.daichang.starlight.server.util.fonts;

import net.daichang.starlight.StarlightMod;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

public class FontManager {
    private static final String path = "C:\\StarLight\\";

    public static FontRenderer C12;
    public static FontRenderer C14;
    public static FontRenderer C16;
    public static FontRenderer C18;
    public static FontRenderer C20;
    public static FontRenderer C30;
    public static FontRenderer Logo;
    public static FontRenderer TC12;
    public static FontRenderer TC14;
    public static FontRenderer TC16;
    public static FontRenderer TC18;
    public static FontRenderer TC20;
    public static FontRenderer TC30;
    public static FontRenderer Bold16;
    public static FontRenderer Genshin14;
    public static FontRenderer Genshin16;
    public static FontRenderer Genshin18;
    public static FontRenderer Genshin20;
    public static FontRenderer Genshin40;
    public static FontRenderer SJFont;

    public static void init() {
        Genshin40 = createFontRenderer(getFont(40, new File(path + "haha.ttf")), 40);
        Genshin20 = createFontRenderer(getFont(20, new File(path + "haha.ttf")), 20);
        Genshin18 = createFontRenderer(getFont(18, new File( path + "haha.ttf")), 18);
        Genshin16 = createFontRenderer(getFont(16, new File(path + "haha.ttf")), 16);
        Genshin14 = createFontRenderer(getFont(14, new File(path + "haha.ttf")), 14);
        Bold16 = createFontRenderer(getFont(16, new File(path + "bold.ttf")), 16);
        C12 = createFontRenderer(getFont(14, new File(path + "weiruan.ttf")), 12);
        C14 = createFontRenderer(getFont(14, new File(path + "weiruan.ttf")), 14);
        C16 = createFontRenderer(getFont(16, new File(path + "weiruan.ttf")), 16);
        C18  = createFontRenderer(getFont(18, new File(path + "weiruan.ttf")), 18);
        C20 = createFontRenderer(getFont(20, new File(path + "weiruan.ttf")),20);
        C30 = createFontRenderer(getFont(30, new File(path + "weiruan.ttf")),30);
        TC12 = createFontRenderer(getFont(14, new File(path + "tenacitybold.ttf")), 12);
        TC14 = createFontRenderer(getFont(14, new File(path + "tenacitybold.ttf")), 14);
        TC16 = createFontRenderer(getFont(16, new File(path + "tenacitybold.ttf")), 16);
        TC18  = createFontRenderer(getFont(18, new File(path + "tenacitybold.ttf")), 18);
        TC20 = createFontRenderer(getFont(20, new File(path + "tenacitybold.ttf")),20);
        TC30 = createFontRenderer(getFont(30, new File(path + "tenacitybold.ttf")),30);
        Logo = createFontRenderer(getFont(40,new File(path + "a.ttf")),40);

        SJFont = createFontRenderer(StarJFont.getFont(), 30);

        StarlightMod.INFO("成功加载字体文件");
    }

    public static Font getFont(int size, File file) {
        Font font;
        try (FileInputStream fis = new FileInputStream(file)) {
            font = Font.createFont(Font.TRUETYPE_FONT, fis).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("eee");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    public static FontRenderer createFontRenderer(Font font, int size) {
        return new FontRenderer(font, size / 2f);
    }
}
