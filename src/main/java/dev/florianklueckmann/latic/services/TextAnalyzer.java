package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.Task;
import dev.florianklueckmann.latic.TextItemData;
import edu.stanford.nlp.simple.Document;
import javafx.collections.ObservableList;

public interface TextAnalyzer
{
    Document getDoc();

    void setDoc(Document doc);

    void processTasks(TextItemData textItemData, ObservableList<Task> tasks);
}
