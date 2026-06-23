package ru.voidrp.dailyquests.quest;

import java.util.*;

/**
 * One ultra-hard quest per 3 days per player.
 * Boss kills, rare crafts, hard collection — requires serious progression.
 */
public final class HardQuestPool {

    private static final List<QuestTemplate> POOL = new ArrayList<>();

    static {
        // ── Vanilla bosses ──
        add("hard_wither",          "Иссушитель",
            "Убей Иссушителя",
            QuestType.KILL, "minecraft:wither",               1, 8000, 3000);
        add("hard_elder_guardian",  "Морской тиран",
            "Убей 3 старших стражей",
            QuestType.KILL, "minecraft:elder_guardian",       3, 6000, 2500);
        add("hard_ravager_slayer",  "Истребитель опустошителей",
            "Убей 8 опустошителей",
            QuestType.KILL, "minecraft:ravager",              8, 5000, 2000);
        add("hard_evoker_purge",    "Зачистка поместья",
            "Убей 15 заклинателей",
            QuestType.KILL, "minecraft:evoker",              15, 5500, 2200);

        // ── Twilight Forest bosses ──
        add("hard_naga",            "Покоритель Нага",
            "Убей Нага Сумеречного Леса",
            QuestType.KILL, "twilightforest:naga",            1, 6000, 2500);
        add("hard_lich",            "Тёмный Лич",
            "Убей Лича Сумеречного Леса",
            QuestType.KILL, "twilightforest:lich",            1, 7000, 3000);
        add("hard_hydra",           "Многоголовая гидра",
            "Убей Гидру Сумеречного Леса",
            QuestType.KILL, "twilightforest:hydra",           1, 9000, 4000);
        add("hard_ur_ghast",        "Призрак Небес",
            "Убей Ур-Гаста в Башне Призраков",
            QuestType.KILL, "twilightforest:ur_ghast",        1, 9000, 4000);
        add("hard_snow_queen",      "Снежная Королева",
            "Убей Снежную Королеву",
            QuestType.KILL, "twilightforest:snow_queen",      1, 8000, 3500);
        add("hard_alpha_yeti",      "Властелин Йети",
            "Убей Альфа-Йети",
            QuestType.KILL, "twilightforest:alpha_yeti",      1, 7000, 3000);
        add("hard_knight_phantom",  "Призрак-Рыцарь",
            "Убей 4 Призраков-Рыцарей",
            QuestType.KILL, "twilightforest:knight_phantom",  4, 6000, 2500);

        // ── Ice & Fire bosses ──
        add("hard_fire_dragon",     "Огненный Дракон",
            "Убей огненного дракона",
            QuestType.KILL, "iceandfire:fire_dragon",         1, 10000, 4500);
        add("hard_ice_dragon",      "Ледяной Дракон",
            "Убей ледяного дракона",
            QuestType.KILL, "iceandfire:ice_dragon",          1, 10000, 4500);
        add("hard_death_worm",      "Червь смерти",
            "Убей 2 Червей смерти",
            QuestType.KILL, "iceandfire:death_worm",          2, 8000, 3500);
        add("hard_sea_serpent_hard","Владыка морей",
            "Убей 3 морских змей",
            QuestType.KILL, "iceandfire:seaserpent",          3, 8000, 3500);
        add("hard_amphithere",      "Крылатый змей",
            "Убей 2 амфитиров",
            QuestType.KILL, "iceandfire:amphithere",          2, 7000, 3000);
        add("hard_cyclops_hard",    "Циклопья гора",
            "Убей 5 циклопов",
            QuestType.KILL, "iceandfire:cyclops",             5, 7000, 3000);

        // ── MowziesMobs bosses ──
        add("hard_barako",          "Солнечный вождь",
            "Убей Барако, Солнечного Вождя",
            QuestType.KILL, "mowziesmobs:barako",             1, 8000, 3500);
        add("hard_frostmaw",        "Ледяная пасть",
            "Убей Фростмо",
            QuestType.KILL, "mowziesmobs:frostmaw",           1, 8000, 3500);
        add("hard_umvuthana",       "Разлагатель",
            "Убей Умвутану",
            QuestType.KILL, "mowziesmobs:umvuthana",          1, 7000, 3000);

        // ── L'Ender's Cataclysm bosses ──
        add("hard_ender_guardian",  "Стражник Эндера",
            "Убей Стражника Эндера",
            QuestType.KILL, "cataclysm:ender_guardian",       1, 10000, 4500);
        add("hard_ignis",           "Дух Огня — Игнис",
            "Убей Игниса",
            QuestType.KILL, "cataclysm:ignis",                1, 10000, 4500);
        add("hard_leviathan",       "Левиафан",
            "Убей Левиафана",
            QuestType.KILL, "cataclysm:the_leviathan",        1, 12000, 5500);
        add("hard_ancient_remnant", "Древний Пережиток",
            "Убей Древний Пережиток",
            QuestType.KILL, "cataclysm:ancient_remnant",      1, 11000, 5000);

        // ── Aether bosses ──
        add("hard_slider",          "Каменный Ползун",
            "Убей 2 Ползунов в Этере",
            QuestType.KILL, "aether:slider",                  2, 7000, 3000);
        add("hard_valkyrie_queen",  "Королева Валькирий",
            "Убей Королеву Валькирий",
            QuestType.KILL, "aether:valkyrie_queen",          1, 9000, 4000);
        add("hard_sun_spirit",      "Дух Солнца",
            "Убей Духа Солнца в Этере",
            QuestType.KILL, "aether:sun_spirit",              1, 10000, 4500);

        // ── Alex's Mobs hard ──
        add("hard_void_worm",       "Пустотный Червь",
            "Убей Пустотного Червя",
            QuestType.KILL, "alexsmobs:void_worm",            1, 12000, 5500);
        add("hard_enderiophage",    "Пожиратель Эндера",
            "Убей Пожирателя Эндера",
            QuestType.KILL, "alexsmobs:enderiophage",         1, 11000, 5000);

        // ── Rare collection ──
        add("hard_collect_nether_star", "Звёздный коллекционер",
            "Собери 2 звезды Нижнего Мира",
            QuestType.COLLECT, "NETHER_STAR",                 2, 10000, 4000);
        add("hard_collect_netherite",   "Неизериевый кузнец",
            "Собери 8 непереритовых слитков",
            QuestType.COLLECT, "NETHERITE_INGOT",             8, 8000, 3500);
        add("hard_collect_echo_shard",  "Эхо глубин",
            "Собери 16 эхо-фрагментов",
            QuestType.COLLECT, "ECHO_SHARD",                 16, 6000, 2500);
        add("hard_collect_shulker",     "Шалкерный промысел",
            "Собери 8 панцирей шалкера",
            QuestType.COLLECT, "SHULKER_SHELL",               8, 7000, 3000);
        add("hard_collect_elytra",      "Крылья свободы",
            "Найди и подбери элитры",
            QuestType.COLLECT, "ELYTRA",                      1, 15000, 6000);
        add("hard_collect_ancient_debris","Древние обломки",
            "Собери 16 древних обломков",
            QuestType.COLLECT, "ANCIENT_DEBRIS",             16, 9000, 4000);
        add("hard_collect_trident",     "Трезубец морей",
            "Добудь и собери 1 трезубец",
            QuestType.COLLECT, "TRIDENT",                     1, 8000, 3500);

        // ── Hard craft ──
        add("hard_craft_beacon",        "Маяк силы",
            "Скрафти маяк",
            QuestType.CRAFT, "BEACON",                        1, 12000, 5000);
        add("hard_craft_netherite_sword","Клинок Нижнего Мира",
            "Скрафти непереритовый меч",
            QuestType.CRAFT, "NETHERITE_SWORD",               1, 6000, 2500);
        add("hard_craft_netherite_chest","Нагрудник тьмы",
            "Скрафти непереритовый нагрудник",
            QuestType.CRAFT, "NETHERITE_CHESTPLATE",          1, 7000, 3000);
        add("hard_craft_enchanting_table","Стол чар",
            "Скрафти 3 стола зачарований",
            QuestType.CRAFT, "ENCHANTING_TABLE",              3, 5000, 2000);
        add("hard_craft_end_crystal",   "Кристалл Конца",
            "Скрафти 4 кристалла Конца",
            QuestType.CRAFT, "END_CRYSTAL",                   4, 8000, 3500);
        add("hard_craft_tnt_x64",       "Арсенал",
            "Скрафти 64 блока TNT",
            QuestType.CRAFT, "TNT",                          64, 4000, 1500);
    }

    private static void add(String id, String name, String desc,
                             QuestType type, String target,
                             int required, double money, int exp) {
        POOL.add(new QuestTemplate(id, name, desc, type, target, required, money, exp));
    }

    public static QuestTemplate pickOne(long seed) {
        if (POOL.isEmpty()) return null;
        List<QuestTemplate> shuffled = new ArrayList<>(POOL);
        Collections.shuffle(shuffled, new Random(seed));
        return shuffled.get(0);
    }
}
