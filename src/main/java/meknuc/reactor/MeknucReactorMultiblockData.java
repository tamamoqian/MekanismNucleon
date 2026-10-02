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
import mekanism.api.chemical.ChemicalStack;
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
import mekanism.common.util.NBTUtils;
import meknuc.chemicals.MeknucChemicals;
import meknuc.reactor.AttributeStateReactorPortMode.ReactorPortMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

public class MeknucReactorMultiblockData extends MultiblockData {

    public static final int CACHE_CAPACITY = 128;
    public static final int WATER_CAPACITY_PER_BLOCK = 400000;
    public static final double HEAT_PER_ROD = 17.7083;
    public static final double HEAT_CAPACITY_PER_ROD = 12.5;
    private static final double INVERSE_CONDUCTION_COEFFICIENT = 10.0;
    private static final double INVERSE_INSULATION_COEFFICIENT = 10000.0;
    private static final double ENVIRONMENT_INVERSE_CONDUCTION =
          INVERSE_CONDUCTION_COEFFICIENT + 2.0 * INVERSE_INSULATION_COEFFICIENT;
    public static final double HEAT_PER_MB = 0.00014;
    public static final double NOMINAL_INSERTION = 0.5;
    private static final int NOMINAL_INSERTION_PERCENT = (int) (NOMINAL_INSERTION * 100.0);
    public static final double MIN_TIME_FACTOR = 0.20;
    public static final double MAX_TIME_FACTOR = 2.20;
    public static final double OUTPUT_PER_MB = 1.0;
    public static final double DAMAGE_FACTOR = 0.001;
    public static final double MAX_TEMPERATURE_STEP = 2.0;
    private static final double BOILING_TEMPERATURE = 373.15;
    private static final double NOMINAL_TEMPERATURE = 673.15;
    public static final double DAMAGE_TEMPERATURE = 1073.15;
    public static final double MAX_DAMAGE = 100.0;
    public static final double REPAIR_PER_TICK = 0.01;
    public static final int MELTDOWN_TICKS = 200;
    public static final float MELTDOWN_EXPLOSION_RADIUS = 25.0F;
    public static final int MELTDOWN_RADIATION_RADIUS = 150;
    public static final double MELTDOWN_RADIATION_MAGNITUDE = 100.0;
    private static final double NOMINAL_STEAM_PER_ROD = 119791.67;
    public static final double EXCHANGE_PER_ROD =
          NOMINAL_STEAM_PER_ROD * HEAT_PER_MB * (1.0 - NOMINAL_INSERTION) / (NOMINAL_TEMPERATURE - BOILING_TEMPERATURE);
    private static final int INTERIOR_AREA = 37;
    private static final int BOUNDING_AREA = 49;

    private final List<FuelColumn> fuelColumns = new ArrayList<>();
    private final IInventorySlot fuelSlot;
    private final IInventorySlot wasteSlot;

    @ContainerSync
    public final IExtendedFluidTank waterTank;

    @ContainerSync
    public final IChemicalTank steamTank;

    @ContainerSync(getter = "getTemperature", setter = "setTemperature")
    private double temperature = 300.0;

    private double ambientTemp = 300.0;

    @ContainerSync(getter = "isActive", setter = "setActive")
    private boolean active;

    @ContainerSync(getter = "getControlRodInsertion", setter = "setControlRodInsertion")
    private int controlRodInsertion = NOMINAL_INSERTION_PERCENT;

    @ContainerSync(getter = "getBurnTime", setter = "setBurnTime")
    private int burnTime;

    private double burnPartial;
    private boolean insertionFromCache;
    private boolean temperatureFromCache;

    @ContainerSync(getter = "getDamage", setter = "setDamage")
    private double damage;

    @ContainerSync(getter = "getMeltdownTimer", setter = "setMeltdownTimer")
    private int meltdownTimer;

    @ContainerSync(getter = "isMeltedDown", setter = "setMeltedDown")
    private boolean meltedDown;

    @ContainerSync(getter = "getInCoreCount", setter = "setInCoreCount")
    private int inCoreCount;

    @ContainerSync(getter = "getLastEnvironmentLoss", setter = "setLastEnvironmentLoss")
    private double lastEnvironmentLoss;

    @ContainerSync(getter = "getFuelCount", setter = "setFuelCount")
    private int fuelCount;

    @ContainerSync(getter = "getWasteCount", setter = "setWasteCount")
    private int wasteCount;

    @ContainerSync(getter = "getFuelItemId", setter = "setFuelItemId")
    private int fuelItemId;

    @ContainerSync(getter = "getWasteItemId", setter = "setWasteItemId")
    private int wasteItemId;

    @ContainerSync(getter = "getLastFlow", setter = "setLastFlow")
    private double lastFlow;

    @ContainerSync(getter = "getBatchBurnTime", setter = "setBatchBurnTime")
    private int batchBurnTime;

    private ItemStack runningProduct = ItemStack.EMPTY;

    public MeknucReactorMultiblockData(BlockEntity tile) {
        super(tile);
        ambientTemp = HeatAPI.getAmbientTemp(tile.getLevel(), tile.getBlockPos());
        temperature = ambientTemp;
        waterTank = VariableCapacityFluidTank.input(this, () -> (int) waterCapacity(),
              fluid -> fluid.is(FluidTags.WATER), this);
        steamTank = VariableCapacityChemicalTank.output(this, this::waterCapacity,
              ConstantPredicates.alwaysTrue(), this);
        fluidTanks.add(waterTank);
        chemicalTanks.add(steamTank);
        fuelSlot = new CacheSlot(CACHE_CAPACITY, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.alwaysTrueBi(),
              MeknucReactorFuels::isFuel, this);
        wasteSlot = new CacheSlot(CACHE_CAPACITY, ConstantPredicates.alwaysTrueBi(), ConstantPredicates.internalOnly(),
              ConstantPredicates.alwaysTrue(), this);
        inventorySlots.add(fuelSlot);
        inventorySlots.add(wasteSlot);
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
    }

    @Override
    public void writeUpdateTag(CompoundTag tag, Provider provider) {
        super.writeUpdateTag(tag, provider);
        tag.putBoolean("reactor_active", active);
        tag.putBoolean("reactor_melted", meltedDown);
        tag.putInt("reactor_burn_time", burnTime);
        tag.putInt("reactor_insertion", controlRodInsertion);
    }

    private void tickReaction() {
        int rods = getFuelRodCount();
        tickFuelCycle(rods);
        if (rods <= 0) {
            return;
        }
        double capacity = rods * HEAT_CAPACITY_PER_ROD;
        double heat = active && burnTime > 0 ? rods * HEAT_PER_ROD * (1.0 - controlRodInsertion / 100.0) : 0.0;
        double ambientLoss = capacity * (temperature - ambientTemp) / ENVIRONMENT_INVERSE_CONDUCTION;
        setLastEnvironmentLoss(ambientLoss);
        double previous = temperature;
        temperature += (heat - ambientLoss) / capacity;

        long available = getWaterStored();
        long room = steamTank.getCapacity() - steamTank.getStored();
        double maxFlow = Math.max(0.0, Math.min(available, room) / OUTPUT_PER_MB);
        double exchange = rods * EXCHANGE_PER_ROD;
        double removed = Math.max(0.0, Math.min(exchange * (temperature - BOILING_TEMPERATURE), HEAT_PER_MB * maxFlow));
        double flow = 0.0;
        if (removed > 0.0) {
            flow = removed / HEAT_PER_MB;
            extractWater((int) Math.ceil(flow));
            steamTank.insert(new ChemicalStack(MeknucChemicals.HIGH_PRESSURE_STEAM,
                  (long) Math.floor(flow * OUTPUT_PER_MB)), Action.EXECUTE, AutomationType.INTERNAL);
            temperature -= removed / capacity;
        }
        setLastFlow(Math.floor(flow * OUTPUT_PER_MB));

        double step = temperature - previous;
        if (Math.abs(step) > MAX_TEMPERATURE_STEP) {
            temperature = previous + Math.copySign(MAX_TEMPERATURE_STEP, step);
        }
        setTemperature(temperature);
    }

    private void handleDamage(Level world) {
        if (meltedDown) {
            return;
        }
        if (temperature > DAMAGE_TEMPERATURE) {
            setDamage(damage + (temperature - DAMAGE_TEMPERATURE) * DAMAGE_FACTOR);
        } else {
            setDamage(damage - REPAIR_PER_TICK);
        }
        if (damage >= MAX_DAMAGE) {
            int next = meltdownTimer > 0 ? meltdownTimer : MELTDOWN_TICKS;
            setMeltdownTimer(next - 1);
            if (next <= 1) {
                createMeltdown(world);
            }
        } else if (meltdownTimer != 0) {
            setMeltdownTimer(0);
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

    private void tickFuelCycle(int rods) {
        if (rods <= 0) {
            setBurnTime(0);
            return;
        }
        if (!active) {
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
        MeknucReactorFuels.ReactorFuel spec = MeknucReactorFuels.get(fuel);
        if (spec == null || fuel.getCount() < rods) {
            return;
        }
        ItemStack product = MeknucReactorFuels.product(spec, rods);
        if (!canStore(product)) {
            return;
        }
        fuelSlot.setStack(fuel.copyWithCount(fuel.getCount() - rods));
        runningProduct = product;
        setInCoreCount(product.getCount());
        setBatchBurnTime(spec.burnTime());
        setBurnTime(spec.burnTime());
        burnPartial = 0.0;
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

    public int getFuelRodCount() {
        int count = 0;
        for (FuelColumn column : fuelColumns) {
            count += column.fuelHeight();
        }
        return count;
    }

    public long waterCapacity() {
        long height = Math.max(1, getVolume() / BOUNDING_AREA);
        return (long) INTERIOR_AREA * Math.max(1, height - 2) * WATER_CAPACITY_PER_BLOCK;
    }

    private long getWaterStored() {
        return waterTank.getFluidAmount();
    }

    private void extractWater(int amount) {
        waterTank.extract(amount, Action.EXECUTE, AutomationType.INTERNAL);
    }

    @Override
    public void onContentsChanged() {
        super.onContentsChanged();
        refreshCacheCounts();
    }

    private void refreshCacheCounts() {
        ItemStack fuel = fuelSlot.getStack();
        setFuelCount(fuel.getCount());
        setFuelItemId(fuel.isEmpty() ? 0 : BuiltInRegistries.ITEM.getId(fuel.getItem()));
        ItemStack waste = wasteSlot.getStack();
        setWasteCount(waste.getCount());
        setWasteItemId(waste.isEmpty() ? 0 : BuiltInRegistries.ITEM.getId(waste.getItem()));
        setInCoreCount(runningProduct.getCount());
    }

    public List<IInventorySlot> getInventorySlots(ReactorPortMode mode) {
        return switch (mode) {
            case FUEL_INPUT -> List.of(fuelSlot);
            case WASTE_OUTPUT -> List.of(wasteSlot);
            default -> Collections.emptyList();
        };
    }

    public List<IExtendedFluidTank> getFluidTanks(ReactorPortMode mode) {
        return switch (mode) {
            case COOLANT_INPUT -> List.of(waterTank);
            default -> Collections.emptyList();
        };
    }

    public List<IChemicalTank> getChemicalTanks(ReactorPortMode mode) {
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

    void restoreControlRodInsertion(int value) {
        setControlRodInsertion(value);
        insertionFromCache = true;
    }

    void restoreTemperature(double value) {
        setTemperature(value);
        temperatureFromCache = true;
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

    public ItemStack getRunningProduct() {
        return runningProduct;
    }

    public void setRunningProduct(ItemStack stack) {
        runningProduct = stack;
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
        return fuelSlot.getStack();
    }

    public ItemStack getWasteStack() {
        return wasteSlot.getStack();
    }

    public int getInCoreCount() {
        return inCoreCount;
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

    public int getFuelItemId() {
        return fuelItemId;
    }

    public void setFuelItemId(int id) {
        if (fuelItemId != id) {
            fuelItemId = id;
            markDirty();
        }
    }

    public int getWasteItemId() {
        return wasteItemId;
    }

    public void setWasteItemId(int id) {
        if (wasteItemId != id) {
            wasteItemId = id;
            markDirty();
        }
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

    public void setInCoreCount(int count) {
        int clamped = Math.max(0, count);
        if (inCoreCount != clamped) {
            inCoreCount = clamped;
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
