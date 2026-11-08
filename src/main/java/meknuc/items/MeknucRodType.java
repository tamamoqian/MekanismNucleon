package meknuc.items;

import java.util.Set;
import org.jetbrains.annotations.Nullable;

public enum MeknucRodType {

    TEST("test_fuel_rod", 0),
    URANIUM_235_MOX("uranium_235_mox_fuel_rod", 20000, ReactorKind.PWR, ReactorKind.BWR),
    URANIUM_235_THORIUM("uranium_235_thorium_fuel_rod", 16000, ReactorKind.PWR, ReactorKind.BWR),
    URANIUM_235_PLUTONIUM_239("uranium_235_plutonium_239_fuel_rod", 30000, ReactorKind.PWR, ReactorKind.BWR),
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
    private final Set<ReactorKind> reactors;

    MeknucRodType(String id, int burnTime, ReactorKind... reactors) {
        this.id = id;
        this.burnTime = burnTime;
        this.reactors = reactors.length == 0 ? Set.of() : Set.of(reactors);
    }

    public String id() {
        return id;
    }

    public int burnTime() {
        return burnTime;
    }

    public boolean supports(ReactorKind reactor) {
        return reactors.contains(reactor);
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

    public enum ReactorKind {
        PWR,
        BWR
    }
}
