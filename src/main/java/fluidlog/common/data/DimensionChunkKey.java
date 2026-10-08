package fluidlog.common.data;

import net.minecraft.world.chunk.Chunk;

public class DimensionChunkKey {

    public final int dimension;
    public final int chunkX;
    public final int chunkZ;

    public DimensionChunkKey(int dimension, int chunkX, int chunkZ) {
        this.dimension = dimension;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public static DimensionChunkKey of(Chunk chunk) {
        return new DimensionChunkKey(chunk.worldObj.provider.dimensionId, chunk.xPosition, chunk.zPosition);
    }

    @Override
    public int hashCode() {
        int result = dimension;
        result = 31 * result + chunkX;
        result = 31 * result + chunkZ;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof DimensionChunkKey other)) return false;

        return dimension == other.dimension && chunkX == other.chunkX && chunkZ == other.chunkZ;
    }
}
