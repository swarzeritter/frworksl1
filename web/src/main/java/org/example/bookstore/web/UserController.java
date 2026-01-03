package org.example.bookstore.web;

import org.example.bookstore.domain.CommentEntity;
import org.example.bookstore.domain.UserEntity;
import org.example.bookstore.service.CommentService;
import org.example.bookstore.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class UserController {

    private final CommentService commentService;
    private final UserService userService;

    public UserController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/users/{id}/comments")
    public String userComments(@PathVariable Long id, Model model) {
        UserEntity user = userService.findById(id);
        List<CommentEntity> comments = commentService.getCommentsByUser(id);
        
        model.addAttribute("username", user.getUsername());
        model.addAttribute("comments", comments);
        
        return "user-comments";
    }
}


