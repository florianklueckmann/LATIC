package software.latic.helper;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import software.latic.PrimaryViewModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocxReader implements FileReader {
    private static final DocxReader reader = new DocxReader();
    public static DocxReader getInstance() {
        return reader;
    }

    public List<CharSequence> getContent(String fileName, Map<String, Boolean> options) throws IOException {
        Path filePath = Paths.get(fileName);

        XWPFDocument document = new XWPFDocument(Files.newInputStream(filePath));

        var content = new ArrayList<CharSequence>();

        if (options.getOrDefault("analyzeHeaders", false)) {
            content.addAll(getHeaders(document));
        }
        content.addAll(getParagraphs(document));

        if (options.getOrDefault("analyzeFooters", false)) {
            content.addAll(getFooters(document));
        }

//        var content = Stream.concat(getParagraphs(document).stream(), getFooters(document).stream()).toList();;

        document.close();

        return content;

    }

    public List<CharSequence> getContent(String fileName) throws IOException {
        var defaultOptions = Map.of("analyzeHeaders", false, "analyzeFooters", false);

        return getContent(fileName, defaultOptions);
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
