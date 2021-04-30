package dev.florianklueckmann.latic.text_analyzer;

import dev.florianklueckmann.latic.task.Task;
import dev.florianklueckmann.latic.item.TextItemData;
import edu.stanford.nlp.simple.Document;
import javafx.collections.ObservableList;

public interface TextAnalyzer
{
    Document getDoc();

    void setDoc(Document doc);

    void processTasks(TextItemData textItemData, ObservableList<Task> tasks);
}
