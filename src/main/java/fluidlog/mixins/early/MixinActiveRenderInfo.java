package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fluidlog.api.AbstractFluidLoggingManager;

@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo {

    @WrapOperation(
        method = "getBlockAtEntityViewpoint",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private static Block fluidlogging$getBlockAtEntityViewpoint$getBlock(World world, int x, int y, int z,
        Operation<Block> original) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid == null || !fluid.canBePlacedInWorld()) return original.call(world, x, y, z);
        return fluid.getBlock();
    }
}
