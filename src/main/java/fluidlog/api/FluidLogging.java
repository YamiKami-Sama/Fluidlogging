package fluidlog.api;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import fluidlog.FluidLoggingManager;
import fluidlog.common.registry.FluidloggingRegistry;

public class FluidLogging {

    public static boolean isFluidLogged(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingManager.getFluid(world, x, y, z) != null;
    }

    /**
     * Gets the fluid contained at this position.
     * Returns null if the block isn't fluidlogged.
     */
    @Nullable
    public static Fluid getFluid(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingManager.getFluid(world, x, y, z);
    }

    /**
     * Sets the fluid contained at this position.
     * Returns false if the block is not allowed to contain the fluid or have already a fluid.
     */
    public static boolean setFluid(IBlockAccess world, int x, int y, int z, Fluid fluid) {
        return FluidLoggingManager.setFluid(world, x, y, z, fluid);
    }

    /**
     * Sets the fluid contained at this position.
     * Does not send update to players
     * 
     * @return false if the block is not allowed to contain the fluid or have already a fluid.
     */
    public static boolean setFluidNoUpdate(IBlockAccess world, int x, int y, int z, Fluid fluid) {
        return FluidLoggingManager.setFluid(world, x, y, z, fluid, false);
    }

    /**
     * Removes the fluid from this position.
     *
     * Returns Cleared fluid, null if nothing clear
     */
    @Nullable
    public static Fluid clearFluid(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        var meta = world.getBlockMetadata(x, y, z);
        var fluidLoggable = FluidloggingRegistry.getBlockIFluidLoggable(block);
        if (fluidLoggable == null || !fluidLoggable.canClearFluidLog(world, x, y, z, block, meta)) return null;
        return FluidLoggingManager.clearFluid(world, x, y, z);
    }

    /**
     * Returns whether the block at this position can contain this fluid.
     */
    public static boolean canFluidLog(World world, int x, int y, int z, Fluid fluid) {
        var block = world.getBlock(x, y, z);
        var meta = world.getBlockMetadata(x, y, z);

        return FluidloggingRegistry.canFluidLog(world, x, y, z, block, meta, fluid);
    }

    /**
     * Returns whether the block with meta can contain this fluid at the given position.
     */
    public static boolean canFluidLog(World world, int x, int y, int z, Block block, int meta, Fluid fluid) {
        return FluidloggingRegistry.canFluidLog(world, x, y, z, block, meta, fluid);
    }
}
