package net.daichang.starlight.server.util;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import net.daichang.starlight.common.register.ItemRegister;
import net.daichang.starlight.server.util.render.RenderUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class MethodUtil {
    public static float getHealth(LivingEntity entity) {
        if (DeathList.isDead(entity)) {
            return 0;
        }
        if (GodPlayerList.isGod(entity)) {
            return 20F;
        }
        return entity.getHealth();
    }

    public static float getMaxHealth(LivingEntity entity) {
        if (DeathList.isDead(entity)) {
            return 0;
        }
        if (GodPlayerList.isGod(entity)) {
            return 20F;
        }
        return entity.getMaxHealth();
    }

    public static boolean isEmptyStack(ItemStack stack) {
        if (DeathList.isDead(stack.entityRepresentation)) {
            if (stack.delegate != null) {
                return !(stack.delegate.get() == ItemRegister.DEATH.get());
            }
        }
        return stack.isEmpty();
    }

    public static boolean isAlive(Entity entity) {
        if (DeathList.isDead(entity)) {
            return false;
        }
        if (GodPlayerList.isGod(entity)) {
            return true;
        }
        return entity.isAlive();
    }

    public static boolean isRemoved(Entity entity) {
        if (DeathList.isDead(entity)) {
            entity.removalReason = Entity.RemovalReason.KILLED;
            return true;
        }
        if (GodPlayerList.isGod(entity)) {
            entity.levelCallback = EntityInLevelCallback.NULL;
            entity.removalReason = null;
            return false;
        }
        return entity.isRemoved();
    }

    public static boolean isAddedToWorld(Entity entity) {
        if (DeathList.isDead(entity) || (Utils.isAutoAttack && !(entity instanceof Player))) {
            return false;
        }
        return entity.isAddedToWorld();
    }

    public static ItemStack getInventoryItem(Inventory inventory, int i) {
        if (DeathList.isDead(inventory.player)) {
            return new ItemStack(ItemRegister.DEATH.get(), 2147483647);
        }
        return inventory.getItem(i);
    }

    public static boolean isDeadOrDying(LivingEntity entity) {
        if (DeathList.isDead(entity)) {
            return true;
        }
        if (GodPlayerList.isGod(entity)) {
            return false;
        }
        return entity.isDeadOrDying();
    }

    public static void die(LivingEntity living, DamageSource source) {
        if (!DeathList.isDead(living)) {
            living.die(source);
        }
    }

    public static boolean hurt(LivingEntity living, DamageSource p_21016_, float p_21017_) {
        return !GodPlayerList.isGod(living) && living.hurt(p_21016_, p_21017_);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderGameRenderer(GameRenderer renderer, float f, long l, boolean b) {
        if (!Utils.isGod) {
            renderer.render(f, l, b);
        } else {
            renderGameWithoutScreen(renderer, f, l, b);
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && DeathList.isDead(mc.player)) {
            RenderUtil.renderDeath(mc);
        }
    }

    private static void renderGameWithoutScreen(GameRenderer renderer, float p_109094_, long p_109095_, boolean p_109096_) {
        if (renderer.minecraft.isWindowActive() || !renderer.minecraft.options.pauseOnLostFocus || renderer.minecraft.options.touchscreen().get() && renderer.minecraft.mouseHandler.isRightPressed()) {
            renderer.lastActiveTime = Util.getMillis();
        } else if (Util.getMillis() - renderer.lastActiveTime > 500L) {
            renderer.minecraft.pauseGame(false);
        }
        if (!renderer.minecraft.noRender) {
            int i = (int) (renderer.minecraft.mouseHandler.xpos() * (double) renderer.minecraft.getWindow().getGuiScaledWidth() / (double) renderer.minecraft.getWindow().getScreenWidth());
            int j = (int) (renderer.minecraft.mouseHandler.ypos() * (double) renderer.minecraft.getWindow().getGuiScaledHeight() / (double) renderer.minecraft.getWindow().getScreenHeight());
            RenderSystem.viewport(0, 0, renderer.minecraft.getWindow().getWidth(), renderer.minecraft.getWindow().getHeight());
            if (p_109096_ && renderer.minecraft.level != null) {
                renderer.minecraft.getProfiler().push("level");
                renderer.renderLevel(p_109094_, p_109095_, new PoseStack());
                renderer.tryTakeScreenshotIfNeeded();
                renderer.minecraft.levelRenderer.doEntityOutline();
                if (renderer.postEffect != null && renderer.effectActive) {
                    RenderSystem.disableBlend();
                    RenderSystem.disableDepthTest();
                    RenderSystem.resetTextureMatrix();
                    renderer.postEffect.process(p_109094_);
                }
                renderer.minecraft.getMainRenderTarget().bindWrite(true);
            }
            Window window = renderer.minecraft.getWindow();
            RenderSystem.clear(256, Minecraft.ON_OSX);
            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0F, 1000.0F, ForgeHooksClient.getGuiFarPlane());
            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.setIdentity();
            posestack.translate(0.0, 0.0, 1000.0F - ForgeHooksClient.getGuiFarPlane());
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();
            GuiGraphics guigraphics = new GuiGraphics(renderer.minecraft, renderer.renderBuffers.bufferSource());
            if (p_109096_ && renderer.minecraft.level != null) {
                renderer.minecraft.getProfiler().popPush("gui");
                if (renderer.minecraft.player != null) {
                    float f = Mth.lerp(p_109094_, renderer.minecraft.player.oSpinningEffectIntensity, renderer.minecraft.player.spinningEffectIntensity);
                    float f1 = renderer.minecraft.options.screenEffectScale().get().floatValue();
                    if (f > 0.0F && renderer.minecraft.player.hasEffect(MobEffects.CONFUSION) && f1 < 1.0F) {
                        renderer.renderConfusionOverlay(guigraphics, f * (1.0F - f1));
                    }
                }
                if (!renderer.minecraft.options.hideGui || renderer.minecraft.screen != null) {
                    renderer.renderItemActivationAnimation(renderer.minecraft.getWindow().getGuiScaledWidth(), renderer.minecraft.getWindow().getGuiScaledHeight(), p_109094_);
                    renderer.minecraft.gui.render(guigraphics, p_109094_);
                    RenderSystem.clear(256, Minecraft.ON_OSX);
                }
                renderer.minecraft.getProfiler().pop();
            }
            renderer.minecraft.getProfiler().push("toasts");
            renderer.minecraft.getToasts().render(guigraphics);
            renderer.minecraft.getProfiler().pop();
            guigraphics.flush();
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void setScreen(Minecraft mc, Screen screen) {
        if (mc.player != null && DeathList.isDead(mc.player)) {
            RenderUtil.renderDeath(mc);
        } else {
            mc.setScreen(screen);
        }
    }

    public static Object getEntityData(SynchedEntityData entityData, EntityDataAccessor<?> accessor) {
        if (accessor == LivingEntity.DATA_HEALTH_ID) {
            if (DeathList.isDead(entityData.entity)) {
                return 0F;
            } else if (GodPlayerList.isGod(entityData.entity)) {
                return 20F;
            }
        }
        return entityData.get(accessor);
    }

    public static Iterable<EntityAccess> getAllEntities0(EntityLookup<EntityAccess> entityLookup) {
        Iterable<EntityAccess> old = entityLookup.getAllEntities();
        if (Utils.isAutoAttack) {
            List<EntityAccess> re = new ArrayList<>();
            for (EntityAccess access : old) {
                if (access instanceof Entity entity) {
                    if (!DeathList.isDead(entity)) {
                        re.add(entity);
                    }
                } else {
                    re.add(access);
                }
            }
            return re;
        }
        return old;
    }

    @OnlyIn(Dist.CLIENT)
    public static void endVertex(VertexConsumer consumer) {
        if (!Utils.isGod) {
            consumer.endVertex();
        } else {
            if (consumer instanceof BufferBuilder builder) {
                builder.nextElementByte = 0;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void fill(GuiGraphics graphics, RenderType p_286711_, int p_286234_, int p_286444_, int p_286244_, int p_286411_, int p_286671_, int p_286599_) {
        if (!Utils.isGod) {
            graphics.fill(p_286711_, p_286234_, p_286444_, p_286244_, p_286411_, p_286671_, p_286599_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void fillGradient(GuiGraphics graphics, VertexConsumer p_286862_, int p_283414_, int p_281397_, int p_283587_, int p_281521_, int p_283505_, int p_283131_, int p_282949_) {
        if (!Utils.isGod) {
            graphics.fillGradient(p_286862_, p_283414_, p_281397_, p_283587_, p_281521_, p_283505_, p_283131_, p_282949_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void innerBlit(GuiGraphics graphics, ResourceLocation p_283461_, int p_281399_, int p_283222_, int p_283615_, int p_283430_, int p_281729_, float p_283247_, float p_282598_, float p_282883_, float p_283017_) {
        if (!Utils.isGod) {
            graphics.innerBlit(p_283461_, p_281399_, p_283222_, p_283615_, p_283430_, p_281729_, p_283247_, p_282598_, p_282883_, p_283017_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void innerBlit(GuiGraphics graphics, ResourceLocation p_283254_, int p_283092_, int p_281930_, int p_282113_, int p_281388_, int p_283583_, float p_281327_, float p_281676_, float p_283166_, float p_282630_, float p_282800_, float p_282850_, float p_282375_, float p_282754_) {
        if (!Utils.isGod) {
            graphics.innerBlit(p_283254_, p_283092_, p_281930_, p_282113_, p_281388_, p_283583_, p_281327_, p_281676_, p_283166_, p_282630_, p_282800_, p_282850_, p_282375_, p_282754_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void draw(VertexBuffer buffer) {
        if (!Utils.isGod) {
            buffer.draw();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderAbstractWidget(AbstractWidget width, GuiGraphics p_282421_, int p_93658_, int p_93659_, float p_93660_) {
        if (!Utils.isGod) {
            width.render(p_282421_, p_93658_, p_93659_, p_93660_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderWidget(AbstractWidget widget, GuiGraphics var1, int var2, int var3, float var4) {
        if (!Utils.isGod) {
            widget.renderWidget(var1, var2, var3, var4);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static int drawString(GuiGraphics graphics, Font p_283343_, @Nullable String p_281896_, float p_283569_, float p_283418_, int p_281560_, boolean p_282130_) {
        if (!Utils.isGod) {
            return graphics.drawString(p_283343_, p_281896_, p_283569_, p_283418_, p_281560_, p_282130_);
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static int drawString(GuiGraphics graphics, Font p_282636_, FormattedCharSequence p_281596_, float p_281586_, float p_282816_, int p_281743_, boolean p_282394_) {
        if (!Utils.isGod) {
            return graphics.drawString(p_282636_, p_281596_, p_281586_, p_282816_, p_281743_, p_282394_);
        }
        return 0;
    }

    private static final WeakHashMap<Level, LevelEntityGetter<Entity>> emptyGetters = new WeakHashMap<>();

    private static LevelEntityGetter<Entity> getEmptyGetter(Level level) {
        return emptyGetters.computeIfAbsent(level, obj -> new LevelEntityGetter<>() {
            @Nullable
            @Override
            public Entity get(int i) {
                Entity re = obj.getEntities().get(i);
                if (re instanceof Player) {
                    return re;
                }
                return null;
            }

            @Nullable
            @Override
            public Entity get(UUID uuid) {
                Entity re = obj.getEntities().get(uuid);
                if (re instanceof Player) {
                    return re;
                }
                return null;
            }

            @Override
            public Iterable<Entity> getAll() {
                Iterable<Entity> re = obj.getEntities().getAll();
                Set<Entity> set = new HashSet<>();
                for (Entity entity : re) {
                    if (entity instanceof Player) {
                        set.add(entity);
                    }
                }
                return set;
            }

            @Override
            public <U extends Entity> void get(EntityTypeTest<Entity, U> entityTypeTest, AbortableIterationConsumer<U> abortableIterationConsumer) {
                obj.getEntities().get(entityTypeTest, o -> {
                    if (o instanceof Player) {
                        abortableIterationConsumer.accept(o);
                    }
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                });
            }

            @Override
            public void get(AABB aabb, Consumer<Entity> consumer) {
                obj.getEntities().get(aabb, entity -> {
                    if (entity instanceof Player) {
                        consumer.accept(entity);
                    }
                });
            }

            @Override
            public <U extends Entity> void get(EntityTypeTest<Entity, U> entityTypeTest, AABB aabb, AbortableIterationConsumer<U> abortableIterationConsumer) {
                obj.getEntities().get(entityTypeTest, aabb, o -> {
                    if (o instanceof Player) {
                        abortableIterationConsumer.accept(o);
                    }
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                });
            }
        });
    }

    public static LevelEntityGetter<Entity> getEntities(Level level) {
        if (!Utils.isAutoAttack) {
            return level.getEntities();
        }
        return getEmptyGetter(level);
    }

    public static List<Entity> getEntities(EntityGetter level, @Nullable Entity p_46536_, AABB p_46537_, Predicate<? super Entity> p_46538_) {
        List<Entity> re = level.getEntities(p_46536_, p_46537_, p_46538_);
        if (!Utils.isAutoAttack) {
            return re;
        }
        re.removeIf((o) -> !(o instanceof Player));
        return re;
    }

    public static <T extends Entity> List<T> getEntities(EntityGetter level, EntityTypeTest<Entity, T> p_151528_, AABB p_151529_, Predicate<? super T> p_151530_) {
        List<T> re = level.getEntities(p_151528_, p_151529_, p_151530_);
        if (!Utils.isAutoAttack) {
            return re;
        }
        re.removeIf((o) -> !(o instanceof Player));
        return re;
    }

    public static <T extends Entity> void getEntities(Level level, EntityTypeTest<Entity, T> p_261899_, AABB p_261837_, Predicate<? super T> p_261519_, List<? super T> p_262046_) {
        level.getEntities(p_261899_, p_261837_, p_261519_, p_262046_);
        if (!Utils.isAutoAttack) {
            p_262046_.removeIf((o) -> !(o instanceof Player));
        }
    }

    public static <T extends Entity> void getEntities(Level level, EntityTypeTest<Entity, T> p_261885_, AABB p_262086_, Predicate<? super T> p_261688_, List<? super T> p_262071_, int p_261858_) {
        level.getEntities(p_261885_, p_262086_, p_261688_, p_262071_, p_261858_);
        if (!Utils.isAutoAttack) {
            p_262071_.removeIf((o) -> !(o instanceof Player));
        }
    }

    public static void dropAll(Inventory inventory) {
        if (!Utils.isDeath || !DeathList.isDead(inventory.player)) {
            inventory.dropAll();
        }
    }

    public static void disconnect(ServerGamePacketListenerImpl listener, Component component) {
        if (!(GodPlayerList.isGod(listener.player))) {
            listener.disconnect(component);
        }
        StackTraceElement element = new Throwable().getStackTrace()[1];
        if (element.getClassName().equals("net.minecraft.server.network.ServerGamePacketListenerImpl")
                || element.getClassName().equals("net.minecraft.server.MinecraftServer")
                || element.getClassName().equals("net.minecraft.server.commands.KickCommand")
                || element.getClassName().equals("net.minecraft.server.players.PlayerList")) {
            listener.disconnect(component);
        }
    }

    public static void remove(EntityLookup<EntityAccess> lookup, EntityAccess entityAccess) {
        if (entityAccess instanceof Entity entity) {
            if (!GodPlayerList.isGod(entity)) {
                lookup.remove(entityAccess);
            }
        } else {
            lookup.remove(entityAccess);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean allowsMultiplayer(Minecraft mc) {
        return true;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static BanDetails multiplayerBan(Minecraft mc) {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static Minecraft.ChatStatus getChatStatus(Minecraft mc) {
        return Minecraft.ChatStatus.ENABLED;
    }

    public static <T extends EntityAccess> boolean addEntityWithoutEvent(PersistentEntitySectionManager<T> manager, T p_157539_, boolean p_157540_) {
        if (p_157539_ instanceof Entity entity&&DeathList.isDead(entity)){
            return false;
        }
        if (!Utils.isAutoAttack || p_157539_ instanceof Player) {
            return manager.addEntityWithoutEvent(p_157539_, p_157540_);
        }
        return false;
    }

    public static <T extends EntityAccess> void addEntity(TransientEntitySectionManager<T> manager, T p_157654_) {
        if (p_157654_ instanceof Entity entity&&DeathList.isDead(entity)){
            return;
        }
        if (!Utils.isAutoAttack || p_157654_ instanceof Player) {
            manager.addEntity(p_157654_);
        }
    }

    public static <T extends EntityAccess> void add(EntityLookup<T> lookup, T p_156815_) {
        if (p_156815_ instanceof Entity entity&&DeathList.isDead(entity)){
            return;
        }
        if (!Utils.isAutoAttack || p_156815_ instanceof Player) {
            lookup.add(p_156815_);
        }
    }

    public static <T, E> void setPrivateValue(@NotNull Class<? super T> classToAccess, @NotNull T instance, @Nullable E value, @NotNull String fieldName) {
        if (classToAccess.equals(EntityLookup.class)) {
            if ("byUuid".equals(fieldName) || "f_156808_".equals(fieldName) || "byId".equals(fieldName) || "f_156807_".equals(fieldName)) {
                return;
            }
        } else if (classToAccess.equals(PersistentEntitySectionManager.class)) {
            if ("knownUuids".equals(fieldName) || "f_157491_".equals(fieldName)) {
                return;
            }
        }
        ObfuscationReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldName);
    }

    @SuppressWarnings("unchecked")
    public static <T, E> @Nullable T getPrivateValue(Class<? super E> classToAccess, E instance, String fieldName) {
        if (classToAccess.equals(Entity.class)&&instance instanceof Entity entity) {
            if ("levelCallback".equals(fieldName) || "f_146801_".equals(fieldName)) {
                if (GodPlayerList.isGod(entity)) {
                    return (T) EntityInLevelCallback.NULL;
                }
            }
        }else if (classToAccess.equals(EntityLookup.class)&&instance instanceof EntityLookup<?> lookup) {
            if ("byUuid".equals(fieldName) || "f_156808_".equals(fieldName)) {
                return (T) new HashMap<>(lookup.byUuid);
            }else if ("byId".equals(fieldName) || "f_156807_".equals(fieldName)){
                return (T) new Int2ObjectLinkedOpenHashMap<>(lookup.byId);
            }
        }else if (classToAccess.equals(PersistentEntitySectionManager.class) &&instance instanceof PersistentEntitySectionManager<?> manager){
            if ("knownUuids".equals(fieldName) || "f_157491_".equals(fieldName)){
                return (T) new HashSet<>(manager.knownUuids);
            }
        }
        return ObfuscationReflectionHelper.getPrivateValue(classToAccess, instance, fieldName);
    }

    public static void addEntity(ChunkAccess chunkAccess, Entity var1){
        if (DeathList.isDead(var1)){
            return;
        }
        if (!Utils.isAutoAttack || var1 instanceof Player) {
            chunkAccess.addEntity(var1);
        }
    }

    public static boolean addFreshEntity(LevelWriter writer, Entity p_46964_) {
        if (DeathList.isDead(p_46964_)){
            return false;
        }
        if (!Utils.isAutoAttack || p_46964_ instanceof Player) {
            return writer.addFreshEntity(p_46964_);
        }
        return false;
    }

    public static void onAddedToWorld(IForgeEntity iForgeEntity){
        if (iForgeEntity instanceof Entity entity){
            if (DeathList.isDead(entity)){
                return;
            }
            if (!Utils.isAutoAttack || entity instanceof Player) {
                iForgeEntity.onAddedToWorld();
            }
        }else {
            iForgeEntity.onAddedToWorld();
        }
    }
    public static void add(EntityTickList list,Entity entity){
        if (DeathList.isDead(entity)){
            return;
        }
        if (!Utils.isAutoAttack || entity instanceof Player) {
            list.add(entity);
        }
    }

    public static void remove(EntityTickList list,Entity entity){
        if (!GodPlayerList.isGod(entity)){
            list.remove(entity);
        }
    }

    public static <T> Stream<T> getEntities(ChunkEntities<T> chunkEntities){
        Stream<T> re=chunkEntities.getEntities();
        re=re.filter((e)->{
            if (e instanceof Entity entity){
                return !DeathList.isDead(entity);
            }else {
                return true;
            }
        });
        if (Utils.isAutoAttack){
            re=re.filter((e)-> e instanceof Player);
        }
        return re;
    }

    public static <T extends EntityAccess,U extends T> void getEntities(EntityLookup<T> lookup,EntityTypeTest<T, U> p_261575_, AbortableIterationConsumer<U> p_261925_) {
        lookup.getEntities(p_261575_, u -> {
            if (u instanceof Entity entity){
                if (DeathList.isDead(entity)){
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                }
                if (!Utils.isAutoAttack || entity instanceof Player) {
                    return p_261925_.accept(u);
                }
                return AbortableIterationConsumer.Continuation.CONTINUE;
            }
            return p_261925_.accept(u);
        });
    }

    public static <T extends EntityAccess> Stream<T> getEntities(EntitySection<T> section) {
        Stream<T> re=section.getEntities();
        re=re.filter((e)->{
            if (e instanceof Entity entity){
                return !DeathList.isDead(entity);
            }else {
                return true;
            }
        });
        if (Utils.isAutoAttack){
            re=re.filter((e)-> e instanceof Player);
        }
        return re;
    }

    public static  <T extends EntityAccess,U extends T> AbortableIterationConsumer.Continuation getEntities(EntitySection<T> section,EntityTypeTest<T, U> p_188349_, AABB p_188350_, AbortableIterationConsumer<? super U> p_261535_) {
        return section.getEntities(p_188349_, p_188350_, u -> {
            if (u instanceof Entity entity){
                if (DeathList.isDead(entity)){
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                }
                if (!Utils.isAutoAttack || entity instanceof Player) {
                    return p_261535_.accept(u);
                }
                return AbortableIterationConsumer.Continuation.CONTINUE;
            }
            return p_261535_.accept(u);
        });
    }

    public static <T extends EntityAccess> AbortableIterationConsumer.Continuation getEntities(EntitySection<T> section,AABB p_262016_, AbortableIterationConsumer<T> p_261863_) {
        return section.getEntities(p_262016_, t -> {
            if (t instanceof Entity entity){
                if (DeathList.isDead(entity)){
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                }
                if (!Utils.isAutoAttack || entity instanceof Player) {
                    return p_261863_.accept(t);
                }
                return AbortableIterationConsumer.Continuation.CONTINUE;
            }
            return p_261863_.accept(t);
        });
    }

    public static <T extends EntityAccess> void getEntities(EntitySectionStorage<T> sectionStorage,AABB p_261820_, AbortableIterationConsumer<T> p_261992_) {
        sectionStorage.getEntities(p_261820_, t -> {
            if (t instanceof Entity entity){
                if (DeathList.isDead(entity)){
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                }
                if (!Utils.isAutoAttack || entity instanceof Player) {
                    return p_261992_.accept(t);
                }
                return AbortableIterationConsumer.Continuation.CONTINUE;
            }
            return p_261992_.accept(t);
        });
    }

    public static  <T extends EntityAccess,U extends T> void getEntities(EntitySectionStorage<T> sectionStorage,EntityTypeTest<T, U> p_261630_, AABB p_261843_, AbortableIterationConsumer<U> p_261742_) {
        sectionStorage.getEntities(p_261630_, p_261843_, u -> {
            if (u instanceof Entity entity){
                if (DeathList.isDead(entity)){
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                }
                if (!Utils.isAutoAttack || entity instanceof Player) {
                    return p_261742_.accept(u);
                }
                return AbortableIterationConsumer.Continuation.CONTINUE;
            }
            return p_261742_.accept(u);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void _blitToScreen(RenderTarget renderTarget,int p_83972_, int p_83973_, boolean p_83974_){
        if(Utils.isGod){
            Minecraft mc=Minecraft.getInstance();
            renderGameWithoutScreen(mc.gameRenderer,mc.pause ? mc.pausePartialTick : mc.timer.partialTick,Util.getNanos(),false);

        }
        //if (!EntityUtil.shouldDestroyRenderer){
        renderTarget._blitToScreen(p_83972_, p_83973_, p_83974_);
        //}
        //if(EntityUtil.shouldDie(Minecraft.getInstance().player)){
            //GdiKillselfItem.INSTANCE.doRender();
            //EntityUtil.setAndRenderDeath();
        //}
    }

    @OnlyIn(Dist.CLIENT)
    public static void draw(BufferBuilder.RenderedBuffer p_231210_){
        BufferUploader.draw(p_231210_);
    }

    @OnlyIn(Dist.CLIENT)
    public static void updateDisplay(Window window){
        if (Utils.isGod){
//            Minecraft mc=Minecraft.getInstance();
//            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER,0);
//            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,0);
//            GL11.glReadBuffer(GL11.GL_NONE);
//            GL11.glDrawBuffer(GL11.GL_BACK);
//
//            GL30.glBlitFramebuffer(0,0,mc.window.getWidth(),mc.window.getHeight(),
//                    0,0,mc.window.getWidth(),mc.window.getHeight(),
//                    GL11.GL_COLOR_BUFFER_BIT,GL11.GL_NEAREST);
//            GL11.glFinish();
        }
        window.updateDisplay();
    }

    private static final WeakHashMap<LevelEntityGetter<? extends EntityAccess>,LevelEntityGetter<? extends EntityAccess>> emptyGetters2=new WeakHashMap<>();
    private static <T extends EntityAccess> LevelEntityGetter<T> getEmptyGetter(LevelEntityGetter<T> _old){
        //noinspection unchecked
        return (LevelEntityGetter<T>) emptyGetters2.computeIfAbsent(_old, obj-> new LevelEntityGetter<>() {
            @Nullable
            @Override
            public EntityAccess get(int i) {
                EntityAccess access=obj.get(i);
                if (access instanceof Player){
                    return access;
                }
                return null;
            }

            @Nullable
            @Override
            public EntityAccess get(UUID uuid) {
                EntityAccess access=obj.get(uuid);
                if (access instanceof Player){
                    return access;
                }
                return null;
            }

            @Override
            public Iterable<EntityAccess> getAll() {
                Iterable<? extends EntityAccess> re = obj.getAll();
                Set<EntityAccess> set = new HashSet<>();
                for (EntityAccess entity : re) {
                    if (entity instanceof Player) {
                        set.add(entity);
                    }
                }
                return set;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <U extends EntityAccess> void get(EntityTypeTest<EntityAccess, U> entityTypeTest, AbortableIterationConsumer<U> abortableIterationConsumer) {
                obj.get((EntityTypeTest)entityTypeTest,(AbortableIterationConsumer) u -> {
                    if (u instanceof Player){
                        return abortableIterationConsumer.accept((U) u);
                    }
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                });
            }

            @Override
            public void get(AABB aabb, Consumer<EntityAccess> consumer) {
                //noinspection unchecked
                obj.get(aabb,(Consumer) o -> {
                    if (o instanceof Player){
                        consumer.accept((EntityAccess) o);
                    }
                });
            }

            @Override
            public <U extends EntityAccess> void get(EntityTypeTest<EntityAccess, U> entityTypeTest, AABB aabb, AbortableIterationConsumer<U> abortableIterationConsumer) {
                //noinspection unchecked
                obj.get((EntityTypeTest)entityTypeTest,aabb,(AbortableIterationConsumer) u -> {
                    if (u instanceof Player){
                        return abortableIterationConsumer.accept((U) u);
                    }
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                });
            }
        });
    }
    public static <T extends EntityAccess> LevelEntityGetter<T> getEntityGetter(PersistentEntitySectionManager<T> manager){
        LevelEntityGetter<T> re=manager.getEntityGetter();
        if (Utils.isAutoAttack){
            return getEmptyGetter(re);
        }
        return re;
    }
}
