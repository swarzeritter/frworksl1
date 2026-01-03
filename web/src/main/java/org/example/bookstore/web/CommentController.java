package org.example.bookstore.web;

import org.example.bookstore.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String createComment(@RequestParam Long bookId,
                                @RequestParam String author,
                                @RequestParam String text) {
        commentService.createComment(bookId, author, text);
        return "redirect:/book-details/" + bookId;
    }
}
