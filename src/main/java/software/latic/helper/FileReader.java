package software.latic.helper;

import java.io.IOException;

public interface FileReader {
    FileContent getContent(String filePath) throws IOException;
}
