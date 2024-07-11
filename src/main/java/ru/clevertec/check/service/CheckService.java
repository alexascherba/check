package ru.clevertec.check.service;

import ru.clevertec.check.exception.BadRequestException;
import ru.clevertec.check.exception.NotEnoughMoneyException;
import ru.clevertec.check.exception.InternalServerErrorException;
import ru.clevertec.check.model.Check;
import ru.clevertec.check.model.Product;
import ru.clevertec.check.model.DiscountCard;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CheckService {
    private ProductService productService;
    private DiscountCardService discountCardService;

    public CheckService() {
        this.productService = new ProductService();
        this.discountCardService = new DiscountCardService();
    }

    public Check generateCheck(String[] args) {
        Map<Integer, Integer> productQuantities = new HashMap<>();
        DiscountCard discountCard = null;
        double balanceDebitCard = 0;
        Check check = new Check();

        try {
            for (String arg : args) {
                if (arg.startsWith("discountCard=")) {
                    int cardNumber = Integer.parseInt(arg.split("=")[1]);
                    discountCard = discountCardService.getDiscountCardByNumber(cardNumber);
                } else if (arg.startsWith("balanceDebitCard=")) {
                    balanceDebitCard = Double.parseDouble(arg.split("=")[1]);
                } else {
                    String[] productInfo = arg.split("-");
                    if (productInfo.length != 2) {
                        throw new BadRequestException("Invalid product format.");
                    }
                    int id = Integer.parseInt(productInfo[0]);
                    int quantity = Integer.parseInt(productInfo[1]);
                    if (quantity <= 0) {
                        throw new BadRequestException("Quantity must be greater than zero.");
                    }
                    productQuantities.put(id, productQuantities.getOrDefault(id, 0) + quantity);
                }
            }

            if (productQuantities.isEmpty()) {
                throw new BadRequestException("No products in the check.");
            }

            List<Product> products = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                Product product = productService.getProductById(entry.getKey());
                if (product == null) {
                    throw new BadRequestException("Product with ID " + entry.getKey() + " not found.");
                }
                for (int i = 0; i < entry.getValue(); i++) {
                    products.add(product);
                }
            }

            check.setProducts(products);
            check.setDiscountCard(discountCard);
            check.setBalanceDebitCard(balanceDebitCard);
            calculateCheckTotals(check);

            if (check.getTotalWithDiscount() > balanceDebitCard) {
                throw new NotEnoughMoneyException("Not enough money on the debit card.");
            }

            return check;
        } catch (BadRequestException | NotEnoughMoneyException e) {
            check.setProducts(Collections.emptyList());
            check.setTotalPrice(0);
            check.setTotalDiscount(0);
            check.setTotalWithDiscount(0);
            saveCheckToFile(check, "./result.csv", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            check.setProducts(Collections.emptyList());
            check.setTotalPrice(0);
            check.setTotalDiscount(0);
            check.setTotalWithDiscount(0);
            saveCheckToFile(check, "./result.csv", "Invalid number format in arguments.");
            throw new BadRequestException("Invalid number format in arguments.");
        } catch (Exception e) {
            check.setProducts(Collections.emptyList());
            check.setTotalPrice(0);
            check.setTotalDiscount(0);
            check.setTotalWithDiscount(0);
            saveCheckToFile(check, "./result.csv", "An internal error occurred: " + e.getMessage());
            throw new InternalServerErrorException("An internal error occurred: " + e.getMessage());
        }
    }

    private void calculateCheckTotals(Check check) {
        double totalPrice = 0;
        double totalDiscount = 0;
        double totalWithDiscount = 0;

        List<DiscountCard> discountCardsFromCsv = CsvFileReader.readDiscountCards("./src/main/resources/discountCards.csv");

        DiscountCard discountCard = check.getDiscountCard();
        boolean discountCardInCsv = true;

        if (discountCard != null) {
            discountCardInCsv = discountCardsFromCsv.stream()
                    .anyMatch(card -> card.getNumber() == discountCard.getNumber());
        }

        Map<String, List<Product>> groupedProducts = check.getProducts().stream()
                .collect(Collectors.groupingBy(Product::getDescription));

        for (Map.Entry<String, List<Product>> entry : groupedProducts.entrySet()) {
            List<Product> products = entry.getValue();
            String description = entry.getKey();
            int quantity = products.size();
            Product firstProduct = products.get(0);
            double price = firstProduct.getPrice();
            double totalProductPrice = price * quantity;

            double discount = 0;
            if (firstProduct.isWholesale() && quantity >= 5) {
                discount = totalProductPrice * 0.10;
            } else if (discountCard != null) {
                discount = totalProductPrice * discountCard.getDiscountAmount() / 100;
            }

            totalPrice += totalProductPrice;
            totalDiscount += discount;
        }

        totalWithDiscount = totalPrice - totalDiscount;

        if (discountCard != null && !discountCardInCsv) {
            double additionalDiscount = totalWithDiscount * 0.02;
            totalDiscount += additionalDiscount;
            totalWithDiscount -= additionalDiscount;
        }

        check.setTotalPrice(totalPrice);
        check.setTotalDiscount(totalDiscount);
        check.setTotalWithDiscount(totalWithDiscount);

    }

    public void printCheckToConsole(Check check) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd; HH:mm:ss");
        String formattedDateTime = check.getDateTime().format(formatter);

        System.out.println("Date and Time: " + formattedDateTime);
        System.out.println("DESCRIPTION; QTY; PRICE; TOTAL; DISCOUNT");

        Map<String, List<Product>> groupedProducts = check.getProducts().stream()
                .collect(Collectors.groupingBy(Product::getDescription));

        for (Map.Entry<String, List<Product>> entry : groupedProducts.entrySet()) {
            String description = entry.getKey();
            List<Product> products = entry.getValue();
            int quantity = products.size();
            Product firstProduct = products.get(0);
            double price = firstProduct.getPrice();
            double total = price * quantity;

            double discount = 0;
            if (firstProduct.isWholesale() && quantity >= 5) {
                discount = total * 0.10;
            } else if (check.getDiscountCard() != null) {
                discount = total * check.getDiscountCard().getDiscountAmount() / 100;
            }

            System.out.printf("DESCRIPTION: %s; QTY: %d; PRICE: %.2f; TOTAL: %.2f; DISCOUNT: %.2f\n",
                    description, quantity, price, total, discount);
        }

        System.out.printf("TOTAL PRICE: %.2f; TOTAL DISCOUNT: %.2f; TOTAL WITH DISCOUNT: %.2f\n",
                check.getTotalPrice(), check.getTotalDiscount(), check.getTotalWithDiscount());
    }

    public void saveCheckToFile(Check check, String filePath, String errorMessage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd; HH:mm:ss");
        String formattedDateTime = check.getDateTime() != null ? check.getDateTime().format(formatter) : "No Date Available";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Date and Time\n");
            writer.write(String.format("%s\n", formattedDateTime));
            writer.write("DESCRIPTION; QTY; PRICE; TOTAL; DISCOUNT\n");

            Map<String, List<Product>> groupedProducts = check.getProducts() != null ?
                    check.getProducts().stream().collect(Collectors.groupingBy(Product::getDescription)) :
                    Collections.emptyMap();

            for (Map.Entry<String, List<Product>> entry : groupedProducts.entrySet()) {
                String description = entry.getKey();
                List<Product> products = entry.getValue();
                int quantity = products.size();
                Product firstProduct = products.get(0);
                double price = firstProduct.getPrice();
                double total = price * quantity;

                double discount = 0;
                if (firstProduct.isWholesale() && quantity >= 5) {
                    discount = total * 0.10;
                } else if (check.getDiscountCard() != null) {
                    discount = total * check.getDiscountCard().getDiscountAmount() / 100;
                }

                writer.write(String.format("DESCRIPTION: %s; QTY: %d; PRICE: %.2f; TOTAL: %.2f; DISCOUNT: %.2f\n",
                        description, quantity, price, total, discount));
            }

            writer.write(String.format("TOTAL PRICE: %.2f; TOTAL DISCOUNT: %.2f; TOTAL WITH DISCOUNT: %.2f\n",
                    check.getTotalPrice(), check.getTotalDiscount(), check.getTotalWithDiscount()));

            if (errorMessage != null) {
                writer.write(String.format("ERROR: %s\n", errorMessage));
            }
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to save check to file: " + e.getMessage());
        }
    }
}
