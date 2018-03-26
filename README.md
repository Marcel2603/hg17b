Dies ist das initiale Repository eurer Softwaretechnik Praktikumsgruppe.
Bitte lest die Handreichung im OO-Portal für weitere Informationen.
Falls ihr nicht wisst, wie ihr `git` verwendet könnt ihr euch das [Git-Tutorial](https://white-gecko.github.io/GitTutorial/) nochmal ansehen.

*Viel Spaß!*

## Gradle Anleitung:
builds und die jar werden im App/Server-Ordner erstellt, Testreports und Checkstylereports werden in den reports-Ordner geschrieben.

Alle folgenden Kommandos beziehen sich darauf, dass man sich im Hauptordner befindet. Wenn man im App- bzw. Server-Ordner ist kann man Builds und Tests direkt mit `gradle build`/`gradle test` starten.
### App
- build: `./gradlew Code:App:app:build`
- check Code-Style: `./gadlew checkstyleApp`
- run unit-Tests: `./gradlew Code:App:app:test`

### Server
- build: `./gradlew Code:Server:build`
- jar erstellen: `./gradlew shadowJar` (jar heißt Server-all.jar)
- check Code-Style: `./gradlew checkstyleServer`
- run unit-Tests: `./gradlew Code:Server:test`

## Hinweise zu CI
Wenn im Server-/App-Ordner etwas verändert wurde, werden vor dem Commit automatisch die Tests ausgeführt. Wenn ein Fehler auftritt kann dieser so behoben werden, bevor gepusht wird.
Die Reports werden zum Commit hinzugefügt.

Online kontrolliert gitlab die Testergebnisse. Bei Code-Style-Fehlern wird eine Warnung bei fehlern bei den Unit-Tests wird ein Fehler ausgegeben.
Bei fehlern in Unit-Tests wird ausgegeben ob der Fehler beim Server oder der App war, so dass man sich anschließend die Reports im reports-Ordner angucken kann.