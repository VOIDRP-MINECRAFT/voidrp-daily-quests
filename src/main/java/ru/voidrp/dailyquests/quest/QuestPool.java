package ru.voidrp.dailyquests.quest;

import java.util.*;

public final class QuestPool {

    private static final List<QuestTemplate> POOL = new ArrayList<>();

    static {
        // ── Vanilla kill ──
        add("kill_zombie",         "Охотник на зомби",        "Убей {n} зомби",                QuestType.KILL,    "minecraft:zombie",            10, 300, 100);
        add("kill_skeleton",       "Снайпер скелетов",        "Убей {n} скелетов",             QuestType.KILL,    "minecraft:skeleton",           8, 350, 120);
        add("kill_creeper",        "Взрывная охота",          "Убей {n} криперов",             QuestType.KILL,    "minecraft:creeper",            5, 400, 150);
        add("kill_spider",         "Охота на пауков",         "Убей {n} пауков",               QuestType.KILL,    "minecraft:spider",            12, 250,  80);
        add("kill_cave_spider",    "Пещерный охотник",        "Убей {n} пещерных пауков",      QuestType.KILL,    "minecraft:cave_spider",       15, 250,  80);
        add("kill_drowned",        "Морской охотник",         "Убей {n} утопленников",         QuestType.KILL,    "minecraft:drowned",            8, 350, 120);
        add("kill_witch",          "Ведьмин котёл",           "Убей {n} ведьм",                QuestType.KILL,    "minecraft:witch",              4, 500, 200);
        add("kill_blaze",          "Адский охотник",          "Убей {n} блейзов",              QuestType.KILL,    "minecraft:blaze",              6, 600, 250);
        add("kill_enderman",       "Охота в Эндере",          "Убей {n} эндерменов",           QuestType.KILL,    "minecraft:enderman",           8, 450, 180);
        add("kill_phantom",        "Страж ночи",              "Убей {n} фантомов",             QuestType.KILL,    "minecraft:phantom",            5, 400, 160);
        add("kill_guardian",       "Страж подземелий",        "Убей {n} стражей",              QuestType.KILL,    "minecraft:guardian",           5, 500, 200);
        add("kill_wither_skeleton","Завоеватель крепости",    "Убей {n} иссушителей скелетов", QuestType.KILL,    "minecraft:wither_skeleton",    5, 600, 250);
        add("kill_piglin",         "Пиглинский налёт",        "Убей {n} пиглинов",             QuestType.KILL,    "minecraft:piglin",            10, 300, 100);
        add("kill_piglin_brute",   "Элитный пиглин",          "Убей {n} пиглинов-бруте",       QuestType.KILL,    "minecraft:piglin_brute",       4, 600, 250);
        add("kill_zombified_piglin","Гробовщик",              "Убей {n} зомбированных пиглинов",QuestType.KILL,    "minecraft:zombified_piglin",  10, 300, 100);
        add("kill_husk",           "Пустынный страж",         "Убей {n} хасков",               QuestType.KILL,    "minecraft:husk",              10, 280,  90);
        add("kill_stray",          "Ледяной лучник",          "Убей {n} замёрзших скелетов",   QuestType.KILL,    "minecraft:stray",             10, 280,  90);
        add("kill_pillager",       "Налётчик",                "Убей {n} разбойников",          QuestType.KILL,    "minecraft:pillager",           8, 400, 150);
        add("kill_vindicator",     "Карательный отряд",       "Убей {n} карателей",            QuestType.KILL,    "minecraft:vindicator",         6, 500, 200);
        add("kill_ravager",        "Силач деревни",           "Убей {n} опустошителей",        QuestType.KILL,    "minecraft:ravager",            2, 800, 350);
        add("kill_evoker",         "Мастер теней",            "Убей {n} заклинателей",         QuestType.KILL,    "minecraft:evoker",             3, 700, 300);
        add("kill_ghast",          "Нетерный призрак",        "Убей {n} гастов",               QuestType.KILL,    "minecraft:ghast",              5, 500, 200);
        add("kill_magma_cube",     "Лавовый прыгун",          "Убей {n} магмовых кубов",       QuestType.KILL,    "minecraft:magma_cube",         8, 400, 150);
        add("kill_hoglin",         "Хоглин гриль",            "Убей {n} хоглинов",             QuestType.KILL,    "minecraft:hoglin",             5, 500, 200);
        add("kill_zoglin",         "Зоглин на охоте",         "Убей {n} зоглинов",             QuestType.KILL,    "minecraft:zoglin",             5, 550, 220);
        add("kill_shulker",        "Охота на шалкеров",       "Убей {n} шалкеров",             QuestType.KILL,    "minecraft:shulker",            4, 600, 250);
        add("kill_slime",          "Слизистый день",          "Убей {n} слаймов",              QuestType.KILL,    "minecraft:slime",              8, 350, 120);

        // ── Alex's Mobs kill ──
        add("kill_raccoon",        "Охота на енотов",         "Убей {n} енотов",               QuestType.KILL,    "alexsmobs:raccoon",            8, 350, 120);
        add("kill_crocodile",      "Опасные воды",            "Убей {n} крокодилов",           QuestType.KILL,    "alexsmobs:crocodile",          4, 550, 220);
        add("kill_rattlesnake",    "Укус гремучей змеи",      "Убей {n} гремучих змей",        QuestType.KILL,    "alexsmobs:rattlesnake",        5, 500, 200);
        add("kill_shark",          "Морской хищник",          "Убей {n} акул",                 QuestType.KILL,    "alexsmobs:hammerhead_shark",   3, 700, 300);
        add("kill_komodo",         "Комодский дракон",        "Убей {n} комодских драконов",   QuestType.KILL,    "alexsmobs:komodo_dragon",      3, 700, 300);
        add("kill_soul_vulture",   "Адский гриф",             "Убей {n} душевных стервятников",QuestType.KILL,    "alexsmobs:soul_vulture",       5, 500, 200);
        add("kill_warped_toad",    "Болотная охота",          "Убей {n} жаб искажения",        QuestType.KILL,    "alexsmobs:warped_toad",        5, 450, 180);
        add("kill_mimicube",       "Ловушка-куб",             "Убей {n} мимикубов",            QuestType.KILL,    "alexsmobs:mimicube",           4, 600, 250);
        add("kill_skelewag",       "Скелет-пёс",              "Убей {n} скелет-псов",          QuestType.KILL,    "alexsmobs:skelewag",           5, 500, 200);
        add("kill_cockroach",      "Тараканья охота",         "Убей {n} тараканов",            QuestType.KILL,    "alexsmobs:cockroach",         20, 200,  60);

        // ── Ice & Fire kill ──
        add("kill_troll",          "Охотник на троллей",      "Убей {n} троллей",              QuestType.KILL,    "iceandfire:troll",             3, 700, 300);
        add("kill_stymphalian",    "Пернатый враг",           "Убей {n} стимфалийских птиц",   QuestType.KILL,    "iceandfire:stymphalian_bird",  5, 500, 200);
        add("kill_pixie",          "Охота на пикси",          "Убей {n} пикси",                QuestType.KILL,    "iceandfire:pixie",             8, 400, 150);
        add("kill_sea_serpent",    "Морской ужас",            "Убей {n} морских змей",         QuestType.KILL,    "iceandfire:seaserpent",        2, 900, 400);
        add("kill_hippogryph",     "Охота на гиппогрифов",    "Убей {n} гиппогрифов",          QuestType.KILL,    "iceandfire:hippogryph",        3, 700, 300);
        add("kill_cyclops",        "Глаз Циклопа",            "Убей {n} циклопов",             QuestType.KILL,    "iceandfire:cyclops",           2, 900, 400);
        add("kill_myrmex",         "Муравейник",              "Убей {n} мирмексов",            QuestType.KILL,    "iceandfire:myrmex_worker",    10, 400, 150);

        // ── MowziesMobs kill ──
        add("kill_foliaath",       "Листовой кошмар",         "Убей {n} фолиатов",             QuestType.KILL,    "mowziesmobs:foliaath",         4, 600, 250);
        add("kill_grottol",        "Тёмная пещера",           "Убей {n} гроттолов",            QuestType.KILL,    "mowziesmobs:grottol",          6, 500, 200);
        add("kill_wroughtnaut",    "Кованый воин",            "Убей {n} кованых рыцарей",      QuestType.KILL,    "mowziesmobs:ferrous_wroughtnaut",2,1000, 450);
        add("kill_naga",           "Танец наги",              "Убей {n} наг",                  QuestType.KILL,    "mowziesmobs:naga",             3, 700, 300);

        // ── Friends & Foes kill ──
        add("kill_crab",           "Клешни в бою",            "Убей {n} крабов",               QuestType.KILL,    "friendsandfoes:crab",          6, 450, 180);
        add("kill_iceologer",      "Ледяной колдун",          "Убей {n} айсолоджеров",         QuestType.KILL,    "friendsandfoes:iceologer",     3, 700, 300);
        add("kill_illusioner",     "Обманщик иллюзий",        "Убей {n} иллюзионеров",         QuestType.KILL,    "friendsandfoes:illusioner",    3, 700, 300);
        add("kill_mauler",         "Пустынный мялец",         "Убей {n} мяльцев",              QuestType.KILL,    "friendsandfoes:mauler",        5, 500, 200);

        // ── Collect ──
        add("collect_iron",        "Железный рудокоп",        "Собери {n} железных слитков",   QuestType.COLLECT, "IRON_INGOT",                  16, 400, 150);
        add("collect_gold",        "Золотая лихорадка",       "Собери {n} золотых слитков",    QuestType.COLLECT, "GOLD_INGOT",                   8, 600, 250);
        add("collect_diamond",     "Гранильщик",              "Собери {n} алмазов",            QuestType.COLLECT, "DIAMOND",                      4, 800, 350);
        add("collect_oak_log",     "Лесоруб",                 "Собери {n} дубовых брёвен",     QuestType.COLLECT, "OAK_LOG",                     32, 200,  50);
        add("collect_spruce_log",  "Таёжный лесоруб",         "Собери {n} еловых брёвен",      QuestType.COLLECT, "SPRUCE_LOG",                  32, 200,  50);
        add("collect_wheat",       "Фермер пшеницы",          "Собери {n} пшеницы",            QuestType.COLLECT, "WHEAT",                       64, 200,  50);
        add("collect_gunpowder",   "Пороховой запас",         "Собери {n} пороха",             QuestType.COLLECT, "GUNPOWDER",                   10, 350, 120);
        add("collect_ender_pearl", "Жемчужный охотник",       "Собери {n} жемчужин эндера",    QuestType.COLLECT, "ENDER_PEARL",                  6, 500, 200);
        add("collect_blaze_rod",   "Адский запас",            "Собери {n} стержней блейза",    QuestType.COLLECT, "BLAZE_ROD",                    4, 600, 250);
        add("collect_slimeball",   "Слизистый промысел",      "Собери {n} слизневых шариков",  QuestType.COLLECT, "SLIME_BALL",                   8, 350, 120);
        add("collect_bone",        "Костяная коллекция",      "Собери {n} костей",             QuestType.COLLECT, "BONE",                        16, 250,  80);
        add("collect_emerald",     "Торговец изумрудами",     "Собери {n} изумрудов",          QuestType.COLLECT, "EMERALD",                      6, 700, 300);
        add("collect_leather",     "Кожевник",                "Собери {n} кожи",               QuestType.COLLECT, "LEATHER",                     16, 300, 100);
        add("collect_string",      "Паучья нить",             "Собери {n} нитей",              QuestType.COLLECT, "STRING",                      32, 200,  60);
        add("collect_rotten_flesh","Гнилой торговец",         "Собери {n} гнилого мяса",       QuestType.COLLECT, "ROTTEN_FLESH",                32, 150,  40);
        add("collect_sugar_cane",  "Сладкий тростник",        "Собери {n} сахарного тростника",QuestType.COLLECT, "SUGAR_CANE",                  32, 200,  50);
        add("collect_feather",     "Перьевой сбор",           "Собери {n} перьев",             QuestType.COLLECT, "FEATHER",                     16, 200,  60);
        add("collect_copper",      "Медный рудокоп",          "Собери {n} медных слитков",     QuestType.COLLECT, "COPPER_INGOT",                24, 300, 100);
        add("collect_amethyst",    "Аметистовый клад",        "Собери {n} осколков аметиста",  QuestType.COLLECT, "AMETHYST_SHARD",              12, 400, 150);
        add("collect_glowstone",   "Нетерный свет",           "Собери {n} блоков глоустоуна",  QuestType.COLLECT, "GLOWSTONE_DUST",              16, 400, 140);

        // ── Mine ──
        add("mine_stone",          "Каменотёс",               "Добудь {n} камня",              QuestType.MINE,    "STONE",                       32, 200,  50);
        add("mine_deepslate",      "Глубинный шахтёр",        "Добудь {n} глубинного сланца",  QuestType.MINE,    "DEEPSLATE",                   32, 250,  80);
        add("mine_iron_ore",       "Железный шахтёр",         "Добудь {n} железной руды",      QuestType.MINE,    "IRON_ORE",                    12, 400, 150);
        add("mine_iron_ore_ds",    "Глубокий шахтёр",         "Добудь {n} глубинной жел. руды",QuestType.MINE,    "DEEPSLATE_IRON_ORE",          12, 450, 170);
        add("mine_gold_ore",       "Золотой шахтёр",          "Добудь {n} золотой руды",       QuestType.MINE,    "GOLD_ORE",                     6, 600, 250);
        add("mine_coal_ore",       "Угольный шахтёр",         "Добудь {n} угольной руды",      QuestType.MINE,    "COAL_ORE",                    20, 200,  60);
        add("mine_copper_ore",     "Медный шахтёр",           "Добудь {n} медной руды",        QuestType.MINE,    "COPPER_ORE",                  16, 300, 100);
        add("mine_gravel",         "Гравийный промысел",      "Добудь {n} гравия",             QuestType.MINE,    "GRAVEL",                      32, 150,  40);
        add("mine_sand",           "Песчаный промысел",       "Добудь {n} песка",              QuestType.MINE,    "SAND",                        32, 150,  40);
        add("mine_netherrack",     "Нетерный каменщик",       "Добудь {n} нетерного камня",    QuestType.MINE,    "NETHERRACK",                  48, 200,  60);

        // ── Fish ──
        add("fish_any",            "Рыбак",                   "Поймай {n} рыб",                QuestType.FISH,    "ANY",                          8, 300, 100);
        add("fish_salmon",         "Лосось на ужин",          "Поймай {n} лосося",             QuestType.FISH,    "RAW_SALMON",                   5, 350, 120);
        add("fish_cod",            "Рыбный рынок",            "Поймай {n} трески",             QuestType.FISH,    "COD",                         10, 250,  80);
        add("fish_pufferfish",     "Ядовитый улов",           "Поймай {n} иглобрюхов",         QuestType.FISH,    "PUFFERFISH",                   3, 500, 200);
        add("fish_tropical",       "Тропический улов",        "Поймай {n} тропических рыб",    QuestType.FISH,    "TROPICAL_FISH",                4, 450, 180);

        // ── Market sell ──
        add("market_sell_any",     "Торговец",                "Продай {n} любых предметов на рынке",       QuestType.MARKET_SELL, "any",  32, 800, 300);
        add("market_sell_10",      "Мелкий торговец",         "Продай {n} предметов на рынке",              QuestType.MARKET_SELL, "any",  10, 400, 150);
        add("market_sell_diamonds","Алмазный делец",          "Продай {n} алмазов на рынке",               QuestType.MARKET_SELL, "minecraft:diamond", 4, 1200, 500);
        add("market_sell_iron",    "Железный купец",          "Продай {n} железных слитков на рынке",      QuestType.MARKET_SELL, "minecraft:iron_ingot", 64, 600, 250);
        add("market_sell_gold",    "Золотой купец",           "Продай {n} золотых слитков на рынке",       QuestType.MARKET_SELL, "minecraft:gold_ingot", 32, 700, 300);

        // ── Market buy ──
        add("market_buy_any",      "Покупатель",              "Купи {n} любых предметов на рынке",         QuestType.MARKET_BUY,  "any",  16, 600, 200);
        add("market_buy_5",        "Мелкий покупатель",       "Купи {n} предметов на рынке",               QuestType.MARKET_BUY,  "any",   5, 300, 100);

        // ── ModSell ──
        add("modsell_any",         "Торговец артефактами",    "Продай {n} модовых предметов через /modsell",QuestType.MOD_SELL,    "any",  10, 600, 200);
        add("modsell_bulk",        "Оптовый продавец",        "Продай {n} модовых предметов через /modsell",QuestType.MOD_SELL,    "any",  50, 1500, 600);

        // ── Breed ──
        add("breed_cow",           "Животновод",              "Разведи {n} коров",             QuestType.BREED,   "minecraft:cow",                3, 400, 150);
        add("breed_pig",           "Свиновод",                "Разведи {n} свиней",            QuestType.BREED,   "minecraft:pig",                3, 350, 120);
        add("breed_sheep",         "Овцевод",                 "Разведи {n} овец",              QuestType.BREED,   "minecraft:sheep",              3, 350, 120);
        add("breed_chicken",       "Птицевод",                "Разведи {n} кур",               QuestType.BREED,   "minecraft:chicken",            5, 250,  80);
        add("breed_horse",         "Конный завод",            "Разведи {n} лошадей",           QuestType.BREED,   "minecraft:horse",              2, 600, 250);
        add("breed_rabbit",        "Кроликовод",              "Разведи {n} кроликов",          QuestType.BREED,   "minecraft:rabbit",             4, 300, 100);
        add("breed_wolf",          "Заводчик волков",         "Разведи {n} волков",            QuestType.BREED,   "minecraft:wolf",               2, 700, 300);
        add("breed_fox",           "Разведение лис",          "Разведи {n} лисиц",             QuestType.BREED,   "minecraft:fox",                2, 700, 300);
    }

    private static void add(String id, String name, String desc,
                             QuestType type, String target,
                             int required, double money, int exp) {
        POOL.add(new QuestTemplate(id, name, desc, type, target, required, money, exp));
    }

    /**
     * Picks {@code count} random quests for a player, ensuring at least 1 KILL
     * and at least 1 non-KILL quest in the result.
     */
    public static List<QuestTemplate> pickRandom(int count, long seed) {
        if (count <= 0 || POOL.isEmpty()) return List.of();

        List<QuestTemplate> shuffled = new ArrayList<>(POOL);
        Collections.shuffle(shuffled, new Random(seed));

        List<QuestTemplate> result = new ArrayList<>(count);
        Set<String> usedIds = new HashSet<>();

        // Ensure at least 1 KILL
        for (QuestTemplate t : shuffled) {
            if (t.type == QuestType.KILL) { result.add(t); usedIds.add(t.id); break; }
        }
        // Ensure at least 1 non-KILL
        for (QuestTemplate t : shuffled) {
            if (t.type != QuestType.KILL && !usedIds.contains(t.id)) {
                result.add(t); usedIds.add(t.id); break;
            }
        }
        // Fill the rest
        for (QuestTemplate t : shuffled) {
            if (result.size() >= count) break;
            if (!usedIds.contains(t.id)) { result.add(t); usedIds.add(t.id); }
        }
        return result;
    }
}
