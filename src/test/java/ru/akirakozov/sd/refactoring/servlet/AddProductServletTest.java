package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static ru.akirakozov.sd.refactoring.dao.DaoUtils.createTables;

public class AddProductServletTest {
    private AbstractProductServlet servlet;
    private final ProductDao productDao = mock(ProductDao.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final StringWriter writer = new StringWriter();

    @BeforeEach
    public void setup() throws IOException, SQLException {
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        createTables();
        servlet = new AddProductServlet(productDao);
    }

    @Test
    public void OK() throws IOException, SQLException {
        when(request.getParameter("name")).thenReturn("bla");
        when(request.getParameter("price")).thenReturn("1");
        PrintWriter printer = new PrintWriter(writer);
        when(response.getWriter())
                .thenReturn(printer);
        servlet.doGet(request, response);

        assertEquals("OK\n", writer.toString());
    }

    @Test
    public void runtimeException() {
        when(request.getParameter("name")).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> servlet.doGet(request, response));
        assertEquals("", writer.toString());
    }

    @Test
    public void numberFormatException() {
        assertThrows(NumberFormatException.class, () -> servlet.doRequest(request, response));
        assertEquals("", writer.toString());

        when(request.getParameter("name")).thenReturn("bla");

        assertThrows(NumberFormatException.class, () -> servlet.doRequest(request, response));
        assertEquals("", writer.toString());

        when(request.getParameter("price")).thenReturn("Not a number");

        assertThrows(NumberFormatException.class, () -> servlet.doRequest(request, response));
        assertEquals("", writer.toString());
    }
}
