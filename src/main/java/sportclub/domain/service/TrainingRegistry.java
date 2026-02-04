package sportclub.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sportclub.domain.model.Member;
import sportclub.domain.model.Member.TrainingRecord;

public class TrainingRegistry {
    private final HashMap<LocalDate, List<TrainingRecord>> trainingByDate;
    private final HashMap<Integer, List<TrainingRecord>> trainingByMember;

    public TrainingRegistry() {
        this.trainingByDate = new HashMap<>();
        this.trainingByMember = new HashMap<>();
    }

    public void registerTraining(Member member, TrainingRecord record) {
        LocalDate date = record.date();

        trainingByDate.putIfAbsent(date, new ArrayList<>());
        trainingByDate.get(date).add(record);

        int memberId = member.getId();
        trainingByMember.putIfAbsent(memberId, new ArrayList<>());
        trainingByMember.get(memberId).add(record);
    }

    public List<TrainingRecord> getTrainingsByDate(LocalDate date) {
        return trainingByDate.getOrDefault(date, new ArrayList<>());
    }

    public List<TrainingRecord> getTrainingsByMember(int memberId) {
        return trainingByMember.getOrDefault(memberId, new ArrayList<>());
    }

    public Map<LocalDate, Integer> getTrainingCountByDate() {
        Map<LocalDate, Integer> result = new HashMap<>();
        for (Map.Entry<LocalDate, List<TrainingRecord>> entry : trainingByDate.entrySet()) {
            result.put(entry.getKey(), entry.getValue().size());
        }
        return result;
    }

    public Map<Integer, Integer> getTrainingCountByMember() {
        Map<Integer, Integer> result = new HashMap<>();
        for (Map.Entry<Integer, List<TrainingRecord>> entry : trainingByMember.entrySet()) {
            result.put(entry.getKey(), entry.getValue().size());
        }
        return result;
    }

    public double getAverageIntensityByDate(LocalDate date) {
        List<TrainingRecord> trainings = getTrainingsByDate(date);
        if (trainings.isEmpty()) return 0.0;

        return trainings.stream().mapToInt(TrainingRecord::intensity).average().orElse(0.0);
    }

    public double getAverageDurationByDate(LocalDate date) {
        List<TrainingRecord> trainings = getTrainingsByDate(date);
        if (trainings.isEmpty()) return 0.0;

        return trainings.stream().mapToInt(TrainingRecord::durationMinutes).average().orElse(0.0);
    }

    public Map<String, Integer> getTrainingDistributionByRole(List<Member> members) {
        Map<String, Integer> result = new HashMap<>();
        Map<String, Integer> countByRole = new HashMap<>();

        for (Map.Entry<Integer, List<TrainingRecord>> entry : trainingByMember.entrySet()) {
            int memberId = entry.getKey();
            Member member = findMemberById(members, memberId);
            if (member != null) {
                String role = member.getRole();
                int current = result.getOrDefault(role, 0);
                result.put(role, current + entry.getValue().size());
                countByRole.put(role, countByRole.getOrDefault(role, 0) + 1);
            }
        }

        return result;
    }

    public List<LocalDate> getMostActiveDays(int limit) {
        return getTrainingCountByDate().entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<Integer> getMostActiveMembers(int limit) {
        return getTrainingCountByMember().entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    public void clear() {
        trainingByDate.clear();
        trainingByMember.clear();
    }

    public int getTotalTrainings() {
        return trainingByDate.values().stream().mapToInt(List::size).sum();
    }

    private Member findMemberById(List<Member> members, int id) {
        return members.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }
}
