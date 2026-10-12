package fluidlog.mixins.early.compat.efr;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fluidlog.api.AbstractFluidLoggingManager;

@Mixin(value = BlockPistonBase.class, priority = 1100)
public class MixinBlockPistonBase {

    @WrapOperation(
        method = "etfuturum$pushBlocks",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z"),
        remap = false)
    private boolean fluidlogging$efrSetBlockToAir(World world, int x, int y, int z, Operation<Boolean> original) {
        var block = world.getBlock(x, y, z);
        if (block instanceof BlockLiquid || block instanceof IFluidBlock) return true;

        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.getBlock() != null) {
            return world.setBlock(x, y, z, fluid.getBlock());
        }

        return original.call(world, x, y, z);
    }

}
