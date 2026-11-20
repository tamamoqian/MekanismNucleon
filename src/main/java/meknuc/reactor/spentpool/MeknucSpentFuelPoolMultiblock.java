package meknuc.reactor.spentpool;

import mekanism.common.lib.multiblock.MultiblockManager;

/**
 * 乏燃料冷却池多方块。
 */
public class MeknucSpentFuelPoolMultiblock {

    public static final String NAME = "spentFuelPool";

    public static final MultiblockManager<MeknucSpentFuelPoolMultiblockData> MANAGER = new MultiblockManager<>(
          NAME, MeknucSpentFuelPoolCache::new, MeknucSpentFuelPoolValidator::new);

    public static void initialize() {
    }

    private MeknucSpentFuelPoolMultiblock() {
    }
}
