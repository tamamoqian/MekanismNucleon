package meknuc.particle;

import java.util.List;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.registries.MekanismItems;
import mekanism.common.registration.impl.DeferredChemical;
import meknuc.chemicals.MeknucChemicals;
import meknuc.items.MeknucItemBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 粒子加速器的配方表。
 * <p>
 * 机器分两种工作模式：
 * <ul>
 *   <li><b>粒子加速</b>：靶材 + 热质子 / 热中子等离子体 -> 新的靶材；</li>
 *   <li><b>束流收集</b>：氢气或铀-238粉 -> 热质子 / 热中子等离子体。</li>
 * </ul>
 * 处理时间以 tick 计，能耗以 FE/t 计。1 MFE = 1,000,000 FE。
 * <p>
 * 配方列表延迟构建，避免在注册表冻结之前解析物品。
 */
public final class MeknucParticleAcceleratorRecipes {

    /** 1 MFE = 1,000,000 FE。 */
    public static final long MFE = 1_000_000L;

    /** 粒子加速模式。 */
    public static final int MODE_ACCELERATION = 0;
    /** 束流收集模式。 */
    public static final int MODE_COLLECTION = 1;

    private static List<Recipe> recipes;

    private MeknucParticleAcceleratorRecipes() {
    }

    public static List<Recipe> recipes() {
        if (recipes == null) {
            recipes = build();
        }
        return recipes;
    }

    /**
     * 输入的靶材是否被任意一条配方接受。
     */
    public static boolean isTargetInput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (Recipe recipe : recipes()) {
            if (!recipe.input().isEmpty() && stack.is(recipe.input().getItem())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 输入的化学品是否被任意一条配方接受（作为输入或产物）。
     */
    public static boolean isBeamChemical(Chemical chemical) {
        for (Recipe recipe : recipes()) {
            for (ChemicalInput chemicalInput : recipe.chemicalInputs()) {
                if (chemicalInput.chemical().get() == chemical) {
                    return true;
                }
            }
            if (recipe.hasChemicalOutput() && recipe.chemicalOutput().get() == chemical) {
                return true;
            }
        }
        return false;
    }

    private static List<Recipe> build() {
        return List.of(
              // ---- 粒子加速模式 ----
              new Recipe(id("uranium_233_to_uranium_235"), MODE_ACCELERATION,
                    stack(MeknucItemBase.URANIUM_233_DUST.get(), 1),
                    List.of(chemical(MeknucChemicals.HOT_NEUTRON_PLASMA, 1_000L)),
                    stack(MeknucItemBase.URANIUM_235_DUST.get(), 1), null, 0L,
                    100, 25L * MFE),
              new Recipe(id("uranium_238_to_plutonium_239"), MODE_ACCELERATION,
                    stack(MeknucItemBase.URANIUM_238_DUST.get(), 1),
                    List.of(chemical(MeknucChemicals.HOT_PROTON_PLASMA, 1_000L)),
                    stack(MeknucItemBase.PLUTONIUM_239_DUST.get(), 1), null, 0L,
                    400, 50L * MFE),
              new Recipe(id("enriched_carbon_to_diamond"), MODE_ACCELERATION,
                    stack(MekanismItems.ENRICHED_CARBON.asItem(), 1), List.of(),
                    stack(MekanismItems.DIAMOND_DUST.asItem(), 1), null, 0L,
                    20, 10L * MFE),
              new Recipe(id("plutonium_239_to_americium_241"), MODE_ACCELERATION,
                    stack(MeknucItemBase.PLUTONIUM_239_DUST.get(), 1),
                    List.of(chemical(MeknucChemicals.HOT_PROTON_PLASMA, 1_000L)),
                    stack(MeknucItemBase.AMERICIUM_241_DUST.get(), 1), null, 0L,
                    100, 65L * MFE),
              new Recipe(id("lead_to_polonium_210"), MODE_ACCELERATION,
                    stack(item("mekanism", "dust_lead"), 1),
                    List.of(chemical(MeknucChemicals.HOT_PROTON_PLASMA, 10_000L),
                          chemical(MeknucChemicals.HOT_NEUTRON_PLASMA, 10_000L)),
                    stack(MeknucItemBase.POLONIUM_210_DUST.get(), 1), null, 0L,
                    1_200, 100L * MFE),
              new Recipe(id("thorium_to_ruthenium"), MODE_ACCELERATION,
                    stack(MeknucItemBase.THORIUM_DUST.get(), 1),
                    List.of(chemical(MeknucChemicals.HOT_PROTON_PLASMA, 1_000L)),
                    stack(MeknucItemBase.RUTHENIUM_DUST.get(), 1), null, 0L,
                    200, 45L * MFE),
              new Recipe(id("tin_to_iron"), MODE_ACCELERATION,
                    stack(item("mekanism", "dust_tin"), 1),
                    List.of(chemical(MeknucChemicals.HOT_PROTON_PLASMA, 1_000L)),
                    stack(item("mekanism", "dust_iron"), 2), null, 0L,
                    1, 10L * MFE),
              new Recipe(id("polonium_to_antimatter_pellet"), MODE_ACCELERATION,
                    stack(MeknucItemBase.POLONIUM_210_DUST.get(), 100),
                    List.of(chemical(MeknucChemicals.HOT_PROTON_PLASMA, 10_000L),
                          chemical(MeknucChemicals.HOT_NEUTRON_PLASMA, 10_000L)),
                    stack(MekanismItems.ANTIMATTER_PELLET.asItem(), 1), null, 0L,
                    4_800, 200L * MFE),
              // ---- 束流收集模式 ----
              new Recipe(id("hydrogen_to_hot_proton_plasma"), MODE_COLLECTION,
                    ItemStack.EMPTY, List.of(chemical(MekanismChemicals.HYDROGEN, 10_000L)),
                    ItemStack.EMPTY, MeknucChemicals.HOT_PROTON_PLASMA, 1_000L,
                    200, 10L * MFE),
              new Recipe(id("uranium_238_to_hot_neutron_plasma"), MODE_COLLECTION,
                    stack(MeknucItemBase.URANIUM_238_DUST.get(), 1), List.of(),
                    ItemStack.EMPTY, MeknucChemicals.HOT_NEUTRON_PLASMA, 1_000L,
                    200, 15L * MFE)
        );
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(meknuc.meknuc.MODID, "particle_accelerator/" + path);
    }

    private static Item item(String namespace, String path) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(namespace, path))
              .orElse(Items.AIR);
    }

    private static ItemStack stack(Item item, int count) {
        return new ItemStack(item, count);
    }

    private static ChemicalInput chemical(DeferredChemical<Chemical> chemical, long amount) {
        return new ChemicalInput(chemical, amount);
    }

    /**
     * 一种粒子束（等离子体）输入。
     */
    public record ChemicalInput(DeferredChemical<Chemical> chemical, long amount) {

        public String name() {
            return chemical.get().getTextComponent().getString();
        }

        public ChemicalStack stack() {
            return chemical.asStack(amount);
        }
    }

    /**
     * 一条粒子加速器配方。
     *
     * @param mode                工作模式（{@link #MODE_ACCELERATION} / {@link #MODE_COLLECTION}）
     * @param input               靶材输入（含数量），可以为空表示不需要靶材
     * @param chemicalInputs      粒子束输入（0~2 种化学品）
     * @param output              靶材输出，可以为空
     * @param chemicalOutput      化学品产物，可以为 {@code null}
     * @param chemicalOutputAmount 化学品产物数量（mB）
     * @param durationTicks       处理时间（tick）
     * @param energyPerTick       每 tick 能耗（FE）
     */
    public record Recipe(ResourceLocation id, int mode, ItemStack input, List<ChemicalInput> chemicalInputs,
          ItemStack output, DeferredChemical<Chemical> chemicalOutput, long chemicalOutputAmount,
          int durationTicks, long energyPerTick) {

        public boolean hasItemInput() {
            return !input.isEmpty();
        }

        public int inputCount() {
            return input.getCount();
        }

        public ItemStack inputDisplay() {
            return input.copy();
        }

        public ItemStack outputDisplay() {
            return output.copy();
        }

        public boolean hasItemOutput() {
            return !output.isEmpty();
        }

        public boolean hasChemicalOutput() {
            return chemicalOutput != null;
        }

        public ChemicalStack chemicalOutputStack() {
            return chemicalOutput == null ? ChemicalStack.EMPTY : chemicalOutput.asStack(chemicalOutputAmount);
        }

        public String inputName() {
            return input.getHoverName().getString();
        }

        public String outputName() {
            if (hasChemicalOutput()) {
                return chemicalOutput.get().getTextComponent().getString() + " " + chemicalOutputAmount + " mB";
            }
            return output.getHoverName().getString();
        }

        /** 简短输入描述：靶材 + 各等离子体。 */
        public String inputSummary() {
            StringBuilder builder = new StringBuilder();
            if (hasItemInput()) {
                builder.append(inputName()).append(" x").append(inputCount());
            }
            for (ChemicalInput chemicalInput : chemicalInputs) {
                if (builder.length() > 0) {
                    builder.append(" + ");
                }
                builder.append(chemicalInput.name()).append(' ').append(chemicalInput.amount()).append(" mB");
            }
            return builder.length() == 0 ? "-" : builder.toString();
        }

        /** 只取主要输入名，用于 GUI 小屏幕。 */
        public String inputShort() {
            if (hasItemInput()) {
                return inputName();
            }
            return chemicalInputs.isEmpty() ? "-" : chemicalInputs.get(0).name();
        }

        /** 只取主要输出名，用于 GUI 小屏幕。 */
        public String outputShort() {
            if (hasChemicalOutput()) {
                return chemicalOutput.get().getTextComponent().getString();
            }
            return output.getHoverName().getString();
        }
    }
}
