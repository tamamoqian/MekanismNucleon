package meknuc.chemicals;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import mekanism.common.registration.impl.ChemicalDeferredRegister;
import mekanism.common.registration.impl.DeferredChemical;
import mekanism.common.util.ChemicalUtil;
import meknuc.meknuc;
import net.minecraft.resources.ResourceLocation;

public class MeknucChemicals {

    public static final ChemicalDeferredRegister CHEMICALS = new ChemicalDeferredRegister(meknuc.MODID);

    private static final int HIGH_PRESSURE_STEAM_COLOR = 0xFFF2B0;

    public static final DeferredChemical<Chemical> HIGH_PRESSURE_STEAM = CHEMICALS.register("high_pressure_steam",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(
                ResourceLocation.fromNamespaceAndPath("mekanism", "liquid/steam"))
                .tint(HIGH_PRESSURE_STEAM_COLOR), HIGH_PRESSURE_STEAM_COLOR));

    private MeknucChemicals() {
    }
}
