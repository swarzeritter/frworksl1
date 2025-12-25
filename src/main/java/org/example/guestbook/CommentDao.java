package org.example.guestbook;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {
    private static final String JDBC_URL = "jdbc:h2:file:/data/guest;AUTO_SERVER=TRUE";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 driver not found", e);
        }
        initSchema();
    }

    private static void initSchema() {
        String ddl = """
                create table if not exists comments (
                    id bigint generated always as identity primary key,
                    author varchar(64) not null,
                    text varchar(1000) not null,
                    created_at timestamp not null
                )
                """;
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement st = c.createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize schema", e);
        }
    }

    public List<Comment> findAllDesc() throws SQLException {
        String sql = "select id, author, text, created_at from comments order by id desc";
        List<Comment> result = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Comment cmt = new Comment(
                        rs.getLong("id"),
                        rs.getString("author"),
                        rs.getString("text"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                result.add(cmt);
            }
        }
        return result;
    }

    public Comment insert(String author, String text) throws SQLException {
        String sql = "insert into comments(author, text, created_at) values (?, ?, ?)";
        LocalDateTime now = LocalDateTime.now();
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author);
            ps.setString(2, text);
            ps.setTimestamp(3, Timestamp.valueOf(now));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                long id = 0;
                if (keys.next()) {
                    id = keys.getLong(1);
                }
                return new Comment(id, author, text, now);
            }
        }
    }
}

