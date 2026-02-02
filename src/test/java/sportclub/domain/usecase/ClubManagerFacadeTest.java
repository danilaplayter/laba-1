package sportclub.domain.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import sportclub.domain.interfaces.Payable;
import sportclub.domain.model.*;
import sportclub.domain.service.*;
import sportclub.utils.InputValidator;

class ClubManagerFacadeTest {
    private ClubManagerFacade facade;
    private MemberManagementService memberService;
    private MemberSearchService searchService;
    private MemberSortService sortService;
    private TrainingService trainingService;
    private FinanceService financeService;
    private StatisticsService statisticsService;
    private ReportService reportService;
    private ImportExportService importExportService;
    @TempDir File tempDir;

    @BeforeEach
    void setUp() {
        memberService = mock(MemberManagementService.class);
        searchService = mock(MemberSearchService.class);
        sortService = mock(MemberSortService.class);
        trainingService = mock(TrainingService.class);
        financeService = mock(FinanceService.class);
        statisticsService = mock(StatisticsService.class);
        reportService = mock(ReportService.class);
        importExportService = mock(ImportExportService.class);
        facade = new ClubManagerFacade();
        try {
            setField(facade, "memberService", memberService);
            setField(facade, "searchService", searchService);
            setField(facade, "sortService", sortService);
            setField(facade, "trainingService", trainingService);
            setField(facade, "financeService", financeService);
            setField(facade, "statisticsService", statisticsService);
            setField(facade, "reportService", reportService);
            setField(facade, "importExportService", importExportService);
        } catch (Exception e) {
            fail("Не удалось установить зависимости через рефлексию: " + e.getMessage());
        }
        Member.resetIdCounter();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testAddPlayer() {
        Player mockPlayer =
                new Player(
                        "Тест Игрок", 25, "Тест Команда", "Вратарь", 1, BigDecimal.valueOf(50000));
        when(memberService.addPlayer(
                        anyString(),
                        anyInt(),
                        anyString(),
                        anyString(),
                        anyInt(),
                        any(BigDecimal.class)))
                .thenReturn(mockPlayer);
        Member result =
                facade.addPlayer(
                        "Тест Игрок", 25, "Тест Команда", "Вратарь", 1, BigDecimal.valueOf(50000));
        assertNotNull(result);
        assertEquals("Тест Игрок", result.getName());
        assertEquals("Игрок", result.getRole());
        assertInstanceOf(Player.class, result);
    }

    @Test
    void testAddCoach() {
        Coach mockCoach =
                new Coach(
                        "Тест Тренер",
                        40,
                        "Тест Команда",
                        "Фитнес",
                        "Высшая",
                        BigDecimal.valueOf(70000));
        when(memberService.addCoach(
                        anyString(),
                        anyInt(),
                        anyString(),
                        anyString(),
                        anyString(),
                        any(BigDecimal.class)))
                .thenReturn(mockCoach);
        Member result =
                facade.addCoach(
                        "Тест Тренер",
                        40,
                        "Тест Команда",
                        "Фитнес",
                        "Высшая",
                        BigDecimal.valueOf(70000));
        assertNotNull(result);
        assertEquals("Тест Тренер", result.getName());
        assertEquals("Тренер", result.getRole());
        assertInstanceOf(Coach.class, result);
    }

    @Test
    void testAddManager() {
        Manager mockManager =
                new Manager(
                        "Тест Менеджер",
                        35,
                        "Тест Команда",
                        "Финансы",
                        "Бюджет",
                        BigDecimal.valueOf(60000));
        when(memberService.addManager(
                        anyString(),
                        anyInt(),
                        anyString(),
                        anyString(),
                        anyString(),
                        any(BigDecimal.class)))
                .thenReturn(mockManager);
        Member result =
                facade.addManager(
                        "Тест Менеджер",
                        35,
                        "Тест Команда",
                        "Финансы",
                        "Бюджет",
                        BigDecimal.valueOf(60000));
        assertNotNull(result);
        assertEquals("Тест Менеджер", result.getName());
        assertEquals("Менеджер", result.getRole());
        assertInstanceOf(Manager.class, result);
    }

    @Test
    void testRemoveMember() {
        when(memberService.removeMember(1)).thenReturn(true);
        boolean result = facade.removeMember(1);
        assertTrue(result);
        verify(memberService).removeMember(1);
    }

    @Test
    void testFindMemberById() {
        Member mockMember =
                new Player("Тест", 25, "Команда", "Позиция", 1, BigDecimal.valueOf(50000));
        when(memberService.findMemberById(1)).thenReturn(Optional.of(mockMember));
        Optional<Member> result = facade.findMemberById(1);
        assertTrue(result.isPresent());
        assertEquals("Тест", result.get().getName());
    }

    @Test
    void testGetAllMembers() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player("Игрок", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)),
                        new Coach(
                                "Тренер",
                                40,
                                "Команда",
                                "Фитнес",
                                "Высшая",
                                BigDecimal.valueOf(70000)));
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        List<Member> result = facade.getAllMembers();
        assertEquals(2, result.size());
        assertEquals("Игрок", result.get(0).getName());
        assertEquals("Тренер", result.get(1).getName());
    }

    @Test
    void testSearchByName() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player(
                                "Иван Петров",
                                25,
                                "Команда",
                                "Вратарь",
                                1,
                                BigDecimal.valueOf(50000)),
                        new Player(
                                "Петр Иванов",
                                28,
                                "Команда",
                                "Нападающий",
                                9,
                                BigDecimal.valueOf(60000)));
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(searchService.searchByName(mockMembers, "Иван"))
                .thenReturn(Collections.singletonList(mockMembers.get(0)));
        List<Member> result = facade.searchByName("Иван");
        assertEquals(1, result.size());
        assertEquals("Иван Петров", result.get(0).getName());
    }

    @Test
    void testFilterByRole() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player("Игрок", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)),
                        new Coach(
                                "Тренер",
                                40,
                                "Команда",
                                "Фитнес",
                                "Высшая",
                                BigDecimal.valueOf(70000)));
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(searchService.filterByRole(mockMembers, "Игрок"))
                .thenReturn(Collections.singletonList(mockMembers.get(0)));
        List<Member> result = facade.filterByRole("Игрок");
        assertEquals(1, result.size());
        assertEquals("Игрок", result.get(0).getName());
        assertEquals("Игрок", result.get(0).getRole());
    }

    @Test
    void testFilterByAgeRange() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player(
                                "Молодой", 20, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)),
                        new Player(
                                "Взрослый",
                                30,
                                "Команда",
                                "Нападающий",
                                9,
                                BigDecimal.valueOf(60000)));
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(searchService.filterByAgeRange(mockMembers, 20, 25))
                .thenReturn(Collections.singletonList(mockMembers.get(0)));
        List<Member> result = facade.filterByAgeRange(20, 25);
        assertEquals(1, result.size());
        assertEquals("Молодой", result.get(0).getName());
        assertEquals(20, result.get(0).getAge());
    }

    @Test
    void testSortByName() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player("Борис", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)),
                        new Player(
                                "Алексей",
                                28,
                                "Команда",
                                "Нападающий",
                                9,
                                BigDecimal.valueOf(60000)));
        List<Member> sortedMembers = Arrays.asList(mockMembers.get(1), mockMembers.get(0));
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(sortService.sortMembers(mockMembers, MemberSortService.SortType.BY_NAME_ASC))
                .thenReturn(sortedMembers);
        List<Member> result = facade.sortByName(true);
        assertEquals(2, result.size());
        assertEquals("Алексей", result.get(0).getName());
        assertEquals("Борис", result.get(1).getName());
    }

    @Test
    void testSortBySalary() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player(
                                "Игрок1", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)),
                        new Player(
                                "Игрок2",
                                28,
                                "Команда",
                                "Нападающий",
                                9,
                                BigDecimal.valueOf(70000)));
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(sortService.sortMembers(mockMembers, MemberSortService.SortType.BY_SALARY_DESC))
                .thenReturn(Arrays.asList(mockMembers.get(1), mockMembers.get(0)));
        List<Member> result = facade.sortBySalary(false);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.valueOf(70000), result.get(0).getBaseSalary());
        assertEquals(BigDecimal.valueOf(50000), result.get(1).getBaseSalary());
    }

    @Test
    void testGetRoleDistribution() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player(
                                "Игрок1", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)),
                        new Player(
                                "Игрок2",
                                28,
                                "Команда",
                                "Нападающий",
                                9,
                                BigDecimal.valueOf(60000)),
                        new Coach(
                                "Тренер",
                                40,
                                "Команда",
                                "Фитнес",
                                "Высшая",
                                BigDecimal.valueOf(70000)));
        Map<String, Long> mockDistribution =
                Map.of(
                        "Игрок", 2L,
                        "Тренер", 1L);
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(statisticsService.getRoleDistribution(mockMembers)).thenReturn(mockDistribution);
        Map<String, Long> result = facade.getRoleDistribution();
        assertEquals(2, result.size());
        assertEquals(2L, result.get("Игрок"));
        assertEquals(1L, result.get("Тренер"));
    }

    @Test
    void testGetTopBySalary() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player(
                                "Игрок1", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)),
                        new Player(
                                "Игрок2",
                                28,
                                "Команда",
                                "Нападающий",
                                9,
                                BigDecimal.valueOf(70000)),
                        new Player(
                                "Игрок3", 30, "Команда", "Защитник", 5, BigDecimal.valueOf(60000)));
        List<Member> topMembers = Arrays.asList(mockMembers.get(1), mockMembers.get(2));
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(statisticsService.getTopBySalary(mockMembers, 2)).thenReturn(topMembers);
        List<Member> result = facade.getTopBySalary(2);
        assertEquals(2, result.size());
        assertEquals("Игрок2", result.get(0).getName());
        assertEquals(BigDecimal.valueOf(70000), result.get(0).getBaseSalary());
    }

    @Test
    void testConductTraining() {
        Player mockPlayer =
                new Player("Игрок", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000));
        when(memberService.findMemberById(1)).thenReturn(Optional.of(mockPlayer));
        when(trainingService.conductTraining(mockPlayer, 60, 8, LocalDate.now()))
                .thenReturn("Тренировка успешно проведена");
        String result = facade.conductTraining(1, 60, 8);
        assertEquals("Тренировка успешно проведена", result);
    }

    @Test
    void testPaySalary() {
        Player mockPlayer =
                new Player("Игрок", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000));
        Payable.PaymentResult paymentResult =
                new Payable.PaymentResult(
                        BigDecimal.valueOf(55000),
                        BigDecimal.valueOf(5000),
                        LocalDate.now(),
                        true,
                        "Зарплата выплачена");
        when(memberService.findMemberById(1)).thenReturn(Optional.of(mockPlayer));
        when(financeService.paySalary(mockPlayer, LocalDate.now())).thenReturn(paymentResult);
        Payable.PaymentResult result = facade.paySalary(1, LocalDate.now());
        assertTrue(result.successful());
        assertEquals("Зарплата выплачена", result.message());
        assertEquals(BigDecimal.valueOf(55000), result.amount());
    }

    @Test
    void testCalculateTotalMonthlySalary() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player(
                                "Игрок1", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)),
                        new Coach(
                                "Тренер",
                                40,
                                "Команда",
                                "Фитнес",
                                "Высшая",
                                BigDecimal.valueOf(70000)));
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(financeService.calculateTotalSalaries(mockMembers))
                .thenReturn(BigDecimal.valueOf(120000));
        BigDecimal result = facade.calculateTotalMonthlySalary();
        assertEquals(BigDecimal.valueOf(120000), result);
    }

    @Test
    void testGenerateAllMembersReport() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player(
                                "Игрок", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)));
        String mockReport = "ОТЧЕТ ПО ВСЕМ ЧЛЕНАМ КЛУБА\nИгрок";
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(reportService.generateAllMembersReport(mockMembers)).thenReturn(mockReport);
        String result = facade.generateAllMembersReport();
        assertNotNull(result);
        assertTrue(result.contains("ОТЧЕТ ПО ВСЕМ ЧЛЕНАМ КЛУБА"));
    }

    @Test
    void testExportToJson() {
        List<Member> mockMembers =
                Arrays.asList(
                        new Player(
                                "Игрок", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000)));
        String filename = tempDir.getPath() + "/test.json";
        when(memberService.getAllMembers()).thenReturn(mockMembers);
        when(importExportService.exportToJson(mockMembers, filename)).thenReturn(true);
        boolean result = facade.exportToJson(filename);
        assertTrue(result);
    }

    @Test
    void testEditMemberName() {
        Player mockPlayer =
                new Player("Старое Имя", 25, "Команда", "Вратарь", 1, BigDecimal.valueOf(50000));
        when(memberService.findMemberById(1)).thenReturn(Optional.of(mockPlayer));
        facade.editMemberName(1, "Новое Имя");
        assertEquals("Новое Имя", mockPlayer.getName());
    }

    @Test
    void testClearAllMembers() {
        facade.clearAllMembers();
        verify(memberService).clearAll();
    }

    @Test
    void testResetIdCounter() {
        facade.resetIdCounter();
        verify(memberService).resetIdCounter();
    }

    @Test
    void testGetMemberCount() {
        when(memberService.getMemberCount()).thenReturn(5);
        int result = facade.getMemberCount();
        assertEquals(5, result);
    }

    @Nested
    class MemberModelTests {
        @Test
        void testPlayerSpecificMethods() {
            Player player =
                    new Player("Игрок", 25, "Команда", "Нападающий", 9, BigDecimal.valueOf(50000));
            player.scoreGoal();
            player.makeAssist();
            assertEquals(1, player.getGoalsScored());
            assertEquals(1, player.getAssists());
            assertEquals(100.0, player.getFitnessLevel(), 0.01);
        }

        @Test
        void testCoachSuccessRate() {
            Coach coach =
                    new Coach(
                            "Тренер", 40, "Команда", "Фитнес", "Высшая", BigDecimal.valueOf(70000));
            coach.train(60, 8, LocalDate.now());
            coach.train(60, 5, LocalDate.now());
            assertEquals(2, coach.getTrainingCount());
            assertEquals(1, coach.getSuccessfulSessions());
            assertEquals(50.0, coach.getSuccessRate(), 0.01);
        }

        @Test
        void testManagerContractSigning() {
            Manager manager =
                    new Manager(
                            "Менеджер",
                            35,
                            "Команда",
                            "Финансы",
                            "Бюджет",
                            BigDecimal.valueOf(60000));
            manager.signContract(BigDecimal.valueOf(100000));
            manager.signContract(BigDecimal.valueOf(200000));
            assertEquals(2, manager.getContractsSigned());
            assertEquals(BigDecimal.valueOf(300000), manager.getBudgetManaged());
        }

        @Test
        void testMemberPolymorphism() {
            Member player =
                    new Player("Игрок", 25, "Команда", "Нападающий", 9, BigDecimal.valueOf(50000));
            Member coach =
                    new Coach(
                            "Тренер", 40, "Команда", "Фитнес", "Высшая", BigDecimal.valueOf(70000));
            Member manager =
                    new Manager(
                            "Менеджер",
                            35,
                            "Команда",
                            "Финансы",
                            "Бюджет",
                            BigDecimal.valueOf(60000));
            List<Member> members = Arrays.asList(player, coach, manager);
            for (Member member : members) {
                assertNotNull(member.getName());
                assertNotNull(member.getRole());
                assertNotNull(member.getTeam());
                assertNotNull(member.getBaseSalary());
                assertNotNull(member.calculateBonus());
                String toString = member.toString();
                assertTrue(toString.contains(member.getName()));
                assertTrue(toString.contains(member.getRole()));
                assertTrue(toString.contains(member.getTeam()));
                String details = member.getDetails();
                assertNotNull(details);
                assertFalse(details.isEmpty());
            }
        }

        @Test
        void testMemberTrainingRecords() {
            Player player =
                    new Player("Игрок", 25, "Команда", "Нападающий", 9, BigDecimal.valueOf(50000));
            String result1 = player.train(60, 8, LocalDate.now().minusDays(1));
            String result2 = player.train(90, 9, LocalDate.now());
            assertEquals(2, player.getTrainingCount());
            assertTrue(player.getAverageIntensity() > 0);
            assertNotNull(player.getTrainingHistory());
            assertTrue(player.getTrainingHistory().contains("История тренировок"));
            assertTrue(player.getTrainingHistory().contains("Игрок"));
            double progress = player.calculateProgress();
            assertTrue(progress >= -100 && progress <= 100);
        }

        @Test
        void testMemberPaymentRecords() {
            Player player =
                    new Player("Игрок", 25, "Команда", "Нападающий", 9, BigDecimal.valueOf(50000));
            Payable.PaymentResult result = player.paySalary(LocalDate.now());
            assertTrue(result.successful());
            assertNotNull(result.message());
            assertTrue(result.amount().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(result.bonus().compareTo(BigDecimal.ZERO) >= 0);
            assertNotNull(player.getPaymentHistory());
            assertTrue(player.getPaymentHistory().contains("История выплат"));
            assertTrue(player.getPaymentHistory().contains("Игрок"));
            BigDecimal totalPaid = player.getTotalPaid();
            assertTrue(totalPaid.compareTo(BigDecimal.ZERO) > 0);
            assertEquals(result.amount(), totalPaid);
        }
    }

    @Nested
    class InputValidatorTests {
        @Test
        void testValidName() {
            assertTrue(InputValidator.isValidName("Иван"));
            assertTrue(InputValidator.isValidName("Иван Иванович"));
            assertFalse(InputValidator.isValidName(""));
            assertFalse(InputValidator.isValidName(null));
            assertFalse(InputValidator.isValidName("a".repeat(101)));
        }

        @Test
        void testValidAge() {
            assertTrue(InputValidator.isValidAge(16));
            assertTrue(InputValidator.isValidAge(35));
            assertTrue(InputValidator.isValidAge(70));
            assertFalse(InputValidator.isValidAge(15));
            assertFalse(InputValidator.isValidAge(71));
        }

        @Test
        void testValidSalary() {
            assertTrue(InputValidator.isValidSalary(BigDecimal.valueOf(100)));
            assertTrue(InputValidator.isValidSalary(BigDecimal.valueOf(50000)));
            assertTrue(InputValidator.isValidSalary(BigDecimal.valueOf(1000000)));
            assertFalse(InputValidator.isValidSalary(BigDecimal.valueOf(99)));
            assertFalse(InputValidator.isValidSalary(BigDecimal.valueOf(1000001)));
            assertFalse(InputValidator.isValidSalary(null));
        }

        @Test
        void testValidTeam() {
            assertTrue(InputValidator.isValidTeam("Спартак"));
            assertTrue(InputValidator.isValidTeam("ЦСКА"));
            assertFalse(InputValidator.isValidTeam(""));
            assertFalse(InputValidator.isValidTeam(null));
            assertFalse(InputValidator.isValidTeam("a".repeat(51)));
        }

        @Test
        void testValidJerseyNumber() {
            assertTrue(InputValidator.isValidJerseyNumber(1));
            assertTrue(InputValidator.isValidJerseyNumber(99));
            assertFalse(InputValidator.isValidJerseyNumber(0));
            assertFalse(InputValidator.isValidJerseyNumber(100));
        }
    }
}
