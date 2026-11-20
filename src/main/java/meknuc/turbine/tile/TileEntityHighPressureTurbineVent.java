package meknuc.turbine.tile;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import mekanism.api.IContentsListener;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder;
import meknuc.turbine.MeknucTurbineBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 高压涡轮排气口。
 * <p>
 * 与 Mekanism 工业涡轮的排气口作用相同：暴露多方块的冷凝水缓存，并把水自动
 * 推向相邻流体容器。必须放在墙面上，且位于复合旋钮装置层或更高。
 */
public class TileEntityHighPressureTurbineVent extends TileEntityHighPressureTurbineCasing {

    private final Map<Direction, BlockCapabilityCache<IFluidHandler, Direction>> fluidCapabilityCaches = new EnumMap<>(Direction.class);

    public TileEntityHighPressureTurbineVent(BlockPos pos, BlockState state) {
        super(MeknucTurbineBlocks.HIGH_PRESSURE_STEAM_TURBINE_VENT, pos, state);
    }

    @NotNull
    @Override
    protected IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> getMultiblock().getFluidTanks(side);
    }

    @Override
    public boolean persists(ContainerType<?, ?, ?> type) {
        if (type == ContainerType.FLUID) {
            return false;
        }
        return super.persists(type);
    }

    @SuppressWarnings("unchecked")
    public void addFluidTargetCapability(List<BlockCapabilityCache<IFluidHandler, Direction>> outputTargets, Direction side) {
        BlockCapabilityCache<IFluidHandler, Direction> cache = fluidCapabilityCaches.get(side);
        if (cache == null) {
            cache = (BlockCapabilityCache<IFluidHandler, Direction>) Capabilities.FLUID.createCache(
                  (ServerLevel) level, worldPosition.relative(side), side.getOpposite());
            fluidCapabilityCaches.put(side, cache);
        }
        outputTargets.add(cache);
    }
}
