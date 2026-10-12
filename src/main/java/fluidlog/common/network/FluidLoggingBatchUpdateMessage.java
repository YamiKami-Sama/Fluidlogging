package fluidlog.common.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class FluidLoggingBatchUpdateMessage implements IMessage {

    public int dim;
    public List<FluidLoggingEntry> fluidEntries;

    public FluidLoggingBatchUpdateMessage() {
        fluidEntries = new ArrayList<>();
    }

    public FluidLoggingBatchUpdateMessage(int dim, List<FluidLoggingEntry> fluidEntries) {
        this.dim = dim;
        this.fluidEntries = fluidEntries;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        var entriesSize = buf.readInt();
        fluidEntries = new ArrayList<>(entriesSize);

        for (var i = 0; i < entriesSize; i++) {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            var fluidName = ByteBufUtils.readUTF8String(buf);

            fluidEntries.add(new FluidLoggingEntry(x, y, z, fluidName));
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(dim);

        buffer.writeInt(fluidEntries.size());

        for (var entry : fluidEntries) {
            buffer.writeInt(entry.x);
            buffer.writeInt(entry.y);
            buffer.writeInt(entry.z);

            ByteBufUtils.writeUTF8String(buffer, entry.fluidName);
        }
    }

    public static class FluidLoggingEntry {

        public int x;
        public int y;
        public int z;
        public String fluidName;

        public FluidLoggingEntry(int x, int y, int z, String fluidName) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.fluidName = fluidName == null ? "" : fluidName;
        }

        public Fluid getFluid() {
            return fluidName.isEmpty() ? null : FluidRegistry.getFluid(fluidName);
        }
    }
}
