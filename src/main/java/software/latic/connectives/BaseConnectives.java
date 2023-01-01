package software.latic.connectives;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import software.latic.helper.CsvReader;
import software.latic.translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class BaseConnectives implements Connectives {

    List<String> singleWordConnectives;
    List<String[]> twoWordInOneSentenceConnectives;

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
        twoWordInOneSentenceConnectives = CsvReader.getInstance()
                .readFile(String.format("connectives/twoWordInOneSentenceConnectives_%s.csv", Translation.getInstance().getLanguageTag()))
                .stream()
                .map(s -> s.split(";")).toList();
    }

    @Override
    public int connectivesInDocument(Document doc) {
        var sentences = doc.sentences();

        var docConnectiveCount = 0;

        //TODO beim init für jedes Connective ein Pattern in einer PatternList erstelleN?

        docConnectiveCount += singleWordConnectivesInDocument(sentences);
        docConnectiveCount += twoWordConnectivesInDocument(sentences);

        return docConnectiveCount;
    }

    private int countConnective(List<Integer> start, List<Integer> end, int removeConnectives) {
        var count = new AtomicInteger();

        start.forEach(s -> {
            if (!end.contains(s)) {
                count.getAndIncrement();
            }
        });

        return count.get() - removeConnectives;
    }

    private int singleWordConnectivesInDocument(List<Sentence> sentences) {
        var matchedStart = new ArrayList<Integer>();
        var matchedEnd = new ArrayList<Integer>();

        var connectivesTaggedNoun = 0;

        for (var sentence : sentences) {
            for (String connective : singleWordConnectives) {
                var pattern = Pattern.compile("\\b" + connective + "\\b");

                var matcher = pattern.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));

                while (matcher.find()) {
                    connectivesTaggedNoun = connectivesWithInvalidTag(sentence, connective);

                    matchedStart.add(matcher.start() - 1);
                    matchedEnd.add(matcher.end());
                }
            }
        }
        return countConnective(matchedStart, matchedEnd, connectivesTaggedNoun);
    }

    protected int twoWordConnectivesInDocument(List<Sentence> sentences) {
        var matchedStart = new ArrayList<Integer>();
        var matchedEnd = new ArrayList<Integer>();

        var connectivesTaggedNoun = 0;
        var count = 0;

        for (var sentence : sentences) {
            for (String[] connective : twoWordInOneSentenceConnectives) {
                if (connective.length < 2) {
                    break;
                }
                var patternLead = Pattern.compile("\\b" + connective[0] + "\\b");
                var patternFollow = Pattern.compile("\\b" + connective[1] + "\\b");

                var matcherLead = patternLead.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));

                while (matcherLead.find()) {
                    var matchedLeadEnd = matcherLead.end();

                    var matcherFollow = patternFollow.matcher(sentence.text().toLowerCase(Translation.getInstance().getLocale()));

                    if (matcherFollow.find(matchedLeadEnd)) {
                        var matchedFollowStart = matcherFollow.start();

                        if(matchedLeadEnd < matchedFollowStart
                                && (connectivesWithInvalidTag(sentence, connective[0])
                                    + connectivesWithInvalidTag(sentence, connective[0])
                                        == 0)) {
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
        for (var token: sentence.tokens()) {
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
            return posTag.contains("NN") || posTag.contains("JJ");
        } else {
            return posTag.equals("NOUN") || posTag.equals("ADJ");
        }
    }
}
