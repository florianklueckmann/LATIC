package software.latic.connectives;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import software.latic.App;
import software.latic.Logging;
import software.latic.helper.CsvReader;
import software.latic.translation.SupportedLocales;
import software.latic.translation.Translation;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class BaseConnectives implements Connectives {

    List<String> singleWordConnectives;
    List<String[]> twoWordInOneSentenceConnectives;
    List<String[]> twoWordInManySentencesConnectives;

    private static final String FRONT_BOUNDARY = "(?<=[\\s,.:;\"'\\-«]|^)";
    private static final String REAR_BOUNDARY =  "(?=[\\s,.:;\"'\\-»]|$)";

    private static final List<Locale> SUPPORTED_LOCALES = List.of(SupportedLocales.ENGLISH.getLocale(), SupportedLocales.GERMAN.getLocale());

    private Locale currentLocale = Translation.getInstance().getLocale();

    private static final Connectives baseConnectives = new BaseConnectives();

    public static Connectives getInstance() {
        return baseConnectives;
    }

    public BaseConnectives() {
        initialize();
    }

    private boolean stopInitialize() {
        if (this.currentLocale == null) {
            return false;
        } else {
            return !SUPPORTED_LOCALES.contains(Translation.getInstance().getLocale()) && this.currentLocale == Translation.getInstance().getLocale();
        }
    }

    public void initialize() {
        if (stopInitialize()) {
            return;
        }


        singleWordConnectives = CsvReader.getInstance()
                .readFile(String.format("connectives/singleWordConnectives_%s.csv", Translation.getInstance().getLanguageTag()));
        twoWordInOneSentenceConnectives = CsvReader.getInstance()
                .readFile(String.format("connectives/twoWordInOneSentenceConnectives_%s.csv", Translation.getInstance().getLanguageTag()))
                .stream()
                .map(s -> s.split(";")).toList();
        twoWordInManySentencesConnectives = CsvReader.getInstance()
                .readFile(String.format("connectives/twoWordInManySentenceConnectives_%s.csv", Translation.getInstance().getLanguageTag()))
                .stream()
                .map(s -> s.split(";")).toList();

        currentLocale = Translation.getInstance().getLocale();
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

    private int singleWordConnectivesInDocument(List<Sentence> sentences) {
        var count = 0;

        for (var sentence : sentences) {

            List<Connective> connectives = new ArrayList<>();

            var matchedStart = new ArrayList<Integer>();
            var matchedEnd = new ArrayList<Integer>();

            if (App.getLoggingLevel() == Level.INFO) {
                System.out.println("Sentence: " + sentence.text());
            }
            for (String e : singleWordConnectives) {
                var entry = List.of(e.split(";"));

                var connective = entry.get(0);
                var conditions = Arrays.stream(entry.size() > 1 ? entry.get(1).split(",") : new String[0]).toList();


                var pattern = Pattern.compile(FRONT_BOUNDARY + connective.toLowerCase() + REAR_BOUNDARY);

                var matcher = pattern.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));

                while (matcher.find()) {
                    if (!connective.contains(" ") && !connective.contains("-")) {
                        if (!hasValidTag(sentence, connective)) {
                            break;
                        }
                    }

                    var conditionFulfilled = false;

                    if (conditions.size() > 0) {
                        for (Token token : sentence.tokens()) {
                            if (token.word().equalsIgnoreCase(connective)) {
                                if (conditions.contains(token.posTag()) && token.beginPosition() == matcher.start()) {
                                    conditionFulfilled = true;
                                    break;
                                }
                            }
                        }
                    }

                    sentence.tokens().forEach(token -> System.out.println(token.word() + " " + token.posTag()));

                    if (App.getLoggingLevel() == Level.INFO) {
                        System.out.println("    singleWordConnectives: " + connective);
                    }

                    if (!matchedStart.contains(matcher.start()) && !matchedEnd.contains(matcher.end())
                            && (conditionFulfilled || conditions.size() == 0)) {
                        matchedStart.add(matcher.start());
                        matchedEnd.add(matcher.end() - 1);
                        connectives.add(new Connective(sentence.text().substring(matcher.start(), matcher.end()), matcher.start(), matcher.end()));
                    }
                }
            }
            count += removeConnectivesIfOverlap(connectives).size();
        }
        return count;
    }

    public List<Connective> removeConnectivesIfOverlap(List<Connective> connectives) {
        connectives.sort(Comparator.comparingInt(Connective::getStart));
        for (int i = 0; i < connectives.size(); i++) {
            Connective currentConnective = connectives.get(i);
            for (int j = i+1; j < connectives.size(); j++) {
                Connective nextConnective = connectives.get(j);
                if (nextConnective.getStart() < currentConnective.getEnd()) {
                    connectives.remove(currentConnective);
                    i--;
                    break;
                }
            }
        }
        return connectives;
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
                                && hasValidTag(sentence, connective[0])
                                && hasValidTag(sentence, connective[1])) {
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
                            if (hasValidTag(sentence, connective[0]) && hasValidTag(sentence, connective[1])) {
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


    private boolean hasValidTag(Sentence sentence, String connective) {
        for (var token : sentence.tokens()) {
            if (token.word().equalsIgnoreCase(connective)) {
                return !isValidTagForConnective(token.posTag());
            }
        }
        return true;
    }


    private boolean isValidTagForConnective(String posTag) {
        if (Translation.getInstance().getLocale().equals(Locale.ENGLISH)) {
            return posTag.contains("NN") || posTag.contains("JJ") || posTag.startsWith("V");
        } else {
            return posTag.equals("NOUN") || posTag.equals("PROPN") || posTag.equals("VERB") || posTag.equals("AUX") || posTag.equals("INTJ");
            //NOUN; ADJ; VERB; AUX; INTJ; NUM; PROPN; PUNCT; SYM
            //REMOVED ADJ due to errors ADJ/ADV
        }
    }
}
