package software.latic.helper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class PDFReader implements FileReader {
    private static final PDFReader reader = new PDFReader();
    public static PDFReader getInstance() {
        return reader;
    }

    /**
     * Opt-in: strip non-body front-matter / production artifacts from the extracted
     * text (off by default to preserve existing behaviour). Useful for InDesign-exported
     * children's-book PDFs whose text layer contains production lines, imprint and page
     * numbers that otherwise inflate the word-difficulty / polysyllable metrics
     * (see .notes/pdf-reader-hanna-debug-2026-06-08.md).
     */
    private boolean stripFrontMatter = false;

    public void setStripFrontMatter(boolean stripFrontMatter) {
        this.stripFrontMatter = stripFrontMatter;
    }

    public boolean isStripFrontMatter() {
        return stripFrontMatter;
    }

    public FileContent getContent(String fileName) throws IOException {
        PDDocument document = PDDocument.load(new File(fileName));

        String text = "";
        int pages = document.getNumberOfPages();
        double averageFontSizePt = 12.0;
        double medianFontSizePt = 12.0;

        if (!document.isEncrypted()) {
            FontSizeStripper stripper = new FontSizeStripper();
            text = stripper.getText(document);
            averageFontSizePt = stripper.getAverageFontSize();
            medianFontSizePt = stripper.getMedianFontSize();
        }
        document.close();

        if (stripFrontMatter) {
            text = removeFrontMatter(text);
        }

        // Convert pt to mm (1 pt = 0.352778 mm)
        double ptToMm = 0.352778;
        double fontSizeMm = averageFontSizePt * ptToMm;
        double fontSizeMmMedian = medianFontSizePt * ptToMm;

        return new FileContent(List.of(text), pages, fontSizeMm, fontSizeMmMedian);
    }

    // --- Front-matter filtering -------------------------------------------------

    // InDesign export artifacts, e.g. "10132_hanna_berlin_inhalt.indd   2-3 ...Uhr".
    private static final Pattern INDD_LINE = Pattern.compile(".*\\.indd\\b.*");
    // Lines that are only page numbers / spreads, e.g. "2 3" or "1".
    private static final Pattern PAGE_NUMBER_LINE = Pattern.compile("[\\d\\s\\-]+");
    // Imprint / cover markers.
    private static final Pattern IMPRINT_LINE = Pattern.compile(
            "(?i).*(\\bISBN\\b|©|\\(c\\)|copyright|www\\.|https?://"
                    + "|\\bHrsg\\.|Alle Rechte vorbehalten|Regenbogen-Lesekiste"
                    + "|\\bLesestufe\\s*\\d|verlag für pädagogische medien|\\bVerlag\\s+GmbH).*");

    /**
     * Removes lines that are clearly not body text: InDesign production lines, lines that
     * are only page numbers, and common imprint/cover markers (ISBN, ©, URLs, publisher,
     * series). Conservative on purpose — it does not try to detect blurbs or titles, only
     * the unambiguous artifacts that distort the analysis. Package-private for testing.
     */
    static String removeFrontMatter(String text) {
        StringBuilder sb = new StringBuilder();
        for (String line : text.split("\\R", -1)) {
            if (isFrontMatterLine(line)) {
                continue;
            }
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    static boolean isFrontMatterLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        return INDD_LINE.matcher(trimmed).matches()
                || PAGE_NUMBER_LINE.matcher(trimmed).matches()
                || IMPRINT_LINE.matcher(trimmed).matches();
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

        public double getMedianFontSize() {
            return median(fontSizes);
        }
    }

    /** Median of the values, or 12.0 if empty. Package-private for testing. */
    static double median(List<Float> values) {
        if (values.isEmpty()) {
            return 12.0;
        }
        List<Float> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int n = sorted.size();
        if (n % 2 == 1) {
            return sorted.get(n / 2);
        }
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }
}
