package meknuc.reactor.spentpool;

import mekanism.common.registration.impl.SoundEventDeferredRegister;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import net.minecraft.sounds.SoundEvent;

/**
 * 乏燃料池音效。
 * <p>
 * “盖革咔嗒”样本取自 Mekanism 的 {@code geiger_slow} 循环中的单个咔嗒声
 * （Mekanism 为 MIT 许可），固定每 20 tick 播放一次，与燃料棒数量无关。
 */
public class MeknucSpentFuelPoolSounds {

    public static final SoundEventDeferredRegister SOUND_EVENTS = new SoundEventDeferredRegister(meknuc.meknuc.MODID);

    public static final SoundEventRegistryObject<SoundEvent> SPENT_FUEL_POOL_GEIGER =
          SOUND_EVENTS.register("tile.machine.spent_fuel_pool_geiger");

    private MeknucSpentFuelPoolSounds() {
    }
}
