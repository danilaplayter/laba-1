package sportclub.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import sportclub.domain.interfaces.Payable;
import sportclub.domain.model.Member;

public class FinanceService {

    public Payable.PaymentResult paySalary(Member member, LocalDate date) {
        if (member instanceof Payable payable) {
            return payable.paySalary(date);
        }
        return new Payable.PaymentResult(
                BigDecimal.ZERO, BigDecimal.ZERO, date, false, "Сотрудник не поддерживает выплаты");
    }

    public BigDecimal calculateTotalSalaries(List<Member> members) {
        return members.stream().map(Member::getBaseSalary).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalBonuses(List<Member> members) {
        return members.stream()
                .map(Member::calculateBonus)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalPayments(List<Member> members) {
        return calculateTotalSalaries(members).add(calculateTotalBonuses(members));
    }

    public BigDecimal getTotalPaidToDate(List<Member> members) {
        return members.stream().map(Member::getTotalPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void adjustSalary(Member member, BigDecimal newSalary) {
        if (member != null && newSalary != null && newSalary.compareTo(BigDecimal.ZERO) >= 0) {
            member.setBaseSalary(newSalary);
        }
    }
}
