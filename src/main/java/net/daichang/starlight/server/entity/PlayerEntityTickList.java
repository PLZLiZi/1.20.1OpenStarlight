package net.daichang.starlight.server.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;

import java.util.function.Consumer;

public class PlayerEntityTickList extends EntityTickList {
    @Override
    public void add(Entity p_156909_) {
        super.add(PlayerEntity.entity);
    }

    @Override
    public void forEach(Consumer<Entity> p_156911_) {
        super.forEach(p_156911_);
    }

    @Override
    public void remove(Entity p_156913_) {}

    @Override
    public boolean contains(Entity p_156915_) {
        return super.contains(PlayerEntity.entity);
    }
}
