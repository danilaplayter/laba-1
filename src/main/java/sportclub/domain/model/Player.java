package sportclub.domain.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player extends Member {
    private String position;
    private int jerseyNumber;

    public Player(String name, int age, String team, String position, int jerseyNumber) {
        super(name, age, team);
        this.role = "Игрок";
        this.position = position;
        this.jerseyNumber = jerseyNumber;
    }

    @Override
    public String getDetails() {
        return String.format("Позиция: %s, Номер: %d", position, jerseyNumber);
    }

    @Override
    public String toString() {
        return super.toString() + " | " + getDetails();
    }
}
