package org.example.guestbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CommentsServlet", urlPatterns = "/comments")
public class CommentsServlet extends HttpServlet {

    private final CommentDao dao = new CommentDao();
    private final ObjectMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);

    public CommentsServlet() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            List<Comment> comments = dao.findAllDesc();
            mapper.writeValue(resp.getOutputStream(), comments);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getOutputStream(), new ErrorResponse("Database error"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        log.debug("POST /comments - author: '{}', text length: {}", author, text != null ? text.length() : 0);

        if (!isValid(author, 64) || !isValid(text, 1000)) {
            String textPreview = text != null && text.length() > 0 
                ? text.substring(0, Math.min(50, text.length())) 
                : (text != null ? "(empty)" : "(null)");
            log.warn("Validation failed - author: '{}', text: '{}'", author, textPreview);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Comment saved = dao.insert(author.trim(), text.trim());
            // Required: one INFO log after successful insertion
            log.info("New comment added: id={}, author='{}', length={}", saved.getId(), saved.getAuthor(), saved.getText().length());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
        } catch (SQLException e) {
            log.error("Failed to insert comment", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValid(String value, int maxLen) {
        if (value == null) return false;
        String trimmed = value.trim();
        return !trimmed.isEmpty() && trimmed.length() <= maxLen;
    }

    private record ErrorResponse(String message) {
    }
}

