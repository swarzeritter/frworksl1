package org.example.bookstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.json.JsonMapper;
import org.example.bookstore.domain.Book;
import org.example.bookstore.domain.Comment;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.persistence.JdbcCatalogRepository;
import org.example.bookstore.persistence.JdbcCommentRepository;
import org.example.bookstore.service.CatalogService;
import org.example.bookstore.service.CommentService;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavalinBookApp {

    public static void main(String[] args) {
        // 1. Manual Dependency Injection (Composition Root)
        
        // Database Configuration
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:file:/data/bookstore;AUTO_SERVER=TRUE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        
        // Infrastructure (Persistence)
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcCatalogRepository catalogRepository = new JdbcCatalogRepository(jdbcTemplate);
        JdbcCommentRepository commentRepository = new JdbcCommentRepository(jdbcTemplate);
        
        // Domain Services
        CatalogService catalogService = new CatalogService(catalogRepository);
        CommentService commentService = new CommentService(commentRepository);

        // 2. Javalin Configuration
        // Configure Jackson for Java 8 Date/Time
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonMapper jsonMapper = new JsonMapper() {
            @NotNull
            @Override
            public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                try {
                    return objectMapper.writeValueAsString(obj);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @NotNull
            @Override
            public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                try {
                    return objectMapper.readValue(json, objectMapper.constructType(targetType));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(jsonMapper);
            
            config.requestLogger.http((ctx, ms) -> {
                System.out.println(ctx.method() + " " + ctx.path() + " took " + ms + " ms");
            });
            // CORS configuration if needed
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        });

        // 3. Middleware (Example: Log request headers)
        app.before(ctx -> {
            System.out.println("Incoming request: " + ctx.fullUrl());
        });

        // 4. Routes
        
        // GET /books
        app.get("/books", ctx -> {
            int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
            int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(10);
            String q = ctx.queryParam("q");
            String sort = ctx.queryParamAsClass("sort", String.class).getOrDefault("id");
            
            PageRequest pageRequest = new PageRequest(page, size, sort);
            ctx.json(catalogService.findBooks(q, pageRequest));
        });

        // GET /books/{id}
        app.get("/books/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            Book book = catalogService.findBookById(id);
            if (book == null) {
                ctx.status(HttpStatus.NOT_FOUND);
            } else {
                ctx.json(book);
            }
        });

        // GET /book-details/{id}
        app.get("/book-details/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            Book book = catalogService.findBookById(id);
            if (book == null) {
                ctx.status(HttpStatus.NOT_FOUND);
                return;
            }
            List<Comment> comments = commentService.findCommentsByBookId(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("book", book);
            response.put("comments", comments);
            ctx.json(response);
        });

        // GET /comments
        app.get("/comments", ctx -> {
            Long bookId = ctx.queryParamAsClass("bookId", Long.class).get();
            ctx.json(commentService.findCommentsByBookId(bookId));
        });

        // POST /comments
        app.post("/comments", ctx -> {
            CreateCommentRequest request = ctx.bodyAsClass(CreateCommentRequest.class);
            // Basic validation
            if (request.bookId == null || request.author == null || request.text == null) {
                throw new IllegalArgumentException("Missing required fields");
            }
            
            Comment comment = commentService.createComment(request.bookId, request.author, request.text);
            ctx.status(HttpStatus.CREATED).json(comment);
        });

        // DELETE /comments/{id}
        app.delete("/comments/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            commentService.deleteComment(id);
            ctx.status(HttpStatus.NO_CONTENT);
        });

        // 5. Exception Handling
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", "Bad Request", "message", e.getMessage()));
        });

        app.exception(IllegalStateException.class, (e, ctx) -> {
            ctx.status(HttpStatus.CONFLICT).json(Map.of("error", "Conflict", "message", e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace(); // Log stack trace
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("error", "Internal Server Error", "message", "Something went wrong"));
        });

        // Start server
        app.start(8080);
    }

    // DTO
    public record CreateCommentRequest(Long bookId, String author, String text) {}
}
