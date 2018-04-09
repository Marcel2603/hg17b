In diesem Release wurden der Scanner in der App eingebaut.

Bei der Anmeldung der Veranstalter über eine sichere SSL-Verbindung gibt es noch Probleme, weshalb diese nun ohne solche funktioniert. 
Ansonsten sind alle Anforderungen nun eigebaut. Für die Kann-Ziele Einstellungen und Anmeldung der Schüler bei den Veranstaltungen gibt es in der App schon buttons, falls die App irgendwann weiterentwickelt werden soll, diese sind aber aktuell noch ohne funktion.


Die App und der Server lassen sich mit gradle compilieren. Die build.gradle-Datein und ein gradle-Wrapper sind in den jeweiligen Ordnern vorhanden. Der Server kann mit java -jar Server.jar ausgeführt werden. Für die Aktivierung der Keys über einen Link muss die activate.php auf einem php fähigen Webserver zugänglich gemacht werden und der String URL in der Handler.java muss auf die activate.php zeigen.

Beim Aufruf der activate.php wird die KeyAdder.jar, die sich im selben Verzeichnis wie die Server.jar befinden muss ausgeführt. Dazu müssen in der activate.php gegebenenfalls die Pfade zur java executeable auf dem Server, sowie das Verzeichnis angepasst werden.

Damit die KeyAdder.jar auf dem Server laufen kann wird die Java Cryptography Extension benötigt. ( http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html ) Zur Installation die zip herunterladen und die Dateien jce\local_policy.jar und jce\US_export_policy.jar in das Verzeichnis %JAVA_HOME%\jre\lib\security entpacken.

Hinweise zum compilieren mit Gradle:
Im App-Ordner muss in der Datei local.properies der Pfad der Android SDK-Installation eingetragen werden bzw. die Adroid SDK muss auf andere Weise importiert werden.
Für den build verwenden Sie das Commando './gradlew build' für die jar des Server verwenden Sie './gradlew shadowjar', dies erstellt eine Jar (Server-all.jar) mit allen Dependencies im Ordner build/libs/