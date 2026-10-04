package fluidlog.common.network;

import java.nio.charset.StandardCharsets;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class FluidLoggingUpdateMessage implements IMessage {

    public int dim;
    public int x;
    public int y;
    public int z;

    public String fluidName;

    public FluidLoggingUpdateMessage() {}

    public FluidLoggingUpdateMessage(int dim, int x, int y, int z, Fluid fluid) {
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;

        this.fluidName = fluid == null ? "" : fluid.getName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();

        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        var bytes = new byte[buf.readShort()];

        buf.readBytes(bytes);
        if (bytes.length == 0) fluidName = null;
        else fluidName = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(dim);

        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);

        byte[] bytes = fluidName.getBytes(StandardCharsets.UTF_8);

        buffer.writeShort(bytes.length);
        buffer.writeBytes(bytes);
    }

    public Fluid getFluid() {
        if (fluidName == null) return null;
        return FluidRegistry.getFluid(fluidName);
    }
}
