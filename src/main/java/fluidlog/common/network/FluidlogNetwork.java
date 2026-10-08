package fluidlog.common.network;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class FluidlogNetwork {

    private static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("fluidlog_update");

    public static void sendFluidUpdate(int dim, int x, int y, int z, Fluid fluid) {
        var chunkViewDistance = MinecraftServer.getServer()
            .getConfigurationManager()
            .getViewDistance();
        CHANNEL.sendToAllAround(
            new FluidLoggingUpdateMessage(dim, x, y, z, fluid),
            new NetworkRegistry.TargetPoint(dim, x, y, z, chunkViewDistance * 16));
    }

    public static void init() {
        CHANNEL.registerMessage(FluidLoggingMessageHandler.class, FluidLoggingUpdateMessage.class, 0, Side.CLIENT);
    }
}
