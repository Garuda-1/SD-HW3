package ru.akirakozov.sd.refactoring;

import com.google.common.flogger.FluentLogger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.akirakozov.sd.refactoring.database.DatabaseUtils;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author akirakozov
 */
public class Main {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static final AtomicBoolean serverStarted = new AtomicBoolean(false);

    public static void main(String[] args) throws Exception {
        logger.atInfo().log("Starting server...");

        DatabaseUtils.ensureProductTableExists();

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet()), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet()),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet()),"/query");

        server.start();
        logger.atInfo().log("Server started, notifying");
        synchronized (serverStarted) {
            serverStarted.set(true);
            serverStarted.notify();
        }
        try {
            server.join();
        } catch (InterruptedException e) {
            logger.atInfo().log("Server thread interrupted, terminating...");
            server.stop();
        } finally {
            logger.atInfo().log("Server thread terminated");
        }
    }
}
