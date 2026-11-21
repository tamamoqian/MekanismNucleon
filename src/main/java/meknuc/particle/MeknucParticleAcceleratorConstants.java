package meknuc.particle;

public class MeknucParticleAcceleratorConstants {

    public static final long ENERGY_CAPACITY = 16_000_000_000L;
    public static final long CHEMICAL_CAPACITY = 128_000L;
    public static final long PRODUCT_CAPACITY = 64_000L;
    public static final int SLOT_LIMIT = 1_000;
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_MISSING_ITEM = 2;
    public static final int STATUS_MISSING_CHEMICAL = 3;
    public static final int STATUS_OUTPUT_FULL = 4;
    public static final int STATUS_NO_ENERGY = 5;
    public static final int STATUS_NO_RECIPE = 6;

    private MeknucParticleAcceleratorConstants() {
    }
}
