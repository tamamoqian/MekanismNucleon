package meknuc.reactor;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.util.MekanismUtils;
import meknuc.chemicals.MeknucChemicals;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.reactor.AttributeStatePwrPortMode.ReactorPortMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

public class MeknucPwrMultiblockData extends MultiblockData {

    public static final int CACHE_CAPACITY = 357;
    public static final int SLOT_STACK_LIMIT = 99;

    public static final double MW_PER_ASSEMBLY = 15.625;
    public static final double CORE_HEAT_CAPACITY = 180.0;
    public static final double AMBIENT_TEMPERATURE = 20.0;
    public static final double REFERENCE_CONDUCTION = 6.0;
    public static final double COOLING_FACTOR = 0.8;
    public static final double BOIL_LIMIT_PER_BLOCK = 2.0;
    public static final long COOLANT_CAPACITY_PER_BLOCK = 15000L;
    public static final double MB_PER_MW = 10714.2857142857;
    public static final double PRESSURE_STEP_TEMPERATURE = 8.75;
    public static final double PRESSURE_STEP_MPA = 0.5;
    public static final double MAX_PRESSURE = 20.0;
    public static final double FLOW_MULTIPLIER_PER_MPA = 0.1;
    public static final double COOLANT_START_TEMPERATURE = 100.0;
    public static final double COOLANT_STOP_TEMPERATURE = 95.0;
    public static final double DERATE_TEMPERATURE = 450.0;
    public static final double DERATE_STEP_TEMPERATURE = 10.0;
    public static final double DERATE_FACTOR = 0.1;
    public static final double ROD_FAILURE_TEMPERATURE = 500.0;
    public static final double MELTDOWN_TEMPERATURE = 1200.0;
    public static final double MELTDOWN_RAMP_PER_SECOND = 1000.0 / 60.0;
    public static final double POWER_RAMP_SECONDS = 20.0;
    public static final int NOMINAL_INSERTION_PERCENT = 50;
    public static final double MIN_FUEL_POWER_FACTOR = 0.5;

    private static final double TICK_SECONDS = 0.05;

    public static final int MELTDOWN_TICKS = 200;
    public static final float MELTDOWN_EXPLOSION_RADIUS = 25.0F;
    public static final int MELTDOWN_RADIATION_RADIUS = 150;
    public static final double MELTDOWN_RADIATION_MAGNITUDE = 100.0;

    private final List<IInventorySlot> fuelSlots;
    private final List<IInventorySlot> wasteSlots;

    @ContainerSync
    public final IChemicalTank coolantTank;

    @ContainerSync
    public final IChemicalTank heatedCoolantTank;

    @ContainerSync(getter = "getTemperature", setter = "setTemperature")
    private double temperature = AMBIENT_TEMPERATURE;

    @ContainerSync(getter = "isActive", setter = "setActive")
    private boolean active;

    @ContainerSync(getter = "getControlRodInsertion", setter = "setControlRodInsertion")
    private int controlRodInsertion = NOMINAL_INSERTION_PERCENT;

    @ContainerSync(getter = "getPowerLevel", setter = "setPowerLevel")
    private double powerLevel;

    @ContainerSync(getter = "isCoolantOn", setter = "setCoolantOn")
    private boolean coolantActive;

    @ContainerSync(getter = "isRodsFailed", setter = "setRodsFailed")
    private boolean rodsFailed;

    @ContainerSync(getter = "getBurnTime", setter = "setBurnTime")
    private int burnTime;

    @ContainerSync(getter = "getBatchBurnTime", setter = "setBatchBurnTime")
    private int batchBurnTime;

    @ContainerSync(getter = "isAutoStopOnFuelExhausted", setter = "setAutoStopOnFuelExhausted")
    private boolean autoStopOnFuelExhausted = true;

    private double burnProgress;
    private int batchBaseTicks;
    private double coolantCarry;
    private double lastFlow;
    private boolean insertionFromCache;
    private boolean temperatureFromCache;
    private boolean autoStopFromCache;

    @ContainerSync(getter = "getMeltdownTimer", setter = "setMeltdownTimer")
    private int meltdownTimer;

    @ContainerSync(getter = "isMeltedDown", setter = "setMeltedDown")
    private boolean meltedDown;

    @ContainerSync(getter = "getInCoreCount", setter = "setInCoreCount")
    private int inCoreCount;

    @ContainerSync(getter = "getFuelAssemblies", setter = "setFuelAssemblies")
    private int fuelAssemblies;

    @ContainerSync(getter = "getFuelCount", setter = "setFuelCount")
    private int fuelCount;

    @ContainerSync(getter = "getWasteCount", setter = "setWasteCount")
    private int wasteCount;

    @ContainerSync(getter = "getFuelStack", setter = "setFuelStack")
    private ItemStack fuelStack = ItemStack.EMPTY;

    @ContainerSync(getter = "getWasteStack", setter = "setWasteStack")
    private ItemStack wasteStack = ItemStack.EMPTY;

    private ItemStack runningProduct = ItemStack.EMPTY;

    public MeknucPwrMultiblockData(BlockEntity tile) {
        super(tile);
        coolantTank = VariableCapacityChemicalTank.input(this, this::waterCapacity,
              chemical -> chemical.is(MeknucChemicals.BORATED_LIGHT_WATER.get()), this);
        heatedCoolantTank = VariableCapacityChemicalTank.output(this, this::waterCapacity,
              chemical -> chemical.is(MeknucChemicals.PRESSURIZED_BORATED_LIGHT_WATER.get()), this);
        chemicalTanks.add(coolantTank);
        chemicalTanks.add(heatedCoolantTank);
        fuelSlots = createSlots(CACHE_CAPACITY, ConstantPredicates.alwaysTrueBi(), MeknucPwrFuels::isFuel, true);
        wasteSlots = createSlots(CACHE_CAPACITY, ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue(),
              false);
        inventorySlots.addAll(fuelSlots);
        inventorySlots.addAll(wasteSlots);
    }

    private List<IInventorySlot> createSlots(int capacity, BiPredicate<ItemStack, AutomationType> canInsert,
          Predicate<ItemStack> validator, boolean fuel) {
        List<IInventorySlot> slots = new ArrayList<>();
        int remaining = capacity;
        while (remaining > 0) {
            int limit = Math.min(SLOT_STACK_LIMIT, remaining);
            slots.add(new CacheSlot(limit, ConstantPredicates.alwaysTrueBi(), canInsert, validator, this, fuel));
            remaining -= limit;
        }
        return slots;
    }

    public int getCacheCapacity() {
        return Math.max(0, fuelAssemblies);
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        if (!temperatureFromCache) {
            setTemperature(AMBIENT_TEMPERATURE);
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
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (isFormed() && !isRemote()) {
            refreshCacheCounts();
            tickCore();
            handleMeltdown(world);
        }
        return needsPacket;
    }

    private void tickCore() {
        int rods = fuelAssemblies;
        double designPower = getDesignPower();
        tickFuelCycle(rods, designPower);
        double target = active && !meltedDown && inCoreCount > 0
              ? designPower * getFuelPowerFactor() * getRodPowerFactor() : 0.0;
        setPowerLevel(powerLevel + (target - powerLevel) * Math.min(1.0, TICK_SECONDS / POWER_RAMP_SECONDS));

        double volume = Math.max(0, getVolume());
        double boilLimit = volume * BOIL_LIMIT_PER_BLOCK;
        double designFlow = designPower * MB_PER_MW;
        double maxCoolRate = Math.min(volume * COOLANT_CAPACITY_PER_BLOCK, designFlow);
        double baseFlow = Math.max(0.0, Math.min(designFlow, Math.min(boilLimit * MB_PER_MW, maxCoolRate)));

        if (temperature > COOLANT_START_TEMPERATURE) {
            setCoolantOn(true);
        } else if (temperature < COOLANT_STOP_TEMPERATURE) {
            setCoolantOn(false);
        }
        if (!coolantActive || meltedDown) {
            baseFlow = 0.0;
        }

        double pressure = waterPressure(temperature);
        double requested = baseFlow * pressure * FLOW_MULTIPLIER_PER_MPA;
        long stored = coolantTank.getStored();
        long room = heatedCoolantTank.getCapacity() - heatedCoolantTank.getStored();
        double available = Math.max(0.0, Math.min(stored, room));
        coolantCarry = Math.min(coolantCarry + requested, requested + 1.0);
        long transfer = (long) Math.floor(Math.min(coolantCarry, available));
        if (transfer > 0) {
            coolantTank.extract(transfer, Action.EXECUTE, AutomationType.INTERNAL);
            heatedCoolantTank.insert(MeknucChemicals.PRESSURIZED_BORATED_LIGHT_WATER.asStack(transfer),
                  Action.EXECUTE, AutomationType.INTERNAL);
            coolantCarry -= transfer;
        }
        if (!coolantActive) {
            coolantCarry = 0.0;
        }
        lastFlow = transfer;

        double efficiency = coolantEfficiency(temperature);
        double next = temperature;
        if (rodsFailed) {
            next += MELTDOWN_RAMP_PER_SECOND * TICK_SECONDS;
        } else {
            double conduction = REFERENCE_CONDUCTION * (lastFlow / Math.max(designFlow, 1.0e-9)) * efficiency;
            double removalCap = COOLING_FACTOR * boilLimit * efficiency;
            double removed = Math.min(Math.max(0.0, conduction * (next - AMBIENT_TEMPERATURE)), removalCap);
            next += (powerLevel - removed) * TICK_SECONDS / CORE_HEAT_CAPACITY;
        }
        if (next < AMBIENT_TEMPERATURE) {
            next = AMBIENT_TEMPERATURE;
        }
        if (next >= ROD_FAILURE_TEMPERATURE) {
            setRodsFailed(true);
        }
        if (next >= MELTDOWN_TEMPERATURE) {
            next = MELTDOWN_TEMPERATURE;
            startMeltdown();
        }
        setTemperature(next);
    }

    private void handleMeltdown(Level world) {
        if (meltedDown) {
            return;
        }
        if (meltdownTimer > 0) {
            setMeltdownTimer(meltdownTimer - 1);
            if (meltdownTimer <= 0) {
                createMeltdown(world);
            }
        }
    }

    private void startMeltdown() {
        if (meltdownTimer <= 0) {
            setMeltdownTimer(MELTDOWN_TICKS);
        }
    }

    private void createMeltdown(Level world) {
        if (meltedDown) {
            return;
        }
        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();
        double centerX = (min.getX() + max.getX() + 1) / 2.0;
        double centerY = max.getY() + 0.5;
        double centerZ = (min.getZ() + max.getZ() + 1) / 2.0;
        Explosion explosion = new MeltdownExplosion(world, centerX, centerY, centerZ, MELTDOWN_EXPLOSION_RADIUS,
              Explosion.BlockInteraction.DESTROY_WITH_DECAY);
        if (!EventHooks.onExplosionStart(world, explosion)) {
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
        destroyStructure(world, explosion);
        radiateMeltdown(world, centerX, centerY, centerZ);
        meltdownHappened(world);
    }

    private void destroyStructure(Level world, Explosion explosion) {
        List<BlockPos> targets = new ArrayList<>();
        for (BlockPos pos : locations) {
            if (world.random.nextBoolean()) {
                targets.add(pos);
            }
        }
        for (BlockPos pos : internalLocations) {
            if (world.random.nextBoolean()) {
                targets.add(pos);
            }
        }
        List<Pair<ItemStack, BlockPos>> drops = new ArrayList<>();
        for (BlockPos pos : targets) {
            if (!world.isLoaded(pos)) {
                continue;
            }
            BlockState state = world.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            state.onExplosionHit(world, pos, explosion, (stack, dropPos) -> addDrop(drops, stack, dropPos));
        }
        for (Pair<ItemStack, BlockPos> pair : drops) {
            Block.popResource(world, pair.getSecond(), pair.getFirst());
        }
    }

    private static void addDrop(List<Pair<ItemStack, BlockPos>> drops, ItemStack stack, BlockPos pos) {
        if (stack.isEmpty()) {
            return;
        }
        for (Pair<ItemStack, BlockPos> existing : drops) {
            if (existing.getSecond().equals(pos)) {
                existing.getFirst().grow(stack.getCount());
                return;
            }
        }
        drops.add(Pair.of(stack, pos));
    }

    private static class MeltdownExplosion extends Explosion {

        private MeltdownExplosion(Level world, double x, double y, double z, float radius,
              Explosion.BlockInteraction mode) {
            super(world, null, x, y, z, radius, false, mode);
        }
    }

    private void radiateMeltdown(Level world, double centerX, double centerY, double centerZ) {
        if (!IRadiationManager.INSTANCE.isRadiationEnabled()) {
            return;
        }
        int centerChunkX = Mth.floor(centerX) >> 4;
        int centerChunkZ = Mth.floor(centerZ) >> 4;
        int chunkRadius = MELTDOWN_RADIATION_RADIUS / 16 + 1;
        int sourceY = Mth.floor(centerY);
        for (int offsetX = -chunkRadius; offsetX <= chunkRadius; offsetX++) {
            for (int offsetZ = -chunkRadius; offsetZ <= chunkRadius; offsetZ++) {
                int sourceX = ((centerChunkX + offsetX) << 4) + 8;
                int sourceZ = ((centerChunkZ + offsetZ) << 4) + 8;
                double deltaX = sourceX - centerX;
                double deltaZ = sourceZ - centerZ;
                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                if (distance >= MELTDOWN_RADIATION_RADIUS) {
                    continue;
                }
                double magnitude = MELTDOWN_RADIATION_MAGNITUDE * (1.0 - distance / MELTDOWN_RADIATION_RADIUS);
                if (magnitude <= 0.0) {
                    continue;
                }
                IRadiationManager.INSTANCE.radiate(world, new BlockPos(sourceX, sourceY, sourceZ), magnitude);
            }
        }
    }

    @Override
    public void meltdownHappened(Level world) {
        setActive(false);
        setMeltedDown(true);
        setMeltdownTimer(0);
        setBurnTime(0);
        setPowerLevel(0.0);
        setRodsFailed(true);
    }

    private void tickFuelCycle(int rods, double designPower) {
        if (rods <= 0) {
            setInCoreCount(0);
            setBurnTime(0);
            setBatchBurnTime(0);
            burnProgress = 0.0;
            batchBaseTicks = 0;
            return;
        }
        if (inCoreCount <= 0) {
            if (!active || meltedDown) {
                setBurnTime(0);
                return;
            }
            startCycle(rods);
            if (inCoreCount <= 0) {
                return;
            }
        }
        if (active && !meltedDown && designPower > 0.0) {
            double fraction = Mth.clamp(powerLevel / designPower, 0.0, 1.0);
            if (fraction > 0.0) {
                burnProgress = Math.min(1.0, burnProgress + fraction * TICK_SECONDS / getBatchLifeSeconds());
                if (burnProgress >= 1.0) {
                    finishCycle();
                }
            }
        }
        refreshBurnTimeDisplay();
    }

    private void startCycle(int rods) {
        ItemStack fuel = findFuel();
        MeknucRodType type = MeknucPwrFuels.get(fuel);
        if (type == null || countFuel(fuel) < rods) {
            stopIfExhausted();
            return;
        }
        ItemStack product = fuel.copyWithCount(1);
        MeknucItemFuelRodBase.setRadiation(product, 0);
        MeknucItemFuelRodBase.setHot(product, true);
        if (!canStore(product, rods)) {
            stopIfExhausted();
            return;
        }
        consumeFuel(fuel, rods);
        runningProduct = product;
        setInCoreCount(rods);
        burnProgress = 0.0;
        batchBaseTicks = type.burnTime();
        refreshBurnTimeDisplay();
    }

    void refreshBurnTimeDisplay() {
        if (inCoreCount <= 0 || batchBaseTicks <= 0) {
            setBatchBurnTime(0);
            setBurnTime(0);
            return;
        }
        int total = (int) Math.round(batchBaseTicks * getBurnTimeFactor());
        setBatchBurnTime(total);
        setBurnTime((int) Math.round(total * (1.0 - burnProgress)));
    }

    private double getBatchLifeSeconds() {
        return Math.max(1.0, batchBaseTicks * getBurnTimeFactor() / 20.0);
    }

    private double getBurnTimeFactor() {
        return 1.0 + controlRodInsertion / 100.0;
    }

    public double getFuelPowerFactor() {
        if (inCoreCount <= 0) {
            return 1.0;
        }
        return Math.max(MIN_FUEL_POWER_FACTOR, 1.0 - 0.5 * burnProgress);
    }

    public double getRodPowerFactor() {
        return rodsFailed ? 1.0 : 1.0 - controlRodInsertion / 100.0;
    }

    private void stopIfExhausted() {
        if (autoStopOnFuelExhausted) {
            setActive(false);
        }
    }

    private void finishCycle() {
        if (!runningProduct.isEmpty() && inCoreCount > 0) {
            storeWaste(runningProduct, inCoreCount);
        }
        runningProduct = ItemStack.EMPTY;
        setInCoreCount(0);
        burnProgress = 0.0;
        batchBaseTicks = 0;
        setBatchBurnTime(0);
        setBurnTime(0);
    }

    private ItemStack findFuel() {
        for (IInventorySlot slot : fuelSlots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private int countFuel(ItemStack template) {
        int total = 0;
        for (IInventorySlot slot : fuelSlots) {
            ItemStack stack = slot.getStack();
            if (ItemStack.isSameItemSameComponents(stack, template)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private void consumeFuel(ItemStack template, int amount) {
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

    private void storeWaste(ItemStack template, int amount) {
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

    private boolean canStore(ItemStack template, int amount) {
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

    public int getFuelAssemblies() {
        return fuelAssemblies;
    }

    public void setFuelAssemblies(int count) {
        int clamped = Math.max(0, count);
        if (fuelAssemblies != clamped) {
            fuelAssemblies = clamped;
            markDirty();
        }
    }

    public double getDesignPower() {
        return Math.max(0, fuelAssemblies) * MW_PER_ASSEMBLY;
    }

    public double getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(double value) {
        double clamped = Math.max(0.0, value);
        if (powerLevel != clamped) {
            powerLevel = clamped;
            markDirty();
        }
    }

    public boolean isCoolantOn() {
        return coolantActive;
    }

    public void setCoolantOn(boolean value) {
        if (coolantActive != value) {
            coolantActive = value;
            markDirty();
        }
    }

    public boolean isRodsFailed() {
        return rodsFailed;
    }

    public void setRodsFailed(boolean value) {
        if (rodsFailed != value) {
            rodsFailed = value;
            markDirty();
        }
    }

    public double getBurnProgress() {
        return burnProgress;
    }

    public int getBatchBaseTicks() {
        return batchBaseTicks;
    }

    public static double waterPressure(double temperature) {
        if (temperature <= COOLANT_START_TEMPERATURE) {
            return 0.0;
        }
        return Math.min(MAX_PRESSURE,
              PRESSURE_STEP_MPA * (temperature - COOLANT_START_TEMPERATURE) / PRESSURE_STEP_TEMPERATURE);
    }

    public static double coolantEfficiency(double temperature) {
        if (temperature <= DERATE_TEMPERATURE) {
            return 1.0;
        }
        double steps = Math.floor((temperature - DERATE_TEMPERATURE) / DERATE_STEP_TEMPERATURE);
        return Math.max(0.0, 1.0 - DERATE_FACTOR * steps);
    }

    public double getWaterPressure() {
        return waterPressure(temperature);
    }

    public double getFlowMultiplier() {
        return getWaterPressure() * FLOW_MULTIPLIER_PER_MPA;
    }

    public double getCoolingEfficiency() {
        return coolantEfficiency(temperature);
    }

    public double getBoilLimit() {
        return Math.max(0, getVolume()) * BOIL_LIMIT_PER_BLOCK;
    }

    public double getRemovalCap() {
        return COOLING_FACTOR * getBoilLimit() * getCoolingEfficiency();
    }

    public double getMaxCoolRate() {
        double designFlow = getDesignPower() * MB_PER_MW;
        return Math.min(Math.max(0, getVolume()) * COOLANT_CAPACITY_PER_BLOCK, designFlow);
    }

    public double getBaseFlow() {
        if (!coolantActive) {
            return 0.0;
        }
        double designFlow = getDesignPower() * MB_PER_MW;
        return Math.max(0.0, Math.min(designFlow, Math.min(getBoilLimit() * MB_PER_MW, getMaxCoolRate())));
    }

    public double getCoolantRate() {
        return getBaseFlow() * getFlowMultiplier();
    }

    @Override
    protected int getMultiblockRedstoneLevel() {
        int capacity = getCacheCapacity();
        return capacity <= 0 ? 0 : MekanismUtils.redstoneLevelFromContents(fuelCount, capacity);
    }

    public long waterCapacity() {
        return Math.max(0, getVolume()) * COOLANT_CAPACITY_PER_BLOCK;
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
        refreshCacheCounts();
    }

    private void refreshCacheCounts() {
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
        if (!ItemStack.matches(fuelStack, fuelSample)) {
            fuelStack = fuelSample;
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
        if (!ItemStack.matches(wasteStack, wasteSample)) {
            wasteStack = wasteSample;
        }
    }

    public List<IInventorySlot> getInventorySlots(ReactorPortMode mode) {
        return switch (mode) {
            case FUEL_INPUT -> fuelSlots;
            case WASTE_OUTPUT -> wasteSlots;
            default -> Collections.emptyList();
        };
    }

    public List<IExtendedFluidTank> getFluidTanks(ReactorPortMode mode) {
        return Collections.emptyList();
    }

    public List<IChemicalTank> getChemicalTanks(ReactorPortMode mode) {
        return switch (mode) {
            case COOLANT_INPUT -> List.of(coolantTank);
            case COOLANT_OUTPUT -> List.of(heatedCoolantTank);
            default -> Collections.emptyList();
        };
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (active && meltedDown) {
            return;
        }
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
            refreshBurnTimeDisplay();
        }
    }

    public int getBurnTime() {
        return burnTime;
    }

    public boolean isAutoStopOnFuelExhausted() {
        return autoStopOnFuelExhausted;
    }

    public void setAutoStopOnFuelExhausted(boolean enabled) {
        if (autoStopOnFuelExhausted != enabled) {
            autoStopOnFuelExhausted = enabled;
            markDirty();
        }
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

    public void setBurnTime(int time) {
        int clamped = Math.max(0, time);
        if (burnTime != clamped) {
            burnTime = clamped;
            markDirty();
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double value) {
        double clamped = Math.max(AMBIENT_TEMPERATURE, value);
        if (temperature != clamped) {
            temperature = clamped;
            markDirty();
        }
    }

    public ItemStack getRunningProduct() {
        return runningProduct;
    }

    public void setRunningProduct(ItemStack stack) {
        runningProduct = stack;
    }

    public void setBurnProgress(double progress) {
        burnProgress = Mth.clamp(progress, 0.0, 1.0);
    }

    public void setBatchBaseTicks(int ticks) {
        batchBaseTicks = Math.max(0, ticks);
    }

    public int getMeltdownTimer() {
        return meltdownTimer;
    }

    public void setMeltdownTimer(int timer) {
        int clamped = Mth.clamp(timer, 0, MELTDOWN_TICKS);
        if (meltdownTimer != clamped) {
            meltdownTimer = clamped;
            markDirty();
        }
    }

    public int getMeltdownSeconds() {
        return (meltdownTimer + 19) / 20;
    }

    public boolean isMeltedDown() {
        return meltedDown;
    }

    public void setMeltedDown(boolean melted) {
        if (meltedDown != melted) {
            meltedDown = melted;
            markDirty();
            if (melted) {
                setActive(false);
            }
        }
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

    public int getInCoreCount() {
        return inCoreCount;
    }

    public int getFuelCount() {
        return fuelCount;
    }

    public void setFuelCount(int count) {
        int clamped = Mth.clamp(count, 0, CACHE_CAPACITY);
        if (fuelCount != clamped) {
            fuelCount = clamped;
            markDirty();
        }
    }

    public int getWasteCount() {
        return wasteCount;
    }

    public void setWasteCount(int count) {
        int clamped = Mth.clamp(count, 0, CACHE_CAPACITY);
        if (wasteCount != clamped) {
            wasteCount = clamped;
            markDirty();
        }
    }

    public int getBatchBurnTime() {
        return batchBurnTime;
    }

    public void setBatchBurnTime(int time) {
        int clamped = Math.max(0, time);
        if (batchBurnTime != clamped) {
            batchBurnTime = clamped;
            markDirty();
        }
    }

    public void setInCoreCount(int count) {
        int clamped = Math.max(0, count);
        if (inCoreCount != clamped) {
            inCoreCount = clamped;
            markDirty();
        }
    }

    private class CacheSlot extends BasicInventorySlot {

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
