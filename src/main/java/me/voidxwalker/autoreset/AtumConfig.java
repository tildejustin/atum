package me.voidxwalker.autoreset;

import me.voidxwalker.autoreset.interfaces.ISeedStringHolder;
import me.voidxwalker.autoreset.mixin.access.CreateWorldScreen$ModeAccessor;
import me.voidxwalker.autoreset.mixin.access.IntegratedServerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcsr.speedrunapi.config.SpeedrunConfigAPI;
import org.mcsr.speedrunapi.config.SpeedrunConfigContainer;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.annotations.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AtumConfig implements SpeedrunConfig {
    @Config.Ignored
    public final Path dataPackDirectory = SpeedrunConfigAPI.getConfigDir().resolve("atum").resolve("datapacks");
    public CreateWorldScreen.Mode gameMode = CreateWorldScreen.Mode.SURVIVAL;
    public boolean structures = true;
    @Config.Strings.MaxChars(32)
    public String seed = "";
    public boolean bonusChest = false;
    public boolean cheatsEnabled;
    public AtumGeneratorType generatorType = AtumGeneratorType.DEFAULT;
    public String generatorDetails = "";
    public boolean demoMode;
    @Config.Ignored
    public AttemptTracker attemptTracker = new AttemptTracker();
    @Config.Ignored
    private SpeedrunConfigContainer<?> container;
    @SuppressWarnings({"unused", "FieldCanBeLocal"}) // saved to config for PaceMan
    private boolean hasLegalSettings;

    {
        Atum.config = this;
    }

    public AtumConfig() throws IOException {
    }

    public boolean isSetSeed() {
        return !this.seed.isEmpty();
    }

    public void save() {
        try {
            this.container.save();
        } catch (IOException e) {
            Atum.LOGGER.warn("Failed to save Atum config.");
        }
    }

    public boolean updateHasLegalSettings() {
        return this.hasLegalSettings = (this.gameMode == CreateWorldScreen.Mode.SURVIVAL || this.gameMode == CreateWorldScreen.Mode.HARDCORE) &&
                this.structures &&
                !this.bonusChest &&
                !this.cheatsEnabled &&
                this.generatorType == AtumGeneratorType.DEFAULT &&
                !this.demoMode;
    }

    public Text getIllegalSettingsWarning() {
        List<Text> warnings = this.getIllegalSettingsTexts();
        if (warnings.isEmpty()) {
            return new TranslatableText("gui.none");
        }
        Text warning = warnings.remove(0);
        for (Text w : warnings) {
            warning.append(", ").append(w);
        }
        return warning;
    }

    private List<Text> getIllegalSettingsTexts() {
        List<Text> texts = new ArrayList<>();
        if (this.gameMode != CreateWorldScreen.Mode.SURVIVAL && this.gameMode != CreateWorldScreen.Mode.HARDCORE) {
            texts.add(new TranslatableText("selectWorld.gameMode").append(": ").append(new TranslatableText("selectWorld.gameMode." + ((CreateWorldScreen$ModeAccessor) (Object) this.gameMode).getTranslationSuffix())));
        }
        if (this.cheatsEnabled) {
            texts.add(new TranslatableText("selectWorld.allowCommands").append(" ").append(I18n.translate("options.on")));
        }
        if (!this.structures) {
            texts.add(new TranslatableText("selectWorld.mapFeatures").append(" ").append(I18n.translate("options.off")));
        }
        if (this.bonusChest) {
            texts.add(new TranslatableText("selectWorld.bonusItems").append(" ").append(I18n.translate("options.on")));
        }
        if (this.generatorType != AtumGeneratorType.DEFAULT) {
            texts.add(new TranslatableText("selectWorld.mapType").append(" ").append(I18n.translate(this.generatorType.get().getTranslationKey())));
        }
        if (this.demoMode) {
            texts.add(new TranslatableText("atum.config.demoMode", I18n.translate("options.on")));
        }
        return texts;
    }

    public void resetToLegalSettings() {
        if (this.gameMode != CreateWorldScreen.Mode.HARDCORE) {
            this.gameMode = CreateWorldScreen.Mode.SURVIVAL;
        }
        this.structures = true;
        this.bonusChest = false;
        this.cheatsEnabled = false;
        this.generatorType = AtumGeneratorType.DEFAULT;
        this.generatorDetails = "";
        if (Files.exists(this.dataPackDirectory)) {
            try {
                FileUtils.cleanDirectory(this.dataPackDirectory.toFile());
            } catch (IOException e) {
                Atum.LOGGER.error("Failed to clear datapack directory!", e);
            }
        }
        this.demoMode = false;
    }

    public List<String> getDebugText() {
        List<String> debugText = new ArrayList<>();

        debugText.add("");

        if (Atum.inDemoMode()) {
            debugText.add("Resetting the demo seed");
            return debugText;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isIntegratedServerRunning()) {
            String creationSeed = ((ISeedStringHolder) ((IntegratedServerAccessor) client.getServer()).getLevelInfo().getGeneratorOptions()).atum$getSeedString();
            if (!creationSeed.isEmpty()) {
                if (Atum.getSeedProvider().shouldShowSeed()) {
                    debugText.add("Resetting the seed \"" + creationSeed + "\"");
                } else {
                    debugText.add("Resetting a set seed");
                }
            } else {
                debugText.add("Resetting a random seed");
            }
        }

        for (Text text : this.getIllegalSettingsTexts()) {
            debugText.add(text.getString());
        }

        return debugText;
    }

    @Override
    public void finishInitialization(SpeedrunConfigContainer<?> container) {
        this.container = container;
    }

    @Override
    public void finishLoading() {
        this.updateHasLegalSettings();
    }

    @Override
    public String modID() {
        return "atum";
    }

    @Override
    public @NotNull Screen createConfigScreen(Screen parent) {
        // isAvailable() already takes care of this, but because it's so important we do another check just to be completely sure Atum is not running when the player opens the Atum config
        if (Atum.isRunning()) {
            throw new IllegalStateException("Cannot configure Atum while it's running.");
        }
        return new AtumCreateWorldScreen(parent);
    }

    @Override
    public boolean isAvailable() {
        return !Atum.isRunning();
    }

    @SuppressWarnings("unused")
    public enum AtumGeneratorType {
        DEFAULT(LevelGeneratorType.DEFAULT),
        FLAT(LevelGeneratorType.FLAT),
        LARGE_BIOMES(LevelGeneratorType.LARGE_BIOMES),
        AMPLIFIED(LevelGeneratorType.AMPLIFIED),
        CUSTOMIZED(LevelGeneratorType.CUSTOMIZED),
        BUFFET(LevelGeneratorType.BUFFET),
        DEBUG(LevelGeneratorType.DEBUG_ALL_BLOCK_STATES);

        private final LevelGeneratorType generatorType;

        AtumGeneratorType(LevelGeneratorType generatorType) {
            this.generatorType = generatorType;
        }

        public static @Nullable AtumGeneratorType from(LevelGeneratorType generatorType) {
            for (AtumGeneratorType atumGeneratorType : values()) {
                if (atumGeneratorType.get() == generatorType) {
                    return atumGeneratorType;
                }
            }
            return null;
        }

        public LevelGeneratorType get() {
            return this.generatorType;
        }
    }
}
