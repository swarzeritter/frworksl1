package org.example.bookstore.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.bookstore.domain.Comment;
import org.example.bookstore.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<Comment> getComments(@RequestParam Long bookId) {
        return commentService.findCommentsByBookId(bookId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(request.bookId(), request.author(), request.text());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

    // DTO Record for JSON input with Validation
    public record CreateCommentRequest(
            @NotNull(message = "Book ID is required") Long bookId,
            @NotBlank(message = "Author is required") @Size(max = 64, message = "Author name too long") String author,
            @NotBlank(message = "Text is required") @Size(max = 1000, message = "Text too long") String text
    ) {}
}



