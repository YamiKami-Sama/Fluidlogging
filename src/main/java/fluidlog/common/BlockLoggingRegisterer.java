package fluidlog.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
import codechicken.multipart.TileMultipart;
import codechicken.translocator.BlockTranslocator;
import cpw.mods.fml.common.Loader;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import fluidlog.api.IBlockFlow;
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
            FluidLoggingRegistry.registerFluidLoggableBlock(BlockGenericPipe.class);
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

        if (Mods.ForgeMicroblocks.isModLoaded()) {
            registerForgeMicroblocks();
        }

        if (Loader.isModLoaded("gregtech")) {
            registerGregTech();
        }

        if (Mods.IndustrialCraft2.isModLoaded()) {
            registerIndustrialCraft2();
        }

        if (Mods.LogisticsPipes.isModLoaded()) {
            FluidLoggingRegistry.registerFluidLoggableBlock(LogisticsBlockGenericPipe.class);
        }

        if (Mods.Translocator.isModLoaded()) {
            FluidLoggingRegistry.registerFluidLoggableBlock(BlockTranslocator.class);
        }
    }

    private static void registerVanillaBlocks() {
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockAnvil.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockStainedGlassPane.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockSkull.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockPane.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockBrewingStand.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockButton.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpet.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockChest.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockDragonEgg.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockFence.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockFenceGate.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockDoor.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockEnchantmentTable.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockLadder.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockLever.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockPistonMoving.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockBasePressurePlate.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockRail.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockRailDetector.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockRailPowered.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockReed.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockSign.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockSlab.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockStairs.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockTrapDoor.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockLeaves.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockWall.class);
    }

    private static void registerAppliedEnergistics2() {
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCableBus.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCrank.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCharger.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(appeng.block.storage.BlockChest.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockLightDetector.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockQuantumBase.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockQuartzTorch.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockSkyChest.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockSkyCompass.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockTinyTNT.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockWireless.class);
    }

    private static void registerAE2FluidCrafting() {
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockWalrus.class);
    }

    private static void registerCarpentersBlocks() {
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersBarrier.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersButton.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersDoor.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersFlowerPot.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersGarageDoor.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersGate.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersHatch.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersLadder.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersPressurePlate.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersSlope.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCarpentersStairs.class);
    }

    private static void registerEnderIO() {
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockConduitBundle.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockEnchanter.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockEndermanSkull.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockObeliskAbstract.class);
    }

    private static void registerEtFuturum() {
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockBanner.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockChain.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCopperGrate.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockLantern.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockStonecutter.class);
    }

    private static void registerForgeMicroblocks() {
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockMultipart.class);
        FluidLoggingRegistry.registerCustomBlockFlow(BlockMultipart.class, new IBlockFlow() {

            @Override
            public boolean isControlledByTile() {
                return true;
            }

            public boolean isFlowRestrictedOnSide(World world, int x, int y, int z, ForgeDirection side) {
                if (world.getTileEntity(x, y, z) instanceof TileMultipart multipart) {
                    return multipart.isSolid(side.ordinal());
                }
                return false;
            }

            @Override
            public boolean[] getSidesFlowRestricted(World world, int x, int y, int z) {
                var restrictedFlow = new boolean[] { false, false, false, false, false, false };
                var tile = world.getTileEntity(x, y, z);
                if (tile instanceof TileMultipart multipart) {
                    if (multipart.isSolid(ForgeDirection.DOWN.ordinal())) restrictedFlow[0] = true;
                    if (multipart.isSolid(ForgeDirection.UP.ordinal())) restrictedFlow[1] = true;
                    if (multipart.isSolid(ForgeDirection.NORTH.ordinal())) restrictedFlow[2] = true;
                    if (multipart.isSolid(ForgeDirection.SOUTH.ordinal())) restrictedFlow[3] = true;
                    if (multipart.isSolid(ForgeDirection.WEST.ordinal())) restrictedFlow[4] = true;
                    if (multipart.isSolid(ForgeDirection.EAST.ordinal())) restrictedFlow[5] = true;
                }
                return restrictedFlow;
            }
        });
    }

    public static void registerGregTech() {
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockFrameBox.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockMachines.class, new IFluidLoggable() {

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
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockPoleFence.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockScaffold.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockRubberSheet.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockLuminator.class);
        FluidLoggingRegistry.registerFluidLoggableBlock(BlockCable.class, new IFluidLoggable() {

            @Override
            public boolean canFluidLog(World world, int x, int y, int z, Block block, int meta, Fluid fluid) {
                // not insulated are blacklisted, otherwise need to spread damage in water, but... i will not do that ^^
                return meta != 1 && meta != 2 && meta != 5 && meta != 10;
            }
        });
    }
}
