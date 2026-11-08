package meknuc.reactor;

import java.util.List;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.item.block.ItemBlockTooltip;
import meknuc.items.MeknucItemBase;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter;
import meknuc.reactor.tile.TileEntityPwrLogicAdapter.PressurizedWaterReactorLogic;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class ItemBlockPwrLogicAdapter extends ItemBlockTooltip<BlockBasicMultiblock<TileEntityPwrLogicAdapter>> {

    public ItemBlockPwrLogicAdapter(BlockBasicMultiblock<TileEntityPwrLogicAdapter> block, Properties properties) {
        super(block, true, properties.component(MeknucItemBase.PWR_LOGIC_TYPE, PressurizedWaterReactorLogic.DISABLED));
    }

    @Override
    protected void addDetails(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addDetails(stack, context, tooltip, flag);
        PressurizedWaterReactorLogic logicType = stack.getOrDefault(MeknucItemBase.PWR_LOGIC_TYPE, PressurizedWaterReactorLogic.DISABLED);
        tooltip.add(MeknucPwrLang.REACTOR_LOGIC_REDSTONE_MODE.translate(logicType.getColor(), logicType.getTranslatedName()));
    }
}
