package meknuc.reactor;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.common.registries.MekanismChemicals;
import meknuc.items.MeknucItemFuelRodBase;
import meknuc.items.MeknucRodType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

final class MeknucBwrSimulation {

    private final MeknucBwrMultiblockData data;
    private double burnPartial;

    MeknucBwrSimulation(MeknucBwrMultiblockData data) {
        this.data = data;
    }

    void tickReaction() {
        int rods = data.getFuelAssemblies();
        tickFuelCycle(rods);
        if (rods <= 0) {
            data.setLastFissionPower(0.0);
            data.setLastVoidFraction(0.0);
            return;
        }
        double current = data.getTemperature();
        double ambient = data.getAmbientTemperature();
        double tempC = current - 273.15;
        double envC = ambient - 273.15;
        double capacity = rods * MeknucBwrConstants.HEAT_CAPACITY_PER_ASSEMBLY;
        double environment = rods * MeknucBwrMultiblockData.ENVIRONMENT_LOSS_PER_ASSEMBLY;
        double recirculation = rods * MeknucBwrMultiblockData.RECIRCULATION_PER_ASSEMBLY;
        double steamTarget = rods * MeknucBwrConstants.STEAM_PER_ASSEMBLY;
        double heatTarget = steamTarget * MeknucBwrConstants.HEAT_PER_MB;
        double exchange = heatTarget / (MeknucBwrConstants.DESIGN_TEMPERATURE
              - MeknucBwrConstants.SATURATION_TEMPERATURE) * data.getBoilEfficiency();
        double powerFactor = 1.0 - data.getControlRodInsertion() / 100.0;
        double temperatureFactor = Mth.clamp(
              1.0 - MeknucBwrMultiblockData.TEMPERATURE_FEEDBACK * (tempC - MeknucBwrConstants.DESIGN_TEMPERATURE),
              0.0, 1.0);
        double availablePower = rods * MeknucBwrMultiblockData.HEAT_PER_ASSEMBLY * powerFactor * temperatureFactor;
        double voidDenominator = MeknucBwrConstants.HEAT_PER_MB * recirculation;
        double fissionPower = data.isActive() && data.getBurnTime() > 0
              ? availablePower / (1.0 + MeknucBwrMultiblockData.VOID_FEEDBACK * MeknucBwrMultiblockData.VOID_GENERATION
                    * availablePower / voidDenominator)
              : 0.0;
        double voidFraction = Mth.clamp(
              MeknucBwrMultiblockData.VOID_GENERATION * fissionPower / voidDenominator, 0.0,
              MeknucBwrMultiblockData.MAX_VOID);
        data.setLastFissionPower(fissionPower);
        data.setLastVoidFraction(voidFraction);
        double dryoutFactor = voidFraction >= MeknucBwrMultiblockData.CRITICAL_VOID
              ? MeknucBwrMultiblockData.DRYOUT_HEAT_FACTOR : 1.0;

        double demand = exchange * Math.max(0.0, tempC - MeknucBwrConstants.SATURATION_TEMPERATURE) * dryoutFactor;
        double feedwaterLimit = steamTarget * MeknucBwrMultiblockData.FEEDWATER_MARGIN
              * MeknucBwrConstants.HEAT_PER_MB;
        double waterLimit = data.waterTank.getFluidAmount() * MeknucBwrConstants.HEAT_PER_MB;
        double heatTaken = Math.max(0.0, Math.min(Math.min(demand, feedwaterLimit), waterLimit));

        long boilStored = data.boilTank.getStored();
        double boilLimit = Math.max(0.0,
              (data.boilTank.getCapacity() - boilStored) + steamTarget * MeknucBwrMultiblockData.OUTPUT_MULTIPLIER);
        double boiled = Math.min(heatTaken / MeknucBwrConstants.HEAT_PER_MB, boilLimit);
        heatTaken = boiled * MeknucBwrConstants.HEAT_PER_MB;

        if (boiled > 0.0) {
            data.waterTank.extract((int) Math.ceil(boiled), Action.EXECUTE, AutomationType.INTERNAL);
            data.boilTank.insert(MekanismChemicals.STEAM.asStack((long) Math.floor(boiled)),
                  Action.EXECUTE, AutomationType.INTERNAL);
        }

        double output = Math.min(boilStored + boiled,
              steamTarget * MeknucBwrMultiblockData.OUTPUT_MULTIPLIER);
        if (output > 0.0) {
            long amount = (long) Math.floor(output);
            data.boilTank.extract(amount, Action.EXECUTE, AutomationType.INTERNAL);
            data.steamTank.insert(MekanismChemicals.STEAM.asStack(amount), Action.EXECUTE, AutomationType.INTERNAL);
        }
        data.setLastFlow(Math.floor(output));

        double environmentLoss = environment * Math.max(0.0, tempC - envC);
        data.setLastEnvironmentLoss(environmentLoss / capacity);

        double next = current + (fissionPower - heatTaken - environmentLoss) / capacity;
        double step = next - current;
        if (Math.abs(step) > MeknucBwrMultiblockData.MAX_TEMPERATURE_STEP) {
            next = current + Math.copySign(MeknucBwrMultiblockData.MAX_TEMPERATURE_STEP, step);
        }
        data.setTemperature(next);
    }

    private void tickFuelCycle(int rods) {
        if (rods <= 0) {
            data.setBurnTime(0);
            data.stopIfExhausted();
            return;
        }
        if (!data.isActive()) {
            return;
        }
        if (data.isBurnPaused()) {
            return;
        }
        if (data.getBurnTime() > 0) {
            burnPartial += 1.0 / data.getBurnTimeMultiplier();
            int steps = (int) burnPartial;
            if (steps > 0) {
                burnPartial -= steps;
                data.setBurnTime(data.getBurnTime() - steps);
                if (data.getBurnTime() <= 0) {
                    finishCycle();
                }
            }
            return;
        }
        ItemStack fuel = data.findFuel();
        MeknucRodType type = MeknucBwrFuels.get(fuel);
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
        data.setBatchBurnTime(type.burnTime());
        data.setBurnTime(type.burnTime());
        burnPartial = 0.0;
    }

    private void finishCycle() {
        if (!data.getRunningProduct().isEmpty() && data.getInCoreCount() > 0) {
            data.storeWaste(data.getRunningProduct(), data.getInCoreCount());
        }
        data.setRunningProduct(ItemStack.EMPTY);
        data.setInCoreCount(0);
        data.setBatchBurnTime(0);
    }

    void handleDamage(Level world) {
        if (data.isMeltedDown()) {
            return;
        }
        double tempC = data.getTemperature() - 273.15;
        double damage = data.getDamage();
        if (tempC > MeknucBwrMultiblockData.DAMAGE_TEMPERATURE) {
            data.setDamage(damage + (tempC - MeknucBwrMultiblockData.DAMAGE_TEMPERATURE)
                  / MeknucBwrMultiblockData.DAMAGE_DIVISOR * 100.0 * MeknucBwrMultiblockData.DAMAGE_FACTOR);
        }
        if (damage >= MeknucBwrMultiblockData.MELTDOWN_THRESHOLD) {
            data.createMeltdown(world);
        }
    }
}
