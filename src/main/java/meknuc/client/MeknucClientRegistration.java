package meknuc.client;

import mekanism.client.ClientRegistrationUtil;
import meknuc.client.gui.MeknucPwrGui;
import meknuc.client.gui.MeknucPwrLogicAdapterGui;
import meknuc.client.gui.MeknucPwrStatsGui;
import meknuc.reactor.MeknucPwrContainerTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = meknuc.meknuc.MODID, value = Dist.CLIENT)
public class MeknucClientRegistration {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        ClientRegistrationUtil.registerScreen(event, MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR,
              MeknucPwrGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR_STATS,
              MeknucPwrStatsGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucPwrContainerTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucPwrLogicAdapterGui::new);
    }
}
