package meknuc.reactor;

import mekanism.common.registration.impl.SoundEventDeferredRegister;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import net.minecraft.sounds.SoundEvent;

public class MeknucPwrSounds {

    public static final SoundEventDeferredRegister SOUND_EVENTS = new SoundEventDeferredRegister("mekanism");

    public static final SoundEventRegistryObject<SoundEvent> PRESSURIZED_WATER_REACTOR_RUNNING =
          SOUND_EVENTS.register("tile.machine.pressurized_water_reactor_running");

    private MeknucPwrSounds() {
    }
}
