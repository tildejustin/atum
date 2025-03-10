package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.*;
import net.minecraft.client.gui.screen.world.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.world.Difficulty;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    public boolean hardcore;

    @Shadow
    private TextFieldWidget levelNameField;

    @Shadow
    protected abstract void createLevel();

    @Shadow
    @Final
    public MoreOptionsDialog moreOptionsDialog;

    @Shadow
    private Difficulty difficulty;

    @Shadow
    private Difficulty safeDifficulty;

    @Inject(method = "init", at = @At("TAIL"))
    private void atum_createDesiredWorld(CallbackInfo info) throws IOException {
        if (Atum.isRunning) {
            Difficulty difficulty;
            switch (Atum.difficulty) {
                case 0:
                    difficulty = Difficulty.PEACEFUL;
                    break;
                case 1:
                    difficulty = Difficulty.EASY;
                    break;
                case 2:
                    difficulty = Difficulty.NORMAL;
                    break;
                case 3:
                    difficulty = Difficulty.HARD;
                    break;
                case -1:
                    difficulty = Difficulty.HARD;
                    hardcore = true;
                    break;
                default:
                    Atum.log(Level.WARN, "Invalid difficulty");
                    difficulty = Difficulty.EASY;
                    break;
            }
            this.difficulty = difficulty;
            this.safeDifficulty = difficulty;
            if (Atum.seed == null || Atum.seed.isEmpty()) {
                Atum.rsgAttempts++;
            } else {
                Atum.ssgAttempts++;
            }

            Atum.saveProperties();
            levelNameField.setText((Atum.seed == null || Atum.seed.isEmpty()) ? "Random Speedrun #" + Atum.rsgAttempts : "Set Speedrun #" + Atum.ssgAttempts);
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGeneratorType(GeneratorTypeAccessor.getVALUES().get(Atum.generatorType));
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGenerateStructure(Atum.structures);
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGenerateBonusChest(Atum.bonusChest);
            createLevel();
        }
    }
}
