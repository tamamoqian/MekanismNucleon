package meknuc.client;

import mekanism.client.ClientRegistrationUtil;
import meknuc.Meknuc;
import meknuc.client.gui.MeknucBwrGui;
import meknuc.client.gui.MeknucBwrLogicAdapterGui;
import meknuc.client.gui.MeknucBwrStatsGui;
import meknuc.client.gui.MeknucParticleAcceleratorGui;
import meknuc.client.gui.MeknucFbrGui;
import meknuc.client.gui.MeknucPwrGui;
import meknuc.client.gui.MeknucPwrLogicAdapterGui;
import meknuc.client.gui.MeknucPwrStatsGui;
import meknuc.client.gui.MeknucSpentFuelPoolGui;
import meknuc.client.gui.MeknucWasteStorageGui;
import meknuc.particle.MeknucParticleAcceleratorContainerTypes;
import meknuc.reactor.MeknucBwrContainerTypes;
import meknuc.reactor.MeknucFbrContainerTypes;
import meknuc.reactor.MeknucPwrContainerTypes;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolContainerTypes;
import meknuc.wastestorage.MeknucWasteStorageContainerTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Meknuc.MOD_ID, value = Dist.CLIENT)
public class MeknucClientRegistration {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        ClientRegistrationUtil.registerScreen(event, MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR,
              MeknucPwrGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucFbrContainerTypes.FAST_BREEDER_REACTOR,
              MeknucFbrGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR_STATS,
              MeknucPwrStatsGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucPwrLogicAdapterGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucBwrContainerTypes.BOILING_WATER_REACTOR,
              MeknucBwrGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucBwrContainerTypes.BOILING_WATER_REACTOR_STATS,
              MeknucBwrStatsGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucBwrContainerTypes.BOILING_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucBwrLogicAdapterGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucSpentFuelPoolContainerTypes.SPENT_FUEL_POOL,
              MeknucSpentFuelPoolGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucWasteStorageContainerTypes.WASTE_STORAGE,
              MeknucWasteStorageGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucParticleAcceleratorContainerTypes.PARTICLE_ACCELERATOR,
              MeknucParticleAcceleratorGui::new);
    }
}
