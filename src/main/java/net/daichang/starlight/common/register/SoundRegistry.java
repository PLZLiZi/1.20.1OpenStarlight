package net.daichang.starlight.common.register;

import net.daichang.starlight.StarlightMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> sounds = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, StarlightMod.MOD_ID);

    public static RegistryObject<SoundEvent> register(String name) {
        return sounds.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(StarlightMod.MOD_ID, name)));
    }

    public static RegistryObject<SoundEvent> register(String name, String id) {
        return sounds.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(StarlightMod.MOD_ID, id)));
    }

    public static final RegistryObject<SoundEvent> BACK_GROUND;
    public static RegistryObject<SoundEvent> YUZU_TITLE_MUSIC;
    public static RegistryObject<SoundEvent> YUZU_TITLE_BUTTON_ON;
    public static RegistryObject<SoundEvent> YUZU_TITLE_BUTTON_CLICK;
    public static RegistryObject<SoundEvent> YUZU_TITLE_BUTTON_NEW_GAME;
    public static RegistryObject<SoundEvent> YUZU_TITLE_BUTTON_SELECT_WORLD;
    public static RegistryObject<SoundEvent> YUZU_TITLE_BUTTON_OPTIONS;
    public static RegistryObject<SoundEvent> YUZU_TITLE_BUTTON_QUIT_GAME;
    public static RegistryObject<SoundEvent> YUZU_TITLE_SENREN;
    public static RegistryObject<SoundEvent> YUZU_TITLE_BUTTON_REALMS;
    public static RegistryObject<SoundEvent> YUZU_TITLE_BUTTON_MOD_LIST;

    static {
        BACK_GROUND = register("background", "background");
        YUZU_TITLE_BUTTON_MOD_LIST = register("yuzu_title_button_mod_list");
        YUZU_TITLE_BUTTON_REALMS = register("yuzu_title_button_realms");
        YUZU_TITLE_SENREN = register("yuzu_title_senren");
        YUZU_TITLE_BUTTON_QUIT_GAME = register("yuzu_title_button_quit_game");
        YUZU_TITLE_BUTTON_OPTIONS = register("yuzu_title_button_options");
        YUZU_TITLE_BUTTON_SELECT_WORLD = register("yuzu_title_button_select_world");
        YUZU_TITLE_BUTTON_NEW_GAME = register("yuzu_title_button_new_game");
        YUZU_TITLE_BUTTON_CLICK = register("yuzu_title_button_click");
        YUZU_TITLE_BUTTON_ON = register("yuzu_title_button_on");
        YUZU_TITLE_MUSIC = register("yuzu_title_music");
    }
}
