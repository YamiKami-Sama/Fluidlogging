package fluidlog.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import fluidlog.api.AbstractFluidLoggingManager;

public class FluidLoggingUtils {

    public static boolean[] computeBlockRestrictiveFlowFace(Block block, World world, int x, int y, int z) {
        var box = block.getCollisionBoundingBoxFromPool(world, x, y, z);
        boolean[] fullFaces = new boolean[6];

        if (box == null) {
            return fullFaces;
        }
        var collisions = new ArrayList<AxisAlignedBB>();
        var fullBoxCheck = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        block.addCollisionBoxesToList(world, x, y, z, fullBoxCheck, collisions, null);

        if (collisions.isEmpty()) {
            return fullFaces;
        }

        // Each face is projected onto its two perpendicular axes.
        fullFaces[ForgeDirection.DOWN.ordinal()] = coversFace(collisions, x, y, z, 1); // Y = bottom

        fullFaces[ForgeDirection.UP.ordinal()] = coversFace(collisions, x, y, z, 2); // Y = top

        fullFaces[ForgeDirection.NORTH.ordinal()] = coversFace(collisions, x, y, z, 3); // Z = north

        fullFaces[ForgeDirection.SOUTH.ordinal()] = coversFace(collisions, x, y, z, 4); // Z = south

        fullFaces[ForgeDirection.WEST.ordinal()] = coversFace(collisions, x, y, z, 5); // X = west

        fullFaces[ForgeDirection.EAST.ordinal()] = coversFace(collisions, x, y, z, 6); // X = east

        return fullFaces;
    }

    private static boolean coversFace(List<AxisAlignedBB> boxes, int x, int y, int z, int face) {

        var rects = new ArrayList<double[]>();

        for (AxisAlignedBB b : boxes) {
            double minU, maxU, minV, maxV;
            boolean touches;

            switch (face) {
                case 1: // DOWN
                    touches = b.minY <= y + 1.0E-7;
                    minU = b.minX - x;
                    maxU = b.maxX - x;
                    minV = b.minZ - z;
                    maxV = b.maxZ - z;
                    break;

                case 2: // UP
                    touches = b.maxY >= y + 1 - 1.0E-7;
                    minU = b.minX - x;
                    maxU = b.maxX - x;
                    minV = b.minZ - z;
                    maxV = b.maxZ - z;
                    break;

                case 3: // NORTH
                    touches = b.minZ <= z + 1.0E-7;
                    minU = b.minX - x;
                    maxU = b.maxX - x;
                    minV = b.minY - y;
                    maxV = b.maxY - y;
                    break;

                case 4: // SOUTH
                    touches = b.maxZ >= z + 1 - 1.0E-7;
                    minU = b.minX - x;
                    maxU = b.maxX - x;
                    minV = b.minY - y;
                    maxV = b.maxY - y;
                    break;

                case 5: // WEST
                    touches = b.minX <= x + 1.0E-7;
                    minU = b.minZ - z;
                    maxU = b.maxZ - z;
                    minV = b.minY - y;
                    maxV = b.maxY - y;
                    break;

                case 6: // EAST
                    touches = b.maxX >= x + 1 - 1.0E-7;
                    minU = b.minZ - z;
                    maxU = b.maxZ - z;
                    minV = b.minY - y;
                    maxV = b.maxY - y;
                    break;

                default:
                    return false;
            }

            if (!touches) {
                continue;
            }

            // Clip the projected rectangle to the block face.
            minU = Math.max(0.0, minU);
            maxU = Math.min(1.0, maxU);
            minV = Math.max(0.0, minV);
            maxV = Math.min(1.0, maxV);

            if (maxU - minU > 1.0E-7 && maxV - minV > 1.0E-7) {
                rects.add(new double[] { minU, maxU, minV, maxV });
            }
        }

        if (rects.isEmpty()) {
            return false;
        }

        // Collect all U boundaries, plus the face boundaries.
        var boundaries = new ArrayList<Double>();
        boundaries.add(0.0);
        boundaries.add(1.0);

        for (double[] r : rects) {
            boundaries.add(r[0]);
            boundaries.add(r[1]);
        }

        Collections.sort(boundaries);

        // For each vertical strip, check that its entire V interval is covered.
        for (int i = 0; i < boundaries.size() - 1; i++) {
            double left = boundaries.get(i);
            double right = boundaries.get(i + 1);

            if (right - left <= 1.0E-7) {
                continue;
            }

            double mid = (left + right) * 0.5;

            List<double[]> intervals = new ArrayList<double[]>();

            for (double[] r : rects) {
                if (r[0] <= mid && r[1] >= mid) {
                    intervals.add(new double[] { r[2], r[3] });
                }
            }

            if (intervals.isEmpty()) {
                return false;
            }

            intervals.sort((a, b) -> Double.compare(a[0], b[0]));

            double coveredUntil = 0.0;

            for (double[] interval : intervals) {
                if (interval[0] > coveredUntil + 1.0E-7) {
                    return false;
                }

                coveredUntil = Math.max(coveredUntil, interval[1]);

                if (coveredUntil >= 1.0 - 1.0E-7) {
                    break;
                }
            }

            if (coveredUntil < 1.0 - 1.0E-7) {
                return false;
            }
        }

        return true;
    }

    public static Block getFluidLoggedBlockOrBlock(IBlockAccess world, int x, int y, int z) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) {
            return fluid.getBlock();
        }

        return world.getBlock(x, y, z);
    }

    public static int getFluidLoggedBlockMetaOrBlockMeta(IBlockAccess world, int x, int y, int z) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) {
            return 0;
        }

        return world.getBlockMetadata(x, y, z);
    }

    public static int getLightValue(IBlockAccess world, int x, int y, int z, int blockLight) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid == null || fluid.getBlock() == null) {
            return blockLight;
        }

        var fluidLight = fluid.getBlock()
            .getLightValue();
        return Math.max(fluidLight, blockLight);
    }

    public static int getLightOpacity(IBlockAccess world, int x, int y, int z, int blockOpacity) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid == null || fluid.getBlock() == null) {
            return blockOpacity;
        }

        var fluidOpacity = fluid.getBlock()
            .getLightOpacity();
        return Math.max(fluidOpacity, blockOpacity);
    }

}
