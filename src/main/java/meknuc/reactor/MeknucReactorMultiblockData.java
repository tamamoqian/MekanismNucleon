package meknuc.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.util.MekanismUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.EventHooks;

public abstract class MeknucReactorMultiblockData extends MultiblockData {

    protected static final int NOMINAL_INSERTION_PERCENT = 50;

    protected final List<IInventorySlot> fuelSlots;
    protected final List<IInventorySlot> wasteSlots;

    private ItemStack runningProduct = ItemStack.EMPTY;
    private boolean insertionFromCache;
    private boolean temperatureFromCache;
    private boolean autoStopFromCache;

    protected MeknucReactorMultiblockData(BlockEntity tile, int cacheCapacity, Predicate<ItemStack> fuelValidator) {
        super(tile);
        fuelSlots = createSlots(cacheCapacity, ConstantPredicates.alwaysTrueBi(), fuelValidator, true);
        wasteSlots = createSlots(cacheCapacity, ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue(), false);
        inventorySlots.addAll(fuelSlots);
        inventorySlots.addAll(wasteSlots);
    }

    public abstract int getFuelAssemblies();

    protected abstract double defaultTemperature();

    public abstract void setTemperature(double value);

    public abstract void setControlRodInsertion(int value);

    public abstract void setAutoStopOnFuelExhausted(boolean enabled);

    public abstract boolean isAutoStopOnFuelExhausted();

    public abstract void setActive(boolean active);

    public abstract boolean isActive();

    public abstract int getControlRodInsertion();

    public abstract double getTemperature();

    public abstract int getMeltdownTimer();

    public abstract void setMeltdownTimer(int timer);

    public abstract boolean isMeltedDown();

    public abstract void setMeltedDown(boolean melted);

    public abstract int getInCoreCount();

    public abstract void setInCoreCount(int count);

    public abstract int getFuelCount();

    public abstract void setFuelCount(int count);

    public abstract int getWasteCount();

    public abstract void setWasteCount(int count);

    public abstract ItemStack getFuelStack();

    public abstract void setFuelStack(ItemStack stack);

    public abstract ItemStack getWasteStack();

    public abstract void setWasteStack(ItemStack stack);

    public int getCacheCapacity() {
        return Math.max(0, getFuelAssemblies());
    }

    private List<IInventorySlot> createSlots(int capacity, BiPredicate<ItemStack, AutomationType> canInsert,
          Predicate<ItemStack> validator, boolean fuel) {
        List<IInventorySlot> slots = new ArrayList<>();
        int remaining = capacity;
        while (remaining > 0) {
            int limit = Math.min(MeknucReactorConstants.SLOT_STACK_LIMIT, remaining);
            slots.add(new CacheSlot(limit, ConstantPredicates.alwaysTrueBi(), canInsert, validator, this, fuel));
            remaining -= limit;
        }
        return slots;
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        if (!temperatureFromCache) {
            setTemperature(defaultTemperature());
        }
        temperatureFromCache = false;
        if (!insertionFromCache) {
            setControlRodInsertion(NOMINAL_INSERTION_PERCENT);
        }
        insertionFromCache = false;
        if (!autoStopFromCache) {
            setAutoStopOnFuelExhausted(true);
        }
        autoStopFromCache = false;
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
        refreshCacheCounts();
    }

    protected final void refreshCacheCounts() {
        if (isRemote()) {
            return;
        }
        int fuelTotal = 0;
        ItemStack fuelSample = ItemStack.EMPTY;
        for (IInventorySlot slot : fuelSlots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                fuelTotal += stack.getCount();
                if (fuelSample.isEmpty()) {
                    fuelSample = stack.copy();
                }
            }
        }
        setFuelCount(fuelTotal);
        if (!ItemStack.matches(getFuelStack(), fuelSample)) {
            setFuelStack(fuelSample);
        }
        int wasteTotal = 0;
        ItemStack wasteSample = ItemStack.EMPTY;
        for (IInventorySlot slot : wasteSlots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                wasteTotal += stack.getCount();
                if (wasteSample.isEmpty()) {
                    wasteSample = stack.copy();
                }
            }
        }
        setWasteCount(wasteTotal);
        if (!ItemStack.matches(getWasteStack(), wasteSample)) {
            setWasteStack(wasteSample);
        }
    }

    @Override
    protected int getMultiblockRedstoneLevel() {
        int capacity = getCacheCapacity();
        return capacity <= 0 ? 0 : MekanismUtils.redstoneLevelFromContents(getFuelCount(), capacity);
    }

    protected ItemStack findFuel() {
        for (IInventorySlot slot : fuelSlots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    protected int countFuel(ItemStack template) {
        int total = 0;
        for (IInventorySlot slot : fuelSlots) {
            ItemStack stack = slot.getStack();
            if (ItemStack.isSameItemSameComponents(stack, template)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    protected void consumeFuel(ItemStack template, int amount) {
        int remaining = amount;
        for (IInventorySlot slot : fuelSlots) {
            if (remaining <= 0) {
                return;
            }
            ItemStack stack = slot.getStack();
            if (ItemStack.isSameItemSameComponents(stack, template)) {
                int taken = Math.min(remaining, stack.getCount());
                slot.setStack(stack.copyWithCount(stack.getCount() - taken));
                remaining -= taken;
            }
        }
    }

    protected void storeWaste(ItemStack template, int amount) {
        int remaining = amount;
        for (IInventorySlot slot : wasteSlots) {
            if (remaining <= 0) {
                return;
            }
            ItemStack waste = slot.getStack();
            int stored;
            if (waste.isEmpty()) {
                stored = Math.min(remaining, slot.getLimit(template));
                slot.setStack(template.copyWithCount(stored));
            } else if (ItemStack.isSameItemSameComponents(waste, template)) {
                stored = Math.min(remaining, slot.getLimit(template) - waste.getCount());
                waste.grow(stored);
                slot.setStack(waste);
            } else {
                continue;
            }
            remaining -= stored;
        }
    }

    protected boolean canStore(ItemStack template, int amount) {
        int stored = 0;
        int room = 0;
        for (IInventorySlot slot : wasteSlots) {
            ItemStack waste = slot.getStack();
            stored += waste.getCount();
            if (waste.isEmpty()) {
                room += slot.getLimit(template);
            } else if (ItemStack.isSameItemSameComponents(waste, template)) {
                room += slot.getLimit(template) - waste.getCount();
            }
        }
        return room >= amount && stored + amount <= getCacheCapacity();
    }

    protected void stopIfExhausted() {
        if (isAutoStopOnFuelExhausted()) {
            setActive(false);
        }
    }

    protected void createMeltdown(Level world) {
        if (isMeltedDown()) {
            return;
        }
        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();
        double centerX = (min.getX() + max.getX() + 1) / 2.0;
        double centerY = max.getY() + 0.5;
        double centerZ = (min.getZ() + max.getZ() + 1) / 2.0;
        Explosion explosion = new MeknucReactorMeltdown.MeltdownExplosion(world, centerX, centerY, centerZ,
              MeknucReactorConstants.MELTDOWN_EXPLOSION_RADIUS, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
        if (!EventHooks.onExplosionStart(world, explosion)) {
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
        MeknucReactorMeltdown.destroyStructure(world, explosion, locations, internalLocations);
        MeknucReactorMeltdown.radiate(world, centerX, centerY, centerZ);
        meltdownHappened(world);
    }

    public ItemStack getRunningProduct() {
        return runningProduct;
    }

    public void setRunningProduct(ItemStack stack) {
        runningProduct = stack;
    }

    void restoreAutoStopOnFuelExhausted(boolean value) {
        setAutoStopOnFuelExhausted(value);
        autoStopFromCache = true;
    }

    void restoreControlRodInsertion(int value) {
        setControlRodInsertion(value);
        insertionFromCache = true;
    }

    void restoreTemperature(double value) {
        setTemperature(value);
        temperatureFromCache = true;
    }

    protected class CacheSlot extends BasicInventorySlot {

        private final int slotLimit;
        private final boolean fuel;

        private CacheSlot(int limit, BiPredicate<ItemStack, AutomationType> canExtract,
              BiPredicate<ItemStack, AutomationType> canInsert, Predicate<ItemStack> validator, IContentsListener listener,
              boolean fuel) {
            super(limit, canExtract, canInsert, validator, listener, 0, 0);
            slotLimit = limit;
            this.fuel = fuel;
            obeyStackLimit = false;
        }

        @Override
        public int getLimit(ItemStack stack) {
            if (fuel) {
                int others = 0;
                for (IInventorySlot slot : fuelSlots) {
                    if (slot != this) {
                        others += slot.getStack().getCount();
                    }
                }
                return Math.max(0, Math.min(slotLimit, getCacheCapacity() - others));
            }
            return slotLimit;
        }
    }
}
