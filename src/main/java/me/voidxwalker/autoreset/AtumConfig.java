package me.voidxwalker.autoreset;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;
import org.mcsr.speedrunapi.config.api.EnumTextProvider;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.api.annotations.NoConfig;
import org.mcsr.speedrunapi.config.api.annotations.SpeedrunConfig;

import java.io.IOException;

@SpeedrunConfig(modID = "atum")
public class AtumConfig {
    public static AtumConfig instance;

    @Config.Name(value = "selectWorld.gameMode")
    public AtumGameMode gameMode = AtumGameMode.SURVIVAL;

    @Config.Name(value = "options.difficulty")
    public AtumDifficulty difficulty = AtumDifficulty.NORMAL;

    @Config.Name(value = "commands.seed.success")
    public String seed = "";

    @Config.Name(value = "selectWorld.mapFeatures")
    public boolean structures = true;

    @Config.Name(value = "selectWorld.mapType")
    public AtumGeneratorType generatorType = AtumGeneratorType.DEFAULT;

    @Config.Name(value = "selectWorld.bonusItems")
    public boolean bonusChest = false;

    @NoConfig
    public AttemptTracker attemptTracker = new AttemptTracker();

    {
        instance = this;
    }

    public AtumConfig() throws IOException {
    }

    @SuppressWarnings("unused")
    public enum AtumGeneratorType implements EnumTextProvider {
        DEFAULT(GeneratorType.DEFAULT),
        FLAT(GeneratorType.FLAT),
        LARGE_BIOMES(GeneratorType.LARGE_BIOMES),
        AMPLIFIED(GeneratorType.AMPLIFIED),
        DEBUG(GeneratorType.DEBUG_ALL_BLOCK_STATES);

        private final GeneratorType generatorType;

        AtumGeneratorType(GeneratorType generatorType) {
            this.generatorType = generatorType;
        }

        public GeneratorType get() {
            return this.generatorType;
        }

        @Override
        public Text toText() {
            return this.generatorType.getTranslationKey();
        }
    }

    @SuppressWarnings("unused")
    public enum AtumGameMode implements EnumTextProvider {
        SURVIVAL(CreateWorldScreen.Mode.SURVIVAL),
        HARDCORE(CreateWorldScreen.Mode.HARDCORE),
        CREATIVE(CreateWorldScreen.Mode.CREATIVE),
        SPECTATOR(CreateWorldScreen.Mode.DEBUG);

        private final CreateWorldScreen.Mode mode;

        AtumGameMode(CreateWorldScreen.Mode mode) {
            this.mode = mode;
        }

        public CreateWorldScreen.Mode get() {
            return this.mode;
        }

        @Override
        public Text toText() {
            return new TranslatableText("selectWorld.gameMode." + this.mode.translationSuffix);
        }
    }

    @SuppressWarnings("unused")
    public enum AtumDifficulty implements EnumTextProvider {
        PEACEFUL(Difficulty.PEACEFUL),
        EASY(Difficulty.EASY),
        NORMAL(Difficulty.NORMAL),
        HARD(Difficulty.HARD);

        private final Difficulty difficulty;

        AtumDifficulty(Difficulty difficulty) {
            this.difficulty = difficulty;
        }

        public Difficulty get() {
            return this.difficulty;
        }

        @Override
        public Text toText() {
            return this.difficulty.getTranslatableName();
        }
    }
}
