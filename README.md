# Falling Leaves Seasons

Fabric-Mod für **Minecraft 1.21.11** (nur Client). Sie ändert die Einstellung **Leaf Particle Chance** von
*Falling Leaves Plus* je nach Jahreszeit aus *Serene Seasons*.

| Jahreszeit | Standardwert |
|------------|--------------|
| Frühling   | 0.01 (Standard von Falling Leaves Plus) |
| Sommer     | 0.01 |
| Herbst     | 0.04 |
| Winter     | 0.0 (keine Blätter) |

## Benötigt

- Fabric Loader und Fabric API
- Falling Leaves Plus 21.11.0 (Fabric, 1.21.11) samt Puzzles Lib
- Serene Seasons für Fabric 1.21.11 samt GlitchCore

## Einstellen

Nach dem ersten Start liegt die Datei `config/fallingleavesseasons.json` im Minecraft-Ordner:

```json
{
  "spring": 0.01,
  "summer": 0.01,
  "autumn": 0.04,
  "winter": 0.0,
  "blockSpecificFactors": {
    "spring": 1.0,
    "summer": 1.0,
    "autumn": 4.0,
    "winter": 0.0
  }
}
```

`blockSpecificFactors` gilt für die Blattarten, für die Falling Leaves Plus einen eigenen Wert mitbringt
(siehe unten). Deren Wert wird mit dem Faktor der aktuellen Jahreszeit multipliziert, Standard: Herbst ×4,
Winter ×0, sonst ×1. Faktoren dürfen zwischen `0` und `100` liegen.

Erlaubt sind für die Chancen Werte von `0.0` bis `1.0`. Die Datei wird beim Betreten einer Welt neu gelesen, ein Neustart ist
also nicht nötig. Die Chance gilt pro Laubblock und Animations-Tick, `0.01` ist der Standard von Falling Leaves Plus.

## Wie es funktioniert

Einmal pro Sekunde fragt die Mod die aktuelle Jahreszeit bei Serene Seasons ab und setzt
`leafParticleChance` in der Client-Config von Falling Leaves Plus auf den passenden Wert. Beim Verlassen
der Welt wird der Wert wiederhergestellt, den du in Falling Leaves Plus selbst eingestellt hattest. Die Datei
von Falling Leaves Plus wird dabei nicht verändert.

## Blattarten mit eigenem Wert

Falling Leaves Plus bringt für Kirschblüten (0.1), Blasse Eiche (0.02) und blühende Azalee (0.02) eigene Werte
mit, die die globale Einstellung überschreiben. Diese werden über einen Mixin auf
`ParticleSettings.getLeafParticleChance` mit `blockSpecificFactors` multipliziert. Im Herbst fallen sie also
4× so oft (Kirsche 0.4), im Winter gar nicht. Das gilt auch für Blattarten, die ein Ressourcenpaket oder eine
andere Mod mit eigenem Wert ergänzt.

## Bauen

Die Mod ist nicht fertig kompiliert. Zum Bauen brauchst du Java 21:

```
./gradlew build
```

Die fertige Datei liegt dann in `build/libs/falling-leaves-seasons-1.1.0.jar` (nicht die `-sources.jar`) und
kommt in den `mods`-Ordner.

Ohne lokale Java-Umgebung: Das Projekt auf GitHub hochladen. Der Workflow `.github/workflows/build.yml` baut
die `.jar` automatisch, sie steht danach unter *Actions* → *build* → *Artifacts* zum Download bereit.
