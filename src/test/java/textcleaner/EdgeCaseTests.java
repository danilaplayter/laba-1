package textcleaner;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EdgeCaseTests {

    private final TextCleaner textCleaner = new TextCleaner();

    @Test
    void testVeryLongText() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("word ");
        }

        String result = textCleaner.cleanText(longText.toString(), 2);
        assertThat(result.split(" ")).hasSize(1000);
    }

    @Test
    void testUnicodeCharacters() {
        String input = "Привет 🎉 мир 🌍 123";
        String result = textCleaner.cleanText(input, 6);
        assertThat(result).isEqualTo("Привет мир");
    }

    @Test
    void testMixedCaseText() {
        String input = "Hello World JAVA";
        String result = textCleaner.cleanText(input, 2);
        assertThat(result).isEqualTo("Hello World JAVA");
    }
}
