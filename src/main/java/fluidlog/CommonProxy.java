package fluidlog;

import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPane;
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

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import fluidlog.common.command.FluidLoggingCommand;
import fluidlog.common.event.FluidLoggingEventHandler;
import fluidlog.common.network.FluidlogNetwork;
import fluidlog.common.registry.FluidloggingRegistry;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        FluidlogNetwork.init();
        MinecraftForge.EVENT_BUS.register(new FluidLoggingEventHandler());
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        FluidloggingRegistry.register(BlockAnvil.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockStainedGlassPane.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockDispenser.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockSkull.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockPane.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockBrewingStand.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockButton.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockCarpet.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockChest.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockDragonEgg.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockFence.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockFenceGate.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockHopper.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockDoor.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockEnchantmentTable.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockLadder.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockRail.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockRailDetector.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockRailPowered.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockSign.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockSlab.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockStairs.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockTrapDoor.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
        FluidloggingRegistry.register(BlockLeaves.class, FluidloggingRegistry.DEFAULT_LOGGABLE);
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new FluidLoggingCommand());
    }
}
