package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.*;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.*;

@Mixin(MoreOptionsDialog.class)
public abstract class MoreOptionsDialogMixin implements IMoreOptionsDialog {
    @Shadow
    private GeneratorOptions generatorOptions;

    @Shadow
    private Optional<GeneratorType> generatorType;

    @Shadow
    private DynamicRegistryManager.Immutable registryManager;

    @ModifyArg(method = "getGeneratorOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/GeneratorOptions;withHardcore(ZLjava/util/OptionalLong;)Lnet/minecraft/world/gen/GeneratorOptions;", ordinal = 0), index = 1)
    public OptionalLong setSeed(OptionalLong originalSeed) {
        if (!Atum.isRunning) {
            return originalSeed;
        }
        OptionalLong optionalLong = GeneratorOptions.parseSeed(Atum.seed);
        Atum.log(Level.INFO, "Resetting " + ((Atum.seed == null || Atum.seed.isEmpty() ? "a random seed" : "the set seed: " + "\"" + optionalLong.getAsLong() + "\"")));
        return optionalLong;
    }

    public void atum$setGeneratorType(GeneratorType generatorType) {
        this.generatorType = Optional.of(generatorType);
        this.generatorOptions = generatorType.createDefaultOptions(this.registryManager, this.generatorOptions.getSeed(), this.generatorOptions.shouldGenerateStructures(), this.generatorOptions.hasBonusChest());
    }

    public void atum$setGenerateStructure(boolean generate) {
        if (generate != generatorOptions.shouldGenerateStructures()) {
            generatorOptions = generatorOptions.toggleGenerateStructures();
        }
    }

    public void atum$setGenerateBonusChest(boolean generate) {
        if (generate != generatorOptions.hasBonusChest()) {
            generatorOptions = generatorOptions.toggleBonusChest();
        }
    }
}
