package textcleaner;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TextCleanerAppTest {

    @TempDir Path tempDir;

    @Test
    void testMainMethodWithExitCommand() {
        String input = "0\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Thread thread =
                new Thread(
                        () -> {
                            TextCleanerApp.main(new String[] {});
                        });
        thread.start();

        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String output = out.toString();
        assertThat(output).contains("Программа очистки текста");
        assertThat(output).contains("Программа завершена");
    }
}
