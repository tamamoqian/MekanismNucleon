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
import mekanism.api.heat.HeatAPI;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.reactor.AttributeStateBwrPortMode.BwrPortMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MeknucBwrMultiblockData extends MultiblockData {

    public static final int CACHE_CAPACITY = 2940;
    public static final int COOLANT_CAPACITY_PER_BLOCK = 4000000;
    public static final int BOIL_CAPACITY_PER_BLOCK = 8000000;
    public static final double HEAT_PER_ASSEMBLY = 4.52942;
    public static final double HEAT_CAPACITY_PER_ASSEMBLY = 11.5;
    public static final double ENVIRONMENT_LOSS_PER_ASSEMBLY = 0.0007;
    public static final double HEAT_PER_MB = 0.00014;
    public static final double RECIRCULATION_PER_ASSEMBLY = 49218.75;
    public static final double STEAM_PER_ASSEMBLY = 9375.0;
    public static final double FEEDWATER_MARGIN = 1.2;
    public static final double OUTPUT_MULTIPLIER = 1.0;
    public static final double VOID_FEEDBACK = 1.0;
    public static final double VOID_GENERATION = 1.0;
    public static final double TEMPERATURE_FEEDBACK = 0.0005;
    public static final double CRITICAL_VOID = 0.35;
    public static final double DRYOUT_HEAT_FACTOR = 0.15;
    public static final double MAX_VOID = 0.95;
    public static final double DESIGN_TEMPERATURE = 585.0;
    public static final double SATURATION_TEMPERATURE = 285.0;
    public static final double NOMINAL_INSERTION = 0.5;
    private static final int NOMINAL_INSERTION_PERCENT = (int) (NOMINAL_INSERTION * 100.0);
    public static final double MIN_TIME_FACTOR = 0.20;
    public static final double MAX_TIME_FACTOR = 2.20;
    public static final double MAX_TEMPERATURE_STEP = 1.0;
    public static final double DAMAGE_TEMPERATURE = 1000.0;
    public static final double DAMAGE_DIVISOR = 5000.0;
    public static final double DAMAGE_FACTOR = 1.0;
    public static final double MAX_DAMAGE = 100.0;
    public static final double MELTDOWN_THRESHOLD = 80.0;
    public static final int MELTDOWN_TICKS = 200;
    private static final double SURFACE_AREA_TARGET = 4.0;
    public static final float MELTDOWN_EXPLOSION_RADIUS = 25.0F;
    public static final int MELTDOWN_RADIATION_RADIUS = 150;
    public static final double MELTDOWN_RADIATION_MAGNITUDE = 100.0;

    private final List<FuelColumn> fuelColumns = new ArrayList<>();
    private final IInventorySlot fuelSlot;
    private final IInventorySlot wasteSlot;

    @ContainerSync
    public final IExtendedFluidTank waterTank;

    @ContainerSync
    public final IChemicalTank steamTank;

    @ContainerSync
    public final IChemicalTank boilTank;

    @ContainerSync(getter = "getTemperature", setter = "setTemperature")
    private double temperature = 300.0;

    private double ambientTemp = 300.0;

    @ContainerSync(getter = "isActive", setter = "setActive")
    private boolean active;

    @ContainerSync(getter = "getControlRodInsertion", setter = "setControlRodInsertion")
    private int controlRodInsertion = NOMINAL_INSERTION_PERCENT;

    @ContainerSync(getter = "getBurnTime", setter = "setBurnTime")
    private int burnTime;

    @ContainerSync(getter = "isAutoStopOnFuelExhausted", setter = "setAutoStopOnFuelExhausted")
    private boolean autoStopOnFuelExhausted = true;

    @ContainerSync(getter = "getDamage", setter = "setDamage")
    private double damage;

    @ContainerSync(getter = "getMeltdownTimer", setter = "setMeltdownTimer")
    private int meltdownTimer;

    @ContainerSync(getter = "isMeltedDown", setter = "setMeltedDown")
    private boolean meltedDown;

    @ContainerSync(getter = "getInCoreCount", setter = "setInCoreCount")
    private int inCoreCount;

    @ContainerSync(getter = "getFuelAssemblies", setter = "setFuelAssemblies")
    private int fuelAssemblies;

    @ContainerSync(getter = "getSurfaceArea", setter = "setSurfaceArea")
    private int surfaceArea;

    @ContainerSync(getter = "getLastEnvironmentLoss", setter = "setLastEnvironmentLoss")
    private double lastEnvironmentLoss;

    @ContainerSync(getter = "getFuelCount", setter = "setFuelCount")
    private int fuelCount;

    @ContainerSync(getter = "getWasteCount", setter = "setWasteCount")
    private int wasteCount;

    @ContainerSync(getter = "getFuelStack", setter = "setFuelStack")
    private ItemStack fuelStack = ItemStack.EMPTY;

    @ContainerSync(getter = "getWasteStack", setter = "setWasteStack")
    private ItemStack wasteStack = ItemStack.EMPTY;

    @ContainerSync(getter = "getLastFlow", setter = "setLastFlow")
    private double lastFlow;

    @ContainerSync(getter = "getBatchBurnTime", setter = "setBatchBurnTime")
    private int batchBurnTime;

    private double burnPartial;
    private ItemStack runningProduct = ItemStack.EMPTY;
    private boolean autoStopFromCache;
    private boolean insertionFromCache;
    private boolean temperatureFromCache;

    public MeknucBwrMultiblockData(BlockEntity tile) {
        super(tile);
        ambientTemp = HeatAPI.getAmbientTemp(tile.getLevel(), tile.getBlockPos());
        temperature = ambientTemp;
        waterTank = VariableCapacityFluidTank.input(this, () -> (int) coolantCapacity(),
              fluid -> fluid.is(FluidTags.WATER), this);
        steamTank = VariableCapacityChemicalTank.output(this::boilCapacity,
              chemical -> chemical.is(MekanismChemicals.STEAM.get()), this);
        boilTank = VariableCapacityChemicalTank.create(this::boilCapacity,
              ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(),
              chemical -> chemical.is(MekanismChemicals.STEAM.get()), this);
        fluidTanks.add(waterTank);
        chemicalTanks.add(boilTank);
        chemicalTanks.add(steamTank);
        fuelSlot = new CacheSlot(CACHE_CAPACITY, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueBi(),
              MeknucBwrFuels::isFuel, this);
        wasteSlot = new CacheSlot(CACHE_CAPACITY, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(),
              ConstantPredicates.alwaysTrue(), this);
        inventorySlots.add(fuelSlot);
        inventorySlots.add(wasteSlot);
    }

    public int getDiameter() {
        return length();
    }

    public int getDepth() {
        return width();
    }

    public int getInteriorVolume() {
        int spanX = getDiameter() - 4;
        int spanZ = getDepth() - 4;
        int layers = height() - 2;
        if (spanX <= 0 || spanZ <= 0 || layers <= 0) {
            return 1;
        }
        return spanX * spanZ * layers;
    }

    @Override
    public void onCreated(Level world) {
        super.onCreated(world);
        ambientTemp = calculateAverageAmbientTemperature(world);
        if (!temperatureFromCache) {
            setTemperature(ambientTemp);
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
        refreshCacheCounts();
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (isFormed() && !isRemote()) {
            refreshCacheCounts();
            tickReaction();
            handleDamage(world);
        }
        return needsPacket;
    }

    @Override
    public void readUpdateTag(CompoundTag tag, Provider provider) {
        super.readUpdateTag(tag, provider);
        NBTUtils.setBooleanIfPresent(tag, "reactor_active", value -> active = value);
        NBTUtils.setBooleanIfPresent(tag, "reactor_melted", value -> meltedDown = value);
        NBTUtils.setIntIfPresent(tag, "reactor_burn_time", value -> burnTime = value);
        NBTUtils.setIntIfPresent(tag, "reactor_insertion", value -> controlRodInsertion = value);
        NBTUtils.setBooleanIfPresent(tag, "reactor_auto_stop", value -> autoStopOnFuelExhausted = value);
    }

    @Override
    public void writeUpdateTag(CompoundTag tag, Provider provider) {
        super.writeUpdateTag(tag, provider);
        tag.putBoolean("reactor_active", active);
        tag.putBoolean("reactor_melted", meltedDown);
        tag.putInt("reactor_burn_time", burnTime);
        tag.putInt("reactor_insertion", controlRodInsertion);
        tag.putBoolean("reactor_auto_stop", autoStopOnFuelExhausted);
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

    @Override
    protected int getMultiblockRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fuelCount, CACHE_CAPACITY);
    }

    public int getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(int area) {
        int clamped = Math.max(0, area);
        if (surfaceArea != clamped) {
            surfaceArea = clamped;
            markDirty();
        }
    }

    public double getBoilEfficiency() {
        if (fuelAssemblies <= 0) {
            return 0.0;
        }
        return Math.min(1.0, (double) surfaceArea / fuelAssemblies / SURFACE_AREA_TARGET);
    }

    public ItemStack getRunningProduct() {
        return runningProduct;
    }

    public void setRunningProduct(ItemStack stack) {
        runningProduct = stack;
    }

    void restoreControlRodInsertion(int value) {
        setControlRodInsertion(value);
        insertionFromCache = true;
    }

    void restoreTemperature(double value) {
        setTemperature(value);
        temperatureFromCache = true;
    }

    public long coolantCapacity() {
        return (long) getInteriorVolume() * COOLANT_CAPACITY_PER_BLOCK;
    }

    public long boilCapacity() {
        return (long) getInteriorVolume() * BOIL_CAPACITY_PER_BLOCK;
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
        ItemStack fuel = fuelSlot.getStack();
        setFuelCount(fuel.getCount());
        if (!ItemStack.matches(fuelStack, fuel)) {
            fuelStack = fuel.copy();
        }
        ItemStack waste = wasteSlot.getStack();
        setWasteCount(waste.getCount());
        if (!ItemStack.matches(wasteStack, waste)) {
            wasteStack = waste.copy();
        }
        setInCoreCount(runningProduct.getCount());
    }

    private void tickReaction() {
        int rods = getFuelAssemblies();
        tickFuelCycle(rods);
        if (rods <= 0) {
            return;
        }
        double tempC = temperature - 273.15;
        double envC = ambientTemp - 273.15;
        double capacity = rods * HEAT_CAPACITY_PER_ASSEMBLY;
        double environment = rods * ENVIRONMENT_LOSS_PER_ASSEMBLY;
        double recirculation = rods * RECIRCULATION_PER_ASSEMBLY;
        double steamTarget = rods * STEAM_PER_ASSEMBLY;
        double heatTarget = steamTarget * HEAT_PER_MB;
        double exchange = heatTarget / (DESIGN_TEMPERATURE - SATURATION_TEMPERATURE) * getBoilEfficiency();
        double powerFactor = 1.0 - controlRodInsertion / 100.0;
        double temperatureFactor = Mth.clamp(1.0 - TEMPERATURE_FEEDBACK * (tempC - DESIGN_TEMPERATURE), 0.0, 1.0);
        double availablePower = rods * HEAT_PER_ASSEMBLY * powerFactor * temperatureFactor;
        double voidDenominator = HEAT_PER_MB * recirculation;
        double fissionPower = active && burnTime > 0
              ? availablePower / (1.0 + VOID_FEEDBACK * VOID_GENERATION * availablePower / voidDenominator)
              : 0.0;
        double voidFraction = Mth.clamp(VOID_GENERATION * fissionPower / voidDenominator, 0.0, MAX_VOID);
        double dryoutFactor = voidFraction >= CRITICAL_VOID ? DRYOUT_HEAT_FACTOR : 1.0;

        double demand = exchange * Math.max(0.0, tempC - SATURATION_TEMPERATURE) * dryoutFactor;
        double feedwaterLimit = steamTarget * FEEDWATER_MARGIN * HEAT_PER_MB;
        double waterLimit = waterTank.getFluidAmount() * HEAT_PER_MB;
        double heatTaken = Math.max(0.0, Math.min(Math.min(demand, feedwaterLimit), waterLimit));

        long boilStored = boilTank.getStored();
        double boilLimit = Math.max(0.0, (boilTank.getCapacity() - boilStored) + steamTarget * OUTPUT_MULTIPLIER);
        double boiled = Math.min(heatTaken / HEAT_PER_MB, boilLimit);
        heatTaken = boiled * HEAT_PER_MB;

        if (boiled > 0.0) {
            waterTank.extract((int) Math.ceil(boiled), Action.EXECUTE, AutomationType.INTERNAL);
            boilTank.insert(MekanismChemicals.STEAM.asStack((long) Math.floor(boiled)),
                  Action.EXECUTE, AutomationType.INTERNAL);
        }

        double output = Math.min(boilStored + boiled, steamTarget * OUTPUT_MULTIPLIER);
        if (output > 0.0) {
            long amount = (long) Math.floor(output);
            boilTank.extract(amount, Action.EXECUTE, AutomationType.INTERNAL);
            steamTank.insert(MekanismChemicals.STEAM.asStack(amount), Action.EXECUTE, AutomationType.INTERNAL);
        }
        setLastFlow(Math.floor(output));

        double environmentLoss = environment * Math.max(0.0, tempC - envC);
        setLastEnvironmentLoss(environmentLoss / capacity);

        double previous = temperature;
        temperature += (fissionPower - heatTaken - environmentLoss) / capacity;
        double step = temperature - previous;
        if (Math.abs(step) > MAX_TEMPERATURE_STEP) {
            temperature = previous + Math.copySign(MAX_TEMPERATURE_STEP, step);
        }
        setTemperature(temperature);
    }

    private void tickFuelCycle(int rods) {
        if (rods <= 0) {
            setBurnTime(0);
            stopIfExhausted();
            return;
        }
        if (!active) {
            return;
        }
        if (isBurnPaused()) {
            return;
        }
        if (burnTime > 0) {
            burnPartial += 1.0 / getBurnTimeMultiplier();
            int steps = (int) burnPartial;
            if (steps > 0) {
                burnPartial -= steps;
                setBurnTime(burnTime - steps);
                if (burnTime <= 0) {
                    finishCycle();
                }
            }
            return;
        }
        ItemStack fuel = fuelSlot.getStack();
        MeknucRodType type = MeknucBwrFuels.get(fuel);
        if (type == null || fuel.getCount() < rods) {
            stopIfExhausted();
            return;
        }
        ItemStack product = fuel.copyWithCount(rods);
        MeknucItemFuelRodBase.setRadiation(product, 0);
        MeknucItemFuelRodBase.setHot(product, true);
        if (!canStore(product)) {
            stopIfExhausted();
            return;
        }
        fuelSlot.setStack(fuel.copyWithCount(fuel.getCount() - rods));
        runningProduct = product;
        setInCoreCount(product.getCount());
        setBatchBurnTime(type.burnTime());
        setBurnTime(type.burnTime());
        burnPartial = 0.0;
    }

    private void stopIfExhausted() {
        if (autoStopOnFuelExhausted) {
            setActive(false);
        }
    }

    private void finishCycle() {
        if (!runningProduct.isEmpty() && canStore(runningProduct)) {
            ItemStack waste = wasteSlot.getStack();
            if (waste.isEmpty()) {
                wasteSlot.setStack(runningProduct.copy());
            } else if (ItemStack.isSameItemSameComponents(waste, runningProduct)
                  && waste.getCount() + runningProduct.getCount() <= wasteSlot.getLimit(ItemStack.EMPTY)) {
                waste.grow(runningProduct.getCount());
                wasteSlot.setStack(waste);
            }
        }
        runningProduct = ItemStack.EMPTY;
        setInCoreCount(0);
        setBatchBurnTime(0);
    }

    private boolean canStore(ItemStack stack) {
        ItemStack waste = wasteSlot.getStack();
        if (waste.isEmpty()) {
            return stack.getCount() <= wasteSlot.getLimit(ItemStack.EMPTY);
        }
        return ItemStack.isSameItemSameComponents(waste, stack)
              && waste.getCount() + stack.getCount() <= wasteSlot.getLimit(ItemStack.EMPTY);
    }

    private void handleDamage(Level world) {
        if (meltedDown) {
            return;
        }
        double tempC = temperature - 273.15;
        if (tempC > DAMAGE_TEMPERATURE) {
            setDamage(damage + (tempC - DAMAGE_TEMPERATURE) / DAMAGE_DIVISOR * 100.0 * DAMAGE_FACTOR);
        }
        if (damage >= MELTDOWN_THRESHOLD) {
            createMeltdown(world);
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
        setDamage(MAX_DAMAGE);
    }

    public List<IInventorySlot> getInventorySlots(BwrPortMode mode) {
        return switch (mode) {
            case FUEL_INPUT -> List.of(fuelSlot);
            case WASTE_OUTPUT -> List.of(wasteSlot);
            default -> Collections.emptyList();
        };
    }

    public List<IExtendedFluidTank> getFluidTanks(BwrPortMode mode) {
        return switch (mode) {
            case COOLANT_INPUT -> List.of(waterTank);
            default -> Collections.emptyList();
        };
    }

    public List<IChemicalTank> getChemicalTanks(BwrPortMode mode) {
        return switch (mode) {
            case COOLANT_OUTPUT -> List.of(steamTank);
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

    public boolean isBurnPaused() {
        return controlRodInsertion >= 100;
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

    public double getBurnTimeMultiplier() {
        int offset = controlRodInsertion - NOMINAL_INSERTION_PERCENT;
        if (offset == 0) {
            return 1.0;
        }
        if (offset > 0) {
            return 1.0 + (MAX_TIME_FACTOR - 1.0) * offset / (100.0 - NOMINAL_INSERTION_PERCENT);
        }
        return 1.0 - (1.0 - MIN_TIME_FACTOR) * (-offset) / (double) NOMINAL_INSERTION_PERCENT;
    }

    public int getDisplayedBurnTime() {
        return (int) Math.round(burnTime * getBurnTimeMultiplier());
    }

    public int getDisplayedBatchBurnTime() {
        return (int) Math.round(batchBurnTime * getBurnTimeMultiplier());
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        double clamped = Math.max(ambientTemp, temperature);
        if (this.temperature != clamped) {
            this.temperature = clamped;
            markDirty();
        }
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

    public void setInCoreCount(int count) {
        int clamped = Math.max(0, count);
        if (inCoreCount != clamped) {
            inCoreCount = clamped;
            markDirty();
        }
    }

    public double getLastEnvironmentLoss() {
        return lastEnvironmentLoss;
    }

    public void setLastEnvironmentLoss(double loss) {
        if (lastEnvironmentLoss != loss) {
            lastEnvironmentLoss = loss;
            markDirty();
        }
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

    public double getLastFlow() {
        return lastFlow;
    }

    public void setLastFlow(double flow) {
        double clamped = Math.max(0.0, flow);
        if (lastFlow != clamped) {
            lastFlow = clamped;
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

    public List<FuelColumn> getFuelColumns() {
        return Collections.unmodifiableList(fuelColumns);
    }

    public void setFuelColumns(List<FuelColumn> columns) {
        fuelColumns.clear();
        fuelColumns.addAll(columns);
    }

    public record FuelColumn(BlockPos base, int fuelHeight) {

        public int totalHeight() {
            return fuelHeight;
        }
    }

    private static class CacheSlot extends BasicInventorySlot {

        private CacheSlot(int limit, BiPredicate<ItemStack, AutomationType> canExtract,
              BiPredicate<ItemStack, AutomationType> canInsert, Predicate<ItemStack> validator,
              IContentsListener listener) {
            super(limit, canExtract, canInsert, validator, listener, 0, 0);
            obeyStackLimit = false;
        }
    }
}
