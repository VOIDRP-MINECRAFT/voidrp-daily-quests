package ru.voidrp.dailyquests.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.voidrp.dailyquests.quest.ActiveQuest;
import ru.voidrp.dailyquests.quest.HardQuestPool;
import ru.voidrp.dailyquests.quest.QuestTemplate;
import ru.voidrp.dailyquests.quest.QuestType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Stores one hard quest per player, resetting every 3 days.
 * Period is aligned to epochs divided by 3 so all players share the same
 * 3-day windows (resets simultaneously for everyone).
 */
public final class HardQuestStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int RESET_DAYS = 3;

    private final Path dataDir;
    private final Logger log;
    private final Map<UUID, PlayerQuestState> cache = new ConcurrentHashMap<>();

    public HardQuestStorage(Path dataDir, Logger log) {
        this.dataDir = dataDir;
        this.log     = log;
        try { Files.createDirectories(dataDir); } catch (IOException e) { log.warning("Cannot create hard quest dir: " + e.getMessage()); }
    }

    public PlayerQuestState get(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::loadOrCreate);
    }

    public void save(UUID uuid) {
        PlayerQuestState s = cache.get(uuid);
        if (s != null) persist(uuid, s);
    }

    public void saveAll() {
        cache.forEach(this::persist);
    }

    /** Returns true if a new quest was generated. */
    public boolean ensureCurrentPeriod(UUID uuid) {
        String periodKey = currentPeriodKey();
        PlayerQuestState state = get(uuid);
        if (periodKey.equals(state.lastResetDate)) return false;

        state.lastResetDate = periodKey;
        state.quests.clear();

        long seed = uuid.getLeastSignificantBits() ^ periodKey.hashCode();
        QuestTemplate t = HardQuestPool.pickOne(seed);
        if (t != null) state.quests.add(ActiveQuest.from(t));

        persist(uuid, state);
        return true;
    }

    public void evict(UUID uuid) {
        PlayerQuestState s = cache.remove(uuid);
        if (s != null) persist(uuid, s);
    }

    public void reload() {
        saveAll();
        cache.clear();
    }

    /** Days until next reset for display purposes. */
    public static int daysUntilReset() {
        long daysSinceEpoch = ChronoUnit.DAYS.between(LocalDate.EPOCH, LocalDate.now());
        long daysIntoWindow = daysSinceEpoch % RESET_DAYS;
        return (int)(RESET_DAYS - daysIntoWindow);
    }

    private static String currentPeriodKey() {
        long days = ChronoUnit.DAYS.between(LocalDate.EPOCH, LocalDate.now());
        return String.valueOf(days / RESET_DAYS);
    }

    // -------------------------------------------------------------------------

    private PlayerQuestState loadOrCreate(UUID uuid) {
        Path file = dataDir.resolve(uuid + ".json");
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                PlayerQuestState s = GSON.fromJson(r, PlayerQuestState.class);
                if (s != null) { fixEnums(s); return s; }
            } catch (Exception e) {
                log.warning("Cannot load hard quest for " + uuid + ": " + e.getMessage());
            }
        }
        return new PlayerQuestState();
    }

    private void persist(UUID uuid, PlayerQuestState state) {
        Path file = dataDir.resolve(uuid + ".json");
        try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            GSON.toJson(state, w);
        } catch (IOException e) {
            log.warning("Cannot save hard quest for " + uuid + ": " + e.getMessage());
        }
    }

    private void fixEnums(PlayerQuestState state) {
        if (state.quests == null) { state.quests = new ArrayList<>(); return; }
        for (ActiveQuest q : state.quests) {
            if (q.type != null) continue;
            if (q.templateId == null) continue;
            String id = q.templateId;
            if (id.startsWith("hard_collect_")) q.type = QuestType.COLLECT;
            else if (id.startsWith("hard_craft_")) q.type = QuestType.CRAFT;
            else q.type = QuestType.KILL;
        }
    }
}
