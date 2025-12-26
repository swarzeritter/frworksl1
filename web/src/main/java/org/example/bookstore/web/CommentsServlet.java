package org.example.bookstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bookstore.domain.Comment;
import org.example.bookstore.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "CommentsServlet", urlPatterns = "/comments/*")
public class CommentsServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);
    private final CommentService commentService;
    private final ObjectMapper mapper;

    public CommentsServlet() {
        var commentRepository = new org.example.bookstore.persistence.JdbcCommentRepository();
        this.commentService = new CommentService(commentRepository);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        String bookIdStr = req.getParameter("bookId");
        if (bookIdStr == null || bookIdStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Missing bookId parameter"));
            return;
        }
        
        try {
            Long bookId = Long.parseLong(bookIdStr);
            var comments = commentService.findCommentsByBookId(bookId);
            mapper.writeValue(resp.getOutputStream(), comments);
        } catch (NumberFormatException e) {
            log.warn("Invalid book ID: {}", bookIdStr);
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        req.setCharacterEncoding("UTF-8");
        
        String bookIdStr = req.getParameter("bookId");
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        log.info("Received POST request: bookId={}, author={}, textLength={}", bookIdStr, author, text != null ? text.length() : 0);

        if (bookIdStr == null || bookIdStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Missing bookId parameter"));
            return;
        }

        try {
            Long bookId = Long.parseLong(bookIdStr);
            Comment comment = commentService.createComment(bookId, author, text);
            
            log.info("Comment created: id={}, bookId={}, author='{}', length={}", 
                    comment.getId(), comment.getBookId(), comment.getAuthor(), comment.getText().length());
            
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getOutputStream(), comment);
        } catch (NumberFormatException e) {
            log.warn("Invalid book ID: {}", bookIdStr);
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        // Path format: /comments/{commentId}
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.startsWith("/")) {
             resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Invalid path"));
             return;
        }

        String commentIdStr = pathInfo.substring(1);
        if (commentIdStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Missing comment ID"));
            return;
        }
        
        try {
            Long commentId = Long.parseLong(commentIdStr);
            commentService.deleteComment(commentId);
            
            log.info("Comment deleted: id={}", commentId);
            
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            log.warn("Invalid comment ID: {}", commentIdStr);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", "Invalid comment ID"));
        } catch (IllegalArgumentException e) {
            log.warn("Bad request: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Bad Request", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("Conflict: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Conflict", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Internal Server Error", "An error occurred"));
        }
    }
}
