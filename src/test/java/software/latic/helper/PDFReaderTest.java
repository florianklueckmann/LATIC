//package software.latic.helper;
//
//import org.junit.jupiter.api.Test;
//import java.io.IOException;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class PDFReaderTest {
//
//    @Test
//    void testFontSizeExtraction() throws IOException {
//        String pdfPath = ".testfiles/EuleLiliShortNoImg.pdf";
//        FileContent content = PDFReader.getInstance().getContent(pdfPath);
//
//        assertNotNull(content);
//        double fontSizeMm = content.getFontSizeMm();
//
//        // EuleLiliShortNoImg.pdf is expected to have standard font size (~12pt)
//        // 12pt * 0.352778 = 4.233336 mm
//        assertEquals(4.23, fontSizeMm, 0.1, "Font size should be around 4.23mm (12pt)");
//
//        System.out.println("[DEBUG_LOG] File: " + pdfPath + " Font size: " + fontSizeMm + " mm");
//    }
//
//    @Test
//    void testLargeFontSizeExtraction() throws IOException {
//        String pdfPath = ".testfiles/Die kleine Eule Lili.pdf";
//        FileContent content = PDFReader.getInstance().getContent(pdfPath);
//
//        assertNotNull(content);
//        double fontSizeMm = content.getFontSizeMm();
//
//        // This file was found to have ~8.98mm in our previous run
//        assertTrue(fontSizeMm > 6.0, "Font size should be larger than 6mm for this file");
//
//        System.out.println("[DEBUG_LOG] File: " + pdfPath + " Font size: " + fontSizeMm + " mm");
//    }
//}
