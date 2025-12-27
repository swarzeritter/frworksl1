package org.example.bookstore.persistence;

import org.example.bookstore.domain.Comment;
import org.example.bookstore.port.CommentRepositoryPort;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcCommentRepository implements CommentRepositoryPort {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initSchema();
    }

    private void initSchema() {
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
        jdbcTemplate.execute(ddl);
    }

    private final RowMapper<Comment> commentRowMapper = (rs, rowNum) -> new Comment(
            rs.getLong("id"),
            rs.getLong("book_id"),
            rs.getString("author"),
            rs.getString("text"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    @Override
    public List<Comment> findByBookId(Long bookId) {
        String sql = "select id, book_id, author, text, created_at from comments where book_id = ? order by id desc";
        return jdbcTemplate.query(sql, commentRowMapper, bookId);
    }

    @Override
    public Comment save(Comment comment) {
        String sql = "insert into comments(book_id, author, text, created_at) values (?, ?, ?, ?)";
        LocalDateTime now = comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now();
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getBookId());
            ps.setString(2, comment.getAuthor());
            ps.setString(3, comment.getText());
            ps.setTimestamp(4, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            comment.setId(keyHolder.getKey().longValue());
            comment.setCreatedAt(now);
        }
        
        return comment;
    }

    @Override
    public Comment findById(Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from comments where id = ?", commentRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        int rowsAffected = jdbcTemplate.update("delete from comments where id = ?", id);
        if (rowsAffected == 0) {
            throw new IllegalArgumentException("Comment not found");
        }
    }
}
