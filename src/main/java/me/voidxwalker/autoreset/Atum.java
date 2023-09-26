package me.voidxwalker.autoreset;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.options.KeyBinding;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class Atum implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Atum.class);
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static KeyBinding resetKey;
    public static boolean running = false;
    public static boolean loading = false;
    public static boolean blocking = false;

    public static void log(Level level, String message) {
        Atum.LOGGER.log(level, message);
    }

    public static void tryCreateWorld() {
        if (loading) return;
        running = loading = true;
        blocking = false;
        client.openScreen(new CreateWorldScreen(null));
    }

    @Override
    public void onInitialize() {
        resetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Create New World",
                GLFW.GLFW_KEY_F6,
                "key.categories.atum"
        ));
    }
}
