package meknuc.turbine;

import java.util.function.Supplier;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import meknuc.turbine.tile.TileEntityHighPressureTurbineCasing;
import meknuc.turbine.tile.TileEntityHighPressureTurbineCondenser;
import meknuc.turbine.tile.TileEntityHighPressureTurbinePort;
import meknuc.turbine.tile.TileEntityHighPressureTurbineVent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

/**
 * 高压蒸汽涡轮的方块与方块物品注册。
 */
public class MeknucTurbineBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister("meknuc");

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityHighPressureTurbineCasing>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityHighPressureTurbineCasing>>> HIGH_PRESSURE_STEAM_TURBINE_CASING =
          registerTooltipBlock("high_pressure_steam_turbine_casing",
                () -> new BlockBasicMultiblock<>(MeknucTurbineBlockTypes.HIGH_PRESSURE_STEAM_TURBINE_CASING,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityHighPressureTurbinePort>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityHighPressureTurbinePort>>> HIGH_PRESSURE_STEAM_TURBINE_PORT =
          registerTooltipBlock("high_pressure_steam_turbine_port",
                () -> new BlockBasicMultiblock<>(MeknucTurbineBlockTypes.HIGH_PRESSURE_STEAM_TURBINE_PORT,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockBasicMultiblock<TileEntityHighPressureTurbineVent>,
          ItemBlockTooltip<BlockBasicMultiblock<TileEntityHighPressureTurbineVent>>> HIGH_PRESSURE_STEAM_TURBINE_VENT =
          registerTooltipBlock("high_pressure_steam_turbine_vent",
                () -> new BlockBasicMultiblock<>(MeknucTurbineBlockTypes.HIGH_PRESSURE_STEAM_TURBINE_VENT,
                      properties -> properties.mapColor(MapColor.METAL)));

    public static final BlockRegistryObject<BlockTileModel<TileEntityHighPressureTurbineCondenser,
          BlockTypeTile<TileEntityHighPressureTurbineCondenser>>,
          ItemBlockTooltip<BlockTileModel<TileEntityHighPressureTurbineCondenser, BlockTypeTile<TileEntityHighPressureTurbineCondenser>>>> HIGH_PRESSURE_STEAM_CONDENSER =
          registerTooltipBlock("high_pressure_steam_condenser",
                () -> new BlockTileModel<>(MeknucTurbineBlockTypes.HIGH_PRESSURE_STEAM_CONDENSER,
                      properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY)));

    private static <BLOCK extends Block & IHasDescription> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerTooltipBlock(
          String name, Supplier<BLOCK> blockCreator) {
        return BLOCKS.register(name, blockCreator, (block, properties) -> new ItemBlockTooltip<>(block, properties));
    }

    public static void addTabBlocks(CreativeModeTab.Output output) {
        output.accept(HIGH_PRESSURE_STEAM_TURBINE_CASING);
        output.accept(HIGH_PRESSURE_STEAM_TURBINE_PORT);
        output.accept(HIGH_PRESSURE_STEAM_TURBINE_VENT);
        output.accept(HIGH_PRESSURE_STEAM_CONDENSER);
    }

    private MeknucTurbineBlocks() {
    }
}
