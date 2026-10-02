package meknuc.mixin;

import java.util.function.Predicate;
import mekanism.api.chemical.ChemicalStack;
import mekanism.generators.common.content.turbine.TurbineChemicalTank;
import meknuc.chemicals.MeknucChemicals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = TurbineChemicalTank.class, remap = false)
public class MeknucTurbineChemicalTankMixin {

    @ModifyArg(method = "<init>",
          at = @At(value = "INVOKE",
                target = "Lmekanism/common/capabilities/chemical/VariableCapacityChemicalTank;<init>(Ljava/util/function/LongSupplier;Ljava/util/function/BiPredicate;Ljava/util/function/BiPredicate;Ljava/util/function/Predicate;Lmekanism/api/chemical/attribute/ChemicalAttributeValidator;Lmekanism/api/IContentsListener;)V",
                remap = false),
          index = 3, remap = false)
    private static Predicate<ChemicalStack> meknuc$acceptHighPressureSteam(Predicate<ChemicalStack> original) {
        return stack -> original.test(stack) || stack.is(MeknucChemicals.HIGH_PRESSURE_STEAM);
    }
}
