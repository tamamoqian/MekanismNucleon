package meknuc.reactor;

import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockTypeTile;
import meknuc.reactor.tile.TileEntityBwrControlAssembly;
import meknuc.reactor.tile.TileEntityBwrFuelAssembly;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import meknuc.reactor.tile.TileEntityBwrPart;
import meknuc.reactor.tile.TileEntityBwrPort;

public class MeknucBwrBlockTypes {

    public static final BlockTypeTile<TileEntityBwrPart> BOILING_WATER_REACTOR_CASING =
          MeknucReactorBlockTypes.createExternal(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_CASING,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_CASING,
                new AttributeGui(() -> MeknucBwrContainerTypes.BOILING_WATER_REACTOR, MeknucBwrLang.GUI_TITLE),
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    public static final BlockTypeTile<TileEntityBwrPort> BOILING_WATER_REACTOR_PORT =
          MeknucReactorBlockTypes.createPort(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_PORT,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_PORT,
                new AttributeStateBwrPortMode(),
                new AttributeGui(() -> MeknucBwrContainerTypes.BOILING_WATER_REACTOR, MeknucBwrLang.GUI_TITLE),
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    public static final BlockTypeTile<TileEntityBwrLogicAdapter> BOILING_WATER_REACTOR_LOGIC_ADAPTER =
          MeknucReactorBlockTypes.createLogicAdapter(
                MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_LOGIC_ADAPTER,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_LOGIC_ADAPTER,
                new Attribute[]{new Attributes.AttributeRedstoneEmitter<TileEntityBwrLogicAdapter>(
                      TileEntityBwrLogicAdapter::getRedstoneLevel),
                      new AttributeStateBwrLogicAdapterMode<>(
                            AttributeStateBwrLogicAdapterMode.modeProperty, BwrLogicAdapterMode.DISABLED)},
                new AttributeGui(() -> MeknucBwrContainerTypes.BOILING_WATER_REACTOR_LOGIC_ADAPTER,
                      MeknucBwrLang.GUI_LOGIC_TITLE),
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    public static final BlockTypeTile<TileEntityBwrFuelAssembly> BOILING_WATER_REACTOR_FUEL_ASSEMBLY =
          MeknucReactorBlockTypes.createInternal(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_FUEL_ASSEMBLY,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_FUEL_ASSEMBLY,
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    public static final BlockTypeTile<TileEntityBwrControlAssembly> BOILING_WATER_REACTOR_CONTROL_ASSEMBLY =
          MeknucReactorBlockTypes.createInternal(MeknucBwrLang.DESCRIPTION_BOILING_WATER_REACTOR_CONTROL_ASSEMBLY,
                () -> MeknucBwrTileEntityTypes.BOILING_WATER_REACTOR_CONTROL_ASSEMBLY,
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    private MeknucBwrBlockTypes() {
    }
}
