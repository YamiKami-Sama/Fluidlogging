package fluidlog.api;

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
     *
     * Returns null if the block isn't fluidlogged.
     */
    public static Fluid getFluid(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingManager.getFluid(world, x, y, z);
    }

    /**
     * Sets the fluid contained at this position.
     *
     * Server side only.
     *
     * Returns false if the block is not allowed to contain the fluid.
     */
    public static boolean setFluid(IBlockAccess world, int x, int y, int z, Fluid fluid) {
        return FluidLoggingManager.setFluid(world, x, y, z, fluid);
    }

    /**
     * Removes the fluid from this position.
     *
     * Returns true if success
     */
    public static boolean clearFluid(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        var meta = world.getBlockMetadata(x, y, z);
        var fluidLoggable = FluidloggingRegistry.getBlockIFluidLoggable(block);
        if (fluidLoggable == null || !fluidLoggable.canClearFluidLog(world, x, y, z, block, meta)) return false;
        FluidLoggingManager.clearFluid(world, x, y, z);
        return true;
    }

    /**
     * Returns whether the block at this position can contain this fluid.
     */
    public static boolean canFluidLog(World world, int x, int y, int z, Fluid fluid) {
        var block = world.getBlock(x, y, z);
        var meta = world.getBlockMetadata(x, y, z);

        return FluidloggingRegistry.canFluidLog(world, x, y, z, block, meta, fluid);
    }
}
