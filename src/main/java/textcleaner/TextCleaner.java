package textcleaner;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextCleaner {
    private static final Logger logger = LogManager.getLogger(TextCleaner.class);

    private boolean removeSpaces = false;
    private boolean removeExtraSpaces = true;
    private boolean removeSpecialChars = false;
    private boolean removeDigits = false;
    private boolean removePunctuation = false;

    private final List<CleaningRecord> history = new ArrayList<>();

    public String cleanText(String text, int mode) {
        if (text == null || text.isEmpty()) {
            logger.warn("Попытка очистки пустого текста");
            return text;
        }

        String result = text;

        switch (mode) {
            case 1:
                result = removeAllSpaces(text);
                break;
            case 2:
                result = removeExtraSpaces(text);
                break;
            case 3:
                result = removeSpecialCharacters(text);
                break;
            case 4:
                result = removeDigits(text);
                break;
            case 5:
                result = removePunctuation(text);
                break;
            case 6:
                result = complexClean(text);
                break;
            default:
                logger.error("Неизвестный режим очистки: " + mode);
                throw new IllegalArgumentException("Неизвестный режим очистки: " + mode);
        }

        logger.info(
                "Текст очищен. Исходная длина: "
                        + text.length()
                        + ", конечная длина: "
                        + result.length());
        return result;
    }

    public String cleanText(String text) {
        String result = text;

        if (removeExtraSpaces) {
            result = removeExtraSpaces(result);
        }
        if (removeSpaces) {
            result = removeAllSpaces(result);
        }
        if (removeSpecialChars) {
            result = removeSpecialCharacters(result);
        }
        if (removeDigits) {
            result = removeDigits(result);
        }
        if (removePunctuation) {
            result = removePunctuation(result);
        }

        return result;
    }

    private String removeAllSpaces(String text) {
        return text.replaceAll("\\s+", "");
    }

    private String removeExtraSpaces(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    private String removeSpecialCharacters(String text) {
        return text.replaceAll("[^a-zA-Zа-яА-ЯёЁ0-9\\s]", "");
    }

    private String removeDigits(String text) {
        return text.replaceAll("\\d", "");
    }

    private String removePunctuation(String text) {
        return text.replaceAll("[\\p{P}\\p{S}]", "");
    }

    private String complexClean(String text) {
        String result = text;
        result = removeDigits(result);
        result = removeSpecialCharacters(result);
        result = removePunctuation(result);
        result = removeExtraSpaces(result);
        return result.trim();
    }

    public void saveToHistory(String original, String cleaned, int mode) {
        CleaningRecord record = new CleaningRecord(original, cleaned, mode);
        history.add(record);
        logger.info(
                "Запись добавлена в историю. Режим: "
                        + mode
                        + ", исходная длина: "
                        + original.length()
                        + ", конечная длина: "
                        + cleaned.length());
    }

    public static Logger getLogger() {
        return logger;
    }

    public boolean isRemoveSpaces() {
        return removeSpaces;
    }

    public void setRemoveSpaces(boolean removeSpaces) {
        this.removeSpaces = removeSpaces;
        logger.debug("Установлено removeSpaces = " + removeSpaces);
    }

    public boolean isRemoveExtraSpaces() {
        return removeExtraSpaces;
    }

    public void setRemoveExtraSpaces(boolean removeExtraSpaces) {
        this.removeExtraSpaces = removeExtraSpaces;
        logger.debug("Установлено removeExtraSpaces = " + removeExtraSpaces);
    }

    public boolean isRemoveSpecialChars() {
        return removeSpecialChars;
    }

    public void setRemoveSpecialChars(boolean removeSpecialChars) {
        this.removeSpecialChars = removeSpecialChars;
        logger.debug("Установлено removeSpecialChars = " + removeSpecialChars);
    }

    public boolean isRemoveDigits() {
        return removeDigits;
    }

    public void setRemoveDigits(boolean removeDigits) {
        this.removeDigits = removeDigits;
        logger.debug("Установлено removeDigits = " + removeDigits);
    }

    public boolean isRemovePunctuation() {
        return removePunctuation;
    }

    public void setRemovePunctuation(boolean removePunctuation) {
        this.removePunctuation = removePunctuation;
        logger.debug("Установлено removePunctuation = " + removePunctuation);
    }

    public List<CleaningRecord> getHistory() {
        return new ArrayList<>(history);
    }
}
