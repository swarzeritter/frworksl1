package org.example.guestbook;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "IndexServlet", urlPatterns = {"/"})
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");

        PrintWriter out = resp.getWriter();
        out.println("""
                <!doctype html>
                <html lang="uk">
                <head>
                    <meta charset="UTF-8">
                    <title>Книга відгуків</title>
                    <style>
                        body { font-family: system-ui, sans-serif; background: #f3f4f6; margin: 0; padding: 0; }
                        .container { max-width: 800px; margin: 40px auto; background: #ffffff; padding: 24px 28px; border-radius: 12px; box-shadow: 0 10px 30px rgba(15, 23, 42, 0.1); }
                        h1 { margin-top: 0; font-size: 26px; color: #111827; }
                        p.desc { margin-top: 4px; color: #6b7280; }
                        form { margin-top: 24px; display: grid; grid-template-columns: 1fr; gap: 16px; }
                        label { font-weight: 600; color: #374151; font-size: 14px; }
                        input[type="text"], textarea {
                            width: 100%; padding: 10px 12px; border-radius: 8px;
                            border: 1px solid #d1d5db; font: inherit; resize: vertical; min-height: 44px;
                        }
                        input[type="text"]:focus, textarea:focus {
                            outline: none; border-color: #2563eb; box-shadow: 0 0 0 1px #2563eb33;
                        }
                        button {
                            justify-self: start;
                            background: #2563eb; color: #ffffff; border: none;
                            padding: 10px 18px; border-radius: 999px; font-weight: 600;
                            cursor: pointer; font-size: 14px; display: inline-flex; align-items: center; gap: 8px;
                        }
                        button:hover { background: #1d4ed8; }
                        button:active { transform: translateY(1px); }
                        .status { margin-top: 10px; font-size: 14px; min-height: 18px; }
                        .status.ok { color: #16a34a; }
                        .status.err { color: #dc2626; }
                        .list-header { margin-top: 32px; display: flex; align-items: baseline; justify-content: space-between; gap: 8px; }
                        .list-header h2 { margin: 0; font-size: 20px; color: #111827; }
                        .list-header span.count { font-size: 13px; color: #6b7280; }
                        ul#comments { list-style: none; padding: 0; margin: 16px 0 0; display: flex; flex-direction: column; gap: 12px; }
                        .comment { padding: 12px 14px; border-radius: 10px; background: #f9fafb; border: 1px solid #e5e7eb; }
                        .comment-header { display: flex; justify-content: space-between; align-items: baseline; gap: 8px; }
                        .comment-author { font-weight: 600; color: #111827; font-size: 14px; }
                        .comment-date { font-size: 12px; color: #9ca3af; }
                        .comment-text { margin-top: 6px; white-space: pre-wrap; font-size: 14px; color: #374151; }
                        .empty { margin-top: 8px; font-size: 14px; color: #9ca3af; }
                    </style>
                </head>
                <body>
                <div class="container">
                    <h1>Книга відгуків</h1>
                    <p class="desc">Введіть ім'я та текст відгуку. Нові записи з'являються зверху.</p>

                    <form id="comment-form">
                        <div>
                            <label for="author">Автор (обов'язково, ≤ 64 символи)</label>
                            <input type="text" id="author" name="author" maxlength="64" required>
                        </div>
                        <div>
                            <label for="text">Текст (обов'язково, ≤ 1000 символів)</label>
                            <textarea id="text" name="text" rows="4" maxlength="1000" required></textarea>
                        </div>
                        <button type="submit">Додати відгук</button>
                        <div id="status" class="status"></div>
                    </form>

                    <div class="list-header">
                        <h2>Відгуки</h2>
                        <span id="count" class="count"></span>
                    </div>
                    <ul id="comments"></ul>
                    <div id="empty" class="empty" style="display: none;">Ще немає жодного відгуку.</div>
                </div>

                <script>
                    async function loadComments() {
                        try {
                            const resp = await fetch('comments');
                            if (!resp.ok) {
                                throw new Error('HTTP ' + resp.status);
                            }
                            const data = await resp.json();
                            console.log('Loaded comments:', data);
                            const list = document.getElementById('comments');
                            const empty = document.getElementById('empty');
                            const countSpan = document.getElementById('count');

                            list.innerHTML = '';
                            if (!data || data.length === 0) {
                                empty.style.display = 'block';
                                countSpan.textContent = '0 записів';
                                return;
                            }

                            empty.style.display = 'none';
                            countSpan.textContent = data.length + ' запис(и)';

                            for (const c of data) {
                                try {
                                    const li = document.createElement('li');
                                    li.className = 'comment';
                                    const header = document.createElement('div');
                                    header.className = 'comment-header';
                                    const author = document.createElement('span');
                                    author.className = 'comment-author';
                                    author.textContent = c.author || '(без імені)';
                                    const date = document.createElement('span');
                                    date.className = 'comment-date';
                                    if (c.createdAt) {
                                        const dateStr = typeof c.createdAt === 'string' 
                                            ? c.createdAt.replace('T', ' ').substring(0, 16)
                                            : new Date(c.createdAt).toISOString().replace('T', ' ').substring(0, 16);
                                        date.textContent = dateStr;
                                    } else {
                                        date.textContent = '';
                                    }
                                    header.appendChild(author);
                                    header.appendChild(date);

                                    const text = document.createElement('div');
                                    text.className = 'comment-text';
                                    text.textContent = c.text || '';

                                    li.appendChild(header);
                                    li.appendChild(text);
                                    list.appendChild(li);
                                } catch (err) {
                                    console.error('Error rendering comment:', c, err);
                                }
                            }
                        } catch (e) {
                            console.error('Error loading comments:', e);
                            const status = document.getElementById('status');
                            if (status) {
                                status.textContent = 'Помилка завантаження відгуків: ' + e.message;
                                status.classList.add('err');
                            }
                        }
                    }

                    document.getElementById('comment-form').addEventListener('submit', async (e) => {
                        e.preventDefault();
                        const status = document.getElementById('status');
                        status.textContent = '';
                        status.className = 'status';

                        const form = e.target;
                        const formData = new FormData(form);

                        const author = formData.get('author')?.trim();
                        const text = formData.get('text')?.trim();
                        if (!author || author.length > 64 || !text || text.length > 1000) {
                            status.textContent = 'Перевірте правильність введених даних.';
                            status.classList.add('err');
                            return;
                        }

                        try {
                            const params = new URLSearchParams();
                            params.append('author', author);
                            params.append('text', text);
                            
                            const resp = await fetch('comments', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                                },
                                body: params.toString()
                            });
                            if (resp.status === 204) {
                                status.textContent = 'Відгук збережено.';
                                status.classList.add('ok');
                                form.reset();
                                await loadComments();
                            } else if (resp.status === 400) {
                                status.textContent = 'Помилка валідації (400).';
                                status.classList.add('err');
                            } else {
                                status.textContent = 'Помилка сервера (' + resp.status + ').';
                                status.classList.add('err');
                            }
                        } catch (err) {
                            status.textContent = 'Помилка мережі.';
                            status.classList.add('err');
                        }
                    });

                    loadComments();
                </script>
                </body>
                </html>
                """);
    }
}

