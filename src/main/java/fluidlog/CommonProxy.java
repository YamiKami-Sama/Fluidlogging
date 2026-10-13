package fluidlog;

import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import fluidlog.api.registry.FluidLoggingRegistry;
import fluidlog.common.command.FluidLoggingCommand;
import fluidlog.common.event.FluidLoggingEventHandler;
import fluidlog.common.network.FluidlogNetwork;

public class CommonProxy {

    public static FluidLoggingEventHandler EVENT_HANDLER;

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        FluidlogNetwork.init();
        EVENT_HANDLER = new FluidLoggingEventHandler();
        MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
        FMLCommonHandler.instance()
            .bus()
            .register(EVENT_HANDLER);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        FluidLoggingRegistry.register(BlockAnvil.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockStainedGlassPane.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockSkull.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockPane.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockBrewingStand.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockButton.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockCarpet.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockChest.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockDragonEgg.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockFence.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockFenceGate.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockHopper.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockDoor.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockEnchantmentTable.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockLadder.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockPistonMoving.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockRail.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockRailDetector.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockRailPowered.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockSign.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockSlab.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockStairs.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockTrapDoor.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        FluidLoggingRegistry.register(BlockLeaves.class, FluidLoggingRegistry.DEFAULT_LOGGABLE);
        // TODO conf ? maybe intermod commmunication
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new FluidLoggingCommand());
    }
}
