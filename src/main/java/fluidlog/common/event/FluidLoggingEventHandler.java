package fluidlog.common.event;

import static fluidlog.common.data.FluidLoggingChunkData.FLUIDS_DATA_TAG;

import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fluidlog.FluidloggingManager;
import fluidlog.api.FluidLogging;

public class FluidLoggingEventHandler {

    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load event) {
        var root = event.getData();
        if (!root.hasKey(FLUIDS_DATA_TAG)) return;

        var fluidChunkData = FluidloggingManager.getChunkFluidLoggingData(event.getChunk());
        fluidChunkData.readFromNBT(root);
    }

    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save event) {
        var chunkFluidLoggingData = FluidloggingManager.getChunkFluidLoggingData(event.getChunk());
        chunkFluidLoggingData.writeToNBT(event.getData());
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        FluidloggingManager.unload(event.getChunk());
    }

    @SubscribeEvent
    public void onBucketEvent(FillBucketEvent event) {
        if (event.world.isRemote) {
            return;
        }

        var target = event.target;

        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return;
        }

        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;

        var fluid = FluidLogging.getFluid(event.world, x, y, z);

        if (fluid == null) {
            return;
        }

        if (!event.world.canMineBlock(event.entityPlayer, x, y, z)) return;

        var filledBucket = FluidContainerRegistry
            .fillFluidContainer(new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), event.current);

        if (filledBucket == null) return;
        if (!FluidLogging.clearFluid(event.world, x, y, z)) return;

        event.result = filledBucket;
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        var world = event.world;
        if (world.isRemote) return;

        var fluid = FluidLogging.getFluid(world, event.x, event.y, event.z);
        if (fluid == null) return;

        world.setBlock(event.x, event.y, event.z, fluid.getBlock(), 0, 3); // 1 (block update) + 2 (notify client) = 3
        FluidloggingManager.clearFluid(world, event.x, event.y, event.z);
    }

}
