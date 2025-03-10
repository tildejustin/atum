package me.voidxwalker.autoreset;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.*;
import net.minecraft.util.Language;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.util.*;

public class Atum implements ModInitializer {
    public static boolean isRunning = false;
    public static Logger LOGGER = LogManager.getLogger();

    public static String seed = "";
    public static int difficulty = 1;
    public static int generatorType = 0;
    public static int rsgAttempts;
    public static int ssgAttempts;
    public static boolean structures = true;
    public static boolean bonusChest = false;
    static Map<String, String> extraProperties = new LinkedHashMap<>();

    static File configFile;

    public static KeyBinding resetKey;
    public static boolean shouldReset = false;

    public static void scheduleReset() {
        shouldReset = true;
    }

    public static void createNewWorld() {
        isRunning = true;
        shouldReset = false;

        MinecraftClient.getInstance().openScreen(new CreateWorldScreen(null));
    }

    public static boolean isResetScheduled() {
        return shouldReset;
    }

    public static boolean shouldReset() {
        return isResetScheduled() && !isBlocking();
    }

    public static boolean isBlocking() {
        return MinecraftClient.getInstance().getOverlay() != null || isLoadingWorld();
    }

    public static boolean isLoadingWorld() {
        return MinecraftClient.getInstance().getServer() != null && MinecraftClient.getInstance().world == null;
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, message);
    }

    public static Text getTranslation(String path, String text) {
        if (Language.getInstance().get(path).equals(path)) {
            return new LiteralText(text);
        } else {
            return new TranslatableText(path);
        }
    }

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        resetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                getTranslation("key.atum.reset", "Create New World").getString(),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                getTranslation("key.categories.atum", "Atum").getString()
        ));
        new File("config").mkdir();
        new File("config/atum").mkdir();
        configFile = new File("config/atum/atum.properties");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                saveProperties();
            } catch (IOException e) {
                log(Level.ERROR, "Could not create config file:\n" + e.getMessage());
            }
            File difficultyFile = new File("ardifficulty.txt");
            if (difficultyFile.exists()) {
                String difInput = load(difficultyFile);
                difficulty = difInput == null ? 1 : Integer.parseInt(difInput.trim());
                if (difficulty > 4) {
                    difficulty = 1;
                }
                difficultyFile.delete();
            }
            File seedFile = new File("seed.txt");
            if (seedFile.exists()) {
                seed = load(seedFile);
                seed = seed == null ? "" : seed;
                seedFile.delete();
            }
            File attemptsFile = new File("attempts.txt");
            if (attemptsFile.exists()) {
                String s = load(attemptsFile);
                if (s != null) {
                    try {
                        rsgAttempts = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        rsgAttempts = 0;
                    }
                }
                attemptsFile.delete();
            }
        } else {
            loadFromProperties(getProperties(configFile));
        }
    }

    public static String load(File file) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNext()) {
                return scanner.nextLine();
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            log(Level.ERROR, "Could not load:\n" + e.getMessage());
            return null;
        }
    }

    static Properties getProperties(File configFile) {
        try (FileInputStream f = new FileInputStream(configFile)) {
            Properties properties = new Properties();
            properties.load(f);
            return properties;
        } catch (IOException e) {
            return null;
        }
    }

    public static void saveProperties() throws IOException {
        try (FileWriter f = new FileWriter(configFile)) {
            Properties properties = getProperties();
            properties.putAll(extraProperties);
            properties.store(f, "This is the config file for Atum.\nseed: leave empty for a random seed\ndifficulty: -1 = HARDCORE, 0 = PEACEFUL, 1 = EASY, 2= NORMAL, 3= HARD \ngeneratorType: 0 = DEFAULT, 1= FLAT, 2= LARGE_BIOMES, 3 = AMPLIFIED, 4 = SINGLE_BIOME_SURFACE, 5 = SINGLE_BIOME_CAVES, 6 =SINGLE_BIOME_FLOATING_ISLANDS");
        } catch (IOException e) {
            log(Level.WARN, "Could not save config file:\n" + e.getMessage());
        }
    }

    @NotNull
    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("rsgAttempts", String.valueOf(rsgAttempts));
        properties.put("ssgAttempts", String.valueOf(ssgAttempts));
        properties.put("seed", seed);
        properties.put("difficulty", String.valueOf(difficulty));
        properties.put("generatorType", String.valueOf(generatorType));
        properties.put("structures", String.valueOf(structures));
        properties.put("bonusChest", String.valueOf(bonusChest));
        return properties;
    }

    static void loadFromProperties(Properties properties) {
        if (properties != null) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                if (!entry.getKey().equals("seed") && !entry.getKey().equals("difficulty") && !entry.getKey().equals("generatorType") && !entry.getKey().equals("rsgAttempts") && !entry.getKey().equals("ssgAttempts") && !entry.getKey().equals("structures") && !entry.getKey().equals("bonusChest")) {
                    extraProperties.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
            seed = !properties.containsKey("seed") ? "" : properties.getProperty("seed");
            if (seed == null) {
                seed = "";
            }
            seed = seed.trim();
            try {
                difficulty = !properties.containsKey("difficulty") ? 1 : Integer.parseInt(properties.getProperty("difficulty"));
            } catch (NumberFormatException e) {
                difficulty = 1;
            }
            if (difficulty > 4) {
                difficulty = 1;
            }
            try {
                generatorType = !properties.containsKey("generatorType") ? 0 : Integer.parseInt(properties.getProperty("generatorType"));
            } catch (NumberFormatException e) {
                generatorType = 0;
            }
            if (generatorType > 6) {
                generatorType = 0;
            }
            try {
                rsgAttempts = !properties.containsKey("rsgAttempts") ? 0 : Integer.parseInt(properties.getProperty("rsgAttempts"));
            } catch (NumberFormatException e) {
                rsgAttempts = 0;
            }
            try {
                ssgAttempts = !properties.containsKey("ssgAttempts") ? 0 : Integer.parseInt(properties.getProperty("ssgAttempts"));
            } catch (NumberFormatException e) {
                ssgAttempts = 0;
            }
            structures = !properties.containsKey("structures") || Boolean.parseBoolean(properties.getProperty("structures"));
            bonusChest = Boolean.parseBoolean(properties.getProperty("bonusChest"));
        }
    }
}
