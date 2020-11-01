package ru.akirakozov.sd.refactoring.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.akirakozov.sd.refactoring.dao.ProductDao;

import static ru.akirakozov.sd.refactoring.html.ProductHTML.printProductsHTML;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends AbstractProductServlet {
    public GetProductsServlet(ProductDao productDao) {
        super(productDao);
    }

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        printProductsHTML(productDao.getProducts(), response.getWriter());
    }
}
