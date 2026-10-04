package fluidlog.common.registry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import fluidlog.api.IFluidLoggable;

public class FluidloggingRegistry {

    private static final Map<Block, IFluidLoggable> BLOCK_HANDLERS = new HashMap<>();

    private FluidloggingRegistry() {}

    public static void register(Block block, IFluidLoggable handler) {
        BLOCK_HANDLERS.put(block, handler);
    }

    public static boolean canFluidLog(World world, int x, int y, int z, Block block, int meta, Fluid fluid) {
        var handler = BLOCK_HANDLERS.get(block);
        if (handler == null) return false;

        return handler.canFluidLog(world, x, y, z, block, meta, fluid);
    }

    public static IFluidLoggable getBlockIFluidLoggable(Block block) {
        return BLOCK_HANDLERS.get(block);
    }

}
