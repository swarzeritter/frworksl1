# Bookstore Application (Lab 9)

Багатомодульний Spring Boot проєкт ("Bookstore"), що демонструє використання сучасних технологій Java для веб-розробки.

## Модулі

*   **core**: Бізнес-логіка, доменні сутності (JPA Entities), порти (інтерфейси), сервіси.
*   **persistence**: Реалізація доступу до даних (Adapters), Spring Data JPA Repositories, Flyway міграції.
*   **web**: Веб-інтерфейс (Spring MVC, Thymeleaf), REST контролери, конфігурація безпеки.

## Технології (Lab 9 Update)

*   **Spring Security**: Автентифікація та авторизація (Login, Register, Roles).
*   **Spring Boot 3.x**
*   **Spring Data JPA / Hibernate**: ORM для роботи з базою даних.
*   **H2 Database**: Вбудована база даних.
*   **Flyway**: Версіонування БД.
*   **Thymeleaf + Extras**: Серверний рендеринг HTML з підтримкою Security тегів.
*   **FreeMarker**: Генерація листів.

## Запуск

1.  Переконайтеся, що у вас встановлено JDK 17+ та Maven.
2.  Перейдіть у кореневу директорію проєкту:
    ```bash
    cd frworksl1
    ```
3.  Встановіть залежності та зберіть модулі:
    ```bash
    mvn clean install
    ```
4.  Перейдіть у модуль `web` та запустіть застосунок:
    ```bash
    cd web
    mvn spring-boot:run
    ```

## Функціонал

*   **Безпека**:
    *   Реєстрація нових користувачів (роль USER за замовчуванням).
    *   Логін/Логаут.
    *   Захист сторінок та API на основі ролей (ADMIN/USER).
*   **Книги**: Перегляд (всі), Додавання (тільки ADMIN).
*   **Коментарі**: Додавання коментарів до книг.
*   **Email**: Сповіщення про нові книги.

## Тестові користувачі

У системі вже попередньо створено користувачів (через міграцію Flyway):

| Роль | Логін | Пароль |
| :--- | :--- | :--- |
| **ADMIN** | `admin` | `password` |
| **USER** | `user` | `password` |

Ви також можете зареєструвати власного користувача через форму `/register` (отримає роль USER).

## База даних

*   **Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
*   **JDBC URL**: `jdbc:h2:file:./bookstore_v2;AUTO_SERVER=TRUE`
*   **User**: `sa`
*   **Password**: (пусто)
