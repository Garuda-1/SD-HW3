package ru.akirakozov.sd.refactoring.servlet;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

class ProductServletIntegrationTests extends AbstractProductServletTest {

    @Test
    @DisplayName("Add one product")
    public void addOneProduct() throws Exception {
        addProducts(new Product("iphone6", 300));
    }

    @Test
    @DisplayName("Get empty products list")
    public void getProductsEmpty() throws Exception {
        assertThat(getProducts()).isEmpty();
    }

    @Test
    @DisplayName("Add 1 product and get")
    public void addOneProductAndGet() throws Exception {
        Product product = new Product("iphone6", 300);
        addProducts(product);
        List<Product> products = getProducts();
        assertThat(products.size()).isEqualTo(1);
        assertThat(products.get(0)).isEqualTo(product);
    }

    @Test
    @DisplayName("Add 2 equal products")
    public void addTwoEqualProducts() throws Exception {
        Product product = new Product("iphone6", 300);
        addProducts(product, product);
        List<Product> products = getProducts();
        assertThat(products.size()).isEqualTo(2);
        assertThat(products.get(0)).isEqualTo(product);
        assertThat(products.get(1)).isEqualTo(product);
    }

    @Test
    @DisplayName("Add 2 distinct products")
    public void addTwoDistinctProducts() throws Exception {
        Product product1 = new Product("iphone6", 300);
        Product product2 = new Product("iphone7", 400);
        addProducts(product1, product2);
        List<Product> products = getProducts();
        assertThat(products.size()).isEqualTo(2);
        assertThat(products).containsExactly(product1, product2);
    }

    @Test
    @DisplayName("Query command 'min'")
    public void queryCommandMin() throws Exception {
        Product product1 = new Product("iphone6", 300);
        Product product2 = new Product("iphone7", 400);
        Product product3 = new Product("iphone8", 500);
        Stream<Product> products = Stream.of(product1, product2, product3);
        addProducts(product1, product2, product3);
        Product minProduct = queryProduct("min");
        assertThat(minProduct).isNotNull();
        assertThat(minProduct).isEqualTo(
                products.min(Comparator.comparingLong(Product::getProductPrice)).orElseThrow());
    }

    @Test
    @DisplayName("Query command 'max'")
    public void queryCommandMax() throws Exception {
        Product product1 = new Product("iphone6", 300);
        Product product2 = new Product("iphone7", 400);
        Product product3 = new Product("iphone8", 500);
        Stream<Product> products = Stream.of(product1, product2, product3);
        addProducts(product1, product2, product3);
        Product minProduct = queryProduct("max");
        assertThat(minProduct).isNotNull();
        assertThat(minProduct).isEqualTo(
                products.max(Comparator.comparingLong(Product::getProductPrice)).orElseThrow());
    }

    @Test
    @DisplayName("Query command 'sum'")
    public void queryCommandSum() throws Exception {
        Product product1 = new Product("iphone6", 300);
        Product product2 = new Product("iphone7", 400);
        Product product3 = new Product("iphone8", 500);
        Stream<Product> products = Stream.of(product1, product2, product3);
        addProducts(product1, product2, product3);
        long sum = queryScalar("sum");
        assertThat(sum).isEqualTo(products.mapToLong(Product::getProductPrice).sum());
    }

    @Test
    @DisplayName("Query command 'count'")
    public void queryCommandCount() throws Exception {
        Product product1 = new Product("iphone6", 300);
        Product product2 = new Product("iphone7", 400);
        Product product3 = new Product("iphone8", 500);
        Stream<Product> products = Stream.of(product1, product2, product3);
        addProducts(product1, product2, product3);
        long count = queryScalar("count");
        assertThat(count).isEqualTo(products.count());
    }

    @Test
    @DisplayName("Query unknown command")
    public void queryUnknownCommand() throws Exception {
        makeRequest("/query?command=foo", responseEntity -> {
            try {
                String line = getLinesFromBody(EntityUtils.toString(responseEntity)).get(0);
                assertThat(line.trim()).isEqualTo("Unknown command: foo");
            } catch (Exception e) {
                fail();
            }
        });
    }
}