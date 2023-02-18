package software.latic.connectives;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
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

    private static final String FRONT_BOUNDARY = "(?<=[\\s,.:;\"'\\-«]|^)";
    private static final String REAR_BOUNDARY =  "(?=[\\s,.:;\"'\\-»]|$)";


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

    private int countConnective(List<Integer> start, int removeConnectives) {
        var count = new AtomicInteger();

        start.forEach(s -> count.getAndIncrement());

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
                var pattern = Pattern.compile(FRONT_BOUNDARY + connective.toLowerCase() + REAR_BOUNDARY);

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
            count += countConnective(matchedStart, connectivesTaggedNoun);
        }
        return count;
    }

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
                var values = Arrays.stream(c[1].split(",")).toList();

                System.out.println("----" + key + " " + Arrays.toString(values.toArray()));

                var pattern = Pattern.compile(FRONT_BOUNDARY + key + REAR_BOUNDARY);

                var matcher = pattern.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));

                while (matcher.find()) {
                    var conditionFulfilled = false;

                    for (Token token : sentence.tokens()) {
                        if (token.word().equalsIgnoreCase(key)) {
                            if (values.contains(token.posTag())) {
                                conditionFulfilled = true;
                                break;
                            }
                        }
                    }

                    if (!connective.contains(" ") && !connective.contains("-")) {
                        connectivesTaggedNoun += connectivesWithInvalidTag(sentence, connective);
                    }

                    if (App.getLoggingLevel() == Level.INFO) {
                        System.out.println("    singleWordConnectives: " + connective);
                    }

                    if (!matchedStart.contains(matcher.start()) && conditionFulfilled) {
                        matchedStart.add(matcher.start());
                        matchedEnd.add(matcher.end() - 1);
                    }
                }
            }
        }
        return countConnective(matchedStart, connectivesTaggedNoun);
    }

    protected int twoWordConnectivesInOneSentence(List<Sentence> sentences) {
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
                var patternLead = Pattern.compile(FRONT_BOUNDARY + connective[0].toLowerCase() + REAR_BOUNDARY);
                var patternFollow = Pattern.compile(FRONT_BOUNDARY + connective[1].toLowerCase() + REAR_BOUNDARY);

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
        var count = 0;

        for (String[] connective : twoWordInManySentencesConnectives) {
            if (connective.length < 2) {
                break;
            }
            var patternLead = Pattern.compile(FRONT_BOUNDARY + connective[0].toLowerCase() + REAR_BOUNDARY);
            var patternFollow = Pattern.compile(FRONT_BOUNDARY + connective[1].toLowerCase() + REAR_BOUNDARY);

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
                            Logging.getInstance().debug("BaseConnectives","InMany: " + connective[0] + " " + connective[1]);
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
                            if (connective[0].equalsIgnoreCase("not only") && connective[1].equalsIgnoreCase("but also")) {
                                count = count -2;
                            } else {
                                count++;
                            }
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
            return posTag.equals("NOUN") || posTag.equals("PROPN") || posTag.equals("VERB") || posTag.equals("AUX") || posTag.equals("INTJ");
            //NOUN; ADJ; VERB; AUX; INTJ; NUM; PROPN; PUNCT; SYM
            //REMOVED ADJ due to errors ADJ/ADV
        }
    }
}
