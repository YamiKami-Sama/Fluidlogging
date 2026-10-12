package fluidlog.mixinplugin;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

public enum TargetMods implements ITargetMod {

    ET_FUTURUM("ganymedes01.etfuturum.mixinplugin.EtFuturumEarlyMixins", "etfuturum"),
    ANGELICA("com.gtnewhorizons.angelica.loading.AngelicaTweaker", "angelica");

    private final TargetModBuilder builder;

    TargetMods(String coreModClass, String modId) {
        this.builder = new TargetModBuilder().setCoreModClass(coreModClass)
            .setModId(modId);
    }

    @Nonnull
    @Override
    public TargetModBuilder getBuilder() {
        return builder;
    }
}
