# Bookstore Application (Spring MVC + Thymeleaf + FreeMarker Mail)

Веб-застосунок "Каталог книг" з функцією email-сповіщень.

## Структура проєкту

- **core**: Доменна логіка.
- **persistence**: Репозиторії даних.
- **web**: 
  - MVC Контролери та UI на **Thymeleaf**.
  - Email-сервіс на **FreeMarker** (Lab 7).

## Технології

- Java 21
- Spring Boot 3.x
- Thymeleaf (Web UI)
- FreeMarker (Email Templates)
- JavaMailSender (SMTP)
- H2 Database

## Функціональність

1. **Каталог книг**: Перегляд, пошук, сортування (`/books`).
2. **Деталі**: Перегляд інформації та відгуків (`/book-details/{id}`).
3. **Додавання**: Форма додавання нової книги (`/books/add`).
4. **Email-сповіщення**: При додаванні книги адміністратор отримує HTML-лист з деталями.
   - Підтримка умовного форматування (раритетні книги).
   - Стилізований шаблон з логотипом.

## Запуск

Перед запуском налаштуйте SMTP у `src/main/resources/application.properties`:
```properties
spring.mail.username=ВАШ_GMAIL
spring.mail.password=ВАШ_APP_PASSWORD
```

Запуск через Maven:
```bash
cd frworksl1/web
mvn spring-boot:run
```
