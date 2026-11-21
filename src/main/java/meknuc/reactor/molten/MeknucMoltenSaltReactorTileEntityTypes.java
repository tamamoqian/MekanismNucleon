package meknuc.reactor.molten;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import meknuc.Meknuc;
import meknuc.blockentities.MeknucBlockEntityReactorPart;
import meknuc.blockentities.MeknucBlockEntityReactorPort;

public class MeknucMoltenSaltReactorTileEntityTypes {

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Meknuc.MOD_ID);

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CASING, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_CONTROL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPart> THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_CORROSION_RESISTANT_FUEL_ASSEMBLY, MeknucBlockEntityReactorPart::new)
                .build();

    public static final TileEntityTypeRegistryObject<MeknucBlockEntityReactorPort> THORIUM_MOLTEN_SALT_REACTOR_PORT =
          TILE_ENTITY_TYPES.mekBuilder(MeknucMoltenSaltReactorBlocks.THORIUM_MOLTEN_SALT_REACTOR_PORT, MeknucBlockEntityReactorPort::new)
                .withSimple(Capabilities.CONFIGURABLE)
                .build();

    private MeknucMoltenSaltReactorTileEntityTypes() {
    }
}
