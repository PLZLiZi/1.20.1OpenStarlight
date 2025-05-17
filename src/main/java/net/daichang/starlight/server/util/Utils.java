package net.daichang.starlight.server.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.netty.util.collection.IntObjectHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.client.gui.StarForgeGui;
import net.daichang.starlight.common.register.EntityRegistry;
import net.daichang.starlight.common.register.ItemRegister;
import net.daichang.starlight.server.entity.PlayerEntity;
import net.daichang.starlight.server.entity.light.RainbowLightingEntity;
import net.daichang.starlight.server.mc.DeathInventory;
import net.daichang.starlight.server.mc.FuckEntityLookUp;
import net.daichang.starlight.server.mc.StarMouseHanlder;
import net.daichang.starlight.server.mc.level.StarClinetLevel;
import net.daichang.starlight.server.mc.level.StarServerLevel;
import net.daichang.starlight.server.mc.players.FuckDeathPlayer;
import net.daichang.starlight.server.mc.players.FuckDeathServerPlayer;
import net.daichang.starlight.server.mc.players.FuckLoalPlayer;
import net.daichang.starlight.server.mc.players.FuckServerPlayer;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.daichang.starlightbyte.javassist.ClassPool;
import net.daichang.starlightbyte.javassist.CtClass;
import net.daichang.starlightbyte.javassist.CtMethod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.lang.Math.*;
import static net.daichang.starlight.StarlightMod.INFO;
import static net.minecraft.util.Mth.square;

public class Utils {
    public static boolean isDeath = false;

    public static boolean isGod = false;

    public static boolean allReturn = false;

    public static boolean isAutoAttack = false;

    public static boolean isStarLightPlayer = false;

    public static boolean isNormalKill = false;

    public static boolean isSuperKill = false;

    public static boolean isKillItem = false;

    public static int entityMode = 3;

    public static void starLightPlayer(Player player) {
        Minecraft mc = Minecraft.getInstance();
        HelperLib.setClass(mc.gameRenderer, GameRenderer.class);
        HelperLib.setClass(mc.mouseHandler, StarMouseHanlder.class);
        HelperLib.setClass(mc.gui, StarForgeGui.class);
        
        isStarLightPlayer = true;
        player.displayname = Component.literal("Arisen");
        Override_DATA_HEALTH_ID(player, 20.0F);
        Abilities attributes = player.getAbilities();
        attributes.mayfly = true;
        player.onUpdateAbilities();
        player.deathTime = -2;
        player.hurtTime = -2;
        GodPlayerList.addGod(player);
        DeathList.removeDead(player);
        Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(20.0D);
        player.setInvisible(false);
        HelperLib.setClass(player.getInventory(), Inventory.class);
        if (!(mc.player instanceof FuckLoalPlayer)){
            HelperLib.setClass(mc.player, FuckLoalPlayer.class);
        }
        if (player instanceof ServerPlayer serverPlayer){
            if (!(serverPlayer instanceof FuckServerPlayer)) {
                HelperLib.setClass(serverPlayer, FuckServerPlayer.class);
            }
        }
        if (player.getY() < -70){
            player.setDeltaMovement(0, 4, 0);
            player.displayClientMessage(Component.literal("虚空救援,小子"), true);
        }
    }

    public static void starLightPlayer(Player player, Level level) {
        starLightPlayer(player);
        if (level.isClientSide()) {
            ClientLevel clientLevel = (ClientLevel) level;
            clientLevel.setDayTime(clientLevel.dayTime()+20);
        }
    }

    public static boolean isBlocking(@NotNull Player target) {
        return target.getUseItem().getItem() == ItemRegister.STARLIGHT_ITEM.get().getDefaultInstance().getItem() && target.isUsingItem() && target.getUseItem().getItem().getUseAnimation(target.getUseItem()) == Utils.getUseAnim();
    }

    public static UseAnim getUseAnim() {
        return UseAnim.valueOf(StarlightMod.MOD_ID + ":BLOCK");
    }

    public static void copyProperties(Class<?> clazz, Object source, Object target) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers()))
                    field.set(target, field.get(source));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Override_DATA_HEALTH_ID(LivingEntity livingEntity, final float X) {
        SynchedEntityData data = new SynchedEntityData(livingEntity) {
            @NotNull
            public <T> T get(@NotNull EntityDataAccessor<T> p_135371_) {
                return (p_135371_ == LivingEntity.DATA_HEALTH_ID) ? (T)Float.valueOf(X) : (T)super.get(p_135371_);
            }
        };
        copyProperties(SynchedEntityData.class, livingEntity.entityData, data);
        livingEntity.entityData = data;
    }

    public static void Override_DATA_HEALTH_ID(Player player, final float X) {
        SynchedEntityData data = new SynchedEntityData(player) {
            @NotNull
            public <T> T get(@NotNull EntityDataAccessor<T> p_135371_) {
                return (p_135371_ == LivingEntity.DATA_HEALTH_ID) ? (T)Float.valueOf(X) : (T)super.get(p_135371_);
            }
        };
        copyProperties(SynchedEntityData.class, player.entityData, data);
        player.entityData = data;
    }

    public static void Override_DATA_HEALTH_ID(Entity entity, final float X) {
        SynchedEntityData data = new SynchedEntityData(entity) {
            @NotNull
            public <T> T get(@NotNull EntityDataAccessor<T> p_135371_) {
                return (p_135371_ == LivingEntity.DATA_HEALTH_ID) ? (T)Float.valueOf(X) : (T)super.get(p_135371_);
            }
        };
        copyProperties(SynchedEntityData.class, entity.entityData, data);
        entity.entityData = data;
    }

    public static void backtrack(Class<?> caller) {
        try {
            Field[] fields = caller.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType().getTypeName().equals("boolean")) {
                    field.setAccessible(true);
                    field.set(null, Boolean.valueOf(false));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void killEntity(Entity target){
        killEntityEx(target);
        DeathList.addDead(target);
    }

    public static void killEntityEx(Entity target){
        if(target != null && !(target instanceof Player) && !(target instanceof PlayerEntity)) {
            MinecraftForge.EVENT_BUS.unregister(target);
            Override_DATA_HEALTH_ID(target, 0.0F);
            backtrack(target.getClass());
            EntityInLevelCallback inLevelCallback = EntityInLevelCallback.NULL;
            target.levelCallback = inLevelCallback;
            target.getPassengers().forEach(Entity::stopRiding);
            Entity.RemovalReason reason = Entity.RemovalReason.KILLED;
            target.removalReason = reason;
            target.onClientRemoval();
            target.onRemovedFromWorld();
            target.remove(reason);
            target.setRemoved(reason);
            target.isAddedToWorld = false;
            target.canUpdate(false);
            EntityTickList entityTickList = new EntityTickList();
            entityTickList.remove(target);
            entityTickList.active.clear();
            entityTickList.passive.clear();
            if (target instanceof LivingEntity living) {
                living.getBrain().clearMemories();
                for(String s : living.getTags()) living.removeTag(s);
                living.invalidateCaps();
                Override_DATA_HEALTH_ID(living, 0.0F);
            }
            Level level = target.level();
            level.shouldTickDeath(target);
            if (level instanceof ServerLevel surface) {
                Set<UUID> newKnownUuids = Sets.newHashSet();
                newKnownUuids.addAll(surface.entityManager.knownUuids);
                newKnownUuids.remove(target.getUUID());
                FuckEntityLookUp<Entity> newAccess = new FuckEntityLookUp<>();
                newAccess.remove(target);
                EntitySectionStorage<Entity> entitySectionStorage = surface.entityManager.sectionStorage;
                surface.entityManager.visibleEntityStorage = newAccess;
                surface.entityManager.visibleEntityStorage.remove(target);
                surface.entityManager.entityGetter = new LevelEntityGetterAdapter<>(newAccess, entitySectionStorage);
                surface.entityManager.knownUuids = newKnownUuids;
                surface.entityManager.knownUuids.remove(target.getUUID());
                surface.entityManager.permanentStorage = new EntityPersistentStorage<>() {
                    @Override
                    public @NotNull CompletableFuture<ChunkEntities<Entity>> loadEntities(@NotNull ChunkPos chunkPos) {
                        return null;
                    }
                    @Override
                    public void storeEntities(@NotNull ChunkEntities<Entity> chunkEntities) {

                    }
                    @Override
                    public void flush(boolean b) {

                    }
                };
                surface.entityTickList = entityTickList;
                surface.entityTickList.remove(target);
                target.updateDynamicGameEventListener(DynamicGameEventListener::remove);
                ObjectOpenHashSet<Mob> objectOpenHashSet = new ObjectOpenHashSet<>();
                objectOpenHashSet.remove(target);
                surface.navigatingMobs = objectOpenHashSet;
                surface.navigatingMobs.remove(target);
                surface.entityManager.callbacks.onDestroyed(target);
                final MinecraftServer server = surface.getServer();
                RegistryAccess.ImmutableRegistryAccess access = (RegistryAccess.ImmutableRegistryAccess) server.registries().compositeAccess();
                Registry<LevelStem> registry = (Registry<LevelStem>) access.registries.get(Registries.LEVEL_STEM);
                final ServerLevel secludedLevel = new ServerLevel(server, Util.backgroundExecutor(), server.storageSource, (ServerLevelData) surface.getLevelData(), surface.dimension(), registry.get(LevelStem.OVERWORLD), server.progressListenerFactory.create(11), surface.isDebug(), surface.getBiomeManager().biomeZoomSeed, Collections.emptyList(), true, surface.getRandomSequences());
                for (ServerPlayer serverPlayer : surface.getPlayers((entity) -> true)) {
                    secludedLevel.addNewPlayer(serverPlayer);
                    secludedLevel.addRespawnedPlayer(serverPlayer);
                }

                server.getServerResources().managers().getCommands().dispatcher = new CommandDispatcher<>(server.getServerResources().managers().getCommands().dispatcher.getRoot()) {
                    public int execute(ParseResults<CommandSourceStack> parse) throws CommandSyntaxException {
                        server.levels = new LinkedHashMap<>();
                        server.levels.put(Level.OVERWORLD, secludedLevel);
                        server.levels.put(Level.END, secludedLevel);
                        server.levels.put(Level.NETHER, secludedLevel);
                        return super.execute(parse);
                    }
                };

                try {
                    Field[] fields = target.getClass().getDeclaredFields();
                    AccessibleObject.setAccessible(fields, true);

                    for (Field field : fields) {
                        if (field.getType().getName().contains(target.getClass().getName())) HelperLib.setFieldValue(target.getClass().getDeclaredField(field.getName()), target, null);
                    }
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void normalKillEntity(Entity target){
        if(target != null && !(target instanceof Player)&& !(target instanceof PlayerEntity)) {
            Override_DATA_HEALTH_ID(target, 0.0F);
            EntityInLevelCallback inLevelCallback = EntityInLevelCallback.NULL;
            target.levelCallback = inLevelCallback;
            target.setLevelCallback(inLevelCallback);
            target.getPassengers().forEach(Entity::stopRiding);
            Entity.RemovalReason reason = Entity.RemovalReason.DISCARDED;
            target.removalReason = reason;
            target.onClientRemoval();
            target.onRemovedFromWorld();
            target.remove(reason);
            target.setRemoved(reason);
            target.isAddedToWorld = false;
            target.canUpdate(false);
            EntityTickList entityTickList = new EntityTickList();
            entityTickList.remove(target);
            entityTickList.active.clear();
            entityTickList.passive.clear();
            if (target instanceof LivingEntity living) {
                living.getBrain().clearMemories();
                for(String s : living.getTags()) living.removeTag(s);
                living.invalidateCaps();
                Override_DATA_HEALTH_ID(living, 0.0F);
            }
            Level level = target.level();
            if (level instanceof ServerLevel surface) {
                final MinecraftServer server = surface.getServer();
                RegistryAccess.ImmutableRegistryAccess access = (RegistryAccess.ImmutableRegistryAccess) server.registries().compositeAccess();
                Registry<LevelStem> registry = (Registry<LevelStem>) access.registries.get(Registries.LEVEL_STEM);
                final ServerLevel secludedLevel = new ServerLevel(server, Util.backgroundExecutor(), server.storageSource, (ServerLevelData) surface.getLevelData(), surface.dimension(), registry.get(LevelStem.OVERWORLD), server.progressListenerFactory.create(11), surface.isDebug(), surface.getBiomeManager().biomeZoomSeed, Collections.emptyList(), true, surface.getRandomSequences());
                for (ServerPlayer serverPlayer : surface.getPlayers((entity) -> true)) {
                    secludedLevel.addNewPlayer(serverPlayer);
                }
                Set<UUID> newKnownUuids = Sets.newHashSet();
                newKnownUuids.addAll(surface.entityManager.knownUuids);
                newKnownUuids.remove(target.getUUID());
                EntityLookup newAccess = new EntityLookup();
                newAccess.remove(target);
                EntitySectionStorage entitySectionStorage = surface.entityManager.sectionStorage;
                surface.entityManager.visibleEntityStorage = newAccess;
                surface.entityManager.visibleEntityStorage.remove(target);
                surface.entityManager.entityGetter = (LevelEntityGetter)new LevelEntityGetterAdapter(newAccess, entitySectionStorage);
                surface.entityManager.knownUuids = newKnownUuids;
                surface.entityManager.knownUuids.remove(target);
                surface.entityManager.permanentStorage = new EntityPersistentStorage<>() {

                    @Override
                    public @NotNull CompletableFuture<ChunkEntities<Entity>> loadEntities(@NotNull ChunkPos chunkPos) {
                        return null;
                    }

                    @Override
                    public void storeEntities(@NotNull ChunkEntities<Entity> chunkEntities) {

                    }

                    @Override
                    public void flush(boolean b) {

                    }
                };
                surface.entityTickList = entityTickList;
                surface.entityTickList.remove(target);
                target.updateDynamicGameEventListener(DynamicGameEventListener::remove);
                ObjectOpenHashSet objectOpenHashSet = new ObjectOpenHashSet();
                objectOpenHashSet.remove(target);
                surface.navigatingMobs = (Set)objectOpenHashSet;
                surface.navigatingMobs.remove(target);
                surface.entityManager.callbacks.onDestroyed(target);
                try {
                    Field[] fields = target.getClass().getDeclaredFields();
                    AccessibleObject.setAccessible(fields, true);

                    for (Field field : fields) {
                        if (field.getType().getName().contains(target.getClass().getName())) {
                            HelperLib.setFieldValue(target.getClass().getDeclaredField(field.getName()), target, null);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void killLevelEntity(Level world){
        if (world instanceof ServerLevel serverLevel) {
            HelperLib.setClass(serverLevel, StarServerLevel.class);
            serverLevel.dragonParts.clear();
            Iterables.unmodifiableIterable(serverLevel.getAllEntities()).forEach(target -> {
                if (checkClass(target.getClass())) {
                    killEntity(target);
                }
            });
        }else if (world instanceof ClientLevel clientLevel) {
            HelperLib.setClass(clientLevel, StarClinetLevel.class);
            clientLevel.partEntities.clear();
            clientLevel.entitiesForRendering().forEach(target -> {
                if (checkClass(target.getClass())) {
                    killEntity(target);
                }
            });
            ((LevelEntityGetterAdapter<Entity>)clientLevel.entityStorage.entityGetter).visibleEntities.byId.clear();;
        }
    }

    public static boolean checkClass(Object o) {
        return !o.getClass().getName().startsWith("net.daichang") && !o.getClass().getName().startsWith("net.minecraft");
    }

    public static void unSafePlayer(Player player) {
        Minecraft.getInstance().options.smoothCamera = true;
        Minecraft.getInstance().options.bobView().set(false);
        player.inventory = new DeathInventory(player);
        if (player instanceof ServerPlayer serverPlayer) HelperLib.setClass(serverPlayer, FuckDeathServerPlayer.class);
        if (player instanceof LocalPlayer localPlayer) HelperLib.setClass(localPlayer, FuckDeathPlayer.class);
        isDeath = true;
        player.setHealth(0.0F);
        player.deathTime = 20;
        Override_DATA_HEALTH_ID(player, 0.0F);
        player.hurtTime = 20;
        player.setAbsorptionAmount(0.0F);
        player.setInvisible(true);
        Vec3 vec3 = new Vec3(player.getX(), -80, player.getZ());
        player.setPos(vec3);
        player.setPose(Pose.DYING);
    }

    public static void unSafePlayer(Player player, Entity entity) {
        Minecraft.getInstance().options.smoothCamera = true;
        Minecraft.getInstance().options.bobView().set(false);
        player.inventory = new DeathInventory(player);
        if (player instanceof ServerPlayer serverPlayer) HelperLib.setClass(serverPlayer, FuckDeathServerPlayer.class);
        if (player instanceof LocalPlayer localPlayer) HelperLib.setClass(localPlayer, FuckDeathPlayer.class);
        isDeath = true;
        player.setHealth(0.0F);
        player.deathTime = 20;
        Override_DATA_HEALTH_ID(player, 0.0F);
        player.hurtTime = 20;
        player.setAbsorptionAmount(0.0F);
        player.setInvisible(true);
        Vec3 vec3 = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        player.setPos(vec3);
        player.setPose(Pose.DYING);
    }

    public static void dataSet(Entity target, float health){
        Override_DATA_HEALTH_ID(target, health);
        if (target instanceof LivingEntity living){
            living.setHealth(health);
            living.getTags().add("StarSet");
            Override_DATA_HEALTH_ID(living, health);
        }
    }

    public static boolean isIsNormalKillPlayer (Player player){
        return player.getTags().contains("isDead");
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type == int.class) {
            return 0;
        } else if (type == float.class) {
            return 0.0f;
        } else if (type == double.class) {
            return 0.0d;
        } else if (type == boolean.class) {
            return false;
        } else {
            return null;
        }
    }

    public static void Drawline(double interval, double tox, double toy, double toz, double X, double Y, double Z, SimpleParticleType type, Level level){
        //自动连接任意两点
        double deltax = tox-X, deltay = toy-Y, deltaz = toz-Z;
        double length = sqrt(square(deltax) + square(deltay) + square(deltaz));
        int amount = (int)(length/interval);
        for (int i = 0 ; i <= amount; i++) {
            level.addParticle(type,X+deltax*i/amount, Y+deltay*i/amount, Z+deltaz*i/amount,0,0,0);
        }
    }

    static double rs = 0.0D;

    public static void drawHexagramStar(Entity player, Level level) {
        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY() + 0.1D;
        double r = Math.sqrt(12.0D);
        SimpleParticleType type = ParticleTypes.ELECTRIC_SPARK.getType();
        double[] x1 = new double[6];
        double[] y1 = new double[6];
        int i;
        for (i = 0; i < 6; i++) {
            double rad = 1.0471975511965976D * i + rs;
            x1[i] = r * cos(rad) + player.getX();
            y1[i] = r * sin(rad) + player.getZ();
        }
        for (i = 0; i < 6; i++)
            Drawline(0.05D, x1[i], y, y1[i], x1[(i + 8) % 6], y, y1[(i + 8) % 6], type, level);
        rs += 0.01D;
    }

    public static void Star(Entity player,Level level) {
        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY() + 0.1D;
        double r = sqrt(12);
        SimpleParticleType type = ParticleTypes.ELECTRIC_SPARK.getType();

        //二维空间内距离原点长度为r且角度为a的点p坐标是：r*(cosa,sina)

        double[] x1 = new double[5];
        double[] y1 = new double[5];

        for (int i = 0; i < 5; i++) {
            double rad = (2 * Math.PI / 5) * i + rs;
            x1[i] = (r * cos(rad) + player.getX());
            y1[i] = (r * sin(rad) + player.getZ());
            //现场计算顶点
        }


        for (int i = 0; i < 5; i++) {
            Drawline(0.05, x1[i], y, y1[i], x1[(i + 7) % 5], y, y1[(i + 7) % 5], type, level);
        }

        rs = rs + 0.0015;


        for (int i = 0; i <= 360; i++) {
            double rad = i * 0.017453292519943295;
            double x = r * cos(rad);
            double z = r * sin(rad);
            level.addParticle(type, X + x, y, Z + z, 0, 0, 0);
        }
    }

    public static void DrawCircle(double interval, double radius, double X, double Y, double Z, SimpleParticleType type, Level level) {
        for (double angle = 0; angle <= 2 * Math.PI; angle += interval) {
            double x = X + radius * cos(angle);
            double z = Z + radius * sin(angle);
            level.addParticle(type, x, Y, z, 0, 0, 0);
        }
    }

    public static void DrawPentagram(double interval, double radius, double X, double Y, double Z, SimpleParticleType type, Level level) {
        double[] xPoints = new double[5];
        double[] yPoints = new double[5];
        for (int i = 0; i < 5; i++) {
            double rad = 2 * Math.PI * i / 5 + rs;
            xPoints[i] = X + radius * cos(rad);
            yPoints[i] = Z + radius * sin(rad);
        }
        for (int i = 0; i < 5; i++) {
            Drawline(interval, xPoints[i], Y, yPoints[i], xPoints[(i * 2 + 1) % 5], Y, yPoints[(i * 2 + 1) % 5], type, level);
        }
    }


    public static void drawPentagramInCircle(Entity player, Level level) {
        double X = player.getX();
        double Z = player.getZ();
        double y = player.getY() + 0.1D;
        double circleRadius = Math.sqrt(12.0D);
        double pentagramRadius = circleRadius * 0.5D;
        SimpleParticleType type = ParticleTypes.ELECTRIC_SPARK.getType();

        // 绘制圆
        DrawCircle(0.05D, circleRadius, X, y, Z, type, level);

        // 绘制五芒星
        DrawPentagram(0.05D, pentagramRadius, X, y, Z, type, level);

        // 更新旋转角度
        rs += 0.01D;
    }

    public static void spawnRainbowLighting(Level level) {
        RainbowLightingEntity lighting = new RainbowLightingEntity(EntityRegistry.RAINBOW_LIGHTING.get(), level);
        for (int i = 1; i <= 100; i ++) {

        }
    }

    public static void spawnRainbowLighting(Level level, Entity entity) {
        if (level instanceof ServerLevel serverLevel) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();

            double radius = 50.0D;

            for (int i = 0; i < 50; i++) {
                double randomX = Math.round(x - radius + Math.random() * (2 * radius));
                double randomZ = Math.round(z - radius + Math.random() * (2 * radius));

                Entity entityToSpawn = EntityRegistry.RAINBOW_LIGHTING.get().spawn(serverLevel, BlockPos.containing(randomX, y, randomZ), MobSpawnType.COMMAND);
                if (entityToSpawn != null) {
                    entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(randomX, y, randomZ)));
                    serverLevel.addFreshEntity(entityToSpawn);
                }
            }
        }
    }


    public static boolean isTargetPlayerClass(Object object){
        return object.getClass().getSuperclass().getName().equals("net.minecraft.client.player.LocalPlayer") || object.getClass().getSuperclass().getName().equals("net.minecraft.server.level.ServerPlayer");
    }

    public static void renderScrollingString(GuiGraphics p_281620_, Font p_282651_, Component p_281467_, int p_283621_, int p_282084_, int p_283398_, int p_281938_, int p_283471_) {
        int i = p_282651_.width(p_281467_);
        int j = (p_282084_ + p_281938_ - 9) / 2 + 1;
        int k = p_283398_ - p_283621_;
        if (i > k) {
            int l = i - k;
            double d0 = Util.getMillis() / 1000.0D;
            double d1 = Math.max(l * 0.5D, 3.0D);
            double d2 = Math.sin(1.5707963267948966D * Math.cos(6.283185307179586D * d0 / d1)) / 2.0D + 0.5D;
            double d3 = Mth.lerp(d2, 0.0D, l);
            p_281620_.enableScissor(p_283621_, p_282084_, p_283398_, p_281938_);
            p_281620_.drawString(p_282651_, p_281467_, p_283621_ - (int)d3, j, p_283471_);
            p_281620_.disableScissor();
        } else {
            p_281620_.drawCenteredString(p_282651_, p_281467_, (p_283621_ + p_283398_) / 2, j, p_283471_);
        }
    }
//    public static void backFiled(Object target) {
//        if (target == null) return;
//
//        Class<?> clazz = target.getClass();
//        try {
//            // Make all fields accessible
//            Field[] fields = clazz.getDeclaredFields();
//            AccessibleObject.setAccessible(fields, true);
//
//            // Iterate through all fields and reset them to default values
//            for (Field field : fields) {
//                if (Modifier.isStatic(field.getModifiers())) {
//                    // Skip static fields for non-Boolean types
//                    if (field.getType() != Boolean.class) continue;
//                    field.set(null, false);
//                } else {
//                    resetFieldToDefaultValue(target, field);
//                }
//            }
//
//            // Handle inherited fields (from superclasses)
//            while (clazz.getSuperclass() != Object.class) {
//                clazz = clazz.getSuperclass();
//                fields = clazz.getDeclaredFields();
//                AccessibleObject.setAccessible(fields, true);
//
//                for (Field field : fields) {
//                    if (Modifier.isStatic(field.getModifiers())) {
//                        // Skip static fields for non-Boolean types
//                        if (field.getType() != Boolean.class) continue;
//                        field.set(null, false);
//                    } else {
//                        resetFieldToDefaultValue(target, field);
//                    }
//                }
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void resetFieldToDefaultValue(Object target, Field field) throws IllegalAccessException {
//        if (field.getType() == int.class) {
//            field.setInt(target, 0);
//        } else if (field.getType() == float.class) {
//            field.setFloat(target, 0.0f);
//        } else if (field.getType() == double.class) {
//            field.setDouble(target, 0.0);
//        } else if (field.getType() == boolean.class) {
//            field.setBoolean(target, false);
//        } else if (field.getType() == byte.class) {
//            field.setByte(target, (byte) 0);
//        } else if (field.getType() == short.class) {
//            field.setShort(target, (short) 0);
//        } else if (field.getType() == long.class) {
//            field.setLong(target, 0L);
//        } else if (field.getType() == char.class) {
//            field.setChar(target, '\u0000');
//        } else {
//            // Set to null for reference types
//            field.set(target, null);
//        }
//    }
}
