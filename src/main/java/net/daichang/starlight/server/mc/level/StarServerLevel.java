package net.daichang.starlight.server.mc.level;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import com.google.common.collect.Iterables;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;

public class StarServerLevel extends ServerLevel {

    public StarServerLevel(MinecraftServer p_214999_, Executor p_215000_, LevelStorageAccess p_215001_,
            ServerLevelData p_215002_, ResourceKey<Level> p_215003_, LevelStem p_215004_,
            ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List<CustomSpawner> p_215008_,
            boolean p_215009_, RandomSequences p_288977_) {
        super(p_214999_, p_215000_, p_215001_, p_215002_, p_215003_, p_215004_, p_215005_, p_215006_, p_215007_, p_215008_,
                p_215009_, p_288977_);
    }

    public Iterable<Entity> getAllEntities() {
        List<Entity> now = new ArrayList<>();
        Iterable<Entity> old = super.getAllEntities();
        old.forEach(entity -> {
            if (entity instanceof Player) {
                now.add(entity);
            }
        });
        return Iterables.unmodifiableIterable(now);
    }
}