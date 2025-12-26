package org.example.bookstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bookstore.domain.Book;
import org.example.bookstore.domain.Comment;
import org.example.bookstore.service.CatalogService;
import org.example.bookstore.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "BookDetailsServlet", urlPatterns = "/book-details/*")
public class BookDetailsServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BookDetailsServlet.class);
    private final CatalogService catalogService;
    private final CommentService commentService;
    private final ObjectMapper mapper;

    public BookDetailsServlet() {
        // Note: In a real application, this would use dependency injection
        var catalogRepository = new org.example.bookstore.persistence.JdbcCatalogRepository();
        var commentRepository = new org.example.bookstore.persistence.JdbcCommentRepository();
        this.catalogService = new CatalogService(catalogRepository);
        this.commentService = new CommentService(commentRepository);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.startsWith("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Invalid path"));
            return;
        }

        // Expected path: /{id}
        String idStr = pathInfo.substring(1);
        if (idStr.isEmpty()) {
             resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Missing book ID"));
             return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            Book book = catalogService.findBookById(id);
            
            if (book == null) {
                log.warn("Book not found: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Not Found", "Book not found"));
                return;
            }

            List<Comment> comments = commentService.findCommentsByBookId(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("book", book);
            response.put("comments", comments);
            
            mapper.writeValue(resp.getOutputStream(), response);
        } catch (NumberFormatException e) {
            log.warn("Invalid book ID: {}", idStr);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Invalid book ID"));
        } catch (IllegalArgumentException e) {
            log.warn("Bad request: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Internal Server Error", "An error occurred"));
        }
    }
}
