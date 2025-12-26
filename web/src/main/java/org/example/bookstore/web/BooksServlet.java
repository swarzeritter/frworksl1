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
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "BooksServlet", urlPatterns = "/books/*")
public class BooksServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BooksServlet.class);
    private final CatalogService catalogService;
    private final ObjectMapper mapper;

    public BooksServlet() {
        // Note: In a real application, this would use dependency injection
        var catalogRepository = new org.example.bookstore.persistence.JdbcCatalogRepository();
        this.catalogService = new CatalogService(catalogRepository);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /books - список книг
                handleListBooks(req, resp);
            } else {
                // GET /books/{id} - книга за ID
                handleGetBook(pathInfo, resp);
            }
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

    private void handleListBooks(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("q");
        String pageParam = req.getParameter("page");
        String sizeParam = req.getParameter("size");
        String sort = req.getParameter("sort");

        int page = pageParam != null ? Integer.parseInt(pageParam) : 0;
        int size = sizeParam != null ? Integer.parseInt(sizeParam) : 10;

        PageRequest pageRequest = new PageRequest(page, size, sort);
        Page<Book> books = catalogService.findBooks(query, pageRequest);
        
        mapper.writeValue(resp.getOutputStream(), books);
    }

    private void handleGetBook(String pathInfo, HttpServletResponse resp) throws IOException {
        String idStr = pathInfo.substring(1); // Remove leading /
        try {
            Long id = Long.parseLong(idStr);
            Book book = catalogService.findBookById(id);
            
            if (book == null) {
                log.warn("Book not found: {}", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Not Found", "Book not found"));
            } else {
                mapper.writeValue(resp.getOutputStream(), book);
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid book ID: {}", idStr);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Invalid book ID"));
        }
    }
}

