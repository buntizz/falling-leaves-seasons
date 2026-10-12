package fallingleavesseasons;

import net.minecraft.world.level.Level;

import java.lang.reflect.InvocationTargetException;

/**
 * Setzt einmal pro Sekunde die Leaf Particle Chance von Falling Leaves Plus passend zur Jahreszeit.
 *
 * <p>Falling Leaves Plus liest den Wert bei jedem Partikel-Tick direkt aus seiner Config. Ein
 * Config-Reload (z. B. nach dem Speichern im Config-Menue) setzt das Feld auf den Dateiwert zurueck,
 * deshalb wird der Zielwert regelmaessig neu gesetzt.
 */
final class SeasonLeafController {
    private static final int INTERVAL_TICKS = 20;

    private SeasonConfig config = new SeasonConfig();
    private FlpBridge flp;
    private SereneSeasonsBridge seasons;

    private boolean failed;
    private boolean inWorld;
    private int ticks;

    /** Der Wert, den wir zuletzt gesetzt haben, und der Wert, den der Spieler vorher eingestellt hatte. */
    private Double lastSet;
    private Double baseline;

    /**
     * Wird jeden Client-Tick aufgerufen.
     *
     * @param level die aktuelle Client-Welt oder {@code null} im Hauptmenue
     */
    void onTick(Level level) {
        if (this.failed) {
            return;
        }

        if (level == null) {
            if (this.inWorld) {
                this.inWorld = false;
                this.restoreBaseline();
            }
            return;
        }

        if (!this.inWorld) {
            this.inWorld = true;
            this.ticks = 0;
            // Beim Betreten einer Welt die Config-Datei neu lesen, damit Aenderungen ohne Neustart wirken.
            this.config = SeasonConfig.load();
        }

        if (this.ticks++ % INTERVAL_TICKS != 0) {
            return;
        }

        try {
            this.apply(level);
        } catch (ReflectiveOperationException | LinkageError e) {
            if (e instanceof InvocationTargetException) {
                // Serene Seasons ist vermutlich noch nicht bereit, im naechsten Intervall nochmal versuchen.
                return;
            }
            this.failed = true;
            SeasonState.blockFactor = 1.0;
            FallingLeavesSeasons.LOGGER.error(
                    "Falling Leaves Plus oder Serene Seasons haben nicht die erwartete Struktur. "
                            + "Falling Leaves Seasons schaltet sich ab.", e);
        }
    }

    private void apply(Level level) throws ReflectiveOperationException {
        if (this.flp == null) {
            this.flp = FlpBridge.create();
        }
        if (this.seasons == null) {
            this.seasons = SereneSeasonsBridge.create();
        }

        String season = this.seasons.currentSeason(level);
        Double target = this.config.chanceFor(season);
        if (target == null) {
            return;
        }
        SeasonState.blockFactor = this.config.blockFactorFor(season);

        double current = this.flp.read();
        if (this.lastSet == null || Double.compare(current, this.lastSet) != 0) {
            // Der Wert wurde von aussen gesetzt (Start, Config-Reload, Config-Menue): Merken als Ausgangswert.
            this.baseline = current;
        }
        if (Double.compare(current, target) != 0) {
            this.flp.write(target);
        }
        this.lastSet = target;
    }

    /** Stellt beim Verlassen der Welt den urspruenglich eingestellten Wert wieder her. */
    private void restoreBaseline() {
        try {
            if (this.flp != null && this.lastSet != null && this.baseline != null
                    && Double.compare(this.flp.read(), this.lastSet) == 0) {
                this.flp.write(this.baseline);
            }
        } catch (ReflectiveOperationException | LinkageError e) {
            FallingLeavesSeasons.LOGGER.warn("Konnte die urspruengliche Leaf Particle Chance nicht wiederherstellen", e);
        } finally {
            SeasonState.blockFactor = 1.0;
            this.lastSet = null;
            this.baseline = null;
        }
    }
}
