package meknuc.blockentities;

import java.util.ArrayList;
import java.util.List;

import meknuc.blocks.MeknucBlockBase;
import meknuc.items.MeknucItemBase;
import meknuc.items.MeknucItemFuelRodBase;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.api.math.MathUtils;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.energy.BlockEnergyCapabilityCache;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.CableUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MeknucBlockEntityRTG extends TileEntityMekanism {

    public static final long CAPACITY = 10240L;
    public static final long MAX_GENERATION = 350L;
    public static final long MAX_OUTPUT = MAX_GENERATION * 2;

    public InputInventorySlot fuelSlot;
    public OutputInventorySlot outputSlot;
    public EnergyInventorySlot energySlot;
    public BasicEnergyContainer energyContainer;

    @Nullable
    private List<BlockEnergyCapabilityCache> outputCaches;
    private double fractionalEnergy;
    private double fractionalOps;

    public MeknucBlockEntityRTG(BlockPos pos, BlockState state) {
        super(MeknucBlockBase.RADIOISOTOPE_THERMOELECTRIC_GENERATOR, pos, state);
    }

    @NotNull
    @Override
    protected IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(facingSupplier);
        builder.addContainer(energyContainer = BasicEnergyContainer.output(CAPACITY, listener));
        return builder.build();
    }

    @NotNull
    @Override
    protected IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(facingSupplier);
        builder.addSlot(fuelSlot = InputInventorySlot.at(stack -> stack.getItem() instanceof MeknucItemFuelRodBase, listener, 17, 20));
        builder.addSlot(outputSlot = OutputInventorySlot.at(listener, 17, 50));
        builder.addSlot(energySlot = EnergyInventorySlot.drain(getEnergyContainer(), listener, 143, 35));
        return builder.build();
    }

    @Override
    protected boolean onUpdateServer() {
        boolean sendUpdatePacket = super.onUpdateServer();
        energySlot.drainContainer();
        if (canFunction()) {
            generatePower();
            if (outputCaches == null) {
                outputCaches = new ArrayList<>();
                for (Direction side : Direction.values()) {
                    outputCaches.add(BlockEnergyCapabilityCache.create((ServerLevel) level, worldPosition.relative(side), side.getOpposite()));
                }
            }
            CableUtils.emit(outputCaches, energyContainer, MAX_OUTPUT);
        }
        return sendUpdatePacket;
    }

    private void generatePower() {
        fractionalOps += getSpeedMultiplier();
        int operations = (int) fractionalOps;
        fractionalOps -= operations;
        for (int i = 0; i < operations; i++) {
            double rate = getRatePerOperation();
            if (rate <= 0) {
                break;
            }
            fractionalEnergy += rate;
            long output = (long) fractionalEnergy;
            fractionalEnergy -= output;
            if (output > 0) {
                energyContainer.insert(output, Action.EXECUTE, AutomationType.INTERNAL);
            }
            ItemStack rod = fuelSlot.getStack();
            if (rod.getItem() instanceof MeknucItemFuelRodBase) {
                int radiation = MeknucItemFuelRodBase.getRadiation(rod) - 1;
                if (radiation <= 0) {
                    ItemStack depleted = rod.copyWithCount(1);
                    MeknucItemFuelRodBase.setRadiation(depleted, 0);
                    if (outputSlot.insertItem(depleted, Action.SIMULATE, AutomationType.INTERNAL).isEmpty()) {
                        outputSlot.insertItem(depleted, Action.EXECUTE, AutomationType.INTERNAL);
                        fuelSlot.growStack(-1, Action.EXECUTE);
                        ItemStack remaining = fuelSlot.getStack();
                        if (!remaining.isEmpty()) {
                            remaining.remove(MeknucItemBase.RADIATION.get());
                            fuelSlot.setStackUnchecked(remaining);
                        }
                    }
                } else {
                    MeknucItemFuelRodBase.setRadiation(rod, radiation);
                }
            }
        }
    }

    public double getProducingRate() {
        return getRatePerOperation() * getSpeedMultiplier();
    }

    private double getRatePerOperation() {
        ItemStack rod = fuelSlot.getStack();
        double rate = 0D;
        if (rod.getItem() instanceof MeknucItemFuelRodBase) {
            rate = Math.min(MeknucItemFuelRodBase.getPowerPerTick(rod), MAX_GENERATION);
        }
        if (rate <= 0) {
            return 0D;
        }
        rate *= getEnergyMultiplier();
        if (energyContainer.insert(MathUtils.ceilToLong(rate), Action.SIMULATE, AutomationType.INTERNAL) != 0L) {
            return 0D;
        }
        return rate;
    }

    private double getSpeedMultiplier() {
        return supportsUpgrades() ? Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), MekanismUtils.fractionUpgrades(this, Upgrade.SPEED)) : 1.0D;
    }

    private double getEnergyMultiplier() {
        return supportsUpgrades() ? Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), MekanismUtils.fractionUpgrades(this, Upgrade.ENERGY)) : 1.0D;
    }

    public BasicEnergyContainer getEnergyContainer() {
        return energyContainer;
    }
}
