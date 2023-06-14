package xyz.tildejustin.atum;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Atum implements ClientModInitializer {
    public static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("atum.json").toFile();
    public static Config config;
    // between when a reset is started and the player is in the next world, this will be true
    // no resets must be allowed when this is true
    public static boolean loading = false;
    // this will be true as long as resetting is true
    public static boolean running = false;
    public static KeyBinding resetKey;

    public static void tryCreateWorld() {
        if (Atum.loading) {
            return;
        }
        Atum.loading = true;
        Atum.running = true;

        long seed = new Random().nextLong();
        String candidateSeed = Atum.config.seed;
        if (!MathHelper.isEmpty(candidateSeed)) {
            try {
                long newSeed = Long.parseLong(candidateSeed);
                if (newSeed != 0L) {
                    seed = newSeed;
                }
            } catch (NumberFormatException e) {
                seed = candidateSeed.hashCode();
            }
        }
        LevelInfo levelInfo = new LevelInfo(
                seed,
                // returns SURVIVAL when hardcore
                GameMode.setGameModeWithString(Atum.config.gameMode),
                Atum.config.structures,
                Atum.config.hardcore,
                Atum.config.getGeneratorType()
        );
        if (Atum.config.bonusChest && !levelInfo.isHardcore()) {
            levelInfo.setBonusChest();
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        String levelName;
        String saveDirectoryName = levelName = Atum.config.seed.isEmpty() ?
                "Random Speedrun #" + ++Atum.config.randomSeedAttempts :
                "Set Speedrun #" + ++Atum.config.setSeedAttempts;
        // fixes that bug contaria said was so dangerous I couldn't talk about it,
        // but the code was on a private repo, so I couldn't fix his fix that also crashed the game
        // gotta love atum :D
        // https://discord.com/channels/889854621491814491/889854621491814493/998920572396380201
        saveDirectoryName = CreateWorldScreen.checkDirectoryName(minecraft.getCurrentSave(), saveDirectoryName);
        Config.writeConfig(Atum.config);
        Minecraft.getMinecraft().method_2935(saveDirectoryName, levelName, levelInfo);
    }

    @Override
    public void onInitializeClient() {
        try {
            Atum.config = Config.readConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Atum.resetKey = new KeyBinding(
                "key.atum.reset",
                64
        );
    }
}
