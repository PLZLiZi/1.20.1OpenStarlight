package net.daichang.starlight.server.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

public class FuckMouseHandler extends MouseHandler {
    public FuckMouseHandler(Minecraft p_91522_) {
        super(p_91522_);
    }

    @Override
    public boolean isLeftPressed() {
        return false;
    }

    @Override
    public boolean isMiddlePressed() {
        return false;
    }

    @Override
    public boolean isMouseGrabbed() {
        return false;
    }

    @Override
    public boolean isRightPressed() {
        return false;
    }
}
