package fluidlog;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fluidlog.api.FluidLogging;
import fluidlog.common.data.FluidLoggingChunkData;
import fluidlog.common.network.FluidlogNetwork;

public class FluidloggingManager {

    private static final Map<Chunk, FluidLoggingChunkData> CHUNKS_DATA = new WeakHashMap<>();

    @SideOnly(Side.CLIENT)
    public static FluidLoggingChunkData getChunkData(Chunk chunk) {
        return CHUNKS_DATA.get(chunk);
    }

    public static FluidLoggingChunkData getChunkFluidLoggingData(Chunk chunk) {
        var checkData = CHUNKS_DATA.get(chunk);

        if (checkData == null) {
            checkData = new FluidLoggingChunkData();
            CHUNKS_DATA.put(chunk, checkData);
        }

        return checkData;
    }

    public static FluidLoggingChunkData getChunkFluidLoggingData(World world, int x, int z) {
        var chunk = world.getChunkProvider()
            .provideChunk(x, z);
        return getChunkFluidLoggingData(chunk);
    }

    public static Fluid getFluid(World world, int x, int y, int z) {
        if (y < 0 || y >= 256) return null;

        var chunk = world.getChunkProvider()
            .provideChunk(x, z);
        return getChunkFluidLoggingData(chunk).getFluid(x & 15, y, z & 15);
    }

    public static boolean setFluid(World world, int x, int y, int z, Fluid fluid) {
        if (world.isRemote) return false;

        if (!FluidLogging.canFluidLog(world, x, y, z, fluid)) return false;

        var chunk = world.getChunkFromBlockCoords(x, z);

        var chunkFluidLoggingData = getChunkFluidLoggingData(chunk);
        chunkFluidLoggingData.setFluid(x & 15, y, z & 15, fluid);

        FluidlogNetwork.sendFluidUpdate(world.provider.dimensionId, x, y, z, fluid);

        return true;
    }

    public static void clearFluid(World world, int x, int y, int z) {
        if (world.isRemote) return;
        var chunk = world.getChunkFromBlockCoords(x, z);

        var chunkFluidLoggingData = getChunkFluidLoggingData(chunk);
        chunkFluidLoggingData.clearFluid(x & 15, y, z & 15);

        FluidlogNetwork.sendFluidUpdate(world.provider.dimensionId, x, y, z, null);
    }

    public static void unload(Chunk chunk) {
        CHUNKS_DATA.remove(chunk);
    }

}
