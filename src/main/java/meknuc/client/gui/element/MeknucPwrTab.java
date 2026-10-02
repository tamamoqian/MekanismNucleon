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
import meknuc.reactor.MeknucPwrGuiTarget;
import meknuc.reactor.MeknucPwrLang;
import meknuc.reactor.MeknucPwrOpenGuiPacket;
import meknuc.reactor.tile.TileEntityPwrPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MeknucPwrTab
      extends GuiTabElementType<TileEntityPwrPart, MeknucPwrTab.PwrTab> {

    public MeknucPwrTab(IGuiWrapper gui, TileEntityPwrPart tile, PwrTab type) {
        super(gui, tile, type);
    }

    public enum PwrTab implements TabType<TileEntityPwrPart> {
        MAIN("radioactive.png", MeknucPwrLang.GUI_MAIN_TAB, MeknucPwrGuiTarget.MAIN,
              SpecialColors.TAB_MULTIBLOCK_MAIN),
        STAT("stats.png", MeknucPwrLang.GUI_STATS_TAB, MeknucPwrGuiTarget.STATS,
              SpecialColors.TAB_MULTIBLOCK_STATS);

        private final String path;
        private final ILangEntry description;
        private final int target;
        private final ColorRegistryObject colorRO;

        PwrTab(String path, ILangEntry description, int target, ColorRegistryObject colorRO) {
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
        public void onClick(TileEntityPwrPart tile) {
            PacketUtils.sendToServer(new MeknucPwrOpenGuiPacket(tile.getBlockPos(), target));
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
