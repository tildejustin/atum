package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.AttemptTracker;
import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumConfig;
import me.voidxwalker.autoreset.IMoreOptionsDialog;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    private Difficulty field_24290;

    @Shadow
    protected abstract void createLevel();

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.running) {
            field_24290 = AtumConfig.instance.difficulty.get();
            if (AtumConfig.instance.gameMode.equals(AtumConfig.AtumGameMode.HARDCORE)) {
                field_24290 = Difficulty.HARD;
                hardcore = true;
            }
            levelNameField.setText(
                    AtumConfig.instance.seed.isEmpty() || MoreOptionsDialog.tryParseLong(AtumConfig.instance.seed).orElse(1) == 0
                            ? "Random Speedrun #" + AtumConfig.instance.attemptTracker.increment(AttemptTracker.Type.RSG)
                            : "Set Speedrun #" + AtumConfig.instance.attemptTracker.increment(AttemptTracker.Type.SSG)
            );
            ((IMoreOptionsDialog) moreOptionsDialog).setGeneratorType(AtumConfig.instance.generatorType.get());
            ((IMoreOptionsDialog) moreOptionsDialog).setGenerateStructure(AtumConfig.instance.structures);
            ((IMoreOptionsDialog) moreOptionsDialog).setGenerateBonusChest(AtumConfig.instance.bonusChest);
            createLevel();
        }
    }
}
