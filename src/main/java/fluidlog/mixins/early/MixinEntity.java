package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fluidlog.api.FluidLogging;

@Mixin(Entity.class)
public class MixinEntity {

    @Redirect(
        method = "func_145775_I",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V"))
    private void fluidlogging$onEntityCollidedWithBlock(Block block, World world, int x, int y, int z, Entity entity) {
        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) {
            fluid.getBlock()
                .onEntityCollidedWithBlock(world, x, y, z, entity);
        }

        block.onEntityCollidedWithBlock(world, x, y, z, entity);
    }

    @WrapOperation(
        method = "isInsideOfMaterial",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$isInsideOfMaterial$getBlock(World world, int x, int y, int z,
        Operation<Block> original) {
        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid == null || !fluid.canBePlacedInWorld()) return original.call(world, x, y, z);
        return fluid.getBlock();
    }

}
