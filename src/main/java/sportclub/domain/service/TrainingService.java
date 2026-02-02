package sportclub.domain.service;

import java.time.LocalDate;
import java.util.List;
import sportclub.domain.model.Member;

public class TrainingService {

    public String conductTraining(
            Member member, int durationMinutes, int intensity, LocalDate date) {
        if (member == null) {
            return "Участник не найден!";
        }

        if (durationMinutes <= 0 || durationMinutes > 240) {
            return "Некорректная продолжительность тренировки!";
        }

        if (intensity < 1 || intensity > 10) {
            return "Интенсивность должна быть от 1 до 10!";
        }

        return member.train(durationMinutes, intensity, date);
    }

    public double getAverageIntensity(List<Member> members) {
        return members.stream().mapToDouble(Member::getAverageIntensity).average().orElse(0.0);
    }

    public int getTotalTrainingCount(List<Member> members) {
        return members.stream().mapToInt(Member::getTrainingCount).sum();
    }
}
