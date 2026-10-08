package fluidlog.mixins;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fluidlog.FluidLoggingManager;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Inject(
        method = "updateRenderer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;getRenderBlockPass()I",
            shift = At.Shift.AFTER
        )
    )
    private void fluidlogging$renderFluid(
        EntityLivingBase entity,
        CallbackInfo ci,
        @Local(name = "k2") int renderPass,
        @Local(name = "renderblocks") RenderBlocks renderBlocks,
        @Local(name = "j3") int x,
        @Local(name = "l2") int y,
        @Local(name = "i3") int z,
        @Local(name = "flag") LocalBooleanRef flag, // do we try to render the pass
        @Local(name = "flag1") LocalBooleanRef flag1 // do a render has happened in the pass
    ) {
        var fluid = FluidLoggingManager.getFluid(renderBlocks.blockAccess, x, y, z );
        if (fluid == null || fluid.getBlock() == null) return;

        var block = fluid.getBlock();

        if (block.getRenderBlockPass() > renderPass)
        {
            flag.set(true);
        }

        if(!block.canRenderInPass(renderPass)) return;

        if( renderBlocks.renderBlockLiquid(block, x, y, z) ) flag1.set(true);
    }

}
