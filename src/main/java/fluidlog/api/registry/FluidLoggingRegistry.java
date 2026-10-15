package fluidlog.api.registry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import fluidlog.Fluidlog;
import fluidlog.api.IFluidLoggable;

public class FluidLoggingRegistry {

    public static final IFluidLoggable DEFAULT_LOGGABLE = new IFluidLoggable() {};

    private static final Map<Class<? extends Block>, IFluidLoggable> BLOCK_HANDLERS = new HashMap<>();

    private FluidLoggingRegistry() {}

    public static void register(Class<? extends Block> blockClass, IFluidLoggable handler) {
        if (BLOCK_HANDLERS.containsKey(blockClass))
            Fluidlog.LOGGER.warn("Registering an already registered fluid loggable block {}", blockClass);
        BLOCK_HANDLERS.put(blockClass, handler == null ? DEFAULT_LOGGABLE : handler);
    }

    public static void register(Class<? extends Block> blockClass) {
        register(blockClass, DEFAULT_LOGGABLE);
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

}
