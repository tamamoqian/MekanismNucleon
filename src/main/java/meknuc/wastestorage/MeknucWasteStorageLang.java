package meknuc.wastestorage;

import mekanism.api.text.ILangEntry;

/**
 * 放射性废料存储罐 GUI / 提示的翻译键。
 */
public enum MeknucWasteStorageLang implements ILangEntry {
    WASTE_HEADER("gui.meknuc.waste_storage.waste_header"),
    WASTE_EMPTY("gui.meknuc.waste_storage.waste_empty"),
    WASTE_AMOUNT("gui.meknuc.waste_storage.waste_amount"),
    FUEL_RODS("gui.meknuc.waste_storage.fuel_rods"),
    PRODUCT_HEADER("gui.meknuc.waste_storage.product_header"),
    PRODUCT_FULL("gui.meknuc.waste_storage.product_full"),
    RECIPES_HEADER("gui.meknuc.waste_storage.recipes_header"),
    RECIPE_RUNNING("gui.meknuc.waste_storage.recipe_running"),
    RECIPE_RUNNING_THREADS("gui.meknuc.waste_storage.recipe_running_threads"),
    THREADS("gui.meknuc.waste_storage.threads"),
    THREAD_COUNT("gui.meknuc.waste_storage.thread_count"),
    RECIPE_PAUSED("gui.meknuc.waste_storage.recipe_paused"),
    RECIPE_IDLE("gui.meknuc.waste_storage.recipe_idle"),
    RECIPE_TOOLTIP("gui.meknuc.waste_storage.recipe_tooltip"),
    PROGRESS("gui.meknuc.waste_storage.progress"),
    NO_ROOM("gui.meknuc.waste_storage.no_room");

    private final String key;

    MeknucWasteStorageLang(String key) {
        this.key = key;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}
