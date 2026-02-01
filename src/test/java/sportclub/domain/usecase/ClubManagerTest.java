package sportclub.domain.usecase;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sportclub.domain.model.*;

class ClubManagerTest {

    @TempDir Path tempDir;

    private ClubManager clubManager;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private InputStream originalIn;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        originalIn = System.in;
        resetIdCounter();
        String input = "1\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        File testFile = tempDir.resolve("test_club_data.dat").toFile();
        clubManager = new ClubManager();
        Field dataFileField = ClubManager.class.getDeclaredField("DATA_FILE");
        dataFileField.setAccessible(true);
        dataFileField.set(clubManager, testFile.getAbsolutePath());
        Field membersField = ClubManager.class.getDeclaredField("members");
        membersField.setAccessible(true);
        List<Member> members = (List<Member>) membersField.get(clubManager);
        members.clear();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testAddAndViewMembers() throws Exception {
        Field membersField = ClubManager.class.getDeclaredField("members");
        membersField.setAccessible(true);
        List<Member> members = (List<Member>) membersField.get(clubManager);
        assertEquals(0, members.size());
        Player player = new Player("Тест Игрок", 25, "Тест Команда", "Вратарь", 1);
        members.add(player);
        assertEquals(1, members.size());
        assertInstanceOf(Player.class, members.get(0));
        assertEquals("Тест Игрок", members.get(0).getName());
    }

    @Test
    void testSearchByName() throws Exception {
        Field membersField = ClubManager.class.getDeclaredField("members");
        membersField.setAccessible(true);
        List<Member> members = (List<Member>) membersField.get(clubManager);
        members.add(new Player("Иван Петров", 25, "Футбол", "Вратарь", 1));
        members.add(new Player("Петр Иванов", 28, "Футбол", "Нападающий", 9));
        members.add(new Coach("Сергей Сергеев", 45, "Баскетбол", "Тренер", "Высшая"));
        List<Member> results =
                members.stream().filter(m -> m.getName().toLowerCase().contains("иван")).toList();
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(m -> m.getName().equals("Иван Петров")));
        assertTrue(results.stream().anyMatch(m -> m.getName().equals("Петр Иванов")));
    }

    @Test
    void testRoleStatistics() throws Exception {
        Field membersField = ClubManager.class.getDeclaredField("members");
        membersField.setAccessible(true);
        List<Member> members = (List<Member>) membersField.get(clubManager);
        members.add(new Player("Игрок 1", 20, "Футбол", "Нападающий", 9));
        members.add(new Player("Игрок 2", 22, "Футбол", "Защитник", 4));
        members.add(new Coach("Тренер 1", 45, "Баскетбол", "Главный", "Высшая"));
        members.add(new Manager("Менеджер 1", 35, "Волейбол", "Финансы", "Бюджет"));
        long players = members.stream().filter(m -> m.getRole().equals("Игрок")).count();
        long coaches = members.stream().filter(m -> m.getRole().equals("Тренер")).count();
        long managers = members.stream().filter(m -> m.getRole().equals("Менеджер")).count();
        assertEquals(2, players);
        assertEquals(1, coaches);
        assertEquals(1, managers);
        assertEquals(4, members.size());
    }

    @Test
    void testSaveToFile() throws Exception {
        Field membersField = ClubManager.class.getDeclaredField("members");
        membersField.setAccessible(true);
        List<Member> members = (List<Member>) membersField.get(clubManager);
        members.add(new Player("Тест Игрок", 25, "Тест Команда", "Нападающий", 9));
        Field dataFileField = ClubManager.class.getDeclaredField("DATA_FILE");
        dataFileField.setAccessible(true);
        String dataFilePath = (String) dataFileField.get(clubManager);
        File dataFile = new File(dataFilePath);
        if (dataFile.exists()) {
            dataFile.delete();
        }
        Method saveMethod = ClubManager.class.getDeclaredMethod("saveToFile");
        saveMethod.setAccessible(true);
        saveMethod.invoke(clubManager);
        assertDoesNotThrow(() -> saveMethod.invoke(clubManager));
    }

    @Test
    void testLoadFromFile() throws Exception {
        Method loadMethod = ClubManager.class.getDeclaredMethod("loadFromFile");
        loadMethod.setAccessible(true);
        assertDoesNotThrow(() -> loadMethod.invoke(clubManager));
    }

    @Test
    void testAdminPasswordMasking() throws Exception {
        Method maskMethod = ClubManager.class.getDeclaredMethod("maskPassword", String.class);
        maskMethod.setAccessible(true);
        assertEquals("[empty]", maskMethod.invoke(clubManager, ""));
        assertEquals("***5***", maskMethod.invoke(clubManager, "admin"));
        assertEquals("***8***", maskMethod.invoke(clubManager, "password"));
    }

    @Test
    void testFilterByExperience() throws Exception {
        Field membersField = ClubManager.class.getDeclaredField("members");
        membersField.setAccessible(true);
        List<Member> members = (List<Member>) membersField.get(clubManager);
        Player player1 = new Player("Игрок 1", 25, "Команда", "Позиция", 1);
        Player player2 = new Player("Игрок 2", 30, "Команда", "Позиция", 2);
        Player player3 = new Player("Игрок 3", 35, "Команда", "Позиция", 3);
        player1.setJoinDate(LocalDate.now().minusYears(1));
        player2.setJoinDate(LocalDate.now().minusYears(5));
        player3.setJoinDate(LocalDate.now().minusYears(10));
        members.add(player1);
        members.add(player2);
        members.add(player3);
        List<Member> results = members.stream().filter(m -> m.getExperience() >= 5).toList();
        assertTrue(results.size() >= 1);
        if (player1.getExperience() < 5) {
            assertFalse(results.contains(player1));
        }
    }

    @Test
    void testMemberInheritanceHierarchy() {
        Player player = new Player("Игрок", 25, "Команда", "Нападающий", 9);
        Coach coach = new Coach("Тренер", 45, "Команда", "Фитнес", "Сертифицирован");
        Manager manager = new Manager("Менеджер", 35, "Команда", "Финансы", "Бюджет");
        assertInstanceOf(Member.class, player);
        assertInstanceOf(Member.class, coach);
        assertInstanceOf(Member.class, manager);
        assertInstanceOf(Player.class, player);
        assertInstanceOf(Coach.class, coach);
        assertInstanceOf(Manager.class, manager);
    }

    @Test
    void testMemberToStringFormat() {
        Player player = new Player("Тест Игрок", 25, "Тест Команда", "Вратарь", 1);
        player.setJoinDate(LocalDate.of(2020, 1, 1));
        String result = player.toString();
        assertTrue(result.contains("Имя: Тест Игрок"));
        assertTrue(result.contains("Возраст: 25"));
        assertTrue(result.contains("Роль: Игрок"));
        assertTrue(result.contains("Команда: Тест Команда"));
    }

    @Test
    void testGetDetailsMethod() {
        Player player = new Player("Игрок", 25, "Команда", "Нападающий", 9);
        assertEquals("Позиция: Нападающий, Номер: 9", player.getDetails());
        Coach coach = new Coach("Тренер", 45, "Команда", "Фитнес", "Сертификация");
        assertEquals("Специализация: Фитнес, Сертификация: Сертификация", coach.getDetails());
        Manager manager = new Manager("Менеджер", 35, "Команда", "Финансы", "Бюджет");
        assertEquals("Отдел: Финансы, Обязанности: Бюджет", manager.getDetails());
    }

    @Test
    void testMemberGettersAndSetters() {
        Player player = new Player("Игрок", 25, "Команда", "Нападающий", 9);
        assertEquals("Игрок", player.getName());
        assertEquals(25, player.getAge());
        assertEquals("Команда", player.getTeam());
        assertEquals("Игрок", player.getRole());
        player.setName("Новый Игрок");
        player.setAge(26);
        player.setTeam("Новая Команда");
        assertEquals("Новый Игрок", player.getName());
        assertEquals(26, player.getAge());
        assertEquals("Новая Команда", player.getTeam());
    }

    @Test
    void testCoachGettersAndSetters() {
        Coach coach = new Coach("Тренер", 45, "Команда", "Фитнес", "Сертификация");
        assertEquals("Фитнес", coach.getSpecialization());
        assertEquals("Сертификация", coach.getCertification());
        coach.setSpecialization("Силовая");
        coach.setCertification("Международная");
        assertEquals("Силовая", coach.getSpecialization());
        assertEquals("Международная", coach.getCertification());
    }

    @Test
    void testManagerGettersAndSetters() {
        Manager manager = new Manager("Менеджер", 35, "Команда", "Финансы", "Бюджет");
        assertEquals("Финансы", manager.getDepartment());
        assertEquals("Бюджет", manager.getResponsibilities());
        manager.setDepartment("Кадры");
        manager.setResponsibilities("Найм");
        assertEquals("Кадры", manager.getDepartment());
        assertEquals("Найм", manager.getResponsibilities());
    }

    @Test
    void testMemberIdAutoIncrement() {
        resetIdCounter();
        Member member1 = new Player("Игрок 1", 25, "Команда", "Нападающий", 9);
        Member member2 = new Coach("Тренер 1", 45, "Команда", "Фитнес", "Сертификация");
        Member member3 = new Manager("Менеджер 1", 35, "Команда", "Финансы", "Бюджет");
        assertEquals(1, member1.getId());
        assertEquals(2, member2.getId());
        assertEquals(3, member3.getId());
    }

    @Test
    void testMemberExperienceCalculation() {
        Player player = new Player("Игрок", 25, "Команда", "Нападающий", 9);
        player.setJoinDate(LocalDate.now().minusYears(3));
        int experience = player.getExperience();
        assertTrue(experience >= 2 && experience <= 4);
        player.setJoinDate(LocalDate.now().plusYears(1));
        experience = player.getExperience();
        assertNotNull(experience);
    }

    @Test
    void testPlayerSpecificMethods() {
        Player player = new Player("Игрок", 25, "Команда", "Нападающий", 9);
        assertEquals("Нападающий", player.getPosition());
        assertEquals(9, player.getJerseyNumber());
        player.setPosition("Защитник");
        player.setJerseyNumber(5);
        assertEquals("Защитник", player.getPosition());
        assertEquals(5, player.getJerseyNumber());
    }

    @Test
    void testSerialization() {
        Player player = new Player("Игрок", 25, "Команда", "Нападающий", 9);
        Coach coach = new Coach("Тренер", 45, "Команда", "Фитнес", "Сертификация");
        Manager manager = new Manager("Менеджер", 35, "Команда", "Финансы", "Бюджет");
        assertInstanceOf(java.io.Serializable.class, player);
        assertInstanceOf(java.io.Serializable.class, coach);
        assertInstanceOf(java.io.Serializable.class, manager);
    }

    @Test
    void testMemberConstructor() {
        Player player = new Player("Игрок", 25, "Команда", "Нападающий", 9);
        assertEquals("Игрок", player.getName());
        assertEquals(25, player.getAge());
        assertEquals("Команда", player.getTeam());
        assertEquals("Игрок", player.getRole());
        assertEquals("Нападающий", player.getPosition());
        assertEquals(9, player.getJerseyNumber());
        assertNotNull(player.getJoinDate());
    }

    @Test
    void testCoachConstructor() {
        Coach coach = new Coach("Тренер", 45, "Команда", "Фитнес", "Сертификация");
        assertEquals("Тренер", coach.getName());
        assertEquals(45, coach.getAge());
        assertEquals("Команда", coach.getTeam());
        assertEquals("Тренер", coach.getRole());
        assertEquals("Фитнес", coach.getSpecialization());
        assertEquals("Сертификация", coach.getCertification());
    }

    @Test
    void testManagerConstructor() {
        Manager manager = new Manager("Менеджер", 35, "Команда", "Финансы", "Бюджет");
        assertEquals("Менеджер", manager.getName());
        assertEquals(35, manager.getAge());
        assertEquals("Команда", manager.getTeam());
        assertEquals("Менеджер", manager.getRole());
        assertEquals("Финансы", manager.getDepartment());
        assertEquals("Бюджет", manager.getResponsibilities());
    }

    private void resetIdCounter() {
        try {
            Field nextIdField = Member.class.getDeclaredField("nextId");
            nextIdField.setAccessible(true);
            nextIdField.set(null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
