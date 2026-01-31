package textcleaner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CleaningRecordTest {

    @Test
    void testCleaningRecordCreation() {
        String original = "test input";
        String cleaned = "test output";
        int mode = 2;

        CleaningRecord record = new CleaningRecord(original, cleaned, mode);

        assertThat(record.getOriginal()).isEqualTo(original);
        assertThat(record.getCleaned()).isEqualTo(cleaned);
        assertThat(record.getMode()).isEqualTo(mode);
        assertThat(record.getTimestamp()).isNotNull();
        assertThat(record.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void testFormattedTimestamp() {
        CleaningRecord record = new CleaningRecord("test", "test", 1);

        String formatted = record.getFormattedTimestamp();
        assertThat(formatted).isNotNull();
        assertThat(formatted).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }

    @Test
    void testToString() {
        CleaningRecord record = new CleaningRecord("hello", "world", 3);

        String stringRepresentation = record.toString();
        assertThat(stringRepresentation)
                .contains("CleaningRecord")
                .contains("hello")
                .contains("world")
                .contains("mode=3");
    }

    @Test
    void testRecordEquality() {
        CleaningRecord record1 = new CleaningRecord("test", "test", 1);
        CleaningRecord record2 = new CleaningRecord("test", "test", 1);

        assertThat(record1).isNotSameAs(record2);
        assertThat(record1.getOriginal()).isEqualTo(record2.getOriginal());
        assertThat(record1.getCleaned()).isEqualTo(record2.getCleaned());
        assertThat(record1.getMode()).isEqualTo(record2.getMode());
    }
}
