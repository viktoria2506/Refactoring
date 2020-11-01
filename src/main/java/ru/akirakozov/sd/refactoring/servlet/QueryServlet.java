package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.product.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static ru.akirakozov.sd.refactoring.html.ProductHTML.printInfoHTML;
import static ru.akirakozov.sd.refactoring.html.ProductHTML.printProductHTML;

/**
 * @author akirakozov
 */
public class QueryServlet extends AbstractProductServlet  {
    public QueryServlet(ProductDao productDao) {
        super(productDao);
    }

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            Optional<Product> maxPriceProduct = productDao.findMaxPriceProduct();
            printProductHTML(maxPriceProduct, "Product with max price: ", response.getWriter());
        } else if ("min".equals(command)) {
            Optional<Product> minPriceProduct = productDao.findMinPriceProduct();
            printProductHTML(minPriceProduct, "Product with min price: ", response.getWriter());
        } else if ("sum".equals(command)) {
            long summaryPrice = productDao.getPricesSum();
            printInfoHTML(summaryPrice, "Summary price: ", response.getWriter());
        } else if ("count".equals(command)) {
            int count = productDao.getProductsCount();
            printInfoHTML(count, "Number of products: ", response.getWriter());
        } else {
            response.getWriter().println("Unknown command: " + command);
        }
    }

}
