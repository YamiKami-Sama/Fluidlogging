package fluidlog.common.network;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import fluidlog.api.AbstractFluidLoggingManager;

public class FluidLoggingMessageHandler implements IMessageHandler<FluidLoggingUpdateMessage, IMessage> {

    @Override
    public IMessage onMessage(FluidLoggingUpdateMessage message, MessageContext ctx) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                var world = Minecraft.getMinecraft().theWorld;
                if (world == null || world.provider.dimensionId != message.dim) return;

                var fluid = message.getFluid();
                if (fluid == null) {
                    AbstractFluidLoggingManager.INSTANCE.clearFluidNoCheck(world, message.x, message.y, message.z);
                } else {
                    AbstractFluidLoggingManager.INSTANCE.setFluid(world, message.x, message.y, message.z, fluid);
                }
            });

        return null;
    }
}
