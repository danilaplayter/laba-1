package sportclub.domain.usecase;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sportclub.config.AppConstants;
import sportclub.domain.interfaces.Payable;
import sportclub.domain.model.Coach;
import sportclub.domain.model.Manager;
import sportclub.domain.model.Member;
import sportclub.domain.model.Player;

import sportclub.domain.service.SalaryRegistry;
import sportclub.domain.service.StatisticsService;
import sportclub.domain.service.TransferJournal;
import sportclub.utils.InputValidator;
import sportclub.utils.LoggerUtil;

public class ClubManager {
    private static final Logger logger = LogManager.getLogger(ClubManager.class);
    private static final Logger adminLogger = LogManager.getLogger("sportclub.admin");
    private static final Logger financeLogger = LogManager.getLogger("sportclub.finance");
    private static final Logger fileLogger = LogManager.getLogger("sportclub.file");

    private final ClubManagerFacade facade;
    private Scanner scanner;
    private boolean isAdmin;
    private final DateTimeFormatter dtf =
            DateTimeFormatter.ofPattern(AppConstants.DATE_TIME_FORMAT);

    public ClubManager() {
        this.facade = new ClubManagerFacade();
        this.scanner = new Scanner(System.in);
        this.isAdmin = false;

        createDirectories();
        LoggerUtil.logStartup(logger);
        loadFromFile();
    }

    private void createDirectories() {
        try {
            new File(AppConstants.DATA_DIRECTORY).mkdirs();
            new File(AppConstants.LOGS_DIRECTORY).mkdirs();
            new File(AppConstants.REPORTS_DIRECTORY).mkdirs();
            logger.debug("Созданы необходимые директории");
        } catch (Exception e) {
            logger.error("Ошибка при создании директорий: {}", e.getMessage());
        }
    }

    public void showMenu() {
        LoggerUtil.logOperation(logger, "Запуск интерактивного меню");

        while (true) {
            System.out.println("\n=== РАСШИРЕННАЯ СИСТЕМА УПРАВЛЕНИЯ СПОРТИВНЫМ КЛУБОМ ===");
            System.out.println("1. Просмотреть всех членов клуба");
            System.out.println("2. Добавить игрока");
            System.out.println("3. Добавить тренера");
            System.out.println("4. Добавить менеджера");
            System.out.println("5. Поиск и фильтрация");
            System.out.println("6. Сортировка");
            System.out.println("7. Статистика и аналитика");
            System.out.println("8. Тренировки");
            System.out.println("9. Финансы");
            System.out.println("10. Импорт/Экспорт данных");
            System.out.println("11. Администрирование");
            System.out.println("12. Отчёты");
            System.out.println("13. Управление командами");
            System.out.println("14. Журнал трансферов");
            System.out.println("15. Реестр зарплат");
            System.out.println("16. Реестр тренировок");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                logger.debug("Выбран пункт меню: {}", choice);

                switch (choice) {
                    case 1 -> viewAllMembers();
                    case 2 -> addPlayer();
                    case 3 -> addCoach();
                    case 4 -> addManager();
                    case 5 -> searchAndFilterMenu();
                    case 6 -> sortMenu();
                    case 7 -> statisticsMenu();
                    case 8 -> trainingMenu();
                    case 9 -> financeMenu();
                    case 10 -> importExportMenu();
                    case 11 -> adminMenu();
                    case 12 -> reportsMenu();
                    case 13 -> teamsMenu();
                    case 14 -> transferJournalMenu();
                    case 15 -> salaryRegistryMenu();
                    case 16 -> trainingRegistryMenu();
                    case 0 -> {
                        LoggerUtil.logShutdown(logger);
                        saveToFile();
                        System.out.println("Данные сохранены. Выход...");
                        return;
                    }
                    default -> {
                        System.out.println("Неверный выбор!");
                        logger.warn("Неверный пункт меню: {}", choice);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите число!");
                logger.error("Ошибка преобразования ввода: {}", e.getMessage());
            } catch (Exception e) {
                logger.error("Непредвиденная ошибка: {}", e.getMessage(), e);
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void viewAllMembers() {
        logger.debug("Просмотр всех членов клуба");
        List<Member> members = facade.getAllMembers();

        if (members.isEmpty()) {
            System.out.println("Клуб пуст!");
            logger.info("Клуб пуст, нет членов для отображения");
            return;
        }

        System.out.println("\n=== ВСЕ ЧЛЕНЫ КЛУБА ===");
        System.out.printf("Общее количество: %d\n", members.size());
        members.forEach(System.out::println);
        logger.info("Отображено {} членов клуба", members.size());
    }

    private void addPlayer() {
        logger.info("Начало добавления игрока");
        System.out.println("\n=== ДОБАВЛЕНИЕ ИГРОКА ===");

        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();
            if (!InputValidator.isValidName(name)) {
                System.out.println(
                        "Неверное имя! Имя не должно быть пустым и не более 100 символов.");
                return;
            }

            System.out.print("Возраст: ");
            int age = Integer.parseInt(scanner.nextLine());
            if (!InputValidator.isValidAge(age)) {
                System.out.printf(
                        "Возраст должен быть от %d до %d лет!%n",
                        AppConstants.MIN_AGE, AppConstants.MAX_AGE);
                return;
            }

            System.out.print("Команда: ");
            String team = scanner.nextLine();
            if (!InputValidator.isValidTeam(team)) {
                System.out.println("Неверное название команды! Не более 50 символов.");
                return;
            }

            System.out.print("Позиция: ");
            String position = scanner.nextLine();
            if (!InputValidator.isValidPosition(position)) {
                System.out.println("Неверная позиция! Не более 30 символов.");
                return;
            }

            System.out.print("Номер игрока (1-99): ");
            int number = Integer.parseInt(scanner.nextLine());
            if (!InputValidator.isValidJerseyNumber(number)) {
                System.out.println("Номер должен быть от 1 до 99!");
                return;
            }

            System.out.print("Базовая зарплата: ");
            BigDecimal salary = new BigDecimal(scanner.nextLine());
            if (!InputValidator.isValidSalary(salary)) {
                System.out.printf(
                        "Зарплата должна быть от %s до %s!%n",
                        AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                return;
            }

            Member player = facade.addPlayer(name, age, team, position, number, salary);
            System.out.println("Игрок добавлен! ID: " + player.getId());
            LoggerUtil.logOperation(
                    logger,
                    "Добавление игрока",
                    "Имя",
                    name,
                    "ID",
                    player.getId(),
                    "Команда",
                    team);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            LoggerUtil.logError(logger, "Добавление игрока", e);
        }
    }

    private void addCoach() {
        logger.info("Начало добавления тренера");
        System.out.println("\n=== ДОБАВЛЕНИЕ ТРЕНЕРА ===");

        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();
            if (!InputValidator.isValidName(name)) {
                System.out.println(
                        "Неверное имя! Имя не должно быть пустым и не более 100 символов.");
                return;
            }

            System.out.print("Возраст: ");
            int age = Integer.parseInt(scanner.nextLine());
            if (!InputValidator.isValidAge(age)) {
                System.out.printf(
                        "Возраст должен быть от %d до %d лет!%n",
                        AppConstants.MIN_AGE, AppConstants.MAX_AGE);
                return;
            }

            System.out.print("Команда: ");
            String team = scanner.nextLine();
            if (!InputValidator.isValidTeam(team)) {
                System.out.println("Неверное название команды! Не более 50 символов.");
                return;
            }

            System.out.print("Специализация: ");
            String specialization = scanner.nextLine();

            System.out.print("Сертификация: ");
            String certification = scanner.nextLine();

            System.out.print("Базовая зарплата: ");
            BigDecimal salary = new BigDecimal(scanner.nextLine());
            if (!InputValidator.isValidSalary(salary)) {
                System.out.printf(
                        "Зарплата должна быть от %s до %s!%n",
                        AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                return;
            }

            Member coach = facade.addCoach(name, age, team, specialization, certification, salary);
            System.out.println("Тренер добавлен! ID: " + coach.getId());
            LoggerUtil.logOperation(
                    logger,
                    "Добавление тренера",
                    "Имя",
                    name,
                    "ID",
                    coach.getId(),
                    "Специализация",
                    specialization);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            LoggerUtil.logError(logger, "Добавление тренера", e);
        }
    }

    private void addManager() {
        logger.info("Начало добавления менеджера");
        System.out.println("\n=== ДОБАВЛЕНИЕ МЕНЕДЖЕРА ===");

        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();
            if (!InputValidator.isValidName(name)) {
                System.out.println(
                        "Неверное имя! Имя не должно быть пустым и не более 100 символов.");
                return;
            }

            System.out.print("Возраст: ");
            int age = Integer.parseInt(scanner.nextLine());
            if (!InputValidator.isValidAge(age)) {
                System.out.printf(
                        "Возраст должен быть от %d до %d лет!%n",
                        AppConstants.MIN_AGE, AppConstants.MAX_AGE);
                return;
            }

            System.out.print("Команда: ");
            String team = scanner.nextLine();
            if (!InputValidator.isValidTeam(team)) {
                System.out.println("Неверное название команды! Не более 50 символов.");
                return;
            }

            System.out.print("Отдел: ");
            String department = scanner.nextLine();

            System.out.print("Обязанности: ");
            String responsibilities = scanner.nextLine();

            System.out.print("Базовая зарплата: ");
            BigDecimal salary = new BigDecimal(scanner.nextLine());
            if (!InputValidator.isValidSalary(salary)) {
                System.out.printf(
                        "Зарплата должна быть от %s до %s!%n",
                        AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                return;
            }

            Member manager =
                    facade.addManager(name, age, team, department, responsibilities, salary);
            System.out.println("Менеджер добавлен! ID: " + manager.getId());
            LoggerUtil.logOperation(
                    logger,
                    "Добавление менеджера",
                    "Имя",
                    name,
                    "ID",
                    manager.getId(),
                    "Отдел",
                    department);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            LoggerUtil.logError(logger, "Добавление менеджера", e);
        }
    }

    private void searchAndFilterMenu() {
        System.out.println("\n=== ПОИСК И ФИЛЬТРАЦИЯ ===");
        System.out.println("1. Простой поиск по имени");
        System.out.println("2. Поиск по команде");
        System.out.println("3. Фильтр по роли");
        System.out.println("4. Фильтр по стажу");
        System.out.println("5. Фильтр по возрасту");
        System.out.println("6. Комбинированный фильтр");
        System.out.println("7. Фильтр по зарплате");
        System.out.println("8. Фильтр по позиции/специализации");
        System.out.println("9. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> searchByName();
                case 2 -> searchByTeam();
                case 3 -> filterByRole();
                case 4 -> filterByExperience();
                case 5 -> filterByAge();
                case 6 -> combinedFilter();
                case 7 -> filterBySalary();
                case 8 -> filterBySpecialization();
                case 9 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void searchByName() {
        System.out.print("\nВведите имя для поиска: ");
        String name = scanner.nextLine();
        List<Member> results = facade.searchByName(name);
        displayResults(results, "поиска по имени");
    }

    private void searchByTeam() {
        System.out.print("\nВведите название команды: ");
        String team = scanner.nextLine();
        List<Member> results = facade.searchByTeam(team);
        displayResults(results, "поиска по команде");
    }

    private void filterByRole() {
        System.out.println("\nВыберите роль:");
        System.out.println("1. Игрок");
        System.out.println("2. Тренер");
        System.out.println("3. Менеджер");
        System.out.print("Ваш выбор: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            String role =
                    switch (choice) {
                        case 1 -> "Игрок";
                        case 2 -> "Тренер";
                        case 3 -> "Менеджер";
                        default -> null;
                    };

            if (role != null) {
                List<Member> results = facade.filterByRole(role);
                displayResults(results, "фильтра по роли");
            } else {
                System.out.println("Неверный выбор роли!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void filterByExperience() {
        try {
            System.out.print("\nМинимальный стаж (лет): ");
            int minExp = Integer.parseInt(scanner.nextLine());
            System.out.print("Максимальный стаж (лет, -1 для любого): ");
            int maxExp = Integer.parseInt(scanner.nextLine());
            maxExp = maxExp == -1 ? Integer.MAX_VALUE : maxExp;

            List<Member> results = facade.filterByExperience(minExp, maxExp);
            displayResults(results, "фильтра по стажу");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void filterByAge() {
        try {
            System.out.print("\nМинимальный возраст: ");
            int minAge = Integer.parseInt(scanner.nextLine());
            System.out.print("Максимальный возраст: ");
            int maxAge = Integer.parseInt(scanner.nextLine());

            if (minAge > maxAge) {
                System.out.println("Минимальный возраст не может быть больше максимального!");
                return;
            }

            List<Member> results = facade.filterByAgeRange(minAge, maxAge);
            displayResults(results, "фильтра по возрасту");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void combinedFilter() {
        System.out.println("\n=== КОМБИНИРОВАННЫЙ ФИЛЬТР ===");

        System.out.print("Роль (игрок/тренер/менеджер, оставьте пустым для всех): ");
        String role = scanner.nextLine().trim();
        if (role.isEmpty()) role = null;

        Integer minAge = null, maxAge = null;
        Integer minExp = null, maxExp = null;
        BigDecimal minSalary = null, maxSalary = null;

        try {
            System.out.print("Минимальный возраст (оставьте пустым для всех): ");
            String ageInput = scanner.nextLine().trim();
            if (!ageInput.isEmpty()) {
                minAge = Integer.parseInt(ageInput);
                if (minAge < AppConstants.MIN_AGE || minAge > AppConstants.MAX_AGE) {
                    System.out.printf(
                            "Возраст должен быть от %d до %d лет!%n",
                            AppConstants.MIN_AGE, AppConstants.MAX_AGE);
                    return;
                }
            }

            System.out.print("Максимальный возраст (оставьте пустым для всех): ");
            ageInput = scanner.nextLine().trim();
            if (!ageInput.isEmpty()) {
                maxAge = Integer.parseInt(ageInput);
                if (maxAge < AppConstants.MIN_AGE || maxAge > AppConstants.MAX_AGE) {
                    System.out.printf(
                            "Возраст должен быть от %d до %d лет!%n",
                            AppConstants.MIN_AGE, AppConstants.MAX_AGE);
                    return;
                }
            }

            if (minAge != null && maxAge != null && minAge > maxAge) {
                System.out.println("Минимальный возраст не может быть больше максимального!");
                return;
            }

            System.out.print("Минимальный стаж (лет, оставьте пустым для всех): ");
            String expInput = scanner.nextLine().trim();
            if (!expInput.isEmpty()) minExp = Integer.parseInt(expInput);

            System.out.print("Максимальный стаж (лет, оставьте пустым для всех): ");
            expInput = scanner.nextLine().trim();
            if (!expInput.isEmpty()) maxExp = Integer.parseInt(expInput);

            if (minExp != null && maxExp != null && minExp > maxExp) {
                System.out.println("Минимальный стаж не может быть больше максимального!");
                return;
            }

            System.out.print("Минимальная зарплата (оставьте пустым для всех): ");
            String salaryInput = scanner.nextLine().trim();
            if (!salaryInput.isEmpty()) {
                minSalary = new BigDecimal(salaryInput);
                if (minSalary.compareTo(AppConstants.MIN_SALARY) < 0
                        || minSalary.compareTo(AppConstants.MAX_SALARY) > 0) {
                    System.out.printf(
                            "Зарплата должна быть от %s до %s!%n",
                            AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                    return;
                }
            }

            System.out.print("Максимальная зарплата (оставьте пустым для всех): ");
            salaryInput = scanner.nextLine().trim();
            if (!salaryInput.isEmpty()) {
                maxSalary = new BigDecimal(salaryInput);
                if (maxSalary.compareTo(AppConstants.MIN_SALARY) < 0
                        || maxSalary.compareTo(AppConstants.MAX_SALARY) > 0) {
                    System.out.printf(
                            "Зарплата должна быть от %s до %s!%n",
                            AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                    return;
                }
            }

            if (minSalary != null && maxSalary != null && minSalary.compareTo(maxSalary) > 0) {
                System.out.println("Минимальная зарплата не может быть больше максимальной!");
                return;
            }

            List<Member> results =
                    facade.filterMembers(
                            role, minAge, maxAge, minExp, maxExp, minSalary, maxSalary);
            displayResults(results, "комбинированного фильтра");

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            logger.error("Ошибка при комбинированном фильтре: {}", e.getMessage());
        }
    }

    private void filterBySalary() {
        try {
            System.out.print("\nМинимальная зарплата: ");
            BigDecimal minSalary = new BigDecimal(scanner.nextLine());
            if (minSalary.compareTo(AppConstants.MIN_SALARY) < 0
                    || minSalary.compareTo(AppConstants.MAX_SALARY) > 0) {
                System.out.printf(
                        "Зарплата должна быть от %s до %s!%n",
                        AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                return;
            }

            System.out.print("Максимальная зарплата: ");
            BigDecimal maxSalary = new BigDecimal(scanner.nextLine());
            if (maxSalary.compareTo(AppConstants.MIN_SALARY) < 0
                    || maxSalary.compareTo(AppConstants.MAX_SALARY) > 0) {
                System.out.printf(
                        "Зарплата должна быть от %s до %s!%n",
                        AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                return;
            }

            if (minSalary.compareTo(maxSalary) > 0) {
                System.out.println("Минимальная зарплата не может быть больше максимальной!");
                return;
            }

            List<Member> results = facade.filterBySalaryRange(minSalary, maxSalary);
            displayResults(results, "фильтра по зарплате");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void filterBySpecialization() {
        System.out.println("\n=== ФИЛЬТР ПО СПЕЦИАЛИЗАЦИИ/ПОЗИЦИИ ===");
        System.out.println("1. Для игроков (позиция)");
        System.out.println("2. Для тренеров (специализация)");
        System.out.println("3. Для менеджеров (отдел)");
        System.out.print("Ваш выбор: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            List<Member> results = new ArrayList<>();

            switch (choice) {
                case 1 -> {
                    System.out.print("Введите позицию для поиска: ");
                    String position = scanner.nextLine().toLowerCase();
                    List<Player> players = facade.filterPlayersByPosition(position);
                    results.addAll(players);
                }
                case 2 -> {
                    System.out.print("Введите специализацию для поиска: ");
                    String specialization = scanner.nextLine().toLowerCase();
                    List<Coach> coaches = facade.filterCoachesBySpecialization(specialization);
                    results.addAll(coaches);
                }
                case 3 -> {
                    System.out.print("Введите отдел для поиска: ");
                    String department = scanner.nextLine().toLowerCase();
                    List<Manager> managers = facade.filterManagersByDepartment(department);
                    results.addAll(managers);
                }
                default -> {
                    System.out.println("Неверный выбор!");
                    return;
                }
            }

            displayResults(results, "фильтра по специализации");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void sortMenu() {
        System.out.println("\n=== СОРТИРОВКА ===");
        System.out.println("1. По имени (А-Я)");
        System.out.println("2. По имени (Я-А)");
        System.out.println("3. По возрасту (возрастание)");
        System.out.println("4. По возрасту (убывание)");
        System.out.println("5. По стажу (возрастание)");
        System.out.println("6. По стажу (убывание)");
        System.out.println("7. По зарплате (возрастание)");
        System.out.println("8. По зарплате (убывание)");
        System.out.println("9. По дате вступления");
        System.out.println("10. Комбинированная сортировка (имя + зарплата)");
        System.out.println("11. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            List<Member> sorted;

            switch (choice) {
                case 1 -> sorted = facade.sortByName(true);
                case 2 -> sorted = facade.sortByName(false);
                case 3 -> sorted = facade.sortByAge(true);
                case 4 -> sorted = facade.sortByAge(false);
                case 5 -> sorted = facade.sortByExperience(true);
                case 6 -> sorted = facade.sortByExperience(false);
                case 7 -> sorted = facade.sortBySalary(true);
                case 8 -> sorted = facade.sortBySalary(false);
                case 9 -> sorted = facade.sortByJoinDate();
                case 10 -> {
                    System.out.println("Сортировка по имени (1-А-Я, 2-Я-А): ");
                    int nameOrder = Integer.parseInt(scanner.nextLine());
                    System.out.println("Сортировка по зарплате (1-возрастание, 2-убывание): ");
                    int salaryOrder = Integer.parseInt(scanner.nextLine());
                    sorted = facade.sortByMultipleFields(nameOrder == 1, salaryOrder == 1);
                }
                case 11 -> {
                    return;
                }
                default -> {
                    System.out.println("Неверный выбор!");
                    return;
                }
            }

            System.out.println("\n=== РЕЗУЛЬТАТЫ СОРТИРОВКИ ===");
            System.out.printf("Найдено: %d записей\n", sorted.size());
            sorted.forEach(System.out::println);
            logger.info("Выполнена сортировка по варианту {}", choice);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void statisticsMenu() {
        System.out.println("\n=== СТАТИСТИКА И АНАЛИТИКА ===");
        System.out.println("1. Статистика по ролям");
        System.out.println("2. Статистика по возрасту");
        System.out.println("3. Статистика по стажу");
        System.out.println("4. Финансовая статистика");
        System.out.println("5. Статистика тренировок");
        System.out.println("6. Топ-5 по зарплате");
        System.out.println("7. Топ-5 по стажу");
        System.out.println("8. Топ игроков по эффективности");
        System.out.println("9. Топ тренеров по успешности");
        System.out.println("10. Топ менеджеров по контрактам");
        System.out.println("11. Средние показатели");
        System.out.println("12. Распределение по командам");
        System.out.println("13. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> showRoleStatistics();
                case 2 -> showAgeStatistics();
                case 3 -> showExperienceStatistics();
                case 4 -> showFinancialStatistics();
                case 5 -> showTrainingStatistics();
                case 6 -> showTopBySalary();
                case 7 -> showTopByExperience();
                case 8 -> showTopPlayersByPerformance();
                case 9 -> showTopCoachesBySuccessRate();
                case 10 -> showTopManagersByContracts();
                case 11 -> showAverageStatistics();
                case 12 -> showTeamDistribution();
                case 13 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void showRoleStatistics() {
        Map<String, Long> roleDistribution = facade.getRoleDistribution();
        Map<String, Double> avgAgeByRole = facade.getAverageAgeByRole();
        Map<String, BigDecimal> avgSalaryByRole = facade.getAverageSalaryByRole();

        System.out.println("\n=== СТАТИСТИКА ПО РОЛЯМ ===");
        System.out.printf("Общее количество членов: %d\n", facade.getMemberCount());

        for (Map.Entry<String, Long> entry : roleDistribution.entrySet()) {
            String role = entry.getKey();
            long count = entry.getValue();
            double percentage = (double) count / facade.getMemberCount() * 100;

            System.out.printf("\n%s: %d человек (%.1f%%)%n", role, count, percentage);
            System.out.printf("  Средний возраст: %.1f лет%n", avgAgeByRole.get(role));
            System.out.printf("  Средняя зарплата: %.2f%n", avgSalaryByRole.get(role));
        }

        logger.info("Выведена статистика по ролям");
    }

    private void showAgeStatistics() {
        StatisticsService.AgeStatistics stats = facade.getAgeStatistics();
        double avgAge = facade.calculateAverageAge();

        System.out.println("\n=== СТАТИСТИКА ПО ВОЗРАСТУ ===");
        System.out.printf("Средний возраст: %.1f лет%n", avgAge);
        System.out.printf("Минимальный возраст: %d лет%n", stats.getMinAge());
        System.out.printf("Максимальный возраст: %d лет%n", stats.getMaxAge());

        List<Member> members = facade.getAllMembers();
        long under25 = members.stream().filter(m -> m.getAge() < 25).count();
        long age25to35 = members.stream().filter(m -> m.getAge() >= 25 && m.getAge() <= 35).count();
        long age36to45 = members.stream().filter(m -> m.getAge() >= 36 && m.getAge() <= 45).count();
        long over45 = members.stream().filter(m -> m.getAge() > 45).count();

        System.out.println("\nРаспределение по возрастным группам:");
        System.out.printf(
                "  До 25 лет: %d (%.1f%%)%n", under25, (double) under25 / members.size() * 100);
        System.out.printf(
                "  25-35 лет: %d (%.1f%%)%n", age25to35, (double) age25to35 / members.size() * 100);
        System.out.printf(
                "  36-45 лет: %d (%.1f%%)%n", age36to45, (double) age36to45 / members.size() * 100);
        System.out.printf(
                "  Старше 45: %d (%.1f%%)%n", over45, (double) over45 / members.size() * 100);

        logger.info("Выведена статистика по возрасту");
    }

    private void showExperienceStatistics() {
        StatisticsService.ExperienceStatistics stats = facade.getExperienceStatistics();
        double avgExp = facade.calculateAverageExperience();

        System.out.println("\n=== СТАТИСТИКА ПО СТАЖУ ===");
        System.out.printf("Средний стаж: %.1f лет%n", avgExp);
        System.out.printf("Минимальный стаж: %d лет%n", stats.getMinExperience());
        System.out.printf("Максимальный стаж: %d лет%n", stats.getMaxExperience());

        List<Member> members = facade.getAllMembers();
        long under5 = members.stream().filter(m -> m.getExperience() < 5).count();
        long exp5to10 =
                members.stream()
                        .filter(m -> m.getExperience() >= 5 && m.getExperience() <= 10)
                        .count();
        long exp11to20 =
                members.stream()
                        .filter(m -> m.getExperience() >= 11 && m.getExperience() <= 20)
                        .count();
        long over20 = members.stream().filter(m -> m.getExperience() > 20).count();

        System.out.println("\nРаспределение по стажу:");
        System.out.printf(
                "  До 5 лет: %d (%.1f%%)%n", under5, (double) under5 / members.size() * 100);
        System.out.printf(
                "  5-10 лет: %d (%.1f%%)%n", exp5to10, (double) exp5to10 / members.size() * 100);
        System.out.printf(
                "  11-20 лет: %d (%.1f%%)%n", exp11to20, (double) exp11to20 / members.size() * 100);
        System.out.printf(
                "  Более 20 лет: %d (%.1f%%)%n", over20, (double) over20 / members.size() * 100);

        logger.info("Выведена статистика по стажу");
    }

    private void showFinancialStatistics() {
        StatisticsService.SalaryStatistics stats = facade.getSalaryStatistics();
        BigDecimal totalBonuses = facade.calculateTotalBonuses();
        BigDecimal totalPaid = facade.getTotalPaidToDate();
        BigDecimal plannedPayments = stats.getTotalSalary().add(totalBonuses);

        System.out.println("\n=== ФИНАНСОВАЯ СТАТИСТИКА ===");
        System.out.printf("Общий фонд зарплат: %s%n", stats.getTotalSalary());
        System.out.printf("Средняя зарплата: %s%n", stats.getAverageSalary());
        System.out.printf("Максимальная зарплата: %s%n", stats.getMaxSalary());
        System.out.printf("Минимальная зарплата: %s%n", stats.getMinSalary());
        System.out.printf("Общая сумма бонусов: %s%n", totalBonuses);
        System.out.printf("Общая выплаченная сумма: %s%n", totalPaid);
        System.out.printf("Планируемые выплаты: %s%n", plannedPayments);

        financeLogger.info(
                "Финансовая статистика: фонд={}, средняя={}, бонусы={}",
                stats.getTotalSalary(),
                stats.getAverageSalary(),
                totalBonuses);
    }

    private void showTrainingStatistics() {
        int totalTrainings = facade.getTotalTrainingCount();
        double avgIntensity = facade.getAverageIntensity();
        int memberCount = facade.getMemberCount();

        System.out.println("\n=== СТАТИСТИКА ТРЕНИРОВОК ===");
        System.out.printf("Общее количество тренировок: %d%n", totalTrainings);
        System.out.printf("Средняя интенсивность: %.1f/10%n", avgIntensity);
        System.out.printf(
                "Среднее количество тренировок на человека: %.1f%n",
                memberCount > 0 ? (double) totalTrainings / memberCount : 0);

        List<Member> players = facade.filterByRole("Игрок");
        List<Member> coaches = facade.filterByRole("Тренер");
        List<Member> managers = facade.filterByRole("Менеджер");

        System.out.println("\nСтатистика по ролям:");
        if (!players.isEmpty()) {
            double playerAvg =
                    players.stream().mapToInt(Member::getTrainingCount).average().orElse(0);
            System.out.printf("  Игроки: %.1f тренировок на человека%n", playerAvg);
        }
        if (!coaches.isEmpty()) {
            double coachAvg =
                    coaches.stream().mapToInt(Member::getTrainingCount).average().orElse(0);
            System.out.printf("  Тренеры: %.1f тренировок на человека%n", coachAvg);
        }
        if (!managers.isEmpty()) {
            double managerAvg =
                    managers.stream().mapToInt(Member::getTrainingCount).average().orElse(0);
            System.out.printf("  Менеджеры: %.1f тренировок на человека%n", managerAvg);
        }

        logger.info("Выведена статистика тренировок");
    }

    private void showTopBySalary() {
        List<Member> topSalary = facade.getTopBySalary(5);
        System.out.println("\n=== ТОП-5 ПО ЗАРПЛАТЕ ===");
        for (int i = 0; i < topSalary.size(); i++) {
            Member m = topSalary.get(i);
            System.out.printf(
                    "%d. %s (%s, %s) - %s%n",
                    i + 1, m.getName(), m.getRole(), m.getTeam(), m.getBaseSalary());
        }
        logger.info("Выведен топ-5 по зарплате");
    }

    private void showTopByExperience() {
        List<Member> topExp = facade.getTopByExperience(5);
        System.out.println("\n=== ТОП-5 ПО СТАЖУ ===");
        for (int i = 0; i < topExp.size(); i++) {
            Member m = topExp.get(i);
            System.out.printf(
                    "%d. %s (%s, %s) - %d лет (вступил: %s)%n",
                    i + 1,
                    m.getName(),
                    m.getRole(),
                    m.getTeam(),
                    m.getExperience(),
                    m.getJoinDate());
        }
        logger.info("Выведен топ-5 по стажу");
    }

    private void showTopPlayersByPerformance() {
        List<Member> topPlayers = facade.getTopPlayersByPerformance(5);
        System.out.println("\n=== ТОП-5 ИГРОКОВ ПО ЭФФЕКТИВНОСТИ ===");
        for (int i = 0; i < topPlayers.size(); i++) {
            Player p = (Player) topPlayers.get(i);
            System.out.printf(
                    "%d. %s (%s, №%d) - Рейтинг: %.2f, Голы: %d, Ассисты: %d%n",
                    i + 1,
                    p.getName(),
                    p.getPosition(),
                    p.getJerseyNumber(),
                    p.getPerformanceRating(),
                    p.getGoalsScored(),
                    p.getAssists());
        }
        logger.info("Выведен топ-5 игроков по эффективности");
    }

    private void showTopCoachesBySuccessRate() {
        List<Member> topCoaches = facade.getTopCoachesBySuccessRate(5);
        System.out.println("\n=== ТОП-5 ТРЕНЕРОВ ПО УСПЕШНОСТИ ===");
        for (int i = 0; i < topCoaches.size(); i++) {
            Coach c = (Coach) topCoaches.get(i);
            System.out.printf(
                    "%d. %s (%s) - Успешность: %.1f%%, Тренировок: %d, Успешных: %d%n",
                    i + 1,
                    c.getName(),
                    c.getSpecialization(),
                    c.getSuccessRate(),
                    c.getTrainingCount(),
                    c.getSuccessfulSessions());
        }
        logger.info("Выведен топ-5 тренеров по успешности");
    }

    private void showTopManagersByContracts() {
        List<Member> topManagers = facade.getTopManagersByContracts(5);
        System.out.println("\n=== ТОП-5 МЕНЕДЖЕРОВ ПО КОНТРАКТАМ ===");
        for (int i = 0; i < topManagers.size(); i++) {
            Manager m = (Manager) topManagers.get(i);
            System.out.printf(
                    "%d. %s (%s) - Контрактов: %d, Управляемый бюджет: %s%n",
                    i + 1,
                    m.getName(),
                    m.getDepartment(),
                    m.getContractsSigned(),
                    m.getBudgetManaged());
        }
        logger.info("Выведен топ-5 менеджеров по контрактам");
    }

    private void showAverageStatistics() {
        double avgAge = facade.calculateAverageAge();
        double avgExp = facade.calculateAverageExperience();
        StatisticsService.SalaryStatistics salaryStats = facade.getSalaryStatistics();
        int totalTrainings = facade.getTotalTrainingCount();
        int memberCount = facade.getMemberCount();

        System.out.println("\n=== СРЕДНИЕ ПОКАЗАТЕЛИ ===");
        System.out.printf("Средний возраст: %.1f лет%n", avgAge);
        System.out.printf("Средний стаж: %.1f лет%n", avgExp);
        System.out.printf("Средняя зарплата: %.2f%n", salaryStats.getAverageSalary());
        System.out.printf(
                "Среднее количество тренировок на человека: %.1f%n",
                memberCount > 0 ? (double) totalTrainings / memberCount : 0);
        System.out.printf(
                "Средняя интенсивность тренировок: %.1f/10%n", facade.getAverageIntensity());

        logger.info("Выведены средние показатели");
    }

    private void showTeamDistribution() {
        Map<String, Integer> teamDistribution = facade.getTeamDistribution();
        List<String> allTeams = facade.getAllTeams();

        System.out.println("\n=== РАСПРЕДЕЛЕНИЕ ПО КОМАНДАМ ===");
        System.out.printf("Всего команд: %d\n", allTeams.size());

        for (String team : allTeams) {
            int count = teamDistribution.getOrDefault(team, 0);
            double percentage = (double) count / facade.getMemberCount() * 100;
            System.out.printf("\n%s: %d человек (%.1f%%)%n", team, count, percentage);

            Map<String, List<Member>> membersByRole =
                    facade.getMembersByTeam().getOrDefault(team, Collections.emptyList()).stream()
                            .collect(Collectors.groupingBy(Member::getRole));

            for (Map.Entry<String, List<Member>> entry : membersByRole.entrySet()) {
                System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue().size());
            }
        }

        logger.info("Выведено распределение по командам");
    }

    private void trainingMenu() {
        System.out.println("\n=== ТРЕНИРОВКИ ===");
        System.out.println("1. Провести тренировку");
        System.out.println("2. Показать историю тренировок");
        System.out.println("3. Показать прогресс по тренировкам");
        System.out.println("4. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> conductTraining();
                case 2 -> showTrainingHistory();
                case 3 -> showTrainingProgress();
                case 4 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void conductTraining() {
        try {
            System.out.print("\nID участника для тренировки: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Продолжительность (минут): ");
            int duration = Integer.parseInt(scanner.nextLine());
            if (!InputValidator.isValidTrainingDuration(duration)) {
                System.out.printf(
                        "Длительность тренировки должна быть от %d до %d минут!%n",
                        AppConstants.MIN_TRAINING_DURATION, AppConstants.MAX_TRAINING_DURATION);
                return;
            }

            System.out.print("Интенсивность (1-10): ");
            int intensity = Integer.parseInt(scanner.nextLine());
            if (!InputValidator.isValidTrainingIntensity(intensity)) {
                System.out.printf(
                        "Интенсивность должна быть от %d до %d!%n",
                        AppConstants.MIN_TRAINING_INTENSITY, AppConstants.MAX_TRAINING_INTENSITY);
                return;
            }

            String result = facade.conductTraining(id, duration, intensity);
            System.out.println("Результат: " + result);
            logger.info(
                    "Проведена тренировка для участника ID: {}, длительность: {}, интенсивность: {}",
                    id,
                    duration,
                    intensity);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void showTrainingHistory() {
        try {
            System.out.print("\nID участника для просмотра истории тренировок: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Member> member = facade.findMemberById(id);
            if (member.isPresent()) {
                System.out.println(member.get().getTrainingHistory());
                logger.info("Показана история тренировок для {}", member.get().getName());
            } else {
                System.out.println("Участник не найден!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void showTrainingProgress() {
        try {
            System.out.print("\nID участника для просмотра прогресса: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Member> member = facade.findMemberById(id);
            if (member.isPresent()) {
                double progress = member.get().calculateProgress();
                int trainingCount = member.get().getTrainingCount();
                double avgIntensity = member.get().getAverageIntensity();

                System.out.printf("Прогресс %s: %.1f%%%n", member.get().getName(), progress);
                System.out.printf("Количество тренировок: %d%n", trainingCount);
                System.out.printf("Средняя интенсивность: %.1f/10%n", avgIntensity);

                if (member.get() instanceof Player player) {
                    System.out.printf("Уровень фитнеса: %.1f%%%n", player.getFitnessLevel());
                } else if (member.get() instanceof Coach coach) {
                    System.out.printf("Успешность: %.1f%%%n", coach.getSuccessRate());
                }

                logger.info(
                        "Показан прогресс тренировок для {}: {}%",
                        member.get().getName(), progress);
            } else {
                System.out.println("Участник не найден!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void financeMenu() {
        System.out.println("\n=== ФИНАНСЫ ===");
        System.out.println("1. Выплатить зарплату");
        System.out.println("2. Показать историю выплат");
        System.out.println("3. Изменить зарплату");
        System.out.println("4. Рассчитать бонусы");
        System.out.println("5. Показать общие финансовые показатели");
        System.out.println("6. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> paySalary();
                case 2 -> showPaymentHistory();
                case 3 -> changeSalary();
                case 4 -> calculateBonuses();
                case 5 -> showFinancialSummary();
                case 6 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void paySalary() {
        System.out.println("\n=== ВЫПЛАТА ЗАРПЛАТ ===");
        System.out.println("1. Выплатить всем");
        System.out.println("2. Выплатить по ID");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                List<Member> members = facade.getAllMembers();
                BigDecimal totalPaid = BigDecimal.ZERO;

                System.out.println("\nВыплата зарплат всем сотрудникам:");
                for (Member member : members) {
                    Payable.PaymentResult result = member.paySalary(LocalDate.now());
                    System.out.printf("  %s: %s%n", member.getName(), result.message());
                    totalPaid = totalPaid.add(result.amount());
                }
                System.out.printf("\nОбщая выплаченная сумма: %s%n", totalPaid);
                logger.info("Выплачены зарплаты всем сотрудникам, общая сумма: {}", totalPaid);

            } else if (choice == 2) {
                System.out.print("ID сотрудника: ");
                int id = Integer.parseInt(scanner.nextLine());

                Optional<Member> member = facade.findMemberById(id);
                if (member.isPresent()) {
                    Payable.PaymentResult result = member.get().paySalary(LocalDate.now());
                    System.out.println(result.message());
                    logger.info(
                            "Выплачена зарплата {}: {}", member.get().getName(), result.amount());
                } else {
                    System.out.println("Сотрудник не найден!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void showPaymentHistory() {
        try {
            System.out.print("\nID сотрудника для просмотра истории выплат: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Member> member = facade.findMemberById(id);
            if (member.isPresent()) {
                System.out.println(member.get().getPaymentHistory());
                System.out.printf("Общая выплаченная сумма: %s%n", member.get().getTotalPaid());
                logger.info("Показана история выплат для {}", member.get().getName());
            } else {
                System.out.println("Сотрудник не найден!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void changeSalary() {
        try {
            System.out.print("\nID сотрудника для изменения зарплаты: ");
            int id = Integer.parseInt(scanner.nextLine());

            Optional<Member> member = facade.findMemberById(id);
            if (member.isPresent()) {
                System.out.printf("Текущая зарплата: %s%n", member.get().getBaseSalary());
                System.out.print("Введите новую зарплату: ");
                BigDecimal newSalary = new BigDecimal(scanner.nextLine());

                if (!InputValidator.isValidSalary(newSalary)) {
                    System.out.printf(
                            "Зарплата должна быть от %s до %s!%n",
                            AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                    return;
                }

                facade.adjustSalary(id, newSalary);
                System.out.println("Зарплата обновлена!");
                logger.info(
                        "Изменена зарплата для {}: с {} на {}",
                        member.get().getName(),
                        member.get().getBaseSalary(),
                        newSalary);
            } else {
                System.out.println("Сотрудник не найден!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void calculateBonuses() {
        System.out.println("\n=== РАСЧЁТ БОНУСОВ ===");
        System.out.println("1. Для конкретного сотрудника");
        System.out.println("2. Для всех сотрудников");
        System.out.println("3. Для определенной роли");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                System.out.print("ID сотрудника: ");
                int id = Integer.parseInt(scanner.nextLine());

                Optional<Member> member = facade.findMemberById(id);
                if (member.isPresent()) {
                    BigDecimal bonus = member.get().calculateBonus();
                    BigDecimal total = member.get().getBaseSalary().add(bonus);

                    System.out.printf("Бонус для %s: %s%n", member.get().getName(), bonus);
                    System.out.printf("Зарплата: %s%n", member.get().getBaseSalary());
                    System.out.printf("Итого к выплате: %s%n", total);
                    logger.info("Рассчитан бонус для {}: {}", member.get().getName(), bonus);
                } else {
                    System.out.println("Сотрудник не найден!");
                }
            } else if (choice == 2) {
                BigDecimal totalBonuses = facade.calculateTotalBonuses();
                BigDecimal totalSalaries = facade.calculateTotalMonthlySalary();

                System.out.printf("Общая сумма бонусов для всех сотрудников: %s%n", totalBonuses);
                System.out.printf("Общий фонд зарплат: %s%n", totalSalaries);
                System.out.printf("Общая сумма к выплате: %s%n", totalSalaries.add(totalBonuses));
                logger.info("Рассчитаны бонусы для всех сотрудников: {}", totalBonuses);

            } else if (choice == 3) {
                System.out.println("Выберите роль:");
                System.out.println("1. Игрок");
                System.out.println("2. Тренер");
                System.out.println("3. Менеджер");
                System.out.print("Ваш выбор: ");

                int roleChoice = Integer.parseInt(scanner.nextLine());
                String role =
                        switch (roleChoice) {
                            case 1 -> "Игрок";
                            case 2 -> "Тренер";
                            case 3 -> "Менеджер";
                            default -> null;
                        };

                if (role != null) {
                    List<Member> roleMembers = facade.filterByRole(role);
                    BigDecimal roleBonuses =
                            roleMembers.stream()
                                    .map(Member::calculateBonus)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                    System.out.printf("Общая сумма бонусов для %s: %s%n", role, roleBonuses);
                    System.out.printf("Количество сотрудников: %d%n", roleMembers.size());
                    System.out.printf(
                            "Средний бонус: %s%n",
                            roleBonuses.divide(
                                    BigDecimal.valueOf(roleMembers.size()),
                                    2,
                                    BigDecimal.ROUND_HALF_UP));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void showFinancialSummary() {
        BigDecimal totalSalary = facade.calculateTotalMonthlySalary();
        BigDecimal totalBonuses = facade.calculateTotalBonuses();
        BigDecimal totalPaid = facade.getTotalPaidToDate();
        BigDecimal plannedPayments = totalSalary.add(totalBonuses);

        System.out.println("\n=== ОБЩИЕ ФИНАНСОВЫЕ ПОКАЗАТЕЛИ ===");
        System.out.printf("Общий фонд зарплат: %s%n", totalSalary);
        System.out.printf("Общая сумма бонусов: %s%n", totalBonuses);
        System.out.printf("Общая выплаченная сумма: %s%n", totalPaid);
        System.out.printf("Планируемые выплаты (зарплаты + бонусы): %s%n", plannedPayments);
        System.out.printf("Остаток к выплате: %s%n", plannedPayments.subtract(totalPaid));

        System.out.println("\nРаспределение по ролям:");
        for (String role : Arrays.asList("Игрок", "Тренер", "Менеджер")) {
            List<Member> roleMembers = facade.filterByRole(role);
            if (!roleMembers.isEmpty()) {
                BigDecimal roleSalary =
                        roleMembers.stream()
                                .map(Member::getBaseSalary)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal roleBonuses =
                        roleMembers.stream()
                                .map(Member::calculateBonus)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                System.out.printf(
                        "  %s (%d чел.): Зарплаты: %s, Бонусы: %s, Итого: %s%n",
                        role,
                        roleMembers.size(),
                        roleSalary,
                        roleBonuses,
                        roleSalary.add(roleBonuses));
            }
        }
    }

    private void importExportMenu() {
        System.out.println("\n=== ИМПОРТ/ЭКСПОРТ ДАННЫХ ===");
        System.out.println("1. Экспорт в JSON");
        System.out.println("2. Экспорт в XML");
        System.out.println("3. Экспорт в CSV");
        System.out.println("4. Импорт из JSON");
        System.out.println("5. Импорт из XML");
        System.out.println("6. Импорт из CSV");
        System.out.println("7. Загрузить из бинарного файла");
        System.out.println("8. Сохранить в бинарный файл");
        System.out.println("9. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> exportToJson();
                case 2 -> exportToXml();
                case 3 -> exportToCsv();
                case 4 -> importFromJson();
                case 5 -> importFromXml();
                case 6 -> importFromCsv();
                case 7 -> loadFromBinary();
                case 8 -> saveToBinary();
                case 9 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void exportToJson() {
        boolean success = facade.exportToJson(AppConstants.JSON_DATA_FILE);
        if (success) {
            System.out.println("Данные экспортированы в JSON: " + AppConstants.JSON_DATA_FILE);
            logger.info("Экспорт данных в JSON: {} записей", facade.getMemberCount());
        } else {
            System.out.println("Ошибка экспорта в JSON!");
            logger.error("Ошибка экспорта данных в JSON");
        }
    }

    private void exportToXml() {
        boolean success = facade.exportToXml(AppConstants.XML_DATA_FILE);
        if (success) {
            System.out.println("Данные экспортированы в XML: " + AppConstants.XML_DATA_FILE);
            logger.info("Экспорт данных в XML: {} записей", facade.getMemberCount());
        } else {
            System.out.println("Ошибка экспорта в XML!");
            logger.error("Ошибка экспорта данных в XML");
        }
    }

    private void exportToCsv() {
        boolean success = facade.exportToCsv(AppConstants.CSV_DATA_FILE);
        if (success) {
            System.out.println("Данные экспортированы в CSV: " + AppConstants.CSV_DATA_FILE);
            logger.info("Экспорт данных в CSV: {} записей", facade.getMemberCount());
        } else {
            System.out.println("Ошибка экспорта в CSV!");
            logger.error("Ошибка экспорта данных в CSV");
        }
    }

    private void importFromJson() {
        try {
            System.out.print(
                    "Импортировать данные из JSON? Существующие данные будут удалены. (да/нет): ");
            String confirm = scanner.nextLine().toLowerCase();

            if (confirm.equals("да")) {
                List<Member> imported = facade.importFromJson(AppConstants.JSON_DATA_FILE);
                System.out.println("Импортировано " + imported.size() + " записей из JSON");
                logger.info("Импорт из JSON: {} записей", imported.size());
            }
        } catch (java.io.IOException e) {
            System.out.println("Ошибка импорта: " + e.getMessage());
            logger.error("Ошибка импорта из JSON: {}", e.getMessage());
        }
    }

    private void importFromXml() {
        try {
            System.out.print(
                    "Импортировать данные из XML? Существующие данные будут удалены. (да/нет): ");
            String confirm = scanner.nextLine().toLowerCase();

            if (confirm.equals("да")) {
                List<Member> imported = facade.importFromXml(AppConstants.XML_DATA_FILE);
                System.out.println("Импортировано " + imported.size() + " записей из XML");
                logger.info("Импорт из XML: {} записей", imported.size());
            }
        } catch (java.io.IOException e) {
            System.out.println("Ошибка импорта: " + e.getMessage());
            logger.error("Ошибка импорта из XML: {}", e.getMessage());
        }
    }

    private void importFromCsv() {
        try {
            System.out.print(
                    "Импортировать данные из CSV? Существующие данные будут удалены. (да/нет): ");
            String confirm = scanner.nextLine().toLowerCase();

            if (confirm.equals("да")) {
                List<Member> imported = facade.importFromCsv(AppConstants.CSV_DATA_FILE);
                System.out.println("Импортировано " + imported.size() + " записей из CSV");
                logger.info("Импорт из CSV: {} записей", imported.size());
            }
        } catch (java.io.IOException e) {
            System.out.println("Ошибка импорта: " + e.getMessage());
            logger.error("Ошибка импорта из CSV: {}", e.getMessage());
        }
    }

    private void loadFromBinary() {
        System.out.print(
                "Загрузить данные из бинарного файла? Существующие данные будут удалены. (да/нет): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("да")) {
            facade.loadFromBinary(AppConstants.BINARY_DATA_FILE);
            System.out.println(
                    "Данные загружены из бинарного файла: " + AppConstants.BINARY_DATA_FILE);
            System.out.println("Загружено записей: " + facade.getMemberCount());
            logger.info("Загрузка из бинарного файла: {} записей", facade.getMemberCount());
        }
    }

    private void saveToBinary() {
        facade.saveToBinary(AppConstants.BINARY_DATA_FILE);
        System.out.println("Данные сохранены в бинарный файл: " + AppConstants.BINARY_DATA_FILE);
        logger.info("Сохранение в бинарный файл: {} записей", facade.getMemberCount());
    }

    private void adminMenu() {
        if (!isAdmin) {
            System.out.print("\nВведите пароль администратора: ");
            String password = scanner.nextLine();

            if (!password.equals(AppConstants.ADMIN_PASSWORD)) {
                System.out.println("Неверный пароль!");
                logger.warn("Неудачная попытка входа администратора");
                return;
            }
            isAdmin = true;
            System.out.println("Доступ администратора получен!");
            adminLogger.info("Успешный вход администратора в систему");
        }

        while (isAdmin) {
            System.out.println("\n=== АДМИНИСТРАТОР ===");
            System.out.println("1. Удалить запись");
            System.out.println("2. Редактировать данные");
            System.out.println("3. Показать все ID");
            System.out.println("4. Сбросить ID счетчик");
            System.out.println("5. Очистить все данные");
            System.out.println("6. Вернуться в главное меню");
            System.out.print("Выберите действие: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                adminLogger.debug("Администратор выбрал пункт меню: {}", choice);

                switch (choice) {
                    case 1 -> deleteMember();
                    case 2 -> editMember();
                    case 3 -> showAllIds();
                    case 4 -> resetIdCounter();
                    case 5 -> clearAllData();
                    case 6 -> {
                        adminLogger.info("Администратор вышел из системы");
                        isAdmin = false;
                        return;
                    }
                    default -> {
                        System.out.println("Неверный выбор!");
                        adminLogger.warn("Неверный выбор пункта меню администратором: {}", choice);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода числа!");
                adminLogger.error("Ошибка ввода администратора: {}", e.getMessage());
            }
        }
    }

    private void deleteMember() {
        try {
            System.out.print("\nВведите ID для удаления: ");
            int id = Integer.parseInt(scanner.nextLine());
            adminLogger.info("Попытка удаления записи с ID: {}", id);

            Optional<Member> toRemove = facade.findMemberById(id);
            if (toRemove.isPresent()) {
                System.out.println("Удалить: " + toRemove.get());
                System.out.print("Подтвердите (да/нет): ");
                String confirm = scanner.nextLine().toLowerCase();

                if (confirm.equals("да")) {
                    boolean removed = facade.removeMember(id);
                    if (removed) {
                        System.out.println("Запись удалена!");
                        adminLogger.warn("Удалена запись: {}", toRemove.get());
                        logger.info("Администратор удалил запись с ID: {}", id);
                    } else {
                        System.out.println("Ошибка при удалении записи!");
                    }
                } else {
                    adminLogger.info("Удаление записи с ID: {} отменено пользователем", id);
                }
            } else {
                System.out.println("Запись с ID " + id + " не найдена!");
                adminLogger.warn("Попытка удаления несуществующей записи с ID: {}", id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void editMember() {
        try {
            System.out.print("\nВведите ID для редактирования: ");
            int id = Integer.parseInt(scanner.nextLine());
            adminLogger.info("Попытка редактирования записи с ID: {}", id);

            Optional<Member> member = facade.findMemberById(id);
            if (member.isPresent()) {
                System.out.println("Текущие данные: " + member.get());
                System.out.println("\nЧто вы хотите изменить?");
                System.out.println("1. Изменить имя");
                System.out.println("2. Изменить возраст");
                System.out.println("3. Изменить команду");
                System.out.println("4. Изменить зарплату");

                Member m = member.get();
                if (m instanceof Player) {
                    System.out.println("5. Изменить позицию (для игрока)");
                    System.out.println("6. Изменить номер (для игрока)");
                } else if (m instanceof Coach) {
                    System.out.println("5. Изменить специализацию (для тренера)");
                    System.out.println("6. Изменить сертификацию (для тренера)");
                } else if (m instanceof Manager) {
                    System.out.println("5. Изменить отдел (для менеджера)");
                    System.out.println("6. Изменить обязанности (для менеджера)");
                }

                System.out.print("Ваш выбор: ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> {
                        System.out.print("Введите новое имя: ");
                        String newName = scanner.nextLine();
                        if (InputValidator.isValidName(newName)) {
                            facade.editMemberName(id, newName);
                            System.out.println("Имя обновлено!");
                            adminLogger.info("Изменено имя для записи ID: {} на '{}'", id, newName);
                        } else {
                            System.out.println("Неверное имя!");
                        }
                    }
                    case 2 -> {
                        System.out.print("Введите новый возраст: ");
                        int newAge = Integer.parseInt(scanner.nextLine());
                        if (InputValidator.isValidAge(newAge)) {
                            facade.editMemberAge(id, newAge);
                            System.out.println("Возраст обновлен!");
                            adminLogger.info("Изменен возраст для записи ID: {} на {}", id, newAge);
                        } else {
                            System.out.printf(
                                    "Возраст должен быть от %d до %d лет!%n",
                                    AppConstants.MIN_AGE, AppConstants.MAX_AGE);
                        }
                    }
                    case 3 -> {
                        System.out.print("Введите новую команду: ");
                        String newTeam = scanner.nextLine();
                        if (InputValidator.isValidTeam(newTeam)) {
                            facade.editMemberTeam(id, newTeam);
                            System.out.println("Команда обновлена!");
                            adminLogger.info(
                                    "Изменена команда для записи ID: {} на '{}'", id, newTeam);
                        } else {
                            System.out.println("Неверное название команды!");
                        }
                    }
                    case 4 -> {
                        System.out.print("Введите новую зарплату: ");
                        BigDecimal newSalary = new BigDecimal(scanner.nextLine());
                        if (InputValidator.isValidSalary(newSalary)) {
                            facade.adjustSalary(id, newSalary);
                            System.out.println("Зарплата обновлена!");
                            adminLogger.info(
                                    "Изменена зарплата для записи ID: {} на {}", id, newSalary);
                        } else {
                            System.out.printf(
                                    "Зарплата должна быть от %s до %s!%n",
                                    AppConstants.MIN_SALARY, AppConstants.MAX_SALARY);
                        }
                    }
                    case 5 -> {
                        if (m instanceof Player) {
                            System.out.print("Введите новую позицию: ");
                            String newPosition = scanner.nextLine();
                            if (InputValidator.isValidPosition(newPosition)) {
                                facade.editPlayerPosition(id, newPosition);
                                System.out.println("Позиция обновлена!");
                                adminLogger.info(
                                        "Изменена позиция игрока ID: {} на '{}'", id, newPosition);
                            } else {
                                System.out.println("Неверная позиция!");
                            }
                        } else if (m instanceof Coach) {
                            System.out.print("Введите новую специализацию: ");
                            String newSpecialization = scanner.nextLine();
                            facade.editCoachSpecialization(id, newSpecialization);
                            System.out.println("Специализация обновлена!");
                            adminLogger.info(
                                    "Изменена специализация тренера ID: {} на '{}'",
                                    id,
                                    newSpecialization);
                        } else if (m instanceof Manager) {
                            System.out.print("Введите новый отдел: ");
                            String newDepartment = scanner.nextLine();
                            facade.editManagerDepartment(id, newDepartment);
                            System.out.println("Отдел обновлен!");
                            adminLogger.info(
                                    "Изменен отдел менеджера ID: {} на '{}'", id, newDepartment);
                        }
                    }
                    case 6 -> {
                        if (m instanceof Player) {
                            System.out.print("Введите новый номер: ");
                            int newNumber = Integer.parseInt(scanner.nextLine());
                            if (InputValidator.isValidJerseyNumber(newNumber)) {
                                facade.editPlayerJerseyNumber(id, newNumber);
                                System.out.println("Номер обновлен!");
                                adminLogger.info(
                                        "Изменен номер игрока ID: {} на {}", id, newNumber);
                            } else {
                                System.out.println("Номер должен быть от 1 до 99!");
                            }
                        } else if (m instanceof Coach) {
                            System.out.print("Введите новую сертификацию: ");
                            String newCertification = scanner.nextLine();
                            facade.editCoachCertification(id, newCertification);
                            System.out.println("Сертификация обновлена!");
                            adminLogger.info(
                                    "Изменена сертификация тренера ID: {} на '{}'",
                                    id,
                                    newCertification);
                        } else if (m instanceof Manager) {
                            System.out.print("Введите новые обязанности: ");
                            String newResponsibilities = scanner.nextLine();
                            facade.editManagerResponsibilities(id, newResponsibilities);
                            System.out.println("Обязанности обновлены!");
                            adminLogger.info(
                                    "Изменены обязанности менеджера ID: {} на '{}'",
                                    id,
                                    newResponsibilities);
                        }
                    }
                    default -> System.out.println("Неверный выбор!");
                }
            } else {
                System.out.println("Запись с ID " + id + " не найдена!");
                adminLogger.warn("Попытка редактирования несуществующей записи с ID: {}", id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void showAllIds() {
        System.out.println("\n=== ВСЕ ID В СИСТЕМЕ ===");
        System.out.printf("Общее количество: %d\n", facade.getMemberCount());
        facade.getAllMembers()
                .forEach(
                        m ->
                                System.out.println(
                                        "ID: "
                                                + m.getId()
                                                + " | "
                                                + m.getName()
                                                + " | "
                                                + m.getRole()
                                                + " | "
                                                + m.getTeam()));
        adminLogger.debug("Администратор запросил список всех ID");
    }

    private void resetIdCounter() {
        System.out.print("Сбросить счетчик ID? Текущие ID останутся без изменений. (да/нет): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("да")) {
            facade.resetIdCounter();
            System.out.println("Счетчик ID сброшен!");
            adminLogger.info("Администратор сбросил счетчик ID");
        }
    }

    private void clearAllData() {
        System.out.print("Вы уверены, что хотите удалить все данные? (да/нет): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("да")) {
            facade.clearAllMembers();
            System.out.println("Все данные удалены!");
            adminLogger.warn("Администратор удалил все данные");
            logger.info("Все данные клуба удалены администратором");
        }
    }

    private void reportsMenu() {
        System.out.println("\n=== ОТЧЁТЫ ===");
        System.out.println("1. Отчёт по всем членам клуба");
        System.out.println("2. Отчёт по игрокам");
        System.out.println("3. Отчёт по тренерам");
        System.out.println("4. Отчёт по менеджерам");
        System.out.println("5. Финансовый отчёт");
        System.out.println("6. Отчёт по тренировкам");
        System.out.println("7. Отчёт по ролям");
        System.out.println("8. Экспорт отчёта в файл");
        System.out.println("9. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> System.out.println(facade.generateAllMembersReport());
                case 2 -> System.out.println(facade.generatePlayersReport());
                case 3 -> System.out.println(facade.generateCoachesReport());
                case 4 -> System.out.println(facade.generateManagersReport());
                case 5 -> System.out.println(facade.generateFinancialReport());
                case 6 -> System.out.println(facade.generateTrainingReport());
                case 7 -> System.out.println(facade.generateRoleBasedReport());
                case 8 -> exportReportToFile();
                case 9 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void exportReportToFile() {
        System.out.println("\n=== ЭКСПОРТ ОТЧЁТА В ФАЙЛ ===");
        System.out.println("1. Экспорт отчёта по всем членам клуба");
        System.out.println("2. Экспорт финансового отчёта");
        System.out.println("3. Экспорт отчёта по тренировкам");
        System.out.println("4. Экспорт отчёта по ролям");
        System.out.print("Выберите тип отчёта: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            System.out.print("Введите имя файла (без расширения): ");
            String filename = scanner.nextLine();

            String reportContent = "";
            String fileExtension = ".txt";

            switch (choice) {
                case 1 -> {
                    reportContent = facade.generateAllMembersReport();
                    filename += "_members_report";
                }
                case 2 -> {
                    reportContent = facade.generateFinancialReport();
                    filename += "_financial_report";
                }
                case 3 -> {
                    reportContent = facade.generateTrainingReport();
                    filename += "_training_report";
                }
                case 4 -> {
                    reportContent = facade.generateRoleBasedReport();
                    filename += "_role_report";
                }
                default -> {
                    System.out.println("Неверный выбор!");
                    return;
                }
            }

            String fullPath = AppConstants.REPORTS_DIRECTORY + filename + fileExtension;
            boolean success = facade.exportReportToFile(reportContent, fullPath);

            if (success) {
                System.out.println("Отчёт сохранён в файл: " + fullPath);
                logger.info("Экспортирован отчёт в файл: {}", fullPath);
            } else {
                System.out.println("Ошибка при сохранении отчёта!");
                logger.error("Ошибка при экспорте отчёта в файл: {}", fullPath);
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void teamsMenu() {
        System.out.println("\n=== УПРАВЛЕНИЕ КОМАНДАМИ ===");
        System.out.println("1. Показать все команды");
        System.out.println("2. Показать распределение по командам");
        System.out.println("3. Показать состав команды");
        System.out.println("4. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> showAllTeams();
                case 2 -> showTeamDistribution();
                case 3 -> showTeamMembers();
                case 4 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void showAllTeams() {
        List<String> allTeams = facade.getAllTeams();
        System.out.println("\n=== ВСЕ КОМАНДЫ ===");
        System.out.printf("Всего команд: %d\n", allTeams.size());

        for (int i = 0; i < allTeams.size(); i++) {
            String team = allTeams.get(i);
            List<Member> teamMembers = facade.searchByTeam(team);
            System.out.printf("%d. %s (%d человек)%n", i + 1, team, teamMembers.size());
        }
    }

    private void showTeamMembers() {
        System.out.print("\nВведите название команды: ");
        String team = scanner.nextLine();

        List<Member> teamMembers = facade.searchByTeam(team);
        if (teamMembers.isEmpty()) {
            System.out.println("Команда не найдена или пуста!");
            return;
        }

        System.out.printf("\n=== СОСТАВ КОМАНДЫ: %s ===%n", team);
        System.out.printf("Всего членов: %d\n", teamMembers.size());

        Map<String, List<Member>> byRole =
                teamMembers.stream().collect(Collectors.groupingBy(Member::getRole));

        for (Map.Entry<String, List<Member>> entry : byRole.entrySet()) {
            System.out.printf("\n%s (%d):%n", entry.getKey(), entry.getValue().size());
            for (Member member : entry.getValue()) {
                System.out.printf("  %s", member.getName());
                if (member instanceof Player player) {
                    System.out.printf(" - %s, №%d", player.getPosition(), player.getJerseyNumber());
                } else if (member instanceof Coach coach) {
                    System.out.printf(" - %s", coach.getSpecialization());
                } else if (member instanceof Manager manager) {
                    System.out.printf(" - %s", manager.getDepartment());
                }
                System.out.println();
            }
        }

        System.out.println("\nСтатистика команды:");
        System.out.printf(
                "  Средний возраст: %.1f лет%n",
                teamMembers.stream().mapToInt(Member::getAge).average().orElse(0));
        System.out.printf(
                "  Средний стаж: %.1f лет%n",
                teamMembers.stream().mapToInt(Member::getExperience).average().orElse(0));
        System.out.printf(
                "  Общий фонд зарплат: %s%n",
                teamMembers.stream()
                        .map(Member::getBaseSalary)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        fileLogger.info(
                "Попытка загрузки данных из бинарного файла: {}", AppConstants.BINARY_DATA_FILE);

        try {
            facade.loadFromBinary(AppConstants.BINARY_DATA_FILE);
            System.out.println("Данные загружены! Записей: " + facade.getMemberCount());
            fileLogger.info(
                    "Успешно загружено {} записей из файла {}",
                    facade.getMemberCount(),
                    AppConstants.BINARY_DATA_FILE);

        } catch (Exception e) {
            System.out.println("Файл данных не найден. Будет создан новый.");
            fileLogger.warn(
                    "Файл данных не найден: {}. Создан новый файл.", AppConstants.BINARY_DATA_FILE);
        }
    }

    private void saveToFile() {
        fileLogger.info(
                "Попытка сохранения данных в бинарный файл: {}", AppConstants.BINARY_DATA_FILE);
        facade.saveToBinary(AppConstants.BINARY_DATA_FILE);
        System.out.println("Данные сохранены в бинарном формате!");
        fileLogger.info(
                "Успешно сохранено {} записей в файл {}",
                facade.getMemberCount(),
                AppConstants.BINARY_DATA_FILE);
    }

    private void displayResults(List<Member> results, String filterName) {
        if (results.isEmpty()) {
            System.out.println("Не найдено!");
            logger.info("{} не дал результатов", filterName);
        } else {
            System.out.printf(
                    "\n=== РЕЗУЛЬТАТЫ %s (%d) ===%n", filterName.toUpperCase(), results.size());
            results.forEach(System.out::println);
            logger.info("{} дал {} результатов", filterName, results.size());
        }
    }

    public void shutdown() {
        LoggerUtil.logShutdown(logger);
        saveToFile();
        if (scanner != null) {
            scanner.close();
        }
    }

    private void transferJournalMenu() {
        System.out.println("\n=== ЖУРНАЛ ТРАНСФЕРОВ ===");
        System.out.println("1. Просмотреть все записи");
        System.out.println("2. Просмотреть записи по участнику");
        System.out.println("3. Просмотреть последние 10 записей");
        System.out.println("4. Сгенерировать отчет");
        System.out.println("5. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> showAllTransferRecords();
                case 2 -> showTransferRecordsByMember();
                case 3 -> showRecentTransferRecords();
                case 4 -> generateTransferReport();
                case 5 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void showAllTransferRecords() {
        List<TransferJournal.TransferRecord> records = facade.getTransferRecords();
        if (records.isEmpty()) {
            System.out.println("Журнал трансферов пуст!");
            return;
        }

        System.out.println("\n=== ВСЕ ЗАПИСИ ТРАНСФЕРОВ ===");
        System.out.printf("Всего записей: %d\n", records.size());
        records.forEach(System.out::println);
    }

    private void showTransferRecordsByMember() {
        try {
            System.out.print("Введите ID участника: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            List<TransferJournal.TransferRecord> records =
                facade.getTransferRecordsByMember(memberId);

            if (records.isEmpty()) {
                System.out.println("Записей для данного участника не найдено!");
                return;
            }

            System.out.println("\n=== ЗАПИСИ ТРАНСФЕРОВ ДЛЯ УЧАСТНИКА ===");
            System.out.printf("Всего записей: %d\n", records.size());
            records.forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void showRecentTransferRecords() {
        List<TransferJournal.TransferRecord> records = facade.getTransferRecords();
        if (records.isEmpty()) {
            System.out.println("Журнал трансферов пуст!");
            return;
        }

        int limit = Math.min(10, records.size());
        System.out.println("\n=== ПОСЛЕДНИЕ 10 ЗАПИСЕЙ ТРАНСФЕРОВ ===");
        for (int i = 0; i < limit; i++) {
            System.out.println(records.get(i));
        }
    }

    private void generateTransferReport() {
        String report = facade.generateTransferReport();
        System.out.println(report);

        System.out.print("Сохранить отчет в файл? (да/нет): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("да")) {
            System.out.print("Введите имя файла: ");
            String filename = scanner.nextLine();
            boolean success = facade.exportReportToFile(report,
                "reports/transfer_" + filename + ".txt");

            if (success) {
                System.out.println("Отчет сохранен!");
            } else {
                System.out.println("Ошибка при сохранении отчета!");
            }
        }
    }

    private void salaryRegistryMenu() {
        System.out.println("\n=== РЕЕСТР ЗАРПЛАТ ===");
        System.out.println("1. Показать все зарплаты");
        System.out.println("2. Показать историю зарплат участника");
        System.out.println("3. Показать статистику по зарплатам");
        System.out.println("4. Показать средние зарплаты по ролям");
        System.out.println("5. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> showAllSalaries();
                case 2 -> showSalaryHistory();
                case 3 -> showSalaryStatistics();
                case 4 -> showAverageSalariesByRole();
                case 5 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void showAllSalaries() {
        List<Member> members = facade.getAllMembers();
        System.out.println("\n=== ВСЕ ЗАРПЛАТЫ ===");
        System.out.printf("Всего записей: %d\n", members.size());

        members.forEach(m -> {
            BigDecimal salary = facade.getSalaryFromRegistry(m.getId());
            System.out.printf("%s (ID: %d, %s): %s\n",
                m.getName(), m.getId(), m.getRole(), salary);
        });
    }

    private void showSalaryHistory() {
        try {
            System.out.print("Введите ID участника: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            SalaryRegistry.SalaryHistory history = facade.getSalaryHistory(memberId);
            if (history == null) {
                System.out.println("История зарплат не найдена!");
                return;
            }

            System.out.println("\n=== ИСТОРИЯ ЗАРПЛАТ ===");
            System.out.printf("Текущая зарплата: %s\n", history.getCurrentSalary());
            System.out.printf("Предыдущая зарплата: %s\n", history.getPreviousSalary());
            System.out.printf("Изменение: %s (%.2f%%)\n",
                history.getSalaryChange(), history.getSalaryGrowthPercentage());

            System.out.println("\nВсе записи:");
            history.getAllRecords().forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
        }
    }

    private void showSalaryStatistics() {
        List<Member> members = facade.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("Реестр зарплат пуст!");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal min = null;
        BigDecimal max = null;

        for (Member member : members) {
            BigDecimal salary = facade.getSalaryFromRegistry(member.getId());
            total = total.add(salary);

            if (min == null || salary.compareTo(min) < 0) {
                min = salary;
            }
            if (max == null || salary.compareTo(max) > 0) {
                max = salary;
            }
        }

        BigDecimal average = total.divide(BigDecimal.valueOf(members.size()),
            2, BigDecimal.ROUND_HALF_UP);

        System.out.println("\n=== СТАТИСТИКА ЗАРПЛАТ ===");
        System.out.printf("Общий фонд: %s\n", total);
        System.out.printf("Средняя зарплата: %s\n", average);
        System.out.printf("Минимальная зарплата: %s\n", min);
        System.out.printf("Максимальная зарплата: %s\n", max);
    }

    private void showAverageSalariesByRole() {
        Map<String, BigDecimal> averages = facade.getAverageSalaryByRoleFromRegistry();

        System.out.println("\n=== СРЕДНИЕ ЗАРПЛАТЫ ПО РОЛЯМ ===");
        for (Map.Entry<String, BigDecimal> entry : averages.entrySet()) {
            System.out.printf("%s: %s\n", entry.getKey(), entry.getValue());
        }
    }

    private void trainingRegistryMenu() {
        System.out.println("\n=== РЕЕСТР ТРЕНИРОВОК ===");
        System.out.println("1. Показать статистику по дням");
        System.out.println("2. Показать самые активные дни");
        System.out.println("3. Назад");
        System.out.print("Выберите действие: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> showTrainingStatisticsByDate();
                case 2 -> showMostActiveDays();
                case 3 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода!");
        }
    }

    private void showTrainingStatisticsByDate() {
        Map<LocalDate, Integer> counts = facade.getTrainingCountByDate();

        System.out.println("\n=== СТАТИСТИКА ТРЕНИРОВОК ПО ДНЯМ ===");
        System.out.printf("Всего дней с тренировками: %d\n", counts.size());

        counts.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .forEach(entry ->
                System.out.printf("%s: %d тренировок\n", entry.getKey(), entry.getValue()));
    }

    private void showMostActiveDays() {
        List<LocalDate> activeDays = facade.getMostActiveTrainingDays(5);

        System.out.println("\n=== САМЫЕ АКТИВНЫЕ ДНИ ===");
        for (int i = 0; i < activeDays.size(); i++) {
            LocalDate date = activeDays.get(i);
            int count = facade.getTrainingCountByDate().getOrDefault(date, 0);
            System.out.printf("%d. %s: %d тренировок\n", i + 1, date, count);
        }
    }

}
