package meknuc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mekanism.api.chemical.IChemicalTank;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import meknuc.chemicals.MeknucChemicals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TurbineMultiblockData.class, remap = false)
public abstract class MeknucTurbineMultiblockDataMixin {

    @Shadow
    public IChemicalTank chemicalTank;

    @ModifyExpressionValue(method = "tick(Lnet/minecraft/world/level/Level;)Z",
          at = @At(value = "INVOKE", target = "Lmekanism/common/config/value/CachedLongValue;get()J", remap = false),
          remap = false, expect = 1)
    private long meknuc$doubleHighPressureSteamEnergy(long original) {
        return chemicalTank.getStack().is(MeknucChemicals.HIGH_PRESSURE_STEAM) ? original * 2L : original;
    }
}
