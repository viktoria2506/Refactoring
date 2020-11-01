package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetProductsServletTest {
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final StringWriter writer = new StringWriter();
    private final GetProductsServlet getProductsServlet = new GetProductsServlet();

    private static final String DB_ADDRESS = "jdbc:sqlite:test.db";

    @BeforeEach
    public void setup() throws IOException, SQLException {
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            final String query = "drop table if exists product";
            connection.prepareStatement(query).execute();
        }

        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            final String query = "create table if not exists product(" +
                    "id integer primary key autoincrement not null," +
                    "name text not null," +
                    "price int not null)";
            connection.prepareStatement(query).execute();
        }
    }

    @Test
    public void OK() throws SQLException, IOException {
        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            final String query = "insert into product(name, price) values" +
                    "('bla', '1')," +
                    "('aaa', '0')," +
                    "('bb', '-1')";
            connection.prepareStatement(query).execute();
        }
        getProductsServlet.doGet(request, response);
        assertEquals("<html><body>\n" +
                "<h1>All items that we have: </h1>\n" +
                        "bla	1</br>\n" +
                        "aaa	0</br>\n" +
                        "bb	-1</br>\n" +
                        "</body></html>\n"
                , writer.toString());
    }

    @Test
    public void OKEmpty() throws IOException {
        getProductsServlet.doGet(request, response);
        assertEquals("<html><body>\n" +
                        "<h1>All items that we have: </h1>\n" +
                        "</body></html>\n"
                , writer.toString());
    }
}
