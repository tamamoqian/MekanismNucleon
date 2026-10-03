package meknuc.client;

import meknuc.client.gui.MeknucRtgGui;
import meknuc.items.MeknucItemBase;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.menu.MeknucMenuTypes;
import meknuc.meknuc;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = meknuc.MODID, value = Dist.CLIENT)
public class MeknucClient {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MeknucMenuTypes.RTG.get(), MeknucRtgGui::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(MeknucItemBase.FUEL_ROD.get(),
              ResourceLocation.fromNamespaceAndPath(meknuc.MODID, "rod_variant"),
              (stack, level, entity, seed) -> MeknucItemFuelRodBase.variantValue(stack)));
    }
}
