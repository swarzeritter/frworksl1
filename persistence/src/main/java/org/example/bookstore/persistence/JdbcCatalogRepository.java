package org.example.bookstore.persistence;

import org.example.bookstore.domain.Book;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.port.CatalogRepositoryPort;
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
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcCatalogRepository implements CatalogRepositoryPort {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCatalogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initSchema(); 
        initSampleData();
    }

    private void initSchema() {
        // Drop table for clean state in lab environment to ensure new columns are added
        jdbcTemplate.execute("DROP TABLE IF EXISTS books CASCADE");
        
        String ddl = """
                create table if not exists books (
                    id bigint generated always as identity primary key,
                    title varchar(255) not null,
                    author varchar(255) not null,
                    isbn varchar(20) not null,
                    description varchar(2000),
                    year_published int,
                    added_at timestamp
                );
                """;
        jdbcTemplate.execute(ddl);
    }
    
    private void initSampleData() {
        Long count = jdbcTemplate.queryForObject("select count(*) from books", Long.class);
        if (count != null && count == 0) {
            String sql = "insert into books(title, author, isbn, description, year_published, added_at) values (?, ?, ?, ?, ?, ?)";
            LocalDateTime now = LocalDateTime.now();
            jdbcTemplate.update(sql, "Spring Boot in Action", "Craig Walls", "9781617292545", "Spring Boot in Action is a developer-focused guide.", 2016, Timestamp.valueOf(now));
            jdbcTemplate.update(sql, "Cloud Native Java", "Josh Long", "9781449374648", "Cloud Native Java demonstrates how to build systems.", 2017, Timestamp.valueOf(now));
            jdbcTemplate.update(sql, "Java Concurrency in Practice", "Brian Goetz", "9780321349606", "Java Concurrency in Practice.", 2006, Timestamp.valueOf(now)); // Rare book (< 2010)
        }
    }

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getString("description"),
            rs.getObject("year_published") != null ? rs.getInt("year_published") : null
        );
        Timestamp addedAt = rs.getTimestamp("added_at");
        if (addedAt != null) {
            book.setAddedAt(addedAt.toLocalDateTime());
        }
        return book;
    };

    @Override
    public Page<Book> findAll(String query, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("select * from books");
        StringBuilder countSql = new StringBuilder("select count(*) from books");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            String whereClause = " where lower(title) like ? or lower(author) like ? or lower(isbn) like ?";
            sql.append(whereClause);
            countSql.append(whereClause);
            String searchPattern = "%" + query.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (pageRequest.getSort() != null) {
            String sortColumn = "title".equals(pageRequest.getSort()) ? "title" : "author".equals(pageRequest.getSort()) ? "author" : "id";
            sql.append(" order by ").append(sortColumn);
        } else {
            sql.append(" order by id");
        }

        sql.append(" limit ? offset ?");
        params.add(pageRequest.getSize());
        params.add(pageRequest.getOffset());

        List<Book> content = jdbcTemplate.query(sql.toString(), bookRowMapper, params.toArray());

        // Count query params (without limit/offset)
        List<Object> countParams = params.subList(0, params.size() - 2);
        Long totalElements = jdbcTemplate.queryForObject(countSql.toString(), Long.class, countParams.toArray());

        return new Page<>(content, pageRequest.getPage(), pageRequest.getSize(), totalElements != null ? totalElements : 0);
    }

    @Override
    public Book findById(Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from books where id = ?", bookRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            String sql = "insert into books(title, author, isbn, description, year_published, added_at) values (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, book.getTitle());
                ps.setString(2, book.getAuthor());
                ps.setString(3, book.getIsbn());
                ps.setString(4, book.getDescription());
                if (book.getYear() != null) {
                    ps.setInt(5, book.getYear());
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }
                ps.setTimestamp(6, Timestamp.valueOf(book.getAddedAt()));
                return ps;
            }, keyHolder);
            book.setId(keyHolder.getKey().longValue());
        } else {
            String sql = "update books set title = ?, author = ?, isbn = ?, description = ?, year_published = ? where id = ?";
            jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getIsbn(), book.getDescription(), book.getYear(), book.getId());
        }
        return book;
    }
}
