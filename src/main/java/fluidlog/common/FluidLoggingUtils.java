package fluidlog.common;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import fluidlog.api.AbstractFluidLoggingManager;

public class FluidLoggingUtils {

    public static Block getFluidLoggedBlockOrBlock(IBlockAccess world, int x, int y, int z) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) {
            return fluid.getBlock();
        }

        return world.getBlock(x, y, z);
    }

    public static int getFluidLoggedBlockMetaOrBlockMeta(IBlockAccess world, int x, int y, int z) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) {
            return 0;
        }

        return world.getBlockMetadata(x, y, z);
    }

}
