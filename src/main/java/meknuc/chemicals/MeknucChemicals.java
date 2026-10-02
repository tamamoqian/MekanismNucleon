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

    private static final int PRESSURIZED_LIGHT_WATER_COLOR = 0x9FD8F0;

    public static final DeferredChemical<Chemical> PRESSURIZED_LIGHT_WATER = CHEMICALS.register("pressurized_light_water",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(
                ResourceLocation.fromNamespaceAndPath("mekanism", "liquid/steam")).tint(PRESSURIZED_LIGHT_WATER_COLOR),
                PRESSURIZED_LIGHT_WATER_COLOR));

    private MeknucChemicals() {
    }
}
