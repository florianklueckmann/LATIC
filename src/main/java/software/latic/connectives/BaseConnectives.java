package software.latic.connectives;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import software.latic.App;
import software.latic.Logging;
import software.latic.helper.CsvReader;
import software.latic.translation.Translation;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class BaseConnectives implements Connectives {

    List<String> singleWordConnectives;
    List<String> singleWordConnectivesWithCondition;
    List<String[]> twoWordInOneSentenceConnectives;
    List<String[]> twoWordInManySentencesConnectives;

    private static final Connectives baseConnectives = new BaseConnectives();

    public static Connectives getInstance() {
        return baseConnectives;
    }

    public BaseConnectives() {
        initialize();
    }

    private void initialize() {
        singleWordConnectives = CsvReader.getInstance()
                .readFile(String.format("connectives/singleWordConnectives_%s.csv", Translation.getInstance().getLanguageTag()));
        singleWordConnectivesWithCondition = CsvReader.getInstance()
                .readFile(String.format("connectives/singleWordConnectivesWithCondition_%s.csv", Translation.getInstance().getLanguageTag()));
        twoWordInOneSentenceConnectives = CsvReader.getInstance()
                .readFile(String.format("connectives/twoWordInOneSentenceConnectives_%s.csv", Translation.getInstance().getLanguageTag()))
                .stream()
                .map(s -> s.split(";")).toList();
        twoWordInManySentencesConnectives = CsvReader.getInstance()
                .readFile(String.format("connectives/twoWordInManySentenceConnectives_%s.csv", Translation.getInstance().getLanguageTag()))
                .stream()
                .map(s -> s.split(";")).toList();
    }

    @Override
    public int connectivesInDocument(Document doc) {
        var sentences = doc.sentences();

        var docConnectiveCount = 0;

        //TODO beim init für jedes Connective ein Pattern in einer PatternList erstelleN?

        var singleWordConnectivesInDocument = singleWordConnectivesInDocument(sentences);
        var twoWordConnectivesInOneSentence = twoWordConnectivesInOneSentence(sentences);
        var twoWordConnectivesInManySentence = twoWordConnectivesInManySentence(doc);

        docConnectiveCount += singleWordConnectivesInDocument;
        docConnectiveCount += twoWordConnectivesInOneSentence;
        docConnectiveCount += twoWordConnectivesInManySentence;


        if (App.getLoggingLevel() == Level.INFO) {
            System.out.println("---------------------------------------------");
            System.out.println("singleWordConnectivesInDocument: " + singleWordConnectivesInDocument);
            System.out.println("twoWordConnectivesInOneSentence: " + twoWordConnectivesInOneSentence);
            System.out.println("twoWordConnectivesInManySentence: " + twoWordConnectivesInManySentence);
            System.out.println("total connectives: " + docConnectiveCount);
            System.out.println("---------------------------------------------");
        }

        return docConnectiveCount;
    }

    private int countConnective(List<Integer> start, List<Integer> end, int removeConnectives) {
        var count = new AtomicInteger();

        var st = start;

        start.forEach(s -> {
            //TODO Discuss if we want to count every connective in a chain of connectives
//            if (!end.contains(s)) {
                count.getAndIncrement();
//            }
        });

        return count.get() - removeConnectives;
    }

    private int singleWordConnectivesInDocument(List<Sentence> sentences) {
        var count = 0;

        for (var sentence : sentences) {

            var matchedStart = new ArrayList<Integer>();
            var matchedEnd = new ArrayList<Integer>();

            var connectivesTaggedNoun = 0;


            if (App.getLoggingLevel() == Level.INFO) {
                System.out.println("Sentence: " + sentence.text());
            }
            for (String connective : singleWordConnectives) {
                var pattern = Pattern.compile("\\b" + connective.toLowerCase() + "\\b");

                var matcher = pattern.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));

                while (matcher.find()) {
                    //TODO Look into that

                    if (!connective.contains(" ") && !connective.contains("-")) {
                        connectivesTaggedNoun += connectivesWithInvalidTag(sentence, connective);
                    }

                    if (App.getLoggingLevel() == Level.INFO) {
                        System.out.println("    singleWordConnectives: " + connective);
                    }

                    if (!matchedStart.contains(matcher.start()) && !matchedEnd.contains(matcher.end())) {
                        matchedStart.add(matcher.start());
                        matchedEnd.add(matcher.end() - 1);
                    }
                }
            }
            count += countConnective(matchedStart, matchedEnd, connectivesTaggedNoun);
        }
        return count;
    }

//    private List<Connective> singleWordConnectivesInDocument(List<Sentence> sentences) {
//        var count = 0;
//
//        var connectives = new ArrayList<Connective>();
//
//        for (var sentence : sentences) {
//
//            if (App.getLoggingLevel() == Level.INFO) {
//                System.out.println("Sentence: " + sentence.text());
//            }
//            for (String connective : singleWordConnectives) {
//                var pattern = Pattern.compile("\\b" + connective.toLowerCase() + "\\b");
//
//                var matcher = pattern.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));
//
//                while (matcher.find()) {
//                    if (App.getLoggingLevel() == Level.INFO) {
//                        System.out.println("    singleWordConnectives: " + connective);
//                    }
//
//                    connectives.add(new Connective(sentence.text().substring(matcher.start(), matcher.end()), matcher.start(), matcher.end()));
//
//                }
//            }
//            count += countConnective(matchedStart, matchedEnd, connectivesTaggedNoun);
//        }
//        return count;
//    }

    public int singleWordConnectivesWithConditionInDocument(List<Sentence> sentences) {
        var matchedStart = new ArrayList<Integer>();
        var matchedEnd = new ArrayList<Integer>();

        var connectivesTaggedNoun = 0;

        for (var sentence : sentences) {
            if (App.getLoggingLevel() == Level.INFO) {
                System.out.println("Sentence: " + sentence.text());
            }
            for (String connective : singleWordConnectivesWithCondition) {
                var c = (connective.split(";"));
                System.out.println(connective);
                System.out.println(c[0]);
                var key = c[0];
                var values = c[1].split(",");

                System.out.println("----" + key + " " + Arrays.toString(values));

                var pattern = Pattern.compile("\\b" + connective.toLowerCase() + "\\b");

//                System.out.println("pattern " + pattern.pattern());

                var matcher = pattern.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));

                while (matcher.find()) {
                    //TODO Look into that

                    if (!connective.contains(" ") && !connective.contains("-")) {
                        connectivesTaggedNoun += connectivesWithInvalidTag(sentence, connective);
                    }

                    if (App.getLoggingLevel() == Level.INFO) {
                        System.out.println("    singleWordConnectives: " + connective);
                    }

                    if (!matchedStart.contains(matcher.start())) {
                        matchedStart.add(matcher.start());
                        matchedEnd.add(matcher.end() - 1);
                    }
                }
            }
        }
        return countConnective(matchedStart, matchedEnd, connectivesTaggedNoun);
    }

    protected int twoWordConnectivesInOneSentence(List<Sentence> sentences) {
        var matchedStart = new ArrayList<Integer>();
        var matchedEnd = new ArrayList<Integer>();

        var connectivesTaggedNoun = 0;
        var count = 0;

        for (var sentence : sentences) {
            if (App.getLoggingLevel() == Level.INFO) {
                System.out.println("Sentence: " + sentence.text());
            }
            var connectivesInSentence = 0;
            for (String[] connective : twoWordInOneSentenceConnectives) {
                if (connective.length < 2) {
                    break;
                }
                var patternLead = Pattern.compile("\\b" + connective[0].toLowerCase() + "\\b");
                var patternFollow = Pattern.compile("\\b" + connective[1].toLowerCase() + "\\b");

                var matcherLead = patternLead.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));


                var lastFollowFound = -1;

                while (matcherLead.find()) {
                    var matchedLeadEnd = matcherLead.end();

                    var matcherFollow = patternFollow.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));

                    if (matcherFollow.find(matchedLeadEnd) && lastFollowFound < matchedLeadEnd) {
                        var matchedFollowStart = matcherFollow.start();
                        lastFollowFound = matcherFollow.end();

                        if (matchedLeadEnd < matchedFollowStart
                                && (connectivesWithInvalidTag(sentence, connective[0])
                                + connectivesWithInvalidTag(sentence, connective[1])
                                == 0)) {
                            connectivesInSentence++;
                            if (App.getLoggingLevel() == Level.INFO) {
                                System.out.println("    TwoWordInOneSentence Connectives: " + connective[0] + " " + connective[1]);
                            }
                        }
                    }
                }
            }
            count += connectivesInSentence;
            if (App.getLoggingLevel() == Level.INFO) {
                System.out.println("    TwoWordInOneSentence Connectives: " + connectivesInSentence);
            }
        }
        return count;
    }

    protected int twoWordConnectivesInManySentence(Document doc) {
        var matchedStart = new ArrayList<Integer>();
        var matchedEnd = new ArrayList<Integer>();

        var connectivesTaggedNoun = 0;
        var count = 0;

        for (String[] connective : twoWordInManySentencesConnectives) {
            if (connective.length < 2) {
                break;
            }
            var patternLead = Pattern.compile("\\b" + connective[0].toLowerCase() + "\\b");
            var patternFollow = Pattern.compile("\\b" + connective[1].toLowerCase() + "\\b");

            var matcherLead = patternLead.matcher(doc.text().toLowerCase(Translation.getInstance().getLocale()));


            var lastFollowFound = -1;

            while (matcherLead.find()) {
                var matchedLeadEnd = matcherLead.end();

                var matcherFollow = patternFollow.matcher(doc.text().toLowerCase(Translation.getInstance().getLocale()));

                if (matcherFollow.find(matchedLeadEnd) && lastFollowFound < matchedLeadEnd) {
                    var matchedFollowStart = matcherFollow.start();
                    lastFollowFound = matcherFollow.end();

                    if (matchedLeadEnd < matchedFollowStart) {
                        if (App.getLoggingLevel() == Level.INFO) {

                            System.out.println("InMany: " + connective[0] + " " + connective[1]);
                        }
                        var isValidConnective = false;
                        for (Sentence sentence : doc.sentences()) {
                            if (connectivesWithInvalidTag(sentence, connective[0])
                                    == 0 && connectivesWithInvalidTag(sentence, connective[1])
                                    == 0) {
                                isValidConnective = true;
                            }
                        }
                        if (isValidConnective) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }


    private int connectivesWithInvalidTag(Sentence sentence, String connective) {
        int count = 0;
        for (var token : sentence.tokens()) {
            if (token.word().equalsIgnoreCase(connective)) {
                if (isNotAValidConnective(token.posTag())) {
                    count++;
                }
            }
        }
        return count;
    }


    private boolean isNotAValidConnective(String posTag) {
        if (Translation.getInstance().getLocale().equals(Locale.ENGLISH)) {
            return posTag.contains("NN") || posTag.contains("JJ") || posTag.startsWith("V");
        } else {
            var invalid = new ArrayList<>(){};
            return posTag.equals("NOUN") || posTag.equals("ADJ") || posTag.equals("VERB") || posTag.equals("AUX") || posTag.equals("INTJ");
            //NOUN; ADJ; VERB; AUX; INTJ; NUM; PROPN; PUNCT; SYM
            //TODO REMOVE ADJ due to errors ADJ/ADV
        }
    }
}
