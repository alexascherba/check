package ru.clevertec.check.service;

import ru.clevertec.check.model.Product;
import ru.clevertec.check.model.DiscountCard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileReader {
    public static List<Product> readProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("id")) {
                    continue;
                }
                String[] values = line.split(";");
                Product product = new Product();
                product.setId(Integer.parseInt(values[0]));
                product.setDescription(values[1]);
                product.setPrice(Double.parseDouble(values[2]));
                product.setQuantityInStock(Integer.parseInt(values[3]));
                product.setWholesale(Boolean.parseBoolean(values[4]));
                products.add(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static List<DiscountCard> readDiscountCards(String filePath) {
        List<DiscountCard> discountCards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean skipHeader = true;
            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
                String[] values = line.split(";");
                if (values.length != 3) {
                    throw new IOException("Invalid CSV format");
                }
                DiscountCard discountCard = new DiscountCard();
                discountCard.setId(Integer.parseInt(values[0]));
                discountCard.setNumber(Integer.parseInt(values[1]));
                discountCard.setDiscountAmount(Double.parseDouble(values[2]));
                discountCards.add(discountCard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return discountCards;
    }
}