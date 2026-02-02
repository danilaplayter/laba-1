package sportclub.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coach extends Member {

    private String specialization;
    private String certification;
    private String coachingStyle;
    private int successfulSessions;

    public Coach(
            String name,
            int age,
            String team,
            String specialization,
            String certification,
            BigDecimal baseSalary) {
        super(name, age, team, baseSalary);
        this.role = "Тренер";
        this.specialization = specialization;
        this.certification = certification;
        this.coachingStyle = "Стандартный";
        this.successfulSessions = 0;
    }

    @Override
    public String train(int durationMinutes, int intensity, LocalDate date) {
        if (intensity > 7) {
            successfulSessions++;
        }

        String result =
                String.format(
                        "Тренировка тренера %s: проведена сессия %d минут. Успешных сессий: %d",
                        name, durationMinutes, successfulSessions);

        addTrainingResult(result, date);
        return result;
    }

    @Override
    public BigDecimal calculateBonus() {
        BigDecimal sessionBonus = BigDecimal.valueOf(successfulSessions * 200);
        return super.calculateBonus().add(sessionBonus);
    }

    public double getSuccessRate() {
        return getTrainingCount() > 0
                ? (double) successfulSessions / getTrainingCount() * 100
                : 0.0;
    }

    @Override
    public String getDetails() {
        return String.format(
                "Специализация: %s, Сертификация: %s, Стиль: %s, Успешность: %.1f%%",
                specialization, certification, coachingStyle, getSuccessRate());
    }

    @Override
    public String toString() {
        return super.toString() + " | " + getDetails();
    }
}
