package meknuc.wastestorage;

import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import meknuc.wastestorage.tile.TileEntityWasteStorageCasing;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * 放射性废料存储罐的容器。
 * <p>
 * 多方块里的 128 个存储槽（64 废料 + 64 产物）全部注册进容器，但都不绘制在 GUI 上：
 * 它们只用于容器同步，让客户端 GUI 能统计「各类型存量 / 产物总数 / 是否已满」。
 */
public class MeknucWasteStorageContainer extends MekanismTileContainer<TileEntityWasteStorageCasing> {

    /** 与 GUI 的 inventoryLabelX 对齐。 */
    private static final int INVENTORY_X = 16;
    /** 与 GUI 的槽位区域对齐。 */
    private static final int INVENTORY_Y = 174;

    public MeknucWasteStorageContainer(int id, Inventory inv, TileEntityWasteStorageCasing tile) {
        super(MeknucWasteStorageContainerTypes.WASTE_STORAGE, id, inv, tile);
    }

    @Override
    protected void addSlots() {
        // 槽位不画在 GUI 上，只用于把 128 个存储槽的内容同步到客户端
        for (IInventorySlot inventorySlot : tile.getMultiblock().getInventorySlots(null)) {
            Slot containerSlot = inventorySlot.createContainerSlot();
            if (containerSlot != null) {
                addSlot(containerSlot);
            }
        }
    }

    @Override
    protected int getInventoryXOffset() {
        return INVENTORY_X;
    }

    @Override
    protected int getInventoryYOffset() {
        return INVENTORY_Y;
    }
}
