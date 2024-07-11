package ru.clevertec.check;

import ru.clevertec.check.exception.BadRequestException;
import ru.clevertec.check.exception.NotEnoughMoneyException;
import ru.clevertec.check.exception.InternalServerErrorException;
import ru.clevertec.check.model.Check;
import ru.clevertec.check.service.CheckService;

public class CheckRunner {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Invalid arguments. Usage: java -cp out ru.clevertec.check.CheckRunner id-quantity discountCard=xxxx balanceDebitCard=xxxx");
            return;
        }

        CheckService checkService = new CheckService();
        try {
            Check check = checkService.generateCheck(args);
            checkService.printCheckToConsole(check);
            checkService.saveCheckToFile(check, "./result.csv", null);
        } catch (BadRequestException e) {
            System.out.println("BAD REQUEST: " + e.getMessage());
            checkService.saveCheckToFile(new Check(), "./result.csv", e.getMessage());
        } catch (NotEnoughMoneyException e) {
            System.out.println("NOT ENOUGH MONEY: " + e.getMessage());
            checkService.saveCheckToFile(new Check(), "./result.csv", e.getMessage());
        } catch (InternalServerErrorException e) {
            System.out.println("INTERNAL SERVER ERROR: " + e.getMessage());
            checkService.saveCheckToFile(new Check(), "./result.csv", e.getMessage());
        }
    }
}

