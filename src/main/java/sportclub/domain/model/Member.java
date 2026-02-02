package sportclub.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sportclub.domain.interfaces.Payable;
import sportclub.domain.interfaces.Trainable;

@Getter
@Setter
public abstract class Member implements Serializable, Trainable, Payable {

    protected int id;
    protected String name;
    protected int age;
    protected LocalDate joinDate;
    protected String role;
    protected String team;
    protected BigDecimal baseSalary;
    protected List<TrainingRecord> trainingRecords;
    protected List<PaymentRecord> paymentRecords;

    private static int nextId = 1;

    public Member(String name, int age, String team, BigDecimal baseSalary) {
        this.id = nextId++;
        this.name = name;
        this.age = age;
        this.joinDate = LocalDate.now();
        this.team = team;
        this.baseSalary = baseSalary != null ? baseSalary : BigDecimal.ZERO;
        this.trainingRecords = new ArrayList<>();
        this.paymentRecords = new ArrayList<>();
    }

    public int getExperience() {
        return Period.between(joinDate, LocalDate.now()).getYears();
    }

    public abstract String getDetails();

    @Override
    public String toString() {
        return String.format(
                "ID: %d | Имя: %s | Возраст: %d | Роль: %s | Команда: %s | Стаж: %d лет | Зарплата: %s",
                id, name, age, role, team, getExperience(), baseSalary);
    }

    @Override
    public String train(int durationMinutes, int intensity, LocalDate date) {
        String result =
                String.format(
                        "Тренировка %s: %d минут, интенсивность %d/10",
                        name, durationMinutes, intensity);
        trainingRecords.add(new TrainingRecord(durationMinutes, intensity, date, result));
        return result;
    }

    @Override
    public int getTrainingCount() {
        return trainingRecords.size();
    }

    @Override
    public double getAverageIntensity() {
        return trainingRecords.stream().mapToInt(TrainingRecord::intensity).average().orElse(0.0);
    }

    @Override
    public void addTrainingResult(String result, LocalDate date) {
        trainingRecords.add(new TrainingRecord(60, 5, date, result));
    }

    @Override
    public String getTrainingHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("История тренировок для ").append(name).append(":\n");
        trainingRecords.forEach(
                record ->
                        sb.append(record.date()).append(": ").append(record.result()).append("\n"));
        return sb.toString();
    }

    @Override
    public double calculateProgress() {
        if (trainingRecords.size() < 2) return 0.0;
        int firstIntensity = trainingRecords.get(0).intensity();
        int lastIntensity = trainingRecords.get(trainingRecords.size() - 1).intensity();
        return ((double) (lastIntensity - firstIntensity) / firstIntensity) * 100;
    }

    @Override
    public void setBaseSalary(BigDecimal salary) {
        this.baseSalary = salary;
    }

    @Override
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    @Override
    public BigDecimal calculateBonus() {
        return baseSalary.multiply(BigDecimal.valueOf(getExperience() * 0.1));
    }

    @Override
    public PaymentResult paySalary(LocalDate date) {
        BigDecimal bonus = calculateBonus();
        BigDecimal total = baseSalary.add(bonus);

        PaymentRecord record = new PaymentRecord(date, baseSalary, bonus, total);
        paymentRecords.add(record);

        return new PaymentResult(
                total,
                bonus,
                date,
                true,
                String.format("Выплачено %s (базовая: %s, бонус: %s)", total, baseSalary, bonus));
    }

    @Override
    public String getPaymentHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("История выплат для ").append(name).append(":\n");
        paymentRecords.forEach(
                record ->
                        sb.append(record.date()).append(": ").append(record.total()).append("\n"));
        return sb.toString();
    }

    @Override
    public BigDecimal getTotalPaid() {
        return paymentRecords.stream()
                .map(PaymentRecord::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static void resetIdCounter() {
        nextId = 1;
    }

    public record TrainingRecord(int durationMinutes, int intensity, LocalDate date, String result)
            implements Serializable {}

    public record PaymentRecord(
            LocalDate date, BigDecimal baseSalary, BigDecimal bonus, BigDecimal total)
            implements Serializable {}
}
