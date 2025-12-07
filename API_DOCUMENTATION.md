# API Документация для сервера анализа расходов

## Базовый URL
`https://your-backend-url.com/api`

---

## Endpoints

### 1. Отправка расходов
**POST** `/expenses`

#### Описание
Сохраняет список расходов пользователя на сервере.

#### Request Headers
```
Content-Type: application/json
```

#### Request Body
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 250.50,
    "category": "Food",
    "description": "Groceries shopping",
    "date": "2023-12-07T14:30:00"
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "amount": 50.00,
    "category": "Transport",
    "description": "Taxi",
    "date": "2023-12-07T16:00:00"
  }
]
```

#### Response
```json
HTTP 200 OK
```

---

### 2. Анализ расходов
**POST** `/expenses/analyze`

#### Описание
Анализирует список расходов и возвращает детальную статистику с рекомендациями.

#### Request Headers
```
Content-Type: application/json
```

#### Request Body
Массив объектов расходов (такой же формат, как в `/expenses`)

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 250.50,
    "category": "Food",
    "description": "Groceries shopping",
    "date": "2023-12-07T14:30:00"
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "amount": 50.00,
    "category": "Transport",
    "description": "Taxi",
    "date": "2023-12-07T16:00:00"
  },
  {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "amount": 150.00,
    "category": "Food",
    "description": "Restaurant",
    "date": "2023-12-07T19:00:00"
  }
]
```

#### Response (Вариант 1 - структурированный JSON)
```json
{
  "totalAmount": 450.50,
  "categoryBreakdown": {
    "Food": 400.50,
    "Transport": 50.00
  },
  "averageExpense": 150.17,
  "topCategory": "Food",
  "recommendations": [
    "Расходы на еду составляют 89% от общих расходов",
    "Рекомендуется сократить походы в рестораны",
    "Попробуйте готовить дома чаще"
  ],
  "summary": "За анализируемый период вы потратили 450.50. Основная часть расходов пришлась на категорию Food."
}
```

#### Response (Вариант 2 - простой текст)
```
Анализ расходов:
Общая сумма: 450.50
Средний расход: 150.17
Топ категория: Food (400.50)

Рекомендации:
- Расходы на еду составляют 89% от общих расходов
- Рекомендуется сократить походы в рестораны
```

#### Error Response
```json
{
  "error": "Invalid request format",
  "message": "Detailed error description"
}
```

---

## Модели данных

### Expense
```kotlin
{
  "id": String,              // UUID расхода
  "amount": Double,          // Сумма расхода
  "category": String,        // Категория (Food, Transport, Entertainment, etc.)
  "description": String,     // Описание расхода
  "date": String            // Дата и время в формате ISO 8601 (YYYY-MM-DDTHH:mm:ss)
}
```

### ExpenseAnalysis (для структурированного ответа)
```kotlin
{
  "totalAmount": Double,                    // Общая сумма всех расходов
  "categoryBreakdown": Map<String, Double>, // Разбивка по категориям (категория -> сумма)
  "averageExpense": Double,                 // Средний размер расхода
  "topCategory": String,                    // Категория с наибольшими расходами
  "recommendations": List<String>,          // Список рекомендаций
  "summary": String                         // Общее резюме анализа
}
```

---

## Примеры использования

### Пример 1: Отправка расхода
```bash
curl -X POST https://your-backend-url.com/api/expenses \
  -H "Content-Type: application/json" \
  -d '[{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "amount": 100.0,
    "category": "Food",
    "description": "Lunch",
    "date": "2023-12-07T12:00:00"
  }]'
```

### Пример 2: Анализ расходов
```bash
curl -X POST https://your-backend-url.com/api/expenses/analyze \
  -H "Content-Type: application/json" \
  -d '[{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "amount": 100.0,
    "category": "Food",
    "description": "Lunch",
    "date": "2023-12-07T12:00:00"
  }]'
```

---

## Примечания

1. Все даты должны быть в формате ISO 8601: `YYYY-MM-DDTHH:mm:ss`
2. Суммы (amount) должны быть положительными числами типа Double
3. ID генерируется на клиенте в формате UUID
4. Сервер может возвращать анализ как в виде структурированного JSON, так и в виде простого текста
5. Приложение автоматически обработает оба формата ответа
6. Рекомендации и summary должны быть на русском языке

