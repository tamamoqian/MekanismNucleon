package meknuc.reactor;

import meknuc.Meknuc;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter.PressurizedWaterReactorLogic;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MeknucReactorComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Meknuc.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PressurizedWaterReactorLogic>> PWR_LOGIC_TYPE =
            DATA_COMPONENTS.register("pwr_logic_type", () -> DataComponentType.<PressurizedWaterReactorLogic>builder()
                    .persistent(PressurizedWaterReactorLogic.CODEC)
                    .networkSynchronized(PressurizedWaterReactorLogic.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BwrLogicAdapterMode>> BWR_LOGIC_TYPE =
            DATA_COMPONENTS.register("bwr_logic_type", () -> DataComponentType.<BwrLogicAdapterMode>builder()
                    .persistent(BwrLogicAdapterMode.CODEC)
                    .networkSynchronized(BwrLogicAdapterMode.STREAM_CODEC).build());
}
