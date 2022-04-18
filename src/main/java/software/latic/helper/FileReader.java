package software.latic.helper;

import java.io.IOException;
import java.util.List;

public interface FileReader {
    List<CharSequence> getContent(String filePath) throws IOException;
}
