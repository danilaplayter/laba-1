package sportclub.domain.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import sportclub.domain.model.*;

public class ReportService {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String generateAllMembersReport(List<Member> members) {
        StringBuilder report = new StringBuilder();
        report.append("ОТЧЕТ ПО ВСЕМ ЧЛЕНАМ КЛУБА\n");
        report.append("Дата генерации: ")
                .append(LocalDateTime.now().format(DATE_FORMATTER))
                .append("\n\n");
        report.append("Общее количество: ").append(members.size()).append("\n\n");

        for (Member member : members) {
            report.append(member.toString()).append("\n");
        }

        return report.toString();
    }

    public String generateFinancialReport(List<Member> members) {
        BigDecimal totalSalary =
                members.stream()
                        .map(Member::getBaseSalary)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBonuses =
                members.stream()
                        .map(Member::calculateBonus)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder report = new StringBuilder();
        report.append("ФИНАНСОВЫЙ ОТЧЕТ\n");
        report.append("Дата генерации: ")
                .append(LocalDateTime.now().format(DATE_FORMATTER))
                .append("\n\n");
        report.append("Общий фонд зарплат: ").append(totalSalary).append("\n");
        report.append("Общая сумма бонусов: ").append(totalBonuses).append("\n");
        report.append("Планируемые выплаты: ").append(totalSalary.add(totalBonuses)).append("\n\n");

        report.append("Детализация по сотрудникам:\n");
        for (Member member : members) {
            BigDecimal bonus = member.calculateBonus();
            report.append(
                    String.format(
                            "%s (ID: %d): Зарплата: %s, Бонус: %s, Итого: %s\n",
                            member.getName(),
                            member.getId(),
                            member.getBaseSalary(),
                            bonus,
                            member.getBaseSalary().add(bonus)));
        }

        return report.toString();
    }

    public String generateTrainingReport(List<Member> members) {
        int totalTrainings = members.stream().mapToInt(Member::getTrainingCount).sum();

        double avgIntensity =
                members.stream().mapToDouble(Member::getAverageIntensity).average().orElse(0.0);

        StringBuilder report = new StringBuilder();
        report.append("ОТЧЕТ ПО ТРЕНИРОВКАМ\n");
        report.append("Дата генерации: ")
                .append(LocalDateTime.now().format(DATE_FORMATTER))
                .append("\n\n");
        report.append("Общее количество тренировок: ").append(totalTrainings).append("\n");
        report.append("Средняя интенсивность: ")
                .append(String.format("%.1f/10", avgIntensity))
                .append("\n\n");

        report.append("Детализация по участникам:\n");
        for (Member member : members) {
            report.append(
                    String.format(
                            "%s (ID: %d): %d тренировок, Средняя интенсивность: %.1f\n",
                            member.getName(),
                            member.getId(),
                            member.getTrainingCount(),
                            member.getAverageIntensity()));
        }

        return report.toString();
    }

    public boolean exportReportToFile(String report, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.write(report);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String generateRoleBasedReport(List<Member> members) {
        Map<String, List<Member>> membersByRole =
                members.stream().collect(Collectors.groupingBy(Member::getRole));

        StringBuilder report = new StringBuilder();
        report.append("ОТЧЕТ ПО РОЛЯМ\n");
        report.append("Дата генерации: ")
                .append(LocalDateTime.now().format(DATE_FORMATTER))
                .append("\n\n");

        for (Map.Entry<String, List<Member>> entry : membersByRole.entrySet()) {
            String role = entry.getKey();
            List<Member> roleMembers = entry.getValue();

            report.append(role).append(" (").append(roleMembers.size()).append("):\n");
            for (Member member : roleMembers) {
                report.append("  ")
                        .append(member.getName())
                        .append(" - ")
                        .append(member.getTeam())
                        .append("\n");
            }
            report.append("\n");
        }

        return report.toString();
    }
}
