package meknuc.reactor;

import java.util.List;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.item.block.ItemBlockTooltip;
import meknuc.items.MeknucItemBase;
import meknuc.reactor.tile.TileEntityBwrLogicAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class ItemBlockBwrLogicAdapter extends ItemBlockTooltip<BlockBasicMultiblock<TileEntityBwrLogicAdapter>> {

    public ItemBlockBwrLogicAdapter(BlockBasicMultiblock<TileEntityBwrLogicAdapter> block, Properties properties) {
        super(block, true, properties.component(MeknucItemBase.BWR_LOGIC_TYPE, BwrLogicAdapterMode.DISABLED));
    }

    @Override
    protected void addDetails(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.addDetails(stack, context, tooltip, flag);
        BwrLogicAdapterMode logicType = stack.getOrDefault(MeknucItemBase.BWR_LOGIC_TYPE, BwrLogicAdapterMode.DISABLED);
        tooltip.add(MeknucBwrLang.REACTOR_LOGIC_REDSTONE_MODE.translate(logicType.getTextComponent()));
    }
}
