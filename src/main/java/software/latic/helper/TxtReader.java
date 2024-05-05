package software.latic.helper;

import com.ibm.icu.text.CharsetDetector;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class TxtReader implements FileReader {
    private static final TxtReader reader = new TxtReader();
    public static TxtReader getInstance() {
        return reader;
    }
    @Override
    public List<CharSequence> getContent(String filePath) throws IOException {
        try (var inputStream = new FileInputStream(filePath)) {
            var detector = new CharsetDetector();
            detector.setText(inputStream.readAllBytes());
            var detected = detector.detect();
            var reader= new BufferedReader(new InputStreamReader(new FileInputStream(filePath), detected.getName()));
            return reader.lines().collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
