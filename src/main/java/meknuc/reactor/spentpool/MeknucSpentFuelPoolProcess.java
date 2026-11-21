package meknuc.reactor.spentpool;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.registries.MekanismChemicals;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import meknuc.reactor.spentpool.MeknucSpentFuelPoolMultiblockData.RodThread;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

final class MeknucSpentFuelPoolProcess {

    private final MeknucSpentFuelPoolMultiblockData data;
    private final List<IInventorySlot> inputSlots;
    private final List<IInventorySlot> outputSlots;
    private final List<RodThread> threads = new ArrayList<>();
    private int dirtyCounter;
    private int soundCooldown;

    MeknucSpentFuelPoolProcess(MeknucSpentFuelPoolMultiblockData data, List<IInventorySlot> inputSlots,
          List<IInventorySlot> outputSlots) {
        this.data = data;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
    }

    List<RodThread> getThreads() {
        return threads;
    }

    int size() {
        return threads.size();
    }

    void tick(Level world, boolean paused, int cooled) {
        if (!paused) {
            startThreads(cooled);
        }
        if (threads.isEmpty()) {
            soundCooldown = 0;
            return;
        }
        int waterAvailable = data.waterTank.getFluidAmount();
        long steamRoom = data.steamTank.getCapacity() - data.steamTank.getStored();
        int runnable = paused ? 0 : Math.min(threads.size(), (int) Math.min(
              waterAvailable / MeknucSpentFuelPoolConstants.WATER_PER_THREAD_PER_TICK,
              steamRoom / MeknucSpentFuelPoolConstants.STEAM_PER_THREAD_PER_TICK));
        if (runnable > 0) {
            data.waterTank.extract(
                  runnable * MeknucSpentFuelPoolConstants.WATER_PER_THREAD_PER_TICK,
                  Action.EXECUTE, AutomationType.INTERNAL);
            data.steamTank.insert(MekanismChemicals.STEAM.asStack(
                  (long) runnable * MeknucSpentFuelPoolConstants.STEAM_PER_THREAD_PER_TICK),
                  Action.EXECUTE, AutomationType.INTERNAL);
        }
        boolean changed = false;
        for (int i = 0; i < runnable; i++) {
            RodThread thread = threads.get(i);
            if (thread.getProgress() < MeknucSpentFuelPoolConstants.COOLING_TICKS) {
                thread.progress++;
                changed = true;
            }
        }
        for (int i = threads.size() - 1; i >= 0; i--) {
            RodThread thread = threads.get(i);
            if (thread.getProgress() >= MeknucSpentFuelPoolConstants.COOLING_TICKS
                  && addCooledRod(thread.getType())) {
                threads.remove(i);
                changed = true;
            }
        }
        if (changed && ++dirtyCounter % 20 == 0) {
            data.markChanged();
        }
        tickSound(world);
    }

    private void tickSound(Level world) {
        if (world.isClientSide) {
            return;
        }
        if (threads.isEmpty()) {
            soundCooldown = 0;
            return;
        }
        if (soundCooldown > 0) {
            soundCooldown--;
            return;
        }
        soundCooldown = MeknucSpentFuelPoolConstants.MIN_CLICK_INTERVAL_TICKS
              + world.random.nextInt(MeknucSpentFuelPoolConstants.RANDOM_CLICK_EXTRA_TICKS + 1);
        BlockPos min = data.getMinPos();
        BlockPos max = data.getMaxPos();
        BlockPos center = new BlockPos((min.getX() + max.getX()) / 2,
              (min.getY() + max.getY()) / 2, (min.getZ() + max.getZ()) / 2);
        world.playSound(null, center, MeknucSpentFuelPoolSounds.SPENT_FUEL_POOL_GEIGER.get(),
              SoundSource.BLOCKS, 0.35F, 1.0F);
    }

    private void startThreads(int cooled) {
        if (cooled >= MeknucSpentFuelPoolConstants.MAX_COOLED_RODS) {
            return;
        }
        int maxThreads = data.getMaxThreads();
        while (threads.size() < maxThreads) {
            IInventorySlot source = selectNextInputSlot();
            if (source == null) {
                return;
            }
            MeknucRodType type = MeknucItemFuelRodBase.getRodType(source.getStack());
            source.extractItem(1, Action.EXECUTE, AutomationType.INTERNAL);
            threads.add(new RodThread(type, 0));
            data.markChanged();
        }
    }

    private IInventorySlot selectNextInputSlot() {
        Map<MeknucRodType, Integer> running = new EnumMap<>(MeknucRodType.class);
        for (RodThread thread : threads) {
            running.merge(thread.getType(), 1, Integer::sum);
        }
        IInventorySlot best = null;
        int bestRunning = Integer.MAX_VALUE;
        for (IInventorySlot slot : inputSlots) {
            if (slot.isEmpty() || !MeknucSpentFuelPoolMultiblockData.isAcceptedFuel(slot.getStack())) {
                continue;
            }
            int count = running.getOrDefault(MeknucItemFuelRodBase.getRodType(slot.getStack()), 0);
            if (count < bestRunning) {
                best = slot;
                bestRunning = count;
            }
        }
        return best;
    }

    private boolean addCooledRod(MeknucRodType type) {
        for (IInventorySlot slot : outputSlots) {
            if (slot.isEmpty()) {
                slot.setStack(MeknucItemFuelRodBase.depletedStack(type, 1));
                return true;
            }
        }
        return false;
    }

    List<RodThread> copyThreads() {
        List<RodThread> copy = new ArrayList<>(threads.size());
        for (RodThread thread : threads) {
            copy.add(new RodThread(thread.getType(), thread.getProgress()));
        }
        return copy;
    }

    void restoreThreads(List<RodThread> restored) {
        threads.clear();
        int max = data.getMaxThreads();
        for (RodThread thread : restored) {
            if (max > 0 && threads.size() >= max) {
                break;
            }
            threads.add(new RodThread(thread.getType(), thread.getProgress()));
        }
        data.markChanged();
    }
}
