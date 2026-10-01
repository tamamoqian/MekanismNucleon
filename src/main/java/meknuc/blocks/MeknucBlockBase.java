package meknuc.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import meknuc.meknuc;
import meknuc.items.MeknucItemBase;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MeknucBlockBase extends Block {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(meknuc.MODID);
    private static final List<Supplier<ItemStack>> TAB_BLOCKS = new ArrayList<>();
    public static final DeferredBlock<MeknucBlockOreZircon> ZIRCON_BLOCK_ORE_BLOCK = register("zircon_ore_block",
            () -> new MeknucBlockOreZircon(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemZircon> ZIRCON_ORE_ITEM_BLOCK = register("zircon_ore_item_block",
            () -> new MeknucBlockOreItemZircon(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockZircon> ZIRCON_BLOCK = register("zircon_block",
            () -> new MeknucBlockZircon(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreThorium> THORIUM_BLOCK_ORE_BLOCK = register("thorium_ore_block",
            () -> new MeknucBlockOreThorium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemThorium> THORIUM_ORE_ITEM_BLOCK = register("thorium_ore_item_block",
            () -> new MeknucBlockOreItemThorium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockThorium> THORIUM_BLOCK = register("thorium_block",
            () -> new MeknucBlockThorium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreBeryllium> BERYLLIUM_BLOCK_ORE_BLOCK = register("beryllium_ore_block",
            () -> new MeknucBlockOreBeryllium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemBeryllium> BERYLLIUM_ORE_ITEM_BLOCK = register("beryllium_ore_item_block",
            () -> new MeknucBlockOreItemBeryllium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBeryllium> BERYLLIUM_BLOCK = register("beryllium_block",
            () -> new MeknucBlockBeryllium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreBoron> BORON_BLOCK_ORE_BLOCK = register("boron_ore_block",
            () -> new MeknucBlockOreBoron(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemBoron> BORON_ORE_ITEM_BLOCK = register("boron_ore_item_block",
            () -> new MeknucBlockOreItemBoron(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockBoron> BORON_BLOCK = register("boron_block",
            () -> new MeknucBlockBoron(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreChrome> CHROME_BLOCK_ORE_BLOCK = register("chrome_ore_block",
            () -> new MeknucBlockOreChrome(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreItemChrome> CHROME_ORE_ITEM_BLOCK = register("chrome_ore_item_block",
            () -> new MeknucBlockOreItemChrome(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockChrome> CHROME_BLOCK = register("chrome_block",
            () -> new MeknucBlockChrome(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateThorium> THORIUM_DEEPSLATE_ORE_BLOCK = register("thorium_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateThorium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateZircon> ZIRCON_DEEPSLATE_ORE_BLOCK = register("zircon_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateZircon(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateChrome> CHROME_DEEPSLATE_ORE_BLOCK = register("chrome_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateChrome(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateBoron> BORON_DEEPSLATE_ORE_BLOCK = register("boron_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateBoron(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeknucBlockOreDeepslateBeryllium> BERYLLIUM_DEEPSLATE_ORE_BLOCK = register("beryllium_deepslate_ore_block",
            () -> new MeknucBlockOreDeepslateBeryllium(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops()));

    public MeknucBlockBase(Properties properties) {
        super(properties);
    }

    private static <T extends MeknucBlockBase> DeferredBlock<T> register(String name, Supplier<T> supplier) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        DeferredItem<BlockItem> blockItem = MeknucItemBase.ITEMS.registerSimpleBlockItem(name, block);
        TAB_BLOCKS.add(() -> blockItem.get().getDefaultInstance());
        return block;
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        TAB_BLOCKS.forEach(supplier -> output.accept(supplier.get()));
    }
}
