package fluidlog.common.network;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
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

    public static void sendBatchFluidUpdate(int dim, int x, int y, int z,
        List<FluidLoggingBatchUpdateMessage.FluidLoggingEntry> entries) {
        var chunkViewDistance = MinecraftServer.getServer()
            .getConfigurationManager()
            .getViewDistance();
        CHANNEL.sendToAllAround(
            new FluidLoggingBatchUpdateMessage(dim, entries),
            new NetworkRegistry.TargetPoint(dim, x, y, z, chunkViewDistance * 16));
    }

    public static void sendBatchFluidUpdate(int dim, EntityPlayerMP player,
        List<FluidLoggingBatchUpdateMessage.FluidLoggingEntry> entries) {
        CHANNEL.sendTo(new FluidLoggingBatchUpdateMessage(dim, entries), player);
    }

    public static void init() {
        CHANNEL.registerMessage(FluidLoggingMessageHandler.class, FluidLoggingUpdateMessage.class, 0, Side.CLIENT);
        CHANNEL.registerMessage(
            FluidLoggingBatchMessageHandler.class,
            FluidLoggingBatchUpdateMessage.class,
            1,
            Side.CLIENT);
    }
}
