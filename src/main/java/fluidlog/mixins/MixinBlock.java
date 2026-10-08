package fluidlog.mixins;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fluidlog.api.FluidLogging;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(
        method = "getMixedBrightnessForBlock(Lnet/minecraft/world/IBlockAccess;III)I",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fluidlogging$getLightValue(
        IBlockAccess world,
        int x,
        int y,
        int z,
        CallbackInfoReturnable<Integer> cir
    ) {
        int blockLight = cir.getReturnValueI();

        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid == null) {
            return;
        }

        var fluidLight = fluid.getLuminosity();
        if (fluidLight > blockLight) {
            cir.setReturnValue(fluidLight);
        }
    }

}
