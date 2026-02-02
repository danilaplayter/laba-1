package sportclub.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Manager extends Member {

    private String department;
    private String responsibilities;
    private int contractsSigned;
    private BigDecimal budgetManaged;

    public Manager(
            String name,
            int age,
            String team,
            String department,
            String responsibilities,
            BigDecimal baseSalary) {
        super(name, age, team, baseSalary);
        this.role = "Менеджер";
        this.department = department;
        this.responsibilities = responsibilities;
        this.contractsSigned = 0;
        this.budgetManaged = BigDecimal.ZERO;
    }

    @Override
    public String train(int durationMinutes, int intensity, LocalDate date) {
        String result =
                String.format(
                        "Курс повышения квалификации для менеджера %s: %d минут",
                        name, durationMinutes);

        addTrainingResult(result, date);
        return result;
    }

    @Override
    public BigDecimal calculateBonus() {
        BigDecimal contractBonus = BigDecimal.valueOf(contractsSigned * 500);
        BigDecimal budgetBonus = budgetManaged.multiply(BigDecimal.valueOf(0.01));
        return super.calculateBonus().add(contractBonus).add(budgetBonus);
    }

    public void signContract(BigDecimal value) {
        contractsSigned++;
        budgetManaged = budgetManaged.add(value);
    }

    @Override
    public String getDetails() {
        return String.format(
                "Отдел: %s, Обязанности: %s, Контрактов: %d, Бюджет: %s",
                department, responsibilities, contractsSigned, budgetManaged);
    }

    @Override
    public String toString() {
        return super.toString() + " | " + getDetails();
    }
}
