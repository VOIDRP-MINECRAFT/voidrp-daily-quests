package ru.voidrp.dailyquests.quest;

import java.util.*;

/**
 * Weekly delivery quests requiring rare modded drops and deep progression.
 * All rewards 50k–200k coins.
 *
 * Modded item keys use namespaced format: "mod:item_name"
 */
public final class DeliveryQuestPool {

    private static final List<DeliveryQuestTemplate> POOL = new ArrayList<>();

    static {
        // ── Ice & Fire / драконы ──────────────────────────────────────────────

        add("del_dragon_hunter",  "Охотник на Драконов",
            "Принеси трофеи с убитых драконов обоих типов",
            100_000, 45_000,
            new RequiredItem("iceandfire:fire_dragon_scale",  12, "Чешуя Огненного Дракона"),
            new RequiredItem("iceandfire:ice_dragon_scale",   12, "Чешуя Ледяного Дракона"),
            new RequiredItem("iceandfire:dragon_bone",        32, "Кость Дракона"));

        add("del_dragon_heart",   "Сердце Дракона",
            "Добудь сердца из самых сильных драконов",
            130_000, 60_000,
            new RequiredItem("iceandfire:dragon_heart",        3, "Сердце Дракона"),
            new RequiredItem("iceandfire:fire_dragon_scale",   8, "Чешуя Огненного Дракона"),
            new RequiredItem("iceandfire:ice_dragon_scale",    8, "Чешуя Ледяного Дракона"));

        add("del_sea_horror",     "Ужас Морских Глубин",
            "Принеси доказательства победы над морскими чудовищами",
            95_000, 42_000,
            new RequiredItem("iceandfire:sea_serpent_scale",  24, "Чешуя Морской Змеи"),
            new RequiredItem("iceandfire:troll_tusk",          8, "Клык Тролля"),
            new RequiredItem("minecraft:trident",              2, "Трезубец"));

        add("del_myrmex_hive",    "Разорение Улья",
            "Уничтожь мирмексов и принеси трофеи",
            75_000, 33_000,
            new RequiredItem("iceandfire:myrmex_resin",       32, "Смола Мирмекса"),
            new RequiredItem("iceandfire:myrmex_chitin",      16, "Хитин Мирмекса"),
            new RequiredItem("iceandfire:dragon_bone",        16, "Кость Дракона"));

        add("del_troll_slayer",   "Истребитель Троллей",
            "Принеси трофеи с поверженных троллей",
            80_000, 36_000,
            new RequiredItem("iceandfire:troll_tusk",         16, "Клык Тролля"),
            new RequiredItem("iceandfire:troll_leather",      12, "Шкура Тролля"),
            new RequiredItem("iceandfire:fire_dragon_blood",   4, "Кровь Огненного Дракона"));

        // ── Twilight Forest ───────────────────────────────────────────────────

        add("del_twilight_smith", "Кузнец Сумерек",
            "Принеси редкие материалы из Сумеречного Леса",
            90_000, 40_000,
            new RequiredItem("twilightforest:fiery_ingot",    32, "Огненный Слиток"),
            new RequiredItem("twilightforest:knightmetal_ingot", 16, "Рыцарский Металл"),
            new RequiredItem("twilightforest:carminite",       8, "Карминит"));

        add("del_naga_lord",      "Завоеватель Нага",
            "Собери трофеи из Сумеречного Леса с разных боссов",
            85_000, 38_000,
            new RequiredItem("twilightforest:naga_scale",     32, "Чешуя Нага"),
            new RequiredItem("twilightforest:ironwood_ingot",  16, "Железный Слиток Сумерек"),
            new RequiredItem("twilightforest:steeleaf",        64, "Стальной Лист"));

        add("del_twilight_full",  "Покоритель Сумерек",
            "Принеси материалы из каждого уголка Сумеречного Леса",
            120_000, 55_000,
            new RequiredItem("twilightforest:fiery_ingot",    16, "Огненный Слиток"),
            new RequiredItem("twilightforest:knightmetal_ingot", 8, "Рыцарский Металл"),
            new RequiredItem("twilightforest:naga_scale",     16, "Чешуя Нага"),
            new RequiredItem("twilightforest:carminite",       4, "Карминит"),
            new RequiredItem("twilightforest:ironwood_ingot",  8, "Железный Слиток Сумерек"));

        // ── Draconic Evolution ────────────────────────────────────────────────

        add("del_draconium",      "Путь Дракона",
            "Принеси слитки дракониума — начало великого пути",
            80_000, 35_000,
            new RequiredItem("draconicevolution:draconium_ingot", 64, "Слиток Дракониума"),
            new RequiredItem("draconicevolution:dragon_heart",     2, "Сердце Дракона"),
            new RequiredItem("minecraft:nether_star",              1, "Звезда Нижнего Мира"));

        add("del_awakened",       "Пробуждённый Артефакт",
            "Только лучшие кузнецы могут выковать этот артефакт",
            160_000, 75_000,
            new RequiredItem("draconicevolution:awakened_draconium_ingot", 8, "Пробуждённый Дракониум"),
            new RequiredItem("draconicevolution:dragon_heart",              2, "Сердце Дракона"),
            new RequiredItem("minecraft:nether_star",                       2, "Звезда Нижнего Мира"));

        add("del_chaos",          "Сила Хаоса",
            "Принеси осколки хаоса — редчайшие артефакты в мире",
            200_000, 100_000,
            new RequiredItem("draconicevolution:chaos_shard",     1, "Осколок Хаоса"),
            new RequiredItem("draconicevolution:awakened_draconium_ingot", 4, "Пробуждённый Дракониум"),
            new RequiredItem("draconicevolution:dragon_heart",    1, "Сердце Дракона"));

        // ── MowziesMobs ───────────────────────────────────────────────────────

        add("del_mowzie_bosses",  "Легендарные Охотничьи Трофеи",
            "Принеси трофеи с самых опасных существ в мире",
            110_000, 50_000,
            new RequiredItem("mowziesmobs:barako_hair",       4, "Волосы Барако"),
            new RequiredItem("mowziesmobs:frostmaw_spike",    4, "Шип Фростмо"),
            new RequiredItem("mowziesmobs:naga_fang",         8, "Клык Наги"));

        // ── Alex's Mobs ───────────────────────────────────────────────────────

        add("del_void_conqueror", "Завоеватель Пустоты",
            "Принеси трофеи из самых тёмных уголков Энда",
            180_000, 85_000,
            new RequiredItem("alexsmobs:void_worm_plate",     4, "Пластина Пустотного Червя"),
            new RequiredItem("alexsmobs:enderiophage_eye",    3, "Глаз Пожирателя Энда"),
            new RequiredItem("minecraft:nether_star",          2, "Звезда Нижнего Мира"));

        // ── Мульти-мод мегаквесты ─────────────────────────────────────────────

        add("del_legend",         "Собиратель Легенд",
            "Докажи что покорил все измерения — принеси редчайшие трофеи из каждого",
            200_000, 100_000,
            new RequiredItem("iceandfire:dragon_heart",               1, "Сердце Дракона"),
            new RequiredItem("twilightforest:fiery_ingot",           16, "Огненный Слиток"),
            new RequiredItem("draconicevolution:awakened_draconium_ingot", 4, "Пробуждённый Дракониум"),
            new RequiredItem("minecraft:nether_star",                  2, "Звезда Нижнего Мира"),
            new RequiredItem("minecraft:elytra",                       1, "Элитры"));

        add("del_dimension_lord", "Владыка Измерений",
            "Принеси доказательства своей власти над каждым измерением",
            150_000, 70_000,
            new RequiredItem("iceandfire:fire_dragon_scale",         8, "Чешуя Огненного Дракона"),
            new RequiredItem("twilightforest:carminite",              8, "Карминит"),
            new RequiredItem("draconicevolution:draconium_ingot",    32, "Слиток Дракониума"),
            new RequiredItem("minecraft:ancient_debris",             16, "Древние Обломки"),
            new RequiredItem("minecraft:echo_shard",                 16, "Эхо-фрагмент"));

        add("del_nether_conq",    "Покоритель Нижнего Мира",
            "Зачисти Нижний Мир — принеси его богатства",
            95_000, 42_000,
            new RequiredItem("minecraft:nether_star",                 2, "Звезда Нижнего Мира"),
            new RequiredItem("minecraft:ancient_debris",             32, "Древние Обломки"),
            new RequiredItem("minecraft:wither_skeleton_skull",      20, "Череп Иссушителя Скелета"),
            new RequiredItem("minecraft:ghast_tear",                 16, "Слеза Гаста"));

        add("del_end_conq",       "Покоритель Конца",
            "Подчини измерение Конца своей воле",
            90_000, 40_000,
            new RequiredItem("minecraft:shulker_shell",              12, "Панцирь Шалкера"),
            new RequiredItem("minecraft:dragon_breath",              64, "Дыхание Дракона"),
            new RequiredItem("minecraft:end_crystal",                16, "Кристалл Конца"),
            new RequiredItem("minecraft:elytra",                      1, "Элитры"));
    }

    @SafeVarargs
    private static void add(String id, String name, String desc,
                             double money, int exp, RequiredItem... items) {
        POOL.add(new DeliveryQuestTemplate(id, name, desc, List.of(items), money, exp));
    }

    public static DeliveryQuestTemplate pickOne(long seed) {
        if (POOL.isEmpty()) return null;
        List<DeliveryQuestTemplate> shuffled = new ArrayList<>(POOL);
        Collections.shuffle(shuffled, new Random(seed));
        return shuffled.get(0);
    }
}
