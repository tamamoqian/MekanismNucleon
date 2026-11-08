package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockManager;

public class MeknucBwrMultiblock {

    public static final String NAME = "boilingWaterReactor";

    public static final MultiblockManager<MeknucBwrMultiblockData> MANAGER = new MultiblockManager<>(
          NAME, MeknucBwrCache::new, MeknucBwrValidator::new);

    public static void initialize() {
    }

    private MeknucBwrMultiblock() {
    }
}
