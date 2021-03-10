package com.fuzs.puzzleslib_ah;

import com.fuzs.puzzleslib_ah.capability.CapabilityController;
import com.fuzs.puzzleslib_ah.config.ConfigManager;
import com.fuzs.puzzleslib_ah.element.registry.ElementRegistry;
import com.fuzs.puzzleslib_ah.network.NetworkHandler;
import com.fuzs.puzzleslib_ah.proxy.IProxy;
import com.fuzs.puzzleslib_ah.registry.RegistryManager;
import com.fuzs.puzzleslib_ah.util.PuzzlesLibUtil;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
//@Mod(PuzzlesLib.MODID)
public class PuzzlesLib {

    public static final String MODID = "puzzleslib";
    public static final String NAME = "Puzzles Lib";
    public static final Logger LOGGER = LogManager.getLogger(PuzzlesLib.NAME);

    private static IProxy<?> sidedProxy;
    private static RegistryManager registryManager;
    private static NetworkHandler networkHandler;
    private static CapabilityController capabilityController;

    public PuzzlesLib() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(getRegistryManager()::onRegistryRegister);
    }

    protected void onCommonSetup(final FMLCommonSetupEvent evt) {

        ElementRegistry.load(evt);
        ConfigManager.get().syncAll(ModConfig.Type.COMMON);
    }

    protected void onClientSetup(final FMLClientSetupEvent evt) {

        ElementRegistry.load(evt);
        ConfigManager.get().syncAll(ModConfig.Type.CLIENT);
    }

    protected void onServerSetup(final FMLDedicatedServerSetupEvent evt) {

        ElementRegistry.load(evt);
        ConfigManager.get().syncAll(ModConfig.Type.SERVER);
    }

    /**
     * set mod to only be required on one side, server or client
     * works like <code>clientSideOnly</code> back in 1.12
     */
    protected final void setSideSideOnly() {

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    }

    /**
     * @return proxy for getting physical side specific objects
     */
    public static IProxy<?> getProxy() {

        return PuzzlesLibUtil.getOrElse(sidedProxy, IProxy::getProxy, instance -> sidedProxy = instance);
    }

    /**
     * @return registry manager for puzzles lib mods
     */
    public static RegistryManager getRegistryManager() {

        return PuzzlesLibUtil.getOrElse(registryManager, RegistryManager::new, instance -> registryManager = instance);
    }

    /**
     * @return network handler for puzzles lib mods
     */
    public static NetworkHandler getNetworkHandler() {

        return PuzzlesLibUtil.getOrElse(networkHandler, NetworkHandler::new, instance -> networkHandler = instance);
    }

    /**
     * @return capability controller for puzzles lib mods
     */
    public static CapabilityController getCapabilityController() {

        return PuzzlesLibUtil.getOrElse(capabilityController, CapabilityController::new, instance -> capabilityController = instance);
    }

}
