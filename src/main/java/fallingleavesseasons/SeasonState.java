package fallingleavesseasons;

/**
 * Wird vom Mixin gelesen: Faktor, mit dem die blockspezifischen Chancen von Falling Leaves Plus
 * (z. B. Kirschblueten) in der aktuellen Jahreszeit multipliziert werden. 1.0 = unveraendert.
 */
public final class SeasonState {
    public static volatile double blockFactor = 1.0;

    private SeasonState() {
    }
}
