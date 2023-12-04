package me.voidxwalker.autoreset.mixin.config;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.voidxwalker.autoreset.AttemptTracker;
import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumCreateWorldScreen;
import me.voidxwalker.autoreset.interfaces.IMoreOptionsDialog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    @Shadow
    @Final
    private Screen parent;

    @Shadow
    private Difficulty field_24290;
    @Shadow
    private Difficulty field_24289;
    @Shadow
    private CreateWorldScreen.Mode currentMode;
    @Shadow
    private boolean cheatsEnabled;
    @Shadow
    private boolean tweakedCheats;
    @Shadow
    private GameRules gameRules;
    @Shadow
    protected DataPackSettings field_25479;
    @Shadow
    private @Nullable Path field_25477;

    @Shadow
    @Final
    public MoreOptionsDialog moreOptionsDialog;
    @Shadow
    private TextFieldWidget levelNameField;
    @Shadow
    private ButtonWidget createLevelButton;
    @Shadow
    private ButtonWidget field_25478;

    @Shadow
    protected abstract void createLevel();

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;)V", at = @At("TAIL"))
    private void loadAtumConfigurations(CallbackInfo ci) {
        //noinspection ConstantValue
        if (!((Object) this instanceof AtumCreateWorldScreen)) {
            return;
        }

        this.currentMode = Atum.config.gameMode;
        this.field_24290 = this.field_24289 = Atum.config.difficulty;
        this.cheatsEnabled = Atum.config.cheatsEnabled;
        this.tweakedCheats = true;
        this.gameRules.setAllValues(Atum.config.gameRules, null);
        this.field_25479 = new DataPackSettings(Atum.config.dataPackSettings.getEnabled(), Atum.config.dataPackSettings.getDisabled());

        ((IMoreOptionsDialog) this.moreOptionsDialog).atum$loadAtumConfigurations();

        if (Atum.isRunning()) {
            if (Files.exists(Atum.config.dataPackDirectory) && Files.isDirectory(Atum.config.dataPackDirectory)) {
                this.field_25477 = CreateWorldScreen.method_29685(Atum.config.dataPackDirectory, this.client);
                if (this.field_25477 == null) {
                    Atum.config.dataPackMismatch = true;
                    Atum.log(Level.WARN, "Data pack mismatch, failed to copy data packs!");
                }
            } else if (!Atum.config.isDefaultDataPackSettings(this.field_25479)) {
                Atum.config.dataPackMismatch = true;
                Atum.log(Level.WARN, "Data pack mismatch, the Atum data pack directory is missing!");
            }
        } else {
            this.field_25477 = Atum.config.dataPackDirectory;
        }
    }

    @ModifyExpressionValue(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;", ordinal = 0), slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ScreenTexts;CANCEL:Lnet/minecraft/text/Text;")
    ))
    private AbstractButtonWidget captureCancelButton(AbstractButtonWidget button, @Share("cancelButton") LocalRef<ButtonWidget> cancelButton) {
        cancelButton.set((ButtonWidget) button);
        return button;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void modifyAtumCreateWorldScreen(CallbackInfo info, @Share("cancelButton") LocalRef<ButtonWidget> cancelButton) {
        //noinspection ConstantValue
        if (!((Object) this instanceof AtumCreateWorldScreen)) {
            return;
        }

        if (Atum.isRunning()) {
            if (Atum.config.isSetSeed()) {
                this.levelNameField.setText("Set Speedrun #" + Atum.config.attemptTracker.increment(AttemptTracker.Type.SSG));
                Atum.log(Level.INFO, String.format("Resetting the set seed: \"%s\"", Atum.config.seed));
            } else {
                this.levelNameField.setText("Random Speedrun #" + Atum.config.attemptTracker.increment(AttemptTracker.Type.RSG));
                Atum.log(Level.INFO, "Resetting a random seed");
            }
            this.createLevel();
        } else {
            this.levelNameField.setText("Atum Seed");
            this.levelNameField.setSelected(false);
            this.levelNameField.setEditable(false);
            this.levelNameField.setFocusUnlocked(false);
            this.levelNameField.active = false;

            this.field_25478.active = this.field_25477 != null;

            this.createLevelButton.setMessage(new TranslatableText("gui.done"));
            cancelButton.get().visible = false;
        }
    }

    @Inject(method = "createLevel", at = @At("HEAD"), cancellable = true)
    private void saveAtumConfigurations(CallbackInfo ci) {
        //noinspection ConstantValue
        if (!((Object) this instanceof AtumCreateWorldScreen) || Atum.isRunning()) {
            return;
        }

        Atum.config.gameMode = this.currentMode;
        Atum.config.difficulty = this.field_24289;
        Atum.config.cheatsEnabled = this.cheatsEnabled;
        Atum.config.setGameRules(this.gameRules.copy());
        Atum.config.setDataPackSettings(this.field_25479);

        ((IMoreOptionsDialog) this.moreOptionsDialog).atum$saveAtumConfigurations();

        Atum.config.save();

        this.client.openScreen(this.parent);

        ci.cancel();
    }

    @ModifyExpressionValue(method = "method_29685", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"))
    private static Stream<Path> filterAtumDataPacks(Stream<Path> dataPacks, Path directory, MinecraftClient minecraftClient) {
        if (directory.equals(Atum.config.dataPackDirectory)) {
            Set<String> expectedDataPacks = Atum.config.getExpectedDataPacks();

            dataPacks = dataPacks.filter(path -> {
                String dataPackName = path.toString().replace("\\", "/").replaceFirst(Atum.config.dataPackDirectory.toString().replace("\\", "/"), "file");
                return expectedDataPacks.remove(dataPackName);
            }).collect(Collectors.toList()).stream();

            if (!expectedDataPacks.isEmpty()) {
                Atum.config.dataPackMismatch = true;
                Atum.log(Level.WARN, "Data pack mismatch, some of the configured files are missing!");
            }
        }
        return dataPacks;
    }
}
