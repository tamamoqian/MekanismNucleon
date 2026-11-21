package meknuc;

import net.minecraft.resources.ResourceLocation;

public final class MeknucReference {

    private MeknucReference() {
    }

    public static final String MOD_ID = Meknuc.MOD_ID;
    public static final String MOD_NAME = "Mekanism: Nucleon";
    public static final String VERSION = "1.0.0";

    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Meknuc.MOD_ID, path);
    }
}
