package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.common.FluidLoggingUtils;

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid {

    @Redirect(
        method = { "getEffectiveFlowDecay", "getFlowVector" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"))
    private int fluidlogging$getBlockMetadata(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = { "getEffectiveFlowDecay", "getFlowVector", "shouldSideBeRendered", "func_149804_e" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getBlock(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @Redirect(
        method = { "func_149804_e", "randomDisplayTick", "func_149805_n" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"))
    private int fluidlogging$getBlockMetadata(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = { "func_149804_e", "randomDisplayTick", "func_149805_n" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getBlock(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

}
