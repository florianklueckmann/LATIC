//package software.latic;
//
//import software.latic.services.NlpTextAnalyzer;
//import software.latic.services.SimpleTextAnalyzer;
//import software.latic.services.TextFormattingService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.Assertions;
//
//import java.util.Dictionary;
//import java.util.HashMap;
//
//
//class PrimaryModelIT {
//    String enTestText = "I have a really nice house in the forest. Forest houses are my favourite.";
//    int enWordCount = 14;
//    double enAvgWordLength = 4.142857143;
//    int enSentenceCount = 2;
//    String deTestText = "Ich habe ein wirklich schönes Haus im Wald. Häuser im Wald mag ich am liebsten.";
//    int deWordCount = 15;
//
//    TestItem deItemEarthAndSun = new TestItem(
//            "Die Erde dreht sich um die Sonne. Wie lange dauert diese Umlaufzeit ca.?\n" +
//                    "12 Stunden.\n" +
//                    "1 Tag.\n" +
//                    "3 Monate.\n" +
//                    "1 Jahr.",
//            21, 6, 3.5, 3.90, 13.66);
//
////    PrimaryModel primaryModel = new PrimaryModel();
//
//    TextFormattingService textFormatter = new TextFormattingService();
//    SimpleTextAnalyzer simpleTextAnalyzer = new SimpleTextAnalyzer(textFormatter);
//    NlpTextAnalyzer nlp = new NlpTextAnalyzer();
//
//    PrimaryModel primaryModel = new PrimaryModel(simpleTextAnalyzer, textFormatter, nlp);
//
//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void manualTest() {
//
//        System.out.println("Hi");
//
//        primaryModel.setLanguage("german");
//
//        simpleTextAnalyzer.setDoc(doc);
//        System.out.println(simpleTextAnalyzer.getTextLength(paragraphs));
//        //initializeDocument(input);
//        System.out.println(doc.text());
//
//
//
//        testingStuff();
//
//    }
//
//    //CORENLP Stuff
//    void testingStuff() {
//
//        //var sent = doc.sentence(0);
//
//        for (var sen : doc.sentences()) {
//            var posTags = sen.posTags();
//            log(sen);
//            log("POS: \n" + posTags);
//            log((int) posTags.stream().filter(e -> e.equals("ADP")).count());
//        }
//
//        System.out.println("WC: " +simpleTextAnalyzer.getWordCount());
//        System.out.println("SC: " +simpleTextAnalyzer.getSentenceCount());
//        System.out.println("AVGSENTWW: " +simpleTextAnalyzer.getAverageSentenceLengthCharactersWithoutWhitespaces());
//        System.out.println("AVGSENT: " +simpleTextAnalyzer.getAverageSentenceLengthCharacters());
//        System.out.println();
//    }
//
//
//    protected void calculateStuff() {
//        System.out.println("Hi");
//
//        setLanguage("german");
//
//        simpleTextAnalyzer.setDoc(doc);
//        System.out.println(simpleTextAnalyzer.getTextLength(paragraphs));
//        //initializeDocument(input);
//        System.out.println(doc.text());
//
//        testingStuff();
//    }
//}
