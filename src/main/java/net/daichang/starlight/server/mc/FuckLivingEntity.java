package net.daichang.starlight.server.mc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FuckLivingEntity extends LivingEntity {
    protected FuckLivingEntity(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    public void remove(RemovalReason p_276115_) {
        super.remove(p_276115_);
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
    }

    @Override
    protected void unsetRemoved() {
        super.unsetRemoved();
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return null;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    public float getHealth() {
        return super.getHealth();
    }

    @Override
    public void setHealth(float p_21154_) {
        super.setHealth(p_21154_);
    }

    @Override
    public void tick() {
        tickDeath();
    }
}
