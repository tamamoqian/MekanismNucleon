package meknuc.particle;

import mekanism.common.inventory.container.tile.MekanismTileContainer;
import meknuc.particle.tile.TileEntityParticleAcceleratorController;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * 粒子加速器的容器。
 * <p>
 * GUI 不显示玩家背包以外的普通槽位，但这里仍然注册靶材输入 / 输出槽，
 * 让玩家可以直接把靶材放进机器、把成品取出来（管道也可以从对应端口进出）。
 * 底部两个按钮用于切换「粒子加速 / 束流收集」工作模式。
 */
public class MeknucParticleAcceleratorContainer extends MekanismTileContainer<TileEntityParticleAcceleratorController> {

    /** 模式按钮的起始 id：+0 = 粒子加速，+1 = 束流收集。 */
    public static final int BUTTON_MODE_BASE = 1000;

    public MeknucParticleAcceleratorContainer(int id, Inventory inv, TileEntityParticleAcceleratorController tile) {
        super(MeknucParticleAcceleratorContainerTypes.PARTICLE_ACCELERATOR, id, inv, tile);
    }

    @Override
    protected void addSlots() {
        // 基类会先注册升级槽（升级窗口需要它们），再按方块实体暴露的槽位注册靶材输入 / 输出槽
        super.addSlots();
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
        MeknucParticleAcceleratorMultiblockData multiblock = tile.getMultiblock();
        if (id >= BUTTON_MODE_BASE
              && id <= BUTTON_MODE_BASE + MeknucParticleAcceleratorRecipes.MODE_COLLECTION) {
            return multiblock.changeMode(id - BUTTON_MODE_BASE);
        }
        return false;
    }
}
