package meknuc.reactor;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public abstract class MeknucReactorContainer<T extends TileEntityMekanism>
      extends MekanismTileContainer<T> {

    public static final int BUTTON_ACTIVATE = 0;
    public static final int BUTTON_AUTO_STOP = 1;
    public static final int BUTTON_INSERTION = 1000;

    protected MeknucReactorContainer(ContainerTypeRegistryObject<?> type, int id, Inventory inv, T tile) {
        super(type, id, inv, tile);
    }

    @Override
    protected void addSlots() {
    }

    @Override
    protected int getInventoryXOffset() {
        return 16;
    }

    @Override
    protected int getInventoryYOffset() {
        return 174;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        MeknucReactorMultiblockData multiblock = multiblock();
        switch (id) {
            case BUTTON_ACTIVATE -> multiblock.setActive(true);
            case BUTTON_AUTO_STOP -> multiblock.setAutoStopOnFuelExhausted(!multiblock.isAutoStopOnFuelExhausted());
            default -> {
                if (id < BUTTON_INSERTION) {
                    return false;
                }
                multiblock.setControlRodInsertion(id - BUTTON_INSERTION);
            }
        }
        return true;
    }

    protected abstract MeknucReactorMultiblockData multiblock();
}
