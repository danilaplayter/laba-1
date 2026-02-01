package sportclub.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Manager extends Member {
    private String department;
    private String responsibilities;

    public Manager(String name, int age, String team, String department, String responsibilities) {
        super(name, age, team);
        this.role = "Менеджер";
        this.department = department;
        this.responsibilities = responsibilities;
    }

    @Override
    public String getDetails() {
        return String.format("Отдел: %s, Обязанности: %s", department, responsibilities);
    }

    @Override
    public String toString() {
        return super.toString() + " | " + getDetails();
    }
}
