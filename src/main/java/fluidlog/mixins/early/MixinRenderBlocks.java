package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.api.AbstractFluidLoggingManager;
import fluidlog.common.FluidLoggingUtils;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {

    @Redirect(
        method = { "renderBlockLiquid", "getLiquidHeight" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"))
    private int fluidlogging$getBlockMetadata(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = "getLiquidHeight",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getLiquidHeight$getBlock(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @Redirect(
        method = "renderBlockLiquid",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;shouldSideBeRendered(Lnet/minecraft/world/IBlockAccess;IIII)Z"))
    private boolean fluidlogging$getLiquidHeight$shouldSideBeRendered(Block block, IBlockAccess blockAccess, int x,
        int y, int z, int side) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(blockAccess, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) {
            return fluid.getBlock()
                .getMaterial() != block.getMaterial()
                && (side == 1 || block.shouldSideBeRendered(blockAccess, x, y, z, side));
        }

        return block.shouldSideBeRendered(blockAccess, x, y, z, side);
    }

}
