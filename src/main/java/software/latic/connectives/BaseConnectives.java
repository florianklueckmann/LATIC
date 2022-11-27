package software.latic.connectives;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import software.latic.helper.CsvReader;
import software.latic.translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class BaseConnectives implements Connectives {

    List<String> singleWordConnectives;

    private static final Connectives baseConnectives = new BaseConnectives();

    public static Connectives getInstance() {
        return baseConnectives;
    }

    private void initialize() {
        singleWordConnectives = CsvReader.getInstance()
                .readFile(String.format("connectives/singleWordConnectives_%s.csv", Translation.getInstance().getLanguageTag()));
    }

    @Override
    public int connectivesInDocument(Document doc) {
        initialize();
        var sentences = doc.sentences();

        var docConnectiveCount = 0;

        //TODO beim init für jedes Connective ein Pattern in einer PatternList erstelleN?

        docConnectiveCount += singleWordConnectivesInDocument(sentences);

        return docConnectiveCount;
    }

    private int countConnective(List<Integer> start, List<Integer> end) {
        var count = new AtomicInteger();

        start.forEach(s -> {
            if (!end.contains(s)) {
                count.getAndIncrement();
            }
        });

        return count.get();
    }

    private int singleWordConnectivesInDocument(List<Sentence> sentences) {
        var matchedStart = new ArrayList<Integer>();
        var matchedEnd = new ArrayList<Integer>();

        for (var sentence : sentences) {
            for (String connective : singleWordConnectives) {
                var pattern = Pattern.compile("\\b" + connective + "\\b");

                var matcher = pattern.matcher(sentence.text());

                while (matcher.find()) {
                    matchedStart.add(matcher.start() - 1);
                    matchedEnd.add(matcher.end());
                }
            }
        }
        return countConnective(matchedStart, matchedEnd);
    }
}
