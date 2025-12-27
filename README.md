# Bookstore Application (Spring Boot / MVC)

Веб-застосунок "Каталог книг", побудований на Spring Boot з використанням архітектури Spring MVC.

## Структура проєкту

- **core**: Доменна логіка, сервіси (`@Service`), моделі.
- **persistence**: Репозиторії даних (`@Repository`, `JdbcTemplate`).
- **web**: REST контролери (`@RestController`), конфігурація MVC (`WebMvcConfigurer`), обробка помилок.

## Технології

- Java 21
- Spring Boot 3.2.0 (Web, JDBC)
- Spring MVC (DispatcherServlet, REST)
- Jakarta Validation (Hibernate Validator)
- H2 Database
- Maven

## Запуск

### Через Maven
```bash
cd frworksl1
# Запустити з кореневої папки (модуль web)
mvn spring-boot:run -pl web
```

### Як JAR файл
```bash
mvn clean package
java -jar web/target/web-1.0-SNAPSHOT.jar
```

Додаток доступний за адресою: http://localhost:8080

## API Endpoints

- **GET /books** - Список книг (з пагінацією: `?page=0&size=10`)
- **GET /books/{id}** - Книга за ID
- **GET /book-details/{id}** - Книга з коментарями
- **GET /comments?bookId={id}** - Коментарі до книги
- **POST /comments** - Створити коментар
  - Body JSON: `{"bookId": 1, "author": "User", "text": "Comment text"}`
- **DELETE /comments/{id}** - Видалити коментар
- **GET /version** - Версія додатку

## Конфігурація
- `web/src/main/resources/application.properties` - Основні налаштування.
- `org.example.bookstore.config.WebConfig` - Налаштування Spring MVC (CORS тощо).
