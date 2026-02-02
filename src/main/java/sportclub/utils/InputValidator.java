package sportclub.utils;

import java.math.BigDecimal;
import sportclub.config.AppConstants;

public class InputValidator {

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }

    public static boolean isValidAge(int age) {
        return age >= AppConstants.MIN_AGE && age <= AppConstants.MAX_AGE;
    }

    public static boolean isValidSalary(BigDecimal salary) {
        return salary != null
                && salary.compareTo(AppConstants.MIN_SALARY) >= 0
                && salary.compareTo(AppConstants.MAX_SALARY) <= 0;
    }

    public static boolean isValidTeam(String team) {
        return team != null && !team.trim().isEmpty() && team.length() <= 50;
    }

    public static boolean isValidPosition(String position) {
        return position != null && !position.trim().isEmpty() && position.length() <= 30;
    }

    public static boolean isValidJerseyNumber(int number) {
        return number >= 1 && number <= 99;
    }

    public static boolean isValidTrainingDuration(int duration) {
        return duration >= AppConstants.MIN_TRAINING_DURATION
                && duration <= AppConstants.MAX_TRAINING_DURATION;
    }

    public static boolean isValidTrainingIntensity(int intensity) {
        return intensity >= AppConstants.MIN_TRAINING_INTENSITY
                && intensity <= AppConstants.MAX_TRAINING_INTENSITY;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
