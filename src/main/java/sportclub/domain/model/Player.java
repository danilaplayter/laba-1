package sportclub.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player extends Member {

    private String position;
    private int jerseyNumber;
    private int goalsScored;
    private int assists;
    private double fitnessLevel;

    public Player(
            String name,
            int age,
            String team,
            String position,
            int jerseyNumber,
            BigDecimal baseSalary) {
        super(name, age, team, baseSalary);
        this.role = "Игрок";
        this.position = position;
        this.jerseyNumber = jerseyNumber;
        this.goalsScored = 0;
        this.assists = 0;
        this.fitnessLevel = 100.0;
    }

    @Override
    public String train(int durationMinutes, int intensity, LocalDate date) {
        double fitnessIncrease = (durationMinutes * intensity) / 100.0;
        fitnessLevel = Math.min(100.0, fitnessLevel + fitnessIncrease);

        String result =
                String.format(
                        "Тренировка игрока %s: %d минут, интенсивность %d/10. Фитнес: %.1f%% (+%.1f%%)",
                        name, durationMinutes, intensity, fitnessLevel, fitnessIncrease);

        addTrainingResult(result, date);
        return result;
    }

    @Override
    public BigDecimal calculateBonus() {
        BigDecimal performanceBonus = BigDecimal.valueOf(goalsScored * 1000 + assists * 500);
        return super.calculateBonus().add(performanceBonus);
    }

    public void scoreGoal() {
        goalsScored++;
    }

    public void makeAssist() {
        assists++;
    }

    public double getPerformanceRating() {
        return (goalsScored * 1.5 + assists) / Math.max(1, getTrainingCount());
    }

    @Override
    public String getDetails() {
        return String.format(
                "Позиция: %s, Номер: %d, Голы: %d, Ассисты: %d, Фитнес: %.1f%%",
                position, jerseyNumber, goalsScored, assists, fitnessLevel);
    }

    @Override
    public String toString() {
        return super.toString() + " | " + getDetails();
    }
}
