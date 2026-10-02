package meknuc.client.gui.element;

import mekanism.api.text.ILangEntry;
import mekanism.client.SpecialColors;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiTabElementType;
import mekanism.client.gui.element.tab.TabType;
import mekanism.client.render.lib.ColorAtlas.ColorRegistryObject;
import mekanism.common.network.PacketUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import meknuc.reactor.MeknucReactorGuiTarget;
import meknuc.reactor.MeknucReactorLang;
import meknuc.reactor.MeknucReactorOpenGuiPacket;
import meknuc.reactor.tile.TileEntityPressurizedWaterReactorPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MeknucReactorTab
      extends GuiTabElementType<TileEntityPressurizedWaterReactorPart, MeknucReactorTab.ReactorTab> {

    public MeknucReactorTab(IGuiWrapper gui, TileEntityPressurizedWaterReactorPart tile, ReactorTab type) {
        super(gui, tile, type);
    }

    public enum ReactorTab implements TabType<TileEntityPressurizedWaterReactorPart> {
        MAIN("radioactive.png", MeknucReactorLang.GUI_MAIN_TAB, MeknucReactorGuiTarget.MAIN,
              SpecialColors.TAB_MULTIBLOCK_MAIN),
        STAT("stats.png", MeknucReactorLang.GUI_STATS_TAB, MeknucReactorGuiTarget.STATS,
              SpecialColors.TAB_MULTIBLOCK_STATS);

        private final String path;
        private final ILangEntry description;
        private final int target;
        private final ColorRegistryObject colorRO;

        ReactorTab(String path, ILangEntry description, int target, ColorRegistryObject colorRO) {
            this.path = path;
            this.description = description;
            this.target = target;
            this.colorRO = colorRO;
        }

        @Override
        public ResourceLocation getResource() {
            return MekanismUtils.getResource(ResourceType.GUI, path);
        }

        @Override
        public void onClick(TileEntityPressurizedWaterReactorPart tile) {
            PacketUtils.sendToServer(new MeknucReactorOpenGuiPacket(tile.getBlockPos(), target));
        }

        @Override
        public Component getDescription() {
            return description.translate();
        }

        @Override
        public ColorRegistryObject getTabColor() {
            return colorRO;
        }
    }
}
