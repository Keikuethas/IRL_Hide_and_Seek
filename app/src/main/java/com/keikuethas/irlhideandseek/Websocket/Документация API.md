# 🎮 Документация API игры (Hide & Seek)

> Location-based multiplayer game с ролями, способностями и событиями

---

## 📋 Оглавление
1.  [[Документация API#🎯 Создание игры|🎯 Создание игры]]
2. [[Документация API#⚡ Шаблоны способностей|⚡ Шаблоны способностей]]
3. [[Документация API#💥 Шаблоны событий|💥 Шаблоны событий]]
4. [[Документация API#🏆 Условия победы|🏆 Условия победы]]
5. [[Документация API#👤 Присоединение игрока|👤 Присоединение игрока]]
6. [[Документация API#🔌 WebSocket запросы|🔌 WebSocket запросы]]
7. [[Документация API#🗺️ Типы зон (`zone_type`)|🗺️ Типы зон]]
8. [[Документация API#💀 Причины смерти (`PlayerDeathCauses`)|💀 Причины смерти]]
---

## 🎯 Создание игры

### Запрос: Клиент → Сервер
```json
{
  "name": "Тестовая игра",
  "center_lat": 55.751244,
  "center_lng": 37.618423,
  "safe_zone_radius": 500.0,
  "min_zone_radius": 50.0,
  "zone_shrink_interval": 120,
  "game_duration": 1800,
  "time_to_hide": 300,
  "host_player": {
    "host_name": "Keopint",
    "host_player_location_lat": 20.0,
    "host_player_location_lng": 20.0
  },
  "game_roles": {
    "Amogus": {
      "health": 200,
      "victory_condition": "SEEKER"
    },
    "Hunter": {
      "health": 100,
      "victory_condition": "SEEKER"
    },
    "Hider": {
      "health": 100,
      "victory_condition": "HIDER"
    }
  },
  "roles_abilities": {
    "Amogus": {
      "TRAP": {
        "duration_seconds": 600,
        "number_uses": 2,
        "recharge_time": 60,
        "addition_data": {
          "radius": 10.0,
          "trap_duration_seconds": 120
        }
      },
      "SCAN": {
        "duration_seconds": 600,
        "number_uses": 2,
        "recharge_time": 60,
        "addition_data": {}
      }
    },
    "Hunter": {
      "PERSONAL_BOMB": {
        "duration_seconds": 600,
        "number_uses": 2,
        "recharge_time": 60,
        "addition_data": {
          "radius": 10.0,
          "damage": 100
        }
      }
    },
    "Hider": {
      "SNARE": {
        "duration_seconds": 600,
        "number_uses": 2,
        "recharge_time": 60,
        "addition_data": {
          "radius": 10.0,
          "trap_duration_seconds": 600
        }
      }
    }
  },
  "events": ["BOMB", "AIRDROP", "BOMBARDMENT"],
  "roles_events": {
    "Amogus": ["BOMB", "AIRDROP", "BOMBARDMENT"],
    "Hunter": ["BOMB", "BOMBARDMENT"],
    "Hider": ["BOMB", "AIRDROP", "BOMBARDMENT"]
  },
  "events_configurations": {
    "BOMB": {
      "activation_frequency": "FREQUENT",
      "addition_data": {
        "duration_seconds": 600,
        "radius": 10.0,
        "damage": 100
      }
    },
    "AIRDROP": {
      "activation_frequency": "RARE",
      "addition_data": {
        "radius": 10.0
      }
    },
    "BOMBARDMENT": {
      "activation_frequency": "COMMON",
      "addition_data": {
        "duration_seconds": 600,
        "radius": 5.0,
        "damage": 50
      }
    }
  }
}
```

### Ответ: Сервер → Клиент
```json
{
  "game": {
    "game_code": "C3BLBA",
    "created_at": "2026-05-10T13:31:00",
    "zone_boundary_damage": 1,
    "safe_zone_center_lat": 55.751244,
    "current_safe_zone_id": "11e28ede-00cd-4313-b129-cb6a5bfef0b1",
    "safe_zone_center_lng": 37.618423,
    "last_shrink_at": "2026-05-10T13:31:51.648583",
    "safe_zone_radius": 500,
    "min_zone_radius": 50,
    "id": "0c92cc10-e7a2-4ae2-9054-151c6341040a",
    "zone_shrink_interval": 120,
    "name": "Тестовая игра",
    "game_duration": 1800,
    "status": "WAITING",
    "time_to_hide": 300,
    "roles": [
      {
        "name": "Amogus",
        "health": 200,
        "victory_condition": "SEEKER",
        "id": "c58157e8-e995-4233-8213-4e954206c3c2",
        "events": [...],
        "abilities": [...]
      }
    ]
  },
  "host_player_id": "652f579f-c0bf-43ae-98eb-afe3cf0e0169"
}
```

---

## ⚡ Шаблоны способностей

```json
{
  "abilities_configurations": {
    "SHIELD": {
      "duration_seconds": 600,
      "number_uses": 2,
      "recharge_time": 60,
      "addition_data": {}
    },
    "INTEL": {
      "duration_seconds": 600,
      "number_uses": 2,
      "recharge_time": 60,
      "addition_data": {}
    },
    "SCAN": {
      "duration_seconds": 600,
      "number_uses": 2,
      "recharge_time": 60,
      "addition_data": {}
    },
    "PERSONAL_BOMB": {
      "duration_seconds": 600,
      "number_uses": 2,
      "recharge_time": 60,
      "addition_data": {
        "radius": 10.0,
        "damage": 100
      }
    },
    "TRAP": {
      "duration_seconds": 600,
      "number_uses": 2,
      "recharge_time": 60,
      "addition_data": {
        "radius": 10.0,
        "trap_duration_seconds": 120
      }
    },
    "SNARE": {
      "duration_seconds": 600,
      "addition_data": {
        "radius": 10.0,
        "trap_duration_seconds": 600
      }
    },
    "SAFE_HOUSE": {
      "duration_seconds": 600,
      "number_uses": 2,
      "recharge_time": 60,
      "addition_data": {
        "radius": 20.0
      }
    },
    "SAFE_MANSION": {
      "duration_seconds": 600,
      "number_uses": 2,
      "recharge_time": 60,
      "addition_data": {
        "radius": 30.0
      }
    }
  }
}
```

### 📖 Расшифровка способностей

| Ключ            | Название           | Описание                            |
|-----------------|--------------------|-------------------------------------|
| `SHIELD`        | Щит                | Защитный барьер                     |
| `INTEL`         | Разведданные       | Получение информации                |
| `SCAN`          | Сканирование       | Обнаружение игроков в радиусе       |
| `PERSONAL_BOMB` | Персональная бомба | Взрыв вокруг игрока                 |
| `SNARE`         | Капкан             | Ловушка для охотников               |
| `TRAP`          | Ловушка            | Замедление/обездвиживание           |
| `SAFE_HOUSE`    | Я в домике         | Временная безопасная зона (малая)   |
| `SAFE_MANSION`  | Я в особняке       | Временная безопасная зона (большая) |

---

## 💥 Шаблоны событий

```json
{
  "BOMB": {
    "activation_frequency": "FREQUENT",
    "addition_data": {
      "duration_seconds": 60,
      "radius": 10.0,
      "damage": 100
    }
  },
  "AIRDROP": {
    "activation_frequency": "RARE",
    "addition_data": {
      "radius": 10.0
    }
  },
  "BOMBARDMENT": {
    "activation_frequency": "COMMON",
    "addition_data": {
      "duration_seconds": 60,
      "radius": 5.0,
      "damage": 50
    }
  },
  "REVEAL": {
    "activation_frequency": "COMMON",
    "addition_data": {
      "duration_seconds": 60
    }
  }
}
```

### Частота активации событий:
| Значение   | Описание          |
|------------|-------------------|
| `FREQUENT` | Частое появление  |
| `COMMON`   | Обычное появление |
| `RARE`     | Редкое появление  |

---

## 🏆 Условия победы

> На данный момент реализовано два типа, расширение не планируется до презентации

| Значение | Роль       | Описание                                   |
|----------|------------|--------------------------------------------|
| `HIDER`  | Прячущийся | Выжить до конца игры / не быть найденным   |
| `SEEKER` | Охотник    | Найти всех прячущихся до истечения времени |

---

## 👤 Присоединение игрока

### Запрос: Клиент → Сервер
```json
{
  "name": "Igor",
  "player_location_lat": 20.0,
  "player_location_lng": 33.0
}
```

### Ответ: Сервер → Клиент
```json
{
  "game_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1",
  "player_id": "awfrawde8-4bba-ab83-fsfe-eerwd5555b1",
  "player_name": "Igor",
  "game_status": "WAITING",
  "ws_url": "ws://your-server.com/ws/{game_id}/{player_id}"
}
```

---

## 🔌 WebSocket запросы

### 1. `ping` — проверка связи
**Клиент → Сервер:**
```json
{ "type": "ping", "data": {} }
```
**Сервер → Клиент:**
```json
{
  "type": "pong",
  "data": { "server_time": "2026-04-28T08:01:53.075554" }
}
```

---

### 2. `websocket_connected_player` — подключение к игре
**Сервер → Клиент:**
```json
{
  "type": "websocket_connected_player",
  "data": {
    "player_data": {
      "name": "Keopint",
      "health": 100,
      "location_lat": 20,
      "last_location_update": "2026-05-02T20:56:30.711005",
      "id": "819131a0-aed4-4fc1-b191-6398a264ac1d",
      "game_id": "b3db2f60-82ed-461b-ab29-4768b9e8d7c3",
      "role_id": "a7446315-1aba-43a3-8507-226efafae9bf",
      "is_alive": true,
      "location_lng": 20,
      "trapped_until": null,
      "is_player_ready": false
    },
    "game_data": { ... }
  }
}
```

---

### 3. `update_location` — обновление геолокации
**Клиент → Сервер:**
```json
{
  "type": "update_location",
  "data": {
    "lat": 20.2443,
    "lng": 33.2453
  }
}
```
**Сервер → Все клиенты (кроме отправителя):**
```json
{
  "type": "player_moved",
  "data": {
    "player_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1",
    "location_lat": 20.2443,
    "location_lng": 33.2453,
    "timestamp": "2026-04-28T08:01:53.075554"
  }
}
```

---

### 4. `use_ability` — использование способности
> ⚠️ Работает только при статусе игры `ACTIVE`

**Клиент → Сервер:**
```json
{
  "type": "use_ability",
  "data": { "ability_type": "PERSONAL_BOMB" }
}
```
**Сервер → Клиент:**
```json
{
  "type": "ability_used",
  "data": {
    "ability": "PERSONAL_BOMB",
    "result": 0
  }
}
```
> `result: 0` — успешная активация

---

### 5. `get_game_state` — запрос полного состояния игры
**Клиент → Сервер:**
```json
{ "type": "get_game_state", "data": {} }
```
**Сервер → Клиент:**
```json
{
  "type": "game_state",
  "data": {
    "game_info": {
      "id": "6811c4cb-3537-4373-b05f-a05129f809fe",
      "game_code": "DB7SQ4",
      "name": "TestGame",
      "status": "WAITING",
      "created_at": "2026-05-15T14:48:00",
      "safe_zone_center_lat": 55.751244,
      "safe_zone_center_lng": 37.618423,
      "safe_zone_radius": 500.0,
      "min_zone_radius": 50.0,
      "zone_shrink_interval": 120,
      "game_duration": 1800,
      "time_to_hide": 300,
      "zone_boundary_damage": 1,
      "current_safe_zone_id": "7f1a368d-58e9-4f1f-92f2-f03417961cfc",
      "last_shrink_at": "2026-05-15T14:48:57.378129",
      "roles": [...]
    },
    "player_info": { ... }
  }
}
```

---

### 6. `change_role` — смена роли
> ⚠️ Работает только при статусе игры `WAITING`

**Клиент → Сервер:**
```json
{
  "type": "change_role",
  "data": { "role_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1" }
}
```
**Сервер → Клиент:**
```json
{
  "type": "role_changed",
  "data": { "role_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1" }
}
```

---

### 7. `change_ready_status` — изменение статуса готовности
> ⚠️ Работает только при статусе игры `WAITING`

**Клиент → Сервер:**
```json
{
  "type": "change_ready_status",
  "data": { "status": true }
}
```
**Сервер → Клиент:**
```json
{
  "type": "ready_status_changed",
  "data": { "status": true }
}
```

---

### 8. `create_zone` — отрисовка зоны
**Сервер → Клиенты:**
```json
{
  "type": "create_zone",
  "data": {
    "zone_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1",
    "zone_type": "DANGER",
    "center_lat": 20.2443,
    "center_lng": 33.2453,
    "radius": 11.323
  }
}
```

---

### 9. `delete_zone` — удаление зоны
**Сервер → Клиенты:**
```json
{
  "type": "delete_zone",
  "data": { "zone_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1" }
}
```

---

### 10. `you_died` — смерть текущего игрока
> ⚠️ Клиент должен закрыть WebSocket-соединение после получения

**Сервер → Клиент:**
```json
{
  "type": "you_died",
  "data": {
    "reason": "HP_ARE_OVER",
    "hunter_player_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1"
  }
}
```

---

### 11. `player_died` — смерть другого игрока
**Сервер → Все клиенты:**
```json
{
  "type": "player_died",
  "data": {
    "reason": "HUNTER_FOUND_PLAYER",
    "player_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1",
    "hunter_player_id": "66665833-e5e8-4bba-ab83-e40d5d7182b1"
  }
}
```

---

### 12. `hunter_found_player` — охотник нашёл игрока
**Клиент → Сервер:**
```json
{
  "type": "hunter_found_player",
  "data": { "found_player_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1" }
}
```
**Сервер → Все игроки:**
```json
{
  "type": "player_died",
  "data": {
    "reason": "HUNTER_FOUND_PLAYER",
    "player_id": "23345833-e5e8-4bba-ab83-e40d5d7182b1",
    "hunter_player_id": "66665833-e5e8-4bba-ab83-e40d5d7182b1"
  }
}
```

---

### 13. `start_timer_for_game` — старт активной фазы
**Сервер → Клиенты:**
```json
{
  "type": "start_timer_for_game",
  "data": { "duration_seconds": 1500 }
}
```

---

### 14. `game_finished` — окончание игры
**Сервер → Клиенты:**
```json
{
  "type": "game_finished",
  "data": { "is_victory": true }
}
```

---

## 🗺️ Типы зон (`zone_type`)

| Значение       | Цвет          | Описание                        |
|----------------|---------------|---------------------------------|
| `SAFE`         | 🔵 Синяя      | Начальная безопасная зона       |
| `DANGER`       | 🔴 Красная    | Зона бомбы                      |
| `WARNING`      | 🟠 Оранжевая  | Зона бомбардировки              |
| `AIRDROP`      | 🟡 Жёлтая     | Зона сброса припасов            |
| `SNARE`        | ⚪ Серая       | Капкан                          |
| `TRAP`         | ⚫ Чёрная      | Ловушка                         |
| `SAFE_HOUSE`   | 🟢 Зелёная    | «Я в домике» (малая защита)     |
| `SAFE_MANSION` | 🟣 Фиолетовая | «Я в особняке» (большая защита) |

---

## 💀 Причины смерти (`PlayerDeathCauses`)

| Значение              | Описание                                                 |
|-----------------------|----------------------------------------------------------|
| `HUNTER_FOUND_PLAYER` | Игрок был найден и «пойман» охотником                    |
| `HP_ARE_OVER`         | У игрока закончились очки здоровья (урон от зон/событий) |

---

> 📝 **Примечание:** Все временные значения указаны в секундах, координаты — в десятичных градусах (WGS84), радиусы — в метрах.

*Документация актуальна на май 2026 г.*