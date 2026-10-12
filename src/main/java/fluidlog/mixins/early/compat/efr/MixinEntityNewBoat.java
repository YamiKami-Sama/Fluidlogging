package fluidlog.mixins.early.compat.efr;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.common.FluidLoggingUtils;
import ganymedes01.etfuturum.entities.EntityNewBoat;

@Mixin(EntityNewBoat.class)
public class MixinEntityNewBoat {

    @Redirect(
        method = { "getWaterLevelAbove", "checkInWater", "getUnderwaterStatus", "updateFallState" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
        remap = false)
    private Block fluidlogging$getBlock(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @Redirect(
        method = "getBlockMetadata",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"),
        remap = false)
    private int fluidlogging$getBlockMetadata(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = "getBlockLiquidHeight",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
        remap = false)
    private static Block fluidlogging$getBlockStatic(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @Redirect(
        method = "getBlockLiquidHeight",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"),
        remap = false)
    private static int fluidlogging$getBlockMetadataStatic(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

}
