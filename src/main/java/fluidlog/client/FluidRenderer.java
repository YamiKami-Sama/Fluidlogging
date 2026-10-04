package fluidlog.client;

import java.util.Map;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fluidlog.FluidloggingManager;

@SideOnly(Side.CLIENT)
public class FluidRenderer {

    @SubscribeEvent
    public void render(RenderWorldEvent.Post event) {
        if (event.pass != 1) return;
        Chunk chunk = null;
        var chunkData = FluidloggingManager.getChunkData(chunk);
        if (chunkData == null || chunkData.isChunkEmpty()) return;

        for (Map.Entry<Integer, Fluid> entry : chunkData.getFluids()
            .entrySet()) {
            int packedCoord = entry.getKey();
            var fluid = entry.getValue();

            // todo render visible fluidlogged positions

            // unpack position
            int localX = packedCoord & 0xF;
            int localY = (packedCoord >> 8) & 0xFF;
            int localZ = (packedCoord >> 4) & 0xF;

            renderFluid(chunk, localX, localY, localZ, fluid);
        }

    }

    private void renderFluid(Chunk chunk, int x, int y, int z, Fluid fluid) {

    }

}
