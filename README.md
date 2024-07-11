# Чековая система

## Описание

Этот проект представляет собой систему для генерации чеков с возможностью применения скидок по дисконтным картам. Проект включает в себя обработку аргументов командной строки, вычисление итогов, печать чеков на консоль и сохранение их в файл.

## Структура проекта

- **src/main/java**: Исходный код проекта.
  - **ru.clevertec.check.model**: Модели данных (Product, DiscountCard, Check).
  - **ru.clevertec.check.service**: Сервисы для обработки чеков, продуктов и дисконтных карт.
  - **ru.clevertec.check.exception**: Исключения, используемые в проекте.
- **src/main/resources**: Ресурсные файлы.
  - **discountCards.csv**: Файл с данными дисконтных карт.
  - **products.csv**: Файл с исходными продуктами. 
- **out**: Скомпилированные классы и результат сборки.

## Сборка проекта

Проект написан на Java. Для сборки и запуска проекта необходимо использовать JDK 11 или выше.

### Шаг 1: Сборка проекта

1. Перейдите в корневую директорию проекта.
2. Выполните следующую команду для компиляции исходного кода:

    ```bash
    javac -d out src/main/java/ru/clevertec/check/CheckRunner.java src/main/java/ru/clevertec/check/model/Check.java src/main/java/ru/clevertec/check/model/DiscountCard.java src/main/java/ru/clevertec/check/model/Product.java src/main/java/ru/clevertec/check/service/CheckService.java src/main/java/ru/clevertec/check/service/CsvFileReader.java src/main/java/ru/clevertec/check/service/DiscountCardService.java src/main/java/ru/clevertec/check/service/ProductService.java src/main/java/ru/clevertec/check/exception/BadRequestException.java src/main/java/ru/clevertec/check/exception/NotEnoughMoneyException.java src/main/java/ru/clevertec/check/exception/InternalServerErrorException.java
    ```

   Это создаст папку `out` с скомпилированными классами.

### Шаг 2: Запуск проекта

После сборки проекта можно запустить программу, передав аргументы командной строки.

java -cp out ru.clevertec.check.CheckRunner id-quantity discountCard=xxxx balanceDebitCard=xxxx     

Пример:
```bash
java -cp out ru.clevertec.check.CheckRunner 19-3 2-6 1-6 7-3 discountCard=4444 balanceDebitCard=1000     
```

