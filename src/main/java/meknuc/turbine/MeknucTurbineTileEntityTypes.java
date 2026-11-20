package meknuc.turbine;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.Meknuc;
import meknuc.turbine.tile.TileEntityHighPressureTurbineCasing;
import meknuc.turbine.tile.TileEntityHighPressureTurbineCondenser;
import meknuc.turbine.tile.TileEntityHighPressureTurbinePort;
import meknuc.turbine.tile.TileEntityHighPressureTurbineVent;

/**
 * 高压蒸汽涡轮方块对应的方块实体类型。
 */
public class MeknucTurbineTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<TileEntityHighPressureTurbineCasing> HIGH_PRESSURE_STEAM_TURBINE_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucTurbineBlocks.HIGH_PRESSURE_STEAM_TURBINE_CASING, TileEntityHighPressureTurbineCasing::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityHighPressureTurbinePort> HIGH_PRESSURE_STEAM_TURBINE_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucTurbineBlocks.HIGH_PRESSURE_STEAM_TURBINE_PORT, TileEntityHighPressureTurbinePort::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityHighPressureTurbineVent> HIGH_PRESSURE_STEAM_TURBINE_VENT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucTurbineBlocks.HIGH_PRESSURE_STEAM_TURBINE_VENT, TileEntityHighPressureTurbineVent::new)
                .clientTicker(TileEntityMekanism::tickClient)
                .serverTicker(TileEntityMekanism::tickServer)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    public static final TileEntityTypeRegistryObject<TileEntityHighPressureTurbineCondenser> HIGH_PRESSURE_STEAM_CONDENSER =
          TILE_ENTITY_TYPES.mekBuilder(MeknucTurbineBlocks.HIGH_PRESSURE_STEAM_CONDENSER, TileEntityHighPressureTurbineCondenser::new)
                .serverTicker(TileEntityMekanism::tickServer)
                .build();

    private MeknucTurbineTileEntityTypes() {
    }
}
