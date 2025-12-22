package software.latic.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileContentProvider {
    public static FileContent getContent(String filename) throws IOException {
        var type = getFileTypeExtension(filename);

        return switch (type.toLowerCase(Locale.ROOT)) {
            case "pdf" -> PDFReader.getInstance().getContent(filename);
            case "docx" -> DocxReader.getInstance().getContent(filename);
            case "txt", "csv" -> TxtReader.getInstance().getContent(filename);

            default -> new FileContent(new ArrayList<>(), 1, 6.0);
        };
    }

    public static String getFileTypeExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
