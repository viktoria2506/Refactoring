package ru.akirakozov.sd.refactoring.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.product.Product;

/**
 * @author akirakozov
 */
public class AddProductServlet extends AbstractProductServlet {
    public AddProductServlet(ProductDao productDao) {
        super(productDao);
    }

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = request.getParameter("name");
        long price = Long.parseLong(request.getParameter("price"));

        productDao.addProduct(new Product(name, price));

        response.getWriter().println("OK");
    }
}
