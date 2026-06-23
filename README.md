# 📜 VoidRP Daily Quests

> Paper 1.21.1 плагин — ежедневные квесты, испытания героя и задания Торговца Артефактами.

![Paper](https://img.shields.io/badge/Paper-1.21.1-00AF54)
![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Vault](https://img.shields.io/badge/soft--depend-Vault-yellow)
![License](https://img.shields.io/badge/license-proprietary-red)

---

## 🗺️ Место в экосистеме

```
  Игрок выполняет действие в игре
        │
  voidrp-daily-quests
        │ QuestCompleteEvent
        ├──► voidrp-battlepass (XP за выполнение квеста)
        │
        │ POST /api/v1/daily-quests/* (X-Game-Auth-Secret)
        ▼
  minecraft-backend ←→ voidrp-site
```

---

## ✨ Возможности

### Ежедневные квесты
- Каждый день игрок получает **N случайных квестов** из настраиваемого пула
- Типы заданий: убийства, добыча, крафт, торговля, путешествие, доставка
- Автосброс квестов в заданный час суток (настраивается)
- Денежные и предметные награды через Vault

### Испытание Героя (`/bossquest`)
- Один сложный квест на **3 дня** с повышенной наградой
- Уникальный пул сложных заданий
- Отдельный NPC для получения и сдачи

### Торговец Артефактами (`/delivery`)
- Задания на доставку конкретных предметов в определённое место
- Ротация заданий по таймеру

### Интерфейс
- Закрепление активного квеста на экране (`/questtrack`)
- GUI через NPC
- Команды для всех типов заданий

---

## 📋 Требования

| Компонент | Версия |
|---|---|
| Paper / Mohist | 1.21.1 |
| Java | 21 |
| Vault | опционально (денежные награды) |

---

## 🚀 Сборка и установка

```bash
cd voidrp_daily_quests
./gradlew shadowJar
# → build/libs/voidrp-daily-quests-*.jar
```

1. Скопировать jar в `plugins/`
2. Перезапустить сервер
3. Настроить `plugins/VoidRpDailyQuests/config.yml`
4. Задать пул заданий в `quests.yml`

---

## 🛠️ Команды

| Команда | Описание |
|---|---|
| `/quests` | Открыть список своих квестов |
| `/questtrack` | Закрепить/открепить активный квест на HUD |
| `/bossquest` | Взять/сдать испытание героя |
| `/delivery` | Взять/сдать задание Торговца |

---

## 🔗 Связанные репозитории

| Репо | Связь |
|---|---|
| [minecraft-backend](https://github.com/VOIDRP-MINECRAFT/minecraft-backend) | Хранит прогресс квестов |
| [voidrp-battlepass](https://github.com/VOIDRP-MINECRAFT/voidrp-battlepass) | Получает XP из квестов, расширяет пул |
| [voidrp-gamesync-plugin](https://github.com/VOIDRP-MINECRAFT/voidrp-gamesync-plugin) | Рыночные торги засчитываются в квесты |

---

<div align="center">
<a href="https://void-rp.ru">🌐 Сайт</a> ·
<a href="https://github.com/VOIDRP-MINECRAFT">🏠 Организация</a>
</div>
