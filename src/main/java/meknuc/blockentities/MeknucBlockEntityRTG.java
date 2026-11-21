package meknuc.blockentities;

import java.util.ArrayList;
import java.util.List;

import meknuc.blocks.MeknucBlock;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucItems;
import meknuc.Config;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.Upgrade;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.energy.BlockEnergyCapabilityCache;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.CableUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MeknucBlockEntityRTG extends TileEntityMekanism {

    public static final long CAPACITY = 120_000L;
    public static final long MIN_RATE_PER_SECOND = 1L;
    public static final long MAX_RATE_PER_SECOND = 24000L;
    public static final long HOT_RATE_PER_SECOND = 36000L;
    public static final long DEPLETED_RATE_PER_SECOND = 320L;
    public static final long MAX_OUTPUT = MAX_RATE_PER_SECOND / 20;
    private static final double RAMP_FACTOR = 1.05D;

    public InputInventorySlot fuelSlot;
    public OutputInventorySlot outputSlot;
    public EnergyInventorySlot energySlot;
    public BasicEnergyContainer energyContainer;

    @ContainerSync(getter = "getProducingRate", setter = "setProducingRate")
    private double producingRate;

    @Nullable
    private List<BlockEnergyCapabilityCache> outputCaches;
    private double currentRate = MIN_RATE_PER_SECOND;
    private double fractionalEnergy;
    private double fractionalOps;
    private int ventTimer;

    public MeknucBlockEntityRTG(BlockPos pos, BlockState state) {
        super(MeknucBlock.RADIOISOTOPE_THERMOELECTRIC_GENERATOR, pos, state);
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
        producingRate = currentRate / 20.0D;
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
        ItemStack rod = fuelSlot.getStack();
        boolean isRod = rod.getItem() instanceof MeknucItemFuelRodBase;
        boolean hasFuel = isRod && (MeknucItemFuelRodBase.isInfinite(rod) || MeknucItemFuelRodBase.getRadiation(rod) > 0);
        boolean energyFull = energyContainer.getNeeded() == 0L;
        // 储电满且无燃料：每秒有概率不稳定爆炸
        if (!hasFuel && energyFull && Config.UNSTABLE_EXPLOSION.get()) {
            handleOverload();
            return;
        }
        double target = 0D;
        if (isRod) {
            if (!hasFuel) {
                target = DEPLETED_RATE_PER_SECOND;
            } else if (MeknucItemFuelRodBase.isHot(rod)) {
                target = HOT_RATE_PER_SECOND;
            } else {
                target = MAX_RATE_PER_SECOND;
            }
        }
        target *= getEnergyMultiplier();
        double growth = Math.pow(RAMP_FACTOR, getSpeedMultiplier());
        if (currentRate < target) {
            // 0.1 FE/t -> MAX
            currentRate = currentRate <= 0 ? MIN_RATE_PER_SECOND : Math.min(target, currentRate * growth);
        } else if (currentRate > target) {
            currentRate = Math.max(target, currentRate / growth);
            if (currentRate < MIN_RATE_PER_SECOND) {
                currentRate = target > 0 ? MIN_RATE_PER_SECOND : 0D;
            }
        }
        fractionalEnergy += currentRate / 20.0D;
        long output = (long) fractionalEnergy;
        fractionalEnergy -= output;
        if (output > 0) {
            energyContainer.insert(output, Action.EXECUTE, AutomationType.INTERNAL);
        }
        if (hasFuel && !MeknucItemFuelRodBase.isInfinite(rod) && !energyFull) {
            fractionalOps += getSpeedMultiplier();
            int operations = (int) fractionalOps;
            fractionalOps -= operations;
            for (int i = 0; i < operations; i++) {
                int radiation = MeknucItemFuelRodBase.getRadiation(rod) - 1;
                if (radiation <= 0) {
                    ItemStack depleted = rod.copyWithCount(1);
                    MeknucItemFuelRodBase.setRadiation(depleted, 0);
                    if (outputSlot.insertItem(depleted, Action.SIMULATE, AutomationType.INTERNAL).isEmpty()) {
                        outputSlot.insertItem(depleted, Action.EXECUTE, AutomationType.INTERNAL);
                        fuelSlot.growStack(-1, Action.EXECUTE);
                        ItemStack remaining = fuelSlot.getStack();
                        if (!remaining.isEmpty()) {
                            remaining.remove(MeknucItems.RADIATION.get());
                            fuelSlot.setStackUnchecked(remaining);
                        }
                        break;
                    }
                } else {
                    MeknucItemFuelRodBase.setRadiation(rod, radiation);
                }
            }
        }
    }

    private void handleOverload() {
        currentRate = Math.max(0D, currentRate / Math.pow(RAMP_FACTOR, getSpeedMultiplier()));
        if (currentRate < MIN_RATE_PER_SECOND) {
            currentRate = 0D;
        }
        ventTimer++;
        if (ventTimer >= 20) {
            ventTimer = 0;
            if (level != null && level.random.nextFloat() < (float) Config.UNSTABLE_EXPLOSION_CHANCE.get().doubleValue()) {
                level.explode(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D,
                      8.0F, Level.ExplosionInteraction.TNT);
            }
        }
    }

    public double getProducingRate() {
        return producingRate;
    }

    public void setProducingRate(double rate) {
        producingRate = rate;
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
