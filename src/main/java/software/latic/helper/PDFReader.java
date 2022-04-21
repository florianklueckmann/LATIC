package software.latic.helper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.List;

public class PDFReader implements FileReader {
    private static final PDFReader reader = new PDFReader();
    public static PDFReader getInstance() {
        return reader;
    }

    public List<CharSequence> getContent(String fileName) throws IOException {
        PDDocument document = PDDocument.load(new File(fileName));

        String text = "";

        if (!document.isEncrypted()) {
            PDFTextStripper textStripper = new PDFTextStripper();
            text = textStripper.getText(document);
            document.close();
        }

        return List.of(text);
    }
}
