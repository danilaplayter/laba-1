package textcleaner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CleaningRecord {
    private final String original;
    private final String cleaned;
    private final int mode;
    private final LocalDateTime timestamp;

    public CleaningRecord(String original, String cleaned, int mode) {
        this.original = original;
        this.cleaned = cleaned;
        this.mode = mode;
        this.timestamp = LocalDateTime.now();
    }

    public String getOriginal() {
        return original;
    }

    public String getCleaned() {
        return cleaned;
    }

    public int getMode() {
        return mode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "CleaningRecord{"
                + "timestamp="
                + getFormattedTimestamp()
                + ", mode="
                + mode
                + ", original='"
                + original
                + '\''
                + ", cleaned='"
                + cleaned
                + '\''
                + '}';
    }
}
