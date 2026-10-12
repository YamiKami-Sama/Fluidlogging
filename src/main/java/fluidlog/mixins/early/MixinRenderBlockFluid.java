package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.RenderBlockFluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.common.FluidLoggingUtils;

@Mixin(RenderBlockFluid.class)
public class MixinRenderBlockFluid {

    @Redirect(
        method = { "getFluidHeightForRender", "renderWorldBlock" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"))
    private int fluidlogging$getBlockMetadata(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = { "getFluidHeightForRender", "renderWorldBlock" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getBlock(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

}
