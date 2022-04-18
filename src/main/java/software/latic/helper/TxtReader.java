package software.latic.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TxtReader implements FileReader {
    private static final TxtReader reader = new TxtReader();
    public static TxtReader getInstance() {
        return reader;
    }
    @Override
    public List<CharSequence> getContent(String filePath) throws IOException {
        try(var reader= Files.newBufferedReader(Paths.get(filePath))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}
