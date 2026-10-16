package fluidlog.api.registry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import fluidlog.Fluidlog;
import fluidlog.api.IBlockFlow;
import fluidlog.api.IFluidLoggable;

public class FluidLoggingRegistry {

    public static final IFluidLoggable DEFAULT_LOGGABLE = new IFluidLoggable() {};

    private static final Map<Class<? extends Block>, IFluidLoggable> BLOCK_HANDLERS = new HashMap<>();

    private static final Map<Class<? extends Block>, IBlockFlow> CUSTOM_FLOW_HANDLERS = new HashMap<>();

    private FluidLoggingRegistry() {}

    /**
     * Register a block to be fluid loggable with a custom handler
     *
     * @param blockClass
     * @param handler
     */
    public static void registerFluidLoggableBlock(Class<? extends Block> blockClass, IFluidLoggable handler) {
        if (BLOCK_HANDLERS.containsKey(blockClass))
            Fluidlog.LOGGER.warn("Registering an already registered fluid loggable block {}", blockClass);
        BLOCK_HANDLERS.put(blockClass, handler == null ? DEFAULT_LOGGABLE : handler);
    }

    /**
     * Register a block to be fluid loggable
     *
     * @param blockClass
     */
    public static void registerFluidLoggableBlock(Class<? extends Block> blockClass) {
        registerFluidLoggableBlock(blockClass, DEFAULT_LOGGABLE);
    }

    /**
     * Register a custom block flow mechanic for the fluid logged block,
     * If the block is full bound but must
     *
     * @param blockClass
     * @param flowController
     */
    public static void registerCustomBlockFlow(Class<? extends Block> blockClass, IBlockFlow flowController) {
        if (CUSTOM_FLOW_HANDLERS.containsKey(blockClass))
            Fluidlog.LOGGER.warn("Registering an already registered custom block flow {}", blockClass);
        CUSTOM_FLOW_HANDLERS.put(blockClass, flowController);
    }

    public static boolean canFluidLog(World world, int x, int y, int z, Block block, int meta, Fluid fluid) {
        if (block == null) return false;
        var handler = getLoggable(block);
        if (handler == null) return false;

        return handler.canFluidLog(world, x, y, z, block, meta, fluid);
    }

    @Nullable
    public static IFluidLoggable getBlockIFluidLoggable(Block block) {
        return getLoggable(block);
    }

    @Nullable
    private static IFluidLoggable getLoggable(Block block) {
        if (block == null) return null;

        Class<?> current = block.getClass();
        while (current != null && Block.class.isAssignableFrom(current)) {
            var loggable = BLOCK_HANDLERS.get(current);
            if (loggable != null) {
                // to prevent next superclass checks
                if (!current.equals(block.getClass())) BLOCK_HANDLERS.put((Class<? extends Block>) current, loggable);
                return loggable;
            }
            current = current.getSuperclass();
        }

        return null;
    }

    @Nullable
    public static IBlockFlow getCustomBlockFlow(Block block) {
        return CUSTOM_FLOW_HANDLERS.get(block.getClass());
    }

}
