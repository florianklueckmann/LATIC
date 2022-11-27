package software.latic.connectives;

import edu.stanford.nlp.simple.Document;

public interface Connectives {
    Connectives connectives = null;
    static Connectives getInstance() {
        return connectives;
    }

    int connectivesInDocument(Document doc);
}
