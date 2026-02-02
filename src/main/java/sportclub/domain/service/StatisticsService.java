package sportclub.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sportclub.domain.model.*;

public class StatisticsService {

    public Map<String, Long> getRoleDistribution(List<Member> members) {
        return members.stream()
                .collect(Collectors.groupingBy(Member::getRole, Collectors.counting()));
    }

    public Map<String, Double> getAverageAgeByRole(List<Member> members) {
        return members.stream()
                .collect(
                        Collectors.groupingBy(
                                Member::getRole, Collectors.averagingInt(Member::getAge)));
    }

    public Map<String, BigDecimal> getAverageSalaryByRole(List<Member> members) {
        return members.stream()
                .collect(
                        Collectors.groupingBy(
                                Member::getRole,
                                Collectors.mapping(
                                        Member::getBaseSalary,
                                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> {
                                    long count =
                                            members.stream()
                                                    .filter(m -> m.getRole().equals(entry.getKey()))
                                                    .count();
                                    return count > 0
                                            ? entry.getValue()
                                                    .divide(
                                                            BigDecimal.valueOf(count),
                                                            2,
                                                            RoundingMode.HALF_UP)
                                            : BigDecimal.ZERO;
                                }));
    }

    public AgeStatistics getAgeStatistics(List<Member> members) {
        int minAge = members.stream().mapToInt(Member::getAge).min().orElse(0);

        int maxAge = members.stream().mapToInt(Member::getAge).max().orElse(0);

        double avgAge = members.stream().mapToInt(Member::getAge).average().orElse(0.0);

        return new AgeStatistics(minAge, maxAge, avgAge);
    }

    public ExperienceStatistics getExperienceStatistics(List<Member> members) {
        int minExp = members.stream().mapToInt(Member::getExperience).min().orElse(0);

        int maxExp = members.stream().mapToInt(Member::getExperience).max().orElse(0);

        double avgExp = members.stream().mapToInt(Member::getExperience).average().orElse(0.0);

        return new ExperienceStatistics(minExp, maxExp, avgExp);
    }

    public SalaryStatistics getSalaryStatistics(List<Member> members) {
        BigDecimal minSalary =
                members.stream()
                        .map(Member::getBaseSalary)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        BigDecimal maxSalary =
                members.stream()
                        .map(Member::getBaseSalary)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        BigDecimal totalSalary =
                members.stream()
                        .map(Member::getBaseSalary)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgSalary =
                members.isEmpty()
                        ? BigDecimal.ZERO
                        : totalSalary.divide(
                                BigDecimal.valueOf(members.size()), 2, RoundingMode.HALF_UP);

        return new SalaryStatistics(minSalary, maxSalary, totalSalary, avgSalary);
    }

    public List<Member> getTopBySalary(List<Member> members, int limit) {
        return members.stream()
                .sorted((m1, m2) -> m2.getBaseSalary().compareTo(m1.getBaseSalary()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Member> getTopByExperience(List<Member> members, int limit) {
        return members.stream()
                .sorted((m1, m2) -> Integer.compare(m2.getExperience(), m1.getExperience()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class AgeStatistics {
        private final int minAge;
        private final int maxAge;
        private final double averageAge;

        public AgeStatistics(int minAge, int maxAge, double averageAge) {
            this.minAge = minAge;
            this.maxAge = maxAge;
            this.averageAge = averageAge;
        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ExperienceStatistics {
        private final int minExperience;
        private final int maxExperience;
        private final double averageExperience;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class SalaryStatistics {
        private final BigDecimal minSalary;
        private final BigDecimal maxSalary;
        private final BigDecimal totalSalary;
        private final BigDecimal averageSalary;
    }
}
