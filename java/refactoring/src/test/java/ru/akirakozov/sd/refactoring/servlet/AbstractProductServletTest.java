package ru.akirakozov.sd.refactoring.servlet;

import com.google.common.flogger.FluentLogger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.akirakozov.sd.refactoring.Main;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractProductServletTest {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @Nullable private Thread serverThread;

    @BeforeEach
    public void beforeEach() throws Exception {
        purgeProductTable();
        Main.serverStarted.set(false);
        startServer();
    }

    @AfterEach
    public void afterEach() {
        stopServer();
    }

    void addProducts(Product... products) throws Exception {
        for (Product product : products) {
            makeRequest(String.format("/add-product?name=%s&price=%d", product.getProductName(), product.getProductPrice()),
                    responseEntity -> {
                        try {
                            assertThat(EntityUtils.toString(responseEntity)).isEqualTo("OK\n");
                        } catch (Exception e) {
                            fail();
                        }
                    });
        }
    }

    List<Product> getProducts() throws Exception {
        List<Product> products = new ArrayList<>();
        makeRequest("/get-products", responseEntity -> {
            try {
                getLinesFromBody(EntityUtils.toString(responseEntity)).forEach(line -> {
                    String[] tokens = line.split("\t");
                    String productName = tokens[0].trim();
                    long productPrice = Long.parseLong(tokens[1].trim());
                    products.add(new Product(productName, productPrice));
                });
            } catch (Exception e) {
                fail();
            }
        });
        return products;
    }

    @Nullable Product queryProduct(String command) throws Exception {
        final AtomicReference<Product> result = new AtomicReference<>();
        makeRequest(String.format("/query?command=%s", command), responseEntity -> {
            try {
                String line = getLinesFromBody(EntityUtils.toString(responseEntity)).get(0);
                String[] tokens = line.split("\t");
                String productName = tokens[0].trim();
                long productPrice = Long.parseLong(tokens[1].trim());
                result.set(new Product(productName, productPrice));
            } catch (Exception e) {
                fail();
            }
        });
        return result.get();
    }

    long queryScalar(String command) throws Exception {
        final AtomicReference<Long> result = new AtomicReference<>();
        makeRequest(String.format("/query?command=%s", command), responseEntity -> {
            try {
                String line = getLinesFromBody(EntityUtils.toString(responseEntity)).get(0);
                String[] tokens = line.split(":");
                result.set(Long.parseLong(tokens[1].trim()));
            } catch (Exception e) {
                fail();
            }
        });
        return result.get();
    }

    void makeRequest(String endpoint, Consumer<HttpEntity> httpResponseConsumer) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8081/" + endpoint);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                httpResponseConsumer.accept(response.getEntity());
            }
        }
    }

    List<String> getLinesFromBody(String html) {
        Document document = Jsoup.parse(html);
        return document.body().childNodes().stream()
                .filter(node -> node instanceof TextNode)
                .map(node -> ((TextNode) node).getWholeText())
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    private void startServer() {
        serverThread = new Thread(() -> {
            try {
                Main.main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        logger.atInfo().log("Starting server at thread id=%d, waiting for startup", serverThread.getId());
        synchronized (Main.serverStarted) {
            while (!Main.serverStarted.get()) {
                try {
                    Main.serverStarted.wait(3000);
                    logger.atInfo().log("Server started");
                } catch (InterruptedException e) {
                    logger.atWarning().log("Server start was interrupted");
                    serverThread.interrupt();
                    throw new RuntimeException("Start interrupted");
                }
            }
        }
    }

    private void stopServer() {
        assert serverThread != null;
        serverThread.interrupt();
        logger.atInfo().log("Interrupting server thread, waiting to join");
        try {
            serverThread.join();
            logger.atInfo().log("Server thread terminated");
        } catch (InterruptedException e) {
            logger.atWarning().log("Interrupted, no longer waiting for joining a server thread");
        }
    }

    private void purgeProductTable() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user","postgres");
        properties.setProperty("password","postgres");
        try (Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/products", properties)) {
            String sql = "DROP TABLE IF EXISTS Product";
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        }
        logger.atInfo().log("Table '%s' purged", "products");
    }
}
