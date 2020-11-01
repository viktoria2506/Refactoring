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

public class QueryServletTest {
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final StringWriter writer = new StringWriter();
    private final QueryServlet queryServlet = new QueryServlet();

    private static final String DB_ADDRESS = "jdbc:sqlite:test.db";
    private static final String DB = "insert into product(name, price) values" +
                        "('bla', '1')," +
                        "('apple', '0')," +
                        "('null', '-1')";

    private String joinResult(String res) {
        return "<html><body>\n" + res + "</body></html>\n";

    }

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
    public void testMax() throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("max");
        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            connection.prepareStatement(DB).execute();
        }
        queryServlet.doGet(request, response);
        assertEquals(joinResult("<h1>Items with max price: </h1>\nbla	1</br>\n"), writer.toString());
    }

    @Test
    public void testMin() throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("min");
        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            connection.prepareStatement(DB).execute();
        }
        queryServlet.doGet(request, response);
        assertEquals(joinResult("<h1>Items with min price: </h1>\nnull	-1</br>\n"), writer.toString());
    }

    @Test
    public void testSum() throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("sum");
        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            connection.prepareStatement(DB).execute();
        }
        queryServlet.doGet(request, response);
        assertEquals(joinResult(String.format("Summary price: \n0\n")), writer.toString());
    }

    @Test
    public void testCount() throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("count");
        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            connection.prepareStatement(DB).execute();
        }
        queryServlet.doGet(request, response);
        assertEquals(joinResult(String.format("Number of products: \n3\n")), writer.toString());
    }

    @Test
    public void testQueryUnknownCommand() throws IOException {
        when(request.getParameter("command")).thenReturn("unknown");
        queryServlet.doGet(request, response);
        assertEquals("Unknown command: unknown\n", writer.toString());
    }

    @Test
    public void testQueryNullCommand() throws IOException {
        queryServlet.doGet(request, response);
        assertEquals("Unknown command: null\n", writer.toString());
    }
}
