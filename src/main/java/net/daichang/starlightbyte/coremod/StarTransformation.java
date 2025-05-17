package net.daichang.starlightbyte.coremod;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.daichang.starlight.server.util.helper.HelperLib;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StarTransformation implements ITransformationService {
    static {
        LaunchPluginHandler handler = HelperLib.getFieldValue(Launcher.INSTANCE, "launchPlugins", LaunchPluginHandler.class);
        Map<String, ILaunchPluginService> plugins = (Map<String, ILaunchPluginService>) HelperLib.getFieldValue(handler, "plugins", Map.class);
        Map<String, ILaunchPluginService> newMap = new HashMap<>();
        newMap.put("!Starlight", new StarlightCore());
        if (plugins != null) for (String name : plugins.keySet())
            newMap.put(name, plugins.get(name));
        HelperLib.setFieldValue(handler, "plugins", newMap);
        HelperLib.coexistenceCoreAndMod();
    }

    @Override
    public @NotNull String name() {
        return "Starlight  TransformationService";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        return List.of();
    }
}
