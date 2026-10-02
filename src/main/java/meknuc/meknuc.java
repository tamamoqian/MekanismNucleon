package meknuc;

import meknuc.blocks.MeknucBlockBase;
import meknuc.chemicals.MeknucChemicals;
import meknuc.items.MeknucItemBase;
import meknuc.reactor.MeknucReactor;
import meknuc.reactor.MeknucReactorBlocks;
import meknuc.reactor.MeknucReactorContainerTypes;
import meknuc.reactor.MeknucReactorFuels;
import meknuc.reactor.MeknucReactorLogicPacket;
import meknuc.reactor.MeknucReactorOpenGuiPacket;
import meknuc.reactor.MeknucReactorTileEntityTypes;
import meknuc.reactor.MeknucSounds;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(meknuc.MODID)
public class meknuc {
    public static final String MODID = "meknuc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MEKNUC_TAB = CREATIVE_MODE_TABS.register("meknuc_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.meknuc"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> MeknucItemBase.BERYLLIUM_INGOT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                MeknucItemBase.addTabItems(output);
                MeknucBlockBase.addTabBlocks(output);
                MeknucReactorBlocks.addTabBlocks(output);
            }).build());

    public meknuc(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(MeknucReactorFuels::register);

        MeknucReactor.initialize();
        MeknucBlockBase.BLOCKS.register(modEventBus);
        MeknucItemBase.ITEMS.register(modEventBus);
        MeknucReactorBlocks.BLOCKS.register(modEventBus);
        MeknucChemicals.CHEMICALS.register(modEventBus);
        MeknucReactorTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucReactorContainerTypes.CONTAINER_TYPES.register(modEventBus);
        MeknucSounds.SOUND_EVENTS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(MeknucReactorLogicPacket.TYPE, MeknucReactorLogicPacket.STREAM_CODEC,
              MeknucReactorLogicPacket::handle);
        registrar.playToServer(MeknucReactorOpenGuiPacket.TYPE, MeknucReactorOpenGuiPacket.STREAM_CODEC,
              MeknucReactorOpenGuiPacket::handle);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
