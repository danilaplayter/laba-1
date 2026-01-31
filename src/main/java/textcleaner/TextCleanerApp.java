package textcleaner;

import java.util.Scanner;

public class TextCleanerApp {
    private static final TextCleaner textCleaner = new TextCleaner();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        LogConfigurator.configure();

        System.out.println("=== Программа очистки текста ===");
        System.out.println("Доступные команды:");
        System.out.println("1 - Очистить текст");
        System.out.println("2 - Настройки очистки");
        System.out.println("3 - История операций");
        System.out.println("0 - Выход");

        boolean running = true;
        while (running) {
            System.out.print("\nВведите команду: ");
            String command = scanner.nextLine().trim();

            switch (command) {
                case "1":
                    cleanText();
                    break;
                case "2":
                    showSettings();
                    break;
                case "3":
                    showHistory();
                    break;
                case "0":
                    running = false;
                    TextCleaner.getLogger().info("Завершение работы программы");
                    System.out.println("Программа завершена.");
                    break;
                default:
                    System.out.println("Неизвестная команда. Попробуйте снова.");
                    TextCleaner.getLogger().warn("Введена неизвестная команда: " + command);
            }
        }

        scanner.close();
    }

    private static void cleanText() {
        System.out.println("\n=== Очистка текста ===");
        System.out.print("Введите текст для очистки: ");
        String inputText = scanner.nextLine();

        TextCleaner.getLogger().info("Получен исходный текст: " + inputText);

        System.out.println("\nВыберите режим очистки:");
        System.out.println("1 - Удалить все пробелы");
        System.out.println("2 - Удалить лишние пробелы");
        System.out.println("3 - Удалить специальные символы");
        System.out.println("4 - Удалить цифры");
        System.out.println("5 - Удалить знаки препинания");
        System.out.println("6 - Комплексная очистка");
        System.out.print("Ваш выбор: ");

        int mode;
        try {
            mode = Integer.parseInt(scanner.nextLine().trim());
            if (mode < 1 || mode > 6) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число от 1 до 6");
            TextCleaner.getLogger().error("Ошибка выбора режима очистки", e);
            return;
        }

        String cleanedText = textCleaner.cleanText(inputText, mode);
        System.out.println("\n=== Результат очистки ===");
        System.out.println("Исходный текст: " + inputText);
        System.out.println("Очищенный текст: " + cleanedText);

        textCleaner.saveToHistory(inputText, cleanedText, mode);
    }

    private static void showSettings() {
        System.out.println("\n=== Настройки очистки ===");
        System.out.println("Текущие настройки:");
        System.out.println(
                "1. Удаление пробелов: " + (textCleaner.isRemoveSpaces() ? "ВКЛ" : "ВЫКЛ"));
        System.out.println(
                "2. Удаление лишних пробелов: "
                        + (textCleaner.isRemoveExtraSpaces() ? "ВКЛ" : "ВЫКЛ"));
        System.out.println(
                "3. Удаление спецсимволов: "
                        + (textCleaner.isRemoveSpecialChars() ? "ВКЛ" : "ВЫКЛ"));
        System.out.println("4. Удаление цифр: " + (textCleaner.isRemoveDigits() ? "ВКЛ" : "ВЫКЛ"));
        System.out.println(
                "5. Удаление знаков препинания: "
                        + (textCleaner.isRemovePunctuation() ? "ВКЛ" : "ВЫКЛ"));

        System.out.println("\nИзменить настройку (1-5) или 0 для возврата:");
        System.out.print("Ваш выбор: ");

        String choice = scanner.nextLine().trim();
        if (choice.equals("0")) {
            return;
        }

        try {
            int setting = Integer.parseInt(choice);
            if (setting >= 1 && setting <= 5) {
                boolean currentValue = false;
                switch (setting) {
                    case 1:
                        currentValue = textCleaner.isRemoveSpaces();
                        break;
                    case 2:
                        currentValue = textCleaner.isRemoveExtraSpaces();
                        break;
                    case 3:
                        currentValue = textCleaner.isRemoveSpecialChars();
                        break;
                    case 4:
                        currentValue = textCleaner.isRemoveDigits();
                        break;
                    case 5:
                        currentValue = textCleaner.isRemovePunctuation();
                        break;
                }

                System.out.println("Текущее значение: " + (currentValue ? "ВКЛ" : "ВЫКЛ"));
                System.out.print("Новое значение (ВКЛ/ВЫКЛ): ");
                String newValue = scanner.nextLine().trim().toUpperCase();

                boolean boolValue = newValue.equals("ВКЛ");

                switch (setting) {
                    case 1:
                        textCleaner.setRemoveSpaces(boolValue);
                        break;
                    case 2:
                        textCleaner.setRemoveExtraSpaces(boolValue);
                        break;
                    case 3:
                        textCleaner.setRemoveSpecialChars(boolValue);
                        break;
                    case 4:
                        textCleaner.setRemoveDigits(boolValue);
                        break;
                    case 5:
                        textCleaner.setRemovePunctuation(boolValue);
                        break;
                }

                System.out.println("Настройка изменена.");
                TextCleaner.getLogger()
                        .info("Изменена настройка " + setting + " на значение: " + boolValue);
            } else {
                System.out.println("Неверный выбор.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число от 0 до 5");
            TextCleaner.getLogger().error("Ошибка при изменении настроек", e);
        }
    }

    private static void showHistory() {
        System.out.println("\n=== История операций ===");
        if (textCleaner.getHistory().isEmpty()) {
            System.out.println("История пуста.");
            TextCleaner.getLogger().info("Просмотр пустой истории операций");
        } else {
            for (int i = 0; i < textCleaner.getHistory().size(); i++) {
                System.out.println("Запись " + (i + 1) + ":");
                System.out.println(
                        "  Исходный текст: "
                                + (textCleaner.getHistory().get(i).getOriginal().length() > 50
                                        ? textCleaner
                                                        .getHistory()
                                                        .get(i)
                                                        .getOriginal()
                                                        .substring(0, 47)
                                                + "..."
                                        : textCleaner.getHistory().get(i).getOriginal()));
                System.out.println(
                        "  Режим очистки: "
                                + getModeName(textCleaner.getHistory().get(i).getMode()));
                System.out.println();
            }
            TextCleaner.getLogger()
                    .info(
                            "Просмотр истории операций. Всего записей: "
                                    + textCleaner.getHistory().size());
        }
    }

    private static String getModeName(int mode) {
        switch (mode) {
            case 1:
                return "Удалить все пробелы";
            case 2:
                return "Удалить лишние пробелы";
            case 3:
                return "Удалить специальные символы";
            case 4:
                return "Удалить цифры";
            case 5:
                return "Удалить знаки препинания";
            case 6:
                return "Комплексная очистка";
            default:
                return "Неизвестный режим";
        }
    }
}
