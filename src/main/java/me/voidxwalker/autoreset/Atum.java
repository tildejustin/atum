package me.voidxwalker.autoreset;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.Difficulty;

import java.io.*;
import java.util.Properties;

public class Atum {
    public static boolean running = false;
    public static Difficulty difficulty = Difficulty.PEACEFUL;
    public static boolean hardcore = false;
    public static int resets = 0;
    public static File config = FabricLoader.getInstance().getConfigDir().resolve("atum.properties").toFile();

    static {
        try {
            if (Atum.config.createNewFile()) Atum.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Atum.load();
    }

    public static void save() {
        Properties properties = new Properties();
        properties.put("difficulty", difficulty.getTranslationKey());
        properties.put("hardcore", Boolean.toString(hardcore));
        properties.put("resets", Integer.toString(resets));
        try {
            properties.store(new FileWriter(config), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(Atum.config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Atum.difficulty = Difficulty.getDifficulty(properties.getProperty("difficulty"));
        Atum.hardcore = Boolean.parseBoolean((String) properties.get("hardcore"));
        Atum.resets = Integer.parseInt((String) properties.get("resets"));
    }
}
