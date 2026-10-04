package fluidlog.api;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public interface IFluidLoggable {

    default boolean canFluidLog(World world, int x, int y, int z, Block block, int meta, Fluid fluid) {
        return fluid.canBePlacedInWorld();
    }

    default boolean canClearFluidLog(World world, int x, int y, int z, Block block, int meta) {
        return true;
    }

}
