package sportclub.domain.model;

public class Coach extends Member {
    private String specialization;
    private String certification;

    public Coach(String name, int age, String team, String specialization, String certification) {
        super(name, age, team);
        this.role = "Тренер";
        this.specialization = specialization;
        this.certification = certification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    @Override
    public String getDetails() {
        return String.format("Специализация: %s, Сертификация: %s", specialization, certification);
    }

    @Override
    public String toString() {
        return super.toString() + " | " + getDetails();
    }
}
