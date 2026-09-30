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
    public static final DeferredBlock<MeknucBlock> MEKNUC_BLOCK = register("meknuc_block",
            () -> new MeknucBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

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
