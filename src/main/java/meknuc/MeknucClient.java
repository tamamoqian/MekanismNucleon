package meknuc;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Meknuc.MOD_ID, value = Dist.CLIENT)
public class MeknucClient {

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Meknuc.LOGGER.info("HELLO FROM CLIENT SETUP");
        event.enqueueWork(() -> {
            net.neoforged.fml.ModLoadingContext.get().registerExtensionPoint(
                    net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                    () -> (modContainer, parent) -> new net.neoforged.neoforge.client.gui.ConfigurationScreen(modContainer, parent)
            );
        });
        Meknuc.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
