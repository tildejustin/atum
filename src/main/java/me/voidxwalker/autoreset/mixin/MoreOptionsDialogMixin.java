package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.*;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.*;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.OptionalLong;

@Mixin(MoreOptionsDialog.class)
public abstract class MoreOptionsDialogMixin implements IMoreOptionsDialog {
    @Shadow
    abstract void apply(GeneratorOptionsHolder.Modifier modifier);

    @Shadow private GeneratorOptionsHolder generatorOptionsHolder;

    @Redirect(method = "getGeneratorOptionsHolder(Z)Lnet/minecraft/client/world/GeneratorOptionsHolder;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/GeneratorOptions;parseSeed(Ljava/lang/String;)Ljava/util/OptionalLong;", ordinal = 0))
    public OptionalLong setSeed(String string) {
        if (!Atum.isRunning) {
            return GeneratorOptions.parseSeed(string);
        }
        OptionalLong optionalLong = GeneratorOptions.parseSeed(Atum.seed);
        Atum.log(Level.INFO, "Resetting " + ((Atum.seed == null || Atum.seed.isEmpty() ? "a random seed" : "the set seed: " + "\"" + optionalLong.getAsLong() + "\"")));
        return optionalLong;
    }

    public void atum$setGeneratorType(WorldPreset preset) {
        this.apply(preset::createGeneratorOptions);
    }

    public void atum$setGenerateStructure(boolean generate) {
        this.apply(options -> new GeneratorOptions(options.getSeed(), generate, options.hasBonusChest(), options.getDimensions()));
    }

    public void atum$setGenerateBonusChest(boolean generate) {
        this.apply(options -> new GeneratorOptions(options.getSeed(), options.shouldGenerateStructures(), generate, options.getDimensions()));
    }
}
