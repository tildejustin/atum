package me.voidxwalker.autoreset;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.options.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class Atum implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger(Atum.class);
    // the translation key is for backwards compatibility
    public static KeyBinding resetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Create New World",
            GLFW.GLFW_KEY_F6,
            "key.categories.atum"
    ));
    // a runnable that holds a reference to Atum::tryCreateWorld after the title screen is opened to be run in TitleScreen::tick (for the time being)
    // in order to fix the stack overflow bug present in previous versions of atum
    public static Runnable runnable;
    // if atum should be resetting
    public static boolean running = false;
    // true while a world is generating, if worldpreview is loaded this goes to false when the preview starts
    public static boolean loading = false;
    // true when the hotkey processing is blocked, such as during the splash screen and to stop double triggers because glfw + tick freeze causes issues
    public static boolean blocking = true;

    // to be run whenever a reset is requested, checks and updates state and, if appropriate, opens a CreateWorldScreen for further resetting logic
    public static void tryCreateWorld() {
        if (loading) return;
        running = loading = true;
        blocking = false;
        MinecraftClient.getInstance().openScreen(new CreateWorldScreen(null));
    }

    // need to use fabric initializer to load class early enough to register the keybind before GameOptions is initialized
    @Override
    public void onInitializeClient() {
    }
}
