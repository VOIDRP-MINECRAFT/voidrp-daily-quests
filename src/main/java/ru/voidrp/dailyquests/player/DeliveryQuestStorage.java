package ru.voidrp.dailyquests.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.voidrp.dailyquests.quest.DeliveryActiveQuest;
import ru.voidrp.dailyquests.quest.DeliveryQuestPool;
import ru.voidrp.dailyquests.quest.DeliveryQuestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class DeliveryQuestStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int RESET_DAYS = 7; // weekly

    private static final class State {
        String periodKey = "";
        DeliveryActiveQuest quest;
    }

    private final Path dataDir;
    private final Logger log;
    private final Map<UUID, State> cache = new ConcurrentHashMap<>();

    public DeliveryQuestStorage(Path dataDir, Logger log) {
        this.dataDir = dataDir;
        this.log     = log;
        try { Files.createDirectories(dataDir); } catch (IOException e) { log.warning("Cannot create delivery dir: " + e.getMessage()); }
    }

    public DeliveryActiveQuest get(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::loadOrCreate).quest;
    }

    /** Returns true if a new quest was generated. */
    public boolean ensureCurrentPeriod(UUID uuid) {
        String period = currentPeriodKey();
        State state = cache.computeIfAbsent(uuid, this::loadOrCreate);
        if (period.equals(state.periodKey)) return false;

        state.periodKey = period;
        long seed = uuid.getLeastSignificantBits() ^ period.hashCode();
        DeliveryQuestTemplate t = DeliveryQuestPool.pickOne(seed);
        state.quest = t != null ? DeliveryActiveQuest.from(t) : null;

        persist(uuid, state);
        return true;
    }

    public void save(UUID uuid) {
        State s = cache.get(uuid);
        if (s != null) persist(uuid, s);
    }

    public void saveAll() { cache.forEach(this::persist); }

    public void evict(UUID uuid) {
        State s = cache.remove(uuid);
        if (s != null) persist(uuid, s);
    }

    public void reload() {
        saveAll();
        cache.clear();
    }

    public static int daysUntilReset() {
        long days = ChronoUnit.DAYS.between(LocalDate.EPOCH, LocalDate.now());
        return (int)(RESET_DAYS - (days % RESET_DAYS));
    }

    private static String currentPeriodKey() {
        long days = ChronoUnit.DAYS.between(LocalDate.EPOCH, LocalDate.now());
        return String.valueOf(days / RESET_DAYS);
    }

    private State loadOrCreate(UUID uuid) {
        Path file = dataDir.resolve(uuid + ".json");
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                State s = GSON.fromJson(r, State.class);
                if (s != null) return s;
            } catch (Exception e) {
                log.warning("Cannot load delivery quest for " + uuid + ": " + e.getMessage());
            }
        }
        return new State();
    }

    private void persist(UUID uuid, State state) {
        try (Writer w = Files.newBufferedWriter(dataDir.resolve(uuid + ".json"), StandardCharsets.UTF_8)) {
            GSON.toJson(state, w);
        } catch (IOException e) {
            log.warning("Cannot save delivery quest for " + uuid + ": " + e.getMessage());
        }
    }
}
