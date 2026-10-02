package meknuc.reactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.MultiblockData;
import meknuc.reactor.AttributeStateReactorPortMode.ReactorPortMode;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MeknucReactorMultiblockData extends MultiblockData {

    public static final int CACHE_CAPACITY = 128;
    public static final int COOLANT_CAPACITY_PER_BLOCK = 20000;
    private static final double AMBIENT_TEMPERATURE = 300.0;
    private static final double HEAT_CAPACITY_PER_BLOCK = 1000.0;

    private final List<FuelColumn> fuelColumns = new ArrayList<>();
    private final IInventorySlot fuelSlot;
    private final IInventorySlot wasteSlot;

    @ContainerSync
    public final VariableHeatCapacitor heatCapacitor;

    @ContainerSync
    public final IExtendedFluidTank coolantTank;

    @ContainerSync
    public final IChemicalTank heatedCoolantTank;

    @ContainerSync(getter = "isActive", setter = "setActive")
    private boolean active;

    @ContainerSync(getter = "getControlRodInsertion", setter = "setControlRodInsertion")
    private int controlRodInsertion;

    @ContainerSync(getter = "getBurnTime", setter = "setBurnTime")
    private int burnTime;

    @ContainerSync(getter = "getDamage", setter = "setDamage")
    private double damage;

    @ContainerSync(getter = "getFuelStack", setter = "setFuelStack")
    private ItemStack fuelStack = ItemStack.EMPTY;

    @ContainerSync(getter = "getWasteStack", setter = "setWasteStack")
    private ItemStack wasteStack = ItemStack.EMPTY;

    public MeknucReactorMultiblockData(BlockEntity tile) {
        super(tile);
        coolantTank = VariableCapacityFluidTank.input(this, () -> getVolume() * COOLANT_CAPACITY_PER_BLOCK,
              fluid -> fluid.is(FluidTags.WATER), this);
        heatedCoolantTank = VariableCapacityChemicalTank.output(this,
              () -> (long) getVolume() * COOLANT_CAPACITY_PER_BLOCK, ConstantPredicates.alwaysTrue(), this);
        fluidTanks.add(coolantTank);
        chemicalTanks.add(heatedCoolantTank);
        heatCapacitor = VariableHeatCapacitor.create(HEAT_CAPACITY_PER_BLOCK, () -> 10.0, () -> 10000.0,
              () -> AMBIENT_TEMPERATURE, this);
        heatCapacitors.add(heatCapacitor);
        fuelSlot = new CacheSlot(CACHE_CAPACITY, ConstantPredicates.notExternal(), ConstantPredicates.alwaysTrueBi(),
              ConstantPredicates.alwaysTrue(), this);
        wasteSlot = new CacheSlot(CACHE_CAPACITY, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(),
              ConstantPredicates.alwaysTrue(), this);
        inventorySlots.add(fuelSlot);
        inventorySlots.add(wasteSlot);
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        updateCacheMirrors();
        return needsPacket;
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
        updateCacheMirrors();
    }

    private void updateCacheMirrors() {
        ItemStack fuel = fuelSlot.getStack();
        if (fuel.getCount() != fuelStack.getCount() || !ItemStack.isSameItemSameComponents(fuel, fuelStack)) {
            fuelStack = fuel.copy();
        }
        ItemStack waste = wasteSlot.getStack();
        if (waste.getCount() != wasteStack.getCount() || !ItemStack.isSameItemSameComponents(waste, wasteStack)) {
            wasteStack = waste.copy();
        }
    }

    public List<IInventorySlot> getInventorySlots(ReactorPortMode mode) {
        if (!isFormed() && !isRemote()) {
            return Collections.emptyList();
        }
        return switch (mode) {
            case FUEL_INPUT -> List.of(fuelSlot);
            case WASTE_OUTPUT -> List.of(wasteSlot);
            default -> Collections.emptyList();
        };
    }

    public List<IExtendedFluidTank> getFluidTanks(ReactorPortMode mode) {
        if (!isFormed() && !isRemote()) {
            return Collections.emptyList();
        }
        return switch (mode) {
            case COOLANT_INPUT -> List.of(coolantTank);
            default -> Collections.emptyList();
        };
    }

    public List<IChemicalTank> getChemicalTanks(ReactorPortMode mode) {
        if (!isFormed() && !isRemote()) {
            return Collections.emptyList();
        }
        return switch (mode) {
            case COOLANT_OUTPUT -> List.of(heatedCoolantTank);
            default -> Collections.emptyList();
        };
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            markDirty();
        }
    }

    public int getControlRodInsertion() {
        return controlRodInsertion;
    }

    public void setControlRodInsertion(int insertion) {
        int clamped = Mth.clamp(insertion, 0, 100);
        if (controlRodInsertion != clamped) {
            controlRodInsertion = clamped;
            markDirty();
        }
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int time) {
        int clamped = Math.max(0, time);
        if (burnTime != clamped) {
            burnTime = clamped;
            markDirty();
        }
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        double clamped = Mth.clamp(damage, 0.0, 100.0);
        if (this.damage != clamped) {
            this.damage = clamped;
            markDirty();
        }
    }

    public int getDamagePercent() {
        return (int) Math.round(damage);
    }

    public ItemStack getFuelStack() {
        return fuelStack;
    }

    public void setFuelStack(ItemStack stack) {
        fuelStack = stack;
    }

    public ItemStack getWasteStack() {
        return wasteStack;
    }

    public void setWasteStack(ItemStack stack) {
        wasteStack = stack;
    }

    public List<FuelColumn> getFuelColumns() {
        return Collections.unmodifiableList(fuelColumns);
    }

    public void setFuelColumns(List<FuelColumn> columns) {
        fuelColumns.clear();
        fuelColumns.addAll(columns);
    }

    public record FuelColumn(BlockPos base, int fuelHeight, BlockPos controlRod) {

        public int totalHeight() {
            return fuelHeight + 1;
        }
    }

    private static class CacheSlot extends BasicInventorySlot {

        private CacheSlot(int limit, BiPredicate<ItemStack, AutomationType> canExtract,
              BiPredicate<ItemStack, AutomationType> canInsert, Predicate<ItemStack> validator, IContentsListener listener) {
            super(limit, canExtract, canInsert, validator, listener, 0, 0);
            obeyStackLimit = false;
        }
    }
}
