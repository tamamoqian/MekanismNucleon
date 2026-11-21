package meknuc.turbine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.energy.BlockEnergyCapabilityCache;
import mekanism.common.lib.multiblock.IValveHandler;
import mekanism.common.util.CableUtils;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.WorldUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import meknuc.turbine.tile.TileEntityHighPressureTurbinePort;
import meknuc.turbine.tile.TileEntityHighPressureTurbineVent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * 高压蒸汽涡轮的多方块数据。
 * <p>
 * 直接继承 Mekanism 的 {@link TurbineMultiblockData}：结构统计、缓存、同步、
 * GUI 数据全部复用工业涡轮的实现，只把「同体积下的性能」整体放大 2 倍：
 * <ul>
 *   <li>蒸汽容量 = 工业涡轮 × 2；</li>
 *   <li>能量容量 = 工业涡轮 × 2；</li>
 *   <li>蒸汽吞吐上限 = 工业涡轮 × 2；</li>
 *   <li>每 mB 蒸汽的发电量保持不变，因此发电量也正好 × 2；</li>
 *   <li>冷凝水产量与排气口缓存同样 × 2，保持 1 mB 蒸汽 → 1 mB 水。</li>
 * </ul>
 */
public class MeknucTurbineMultiblockData extends TurbineMultiblockData {

    /** 高压型相对工业涡轮的倍率。 */
    public static final double HIGH_PRESSURE_MULTIPLIER = 2.0D;

    /** 高压端口输出的能量缓存（蒸汽输入端口）。 */
    private final List<BlockEnergyCapabilityCache> highPressureEnergyOutputs = new ArrayList<>();
    /** 高压端口输出的冷凝水缓存（冷凝水输出端口）。 */
    private final List<BlockCapabilityCache<IFluidHandler, net.minecraft.core.Direction>> highPressureFluidOutputs = new ArrayList<>();
    /** 由校验器写入的冷凝水输出口位置。 */
    private List<VentData> highPressureVents = Collections.emptyList();

    public MeknucTurbineMultiblockData(TileEntityTurbineCasing tile) {
        super(tile);
        // 排气口的水缓存也放大 2 倍，避免双倍蒸汽产出把缓存瞬间灌满
        fluidTanks.remove(ventTank);
        ventTank = VariableCapacityFluidTank.output(this,
              () -> isFormed() ? condensers * 2 * MekanismGeneratorsConfig.generators.condenserRate.get() : 2000,
              fluid -> fluid.is(FluidTags.WATER), this);
        fluidTanks.add(ventTank);
    }

    @Override
    public void updateVentData(List<VentData> vents) {
        super.updateVentData(vents);
        this.highPressureVents = vents;
    }

    @Override
    protected void updateEjectors(Level world) {
        highPressureEnergyOutputs.clear();
        highPressureFluidOutputs.clear();
        for (IValveHandler.ValveData valve : valves) {
            TileEntityHighPressureTurbinePort port = WorldUtils.getTileEntity(TileEntityHighPressureTurbinePort.class, world, valve.location);
            if (port != null) {
                port.addEnergyTargetCapability(highPressureEnergyOutputs, valve.side);
            }
        }
        for (VentData data : highPressureVents) {
            TileEntityHighPressureTurbineVent vent = WorldUtils.getTileEntity(TileEntityHighPressureTurbineVent.class, world, data.location());
            if (vent != null) {
                vent.addFluidTargetCapability(highPressureFluidOutputs, data.side());
            }
        }
    }

    @Override
    public long getSteamCapacity() {
        return MathUtils.clampToLong(super.getSteamCapacity() * HIGH_PRESSURE_MULTIPLIER);
    }

    @Override
    public long getEnergyCapacity() {
        return MathUtils.clampToLong(super.getEnergyCapacity() * HIGH_PRESSURE_MULTIPLIER);
    }

    @Override
    public long getMaxFlowRate() {
        return MathUtils.clampToLong(this.industrialMaxFlowRate() * HIGH_PRESSURE_MULTIPLIER);
    }

    @Override
    public long getMaxProduction() {
        return MathUtils.clampToLong(this.energyMultiplier() * this.getMaxFlowRate());
    }

    @Override
    public long getMaxWaterOutput() {
        return MathUtils.clampToLong(super.getMaxWaterOutput() * HIGH_PRESSURE_MULTIPLIER);
    }

    /** 与 Mekanism 原始公式一致的每 mB 蒸汽发电量（不放大，靠双倍流量放大总发电）。 */
    private double energyMultiplier() {
        return (double) MekanismConfig.general.maxEnergyPerSteam.get() / 28.0D
              * (double) Math.min(blades, coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get());
    }

    /** 工业涡轮同结构的单倍蒸汽吞吐上限。 */
    private double industrialMaxFlowRate() {
        double rate = (double) lowerVolume * (getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserChemicalFlow.get());
        return Math.min(rate, (double) vents * MekanismGeneratorsConfig.generators.turbineVentChemicalFlow.get());
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = false;
        for (IValveHandler.ValveData data : valves) {
            data.activeTicks = Math.max(0, data.activeTicks - 1);
            if (data.activeTicks > 0 != data.prevActive) {
                needsPacket = true;
            }
            data.prevActive = data.activeTicks > 0;
        }
        lastSteamInput = newSteamInput;
        newSteamInput = 0L;
        long stored = chemicalTank.getStored();
        double flowRate = 0.0D;
        long energyNeeded = energyContainer.getNeeded();
        if (stored > 0L && energyNeeded > 0L) {
            double energyMultiplier = this.energyMultiplier();
            if (energyMultiplier < 1.0E-5D) {
                clientFlow = 0L;
            } else {
                double origRate = this.industrialMaxFlowRate() * HIGH_PRESSURE_MULTIPLIER;
                double proportion = (double) stored / (double) getSteamCapacity();
                double rate = Math.min(Math.min((double) stored, origRate), (double) energyNeeded / energyMultiplier) * proportion;
                clientFlow = MathUtils.clampToLong(rate);
                if (clientFlow > 0L) {
                    flowRate = rate / origRate;
                    energyContainer.insert(MathUtils.clampToLong(energyMultiplier * rate), Action.EXECUTE, AutomationType.INTERNAL);
                    chemicalTank.shrinkStack(clientFlow, Action.EXECUTE);
                    long water = Math.min(MathUtils.clampToLong(rate),
                          (long) condensers * 2L * MekanismGeneratorsConfig.generators.condenserRate.get());
                    ventTank.insert(new FluidStack(Fluids.WATER, MathUtils.clampToInt(water)), Action.EXECUTE, AutomationType.INTERNAL);
                }
            }
        } else {
            clientFlow = 0L;
        }
        if (!highPressureFluidOutputs.isEmpty() && !ventTank.isEmpty()) {
            ventTank.extract(FluidUtils.emit(highPressureFluidOutputs, ventTank.getFluid()), Action.EXECUTE, AutomationType.INTERNAL);
        }
        CableUtils.emit(highPressureEnergyOutputs, energyContainer);
        if (dumpMode != mekanism.common.tile.TileEntityChemicalTank.GasMode.IDLE && !chemicalTank.isEmpty()) {
            long amount = chemicalTank.getStored();
            long dumpingAmount = Math.min(amount, Math.max(amount / 50L, lastSteamInput * 2L));
            if (dumpMode == mekanism.common.tile.TileEntityChemicalTank.GasMode.DUMPING) {
                chemicalTank.shrinkStack(dumpingAmount, Action.EXECUTE);
            } else {
                long targetLevel = MathUtils.clampToLong((double) chemicalTank.getCapacity() * MekanismConfig.general.dumpExcessKeepRatio.get());
                if (targetLevel < amount) {
                    chemicalTank.shrinkStack(Math.min(amount - targetLevel, dumpingAmount), Action.EXECUTE);
                }
            }
        }
        float newRotation = (float) flowRate;
        if (Math.abs(newRotation - clientRotation) > 0.001F) {
            clientRotation = newRotation;
            needsPacket = true;
        }
        float scale = MekanismUtils.getScale(prevSteamScale, chemicalTank);
        if (MekanismUtils.scaleChanged(scale, prevSteamScale)) {
            needsPacket = true;
            prevSteamScale = scale;
        }
        return needsPacket;
    }
}
