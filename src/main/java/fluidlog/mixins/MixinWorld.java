package fluidlog.mixins;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import fluidlog.FluidLoggingManager;
import fluidlog.api.FluidLogging;

@Mixin(World.class)
public class MixinWorld {

    @Redirect(
        method = "handleMaterialAcceleration",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlockMetadata(III)I"
        )
    )
    private int fluidlogging$getFluidMetadata(IBlockAccess blockAccess, int x, int y, int z) {
        var fluid = FluidLogging.getFluid(blockAccess, x, y, z);
        if (fluid != null) return 0;

        return blockAccess.getBlockMetadata(x, y, z);
    }

    @Redirect(
        method = "handleMaterialAcceleration",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"
        )
    )
    private Block fluidlogging$getBlockForMaterialAcceleration(World world, int x, int y, int z) {
        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid != null && fluid.getBlock() != null) {
            return fluid.getBlock();
        }

        return world.getBlock(x, y, z);
    }

    @Redirect(
        method = "isMaterialInBB",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"
        )
    )
    private Block fluidlogging$isMaterialInBB$getBlock(World world, int x, int y, int z) {
        var fluid = FluidLogging.getFluid(world, x, y, z);
        if (fluid != null && fluid.getBlock() != null) {
            return fluid.getBlock();
        }

        return world.getBlock(x, y, z);
    }

    // I couldn't do a Redirect of getLightValue(Lnet/minecraft/world/IBlockAccess;III), the mixin couldn't find it
    @ModifyVariable(
        method = "computeLightValue",
        at = @At("STORE"),
        name = "blockLight"
    )
    private int fluidlogging$overrideBlockLight(int blockLight, int x, int y, int z, EnumSkyBlock skyBlock) {
        World world = (World) (Object) this;

        var fluid = FluidLoggingManager.getFluid(world, x, y, z);
        if (fluid == null) {
            return blockLight;
        }

        return Math.max(blockLight, fluid.getLuminosity());
    }

}
