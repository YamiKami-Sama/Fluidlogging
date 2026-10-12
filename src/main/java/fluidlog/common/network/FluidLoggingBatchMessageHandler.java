package fluidlog.common.network;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import fluidlog.api.AbstractFluidLoggingManager;

public class FluidLoggingBatchMessageHandler implements IMessageHandler<FluidLoggingBatchUpdateMessage, IMessage> {

    @Override
    public IMessage onMessage(FluidLoggingBatchUpdateMessage message, MessageContext ctx) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                var world = Minecraft.getMinecraft().theWorld;
                for (var fluidEntry : message.fluidEntries) {
                    if (world == null || world.provider.dimensionId != message.dim) return;

                    if (!world.getChunkProvider()
                        .chunkExists(fluidEntry.x & 15, fluidEntry.y & 15)) {
                        return;
                    }

                    var fluid = fluidEntry.getFluid();
                    AbstractFluidLoggingManager.INSTANCE
                        .setFluid(world, fluidEntry.x, fluidEntry.y, fluidEntry.z, fluid);
                }
            });

        return null;
    }
}
