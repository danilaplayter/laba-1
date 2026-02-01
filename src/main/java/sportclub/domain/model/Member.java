package sportclub.domain.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Member implements Serializable {
    protected int id;
    protected String name;
    protected int age;
    protected LocalDate joinDate;
    protected String role;
    protected String team;

    private static int nextId = 1;

    public Member(String name, int age, String team) {
        this.id = nextId++;
        this.name = name;
        this.age = age;
        this.joinDate = LocalDate.now();
        this.team = team;
    }

    public int getExperience() {
        return Period.between(joinDate, LocalDate.now()).getYears();
    }

    public abstract String getDetails();

    @Override
    public String toString() {
        return String.format(
                "ID: %d | Имя: %s | Возраст: %d | Роль: %s | Команда: %s | Стаж: %d лет",
                id, name, age, role, team, getExperience());
    }

    public static void resetIdCounter() {
        nextId = 1;
    }
}
