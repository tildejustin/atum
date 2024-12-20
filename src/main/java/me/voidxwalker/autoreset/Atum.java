package me.voidxwalker.autoreset;

import me.voidxwalker.autoreset.api.seedprovider.AtumWaitingScreen;
import me.voidxwalker.autoreset.api.seedprovider.SeedProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.Optional;

public class Atum implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static AtumConfig config;
    private static final SeedProvider DEFAULT_SEED_PROVIDER = () -> Optional.of(Atum.config.seed);
    private static SeedProvider seedProvider = DEFAULT_SEED_PROVIDER;
    public static FabricKeyBinding resetKey;
    private static boolean running = false;
    private static boolean shouldReset;

    public static void createNewWorld() {
        running = true;
        MinecraftClient.getInstance().openScreen(new AtumCreateWorldScreen(null));
        shouldReset = false;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void stopRunning() {
        shouldReset = false;
        running = false;
    }

    public static void scheduleReset() {
        if (!(MinecraftClient.getInstance().currentScreen instanceof AtumWaitingScreen)) {
            shouldReset = true;
        }
    }

    public static boolean isResetScheduled() {
        return shouldReset;
    }

    public static boolean shouldReset() {
        return isResetScheduled() && !isBlocking();
    }

    public static boolean isBlocking() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.getOverlay() != null || isLoadingWorld() || client.currentScreen instanceof AtumWaitingScreen;
    }

    public static boolean isInWorld() {
        return MinecraftClient.getInstance().world != null;
    }

    public static boolean isLoadingWorld() {
        return MinecraftClient.getInstance().getServer() != null && MinecraftClient.getInstance().world == null;
    }

    public static boolean inDemoMode() {
        return isRunning() && config.demoMode;
    }

    /**
     * Returns true if the seed is set by Atum and no external seed provider is used.
     */
    public static boolean isSetSeed() {
        return Atum.seedProvider != DEFAULT_SEED_PROVIDER && (config.isSetSeed() || config.demoMode);
    }

    public static SeedProvider getSeedProvider() {
        return seedProvider;
    }

    @SuppressWarnings("unused")
    public static void setSeedProvider(SeedProvider seedProvider) {
        Atum.ensureState(Atum.seedProvider == DEFAULT_SEED_PROVIDER, "Seed provider has already been changed! It is likely that multiple mods are trying to set the seed provider!");
        Atum.ensureState(!Atum.isRunning(), "Seed provider set at an illegal time!");
        Atum.seedProvider = Objects.requireNonNull(seedProvider);
    }

    public static void ensureState(boolean condition, String exceptionMessage) throws IllegalStateException {
        if (!condition) throw new IllegalStateException(exceptionMessage);
    }

    @Override
    public void onInitializeClient() {
        KeyBindingRegistry.INSTANCE.addCategory("key.categories.atum");
        resetKey = FabricKeyBinding.Builder.create(
                new Identifier("atum", "create_new_world"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                "key.categories.atum"
        ).build();
        KeyBindingRegistry.INSTANCE.register(resetKey);
    }
}
