package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import fluidlog.FluidLoggingManagerImpl;
import fluidlog.api.AbstractFluidLoggingManager;
import fluidlog.api.registry.FluidLoggingRegistry;
import fluidlog.common.FluidLoggingUtils;

@Mixin(BlockDynamicLiquid.class)
public class MixinBlockDynamicLiquid {

    @Inject(
        method = "func_149811_n(Lnet/minecraft/world/World;III)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    private void fluidlogging$func_149811_n(World world, int x, int y, int z, CallbackInfo cir) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) cir.cancel();
    }

    @Redirect(
        method = "updateTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getLiquidHeight$getBlock(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
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
        method = "func_149813_h",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z"))
    private boolean fluidlogging$flowIntoBlock$setBlock(World world, int x, int y, int z, Block block, int meta,
        int flag) {
        if (AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z) != null) return false;
        return world.setBlock(x, y, z, block, meta, flag);
    }

    @Redirect(
        method = "func_149807_p",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/material/Material;blocksMovement()Z"))
    private boolean fluidlogging$isBlockSolid(Material material, @Local(name = "block") Block block) {
        if (FluidLoggingRegistry.getBlockIFluidLoggable(block) != null) return true;
        return material.blocksMovement();
    }

    @WrapOperation(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockDynamicLiquid;func_149808_o(Lnet/minecraft/world/World;III)[Z"))
    private boolean[] fluidlog$filterFlowDirections(BlockDynamicLiquid instance, World world, int x, int y, int z,
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

}
