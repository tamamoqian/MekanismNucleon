package meknuc.wastestorage;

import mekanism.common.lib.multiblock.MultiblockManager;

/**
 * 放射性废料存储罐多方块。
 */
public class MeknucWasteStorageMultiblock {

    public static final String NAME = "wasteStorage";

    public static final MultiblockManager<MeknucWasteStorageMultiblockData> MANAGER = new MultiblockManager<>(
          NAME, MeknucWasteStorageCache::new, MeknucWasteStorageValidator::new);

    public static void initialize() {
    }

    private MeknucWasteStorageMultiblock() {
    }
}
