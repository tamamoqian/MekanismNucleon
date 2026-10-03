package meknuc.items;

import org.jetbrains.annotations.Nullable;

public enum MeknucRodType {

    TEST("test_fuel_rod", 0),
    URANIUM_235_MOX("uranium_235_mox_fuel_rod", 20000),
    URANIUM_235_THORIUM("uranium_235_thorium_fuel_rod", 16000),
    URANIUM_235_PLUTONIUM_239("uranium_235_plutonium_239_fuel_rod", 30000),
    THORIUM_URANIUM_PLUTONIUM("thorium_uranium_plutonium_fuel_rod", 0),
    URANIUM_235_PLUTONIUM_239_ZIRCALOY("uranium_235_plutonium_239_zircaloy_fuel_rod", 0),
    URANIUM_235_PLUTONIUM_239_CARBIDE("uranium_235_plutonium_239_carbide_fuel_rod", 0),
    BRED_URANIUM_235_PLUTONIUM_239_ZIRCALOY("bred_uranium_235_plutonium_239_zircaloy_fuel_rod", 0),
    BRED_HOT_URANIUM_235_PLUTONIUM_239_ZIRCALOY("bred_hot_uranium_235_plutonium_239_zircaloy_fuel_rod", 0),
    BRED_URANIUM_235_PLUTONIUM_239_CARBIDE("bred_uranium_235_plutonium_239_carbide_fuel_rod", 0),
    BRED_HOT_URANIUM_235_PLUTONIUM_239_CARBIDE("bred_hot_uranium_235_plutonium_239_carbide_fuel_rod", 0);

    public static final MeknucRodType[] VALUES = values();

    private final String id;
    private final int burnTime;

    MeknucRodType(String id, int burnTime) {
        this.id = id;
        this.burnTime = burnTime;
    }

    public String id() {
        return id;
    }

    public int burnTime() {
        return burnTime;
    }

    public boolean hasHotModel() {
        return this == URANIUM_235_MOX || this == URANIUM_235_THORIUM
              || this == URANIUM_235_PLUTONIUM_239 || this == THORIUM_URANIUM_PLUTONIUM;
    }

    @Nullable
    public static MeknucRodType byId(String id) {
        for (MeknucRodType type : VALUES) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
