package meknuc;

import meknuc.blocks.MeknucBlock;
import meknuc.blocks.MeknucBlockTileEntityTypes;
import meknuc.particle.MeknucParticleAcceleratorTileEntityTypes;
import meknuc.reactor.fbr.MeknucFbrTileEntityTypes;
import meknuc.reactor.htgr.MeknucHtgrTileEntityTypes;
import meknuc.reactor.hwr.MeknucHwrTileEntityTypes;
import meknuc.reactor.molten.MeknucMoltenSaltReactorTileEntityTypes;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolTileEntityTypes;
import meknuc.reactor.fbr.MeknucFbrBlocks;
import meknuc.reactor.htgr.MeknucHtgrBlocks;
import meknuc.reactor.hwr.MeknucHwrBlocks;
import meknuc.reactor.molten.MeknucMoltenSaltReactorBlocks;
import meknuc.blocks.MeknucWasteStorageBlocks;
import meknuc.chemicals.MeknucChemicals;
import meknuc.items.MeknucItems;
import meknuc.menu.MeknucMenuTypes;
import meknuc.modules.MeknucModules;
import meknuc.modules.MeknucSuitModuleEvents;
import meknuc.particle.MeknucParticleAcceleratorBlocks;
import meknuc.particle.MeknucParticleAcceleratorContainerTypes;
import meknuc.particle.MeknucParticleAcceleratorMultiblock;
import meknuc.particle.MeknucParticleAcceleratorSounds;
import meknuc.reactor.MeknucPwrMultiblock;
import meknuc.reactor.MeknucPwrBlocks;
import meknuc.reactor.MeknucPwrContainerTypes;
import meknuc.reactor.MeknucReactorLogicPacket;
import meknuc.reactor.MeknucReactorOpenGuiPacket;
import meknuc.reactor.MeknucPwrTileEntityTypes;
import meknuc.reactor.MeknucPwrSounds;
import meknuc.reactor.MeknucReactorComponents;
import meknuc.reactor.MeknucBwrBlocks;
import meknuc.reactor.MeknucBwrContainerTypes;
import meknuc.reactor.MeknucBwrMultiblock;
import meknuc.reactor.MeknucBwrTileEntityTypes;
import meknuc.reactor.fbr.MeknucFbrBlocks;
import meknuc.reactor.htgr.MeknucHtgrBlocks;
import meknuc.reactor.hwr.MeknucHwrBlocks;
import meknuc.reactor.molten.MeknucMoltenSaltReactorBlocks;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolBlocks;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolContainerTypes;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolMultiblock;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolSounds;
import meknuc.turbine.MeknucTurbineBlocks;
import meknuc.turbine.MeknucTurbineMultiblock;
import meknuc.turbine.MeknucTurbineTileEntityTypes;
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

@Mod(Meknuc.MOD_ID)
public class Meknuc {
    public static final String MOD_ID = "meknuc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MEKNUC_TAB = CREATIVE_MODE_TABS.register("meknuc_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.meknuc"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> MeknucItems.BERYLLIUM_INGOT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                MeknucItems.addTabItems(output);
                MeknucModules.addTabItems(output);
                MeknucBlock.addTabBlocks(output);
                MeknucPwrBlocks.addTabBlocks(output);
                MeknucBwrBlocks.addTabBlocks(output);
                MeknucHwrBlocks.addTabBlocks(output);
                MeknucSpentFuelPoolBlocks.addTabBlocks(output);
                MeknucFbrBlocks.addTabBlocks(output);
                MeknucHtgrBlocks.addTabBlocks(output);
                MeknucParticleAcceleratorBlocks.addTabBlocks(output);
                MeknucMoltenSaltReactorBlocks.addTabBlocks(output);
                MeknucWasteStorageBlocks.addTabBlocks(output);
                MeknucTurbineBlocks.addTabBlocks(output);
            }).build());

    public Meknuc(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);

        MeknucPwrMultiblock.initialize();
        MeknucBwrMultiblock.initialize();
        MeknucSpentFuelPoolMultiblock.initialize();
        MeknucParticleAcceleratorMultiblock.initialize();
        MeknucTurbineMultiblock.initialize();
        MeknucBlock.BLOCKS.register(modEventBus);
        MeknucHwrBlocks.BLOCKS.register(modEventBus);
        MeknucSpentFuelPoolBlocks.BLOCKS.register(modEventBus);
        MeknucFbrBlocks.BLOCKS.register(modEventBus);
        MeknucHtgrBlocks.BLOCKS.register(modEventBus);
        MeknucParticleAcceleratorBlocks.BLOCKS.register(modEventBus);
        MeknucMoltenSaltReactorBlocks.BLOCKS.register(modEventBus);
        MeknucWasteStorageBlocks.BLOCKS.register(modEventBus);
        MeknucTurbineBlocks.BLOCKS.register(modEventBus);
        MeknucItems.ITEMS.register(modEventBus);
        MeknucItems.DATA_COMPONENTS.register(modEventBus);
        MeknucReactorComponents.DATA_COMPONENTS.register(modEventBus);
        MeknucModules.MODULES.register(modEventBus);
        MeknucPwrBlocks.BLOCKS.register(modEventBus);
        MeknucBwrBlocks.BLOCKS.register(modEventBus);
        MeknucChemicals.CHEMICALS.register(modEventBus);
        MeknucPwrTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucPwrContainerTypes.CONTAINER_TYPES.register(modEventBus);
        MeknucBwrTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucBwrContainerTypes.CONTAINER_TYPES.register(modEventBus);
        MeknucBlockTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucHwrTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucFbrTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucHtgrTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucMoltenSaltReactorTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucSpentFuelPoolTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucParticleAcceleratorTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucTurbineTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        MeknucMenuTypes.CONTAINER_TYPES.register(modEventBus);
        MeknucSpentFuelPoolContainerTypes.CONTAINER_TYPES.register(modEventBus);
        MeknucSpentFuelPoolSounds.SOUND_EVENTS.register(modEventBus);
        MeknucPwrSounds.SOUND_EVENTS.register(modEventBus);
        MeknucParticleAcceleratorSounds.SOUND_EVENTS.register(modEventBus);
        MeknucParticleAcceleratorContainerTypes.CONTAINER_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new MeknucSuitModuleEvents());
        MeknucModules.sendIMC();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("2");
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
