package software.latic.helper;

import java.util.List;

public class FileContent {
    private final List<CharSequence> content;
    private final int pages;
    private final double fontSizeMm;

    public FileContent(List<CharSequence> content, int pages, double fontSizeMm) {
        this.content = content;
        this.pages = pages;
        this.fontSizeMm = fontSizeMm;
    }

    public List<CharSequence> getContent() {
        return content;
    }

    public int getPages() {
        return pages;
    }

    public double getFontSizeMm() {
        return fontSizeMm;
    }
}
