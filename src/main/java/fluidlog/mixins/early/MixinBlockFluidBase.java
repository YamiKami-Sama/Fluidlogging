package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.common.FluidLoggingUtils;

@Mixin(BlockFluidBase.class)
public class MixinBlockFluidBase {

    @Redirect(
        method = "getLightValue",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"))
    private int fluidlogging$getBlockMetadata(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = { "getFlowVector", "shouldSideBeRendered" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getBlock(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @Redirect(
        method = { "displaceIfPossible" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"))
    private int fluidlogging$getBlockMetadata(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = { "randomDisplayTick", "canDisplace", "displaceIfPossible", "getDensity", "getTemperature",
            "getFlowDirection" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
        remap = false)
    private Block fluidlogging$getBlock(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

}
