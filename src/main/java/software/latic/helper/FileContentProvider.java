package software.latic.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileContentProvider {
    public static List<CharSequence> getContent(String filename) throws IOException {
        var type = getFileTypeExtension(filename);

        System.out.println(type);

        return switch (type.toLowerCase(Locale.ROOT)) {
            case "pdf" -> PDFReader.getInstance().getContent(filename);
            case "docx" -> DocxReader.getInstance().getContent(filename);
            case "txt", "csv" -> TxtReader.getInstance().getContent(filename);

            default -> new ArrayList<>();
        };
    }

    public static String getFileTypeExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
