package fluidlog.client;

import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fluidlog.FluidLoggingManager;

@SideOnly(Side.CLIENT)
public class FluidRenderer {

    //@SubscribeEvent
    public void render(RenderWorldEvent.Pre event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || event.pass != 0) return;

        var renderer = event.renderer;

        int chunkX = renderer.posX >> 4;
        int chunkZ = renderer.posZ >> 4;

        var chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        var data = FluidLoggingManager.getChunkFluidLoggingData(chunk);
        if (data == null) return;

        var toRender = data.getFluids().entrySet().stream().filter(entry->{
            var fluid = entry.getValue();
            var block = fluid.getBlock();
            return block != null;
        }).collect(Collectors.toList());

        if (toRender.isEmpty()) return;

        var tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (Map.Entry<Integer, Fluid> entry : toRender) {
            int packedCoord = entry.getKey();

            int chunkBlockPosX = packedCoord & 0xF;
            int y = (packedCoord >> 8) & 0xFF;
            int chunkBlockPosZ = (packedCoord >> 4) & 0xF;

            int x = (chunkX << 4) + chunkBlockPosX;
            int z = (chunkZ << 4) + chunkBlockPosZ;

            event.renderBlocks.renderBlockLiquid(entry.getValue().getBlock(), x, y, z);
        }
        tessellator.draw();
    }

}
