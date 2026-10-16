package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import fluidlog.FluidLoggingManagerImpl;
import fluidlog.api.AbstractFluidLoggingManager;
import fluidlog.common.FluidLoggingUtils;

@Mixin(BlockFluidClassic.class)
public class MixinBlockFluidClassic {

    @Redirect(
        method = { "updateTick" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"),
        remap = false)
    private int fluidlogging$getBlockMetadata(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = { "isFlowingVertically", "isSourceBlock", "canFlowInto", "getQuantaValue" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getBlock(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @Redirect(
        method = { "getQuantaValue", "getLightValue", "isSourceBlock" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"),
        remap = false)
    private int fluidlogging$getBlockMetadata(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = "updateTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockMetadataWithNotify(IIIII)Z"))
    private boolean fluidlogging$updateTick$setBlockMetadataWithNotify(World world, int x, int y, int z, int meta,
        int flag) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) return false;

        return world.setBlockMetadataWithNotify(x, y, z, meta, flag);
    }

    @Redirect(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;scheduleBlockUpdate(IIILnet/minecraft/block/Block;I)V"))
    private void fluidlogging$updateTick$scheduleBlockUpdate(World world, int x, int y, int z, Block block, int meta) {
        if (AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z) != null) return;
        world.scheduleBlockUpdate(x, y, z, block, meta);
    }

    @Redirect(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;notifyBlocksOfNeighborChange(IIILnet/minecraft/block/Block;)V"))
    private void fluidlogging$updateTick$notifyBlocksOfNeighborChange(World world, int x, int y, int z, Block block) {
        if (AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z) != null) return;
        world.notifyBlocksOfNeighborChange(x, y, z, block);
    }

    @Redirect(
        method = "flowIntoBlock",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;)Z"))
    private boolean fluidlogging$flowIntoBlock$setBlock(World world, int x, int y, int z, Block block) {
        if (AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z) != null) return false;
        return world.setBlock(x, y, z, block);
    }

    @Redirect(
        method = "drain",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;)Z"),
        remap = false)
    private boolean fluidlogging$drain$setBlock(World world, int x, int y, int z, Block block) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld())
            return AbstractFluidLoggingManager.INSTANCE.clearFluid(world, x, y, z) != null;

        return world.setBlock(x, y, z, block);
    }

    @WrapOperation(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fluids/BlockFluidClassic;getOptimalFlowDirections(Lnet/minecraft/world/World;III)[Z",
            remap = false))
    private boolean[] fluidlog$filterFlowSides(BlockFluidClassic instance, World world, int x, int y, int z,
        Operation<boolean[]> original) {

        boolean[] directions = original.call(instance, world, x, y, z);

        var block = world.getBlock(x, y, z);
        if (AbstractFluidLoggingManager.INSTANCE.isFluidLoggable(block)) {
            var fullFaces = FluidLoggingManagerImpl.getRestrictedFlowFaces(world, x, y, z);

            if (fullFaces[ForgeDirection.WEST.ordinal()]) directions[0] = false;
            if (fullFaces[ForgeDirection.EAST.ordinal()]) directions[1] = false;
            if (fullFaces[ForgeDirection.NORTH.ordinal()]) directions[2] = false;
            if (fullFaces[ForgeDirection.SOUTH.ordinal()]) directions[3] = false;
        }

        return directions;
    }

    @WrapOperation(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fluids/BlockFluidClassic;canDisplace(Lnet/minecraft/world/IBlockAccess;III)Z",
            remap = false))
    private boolean fluidlog$filterFlowVertical(BlockFluidClassic instance, IBlockAccess world, int x, int y, int z,
        Operation<Boolean> original, @Local(name = "y") int originY) {

        if (!FluidLoggingUtils.canFlowOut((World) world, x, originY, z, x, y, z)) {
            return false;
        }

        return original.call(instance, world, x, y, z);
    }

    @WrapOperation(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fluids/BlockFluidClassic;getLargerQuanta(Lnet/minecraft/world/IBlockAccess;IIII)I",
            remap = false))
    private int fluidlog$checkSourceBlockSides(BlockFluidClassic instance, IBlockAccess world, int x, int y, int z,
        int previousTouchingSource, Operation<Integer> original, @Local(name = "x") int originX,
        @Local(name = "z") int originZ) {

        if (!FluidLoggingUtils.canFlowOut((World) world, x, y, z, originX, y, originZ)) {
            return previousTouchingSource;
        }

        return original.call(instance, world, x, y, z, previousTouchingSource);
    }

    @WrapOperation(
        method = { "updateTick" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$checkSourceBlockVerticalAndSides(World world, int x, int y, int z,
        Operation<Block> original, @Local(name = "x") int originX, @Local(name = "y") int originY,
        @Local(name = "z") int originZ) {

        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()
            && FluidLoggingUtils.canFlowOut(world, originX, originY, originZ, x, y, z)) {
            return fluid.getBlock();
        }

        return original.call(world, x, y, z);
    }

}
