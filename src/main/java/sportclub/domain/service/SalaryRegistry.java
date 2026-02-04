// File: SalaryRegistry.java
package sportclub.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sportclub.domain.model.Member;

public class SalaryRegistry {
  private final HashMap<Integer, BigDecimal> salaryMap;
  private final HashMap<Integer, SalaryHistory> salaryHistoryMap;

  public SalaryRegistry() {
    this.salaryMap = new HashMap<>();
    this.salaryHistoryMap = new HashMap<>();
  }

  public void registerSalary(int memberId, BigDecimal salary) {
    salaryMap.put(memberId, salary);

    SalaryHistory history = salaryHistoryMap.getOrDefault(memberId, new SalaryHistory(memberId));
    history.addSalaryRecord(salary);
    salaryHistoryMap.put(memberId, history);
  }

  public void updateSalary(int memberId, BigDecimal newSalary) {
    if (salaryMap.containsKey(memberId)) {
      BigDecimal oldSalary = salaryMap.get(memberId);
      salaryMap.put(memberId, newSalary);

      SalaryHistory history = salaryHistoryMap.get(memberId);
      if (history != null) {
        history.addSalaryRecord(newSalary);
      }
    }
  }

  public BigDecimal getSalary(int memberId) {
    return salaryMap.getOrDefault(memberId, BigDecimal.ZERO);
  }

  public BigDecimal getTotalSalaryBudget() {
    return salaryMap.values().stream()
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public double getAverageSalary() {
    if (salaryMap.isEmpty()) return 0.0;
    return getTotalSalaryBudget().doubleValue() / salaryMap.size();
  }

  public BigDecimal getMinSalary() {
    return salaryMap.values().stream()
        .min(BigDecimal::compareTo)
        .orElse(BigDecimal.ZERO);
  }

  public BigDecimal getMaxSalary() {
    return salaryMap.values().stream()
        .max(BigDecimal::compareTo)
        .orElse(BigDecimal.ZERO);
  }

  public Map<String, BigDecimal> getSalaryByRole(Map<Integer, Member> members) {
    Map<String, BigDecimal> result = new HashMap<>();
    Map<String, Integer> countByRole = new HashMap<>();

    for (Map.Entry<Integer, BigDecimal> entry : salaryMap.entrySet()) {
      Member member = members.get(entry.getKey());
      if (member != null) {
        String role = member.getRole();
        BigDecimal current = result.getOrDefault(role, BigDecimal.ZERO);
        result.put(role, current.add(entry.getValue()));
        countByRole.put(role, countByRole.getOrDefault(role, 0) + 1);
      }
    }

    for (Map.Entry<String, BigDecimal> entry : result.entrySet()) {
      int count = countByRole.get(entry.getKey());
      if (count > 0) {
        entry.setValue(entry.getValue().divide(BigDecimal.valueOf(count), 2,
            BigDecimal.ROUND_HALF_UP));
      }
    }

    return result;
  }

  public SalaryHistory getSalaryHistory(int memberId) {
    return salaryHistoryMap.get(memberId);
  }

  public void removeSalary(int memberId) {
    salaryMap.remove(memberId);
    salaryHistoryMap.remove(memberId);
  }

  public void clear() {
    salaryMap.clear();
    salaryHistoryMap.clear();
  }

  public int getRegistrySize() {
    return salaryMap.size();
  }

  public static class SalaryHistory {
    private final int memberId;
    private final LinkedList<BigDecimal> salaryRecords;
    private final LinkedList<LocalDate> changeDates;

    public SalaryHistory(int memberId) {
      this.memberId = memberId;
      this.salaryRecords = new LinkedList<>();
      this.changeDates = new LinkedList<>();
    }

    public void addSalaryRecord(BigDecimal salary) {
      salaryRecords.add(salary);
      changeDates.add(LocalDate.now());
    }

    public BigDecimal getCurrentSalary() {
      return salaryRecords.isEmpty() ? BigDecimal.ZERO : salaryRecords.getLast();
    }

    public BigDecimal getPreviousSalary() {
      return salaryRecords.size() > 1 ?
          salaryRecords.get(salaryRecords.size() - 2) : BigDecimal.ZERO;
    }

    public BigDecimal getSalaryChange() {
      if (salaryRecords.size() < 2) return BigDecimal.ZERO;
      return getCurrentSalary().subtract(getPreviousSalary());
    }

    public double getSalaryGrowthPercentage() {
      if (salaryRecords.size() < 2) return 0.0;
      BigDecimal previous = getPreviousSalary();
      if (previous.compareTo(BigDecimal.ZERO) == 0) return 0.0;

      BigDecimal change = getSalaryChange();
      return change.divide(previous, 4, BigDecimal.ROUND_HALF_UP)
          .multiply(BigDecimal.valueOf(100))
          .doubleValue();
    }

    public List<SalaryRecord> getAllRecords() {
      List<SalaryRecord> records = new ArrayList<>();
      for (int i = 0; i < salaryRecords.size(); i++) {
        records.add(new SalaryRecord(
            changeDates.get(i),
            salaryRecords.get(i)
        ));
      }
      return records;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class SalaryRecord {
      private final LocalDate date;
      private final BigDecimal salary;

      @Override
      public String toString() {
        return String.format("%s: %s", date, salary);
      }
    }
  }
}