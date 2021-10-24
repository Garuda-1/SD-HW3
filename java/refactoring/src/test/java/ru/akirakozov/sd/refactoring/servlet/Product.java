package ru.akirakozov.sd.refactoring.servlet;

import java.util.Objects;

public class Product {

    private final String productName;
    private final long productPrice;

    public Product(String productName, long productPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public long getProductPrice() {
        return productPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productPrice == product.productPrice && Objects.equals(productName, product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productPrice);
    }
}
