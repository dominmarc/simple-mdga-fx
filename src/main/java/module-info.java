module ManDontGetAngry {
	
	exports de.ifdgmbh.mad.mdga.main;
	exports de.ifdgmbh.mad.mdga.controller;
	
	requires transitive javafx.graphics;
	requires javafx.base;
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	
	opens de.ifdgmbh.mad.mdga.controller;
	
}