package software.latic.brelix;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import software.latic.helper.FileContent;
import software.latic.helper.PDFReader;
import software.latic.item.GermanTextItemData;
import software.latic.translation.Translation;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BrelixPdfTest {

    @ParameterizedTest
    @ValueSource(strings = {
            ".testfiles/Die kleine Eule Lili.pdf",
            ".testfiles/No-Title-Die kleine Eule Lili-1.pdf",
            ".testfiles/EuleLiliShortNoImg.pdf"
    })
    void testBrelixWithPdf(String pdfPath) throws IOException {
        FileContent fileContent = PDFReader.getInstance().getContent(pdfPath);
        
        StringBuilder sb = new StringBuilder();
        for (CharSequence cs : fileContent.getContent()) {
            sb.append(cs);
        }
        String text = sb.toString();
        
        assertNotNull(text, "Text should not be null for " + pdfPath);
        System.out.println("[DEBUG_LOG] Testing file: " + pdfPath);
        System.out.println("[DEBUG_LOG] Extracted text length: " + text.length());
        System.out.println("[DEBUG_LOG] Extracted font size (mm): " + fileContent.getFontSizeMm());
        
        Properties props = new Properties();
        try {
            props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Translation.getInstance().setLocale(Locale.GERMAN);
        Document doc = new Document(props, text);
        GermanTextItemData data = new GermanTextItemData(text);
        
        // Populate basic metrics required for Brelix
        int wordCount = 0;
        for (Sentence sent : doc.sentences()) {
            for (String word : sent.words()) {
                if (!word.matches("\\W+")) {
                    wordCount++;
                }
            }
        }
        
        data.setWordCount(wordCount);
        data.setSentenceCount(doc.sentences().size());
        if (data.getSentenceCount() > 0) {
            data.setAverageSentenceLengthWords((double) wordCount / data.getSentenceCount());
        }
        data.setPagesCount(fileContent.getPages());
        data.setFontSizeMm(fileContent.getFontSizeMm());
        
        // TTR (simplified for test)
        long uniqueWords = doc.sentences().stream()
                .flatMap(s -> s.words().stream())
                .filter(w -> !w.matches("\\W+"))
                .map(String::toLowerCase)
                .distinct()
                .count();
        if (wordCount > 0) {
            data.setTypeTokenRatio((double) uniqueWords / wordCount);
        }

        System.out.println("[DEBUG_LOG] Word count: " + data.getWordCount());
        System.out.println("[DEBUG_LOG] Sentence count: " + data.getSentenceCount());
        System.out.println("[DEBUG_LOG] Pages: " + data.getPagesCount());
        System.out.println("[DEBUG_LOG] TTR: " + data.getTypeTokenRatio());

        BrelixAnalyzer.getInstance().analyze(data, doc);

        System.out.println("[DEBUG_LOG] BRELIX 0 Score: " + data.getBrelix0Score() + " (Level: " + data.getBrelix0Level() + ")");
        System.out.println("[DEBUG_LOG] BRELIX 1 Score: " + data.getBrelix1Score() + " (Level: " + data.getBrelix1Level() + ")");
        System.out.println("[DEBUG_LOG] BRELIX 2 Score: " + data.getBrelix2Score() + " (Level: " + data.getBrelix2Level() + ")");
        System.out.println("[DEBUG_LOG] BRELIX 3 Score: " + data.getBrelix3Score() + " (Level: " + data.getBrelix3Level() + ")");
        System.out.println("[DEBUG_LOG] BRELIX 4 Score: " + data.getBrelix4Score() + " (Level: " + data.getBrelix4Level() + ")");
        System.out.println("[DEBUG_LOG] BRELIX 5 Score: " + data.getBrelix5Score() + " (Level: " + data.getBrelix5Level() + ")");
    }
}
