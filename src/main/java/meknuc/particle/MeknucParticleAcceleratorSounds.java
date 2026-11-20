package meknuc.particle;

import mekanism.common.registration.impl.SoundEventDeferredRegister;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import net.minecraft.sounds.SoundEvent;

/**
 * 粒子加速器运行音效（来自用户提供的“粒子对撞机两分钟白噪音”）。
 */
public class MeknucParticleAcceleratorSounds {

    public static final SoundEventDeferredRegister SOUND_EVENTS = new SoundEventDeferredRegister("mekanism");

    public static final SoundEventRegistryObject<SoundEvent> PARTICLE_ACCELERATOR_RUNNING =
          SOUND_EVENTS.register("tile.machine.particle_accelerator_running");

    private MeknucParticleAcceleratorSounds() {
    }
}
