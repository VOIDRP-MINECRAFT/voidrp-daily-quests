package ru.voidrp.dailyquests.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.voidrp.dailyquests.quest.ActiveQuest;
import ru.voidrp.dailyquests.quest.QuestPool;
import ru.voidrp.dailyquests.quest.QuestTemplate;
import ru.voidrp.dailyquests.quest.QuestType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class QuestStorage {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .enableComplexMapKeySerialization()
        .create();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Path dataDir;
    private final int questsPerDay;
    private final Logger log;
    private final Map<UUID, PlayerQuestState> cache = new ConcurrentHashMap<>();

    public QuestStorage(Path dataDir, int questsPerDay, Logger log) {
        this.dataDir     = dataDir;
        this.questsPerDay = questsPerDay;
        this.log         = log;
        try { Files.createDirectories(dataDir); } catch (IOException e) { log.warning("Could not create data dir: " + e.getMessage()); }
    }

    public PlayerQuestState get(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::loadOrCreate);
    }

    public void save(UUID uuid) {
        PlayerQuestState state = cache.get(uuid);
        if (state != null) persist(uuid, state);
    }

    public void saveAll() {
        cache.forEach(this::persist);
    }

    /** Ensures player has quests for today, generating if needed. Returns true if new quests were generated. */
    public boolean ensureToday(UUID uuid) {
        String today = LocalDate.now().format(DATE_FMT);
        PlayerQuestState state = get(uuid);
        if (today.equals(state.lastResetDate)) return false;

        state.lastResetDate = today;
        state.quests.clear();

        long seed = uuid.getLeastSignificantBits() ^ today.hashCode();
        List<QuestTemplate> templates = QuestPool.pickRandom(questsPerDay, seed);
        for (QuestTemplate t : templates) {
            ActiveQuest q = ActiveQuest.from(t);
            q.moneyReward = Math.round(q.moneyReward); // no fractional coins
            state.quests.add(q);
        }
        persist(uuid, state);
        return true;
    }

    public void evict(UUID uuid) {
        PlayerQuestState state = cache.remove(uuid);
        if (state != null) persist(uuid, state);
    }

    public void reload() {
        saveAll();
        cache.clear();
    }

    // -------------------------------------------------------------------------

    private PlayerQuestState loadOrCreate(UUID uuid) {
        Path file = dataDir.resolve(uuid + ".json");
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                PlayerQuestState state = GSON.fromJson(r, PlayerQuestState.class);
                if (state != null) {
                    // Deserializer doesn't restore enum from string automatically via raw Gson
                    fixEnums(state);
                    return state;
                }
            } catch (Exception e) {
                log.warning("Could not load quests for " + uuid + ": " + e.getMessage());
            }
        }
        return new PlayerQuestState();
    }

    private void persist(UUID uuid, PlayerQuestState state) {
        Path file = dataDir.resolve(uuid + ".json");
        try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            GSON.toJson(state, w);
        } catch (IOException e) {
            log.warning("Could not save quests for " + uuid + ": " + e.getMessage());
        }
    }

    private void fixEnums(PlayerQuestState state) {
        if (state.quests == null) { state.quests = new ArrayList<>(); return; }
        for (ActiveQuest q : state.quests) {
            if (q.type == null && q.templateId != null) {
                // Re-derive type from template ID prefix
                String id = q.templateId;
                if (id.startsWith("kill_"))   q.type = QuestType.KILL;
                else if (id.startsWith("collect_")) q.type = QuestType.COLLECT;
                else if (id.startsWith("mine_"))    q.type = QuestType.MINE;
                else if (id.startsWith("fish_"))    q.type = QuestType.FISH;
                else if (id.startsWith("breed_"))   q.type = QuestType.BREED;
            }
        }
    }
}
