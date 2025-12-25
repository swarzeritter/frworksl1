## Лабораторна робота 1 — «Книга відгуків»

**Дисципліна**: Сучасні фреймворки програмування  
**Завдання**: базовий веб-додаток у рамках Jakarta з використанням Maven, Jetty 11, JDBC/H2 та логування.

### Використані версії

- **JDK**: 21 (Java 21)
- **Maven**: 3.x

### Стек технологій

- **Сервлети**: Jakarta Servlet API 5.0.0 (через `jakarta.servlet-api`, scope `provided`)
- **Сервер застосунків**: Jetty 11.0.24 (`jetty-maven-plugin`)
- **База даних**: H2 2.2.224 (вбудована, файловий режим)
- **Доступ до БД**: чистий JDBC
- **JSON-серіалізація**: Jackson Databind 2.17.2 + Jackson Datatype JSR310
- **Логування**: SLF4J 2.0.16 + Logback Classic 1.5.7

### Налаштування БД

- **JDBC URL**: `jdbc:h2:file:/data/guest;AUTO_SERVER=TRUE`
- **Користувач**: `sa`
- **Пароль**: порожній рядок
- **Шлях до файлу БД**: `/data/guest.mv.db` (створюється автоматично)
- **DDL** (створення таблиці `comments`):

```sql
create table if not exists comments (
    id bigint generated always as identity primary key,
    author varchar(64) not null,
    text varchar(1000) not null,
    created_at timestamp not null
);
```

Схема ініціалізується автоматично при першому зверненні до DAO.

### Структура проєкту

- `pom.xml` — конфігурація Maven (war-packaging, плагіни compiler і jetty, залежності)
- `src/main/java/org/example/guestbook/Comment` — модель коментаря
- `src/main/java/org/example/guestbook/CommentDao` — робота з H2 через JDBC
- `src/main/java/org/example/guestbook/CommentsServlet` — REST-ендпоінт `/comments` (GET/POST)
- `src/main/java/org/example/guestbook/IndexServlet` — кореневий ендпоінт `/` з HTML-сторінкою
- `src/main/webapp/WEB-INF/web.xml` — конфігурація веб-додатку
- `src/main/resources/logback.xml` — налаштування логування

Сторінка `/` реалізована як HTML, що генерується в сервлеті `IndexServlet`, з вбудованим JavaScript для звернення до REST-ендпоінта `/comments`.

### Ендпоінти

- **GET `/`**
  - HTML-сторінка з формою (`author`, `text`) та списком відгуків (рендериться через JS, запит на `/comments`).

- **GET `/comments`**
  - Повертає JSON-список коментарів, відсортований за новизною (останні зверху).
  - **Content-Type**: `application/json; charset=UTF-8`
  - **Приклад відповіді**:
    ```json
    [
      {
        "id": 1,
        "author": "Ім'я",
        "text": "Текст відгуку",
        "createdAt": "2025-12-24T13:45:00"
      }
    ]
    ```

- **POST `/comments`**
  - Додає новий запис у БД.
  - **Content-Type**: `application/x-www-form-urlencoded; charset=UTF-8`
  - **Параметри форми**:
    - `author` — обов'язково, довжина ≤ 64
    - `text` — обов'язково, довжина ≤ 1000
  - **Коди відповіді**:
    - `204` (No Content) — успіх, без тіла.
    - `400` (Bad Request) — помилка валідації (порожнє чи надто довге поле).
    - `500` (Internal Server Error) — збій при роботі з БД.

### Логування

Після успішного додавання запису сервлет `CommentsServlet` пише один **INFO**-лог через SLF4J/Logback з основною інформацією:

- `id` нового коментаря,
- `author`,
- довжина `text`.

Очікується, що при додаванні 2–3 відгуків у консолі Maven/Jetty будуть відповідні записи типу `INFO ... New comment added ...`.

### Запуск застосунку

У каталозі `frworks`:

```bash
mvn jetty:run
```

Після успішного старту Jetty застосунок буде доступний за адресою:

- **http://localhost:8080/** — головна сторінка з формою та списком відгуків
- **http://localhost:8080/comments** — JSON API для отримання списку відгуків

### Збірка WAR-файлу

Для створення WAR-файлу:

```bash
mvn package
```

Результат: `target/frworks.war`

