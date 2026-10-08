package fluidlog.common.event;

import static fluidlog.common.data.FluidLoggingChunkData.FLUIDS_DATA_TAG;

import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fluidlog.FluidLoggingManager;
import fluidlog.api.FluidLogging;

public class FluidLoggingEventHandler {

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event){
        FluidLoggingManager.clearFluid(event.world);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load event) {
        var root = event.getData();
        if (!root.hasKey(FLUIDS_DATA_TAG)) return;

        var fluidChunkData = FluidLoggingManager.getChunkFluidLoggingData(event.getChunk());
        if (fluidChunkData == null) return;
        fluidChunkData.readFromNBT(root);
    }

    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save event) {
        var fluidChunkData = FluidLoggingManager.getChunkFluidLoggingData(event.getChunk());
        if (fluidChunkData == null) return;
        fluidChunkData.writeToNBT(event.getData());
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        FluidLoggingManager.unload(event.getChunk());
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        var world = event.world;
        if (world.isRemote) return;

        var fluid = FluidLogging.getFluid(world, event.x, event.y, event.z);
        if (fluid == null) return;

        world.setBlock(event.x, event.y, event.z, fluid.getBlock(), 0, 3); // 1 (block update) + 2 (notify client) = 3
        FluidLoggingManager.clearFluid(world, event.x, event.y, event.z);
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

        var fluid = FluidLogging.getFluid(event.world, x, y, z);

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

        if( !FluidLogging.canFluidLog(event.world, target.blockX, target.blockY, target.blockZ, fluidStack.getFluid()) ){
            return;
        }

        if(!event.world.isRemote) FluidLogging.setFluid(event.world, target.blockX, target.blockY, target.blockZ, fluidStack.getFluid());

        event.result = FluidContainerRegistry.drainFluidContainer(event.current);
        event.setResult(Event.Result.ALLOW);
    }

    private void removeFluid(FillBucketEvent event, Fluid fluid) {
        var filledBucket = FluidContainerRegistry
            .fillFluidContainer(new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), event.current);

        if (filledBucket == null) return;

        var target = event.target;
        if(!event.world.isRemote){
            if (!FluidLogging.clearFluid(event.world, target.blockX, target.blockY, target.blockZ)) return;
        }

        event.result = filledBucket;
        event.setResult(Event.Result.ALLOW);
    }

}
