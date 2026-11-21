package meknuc.reactor.spentpool;

import java.util.ArrayList;
import java.util.List;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import meknuc.reactor.spentpool.tile.TileEntitySpentFuelPoolCasing;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 乏燃料冷却池容器：槽位来自多方块数据（64 个输入槽 + 64 个冷却完成缓存槽），
 * 这些槽位不绘制在 GUI 中，只用于容器同步，让客户端能统计各类型燃料棒数量。
 */
public class MeknucSpentFuelPoolContainer extends MekanismTileContainer<TileEntitySpentFuelPoolCasing> {

    public MeknucSpentFuelPoolContainer(int id, Inventory inv, TileEntitySpentFuelPoolCasing tile) {
        super(MeknucSpentFuelPoolContainerTypes.SPENT_FUEL_POOL, id, inv, tile);
    }

    @Override
    protected void addSlots() {
        for (IInventorySlot inventorySlot : tile.getMultiblock().getInventorySlots(null)) {
            Slot containerSlot = inventorySlot.createContainerSlot();
            if (containerSlot != null) {
                addSlot(containerSlot);
            }
        }
    }

    /**
     * 客户端可读取的已冷却燃料棒（跳过前面的输入缓存槽）。
     */
    public List<ItemStack> getStoredRods() {
        List<ItemStack> rods = new ArrayList<>();
        List<IInventorySlot> slots = tile.getMultiblock().getInventorySlots(null);
        for (int i = MeknucSpentFuelPoolMultiblockData.INPUT_SLOT_COUNT; i < slots.size(); i++) {
            ItemStack stack = slots.get(i).getStack();
            if (!stack.isEmpty()) {
                rods.add(stack);
            }
        }
        return rods;
    }
}
