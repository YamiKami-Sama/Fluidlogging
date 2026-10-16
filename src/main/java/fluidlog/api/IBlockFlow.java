package fluidlog.api;

import net.minecraft.world.World;

/**
 * Custom implementation to check block flow outside the block, useful for block controlled by a tile entity or need to
 * be overridden
 */
public interface IBlockFlow {

    /**
     * Do the flow restriction is controller by a tile entity and not the block/meta
     *
     * @return true will prevent the cache of the block & meta and recompute each time the sides flow
     */
    default boolean isControlledByTile() {
        return false;
    }

    /**
     * Return if the side is full
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param side  {@link net.minecraftforge.common.util.ForgeDirection} ordinal
     * @return
     */
    default boolean isFlowRestrictedOnSide(World world, int x, int y, int z, int side) {
        return getSidesFlowRestricted(world, x, y, z)[side];
    }

    /**
     * Return the table of restricted flow direction,
     * indexed by {@link net.minecraftforge.common.util.ForgeDirection} ordinal.<br>
     * Example: A stairs placed when looking at north (so oriented south),
     * would have [true,false,true,false,false,false] preventing the fluid to flow to
     * the north direction and down direction (if flow downward unlike modded steam)
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return boolean table of restricted flow directions
     */
    boolean[] getSidesFlowRestricted(World world, int x, int y, int z);

}
