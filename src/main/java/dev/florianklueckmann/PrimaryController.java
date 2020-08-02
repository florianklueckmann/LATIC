package dev.florianklueckmann;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import javafx.fxml.FXML;

import edu.stanford.nlp.io.IOUtils;
import org.apache.log4j.BasicConfigurator;

import edu.stanford.nlp.simple.*;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void calculateStuff() throws IOException {
        System.out.println("Hi");
        doStuff();
    }

    private int calculateWordCount(Document doc){

        var wordCount = 0;

        for (Sentence sent : doc.sentences()) {
            wordCount += sent.words().size();
        }

        return wordCount;
    }

    private void doStuff() {
        BasicConfigurator.configure();

//        Scanner scanner = new Scanner(System. in);

        System.out.println("Which Language is your text? [english/german]]");
//        String lang = scanner.nextLine();
        String lang = "english";
        System.out.println("language: " + lang);
        System.out.println("Please enter your text.");
        String text = "My house is a beautiful house.";
//        String text = scanner.nextLine();

        //TODO: Ask for Input and asynchronously startup the NLP?
        Properties props = new Properties();

        try {
            props.load(IOUtils.readerFromString("StanfordCoreNLP-" + lang + ".properties"));
        } catch (IOException e) {
            //TODO: Error Handling
            e.printStackTrace();
        }

        // Annotation ann = corenlp.process("Bananen wachsen an Bäumen im Dschungel.");
        //We should be workig with one Documet object
        Document doc = new Document(props, text);



        // Create a document. No computation is done yet.
        //Document doc = new Document(germanProperties, "Bananen wachsen an Bäumen im Dschungel." );
        //Document docGer = new Document("Bananen wachsen an Bäumen im Dschungel.");


//        doc.sentences(germanProperties);

        System.out.println("Doc word count: " + calculateWordCount(doc)); //fast

        for (Sentence sent : doc.sentences()) {  // Will iterate over the sentences
            // We're only asking for words -- no need to load any models yet
            System.out.println("The second word of the sentence '" + sent + "' is " + sent.word(1)); //fast
            // When we ask for the lemma, it will load and run the part of speech tagger
            System.out.println("The third lemma of the sentence '" + sent + "' is " + sent.lemma(2)); //3.5sec
            // When we ask for the parse, it will load and run the parser
            System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse()); //11.4sec
            // ...

        }
    }

}
