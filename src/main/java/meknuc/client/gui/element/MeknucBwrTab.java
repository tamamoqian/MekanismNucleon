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
import meknuc.reactor.MeknucBwrGuiTarget;
import meknuc.reactor.MeknucBwrLang;
import meknuc.reactor.MeknucBwrOpenGuiPacket;
import meknuc.reactor.tile.TileEntityBwrPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MeknucBwrTab
      extends GuiTabElementType<TileEntityBwrPart, MeknucBwrTab.BwrTab> {

    public MeknucBwrTab(IGuiWrapper gui, TileEntityBwrPart tile, BwrTab type) {
        super(gui, tile, type);
    }

    public enum BwrTab implements TabType<TileEntityBwrPart> {
        MAIN("radioactive.png", MeknucBwrLang.GUI_MAIN_TAB, MeknucBwrGuiTarget.MAIN,
              SpecialColors.TAB_MULTIBLOCK_MAIN),
        STAT("stats.png", MeknucBwrLang.GUI_STATS_TAB, MeknucBwrGuiTarget.STATS,
              SpecialColors.TAB_MULTIBLOCK_STATS);

        private final String path;
        private final ILangEntry description;
        private final int target;
        private final ColorRegistryObject colorRO;

        BwrTab(String path, ILangEntry description, int target, ColorRegistryObject colorRO) {
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
        public void onClick(TileEntityBwrPart tile) {
            PacketUtils.sendToServer(new MeknucBwrOpenGuiPacket(tile.getBlockPos(), target));
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
