package net.daichang.starlight.server.mc;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class DeathGui extends ForgeGui {
    public DeathGui(Minecraft mc) {
        super(mc);
    }

    @Override
    public void renderHealth(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("health");
        RenderSystem.enableBlend();
        Player player = (Player)this.minecraft.getCameraEntity();
        int health = 0;
        boolean highlight = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
        if (player != null && health < this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long) (this.tickCount + 20);
        }

        if (Util.getMillis() - this.lastHealthTime > 1000L) {
            this.lastHealth = health;
            this.displayHealth = health;
            this.lastHealthTime = Util.getMillis();
        }

        this.lastHealth = health;
        int healthLast = this.displayHealth;
        AttributeInstance attrMaxHealth = null;
        if (player != null) {
            attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        }
        float healthMax = 0;
        if (attrMaxHealth != null) {
            healthMax = Math.max((float)attrMaxHealth.getValue(), (float)Math.max(healthLast, health));
        }
        int absorb = 0;
        if (player != null) {
            absorb = Mth.ceil(player.getAbsorptionAmount());
        }
        int healthRows = Mth.ceil((healthMax + (float)absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        this.random.setSeed(this.tickCount * 312871L);
        int left = width / 2 - 91;
        int top = height - this.leftHeight;
        this.leftHeight += healthRows * rowHeight;
        if (rowHeight != 10) {
            this.leftHeight += 10 - rowHeight;
        }

        int regen = -1;
        regen = this.tickCount % Mth.ceil(healthMax + 1.0F);

        this.renderHearts(guiGraphics, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);
        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }
}
