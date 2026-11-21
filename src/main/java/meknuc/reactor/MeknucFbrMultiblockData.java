package meknuc.reactor;

import java.util.List;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.capabilities.chemical.VariableCapacityChemicalTank;
import mekanism.common.inventory.container.sync.dynamic.ContainerSync;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.registries.MekanismChemicals;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import meknuc.blocks.MeknucFbrBlockTypes;
import meknuc.items.MeknucItems;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import mekanism.api.radiation.IRadiationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.EventHooks;

/**
 * 快中子增殖堆（FBR）多方块数据。
 *
 * <p>物理按《快中子增殖堆运行公式 v2》实现：功率由「控制棒插入度 × 氙毒/高温惩罚」给出，
 * 钠带走热量，燃料按进度烧掉，反射包层按批产出钚-239粉。</p>
 */
public class MeknucFbrMultiblockData extends MeknucReactorMultiblockData {

    public static final int MIN_SIZE = 9;
    public static final int MAX_SIZE = 13;
    public static final int MIN_HEIGHT = 5;
    public static final int MAX_HEIGHT = 18;

    public static final double MW_PER_ASSEMBLY = 15.625;
    public static final double COOLING_MW_PER_BLOCK = 3.0;
    public static final double TANK_MB_PER_BLOCK = 12857.1429;
    public static final double SODIUM_MB_PER_TICK_PER_MW = 4285.7143;
    /** 钠的冷却效率：用户要求下调 25%。 */
    public static final double COOLANT_EFFECTIVENESS = 0.75;
    public static final double MOLTEN_ROD_TEMP = 800.0;
    public static final double EXPLODE_TEMP = 1500.0;
    public static final double RUNAWAY_PER_TICK = 1000.0 / 60.0 / 20.0;
    /** 熔毁爆炸：中心半径（方块）与辐射区半径（方块）、中心剂量（Sv）。 */
    public static final float EXPLOSION_RADIUS = 32.0F;
    public static final int RADIATION_RADIUS = 48;
    public static final double RADIATION_CENTER_SV = 100.0;
    public static final int POWER_TAU_TICKS = 400;
    public static final int XENON_DELAY_TICKS = 1200;
    public static final int BREED_BATCH_TICKS = 12000;
    /** 每块中子反射包层每批的增殖通量（kg），按用户要求放大 5 倍。 */
    public static final double BREED_PER_CLADDING = 5.0;
    public static final int SLOT_LIMIT = 64;

    private static final double XENON_BASE = 50.0;
    private static final double XENON_MIN = 20.0;
    private static final double XENON_MAX = 80.0;
    private static final double XENON_RUN_RATE = 10.0 / 1200.0;
    private static final double XENON_STOP_RATE = 15.0 / 1200.0;
    private static final double XENON_DECAY_RATE = 40.0 / 1200.0;

    public final IChemicalTank sodiumTank;
    public final IChemicalTank hotSodiumTank;

    private final IInventorySlot fuelSlot;
    private final IInventorySlot wasteSlot;
    private final IInventorySlot breederSlot;
    private final IInventorySlot plutoniumSlot;

    @ContainerSync(getter = "isActive", setter = "setActive")
    private boolean active;

    @ContainerSync(getter = "getControlRodInsertion", setter = "setControlRodInsertion")
    private int controlRodInsertion = 100;

    @ContainerSync(getter = "getCoreTemperature", setter = "setCoreTemperature")
    private double coreTemp = 25.0;

    @ContainerSync(getter = "getPower", setter = "setPower")
    private double power;

    @ContainerSync(getter = "getXenon", setter = "setXenon")
    private double xenon;

    @ContainerSync(getter = "getBurnProgress", setter = "setBurnProgress")
    private double burnProgress;

    @ContainerSync(getter = "getRodsInCore", setter = "setRodsInCore")
    private int rodsInCore;

    @ContainerSync(getter = "getRodsDepleted", setter = "setRodsDepleted")
    private int rodsDepleted;

    @ContainerSync(getter = "getUraniumStock", setter = "setUraniumStock")
    private int uraniumStock;

    @ContainerSync(getter = "getPlutoniumStock", setter = "setPlutoniumStock")
    private int plutoniumStock;

    @ContainerSync(getter = "getBreedProgress", setter = "setBreedProgress")
    private int breedProgress;

    @ContainerSync(getter = "getCoolantFlow", setter = "setCoolantFlow")
    private int coolantFlow;

    @ContainerSync(getter = "getSodiumStored", setter = "setSodiumStored")
    private int sodiumStored;

    @ContainerSync(getter = "getHotSodiumStored", setter = "setHotSodiumStored")
    private int hotSodiumStored;

    @ContainerSync(getter = "getCladdingCount", setter = "setCladdingCount")
    private int claddingCount;

    @ContainerSync(getter = "getVolume", setter = "setVolume")
    private int volume;

    @ContainerSync(getter = "isRodsMelted", setter = "setRodsMelted")
    private boolean rodsMelted;

    @ContainerSync(getter = "isExploded", setter = "setExploded")
    private boolean exploded;

    /** 控制棒是否被玩家动过：初始全插，未拔出前反应堆不启动。 */
    private boolean rodsReleased;

    private boolean autoStopOnFuelExhausted = true;

    private int fuelAssemblies;
    private int surfaceArea;
    private static final int SOUND_INTERVAL = 50;
    private static final float SOUND_VOLUME = 4.0F;
    private static SoundEvent reactorSound;

    private final BlockPos soundOrigin;
    private int soundTimer;
    private int xenonPhase;
    private int ageTicks;
    private MeknucRodType fuelType = MeknucFbrFuels.MOX;

    public MeknucFbrMultiblockData(BlockEntity tile) {
        super(tile, 0, MeknucFbrFuels::isFuel);
        soundOrigin = tile.getBlockPos();
        sodiumTank = VariableCapacityChemicalTank.input(this, this::tankCapacity,
              chemical -> chemical.is(MekanismChemicals.SODIUM.get()), this);
        hotSodiumTank = VariableCapacityChemicalTank.output(this, this::tankCapacity,
              chemical -> chemical.is(MekanismChemicals.SUPERHEATED_SODIUM.get()), this);
        chemicalTanks.add(sodiumTank);
        chemicalTanks.add(hotSodiumTank);
        fuelSlot = new FbrSlot(SLOT_LIMIT, ConstantPredicates.internalOnly(),
              ConstantPredicates.alwaysTrueBi(), MeknucFbrFuels::isFuel, this);
        wasteSlot = new FbrSlot(SLOT_LIMIT, ConstantPredicates.alwaysTrueBi(),
              ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue(), this);
        breederSlot = new FbrSlot(SLOT_LIMIT, ConstantPredicates.internalOnly(),
              ConstantPredicates.alwaysTrueBi(), stack -> stack.is(MeknucItems.URANIUM_238_DUST.get()), this);
        plutoniumSlot = new FbrSlot(SLOT_LIMIT, ConstantPredicates.alwaysTrueBi(),
              ConstantPredicates.internalOnly(), ConstantPredicates.alwaysTrue(), this);
        inventorySlots.add(fuelSlot);
        inventorySlots.add(wasteSlot);
        inventorySlots.add(breederSlot);
        inventorySlots.add(plutoniumSlot);
    }

    @Override
    protected double defaultTemperature() {
        return 25.0;
    }

    @Override
    protected int defaultControlRodInsertion() {
        return 100;
    }

    @Override
    public double getTemperature() {
        return coreTemp;
    }

    @Override
    public void setTemperature(double value) {
        setCoreTemperature(value);
    }

    @Override
    public boolean isAutoStopOnFuelExhausted() {
        return autoStopOnFuelExhausted;
    }

    @Override
    public void setAutoStopOnFuelExhausted(boolean enabled) {
        autoStopOnFuelExhausted = enabled;
    }

    @Override
    public int getMeltdownTimer() {
        return 0;
    }

    @Override
    public void setMeltdownTimer(int timer) {
    }

    @Override
    public boolean isMeltedDown() {
        return exploded;
    }

    @Override
    public void setMeltedDown(boolean melted) {
        setExploded(melted);
    }

    @Override
    public int getInCoreCount() {
        return rodsInCore;
    }

    @Override
    public void setInCoreCount(int count) {
        setRodsInCore(count);
    }

    @Override
    public int getFuelCount() {
        return 0;
    }

    @Override
    public void setFuelCount(int count) {
    }

    @Override
    public int getWasteCount() {
        return 0;
    }

    @Override
    public void setWasteCount(int count) {
    }

    @Override
    public ItemStack getFuelStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void setFuelStack(ItemStack stack) {
    }

    @Override
    public ItemStack getWasteStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void setWasteStack(ItemStack stack) {
    }

    /**
     * 结构体积；未成形/刚从存档恢复时 volume 可能是 0，这里按最小结构（9×9×5 = 405 方块）兜底，
     * 否则钠罐容量会退化成 1 mB，外部管道看起来就"灌不进去"。
     */
    public int blocks() {
        return Math.max(volume, MIN_SIZE * MIN_SIZE * MIN_HEIGHT);
    }

    private int tankCapacity() {
        return (int) (blocks() * TANK_MB_PER_BLOCK);
    }

    /** 每个燃料组件正好 1 根燃料棒：堆内装填数与烧掉的数都等于燃料组件数。 */
    public static final int RODS_PER_ASSEMBLY = 1;

    private double designPower() {
        return Math.max(0, rodsInCore) * MW_PER_ASSEMBLY / RODS_PER_ASSEMBLY;
    }

    @Override
    public boolean tick(Level world) {
        boolean needsPacket = super.tick(world);
        if (isFormed() && !isRemote()) {
            tickReactor(world);
            needsPacket = needsPacket || world.getGameTime() % 10L == 0L;
        }
        return needsPacket;
    }

    private void tickReactor(Level world) {
        ageTicks++;
        tickSound(world);
        loadFuel();
        if (rodsInCore <= 0) {
            setPower(0);
            setCoolantFlow(0);
            coolDown();
            return;
        }
        double x = controlRodInsertion / 100.0;
        double pXe = xenon > XENON_BASE ? Math.min(0.5, 0.10 * Math.floor((xenon - XENON_BASE) / 10.0)) : 0;
        double pTemp = coreTemp > 700 ? Math.min(0.5, 0.10 * Math.floor((coreTemp - 700) / 50.0)) : 0;
        double xEff = rodsMelted ? 0 : x * (1.0 - pXe) * (1.0 - pTemp);
        double fuelFactor = Math.max(0.75, 1.0 - 0.25 * (burnProgress / 100.0));
        double design = designPower();
        boolean running = rodsReleased && active && !exploded && controlRodInsertion < 100;
        double target = running ? design * (1.0 - xEff) * fuelFactor : 0.0;
        setPower(power + (target - power) / POWER_TAU_TICKS);

        double required = power * SODIUM_MB_PER_TICK_PER_MW;
        double capacity = blocks() * COOLING_MW_PER_BLOCK * SODIUM_MB_PER_TICK_PER_MW;
        long stored = sodiumTank.getStored();
        double flow = Math.max(0.0, Math.min(required, Math.min(capacity, stored)));
        if (flow > 0.0) {
            // 只按真正抽出来的钠算产量，抽不到就一滴都不要凭空生成
            long got = sodiumTank.extract((long) Math.ceil(flow), Action.EXECUTE, AutomationType.INTERNAL).getAmount();
            flow = Math.min(flow, got);
            if (flow > 0.0) {
                hotSodiumTank.insert(MekanismChemicals.SUPERHEATED_SODIUM.get().getStack((long) Math.floor(flow)),
                      Action.EXECUTE, AutomationType.INTERNAL);
            }
        } else {
            flow = 0.0;
        }
        setCoolantFlow((int) Math.floor(flow));
        setSodiumStored((int) Math.min(Integer.MAX_VALUE, sodiumTank.getStored()));
        setHotSodiumStored((int) Math.min(Integer.MAX_VALUE, hotSodiumTank.getStored()));

        if (rodsMelted) {
            setCoreTemperature(coreTemp + RUNAWAY_PER_TICK);
        } else {
            double ratio = flow > 0.001 ? Math.min(20.0, required / (flow * COOLANT_EFFECTIVENESS)) : 20.0;
            double targetTemp = 25.0 + MeknucFbrFuels.fullPowerRise(fuelType) * (power / Math.max(design, 0.001)) * ratio;
            setCoreTemperature(coreTemp + (targetTemp - coreTemp) * 0.05);
        }
        if (coreTemp >= MOLTEN_ROD_TEMP && !rodsMelted) {
            setRodsMelted(true);
        }
        if (coreTemp >= EXPLODE_TEMP && !exploded) {
            setExploded(true);
            setPower(0);
            setActive(false);
            meltdownExplosion(world);
        }
        if (power > 0.0 && !exploded) {
            setBurnProgress(burnProgress + (power / Math.max(design, 0.001))
                  / (MeknucFbrFuels.burnSeconds(fuelType) * 20.0) * 100.0);
            if (burnProgress >= 100.0) {
                finishBurnup();
            }
        }
        setUraniumStock(countUranium());
        tickXenon();
        tickBreeding(xEff);
    }

    /** 直接借用 Mekanism 裂变堆的运转音效，音量放大到 2.5。 */
    private void tickSound(Level world) {
        if (--soundTimer > 0) {
            return;
        }
        soundTimer = SOUND_INTERVAL;
        if (!active || !rodsReleased || rodsInCore <= 0 || exploded) {
            return;
        }
        if (reactorSound == null) {
            reactorSound = BuiltInRegistries.SOUND_EVENT.get(
                  ResourceLocation.fromNamespaceAndPath("mekanismgenerators", "tile.machine.fission_reactor"));
        }
        if (reactorSound != null) {
            world.playSound(null, soundOrigin, reactorSound, SoundSource.BLOCKS, SOUND_VOLUME, 1.0F);
        }
    }

    /** 达到爆炸温度：从结构中心炸开，并以中心 100 Sv 向外衰减生成辐射区。 */
    private void meltdownExplosion(Level world) {
        if (world.isClientSide) {
            return;
        }
        BlockPos min = getMinPos();
        BlockPos max = getMaxPos();
        double centerX = (min.getX() + max.getX() + 1) / 2.0;
        double centerY = (min.getY() + max.getY() + 1) / 2.0;
        double centerZ = (min.getZ() + max.getZ() + 1) / 2.0;
        Explosion explosion = new Explosion(world, null, centerX, centerY, centerZ, EXPLOSION_RADIUS, false,
              Explosion.BlockInteraction.DESTROY_WITH_DECAY);
        if (!EventHooks.onExplosionStart(world, explosion)) {
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
        radiate(world, centerX, centerY, centerZ);
    }

    private void radiate(Level world, double centerX, double centerY, double centerZ) {
        if (!IRadiationManager.INSTANCE.isRadiationEnabled()) {
            return;
        }
        int centerChunkX = Mth.floor(centerX) >> 4;
        int centerChunkZ = Mth.floor(centerZ) >> 4;
        int chunkRadius = RADIATION_RADIUS / 16 + 1;
        int sourceY = Mth.floor(centerY);
        for (int offsetX = -chunkRadius; offsetX <= chunkRadius; offsetX++) {
            for (int offsetZ = -chunkRadius; offsetZ <= chunkRadius; offsetZ++) {
                int sourceX = ((centerChunkX + offsetX) << 4) + 8;
                int sourceZ = ((centerChunkZ + offsetZ) << 4) + 8;
                double deltaX = sourceX - centerX;
                double deltaZ = sourceZ - centerZ;
                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                if (distance >= RADIATION_RADIUS) {
                    continue;
                }
                double magnitude = RADIATION_CENTER_SV * (1.0 - distance / RADIATION_RADIUS);
                if (magnitude > 0.0) {
                    IRadiationManager.INSTANCE.radiate(world, new BlockPos(sourceX, sourceY, sourceZ), magnitude);
                }
            }
        }
    }

    private void coolDown() {
        if (!rodsMelted && coreTemp > 25.0) {
            setCoreTemperature(coreTemp + (25.0 - coreTemp) * 0.01);
        } else if (rodsMelted) {
            setCoreTemperature(coreTemp + RUNAWAY_PER_TICK * 0.0);
        }
    }

    private void loadFuel() {
        int free = Math.max(0, fuelAssemblies * RODS_PER_ASSEMBLY - rodsInCore);
        if (free <= 0) {
            return;
        }
        ItemStack stack = fuelSlot.getStack();
        MeknucRodType type = MeknucFbrFuels.get(stack);
        if (type == null) {
            return;
        }
        if (rodsInCore == 0) {
            fuelType = type;
        }
        int moved = Math.min(free, stack.getCount());
        fuelSlot.shrinkStack(moved, Action.EXECUTE);
        setRodsInCore(rodsInCore + moved);
        setRodsDepleted(0);
        setActive(true);
    }

    private void finishBurnup() {
        int amount = rodsInCore;
        if (amount <= 0) {
            return;
        }
        ItemStack hot = MeknucItemFuelRodBase.hotStack(fuelType, 1);
        for (int i = 0; i < amount; i++) {
            hot.setCount(1);
            ItemStack rest = wasteSlot.insertItem(hot.copy(), Action.EXECUTE, AutomationType.INTERNAL);
            if (!rest.isEmpty()) {
                break;
            }
            setRodsDepleted(rodsDepleted + 1);
        }
        setRodsInCore(0);
        setBurnProgress(0.0);
        setPower(0.0);
        setXenon(0.0);
        xenonPhase = 0;
        ageTicks = 0;
        setActive(false);
    }

    private void tickXenon() {
        if (ageTicks < XENON_DELAY_TICKS && xenonPhase == 0) {
            setXenon(0.0);
            return;
        }
        if (xenonPhase == 0) {
            setXenon(XENON_BASE);
            xenonPhase = 1;
            return;
        }
        boolean stopping = controlRodInsertion >= 100 || !active;
        if (xenonPhase == 1 && stopping) {
            xenonPhase = 2;
        }
        switch (xenonPhase) {
            case 1 -> setXenon(Math.max(XENON_MIN, xenon - XENON_RUN_RATE));
            case 2 -> {
                setXenon(xenon + XENON_STOP_RATE);
                if (xenon >= XENON_MAX) {
                    xenonPhase = 3;
                }
            }
            case 3 -> {
                setXenon(xenon - XENON_DECAY_RATE);
                if (xenon <= 0.0) {
                    setXenon(0.0);
                    xenonPhase = 4;
                }
            }
            default -> setXenon(0.0);
        }
        if (xenonPhase == 4 && !stopping) {
            xenonPhase = 0;
            ageTicks = 0;
        }
    }

    private void tickBreeding(double xEff) {
        setBreedProgress(breedProgress + 1);
        if (breedProgress < BREED_BATCH_TICKS) {
            return;
        }
        setBreedProgress(0);
        if (exploded || claddingCount <= 0) {
            return;
        }
        double tempBonus = Math.min(0.8, Math.max(0.0, (coreTemp - 350.0) / 50.0 * 0.05));
        double powerFactor = Math.max(0.0, Math.min(1.0, 1.0 - xEff));
        int available = countUranium();
        setUraniumStock(available);
        double produced = Math.min(claddingCount * BREED_PER_CLADDING * powerFactor * (1.0 + tempBonus), available);
        int kg = (int) Math.floor(produced);
        if (kg <= 0) {
            return;
        }
        consumeUranium(kg);
        int output = kg;
        while (output > 0) {
            ItemStack dust = new ItemStack(MeknucItems.PLUTONIUM_239_DUST.get(), Math.min(output, 64));
            ItemStack rest = plutoniumSlot.insertItem(dust, Action.EXECUTE, AutomationType.INTERNAL);
            int accepted = dust.getCount() - rest.getCount();
            if (accepted <= 0) {
                break;
            }
            output -= accepted;
            setPlutoniumStock(plutoniumStock + accepted);
        }
    }

    private int countUranium() {
        ItemStack stack = breederSlot.getStack();
        return stack.isEmpty() ? 0 : stack.getCount();
    }

    private void consumeUranium(int kg) {
        int toRemove = Math.min(kg, countUranium());
        if (toRemove > 0) {
            breederSlot.shrinkStack(toRemove, Action.EXECUTE);
        }
        setUraniumStock(countUranium());
    }

    @Override
    public void readUpdateTag(CompoundTag tag, Provider provider) {
        super.readUpdateTag(tag, provider);
        NBTUtils.setBooleanIfPresent(tag, "fbrActive", value -> active = value);
        NBTUtils.setIntIfPresent(tag, "fbrInsertion", value -> controlRodInsertion = value);
        NBTUtils.setDoubleIfPresent(tag, "fbrTemp", value -> coreTemp = value);
        NBTUtils.setDoubleIfPresent(tag, "fbrPower", value -> power = value);
        NBTUtils.setDoubleIfPresent(tag, "fbrXenon", value -> xenon = value);
        NBTUtils.setDoubleIfPresent(tag, "fbrBurn", value -> burnProgress = value);
        NBTUtils.setIntIfPresent(tag, "fbrRods", value -> rodsInCore = value);
        NBTUtils.setIntIfPresent(tag, "fbrDepleted", value -> rodsDepleted = value);
        NBTUtils.setIntIfPresent(tag, "fbrUranium", value -> uraniumStock = value);
        NBTUtils.setIntIfPresent(tag, "fbrPlutonium", value -> plutoniumStock = value);
        NBTUtils.setIntIfPresent(tag, "fbrBreed", value -> breedProgress = value);
        NBTUtils.setIntIfPresent(tag, "fbrFlow", value -> coolantFlow = value);
        NBTUtils.setIntIfPresent(tag, "fbrSodium", value -> sodiumStored = value);
        NBTUtils.setIntIfPresent(tag, "fbrHotSodium", value -> hotSodiumStored = value);
        NBTUtils.setIntIfPresent(tag, "fbrCladding", value -> claddingCount = value);
        NBTUtils.setIntIfPresent(tag, "fbrVolume", value -> volume = value);
        NBTUtils.setBooleanIfPresent(tag, "fbrMelted", value -> rodsMelted = value);
        NBTUtils.setBooleanIfPresent(tag, "fbrExploded", value -> exploded = value);
        NBTUtils.setIntIfPresent(tag, "fbrAssemblies", value -> fuelAssemblies = value);
    }

    @Override
    public void writeUpdateTag(CompoundTag tag, Provider provider) {
        super.writeUpdateTag(tag, provider);
        tag.putBoolean("fbrActive", active);
        tag.putInt("fbrInsertion", controlRodInsertion);
        tag.putDouble("fbrTemp", coreTemp);
        tag.putDouble("fbrPower", power);
        tag.putDouble("fbrXenon", xenon);
        tag.putDouble("fbrBurn", burnProgress);
        tag.putInt("fbrRods", rodsInCore);
        tag.putInt("fbrDepleted", rodsDepleted);
        tag.putInt("fbrUranium", uraniumStock);
        tag.putInt("fbrPlutonium", plutoniumStock);
        tag.putInt("fbrBreed", breedProgress);
        tag.putInt("fbrFlow", coolantFlow);
        tag.putInt("fbrSodium", sodiumStored);
        tag.putInt("fbrHotSodium", hotSodiumStored);
        tag.putInt("fbrCladding", claddingCount);
        tag.putInt("fbrVolume", volume);
        tag.putBoolean("fbrMelted", rodsMelted);
        tag.putBoolean("fbrExploded", exploded);
        tag.putInt("fbrAssemblies", fuelAssemblies);
    }

    public List<IInventorySlot> getInventorySlots(MeknucFbrBlockTypes.Mode mode) {
        return switch (mode) {
            case FUEL_INPUT -> List.of(fuelSlot);
            case BREEDER_FUEL_OUTPUT -> List.of(wasteSlot);
            case BREEDER_MATERIAL_INPUT -> List.of(breederSlot);
            case BREEDER_MATERIAL_OUTPUT -> List.of(plutoniumSlot);
            default -> List.of();
        };
    }

    public List<IChemicalTank> getChemicalTanks(MeknucFbrBlockTypes.Mode mode) {
        return switch (mode) {
            case COOLANT_INPUT -> List.of(sodiumTank);
            case COOLANT_OUTPUT -> List.of(hotSodiumTank);
            default -> List.of();
        };
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        markDirty();
    }

    public int getControlRodInsertion() {
        return controlRodInsertion;
    }

    public void setControlRodInsertion(int insertion) {
        int clamped = Mth.clamp(insertion, 0, 100);
        if (clamped < 100) {
            rodsReleased = true;
        }
        if (controlRodInsertion != clamped) {
            controlRodInsertion = clamped;
            markDirty();
        }
    }

    public boolean areRodsReleased() {
        return rodsReleased;
    }

    public double getCoreTemperature() {
        return coreTemp;
    }

    public void setCoreTemperature(double value) {
        coreTemp = Mth.clamp(value, 0.0, 1600.0);
    }

    public double getPower() {
        return power;
    }

    public void setPower(double value) {
        power = Math.max(0.0, value);
    }

    public double getXenon() {
        return xenon;
    }

    public void setXenon(double value) {
        xenon = Mth.clamp(value, 0.0, 100.0);
    }

    public double getBurnProgress() {
        return burnProgress;
    }

    public void setBurnProgress(double value) {
        burnProgress = Mth.clamp(value, 0.0, 100.0);
    }

    public int getRodsInCore() {
        return rodsInCore;
    }

    public void setRodsInCore(int value) {
        rodsInCore = Math.max(0, value);
    }

    public int getRodsDepleted() {
        return rodsDepleted;
    }

    public void setRodsDepleted(int value) {
        rodsDepleted = Math.max(0, value);
    }

    public int getUraniumStock() {
        return uraniumStock;
    }

    public void setUraniumStock(int value) {
        uraniumStock = Math.max(0, value);
    }

    public int getPlutoniumStock() {
        return plutoniumStock;
    }

    public void setPlutoniumStock(int value) {
        plutoniumStock = Math.max(0, value);
    }

    public int getBreedProgress() {
        return breedProgress;
    }

    public void setBreedProgress(int value) {
        breedProgress = Math.max(0, value);
    }

    public int getCoolantFlow() {
        return coolantFlow;
    }

    public int getSodiumStored() {
        return sodiumStored;
    }

    public void setSodiumStored(int value) {
        sodiumStored = Math.max(0, value);
    }

    public int getHotSodiumStored() {
        return hotSodiumStored;
    }

    public void setHotSodiumStored(int value) {
        hotSodiumStored = Math.max(0, value);
    }

    public long getSodiumCapacity() {
        return (long) blocks() * (long) TANK_MB_PER_BLOCK;
    }

    public long getHotSodiumCapacity() {
        return getSodiumCapacity();
    }

    /** 本 tick 产出的过热钠（= 钠消耗量）。蒸汽/发电交给锅炉与涡轮机，反应堆只负责这一项。 */
    public int getCoolantProduction() {
        return coolantFlow;
    }

    public void setCoolantFlow(int value) {
        coolantFlow = Math.max(0, value);
    }

    public int getCladdingCount() {
        return claddingCount;
    }

    public void setCladdingCount(int value) {
        claddingCount = Math.max(0, value);
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int value) {
        volume = Math.max(1, value);
    }

    public boolean isRodsMelted() {
        return rodsMelted;
    }

    public void setRodsMelted(boolean value) {
        rodsMelted = value;
    }

    public boolean isExploded() {
        return exploded;
    }

    public void setExploded(boolean value) {
        exploded = value;
    }

    @Override
    public int getFuelAssemblies() {
        return fuelAssemblies;
    }

    public void setFuelAssemblies(int value) {
        fuelAssemblies = Math.max(0, value);
    }

    public int getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(int value) {
        surfaceArea = Math.max(0, value);
    }

    public MeknucRodType getFuelType() {
        return fuelType;
    }

    public void setFuelType(MeknucRodType type) {
        if (type != null) {
            fuelType = type;
        }
    }

    public double getXenonPenalty() {
        return xenon > XENON_BASE ? Math.min(0.5, 0.10 * Math.floor((xenon - XENON_BASE) / 10.0)) : 0.0;
    }

    public double getTemperaturePenalty() {
        return coreTemp > 700 ? Math.min(0.5, 0.10 * Math.floor((coreTemp - 700) / 50.0)) : 0.0;
    }

    public double getRodEffectiveness() {
        if (rodsMelted) {
            return 0.0;
        }
        return 1.0 - controlRodInsertion / 100.0 * (1.0 - getXenonPenalty()) * (1.0 - getTemperaturePenalty());
    }

    public ItemStack getWastePreview() {
        return wasteSlot.getStack();
    }

    private static class FbrSlot extends BasicInventorySlot {

        private FbrSlot(int limit, java.util.function.BiPredicate<ItemStack, AutomationType> canExtract,
              java.util.function.BiPredicate<ItemStack, AutomationType> canInsert,
              java.util.function.Predicate<ItemStack> validator, mekanism.api.IContentsListener listener) {
            super(limit, canExtract, canInsert, validator, listener, 0, 0);
            obeyStackLimit = false;
        }
    }
}
