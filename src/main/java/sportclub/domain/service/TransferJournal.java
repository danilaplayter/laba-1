package sportclub.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sportclub.domain.model.Member;

public class TransferJournal {
  private final LinkedList<TransferRecord> transferRecords;
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public TransferJournal() {
    this.transferRecords = new LinkedList<>();
  }

  public void recordTransfer(Member member, TransferType type,
      String description, BigDecimal amount) {
    TransferRecord record = new TransferRecord(
        member.getId(),
        member.getName(),
        member.getRole(),
        member.getTeam(),
        type,
        description,
        amount,
        LocalDateTime.now()
    );
    transferRecords.addFirst(record);
  }

  public void recordTransfer(Member member, TransferType type, String description) {
    recordTransfer(member, type, description, BigDecimal.ZERO);
  }

  public List<TransferRecord> getAllRecords() {
    return new LinkedList<>(transferRecords);
  }

  public List<TransferRecord> getRecordsByMember(int memberId) {
    List<TransferRecord> result = new LinkedList<>();
    for (TransferRecord record : transferRecords) {
      if (record.getMemberId() == memberId) {
        result.add(record);
      }
    }
    return result;
  }

  public List<TransferRecord> getRecordsByType(TransferType type) {
    List<TransferRecord> result = new LinkedList<>();
    for (TransferRecord record : transferRecords) {
      if (record.getType() == type) {
        result.add(record);
      }
    }
    return result;
  }

  public List<TransferRecord> getRecentRecords(int count) {
    List<TransferRecord> result = new LinkedList<>();
    int limit = Math.min(count, transferRecords.size());
    for (int i = 0; i < limit; i++) {
      result.add(transferRecords.get(i));
    }
    return result;
  }

  public void clear() {
    transferRecords.clear();
  }

  public int getRecordCount() {
    return transferRecords.size();
  }

  public String generateReport() {
    StringBuilder report = new StringBuilder();
    report.append("=== ОТЧЕТ ПО ЖУРНАЛУ ТРАНСФЕРОВ ===\n");
    report.append("Дата генерации: ")
        .append(LocalDateTime.now().format(DATE_FORMATTER))
        .append("\n\n");
    report.append("Всего записей: ").append(getRecordCount()).append("\n\n");

    for (TransferRecord record : transferRecords) {
      report.append(record).append("\n");
    }
    return report.toString();
  }


  public enum TransferType {
    TRANSFER_IN("Переход в клуб"),
    TRANSFER_OUT("Уход из клуба"),
    CONTRACT_EXTENSION("Продление контракта"),
    CONTRACT_TERMINATION("Расторжение контракта"),
    SALARY_CHANGE("Изменение зарплаты"),
    POSITION_CHANGE("Изменение позиции"),
    TEAM_CHANGE("Переход в другую команду");

    private final String description;

    TransferType(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  @AllArgsConstructor
  @Getter
  @Setter
  public static class TransferRecord {
    private final int memberId;
    private final String memberName;
    private final String role;
    private final String team;
    private final TransferType type;
    private final String description;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    @Override
    public String toString() {
      return String.format("[%s] %s: %s (%s, %s) - %s %s",
          timestamp.format(DATE_FORMATTER),
          type.getDescription(),
          memberName,
          role,
          team,
          description,
          amount.compareTo(BigDecimal.ZERO) > 0 ?
              String.format("(Сумма: %s)", amount) : "");
    }
  }
}