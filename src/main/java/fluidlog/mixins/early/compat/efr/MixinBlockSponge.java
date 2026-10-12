package fluidlog.mixins.early.compat.efr;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fluidlog.api.AbstractFluidLoggingManager;
import fluidlog.common.FluidLoggingUtils;
import ganymedes01.etfuturum.blocks.BlockSponge;

@Mixin(BlockSponge.class)
public class MixinBlockSponge {

    @Redirect(
        method = "absorb",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$efrGetBlock(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @WrapOperation(
        method = "absorb",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z"))
    private boolean fluidlogging$efrSetBlockToAir(World world, int x, int y, int z, Operation<Boolean> op) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null) {
            return AbstractFluidLoggingManager.INSTANCE.clearFluid(world, x, y, z) != null;
        }

        return op.call(world, x, y, z);
    }

}
