package meknuc;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = meknuc.MODID, value = Dist.CLIENT)
public class meknucClient {

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        meknuc.LOGGER.info("HELLO FROM CLIENT SETUP");
        event.enqueueWork(() -> {
            net.neoforged.fml.ModLoadingContext.get().registerExtensionPoint(
                    net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                    () -> (modContainer, parent) -> new net.neoforged.neoforge.client.gui.ConfigurationScreen(modContainer, parent)
            );
        });
        meknuc.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
