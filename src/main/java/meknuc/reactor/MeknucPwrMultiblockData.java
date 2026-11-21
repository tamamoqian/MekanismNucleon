package meknuc.reactor;

import java.util.Collections;
import java.util.List;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import meknuc.chemicals.MeknucChemicals;
import meknuc.reactor.AttributeStatePwrPortMode.ReactorPortMode;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MeknucPwrMultiblockData extends MeknucReactorMultiblockData {

    public static final int CACHE_CAPACITY = 357;

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
    public static final double DERATE_STEP_TEMPERATURE = 10.0;
    public static final double DERATE_FACTOR = 0.1;
    public static final double MELTDOWN_RAMP_PER_SECOND = 1000.0 / 60.0;
    public static final double POWER_RAMP_SECONDS = 20.0;
    public static final double MIN_FUEL_POWER_FACTOR = 0.5;

    static final double TICK_SECONDS = 0.05;

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

    private final MeknucPwrSimulation simulation;

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

    @ContainerSync(getter = "getFuelCount", setter = "setFuelCount")
    private int fuelCount;

    @ContainerSync(getter = "getWasteCount", setter = "setWasteCount")
    private int wasteCount;

    @ContainerSync(getter = "getFuelStack", setter = "setFuelStack")
    private ItemStack fuelStack = ItemStack.EMPTY;

    @ContainerSync(getter = "getWasteStack", setter = "setWasteStack")
    private ItemStack wasteStack = ItemStack.EMPTY;

    public MeknucPwrMultiblockData(BlockEntity tile) {
        super(tile, CACHE_CAPACITY, MeknucPwrFuels::isFuel);
        simulation = new MeknucPwrSimulation(this);
        coolantTank = VariableCapacityChemicalTank.input(this, this::waterCapacity,
              chemical -> chemical.is(MeknucChemicals.BORATED_LIGHT_WATER.get()), this);
        heatedCoolantTank = VariableCapacityChemicalTank.output(this, this::waterCapacity,
              chemical -> chemical.is(MeknucChemicals.PRESSURIZED_BORATED_LIGHT_WATER.get()), this);
        chemicalTanks.add(coolantTank);
        chemicalTanks.add(heatedCoolantTank);
    }

    @Override
    protected double defaultTemperature() {
        return AMBIENT_TEMPERATURE;
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
        simulation.tickCore();
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

    @Override
    public void meltdownHappened(Level world) {
        setActive(false);
        setMeltedDown(true);
        setMeltdownTimer(0);
        setBurnTime(0);
        setPowerLevel(0.0);
        setRodsFailed(true);
    }

    void refreshBurnTimeDisplay() {
        simulation.refreshBurnTimeDisplay();
    }

    public double getFuelPowerFactor() {
        return simulation.getFuelPowerFactor();
    }

    public double getRodPowerFactor() {
        return simulation.getRodPowerFactor();
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
        return simulation.getBurnProgress();
    }

    public int getBatchBaseTicks() {
        return simulation.getBatchBaseTicks();
    }

    public static double waterPressure(double temperature) {
        if (temperature <= COOLANT_START_TEMPERATURE) {
            return 0.0;
        }
        return Math.min(MAX_PRESSURE,
              PRESSURE_STEP_MPA * (temperature - COOLANT_START_TEMPERATURE) / PRESSURE_STEP_TEMPERATURE);
    }

    public static double coolantEfficiency(double temperature) {
        if (temperature <= MeknucPwrConstants.DERATE_TEMPERATURE) {
            return 1.0;
        }
        double steps = Math.floor((temperature - MeknucPwrConstants.DERATE_TEMPERATURE) / DERATE_STEP_TEMPERATURE);
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

    public long waterCapacity() {
        return Math.max(0, getVolume()) * COOLANT_CAPACITY_PER_BLOCK;
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

    public void setBurnProgress(double progress) {
        simulation.setBurnProgress(progress);
    }

    public void setBatchBaseTicks(int ticks) {
        simulation.setBatchBaseTicks(ticks);
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
}
