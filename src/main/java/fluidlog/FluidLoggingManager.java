package fluidlog;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import fluidlog.api.FluidLogging;
import fluidlog.common.data.FluidLoggingChunkData;
import fluidlog.common.network.FluidlogNetwork;
import fluidlog.mixins.early.ChunkCacheAccessor;

public class FluidLoggingManager {

    private static final Map<Integer, Map<Integer, FluidLoggingChunkData>> CHUNKS_DATA = new HashMap<>();
    private static final Map<Integer, Map<Integer, FluidLoggingChunkData>> CLIENT_CHUNKS_DATA = new HashMap<>();

    @Nonnull
    public static FluidLoggingChunkData getOrCreateChunkFluidLoggingData(Chunk chunk) {
        var dimId = chunk.worldObj.provider.dimensionId;
        return getChunkData(chunk.worldObj.isRemote).computeIfAbsent(dimId, (k) -> new HashMap<>())
            .computeIfAbsent(getChunkKey(chunk), (k) -> new FluidLoggingChunkData());
    }

    @Nullable
    public static FluidLoggingChunkData getChunkFluidLoggingData(Chunk chunk) {
        var dimId = chunk.worldObj.provider.dimensionId;
        var chunkData = getChunkData(chunk.worldObj.isRemote);
        if (!chunkData.containsKey(dimId)) return null;
        return chunkData.get(dimId)
            .get(getChunkKey(chunk));
    }

    @Nullable
    public static FluidLoggingChunkData getChunkFluidLoggingData(World world, int chunkX, int chunkZ) {
        var dimId = world.provider.dimensionId;
        var chunkData = getChunkData(world.isRemote);
        if (!chunkData.containsKey(dimId)) return null;
        return chunkData.get(dimId)
            .get(getChunkKey(chunkX, chunkZ));
    }

    @Nullable
    public static Fluid getChunkFluid(Chunk chunk, int x, int y, int z) {
        var chunkData = getChunkFluidLoggingData(chunk);
        if (chunkData == null) return null;
        return chunkData.getFluid(x, y, z);
    }

    @Nullable
    public static Fluid getFluid(IBlockAccess blockAccess, int x, int y, int z) {
        if (y < 0 || y >= 256) return null;

        var world = getWorld(blockAccess);
        if (world == null) return null;
        var chunkFluidData = getChunkFluidLoggingData(world, x >> 4, z >> 4);
        if (chunkFluidData == null) return null;
        return chunkFluidData.getFluid(x & 15, y, z & 15);
    }

    public static boolean setFluid(IBlockAccess blockAccess, int x, int y, int z, Fluid fluid) {
        return setFluid(blockAccess, x, y, z, fluid, true);
    }

    public static boolean setFluid(IBlockAccess blockAccess, int x, int y, int z, Fluid fluid, boolean sendUpdate) {
        if (fluid == null) return clearFluid(blockAccess, x, y, z, sendUpdate) != null;
        var world = getWorld(blockAccess);
        if (world == null) return false;
        if (!world.isRemote && !FluidLogging.canFluidLog(world, x, y, z, fluid)) return false;

        var dimId = world.provider.dimensionId;
        var dimData = getChunkData(world.isRemote).computeIfAbsent(dimId, k -> new HashMap<>());
        var chunkKey = getChunkKey(x >> 4, z >> 4);
        var chunkFluidLoggingData = dimData.get(chunkKey);
        if (chunkFluidLoggingData == null) {
            chunkFluidLoggingData = new FluidLoggingChunkData();
            dimData.put(chunkKey, chunkFluidLoggingData);
        }
        var oldFluid = chunkFluidLoggingData.getFluid(x & 15, y, z & 15);
        if (!world.isRemote && oldFluid == fluid) return false;

        var evaporateWater = world.provider.isHellWorld && fluid == FluidRegistry.WATER;

        if (evaporateWater) {
            world.playSoundEffect(
                (float) x + 0.5F,
                (float) y + 0.5F,
                (float) z + 0.5F,
                "random.fizz",
                0.5F,
                2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

            for (int l = 0; l < 8; ++l) {
                world.spawnParticle(
                    "largesmoke",
                    (double) x + Math.random(),
                    (double) y + Math.random(),
                    (double) z + Math.random(),
                    0.0D,
                    0.0D,
                    0.0D);
            }
        } else {
            chunkFluidLoggingData.setFluid(x & 15, y, z & 15, fluid);
        }

        if (!evaporateWater) world.markAndNotifyBlock(
            x,
            y,
            z,
            world.getChunkFromBlockCoords(x, z),
            oldFluid == null ? world.getBlock(x, y, z) : oldFluid.getBlock(),
            fluid.getBlock(),
            3);

        if (!world.isRemote) {
            if (sendUpdate) FluidlogNetwork.sendFluidUpdate(world.provider.dimensionId, x, y, z, fluid);
        }

        return true;
    }

    public static void clearFluid(IBlockAccess blockAccess) {
        var world = getWorld(blockAccess);
        if (world == null) return;
        var dimId = world.provider.dimensionId;
        var chunkData = getChunkData(world.isRemote);
        if (!chunkData.containsKey(dimId)) return;
        chunkData.remove(dimId);
    }

    public static Fluid clearFluid(IBlockAccess blockAccess, int x, int y, int z) {
        return clearFluid(blockAccess, x, y, z, true);
    }

    public static Fluid clearFluid(IBlockAccess blockAccess, int x, int y, int z, boolean sendUpdate) {
        var world = getWorld(blockAccess);
        if (world == null) return null;
        var chunk = world.getChunkFromBlockCoords(x, z);

        var chunkFluidLoggingData = getChunkFluidLoggingData(chunk);
        if (chunkFluidLoggingData == null) return null;
        var fluid = chunkFluidLoggingData.getFluid(x & 15, y, z & 15);
        if (!world.isRemote && fluid == null) return null;
        chunkFluidLoggingData.clearFluid(x & 15, y, z & 15);
        var worldBlock = world.getBlock(x, y, z);
        if (!world.isRemote) {
            world.markAndNotifyBlock(x, y, z, chunk, fluid == null ? worldBlock : fluid.getBlock(), worldBlock, 3);
            if (sendUpdate) FluidlogNetwork.sendFluidUpdate(world.provider.dimensionId, x, y, z, null);
        }
        return fluid;
    }

    public static void unload(Chunk chunk) {
        var dimId = chunk.worldObj.provider.dimensionId;
        var chunkData = getChunkData(chunk.worldObj.isRemote);
        if (!chunkData.containsKey(dimId)) return;
        chunkData.get(dimId)
            .remove(getChunkKey(chunk));
    }

    private static World getWorld(IBlockAccess blockAccess) {
        if (blockAccess instanceof World world) {
            return world;
        }

        if (blockAccess instanceof ChunkCache) {
            return ((ChunkCacheAccessor) blockAccess).fluidlogging$getWorld();
        }

        return null;
    }

    private static int getChunkKey(Chunk chunk) {
        return getChunkKey(chunk.xPosition, chunk.zPosition);
    }

    private static int getChunkKey(int x, int z) {
        return (x << 16) | (z & 0xFFFF);
    }

    private static Map<Integer, Map<Integer, FluidLoggingChunkData>> getChunkData(boolean isRemote) {
        return isRemote ? CLIENT_CHUNKS_DATA : CHUNKS_DATA;
    }

    public static void tickFluids(World world) {
        var dimensionData = getChunkData(world.isRemote).get(world.provider.dimensionId);

        if (dimensionData == null) {
            return;
        }

        for (var chunkEntry : dimensionData.entrySet()) {
            if (chunkEntry == null) continue;
            var chunkData = chunkEntry.getValue();
            int chunkKey = chunkEntry.getKey();

            int chunkX = chunkKey >> 16;
            int chunkZ = (short) chunkKey;

            for (Map.Entry<Integer, Fluid> entry : chunkData.getFluids()
                .entrySet()) {
                int packed = entry.getKey();

                int localX = packed & 0xF;
                int localZ = (packed >> 4) & 0xF;
                int y = (packed >> 8) & 0xFF;

                int x = (chunkX << 4) + localX;
                int z = (chunkZ << 4) + localZ;

                var fluid = entry.getValue();
                var fluidBlock = fluid.getBlock();

                if (fluidBlock != null) {
                    int tickRate = fluidBlock.tickRate(world);
                    if (tickRate == 0 || MinecraftServer.getServer()
                        .getTickCounter() % tickRate == 0) {
                        fluidBlock.updateTick(world, x, y, z, world.rand);
                        if (fluid == FluidRegistry.WATER) {
                            Blocks.flowing_water.updateTick(world, x, y, z, world.rand);
                        } else if (fluid == FluidRegistry.LAVA) {
                            Blocks.flowing_lava.updateTick(world, x, y, z, world.rand);
                        }
                    }

                }
            }
        }
    }
}
