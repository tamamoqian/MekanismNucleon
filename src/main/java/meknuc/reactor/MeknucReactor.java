package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockManager;

public class MeknucReactor {

    public static final String NAME = "pressurizedWaterReactor";

    public static final MultiblockManager<MeknucReactorMultiblockData> MANAGER = new MultiblockManager<>(
          NAME, MeknucReactorCache::new, MeknucReactorValidator::new);

    private MeknucReactor() {
    }
}
