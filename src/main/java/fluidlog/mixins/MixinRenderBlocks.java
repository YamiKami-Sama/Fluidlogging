package fluidlog.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fluidlog.api.FluidLogging;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {

    @Redirect(
        method = {"renderBlockLiquid", "getLiquidHeight"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"
        )
    )
    private int fluidlogging$getBlockMetadata(IBlockAccess blockAccess, int x, int y, int z) {
        var fluid = FluidLogging.getFluid(blockAccess, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) return 0;

        return blockAccess.getBlockMetadata(x, y, z);
    }

    @Redirect(
        method = "getLiquidHeight",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"
        )
    )
    private Block fluidlogging$getLiquidHeight$getBlock(IBlockAccess blockAccess, int x, int y, int z) {
        var fluid = FluidLogging.getFluid(blockAccess, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) return fluid.getBlock();

        return blockAccess.getBlock(x, y, z);
    }

    @Redirect(
        method = "renderBlockLiquid",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;shouldSideBeRendered(Lnet/minecraft/world/IBlockAccess;IIII)Z"
        )
    )
    private boolean fluidlogging$getLiquidHeight$shouldSideBeRendered(Block block, IBlockAccess blockAccess, int x, int y, int z, int side) {
        var fluid = FluidLogging.getFluid(blockAccess, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()){
            return fluid.getBlock().getMaterial() != block.getMaterial() && (side == 1 || block.shouldSideBeRendered(blockAccess, x, y, z, side));
        }

        return block.shouldSideBeRendered(blockAccess, x, y, z, side);
    }

}
