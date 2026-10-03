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
    private static final ResourceLocation WATER_TEXTURE = ResourceLocation.fromNamespaceAndPath("mekanism", "liquid/liquid");

    public static final DeferredChemical<Chemical> HIGH_PRESSURE_STEAM = CHEMICALS.register("high_pressure_steam",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(
                ResourceLocation.fromNamespaceAndPath("mekanism", "liquid/steam"))
                .tint(HIGH_PRESSURE_STEAM_COLOR), HIGH_PRESSURE_STEAM_COLOR));

    public static final DeferredChemical<Chemical> AMERICIUM_241 = CHEMICALS.register("americium_241",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF435974), 0xFF435974));

    public static final DeferredChemical<Chemical> TECHNETIUM_99 = CHEMICALS.register("technetium_99",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF99A2A8), 0xFF99A2A8));

    public static final DeferredChemical<Chemical> STRONTIUM_90 = CHEMICALS.register("strontium_90",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF706641), 0xFF706641));

    public static final DeferredChemical<Chemical> NITRIC_ACID = CHEMICALS.register("nitric_acid",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFFDECA38), 0xFFDECA38));

    public static final DeferredChemical<Chemical> PLUTONIUM_239 = CHEMICALS.register("plutonium_239",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF34637E), 0xFF34637E));

    public static final DeferredChemical<Chemical> POLONIUM_210 = CHEMICALS.register("polonium_210",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF1D8A96), 0xFF1D8A96));

    public static final DeferredChemical<Chemical> ASTATINE_210 = CHEMICALS.register("astatine_210",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF5B3281), 0xFF5B3281));

    public static final DeferredChemical<Chemical> BOILING_FLUORIDE_SALT_URANIUM_235_FISSION_FUEL = CHEMICALS.register("boiling_fluoride_salt_uranium_235_fission_fuel",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF717060), 0xFF717060));

    public static final DeferredChemical<Chemical> BOILING_FLUORIDE_SALT_URANIUM_233_FISSION_FUEL = CHEMICALS.register("boiling_fluoride_salt_uranium_233_fission_fuel",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF797866), 0xFF797866));

    public static final DeferredChemical<Chemical> LIQUID_FLUORIDE_SALT_URANIUM_235_FISSION_FUEL = CHEMICALS.register("liquid_fluoride_salt_uranium_235_fission_fuel",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF5E5C4F), 0xFF5E5C4F));

    public static final DeferredChemical<Chemical> LIQUID_FLUORIDE_SALT_URANIUM_233_FISSION_FUEL = CHEMICALS.register("liquid_fluoride_salt_uranium_233_fission_fuel",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF6A6859), 0xFF6A6859));

    public static final DeferredChemical<Chemical> DEPLETED_LIQUID_FLUORIDE_SALT_URANIUM_233_FISSION_FUEL = CHEMICALS.register("depleted_liquid_fluoride_salt_uranium_233_fission_fuel",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF424137), 0xFF424137));

    public static final DeferredChemical<Chemical> DEPLETED_LIQUID_FLUORIDE_SALT_URANIUM_235_FISSION_FUEL = CHEMICALS.register("depleted_liquid_fluoride_salt_uranium_235_fission_fuel",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF49483D), 0xFF49483D));

    public static final DeferredChemical<Chemical> URANYL_NITRATE_FISSION_RESIDUE_MIXTURE = CHEMICALS.register("uranyl_nitrate_fission_residue_mixture",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF3C4B3A), 0xFF3C4B3A));

    public static final DeferredChemical<Chemical> THORIUM_URANIUM_NITRATE_FISSION_RESIDUE_MIXTURE = CHEMICALS.register("thorium_uranium_nitrate_fission_residue_mixture",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF232D22), 0xFF232D22));

    public static final DeferredChemical<Chemical> URANIUM_PLUTONIUM_NITRATE_FISSION_RESIDUE_MIXTURE = CHEMICALS.register("uranium_plutonium_nitrate_fission_residue_mixture",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF334F4E), 0xFF334F4E));

    public static final DeferredChemical<Chemical> THORIUM_URANIUM_PLUTONIUM_NITRATE_FISSION_RESIDUE_MIXTURE = CHEMICALS.register("thorium_uranium_plutonium_nitrate_fission_residue_mixture",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF294140), 0xFF294140));

    public static final DeferredChemical<Chemical> THORIUM_NITRATE_FISSION_RESIDUE_BREEDER_URANIUM_PLUTONIUM_MIXTURE = CHEMICALS.register("thorium_nitrate_fission_residue_breeder_uranium_plutonium_mixture",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF0A0E0E), 0xFF0A0E0E));

    public static final DeferredChemical<Chemical> NEUTRON_BEAM = CHEMICALS.register("neutron_beam",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF4D6C6C), 0xFF4D6C6C));

    public static final DeferredChemical<Chemical> ENRICHED_BORON = CHEMICALS.register("enriched_boron",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFFA4A3A9), 0xFFA4A3A9));

    public static final DeferredChemical<Chemical> ENRICHED_BERYLLIUM = CHEMICALS.register("enriched_beryllium",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF6EA125), 0xFF6EA125));

    public static final DeferredChemical<Chemical> ENRICHED_CHROMIUM = CHEMICALS.register("enriched_chromium",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFFBC9F55), 0xFFBC9F55));

    public static final DeferredChemical<Chemical> ENRICHED_ZIRCONIUM = CHEMICALS.register("enriched_zirconium",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF824680), 0xFF824680));

    public static final DeferredChemical<Chemical> ENRICHED_LEAD = CHEMICALS.register("enriched_lead",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF798988), 0xFF798988));

    public static final DeferredChemical<Chemical> ENRICHED_URANIUM = CHEMICALS.register("enriched_uranium",
          () -> ChemicalUtil.chemical(ChemicalBuilder.builder(MeknucChemicals.WATER_TEXTURE)
                .tint(0xFF97C597), 0xFF97C597));

    private MeknucChemicals() {
    }
}
