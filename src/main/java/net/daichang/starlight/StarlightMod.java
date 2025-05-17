package net.daichang.starlight;

import com.mojang.logging.LogUtils;
import com.sun.tools.attach.VirtualMachine;
import net.daichang.starlight.client.apis.AllReturn;
import net.daichang.starlight.client.apis.DCOpenGlLib;
import net.daichang.starlight.client.apis.DaiChangMaginc;
import net.daichang.starlight.client.window.java.HelperWindow;
import net.daichang.starlight.common.register.EntityRegistry;
import net.daichang.starlight.common.register.ItemRegister;
import net.daichang.starlight.common.register.SoundRegistry;
import net.daichang.starlight.common.register.TabRegister;
import net.daichang.starlight.server.entity.EntityModelOfDC;
import net.daichang.starlight.server.entity.PlayerEntityRender;
import net.daichang.starlight.server.entity.light.RainbowLightingEntity;
import net.daichang.starlight.server.entity.light.RainbowLightingRenderer;
import net.daichang.starlight.server.mc.FuckEventBus;
import net.daichang.starlight.server.util.DeathList;
import net.daichang.starlight.server.util.StarLightUnsafeAccess;
import net.daichang.starlight.server.util.Utils;
import net.daichang.starlight.server.util.daichangs.DCMethodTest;
import net.daichang.starlight.server.util.errors.UnknowIPException;
import net.daichang.starlight.server.util.helper.HelperLib;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.server.ServerLifecycleHooks;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;

@DCMethodTest(name = "starlight", clazz = StarlightMod.class)
@Mod("starlight")
public final class StarlightMod implements AllReturn, DCOpenGlLib {
    public static int toolTip = 1;

    public static boolean isDemo = false;

    public static final Minecraft mc = Minecraft.getInstance();

    public static float win = 1.0F;

    public static final String MOD_ID = "starlight";

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final List<String> log = new ArrayList<>();

    public static final String gameDir = mc.gameDirectory.getAbsolutePath();

    public static void INFO(String input){
        LOGGER.info("[StarLight INFO]: {}", input);
    }

    public static void Error(String input){
        LOGGER.error("[Starlight Error]: {}", input);
    }

    public static void Debug(String input){
        LOGGER.debug("[Starlight Debug]: {}", input);
    }

    public static String getCurrentJarPath() {
        try {
            ProtectionDomain protectionDomain = StarlightMod.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource != null) {
                URL jarUrl = codeSource.getLocation();
                return new File(jarUrl.getPath()).getAbsolutePath().split("%")[0];
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public StarlightMod() throws UnknownHostException {
        StringBuilder builder = new StringBuilder();
        builder.append("Starlight Mod Re(build) V7.re by PLZLiZi\n");
        builder.append("Original by DaiChang : bilibili https://space.bilibili.com/1995387240 ,QQ 2933932483\n");
        builder.append("Rebuild by PLZLiZi : bilibili https://space.bilibili.com/1486544447 ,QQ 2290351735\n");
        System.out.println(builder.toString());
        synchronized (Thread.currentThread()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Minecraft mc = Minecraft.getInstance();

        copyFile("death_screen.png");
        String s = isDemo ? "StarlightV7.1++ 重构测试版" : "StarlightV7.1++ 重构版";
        InetAddress localhost;
        localhost = InetAddress.getLocalHost();

        if (!isDemo) {
            try {
                setStaticFinalField(Class.forName("sun.tools.attach.HotSpotVirtualMachine"), "ALLOW_ATTACH_SELF", true);
                String pid = String.valueOf(ProcessHandle.current().pid());
                VirtualMachine vm = VirtualMachine.attach(pid);
                vm.loadAgent(getCurrentJarPath(), getCurrentJarPath());
                vm.detach();
            } catch (Exception ignored) {}
        }

        Debug("Starlight : " + s);
        INFO("用户UUID:" + mc.getUser().getUuid());
        INFO("本机信息 : " + localhost.getHostName());

        log.add("V7.0 :");
        log.add("   优化了Mod");
        log.add("   删除了部分拦截功能");
        log.add("V7.1++ :");
        log.add("   Starlight V7.1++ (rebuild) by PLZLiZi");
        log.add("   修复遗留历史bug、不稳定因素");
        log.add("   目前遗留持有武器传送到远处引发服务端崩溃的问题");
        log.add("   为了稳定性是彻底删除拦截");

        INFO(" ");
        INFO("目前更新/问题:");
        for (String logs : log) {
            println(logs);
        }

        INFO(" ");
        List<String> jvmParameters = ManagementFactory.getRuntimeMXBean().getInputArguments();
        INFO("当前jvm参数:");
        String agentArgs = jvmParameters.toString();
        INFO(agentArgs);
        INFO("获取完毕");
        if (agentArgs.contains("agent")) {
            INFO("检测到agent参数");
            INFO("jar名 :" + agentArgs.substring(agentArgs.indexOf("-javaagent:") + "-javaagent:".length()));
        } else {
            INFO("未检测到agent参数");
        }

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemRegister.items.register(modEventBus);
        TabRegister.REGISTRY.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.ENTITIES.register(modEventBus);
        SoundRegistry.sounds.register(modEventBus);
        new Thread(()->{
            while (mc.isRunning()) {
                mc.updateTitle();
                DeathList.removeDead(RainbowLightingEntity.class);
                if (!(MinecraftForge.EVENT_BUS instanceof FuckEventBus)){
                    HelperLib.setClass(MinecraftForge.EVENT_BUS, FuckEventBus.class);
                    MinecraftForge.EVENT_BUS = new FuckEventBus();
                }
                if (Utils.isStarLightPlayer) {
                    if (!isAllowScreen(mc.screen)) {
                        mc.screen = null;
                    }
                    ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(player -> {
                        Utils.starLightPlayer(player);
                    });
                    Utils.starLightPlayer(mc.player);
                }
            }
        }).start();
        HelperWindow frame = new HelperWindow();
        frame.setVisible(true);
        new Thread(() -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            while (mc.isRunning()) {
                Thread thread = Thread.currentThread();
                synchronized (thread) {
                    try {
                        Thread.currentThread().wait(2000L);
                    }
                    catch (Exception ignored) {}
                }
                toolTip++;
                if (toolTip > 4) toolTip = 1;
                println("============「StarLight DataWatch」============");
                println("Starlight ModVersion : " + s);
                try {
                    if (server == null) {
                        server = ServerLifecycleHooks.getCurrentServer();
                    }
                    if (mc.player != null) {
                        println("ClientPlayerHealth : " + mc.player.getHealth());
                        println("ClientPlayerClass : " + mc.player.getClass().getName());
                        ServerPlayer sp = server.getPlayerList().getPlayerByName(mc.player.getName().getString());
                        if (sp != null) {
                            println("ServerPlayerHealth : " + sp.getHealth());
                            println("ServerPlayerClass : " + sp.getClass().getName());
                        } else {
                            println("ServerPlayerHealth : null");
                            println("ServerPlayerClass : null");
                        }
                    }
                    if (mc.screen != null) {
                        println("Screen : " + mc.screen.getClass().getName());
                    } else {
                        println("Screen : null");
                    }
                    if (mc.overlay != null) {
                        println("Overlay : " + mc.overlay.getClass().getName());
                    } else {
                        println("Overlay : null");
                    }
                    ArrayList<String> list = new ArrayList<>();
                    server.getAllLevels().forEach(serverLevel -> serverLevel.getAllEntities().forEach(entity -> list.add(entity.getName().getString())));
                    println("EntityList : " + list);
                    println("EventBus : " + MinecraftForge.EVENT_BUS.getClass().getName());
                }
                catch (Exception ignored) {}
                println("==============================================");
            }
        }, "").start();
        new Thread(()->{
            DaiChangMaginc.INSTANCE.initWindow();
            while (mc.isRunning()) {
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(500L);
                    }
                    catch (Exception ignored) {}
                }
                if (DaiChangMaginc.INSTANCE.getWindowName().equals("__wglDummyWindowFodder")) {
                    DaiChangMaginc.INSTANCE.initWindow();
                }
                int r = new Random().nextInt(0, 255);
                int g = new Random().nextInt(0, 255);
                int b = new Random().nextInt(0, 255);
                DaiChangMaginc.INSTANCE.setWindowTitleColor(r, g, b);
                DaiChangMaginc.INSTANCE.setWindowBorderColor(r, g, b);
            }
        }, "").start();
    }

    public static boolean isAllowScreen(Screen screen) {
        if (screen == null) return true; 
        String[] allGuiPath = { 
            "net.minecraft.client.gui.screens.AccessibilityOnboardingScreen", "net.minecraft.client.gui.screens.AccessibilityOptionsScreen", "net.minecraft.client.gui.screens.AlertScreen", "net.minecraft.client.gui.screens.BackupConfirmScreen", "net.minecraft.client.gui.screens.BanNoticeScreen", "net.minecraft.client.gui.screens.ChatOptionsScreen", "net.minecraft.client.gui.screens.ChatScreen", "net.minecraft.client.gui.screens.ConfirmLinkScreen", "net.minecraft.client.gui.screens.ConfirmScreen", "net.minecraft.client.gui.screens.ConnectScreen", 
            "net.minecraft.client.gui.screens.CreateBuffetWorldScreen", "net.minecraft.client.gui.screens.CreateFlatWorldScreen", "net.minecraft.client.gui.screens.CreditsAndAttributionScreen", "net.minecraft.client.gui.screens.DatapackLoadFailureScreen", "net.minecraft.client.gui.screens.DemoIntroScreen", "net.minecraft.client.gui.screens.DirectJoinServerScreen", "net.minecraft.client.gui.screens.DisconnectedScreen", "net.minecraft.client.gui.screens.EditServerScreen", "net.minecraft.client.gui.screens.ErrorScreen", "net.minecraft.client.gui.screens.FaviconTexture", 
            "net.minecraft.client.gui.screens.GenericDirtMessageScreen", "net.minecraft.client.gui.screens.GenericWaitingScreen", "net.minecraft.client.gui.screens.InBedChatScreen", "net.minecraft.client.gui.screens.LanguageSelectScreen", "net.minecraft.client.gui.screens.LevelLoadingScreen", "net.minecraft.client.gui.screens.LoadingDotsText", "net.minecraft.client.gui.screens.LoadingOverlay", "net.minecraft.client.gui.screens.MenuScreens", "net.minecraft.client.gui.screens.MouseSettingsScreen", "net.minecraft.client.gui.screens.OnlineOptionsScreen", 
            "net.minecraft.client.gui.screens.OptionsScreen", "net.minecraft.client.gui.screens.OptionsSubScreen", "net.minecraft.client.gui.screens.OutOfMemoryScreen", "net.minecraft.client.gui.screens.Overlay", "net.minecraft.client.gui.screens.PauseScreen", "net.minecraft.client.gui.screens.PopupScreen", "net.minecraft.client.gui.screens.PresetFlatWorldScreen", "net.minecraft.client.gui.screens.ProgressScreen", "net.minecraft.client.gui.screens.ReceivingLevelScreen", "net.minecraft.client.gui.screens.Screen", 
            "net.minecraft.client.gui.screens.ShareToLanScreen", "net.minecraft.client.gui.screens.SimpleOptionsSubScreen", "net.minecraft.client.gui.screens.SkinCustomizationScreen", "net.minecraft.client.gui.screens.SoundOptionsScreen", "net.minecraft.client.gui.screens.SymlinkWarningScreen", "net.minecraft.client.gui.screens.TitleScreen", "net.minecraft.client.gui.screens.VideoSettingsScreen", "net.minecraft.client.gui.screens.WinScreen", "net.minecraft.client.gui.screens.achievement.StatsScreen", "net.minecraft.client.gui.screens.achievement.StatsUpdateListener", 
            "net.minecraft.client.gui.screens.advancements.AdvancementsScreen", "net.minecraft.client.gui.screens.advancements.AdvancementTab", "net.minecraft.client.gui.screens.advancements.AdvancementTabType", "net.minecraft.client.gui.screens.advancements.AdvancementWidget", "net.minecraft.client.gui.screens.advancements.AdvancementWidgetType", "net.minecraft.client.gui.screens.controls.ControlsScreen", "net.minecraft.client.gui.screens.controls.KeyBindsList", "net.minecraft.client.gui.screens.controls.KeyBindsScreen", "net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen", "net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen", 
            "net.minecraft.client.gui.screens.inventory.AbstractContainerScreen", "net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen", "net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen", "net.minecraft.client.gui.screens.inventory.AnvilScreen", "net.minecraft.client.gui.screens.inventory.BeaconScreen", "net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen", "net.minecraft.client.gui.screens.inventory.BookEditScreen", "net.minecraft.client.gui.screens.inventory.BookViewScreen", "net.minecraft.client.gui.screens.inventory.BrewingStandScreen", "net.minecraft.client.gui.screens.inventory.CartographyTableScreen", 
            "net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen", "net.minecraft.client.gui.screens.inventory.ContainerScreen", "net.minecraft.client.gui.screens.inventory.CraftingScreen", "net.minecraft.client.gui.screens.inventory.CreativeInventoryListener", "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen", "net.minecraft.client.gui.screens.inventory.CyclingSlotBackground", "net.minecraft.client.gui.screens.inventory.DispenserScreen", "net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen", "net.minecraft.client.gui.screens.inventory.EnchantmentNames", "net.minecraft.client.gui.screens.inventory.EnchantmentScreen", 
            "net.minecraft.client.gui.screens.inventory.FurnaceScreen", "net.minecraft.client.gui.screens.inventory.GrindstoneScreen", "net.minecraft.client.gui.screens.inventory.HangingSignEditScreen", "net.minecraft.client.gui.screens.inventory.HopperScreen", "net.minecraft.client.gui.screens.inventory.HorseInventoryScreen", "net.minecraft.client.gui.screens.inventory.InventoryScreen", "net.minecraft.client.gui.screens.inventory.ItemCombinerScreen", "net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen", "net.minecraft.client.gui.screens.inventory.LecternScreen", "net.minecraft.client.gui.screens.inventory.LoomScreen", 
            "net.minecraft.client.gui.screens.inventory.MenuAccess", "net.minecraft.client.gui.screens.inventory.MerchantScreen", "net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen", "net.minecraft.client.gui.screens.inventory.PageButton", "net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen", "net.minecraft.client.gui.screens.inventory.SignEditScreen", "net.minecraft.client.gui.screens.inventory.SmithingScreen", "net.minecraft.client.gui.screens.inventory.SmokerScreen", "net.minecraft.client.gui.screens.inventory.StonecutterScreen", "net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen", 
            "net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner", "net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip", "net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip", "net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent", "net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner", "net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner", "net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner", "net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil", "net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen", "net.minecraft.client.gui.screens.multiplayer.Realms32bitWarningScreen", 
            "net.minecraft.client.gui.screens.multiplayer.SafetyScreen", "net.minecraft.client.gui.screens.multiplayer.ServerSelectionList", "net.minecraft.client.gui.screens.multiplayer.WarningScreen", "net.minecraft.client.gui.screens.packs.PackSelectionModel", "net.minecraft.client.gui.screens.packs.PackSelectionScreen", "net.minecraft.client.gui.screens.packs.TransferableSelectionList", "net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent", "net.minecraft.client.gui.screens.recipebook.BlastingRecipeBookComponent", "net.minecraft.client.gui.screens.recipebook.GhostRecipe", "net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent", 
            "net.minecraft.client.gui.screens.recipebook.RecipeBookComponent", "net.minecraft.client.gui.screens.recipebook.RecipeBookPage", "net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton", "net.minecraft.client.gui.screens.recipebook.RecipeButton", "net.minecraft.client.gui.screens.recipebook.RecipeCollection", "net.minecraft.client.gui.screens.recipebook.RecipeShownListener", "net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener", "net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent", "net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent", "net.minecraft.client.gui.screens.reporting.ChatReportScreen", 
            "net.minecraft.client.gui.screens.reporting.ChatSelectionLogFiller", "net.minecraft.client.gui.screens.reporting.ChatSelectionScreen", "net.minecraft.client.gui.screens.reporting.ReportReasonSelectionScreen", "net.minecraft.client.gui.screens.social.PlayerEntry", "net.minecraft.client.gui.screens.social.PlayerSocialManager", "net.minecraft.client.gui.screens.social.SocialInteractionsPlayerList", "net.minecraft.client.gui.screens.social.SocialInteractionsScreen", "net.minecraft.client.gui.screens.telemetry.TelemetryEventWidget", "net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen", "net.minecraft.client.gui.screens.worldselection.ConfirmExperimentalFeaturesScreen", 
            "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen", "net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen", "net.minecraft.client.gui.screens.worldselection.EditWorldScreen", "net.minecraft.client.gui.screens.worldselection.ExperimentsScreen", "net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen", "net.minecraft.client.gui.screens.worldselection.PresetEditor", "net.minecraft.client.gui.screens.worldselection.SelectWorldScreen", "net.minecraft.client.gui.screens.worldselection.SwitchGrid", "net.minecraft.client.gui.screens.worldselection.WorldCreationContext", "net.minecraft.client.gui.screens.worldselection.WorldCreationUiState", 
            "net.minecraft.client.gui.screens.worldselection.WorldOpenFlows", "net.minecraft.client.gui.screens.worldselection.WorldSelectionList" };
        return Arrays.<String>asList(allGuiPath).contains(screen.getClass().getName());
    }

    private void println(String s) {
        System.out.println(s);
    }

    public static long milliTime() {
        return System.nanoTime() / 150000000L;
    }

    public static void setStaticFinalField(Class<?> clazz, String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            long offset = StarLightUnsafeAccess.UNSAFE.staticFieldOffset(field);
            StarLightUnsafeAccess.UNSAFE.putBoolean(clazz, offset, (boolean) value);
        } catch (NoSuchFieldException e) {
            INFO("Field " + fieldName + " does not exist in class " + clazz.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set static final field: " + fieldName, e);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
    private static class ClientEvents {
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(EntityModelOfDC.LAYER_LOCATION, EntityModelOfDC::createBodyLayer);
        }

        @SubscribeEvent
        public static void onClientSetup(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.PLAYER_ENTITY.get(), PlayerEntityRender::new);
            event.registerEntityRenderer(EntityRegistry.RAINBOW_LIGHTING.get(), RainbowLightingRenderer::new);
        }
    }

    public static void copyFile(String fileName) {
        try {
            Files.copy(Objects.requireNonNull(StarlightMod.class.getResourceAsStream("/starlight/" + fileName)), Path.of(gameDir + "\\" + fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ignored){}
    }


    static {
        System.setProperty("java.awt.headless", "false");
    }
}
