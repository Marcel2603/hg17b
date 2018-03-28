<?php
	/*
	Diese Datei ruft die keyAdder.jar auf um neue keys in den Keystore zu uebertragen.
	Der uebergebene Parameter id entspricht dabei dem Alias des Keys, key ist die vom
	Server erzeugte zufaellige Ziffernfolge, die beim uebertragen des keys an den Server
	vom Server erstellt wird und als Email verschickt wird.
	
	*/



	//Hier den Pfad zur Java Executeable eintragen:
	$javaPath="/usr/java/jdk1.8.0_112/bin/java"; 
	
	//Hier den Pfad zur Server.jar und KeyAdder.jar (muessen identisch sein) eintragen:
	$keyAdderPath="/home/hg17b/Server/"; 
	$output = shell_exec( $javaPath . ' -jar '. $keyAdderPath . 'KeyAdder.jar ' . $_GET['id'] . ' ' . $_GET['key'] . ' ' . $keyAdderPath);
	echo $output;

?>