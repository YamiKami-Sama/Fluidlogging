package fluidlog.api;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;

public abstract class AbstractFluidLoggingManager {

    public static AbstractFluidLoggingManager INSTANCE;

    public abstract boolean isFluidLogged(IBlockAccess world, int x, int y, int z);

    /**
     * Gets the fluid contained at this position.
     * Returns null if the block isn't fluid logged.
     */
    @Nullable
    public abstract Fluid getFluid(IBlockAccess world, int x, int y, int z);

    /**
     * Sets the fluid contained at this position.
     * Returns false if the block is not allowed to contain the fluid or have already a fluid.
     */
    public abstract boolean setFluid(IBlockAccess world, int x, int y, int z, Fluid fluid);

    /**
     * Sets the fluid contained at this position.
     * Does not send update to players
     *
     * @return false if the block is not allowed to contain the fluid or have already a fluid.
     */
    public abstract boolean setFluidNoUpdate(IBlockAccess world, int x, int y, int z, Fluid fluid);

    /**
     * Removes the fluid from this position.
     *
     * @return the cleared fluid, null if nothing cleared
     */
    @Nullable
    public abstract Fluid clearFluid(IBlockAccess world, int x, int y, int z);

    /**
     * Removes the fluid from this position.
     * Does not check if the fluid can be removed
     *
     * @return the cleared fluid, null if nothing cleared
     */
    @Nullable
    public abstract Fluid clearFluidNoCheck(IBlockAccess world, int x, int y, int z);

    /**
     * @return true if the block at this position can contain this fluid.
     */
    public abstract boolean canFluidLog(IBlockAccess world, int x, int y, int z, Fluid fluid);

    /**
     * @return true if the block with meta can contain this fluid at the given position.
     */
    public abstract boolean canFluidLog(IBlockAccess world, int x, int y, int z, Block block, int meta, Fluid fluid);

}
