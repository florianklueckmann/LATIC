package software.latic.helper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PDFReader implements FileReader {
    private static final PDFReader reader = new PDFReader();
    public static PDFReader getInstance() {
        return reader;
    }

    public FileContent getContent(String fileName) throws IOException {
        PDDocument document = PDDocument.load(new File(fileName));

        String text = "";
        int pages = document.getNumberOfPages();
        double averageFontSizePt = 12.0;

        if (!document.isEncrypted()) {
            FontSizeStripper stripper = new FontSizeStripper();
            text = stripper.getText(document);
            averageFontSizePt = stripper.getAverageFontSize();
        }
        document.close();

        // Convert pt to mm (1 pt = 0.352778 mm)
        double fontSizeMm = averageFontSizePt * 0.352778;

        return new FileContent(List.of(text), pages, fontSizeMm);
    }

    private static class FontSizeStripper extends PDFTextStripper {
        private final List<Float> fontSizes = new ArrayList<>();

        public FontSizeStripper() throws IOException {
            super();
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
            for (TextPosition textPosition : textPositions) {
                fontSizes.add(textPosition.getFontSizeInPt());
            }
            super.writeString(text, textPositions);
        }

        public double getAverageFontSize() {
            if (fontSizes.isEmpty()) {
                return 12.0;
            }
            double sum = 0;
            for (float size : fontSizes) {
                sum += size;
            }
            return sum / fontSizes.size();
        }
    }
}
