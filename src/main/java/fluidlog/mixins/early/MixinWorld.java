package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fluidlog.api.AbstractFluidLoggingManager;
import fluidlog.common.FluidLoggingUtils;

@Mixin(World.class)
public abstract class MixinWorld {

    @Redirect(
        method = "handleMaterialAcceleration",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"))
    private int fluidlogging$handleMaterialAcceleration$getFluidMetadata(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @WrapOperation(
        method = { "handleMaterialAcceleration", "isMaterialInBB", "isAnyLiquid" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$handleMaterialAcceleration$getBlock(World world, int x, int y, int z,
        Operation<Block> original) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid == null || !fluid.canBePlacedInWorld()) return original.call(world, x, y, z);
        return fluid.getBlock();
    }

    @Inject(method = "setBlock(IIILnet/minecraft/block/Block;II)Z", at = @At("HEAD"))
    private void fluidlogging$captureOldBlock(int x, int y, int z, Block newBlock, int metadata, int flags,
        CallbackInfoReturnable<Boolean> cir, @Share("fluidlogging$oldBlock") LocalRef<Block> oldBlock,
        @Share("fluidlogging$oldBlockMeta") LocalRef<Integer> oldBlockMeta) {
        oldBlock.set(((World) (Object) this).getBlock(x, y, z));
        oldBlockMeta.set(((World) (Object) this).getBlockMetadata(x, y, z));
    }

    @Inject(method = "setBlock(IIILnet/minecraft/block/Block;II)Z", at = @At("RETURN"))
    private void fluidlogging$handleBlockChange(int x, int y, int z, Block newBlock, int metadata, int flags,
        CallbackInfoReturnable<Boolean> cir, @Share("fluidlogging$oldBlock") LocalRef<Block> oldBlock,
        @Share("fluidlogging$oldBlockMeta") LocalRef<Integer> oldBlockMeta) {
        var world = (World) (Object) this;
        if (!cir.getReturnValueZ()) return;

        var previousBlock = oldBlock.get();
        var currentBlock = world.getBlock(x, y, z);

        // Ignore metadata-only changes.
        if (previousBlock == currentBlock) return;
        var blockMeta = world.getBlockMetadata(x, y, z);

        var fluid = fluidlogging$getFluidForBlock(previousBlock);
        // prevent fluid disappear on client side when placing a block, let the server handle the update
        if (world.isRemote && fluid == null) return;

        // Fluid Source meta is 0
        if (fluid != null && oldBlockMeta.get() == 0
            && AbstractFluidLoggingManager.INSTANCE.canFluidLog(world, x, y, z, currentBlock, blockMeta, fluid)) {
            AbstractFluidLoggingManager.INSTANCE.setFluid(world, x, y, z, fluid);
            return;
        }

        var loggedFluidAtPos = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (loggedFluidAtPos != null) {
            var fluidBlock = loggedFluidAtPos.getBlock();
            if (currentBlock == Blocks.air && fluidBlock != null) {
                AbstractFluidLoggingManager.INSTANCE.clearFluidNoCheck(world, x, y, z);
                // world.setBlock(x, y, z, fluidBlock, 0, flags);
            } else if (!AbstractFluidLoggingManager.INSTANCE
                .canFluidLog(world, x, y, z, currentBlock, blockMeta, loggedFluidAtPos)) {
                    AbstractFluidLoggingManager.INSTANCE.clearFluidNoCheck(world, x, y, z);
                }
        }
    }

    @Redirect(
        method = "setBlock(IIILnet/minecraft/block/Block;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;func_150807_a(IIILnet/minecraft/block/Block;I)Z"))
    private boolean fluidlogging$preserveFluidOnRemoval(Chunk chunk, int localX, int y, int localZ, Block blockIn,
        int metadataIn, @Local(name = "x") int x, @Local(name = "z") int z) {
        var world = (World) (Object) this;

        if (blockIn == Blocks.air) {
            var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);

            if (fluid != null && fluid.getBlock() != null) {
                blockIn = fluid.getBlock();
                metadataIn = 0;
            }
        }

        return chunk.func_150807_a(localX, y, localZ, blockIn, metadataIn);
    }

    private static Fluid fluidlogging$getFluidForBlock(Block block) {
        var fluid = FluidRegistry.lookupFluidForBlock(block);
        if (fluid != null) {
            return fluid;
        }

        // lookupFluidForBlock doesn't work with fluid having double block
        if (block == Blocks.flowing_water) {
            return FluidRegistry.WATER;
        }

        if (block == Blocks.flowing_lava) {
            return FluidRegistry.LAVA;
        }

        return null;
    }

}
