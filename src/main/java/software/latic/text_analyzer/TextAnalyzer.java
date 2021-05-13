package software.latic.text_analyzer;

import software.latic.task.Task;
import software.latic.item.TextItemData;
import edu.stanford.nlp.simple.Document;
import javafx.collections.ObservableList;

public interface TextAnalyzer
{
    Document getDoc();

    void setDoc(Document doc);

    void processTasks(TextItemData textItemData, ObservableList<Task> tasks);
}
