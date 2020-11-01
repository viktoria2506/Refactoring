package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.product.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.akirakozov.sd.refactoring.dao.DaoUtils.createTables;

public class GetProductsServletTest {
    private AbstractProductServlet servlet;
    private final ProductDao productDao = mock(ProductDao.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final StringWriter writer = new StringWriter();

    @BeforeEach
    public void setup() throws SQLException, IOException {
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        servlet = new GetProductsServlet(productDao);
        createTables();
    }

    @Test
    public void OK() throws Exception {
        when(productDao.getProducts())
                .thenReturn(Arrays.asList(
                        new Product("bla", 1),
                        new Product("aaa", 0),
                        new Product("bb", -1)
                ));

        servlet.doGet(request, response);

        assertEquals("<html><body>\n" +
                        "bla	1</br>\n" +
                        "aaa	0</br>\n" +
                        "bb	-1</br>\n" +
                        "</body></html>\n"
                , writer.toString());
    }

    @Test
    public void OKEmpty() throws IOException {
        servlet.doGet(request, response);
        assertEquals("<html><body>\n" +
                        "</body></html>\n"
                , writer.toString());
    }
}
