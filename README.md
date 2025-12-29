# Bookstore Application (Javalin Edition)

Веб-застосунок "Каталог книг", реалізований на мікрофреймворку **Javalin**.

## Структура проєкту

- **core**: Доменна логіка та сервіси (не змінено).
- **persistence**: Репозиторії даних на Spring JDBC (не змінено).
- **web**: Реалізація REST API на Javalin (без Spring Web/MVC).

## Технології

- Java 21
- Javalin 6.1.3
- Jetty (Embedded)
- Jackson (JSON)
- JDBC / H2 Database
- HikariCP (Connection Pooling)

## Запуск

Оскільки ми не використовуємо Spring Boot плагін для запуску (хоча батьківський pom - Spring Boot), найкращий спосіб запустити додаток - через IDE або зібравши JAR.

### Через Maven (Exec)
```bash
cd frworksl1/web
mvn compile exec:java
```

### API Endpoints

Ті ж самі, що і в попередніх лабах:
- **GET /books**
- **GET /book-details/{id}**
- **GET /comments**
- **POST /comments**
- **DELETE /comments/{id}**

## Особливості реалізації
- Всі залежності впроваджуються вручну в `JavalinBookApp.main()`.
- Використовується `application/json` для обміну даними.
- Глобальна обробка помилок через `app.exception()`.
