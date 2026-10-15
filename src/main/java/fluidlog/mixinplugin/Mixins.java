package fluidlog.mixinplugin;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    ACTIVE_RENDER_INFO(new MixinBuilder().setPhase(Phase.EARLY)
        .addClientMixins("MixinActiveRenderInfo")),
    RENDER_BLOCKS(new MixinBuilder().setPhase(Phase.EARLY)
        .addClientMixins("MixinRenderBlocks")),
    RENDER_BLOCK_FLUID(new MixinBuilder().setPhase(Phase.EARLY)
        .addClientMixins("MixinRenderBlockFluid")),
    BLOCK(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlock")),
    BLOCK_DOOR(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlockDoor")),
    BLOCK_DYNAMIC_LIQUID(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlockDynamicLiquid")),
    BLOCK_LIQUID(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlockLiquid")),
    BLOCK_PISTON_BASE(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinVanillaBlockPistonBase")
        .addExcludedMod(TargetMods.ET_FUTURUM)),
    BLOCK_PISTON_BASE_EFR(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("compat.efr.MixinBlockPistonBase")
        .addRequiredMod(TargetMods.ET_FUTURUM)),
    BLOCK_SPONGE_EFR(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("compat.efr.MixinBlockSponge")
        .addRequiredMod(TargetMods.ET_FUTURUM)),
    BLOCK_TRAP_DOOR(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlockTrapDoor")),
    BLOCK_FARMLAND(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlockFarmland")),
    BLOCK_FIRE(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlockFire")),
    BLOCK_FLUID_BASE(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlockFluidBase")),
    BLOCK_FLUID_CLASSIC(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinBlockFluidClassic")),
    CHUNK_CACHE(new MixinBuilder().setPhase(Phase.EARLY)
        .addClientMixins("ChunkCacheAccessor")),
    ENTITY(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinEntity")),
    ENTITY_NEW_BOAT_EFR(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("compat.efr.MixinEntityNewBoat")),
    WORLD(new MixinBuilder().setPhase(Phase.EARLY)
        .addCommonMixins("MixinWorld")),
    // not working with angelica, so just removing it
    WORLD_RENDERER(new MixinBuilder().setPhase(Phase.EARLY)
        .addClientMixins("MixinWorldRenderer")
        .addExcludedMod(TargetMods.ANGELICA));

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }

}
