package fluidlog.mixins.early;

import net.minecraft.block.BlockDoor;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fluidlog.api.AbstractFluidLoggingManager;

@Mixin(BlockDoor.class)
public class MixinBlockDoor {

    // To propagate block update to water that is no longer connected to water source

    @WrapOperation(
        method = "onBlockActivated",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockMetadataWithNotify(IIIII)Z"))
    private boolean fluidlogging$setBlockMetadataWithNotify(World world, int x, int y, int z, int meta, int flag,
        Operation<Boolean> operation) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        return operation.call(world, x, y, z, meta, fluid != null ? 3 : flag);
    }

    @WrapOperation(
        method = "func_150014_a",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockMetadataWithNotify(IIIII)Z"),
        remap = false)
    private boolean fluidlogging$func_150014_a(World world, int x, int y, int z, int meta, int flag,
        Operation<Boolean> operation) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        return operation.call(world, x, y, z, meta, fluid != null ? 3 : flag);
    }

}
