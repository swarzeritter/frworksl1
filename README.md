# Bookstore Application (Spring Boot Edition)

Веб-застосунок "Каталог книг", мігрований на Spring Boot.

## Структура проєкту

- **core**: Доменна логіка та сервіси (`@Service`)
- **persistence**: Репозиторії даних (`@Repository`, `JdbcTemplate`)
- **web**: REST контролери (`@RestController`) та точка входу (`@SpringBootApplication`)

## Технології

- Java 21
- Spring Boot 3.2.0 (Web, JDBC)
- H2 Database
- Maven

## Запуск

### Через Maven
```bash
cd frworksl1/web
mvn spring-boot:run
```

### Як JAR файл
```bash
mvn clean package
java -jar web/target/web-1.0-SNAPSHOT.jar
```

Додаток доступний за адресою: http://localhost:8080

## API Endpoints

- **GET /books** - Список книг
- **GET /books/{id}** - Книга за ID
- **GET /book-details/{id}** - Книга з коментарями
- **GET /comments?bookId={id}** - Коментарі до книги
- **POST /comments** - Створити коментар (Body JSON: `{"bookId": 1, "author": "...", "text": "..."}`)
- **DELETE /comments/{id}** - Видалити коментар
- **GET /version** - Версія додатку (демо @Bean)

## Конфігурація
Налаштування знаходяться в `web/src/main/resources/application.properties`.
