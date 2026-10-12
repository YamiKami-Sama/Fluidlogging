package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fluidlog.api.FluidLogging;
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
        int blockLight = cir.getReturnValueI();

        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid == null || fluid.getBlock() == null) {
            return;
        }

        var fluidLight = fluid.getBlock()
            .getLightValue();
        if (fluidLight > blockLight) {
            cir.setReturnValue(fluidLight);
        }
    }

    @Inject(
        method = "getLightOpacity(Lnet/minecraft/world/IBlockAccess;III)I",
        at = @At("RETURN"),
        cancellable = true,
        remap = false)
    public void fluidlogging$getLightOpacity(IBlockAccess world, int x, int y, int z,
        CallbackInfoReturnable<Integer> cir) {
        int blockOpacity = cir.getReturnValueI();

        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid == null || fluid.getBlock() == null) {
            return;
        }

        var fluidOpacity = fluid.getBlock()
            .getLightOpacity();
        if (fluidOpacity > blockOpacity) {
            cir.setReturnValue(fluidOpacity);
        }
    }

    @Redirect(
        method = "canSustainPlant",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"),
        remap = false)
    private int fluidlogging$getBlockMetadata(IBlockAccess blockAccess, int x, int y, int z) {
        var fluid = FluidLogging.getFluid(blockAccess, x, y, z);
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
