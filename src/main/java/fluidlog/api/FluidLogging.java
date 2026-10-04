package fluidlog.api;

import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import fluidlog.FluidloggingManager;
import fluidlog.common.registry.FluidloggingRegistry;

public class FluidLogging {

    public static boolean isFluidLogged(World world, int x, int y, int z) {
        return FluidloggingManager.getFluid(world, x, y, z) != null;
    }

    /**
     * Gets the fluid contained at this position.
     *
     * Returns null if the block isn't fluidlogged.
     */
    public static Fluid getFluid(World world, int x, int y, int z) {
        return FluidloggingManager.getFluid(world, x, y, z);
    }

    /**
     * Sets the fluid contained at this position.
     *
     * Server side only.
     *
     * Returns false if the block is not allowed to contain the fluid.
     */
    public static boolean setFluid(World world, int x, int y, int z, Fluid fluid) {
        return FluidloggingManager.setFluid(world, x, y, z, fluid);
    }

    /**
     * Removes the fluid from this position.
     *
     * Returns true if success
     */
    public static boolean clearFluid(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        var meta = world.getBlockMetadata(x, y, z);
        if (!FluidloggingRegistry.getBlockIFluidLoggable(block)
            .canClearFluidLog(world, x, y, z, block, meta)) return false;
        FluidloggingManager.clearFluid(world, x, y, z);
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
