package fluidlog.common;

import net.minecraft.block.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import com.carpentersblocks.block.BlockCarpentersBarrier;
import com.carpentersblocks.block.BlockCarpentersButton;
import com.carpentersblocks.block.BlockCarpentersDoor;
import com.carpentersblocks.block.BlockCarpentersFlowerPot;
import com.carpentersblocks.block.BlockCarpentersGarageDoor;
import com.carpentersblocks.block.BlockCarpentersGate;
import com.carpentersblocks.block.BlockCarpentersHatch;
import com.carpentersblocks.block.BlockCarpentersLadder;
import com.carpentersblocks.block.BlockCarpentersPressurePlate;
import com.carpentersblocks.block.BlockCarpentersSlope;
import com.carpentersblocks.block.BlockCarpentersStairs;
import com.glodblock.github.common.block.BlockWalrus;

import appeng.block.grindstone.BlockCrank;
import appeng.block.misc.BlockCharger;
import appeng.block.misc.BlockLightDetector;
import appeng.block.misc.BlockQuartzTorch;
import appeng.block.misc.BlockSkyCompass;
import appeng.block.misc.BlockTinyTNT;
import appeng.block.networking.BlockCableBus;
import appeng.block.networking.BlockWireless;
import appeng.block.qnb.BlockQuantumBase;
import appeng.block.storage.BlockSkyChest;
import buildcraft.transport.BlockGenericPipe;
import codechicken.multipart.BlockMultipart;
import codechicken.translocator.BlockTranslocator;
import cpw.mods.fml.common.Loader;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import fluidlog.api.IFluidLoggable;
import fluidlog.api.registry.FluidLoggingRegistry;
import ganymedes01.etfuturum.blocks.BlockBanner;
import ganymedes01.etfuturum.blocks.BlockChain;
import ganymedes01.etfuturum.blocks.BlockCopperGrate;
import ganymedes01.etfuturum.blocks.BlockLantern;
import ganymedes01.etfuturum.blocks.BlockStonecutter;
import gregtech.api.enums.Mods;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.common.blocks.BlockFrameBox;
import gregtech.common.blocks.BlockMachines;
import ic2.core.block.BlockPoleFence;
import ic2.core.block.BlockRubberSheet;
import ic2.core.block.BlockScaffold;
import ic2.core.block.wiring.BlockCable;
import ic2.core.block.wiring.BlockLuminator;
import logisticspipes.pipes.basic.LogisticsBlockGenericPipe;

public class BlockLoggingRegisterer {

    public static void init() {
        registerVanillaBlocks();

        if (Mods.AppliedEnergistics2.isModLoaded()) {
            registerAppliedEnergistics2();
        }

        if (Mods.AE2FluidCraft.isModLoaded()) {
            registerAE2FluidCrafting();
        }

        if (Mods.BuildCraftTransport.isModLoaded()) {
            FluidLoggingRegistry.register(BlockGenericPipe.class);
        }

        if (Mods.CarpentersBlocks.isModLoaded()) {
            registerCarpentersBlocks();
        }

        if (Mods.EnderIO.isModLoaded()) {
            registerEnderIO();
        }

        if (Mods.EtFuturumRequiem.isModLoaded()) {
            registerEtFuturum();
        }

        if (Loader.isModLoaded("gregtech")) {
            registerGregTech();
        }

        if (Mods.IndustrialCraft2.isModLoaded()) {
            registerIndustrialCraft2();
        }

        if (Mods.LogisticsPipes.isModLoaded()) {
            FluidLoggingRegistry.register(LogisticsBlockGenericPipe.class);
        }

        if (Mods.Translocator.isModLoaded()) {
            FluidLoggingRegistry.register(BlockTranslocator.class);
        }

        if (Mods.WirelessRedstoneCBELogic.isModLoaded()) {
            FluidLoggingRegistry.register(BlockMultipart.class);
        }
    }

    private static void registerVanillaBlocks() {
        FluidLoggingRegistry.register(BlockAnvil.class);
        FluidLoggingRegistry.register(BlockStainedGlassPane.class);
        FluidLoggingRegistry.register(BlockSkull.class);
        FluidLoggingRegistry.register(BlockPane.class);
        FluidLoggingRegistry.register(BlockBrewingStand.class);
        FluidLoggingRegistry.register(BlockButton.class);
        FluidLoggingRegistry.register(BlockCarpet.class);
        FluidLoggingRegistry.register(BlockChest.class);
        FluidLoggingRegistry.register(BlockDragonEgg.class);
        FluidLoggingRegistry.register(BlockFence.class);
        FluidLoggingRegistry.register(BlockFenceGate.class);
        FluidLoggingRegistry.register(BlockDoor.class);
        FluidLoggingRegistry.register(BlockEnchantmentTable.class);
        FluidLoggingRegistry.register(BlockLadder.class);
        FluidLoggingRegistry.register(BlockLever.class);
        FluidLoggingRegistry.register(BlockPistonMoving.class);
        FluidLoggingRegistry.register(BlockBasePressurePlate.class);
        FluidLoggingRegistry.register(BlockRail.class);
        FluidLoggingRegistry.register(BlockRailDetector.class);
        FluidLoggingRegistry.register(BlockRailPowered.class);
        FluidLoggingRegistry.register(BlockReed.class);
        FluidLoggingRegistry.register(BlockSign.class);
        FluidLoggingRegistry.register(BlockSlab.class);
        FluidLoggingRegistry.register(BlockStairs.class);
        FluidLoggingRegistry.register(BlockTrapDoor.class);
        FluidLoggingRegistry.register(BlockLeaves.class);
        FluidLoggingRegistry.register(BlockWall.class);
    }

    private static void registerAppliedEnergistics2() {
        FluidLoggingRegistry.register(BlockCableBus.class);
        FluidLoggingRegistry.register(BlockCrank.class);
        FluidLoggingRegistry.register(BlockCharger.class);
        FluidLoggingRegistry.register(appeng.block.storage.BlockChest.class);
        FluidLoggingRegistry.register(BlockLightDetector.class);
        FluidLoggingRegistry.register(BlockQuantumBase.class);
        FluidLoggingRegistry.register(BlockQuartzTorch.class);
        FluidLoggingRegistry.register(BlockSkyChest.class);
        FluidLoggingRegistry.register(BlockSkyCompass.class);
        FluidLoggingRegistry.register(BlockTinyTNT.class);
        FluidLoggingRegistry.register(BlockWireless.class);
    }

    private static void registerAE2FluidCrafting() {
        FluidLoggingRegistry.register(BlockWalrus.class);
    }

    private static void registerCarpentersBlocks() {
        FluidLoggingRegistry.register(BlockCarpentersBarrier.class);
        FluidLoggingRegistry.register(BlockCarpentersButton.class);
        FluidLoggingRegistry.register(BlockCarpentersDoor.class);
        FluidLoggingRegistry.register(BlockCarpentersFlowerPot.class);
        FluidLoggingRegistry.register(BlockCarpentersGarageDoor.class);
        FluidLoggingRegistry.register(BlockCarpentersGate.class);
        FluidLoggingRegistry.register(BlockCarpentersHatch.class);
        FluidLoggingRegistry.register(BlockCarpentersLadder.class);
        FluidLoggingRegistry.register(BlockCarpentersPressurePlate.class);
        FluidLoggingRegistry.register(BlockCarpentersSlope.class);
        FluidLoggingRegistry.register(BlockCarpentersStairs.class);
    }

    private static void registerEnderIO(){
        FluidLoggingRegistry.register(BlockConduitBundle.class);
        FluidLoggingRegistry.register(BlockEnchanter.class);
        FluidLoggingRegistry.register(BlockEndermanSkull.class);
        FluidLoggingRegistry.register(BlockObeliskAbstract.class);
    }

    private static void registerEtFuturum() {
        FluidLoggingRegistry.register(BlockBanner.class);
        FluidLoggingRegistry.register(BlockChain.class);
        FluidLoggingRegistry.register(BlockCopperGrate.class);
        FluidLoggingRegistry.register(BlockLantern.class);
        FluidLoggingRegistry.register(BlockStonecutter.class);
    }

    public static void registerGregTech() {
        FluidLoggingRegistry.register(BlockFrameBox.class);
        FluidLoggingRegistry.register(BlockMachines.class, new IFluidLoggable() {

            @Override
            public boolean canFluidLog(World world, int x, int y, int z, Block block, int meta, Fluid fluid) {
                if (meta == 7 || meta == 9) return true;
                var te = world.getTileEntity(x, y, z);
                if (te == null) return false;
                if (te instanceof BaseMetaPipeEntity pipe) {
                    if (!pipe.canAccessData()) return true; // by default true, to not void the fluid
                    return pipe.getThickNess() < 1;
                }
                return false;
            }
        });
    }

    public static void registerIndustrialCraft2() {
        FluidLoggingRegistry.register(BlockPoleFence.class);
        FluidLoggingRegistry.register(BlockScaffold.class);
        FluidLoggingRegistry.register(BlockRubberSheet.class);
        FluidLoggingRegistry.register(BlockLuminator.class);
        FluidLoggingRegistry.register(BlockCable.class, new IFluidLoggable() {

            @Override
            public boolean canFluidLog(World world, int x, int y, int z, Block block, int meta, Fluid fluid) {
                // not insulated are blacklisted, otherwise need to spread damage in water, but... i will not do that ^^
                return meta != 1 && meta != 2 && meta != 5 && meta != 10;
            }
        });
    }
}
