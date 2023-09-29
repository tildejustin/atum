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
    @Final
    public MoreOptionsDialog moreOptionsDialog;

    @Shadow
    private TextFieldWidget levelNameField;

    @Shadow
    private Difficulty field_24290;

    @Shadow
    private Difficulty field_24289;

    @Shadow
    protected abstract void createLevel();

    @Shadow
    protected abstract void tweakDefaultsTo(CreateWorldScreen.Mode mode);

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void createDesiredWorld(CallbackInfo info) {
        if (Atum.running) {
            field_24290 = field_24289 = AtumConfig.instance.difficulty.get();
            this.tweakDefaultsTo(AtumConfig.instance.gameMode.get());
            levelNameField.setText(
                    !AtumConfig.instance.checkRandomSeed().isPresent()
                            ? "Random Speedrun #" + AtumConfig.instance.attemptTracker.increment(AttemptTracker.Type.RSG)
                            : "Set Speedrun #" + AtumConfig.instance.attemptTracker.increment(AttemptTracker.Type.SSG)
            );
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGeneratorType(AtumConfig.instance.generatorType.get());
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGenerateStructure(AtumConfig.instance.structures);
            ((IMoreOptionsDialog) moreOptionsDialog).atum$setGenerateBonusChest(AtumConfig.instance.bonusChest);
            this.createLevel();
        }
    }
}
