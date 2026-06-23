# VoidRP Daily Quests

Paper 1.21.1 плагин — система ежедневных квестов, испытаний героя и заданий Торговца Артефактами.

## Возможности

- **Ежедневные квесты** — каждый день игрок получает N случайных квестов из пула
- **Испытание Героя** (`/bossquest`) — один сложный квест на 3 дня с повышенной наградой
- **Торговец Артефактами** (`/delivery`) — задания на доставку предметов
- Закрепление квеста на экране (`/questtrack`) для отслеживания прогресса
- Автосброс квестов в заданный час суток
- GUI через NPC и команды
- Soft-depend на Vault (денежные награды)

## Требования

- Paper / Purpur 1.21.1
- Java 21
- (опционально) Vault

## Сборка

```bash
cd voidrp_daily_quests
./gradlew shadowJar
# → build/libs/voidrp-daily-quests-*.jar
```

## Установка

1. Положить jar в `plugins/`
2. Перезапустить сервер
3. Настроить `plugins/VoidRpDailyQuests/config.yml`

## Конфигурация (`config.yml`)

```yaml
quests-per-day: 3
reset-hour: 0
reward-multiplier: 1.0

npc-names:
  - "§6Квестодатель"

delivery-npc-names:
  - "§5Торговец Артефактами"
```

## Команды

| Команда | Описание |
|---|---|
| `/dq` | Открыть GUI ежедневных квестов |
| `/bossquest` | Открыть Испытание Героя |
| `/delivery` | Открыть задания Торговца |
| `/questtrack` | Закрепить/открепить квест на экране |
| `/dqadmin reset <player>` | Сбросить квесты игрока |
| `/dqadmin info <player>` | Информация о квестах игрока |
| `/bqadmin reset <player>` | Сбросить испытание героя |
| `/bqadmin info <player>` | Информация об испытании |

**Права:** `voidrp.dailyquests.admin`, `voidrp.dailyquests.use`

## NPC интеграция

Плагин реагирует на клик по NPC с именем из списка `npc-names` / `delivery-npc-names`.
Совместим с CitizensCMD: `/npc command add -p dailyquest`.

## Архитектура

```
VoidRpDailyQuestsPlugin.java   — точка входа
quest/
  QuestPool.java               — пул шаблонов квестов
  QuestStorage.java            — персистентность прогресса
  QuestType.java               — типы квестов (убийство, добыча, крафт, …)
  ActiveQuest.java             — активный квест игрока
delivery/
  DeliveryQuestPool.java       — пул заданий доставки
  DeliveryQuestStorage.java    — персистентность
listener/
  QuestProgressListener.java   — отслеживание событий игрока
  NpcInteractListener.java     — открытие GUI по клику на NPC
```
