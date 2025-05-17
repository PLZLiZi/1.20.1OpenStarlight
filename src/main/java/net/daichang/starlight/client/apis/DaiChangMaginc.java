package net.daichang.starlight.client.apis;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface DaiChangMaginc extends Library {
    DaiChangMaginc INSTANCE = Native.load("/net/daichang/api/daichangmagic-x64.dll", DaiChangMaginc.class);
    
    long initWindow();

    String getWindowName();

    void drawImg(String imgPath, int width, int height);

    void setWindowTitle(String title);

    void setWindowTitleColor(int R, int G, int B);

    void setWindowBorderColor(int R, int G, int B);

    void blueScreen(boolean sure);
}