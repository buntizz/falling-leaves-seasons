package fallingleavesseasons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Leaf Particle Chance je Jahreszeit. Wird als {@code config/fallingleavesseasons.json} gespeichert.
 * Erlaubt sind Werte von 0.0 (keine Blaetter) bis 1.0.
 */
final class SeasonConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    double spring = 0.01;
    double summer = 0.01;
    double autumn = 0.04;
    double winter = 0.0;

    /**
     * Faktoren fuer Blattarten, die in Falling Leaves Plus einen eigenen Wert haben (Kirschblueten,
     * Blasse Eiche, bluehende Azalee). Deren Wert wird mit dem Faktor der aktuellen Jahreszeit multipliziert.
     */
    BlockFactors blockSpecificFactors = new BlockFactors();

    static final class BlockFactors {
        double spring = 1.0;
        double summer = 1.0;
        double autumn = 4.0;
        double winter = 0.0;
    }

    /**
     * @param season Name des Enums {@code Season} aus Serene Seasons
     * @return der Faktor fuer blockspezifische Chancen, 1.0 bei unbekannter Jahreszeit
     */
    double blockFactorFor(String season) {
        return switch (season) {
            case "SPRING" -> this.blockSpecificFactors.spring;
            case "SUMMER" -> this.blockSpecificFactors.summer;
            case "AUTUMN" -> this.blockSpecificFactors.autumn;
            case "WINTER" -> this.blockSpecificFactors.winter;
            default -> 1.0;
        };
    }

    /**
     * @param season Name des Enums {@code Season} aus Serene Seasons (SPRING, SUMMER, AUTUMN, WINTER)
     * @return die Chance fuer diese Jahreszeit oder {@code null}, wenn die Jahreszeit unbekannt ist
     */
    Double chanceFor(String season) {
        return switch (season) {
            case "SPRING" -> this.spring;
            case "SUMMER" -> this.summer;
            case "AUTUMN" -> this.autumn;
            case "WINTER" -> this.winter;
            default -> null;
        };
    }

    private void clamp() {
        this.spring = clamp(this.spring);
        this.summer = clamp(this.summer);
        this.autumn = clamp(this.autumn);
        this.winter = clamp(this.winter);

        if (this.blockSpecificFactors == null) {
            this.blockSpecificFactors = new BlockFactors();
        }
        BlockFactors f = this.blockSpecificFactors;
        f.spring = clampFactor(f.spring);
        f.summer = clampFactor(f.summer);
        f.autumn = clampFactor(f.autumn);
        f.winter = clampFactor(f.winter);
    }

    private static double clampFactor(double value) {
        if (Double.isNaN(value)) {
            return 1.0;
        }
        return Math.max(0.0, Math.min(100.0, value));
    }

    private static double clamp(double value) {
        if (Double.isNaN(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    /**
     * Liest die Datei und schreibt sie anschliessend neu, damit fehlende Eintraege mit Standardwerten
     * ergaenzt werden. Bei einer kaputten Datei werden die Standardwerte genutzt, die Datei bleibt dann
     * unveraendert, damit nichts verloren geht.
     */
    static SeasonConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FallingLeavesSeasons.MOD_ID + ".json");
        SeasonConfig config = new SeasonConfig();
        boolean readable = true;

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                SeasonConfig read = GSON.fromJson(reader, SeasonConfig.class);
                if (read != null) {
                    config = read;
                }
            } catch (IOException | JsonParseException e) {
                readable = false;
                FallingLeavesSeasons.LOGGER.warn("Konnte {} nicht lesen, nutze Standardwerte: {}", path, e.toString());
            }
        }

        config.clamp();

        if (readable) {
            try {
                Files.writeString(path, GSON.toJson(config));
            } catch (IOException e) {
                FallingLeavesSeasons.LOGGER.warn("Konnte {} nicht schreiben: {}", path, e.toString());
            }
        }
        return config;
    }
}
