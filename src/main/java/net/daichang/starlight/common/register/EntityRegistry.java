package net.daichang.starlight.common.register;

import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.server.entity.PlayerEntity;
import net.daichang.starlight.server.entity.light.RainbowLightingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, StarlightMod.MOD_ID);

    public static final RegistryObject<EntityType<PlayerEntity>> PLAYER_ENTITY = register(EntityType.Builder.<PlayerEntity>of(PlayerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(PlayerEntity::new).sized(0.6f, 1.8f));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(EntityType.Builder<T> entityTypeBuilder) {
        return ENTITIES.register("starlight", () -> entityTypeBuilder.build("starlight"));
    }
    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return ENTITIES.register(registryname, () -> entityTypeBuilder.build(registryname));
    }

    public static final RegistryObject<EntityType<RainbowLightingEntity>> RAINBOW_LIGHTING = register("rainbow_lighting", EntityType.Builder.<RainbowLightingEntity>of(RainbowLightingEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(RainbowLightingEntity::new).sized(0.6f, 1.8f));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PLAYER_ENTITY.get(), PlayerEntity.createAttributes().build());
    }
}
