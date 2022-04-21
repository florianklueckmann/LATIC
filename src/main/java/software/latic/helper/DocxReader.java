package software.latic.helper;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocxReader implements FileReader {
    private static final DocxReader reader = new DocxReader();
    public static DocxReader getInstance() {
        return reader;
    }

    public List<CharSequence> getContent(String fileName) throws IOException {
        Path filePath = Paths.get(fileName);

        XWPFDocument document = new XWPFDocument(Files.newInputStream(filePath));

        var content = new ArrayList<CharSequence>();

        if (Boolean.parseBoolean(Settings.userPreferences.get("analyzeHeaders", "true"))) {
            content.addAll(getHeaders(document));
        }
        content.addAll(getParagraphs(document));

        if (Boolean.parseBoolean(Settings.userPreferences.get("analyzeFooters", "true"))) {
            content.addAll(getFooters(document));
        }

        document.close();

        return content;

    }

    public List<CharSequence> getHeaders(XWPFDocument document) {
        return document.getHeaderList().stream().map(XWPFHeaderFooter::getText).collect(Collectors.toList());
    }
    public List<CharSequence> getParagraphs(XWPFDocument document) {
        return document.getParagraphs().stream().map(XWPFParagraph::getText).collect(Collectors.toList());
    }
    public List<CharSequence> getFooters(XWPFDocument document) {
        return document.getFooterList().stream().map(XWPFHeaderFooter::getText).collect(Collectors.toList());
    }
}
