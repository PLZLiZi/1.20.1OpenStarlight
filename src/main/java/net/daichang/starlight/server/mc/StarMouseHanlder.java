package net.daichang.starlight.server.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

public class StarMouseHanlder extends MouseHandler {
    public StarMouseHanlder(Minecraft p_91522_) {
        super(p_91522_);
    }

    @Override
    public boolean isMouseGrabbed() {
        return super.isMouseGrabbed();
    }

    @Override
    public boolean isMiddlePressed() {
        return super.isMiddlePressed();
    }

    @Override
    public boolean isRightPressed() {
        return super.isRightPressed();
    }

    @Override
    public boolean isLeftPressed() {
        return super.isLeftPressed();
    }
}
