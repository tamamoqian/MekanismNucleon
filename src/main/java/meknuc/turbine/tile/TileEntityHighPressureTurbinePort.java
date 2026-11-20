package meknuc.turbine.tile;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import mekanism.api.IContentsListener;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.integration.energy.BlockEnergyCapabilityCache;
import meknuc.turbine.MeknucTurbineBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 高压蒸汽涡轮端口（等同工业涡轮的阀门）。
 * <p>
 * 只负责「输入蒸汽 + 输出能量」：暴露多方块的蒸汽缓存与能量缓存，
 * 并把发出来的能量自动推向相邻电缆。冷凝水改由高压涡轮排气口输出。
 */
public class TileEntityHighPressureTurbinePort extends TileEntityHighPressureTurbineCasing {

    private final Map<Direction, BlockEnergyCapabilityCache> energyCapabilityCaches = new EnumMap<>(Direction.class);

    public TileEntityHighPressureTurbinePort(BlockPos pos, BlockState state) {
        super(MeknucTurbineBlocks.HIGH_PRESSURE_STEAM_TURBINE_PORT, pos, state);
    }

    @NotNull
    @Override
    public IChemicalTankHolder getInitialChemicalTanks(IContentsListener listener) {
        return side -> getMultiblock().getChemicalTanks(side);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        return side -> getMultiblock().getEnergyContainers(side);
    }

    @NotNull
    @Override
    protected mekanism.common.capabilities.holder.fluid.IFluidTankHolder getInitialFluidTanks(IContentsListener listener) {
        return side -> Collections.emptyList();
    }

    @Override
    public boolean persists(ContainerType<?, ?, ?> type) {
        if (type == ContainerType.CHEMICAL || type == ContainerType.ENERGY) {
            return false;
        }
        return super.persists(type);
    }

    public void addEnergyTargetCapability(List<BlockEnergyCapabilityCache> outputTargets, Direction side) {
        BlockEnergyCapabilityCache cache = energyCapabilityCaches.get(side);
        if (cache == null) {
            cache = BlockEnergyCapabilityCache.create((ServerLevel) level, worldPosition.relative(side), side.getOpposite());
            energyCapabilityCaches.put(side, cache);
        }
        outputTargets.add(cache);
    }
}
