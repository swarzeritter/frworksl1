package org.example.bookstore.web;

import org.example.bookstore.domain.BookEntity;
import org.example.bookstore.domain.CommentEntity;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.service.CatalogService;
import org.example.bookstore.service.CommentService;
import org.example.bookstore.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookController {

    private final CatalogService catalogService;
    private final CommentService commentService;
    private final MailService mailService;

    @Autowired
    private String appVersion;

    public BookController(CatalogService catalogService, CommentService commentService, MailService mailService) {
        this.catalogService = catalogService;
        this.commentService = commentService;
        this.mailService = mailService;
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
        Page<BookEntity> bookPage = catalogService.findBooks(q, pageRequest);
        model.addAttribute("books", bookPage.getContent());
        return "books";
    }

    @GetMapping("/books/{id}")
    public String getBook(@PathVariable Long id) {
        return "redirect:/book-details/" + id;
    }

    @GetMapping("/book-details/{id}")
    public String getBookDetails(@PathVariable Long id, Model model) {
        BookEntity book = catalogService.findBookById(id);
        if (book == null) {
             return "redirect:/books"; 
        }
        List<CommentEntity> comments = commentService.findCommentsByBookId(id);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "book-details";
    }
    
    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new BookEntity());
        return "book-form";
    }

    @PostMapping("/books/add")
    public String saveBook(@ModelAttribute BookEntity book) {
        catalogService.saveBook(book);
        
        // Lab 7: Send email notification
        mailService.sendNewBookEmail(book);
        
        return "redirect:/books";
    }
}
