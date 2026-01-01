# Bookstore Application (Spring MVC + Thymeleaf)

Веб-застосунок "Каталог книг", реалізований на **Spring Boot** та **Thymeleaf**.

## Структура проєкту

- **core**: Доменна логіка та сервіси.
- **persistence**: Репозиторії даних (Spring JDBC).
- **web**: MVC Контролери та HTML-шаблони (Thymeleaf).

## Технології

- Java 21
- Spring Boot 3.x (Web, Thymeleaf, JDBC)
- H2 Database (Embedded)
- Bootstrap 5 (UI Styles)

## Запуск

Застосунок використовує стандартний плагін Spring Boot.

### Через Maven
```bash
cd frworksl1/web
mvn spring-boot:run
```

Після запуску відкрийте браузер: http://localhost:8080/books

## Функціональність UI

- **Перегляд списку книг**: `/books`
- **Деталі книги та відгуки**: `/book-details/{id}`
- **Додавання книги**: `/books/add`
- **Зміна мови**: Кнопки UA/EN у хедері (параметр `?lang=en`).

## Особливості
- Серверний рендеринг (SSR).
- Інтернаціоналізація інтерфейсу (i18n).
- Використання фрагментів для перевикористання коду (Header, Navbar).
