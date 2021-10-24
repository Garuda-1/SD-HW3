package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.DatabaseUtils;
import ru.akirakozov.sd.refactoring.html.HtmlUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        if ("max".equals(command)) {
            DatabaseUtils.executeQuery("SELECT * FROM Product ORDER BY price DESC LIMIT 1", resultSet ->
                    HtmlUtils.formProductListResponse(resultSet, response, "Product with max price: "));
        } else if ("min".equals(command)) {
            DatabaseUtils.executeQuery("SELECT * FROM Product ORDER BY price LIMIT 1", resultSet ->
                    HtmlUtils.formProductListResponse(resultSet, response, "Product with min price: "));
        } else if ("sum".equals(command)) {
            DatabaseUtils.executeQuery("SELECT SUM(price) FROM Product", resultSet ->
                    HtmlUtils.formScalarResponse(resultSet, response, "Summary price: "));
        } else if ("count".equals(command)) {
            DatabaseUtils.executeQuery("SELECT COUNT(*) FROM Product", resultSet ->
                    HtmlUtils.formScalarResponse(resultSet, response, "Number of products: "));
        } else {
            HtmlUtils.formHtmlResponse(response, "Unknown command: " + command);
        }
    }
}
