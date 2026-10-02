package meknuc.reactor;

import mekanism.common.lib.multiblock.MultiblockManager;

public class MeknucPwrMultiblock {

    public static final String NAME = "pressurizedWaterReactor";

    public static final MultiblockManager<MeknucPwrMultiblockData> MANAGER = new MultiblockManager<>(
          NAME, MeknucPwrCache::new, MeknucPwrValidator::new);

    public static void initialize() {
    }

    private MeknucPwrMultiblock() {
    }
}
