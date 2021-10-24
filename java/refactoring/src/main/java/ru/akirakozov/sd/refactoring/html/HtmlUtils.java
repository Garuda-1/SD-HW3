package ru.akirakozov.sd.refactoring.html;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HtmlUtils {

    public static void formProductListResponse(ResultSet resultSet, HttpServletResponse response,
                                               @Nullable String header) throws SQLException, IOException {
        StringBuilder bodyBuilder = new StringBuilder();
        if (header != null) {
            bodyBuilder.append("<h1>").append(header).append("</h1>\n");
        }
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            int price  = resultSet.getInt("price");
            bodyBuilder.append(name).append("\t").append(price).append("</br>\n");
        }
        formHtmlResponse(response, wrapHtmlBody(bodyBuilder.toString()));
    }

    public static void formScalarResponse(ResultSet resultSet, HttpServletResponse response,
                                            String description) throws SQLException, IOException {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append(description);
        if (resultSet.next()) {
            bodyBuilder.append(resultSet.getInt(1));
        }
        formHtmlResponse(response, wrapHtmlBody(bodyBuilder.toString()));
    }

    public static void formHtmlResponse(HttpServletResponse response, String contents) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.println(contents);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private static String wrapHtmlBody(String body) {
        return "<html><body>\n" + body + "</body></html>\n";
    }
}
