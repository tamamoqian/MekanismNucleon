package meknuc.reactor;

import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockTypeTile;
import meknuc.reactor.tile.TileEntityPwrControlAssembly;
import meknuc.reactor.tile.TileEntityPwrFuelAssembly;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter;
import meknuc.reactor.tile.TileEntityPwrPart;
import meknuc.reactor.tile.TileEntityPwrPort;

public class MeknucPwrBlockTypes {

    public static final BlockTypeTile<TileEntityPwrPart> PRESSURIZED_WATER_REACTOR_CASING =
          MeknucReactorBlockTypes.createExternal(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_CASING,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_CASING,
                new AttributeGui(() -> MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR, MeknucPwrLang.GUI_TITLE),
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    public static final BlockTypeTile<TileEntityPwrPort> PRESSURIZED_WATER_REACTOR_PORT =
          MeknucReactorBlockTypes.createPort(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_PORT,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_PORT,
                new AttributeStatePwrPortMode(),
                new AttributeGui(() -> MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR, MeknucPwrLang.GUI_TITLE),
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    public static final BlockTypeTile<TileEntityPwrLogicAdapter> PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER =
          MeknucReactorBlockTypes.createLogicAdapter(
                MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                new Attribute[]{new Attributes.AttributeRedstoneEmitter<TileEntityPwrLogicAdapter>(
                      TileEntityPwrLogicAdapter::getRedstoneLevel)},
                new AttributeGui(() -> MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
                      MeknucPwrLang.GUI_LOGIC_TITLE),
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    public static final BlockTypeTile<TileEntityPwrFuelAssembly> PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY =
          MeknucReactorBlockTypes.createInternal(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_FUEL_ASSEMBLY,
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    public static final BlockTypeTile<TileEntityPwrControlAssembly> PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY =
          MeknucReactorBlockTypes.createInternal(MeknucPwrLang.DESCRIPTION_PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY,
                () -> MeknucPwrTileEntityTypes.PRESSURIZED_WATER_REACTOR_CONTROL_ASSEMBLY,
                MeknucPwrSounds.PRESSURIZED_WATER_REACTOR_RUNNING);

    private MeknucPwrBlockTypes() {
    }
}
