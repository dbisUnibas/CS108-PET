package ch.unibas.dmi.dbis.cs108pet.main;

import ch.unibas.dmi.dbis.cs108pet.ui.CS108PETApplication;
import javafx.application.Application;

/**
 * This is the official starting point. For some reason, javafx breaks on some platforms when the entrypoint extends
 * application.
 */
public class Main {
  
  public static void main(String[] args) {
    System.setProperty("line.separator", "\n"); // To enforce UN*X line endings
    System.setProperty("javafx.version", "11.0.2"); // To make controlsfx happy
    Application.launch(CS108PETApplication.class,args);
  }
  
}
