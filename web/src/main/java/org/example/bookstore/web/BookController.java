package org.example.bookstore.web;

import org.example.bookstore.domain.Book;
import org.example.bookstore.domain.Comment;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.service.CatalogService;
import org.example.bookstore.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BookController {

    private final CatalogService catalogService;
    private final CommentService commentService;

    // Field injection example (as requested by lab requirements)
    @Autowired
    private String appVersion;

    public BookController(CatalogService catalogService, CommentService commentService) {
        this.catalogService = catalogService;
        this.commentService = commentService;
    }

    @GetMapping("/version")
    public String getVersion() {
        return "App Version: " + appVersion;
    }

    @GetMapping("/books")
    public Page<Book> getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        PageRequest pageRequest = new PageRequest(page, size, sort);
        return catalogService.findBooks(q, pageRequest);
    }

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable Long id) {
        return catalogService.findBookById(id);
    }

    @GetMapping("/book-details/{id}")
    public Map<String, Object> getBookDetails(@PathVariable Long id) {
        Book book = catalogService.findBookById(id);
        List<Comment> comments = commentService.findCommentsByBookId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("book", book);
        response.put("comments", comments);
        return response;
    }
}



