# LATIC - Linguistic Analyzer for Text and Item Characteristics

LATIC is a free and open source desktop application that allows you to analyze and count text and item characteristics
in English, French, German and Spanish texts or items. The [Stanford CoreNLP 4.4.0](https://github.com/stanfordnlp/CoreNLP)
is used to tag parts of speech and allows LATIC to provide very accurate results.
You can find our evaluation in the documentation.

### Available Text and Item Characteristics

* **Analysis at the word level** 
    * Parts of speech (e.g. adjectives, interjections, nouns, ...)
    * Word length (characters, syllables)
* **Analysis at the sentence level**
    * Number of sentences
    * Sentence length (characters with or without spaces, syllables, words)
* **Analysis at the text level**
    * Readability indices (e.g. Flesch, LIX)
    * Lexical diversity/type-token ratio
    * Word count
    * Syllable count
* **BRELIX (Beta) — German text analysis**
    * Bremer Erstlese-Index for assessing reading difficulty of German texts aimed at beginning readers
    * Multiple index variants (BRELIX 0–5) with increasing analytical depth:
        * **BRELIX 0**: LIX corrected by orthographic word difficulty
        * **BRELIX 1**: Adds text density (words per page)
        * **BRELIX 2**: Stronger weighting of word difficulty
        * **BRELIX 3**: Includes typographic factors (font size)
        * **BRELIX 4**: Includes sentence complexity (subordinate clauses)
        * **BRELIX 5**: Includes lexical variety (type-token ratio), suited for general children's literature
    * Analyzes multi-graphemes, rare letters, consonant clusters, and Dehnungs-h
    * Configurable page count and font size via a collapsible settings panel
    * Enable via the "BRELIX (Beta)" checkbox in the file tab

A detailed description of the text and item characteristics and instructions on how to use LATIC are provided in the documentation.

### Contribution

The Standford CoreNLP supports many other languages (e.g. Arabic, Italian, Hungarian), and we'd love to add them to LATIC.
However, this requires the support of people who speak one of these languages at a very good level. 
If you'd like to implement one of the remaining languages with us, please [let us know](mailto:hello@latic.software)!

Of course, we'd also be happy if you'd like to help us improve LATIC in other ways.
Feel free to create issues for feature requests, bugs, or other improvements.

### Development
To compile and package the project use 
`mvn clean compile package` and run the file `LATIC-1.2.2.jar` using `java -jar shade/LATIC-1.2.2.jar`
Note that you have to launch the class AppLauncher to stat the application in your IDE.