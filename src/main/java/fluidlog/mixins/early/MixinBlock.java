package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fluidlog.api.AbstractFluidLoggingManager;
import fluidlog.common.FluidLoggingUtils;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(
        method = "getLightValue(Lnet/minecraft/world/IBlockAccess;III)I",
        at = @At("RETURN"),
        cancellable = true,
        remap = false)
    public void fluidlogging$getLightValue(IBlockAccess world, int x, int y, int z,
        CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FluidLoggingUtils.getLightValue(world, x, y, z, cir.getReturnValueI()));
    }

    @Inject(
        method = "getLightOpacity(Lnet/minecraft/world/IBlockAccess;III)I",
        at = @At("RETURN"),
        cancellable = true,
        remap = false)
    public void fluidlogging$getLightOpacity(IBlockAccess world, int x, int y, int z,
        CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FluidLoggingUtils.getLightOpacity(world, x, y, z, cir.getReturnValueI()));
    }

    @Redirect(
        method = "canSustainPlant",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"),
        remap = false)
    private int fluidlogging$getBlockMetadata(IBlockAccess blockAccess, int x, int y, int z) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(blockAccess, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) return 0;

        return blockAccess.getBlockMetadata(x, y, z);
    }

    @Redirect(
        method = "canSustainPlant",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"),
        remap = false)
    private Block fluidlogging$getBlock(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

}
