package ru.akirakozov.sd.refactoring.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        Properties properties = new Properties();
        properties.setProperty("user","postgres");
        properties.setProperty("password","postgres");

        if ("max".equals(command)) {
            try {
                try (Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/products",
                        properties)) {
                    Statement stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM Product ORDER BY price DESC LIMIT 1");
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("<h1>Product with max price: </h1>");

                    while (rs.next()) {
                        String  name = rs.getString("name");
                        int price  = rs.getInt("price");
                        response.getWriter().println(name + "\t" + price + "</br>");
                    }
                    response.getWriter().println("</body></html>");

                    rs.close();
                    stmt.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("min".equals(command)) {
            try {
                try (Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/products",
                        properties)) {
                    Statement stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM Product ORDER BY price LIMIT 1");
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("<h1>Product with min price: </h1>");

                    while (rs.next()) {
                        String  name = rs.getString("name");
                        int price  = rs.getInt("price");
                        response.getWriter().println(name + "\t" + price + "</br>");
                    }
                    response.getWriter().println("</body></html>");

                    rs.close();
                    stmt.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("sum".equals(command)) {
            try {
                try (Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/products",
                        properties)) {
                    Statement stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT SUM(price) FROM Product");
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("Summary price: ");

                    if (rs.next()) {
                        response.getWriter().println(rs.getInt(1));
                    }
                    response.getWriter().println("</body></html>");

                    rs.close();
                    stmt.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("count".equals(command)) {
            try {
                try (Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/products",
                        properties)) {
                    Statement stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Product");
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("Number of products: ");

                    if (rs.next()) {
                        response.getWriter().println(rs.getInt(1));
                    }
                    response.getWriter().println("</body></html>");

                    rs.close();
                    stmt.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
