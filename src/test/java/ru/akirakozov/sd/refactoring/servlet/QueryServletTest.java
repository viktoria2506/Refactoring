package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.product.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.akirakozov.sd.refactoring.dao.DaoUtils.createTables;

public class QueryServletTest {
    private AbstractProductServlet servlet;
    private final ProductDao productDao = mock(ProductDao.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final StringWriter writer = new StringWriter();
    private final QueryServlet queryServlet = new QueryServlet(productDao);

    private static final String DB_ADDRESS = "jdbc:sqlite:test.db";
    private String joinResult(String res) {
        return "<html><body>\n" + res + "</body></html>\n";
    }

    @BeforeEach
    public void setup() throws IOException, SQLException {
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        servlet = new QueryServlet(productDao);
    }

    @Test
    public void testMax() throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("max");
        when(productDao.findMaxPriceProduct())
                .thenReturn(Optional.of(new Product("bla", 1)));
        servlet.doGet(request, response);

        assertEquals(joinResult("<h1>Product with max price: </h1>\nbla	1</br>\n"), writer.toString());
    }

    @Test
    public void testMin() throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("min");
        when(productDao.findMinPriceProduct())
                .thenReturn(Optional.of(new Product("null", -1)));
        queryServlet.doGet(request, response);

        assertEquals(joinResult("<h1>Product with min price: </h1>\nnull	-1</br>\n"), writer.toString());
    }

    @Test
    public void testSum() throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("sum");
        queryServlet.doGet(request, response);

        assertEquals(joinResult(String.format("Summary price: \n0\n")), writer.toString());
    }

    @Test
    public void testCount() throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("count");
        when(productDao.getProductsCount())
                .thenReturn(3);
        servlet.doGet(request, response);

        assertEquals(joinResult(String.format("Number of products: \n3\n")), writer.toString());
    }

    @Test
    public void testQueryUnknownCommand() throws IOException {
        when(request.getParameter("command")).thenReturn("unknown");
        servlet.doGet(request, response);

        assertEquals("Unknown command: unknown\n", writer.toString());
    }

    @Test
    public void testQueryNullCommand() throws IOException {
        servlet.doGet(request, response);

        assertEquals("Unknown command: null\n", writer.toString());
    }
}
