package sportclub.domain.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import sportclub.domain.model.*;

public class MemberSearchService {

    public List<Member> searchByName(List<Member> members, String name) {
        String searchTerm = name.toLowerCase();
        return members.stream()
                .filter(member -> member.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    public List<Member> searchByTeam(List<Member> members, String team) {
        String searchTerm = team.toLowerCase();
        return members.stream()
                .filter(member -> member.getTeam().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    public List<Member> filterByRole(List<Member> members, String role) {
        return members.stream()
                .filter(member -> member.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    public List<Member> filterByAgeRange(List<Member> members, int minAge, int maxAge) {
        return members.stream()
                .filter(member -> member.getAge() >= minAge && member.getAge() <= maxAge)
                .collect(Collectors.toList());
    }

    public List<Member> filterByExperience(
            List<Member> members, int minExperience, int maxExperience) {
        return members.stream()
                .filter(
                        member -> {
                            int exp = member.getExperience();
                            return exp >= minExperience
                                    && (maxExperience == -1 || exp <= maxExperience);
                        })
                .collect(Collectors.toList());
    }

    public List<Member> filterBySalaryRange(
            List<Member> members, BigDecimal minSalary, BigDecimal maxSalary) {
        return members.stream()
                .filter(
                        member -> {
                            BigDecimal salary = member.getBaseSalary();
                            return salary.compareTo(minSalary) >= 0
                                    && salary.compareTo(maxSalary) <= 0;
                        })
                .collect(Collectors.toList());
    }

    public List<Member> filterMembers(
            List<Member> members,
            String role,
            Integer minAge,
            Integer maxAge,
            Integer minExp,
            Integer maxExp,
            BigDecimal minSalary,
            BigDecimal maxSalary) {

        Predicate<Member> predicate = member -> true;

        if (role != null && !role.isEmpty()) {
            predicate = predicate.and(member -> member.getRole().equalsIgnoreCase(role));
        }

        if (minAge != null) {
            predicate = predicate.and(member -> member.getAge() >= minAge);
        }

        if (maxAge != null) {
            predicate = predicate.and(member -> member.getAge() <= maxAge);
        }

        if (minExp != null) {
            predicate = predicate.and(member -> member.getExperience() >= minExp);
        }

        if (maxExp != null) {
            predicate = predicate.and(member -> member.getExperience() <= maxExp);
        }

        if (minSalary != null) {
            predicate = predicate.and(member -> member.getBaseSalary().compareTo(minSalary) >= 0);
        }

        if (maxSalary != null) {
            predicate = predicate.and(member -> member.getBaseSalary().compareTo(maxSalary) <= 0);
        }

        return members.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<Player> filterPlayersByPosition(List<Member> members, String position) {
        return members.stream()
                .filter(member -> member instanceof Player)
                .map(member -> (Player) member)
                .filter(
                        player ->
                                player.getPosition().toLowerCase().contains(position.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Coach> filterCoachesBySpecialization(List<Member> members, String specialization) {
        return members.stream()
                .filter(member -> member instanceof Coach)
                .map(member -> (Coach) member)
                .filter(
                        coach ->
                                coach.getSpecialization()
                                        .toLowerCase()
                                        .contains(specialization.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Manager> filterManagersByDepartment(List<Member> members, String department) {
        return members.stream()
                .filter(member -> member instanceof Manager)
                .map(member -> (Manager) member)
                .filter(
                        manager ->
                                manager.getDepartment()
                                        .toLowerCase()
                                        .contains(department.toLowerCase()))
                .collect(Collectors.toList());
    }
}
