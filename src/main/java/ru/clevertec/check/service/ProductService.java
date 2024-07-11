package ru.clevertec.check.service;

import ru.clevertec.check.model.Product;

import java.util.List;

public class ProductService {
    private List<Product> products;

    public ProductService() {
        this.products = CsvFileReader.readProducts("./src/main/resources/products.csv");
    }

    public Product getProductById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
}