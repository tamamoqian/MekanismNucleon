package meknuc.reactor.spentpool;

public class MeknucSpentFuelPoolConstants {

    public static final int COOLING_TICKS = 240 * 20;
    public static final int MAX_INPUT_RODS = 64;
    public static final int MAX_COOLED_RODS = 64;
    public static final int INPUT_SLOT_LIMIT = 1;
    public static final int INPUT_SLOT_COUNT = MAX_INPUT_RODS / INPUT_SLOT_LIMIT;
    public static final int FLUID_PER_VOLUME = 1_000;
    public static final int WATER_PER_THREAD_PER_TICK = 1;
    public static final int STEAM_PER_THREAD_PER_TICK = 1;
    public static final int MIN_CLICK_INTERVAL_TICKS = 2;
    public static final int RANDOM_CLICK_EXTRA_TICKS = 7;

    private MeknucSpentFuelPoolConstants() {
    }
}
