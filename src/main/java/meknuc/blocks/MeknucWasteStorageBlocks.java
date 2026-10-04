package meknuc.blocks;

import java.util.function.Supplier;

import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class MeknucWasteStorageBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<MeknucBlockRadiationResistantWasteStorageCasing, ItemBlockTooltip<MeknucBlockRadiationResistantWasteStorageCasing>> RADIATION_RESISTANT_WASTE_STORAGE_CASING =
          register("radiation_resistant_waste_storage_casing", () -> new MeknucBlockRadiationResistantWasteStorageCasing(properties()));

    public static final BlockRegistryObject<MeknucBlockRadiationResistantWasteStoragePort, ItemBlockTooltip<MeknucBlockRadiationResistantWasteStoragePort>> RADIATION_RESISTANT_WASTE_STORAGE_PORT =
          register("radiation_resistant_waste_storage_port", () -> new MeknucBlockRadiationResistantWasteStoragePort(MeknucBlockRadiationResistantWasteStoragePort.BLOCK_TYPE, properties()));

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 5.0F).requiresCorrectToolForDrops();
    }

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> register(
          String name, Supplier<BLOCK> supplier) {
        return BLOCKS.registerDetails(name, supplier);
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(RADIATION_RESISTANT_WASTE_STORAGE_CASING);
        output.accept(RADIATION_RESISTANT_WASTE_STORAGE_PORT);
    }

    private MeknucWasteStorageBlocks() {
    }
}
