package meknuc.client;

import meknuc.Meknuc;
import meknuc.client.gui.MeknucGuiRTG;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucItems;
import meknuc.menu.MeknucMenuTypes;
import meknuc.proxy.IClientRegister;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Meknuc.MOD_ID, value = Dist.CLIENT)
public class MeknucClient {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MeknucMenuTypes.RTG.get(), MeknucGuiRTG::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(IClientRegister::registerAllClients);
        event.enqueueWork(() -> ItemProperties.register(MeknucItems.FUEL_ROD.get(),
              ResourceLocation.fromNamespaceAndPath(Meknuc.MOD_ID, "rod_variant"),
              (stack, level, entity, seed) -> MeknucItemFuelRodBase.variantValue(stack)));
    }
}
