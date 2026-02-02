package sportclub.config;

import java.math.BigDecimal;

public class AppConstants {
    public static final String ADMIN_PASSWORD = "admin123";
    public static final String DATA_DIRECTORY = "data/";
    public static final String LOGS_DIRECTORY = "logs/";
    public static final String REPORTS_DIRECTORY = "reports/";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String BINARY_DATA_FILE = "data/club_data.dat";
    public static final String JSON_DATA_FILE = "data/club_data.json";
    public static final String XML_DATA_FILE = "data/club_data.xml";
    public static final String CSV_DATA_FILE = "data/club_data.csv";

    public static final int MIN_AGE = 16;
    public static final int MAX_AGE = 70;
    public static final BigDecimal MIN_SALARY = BigDecimal.valueOf(100);
    public static final BigDecimal MAX_SALARY = BigDecimal.valueOf(1000000);
    public static final int MIN_TRAINING_DURATION = 15;
    public static final int MAX_TRAINING_DURATION = 240;
    public static final int MIN_TRAINING_INTENSITY = 1;
    public static final int MAX_TRAINING_INTENSITY = 10;
}
