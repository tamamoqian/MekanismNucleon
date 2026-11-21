package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockManager;

public class MeknucFbrMultiblock {

    public static final String NAME = "fastBreederReactor";

    public static final MultiblockManager<MeknucFbrMultiblockData> MANAGER = new MultiblockManager<>(
          NAME, MeknucFbrCache::new, MeknucFbrValidator::new);

    public static void initialize() {
    }

    private MeknucFbrMultiblock() {
    }
}
