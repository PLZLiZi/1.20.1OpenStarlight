package net.daichang.starlight.common.register;

import net.daichang.starlight.StarlightMod;
import net.daichang.starlight.common.items.*;
import net.daichang.starlight.common.items.fake.FakeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegister {
    public static final DeferredRegister<Item> items = DeferredRegister.create(ForgeRegistries.ITEMS, StarlightMod.MOD_ID);

    public static final RegistryObject<Item> FAKE_AIR_1;
    public static final RegistryObject<Item> FAKE_AIR_2;
    public static final RegistryObject<Item> STARLIGHT_ITEM;
    public static final RegistryObject<Item> DEATH;
    public static final RegistryObject<Item> TARGET_MODE_SWITCH;
    public static final RegistryObject<Item> Item_LVING_ENTITY_DEBUG;
    public static final RegistryObject<Item> ENTITY_GETTER;
    public static final RegistryObject<Item> GOD_SPAWN;

    static {
        FAKE_AIR_1 = items.register("fake_air", FakeItem::new);
        FAKE_AIR_2 = items.register("fake_air_2", FakeItem::new);
        STARLIGHT_ITEM = items.register("elyterminal_sword", ElyterminalSword::new);
        DEATH = items.register("death", DeathItem::new);
        TARGET_MODE_SWITCH = items.register("target_mode_switch", AutoAttackItem::new);
        Item_LVING_ENTITY_DEBUG = items.register("entity_debug", EntityDebugStick::new);
        ENTITY_GETTER = items.register("entity_getter", EntityGetter::new);
        GOD_SPAWN = items.register("summon_tafei", () -> new IForgeSpawnEgg(EntityRegistry.PLAYER_ENTITY));
    }
}
