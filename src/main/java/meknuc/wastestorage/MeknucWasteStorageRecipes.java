package meknuc.wastestorage;

import java.util.List;
import meknuc.items.MeknucItemBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 放射性废料存储罐的衰变配方表。
 * <p>
 * 五条配方共用存储罐的「并行线程池」，每个线程按下面的时间独立计时：
 * <ul>
 *     <li>镅-241 粉 → 钌粉，600 秒；</li>
 *     <li>钚-239 粉 → 铀-235 粉，300 秒；</li>
 *     <li>铀-235 粉 → 铅粉，800 秒；</li>
 *     <li>铀-233 粉 → 钍粉，600 秒；</li>
 *     <li>钋-210 粉 → 铅粉，100 秒。</li>
 * </ul>
 * 铀-235 粉既是钚-239 的产物、也是「铀-235 → 铅」的输入：
 * 只有存在废料存储空间里的铀-235 才会继续衰变，衰变完成缓存里的铀-235 不会再被消耗。
 * <p>
 * 配方列表延迟构建，避免在注册表冻结之前解析物品。
 */
public final class MeknucWasteStorageRecipes {

    /** 配方条数（GUI 固定显示这么多行）。 */
    public static final int RECIPE_COUNT = 5;

    private static List<Recipe> recipes;

    private MeknucWasteStorageRecipes() {
    }

    /**
     * 全部衰变配方，顺序即 GUI 从上到下的显示顺序。
     */
    public static List<Recipe> recipes() {
        if (recipes == null) {
            recipes = List.of(
                  new Recipe("americium_241", MeknucItemBase.AMERICIUM_241_DUST.get(),
                        MeknucItemBase.RUTHENIUM_DUST.get(), 600),
                  new Recipe("plutonium_239", MeknucItemBase.PLUTONIUM_239_DUST.get(),
                        MeknucItemBase.URANIUM_235_DUST.get(), 300),
                  new Recipe("uranium_235", MeknucItemBase.URANIUM_235_DUST.get(),
                        item("mekanism", "dust_lead"), 800),
                  new Recipe("uranium_233", MeknucItemBase.URANIUM_233_DUST.get(),
                        MeknucItemBase.THORIUM_DUST.get(), 600),
                  new Recipe("polonium_210", MeknucItemBase.POLONIUM_210_DUST.get(),
                        item("mekanism", "dust_lead"), 100)
            );
        }
        return recipes;
    }

    /**
     * 是否是「衰变完成产物」——只有这些物品能进入衰变完成缓存并允许从输出端口抽出。
     */
    public static boolean isDecayProduct(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (Recipe recipe : recipes()) {
            if (stack.is(recipe.output())) {
                return true;
            }
        }
        return false;
    }

    private static Item item(String namespace, String path) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(namespace, path))
              .orElse(Items.AIR);
    }

    /**
     * 一条衰变配方：1 个输入物品在 {@link #durationTicks()} 刻后变成 1 个输出物品。
     */
    public static final class Recipe {

        private final String id;
        private final Item input;
        private final Item output;
        private final int durationSeconds;
        private Component inputName;
        private Component outputName;

        private Recipe(String id, Item input, Item output, int durationSeconds) {
            this.id = id;
            this.input = input;
            this.output = output;
            this.durationSeconds = durationSeconds;
        }

        public String id() {
            return id;
        }

        public Item input() {
            return input;
        }

        public Item output() {
            return output;
        }

        public int durationSeconds() {
            return durationSeconds;
        }

        public int durationTicks() {
            return durationSeconds * 20;
        }

        public boolean matchesInput(ItemStack stack) {
            return !stack.isEmpty() && stack.is(input);
        }

        public ItemStack inputStack(int count) {
            return new ItemStack(input, count);
        }

        public ItemStack outputStack(int count) {
            return new ItemStack(output, count);
        }

        /**
         * 输入物品的显示名（延迟缓存，避免每帧都新建物品堆）。
         */
        public Component inputName() {
            if (inputName == null) {
                inputName = inputStack(1).getHoverName();
            }
            return inputName;
        }

        /**
         * 输出物品的显示名。
         */
        public Component outputName() {
            if (outputName == null) {
                outputName = outputStack(1).getHoverName();
            }
            return outputName;
        }
    }
}
