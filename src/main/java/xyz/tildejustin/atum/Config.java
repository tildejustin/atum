package xyz.tildejustin.atum;

import net.minecraft.world.level.LevelGeneratorType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.gson.Gson;
import org.spongepowered.include.com.google.gson.GsonBuilder;
import xyz.tildejustin.atum.exception.InvalidConfigException;

import java.io.*;


public class Config {
    public static final Config DEFAULT_CONFIG = new Config(
            "default",
            false,
            true,
            false,
            "",
            "survival",
            0,
            0
    );
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public String generatorType;
    public boolean hardcore;
    public boolean structures;
    public boolean bonusChest;
    public String seed;
    public String gameMode;
    public long randomSeedAttempts;
    public long setSeedAttempts;

    public Config(
            String generatorType,
            boolean hardcore,
            boolean structures,
            boolean bonusChest,
            @Nullable
            String seed,
            String gameMode,
            long randomSeedAttempts,
            long setSeedAttempts
    ) {
        this.generatorType = generatorType;
        this.hardcore = hardcore;
        this.structures = structures;
        this.bonusChest = bonusChest;
        this.seed = seed;
        // gameMode is a misnomer, it is either
        this.gameMode = gameMode;
        this.randomSeedAttempts = randomSeedAttempts;
        this.setSeedAttempts = setSeedAttempts;
    }


    public static Config readConfig() throws IOException {
        if (Atum.CONFIG_FILE.createNewFile()) {
            Config.writeConfig(Config.DEFAULT_CONFIG);
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(Atum.CONFIG_FILE));
        return Config.gson.fromJson(bufferedReader, Config.class);
    }

    public static void writeConfig(Config config) {
        try (Writer writer = new FileWriter(Atum.CONFIG_FILE)) {
            Config.gson.toJson(config, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LevelGeneratorType getGeneratorType() {
        LevelGeneratorType levelGeneratorType = LevelGeneratorType.getTypeFromName(this.generatorType);
        if (levelGeneratorType == null) {
            new InvalidConfigException("level generator is invalid").printStackTrace();
            levelGeneratorType = LevelGeneratorType.DEFAULT;
        }
        return levelGeneratorType;
    }
}
