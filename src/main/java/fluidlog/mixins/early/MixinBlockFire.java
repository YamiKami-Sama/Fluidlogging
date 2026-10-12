package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.api.AbstractFluidLoggingManager;

@Mixin(BlockFire.class)
public class MixinBlockFire {

    @Redirect(
        method = "tryCatchFire",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;getFlammability(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;)I",
            remap = false))
    private int fluidlogging$tryCatchFire$getFlammability(Block block, IBlockAccess world, int x, int y, int z,
        ForgeDirection side) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.getBlock() != null) return fluid.getBlock()
            .getFlammability(world, x, y, z, side);
        return block.getFlammability(world, x, y, z, side);
    }

    @Redirect(
        method = "canCatchFire",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;isFlammable(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;)Z",
            remap = false),
        remap = false)
    private boolean fluidlogging$canCatchFire$isFlammable(Block block, IBlockAccess world, int x, int y, int z,
        ForgeDirection side) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(world, x, y, z);
        if (fluid != null && fluid.getBlock() != null) return fluid.getBlock()
            .isFlammable(world, x, y, z, side);
        return block.isFlammable(world, x, y, z, side);
    }

}
