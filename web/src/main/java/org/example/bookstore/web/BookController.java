package org.example.bookstore.web;

import org.example.bookstore.domain.Book;
import org.example.bookstore.domain.Comment;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.service.CatalogService;
import org.example.bookstore.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookController {

    private final CatalogService catalogService;
    private final CommentService commentService;

    // Field injection example
    @Autowired
    private String appVersion;

    public BookController(CatalogService catalogService, CommentService commentService) {
        this.catalogService = catalogService;
        this.commentService = commentService;
    }

    @GetMapping("/version")
    @ResponseBody
    public String getVersion() {
        return "App Version: " + appVersion;
    }

    @GetMapping("/books")
    public String getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            Model model) {
        
        PageRequest pageRequest = new PageRequest(page, size, sort);
        Page<Book> bookPage = catalogService.findBooks(q, pageRequest);
        model.addAttribute("books", bookPage.getContent());
        return "books";
    }

    // Redirect /books/{id} to details view or generic error if needed
    // But we use /book-details/{id} in templates
    @GetMapping("/books/{id}")
    public String getBook(@PathVariable Long id, Model model) {
        return "redirect:/book-details/" + id;
    }

    @GetMapping("/book-details/{id}")
    public String getBookDetails(@PathVariable Long id, Model model) {
        Book book = catalogService.findBookById(id);
        if (book == null) {
             // Ideally show 404 page
             return "redirect:/books"; 
        }
        List<Comment> comments = commentService.findCommentsByBookId(id);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "book-details";
    }
    
    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @PostMapping("/books/add")
    public String saveBook(@ModelAttribute Book book) {
        catalogService.saveBook(book);
        return "redirect:/books";
    }
}
