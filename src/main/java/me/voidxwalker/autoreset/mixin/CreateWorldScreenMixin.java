package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    public CompoundTag generatorOptionsTag;

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

    @Shadow
    private boolean field_3178;

    @Shadow
    private TextFieldWidget seedField;

    @Inject(method = "init", at = @At("TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.isRunning) {
            if (Atum.difficulty == -1) {
                this.field_3178 = true;
            }
            this.seedField.setText(Atum.seed);

            setGeneratorType(Atum.generatorType);
            setGenerateStructure(Atum.structures);
            setGenerateBonusChest(Atum.bonusChest);
            try {
                Atum.saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Atum.log(Level.INFO, (Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0") ? "Resetting a random seed" : "Resetting the set seed" + " \"" + Atum.seed + "\""));
            levelNameField.setText((Atum.seed == null || Atum.seed.isEmpty() || Atum.seed.trim().equals("0")) ? "Random Speedrun #" + Atum.rsgAttempts : "Set Speedrun #" + Atum.ssgAttempts);
            createLevel();
        }
    }

    @Redirect(method = "createLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 0))
    private void doNotReopenTitleScreen(MinecraftClient instance, Screen screen) {
        if (!Atum.isRunning) {
            instance.openScreen(screen);
        }
    }

    @Unique
    private void setGeneratorType(int generatorType) {
        this.generatorOptionsTag = new CompoundTag();
        this.generatorType = generatorType;
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
