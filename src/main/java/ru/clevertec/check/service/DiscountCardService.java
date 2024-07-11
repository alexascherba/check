package ru.clevertec.check.service;

import ru.clevertec.check.model.DiscountCard;

import java.util.List;

public class DiscountCardService {
    private List<DiscountCard> discountCards;

    public DiscountCardService() {
        this.discountCards = CsvFileReader.readDiscountCards("./src/main/resources/discountCards.csv");
    }

    public DiscountCard getDiscountCardByNumber(int number) {
        return discountCards.stream().filter(dc -> dc.getNumber() == number).findFirst().orElse(null);
    }

}