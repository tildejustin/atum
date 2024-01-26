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
    @Final
    public MoreOptionsDialog moreOptionsDialog;

    @Shadow
    private TextFieldWidget levelNameField;

    @Shadow
    private Difficulty currentDifficulty;

    @Shadow
    protected abstract void createLevel();

    @Inject(method = "init", at = @At("TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.isRunning) {
            Difficulty difficulty;
            switch (Atum.difficulty) {
                case 0 -> difficulty = Difficulty.PEACEFUL;
                case 1 -> difficulty = Difficulty.EASY;
                case 2 -> difficulty = Difficulty.NORMAL;
                case 3 -> difficulty = Difficulty.HARD;
                case -1 -> {
                    difficulty = Difficulty.HARD;
                    hardcore = true;
                }
                default -> {
                    Atum.log(Level.WARN, "Invalid difficulty");
                    difficulty = Difficulty.EASY;
                }
            }
            if (Atum.seed == null || Atum.seed.isEmpty()) {
                Atum.rsgAttempts++;
            } else {
                Atum.ssgAttempts++;
            }
            try {
                Atum.saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentDifficulty = difficulty;
            levelNameField.setText((Atum.seed == null || Atum.seed.isEmpty()) ? "Random Speedrun #" + Atum.rsgAttempts : "Set Speedrun #" + Atum.ssgAttempts);
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGeneratorType(GeneratorTypeAccessor.getVALUES().get(Atum.generatorType));
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGenerateStructure(Atum.structures);
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGenerateBonusChest(Atum.bonusChest);
            createLevel();
        }
    }
}
