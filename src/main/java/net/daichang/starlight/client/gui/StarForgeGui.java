package net.daichang.starlight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.daichang.starlight.client.gui.fonts.FuckFont;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class StarForgeGui extends ForgeGui {
    public StarForgeGui(Minecraft mc) {
        super(mc);
    }

    public void renderHealth(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("health");
        RenderSystem.enableBlend();
        Player player = (Player)this.minecraft.getCameraEntity();
        int health = 20;
        boolean highlight = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
        if (player != null && health < this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = this.tickCount + 20;
        }

        if (Util.getMillis() - this.lastHealthTime > 1000L) {
            this.lastHealth = health;
            this.displayHealth = health;
            this.lastHealthTime = Util.getMillis();
        }

        this.lastHealth = health;
        int healthLast = this.displayHealth;
        float healthMax = 20;
        int a = 1;
        while (a<20) {
            a++;
        }
        int absorb = a;
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
        if (player.hasEffect(MobEffects.REGENERATION)) {
            regen = this.tickCount % Mth.ceil(healthMax + 5.0F);
        }

        this.renderHearts(guiGraphics, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);
        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }

    @Override
    public void renderExperienceBar(GuiGraphics p_281906_, int p_282731_) {
        this.minecraft.getProfiler().push("expBar");
        int l = this.screenHeight - 32 + 3;
        p_281906_.blit(GUI_ICONS_LOCATION, p_282731_, l, 0, 64, 182, 5);
        this.minecraft.getProfiler().pop();
        this.minecraft.getProfiler().push("expLevel");
        String s = "Infinity";
        int i1 = (this.screenWidth - this.getFont().width(s)) / 2;
        int j1 = this.screenHeight - 31 - 4;
        p_281906_.drawString(FuckFont.getFont(), s, i1 + 1, j1, 0, false);
        p_281906_.drawString(FuckFont.getFont(), s, i1 - 1, j1, 0, false);
        p_281906_.drawString(FuckFont.getFont(), s, i1, j1 + 1, 0, false);
        p_281906_.drawString(FuckFont.getFont(), s, i1, j1 - 1, 0, false);
        p_281906_.drawString(FuckFont.getFont(), s, i1, j1, 8453920, false);
        this.minecraft.getProfiler().pop();
    }

    @Override
    protected void renderBossHealth(GuiGraphics guiGraphics) {}
}
