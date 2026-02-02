package sportclub.utils;

import java.util.Arrays;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {

    public static void logOperation(Logger logger, String operation, Object... details) {
        String message =
                String.format(
                        "Операция: %s | Детали: %s",
                        operation,
                        String.join(
                                ", ",
                                Arrays.stream(details)
                                        .map(Object::toString)
                                        .toArray(String[]::new)));
        logger.info(message);
    }

    public static void logError(Logger logger, String operation, Exception e) {
        logger.error("Ошибка при выполнении операции '{}': {}", operation, e.getMessage(), e);
    }

    public static void logStartup(Logger logger) {
        logger.info("=".repeat(60));
        logger.info("Запуск системы управления спортивным клубом");
        logger.info("=".repeat(60));
    }

    public static void logShutdown(Logger logger) {
        logger.info("=".repeat(60));
        logger.info("Завершение работы системы управления спортивным клубом");
        logger.info("=".repeat(60));
    }
}
