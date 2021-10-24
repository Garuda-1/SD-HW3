package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.DatabaseUtils;

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
            DatabaseUtils.executeQuery("SELECT * FROM Product ORDER BY price DESC LIMIT 1", resultSet -> {
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h1>Product with max price: </h1>");
                while (resultSet.next()) {
                    String  name = resultSet.getString("name");
                    int price  = resultSet.getInt("price");
                    response.getWriter().println(name + "\t" + price + "</br>");
                }
                response.getWriter().println("</body></html>");
            });
        } else if ("min".equals(command)) {
            DatabaseUtils.executeQuery("SELECT * FROM Product ORDER BY price LIMIT 1", resultSet -> {
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h1>Product with min price: </h1>");
                while (resultSet.next()) {
                    String  name = resultSet.getString("name");
                    int price  = resultSet.getInt("price");
                    response.getWriter().println(name + "\t" + price + "</br>");
                }
                response.getWriter().println("</body></html>");
            });
        } else if ("sum".equals(command)) {
            DatabaseUtils.executeQuery("SELECT SUM(price) FROM Product", resultSet -> {
                response.getWriter().println("<html><body>");
                response.getWriter().println("Summary price: ");
                if (resultSet.next()) {
                    response.getWriter().println(resultSet.getInt(1));
                }
                response.getWriter().println("</body></html>");
            });
        } else if ("count".equals(command)) {
            DatabaseUtils.executeQuery("SELECT COUNT(*) FROM Product", resultSet -> {
                response.getWriter().println("<html><body>");
                response.getWriter().println("Number of products: ");
                if (resultSet.next()) {
                    response.getWriter().println(resultSet.getInt(1));
                }
                response.getWriter().println("</body></html>");
            });
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
