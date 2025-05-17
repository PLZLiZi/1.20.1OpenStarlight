package net.daichang.starlight.server.entity.light;

import net.daichang.starlight.common.register.EntityRegistry;
import net.daichang.starlight.server.util.DeathList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

public class RainbowLightingEntity extends LightningBolt {
    private int life;
    public long seed;
    private int flashes;
    public RainbowLightingEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(EntityRegistry.RAINBOW_LIGHTING.get(), world);
        DeathList.removeDead(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
    }

    public void tick() {
        DeathList.removeDead(this);
        if (this.life == 5) {
            if (this.level().isClientSide()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            }
        }
        --this.life;
        if (this.life < 0) {
            if (this.flashes <= 0) {
                this.discard();
                this.remove(RemovalReason.KILLED);
                this.onRemovedFromWorld();
            } else {
                --this.flashes;
                this.seed = this.random.nextLong();
            }
        }
    }

    public @NotNull SoundSource getSoundSource() {
        return SoundSource.WEATHER;
    }

    public RainbowLightingEntity(EntityType<RainbowLightingEntity> type, Level world) {
        super(type, world);
        this.noCulling = true;
        this.life = 5;
        this.flashes = 30;
        this.seed = this.random.nextLong();
    }
}