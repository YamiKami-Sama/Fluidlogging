package fluidlog.client;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.RenderBlockFluid;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import fluidlog.api.AbstractFluidLoggingManager;

public class FluidLogRenderer {

    public static boolean renderFluid(int renderPass, RenderBlocks renderBlocks, int x, int y, int z,
        LocalBooleanRef tryRenderInPass) {
        var fluid = AbstractFluidLoggingManager.INSTANCE.getFluid(renderBlocks.blockAccess, x, y, z);
        if (fluid == null || fluid.getBlock() == null) return false;

        var block = fluid.getBlock();

        if (block.getRenderBlockPass() > renderPass) {
            tryRenderInPass.set(true);
        }

        if (!block.canRenderInPass(renderPass)) return false;
        if (block instanceof BlockFluidBase) {
            return RenderBlockFluid.instance
                .renderWorldBlock(renderBlocks.blockAccess, x, y, z, block, block.getRenderType(), renderBlocks);
        } else {
            return renderBlocks.renderBlockLiquid(block, x, y, z);
        }
    }

}
