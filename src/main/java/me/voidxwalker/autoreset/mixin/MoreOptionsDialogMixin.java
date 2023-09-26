package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumConfig;
import me.voidxwalker.autoreset.IMoreOptionsDialog;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.gen.GeneratorOptions;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;
import java.util.OptionalLong;

@Mixin(MoreOptionsDialog.class)
public abstract class MoreOptionsDialogMixin implements IMoreOptionsDialog {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    private Optional<GeneratorType> field_25049;

    @Shadow
    private GeneratorOptions generatorOptions;

    @Shadow
    private RegistryTracker.Modifiable field_25483;

    @Shadow
    private static OptionalLong tryParseLong(String string) {
        return OptionalLong.empty();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyArg(method = "getGeneratorOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/GeneratorOptions;withHardcore(ZLjava/util/OptionalLong;)Lnet/minecraft/world/gen/GeneratorOptions;", ordinal = 0), index = 1)
    public OptionalLong setSeed(OptionalLong seed) {
        if (!Atum.running) {
            return seed;
        }
        OptionalLong newSeed = tryParseLong(AtumConfig.instance.seed);
        if (AtumConfig.instance.seed.isEmpty() || newSeed.orElse(1) == 0) newSeed = OptionalLong.empty();
        else newSeed = OptionalLong.of(newSeed.orElseGet(() -> AtumConfig.instance.seed.hashCode()));
        Atum.log(Level.INFO, !newSeed.isPresent() ? "Resetting a random seed" : String.format("Resetting the set seed: \"%d\"", newSeed.getAsLong()));
        return newSeed;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setGeneratorType(GeneratorType generatorType) {
        this.field_25049 = Optional.of(generatorType);
        this.generatorOptions = generatorType.method_29077(this.field_25483, this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest());
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setGenerateStructure(boolean generate) {
        if (generate != generatorOptions.shouldGenerateStructures()) {
            generatorOptions = generatorOptions.toggleGenerateStructures();
        }
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setGenerateBonusChest(boolean generate) {
        if (generate != generatorOptions.hasBonusChest()) {
            generatorOptions = generatorOptions.toggleBonusChest();
        }
    }
}
