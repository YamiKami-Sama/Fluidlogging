package fluidlog.common.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import fluidlog.Fluidlog;

public class FluidLoggingChunkData {

    public static final String FLUIDS_DATA_TAG = "fl_data";

    private final Map<Integer, Fluid> fluids = new ConcurrentHashMap<>();

    private static int toKey(int x, int y, int z) {
        return (x & 15) | ((z & 15) << 4) | ((y & 255) << 8);
    }

    public boolean isChunkEmpty() {
        return fluids.isEmpty();
    }

    public Map<Integer, Fluid> getFluids() {
        return fluids;
    }

    public Fluid getFluid(int x, int y, int z) {
        return fluids.get(toKey(x, y, z));
    }

    public void setFluid(int x, int y, int z, Fluid fluid) {
        if (fluid == null) {
            fluids.remove(toKey(x, y, z));
        } else {
            fluids.put(toKey(x, y, z), fluid);
        }
    }

    public void clearFluid(int x, int y, int z) {
        fluids.remove(toKey(x, y, z));
    }

    public void clearAllFluid() {
        fluids.clear();
    }

    public void writeToNBT(NBTTagCompound nbt) {
        var fluidsData = new NBTTagList();

        for (Map.Entry<Integer, Fluid> entry : fluids.entrySet()) {
            var fluidName = FluidRegistry.getFluidName(entry.getValue());
            var fluidTag = new NBTTagCompound();
            fluidTag.setInteger("k", entry.getKey());
            fluidTag.setString("v", fluidName);
            fluidsData.appendTag(fluidTag);
        }
        nbt.setTag(FLUIDS_DATA_TAG, fluidsData);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        fluids.clear();
        var fluidsData = nbt.getTagList(FLUIDS_DATA_TAG, 10);

        for (int i = 0; i < fluidsData.tagCount(); i++) {
            var fluidData = fluidsData.getCompoundTagAt(i);
            if (fluidData.hasKey("k") && fluidData.hasKey("v")) {
                try {
                    var fluidName = fluidData.getString("v");
                    var fluid = FluidRegistry.getFluid(fluidName);
                    if (fluid == null)
                        Fluidlog.LOGGER.error("Error reading fluid for name {}, a removed fluid ?", fluidName);
                    else fluids.put(fluidData.getInteger("k"), fluid);
                } catch (Exception e) {
                    Fluidlog.LOGGER.error("Error reading fluid name", e);
                }
            }
        }
    }

}
