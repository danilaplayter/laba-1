package textcleaner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class TextCleanerTest {

    private TextCleaner textCleaner;

    @BeforeEach
    void setUp() {
        textCleaner = new TextCleaner();
    }

    @Test
    void testCleanTextWithNullInput() {
        assertThat(textCleaner.cleanText(null, 1)).isNull();
    }

    @Test
    void testCleanTextWithEmptyInput() {
        assertThat(textCleaner.cleanText("", 1)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"'hello  world', 'helloworld'", "'  test  ', 'test'", "'a b c d', 'abcd'"})
    void testMode1_RemoveAllSpaces(String input, String expected) {
        String result = textCleaner.cleanText(input, 1);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "'hello     world', 'hello world'",
        "'   multiple   spaces   ', 'multiple spaces'",
        "'normal text', 'normal text'"
    })
    void testMode2_RemoveExtraSpaces(String input, String expected) {
        String result = textCleaner.cleanText(input, 2);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "'test@email.com', 'testemailcom'",
        "'price: $100.50', 'price 10050'",
        "'hello#world!', 'helloworld'"
    })
    void testMode3_RemoveSpecialCharacters(String input, String expected) {
        String result = textCleaner.cleanText(input, 3);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"'test123', 'test'", "'1a2b3c', 'abc'", "'no digits here', 'no digits here'"})
    void testMode4_RemoveDigits(String input, String expected) {
        String result = textCleaner.cleanText(input, 4);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "'Hello, World!', 'Hello World'",
        "'test... string???', 'test string'",
        "'no-punctuation', 'nopunctuation'"
    })
    void testMode5_RemovePunctuation(String input, String expected) {
        String result = textCleaner.cleanText(input, 5);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "'  Hello,   World! 123...  ', 'Hello World'",
        "'Test@email.com (2024)', 'Testemailcom'",
        "'  multiple   spaces  & symbols!  ', 'multiple spaces symbols'"
    })
    void testMode6_ComplexClean(String input, String expected) {
        String result = textCleaner.cleanText(input, 6);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testInvalidMode() {
        assertThatThrownBy(() -> textCleaner.cleanText("test", 99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Неизвестный режим очистки");
    }

    @Test
    void testSaveToHistory() {
        String original = "test";
        String cleaned = "test";
        int mode = 1;

        textCleaner.saveToHistory(original, cleaned, mode);

        assertThat(textCleaner.getHistory()).hasSize(1);
        assertThat(textCleaner.getHistory().get(0).getOriginal()).isEqualTo(original);
        assertThat(textCleaner.getHistory().get(0).getCleaned()).isEqualTo(cleaned);
        assertThat(textCleaner.getHistory().get(0).getMode()).isEqualTo(mode);
    }

    @Test
    void testHistoryIsImmutable() {
        textCleaner.saveToHistory("test", "test", 1);

        textCleaner.getHistory().clear();
        assertThat(textCleaner.getHistory()).hasSize(1);
    }

    @Test
    void testAllSettings() {
        textCleaner.setRemoveSpaces(true);
        textCleaner.setRemoveExtraSpaces(false);
        textCleaner.setRemoveSpecialChars(true);
        textCleaner.setRemoveDigits(true);
        textCleaner.setRemovePunctuation(true);

        assertThat(textCleaner.isRemoveSpaces()).isTrue();
        assertThat(textCleaner.isRemoveExtraSpaces()).isFalse();
        assertThat(textCleaner.isRemoveSpecialChars()).isTrue();
        assertThat(textCleaner.isRemoveDigits()).isTrue();
        assertThat(textCleaner.isRemovePunctuation()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void testWhitespaceOnlyInput(String input) {
        for (int mode = 1; mode <= 6; mode++) {
            String result = textCleaner.cleanText(input, mode);
            assertThat(result).isNotNull();
        }
    }

    @Test
    void testRussianTextCleaning() {
        String input = "  Привет,   мир! 123...  ";
        String result = textCleaner.cleanText(input, 6);
        assertThat(result).isEqualTo("Привет мир");
    }
}
