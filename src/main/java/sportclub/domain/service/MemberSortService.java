package sportclub.domain.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import sportclub.domain.model.Member;

public class MemberSortService {

    public enum SortType {
        BY_NAME_ASC,
        BY_NAME_DESC,
        BY_AGE_ASC,
        BY_AGE_DESC,
        BY_EXPERIENCE_ASC,
        BY_EXPERIENCE_DESC,
        BY_SALARY_ASC,
        BY_SALARY_DESC,
        BY_JOIN_DATE
    }

    public List<Member> sortMembers(List<Member> members, SortType sortType) {
        Comparator<Member> comparator = getComparator(sortType);

        return members.stream().sorted(comparator).collect(Collectors.toList());
    }

    private Comparator<Member> getComparator(SortType sortType) {
        return switch (sortType) {
            case BY_NAME_ASC -> Comparator.comparing(Member::getName);
            case BY_NAME_DESC -> Comparator.comparing(Member::getName).reversed();
            case BY_AGE_ASC -> Comparator.comparingInt(Member::getAge);
            case BY_AGE_DESC -> Comparator.comparingInt(Member::getAge).reversed();
            case BY_EXPERIENCE_ASC -> Comparator.comparingInt(Member::getExperience);
            case BY_EXPERIENCE_DESC -> Comparator.comparingInt(Member::getExperience).reversed();
            case BY_SALARY_ASC -> Comparator.comparing(Member::getBaseSalary);
            case BY_SALARY_DESC -> Comparator.comparing(Member::getBaseSalary).reversed();
            case BY_JOIN_DATE -> Comparator.comparing(Member::getJoinDate);
        };
    }

    public List<Member> sortByMultipleFields(
            List<Member> members, SortType primarySort, SortType secondarySort) {
        Comparator<Member> comparator =
                getComparator(primarySort).thenComparing(getComparator(secondarySort));

        return members.stream().sorted(comparator).collect(Collectors.toList());
    }
}
