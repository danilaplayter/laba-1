package sportclub.domain.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import sportclub.domain.interfaces.Payable;
import sportclub.domain.model.*;
import sportclub.domain.model.Member.TrainingRecord;
import sportclub.domain.service.*;
import sportclub.domain.service.TransferJournal.TransferRecord;

public class ClubManagerFacade {
    private final MemberManagementService memberService;
    private final MemberSearchService searchService;
    private final MemberSortService sortService;
    private final TrainingService trainingService;
    private final FinanceService financeService;
    private final StatisticsService statisticsService;
    private final ReportService reportService;
    private final ImportExportService importExportService;
    private final TransferJournal transferJournal;
    private final SalaryRegistry salaryRegistry;
    private final TrainingRegistry trainingRegistry;

    public ClubManagerFacade() {
        this.memberService = new MemberManagementService();
        this.searchService = new MemberSearchService();
        this.sortService = new MemberSortService();
        this.trainingService = new TrainingService();
        this.financeService = new FinanceService();
        this.statisticsService = new StatisticsService();
        this.reportService = new ReportService();
        this.importExportService = new ImportExportService();
        this.trainingRegistry = new TrainingRegistry();
        this.transferJournal = new TransferJournal();
        this.salaryRegistry = new SalaryRegistry();
    }

    public Optional<Member> findMemberById(int id) {
        return memberService.findMemberById(id);
    }

    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    public int getMemberCount() {
        return memberService.getMemberCount();
    }

    public void clearAllMembers() {
        memberService.clearAll();
    }

    public void resetIdCounter() {
        memberService.resetIdCounter();
    }

    public List<Member> searchByName(String name) {
        return searchService.searchByName(getAllMembers(), name);
    }

    public List<Member> searchByTeam(String team) {
        return searchService.searchByTeam(getAllMembers(), team);
    }

    public List<Member> filterByRole(String role) {
        return searchService.filterByRole(getAllMembers(), role);
    }

    public List<Member> filterByAgeRange(int minAge, int maxAge) {
        return searchService.filterByAgeRange(getAllMembers(), minAge, maxAge);
    }

    public List<Member> filterByExperience(int minExperience, int maxExperience) {
        return searchService.filterByExperience(getAllMembers(), minExperience, maxExperience);
    }

    public List<Member> filterBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        return searchService.filterBySalaryRange(getAllMembers(), minSalary, maxSalary);
    }

    public List<Member> filterMembers(
            String role,
            Integer minAge,
            Integer maxAge,
            Integer minExp,
            Integer maxExp,
            BigDecimal minSalary,
            BigDecimal maxSalary) {
        return searchService.filterMembers(
                getAllMembers(), role, minAge, maxAge, minExp, maxExp, minSalary, maxSalary);
    }

    public List<Player> filterPlayersByPosition(String position) {
        return searchService.filterPlayersByPosition(getAllMembers(), position);
    }

    public List<Coach> filterCoachesBySpecialization(String specialization) {
        return searchService.filterCoachesBySpecialization(getAllMembers(), specialization);
    }

    public List<Manager> filterManagersByDepartment(String department) {
        return searchService.filterManagersByDepartment(getAllMembers(), department);
    }

    public List<Member> sortByName(boolean ascending) {
        MemberSortService.SortType sortType =
                ascending
                        ? MemberSortService.SortType.BY_NAME_ASC
                        : MemberSortService.SortType.BY_NAME_DESC;
        return sortService.sortMembers(getAllMembers(), sortType);
    }

    public List<Member> sortByAge(boolean ascending) {
        MemberSortService.SortType sortType =
                ascending
                        ? MemberSortService.SortType.BY_AGE_ASC
                        : MemberSortService.SortType.BY_AGE_DESC;
        return sortService.sortMembers(getAllMembers(), sortType);
    }

    public List<Member> sortBySalary(boolean ascending) {
        MemberSortService.SortType sortType =
                ascending
                        ? MemberSortService.SortType.BY_SALARY_ASC
                        : MemberSortService.SortType.BY_SALARY_DESC;
        return sortService.sortMembers(getAllMembers(), sortType);
    }

    public List<Member> sortByExperience(boolean ascending) {
        MemberSortService.SortType sortType =
                ascending
                        ? MemberSortService.SortType.BY_EXPERIENCE_ASC
                        : MemberSortService.SortType.BY_EXPERIENCE_DESC;
        return sortService.sortMembers(getAllMembers(), sortType);
    }

    public List<Member> sortByJoinDate() {
        return sortService.sortMembers(getAllMembers(), MemberSortService.SortType.BY_JOIN_DATE);
    }

    public List<Member> sortByMultipleFields(boolean nameAsc, boolean salaryAsc) {
        MemberSortService.SortType primary =
                nameAsc
                        ? MemberSortService.SortType.BY_NAME_ASC
                        : MemberSortService.SortType.BY_NAME_DESC;
        MemberSortService.SortType secondary =
                salaryAsc
                        ? MemberSortService.SortType.BY_SALARY_ASC
                        : MemberSortService.SortType.BY_SALARY_DESC;
        return sortService.sortByMultipleFields(getAllMembers(), primary, secondary);
    }



    public double getAverageIntensity() {
        return trainingService.getAverageIntensity(getAllMembers());
    }

    public int getTotalTrainingCount() {
        return trainingService.getTotalTrainingCount(getAllMembers());
    }

    public Payable.PaymentResult paySalary(int memberId, LocalDate date) {
        Optional<Member> member = findMemberById(memberId);
        if (member.isPresent()) {
            return financeService.paySalary(member.get(), date);
        }
        return new Payable.PaymentResult(
                BigDecimal.ZERO, BigDecimal.ZERO, date, false, "Сотрудник не найден");
    }

    public BigDecimal calculateTotalMonthlySalary() {
        return financeService.calculateTotalSalaries(getAllMembers());
    }

    public BigDecimal calculateTotalBonuses() {
        return financeService.calculateTotalBonuses(getAllMembers());
    }

    public BigDecimal getTotalPaidToDate() {
        return financeService.getTotalPaidToDate(getAllMembers());
    }


    public Map<String, Long> getRoleDistribution() {
        return statisticsService.getRoleDistribution(getAllMembers());
    }

    public Map<String, Double> getAverageAgeByRole() {
        return statisticsService.getAverageAgeByRole(getAllMembers());
    }

    public Map<String, BigDecimal> getAverageSalaryByRole() {
        return statisticsService.getAverageSalaryByRole(getAllMembers());
    }

    public StatisticsService.AgeStatistics getAgeStatistics() {
        return statisticsService.getAgeStatistics(getAllMembers());
    }

    public StatisticsService.ExperienceStatistics getExperienceStatistics() {
        return statisticsService.getExperienceStatistics(getAllMembers());
    }

    public StatisticsService.SalaryStatistics getSalaryStatistics() {
        return statisticsService.getSalaryStatistics(getAllMembers());
    }

    public List<Member> getTopBySalary(int limit) {
        return statisticsService.getTopBySalary(getAllMembers(), limit);
    }

    public List<Member> getTopByExperience(int limit) {
        return statisticsService.getTopByExperience(getAllMembers(), limit);
    }

    public List<Member> getTopPlayersByPerformance(int limit) {
        return getAllMembers().stream()
                .filter(m -> m instanceof Player)
                .map(m -> (Player) m)
                .sorted(
                        (p1, p2) ->
                                Double.compare(
                                        p2.getPerformanceRating(), p1.getPerformanceRating()))
                .limit(limit)
                .map(p -> (Member) p)
                .collect(Collectors.toList());
    }

    public List<Member> getTopCoachesBySuccessRate(int limit) {
        return getAllMembers().stream()
                .filter(m -> m instanceof Coach)
                .map(m -> (Coach) m)
                .sorted((c1, c2) -> Double.compare(c2.getSuccessRate(), c1.getSuccessRate()))
                .limit(limit)
                .map(c -> (Member) c)
                .collect(Collectors.toList());
    }

    public List<Member> getTopManagersByContracts(int limit) {
        return getAllMembers().stream()
                .filter(m -> m instanceof Manager)
                .map(m -> (Manager) m)
                .sorted(
                        (m1, m2) ->
                                Integer.compare(m2.getContractsSigned(), m1.getContractsSigned()))
                .limit(limit)
                .map(m -> (Member) m)
                .collect(Collectors.toList());
    }

    public String generateAllMembersReport() {
        return reportService.generateAllMembersReport(getAllMembers());
    }

    public String generateFinancialReport() {
        return reportService.generateFinancialReport(getAllMembers());
    }

    public String generateTrainingReport() {
        return reportService.generateTrainingReport(getAllMembers());
    }

    public String generateRoleBasedReport() {
        return reportService.generateRoleBasedReport(getAllMembers());
    }

    public boolean exportReportToFile(String report, String filename) {
        return reportService.exportReportToFile(report, filename);
    }

    public String generatePlayersReport() {
        List<Member> players = filterByRole("Игрок");
        return reportService.generateAllMembersReport(players);
    }

    public String generateCoachesReport() {
        List<Member> coaches = filterByRole("Тренер");
        return reportService.generateAllMembersReport(coaches);
    }

    public String generateManagersReport() {
        List<Member> managers = filterByRole("Менеджер");
        return reportService.generateAllMembersReport(managers);
    }

    public boolean exportToJson(String filename) {
        return importExportService.exportToJson(getAllMembers(), filename);
    }

    public boolean exportToXml(String filename) {
        return importExportService.exportToXml(getAllMembers(), filename);
    }

    public boolean exportToCsv(String filename) {
        return importExportService.exportToCsv(getAllMembers(), filename);
    }

    public List<Member> importFromJson(String filename) throws java.io.IOException {
        List<Member> imported = importExportService.importFromJson(filename);
        imported.forEach(
                member -> {
                    member.setId(member.getId());
                    memberService.getAllMembers().add(member);
                });
        return imported;
    }

    public List<Member> importFromXml(String filename) throws java.io.IOException {
        List<Member> imported = importExportService.importFromXml(filename);
        imported.forEach(member -> memberService.getAllMembers().add(member));
        return imported;
    }

    public List<Member> importFromCsv(String filename) throws java.io.IOException {
        List<Member> imported = importExportService.importFromCsv(filename);
        imported.forEach(member -> memberService.getAllMembers().add(member));
        return imported;
    }

    public void setMembers(List<Member> members) {
        clearAllMembers();
        members.forEach(member -> memberService.getAllMembers().add(member));
    }

    public void loadFromBinary(String filename) {
        try (java.io.ObjectInputStream ois =
                new java.io.ObjectInputStream(new java.io.FileInputStream(filename))) {
            @SuppressWarnings("unchecked")
            List<Member> members = (List<Member>) ois.readObject();
            setMembers(members);

            int maxId = members.stream().mapToInt(Member::getId).max().orElse(0);
            memberService.resetIdCounter();
            for (int i = 0; i <= maxId; i++) {
                memberService.getAllMembers();
            }
        } catch (Exception e) {
            System.out.println("Ошибка загрузки из бинарного файла: " + e.getMessage());
        }
    }

    public void saveToBinary(String filename) {
        try (java.io.ObjectOutputStream oos =
                new java.io.ObjectOutputStream(new java.io.FileOutputStream(filename))) {
            oos.writeObject(getAllMembers());
        } catch (Exception e) {
            System.out.println("Ошибка сохранения в бинарный файл: " + e.getMessage());
        }
    }

    public boolean updateMember(Member updatedMember) {
        return memberService.updateMember(updatedMember);
    }

    public void editMemberName(int id, String newName) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(m -> m.setName(newName));
    }

    public void editMemberAge(int id, int newAge) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(m -> m.setAge(newAge));
    }


    public void editMemberJoinDate(int id, LocalDate newJoinDate) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(m -> m.setJoinDate(newJoinDate));
    }


    public void editPlayerJerseyNumber(int id, int newNumber) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(
                m -> {
                    if (m instanceof Player player) {
                        player.setJerseyNumber(newNumber);
                    }
                });
    }

    public void editCoachSpecialization(int id, String newSpecialization) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(
                m -> {
                    if (m instanceof Coach coach) {
                        coach.setSpecialization(newSpecialization);
                    }
                });
    }

    public void editCoachCertification(int id, String newCertification) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(
                m -> {
                    if (m instanceof Coach coach) {
                        coach.setCertification(newCertification);
                    }
                });
    }

    public void editManagerDepartment(int id, String newDepartment) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(
                m -> {
                    if (m instanceof Manager manager) {
                        manager.setDepartment(newDepartment);
                    }
                });
    }

    public void editManagerResponsibilities(int id, String newResponsibilities) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(
                m -> {
                    if (m instanceof Manager manager) {
                        manager.setResponsibilities(newResponsibilities);
                    }
                });
    }

    public double calculateAverageAge() {
        return getAllMembers().stream().mapToInt(Member::getAge).average().orElse(0.0);
    }

    public double calculateAverageExperience() {
        return getAllMembers().stream().mapToInt(Member::getExperience).average().orElse(0.0);
    }

    public Map<String, Integer> getTeamDistribution() {
        return getAllMembers().stream()
                .collect(
                        Collectors.groupingBy(
                                Member::getTeam,
                                Collectors.collectingAndThen(
                                        Collectors.counting(), Long::intValue)));
    }

    public List<String> getAllTeams() {
        return getAllMembers().stream()
                .map(Member::getTeam)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public Map<String, List<Member>> getMembersByTeam() {
        return getAllMembers().stream().collect(Collectors.groupingBy(Member::getTeam));
    }

    public void registerSalary(Member member) {
        salaryRegistry.registerSalary(member.getId(), member.getBaseSalary());
    }

    public BigDecimal getSalaryFromRegistry(int memberId) {
        return salaryRegistry.getSalary(memberId);
    }

    public Map<String, BigDecimal> getAverageSalaryByRoleFromRegistry() {
        Map<Integer, Member> memberMap = new HashMap<>();
        getAllMembers().forEach(m -> memberMap.put(m.getId(), m));
        return salaryRegistry.getSalaryByRole(memberMap);
    }

    public SalaryRegistry.SalaryHistory getSalaryHistory(int memberId) {
        return salaryRegistry.getSalaryHistory(memberId);
    }

    public void registerTraining(Member member, TrainingRecord record) {
        trainingRegistry.registerTraining(member, record);
    }

    public List<TrainingRecord> getTrainingsByDate(LocalDate date) {
        return trainingRegistry.getTrainingsByDate(date);
    }

    public Map<LocalDate, Integer> getTrainingCountByDate() {
        return trainingRegistry.getTrainingCountByDate();
    }

    public List<LocalDate> getMostActiveTrainingDays(int limit) {
        return trainingRegistry.getMostActiveDays(limit);
    }

    public void recordTransfer(Member member, TransferJournal.TransferType type,
        String description, BigDecimal amount) {
        transferJournal.recordTransfer(member, type, description, amount);
    }

    public List<TransferJournal.TransferRecord> getTransferRecords() {
        return transferJournal.getAllRecords();
    }

    public List<TransferJournal.TransferRecord> getTransferRecordsByMember(int memberId) {
        return transferJournal.getRecordsByMember(memberId);
    }

    public List<TransferJournal.TransferRecord> getTransferRecordsByType(
        TransferJournal.TransferType type) {
        return transferJournal.getRecordsByType(type);
    }

    public List<TransferJournal.TransferRecord> getRecentTransferRecords(int count) {
        return transferJournal.getRecentRecords(count);
    }

    public String generateTransferReport() {
        return transferJournal.generateReport();
    }

    public int getTransferRecordCount() {
        return transferJournal.getRecordCount();
    }

    public Member addPlayer(
        String name,
        int age,
        String team,
        String position,
        int jerseyNumber,
        BigDecimal salary) {
        Member player = memberService.addPlayer(name, age, team, position, jerseyNumber, salary);
        if (player != null) {
            salaryRegistry.registerSalary(player.getId(), player.getBaseSalary());
            transferJournal.recordTransfer(player,
                TransferJournal.TransferType.TRANSFER_IN,
                "Новый игрок в клубе", salary);
        }
        return player;
    }

    public Member addCoach(
        String name,
        int age,
        String team,
        String specialization,
        String certification,
        BigDecimal salary) {
        Member coach = memberService.addCoach(name, age, team, specialization, certification, salary);
        if (coach != null) {
            salaryRegistry.registerSalary(coach.getId(), coach.getBaseSalary());
            transferJournal.recordTransfer(coach,
                TransferJournal.TransferType.TRANSFER_IN,
                "Новый тренер в клубе", salary);
        }
        return coach;
    }

    public Member addManager(
        String name,
        int age,
        String team,
        String department,
        String responsibilities,
        BigDecimal salary) {
        Member manager = memberService.addManager(name, age, team, department, responsibilities, salary);
        if (manager != null) {
            salaryRegistry.registerSalary(manager.getId(), manager.getBaseSalary());
            transferJournal.recordTransfer(manager,
                TransferJournal.TransferType.TRANSFER_IN,
                "Новый менеджер в клубе", salary);
        }
        return manager;
    }

    public boolean removeMember(int id) {
        Optional<Member> member = findMemberById(id);
        if (member.isPresent()) {
            salaryRegistry.removeSalary(id);
            transferJournal.recordTransfer(member.get(),
                TransferJournal.TransferType.TRANSFER_OUT,
                "Уход из клуба", member.get().getBaseSalary());
        }
        return memberService.removeMember(id);
    }

    public void adjustSalary(int memberId, BigDecimal newSalary) {
        Optional<Member> member = findMemberById(memberId);
        if (member.isPresent()) {
            BigDecimal oldSalary = member.get().getBaseSalary();
            financeService.adjustSalary(member.get(), newSalary);
            salaryRegistry.updateSalary(memberId, newSalary);

            String description = String.format("Изменение зарплаты с %s на %s",
                oldSalary, newSalary);
            transferJournal.recordTransfer(member.get(),
                TransferJournal.TransferType.SALARY_CHANGE,
                description, newSalary);
        }
    }

    public String conductTraining(int memberId, int durationMinutes, int intensity) {
        Optional<Member> member = findMemberById(memberId);
        if (member.isPresent()) {
            String result = trainingService.conductTraining(
                member.get(), durationMinutes, intensity, LocalDate.now());

            TrainingRecord record = new TrainingRecord(
                durationMinutes, intensity, LocalDate.now(), result);
            trainingRegistry.registerTraining(member.get(), record);

            return result;
        }
        return "Участник не найден!";
    }


    public void editMemberTeam(int id, String newTeam) {
        Optional<Member> member = findMemberById(id);
        if (member.isPresent()) {
            String oldTeam = member.get().getTeam();
            member.ifPresent(m -> m.setTeam(newTeam));

            String description = String.format("Переход из команды '%s' в '%s'",
                oldTeam, newTeam);
            transferJournal.recordTransfer(member.get(),
                TransferJournal.TransferType.TEAM_CHANGE,
                description, BigDecimal.ZERO);
        }
    }

    public void editPlayerPosition(int id, String newPosition) {
        Optional<Member> member = findMemberById(id);
        member.ifPresent(
            m -> {
                if (m instanceof Player player) {
                    String oldPosition = player.getPosition();
                    player.setPosition(newPosition);

                    String description = String.format("Изменение позиции с '%s' на '%s'",
                        oldPosition, newPosition);
                    transferJournal.recordTransfer(player,
                        TransferJournal.TransferType.POSITION_CHANGE,
                        description, BigDecimal.ZERO);
                }
            });
    }

    public Map<String, Long> getRoleDistributionDetailed() {
        return getAllMembers().stream()
            .collect(Collectors.groupingBy(
                Member::getRole,
                Collectors.counting()));
    }

    public double getAverageExperienceAll() {
        return getAllMembers().stream()
            .collect(Collectors.summarizingDouble(Member::getExperience))
            .getAverage();
    }

    public Map<String, java.util.DoubleSummaryStatistics> getExperienceStatisticsByRole() {
        return getAllMembers().stream()
            .collect(Collectors.groupingBy(
                Member::getRole,
                Collectors.summarizingDouble(Member::getExperience)));
    }

    public Map<String, java.util.DoubleSummaryStatistics> getAgeStatisticsByRole() {
        return getAllMembers().stream()
            .collect(Collectors.groupingBy(
                Member::getRole,
                Collectors.summarizingDouble(Member::getAge)));
    }

    public List<Member> getTopByAge(int limit, boolean ascending) {
        Comparator<Member> comparator = ascending ?
            Comparator.comparingInt(Member::getAge) :
            Comparator.comparingInt(Member::getAge).reversed();

        return getAllMembers().stream()
            .sorted(comparator)
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<Member> getTopByTrainingCount(int limit) {
        return getAllMembers().stream()
            .sorted((m1, m2) -> Integer.compare(m2.getTrainingCount(), m1.getTrainingCount()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    public Map<String, Integer> getTrainingDistributionByRole() {
        return getAllMembers().stream()
            .collect(Collectors.groupingBy(
                Member::getRole,
                Collectors.summingInt(Member::getTrainingCount)));
    }

    public Map<String, Double> getAverageTrainingCountByRole() {
        return getAllMembers().stream()
            .collect(Collectors.groupingBy(
                Member::getRole,
                Collectors.averagingDouble(Member::getTrainingCount)));
    }

    public List<Integer> getMostActiveMembers(int limit) {
        return trainingRegistry.getMostActiveMembers(limit);
    }

    public TreeMap<LocalDate, Integer> getTrainingDistributionByDateSorted() {
        return new TreeMap<>(trainingRegistry.getTrainingCountByDate());
    }

    public BigDecimal getTotalSalaryBudget() {
        return salaryRegistry.getTotalSalaryBudget();
    }

    public double getAverageSalaryFromRegistry() {
        return salaryRegistry.getAverageSalary();
    }

    public BigDecimal getMinSalaryFromRegistry() {
        return salaryRegistry.getMinSalary();
    }

    public BigDecimal getMaxSalaryFromRegistry() {
        return salaryRegistry.getMaxSalary();
    }

    public void clearAllJournals() {
        transferJournal.clear();
        trainingRegistry.clear();
        salaryRegistry.clear();
    }

    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalMembers", getMemberCount());
        stats.put("averageAge", calculateAverageAge());
        stats.put("averageExperience", calculateAverageExperience());
        stats.put("totalSalaryBudget", getTotalSalaryBudget());
        stats.put("averageSalary", getAverageSalaryFromRegistry());
        stats.put("totalTrainings", trainingRegistry.getTotalTrainings());
        stats.put("totalTransfers", getTransferRecordCount());

        return stats;
    }

    public Map<String, java.util.DoubleSummaryStatistics> getSalaryStatisticsByRole() {
        return getAllMembers().stream()
            .collect(Collectors.groupingBy(
                Member::getRole,
                Collectors.summarizingDouble(m -> m.getBaseSalary().doubleValue())));
    }
}
