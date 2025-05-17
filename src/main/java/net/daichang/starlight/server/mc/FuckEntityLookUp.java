package net.daichang.starlight.server.mc;

import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FuckEntityLookUp<T extends EntityAccess> extends EntityLookup<T> {
    @Override
    public void add(T p_156815_) {
        super.add(p_156815_);
    }

    @Override
    public Iterable getAllEntities() {
        return super.getAllEntities();
    }

    @Override
    public @Nullable T getEntity(int p_156813_) {
        return super.getEntity(p_156813_);
    }

    @Override
    public @Nullable T getEntity(UUID p_156820_) {
        return super.getEntity(p_156820_);
    }

    @Override
    public void getEntities(EntityTypeTest p_261575_, AbortableIterationConsumer p_261925_) {
        super.getEntities(p_261575_, p_261925_);
    }

    @Override
    public void remove(T p_156823_) {
        super.remove(p_156823_);
    }
}
