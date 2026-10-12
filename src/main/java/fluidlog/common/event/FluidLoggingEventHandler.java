package fluidlog.common.event;

import static fluidlog.common.data.FluidLoggingChunkData.FLUIDS_DATA_TAG;

import java.util.stream.Collectors;

import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import fluidlog.FluidLoggingManagerImpl;
import fluidlog.api.AbstractFluidLoggingManager;
import fluidlog.common.network.FluidLoggingBatchUpdateMessage;
import fluidlog.common.network.FluidlogNetwork;

public class FluidLoggingEventHandler {

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        FluidLoggingManagerImpl.clearWorldFluid(event.world);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load event) {
        var root = event.getData();
        if (!root.hasKey(FLUIDS_DATA_TAG)) return;
        FluidLoggingManagerImpl.getOrCreateChunkFluidLoggingData(event.getChunk())
            .readFromNBT(root);
    }

    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save event) {
        var fluidChunkData = FluidLoggingManagerImpl.getChunkFluidLoggingData(event.getChunk());
        if (fluidChunkData == null) return;
        fluidChunkData.writeToNBT(event.getData());
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        FluidLoggingManagerImpl.unload(event.getChunk());
    }

    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event) {
        var chunk = event.chunk;
        var player = event.player;
        var world = event.player.worldObj;

        var data = FluidLoggingManagerImpl.getChunkFluidLoggingData(world, chunk.chunkXPos, chunk.chunkZPos);

        if (data != null) {
            var entryList = data.getFluids()
                .entrySet()
                .stream()
                .map((entry) -> {
                    var packed = entry.getKey();

                    int localX = packed & 0xF;
                    int localZ = (packed >> 4) & 0xF;

                    int x = (chunk.chunkXPos << 4) + localX;
                    int y = (packed >> 8) & 0xFF;
                    int z = (chunk.chunkZPos << 4) + localZ;

                    return new FluidLoggingBatchUpdateMessage.FluidLoggingEntry(
                        x,
                        y,
                        z,
                        entry.getValue()
                            .getName());
                })
                .collect(Collectors.toList());
            FluidlogNetwork.sendBatchFluidUpdate(world.provider.dimensionId, player, entryList);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }

        FluidLoggingManagerImpl.tickFluids(event.world);
    }

    @SubscribeEvent
    public void onBucketEvent(FillBucketEvent event) {
        var target = event.target;

        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return;
        }

        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;

        if (!event.world.canMineBlock(event.entityPlayer, x, y, z)) return;

        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(event.world, x, y, z);

        if (fluid == null) {
            placeFluid(event);
        } else {
            removeFluid(event, fluid);
        }
    }

    private void placeFluid(FillBucketEvent event) {
        var fluidStack = FluidContainerRegistry.getFluidForFilledItem(event.current);
        if (fluidStack == null) return;

        var target = event.target;

        if (!AbstractFluidLoggingManager.INSTANCE
            .canFluidLog(event.world, target.blockX, target.blockY, target.blockZ, fluidStack.getFluid())) {
            return;
        }

        AbstractFluidLoggingManager.INSTANCE
            .setFluid(event.world, target.blockX, target.blockY, target.blockZ, fluidStack.getFluid());

        event.result = FluidContainerRegistry.drainFluidContainer(event.current);
        event.setResult(Event.Result.ALLOW);
    }

    private void removeFluid(FillBucketEvent event, Fluid fluid) {
        var filledBucket = FluidContainerRegistry
            .fillFluidContainer(new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), event.current);

        if (filledBucket == null) return;

        var target = event.target;
        if (AbstractFluidLoggingManager.INSTANCE.clearFluid(event.world, target.blockX, target.blockY, target.blockZ)
            == null) return;

        event.result = filledBucket;
        event.setResult(Event.Result.ALLOW);
    }

}
