package meknuc.blockentities;

import meknuc.blocks.MeknucBlockBase;
import meknuc.meknuc;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;

public class MeknucTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(meknuc.MODID);

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityRTG> RADIOISOTOPE_THERMOELECTRIC_GENERATOR =
          TILE_ENTITY_TYPES.mekBuilder(MeknucBlockBase.RADIOISOTOPE_THERMOELECTRIC_GENERATOR, MeknucBlockEntityRTG::new)
                .serverTicker((level, pos, state, be) -> TileEntityMekanism.tickServer(level, pos, state, be))
                .build();
}
