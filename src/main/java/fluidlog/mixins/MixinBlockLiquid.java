package fluidlog.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.api.FluidLogging;

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid {


    @Redirect(
        method = {"getEffectiveFlowDecay", "getFlowVector"},
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
        method = {"getEffectiveFlowDecay", "getFlowVector", "shouldSideBeRendered"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"
        )
    )
    private Block fluidlogging$getBlock(IBlockAccess blockAccess, int x, int y, int z) {
        var fluid = FluidLogging.getFluid(blockAccess, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) return fluid.getBlock();

        return blockAccess.getBlock(x, y, z);
    }

}
