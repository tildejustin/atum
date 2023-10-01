package me.voidxwalker.autoreset;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AttemptTracker {
    private final AtomicInteger rsgAttempts = new AtomicInteger();
    private final AtomicInteger ssgAttempts = new AtomicInteger();
    private final Map<Type, AtomicInteger> counters = new HashMap<>();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final RandomAccessFile file;

    AttemptTracker() throws IOException {
        File attemptsFile = FabricLoader.getInstance().getGameDir().resolve("attempts.txt").toFile();
        boolean newFile = attemptsFile.createNewFile();
        file = new RandomAccessFile(attemptsFile, "rw");
        if (newFile) {
            file.writeInt(0);
            file.writeInt(0);
            file.seek(0);
        }
        rsgAttempts.set(file.readInt());
        ssgAttempts.set(file.readInt());
        counters.put(Type.RSG, rsgAttempts);
        counters.put(Type.SSG, ssgAttempts);
    }

    public int increment(Type type) {
        int result = counters.get(type).incrementAndGet();
        executor.execute(this::save);
        return result;
    }

    private synchronized void save() {
        try {
            file.setLength(0);
            file.seek(0);
            file.writeInt(rsgAttempts.get());
            file.writeInt(ssgAttempts.get());
        } catch (IOException ignored) {
        }
    }

    public enum Type {
        RSG,
        SSG
    }
}
