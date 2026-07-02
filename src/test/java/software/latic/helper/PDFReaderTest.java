package software.latic.helper;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFReaderTest {

    @Test
    void medianHandlesOddEvenAndEmpty() {
        assertEquals(12.0, PDFReader.median(List.of()), 1e-9, "empty -> fallback 12.0");
        assertEquals(3.0, PDFReader.median(List.of(3.0f)), 1e-9);
        assertEquals(4.0, PDFReader.median(List.of(2.0f, 4.0f, 6.0f)), 1e-9, "odd -> middle");
        assertEquals(5.0, PDFReader.median(List.of(2.0f, 4.0f, 6.0f, 8.0f)), 1e-9, "even -> mean of middle two");
        // robust to outliers (small print) vs the mean
        assertEquals(12.0, PDFReader.median(List.of(1.0f, 12.0f, 12.0f, 12.0f)), 1e-9, "median ignores the low outlier");
    }

    @Test
    void detectsArtifactLines() {
        assertTrue(PDFReader.isFrontMatterLine("10132_hanna_berlin_inhalt.indd   2-3 15.09.2009   9:32:48 Uhr"));
        assertTrue(PDFReader.isFrontMatterLine("2 3"));
        assertTrue(PDFReader.isFrontMatterLine("1"));
        assertTrue(PDFReader.isFrontMatterLine("ISBN: 978-3-403-10132-1"));
        assertTrue(PDFReader.isFrontMatterLine("© 2009 verlag für pädagogische medien"));
        assertTrue(PDFReader.isFrontMatterLine("www.vpm-verlag.de"));
        assertTrue(PDFReader.isFrontMatterLine("Lesestufe 6"));
        assertTrue(PDFReader.isFrontMatterLine("REGENBOGEN-LESEKISTE II"));
        assertTrue(PDFReader.isFrontMatterLine("in der Auer Verlag GmbH"));
    }

    @Test
    void keepsBodyLines() {
        assertFalse(PDFReader.isFrontMatterLine("Der Hund läuft durch den Park."));
        assertFalse(PDFReader.isFrontMatterLine("Die Kinder spielen gerne"));
        assertFalse(PDFReader.isFrontMatterLine("„Wohin gehst du denn?“,"));
        assertFalse(PDFReader.isFrontMatterLine("")); // blank line preserved
    }

    @Test
    void removeFrontMatterStripsArtifactsKeepsBody() {
        String raw = String.join("\n",
                "REGENBOGEN-LESEKISTE II",
                "ISBN: 978-3-403-10132-1",
                "www.vpm-verlag.de",
                "Lesestufe 6",
                "10132_hanna_berlin_inhalt.indd   2-3 15.09.2009   9:32:48 Uhr",
                "Der Hund läuft durch den Park.",
                "Der Vogel singt am Morgen.",
                "2 3",
                "Die Kinder spielen gerne im Garten.");

        String cleaned = PDFReader.removeFrontMatter(raw);

        assertTrue(cleaned.contains("Der Hund läuft durch den Park."));
        assertTrue(cleaned.contains("Der Vogel singt am Morgen."));
        assertTrue(cleaned.contains("Die Kinder spielen gerne im Garten."));
        assertFalse(cleaned.contains(".indd"));
        assertFalse(cleaned.contains("ISBN"));
        assertFalse(cleaned.contains("vpm-verlag"));
        assertFalse(cleaned.contains("Lesestufe 6"));
        assertFalse(cleaned.contains("REGENBOGEN"));

        // exactly the three body lines survive
        long bodyLines = cleaned.lines().filter(l -> !l.isBlank()).count();
        assertEquals(3, bodyLines, "only the three body sentences should remain");
    }
}
