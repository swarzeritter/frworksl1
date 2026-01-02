<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>New Book Added</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 5px; overflow: hidden; }
        .header { background-color: #f8f9fa; padding: 20px; text-align: center; border-bottom: 1px solid #ddd; }
        .content { padding: 20px; }
        .footer { background-color: #343a40; color: white; padding: 15px; text-align: center; font-size: 12px; }
        .book-card { background-color: #fff; border: 1px solid #e9ecef; border-radius: 5px; padding: 15px; margin-top: 15px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
        .rare-badge { display: inline-block; background-color: #ffd700; color: #856404; padding: 5px 10px; border-radius: 15px; font-weight: bold; font-size: 0.9em; margin-top: 10px; border: 1px solid #ffeeba; }
        .label { font-weight: bold; color: #555; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <!-- Logo from GitHub as requested -->
            <img src="https://raw.githubusercontent.com/VladPiatachenko/MPF_Labs/lab7-freemaker/web/src/main/resources/static/img/logo.png" alt="BookApp Logo" style="max-height: 60px;">
            <h2 style="margin: 10px 0 0;">Нова книга в каталозі!</h2>
        </div>
        <div class="content">
            <p>Вітаємо! До нашого каталогу було додано нову книгу.</p>
            
            <div class="book-card">
                <h3 style="margin-top: 0; color: #007bff;">${title}</h3>
                <p><span class="label">Автор:</span> ${author}</p>
                <p><span class="label">Рік видання:</span> ${year! "Не вказано"}</p>
                <p><span class="label">Додано:</span> ${added}</p>
                
                <#if year?? && year < 2000>
                    <div class="rare-badge">★ Раритетне видання!</div>
                </#if>
            </div>
            
            <p style="margin-top: 20px;">Переглянути деталі можна на нашому сайті.</p>
        </div>
        <div class="footer">
            <p>BookApp © 2026</p>
            <p>Ви отримали цей лист, оскільки підписані на сповіщення про нові надходження.</p>
        </div>
    </div>
</body>
</html>

