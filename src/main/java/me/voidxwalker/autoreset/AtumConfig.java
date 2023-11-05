package me.voidxwalker.autoreset;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;
import org.apache.commons.lang3.StringUtils;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.annotations.Config;
import org.mcsr.speedrunapi.config.api.annotations.NoConfig;
import org.mcsr.speedrunapi.config.api.option.EnumTextProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;

public class AtumConfig implements SpeedrunConfig {
    public static AtumConfig instance;

    @Config.Name(value = "selectWorld.gameMode")
    public AtumGameMode gameMode = AtumGameMode.SURVIVAL;

    @Config.Name(value = "options.difficulty")
    public AtumDifficulty difficulty = AtumDifficulty.NORMAL;

    @Config.Name(value = "commands.seed.success")
    @Config.Strings.MaxChars(value = 32)
    public String seed = "";

    @Config.Name(value = "selectWorld.mapFeatures")
    public boolean structures = true;

    @Config.Name(value = "selectWorld.mapType")
    public AtumGeneratorType generatorType = AtumGeneratorType.DEFAULT;

    @Config.Name(value = "selectWorld.bonusItems")
    public boolean bonusChest = false;

    @Config.Name(value = "createWorld.customize.flat.title")
    public String superflatConfig = "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;minecraft:plains";

    @Config.Name(value = "createWorld.customize.custom.fixedBiome")
    public String biome = "plains";

    @NoConfig
    public AttemptTracker attemptTracker = new AttemptTracker();

    @NoConfig
    public static List<AtumGeneratorType> singleBiomeSourceBiomes = Arrays.asList(AtumGeneratorType.SINGLE_BIOME_SURFACE, AtumGeneratorType.SINGLE_BIOME_CAVES, AtumGeneratorType.SINGLE_BIOME_FLOATING_ISLANDS);

    {
        instance = this;
    }

    public AtumConfig() throws IOException {
    }

    public OptionalLong checkRandomSeed() {
        OptionalLong optionalLong;
        if (StringUtils.isEmpty(this.seed)) optionalLong = OptionalLong.empty();
        else {
            OptionalLong optionalLong2 = MoreOptionsDialog.tryParseLong(this.seed);
            if (optionalLong2.isPresent() && optionalLong2.getAsLong() != 0) optionalLong = optionalLong2;
            else optionalLong = OptionalLong.of(this.seed.hashCode());
        }
        return optionalLong;
    }

    @Override
    public String modID() {
        return "atum";
    }

    @Override
    public boolean isAvailable() {
        return MinecraftClient.getInstance().world == null;
    }

    @SuppressWarnings("unused")
    public enum AtumGeneratorType implements EnumTextProvider {
        DEFAULT(GeneratorType.DEFAULT),
        FLAT(GeneratorType.FLAT),
        LARGE_BIOMES(GeneratorType.LARGE_BIOMES),
        AMPLIFIED(GeneratorType.AMPLIFIED),
        SINGLE_BIOME_SURFACE(GeneratorType.SINGLE_BIOME_SURFACE),
        SINGLE_BIOME_CAVES(GeneratorType.SINGLE_BIOME_CAVES),
        SINGLE_BIOME_FLOATING_ISLANDS(GeneratorType.SINGLE_BIOME_FLOATING_ISLANDS),
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
