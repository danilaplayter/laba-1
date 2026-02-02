package sportclub.domain.interfaces;

import java.time.LocalDate;

public interface Trainable {

    String train(int durationMinutes, int intensity, LocalDate date);

    int getTrainingCount();

    double getAverageIntensity();

    void addTrainingResult(String result, LocalDate date);

    String getTrainingHistory();

    double calculateProgress();
}
