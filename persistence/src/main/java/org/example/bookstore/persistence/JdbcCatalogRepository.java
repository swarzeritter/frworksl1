package org.example.bookstore.persistence;

import org.example.bookstore.domain.Book;
import org.example.bookstore.domain.Page;
import org.example.bookstore.domain.PageRequest;
import org.example.bookstore.port.CatalogRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCatalogRepository implements CatalogRepositoryPort {
    private static final Logger log = LoggerFactory.getLogger(JdbcCatalogRepository.class);
    private static final String JDBC_URL = "jdbc:h2:file:/data/bookstore;AUTO_SERVER=TRUE";
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
                create table if not exists books (
                    id bigint generated always as identity primary key,
                    title varchar(255) not null,
                    author varchar(255) not null,
                    isbn varchar(20) not null,
                    description varchar(2000)
                );
                """;
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement st = c.createStatement()) {
            st.executeUpdate(ddl);
            initSampleData(c);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize schema", e);
        }
    }

    private static void initSampleData(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            ResultSet rs = st.executeQuery("select count(*) from books");
            if (rs.next() && rs.getInt(1) == 0) {
                st.executeUpdate("""
                    insert into books (title, author, isbn, description) values
                    ('Effective Java', 'Joshua Bloch', '978-0134685991', 'A programming guide by Java platform architect Joshua Bloch'),
                    ('Clean Code', 'Robert C. Martin', '978-0132350884', 'A Handbook of Agile Software Craftsmanship'),
                    ('Design Patterns', 'Gang of Four', '978-0201633610', 'Elements of Reusable Object-Oriented Software'),
                    ('Java Concurrency in Practice', 'Brian Goetz', '978-0321349606', 'A comprehensive guide to concurrent programming in Java'),
                    ('Refactoring', 'Martin Fowler', '978-0134757599', 'Improving the Design of Existing Code')
                    """);
            }
        }
    }

    @Override
    public Page<Book> findAll(String query, PageRequest pageRequest) {
        List<Book> books = new ArrayList<>();
        long totalElements = 0;

        String whereClause = "";
        if (query != null && !query.trim().isEmpty()) {
            whereClause = " where lower(title) like ? or lower(author) like ? or lower(isbn) like ?";
        }

        String countSql = "select count(*) from books" + whereClause;
        String selectSql = "select id, title, author, isbn, description from books" + whereClause;

        String sortClause = "";
        if (pageRequest.getSort() != null && !pageRequest.getSort().trim().isEmpty()) {
            String sort = pageRequest.getSort().trim();
            if (sort.equals("title") || sort.equals("author")) {
                sortClause = " order by " + sort;
            }
        } else {
            sortClause = " order by id";
        }
        selectSql += sortClause;
        selectSql += " limit ? offset ?";

        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            // Count total
            try (PreparedStatement ps = c.prepareStatement(countSql)) {
                if (!whereClause.isEmpty()) {
                    String searchPattern = "%" + query.toLowerCase() + "%";
                    ps.setString(1, searchPattern);
                    ps.setString(2, searchPattern);
                    ps.setString(3, searchPattern);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalElements = rs.getLong(1);
                    }
                }
            }

            // Select page
            try (PreparedStatement ps = c.prepareStatement(selectSql)) {
                int paramIndex = 1;
                if (!whereClause.isEmpty()) {
                    String searchPattern = "%" + query.toLowerCase() + "%";
                    ps.setString(paramIndex++, searchPattern);
                    ps.setString(paramIndex++, searchPattern);
                    ps.setString(paramIndex++, searchPattern);
                }
                ps.setInt(paramIndex++, pageRequest.getSize());
                ps.setInt(paramIndex, pageRequest.getOffset());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Book book = new Book(
                                rs.getLong("id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("isbn"),
                                rs.getString("description")
                        );
                        books.add(book);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find books", e);
            throw new RuntimeException("Database error", e);
        }

        return new Page<>(books, pageRequest.getPage(), pageRequest.getSize(), totalElements);
    }

    @Override
    public Book findById(Long id) {
        String sql = "select id, title, author, isbn, description from books where id = ?";
        try (Connection c = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find book by id: " + id, e);
            throw new RuntimeException("Database error", e);
        }
        return null;
    }
}

