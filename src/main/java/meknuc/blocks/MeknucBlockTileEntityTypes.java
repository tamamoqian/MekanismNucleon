package meknuc.blocks;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import meknuc.Meknuc;
import meknuc.blockentities.MeknucBlockEntityRTG;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;

public class MeknucBlockTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityRTG> RADIOISOTOPE_THERMOELECTRIC_GENERATOR =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBlock.RADIOISOTOPE_THERMOELECTRIC_GENERATOR, MeknucBlockEntityRTG::new)
                .serverTicker((level, pos, state, be) -> TileEntityMekanism.tickServer(level, pos, state, be))
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> RADIATION_RESISTANT_WASTE_STORAGE_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucWasteStorageBlocks.RADIATION_RESISTANT_WASTE_STORAGE_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> RADIATION_RESISTANT_WASTE_STORAGE_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucWasteStorageBlocks.RADIATION_RESISTANT_WASTE_STORAGE_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    private MeknucBlockTileEntityTypes() {
    }
}
