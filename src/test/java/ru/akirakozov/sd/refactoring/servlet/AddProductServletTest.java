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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AddProductServletTest {
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final StringWriter writer = new StringWriter();
    private final AddProductServlet addProductServlet = new AddProductServlet();

    private static final String DB_ADDRESS = "jdbc:sqlite:test.db";

    @BeforeEach
    public void setup() throws IOException, SQLException {
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            final String query = "drop table if exists product";
            connection.prepareStatement(query).execute();
        }

        try (final Connection connection = DriverManager.getConnection(DB_ADDRESS)) {
            final String query = " create table if not exists product(" +
                        "id integer primary key autoincrement not null," +
                        "name text not null," +
                        "price int not null)";
            connection.prepareStatement(query).execute();
        }
    }

    @Test
    public void OK() throws IOException {
        when(request.getParameter("name")).thenReturn("bla");
        when(request.getParameter("price")).thenReturn("1");
        addProductServlet.doGet(request, response);
        assertEquals("OK\n", writer.toString());
    }

    @Test
    public void runtimeException() {
        when(request.getParameter("name")).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> addProductServlet.doGet(request, response));
        assertEquals("", writer.toString());
    }

    @Test
    public void numberFormatException() {
        assertThrows(NumberFormatException.class, () -> addProductServlet.doGet(request, response));
        assertEquals("", writer.toString());

        when(request.getParameter("name")).thenReturn("bla");
        assertThrows(NumberFormatException.class, () -> addProductServlet.doGet(request, response));
        assertEquals("", writer.toString());

        when(request.getParameter("price")).thenReturn("not a number");
        assertThrows(NumberFormatException.class, () -> addProductServlet.doGet(request, response));
        assertEquals("", writer.toString());
    }
}
