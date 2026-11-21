package meknuc.reactor;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import meknuc.chemicals.MeknucChemicals;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

final class MeknucPwrSimulation {

    private final MeknucPwrMultiblockData data;
    private double burnProgress;
    private int batchBaseTicks;
    private double coolantCarry;
    private double lastFlow;

    MeknucPwrSimulation(MeknucPwrMultiblockData data) {
        this.data = data;
    }

    void tickCore() {
        int rods = data.getFuelAssemblies();
        double designPower = data.getDesignPower();
        tickFuelCycle(rods, designPower);
        double target = data.isActive() && !data.isMeltedDown() && data.getInCoreCount() > 0
              ? designPower * getFuelPowerFactor() * getRodPowerFactor() : 0.0;
        double powerLevel = data.getPowerLevel();
        data.setPowerLevel(powerLevel + (target - powerLevel)
              * Math.min(1.0, MeknucPwrMultiblockData.TICK_SECONDS / MeknucPwrMultiblockData.POWER_RAMP_SECONDS));

        double volume = Math.max(0, data.getVolume());
        double boilLimit = volume * MeknucPwrMultiblockData.BOIL_LIMIT_PER_BLOCK;
        double designFlow = designPower * MeknucPwrMultiblockData.MB_PER_MW;
        double maxCoolRate = Math.min(volume * MeknucPwrMultiblockData.COOLANT_CAPACITY_PER_BLOCK, designFlow);
        double baseFlow = Math.max(0.0,
              Math.min(designFlow, Math.min(boilLimit * MeknucPwrMultiblockData.MB_PER_MW, maxCoolRate)));

        double temperature = data.getTemperature();
        if (temperature > MeknucPwrMultiblockData.COOLANT_START_TEMPERATURE) {
            data.setCoolantOn(true);
        } else if (temperature < MeknucPwrMultiblockData.COOLANT_STOP_TEMPERATURE) {
            data.setCoolantOn(false);
        }
        if (!data.isCoolantOn() || data.isMeltedDown()) {
            baseFlow = 0.0;
        }

        double pressure = MeknucPwrMultiblockData.waterPressure(temperature);
        double requested = baseFlow * pressure * MeknucPwrMultiblockData.FLOW_MULTIPLIER_PER_MPA;
        long stored = data.coolantTank.getStored();
        long room = data.heatedCoolantTank.getCapacity() - data.heatedCoolantTank.getStored();
        double available = Math.max(0.0, Math.min(stored, room));
        coolantCarry = Math.min(coolantCarry + requested, requested + 1.0);
        long transfer = (long) Math.floor(Math.min(coolantCarry, available));
        if (transfer > 0) {
            data.coolantTank.extract(transfer, Action.EXECUTE, AutomationType.INTERNAL);
            data.heatedCoolantTank.insert(MeknucChemicals.PRESSURIZED_BORATED_LIGHT_WATER.asStack(transfer),
                  Action.EXECUTE, AutomationType.INTERNAL);
            coolantCarry -= transfer;
        }
        if (!data.isCoolantOn()) {
            coolantCarry = 0.0;
        }
        lastFlow = transfer;

        double efficiency = MeknucPwrMultiblockData.coolantEfficiency(temperature);
        double next = temperature;
        if (data.isRodsFailed()) {
            next += MeknucPwrMultiblockData.MELTDOWN_RAMP_PER_SECOND * MeknucPwrMultiblockData.TICK_SECONDS;
        } else {
            double conduction = MeknucPwrMultiblockData.REFERENCE_CONDUCTION
                  * (lastFlow / Math.max(designFlow, 1.0e-9)) * efficiency;
            double removalCap = MeknucPwrMultiblockData.COOLING_FACTOR * boilLimit * efficiency;
            double removed = Math.min(
                  Math.max(0.0, conduction * (next - MeknucPwrMultiblockData.AMBIENT_TEMPERATURE)), removalCap);
            next += (data.getPowerLevel() - removed) * MeknucPwrMultiblockData.TICK_SECONDS
                  / MeknucPwrMultiblockData.CORE_HEAT_CAPACITY;
        }
        if (next < MeknucPwrMultiblockData.AMBIENT_TEMPERATURE) {
            next = MeknucPwrMultiblockData.AMBIENT_TEMPERATURE;
        }
        if (next >= MeknucPwrConstants.ROD_FAILURE_TEMPERATURE) {
            data.setRodsFailed(true);
        }
        if (next >= MeknucPwrConstants.MELTDOWN_TEMPERATURE) {
            next = MeknucPwrConstants.MELTDOWN_TEMPERATURE;
            startMeltdown();
        }
        data.setTemperature(next);
    }

    private void tickFuelCycle(int rods, double designPower) {
        if (rods <= 0) {
            data.setInCoreCount(0);
            data.setBurnTime(0);
            data.setBatchBurnTime(0);
            burnProgress = 0.0;
            batchBaseTicks = 0;
            return;
        }
        if (data.getInCoreCount() <= 0) {
            if (!data.isActive() || data.isMeltedDown()) {
                data.setBurnTime(0);
                return;
            }
            startCycle(rods);
            if (data.getInCoreCount() <= 0) {
                return;
            }
        }
        if (data.isActive() && !data.isMeltedDown() && designPower > 0.0) {
            double fraction = Mth.clamp(data.getPowerLevel() / designPower, 0.0, 1.0);
            if (fraction > 0.0) {
                burnProgress = Math.min(1.0,
                      burnProgress + fraction * MeknucPwrMultiblockData.TICK_SECONDS / getBatchLifeSeconds());
                if (burnProgress >= 1.0) {
                    finishCycle();
                }
            }
        }
        refreshBurnTimeDisplay();
    }

    private void startCycle(int rods) {
        ItemStack fuel = data.findFuel();
        MeknucRodType type = MeknucPwrFuels.get(fuel);
        if (type == null || data.countFuel(fuel) < rods) {
            data.stopIfExhausted();
            return;
        }
        ItemStack product = fuel.copyWithCount(1);
        MeknucItemFuelRodBase.setRadiation(product, 0);
        MeknucItemFuelRodBase.setHot(product, true);
        if (!data.canStore(product, rods)) {
            data.stopIfExhausted();
            return;
        }
        data.consumeFuel(fuel, rods);
        data.setRunningProduct(product);
        data.setInCoreCount(rods);
        burnProgress = 0.0;
        batchBaseTicks = type.burnTime();
        refreshBurnTimeDisplay();
    }

    void refreshBurnTimeDisplay() {
        if (data.getInCoreCount() <= 0 || batchBaseTicks <= 0) {
            data.setBatchBurnTime(0);
            data.setBurnTime(0);
            return;
        }
        int total = (int) Math.round(batchBaseTicks * getBurnTimeFactor());
        data.setBatchBurnTime(total);
        data.setBurnTime((int) Math.round(total * (1.0 - burnProgress)));
    }

    private double getBatchLifeSeconds() {
        return Math.max(1.0, batchBaseTicks * getBurnTimeFactor() / 20.0);
    }

    private double getBurnTimeFactor() {
        return 1.0 + data.getControlRodInsertion() / 100.0;
    }

    double getFuelPowerFactor() {
        if (data.getInCoreCount() <= 0) {
            return 1.0;
        }
        return Math.max(MeknucPwrMultiblockData.MIN_FUEL_POWER_FACTOR, 1.0 - 0.5 * burnProgress);
    }

    double getRodPowerFactor() {
        return data.isRodsFailed() ? 1.0 : 1.0 - data.getControlRodInsertion() / 100.0;
    }

    private void finishCycle() {
        if (!data.getRunningProduct().isEmpty() && data.getInCoreCount() > 0) {
            data.storeWaste(data.getRunningProduct(), data.getInCoreCount());
        }
        data.setRunningProduct(ItemStack.EMPTY);
        data.setInCoreCount(0);
        burnProgress = 0.0;
        batchBaseTicks = 0;
        data.setBatchBurnTime(0);
        data.setBurnTime(0);
    }

    private void startMeltdown() {
        if (data.getMeltdownTimer() <= 0) {
            data.setMeltdownTimer(MeknucReactorConstants.MELTDOWN_TICKS);
        }
    }

    double getBurnProgress() {
        return burnProgress;
    }

    void setBurnProgress(double progress) {
        burnProgress = Mth.clamp(progress, 0.0, 1.0);
    }

    int getBatchBaseTicks() {
        return batchBaseTicks;
    }

    void setBatchBaseTicks(int ticks) {
        batchBaseTicks = Math.max(0, ticks);
    }
}
