package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Random;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    private boolean hardcore;

    @Shadow
    private TextFieldWidget levelNameField;

    @Shadow
    private int generatorType;

    @Shadow
    protected abstract void createLevel();

    @Shadow
    private boolean structures;

    @Shadow
    private boolean bonusChest;

    @Shadow public LevelGeneratorOptions generatorOptions;

    @Shadow protected abstract LevelGeneratorType getLevelGeneratorType();

    @Inject(method = "init", at = @At("TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.isRunning) {
            if (Atum.difficulty == -1) {
                hardcore = true;
            }
            createLevel();
        }
    }

    @Redirect(method = "createLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 0))
    private void doNotReopenTitleScreen(MinecraftClient instance, Screen screen) {
        if (!Atum.isRunning) {
            instance.openScreen(screen);
        }
    }

    @Redirect(method = "createLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getText()Ljava/lang/String;", ordinal = 0))
    private String injected(TextFieldWidget instance) throws IOException {
        if (!Atum.isRunning) {
            return instance.getText();
        }
        long l = (new Random()).nextLong();
        String string = Atum.seed == null ? "" : Atum.seed;
        if (!StringUtils.isEmpty(string)) {
            try {
                long m = Long.parseLong(string);
                if (m != 0L) {
                    l = m;
                }
            } catch (NumberFormatException var6) {
                l = string.hashCode();
            }
        }
        if (Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0")) {
            Atum.rsgAttempts++;
        } else {
            Atum.ssgAttempts++;
        }
        setGeneratorType(Atum.generatorType);
        setGenerateStructure(Atum.structures);
        setGenerateBonusChest(Atum.bonusChest);
        Atum.saveProperties();
        Atum.log(Level.INFO, (Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0") ? "Resetting a random seed" : "Resetting the set seed" + " \"" + l + "\""));
        levelNameField.setText((Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0")) ? "Random Speedrun #" + Atum.rsgAttempts : "Set Speedrun #" + Atum.ssgAttempts);
        return "" + l;
    }

    @Unique
    private void setGeneratorType(int generatorType) {
        this.generatorType = generatorType;
        this.generatorOptions = this.getLevelGeneratorType().getDefaultOptions();
    }

    @Unique
    private void setGenerateStructure(boolean generate) {
        this.structures = generate;
    }

    @Unique
    private void setGenerateBonusChest(boolean generate) {
        this.bonusChest = generate;
    }
}
