package fluidlog;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import fluidlog.api.AbstractFluidLoggingManager;
import fluidlog.common.BlockLoggingRegisterer;
import fluidlog.common.command.FluidLoggingCommand;
import fluidlog.common.event.FluidLoggingEventHandler;
import fluidlog.common.network.FluidlogNetwork;

public class CommonProxy {

    public static FluidLoggingEventHandler EVENT_HANDLER;

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        AbstractFluidLoggingManager.INSTANCE = new FluidLoggingManagerImpl();
        FluidlogNetwork.init();
        EVENT_HANDLER = new FluidLoggingEventHandler();
        MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
        FMLCommonHandler.instance()
            .bus()
            .register(EVENT_HANDLER);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        BlockLoggingRegisterer.init();
        // TODO conf ? maybe intermod commmunication
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new FluidLoggingCommand());
    }
}
