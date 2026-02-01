package sportclub.domain.usecase;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import sportclub.domain.model.Coach;
import sportclub.domain.model.Manager;
import sportclub.domain.model.Member;
import sportclub.domain.model.Player;

public class ClubManager {
    private static final Logger logger = LogManager.getLogger(ClubManager.class);
    private static final Logger adminLogger = LogManager.getLogger("sportclub.admin");
    private static final Logger fileLogger = LogManager.getLogger("sportclub.file");

    private List<Member> members;
    private Scanner scanner;
    private boolean isAdmin;
    private final String ADMIN_PASSWORD = "admin123";
    private final String DATA_FILE = "club_data.dat";
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ClubManager() {
        members = new ArrayList<>();
        scanner = new Scanner(System.in);
        isAdmin = false;

        logger.info("=".repeat(50));
        logger.info("Инициализация системы управления спортивным клубом");
        logger.info("Время запуска: {}", LocalDateTime.now().format(dtf));
        logger.info("=".repeat(50));

        loadFromFile();
    }

    public void showMenu() {
        logger.info("Запуск интерактивного меню");

        while (true) {
            System.out.println("\n=== СПОРТИВНЫЙ КЛУБ ===");
            System.out.println("1. Просмотреть всех членов клуба");
            System.out.println("2. Добавить игрока");
            System.out.println("3. Добавить тренера");
            System.out.println("4. Добавить менеджера");
            System.out.println("5. Поиск по имени");
            System.out.println("6. Поиск по команде");
            System.out.println("7. Фильтр по роли");
            System.out.println("8. Фильтр по стажу");
            System.out.println("9. Фильтр по возрасту");
            System.out.println("10. Сортировка по имени");
            System.out.println("11. Сортировка по стажу");
            System.out.println("12. Статистика по ролям");
            System.out.println("13. Администрирование");
            System.out.println("14. Сохранить данные");
            System.out.println("15. Загрузить данные");
            System.out.println("16. Показать лог-статистику");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                logger.debug("Пользователь выбрал пункт меню: {}", choice);

                switch (choice) {
                    case 1 -> viewAllMembers();
                    case 2 -> addPlayer();
                    case 3 -> addCoach();
                    case 4 -> addManager();
                    case 5 -> searchByName();
                    case 6 -> searchByTeam();
                    case 7 -> filterByRole();
                    case 8 -> filterByExperience();
                    case 9 -> filterByAge();
                    case 10 -> sortByName();
                    case 11 -> sortByExperience();
                    case 12 -> showRoleStatistics();
                    case 13 -> adminMenu();
                    case 14 -> saveToFile();
                    case 15 -> loadFromFile();
                    case 16 -> showLogStatistics();
                    case 0 -> {
                        logger.info("Запрошен выход из программы");
                        saveToFile();
                        System.out.println("Данные сохранены. Выход...");
                        logger.info("Программа завершена");
                        logger.info("=".repeat(50));
                        return;
                    }
                    default -> {
                        System.out.println("Неверный выбор!");
                        logger.warn("Пользователь ввел неверный пункт меню: {}", choice);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите число!");
                logger.error("Ошибка преобразования ввода в число: {}", e.getMessage());
            } catch (Exception e) {
                logger.error("Непредвиденная ошибка в главном меню: {}", e.getMessage(), e);
                System.out.println("Произошла ошибка: " + e.getMessage());
            }
        }
    }

    private void viewAllMembers() {
        logger.debug("Просмотр всех членов клуба");

        if (members.isEmpty()) {
            System.out.println("Клуб пуст!");
            logger.info("Клуб пуст, нет членов для отображения");
            return;
        }
        System.out.println("\n=== ВСЕ ЧЛЕНЫ КЛУБА ===");
        members.forEach(System.out::println);
        logger.info("Отображено {} членов клуба", members.size());
    }

    private void addPlayer() {
        logger.info("Начало добавления игрока");
        System.out.println("\n=== ДОБАВЛЕНИЕ ИГРОКА ===");

        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();

            System.out.print("Возраст: ");
            int age = Integer.parseInt(scanner.nextLine());

            System.out.print("Команда: ");
            String team = scanner.nextLine();

            System.out.print("Позиция: ");
            String position = scanner.nextLine();

            System.out.print("Номер игрока: ");
            int number = Integer.parseInt(scanner.nextLine());

            Player player = new Player(name, age, team, position, number);
            members.add(player);

            System.out.println("Игрок добавлен!");
            logger.info(
                    "Добавлен новый игрок: {} (ID: {}, Команда: {}, Позиция: {}, Номер: {})",
                    name,
                    player.getId(),
                    team,
                    position,
                    number);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            logger.error("Ошибка при вводе числовых данных для игрока: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при добавлении игрока: {}", e.getMessage(), e);
            System.out.println("Ошибка при добавлении игрока: " + e.getMessage());
        }
    }

    private void addCoach() {
        logger.info("Начало добавления тренера");
        System.out.println("\n=== ДОБАВЛЕНИЕ ТРЕНЕРА ===");

        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();

            System.out.print("Возраст: ");
            int age = Integer.parseInt(scanner.nextLine());

            System.out.print("Команда: ");
            String team = scanner.nextLine();

            System.out.print("Специализация: ");
            String specialization = scanner.nextLine();

            System.out.print("Сертификация: ");
            String certification = scanner.nextLine();

            Coach coach = new Coach(name, age, team, specialization, certification);
            members.add(coach);

            System.out.println("Тренер добавлен!");
            logger.info(
                    "Добавлен новый тренер: {} (ID: {}, Команда: {}, Специализация: {}, Сертификация: {})",
                    name,
                    coach.getId(),
                    team,
                    specialization,
                    certification);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            logger.error("Ошибка при вводе числовых данных для тренера: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при добавлении тренера: {}", e.getMessage(), e);
            System.out.println("Ошибка при добавлении тренера: " + e.getMessage());
        }
    }

    private void addManager() {
        logger.info("Начало добавления менеджера");
        System.out.println("\n=== ДОБАВЛЕНИЕ МЕНЕДЖЕРА ===");

        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();

            System.out.print("Возраст: ");
            int age = Integer.parseInt(scanner.nextLine());

            System.out.print("Команда: ");
            String team = scanner.nextLine();

            System.out.print("Отдел: ");
            String department = scanner.nextLine();

            System.out.print("Обязанности: ");
            String responsibilities = scanner.nextLine();

            Manager manager = new Manager(name, age, team, department, responsibilities);
            members.add(manager);

            System.out.println("Менеджер добавлен!");
            logger.info(
                    "Добавлен новый менеджер: {} (ID: {}, Команда: {}, Отдел: {})",
                    name,
                    manager.getId(),
                    team,
                    department);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            logger.error("Ошибка при вводе числовых данных для менеджера: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при добавлении менеджера: {}", e.getMessage(), e);
            System.out.println("Ошибка при добавлении менеджера: " + e.getMessage());
        }
    }

    private void searchByName() {
        System.out.print("\nВведите имя для поиска: ");
        String searchName = scanner.nextLine().toLowerCase();
        logger.debug("Поиск по имени: '{}'", searchName);

        List<Member> results =
                members.stream()
                        .filter(m -> m.getName().toLowerCase().contains(searchName))
                        .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("Не найдено!");
            logger.info("Поиск по имени '{}' не дал результатов", searchName);
        } else {
            System.out.println("=== РЕЗУЛЬТАТЫ ПОИСКА ===");
            results.forEach(System.out::println);
            logger.info("Найдено {} результатов по поиску имени '{}'", results.size(), searchName);
        }
    }

    private void searchByTeam() {
        System.out.print("\nВведите название команды: ");
        String team = scanner.nextLine().toLowerCase();
        logger.debug("Поиск по команде: '{}'", team);

        List<Member> results =
                members.stream()
                        .filter(m -> m.getTeam().toLowerCase().contains(team))
                        .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("Не найдено!");
            logger.info("Поиск по команде '{}' не дал результатов", team);
        } else {
            System.out.println("=== РЕЗУЛЬТАТЫ ПОИСКА ===");
            results.forEach(System.out::println);
            logger.info("Найдено {} результатов по поиску команды '{}'", results.size(), team);
        }
    }

    private void filterByRole() {
        logger.debug("Фильтр по роли");
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
                List<Member> results =
                        members.stream()
                                .filter(m -> m.getRole().equals(role))
                                .collect(Collectors.toList());

                if (results.isEmpty()) {
                    System.out.println("Не найдено!");
                    logger.info("Фильтр по роли '{}' не дал результатов", role);
                } else {
                    System.out.println("=== РЕЗУЛЬТАТЫ ФИЛЬТРА ===");
                    results.forEach(System.out::println);
                    logger.info(
                            "Найдено {} результатов по фильтру роли '{}'", results.size(), role);
                }
            } else {
                System.out.println("Неверный выбор роли!");
                logger.warn("Пользователь выбрал неверный номер роли: {}", choice);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            logger.error("Ошибка при выборе роли: {}", e.getMessage());
        }
    }

    private void filterByExperience() {
        System.out.print("\nВведите минимальный стаж (лет): ");

        try {
            int minExp = Integer.parseInt(scanner.nextLine());
            logger.debug("Фильтр по стажу: минимум {} лет", minExp);

            List<Member> results =
                    members.stream()
                            .filter(m -> m.getExperience() >= minExp)
                            .collect(Collectors.toList());

            if (results.isEmpty()) {
                System.out.println("Не найдено!");
                logger.info("Фильтр по стажу (мин. {} лет) не дал результатов", minExp);
            } else {
                System.out.println("=== РЕЗУЛЬТАТЫ ФИЛЬТРА ===");
                results.forEach(System.out::println);
                logger.info(
                        "Найдено {} результатов по фильтру стажа (мин. {} лет)",
                        results.size(),
                        minExp);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            logger.error("Ошибка при вводе стажа: {}", e.getMessage());
        }
    }

    private void filterByAge() {
        logger.debug("Фильтр по возрасту");
        System.out.println("\nФильтр по возрасту:");
        System.out.println("1. До 18 лет");
        System.out.println("2. 18-30 лет");
        System.out.println("3. 31-45 лет");
        System.out.println("4. Старше 45 лет");
        System.out.print("Ваш выбор: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            List<Member> results = new ArrayList<>();

            switch (choice) {
                case 1 -> {
                    results =
                            members.stream()
                                    .filter(m -> m.getAge() < 18)
                                    .collect(Collectors.toList());
                    logger.debug("Выбрана возрастная группа: до 18 лет");
                }
                case 2 -> {
                    results =
                            members.stream()
                                    .filter(m -> m.getAge() >= 18 && m.getAge() <= 30)
                                    .collect(Collectors.toList());
                    logger.debug("Выбрана возрастная группа: 18-30 лет");
                }
                case 3 -> {
                    results =
                            members.stream()
                                    .filter(m -> m.getAge() >= 31 && m.getAge() <= 45)
                                    .collect(Collectors.toList());
                    logger.debug("Выбрана возрастная группа: 31-45 лет");
                }
                case 4 -> {
                    results =
                            members.stream()
                                    .filter(m -> m.getAge() > 45)
                                    .collect(Collectors.toList());
                    logger.debug("Выбрана возрастная группа: старше 45 лет");
                }
                default -> {
                    System.out.println("Неверный выбор!");
                    logger.warn("Пользователь выбрал неверный номер возрастной группы: {}", choice);
                    return;
                }
            }

            if (results.isEmpty()) {
                System.out.println("Не найдено!");
                logger.info("Фильтр по возрастной группе {} не дал результатов", choice);
            } else {
                System.out.println("=== РЕЗУЛЬТАТЫ ФИЛЬТРА ===");
                results.forEach(System.out::println);
                logger.info(
                        "Найдено {} результатов по фильтру возрастной группы {}",
                        results.size(),
                        choice);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода числа!");
            logger.error("Ошибка при выборе возрастной группы: {}", e.getMessage());
        }
    }

    private void sortByName() {
        logger.debug("Сортировка по имени");
        List<Member> sorted = new ArrayList<>(members);
        sorted.sort(Comparator.comparing(Member::getName));

        System.out.println("\n=== СОРТИРОВКА ПО ИМЕНИ ===");
        sorted.forEach(System.out::println);
        logger.info("Выполнена сортировка по имени, отображено {} записей", sorted.size());
    }

    private void sortByExperience() {
        logger.debug("Сортировка по стажу");
        List<Member> sorted = new ArrayList<>(members);
        sorted.sort(Comparator.comparingInt(Member::getExperience).reversed());

        System.out.println("\n=== СОРТИРОВКА ПО СТАЖУ ===");
        sorted.forEach(System.out::println);
        logger.info("Выполнена сортировка по стажу, отображено {} записей", sorted.size());
    }

    private void showRoleStatistics() {
        logger.debug("Показать статистику по ролям");

        long players = members.stream().filter(m -> m.getRole().equals("Игрок")).count();
        long coaches = members.stream().filter(m -> m.getRole().equals("Тренер")).count();
        long managers = members.stream().filter(m -> m.getRole().equals("Менеджер")).count();

        System.out.println("\n=== СТАТИСТИКА ПО РОЛЯМ ===");
        System.out.println("Игроки: " + players);
        System.out.println("Тренеры: " + coaches);
        System.out.println("Менеджеры: " + managers);
        System.out.println("Всего: " + members.size());

        logger.info(
                "Статистика ролей: Игроки={}, Тренеры={}, Менеджеры={}, Всего={}",
                players,
                coaches,
                managers,
                members.size());
    }

    private void adminMenu() {
        if (!isAdmin) {
            System.out.print("\nВведите пароль администратора: ");
            String password = scanner.nextLine();

            if (!password.equals(ADMIN_PASSWORD)) {
                System.out.println("Неверный пароль!");
                logger.warn(
                        "Неудачная попытка входа администратора с паролем: {}",
                        maskPassword(password));
                return;
            }
            isAdmin = true;
            System.out.println("Доступ администратора получен!");
            ThreadContext.put("user", "admin");
            adminLogger.info("Успешный вход администратора в систему");
            logger.info("Администратор вошел в систему");
        }

        while (isAdmin) {
            System.out.println("\n=== АДМИНИСТРАТОР ===");
            System.out.println("1. Удалить запись");
            System.out.println("2. Редактировать роль/стаж");
            System.out.println("3. Показать все ID");
            System.out.println("4. Вернуться в главное меню");
            System.out.print("Выберите действие: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                adminLogger.debug("Администратор выбрал пункт меню: {}", choice);

                switch (choice) {
                    case 1 -> deleteMember();
                    case 2 -> editMember();
                    case 3 -> showAllIds();
                    case 4 -> {
                        adminLogger.info("Администратор вышел из системы");
                        logger.info("Завершение сессии администратора");
                        ThreadContext.remove("user");
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
        System.out.print("\nВведите ID для удаления: ");
        int id = Integer.parseInt(scanner.nextLine());
        adminLogger.info("Попытка удаления записи с ID: {}", id);

        Member toRemove = members.stream().filter(m -> m.getId() == id).findFirst().orElse(null);

        if (toRemove != null) {
            System.out.println("Удалить: " + toRemove);
            System.out.print("Подтвердите (да/нет): ");
            String confirm = scanner.nextLine().toLowerCase();

            if (confirm.equals("да")) {
                members.remove(toRemove);
                System.out.println("Запись удалена!");
                adminLogger.warn("Удалена запись: {}", toRemove);
                logger.info("Администратор удалил запись с ID: {}", id);
            } else {
                adminLogger.info("Удаление записи с ID: {} отменено пользователем", id);
            }
        } else {
            System.out.println("Запись с ID " + id + " не найдена!");
            adminLogger.warn("Попытка удаления несуществующей записи с ID: {}", id);
        }
    }

    private void editMember() {
        System.out.print("\nВведите ID для редактирования: ");
        int id = Integer.parseInt(scanner.nextLine());
        adminLogger.info("Попытка редактирования записи с ID: {}", id);

        Member toEdit = members.stream().filter(m -> m.getId() == id).findFirst().orElse(null);

        if (toEdit != null) {
            System.out.println("Текущие данные: " + toEdit);
            System.out.println("\nЧто вы хотите изменить?");
            System.out.println("1. Изменить стаж (дату вступления)");
            System.out.println("2. Изменить команду");

            if (toEdit instanceof Player) {
                System.out.println("3. Изменить позицию (для игрока)");
                System.out.println("4. Изменить номер (для игрока)");
            } else if (toEdit instanceof Coach) {
                System.out.println("3. Изменить специализацию (для тренера)");
            } else if (toEdit instanceof Manager) {
                System.out.println("3. Изменить отдел (для менеджера)");
            }

            System.out.print("Ваш выбор: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    System.out.print("Введите новый год вступления: ");
                    int year = Integer.parseInt(scanner.nextLine());
                    System.out.print("Введите новый месяц (1-12): ");
                    int month = Integer.parseInt(scanner.nextLine());
                    System.out.print("Введите новый день: ");
                    int day = Integer.parseInt(scanner.nextLine());
                    toEdit.setJoinDate(java.time.LocalDate.of(year, month, day));
                    System.out.println("Стаж обновлен!");
                    adminLogger.info(
                            "Обновлен стаж для записи ID: {} (новая дата: {}-{}-{})",
                            id,
                            year,
                            month,
                            day);
                }
                case 2 -> {
                    System.out.print("Введите новую команду: ");
                    String newTeam = scanner.nextLine();
                    String oldTeam = toEdit.getTeam();
                    toEdit.setTeam(newTeam);
                    System.out.println("Команда обновлена!");
                    adminLogger.info(
                            "Изменена команда для записи ID: {} (с '{}' на '{}')",
                            id,
                            oldTeam,
                            newTeam);
                }
                case 3 -> {
                    if (toEdit instanceof Player player) {
                        System.out.print("Введите новую позицию: ");
                        String newPosition = scanner.nextLine();
                        String oldPosition = player.getPosition();
                        player.setPosition(newPosition);
                        System.out.println("Позиция обновлена!");
                        adminLogger.info(
                                "Изменена позиция игрока ID: {} (с '{}' на '{}')",
                                id,
                                oldPosition,
                                newPosition);
                    } else if (toEdit instanceof Coach coach) {
                        System.out.print("Введите новую специализацию: ");
                        String newSpecialization = scanner.nextLine();
                        String oldSpecialization = coach.getSpecialization();
                        coach.setSpecialization(newSpecialization);
                        System.out.println("Специализация обновлена!");
                        adminLogger.info(
                                "Изменена специализация тренера ID: {} (с '{}' на '{}')",
                                id,
                                oldSpecialization,
                                newSpecialization);
                    } else if (toEdit instanceof Manager manager) {
                        System.out.print("Введите новый отдел: ");
                        String newDepartment = scanner.nextLine();
                        String oldDepartment = manager.getDepartment();
                        manager.setDepartment(newDepartment);
                        System.out.println("Отдел обновлен!");
                        adminLogger.info(
                                "Изменен отдел менеджера ID: {} (с '{}' на '{}')",
                                id,
                                oldDepartment,
                                newDepartment);
                    }
                }
                case 4 -> {
                    if (toEdit instanceof Player player) {
                        System.out.print("Введите новый номер: ");
                        int newNumber = Integer.parseInt(scanner.nextLine());
                        int oldNumber = player.getJerseyNumber();
                        player.setJerseyNumber(newNumber);
                        System.out.println("Номер обновлен!");
                        adminLogger.info(
                                "Изменен номер игрока ID: {} (с {} на {})",
                                id,
                                oldNumber,
                                newNumber);
                    }
                }
            }
        } else {
            System.out.println("Запись с ID " + id + " не найдена!");
            adminLogger.warn("Попытка редактирования несуществующей записи с ID: {}", id);
        }
    }

    private void showAllIds() {
        System.out.println("\n=== ВСЕ ID В СИСТЕМЕ ===");
        members.forEach(
                m ->
                        System.out.println(
                                "ID: " + m.getId() + " | " + m.getName() + " | " + m.getRole()));
        adminLogger.debug("Администратор запросил список всех ID");
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        fileLogger.info("Попытка загрузки данных из файла: {}", DATA_FILE);

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            members = (List<Member>) ois.readObject();

            int maxId = members.stream().mapToInt(Member::getId).max().orElse(0);

            System.out.println("Данные загружены! Записей: " + members.size());
            fileLogger.info("Успешно загружено {} записей из файла {}", members.size(), DATA_FILE);
            logger.info("Данные клуба загружены, всего записей: {}", members.size());

        } catch (FileNotFoundException e) {
            System.out.println("Файл данных не найден. Будет создан новый.");
            fileLogger.warn("Файл данных не найден: {}. Создан новый файл.", DATA_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка загрузки данных: " + e.getMessage());
            fileLogger.error(
                    "Ошибка загрузки данных из файла {}: {}", DATA_FILE, e.getMessage(), e);
            logger.error("Критическая ошибка загрузки данных: {}", e.getMessage());
        }
    }

    private void saveToFile() {
        fileLogger.info("Попытка сохранения данных в файл: {}", DATA_FILE);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(members);
            System.out.println("Данные сохранены!");
            fileLogger.info("Успешно сохранено {} записей в файл {}", members.size(), DATA_FILE);
            logger.info("Данные клуба сохранены, всего записей: {}", members.size());

        } catch (IOException e) {
            System.out.println("Ошибка сохранения данных: " + e.getMessage());
            fileLogger.error(
                    "Ошибка сохранения данных в файл {}: {}", DATA_FILE, e.getMessage(), e);
            logger.error("Критическая ошибка сохранения данных: {}", e.getMessage());
        }
    }

    private void showLogStatistics() {
        System.out.println("\n=== СТАТИСТИКА ЛОГИРОВАНИЯ ===");
        System.out.println("Текущий уровень логирования: INFO");
        System.out.println("Всего записей в клубе: " + members.size());
        System.out.println("Файлы логов находятся в папке: logs/");
        System.out.println("Основной лог файл: sportclub.log");
        System.out.println("Лог администратора: admin-audit.log");
        logger.info("Пользователь запросил статистику логирования");
    }

    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "[empty]";
        }
        return "***" + password.length() + "***";
    }

    public void shutdown() {
        logger.info("Завершение работы ClubManager");
        saveToFile();
        if (scanner != null) {
            scanner.close();
        }
        logger.info("=".repeat(50));
        logger.info("Система управления спортивным клубом завершена");
        logger.info("Время завершения: {}", LocalDateTime.now().format(dtf));
        logger.info("=".repeat(50));
    }
}
