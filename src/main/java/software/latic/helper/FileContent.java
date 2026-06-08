package software.latic.helper;

import java.util.List;

public class FileContent {
    private final List<CharSequence> content;
    private final int pages;
    private final double fontSizeMm;
    private final double fontSizeMmMedian;

    public FileContent(List<CharSequence> content, int pages, double fontSizeMm) {
        this(content, pages, fontSizeMm, fontSizeMm);
    }

    public FileContent(List<CharSequence> content, int pages, double fontSizeMm, double fontSizeMmMedian) {
        this.content = content;
        this.pages = pages;
        this.fontSizeMm = fontSizeMm;
        this.fontSizeMmMedian = fontSizeMmMedian;
    }

    public List<CharSequence> getContent() {
        return content;
    }

    public int getPages() {
        return pages;
    }

    /** Mean glyph height in mm across all extracted text. */
    public double getFontSizeMm() {
        return fontSizeMm;
    }

    /**
     * Median glyph height in mm — more robust to small print (imprint, page numbers,
     * production artifacts) than the mean, so it better reflects the body text size.
     */
    public double getFontSizeMmMedian() {
        return fontSizeMmMedian;
    }
}
