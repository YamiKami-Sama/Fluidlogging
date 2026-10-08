package fluidlog;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;

import fluidlog.api.FluidLogging;
import fluidlog.common.data.FluidLoggingChunkData;
import fluidlog.common.network.FluidlogNetwork;
import fluidlog.mixins.ChunkCacheAccessor;

import javax.annotation.Nullable;

public class FluidLoggingManager {
    private static final Map<Integer, Map<Integer, FluidLoggingChunkData>> CHUNKS_DATA = new HashMap<>();

    @Nullable
    public static FluidLoggingChunkData getChunkFluidLoggingData(Chunk chunk) {
        var dimId = chunk.worldObj.provider.dimensionId;
        if(!CHUNKS_DATA.containsKey(dimId)) return null;
        return CHUNKS_DATA.get(dimId).get(getChunkKey(chunk));
    }

    @Nullable
    public static FluidLoggingChunkData getChunkFluidLoggingData(int dimId, int chunkX, int chunkZ) {
        if(!CHUNKS_DATA.containsKey(dimId)) return null;
        return CHUNKS_DATA.get(dimId).get(getChunkKey(chunkX, chunkZ));
    }

    @Nullable
    public static Fluid getFluid(IBlockAccess blockAccess, int x, int y, int z) {
        if (y < 0 || y >= 256) return null;

        var world = getWorld(blockAccess);
        if(world == null) return null;
        var chunkFluidData = getChunkFluidLoggingData(world.provider.dimensionId, x >> 4, z >> 4);
        if (chunkFluidData == null) return null;
        return chunkFluidData.getFluid(x & 15, y, z & 15);
    }

    public static boolean setFluid(IBlockAccess blockAccess, int x, int y, int z, Fluid fluid) {
        var world = getWorld(blockAccess);
        if(world == null) return false;
        if (!world.isRemote && !FluidLogging.canFluidLog(world, x, y, z, fluid)) return false;

        var dimId = world.provider.dimensionId;
        var dimData = CHUNKS_DATA.computeIfAbsent(dimId, k -> new HashMap<>());
        var chunkKey = getChunkKey(x >> 4, z >> 4);
        var chunkFluidLoggingData = dimData.get(chunkKey);
        if (chunkFluidLoggingData == null) {
            chunkFluidLoggingData = new FluidLoggingChunkData();
            dimData.put(chunkKey, chunkFluidLoggingData);
        }
        var oldFluid = chunkFluidLoggingData.getFluid(x & 15, y, z & 15);
        if(!world.isRemote && oldFluid == fluid) return false;
        chunkFluidLoggingData.setFluid(x & 15, y, z & 15, fluid);

        world.markAndNotifyBlock(x, y, z, world.getChunkFromBlockCoords(x, z), oldFluid == null ? world.getBlock(x, y, z) : oldFluid.getBlock(), fluid.getBlock(), 3);

        if (!world.isRemote) FluidlogNetwork.sendFluidUpdate(world.provider.dimensionId, x, y, z, fluid);

        return true;
    }

    public static void clearFluid(IBlockAccess blockAccess){
        var world = getWorld(blockAccess);
        if(world == null) return;
        var dimId = world.provider.dimensionId;
        if(!CHUNKS_DATA.containsKey(dimId)) return;
        CHUNKS_DATA.remove(dimId);
    }

    public static void clearFluid(IBlockAccess blockAccess, int x, int y, int z) {
        var world = getWorld(blockAccess);
        if(world == null) return;
        var chunk = world.getChunkFromBlockCoords(x, z);

        var chunkFluidLoggingData = getChunkFluidLoggingData(chunk);
        if (chunkFluidLoggingData == null) return;
        var fluid = chunkFluidLoggingData.getFluid(x & 15, y, z & 15);
        if(!world.isRemote && fluid == null) return;
        chunkFluidLoggingData.clearFluid(x & 15, y, z & 15);
        var worldBlock = world.getBlock(x, y, z);
        world.markAndNotifyBlock(x, y, z, chunk, fluid == null ? worldBlock : fluid.getBlock(), worldBlock, 3);
        if (!world.isRemote) FluidlogNetwork.sendFluidUpdate(world.provider.dimensionId, x, y, z, null);
    }

    public static void unload(Chunk chunk) {
        var dimId = chunk.worldObj.provider.dimensionId;
        if(!CHUNKS_DATA.containsKey(dimId)) return;
        CHUNKS_DATA.get(dimId).remove(getChunkKey(chunk));
    }

    private static World getWorld(IBlockAccess blockAccess){
        if (blockAccess instanceof World world) {
            return world;
        }

        if (blockAccess instanceof ChunkCache) {
            return ((ChunkCacheAccessor) blockAccess).fluidlogging$getWorld();
        }

        return null;
    }

    private static int getChunkKey(Chunk chunk){
        return getChunkKey(chunk.xPosition, chunk.zPosition);
    }

    private static int getChunkKey(int x, int z){
        return x << 16 | z;
    }

}
