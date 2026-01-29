package software.latic.brelix;

public class BrelixWordDifficultyCalculator {

    /**
     * Berechnet den Rohwert des Wortschwierigkeits-Indexes (wortschwierig)
     * basierend auf den Rohzählungen der orthographischen Komplexität.
     *
     * Formel: wortschwierig = mehrgliedrig + Buchst_selten + Konshfg [1]
     * (Hier wird Buchst_selten_ohne_c verwendet, um die Definition aus der Konversation zu spiegeln,
     * auch wenn die Formel in Quelle [1] Buchst_selten verwendet.)
     *
     * @param mehrgliedrig Anzahl der mehrgliedrigen Grapheme.
     * @param buchstSeltenOhneC Anzahl der seltenen Buchstaben (bereinigt um c).
     * @param konshfg Anzahl der Konsonantenhäufungen.
     * @return Der Rohwert der Wortschwierigkeit.
     */
    public double berechneWortschwierigkeitRohwert(int mehrgliedrig, int buchstSeltenOhneC, int konshfg) {
        // Die Addition dieser drei Komponenten ergibt den Rohwert für die Wortschwierigkeit [1]
        double wortschwierig = (double) mehrgliedrig + buchstSeltenOhneC + konshfg;
        return wortschwierig;
    }

    /**
     * Berechnet den Prozentsatz der Wortschwierigkeit (Proz_wortschwierig).
     *
     * Formel: Proz_wortschwierig = wortschwierig / wörter * 100 [2]
     *
     * @param wortschwierig Der Rohwert der Wortschwierigkeit (aus berechneWortschwierigkeitRohwert).
     * @param woerter Die Gesamtzahl der Wörter im analysierten Text.
     * @return Der Prozentsatz der Wortschwierigkeit.
     */
    public double berechneProzWortschwierigkeit(double wortschwierig, int woerter) {
        if (woerter == 0) {
            return 0.0;
        }
        // Normalisierung auf die Gesamtzahl der Wörter und Multiplikation mit 100 [2]
        double prozWortschwierigkeit = (wortschwierig / woerter) * 100;
        return prozWortschwierigkeit;
    }

    // Beispiel für die Nutzung des Rechners
    public static void main(String[] args) {
        BrelixWordDifficultyCalculator rechner = new BrelixWordDifficultyCalculator();

        // ANGENOMMENE ROHDATEN (müssten durch komplexe NLP-Analyse gewonnen werden)
        int anzahlMehrgliedrigeGrapheme = 50;
        int anzahlSelteneBuchstabenOhneC = 15;
        int anzahlKonsonantenhäufungen = 20;
        int gesamtzahlWoerter = 500;

        // 1. Berechnung des Rohwerts (wortschwierig)
        double rohwert = rechner.berechneWortschwierigkeitRohwert(
                anzahlMehrgliedrigeGrapheme,
                anzahlSelteneBuchstabenOhneC,
                anzahlKonsonantenhäufungen
        );
        System.out.println("Rohwert der Wortschwierigkeit: " + rohwert); // Ergebnis: 85.0

        // 2. Berechnung des Prozentsatzes (Proz_wortschwierig)
        double prozentWert = rechner.berechneProzWortschwierigkeit(rohwert, gesamtzahlWoerter);
        System.out.println("Prozentsatz der Wortschwierigkeit (Proz_wortschwierig): " + prozentWert + " %");
        // Ergebnis: 85.0 / 500 * 100 = 17.0 %
    }
}
