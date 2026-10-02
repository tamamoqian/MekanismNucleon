package meknuc.client;

import mekanism.client.ClientRegistrationUtil;
import meknuc.client.gui.MeknucReactorGui;
import meknuc.client.gui.MeknucReactorLogicAdapterGui;
import meknuc.client.gui.MeknucReactorStatsGui;
import meknuc.reactor.MeknucReactorContainerTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = meknuc.meknuc.MODID, value = Dist.CLIENT)
public class MeknucClientRegistration {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        ClientRegistrationUtil.registerScreen(event, MeknucReactorContainerTypes.PRESSURIZED_WATER_REACTOR,
              MeknucReactorGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucReactorContainerTypes.PRESSURIZED_WATER_REACTOR_STATS,
              MeknucReactorStatsGui::new);
        ClientRegistrationUtil.registerScreen(event, MeknucReactorContainerTypes.PRESSURIZED_WATER_REACTOR_LOGIC_ADAPTER,
              MeknucReactorLogicAdapterGui::new);
    }
}
