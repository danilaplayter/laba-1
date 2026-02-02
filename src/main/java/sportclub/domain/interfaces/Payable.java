package sportclub.domain.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Payable {

    void setBaseSalary(BigDecimal salary);

    BigDecimal getBaseSalary();

    BigDecimal calculateBonus();

    PaymentResult paySalary(LocalDate date);

    String getPaymentHistory();

    BigDecimal getTotalPaid();

    record PaymentResult(
            BigDecimal amount,
            BigDecimal bonus,
            LocalDate date,
            boolean successful,
            String message) {}
}
