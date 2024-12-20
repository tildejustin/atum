package me.voidxwalker.autoreset.mixin.config;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import me.voidxwalker.autoreset.AttemptTracker;
import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.AtumConfig;
import me.voidxwalker.autoreset.AtumCreateWorldScreen;
import me.voidxwalker.autoreset.api.seedprovider.SeedProvider;
import me.voidxwalker.autoreset.interfaces.ISeedStringHolder;
import me.voidxwalker.autoreset.mixin.access.LevelGeneratorOptionsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {

    @Shadow
    @Final
    private Screen parent;

    @Shadow
    private CreateWorldScreen.Mode currentMode;
    @Shadow
    private boolean cheatsEnabled;
    @Shadow
    private boolean tweakedCheats;
    @Shadow
    private boolean moreOptionsOpen;
    @Shadow
    private TextFieldWidget levelNameField;
    @Shadow
    private ButtonWidget createLevelButton;

    @Unique
    private AbstractButtonWidget demoModeButton;

    @Shadow
    protected abstract void updateSaveFolderName();

    @Shadow
    protected abstract void createLevel();

    @Shadow
    private boolean structures;

    @Shadow
    private boolean bonusChest;

    @Shadow
    private int generatorType;

    @Shadow
    public LevelGeneratorOptions generatorOptions;

    @Shadow
    private TextFieldWidget seedField;

    @Shadow
    private String seed;

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void loadAtumConfigurations(CallbackInfo ci) throws CommandSyntaxException {
        if (!this.isAtum()) {
            return;
        }

        this.currentMode = Atum.config.gameMode;
        this.cheatsEnabled = Atum.config.cheatsEnabled;
        this.tweakedCheats = true;

        this.structures = Atum.config.structures;
        this.bonusChest = Atum.config.bonusChest;

        LevelGeneratorType generatorType = Atum.config.generatorType.get();
        this.generatorType = generatorType.getId();
        LevelGeneratorOptions defaultGeneratorOptions = generatorType.getDefaultOptions();
        this.generatorOptions = new LevelGeneratorOptions(
                defaultGeneratorOptions.getType(),
                defaultGeneratorOptions.getDynamic(),
                ((LevelGeneratorOptionsAccessor) defaultGeneratorOptions).getChunkGeneratorFactory()
        );

        if (Atum.config.generatorType == AtumConfig.AtumGeneratorType.DEFAULT) {
            return;
        }

        if (Atum.config.generatorDetails.isEmpty()) {
            return;
        }

        if (Atum.config.generatorType == AtumConfig.AtumGeneratorType.FLAT) {
            this.generatorOptions = LevelGeneratorOptions.createFlat(LevelGeneratorType.FLAT, new Dynamic<>(NbtOps.INSTANCE, StringNbtReader.parse(Atum.config.generatorDetails)));
        } else if (Atum.config.generatorType == AtumConfig.AtumGeneratorType.BUFFET) {
            this.generatorOptions = LevelGeneratorOptions.createBuffet(LevelGeneratorType.BUFFET, new Dynamic<>(NbtOps.INSTANCE, StringNbtReader.parse(Atum.config.generatorDetails)));
        }
    }

    @WrapWithCondition(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;", ordinal = 0), slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=gui.cancel")
    ))
    private boolean captureCancelButton(CreateWorldScreen screen, AbstractButtonWidget button) {
        return !this.isAtum();
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void modifyAtumCreateWorldScreen(CallbackInfo info) {
        if (!this.isAtum()) {
            return;
        }

        String seed;

        if (Atum.isRunning()) {
            SeedProvider seedProvider = Atum.getSeedProvider();
            Optional<String> seedOpt = seedProvider.getSeed();
            if (seedOpt.isPresent()) {
                seed = seedOpt.get();
            } else {
                if (MinecraftClient.getInstance().isOnThread()) {
                    assert client != null;
                    client.openScreen(Atum.getSeedProvider().getWaitingScreen());
                    return;
                } else {
                    // Note: If a mod ever makes AtumCreateWorldScreens in parallel, the next two lines would cause a race condition.
                    seedProvider.waitForSeed();
                    seedOpt = seedProvider.getSeed();
                    if (!seedOpt.isPresent()) {
                        throw new IllegalStateException("No seed found after waiting!");
                    }
                    seed = seedOpt.get();
                }
            }
        } else {
            seed = Atum.config.seed; // Set seed for config screen
        }

        this.seed = seed;
        this.seedField.setText(this.seed);
        if (Atum.isRunning()) {
            ((ISeedStringHolder) this.generatorOptions).atum$setSeedString(seed);
        }

        if (Atum.isRunning()) {
            if (Atum.inDemoMode()) {
                String demoWorldName = Atum.config.attemptTracker.incrementAndGetWorldName(AttemptTracker.Type.DEMO);
                Atum.LOGGER.info("Creating \"{}\" with demo seed...", demoWorldName);
                MinecraftClient.getInstance().startIntegratedServer(demoWorldName, "Demo_World", MinecraftServer.DEMO_LEVEL_INFO);
                return;
            }

            // micro optimization, vanilla calls the changed listener twice, once on setText and once on setCursorToEnd
            this.levelNameField.setChangedListener(string -> {
            });
            this.levelNameField.setText(seed.isEmpty() ? Atum.config.attemptTracker.incrementAndGetWorldName(AttemptTracker.Type.RSG) : Atum.config.attemptTracker.incrementAndGetWorldName(AttemptTracker.Type.SSG));
            this.updateSaveFolderName();

            if (!seed.isEmpty() && Atum.getSeedProvider().shouldShowSeed()) {
                Atum.LOGGER.info("Creating \"{}\" with seed \"{}\"...", this.levelNameField.getText(), seed);
            } else {
                Atum.LOGGER.info("Creating \"{}\"...", this.levelNameField.getText());
            }
            this.createLevel();
        } else {
            if (!this.seed.isEmpty()) {
                this.levelNameField.setText(Atum.config.attemptTracker.getWorldName(AttemptTracker.Type.SSG));
            } else {
                this.levelNameField.setText(Atum.config.attemptTracker.getWorldName(AttemptTracker.Type.RSG));
            }
            this.levelNameField.setSelected(false);
            this.levelNameField.setEditable(false);
            this.levelNameField.setFocusUnlocked(false);
            this.levelNameField.active = false;

            this.createLevelButton.setMessage(I18n.translate("gui.done"));
            //noinspection AssignmentUsedAsCondition
            this.demoModeButton = this.addButton(new ButtonWidget(
                    this.width / 2 + 5, 125, 150, 20,
                    I18n.translate("atum.config.demoMode", I18n.translate(Atum.config.demoMode ? "options.on" : "options.off")),
                    button -> button.setMessage(I18n.translate("atum.config.demoMode", I18n.translate((Atum.config.demoMode = !Atum.config.demoMode) ? "options.on" : "options.off")))
            ));
            this.demoModeButton.visible = this.moreOptionsOpen;
        }
    }

    @Inject(method = "setMoreOptionsOpen(Z)V", at = @At("TAIL"))
    private void updateLevelNameField(boolean moreOptionsOpen, CallbackInfo ci) {
        if (this.isAtum() && !Atum.isRunning()) {
            if (!this.seed.isEmpty()) {
                this.levelNameField.setText(Atum.config.attemptTracker.getWorldName(AttemptTracker.Type.SSG));
            } else {
                this.levelNameField.setText(Atum.config.attemptTracker.getWorldName(AttemptTracker.Type.RSG));
            }
            if (this.demoModeButton != null) {
                this.demoModeButton.visible = moreOptionsOpen;
            }
        }
    }

    @Inject(method = "createLevel", at = @At("HEAD"), cancellable = true)
    private void saveAtumConfigurations(CallbackInfo ci) {
        assert this.client != null;
        if (!this.isAtum() || Atum.isRunning()) {
            return;
        }

        Atum.config.gameMode = this.currentMode;
        Atum.config.cheatsEnabled = this.cheatsEnabled;

        Atum.config.seed = this.seed;
        Atum.config.generatorType = AtumConfig.AtumGeneratorType.from(LevelGeneratorType.TYPES[this.generatorType]);
        Atum.config.structures = this.structures;
        Atum.config.bonusChest = this.bonusChest;

        String generatorDetails = "";
        if (Atum.config.generatorType == AtumConfig.AtumGeneratorType.FLAT || Atum.config.generatorType == AtumConfig.AtumGeneratorType.BUFFET) {
            generatorDetails = ((CompoundTag) this.generatorOptions.getDynamic().getValue()).asString();
        }
        Atum.config.generatorDetails = generatorDetails;


        if (Atum.config.updateHasLegalSettings()) {
            Atum.config.save();
            this.client.openScreen(this.parent);
        } else {
            this.client.openScreen(new ConfirmScreen(confirm -> {
                if (!confirm) {
                    Atum.config.resetToLegalSettings();
                }
                Atum.config.save();
                this.client.openScreen(this.parent);
            }, new TranslatableText("atum.menu.legal_settings.warning"), Atum.config.getIllegalSettingsWarning(), I18n.translate("atum.menu.legal_settings.confirm"), I18n.translate("atum.menu.legal_settings.reset")));
        }

        ci.cancel();
    }

    @WrapWithCondition(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;updateSaveFolderName()V"
            )
    )
    private boolean doNotUpdateEmptySaveFolderName(CreateWorldScreen screen) {
        // micro-optimization, we call updateSaveFolderName ourselves when creating the level
        return !Atum.isRunning();
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;drawString(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", ordinal = 0), slice = @Slice(
            from = @At(value = "CONSTANT", args = "stringValue=selectWorld.resultFolder")
    ))
    private boolean doNotShowResultFolderOnConfigScreen(CreateWorldScreen screen, TextRenderer textRenderer, String string, int x, int y, int color) {
        return !this.isAtum();
    }

    @Unique
    private boolean isAtum() {
        return (Object) this instanceof AtumCreateWorldScreen;
    }
}
