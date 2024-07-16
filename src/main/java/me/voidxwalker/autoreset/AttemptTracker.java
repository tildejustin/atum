package me.voidxwalker.autoreset;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AttemptTracker {
    private final Map<Type, Counter> counters = new HashMap<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    AttemptTracker() throws IOException {
        this.counters.put(Type.RSG, new Counter("rsg-attempts.txt"));
        this.counters.put(Type.SSG, new Counter("ssg-attempts.txt"));
    }

    public int get(Type type) {
        return this.counters.get(type).get();
    }

    public int increment(Type type) {
        Counter counter = this.counters.get(type);
        int result = counter.incrementAndGet();
        this.executor.execute(counter::save);
        return result;
    }

    public static class Counter {
        private final AtomicInteger counter = new AtomicInteger();
        private final File attemptsFile;

        private Counter(String fileName) throws IOException {
            this.attemptsFile = FabricLoader.getInstance().getGameDir().resolve("atum").resolve(fileName).toFile();
            Files.createDirectories(this.attemptsFile.getParentFile().toPath());
            if (!this.attemptsFile.createNewFile()) {
                this.read();
            }
            this.save();
        }

        private int get() {
            return this.counter.get();
        }

        private int incrementAndGet() {
            return this.counter.incrementAndGet();
        }

        private void read() {
            try {
                this.counter.set(Integer.parseInt(new String(Files.readAllBytes(this.attemptsFile.toPath()))));
            } catch (IOException e) {
                Atum.LOGGER.error("Failed to read attempts file: {}", this.attemptsFile.getName(), e);
            } catch (NumberFormatException e) {
                Atum.LOGGER.error("Failed to parse attempts file: {}", this.attemptsFile.getName(), e);
            }
        }

        private void save() {
            try {
                Files.write(this.attemptsFile.toPath(), String.valueOf(this.get()).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                Atum.LOGGER.error("Failed to save attempts file: {}", this.attemptsFile.getName(), e);
            }
        }
    }

    public enum Type {
        RSG,
        SSG
    }
}
