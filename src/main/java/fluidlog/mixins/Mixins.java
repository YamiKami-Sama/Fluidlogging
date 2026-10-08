package fluidlog.mixins;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

import javax.annotation.Nonnull;

public enum Mixins implements IMixins {

    RENDER_BLOCKS(new MixinBuilder().setPhase(Phase.EARLY).addClientMixins("MixinRenderBlocks")),
    BLOCK(new MixinBuilder().setPhase(Phase.EARLY).addCommonMixins("MixinBlock")),
    BLOCK_LIQUID(new MixinBuilder().setPhase(Phase.EARLY).addCommonMixins("MixinBlockLiquid")),
    CHUNK_CACHE(new MixinBuilder().setPhase(Phase.EARLY).addClientMixins("ChunkCacheAccessor")),
    ENTITY(new MixinBuilder().setPhase(Phase.EARLY).addCommonMixins("MixinEntity")),
    WORLD(new MixinBuilder().setPhase(Phase.EARLY).addCommonMixins("MixinWorld")),
    WORLD_RENDERER(new MixinBuilder().setPhase(Phase.EARLY).addClientMixins("MixinWorldRenderer"));

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
