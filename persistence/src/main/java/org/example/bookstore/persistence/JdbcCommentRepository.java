package org.example.bookstore.persistence;

import org.example.bookstore.domain.Comment;
import org.example.bookstore.port.CommentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcCommentRepository implements CommentRepositoryPort {
    private static final Logger log = LoggerFactory.getLogger(JdbcCommentRepository.class);
    private static final String JDBC_URL = "jdbc:h2:file:/data/bookstore;AUTO_SERVER=TRUE";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    static {
        initSchema();
    }

    private static void initSchema() {
        String ddl = """
                create table if not exists comments (
                    id bigint generated always as identity primary key,
                    book_id bigint not null,
                    author varchar(64) not null,
                    text varchar(1000) not null,
                    created_at timestamp not null,
                    foreign key (book_id) references books(id) on delete cascade
                );
                """;
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement st = c.createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize schema", e);
        }
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        String sql = "select id, book_id, author, text, created_at from comments where book_id = ? order by id desc";
        List<Comment> comments = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment(
                            rs.getLong("id"),
                            rs.getLong("book_id"),
                            rs.getString("author"),
                            rs.getString("text"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find comments by book id: " + bookId, e);
            throw new RuntimeException("Database error", e);
        }
        return comments;
    }

    @Override
    public Comment save(Comment comment) {
        String sql = "insert into comments(book_id, author, text, created_at) values (?, ?, ?, ?)";
        LocalDateTime now = comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now();
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, comment.getBookId());
            ps.setString(2, comment.getAuthor());
            ps.setString(3, comment.getText());
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    comment.setId(keys.getLong(1));
                    comment.setCreatedAt(now);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to save comment", e);
            throw new RuntimeException("Database error", e);
        }
        return comment;
    }

    @Override
    public Comment findById(Long id) {
        String sql = "select id, book_id, author, text, created_at from comments where id = ?";
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Comment(
                            rs.getLong("id"),
                            rs.getLong("book_id"),
                            rs.getString("author"),
                            rs.getString("text"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find comment by id: " + id, e);
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from comments where id = ?";
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Comment not found");
            }
        } catch (SQLException e) {
            log.error("Failed to delete comment: " + id, e);
            throw new RuntimeException("Database error", e);
        }
    }
}

