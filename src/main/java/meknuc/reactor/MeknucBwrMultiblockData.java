package meknuc.reactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.heat.HeatAPI;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.util.NBTUtils;
import meknuc.reactor.AttributeStateBwrPortMode.BwrPortMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MeknucBwrMultiblockData extends MeknucReactorMultiblockData {

    public static final int CACHE_CAPACITY = 2940;
    public static final int COOLANT_CAPACITY_PER_BLOCK = 4000000;
    public static final int BOIL_CAPACITY_PER_BLOCK = 8000000;
    public static final double HEAT_PER_ASSEMBLY = 4.52942;
    public static final double ENVIRONMENT_LOSS_PER_ASSEMBLY = 0.0007;
    public static final double RECIRCULATION_PER_ASSEMBLY = 49218.75;
    public static final double FEEDWATER_MARGIN = 1.2;
    public static final double OUTPUT_MULTIPLIER = 1.0;
    public static final double VOID_FEEDBACK = 1.0;
    public static final double VOID_GENERATION = 1.0;
    public static final double TEMPERATURE_FEEDBACK = 0.0005;
    public static final double CRITICAL_VOID = 0.35;
    public static final double DRYOUT_HEAT_FACTOR = 0.15;
    public static final double MAX_VOID = 0.95;
    public static final double MIN_TIME_FACTOR = 0.20;
    public static final double MAX_TIME_FACTOR = 2.20;
    public static final double MAX_TEMPERATURE_STEP = 1.0;
    public static final double DAMAGE_TEMPERATURE = 1000.0;
    public static final double DAMAGE_DIVISOR = 5000.0;
    public static final double DAMAGE_FACTOR = 1.0;
    public static final double MAX_DAMAGE = 100.0;
    public static final double MELTDOWN_THRESHOLD = 80.0;
    private static final double SURFACE_AREA_TARGET = 4.0;

    private final List<FuelColumn> fuelColumns = new ArrayList<>();

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

    private final MeknucBwrSimulation simulation;

    public MeknucBwrMultiblockData(BlockEntity tile) {
        super(tile, CACHE_CAPACITY, MeknucBwrFuels::isFuel);
        simulation = new MeknucBwrSimulation(this);
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
    }

    @Override
    protected double defaultTemperature() {
        return ambientTemp;
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
        ambientTemp = calculateAverageAmbientTemperature(world);
        super.onCreated(world);
        refreshCacheCounts();
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (isFormed() && !isRemote()) {
            refreshCacheCounts();
            simulation.tickReaction();
            simulation.handleDamage(world);
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

    double getAmbientTemperature() {
        return ambientTemp;
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

    public long coolantCapacity() {
        return (long) getInteriorVolume() * COOLANT_CAPACITY_PER_BLOCK;
    }

    public long boilCapacity() {
        return (long) getInteriorVolume() * BOIL_CAPACITY_PER_BLOCK;
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
            case FUEL_INPUT -> fuelSlots;
            case WASTE_OUTPUT -> wasteSlots;
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
        int clamped = Mth.clamp(timer, 0, MeknucReactorConstants.MELTDOWN_TICKS);
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
}
