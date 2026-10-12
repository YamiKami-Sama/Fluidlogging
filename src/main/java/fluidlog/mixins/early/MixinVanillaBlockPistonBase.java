package fluidlog.mixins.early;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fluidlog.api.FluidLogging;

@Mixin(BlockPistonBase.class)
public class MixinVanillaBlockPistonBase {

    @Redirect(
        method = { "onBlockEventReceived", "tryExtend" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z"))
    private boolean fluidlogging$setBlockToAir(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        if (block instanceof BlockLiquid || block instanceof IFluidBlock) return true;

        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid != null && fluid.getBlock() != null) {
            return world.setBlock(x, y, z, fluid.getBlock());
        }

        return world.setBlockToAir(x, y, z);
    }

}
