package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.common.FluidLoggingUtils;

@Mixin(BlockFarmland.class)
public class MixinBlockFarmland {

    @Redirect(
        method = "func_149821_m",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"),
        remap = false)
    private Block fluidlogging$getBlock(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

}
