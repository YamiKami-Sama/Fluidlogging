package fluidlog.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.api.FluidLogging;
import fluidlog.common.FluidLoggingUtils;

@Mixin(BlockFluidClassic.class)
public class MixinBlockFluidClassic {

    @Redirect(
        method = { "updateTick" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getBlock(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @Redirect(
        method = { "updateTick" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"),
        remap = false)
    private int fluidlogging$getBlockMetadata(World world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = { "isFlowingVertically", "isSourceBlock", "canFlowInto", "getQuantaValue" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block fluidlogging$getBlock(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockOrBlock(world, x, y, z);
    }

    @Redirect(
        method = { "getQuantaValue", "getLightValue", "isSourceBlock" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"),
        remap = false)
    private int fluidlogging$getBlockMetadata(IBlockAccess world, int x, int y, int z) {
        return FluidLoggingUtils.getFluidLoggedBlockMetaOrBlockMeta(world, x, y, z);
    }

    @Redirect(
        method = "updateTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockMetadataWithNotify(IIIII)Z"))
    private boolean fluidlogging$updateTick$setBlockMetadataWithNotify(World world, int x, int y, int z, int meta,
        int flag) {
        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) return false;

        return world.setBlockMetadataWithNotify(x, y, z, meta, flag);
    }

    @Redirect(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;scheduleBlockUpdate(IIILnet/minecraft/block/Block;I)V"))
    private void fluidlogging$updateTick$scheduleBlockUpdate(World world, int x, int y, int z, Block block, int meta) {
        if (FluidLogging.getFluid(world, x, y, z) != null) return;
        world.scheduleBlockUpdate(x, y, z, block, meta);
    }

    @Redirect(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;notifyBlocksOfNeighborChange(IIILnet/minecraft/block/Block;)V"))
    private void fluidlogging$updateTick$notifyBlocksOfNeighborChange(World world, int x, int y, int z, Block block) {
        if (FluidLogging.getFluid(world, x, y, z) != null) return;
        world.notifyBlocksOfNeighborChange(x, y, z, block);
    }

    @Redirect(
        method = "flowIntoBlock",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;)Z"))
    private boolean fluidlogging$flowIntoBlock$setBlock(World world, int x, int y, int z, Block block) {
        if (FluidLogging.getFluid(world, x, y, z) != null) return false;
        return world.setBlock(x, y, z, block);
    }

    @Redirect(
        method = "drain",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;)Z"),
        remap = false)
    private boolean fluidlogging$drain$setBlock(World world, int x, int y, int z, Block block) {
        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid != null && fluid.canBePlacedInWorld()) return FluidLogging.clearFluid(world, x, y, z) != null;

        return world.setBlock(x, y, z, block);
    }

}
