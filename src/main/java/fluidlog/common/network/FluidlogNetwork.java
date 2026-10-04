package fluidlog.common.network;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FluidlogNetwork {

    private static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("fluidlog_update");

    @SideOnly(Side.SERVER)
    public static void sendFluidUpdate(int dim, int x, int y, int z, Fluid fluid) {
        var viewDistance = MinecraftServer.getServer()
            .getConfigurationManager()
            .getViewDistance();
        CHANNEL.sendToAllAround(
            new FluidLoggingUpdateMessage(dim, x, y, z, fluid),
            new NetworkRegistry.TargetPoint(dim, x, y, z, viewDistance));
    }

    public static void init() {
        CHANNEL.registerMessage(FluidLoggingMessageHandler.class, FluidLoggingUpdateMessage.class, 0, Side.CLIENT);
    }
}
